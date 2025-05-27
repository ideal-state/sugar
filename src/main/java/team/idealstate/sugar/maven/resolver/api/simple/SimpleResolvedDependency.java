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

import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import team.idealstate.sugar.maven.resolver.api.DependencyScope;
import team.idealstate.sugar.maven.resolver.api.Repository;
import team.idealstate.sugar.maven.resolver.api.ResolvedDependency;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
final class SimpleResolvedDependency extends SimpleDependency
        implements ResolvedDependency, Comparable<SimpleResolvedDependency> {

    @NonNull
    private final String actualVersion;

    @NonNull
    private final LocalDateTime updatedTime;

    @NonNull
    private final Repository repository;

    @NonNull
    private final List<SimpleDependency> dependencies;

    public SimpleResolvedDependency(
            @NonNull String groupId,
            @NonNull String artifactId,
            @NonNull String extension,
            @NonNull String classifier,
            @NonNull String version,
            @NonNull DependencyScope scope,
            @NonNull String actualVersion,
            @NonNull LocalDateTime updatedTime,
            @NonNull Repository repository,
            @NonNull List<SimpleDependency> dependencies) {
        super(groupId, artifactId, extension, classifier, version, scope);
        this.actualVersion = actualVersion;
        this.updatedTime = updatedTime;
        this.repository = repository;
        this.dependencies = dependencies;
    }

    @Override
    public int compareTo(@NotNull SimpleResolvedDependency other) {
        Validation.notNull(other, "Other must not be null.");
        return getUpdatedTime().compareTo(other.getUpdatedTime());
    }
}
