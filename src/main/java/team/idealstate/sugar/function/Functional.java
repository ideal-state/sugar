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

package team.idealstate.sugar.function;

import team.idealstate.sugar.function.closure.Action;
import team.idealstate.sugar.function.closure.Condition;
import team.idealstate.sugar.function.closure.Function;
import team.idealstate.sugar.function.closure.Provider;
import team.idealstate.sugar.function.exception.FunctionExecutionException;
import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;

import java.io.Closeable;
import java.util.Objects;

public interface Functional<T> {

    @NotNull
    static <T> Functional<T> functional(T it) {
        return () -> it;
    }

    @NotNull
    static <T> Functional<T> conditional(T it, Condition<T> condition) {
        return new ConditionFunctional<>(it, condition);
    }

    @NotNull
    static <V> Lazy<V> lazy(V value) {
        return new CachedLazy<>(value);
    }

    @NotNull
    static <V> Lazy<V> lazy(@NotNull Provider<V> provider) {
        Validation.notNull(provider, "provider must not be null.");
        return new CachedLazy<>(provider);
    }

    T it();

    default boolean isNull() {
        return Objects.isNull(it());
    }

    default boolean isNotNull() {
        return Objects.nonNull(it());
    }

    default Functional<T> apply(@NotNull Action<T> action) {
        Validation.notNull(action, "action must not be null.");
        T it = it();
        try {
            action.execute(it);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new FunctionExecutionException(e);
        }
        return this;
    }

    default void run(@NotNull Action<T> action) {
        Validation.notNull(action, "action must not be null.");
        try {
            action.execute(it());
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new FunctionExecutionException(e);
        }
    }

    default void use(@NotNull Action<T> action) {
        Validation.notNull(action, "action must not be null.");
        T it = it();
        try {
            try {
                action.execute(it);
            } finally {
                if (it instanceof Closeable) {
                    ((Closeable) it).close();
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new FunctionExecutionException(e);
        }
    }

    default <R> Functional<R> convert(@NotNull Function<T, R> function) {
        Validation.notNull(function, "function must not be null.");
        try {
            return functional(function.call(it()));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new FunctionExecutionException(e);
        }
    }

    default Functional<T> when(Condition<T> condition) {
        T it = it();
        return new ConditionFunctional<>(it, condition);
    }
}
