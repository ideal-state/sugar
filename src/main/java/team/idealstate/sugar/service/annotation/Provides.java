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

package team.idealstate.sugar.service.annotation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import team.idealstate.sugar.string.StringUtils;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Provides {

    Class<?>[] value() default {};

    class AnnotationProcessor extends AbstractProcessor {

        private static final String OUTPUT_SERVICES_DIR = "META-INF/services/";

        private final Map<String, List<String>> providers = new ConcurrentHashMap<>();

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return Collections.singleton(Provides.class.getName());
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latestSupported();
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (roundEnv.processingOver()) {
                Filer filer = processingEnv.getFiler();
                for (Map.Entry<String, List<String>> entry : providers.entrySet()) {
                    String service = entry.getKey();
                    List<String> providers = entry.getValue();
                    String path = OUTPUT_SERVICES_DIR + service;
                    try {
                        FileObject fileObject;
                        Set<String> set = null;
                        try {
                            fileObject = filer.getResource(StandardLocation.CLASS_OUTPUT, "", path);
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(fileObject.openInputStream(), StandardCharsets.UTF_8))) {
                                set = reader.lines().collect(Collectors.toCollection(LinkedHashSet::new));
                            }
                        } catch (FileNotFoundException | NoSuchFileException ignored) {
                        }
                        fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", path);
                        if (set == null) {
                            set = new LinkedHashSet<>(providers);
                        } else {
                            set.addAll(providers);
                        }
                        String content = String.join("\n", set);
                        try (BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(fileObject.openOutputStream(), StandardCharsets.UTF_8))) {
                            writer.write(content);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                for (Element element : roundEnv.getElementsAnnotatedWith(Provides.class)) {
                    if (!ElementKind.CLASS.equals(element.getKind()) || !(element instanceof TypeElement)) {
                        continue;
                    }
                    Provides provides = element.getAnnotation(Provides.class);
                    Set<String> services = new LinkedHashSet<>();
                    try {
                        Class<?>[] classes = provides.value();
                        for (Class<?> cls : classes) {
                            services.add(cls.getName());
                        }
                    } catch (MirroredTypesException e) {
                        for (TypeMirror typeMirror : e.getTypeMirrors()) {
                            services.add(typeMirror
                                    .accept(
                                            new SimpleTypeVisitor6<Name, Void>() {
                                                @Override
                                                public Name visitDeclared(DeclaredType t, Void unused) {
                                                    return t.asElement()
                                                            .accept(
                                                                    new SimpleElementVisitor6<Name, Void>() {
                                                                        @Override
                                                                        public Name visitType(
                                                                                TypeElement e1, Void unused) {
                                                                            return e1.getQualifiedName();
                                                                        }
                                                                    },
                                                                    null);
                                                }
                                            },
                                            null)
                                    .toString());
                        }
                    }
                    if (services.isEmpty()) {
                        continue;
                    }
                    String provider = ((TypeElement) element).getQualifiedName().toString();
                    for (String service : services) {
                        if (StringUtils.isNotBlank(service)) {
                            providers
                                    .computeIfAbsent(service, k -> new CopyOnWriteArrayList<>())
                                    .add(provider);
                        }
                    }
                }
            }
            return false;
        }
    }
}
