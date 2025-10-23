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

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.agent.exception.JavaagentException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h4>Java 探针工具</h4>
 *
 * <p>提供常用 Java 探针操作工具方法封装。
 */
public abstract class Javaagent {

    public static void premain(String arguments, @NotNull Instrumentation instrumentation) {
        doMain(arguments, instrumentation);
    }

    public static void agentmain(String arguments, @NotNull Instrumentation instrumentation) {
        doMain(arguments, instrumentation);
    }

    private static void doMain(String arguments, @NotNull Instrumentation instrumentation) throws JavaagentException {
        Validation.requireNotNull(instrumentation, "instrumentation must not be null.");
        setInstrumentation(instrumentation);
    }

    private static volatile Instrumentation instrumentation = null;

    private static void setInstrumentation(@NotNull Instrumentation instrumentation) {
        Validation.requireNotNull(instrumentation, "instrumentation must not be null.");
        Javaagent.instrumentation = instrumentation;
    }

    /**
     *
     *
     * <h4>判断当前是否已加载 Java 探针</h4>
     *
     * @return 是否已加载
     */
    public static boolean isLoaded() {
        return instrumentation != null;
    }

    /**
     *
     *
     * <h4>获取当前 {@link Instrumentation}</h4>
     *
     * <p>前提是 Java 探针已经被加载。
     *
     * @return 返回一个可用的 {@link Instrumentation}
     * @throws JavaagentException 如果 Java 探针没有被加载（即 {@code instrumentation == null}）
     * @see #isLoaded()
     */
    @NotNull
    public static Instrumentation instrumentation() throws JavaagentException {
        Instrumentation instrumentation = Javaagent.instrumentation;
        if (instrumentation == null) {
            throw new JavaagentException("instrumentation is not yet set.");
        }
        return instrumentation;
    }

    @NotNull
    private static String currentProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    private static volatile Class<?> VIRTUAL_MACHINE;
    private static volatile Method ATTACH;
    private static volatile Method LOAD_AGENT;
    private static volatile Method DETACH;

    /**
     *
     *
     * <h4>为当前进程加载 Java 探针</h4>
     *
     * @param javaagent Java 探针文件路径
     * @throws JavaagentException 如果加载 Java 探针失败
     */
    public static void load(@NotNull String javaagent) throws JavaagentException {
        load(currentProcessId(), javaagent);
    }

    /**
     *
     *
     * <h4>为指定进程加载 Java 探针</h4>
     *
     * @param processId 进程 ID
     * @param javaagent Java 探针文件路径
     * @throws JavaagentException 如果加载 Java 探针失败
     */
    public static void load(@NotNull String processId, @NotNull String javaagent) throws JavaagentException {
        Validation.requireNotNullOrBlank(javaagent, "javaagent must not be null or blank.");
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
                    virtualMachine = ATTACH.invoke(null, processId);
                    LOAD_AGENT.invoke(virtualMachine, javaagent);
                } finally {
                    if (virtualMachine != null) {
                        DETACH.invoke(virtualMachine);
                    }
                }
            } catch (ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchMethodException
                    | InvocationTargetException e) {
                throw new JavaagentException(e);
            }
        }
    }
}
