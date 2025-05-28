/*
 *    Copyright 2025 ideal-state
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package team.idealstate.sugar.maven.resolver.api.simple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.maven.resolver.api.Dependency;
import team.idealstate.sugar.maven.resolver.api.DependencyScope;
import team.idealstate.sugar.maven.resolver.api.LocalRepository;
import team.idealstate.sugar.maven.resolver.api.MavenResolver;
import team.idealstate.sugar.maven.resolver.api.RemoteRepository;
import team.idealstate.sugar.maven.resolver.api.Repository;
import team.idealstate.sugar.maven.resolver.api.RepositoryPolicy;
import team.idealstate.sugar.maven.resolver.api.ResolvedArtifact;
import team.idealstate.sugar.maven.resolver.api.ResolvedDependency;
import team.idealstate.sugar.maven.resolver.api.exception.MavenResolutionException;
import team.idealstate.sugar.string.StringUtils;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import team.idealstate.sugar.validate.annotation.Nullable;

@Data
final class SimpleMavenResolver implements MavenResolver {

    private static final Set<DependencyScope> DEFAULT_RESOLVING_SCOPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(DependencyScope.COMPILE, DependencyScope.RUNTIME)));
    private static final String LOCATION_BASE_DELIMITER = "/";
    private static final String LOCATION_NAME_DELIMITER = "-";
    private static final String LOCATION_EXTENSION_DELIMITER = ".";
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final String METADATA_FILE_NAME = "maven-metadata.xml";

    @NonNull
    private final LocalRepository localRepository;

    @NonNull
    private final List<RemoteRepository> remoteRepositories;

    @NonNull
    private final SimpleDependencyResolver dependencyResolver;

    public @NotNull List<RemoteRepository> getRemoteRepositories() {
        if (remoteRepositories.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(remoteRepositories);
    }

    private boolean isExists(@NotNull Repository repository, @NotNull Dependency dependency, @NotNull URI location)
            throws Throwable {
        if (repository instanceof LocalRepository) {
            return new File(location).exists();
        } else if (repository instanceof RemoteRepository) {
            String scheme = location.getScheme();
            if ("file".equals(scheme)) {
                return new File(location).exists();
            }
            if ("http".equals(scheme) || "https".equals(scheme)) {
                HttpURLConnection connection =
                        (HttpURLConnection) location.toURL().openConnection();
                try {
                    return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
                } finally {
                    connection.disconnect();
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    @NotNull
    private String makeParentPath(@NotNull Dependency dependency) {
        return dependency.getGroupId().replace(".", LOCATION_BASE_DELIMITER)
                + LOCATION_BASE_DELIMITER
                + dependency.getArtifactId()
                + LOCATION_BASE_DELIMITER
                + dependency.getVersion()
                + LOCATION_BASE_DELIMITER;
    }

    @NotNull
    private String makePomFilePath(@NotNull ResolvedDependency dependency, boolean actual) {
        String artifactId = dependency.getArtifactId();
        String version = actual ? dependency.asResolved().getActualVersion() : dependency.getVersion();
        String classifier = dependency.getClassifier();
        String extension = "pom";
        return buildArtifactPath(artifactId, version, classifier, extension);
    }

    @NotNull
    private String buildArtifactPath(String artifactId, String version, String classifier, String extension) {
        StringBuilder builder = new StringBuilder(
                        artifactId.length() + version.length() + classifier.length() + extension.length() + 32)
                .append(artifactId)
                .append(LOCATION_NAME_DELIMITER)
                .append(version);
        if (!classifier.isEmpty()) {
            builder.append(LOCATION_NAME_DELIMITER).append(classifier);
        }
        return builder.append(LOCATION_EXTENSION_DELIMITER).append(extension).toString();
    }

    @NotNull
    private String makeArtifactFilePath(@NotNull Dependency dependency, boolean actual) {
        String artifactId = dependency.getArtifactId();
        String version = actual && dependency instanceof ResolvedDependency
                ? dependency.asResolved().getActualVersion()
                : dependency.getVersion();
        String classifier = dependency.getClassifier();
        String extension = dependency.getExtension();
        return buildArtifactPath(artifactId, version, classifier, extension);
    }

    @NotNull
    private URI makeLocation(@NotNull URI url, @NotNull String subpath) throws URISyntaxException {
        String path = url.getPath().replace("\\", "/");
        if (!path.endsWith(LOCATION_BASE_DELIMITER)) {
            path += LOCATION_BASE_DELIMITER;
        }
        return new URI(
                url.getScheme(),
                url.getUserInfo(),
                url.getHost(),
                url.getPort(),
                path + subpath,
                url.getQuery(),
                url.getFragment());
    }

    @NotNull
    private SimpleDependency preprocess(@NotNull SimpleDependency dependency) {
        if (!dependency.isResolved()) {
            return dependency;
        }
        return new SimpleDependency(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion(),
                dependency.getClassifier(),
                dependency.getExtension(),
                dependency.getScope());
    }

    @NotNull
    private SimpleDependency resolve(
            @NotNull Repository repository,
            @NotNull SimpleDependencyResolver dependencyResolver,
            @NotNull SimpleDependency dependency,
            @NotNull File destinationDirectory)
            throws MavenResolutionException {
        try {
            dependency = preprocess(dependency);
            List<SimpleDependency> dependencies = new ArrayList<>();
            SimpleResolvedDependency resolvedDependency = null;
            Set<RepositoryPolicy> policies = repository.getPolicies();
            if (policies.contains(RepositoryPolicy.ALWAYS_UPDATE)
                    && !policies.contains(RepositoryPolicy.NEVER_UPDATE)) {
                if (repository instanceof LocalRepository) {
                    return dependency;
                }
                File matadataFile = downloadFile(
                        repository, dependency, destinationDirectory, METADATA_FILE_NAME, METADATA_FILE_NAME);
                if (matadataFile != null) {
                    SimpleMetadata metadata = SimpleMetadata.resolve(Files.newInputStream(matadataFile.toPath()));
                    resolvedDependency = new SimpleResolvedDependency(
                            dependency.getGroupId(),
                            dependency.getArtifactId(),
                            dependency.getExtension(),
                            dependency.getClassifier(),
                            dependency.getVersion(),
                            dependency.getScope(),
                            metadata.getActualVersion(),
                            metadata.getLastUpdated(),
                            repository,
                            dependencies);
                }
            }
            if (resolvedDependency == null) {
                resolvedDependency = new SimpleResolvedDependency(
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getExtension(),
                        dependency.getClassifier(),
                        dependency.getVersion(),
                        dependency.getScope(),
                        dependency.getVersion(),
                        LocalDateTime.MIN,
                        repository,
                        dependencies);
            }
            File pomFile = downloadFile(
                    repository,
                    resolvedDependency,
                    destinationDirectory,
                    makePomFilePath(resolvedDependency, true),
                    makePomFilePath(resolvedDependency, false));
            if (pomFile == null) {
                return dependency;
            }
            SimplePom pom = SimplePom.resolve(dependencyResolver, Files.newInputStream(pomFile.toPath()));
            dependencies.addAll(pom.getDependencies());
            return resolvedDependency;
        } catch (Throwable e) {
            if (e instanceof MavenResolutionException) {
                throw (MavenResolutionException) e;
            }
            throw new MavenResolutionException(e);
        }
    }

    @Nullable
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File downloadFile(
            @NotNull Repository repository,
            @NotNull SimpleDependency dependency,
            @NotNull File destinationDirectory,
            @NotNull String inputSubfilePath,
            @NotNull String outputSubfilePath)
            throws Throwable {

        String parentPath = makeParentPath(dependency);
        URI location = makeLocation(repository.getUrl(), parentPath + inputSubfilePath);

        if (!isExists(repository, dependency, location)) {
            return null;
        }

        File destinationFile = new File(destinationDirectory, parentPath + outputSubfilePath).getAbsoluteFile();
        if ("file".equals(location.getScheme())) {
            if (new File(location)
                    .toPath()
                    .toAbsolutePath()
                    .normalize()
                    .toString()
                    .equals(destinationFile
                            .toPath()
                            .toAbsolutePath()
                            .normalize()
                            .toString())) {
                return destinationFile;
            }
        }
        File parentFile = destinationFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        Log.info(String.format("Downloading '%s'...", location));

        final int MAX_RETRIES = 5;
        boolean success = false;
        for (int i = 0; i < MAX_RETRIES; i++) {
            long expectedLength = -1;
            long totalRead = 0;

            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                if (repository instanceof LocalRepository) {
                    inputStream = Files.newInputStream(new File(location).toPath());
                } else if (repository instanceof RemoteRepository) {
                    String scheme = location.getScheme();
                    if ("file".equals(scheme)) {
                        inputStream = Files.newInputStream(new File(location).toPath());
                    } else if ("http".equals(scheme) || "https".equals(scheme)) {
                        connection = (HttpURLConnection) location.toURL().openConnection();
                        connection.setRequestMethod("GET");
                        int responseCode = connection.getResponseCode();
                        if (responseCode != HttpURLConnection.HTTP_OK) {
                            if (i < MAX_RETRIES - 1) {
                                continue;
                            } else {
                                throw new MavenResolutionException(
                                        String.format("Failed to download '%s'. HTTP %d", location, responseCode));
                            }
                        }
                        inputStream = connection.getInputStream();
                    }
                }
                if (inputStream == null) {
                    throw new UnsupportedOperationException();
                }
                try (InputStream input = inputStream) {
                    if (connection != null) {
                        String contentLength = connection.getHeaderField("Content-Length");
                        if (contentLength != null && StringUtils.isInteger(contentLength)) {
                            expectedLength = Long.parseLong(contentLength);
                        }
                    }
                    try (OutputStream output = Files.newOutputStream(destinationFile.toPath())) {
                        int read;
                        while ((read = input.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
                            output.write(buffer, 0, read);
                            totalRead += read;
                        }
                        output.flush();
                    }
                    if (expectedLength > 0 && totalRead != expectedLength) {
                        throw new IOException("Download incomplete: expected " + expectedLength + " bytes, got "
                                + totalRead + " bytes.");
                    }
                    if (destinationFile.length() < 100) {
                        throw new IOException("Downloaded file too small to be valid.");
                    }
                    success = true;
                } catch (IOException e) {
                    if (i == MAX_RETRIES - 1) {
                        throw new MavenResolutionException("Failed to download '" + location + "'.", e);
                    }
                    Log.warn(String.format(
                            "Download failed (attempt %d/%d): %s. Retrying...", i + 1, MAX_RETRIES, e.getMessage()));
                    destinationFile.delete();
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            if (success) {
                break;
            }
        }
        if (!success) {
            destinationFile.delete();
            throw new MavenResolutionException("Failed to download '" + location + "'.");
        }

        return destinationFile;
    }

    @NotNull
    private SimpleArtifact download(
            @NotNull Repository repository,
            @NotNull SimpleResolvedDependency dependency,
            @NotNull File destinationDirectory)
            throws MavenResolutionException {
        Validation.notNull(dependency, "Dependency must not be null.");
        Validation.notNull(destinationDirectory, "Destination directory must not be null.");
        try {
            File destinationFile = downloadFile(
                    repository,
                    dependency,
                    destinationDirectory,
                    makeArtifactFilePath(dependency, true),
                    makeArtifactFilePath(dependency, false));
            return destinationFile == null
                    ? new SimpleArtifact(
                            dependency.getGroupId(),
                            dependency.getArtifactId(),
                            dependency.getExtension(),
                            dependency.getClassifier(),
                            dependency.getVersion())
                    : new SimpleResolvedArtifact(
                            dependency.getGroupId(),
                            dependency.getArtifactId(),
                            dependency.getExtension(),
                            dependency.getClassifier(),
                            dependency.getVersion(),
                            dependency.getActualVersion(),
                            dependency.getUpdatedTime(),
                            repository,
                            dependency.getDependencies(),
                            destinationFile);
        } catch (Throwable e) {
            if (e instanceof MavenResolutionException) {
                throw (MavenResolutionException) e;
            }
            throw new MavenResolutionException(e);
        }
    }

    @NotNull
    private Set<SimpleResolvedArtifact> resolving(
            @NotNull File destinationDirectory,
            @NotNull List<RemoteRepository> remoteRepositories,
            @NotNull SimpleDependencyResolver dependencyResolver,
            @NotNull SimpleDependency resolving,
            @NotNull Set<DependencyScope> resolvingDependencyScopes) {
        if (!resolving.isResolved()) {
            resolving = resolve(localRepository, dependencyResolver, resolving, destinationDirectory);
            if (!resolving.isResolved()) {
                for (Repository remoteRepository : remoteRepositories) {
                    resolving = resolve(remoteRepository, dependencyResolver, resolving, destinationDirectory);
                    if (resolving.isResolved()) {
                        break;
                    }
                }
            }
            if (!resolving.isResolved()) {
                throw new MavenResolutionException(String.format("Cannot resolve dependency. %s", resolving));
            }
        }
        SimpleResolvedDependency resolvedDependency = resolving.asResolved();
        SimpleArtifact artifact = download(localRepository, resolvedDependency, destinationDirectory);
        List<SimpleDependency> dependencies = resolvedDependency.getDependencies();
        Set<SimpleResolvedArtifact> result = new LinkedHashSet<>(1 + dependencies.size());
        if (!artifact.isResolved()) {
            Repository resolvedRepository = resolvedDependency.getRepository();
            artifact = download(resolvedRepository, resolvedDependency, destinationDirectory);
            if (!artifact.isResolved()) {
                String repositoryName = resolvedRepository.getName();
                boolean start = false;
                for (Repository remoteRepository : remoteRepositories) {
                    if (start) {
                        if (artifact.isResolved()) {
                            break;
                        }
                        if (repositoryName.equals(remoteRepository.getName())) {
                            continue;
                        }
                        resolving =
                                resolve(remoteRepository, dependencyResolver, resolvedDependency, destinationDirectory);
                        if (resolving.isResolved()) {
                            result.addAll(resolving(
                                    destinationDirectory,
                                    remoteRepositories,
                                    dependencyResolver,
                                    resolving,
                                    resolvingDependencyScopes));
                        }
                    } else if (repositoryName.equals(remoteRepository.getName())) {
                        start = true;
                    }
                }
            } else {
                result.add(artifact.asResolved());
            }
        } else {
            result.add(artifact.asResolved());
        }
        if (result.isEmpty()) {
            throw new MavenResolutionException(String.format("Cannot download dependency. %s", resolving));
        }
        if (!dependencies.isEmpty()) {
            for (SimpleDependency dependency : dependencies) {
                if (!resolvingDependencyScopes.contains(dependency.getScope())) {
                    continue;
                }
                result.addAll(resolving(
                        destinationDirectory,
                        remoteRepositories,
                        dependencyResolver,
                        dependency,
                        resolvingDependencyScopes));
            }
        }
        return result;
    }

    @NotNull
    @Override
    public List<ResolvedArtifact> resolve(
            @NotNull List<Dependency> dependencies, @NotNull DependencyScope... dependencyScopes)
            throws MavenResolutionException {
        Validation.notNull(dependencies, "Dependencies id must not be null or blank.");
        Validation.notNull(dependencyScopes, "Dependency scopes must not be null.");
        if (dependencies.isEmpty()) {
            return Collections.emptyList();
        }
        Set<SimpleResolvedArtifact> resolving = new LinkedHashSet<>(dependencies.size() * 6);
        Set<DependencyScope> resolvingDependencyScopes = dependencyScopes.length == 0
                ? DEFAULT_RESOLVING_SCOPES
                : new HashSet<>(Arrays.asList(dependencyScopes));
        for (SimpleDependency dependency : dependencies.stream()
                .map(it -> {
                    if (it instanceof SimpleDependency) {
                        return (SimpleDependency) it;
                    } else {
                        return new SimpleDependency(
                                it.getGroupId(),
                                it.getArtifactId(),
                                it.getExtension(),
                                it.getClassifier(),
                                it.getVersion(),
                                it.getScope());
                    }
                })
                .collect(Collectors.toList())) {
            resolving.addAll(resolving(
                    getLocalRepository().getLocation(),
                    getRemoteRepositories(),
                    getDependencyResolver(),
                    dependency,
                    resolvingDependencyScopes));
        }
        Map<String, SimpleResolvedArtifact> result = new LinkedHashMap<>(resolving.size());
        for (SimpleResolvedArtifact artifact : resolving) {
            String id = new StringJoiner(getDependencyResolver().getIdDelimiter())
                    .add(artifact.getGroupId())
                    .add(artifact.getArtifactId())
                    .add(artifact.getClassifier())
                    .add(artifact.getExtension())
                    .toString();
            SimpleResolvedArtifact last = result.get(id);
            if (last == null || last.compareTo(artifact) < 0) {
                result.put(id, artifact);
            }
        }
        return new ArrayList<>(result.values());
    }
}
