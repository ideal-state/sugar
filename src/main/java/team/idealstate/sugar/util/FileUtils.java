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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.exception.WeakException;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>文件工具</h3>
 *
 * <p>提供常用的文件操作工具方法封装。
 */
public abstract class FileUtils {

    /**
     *
     *
     * <h4>为文件创建输入流</h4>
     *
     * @param file 目标文件
     * @return 输入流
     * @throws WeakException 创建文件输入流时抛出的 {@link FileNotFoundException} 的运行时包装
     * @see FileInputStream
     */
    @NotNull
    public static InputStream inputStream(@NotNull File file) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw WeakException.suppress(e);
        }
    }

    /**
     *
     *
     * <h4>为文件创建输出流</h4>
     *
     * @param file 目标文件
     * @return 输出流
     * @throws WeakException 创建文件输出流时抛出的 {@link FileNotFoundException} 的运行时包装
     * @see FileOutputStream
     */
    @NotNull
    public static OutputStream outputStream(@NotNull File file) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw WeakException.suppress(e);
        }
    }

    /**
     *
     *
     * <h4>为文件创建字符读取器</h4>
     *
     * @param file 目标文件
     * @return 字符读取器
     * @see IOUtils#DEFAULT_CHARSET
     * @see #reader(File, Charset)
     */
    @NotNull
    public static Reader reader(@NotNull File file) {
        Validation.requireNotNull(file, "file must not be null.");
        return reader(file, IOUtils.DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>为文件创建字符读取器</h4>
     *
     * @param file 目标文件
     * @param charset 字符集
     * @return 字符读取器
     * @throws WeakException 创建文件输入流时抛出的 {@link FileNotFoundException} 的运行时包装
     * @see #inputStream(File)
     * @see IOUtils#reader(InputStream, Charset)
     */
    @NotNull
    public static Reader reader(@NotNull File file, @NotNull Charset charset) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return IOUtils.reader(inputStream(file), charset);
    }

    /**
     *
     *
     * <h4>为文件创建字符写入器</h4>
     *
     * @param file 目标文件
     * @return 字符写入器
     * @see IOUtils#DEFAULT_CHARSET
     * @see #writer(File, Charset)
     */
    @NotNull
    public static Writer writer(@NotNull File file) {
        Validation.requireNotNull(file, "file must not be null.");
        return writer(file, IOUtils.DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>为文件创建字符写入器</h4>
     *
     * @param file 目标文件
     * @param charset 字符集
     * @return 字符写入器
     * @throws WeakException 创建文件输出流时抛出的 {@link FileNotFoundException} 的运行时包装
     * @see #outputStream(File)
     * @see IOUtils#writer(OutputStream, Charset)
     */
    @NotNull
    public static Writer writer(@NotNull File file, @NotNull Charset charset) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return IOUtils.writer(outputStream(file), charset);
    }

    /**
     *
     *
     * <h4>为文件创建缓冲区字符读取器</h4>
     *
     * @param file 目标文件
     * @return 缓冲区字符读取器
     * @see IOUtils#DEFAULT_CHARSET
     * @see IOUtils#DEFAULT_BUFFER_SIZE
     * @see #bufferedReader(File, Charset)
     */
    @NotNull
    public static BufferedReader bufferedReader(@NotNull File file) {
        Validation.requireNotNull(file, "file must not be null.");
        return bufferedReader(file, IOUtils.DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>为文件创建缓冲区字符读取器</h4>
     *
     * @param file 目标文件
     * @param charset 字符集
     * @return 缓冲区字符读取器
     * @throws WeakException 创建文件输入流时抛出的 {@link FileNotFoundException} 的运行时包装
     * @see #inputStream(File)
     * @see IOUtils#bufferedReader(InputStream, Charset)
     */
    @NotNull
    public static BufferedReader bufferedReader(@NotNull File file, @NotNull Charset charset) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return IOUtils.bufferedReader(inputStream(file), charset);
    }

    /**
     *
     *
     * <h4>为文件创建缓冲区字符写入器</h4>
     *
     * @param file 目标文件
     * @return 缓冲区字符写入器
     * @see IOUtils#DEFAULT_CHARSET
     * @see IOUtils#DEFAULT_BUFFER_SIZE
     * @see #bufferedWriter(File, Charset)
     */
    @NotNull
    public static Writer bufferedWriter(@NotNull File file) {
        Validation.requireNotNull(file, "file must not be null.");
        return bufferedWriter(file, IOUtils.DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>为文件创建缓冲区字符写入器</h4>
     *
     * @param file 目标文件
     * @param charset 字符集
     * @return 缓冲区字符写入器
     * @throws WeakException 创建文件输出流时抛出的 {@link FileNotFoundException} 的运行时包装
     * @see #outputStream(File)
     * @see IOUtils#bufferedWriter(OutputStream, Charset)
     */
    @NotNull
    public static Writer bufferedWriter(@NotNull File file, @NotNull Charset charset) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return IOUtils.bufferedWriter(outputStream(file), charset);
    }

    /**
     *
     *
     * <h4>读取文件内容到字节数组</h4>
     *
     * @param file 目标文件
     * @return 包含文件内容的字节数组
     * @throws WeakException 读取文件内容时抛出的 {@link IOException} 的运行时包装
     * @see #inputStream(File)
     * @see IOUtils#readBytes(InputStream)
     */
    public static byte @NotNull [] readBytes(@NotNull File file) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        return IOUtils.readBytes(inputStream(file));
    }

    /**
     *
     *
     * <h4>读取文件内容到行列表</h4>
     *
     * @param file 目标文件
     * @return 读取到的行列表
     * @throws WeakException 读取文件内容时抛出的 {@link IOException} 的运行时包装
     */
    @NotNull
    public static List<@NotNull String> readLines(@NotNull File file) throws WeakException {
        Validation.requireNotNull(file, "file must not be null.");
        return IOUtils.readLines(reader(file));
    }
}
