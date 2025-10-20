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

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.SugarException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>I/O 异常</h3>
 *
 * <p>用于 {@link IOException} 异常的包装，也可能表示自定义 I/O 操作时的非预期错误。
 */
public class InputOutputException extends SugarException {
    private static final long serialVersionUID = -4134144656137285948L;

    public InputOutputException() {
        super();
    }

    public InputOutputException(@NotNull String message) {
        super(Validation.requireNotNull(message, "message must not be null."));
    }

    public InputOutputException(@NotNull String message, @NotNull IOException cause) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."));
    }

    public InputOutputException(@NotNull IOException cause) {
        super(Validation.requireNotNull(cause, "cause must not be null."));
    }

    protected InputOutputException(
            @NotNull String message,
            @NotNull IOException cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."),
                enableSuppression,
                writableStackTrace);
    }
}
