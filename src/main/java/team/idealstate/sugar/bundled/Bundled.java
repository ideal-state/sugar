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

package team.idealstate.sugar.bundled;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import team.idealstate.sugar.bundled.exception.BundledException;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

public abstract class Bundled {
    public static final String BUNDLED_DIR_PATH = "bundled";

    public static void release(@NotNull Class<?> holder, @NotNull File destDir) {
        release(holder, destDir, null);
    }

    public static void release(@NotNull Class<?> holder, @NotNull File destDir, Exclude exclude) {
        release(holder, destDir, "/", false, true, exclude);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void release(
            @NotNull Class<?> holder,
            @NotNull File destDir,
            @NotNull String path,
            boolean overlay,
            boolean includeEmptyDir,
            Exclude exclude) {
        Validation.notNull(holder, "Holder must not be null.");
        Validation.notNull(destDir, "Destination directory must not be null.");
        Validation.notNull(path, "Path must not be null.");
        String mode = overlay ? " (overlay)" : "";
        Log.debug("Release bundled..." + mode);
        CodeSource codeSource = holder.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            Log.warn("Code source is null, skip.");
            return;
        }
        URL location = codeSource.getLocation();
        if (location == null) {
            Log.warn("Location is null, skip.");
            return;
        }
        String normalizedPath = Paths.get(BUNDLED_DIR_PATH, path).normalize().toString();
        File file;
        try {
            file = Paths.get(location.toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new BundledException(e);
        }
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();
            if (entries.hasMoreElements()) {
                byte[] buffer = new byte[1024];
                do {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(normalizedPath)) {
                        entryName = entryName.substring(BUNDLED_DIR_PATH.length());
                        File destFile = new File(destDir, entryName);
                        if (entry.isDirectory()) {
                            if (includeEmptyDir && !destFile.exists()) {
                                destFile.mkdirs();
                            }
                            continue;
                        }
                        if (exclude != null && exclude.exclude(entryName)) {
                            continue;
                        }
                        Log.debug("Bundled entry: " + entryName);
                        File parentFile = destFile.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        boolean exists = destFile.exists();
                        if (overlay || !exists) {
                            if (exists || destFile.createNewFile()) {
                                try (InputStream input = jar.getInputStream(entry)) {
                                    int read = input.read(buffer);
                                    if (read != -1) {
                                        try (FileOutputStream output = new FileOutputStream(destFile)) {
                                            do {
                                                output.write(buffer, 0, read);
                                                output.flush();
                                                read = input.read(buffer);
                                            } while (read != -1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } while (entries.hasMoreElements());
            }
        } catch (IOException e) {
            throw new BundledException(e);
        }
        Log.debug("Release bundled done.");
    }

    public interface Exclude {
        boolean exclude(@NotNull String path);
    }
}
