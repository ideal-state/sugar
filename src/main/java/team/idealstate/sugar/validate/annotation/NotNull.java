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

package team.idealstate.sugar.validate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import team.idealstate.sugar.logging.Log;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

    class AnnotationProcessor extends AbstractProcessor {

        static {
            hookLombok();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void hookLombok() {
            Class<?> nonNullClass;
            try {
                nonNullClass = Class.forName("lombok.NonNull");
            } catch (ClassNotFoundException e) {
                return;
            }
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Object unsafe = theUnsafe.get(null);
                Method staticFieldBase = unsafeClass.getDeclaredMethod("staticFieldBase", Field.class);
                Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);
                Method getAndSetObject =
                        unsafeClass.getDeclaredMethod("getAndSetObject", Object.class, long.class, Object.class);
                Class<?> handlerUtil =
                        Class.forName("lombok.core.handlers.HandlerUtil", true, nonNullClass.getClassLoader());
                Field field = handlerUtil.getDeclaredField("NONNULL_ANNOTATIONS");
                field.setAccessible(true);
                Object base = staticFieldBase.invoke(unsafe, field);
                long offset = (long) staticFieldOffset.invoke(unsafe, field);
                List list = new ArrayList((List) field.get(null));
                list.add(NotNull.class.getName());
                getAndSetObject.invoke(unsafe, base, offset, Collections.unmodifiableList(list));
                field = handlerUtil.getDeclaredField("BASE_COPYABLE_ANNOTATIONS");
                field.setAccessible(true);
                base = staticFieldBase.invoke(unsafe, field);
                offset = (long) staticFieldOffset.invoke(unsafe, field);
                list = new ArrayList((List) field.get(null));
                list.add(NotNull.class.getName());
                list.add(Nullable.class.getName());
                getAndSetObject.invoke(unsafe, base, offset, Collections.unmodifiableList(list));
            } catch (ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchFieldException
                    | NoSuchMethodException
                    | InvocationTargetException e) {
                Log.error(e);
            }
        }

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return Collections.singleton(NotNull.class.getName());
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latestSupported();
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }
}
