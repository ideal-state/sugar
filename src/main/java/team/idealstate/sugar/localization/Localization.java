/*
 *    Copyright 2024 ideal-state
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

package team.idealstate.sugar.localization;

import team.idealstate.sugar.localization.exception.LocalizationException;
import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

import static team.idealstate.sugar.function.Functional.functional;

public abstract class Localization {

    private static final String ROOT_DIR = "localization";
    private static final char SEPARATOR = '/';
    private static final String EXTENSION = ".properties";

    private static String localizationFileName(@NotNull Class<?> localizationInterface, @NotNull Locale locale) {
        StringJoiner stringJoiner = new StringJoiner(String.valueOf(SEPARATOR));
        stringJoiner.add(ROOT_DIR);
        stringJoiner.add(locale.toTag());
        stringJoiner.add(localizationInterface.getName().replace('.', SEPARATOR) + EXTENSION);
        return stringJoiner.toString();
    }

    @NotNull
    public static <T> T localize(@NotNull Class<T> localizationInterface, Locale locale, Locale fallbackLocale) {
        Validation.notNull(localizationInterface, "localizationInterface must not be null");
        Validation.vote(localizationInterface.isInterface(), "localizationInterface must be an interface");
        locale = locale != null ? locale : Locale.current();
        ClassLoader classLoader = localizationInterface.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(localizationFileName(localizationInterface, locale));
        if (inputStream == null) {
            if (fallbackLocale != null) {
                inputStream = classLoader.getResourceAsStream(localizationFileName(localizationInterface, fallbackLocale));
            }
        }
        if (inputStream == null) {
            throw new LocalizationException("Cannot find localization file for " + localizationInterface.getName() + " in " + locale.toTag());
        }
        Properties properties = new Properties();
        functional(inputStream)
                .convert(InputStreamReader::new)
                .use(properties::load);
        Map<String, Object> dictionary = new HashMap<>(properties.size());
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            dictionary.put(entry.getKey().toString(), entry.getValue());
        }
        return localize(localizationInterface, dictionary);
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    public static <T> T localize(@NotNull Class<T> localizationInterface, @NotNull Map<String, Object> dictionary) {
        Validation.notNull(localizationInterface, "localizationInterface must not be null");
        Validation.vote(localizationInterface.isInterface(), "localizationInterface must be an interface");
        Validation.notNull(dictionary, "dictionary must not be null");

        return (T) Proxy.newProxyInstance(
                localizationInterface.getClassLoader(),
                new Class[]{localizationInterface},
                new InternalLocalizationHandler(dictionary)
        );
    }
}
