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

package team.idealstate.sugar.reflection;

import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;

import java.lang.reflect.Proxy;

public abstract class Reflection {

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> T newInstance(ClassLoader classLoader, @NotNull Class<T> reflectionInterface, Object target) {
        Validation.notNull(reflectionInterface, "reflectionInterface must not be null");
        Validation.vote(reflectionInterface.isInterface(), "reflectionInterface must be an interface");
        classLoader = classLoader != null ? classLoader : reflectionInterface.getClassLoader();
        ReflectionHandler reflectionHandler = new ReflectionHandler(classLoader, reflectionInterface, target);
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{reflectionInterface}, reflectionHandler);
    }
}