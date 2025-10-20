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

package team.idealstate.sugar.validate;

import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.validate.exception.ValidationException;

/**
 *
 *
 * <h3>数据校验器</h3>
 *
 * <p>提供基本的数据验证接口，用于对数据进行合法性验证。
 *
 * @param <T> 待验证的数据类型
 */
public interface Validator<T> {

    /**
     *
     *
     * <h4>验证数据</h4>
     *
     * <p>主要验证接口，提供实际上的数据校验逻辑实现。
     *
     * @param data 待验证的数据
     * @return 数据合法性是否符合预期
     */
    boolean validate(T data);

    /**
     *
     *
     * <h4>验证数据（严格）</h4>
     *
     * <p>数据非法时抛出异常，何时抛出则取决于主要验证接口的具体实现。
     *
     * @param data 待验证的数据
     * @param feedback 数据合法性不符合预期时提供的异常反馈信息
     * @throws ValidationException 数据合法性不符合预期时抛出的异常
     * @see #validate(T)
     */
    default void validateOrElseThrow(T data, @NotNull String feedback) throws ValidationException {
        if (!validate(data)) {
            throw new ValidationException(feedback);
        }
    }

    /**
     *
     *
     * <h4>验证数据（严格）并获取原数据</h4>
     *
     * <p>数据合法时将原数据作为返回，非法时则抛出异常。
     *
     * @param data 待验证的数据
     * @param feedback 数据合法性不符合预期时提供的异常反馈信息
     * @return 原数据
     * @throws ValidationException 数据合法性不符合预期时抛出的异常
     * @see #validateOrElseThrow(T, String)
     */
    default T validateAndGet(T data, @NotNull String feedback) throws ValidationException {
        validateOrElseThrow(data, feedback);
        return data;
    }
}
