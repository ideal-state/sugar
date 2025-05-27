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

import team.idealstate.sugar.validate.annotation.NotNull;

public interface Dependency {

    @NotNull
    String getGroupId();

    @NotNull
    String getArtifactId();

    @NotNull
    String getExtension();

    @NotNull
    String getClassifier();

    @NotNull
    String getVersion();

    @NotNull
    DependencyScope getScope();

    default boolean isResolved() {
        return this instanceof ResolvedDependency;
    }

    @NotNull
    default ResolvedDependency asResolved() {
        return (ResolvedDependency) this;
    }
}
