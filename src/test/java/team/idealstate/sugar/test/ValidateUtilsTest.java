/*
 *    Copyright 2024 ideal-state
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

package team.idealstate.sugar.test;

import org.junit.jupiter.api.Test;
import team.idealstate.sugar.validation.ValidateUtils;
import team.idealstate.sugar.validation.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * <p>ValidateUtilsTest</p>
 *
 * <p>创建于 2024/7/18 上午7:57</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidateUtilsTest {

    private static final Object NULL = null;
    private static final Object NOT_NULL = new Object();
    private static final String MESSAGE = "message";

    @Test
    public void validateIsNull() {
        assertDoesNotThrow(() -> ValidateUtils.isNull(NULL, MESSAGE), MESSAGE);
        assertThrowsExactly(ValidationException.class, () -> ValidateUtils.isNull(NOT_NULL, MESSAGE));
    }

    @Test
    public void validateNotNull() {
        assertDoesNotThrow(() -> ValidateUtils.notNull(NOT_NULL, MESSAGE), MESSAGE);
        assertThrowsExactly(ValidationException.class, () -> ValidateUtils.notNull(NULL, MESSAGE));
    }
}
