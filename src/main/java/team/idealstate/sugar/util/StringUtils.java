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

package team.idealstate.sugar.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>字符串工具</h3>
 *
 * <p>提供常用字符串操作工具方法封装。
 */
public abstract class StringUtils {

    /**
     *
     *
     * <h4>判断字符串是否为 {@code null} 或 {@code ""} </h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     */
    public static boolean isNullOrEmpty(@Nullable CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    /**
     *
     *
     * <h4>判断字符串是否为 {@code ""} </h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     */
    public static boolean isEmpty(@NotNull CharSequence charSequence) {
        Validation.isNotNull(charSequence, "charSequence must not be null.");
        return charSequence.length() == 0;
    }

    /**
     *
     *
     * <h4>判断字符串是否不为 {@code null} 或 {@code ""} </h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     */
    public static boolean isNotNullOrEmpty(@Nullable CharSequence charSequence) {
        return charSequence != null && charSequence.length() != 0;
    }

    /**
     *
     *
     * <h4>判断字符串是否不为 {@code ""} </h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     */
    public static boolean isNotEmpty(@NotNull CharSequence charSequence) {
        Validation.isNotNull(charSequence, "charSequence must not be null.");
        return charSequence.length() != 0;
    }

    /**
     *
     *
     * <h4>判断字符串是否为 {@code null} 或仅包含空白字符 </h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     * @see #isNullOrEmpty(CharSequence)
     * @see Character#isWhitespace(char)
     */
    public static boolean isNullOrBlank(@Nullable CharSequence charSequence) {
        if (isNullOrEmpty(charSequence)) {
            return true;
        }
        final int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     *
     * <h4>判断字符串是否仅包含空白字符 </h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     * @see #isEmpty(CharSequence)
     * @see Character#isWhitespace(char)
     */
    public static boolean isBlank(@NotNull CharSequence charSequence) {
        Validation.isNotNull(charSequence, "charSequence must not be null.");
        if (isEmpty(charSequence)) {
            return true;
        }
        final int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     *
     * <h4>判断字符串是否不为 {@code null} 或仅包含空白字符</h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     * @see #isNullOrBlank(CharSequence)
     */
    public static boolean isNotNullOrBlank(@Nullable CharSequence charSequence) {
        return !isNullOrBlank(charSequence);
    }

    /**
     *
     *
     * <h4>判断字符串是否不仅包含空白字符</h4>
     *
     * @param charSequence 字符串
     * @return 是否符合
     * @see #isBlank(CharSequence)
     */
    public static boolean isNotBlank(@NotNull CharSequence charSequence) {
        Validation.isNotNull(charSequence, "charSequence must not be null.");
        return !isBlank(charSequence);
    }

    /**
     *
     *
     * <h4>统计字符串中指定字符出现的次数</h4>
     *
     * @param charSequence 字符串
     * @param fragment 字符片段
     * @return 匹配次数
     */
    public static int countMatches(@NotNull CharSequence charSequence, @NotNull Character fragment) {
        Validation.isNotNull(charSequence, "charSequence must not be null.");
        Validation.isNotNull(fragment, "fragment must not be null.");
        if (isEmpty(charSequence)) {
            return 0;
        }
        final char fragmentChar = fragment;
        final int stringLen = charSequence.length();
        int count = 0;
        for (int i = 0; i < stringLen; i++) {
            char c = charSequence.charAt(i);
            if (c == fragmentChar) {
                count = count + 1;
            }
        }
        return count;
    }

    /**
     *
     *
     * <h4>统计字符串中指定字符串出现的次数</h4>
     *
     * @param charSequence 字符串
     * @param fragment 字符串片段
     * @return 匹配次数
     */
    public static int countMatches(@NotNull CharSequence charSequence, @NotNull CharSequence fragment) {
        Validation.isNotNull(charSequence, "charSequence must not be null.");
        Validation.isNotNull(fragment, "fragment must not be null.");
        if (isEmpty(charSequence) || isEmpty(fragment)) {
            return 0;
        }
        if (fragment.length() == 1) {
            return countMatches(charSequence, fragment.charAt(0));
        }
        final int stringLen = charSequence.length();
        final int matchedLen = fragment.length();
        if (stringLen < matchedLen) {
            return 0;
        }
        int count = 0;
        int matching = 0;
        for (int i = 0; i < stringLen; i++) {
            char c = charSequence.charAt(i);
            if (c == fragment.charAt(matching)) {
                matching = matching + 1;
                if (matching >= matchedLen) {
                    matching = 0;
                    count = count + 1;
                }
            } else {
                matching = 0;
            }
        }
        return count;
    }
}
