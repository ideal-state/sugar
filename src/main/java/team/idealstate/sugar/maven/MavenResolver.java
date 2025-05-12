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

package team.idealstate.sugar.maven;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.eclipse.aether.util.artifact.JavaScopes;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.maven.exception.MavenException;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

public class MavenResolver {

    public static final File CONFIG_FILE = new File("./maven/config.yml");
    private final RepositorySystem system;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    @NotNull
    private static MavenConfiguration loadConfiguration() {
        try {
            return new ObjectMapper(new YAMLFactory())
                    .findAndRegisterModules()
                    .readValue(CONFIG_FILE, MavenConfiguration.class);
        } catch (IOException e) {
            throw new MavenException(e);
        }
    }

    public MavenResolver() {
        this(loadConfiguration());
    }

    public MavenResolver(@NotNull MavenConfiguration configuration) {
        this(configuration, new File(CONFIG_FILE.getParentFile(), "repository"));
    }

    protected MavenResolver(@NotNull MavenConfiguration configuration, @NotNull File localRepository) {
        Validation.requireNotNull(configuration, "Configuration must not be null.");
        Validation.requireNotNull(localRepository, "Local repository must not be null.");
        RepositorySystemSupplier supplier = new RepositorySystemSupplier();
        this.system = supplier.get();
        this.session = MavenRepositorySystemUtils.newSession();
        if (!localRepository.exists()) {
            //noinspection ResultOfMethodCallIgnored
            localRepository.mkdirs();
        }

        session.setLocalRepositoryManager(
                system.newLocalRepositoryManager(session, new LocalRepository(localRepository)));
        if (configuration.getLog()) {
            session.setTransferListener(new TransferLog());
        }
        session.setReadOnly();
        Map<String, MavenConfiguration.Repository> repos = configuration.getRepositories();
        Collection<RemoteRepository> remoteRepositories = new LinkedHashSet<>(repos.size());
        for (Map.Entry<String, MavenConfiguration.Repository> entry : repos.entrySet()) {
            String id = entry.getKey();
            MavenConfiguration.Repository repository = entry.getValue();
            remoteRepositories.add(new RemoteRepository.Builder(id, "default", repository.getUrl())
                    .setReleasePolicy(new RepositoryPolicy(
                            true, RepositoryPolicy.UPDATE_POLICY_NEVER, RepositoryPolicy.CHECKSUM_POLICY_WARN))
                    .setSnapshotPolicy(new RepositoryPolicy(
                            true, RepositoryPolicy.UPDATE_POLICY_ALWAYS, RepositoryPolicy.CHECKSUM_POLICY_WARN))
                    .build());
        }
        if (configuration.getLocal()) {
            remoteRepositories = new LinkedList<>(remoteRepositories);
            try {
                ((LinkedList<RemoteRepository>) remoteRepositories)
                        .addFirst(new RemoteRepository.Builder(
                                        "local",
                                        "default",
                                        localRepository.toURI().toURL().toString())
                                .setReleasePolicy(new RepositoryPolicy(
                                        true,
                                        RepositoryPolicy.UPDATE_POLICY_NEVER,
                                        RepositoryPolicy.CHECKSUM_POLICY_WARN))
                                .setSnapshotPolicy(new RepositoryPolicy(
                                        true,
                                        RepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                        RepositoryPolicy.CHECKSUM_POLICY_WARN))
                                .build());
            } catch (MalformedURLException e) {
                throw new MavenException(e);
            }
        }
        this.repositories = system.newResolutionRepositories(
                session,
                remoteRepositories instanceof List
                        ? (List<RemoteRepository>) remoteRepositories
                        : new ArrayList<>(remoteRepositories));
    }

    @NotNull
    public static String makeDependencyId(@NotNull Artifact artifact, boolean base) {
        Validation.notNull(artifact, "Artifact must not be null.");
        StringJoiner joiner = new StringJoiner(":")
                .add(artifact.getGroupId())
                .add(artifact.getArtifactId())
                .add(artifact.getExtension())
                .add(artifact.getClassifier());
        if (base) {
            return joiner.add(artifact.getBaseVersion()).toString();
        } else {
            return joiner.add(artifact.getVersion()).toString();
        }
    }

    @NotNull
    public static Map<String, File> notMissingOrEx(@NotNull List<ArtifactResult> artifactResults) {
        Validation.notNull(artifactResults, "Artifact results must not be null.");
        if (artifactResults.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, File> files = new LinkedHashMap<>(artifactResults.size());
        for (ArtifactResult artifactResult : artifactResults) {
            ArtifactRequest request = artifactResult.getRequest();
            Artifact artifact = request.getArtifact();
            String id = makeDependencyId(artifact, true);
            if (artifactResult.isMissing()) {
                throw new MavenException(String.format("The dependency '%s' is missing.", id));
            }
            artifact = artifactResult.getArtifact();
            if (artifact == null || !artifactResult.isResolved()) {
                for (Exception exception : artifactResult.getExceptions()) {
                    Log.error(exception);
                }
                throw new MavenException(String.format("The dependency '%s' cannot resolved.", id));
            }
            id = makeDependencyId(artifact, true);
            File file = artifact.getFile();
            if (!file.exists()) {
                throw new MavenException(
                        String.format("The dependency '%s' file '%s' is missing.", id, file.getAbsolutePath()));
            }
            files.put(id, file);
        }
        return files;
    }

    @NotNull
    public List<ArtifactResult> resolve(@NotNull List<Dependency> dependencies) {
        return resolve(dependencies, JavaScopes.COMPILE, JavaScopes.RUNTIME);
    }

    /** @param scopes {@link JavaScopes} */
    @NotNull
    public List<ArtifactResult> resolve(@NotNull List<Dependency> dependencies, @NotNull String... scopes) {
        Validation.requireNotNull(dependencies, "Dependencies must not be null.");
        Validation.requireNotNull(scopes, "Scopes must not be null.");
        if (dependencies.isEmpty()) {
            return Collections.emptyList();
        }
        if (scopes.length == 0) {
            scopes = new String[] {JavaScopes.COMPILE, JavaScopes.RUNTIME};
        }
        String[] finalScopes = scopes;
        dependencies = dependencies.stream()
                .filter(d -> {
                    for (String scope : finalScopes) {
                        if (d.getScope().equalsIgnoreCase(scope)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
        if (dependencies.isEmpty()) {
            return Collections.emptyList();
        }

        DependencyResult dependencyResult;
        try {
            dependencyResult = system.resolveDependencies(
                    session,
                    new DependencyRequest(new CollectRequest((Dependency) null, dependencies, repositories), null));
        } catch (DependencyResolutionException e) {
            throw new MavenException("The dependency resolution failed.", e);
        }
        return dependencyResult.getArtifactResults();
    }

    private static class TransferLog extends AbstractTransferListener {

        @Override
        public void transferStarted(TransferEvent event) {
            TransferResource resource = event.getResource();
            Log.info(String.format("Downloading '%s'...", resource.getRepositoryUrl() + resource.getResourceName()));
        }

        @Override
        public void transferSucceeded(TransferEvent event) {
            TransferResource resource = event.getResource();
            Log.info(String.format("Downloaded '%s'.", resource.getRepositoryUrl() + resource.getResourceName()));
        }

        @Override
        public void transferFailed(TransferEvent event) {
            TransferResource resource = event.getResource();
            Log.debug(() -> String.format(
                    "Failed to download '%s'.", resource.getRepositoryUrl() + resource.getResourceName()));
        }
    }
}
