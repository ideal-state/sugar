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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.yaml.snakeyaml.Yaml;
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
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE.toPath())) {
            return new Yaml().loadAs(reader, MavenConfiguration.class);
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
        session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);
        session.setLocalRepositoryManager(
                system.newLocalRepositoryManager(session, new LocalRepository(localRepository)));
        session.setTransferListener(new TransferLog());
        session.setReadOnly();
        Map<String, MavenConfiguration.Repository> repos = configuration.getRepositories();
        Set<RemoteRepository> remoteRepositories = new LinkedHashSet<>(repos.size() + 1);
        for (Map.Entry<String, MavenConfiguration.Repository> entry : repos.entrySet()) {
            String id = entry.getKey();
            MavenConfiguration.Repository repository = entry.getValue();
            remoteRepositories.add(new RemoteRepository.Builder(id, "default", repository.getUrl()).build());
        }
        this.repositories = system.newResolutionRepositories(session, new ArrayList<>(remoteRepositories));
    }

    @NotNull
    public List<Artifact> resolve(@NotNull List<Dependency> dependencies) {
        return resolve(dependencies, JavaScopes.COMPILE, JavaScopes.RUNTIME);
    }

    /** @param scopes {@link JavaScopes} */
    @NotNull
    public List<Artifact> resolve(@NotNull List<Dependency> dependencies, @NotNull String... scopes) {
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
        return dependencyResult.getArtifactResults().stream()
                .map(ArtifactResult::getArtifact)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
            Log.error(String.format(
                    "Failed to download '%s'.", resource.getRepositoryUrl() + resource.getResourceName()));
        }
    }
}
