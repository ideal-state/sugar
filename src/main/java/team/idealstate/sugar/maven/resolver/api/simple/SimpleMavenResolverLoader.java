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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.xml.parsers.SAXParserFactory;
import team.idealstate.sugar.maven.exception.MavenException;
import team.idealstate.sugar.maven.resolver.api.MavenResolver;
import team.idealstate.sugar.maven.resolver.spi.MavenResolverFactory;
import team.idealstate.sugar.maven.resolver.spi.MavenResolverLoader;
import team.idealstate.sugar.service.ServiceLoader;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import team.idealstate.sugar.validate.annotation.Nullable;

public final class SimpleMavenResolverLoader implements MavenResolverLoader {

    public static final String NAME = "simple";

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @NotNull
    @Override
    public MavenResolver load(@NotNull File configurationFile, @Nullable ClassLoader classLoader) {
        Validation.requireNotNull(configurationFile, "Configuration file must not be null.");
        try {
            return load(Files.newInputStream(configurationFile.toPath()), classLoader);
        } catch (IOException e) {
            throw new MavenException(e);
        }
    }

    @NotNull
    @Override
    public MavenResolver load(@NotNull InputStream configurationInputStream, @Nullable ClassLoader classLoader) {
        Validation.requireNotNull(configurationInputStream, "Configuration input stream must not be null.");
        try (InputStream input = configurationInputStream) {
            MavenResolverFactory mavenResolverFactory =
                    ServiceLoader.singleton(MavenResolverFactory.class, classLoader, SimpleMavenResolverFactory::new);
            SimpleMavenResolverConfiguration configuration = new SimpleMavenResolverConfiguration();
            SAXParserFactory.newInstance().newSAXParser().parse(input, configuration);
            return mavenResolverFactory.create(configuration);
        } catch (Throwable e) {
            if (e instanceof MavenException) {
                throw (MavenException) e;
            }
            throw new MavenException(e);
        }
    }
}
