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

package team.idealstate.sugar.string.manipulation;

import team.idealstate.sugar.string.StringUtils;
import team.idealstate.sugar.validation.ValidateUtils;
import team.idealstate.sugar.validation.annotation.NotNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * <p>StringJoiner</p>
 *
 * <p>创建于 2024/3/27 6:16</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class StringJoiner {

    private static final int DEFAULT_INITIAL_CAPACITY = 64;
    private final String delimiter;
    private final boolean delimiterIsEmpty;
    private final String prefix;
    private final boolean prefixIsEmpty;
    private final String suffix;
    private final boolean suffixIsEmpty;
    private final StringBuilder builder;

    public StringJoiner(@NotNull CharSequence delimiter) {
        this(delimiter, "", "");
    }

    public StringJoiner(@NotNull CharSequence delimiter, @NotNull CharSequence prefix, @NotNull CharSequence suffix) {
        this(DEFAULT_INITIAL_CAPACITY, delimiter, prefix, suffix);
    }

    public StringJoiner(int initialCapacity, @NotNull CharSequence delimiter) {
        this(initialCapacity, delimiter, "", "");
    }

    public StringJoiner(int initialCapacity, @NotNull CharSequence delimiter, @NotNull CharSequence prefix, @NotNull CharSequence suffix) {
        ValidateUtils.isTrue(initialCapacity > 0, "initialCapacity must be greater than 0");
        ValidateUtils.notNull(delimiter, "delimiter must not be null");
        ValidateUtils.notNull(prefix, "prefix must not be null");
        ValidateUtils.notNull(suffix, "suffix must not be null");
        this.delimiter = delimiter.toString();
        this.delimiterIsEmpty = StringUtils.isEmpty(delimiter);
        this.prefix = prefix.toString();
        this.prefixIsEmpty = StringUtils.isEmpty(prefix);
        this.suffix = suffix.toString();
        this.suffixIsEmpty = StringUtils.isEmpty(suffix);
        this.builder = new StringBuilder(initialCapacity);
    }

    @NotNull
    public StringJoiner append(@NotNull StringBuffer stringBuffer) {
        ValidateUtils.notNull(stringBuffer, "stringBuffer must not be null");
        appendDelimiter();
        appendPrefix();
        builder.append(stringBuffer);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull String string) {
        ValidateUtils.notNull(string, "string must not be null");
        appendDelimiter();
        appendPrefix();
        builder.append(string);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(int i) {
        appendDelimiter();
        appendPrefix();
        builder.append(i);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(long l) {
        appendDelimiter();
        appendPrefix();
        builder.append(l);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(float f) {
        appendDelimiter();
        appendPrefix();
        builder.append(f);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(double d) {
        appendDelimiter();
        appendPrefix();
        builder.append(d);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(boolean b) {
        appendDelimiter();
        appendPrefix();
        builder.append(b);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(char c) {
        appendDelimiter();
        appendPrefix();
        builder.append(c);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull char[] chars) {
        ValidateUtils.notNull(chars, "chars must not be null");
        appendDelimiter();
        appendPrefix();
        builder.append(chars);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull char[] chars, int offset) {
        ValidateUtils.notNull(chars, "chars must not be null");
        return append(chars, offset, chars.length);
    }

    @NotNull
    public StringJoiner append(@NotNull char[] chars, int offset, int len) {
        ValidateUtils.notNull(chars, "chars must not be null");
        ValidateUtils.isTrue(offset >= 0 && offset <= len,
                "offset must be greater than or equal to 0 and less than or equal to len");
        ValidateUtils.isTrue(len <= chars.length, "len must be less than or equal to chars's length");
        appendDelimiter();
        appendPrefix();
        builder.append(chars, offset, len);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull CharSequence charSequence) {
        ValidateUtils.notNull(charSequence, "charSequence must not be null");
        appendDelimiter();
        appendPrefix();
        builder.append(charSequence);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull CharSequence charSequence, int start) {
        ValidateUtils.notNull(charSequence, "charSequence must not be null");
        return append(charSequence, start, charSequence.length());
    }

    @NotNull
    public StringJoiner append(@NotNull CharSequence charSequence, int start, int end) {
        ValidateUtils.notNull(charSequence, "charSequence must not be null");
        ValidateUtils.isTrue(start >= 0 && start <= end,
                "start must be greater than or equal to 0 and less than or equal to end");
        ValidateUtils.isTrue(end <= charSequence.length(),
                "end must be less than or equal to charSequence's length");
        appendDelimiter();
        appendPrefix();
        builder.append(charSequence, start, end);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull Object object) {
        ValidateUtils.notNull(object, "object must not be null");
        appendDelimiter();
        appendPrefix();
        builder.append(object);
        appendSuffix();
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull Collection<? extends CharSequence> charSequences) {
        ValidateUtils.notNull(charSequences, "charSequences must not be null");
        charSequences.forEach(this::append);
        return this;
    }

    @NotNull
    public StringJoiner append(@NotNull CharSequence[] charSequences) {
        ValidateUtils.notNull(charSequences, "charSequences must not be null");
        Arrays.stream(charSequences).forEach(this::append);
        return this;
    }

    protected void appendDelimiter() {
        if (builder.length() != 0 && !delimiterIsEmpty) {
            builder.append(delimiter);
        }
    }

    protected void appendPrefix() {
        if (!prefixIsEmpty) {
            builder.append(prefix);
        }
    }

    protected void appendSuffix() {
        if (!suffixIsEmpty) {
            builder.append(suffix);
        }
    }

    @NotNull
    public String getDelimiter() {
        return delimiter;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public String getSuffix() {
        return suffix;
    }

    @NotNull
    @Override
    public String toString() {
        return builder.toString();
    }
}
