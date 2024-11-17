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

import team.idealstate.sugar.common.string.StringUtils;
import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;

public interface Locale {

    Locale zh_CN = new StandardLocale("zh", "CN");
    Locale en_US = new StandardLocale("en", "US");

    static Locale current() {
        java.util.Locale locale = java.util.Locale.getDefault();
        return new StandardLocale(locale.getLanguage(), locale.getCountry());
    }

    @NotNull
    String getLanguage();

    @NotNull
    String getCountry();

    @NotNull
    default String toTag() {
        String language = getLanguage();
        String country = getCountry();
        if (StringUtils.isEmpty(country)) {
            return language;
        }
        return language + "_" + country;
    }

    final class StandardLocale implements Locale {

        private final String language;
        private final String country;

        public StandardLocale(@NotNull String language, @NotNull String country) {
            Validation.notNull(language, "language cannot be null");
            Validation.notNull(country, "country cannot be null");
            this.language = language;
            this.country = country;
        }

        @NotNull
        @Override
        public String getLanguage() {
            return language;
        }

        @NotNull
        @Override
        public String getCountry() {
            return country;
        }
    }
}
