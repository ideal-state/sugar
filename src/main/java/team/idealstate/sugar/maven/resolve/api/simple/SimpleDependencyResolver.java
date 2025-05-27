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

import java.io.InputStream;
import java.util.List;
import team.idealstate.sugar.maven.resolve.api.DependencyResolver;
import team.idealstate.sugar.maven.resolve.api.DependencyScope;
import team.idealstate.sugar.validate.annotation.NotNull;
import team.idealstate.sugar.validate.annotation.Nullable;

final class SimpleDependencyResolver implements DependencyResolver {

    public static String DEFAULT_EXTENSION = "jar";
    public static String DEFAULT_CLASSIFIER = "";

    @NotNull
    @Override
    public SimpleDependency resolve(@NotNull String dependencyId) {
        return (SimpleDependency) DependencyResolver.super.resolve(dependencyId);
    }

    @NotNull
    @Override
    public SimpleDependency resolve(@NotNull String dependencyId, @NotNull DependencyScope scope) {
        return (SimpleDependency) DependencyResolver.super.resolve(dependencyId, scope);
    }

    @NotNull
    @Override
    public SimpleDependency resolve(
            @NotNull String groupId,
            @NotNull String artifactId,
            @Nullable String extension,
            @Nullable String classifier,
            @NotNull String version) {
        return (SimpleDependency) DependencyResolver.super.resolve(groupId, artifactId, extension, classifier, version);
    }

    @NotNull
    @Override
    public SimpleDependency resolve(
            @NotNull String groupId,
            @NotNull String artifactId,
            @Nullable String extension,
            @Nullable String classifier,
            @NotNull String version,
            @NotNull DependencyScope scope) {
        return new SimpleDependency(
                groupId,
                artifactId,
                extension == null ? DEFAULT_EXTENSION : extension,
                classifier == null ? DEFAULT_CLASSIFIER : classifier,
                version,
                scope);
    }

    @NotNull
    @Override
    public List<SimpleDependency> resolvePom(@NotNull InputStream inputStream) {
        return SimplePom.resolve(this, inputStream).getDependencies();
    }
}
