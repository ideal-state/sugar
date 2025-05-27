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

package team.idealstate.sugar.maven.resolver.api.simple;

import lombok.Data;
import lombok.NonNull;
import team.idealstate.sugar.maven.resolver.api.Dependency;
import team.idealstate.sugar.maven.resolver.api.DependencyScope;
import team.idealstate.sugar.validate.annotation.NotNull;

@Data
class SimpleDependency implements Dependency {

    @NonNull
    private final String groupId;

    @NonNull
    private final String artifactId;

    @NonNull
    private final String extension;

    @NonNull
    private final String classifier;

    @NonNull
    private final String version;

    @NonNull
    private final DependencyScope scope;

    @Override
    public boolean isResolved() {
        return this instanceof SimpleResolvedDependency;
    }

    @NotNull
    @Override
    public SimpleResolvedDependency asResolved() {
        return (SimpleResolvedDependency) this;
    }
}
