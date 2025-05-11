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
import team.idealstate.sugar.service.ServiceLoader;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import team.idealstate.sugar.validate.annotation.Nullable;

public abstract class Log {

    public static final String LOG_LEVEL_KEY = "sugar.log.level";
    private static volatile Logger globalLogger = null;
    private static volatile LogLevel globalLevel;

    static {
        String logLevel = System.getProperty(LOG_LEVEL_KEY);
        if (logLevel == null) {
            globalLevel = LogLevel.INFO;
        } else {
            try {
                globalLevel = LogLevel.valueOf(logLevel);
            } catch (IllegalArgumentException ignored) {
                globalLevel = LogLevel.INFO;
            }
        }
    }

    @NotNull
    public static Logger getLogger() {
        if (globalLogger == null) {
            synchronized (Log.class) {
                if (globalLogger == null) {
                    try {
                        globalLogger = ServiceLoader.singleton(Logger.class, Logger.class.getClassLoader());
                    } catch (NoClassDefFoundError e) {
                        System.err.println(makeThrowableDetails(e));
                        return SystemLogger.instance();
                    }
                }
            }
        }
        return globalLogger;
    }

    @Nullable
    public static Logger setLogger(@NotNull Logger logger) {
        Validation.notNull(logger, "Logger must not be null.");
        Logger globalLogger = Log.globalLogger;
        Log.globalLogger = logger;
        return globalLogger;
    }

    @NotNull
    public static LogLevel getLevel() {
        return globalLevel;
    }

    @NotNull
    public static LogLevel setLevel(@NotNull LogLevel level) {
        Validation.notNull(level, "Log level must not be null.");
        LogLevel globalLevel = Log.globalLevel;
        Log.globalLevel = level;
        return globalLevel;
    }

    public static boolean isEnabledLevel(@NotNull LogLevel level) {
        return level.asInt() >= getLevel().asInt();
    }

    public static void println(@NotNull LogLevel level, @NotNull Supplier<String> messageProvider) {
        println(getLogger(), level, messageProvider);
    }

    public static void println(
            @NotNull Logger logger, @NotNull LogLevel level, @NotNull Supplier<String> messageProvider) {
        logger.println(level, messageProvider);
    }

    public static void println(@NotNull LogLevel level, String message) {
        println(getLogger(), level, message);
    }

    public static void println(@NotNull Logger logger, @NotNull LogLevel level, String message) {
        logger.println(level, message);
    }

    public static void trace(@NotNull Supplier<String> messageProvider) {
        trace(getLogger(), messageProvider);
    }

    public static void trace(@NotNull Logger logger, @NotNull Supplier<String> messageProvider) {
        logger.trace(messageProvider);
    }

    public static void trace(String message) {
        trace(getLogger(), message);
    }

    public static void trace(@NotNull Logger logger, String message) {
        logger.trace(message);
    }

    public static void debug(@NotNull Supplier<String> messageProvider) {
        debug(getLogger(), messageProvider);
    }

    public static void debug(@NotNull Logger logger, @NotNull Supplier<String> messageProvider) {
        logger.debug(messageProvider);
    }

    public static void debug(String message) {
        debug(getLogger(), message);
    }

    public static void debug(@NotNull Logger logger, String message) {
        logger.debug(message);
    }

    public static void info(@NotNull Supplier<String> messageProvider) {
        info(getLogger(), messageProvider);
    }

    public static void info(@NotNull Logger logger, @NotNull Supplier<String> messageProvider) {
        logger.info(messageProvider);
    }

    public static void info(String message) {
        info(getLogger(), message);
    }

    public static void info(@NotNull Logger logger, String message) {
        logger.info(message);
    }

    public static void warn(@NotNull Supplier<String> messageProvider) {
        warn(getLogger(), messageProvider);
    }

    public static void warn(@NotNull Logger logger, @NotNull Supplier<String> messageProvider) {
        logger.warn(messageProvider);
    }

    public static void warn(String message) {
        warn(getLogger(), message);
    }

    public static void warn(@NotNull Logger logger, String message) {
        logger.warn(message);
    }

    public static void error(@NotNull Supplier<String> messageProvider) {
        error(getLogger(), messageProvider);
    }

    public static void error(@NotNull Logger logger, @NotNull Supplier<String> messageProvider) {
        logger.error(messageProvider);
    }

    public static void error(String message) {
        error(getLogger(), message);
    }

    public static void error(@NotNull Logger logger, String message) {
        logger.error(message);
    }

    public static void error(@NotNull Throwable throwable) {
        error(getLogger(), throwable);
    }

    public static void error(@NotNull Logger logger, @NotNull Throwable throwable) {
        logger.error(throwable);
    }

    public static void fatal(@NotNull Supplier<String> messageProvider) {
        fatal(getLogger(), messageProvider);
    }

    public static void fatal(@NotNull Logger logger, @NotNull Supplier<String> messageProvider) {
        logger.fatal(messageProvider);
    }

    public static void fatal(String message) {
        fatal(getLogger(), message);
    }

    public static void fatal(@NotNull Logger logger, String message) {
        logger.fatal(message);
    }

    public static void fatal(@NotNull Throwable throwable) {
        fatal(getLogger(), throwable);
    }

    public static void fatal(@NotNull Logger logger, @NotNull Throwable throwable) {
        logger.fatal(throwable);
    }
}
