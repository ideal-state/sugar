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

import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.function.exception.WeakFunctionException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>逻辑表达式（弱异常处理）</h3>
 *
 * @param <T> 待判断的对象类型
 * @see Predicate
 */
@FunctionalInterface
public interface WeakPredicate<T> {

    /**
     *
     *
     * <h4>判断对象是否符合 </h4>
     *
     * <p>和 {@link Predicate#test(Object)} 不同的是，此方法的内部逻辑实现可以无需关注异常项的捕获。
     *
     * <p>如果判断过程中遇到未处理的异常，此方法会直接将其抛出。
     *
     * @param it 待判断的对象
     * @return 对象是否符合
     * @throws Throwable 判断过程中遇到的未处理异常
     * @see Predicate#test(Object)
     */
    boolean test(T it) throws Throwable;

    /**
     *
     *
     * <h4>逻辑与，在当前逻辑表达式后拼接另一个逻辑表达式</h4>
     *
     * @param other 待拼接的逻辑表达式
     * @return 新的复合逻辑表达式
     */
    @NotNull
    default WeakPredicate<T> and(@NotNull WeakPredicate<? super T> other) {
        Validation.isNotNull(other, "other must not be null.");
        return (t) -> test(t) && other.test(t);
    }

    /**
     *
     *
     * <h4>逻辑非，反转当前逻辑表达式的结果</h4>
     *
     * @return 新的复合逻辑表达式
     */
    @NotNull
    default WeakPredicate<T> negate() {
        return (t) -> !test(t);
    }

    /**
     *
     *
     * <h4>逻辑或，在当前逻辑表达式后拼接另一个逻辑表达式</h4>
     *
     * @param other 待拼接的逻辑表达式
     * @return 新的复合逻辑表达式
     */
    @NotNull
    default WeakPredicate<T> or(@NotNull WeakPredicate<? super T> other) {
        Validation.isNotNull(other, "other must not be null.");
        return (t) -> test(t) || other.test(t);
    }

    /**
     *
     *
     * <h4>将原生逻辑表达式转换为弱异常处理逻辑表达式 </h4>
     *
     * @param predicate 待转换的原生逻辑表达式
     * @return 携带原生逻辑表达式本身的弱异常处理逻辑表达式
     */
    @NotNull
    default WeakPredicate<T> convert(@NotNull Predicate<? super T> predicate) {
        Validation.isNotNull(predicate, "predicate must not be null.");
        return predicate::test;
    }

    /**
     *
     *
     * <h4>将此逻辑表达式转换为原生逻辑表达式</h4>
     *
     * <p>对于判断过程中未处理的非运行时异常，其将会被包装为 {@link WeakFunctionException} 并抛出，否则直接抛出。
     *
     * @return 携带此逻辑表达式本身和未处理异常的处理逻辑的原生逻辑表达式
     */
    @NotNull
    default Predicate<T> convert() {
        return (T it) -> {
            try {
                return test(it);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new WeakFunctionException(e);
            }
        };
    }
}
