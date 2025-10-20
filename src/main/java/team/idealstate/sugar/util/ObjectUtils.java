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

package team.idealstate.sugar.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.sugar.exception.InputOutputException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>对象工具</h3>
 *
 * <p>提供常用的对象操作工具方法封装。
 */
public abstract class ObjectUtils {

    /**
     *
     *
     * <h4>判断对象是否为 {@code null}</h4>
     *
     * @param object 待判断的对象
     * @return 对象是否为 {@code null}
     */
    public static boolean isNull(@Nullable Object object) {
        return object == null;
    }

    /**
     *
     *
     * <h4>判断对象是否为非 {@code null}</h4>
     *
     * @param object 待判断的对象
     * @return 对象是否为非 {@code null}
     */
    public static boolean isNotNull(@Nullable Object object) {
        return object != null;
    }

    /**
     *
     *
     * <h4>复制对象</h4>
     *
     * <p>基于原生序列化的深层复制。
     *
     * @param object 待复制的对象
     * @param <T> 复制的对象的类型
     * @return 复制的对象
     * @throws InputOutputException 拷贝过程中遇到的 {@link IOException} 的运行时包装
     * @see Serializable
     * @see ObjectOutputStream
     * @see ObjectInputStream
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T extends Serializable> T copy(@NotNull T object) {
        Validation.isNotNull(object, "object must not be null.");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(IOUtils.DEFAULT_BUFFER_SIZE);
            IOUtils.consume(new ObjectOutputStream(out), it -> it.writeObject(object));
            return IOUtils.use(
                    new ObjectInputStream(new ByteArrayInputStream(out.toByteArray())), it -> (T) it.readObject());
        } catch (IOException e) {
            throw new InputOutputException(e);
        }
    }
}
