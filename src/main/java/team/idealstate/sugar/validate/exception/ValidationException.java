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

package team.idealstate.sugar.validate.exception;

import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.SugarException;

/**
 *
 *
 * <h3>数据校验异常 </h3>
 *
 * <p>通常在对非法数据进行严格的校验时被抛出。
 */
public class ValidationException extends SugarException {

    private static final long serialVersionUID = -5759464121205955422L;

    public ValidationException() {
        super();
    }

    public ValidationException(@NotNull String message) {
        super(message);
    }

    public ValidationException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    public ValidationException(@NotNull Throwable cause) {
        super(cause);
    }

    protected ValidationException(
            @NotNull String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
