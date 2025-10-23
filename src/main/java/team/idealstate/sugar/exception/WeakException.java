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

package team.idealstate.sugar.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.SugarException;
import team.idealstate.sugar.function.WeakConsumer;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>弱异常</h3>
 *
 * <p>用于包装未处理的非运行时异常以达到抑制的目的。
 */
public final class WeakException extends SugarException {
    private static final long serialVersionUID = 7961922056407034332L;

    /**
     *
     *
     * <h4>抑制异常</h4>
     *
     * @param cause 异常本身
     * @return 若 {@code cause} 为 {@link RuntimeException} 时直接返回其本身，否则将其包装为 {@link WeakException} 并返回
     */
    @NotNull
    public static RuntimeException suppress(@NotNull Throwable cause) {
        Validation.requireNotNull(cause, "cause must not be null.");
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new WeakException(cause);
    }

    private WeakException(@NotNull Throwable cause) {
        super(cause);
    }

    /** @return 新的弱异常处理器 */
    @NotNull
    public Handler handler() {
        return new Handler(this);
    }

    /**
     *
     *
     * <h3>弱异常处理器</h3>
     *
     * <p>高效（也许吧？）地处理弱异常。
     */
    @SuppressWarnings("rawtypes")
    public static final class Handler {

        private final WeakException weakException;
        private final Map<Class, WeakConsumer> handlers = new LinkedHashMap<>();
        private volatile boolean handled = false;

        private Handler(@NotNull WeakException weakException) {
            this.weakException = weakException;
        }

        /**
         *
         *
         * <h4>捕获弱异常的实际原因并处理它</h4>
         *
         * <p>此方法本身不包含处理逻辑，它仅将待捕获的异常类型和处理器存入计划中。
         *
         * <p>需要注意的是，最终被选中的处理器最多只有一个，遵循先进先出。
         *
         * @param causeType 异常类
         * @param handler 处理器
         * @param <T> 异常类型
         * @return 处理器自身，用于链式调用
         * @see #handle()
         */
        public <T extends Throwable> Handler catching(@NotNull Class<T> causeType, @NotNull WeakConsumer<T> handler) {
            Validation.requireNotNull(causeType, "causeType must not be null.");
            Validation.requireNotNull(handler, "handler must not be null.");
            handlers.put(causeType, handler);
            return this;
        }

        /**
         *
         *
         * <h4>处理弱异常</h4>
         *
         * <p>作为弱异常的实际处理逻辑，它将迭代计划中的处理器以实现异常类的匹配和最终处理。
         *
         * <p>需要注意的是，最终被选中的处理器最多只有一个。
         *
         * <p>若计划中没有任何处理器能够处理异常，它将再次抛出弱异常本身。
         *
         * @see #catching(Class, WeakConsumer)
         */
        @SuppressWarnings("unchecked")
        public void handle() {
            if (handled) {
                return;
            }
            synchronized (this) {
                if (handled) {
                    return;
                }
                if (!handlers.isEmpty()) {
                    Throwable cause = weakException.getCause();
                    for (Map.Entry<Class, WeakConsumer> entry : handlers.entrySet()) {
                        Class causeType = entry.getKey();
                        if (causeType.isInstance(cause)) {
                            entry.getValue().convert().accept(causeType.cast(cause));
                            return;
                        }
                    }
                    this.handled = true;
                    throw weakException;
                }
            }
        }
    }
}
