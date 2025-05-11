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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.eclipse.aether.graph.Dependency;
import org.objectweb.asm.ClassReader;
import team.idealstate.sugar.agent.Javaagent;
import team.idealstate.sugar.exception.SugarException;
import team.idealstate.sugar.maven.MavenResolver;
import team.idealstate.sugar.maven.PomXmlResolver;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

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
                            .toURI()),
                    null);
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
                sugarClassName = sugarClassName.replace('.', '/');
                String[] interfaces = new ClassReader(buffer).getInterfaces();
                if (interfaces.length != 0 && Arrays.asList(interfaces).contains(sugarClassName)) {
                    loadDependencies(file, null);
                } else {
                    try (JarFile jarFile = new JarFile(file)) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        boolean found = false;
                        EACH_ENTRIES:
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            if (entry.isDirectory()) {
                                continue;
                            }
                            String entryName = entry.getName();
                            if (!entryName.endsWith(".class")) {
                                continue;
                            }
                            String className1 = entryName
                                    .substring(0, entryName.length() - 6)
                                    .replace('\\', '/');
                            try (InputStream input = jarFile.getInputStream(entry)) {
                                ClassReader classReader = new ClassReader(input);
                                if (!className1.equals(classReader.getClassName())) {
                                    continue;
                                }
                                interfaces = classReader.getInterfaces();
                                if (interfaces.length == 0) {
                                    continue;
                                }
                                for (String anInterface : interfaces) {
                                    if (sugarClassName.equals(anInterface)) {
                                        found = true;
                                        break EACH_ENTRIES;
                                    }
                                }
                            }
                        }
                        if (!found) {
                            return null;
                        }
                        loadDependencies(file, jarFile);
                    } catch (IOException e) {
                        throw new SugarException(e);
                    }
                }
                return null;
            } finally {
                RESOLVED.add(key);
                lock.unlock();
            }
        } catch (ClassNotFoundException | URISyntaxException e) {
            return null;
        }
    }

    private static String makeKey(@NotNull File file) {
        return file.toPath().normalize().toString();
    }

    private void loadDependencies(@NotNull File file, JarFile jarFile) {
        String key = makeKey(file);
        if (RESOLVED.contains(key)) {
            return;
        }
        try {
            Set<Dependency> dependencies = new LinkedHashSet<>(128);
            try (JarFile jar = jarFile == null ? new JarFile(file) : jarFile) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entry.isDirectory()
                            || !entryName.startsWith("META-INF/maven/")
                            || !entryName.endsWith("pom.xml")) {
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
