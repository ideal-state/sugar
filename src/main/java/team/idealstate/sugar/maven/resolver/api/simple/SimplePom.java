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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import team.idealstate.sugar.maven.resolver.api.DependencyScope;
import team.idealstate.sugar.maven.resolver.api.exception.MavenResolutionException;
import team.idealstate.sugar.maven.resolver.api.util.DepthDescriptionHandler;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

final class SimplePom extends DepthDescriptionHandler {

    @NotNull
    public static SimplePom resolve(
            @NotNull SimpleDependencyResolver dependencyResolver, @NotNull InputStream inputStream) {
        Validation.requireNotNull(dependencyResolver, "Dependency resolver must not be null.");
        Validation.requireNotNull(inputStream, "Input stream must not be null.");
        try (InputStream input = inputStream) {
            SimplePom handler = new SimplePom(dependencyResolver);
            SAXParserFactory.newInstance().newSAXParser().parse(input, handler);
            return handler;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new MavenResolutionException(e);
        }
    }

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    private static final List<String> DEPTH_DESC_PROPERTY =
            Collections.unmodifiableList(Arrays.asList("project", "properties"));

    private static final List<String> DEPTH_DESC_DEPENDENCY =
            Collections.unmodifiableList(Arrays.asList("project", "dependencies", "dependency"));

    private final SimpleDependencyResolver dependencyResolver;

    private String groupId = null;
    private String artifactId = null;
    private String extension = null;
    private String classifier = null;
    private String version = null;
    private String scope = null;

    private final Map<String, String> properties = new LinkedHashMap<>();

    @NotNull
    public Map<String, String> getProperties() {
        return properties.isEmpty() ? Collections.emptyMap() : new LinkedHashMap<>(properties);
    }

    private final List<SimpleDependency> dependencies = new ArrayList<>();

    @NotNull
    public List<SimpleDependency> getDependencies() {
        Map<String, String> properties = getProperties();
        return dependencies.isEmpty()
                ? Collections.emptyList()
                : dependencies.stream()
                        .map(it -> {
                            String itVersion = it.getVersion();
                            Matcher matcher = PROPERTY_PATTERN.matcher(itVersion);
                            if (matcher.matches()) {
                                matcher = PROPERTY_PATTERN.matcher(itVersion);
                                StringBuffer sb = new StringBuffer();
                                while (matcher.find()) {
                                    String key = matcher.group(1);
                                    String replacement = properties.get(key);
                                    if (replacement != null) {
                                        matcher.appendReplacement(sb, replacement);
                                    } else {
                                        matcher.appendReplacement(sb, matcher.group(0));
                                    }
                                }
                                matcher.appendTail(sb);
                                String resolvedVersion = sb.toString();
                                return dependencyResolver.resolve(
                                        it.getGroupId(),
                                        it.getArtifactId(),
                                        it.getExtension(),
                                        it.getClassifier(),
                                        resolvedVersion);
                            }
                            return it;
                        })
                        .collect(Collectors.toList());
    }

    private SimplePom(@NotNull SimpleDependencyResolver dependencyResolver) {
        Validation.notNull(dependencyResolver, "Dependency resolver must not be null.");
        this.dependencyResolver = dependencyResolver;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isMatched(DEPTH_DESC_DEPENDENCY)) {
            String scope = this.scope;
            if (scope == null) {
                dependencies.add(dependencyResolver.resolve(groupId, artifactId, extension, classifier, version));
            } else {
                dependencies.add(dependencyResolver.resolve(
                        groupId, artifactId, extension, classifier, version, DependencyScope.of(scope)));
            }
            this.groupId = null;
            this.artifactId = null;
            this.extension = null;
            this.classifier = null;
            this.version = null;
            this.scope = null;
        }
        super.endElement(uri, localName, qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isParentMatched(DEPTH_DESC_PROPERTY)) {
            properties.put(currentQName(), new String(ch, start, length));
        } else if (isParentMatched(DEPTH_DESC_DEPENDENCY)) {
            switch (currentQName()) {
                case "groupId":
                    groupId = new String(ch, start, length);
                    break;
                case "artifactId":
                    artifactId = new String(ch, start, length);
                    break;
                case "extension":
                    extension = new String(ch, start, length);
                    break;
                case "classifier":
                    classifier = new String(ch, start, length);
                    break;
                case "version":
                    version = new String(ch, start, length);
                    break;
                case "scope":
                    scope = new String(ch, start, length);
                    break;
            }
        }
    }
}
