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

package team.idealstate.sugar.function.exception;

import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.SugarException;
import team.idealstate.sugar.function.WeakConsumer;
import team.idealstate.sugar.function.WeakFunction;
import team.idealstate.sugar.function.WeakPredicate;
import team.idealstate.sugar.function.WeakSupplier;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>弱异常处理函数异常</h3>
 *
 * <p>用于包装弱异常处理函数在调用过程中遇到的未处理的非运行时异常。
 *
 * @see WeakConsumer
 * @see WeakFunction
 * @see WeakPredicate
 * @see WeakSupplier
 */
public class WeakFunctionException extends SugarException {
    private static final long serialVersionUID = 7961922056407034332L;

    public WeakFunctionException() {
        super();
    }

    public WeakFunctionException(@NotNull String message) {
        super(Validation.requireNotNull(message, "message must not be null."));
    }

    public WeakFunctionException(@NotNull String message, @NotNull Throwable cause) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."));
    }

    public WeakFunctionException(@NotNull Throwable cause) {
        super(Validation.requireNotNull(cause, "cause must not be null."));
    }

    protected WeakFunctionException(
            @NotNull String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."),
                enableSuppression,
                writableStackTrace);
    }
}
