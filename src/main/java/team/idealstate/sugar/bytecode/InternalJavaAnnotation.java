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

package team.idealstate.sugar.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import team.idealstate.sugar.bytecode.api.JavaAnnotatedElement;
import team.idealstate.sugar.bytecode.api.member.JavaClass;
import team.idealstate.sugar.bytecode.api.struct.JavaAnnotation;
import team.idealstate.sugar.function.Lazy;
import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.util.*;

import static team.idealstate.sugar.bytecode.Java.typeof;
import static team.idealstate.sugar.function.Functional.lazy;

class InternalJavaAnnotation implements JavaAnnotation {

    private final String annotationTypeName;
    private final JavaAnnotatedElement annotatedElement;
    private final Map<String, Object> mappings = new HashMap<>(16, 0.6F);

    InternalJavaAnnotation(@NotNull String annotationTypeName, @Nullable JavaAnnotatedElement annotatedElement) {
        Validation.notNull(annotationTypeName, "annotationTypeName must not be null.");
        this.annotationTypeName = annotationTypeName;
        this.annotatedElement = annotatedElement;
    }

    @NotNull
    @Override
    public JavaClass getAnnotationType() {
        return typeof(annotationTypeName);
    }

    @Nullable
    @Override
    public JavaAnnotatedElement getAnnotatedElement() {
        return annotatedElement;
    }

    @NotNull
    @Override
    public Set<String> getMappingNames() {
        return new HashSet<>(mappings.keySet());
    }

    @NotNull
    @Override
    public <V> V getMappingValue(@NotNull String mappingName) {
        Object value = mappings.get(mappingName);
        return AbstractVisitor.unwrapValue(value);
    }

    static abstract class AbstractVisitor extends AnnotationVisitor {

        AbstractVisitor(int api, AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @SuppressWarnings({"unchecked"})
        public static <V> V unwrapValue(Object value) {
            if (value instanceof Lazy) {
                value = ((Lazy<?>) value).get();
            }
            else if (value instanceof List) {
                value = ((List<?>) value).toArray();
                Object[] arr = (Object[]) value;
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] instanceof Lazy) {
                        arr[i] = ((Lazy<?>) arr[i]).get();
                    }
                }
            }
            return (V) value;
        }

        protected abstract void put(String name, Object value);

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (value instanceof Type) {
                Lazy<JavaClass> lazy = lazy(() -> typeof(((Type) value).getClassName()));
                put(name, lazy);
            } else {
                put(name, value);
            }
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            super.visitEnum(name, descriptor, value);
            put(name, new InternalJavaEnum(Type.getType(descriptor).getClassName(), value));
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            AnnotationVisitor annotationVisitor = super.visitAnnotation(name, descriptor);
            InternalJavaAnnotation internalJavaAnnotation = new InternalJavaAnnotation(
                    Type.getType(descriptor).getClassName(), null
            );
            annotationVisitor = new InternalJavaAnnotation.Visitor(api, annotationVisitor, internalJavaAnnotation);
            put(name, internalJavaAnnotation);
            return annotationVisitor;
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            AnnotationVisitor annotationVisitor = super.visitArray(name);
            List<Object> list = new ArrayList<>(8);
            annotationVisitor = new ArrayVisitor(api, annotationVisitor, list);
            put(name, list);
            return annotationVisitor;
        }
    }

    static class Visitor extends AbstractVisitor {

        private final InternalJavaAnnotation internalJavaAnnotation;

        Visitor(int api, AnnotationVisitor annotationVisitor, @NotNull InternalJavaAnnotation internalJavaAnnotation) {
            super(api, annotationVisitor);
            this.internalJavaAnnotation = internalJavaAnnotation;
        }

        protected void put(String name, Object value) {
            internalJavaAnnotation.mappings.put(name, value);
        }
    }

    private static class ArrayVisitor extends AbstractVisitor {

        private final List<Object> list;

        public ArrayVisitor(int api, AnnotationVisitor annotationVisitor, @NotNull List<Object> list) {
            super(api, annotationVisitor);
            Validation.notNull(list, "list must not be null.");
            this.list = list;
        }

        @Override
        protected void put(String name, Object value) {
            list.add(value);
        }
    }
}
