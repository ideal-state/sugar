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

package team.idealstate.sugar.datastructure.mapping;

import team.idealstate.sugar.datastructure.array.ArrayUtils;
import team.idealstate.sugar.object.ObjectUtils;
import team.idealstate.sugar.validation.ValidateUtils;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>MapUtils</p>
 *
 * <p>创建于 2024/3/24 20:09</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes"})
public abstract class MapUtils {

    @NotNull
    public static <K, V> Map<K, V> emptyIfNull(@Nullable Map<K, V> map) {
        return ObjectUtils.isNull(map) ? emptyMap() : map;
    }

    @NotNull
    public static <K, V> Map<K, V> defaultIfEmpty(@NotNull Map<K, V> map, @NotNull Map<K, V> def) {
        ValidateUtils.notNull(map, "map must not be null");
        ValidateUtils.notNull(def, "def must not be null");
        return isEmpty(map) ? def : map;
    }

    @NotNull
    public static <K, V> Map<K, V> defaultIfNullOrEmpty(@Nullable Map<K, V> map, @NotNull Map<K, V> def) {
        ValidateUtils.notNull(def, "def must not be null");
        if (isNullOrEmpty(map)) {
            return def;
        } else {
            assert map != null;
            return map;
        }
    }

    public static boolean isEmpty(@NotNull Map map) {
        ValidateUtils.notNull(map, "map must not be null");
        return map.isEmpty();
    }

    public static boolean isNullOrEmpty(@Nullable Map map) {
        return ObjectUtils.isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(@NotNull Map map) {
        ValidateUtils.notNull(map, "map must not be null");
        return !map.isEmpty();
    }

    public static boolean isNotNullOrEmpty(@Nullable Map map) {
        return ObjectUtils.isNotNull(map) && !map.isEmpty();
    }

    @NotNull
    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    @NotNull
    public static <K, V> Map.Entry<K, V> entryOf(K key, V value) {
        return new MapEntry<>(key, value);
    }

    @SafeVarargs
    @NotNull
    public static <K, V> Map<K, V> mapOf(Map.Entry<K, V>... entries) {
        if (ArrayUtils.isEmpty(entries)) {
            return new HashMap<>(16, 0.6F);
        }
        Map<K, V> map = new HashMap<>(entries.length, 0.6F);
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @SafeVarargs
    @NotNull
    public static <K, V> Map<K, V> linkedMapOf(Map.Entry<K, V>... entries) {
        if (ArrayUtils.isEmpty(entries)) {
            return new LinkedHashMap<>(16, 0.6F);
        }
        Map<K, V> map = new LinkedHashMap<>(entries.length, 0.6F);
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @SafeVarargs
    @NotNull
    public static <K, V> Map<K, V> concurrentMapOf(Map.Entry<K, V>... entries) {
        if (ArrayUtils.isEmpty(entries)) {
            return new ConcurrentHashMap<>(16, 0.6F);
        }
        return new ConcurrentHashMap<>(mapOf(entries));
    }

    @NotNull
    public static <K, V> Map<K, V> mapOf(@NotNull Map<K, V> map) {
        if (isEmpty(map)) {
            return new HashMap<>(16, 0.6F);
        }
        return new HashMap<>(map);
    }

    @NotNull
    public static <K, V> Map<K, V> linkedMapOf(@NotNull Map<K, V> map) {
        if (isEmpty(map)) {
            return new LinkedHashMap<>(16, 0.6F);
        }
        return new LinkedHashMap<>(map);
    }

    @NotNull
    public static <K, V> Map<K, V> concurrentMapOf(@NotNull Map<K, V> map) {
        if (isEmpty(map)) {
            return new ConcurrentHashMap<>(16, 0.6F);
        }
        return new ConcurrentHashMap<>(map);
    }

    private static final class MapEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
