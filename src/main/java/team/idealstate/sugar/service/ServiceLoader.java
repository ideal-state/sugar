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

package team.idealstate.sugar.service;

import java.util.Iterator;
import java.util.function.Supplier;
import team.idealstate.sugar.service.exception.ServiceException;
import team.idealstate.sugar.validate.annotation.NotNull;

@SuppressWarnings({"unused"})
public abstract class ServiceLoader {

    public static <T> T singleton(@NotNull Class<T> serviceType) {
        return singleton(serviceType, null, null);
    }

    public static <T> T singleton(@NotNull Class<T> serviceType, ClassLoader classLoader) {
        return singleton(serviceType, classLoader, null);
    }

    public static <T> T singleton(@NotNull Class<T> serviceType, ClassLoader classLoader, Supplier<T> defaultSupplier) {
        java.util.ServiceLoader<T> serviceLoader = java.util.ServiceLoader.load(serviceType, classLoader);
        Iterator<T> iterator = serviceLoader.iterator();
        String serviceName = serviceType.getSimpleName();
        T service = null;
        while (iterator.hasNext()) {
            if (service == null) {
                service = iterator.next();
            } else {
                throw new ServiceException(String.format("More than one service found for %s.", serviceName));
            }
        }
        if (service == null) {
            if (defaultSupplier == null) {
                throw new ServiceException(String.format("No service found for %s.", serviceName));
            }
            service = defaultSupplier.get();
        }
        return service;
    }
}
