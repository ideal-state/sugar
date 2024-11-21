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

package team.idealstate.sugar.localization;

import team.idealstate.sugar.localization.annotation.Alias;
import team.idealstate.sugar.localization.exception.LocalizationException;
import team.idealstate.sugar.reflection.ReflectionInvocationHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InternalLocalizationHandler implements ReflectionInvocationHandler {

    private final Map<String, Object> dictionary;
    private final Map<String, Object> cache = new ConcurrentHashMap<>(16, 0.6F);

    InternalLocalizationHandler(Map<String, Object> dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = ReflectionInvocationHandler.super.invoke(proxy, method, args);
        if (ret != null) {
            return ret;
        }
        String methodName = method.getName();
        if (cache.containsKey(methodName)) {
            return cache.get(methodName);
        }
        Object value = dictionary.get(methodName);
        if (value == null) {
            Alias alias = method.getDeclaredAnnotation(Alias.class);
            if (alias != null) {
                value = dictionary.get(alias.value());
            }
        }

        if (value != null) {
            if (!method.getReturnType().isAssignableFrom(value.getClass())) {
                throw new LocalizationException("Return type of method " + methodName + " must be assignable from dictionary value.");
            }
        }

        cache.put(methodName, value);
        return value;
    }
}
