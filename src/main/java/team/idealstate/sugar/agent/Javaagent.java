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

package team.idealstate.sugar.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.util.artifact.JavaScopes;
import team.idealstate.sugar.SugarLibraryLoader;
import team.idealstate.sugar.agent.exception.JavaagentException;
import team.idealstate.sugar.banner.Banner;
import team.idealstate.sugar.bundled.Bundled;
import team.idealstate.sugar.exception.SugarException;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.maven.MavenResolver;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

public abstract class Javaagent {

    public static void premain(String arguments, @NotNull Instrumentation instrumentation) {
        try {
            doMain(arguments, instrumentation);
        } catch (IOException e) {
            throw new SugarException(e);
        }
    }

    public static void agentmain(String arguments, @NotNull Instrumentation instrumentation) {
        try {
            doMain(arguments, instrumentation);
        } catch (IOException e) {
            throw new SugarException(e);
        }
    }

    private static void doMain(String arguments, @NotNull Instrumentation instrumentation) throws IOException {
        Validation.requireNotNull(instrumentation, "Instrumentation must not be null.");
        Banner.lines(Javaagent.class).forEach(Log::info);
        Bundled.release(Javaagent.class, new File("./"));
        MavenResolver mavenResolver = new MavenResolver();
        if (arguments != null) {
            List<Artifact> artifacts = mavenResolver.resolve(Collections.singletonList(new Dependency(
                    new DefaultArtifact("team.idealstate.sugar:sugar-next:" + arguments), JavaScopes.COMPILE)));
            if (!artifacts.isEmpty()) {
                for (Artifact artifact : artifacts) {
                    File artifactFile = artifact.getFile();
                    if (!artifactFile.exists()
                            || artifactFile.isDirectory()
                            || !artifactFile.getName().equals(".jar")) {
                        continue;
                    }
                    Log.info(String.format("Loading dependency '%s'...", artifactFile.getAbsolutePath()));
                    instrumentation.appendToSystemClassLoaderSearch(new JarFile(artifactFile));
                }
            }
        }
        instrumentation.addTransformer(new SugarLibraryLoader(instrumentation, mavenResolver));
        setInstrumentation(instrumentation);
    }

    private static volatile Instrumentation instrumentation = null;

    private static void setInstrumentation(@NotNull Instrumentation instrumentation) {
        Validation.requireNotNull(instrumentation, "Instrumentation must not be null.");
        Javaagent.instrumentation = instrumentation;
    }

    @NotNull
    public static Instrumentation instrumentation() {
        return Validation.requireNotNull(Javaagent.instrumentation, "Instrumentation must not be null.");
    }

    @NotNull
    public static String currentProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    private static volatile Class<?> VIRTUAL_MACHINE;
    private static volatile Method ATTACH;
    private static volatile Method LOAD_AGENT;
    private static volatile Method DETACH;

    public static void load(@NotNull String javaagent) {
        Validation.requireNotNullOrBlank(javaagent, "Javaagent must not be null or blank.");
        synchronized (Javaagent.class) {
            Object virtualMachine = null;
            try {
                if (VIRTUAL_MACHINE == null) {
                    VIRTUAL_MACHINE = Class.forName("com.sun.tools.attach.VirtualMachine");
                }
                if (ATTACH == null) {
                    ATTACH = VIRTUAL_MACHINE.getMethod("attach", String.class);
                }
                if (LOAD_AGENT == null) {
                    LOAD_AGENT = VIRTUAL_MACHINE.getMethod("loadAgent", String.class);
                }
                if (DETACH == null) {
                    DETACH = VIRTUAL_MACHINE.getMethod("detach");
                }
                try {
                    virtualMachine = ATTACH.invoke(null, currentProcessId());
                    LOAD_AGENT.invoke(virtualMachine, javaagent);
                } finally {
                    if (virtualMachine != null) {
                        DETACH.invoke(virtualMachine);
                    }
                }
            } catch (NoSuchMethodException
                    | ClassNotFoundException
                    | IllegalAccessException
                    | InvocationTargetException e) {
                throw new JavaagentException(e);
            }
        }
    }
}
