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

package team.idealstate.sugar.object;

import team.idealstate.sugar.datastructure.mapping.MapUtils;
import team.idealstate.sugar.io.IOUtils;
import team.idealstate.sugar.validation.ValidateUtils;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * <p>ObjectUtils</p>
 *
 * <p>创建于 2024/3/24 19:28</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ObjectUtils {

    private static final Map<Class<?>, MethodHandle> CLONE_METHOD_HANDLES = MapUtils.mapOf();

    public static boolean isNull(@Nullable Object object) {
        return object == null;
    }

    public static boolean isNotNull(@Nullable Object object) {
        return object != null;
    }

    @NotNull
    public static <T> T defaultIfNull(@Nullable T object, @NotNull T def) {
        ValidateUtils.notNull(def, "def must not be null");
        return isNull(object) ? def : object;
    }

    public static boolean isEquals(@Nullable Object object1, @Nullable Object object2) {
        return java.util.Objects.equals(object1, object2);
    }

    public static boolean isNotEquals(@Nullable Object object1, @Nullable Object object2) {
        return !isEquals(object1, object2);
    }

    public static boolean isAllEquals(@Nullable Object object1, @Nullable Object object2, Object... objects) {
        if (ObjectUtils.isEquals(object1, object2)) {
            for (Object object3 : objects) {
                if (!ObjectUtils.isEquals(object1, object3)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isNotAllEquals(@Nullable Object object1, @Nullable Object object2, Object... objects) {
        if (ObjectUtils.isEquals(object1, object2)) {
            for (Object object3 : objects) {
                if (!ObjectUtils.isEquals(object1, object3)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean isAnyEquals(@Nullable Object object1, @Nullable Object object2, Object... objects) {
        if (ObjectUtils.isEquals(object1, object2)) {
            return true;
        }
        for (Object object3 : objects) {
            if (ObjectUtils.isEquals(object1, object3)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotAnyEquals(@Nullable Object object1, @Nullable Object object2, Object... objects) {
        return !isAnyEquals(object1, object2, objects);
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    public static <T> T copyOf(@NotNull T object) {
        ValidateUtils.notNull(object, "object must not be null");
        if (ObjectUtils.isCloneable(object)) {
            try {
                return (T) CLONE_METHOD_HANDLES.computeIfAbsent(object.getClass(), (cls) -> {
                    Method clone;
                    try {
                        clone = object.getClass().getDeclaredMethod("clone");
                        return MethodHandles.publicLookup().unreflect(clone);
                    } catch (NoSuchMethodException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).bindTo(object).invoke();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        if (object instanceof Serializable) {
            ByteArrayOutputStream output = IOUtils.byteArrayOutputStream();
            IOUtils.use(IOUtils.objectOutputStream(IOUtils.byteArrayOutputStream()), (outputStream) -> {
                outputStream.writeObject(object);
                outputStream.flush();
            });
            Object[] copy = {null};
            IOUtils.use(IOUtils.objectInputStream(IOUtils.byteArrayInputStream(output.toByteArray())), (resource) -> {
                copy[0] = resource.readObject();
            });
            ValidateUtils.notNull(copy[0], "copied object must not be null");
            return (T) copy[0];
        }
        throw new UnsupportedOperationException("cannot copy an object (object does not implement Cloneable or Serializable)");
    }

    public static boolean isCloneable(@NotNull Object object) {
        ValidateUtils.notNull(object, "object must not be null");
        if (!(object instanceof Cloneable)) {
            return false;
        }
        try {
            if (Modifier.isPublic(object.getClass().getMethod("clone").getModifiers())) {
                return true;
            }
        } catch (NoSuchMethodException ignored) {}
        return false;
    }
}
