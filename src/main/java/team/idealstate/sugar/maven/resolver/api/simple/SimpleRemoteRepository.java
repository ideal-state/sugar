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

import java.net.URI;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import team.idealstate.sugar.maven.resolver.api.RemoteRepository;
import team.idealstate.sugar.maven.resolver.api.RepositoryPolicy;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
final class SimpleRemoteRepository extends AbstractSimpleRepository implements RemoteRepository {

    public SimpleRemoteRepository(@NonNull String name, @NonNull URI url, @NonNull List<RepositoryPolicy> policies) {
        super(name, url, policies);
    }
}
