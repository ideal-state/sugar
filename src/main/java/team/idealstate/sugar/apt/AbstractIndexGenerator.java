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

package team.idealstate.sugar.apt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.util.IOUtils;
import team.idealstate.sugar.util.StackTraceUtils;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>抽象索引生成器</h3>
 *
 * <p>作为抽象层提供基本索引生成方法。
 *
 * @param <A> 提供索引生成配置的注解类型
 * @see GeneralIndexGenerator
 * @see ProvidesIndexGenerator
 * @see AbstractProcessor
 */
public abstract class AbstractIndexGenerator<A extends Annotation> extends AbstractProcessor {

    protected final Class<A> annotationClass;
    protected final Set<String> annotationTypes;
    private final Map<String, Deque<String>> mappings = new ConcurrentHashMap<>();

    protected AbstractIndexGenerator(@NotNull Class<A> annotationClass) {
        Validation.requireNotNull(annotationClass, "annotationClass must not be null.");
        this.annotationClass = annotationClass;
        this.annotationTypes = Collections.singleton(annotationClass.getName());
    }

    @Override
    public final Set<String> getSupportedAnnotationTypes() {
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void addMapping(@NotNull String destinationPath, @NotNull String qualifiedName) {
        mappings.computeIfAbsent(destinationPath, k -> new ConcurrentLinkedDeque<>())
                .addLast(qualifiedName);
    }

    @NotNull
    protected abstract List<@NotNull String> resolveDestinationPaths(
            @NotNull TypeElement typeElement, @NotNull A annotation);

    protected abstract boolean isAllowedElementKind(@NotNull ElementKind elementKind);

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        if (roundEnv.processingOver()) {
            Filer filer = processingEnv.getFiler();
            for (Map.Entry<String, Deque<String>> entry : mappings.entrySet()) {
                String destinationPath = entry.getKey();
                Deque<String> value = entry.getValue();
                Set<String> qualifiedNames = new LinkedHashSet<>(Math.max(value.size() * 2, 16));
                FileObject fileObject;
                try {
                    try {
                        fileObject = filer.getResource(StandardLocation.CLASS_OUTPUT, "", destinationPath);
                        qualifiedNames.addAll(IOUtils.readLines(IOUtils.bufferedReader(fileObject.openInputStream())));
                    } catch (FileNotFoundException | NoSuchFileException ignored) {
                        fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", destinationPath);
                    }
                    qualifiedNames.addAll(value);
                    IOUtils.consume(IOUtils.bufferedWriter(fileObject.openOutputStream()), it -> {
                        for (String qualifiedName : qualifiedNames) {
                            it.append(qualifiedName);
                            it.newLine();
                            it.flush();
                        }
                    });
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, StackTraceUtils.makeDetail(e));
                }
            }
        } else {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotationClass)) {
                if (!(element instanceof TypeElement) || !isAllowedElementKind(element.getKind())) {
                    continue;
                }
                TypeElement typeElement = (TypeElement) element;
                A annotation = element.getAnnotation(annotationClass);
                List<String> destinationPaths;
                try {
                    destinationPaths = resolveDestinationPaths(typeElement, annotation);
                } catch (Throwable e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, StackTraceUtils.makeDetail(e), typeElement);
                    continue;
                }
                if (destinationPaths.isEmpty()) {
                    continue;
                }
                String qualifiedName = typeElement.getQualifiedName().toString();
                for (String destinationPath : destinationPaths) {
                    addMapping(destinationPath, qualifiedName);
                }
            }
        }
        return false;
    }

    @NotNull
    protected static Stream<@NotNull String> visitQualifiedNames(
            @NotNull MirroredTypesException mirroredTypesException) {
        SimpleTypeVisitor6<Name, Void> qualifiedNameVisitor = new SimpleTypeVisitor6<Name, Void>() {
            @Override
            public Name visitDeclared(DeclaredType t, Void unused) {
                return t.asElement()
                        .accept(
                                new SimpleElementVisitor6<Name, Void>() {
                                    @Override
                                    public Name visitType(TypeElement e1, Void unused) {
                                        return e1.getQualifiedName();
                                    }
                                },
                                null);
            }
        };
        return mirroredTypesException.getTypeMirrors().stream()
                .map(typeMirror -> typeMirror.accept(qualifiedNameVisitor, null).toString());
    }
}
