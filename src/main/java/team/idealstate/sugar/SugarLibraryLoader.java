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

import org.eclipse.aether.graph.Dependency;
import team.idealstate.sugar.agent.Javaagent;
import team.idealstate.sugar.exception.SugarException;
import team.idealstate.sugar.maven.MavenResolver;
import team.idealstate.sugar.maven.PomXmlResolver;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

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
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class SugarLibraryLoader implements ClassFileTransformer {

    public static void addTo(
            @NotNull Instrumentation instrumentation, @NotNull MavenResolver mavenResolver, @NotNull String... scopes) {
        Validation.notNull(instrumentation, "Instrumentation must not be null.");
        instrumentation.addTransformer(new SugarLibraryLoader(mavenResolver, scopes), false);
    }

    private static final Set<String> RESOLVED = new CopyOnWriteArraySet<>();
    private static final Map<String, Lock> RESOLVING = new ConcurrentHashMap<>();

    private final MavenResolver mavenResolver;
    private final String[] scopes;

    private SugarLibraryLoader(@NotNull MavenResolver mavenResolver, @NotNull String... scopes) {
        Validation.requireNotNull(mavenResolver, "Maven resolver must not be null.");
        this.mavenResolver = mavenResolver;
        this.scopes = scopes;
        try {
            loadDependencies(
                    new File(Sugar.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()));
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
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String key = makeKey(file);
        if (RESOLVED.contains(key)) {
            RESOLVING.remove(key);
            return null;
        }
        Lock lock = RESOLVING.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        try {
            if (RESOLVED.contains(key)) {
                return null;
            }
            String sugarClassName = Sugar.class.getName();
            if (!Sugar.class.equals(Class.forName(sugarClassName, false, loader))) {
                return null;
            }
            loadDependencies(file);
        } catch (ClassNotFoundException ignored) {
        } finally {
            RESOLVED.add(key);
            lock.unlock();
        }
        return null;
    }

    private static String makeKey(@NotNull File file) {
        return file.toPath().normalize().toString();
    }

    private void loadDependencies(@NotNull File file) {
        String key = makeKey(file);
        if (RESOLVED.contains(key)) {
            return;
        }
        try {
            Set<Dependency> dependencies = new LinkedHashSet<>(128);
            try (JarFile jar = new JarFile(file)) {
                Manifest manifest = jar.getManifest();
                if (manifest == null) {
                    return;
                }
                Attributes attributes = manifest.getMainAttributes();
                if (attributes.isEmpty()) {
                    return;
                }
                Sugar sugar = Sugar.load(jar, false);
                if (sugar == null || !sugar.isEnabled()) {
                    return;
                }
                String pomPath = String.format("META-INF/maven/%s/%s/pom.xml", sugar.getGroupId(), sugar.getArtifactId());
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entry.isDirectory() || !entryName.equals(pomPath)) {
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
            Map<String, File> artifacts =
                    MavenResolver.notMissingOrEx(mavenResolver.resolve(new ArrayList<>(dependencies), scopes));
            Javaagent.appendToSystemClassLoaderSearch(artifacts);
        } finally {
            RESOLVED.add(key);
        }
    }
}
