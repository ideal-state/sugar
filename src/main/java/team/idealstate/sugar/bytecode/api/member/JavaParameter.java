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
import team.idealstate.sugar.bytecode.api.JavaAnnotatedElement;
import team.idealstate.sugar.bytecode.exception.BytecodeException;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface JavaParameter extends Java<Parameter>, JavaAnnotatedElement {
    @NotNull
    @Override
    @SuppressWarnings({"unchecked"})
    default <R extends Parameter> R java(@Nullable ClassLoader classLoader) throws BytecodeException{
        Method method = getDeclaringMethod().java(classLoader);
        return (R) method.getParameters()[getIndex()];
    }

    @NotNull
    JavaMethod getDeclaringMethod();

    int getIndex();

    @NotNull
    String getName();

    @NotNull
    JavaClass getType();
}
