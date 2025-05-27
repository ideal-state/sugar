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

package team.idealstate.sugar.maven.resolve.api.simple;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import team.idealstate.sugar.maven.resolve.api.Repository;
import team.idealstate.sugar.maven.resolve.api.ResolvedArtifact;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
final class SimpleResolvedArtifact extends SimpleArtifact
        implements ResolvedArtifact, Comparable<SimpleResolvedArtifact> {

    @NonNull
    private final String actualVersion;

    @NonNull
    private final LocalDateTime updatedTime;

    @NonNull
    private final Repository repository;

    @NonNull
    private final List<SimpleDependency> dependencies;

    @NonNull
    private final File file;

    public SimpleResolvedArtifact(
            @NonNull String groupId,
            @NonNull String artifactId,
            @NonNull String extension,
            @NonNull String classifier,
            @NonNull String version,
            @NonNull String actualVersion,
            @NonNull LocalDateTime updatedTime,
            @NonNull Repository repository,
            @NonNull List<SimpleDependency> dependencies,
            @NonNull File file) {
        super(groupId, artifactId, extension, classifier, version);
        this.actualVersion = actualVersion;
        this.updatedTime = updatedTime;
        this.repository = repository;
        this.dependencies = dependencies;
        this.file = file;
    }

    @Override
    public int compareTo(@NotNull SimpleResolvedArtifact other) {
        Validation.notNull(other, "Other must not be null.");
        return getUpdatedTime().compareTo(other.getUpdatedTime());
    }
}
