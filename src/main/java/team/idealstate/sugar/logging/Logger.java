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

package team.idealstate.sugar.logging;

import static team.idealstate.sugar.logging.Internal.makeThrowableDetails;

import java.util.function.Supplier;
import team.idealstate.sugar.validate.annotation.NotNull;

public interface Logger {

    void println(@NotNull LogLevel level, @NotNull Supplier<String> messageProvider);

    default void println(@NotNull LogLevel level, String message) {
        println(level, () -> message);
    }

    default void trace(@NotNull Supplier<String> messageProvider) {
        LogLevel level = LogLevel.TRACE;
        if (!Log.isEnabledLevel(level)) {
            return;
        }
        println(level, messageProvider.get());
    }

    default void trace(String message) {
        trace(() -> message);
    }

    default void debug(@NotNull Supplier<String> messageProvider) {
        LogLevel level = LogLevel.DEBUG;
        if (!Log.isEnabledLevel(level)) {
            return;
        }
        println(level, messageProvider.get());
    }

    default void debug(String message) {
        debug(() -> message);
    }

    default void info(@NotNull Supplier<String> messageProvider) {
        LogLevel level = LogLevel.INFO;
        if (!Log.isEnabledLevel(level)) {
            return;
        }
        println(level, messageProvider.get());
    }

    default void info(String message) {
        info(() -> message);
    }

    default void warn(@NotNull Supplier<String> messageProvider) {
        LogLevel level = LogLevel.WARN;
        if (!Log.isEnabledLevel(level)) {
            return;
        }
        println(level, messageProvider.get());
    }

    default void warn(String message) {
        warn(() -> message);
    }

    default void error(@NotNull Supplier<String> messageProvider) {
        LogLevel level = LogLevel.ERROR;
        if (!Log.isEnabledLevel(level)) {
            return;
        }
        println(level, messageProvider.get());
    }

    default void error(String message) {
        error(() -> message);
    }

    default void error(@NotNull Throwable throwable) {
        LogLevel level = LogLevel.ERROR;
        if (!Log.isEnabledLevel(level)) {
            return;
        }

        println(level, makeThrowableDetails(throwable));
    }

    default void fatal(@NotNull Supplier<String> messageProvider) {
        LogLevel level = LogLevel.FATAL;
        if (!Log.isEnabledLevel(level)) {
            return;
        }
        println(level, messageProvider.get());
    }

    default void fatal(String message) {
        fatal(() -> message);
    }

    default void fatal(@NotNull Throwable throwable) {
        LogLevel level = LogLevel.FATAL;
        if (!Log.isEnabledLevel(level)) {
            return;
        }
        println(level, makeThrowableDetails(throwable));
    }
}
