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

import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>顶级异常</h3>
 *
 * <p>用作库功能异常项的父类，主要用作非运行时异常的包装。
 */
public abstract class SugarException extends RuntimeException {
    private static final long serialVersionUID = 4894244095571633722L;

    public SugarException() {
        super();
    }

    public SugarException(@NotNull String message) {
        super(Validation.requireNotNull(message, "message must not be null."));
    }

    public SugarException(@NotNull String message, @NotNull Throwable cause) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."));
    }

    public SugarException(@NotNull Throwable cause) {
        super(Validation.requireNotNull(cause, "cause must not be null."));
    }

    protected SugarException(
            @NotNull String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."),
                enableSuppression,
                writableStackTrace);
    }
}
