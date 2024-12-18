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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;
import team.idealstate.sugar.bytecode.api.member.JavaClass;
import team.idealstate.sugar.bytecode.api.member.JavaField;
import team.idealstate.sugar.bytecode.api.struct.JavaAnnotation;
import team.idealstate.sugar.validation.Validation;
import team.idealstate.sugar.validation.annotation.NotNull;
import team.idealstate.sugar.validation.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static team.idealstate.sugar.bytecode.Java.typeof;

class InternalJavaField implements JavaField {

    private final JavaClass declaringClass;
    private final String name;
    private final String typeName;
    private final Object defaultValue;
    private final int access;
    private final List<JavaAnnotation> annotations = new ArrayList<>(8);
    InternalJavaField(@NotNull JavaClass declaringClass, int access, @NotNull String name,
                      @NotNull String typeName, @Nullable Object defaultValue) {
        Validation.notNull(declaringClass, "declaringClass must not be null.");
        Validation.notNull(name, "name must not be null.");
        Validation.notNull(typeName, "typeName must not be null.");
        this.declaringClass = declaringClass;
        this.access = access;
        this.name = name;
        this.typeName = typeName;
        this.defaultValue = defaultValue;
    }

    @NotNull
    @Override
    public JavaClass getDeclaringClass() {
        return declaringClass;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public JavaClass getType() {
        return typeof(typeName);
    }

    @Nullable
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int getAccess() {
        return access;
    }

    @NotNull
    @Override
    public JavaAnnotation[] getAnnotations() {
        return annotations.toArray(new JavaAnnotation[0]);
    }

    static class Visitor extends FieldVisitor {

        private final InternalJavaField internalJavaField;

        Visitor(int api, FieldVisitor fieldVisitor, @NotNull InternalJavaField internalJavaField) {
            super(api, fieldVisitor);
            Validation.notNull(internalJavaField, "internalJavaField must not be null.");
            this.internalJavaField = internalJavaField;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
            InternalJavaAnnotation internalJavaAnnotation = new InternalJavaAnnotation(
                    Type.getType(descriptor).getClassName(), internalJavaField
            );
            annotationVisitor = new InternalJavaAnnotation.Visitor(api, annotationVisitor, internalJavaAnnotation);
            internalJavaField.annotations.add(internalJavaAnnotation);
            return annotationVisitor;
        }
    }
}
