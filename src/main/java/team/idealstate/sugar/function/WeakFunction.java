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

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.exception.WeakException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>函数（弱异常处理）</h3>
 *
 * @param <T> 函数入参对象的类型
 * @param <R> 函数返回值的类型
 * @see Function
 */
@FunctionalInterface
public interface WeakFunction<T, R> {

    /**
     *
     *
     * <h4>调用函数</h4>
     *
     * <p>和 {@link Function#apply(Object)} 不同的是，此方法的内部逻辑实现可以无需关注异常项的捕获。
     *
     * <p>如果调用过程中遇到未处理的异常，此方法会直接将其抛出。
     *
     * @param it 函数入参对象
     * @return 函数返回值
     * @throws Throwable 调用过程中遇到的未处理异常
     * @see Function#apply(Object)
     */
    R call(T it) throws Throwable;

    /**
     *
     *
     * <h4>在当前函数入参前拼接另一个函数</h4>
     *
     * <p>复合函数在进行调用时，先进行函数 {@code before} 的调用，其结果为 {@code T}，后继续将 {@code T} 作为入参进行函数 {@code this} 的调用。
     *
     * @param before 待拼接的函数
     * @param <V> 待拼接的函数的入参对象类型
     * @return 新的复合函数
     * @see Function#compose(Function)
     */
    @NotNull
    default <V> WeakFunction<V, R> compose(@NotNull WeakFunction<? super V, ? extends T> before) {
        Validation.requireNotNull(before, "before must not be null.");
        return (v) -> call(before.call(v));
    }

    /**
     *
     *
     * <h4>在当前函数后拼接另一个函数</h4>
     *
     * <p>复合函数在进行调用时，先进行函数 {@code this} 的调用，其结果为 {@code R}，后继续将 {@code R} 作为入参进行函数 {@code after} 的调用。
     *
     * @param after 待拼接的函数
     * @param <V> 待拼接的函数的入参对象类型
     * @return 新的复合函数
     * @see Function#andThen(Function)
     */
    @NotNull
    default <V> WeakFunction<T, V> andThen(@NotNull WeakFunction<? super R, ? extends V> after) {
        Validation.requireNotNull(after, "after must not be null.");
        return (t) -> after.call(call(t));
    }

    /**
     *
     *
     * <h4>将原生函数转换为弱异常处理函数 </h4>
     *
     * @param function 待转换的原生函数
     * @return 携带原生函数本身的弱异常处理函数
     */
    @NotNull
    default WeakFunction<T, R> convert(@NotNull Function<? super T, ? extends R> function) {
        Validation.requireNotNull(function, "function must not be null.");
        return function::apply;
    }

    /**
     *
     *
     * <h4>将此函数转换为原生函数</h4>
     *
     * <p>对于供应过程中未处理的异常，其将会被 {@link WeakException} 尝试抑制，然后抛出。
     *
     * @return 携带此函数本身和未处理异常的处理逻辑的原生函数
     */
    @NotNull
    default Function<T, R> convert() {
        return (T it) -> {
            try {
                return call(it);
            } catch (Throwable e) {
                throw WeakException.suppress(e);
            }
        };
    }
}
