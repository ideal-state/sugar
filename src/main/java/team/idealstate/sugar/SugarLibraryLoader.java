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

package team.idealstate.sugar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.objectweb.asm.ClassReader;
import team.idealstate.sugar.exception.SugarException;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.maven.MavenResolver;
import team.idealstate.sugar.maven.PomXmlResolver;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

public final class SugarLibraryLoader implements ClassFileTransformer {

    private static final Set<String> RESOLVED = new CopyOnWriteArraySet<>();

    private static boolean isResolved(@NotNull File file) {
        return !RESOLVED.add(file.getAbsolutePath());
    }

    private final MavenResolver mavenResolver;
    private final Instrumentation instrumentation;
    private final String[] scopes;

    public SugarLibraryLoader(
            @NotNull Instrumentation instrumentation, @NotNull MavenResolver mavenResolver, @NotNull String... scopes) {
        Validation.requireNotNull(instrumentation, "Instrumentation must not be null.");
        Validation.requireNotNull(mavenResolver, "Maven resolver must not be null.");
        this.instrumentation = instrumentation;
        this.mavenResolver = mavenResolver;
        this.scopes = scopes;
        try {
            loadDependencies(
                    new File(Sugar.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()),
                    true);
        } catch (URISyntaxException e) {
            throw new SugarException(e);
        }
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] buffer) {
        if (loader == null || classBeingRedefined != null) {
            return null;
        }
        CodeSource codeSource = protectionDomain.getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL location = codeSource.getLocation();
        if (location == null) {
            return null;
        }
        File file;
        try {
            file = new File(location.toURI());
            if (isResolved(file)) {
                return null;
            }
//            String[] interfaces = new ClassReader(buffer).getInterfaces();
//            if (interfaces.length == 0) {
//                return null;
//            }
//            if (Arrays.stream(interfaces).noneMatch(i -> Sugar.class.getName().equals(i))) {
//                return null;
//            }
            if (!Sugar.class.equals(Class.forName(Sugar.class.getName(), false, loader))) {
                return null;
            }
        } catch (ClassNotFoundException | URISyntaxException e) {
            return null;
        }
        loadDependencies(file, false);
        return null;
    }

    private void loadDependencies(@NotNull File file, boolean verify) {
        if (verify) {
            if (isResolved(file)) {
                return;
            }
        }
        Set<Dependency> dependencies = new LinkedHashSet<>(128);
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entry.isDirectory() || !entryName.startsWith("META-INF/maven/") || !entryName.endsWith("pom.xml")) {
                    continue;
                }
                try (InputStream input = jar.getInputStream(entry)) {
                    List<Dependency> resolve = PomXmlResolver.resolve(input);
                    if (resolve.isEmpty()) {
                        continue;
                    }
                    dependencies.addAll(resolve);
                }
            }
        } catch (IOException e) {
            throw new SugarException(e);
        }
        if (dependencies.isEmpty()) {
            return;
        }
        List<Artifact> artifacts = mavenResolver.resolve(new ArrayList<>(dependencies), scopes);
        if (artifacts.isEmpty()) {
            return;
        }
        for (Artifact artifact : artifacts) {
            File artifactFile = artifact.getFile();
            if (!artifactFile.exists()
                    || artifactFile.isDirectory()
                    || !artifactFile.getName().endsWith(".jar")) {
                continue;
            }
            try {
                Log.info(String.format("Loading dependency '%s'...", artifactFile.getAbsolutePath()));
                instrumentation.appendToSystemClassLoaderSearch(new JarFile(artifactFile));
            } catch (IOException e) {
                throw new SugarException(e);
            }
        }
    }
}
