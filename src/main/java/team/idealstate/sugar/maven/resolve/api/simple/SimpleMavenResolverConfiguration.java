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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.xml.sax.SAXException;
import team.idealstate.sugar.maven.resolve.api.LocalRepository;
import team.idealstate.sugar.maven.resolve.api.MavenResolverConfiguration;
import team.idealstate.sugar.maven.resolve.api.RemoteRepository;
import team.idealstate.sugar.maven.resolve.api.RepositoryPolicy;
import team.idealstate.sugar.maven.resolve.api.util.DepthDescriptionHandler;
import team.idealstate.sugar.validate.annotation.NotNull;

final class SimpleMavenResolverConfiguration extends DepthDescriptionHandler implements MavenResolverConfiguration {

    private static final List<String> DEPTH_DESC_LOCAL_REPOSITORY =
            Collections.unmodifiableList(Arrays.asList("resolver", "local"));

    private static final List<String> DEPTH_DESC_REMOTE_REPOSITORY =
            Collections.unmodifiableList(Arrays.asList("resolver", "remote", "repository"));

    private String localRepositoryName = null;
    private String localRepositoryUrl = null;
    private String localRepositoryPolicies = null;

    private String remoteRepositoryName = null;
    private String remoteRepositoryUrl = null;
    private String remoteRepositoryPolicies = null;

    private final List<RemoteRepository> repositories = new ArrayList<>();

    public SimpleMavenResolverConfiguration() {}

    @NotNull
    @Override
    public LocalRepository getLocalRepository() {
        return new SimpleLocalRepository(
                localRepositoryName == null ? "local" : localRepositoryName,
                localRepositoryUrl == null ? new File(".") : new File(URI.create(localRepositoryUrl)),
                parseRepositoryPolicies(localRepositoryPolicies));
    }

    @NotNull
    @Override
    public List<RemoteRepository> getRemoteRepositories() {
        return repositories.isEmpty() ? Collections.emptyList() : new ArrayList<>(repositories);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isMatched(DEPTH_DESC_REMOTE_REPOSITORY)) {
            repositories.add(new SimpleRemoteRepository(
                    remoteRepositoryName,
                    URI.create(remoteRepositoryUrl),
                    parseRepositoryPolicies(remoteRepositoryPolicies)));
            this.remoteRepositoryName = null;
            this.remoteRepositoryUrl = null;
            this.remoteRepositoryPolicies = null;
        }
        super.endElement(uri, localName, qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isParentMatched(DEPTH_DESC_LOCAL_REPOSITORY)) {
            switch (currentQName()) {
                case "name":
                    this.localRepositoryName = new String(ch, start, length);
                    break;
                case "url":
                    this.localRepositoryUrl = new String(ch, start, length);
                    break;
                case "policies":
                    this.localRepositoryPolicies = new String(ch, start, length);
                    break;
            }
        } else if (isParentMatched(DEPTH_DESC_REMOTE_REPOSITORY)) {
            switch (currentQName()) {
                case "name":
                    this.remoteRepositoryName = new String(ch, start, length);
                    break;
                case "url":
                    this.remoteRepositoryUrl = new String(ch, start, length);
                    break;
                case "policies":
                    this.remoteRepositoryPolicies = new String(ch, start, length);
                    break;
            }
        }
    }

    @NotNull
    private static List<RepositoryPolicy> parseRepositoryPolicies(String policies) {
        if (policies == null) {
            return Collections.emptyList();
        }
        String[] policyStrings = policies.split(",");
        if (policyStrings.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(policyStrings).map(RepositoryPolicy::valueOf).collect(Collectors.toList());
    }
}
