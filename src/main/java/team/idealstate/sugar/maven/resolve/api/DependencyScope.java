/*
 *    Copyright 2025 ideal-state
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

package team.idealstate.sugar.maven.resolve.api;

import java.util.Arrays;
import lombok.Getter;
import lombok.NonNull;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

@Getter
public enum DependencyScope {
    COMPILE("compile"),
    PROVIDED("provided"),
    SYSTEM("system"),
    RUNTIME("runtime"),
    TEST("test");

    public static final DependencyScope DEFAULT = COMPILE;

    @NonNull
    private final String actualName;

    DependencyScope(@NotNull String actualName) {
        Validation.notNullOrBlank(actualName, "Actual name must not be null or blank.");
        this.actualName = actualName;
    }

    public boolean is(@NotNull String actualName) {
        Validation.notNullOrBlank(actualName, "Actual name must not be null or blank.");
        return getActualName().equals(actualName);
    }

    @NotNull
    public static DependencyScope of(@NotNull String actualName) {
        Validation.notNullOrBlank(actualName, "Actual name must not be null or blank.");
        return Arrays.stream(DependencyScope.values())
                .filter(s -> s.is(actualName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid dependency scope: " + actualName));
    }
}
