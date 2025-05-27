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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarFile;
import team.idealstate.sugar.SugarLoggerLoader;
import team.idealstate.sugar.agent.exception.JavaagentException;
import team.idealstate.sugar.banner.Banner;
import team.idealstate.sugar.exception.SugarException;
import team.idealstate.sugar.logging.Log;
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
        Validation.notNull(instrumentation, "Instrumentation must not be null.");
        setInstrumentation(instrumentation);
        Banner.lines(Javaagent.class).forEach(Log::info);
        SugarLoggerLoader.addTo(instrumentation);
    }

    private static final Set<String> LOADED = new CopyOnWriteArraySet<>();

    public static void appendToSystemClassLoaderSearch(@NotNull Map<String, File> artifacts) {
        Instrumentation instrumentation = instrumentation();
        Validation.notNull(artifacts, "Artifacts must not be null.");
        if (artifacts.isEmpty()) {
            return;
        }
        for (Map.Entry<String, File> entry : artifacts.entrySet()) {
            File artifact = entry.getValue();
            if (artifact.isDirectory() || !artifact.getName().endsWith(".jar")) {
                continue;
            }
            String path = artifact.toPath().normalize().toString();
            if (!LOADED.add(path)) {
                continue;
            }
            String id = entry.getKey();
            try {
                instrumentation.appendToSystemClassLoaderSearch(new JarFile(artifact));
                Log.info(() -> String.format("Append to system classpath: '%s'", id));
            } catch (IOException e) {
                throw new SugarException(e);
            }
        }
    }

    private static volatile Instrumentation instrumentation = null;

    private static void setInstrumentation(@NotNull Instrumentation instrumentation) {
        Validation.requireNotNull(instrumentation, "Instrumentation must not be null.");
        Javaagent.instrumentation = instrumentation;
    }

    public static boolean isLoaded() {
        return instrumentation != null;
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
