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

package team.idealstate.sugar.bytecode.api;

import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;

public interface JavaType extends JavaVersion {

    @NotNull
    String getName();

    boolean isAssignableFrom(@NotNull JavaType type);

    default boolean isAssignableTo(@NotNull JavaType type) {
        Validation.notNull(type, "type must not be null.");
        return type.isAssignableFrom(this);
    }
}