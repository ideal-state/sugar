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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.service.annotation.Provides;

/**
 *
 *
 * <h3>服务实现索引生成器</h3>
 *
 * <p>生成或追加服务实现的全限定名称到目录 {@link ProvidesIndexGenerator#SERVICES_DIR} 下。
 *
 * @see Provides
 * @see AbstractProcessor
 * @see ServiceLoader
 */
public final class ProvidesIndexGenerator extends AbstractIndexGenerator<Provides> {

    /**
     *
     *
     * <h4>服务实现索引文件目标目录路径</h4>
     */
    public static final String SERVICES_DIR = "META-INF/services/";

    private final Set<ElementKind> allowedElementKinds = Collections.singleton(ElementKind.CLASS);

    public ProvidesIndexGenerator() {
        super(Provides.class);
    }

    @Override
    protected boolean isAllowedElementKind(@NotNull ElementKind elementKind) {
        return allowedElementKinds.contains(elementKind);
    }

    @Override
    protected @NotNull List<@NotNull String> resolveDestinationPaths(
            @NotNull TypeElement typeElement, @NotNull Provides annotation) {
        List<String> destinationPaths = new ArrayList<>();
        try {
            Class<?>[] classes = annotation.value();
            for (Class<?> cls : classes) {
                destinationPaths.add(SERVICES_DIR + cls.getName());
            }
        } catch (MirroredTypesException e) {
            visitQualifiedNames(e)
                    .map(qualifiedName -> SERVICES_DIR + qualifiedName)
                    .forEach(destinationPaths::add);
        }
        return destinationPaths;
    }
}
