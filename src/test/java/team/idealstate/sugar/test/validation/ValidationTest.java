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

package team.idealstate.sugar.test.validation;

import org.junit.jupiter.api.Test;
import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class ValidationTest {

    private static final Boolean TRUE = true;
    private static final Boolean FALSE = false;
    private static final Object NULL = null;
    private static final Object NOT_NULL = new Object();
    private static final String MESSAGE = "message";

    @Test
    public void isTrue() {
        assertDoesNotThrow(() -> Validation.vote(TRUE, MESSAGE), MESSAGE);
        assertThrowsExactly(ValidationException.class, () -> Validation.isNull(FALSE, MESSAGE));
    }

    @Test
    public void notTrue() {
        assertDoesNotThrow(() -> Validation.vote(FALSE, MESSAGE), MESSAGE);
        assertThrowsExactly(ValidationException.class, () -> Validation.notNull(TRUE, MESSAGE));
    }

    @Test
    public void isNull() {
        assertDoesNotThrow(() -> Validation.isNull(NULL, MESSAGE), MESSAGE);
        assertThrowsExactly(ValidationException.class, () -> Validation.isNull(NOT_NULL, MESSAGE));
    }

    @Test
    public void notNull() {
        assertDoesNotThrow(() -> Validation.notNull(NOT_NULL, MESSAGE), MESSAGE);
        assertThrowsExactly(ValidationException.class, () -> Validation.notNull(NULL, MESSAGE));
    }
}
