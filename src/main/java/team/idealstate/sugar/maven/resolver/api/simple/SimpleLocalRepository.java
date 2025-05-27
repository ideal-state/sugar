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

import java.io.File;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import team.idealstate.sugar.maven.resolver.api.LocalRepository;
import team.idealstate.sugar.maven.resolver.api.RepositoryPolicy;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
final class SimpleLocalRepository extends AbstractSimpleRepository implements LocalRepository {

    private final File location;

    public SimpleLocalRepository(
            @NonNull String name, @NonNull File location, @NonNull List<RepositoryPolicy> policies) {
        super(
                name,
                Validation.requireNotNull(location, "Location must not be null.")
                        .toURI(),
                policies);
        this.location = location;
    }

    @NotNull
    @Override
    public File getLocation() {
        return location;
    }
}
