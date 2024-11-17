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

package team.idealstate.sugar.test.reflection.entity;

import team.idealstate.sugar.reflection.annotation.DeclaringClass;
import team.idealstate.sugar.reflection.annotation.ReflectConstructor;
import team.idealstate.sugar.reflection.annotation.ReflectField;
import team.idealstate.sugar.reflection.annotation.ReflectMethod;

@DeclaringClass(Human.class)
public interface ReflectHuman {

    @ReflectConstructor
    Human newInstance(String name, int age);

    @ReflectField(accessible = true)
    String name();

    @ReflectField
    int age();

    @ReflectField(name = "age")
    int getAndSetAge(int newAge);

    @ReflectMethod
    String getName();

    @ReflectMethod
    void setName(String name);

    @ReflectMethod(accessible = true)
    int getAge();

    @ReflectMethod(accessible = true)
    void setAge(int age);

    @ReflectField(statical = true)
    int LEGAL_AGE();

    @ReflectMethod(statical = true)
    int getLegalAge();

    default void defaultMethod() {
    }

    void unimplementedMethod();
}
