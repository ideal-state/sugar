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

package team.idealstate.sugar.bytecode.api.member;

import team.idealstate.sugar.bytecode.Java;
import team.idealstate.sugar.bytecode.api.JavaAccessible;
import team.idealstate.sugar.bytecode.api.JavaAnnotatedElement;
import team.idealstate.sugar.bytecode.exception.BytecodeException;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public interface JavaConstructor extends Java<Constructor<?>>, JavaAnnotatedElement, JavaAccessible {
    @NotNull
    @Override
    @SuppressWarnings({"unchecked"})
    default <R extends Constructor<?>> R java(@Nullable ClassLoader classLoader) throws BytecodeException {
        Class<?> declaringClass = getDeclaringClass().java(classLoader);
        try {
            JavaParameter[] parameters = getParameters();
            Class<?>[] parameterTypes = Arrays.stream(parameters)
                    .map(parameter -> (Class<?>) parameter.getType().java(classLoader))
                    .toArray(Class<?>[]::new);
            return (R) declaringClass.getDeclaredConstructor(parameterTypes);
        } catch ( NoSuchMethodException e) {
            throw new BytecodeException(e);
        }
    }

    @NotNull
    JavaClass getDeclaringClass();

    @NotNull
    String getName();

    @NotNull
    JavaParameter[] getParameters();

    @NotNull
    JavaClass[] getExceptionTypes();
}