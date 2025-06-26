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

package team.idealstate.sugar.banner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import team.idealstate.sugar.banner.exception.BannerException;
import team.idealstate.sugar.validate.annotation.NotNull;

public abstract class Banner {
    public static final String FILE_NAME = "banner.txt";

    @NotNull
    public static List<String> lines() {
        return lines(Banner.class);
    }

    @NotNull
    public static List<String> lines(@NotNull Class<?> owner) {
        CodeSource codeSource = owner.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return Collections.emptyList();
        }
        URL location = codeSource.getLocation();
        if (location == null) {
            return Collections.emptyList();
        }
        URI uri;
        try {
            uri = location.toURI();
        } catch (URISyntaxException e) {
            throw new BannerException(e);
        }
        File file;
        try {
            file = new File(uri);
        } catch (IllegalArgumentException e) {
            throw new BannerException(e);
        }
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry(FILE_NAME);
            if (entry == null || entry.isDirectory()) {
                return Collections.emptyList();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new BannerException(e);
        }
    }
}
