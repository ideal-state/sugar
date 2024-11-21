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

package team.idealstate.sugar.bytecode;

import team.idealstate.sugar.bytecode.api.member.JavaClass;
import team.idealstate.sugar.bytecode.exception.BytecodeException;
import team.idealstate.sugar.bytecode.exception.BytecodeParsingException;
import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Java<T> {

    @NotNull
    static JavaClass typeof(@NotNull Object object) throws BytecodeParsingException {
        Validation.notNull(object, "object must not be null.");
        return typeof(object.getClass());
    }

    @NotNull
    static JavaClass typeof(@NotNull java.lang.Class<?> cls) throws BytecodeParsingException {
        Validation.notNull(cls, "class must not be null.");
        return typeof(cls.getName());
    }

    @NotNull
    static JavaClass typeof(@NotNull String className) throws BytecodeParsingException {
        Validation.notNull(className, "className must not be null.");
        return Cache.CLASSES.computeIfAbsent(className, InternalJavaClass::newInstance);
    }

    @NotNull
    default <R extends T> R java() throws BytecodeException {
        return java(getClass().getClassLoader());
    }

    @NotNull
    <R extends T> R java(@Nullable ClassLoader classLoader) throws BytecodeException;

    abstract class Cache {
        private static final Map<String, JavaClass> CLASSES = new ConcurrentHashMap<>(64, 0.6F);
    }
}
