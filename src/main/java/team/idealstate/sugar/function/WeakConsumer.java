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

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.function.exception.WeakFunctionException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>消费者（弱异常处理）</h3>
 *
 * @param <T> 待消费的对象类型
 * @see Consumer
 */
@FunctionalInterface
public interface WeakConsumer<T> {

    /**
     *
     *
     * <h4>执行消费</h4>
     *
     * <p>和 {@link Consumer#accept(Object)} 不同的是，此方法的内部逻辑实现可以无需关注异常项的捕获。
     *
     * <p>如果消费过程中遇到未处理的异常，此方法会直接将其抛出。
     *
     * @param it 待消费的对象
     * @throws Throwable 消费过程中遇到的未处理异常
     * @see Consumer#accept(Object)
     */
    void consume(T it) throws Throwable;

    /**
     *
     *
     * <h4>在此消费者前拼接另一个消费者</h4>
     *
     * <p>复合消费者在进行消费操作时，先进行当前消费者 {@code before} 的消费，后进行 {@code this} 的消费。
     *
     * @param before 待拼接的消费者
     * @return 新的复合消费者
     * @see #andThen(WeakConsumer)
     */
    @NotNull
    default WeakConsumer<T> compose(@NotNull WeakConsumer<? super T> before) {
        Validation.isNotNull(before, "before must not be null.");
        return (T it) -> {
            before.consume(it);
            consume(it);
        };
    }

    /**
     *
     *
     * <h4>在此消费者后拼接另一个消费者</h4>
     *
     * <p>复合消费者在进行消费操作时，先进行当前消费者 {@code this} 的消费，后进行 {@code after} 的消费。
     *
     * @param after 待拼接的消费者
     * @return 新的复合消费者
     * @see Consumer#andThen(Consumer)
     */
    @NotNull
    default WeakConsumer<T> andThen(@NotNull WeakConsumer<? super T> after) {
        Validation.isNotNull(after, "after must not be null.");
        return (T it) -> {
            consume(it);
            after.consume(it);
        };
    }

    /**
     *
     *
     * <h4>将原生消费者转换为弱异常处理消费者 </h4>
     *
     * @param consumer 待转换的原生消费者
     * @return 携带原生消费者本身的弱异常处理消费者
     */
    @NotNull
    default WeakConsumer<T> convert(@NotNull Consumer<? super T> consumer) {
        Validation.isNotNull(consumer, "consumer must not be null.");
        return consumer::accept;
    }

    /**
     *
     *
     * <h4>将此消费者转换为原生消费者</h4>
     *
     * <p>对于消费过程中未处理的非运行时异常，其将会被包装为 {@link WeakFunctionException} 并抛出，否则直接抛出。
     *
     * @return 携带此消费者本身和未处理异常的处理逻辑的原生消费者
     */
    @NotNull
    default Consumer<T> convert() {
        return (T it) -> {
            try {
                consume(it);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new WeakFunctionException(e);
            }
        };
    }
}
