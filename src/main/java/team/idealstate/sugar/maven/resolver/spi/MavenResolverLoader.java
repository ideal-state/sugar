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

package team.idealstate.sugar.maven.resolver.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ServiceLoader;
import team.idealstate.sugar.maven.exception.MavenException;
import team.idealstate.sugar.maven.resolver.api.MavenResolver;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import team.idealstate.sugar.validate.annotation.Nullable;

public interface MavenResolverLoader {

    @NotNull
    static MavenResolverLoader instance(@NotNull String name) {
        return instance(name, null);
    }

    @NotNull
    static MavenResolverLoader instance(@NotNull String name, @Nullable ClassLoader classLoader) {
        Validation.notNullOrBlank(name, "Name must not be null or blank.");
        MavenResolverLoader instance = null;
        for (MavenResolverLoader loader : ServiceLoader.load(MavenResolverLoader.class, classLoader)) {
            if (name.equals(loader.getName())) {
                if (instance != null) {
                    throw new MavenException(
                            String.format("There are multiple maven resolver loaders with the same name '%s'.", name));
                }
                instance = loader;
            }
        }
        if (instance == null) {
            throw new MavenException(String.format("Maven resolver loader '%s' not found.", name));
        }
        return instance;
    }

    @NotNull
    String getName();

    @NotNull
    default MavenResolver load(@NotNull File configurationFile, @Nullable ClassLoader classLoader) {
        Validation.requireNotNull(configurationFile, "Configuration file must not be null.");
        try {
            return load(Files.newInputStream(configurationFile.toPath()), classLoader);
        } catch (IOException e) {
            throw new MavenException(e);
        }
    }

    @NotNull
    MavenResolver load(@NotNull InputStream configurationInputStream, @Nullable ClassLoader classLoader);
}
