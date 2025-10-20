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
import org.jetbrains.annotations.Nullable;
import team.idealstate.sugar.validate.exception.ValidationException;

/**
 *
 *
 * <h3>数据校验工具</h3>
 *
 * <p>常用数据校验器的工具形式封装。
 */
@SuppressWarnings("unused")
public abstract class Validation {

    /**
     *
     *
     * <h4>验证对象是否为 {@code null} </h4>
     *
     * @param object 对象，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     */
    @SuppressWarnings("ConstantValue")
    public static void isNull(@Nullable Object object, @NotNull String feedback) throws ValidationException {
        if (object != null) {
            throw new ValidationException(feedback);
        }
        assert object == null;
    }

    /**
     *
     *
     * <h4>验证对象是否为 {@code null} 并获取原值 </h4>
     *
     * @param <T> 对象的类型
     * @param object 对象，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原对象
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     * @see #isNull(Object, String)
     */
    @Nullable
    public static <T> T requireNull(@Nullable T object, @NotNull String feedback) throws ValidationException {
        isNull(object, feedback);
        return null;
    }

    /**
     *
     *
     * <h4>验证对象是否非 {@code null} </h4>
     *
     * @param object 对象，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     */
    @SuppressWarnings("ConstantValue")
    public static void isNotNull(@Nullable Object object, @NotNull String feedback) throws ValidationException {
        if (object == null) {
            throw new ValidationException(feedback);
        }
        assert object != null;
    }

    /**
     *
     *
     * <h4>验证对象是否非 {@code null} 并获取原值 </h4>
     *
     * @param <T> 对象的类型
     * @param object 对象，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原对象
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     * @see #isNotNull(Object, String)
     */
    @NotNull
    public static <T> T requireNotNull(@Nullable T object, @NotNull String feedback) throws ValidationException {
        isNotNull(object, feedback);
        return object;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式 </h4>
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     */
    @SuppressWarnings("ConstantValue")
    public static void isNullOrTrue(@Nullable Boolean expression, @NotNull String feedback) throws ValidationException {
        if (expression != null && !expression) {
            throw new ValidationException(feedback);
        }
        assert expression == null || expression;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式并获取原值 </h4>
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔值/条件表达式结果
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     * @see #isNullOrTrue(Boolean, String)
     */
    @Nullable
    public static Boolean requireNullOrTrue(@Nullable Boolean expression, @NotNull String feedback)
            throws ValidationException {
        isNullOrTrue(expression, feedback);
        return expression;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式 </h4>
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     */
    @SuppressWarnings("ConstantValue")
    public static void isNullOrFalse(@Nullable Boolean expression, @NotNull String feedback)
            throws ValidationException {
        if (expression != null && expression) {
            throw new ValidationException(feedback);
        }
        assert expression == null || !expression;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式并获取原值 </h4>
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔值/条件表达式结果
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     * @see #isNullOrFalse(Boolean, String)
     */
    @Nullable
    public static Boolean requireNullOrFalse(@Nullable Boolean expression, @NotNull String feedback)
            throws ValidationException {
        isNullOrFalse(expression, feedback);
        return expression;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式 </h4>
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     */
    @SuppressWarnings("ConstantValue")
    public static void isTrue(@Nullable Boolean expression, @NotNull String feedback) throws ValidationException {
        if (!Boolean.TRUE.equals(expression)) {
            throw new ValidationException(feedback);
        }
        assert expression;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式并获取原值 </h4>
     *
     * <p>需要注意的是，当 {@code expression} 的数据原型为引用类型且校验结果为非法时，{@code expression} 不一定为 {@code false}，详见 {@link Boolean} 的
     * {@code equals} 方法。
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔值/条件表达式结果
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     * @see #isTrue(Boolean, String)
     */
    @NotNull
    public static Boolean requireTrue(@Nullable Boolean expression, @NotNull String feedback)
            throws ValidationException {
        isTrue(expression, feedback);
        return true;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式 </h4>
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     */
    @SuppressWarnings("ConstantValue")
    public static void isFalse(@Nullable Boolean expression, @NotNull String feedback) throws ValidationException {
        if (!Boolean.FALSE.equals(expression)) {
            throw new ValidationException(feedback);
        }
        assert expression != null && !expression;
    }

    /**
     *
     *
     * <h4>验证布尔值或条件表达式并获取原值 </h4>
     *
     * <p>需要注意的是，当 {@code expression} 的数据原型为引用类型且校验结果为非法时，{@code expression} 不一定为 {@code true}，详见 {@link Boolean} 的
     * {@code equals} 方法。
     *
     * @param expression 布尔值或条件表达式，也许为 {@code null}
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔值/条件表达式结果
     * @throws ValidationException 数据非法时抛出，携带 {@code feedback} 信息
     * @see #isFalse(Boolean, String)
     */
    @NotNull
    public static Boolean requireFalse(@Nullable Boolean expression, @NotNull String feedback)
            throws ValidationException {
        isFalse(expression, feedback);
        return false;
    }
}
