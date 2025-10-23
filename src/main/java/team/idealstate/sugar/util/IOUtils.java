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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.exception.InputOutputException;
import team.idealstate.sugar.function.WeakConsumer;
import team.idealstate.sugar.function.WeakFunction;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>I/O 工具</h3>
 *
 * <p>提供常用 {@link InputStream}/{@link OutputStream}/{@link Reader}/{@link Writer} 工具方法封装。
 */
public abstract class IOUtils {

    /**
     *
     *
     * <h4>默认字符集</h4>
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     *
     *
     * <h4>默认缓冲区字节大小 </h4>
     */
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     *
     *
     * <h4>默认读取行数限制 </h4>
     */
    public static final int DEFAULT_READ_LINES_LIMIT = -1;

    /**
     *
     *
     * <h4>消费资源（自动关闭）</h4>
     *
     * @param closeable 待消费的资源
     * @param consumer 表示消费过程的消费者
     * @param <T> 资源的类型
     * @throws InputOutputException 消费过程中抛出的 {@link IOException} 的运行时包装
     */
    public static <T extends Closeable> void consume(@NotNull T closeable, @NotNull WeakConsumer<T> consumer)
            throws InputOutputException {
        Validation.requireNotNull(closeable, "closeable must not be null.");
        Validation.requireNotNull(consumer, "consumer must not be null.");
        try (T it = closeable) {
            consumer.convert().accept(it);
        } catch (IOException e) {
            throw new InputOutputException(e);
        }
    }

    /**
     *
     *
     * <h4>使用资源（自动关闭）</h4>
     *
     * @param closeable 待使用的资源
     * @param function 表示使用过程的函数
     * @param <T> 资源类型
     * @param <R> 使用过程中产生的结果的类型
     * @return 使用过程结束后产生的结果
     * @throws InputOutputException 使用过程中抛出的 {@link IOException} 的运行时包装
     */
    public static <T extends Closeable, R> R use(@NotNull T closeable, @NotNull WeakFunction<T, R> function)
            throws InputOutputException {
        Validation.requireNotNull(closeable, "closeable must not be null.");
        Validation.requireNotNull(function, "function must not be null.");
        try (T it = closeable) {
            return function.convert().apply(it);
        } catch (IOException e) {
            throw new InputOutputException(e);
        }
    }

