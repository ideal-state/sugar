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

package team.idealstate.sugar.test.localization;

import org.junit.jupiter.api.Test;
import team.idealstate.sugar.localization.Locale;
import team.idealstate.sugar.test.localization.entity.HelloWorld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static team.idealstate.sugar.localization.Localization.localize;

public class LocalizationTest {

    private static final String ZH_CN_MESSAGE = "！！！\n你好世界\n！！！";
    private static final String EN_US_MESSAGE = "!!!\nHello World\n!!!";

    @Test
    public void fallback() {
        HelloWorld helloWorld = localize(
                HelloWorld.class, new Locale.StandardLocale("", ""), Locale.zh_CN);
        assertEquals(ZH_CN_MESSAGE, helloWorld.message());
        assertEquals(ZH_CN_MESSAGE, helloWorld.aliasMessage());
    }

    @Test
    public void zh_CN() {
        HelloWorld helloWorld = localize(HelloWorld.class, Locale.zh_CN, null);
        assertEquals(ZH_CN_MESSAGE, helloWorld.message());
        assertEquals(ZH_CN_MESSAGE, helloWorld.aliasMessage());
    }

    @Test
    public void en_US() {
        HelloWorld helloWorld = localize(HelloWorld.class, Locale.en_US, null);
        assertEquals(EN_US_MESSAGE, helloWorld.message());
        assertEquals(EN_US_MESSAGE, helloWorld.aliasMessage());
    }
}
