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
import team.idealstate.sugar.util.BooleanUtils;
import team.idealstate.sugar.util.ObjectUtils;
import team.idealstate.sugar.util.StringUtils;

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
     * <h4>验证对象并获取原值 </h4>
     *
     * @param <T> 对象的类型
     * @param object 对象
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原对象
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNotNull(Object, String)
     * @see ObjectUtils#isNull(Object)
     */
    @SuppressWarnings("ConstantValue")
    @Nullable
    public static <T> T requireNull(T object, @NotNull String feedback) throws IllegalArgumentException {
        if (!ObjectUtils.isNull(object)) {
            throw new IllegalArgumentException(feedback);
        }
        return object;
    }

    /**
     *
     *
     * <h4>验证对象并获取原值 </h4>
     *
     * @param <T> 对象的类型
     * @param object 对象
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原对象
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNull(Object, String)
     * @see ObjectUtils#isNotNull(Object)
     */
    @NotNull
    public static <T> T requireNotNull(T object, @NotNull String feedback) throws IllegalArgumentException {
        if (!ObjectUtils.isNotNull(object)) {
            throw new IllegalArgumentException(feedback);
        }
        return object;
    }

    /**
     *
     *
     * <h4>验证布尔表达式并获取原值 </h4>
     *
     * @param expression 布尔表达式
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔表达式结果
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNullOrFalse(Boolean, String)
     * @see BooleanUtils#isNullOrTrue(Boolean)
     */
    @Nullable
    public static Boolean requireNullOrTrue(Boolean expression, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!BooleanUtils.isNullOrTrue(expression)) {
            throw new IllegalArgumentException(feedback);
        }
        return expression;
    }

    /**
     *
     *
     * <h4>验证布尔表达式并获取原值 </h4>
     *
     * @param expression 布尔表达式
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔表达式结果
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNullOrTrue(Boolean, String)
     * @see BooleanUtils#isNullOrFalse(Boolean)
     */
    @Nullable
    public static Boolean requireNullOrFalse(Boolean expression, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!BooleanUtils.isNullOrFalse(expression)) {
            throw new IllegalArgumentException(feedback);
        }
        return expression;
    }

    /**
     *
     *
     * <h4>验证布尔表达式并获取原值 </h4>
     *
     * <p>需要注意的是，当 {@code expression} 的数据原型为引用类型且校验结果为非法时，{@code expression} 不一定为 {@code false}，详见 {@link Boolean} 的
     * {@code equals} 方法。
     *
     * @param expression 布尔表达式
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔表达式结果
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireFalse(Boolean, String)
     * @see BooleanUtils#isTrue(Boolean)
     */
    @SuppressWarnings("ConstantValue")
    @NotNull
    public static Boolean requireTrue(Boolean expression, @NotNull String feedback) throws IllegalArgumentException {
        if (!BooleanUtils.isTrue(expression)) {
            throw new IllegalArgumentException(feedback);
        }
        return expression;
    }

    /**
     *
     *
     * <h4>验证布尔表达式并获取原值 </h4>
     *
     * <p>需要注意的是，当 {@code expression} 的数据原型为引用类型且校验结果为非法时，{@code expression} 不一定为 {@code true}，详见 {@link Boolean} 的
     * {@code equals} 方法。
     *
     * @param expression 布尔表达式
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原布尔表达式结果
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireTrue(Boolean, String)
     * @see BooleanUtils#isFalse(Boolean)
     */
    @NotNull
    public static Boolean requireFalse(Boolean expression, @NotNull String feedback) throws IllegalArgumentException {
        if (!BooleanUtils.isFalse(expression)) {
            throw new IllegalArgumentException(feedback);
        }
        return expression;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNotNullOrEmpty(CharSequence, String)
     * @see StringUtils#isNullOrEmpty(CharSequence)
     */
    @Nullable
    public static <T extends CharSequence> T requireNullOrEmpty(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isNullOrEmpty(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNullOrEmpty(CharSequence, String)
     * @see StringUtils#isNotNullOrEmpty(CharSequence)
     */
    @NotNull
    public static <T extends CharSequence> T requireNotNullOrEmpty(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isNotNullOrEmpty(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNotEmpty(CharSequence, String)
     * @see StringUtils#isEmpty(CharSequence)
     */
    @NotNull
    public static <T extends CharSequence> T requireEmpty(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isEmpty(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireEmpty(CharSequence, String)
     * @see StringUtils#isNotEmpty(CharSequence)
     */
    @NotNull
    public static <T extends CharSequence> T requireNotEmpty(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isNotEmpty(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNotNullOrBlank(CharSequence, String)
     * @see StringUtils#isNullOrBlank(CharSequence)
     */
    @Nullable
    public static <T extends CharSequence> T requireNullOrBlank(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isNullOrBlank(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNullOrBlank(CharSequence, String)
     * @see StringUtils#isNotNullOrBlank(CharSequence)
     */
    @NotNull
    public static <T extends CharSequence> T requireNotNullOrBlank(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isNotNullOrBlank(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireNotBlank(CharSequence, String)
     * @see StringUtils#isBlank(CharSequence)
     */
    @NotNull
    public static <T extends CharSequence> T requireBlank(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isBlank(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }

    /**
     *
     *
     * <h4>验证字符序列并获取原值 </h4>
     *
     * @param charSequence 字符序列
     * @param feedback 数据非法时异常携带的反馈信息
     * @return 原字符序列
     * @throws IllegalArgumentException 数据非法时抛出，携带 {@code feedback} 作为信息
     * @see #requireBlank(CharSequence, String)
     * @see StringUtils#isNotBlank(CharSequence)
     */
    @NotNull
    public static <T extends CharSequence> T requireNotBlank(T charSequence, @NotNull String feedback)
            throws IllegalArgumentException {
        if (!StringUtils.isNotBlank(charSequence)) {
            throw new IllegalArgumentException(feedback);
        }
        return charSequence;
    }
}
