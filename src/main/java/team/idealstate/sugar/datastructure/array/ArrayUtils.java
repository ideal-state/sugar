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

package team.idealstate.sugar.datastructure.array;

import team.idealstate.sugar.datastructure.collection.CollectionUtils;
import team.idealstate.sugar.datastructure.collection.ListUtils;
import team.idealstate.sugar.object.ObjectUtils;
import team.idealstate.sugar.validation.ValidateUtils;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.*;

/**
 * <p>ArrayUtils</p>
 *
 * <p>创建于 2024/3/24 1:46</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ArrayUtils {

    private static final Set<Class<?>> NATIVELY_SUPPORTED_CLASSES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class,
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, Boolean.class,
            String.class
    )));

    private static boolean isNativelySupported(@NotNull Class<?> cls) {
        return NATIVELY_SUPPORTED_CLASSES.contains(cls);
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    public static <E> E[] copyOf(@NotNull E[] source) {
        ValidateUtils.notNull(source, "source must not be null");
        return (E[]) copyOfRange((Object) source, 0, source.length);
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    public static <E> E[] copyOfRange(@NotNull E[] source, int offset) {
        ValidateUtils.notNull(source, "source must not be null");
        int len = source.length;
        ValidateUtils.isTrue(offset >= 0 && offset <= len, "offset must be greater than or equal to 0 and less than or equal to len");
        return (E[]) copyOfRange((Object) source, offset, len);
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    public static <E> E[] copyOfRange(@NotNull E[] source, int offset, int len) {
        ValidateUtils.notNull(source, "source must not be null");
        ValidateUtils.isTrue(offset >= 0 && offset <= len, "offset must be greater than or equal to 0 and less than or equal to len");
        ValidateUtils.isTrue(len <= source.length, "len must be less than or equal to source's length");
        return (E[]) copyOfRange((Object) source, offset, len);
    }

    private static Object copyOfRange(Object source, int offset, int len) {
        final int length = Array.getLength(source);
        if (length == 0) {
            return source;
        }
        Class<?> componentType = source.getClass().getComponentType();
        Object copy = Array.newInstance(componentType, len - offset);
        if (isNativelySupported(componentType)) {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(source, offset, copy, 0, len);
            return copy;
        }
        for (int i = offset; i < len; i++) {
            Object element = Array.get(source, i);
            if (element == null) {
                continue;
            }
            if (element.getClass().isArray()) {
                Array.set(copy, i, copyOfRange(element, 0, Array.getLength(element)));
            } else {
                Array.set(copy, i, element);
            }
        }
        return copy;
    }

    @NotNull
    @SafeVarargs
    public static <E> E[] merge(@NotNull E[] array1, @NotNull E[] array2, E... arrays) {
        ValidateUtils.notNull(array1, "array1 must not be null");
        ValidateUtils.notNull(array2, "array2 must not be null");
        List<E[]> arrayList = ListUtils.linkedListOf();
        arrayList.add(array1);
        arrayList.add(array2);
        CollectionUtils.addAll(arrayList, arrays);
        Iterator<E[]> iterator = arrayList.iterator();
        int length = 0;
        while (iterator.hasNext()) {
            E[] array = iterator.next();
            if (ArrayUtils.isEmpty(array)) {
                iterator.remove();
            } else {
                length++;
            }
        }
        if (length == 0) {
            return array1;
        }
        List<E> array = new ArrayList<>(length);
        for (E[] es : arrayList) {
            CollectionUtils.addAll(array, es);
        }
        return array.toArray(array1);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <E> E[] emptyIfNull(@Nullable E[] array, @NotNull Class<E> componentType) {
        ValidateUtils.notNull(componentType, "componentType must not be null");
        return ObjectUtils.isNull(array) ? (E[]) Array.newInstance(componentType, 0) : array;
    }

    @NotNull
    public static <E> E[] defaultIfEmpty(@NotNull E[] array, @NotNull E[] def) {
        ValidateUtils.notNull(array, "array must not be null");
        ValidateUtils.notNull(def, "def must not be null");
        return isEmpty(array) ? def : array;
    }

    @NotNull
    public static <E> E[] defaultIfNullOrEmpty(@Nullable E[] array, @NotNull E[] def) {
        ValidateUtils.notNull(def, "def must not be null");
        if (isNullOrEmpty(array)) {
            return def;
        } else {
            assert array != null;
            return array;
        }
    }

    public static boolean isArray(@NotNull Object array) {
        ValidateUtils.notNull(array, "array must not be null");
        return array.getClass().isArray();
    }

    public static boolean isNotArray(@NotNull Object array) {
        return !isArray(array);
    }

    public static boolean isNullOrArray(@Nullable Object array) {
        return ObjectUtils.isNull(array) || array.getClass().isArray();
    }

    public static boolean isNotNullOrArray(@Nullable Object array) {
        return !isNullOrArray(array);
    }

    public static boolean isEmpty(@NotNull Object[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable Object[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull Object[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable Object[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull byte[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable byte[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull byte[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable byte[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull short[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable short[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull short[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable short[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull int[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable int[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull int[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable int[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull long[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable long[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull long[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable long[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull float[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable float[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull float[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable float[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull double[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable double[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull double[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable double[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull boolean[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable boolean[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull boolean[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable boolean[] array) {
        return !isNullOrEmpty(array);
    }

    public static boolean isEmpty(@NotNull char[] array) {
        ValidateUtils.notNull(array, "array must not be null");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(@Nullable char[] array) {
        return ObjectUtils.isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(@NotNull char[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotNullOrEmpty(@Nullable char[] array) {
        return !isNullOrEmpty(array);
    }
}
