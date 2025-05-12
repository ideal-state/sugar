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

package team.idealstate.sugar;

import java.io.File;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import lombok.Data;
import lombok.NonNull;
import team.idealstate.sugar.exception.SugarException;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.string.StringUtils;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import team.idealstate.sugar.validate.annotation.Nullable;

@Data
public final class Sugar {

    @Nullable
    public static Sugar load(@NotNull JarFile jarFile, boolean close) {
        Validation.notNull(jarFile, "Jar file must not be null.");
        try {
            try {
                Manifest manifest = jarFile.getManifest();
                if (manifest == null) {
                    return null;
                }
                Attributes attributes = manifest.getMainAttributes();
                if (attributes.isEmpty()) {
                    return null;
                }
                String sugar = attributes.getValue("sugar");
                if (!StringUtils.isBoolean(sugar)) {
                    return null;
                }
                boolean enabled = Boolean.parseBoolean(sugar);
                String groupId = attributes.getValue("groupId");
                if (groupId == null) {
                    Log.warn(String.format("Group id is not found in manifest '%s'.", jarFile.getName()));
                    return null;
                }
                String artifactId = attributes.getValue("artifactId");
                if (artifactId == null) {
                    Log.warn(String.format("Artifact id is not found in manifest '%s'.", jarFile.getName()));
                    return null;
                }
                String version = attributes.getValue("version");
                if (version == null) {
                    Log.warn(String.format("Version is not found in manifest '%s'.", jarFile.getName()));
                    return null;
                }
                String into = attributes.getValue("sugar-into");
                File intoFile = null;
                if (into != null) {
                    intoFile = new File(into);
                    Validation.not(intoFile.isAbsolute(), String.format("Into path '%s' must not be absolute.", into));
                    File baseFile = new File(".");
                    Path basePath = baseFile.toPath().toAbsolutePath().normalize();
                    Path intoPath = new File(
                                    baseFile, intoFile.toPath().normalize().toString())
                            .toPath()
                            .toAbsolutePath()
                            .normalize();
                    Validation.is(
                            intoPath.startsWith(basePath),
                            String.format("Into path '%s' must be relative to '%s'.", into, basePath));
                    intoFile = intoPath.toFile();
                }
                return new Sugar(groupId, artifactId, version, enabled, intoFile);
            } finally {
                if (close) {
                    jarFile.close();
                }
            }
        } catch (Throwable e) {
            throw new SugarException(e);
        }
    }

    @NonNull
    private final String groupId;

    @NonNull
    private final String artifactId;

    @NonNull
    private final String version;

    private final boolean enabled;
    private final File into;
}
