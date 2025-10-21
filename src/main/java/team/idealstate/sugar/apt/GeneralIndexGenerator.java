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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.apt.annotation.Index;

/**
 *
 *
 * <h3>通用索引生成器</h3>
 *
 * <p>生成或追加类的全限定名称到文件 {@link Index#value()} 中。
 *
 * @see Index
 * @see AbstractProcessor
 */
public final class GeneralIndexGenerator extends AbstractIndexGenerator<Index> {

    private final Set<ElementKind> allowedElementKinds =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ElementKind.CLASS, ElementKind.INTERFACE)));

    public GeneralIndexGenerator() {
        super(Index.class);
    }

    @Override
    protected boolean isAllowedElementKind(@NotNull ElementKind elementKind) {
        return allowedElementKinds.contains(elementKind);
    }

    @Override
    protected @NotNull List<@NotNull String> resolveDestinationPaths(
            @NotNull TypeElement typeElement, @NotNull Index annotation) {
        return Collections.singletonList(annotation.value());
    }
}
