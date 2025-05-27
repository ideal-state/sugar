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

package team.idealstate.sugar.maven.resolver.api;

import java.io.InputStream;
import java.util.List;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import team.idealstate.sugar.validate.annotation.Nullable;

public interface DependencyResolver {

    @NotNull
    default String getIdDelimiter() {
        return ":";
    }

    @NotNull
    default Dependency resolve(@NotNull String dependencyId) {
        return resolve(dependencyId, DependencyScope.DEFAULT);
    }

    @NotNull
    default Dependency resolve(@NotNull String dependencyId, @NotNull DependencyScope scope) {
        Validation.notNullOrBlank(dependencyId, "Dependency id must not be null or blank.");
        Validation.notNull(scope, "Dependency scope must not be null.");
        String[] parts = dependencyId.split(getIdDelimiter());
        if (parts.length == 3) {
            return resolve(parts[0], parts[1], null, null, parts[2], scope);
        }
        if (parts.length == 4) {
            return resolve(parts[0], parts[1], parts[2], null, parts[3], scope);
        }
        if (parts.length == 5) {
            return resolve(parts[0], parts[1], parts[2], parts[3], parts[4], scope);
        }
        throw new IllegalArgumentException("Invalid dependency id: " + dependencyId);
    }

    @NotNull
    default Dependency resolve(
            @NotNull String groupId,
            @NotNull String artifactId,
            @Nullable String extension,
            @Nullable String classifier,
            @NotNull String version) {
        return resolve(groupId, artifactId, extension, classifier, version, DependencyScope.DEFAULT);
    }

    @NotNull
    Dependency resolve(
            @NotNull String groupId,
            @NotNull String artifactId,
            @Nullable String extension,
            @Nullable String classifier,
            @NotNull String version,
            @NotNull DependencyScope scope);

    @NotNull
    List<? extends Dependency> resolvePom(@NotNull InputStream pomInputStream);
}
