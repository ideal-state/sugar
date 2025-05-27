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

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import team.idealstate.sugar.maven.resolve.api.exception.MavenResolutionException;
import team.idealstate.sugar.maven.resolve.api.util.DepthDescriptionHandler;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

final class SimpleMetadata extends DepthDescriptionHandler {

    @NotNull
    public static SimpleMetadata resolve(@NotNull InputStream inputStream) {
        Validation.requireNotNull(inputStream, "Input stream must not be null.");
        try (InputStream input = inputStream) {
            SimpleMetadata handler = new SimpleMetadata();
            SAXParserFactory.newInstance().newSAXParser().parse(input, handler);
            return handler;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new MavenResolutionException(e);
        }
    }

    private static final DateTimeFormatter LAST_UPDATED_FORMATER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String VERSION_DELIMITER = "-";
    private static final List<String> DEPTH_DESC_VERSION =
            Collections.unmodifiableList(Arrays.asList("metadata", "version"));
    private static final List<String> DEPTH_DESC_VERSIONING_LAST_UPDATED =
            Collections.unmodifiableList(Arrays.asList("metadata", "versioning", "lastUpdated"));
    private static final List<String> DEPTH_DESC_VERSIONING_SNAPSHOT_TIMESTAMP =
            Collections.unmodifiableList(Arrays.asList("metadata", "versioning", "snapshot", "timestamp"));
    private static final List<String> DEPTH_DESC_VERSIONING_SNAPSHOT_BUILD_NUMBER =
            Collections.unmodifiableList(Arrays.asList("metadata", "versioning", "snapshot", "buildNumber"));

    private String snapshotTimestamp = null;
    private String snapshotBuildNumber = null;
    private String version = null;

    private LocalDateTime lastUpdated = null;

    @NotNull
    public LocalDateTime getLastUpdated() {
        return Validation.requireNotNull(lastUpdated, "Last updated must not be null.");
    }

    @NotNull
    public String getActualVersion() {
        Validation.notNull(version, "Version must not be null.");
        Validation.notNull(snapshotTimestamp, "Snapshot timestamp must not be null.");
        Validation.notNull(snapshotBuildNumber, "Snapshot build number must not be null.");
        int lasted = version.toUpperCase().lastIndexOf("-SNAPSHOT");
        Validation.is(lasted >= 0, "Version must be a snapshot version.");
        String version = this.version.substring(0, lasted);
        return version + VERSION_DELIMITER + snapshotTimestamp + VERSION_DELIMITER + snapshotBuildNumber;
    }

    private SimpleMetadata() {}

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isMatched(DEPTH_DESC_VERSION)) {
            this.version = new String(ch, start, length);
        } else if (isMatched(DEPTH_DESC_VERSIONING_LAST_UPDATED)) {
            this.lastUpdated = LocalDateTime.parse(new String(ch, start, length), LAST_UPDATED_FORMATER);
        } else if (isMatched(DEPTH_DESC_VERSIONING_SNAPSHOT_TIMESTAMP)) {
            this.snapshotTimestamp = new String(ch, start, length);
        } else if (isMatched(DEPTH_DESC_VERSIONING_SNAPSHOT_BUILD_NUMBER)) {
            this.snapshotBuildNumber = new String(ch, start, length);
        }
    }
}
