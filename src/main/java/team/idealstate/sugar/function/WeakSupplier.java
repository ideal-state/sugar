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

package team.idealstate.sugar.function;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.exception.WeakException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>供应方（弱异常处理）</h3>
 *
 * @param <T> 供应方供应的对象的类型
 * @see Supplier
 */
@FunctionalInterface
public interface WeakSupplier<T> {

    /**
     *
     *
     * <h4>供应对象 </h4>
     *
     * <p>和 {@link Supplier#get()} 不同的是，此方法的内部逻辑实现可以无需关注异常项的捕获。
     *
     * <p>如果供应过程中遇到未处理的异常，此方法会直接将其抛出。
     *
     * @return 供应的对象
     * @throws Throwable 供应过程中遇到的未处理异常
     * @see Supplier#get()
     */
    T supply() throws Throwable;

    /**
     *
     *
     * <h4>将原生供应方转换为弱异常处理供应方 </h4>
     *
     * @param supplier 待转换的原生供应方
     * @return 携带原生供应方本身的弱异常处理供应方
     */
    @NotNull
    default WeakSupplier<T> convert(@NotNull Supplier<? extends T> supplier) {
        Validation.requireNotNull(supplier, "supplier must not be null.");
        return supplier::get;
    }

    /**
     *
     *
     * <h4>将此供应方转换为原生供应方</h4>
     *
     * <p>对于供应过程中未处理的异常，其将会被 {@link WeakException} 尝试抑制，然后抛出。
     *
     * @return 携带此供应方本身和未处理异常的处理逻辑的原生供应方
     */
    @NotNull
    default Supplier<T> convert() {
        return () -> {
            try {
                return supply();
            } catch (Throwable e) {
                throw WeakException.suppress(e);
            }
        };
    }
}
