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

package team.idealstate.sugar.agent.exception;

import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.SugarException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>Java 探针异常</h3>
 *
 * <p>用于包装 Java 探针运行期间的非运行时异常。
 */
public class JavaagentException extends SugarException {

    private static final long serialVersionUID = -6344832583468274488L;

    public JavaagentException() {
        super();
    }

    public JavaagentException(@NotNull String message) {
        super(Validation.requireNotNull(message, "message must not be null."));
    }

    public JavaagentException(@NotNull String message, @NotNull Throwable cause) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."));
    }

    public JavaagentException(@NotNull Throwable cause) {
        super(Validation.requireNotNull(cause, "cause must not be null."));
    }

    protected JavaagentException(
            @NotNull String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(
                Validation.requireNotNull(message, "message must not be null."),
                Validation.requireNotNull(cause, "cause must not be null."),
                enableSuppression,
                writableStackTrace);
    }
}