    /**
     *
     *
     * <h4>将输入流转换为字符读取器</h4>
     *
     * @param inputStream 待转换的输入流
     * @return 字符读取器
     * @see #DEFAULT_CHARSET
     * @see #reader(InputStream, Charset)
     */
    @NotNull
    public static Reader reader(@NotNull InputStream inputStream) {
        Validation.requireNotNull(inputStream, "inputStream must not be null.");
        return reader(inputStream, DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>将输入流转换为字符读取器</h4>
     *
     * @param inputStream 待转换的输入流
     * @param charset 字符集
     * @return 字符读取器
     * @see InputStreamReader
     */
    @NotNull
    public static Reader reader(@NotNull InputStream inputStream, @NotNull Charset charset) {
        Validation.requireNotNull(inputStream, "inputStream must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return new InputStreamReader(inputStream, charset);
    }

    /**
     *
     *
     * <h4>将输入流转换为字符写入器</h4>
     *
     * @param outputStream 待转换的输出流
     * @return 字符写入器
     * @see #DEFAULT_CHARSET
     * @see #writer(OutputStream, Charset)
     */
    @NotNull
    public static Writer writer(@NotNull OutputStream outputStream) {
        Validation.requireNotNull(outputStream, "outputStream must not be null.");
        return writer(outputStream, DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>将输出流转换为字符写入器</h4>
     *
     * @param outputStream 待转换的输出流
     * @param charset 字符集
     * @return 字符写入器
     * @see OutputStreamWriter
     */
    @NotNull
    public static Writer writer(@NotNull OutputStream outputStream, @NotNull Charset charset) {
        Validation.requireNotNull(outputStream, "outputStream must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return new OutputStreamWriter(outputStream, charset);
    }

    /**
     *
     *
     * <h4>将输入流转换为缓冲区字符读取器</h4>
     *
     * @param inputStream 待转换的输入流
     * @return 缓冲区字符读取器
     * @see #DEFAULT_CHARSET
     * @see #bufferedReader(InputStream, Charset)
     */
    @NotNull
    public static BufferedReader bufferedReader(@NotNull InputStream inputStream) {
        Validation.requireNotNull(inputStream, "inputStream must not be null.");
        return bufferedReader(inputStream, DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>将输入流转换为缓冲区字符读取器</h4>
     *
     * @param inputStream 待转换的输入流
     * @param charset 字符集
     * @return 缓冲区字符读取器
     * @see InputStreamReader
     * @see BufferedReader
     */
    @NotNull
    public static BufferedReader bufferedReader(@NotNull InputStream inputStream, @NotNull Charset charset) {
        Validation.requireNotNull(inputStream, "inputStream must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return new BufferedReader(new InputStreamReader(inputStream, charset), DEFAULT_BUFFER_SIZE);
    }

    /**
     *
     *
     * <h4>将输入流转换为缓冲区字符写入器</h4>
     *
     * @param outputStream 待转换的输出流
     * @return 缓冲区字符写入器
     * @see #DEFAULT_CHARSET
     * @see #writer(OutputStream, Charset)
     */
    @NotNull
    public static BufferedWriter bufferedWriter(@NotNull OutputStream outputStream) {
        Validation.requireNotNull(outputStream, "outputStream must not be null.");
        return bufferedWriter(outputStream, DEFAULT_CHARSET);
    }

    /**
     *
     *
     * <h4>将输出流转换为缓冲区字符写入器</h4>
     *
     * @param outputStream 待转换的输出流
     * @param charset 字符集
     * @return 缓冲区字符写入器
     * @see OutputStreamWriter
     * @see BufferedWriter
     */
    @NotNull
    public static BufferedWriter bufferedWriter(@NotNull OutputStream outputStream, @NotNull Charset charset) {
        Validation.requireNotNull(outputStream, "outputStream must not be null.");
        Validation.requireNotNull(charset, "charset must not be null.");
        return new BufferedWriter(new OutputStreamWriter(outputStream, charset), DEFAULT_BUFFER_SIZE);
    }

    /**
     *
     *
     * <h4>将输入流的内容传输到输出流（自动关闭）</h4>
     *
     * @param inputStream 待读取的输入流
     * @param outputStream 待写入的输出流
     * @return 已写入的字节数
     * @throws InputOutputException 传输过程中抛出的 {@link IOException} 的运行时包装
     */
    @NotNull
    public static BigInteger transfer(@NotNull InputStream inputStream, @NotNull OutputStream outputStream)
            throws InputOutputException {
        Validation.requireNotNull(inputStream, "inputStream must not be null.");
        Validation.requireNotNull(outputStream, "outputStream must not be null.");
        return use(
                        inputStream,
                        input -> use(outputStream, output -> {
                            AtomicReference<BigInteger> transferred = new AtomicReference<>(BigInteger.ZERO);
                            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                            int read;
                            while ((read = input.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
                                output.write(buffer, 0, read);
                                transferred.set(transferred.get().add(BigInteger.valueOf(read)));
                                output.flush();
                            }
                            return transferred;
                        }))
                .get();
    }

    /**
     *
     *
     * <h4>从输入流读取内容到字节数组（自动关闭）</h4>
     *
     * @param inputStream 待读取的输入流
     * @return 包含输入流内容的字节数组
     * @throws InputOutputException 读取输入流时抛出的 {@link IOException} 的运行时包装
     */
    public static byte @NotNull [] readBytes(@NotNull InputStream inputStream) throws InputOutputException {
        Validation.requireNotNull(inputStream, "inputStream must not be null.");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
        transfer(inputStream, outputStream);
        return outputStream.toByteArray();
    }

    /**
     *
     *
     * <h4>从字符读取器读取行到行列表（自动关闭）</h4>
     *
     * @param reader 待读取的字符读取器
     * @return 读取的行列表
     * @throws InputOutputException 读取字符读取器时抛出的 {@link IOException} 的运行时包装
     * @see #DEFAULT_READ_LINES_LIMIT
     * @see #readLines(Reader, int)
     */
    @NotNull
    public static List<@NotNull String> readLines(@NotNull Reader reader) throws InputOutputException {
        Validation.requireNotNull(reader, "reader must not be null.");
        return readLines(reader, DEFAULT_READ_LINES_LIMIT);
    }

    /**
     *
     *
     * <h4>从字符读取器读取行到行列表（自动关闭）</h4>
     *
     * @param reader 待读取的字符读取器
     * @param limit 获取的行数限制
     * @return 读取的行列表
     * @throws InputOutputException 读取字符读取器时抛出的 {@link IOException} 的运行时包装
     * @see BufferedReader#readLine()
     */
    @NotNull
    public static List<@NotNull String> readLines(@NotNull Reader reader, int limit) throws InputOutputException {
        Validation.requireNotNull(reader, "reader must not be null.");
        WeakFunction<BufferedReader, List<String>> reading = it -> {
            List<String> lines;
            boolean unlimited;
            if (limit == 0) {
                return new ArrayList<>(0);
            } else {
                unlimited = limit < 0;
                if (unlimited) {
                    lines = new ArrayList<>();
                } else {
                    lines = new ArrayList<>(limit);
                }
            }
            String line;
            while ((unlimited || lines.size() < limit) && (line = it.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        };
        if (reader instanceof BufferedReader) {
            return use((BufferedReader) reader, reading);
        }
        return use(new BufferedReader(reader, DEFAULT_BUFFER_SIZE), reading);
    }
}
