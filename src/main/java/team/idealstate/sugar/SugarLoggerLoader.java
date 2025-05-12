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

import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.logging.Logger;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class SugarLoggerLoader implements ClassFileTransformer {

    public static void addTo(@NotNull Instrumentation instrumentation) {
        instrumentation.addTransformer(new SugarLoggerLoader(instrumentation), false);
    }

    private volatile Instrumentation instrumentation;

    private SugarLoggerLoader(@NotNull Instrumentation instrumentation) {
        Validation.notNull(instrumentation, "instrumentation must not be null.");
        this.instrumentation = instrumentation;
    }

    private static final Set<Integer> LOADING = new CopyOnWriteArraySet<>();

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] buffer) {
        Instrumentation instrumentation = this.instrumentation;
        if (instrumentation == null) {
            return null;
        }
        int identityHashCode = System.identityHashCode(loader);
        if (loader == null || ClassLoader.getSystemClassLoader().equals(loader) || classBeingRedefined != null || !LOADING.add(identityHashCode)) {
            return null;
        }
        try {
            String loggerClassName = Logger.class.getName();
            try {
                if (!Logger.class.equals(Class.forName(loggerClassName, false, loader))) {
                    return null;
                }
            } catch (ClassNotFoundException e) {
                return null;
            }
            Logger logger = Log.getLogger(loader);
            if (logger != null) {
                Log.setLogger(logger);
                this.instrumentation = null;
                instrumentation.removeTransformer(this);
            }
        } finally {
            LOADING.remove(identityHashCode);
        }
        return null;
    }
}
