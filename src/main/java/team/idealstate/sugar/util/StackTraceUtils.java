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

import java.io.PrintWriter;
import java.io.StringWriter;
import org.jetbrains.annotations.NotNull;

/**
 *
 *
 * <h3>堆栈追踪工具</h3>
 *
 * <p>提供常用堆栈追踪操作工具方法封装。
 */
public abstract class StackTraceUtils {

    /**
     *
     *
     * <h4>生成异常携带的信息和堆栈细节</h4>
     *
     * @param throwable 异常项
     * @return 异常携带的信息和堆栈细节
     * @see Throwable#printStackTrace(PrintWriter)
     */
    @NotNull
    public static String makeDetail(@NotNull Throwable throwable) {
        StringWriter stackTrace = new StringWriter(IOUtils.DEFAULT_BUFFER_SIZE);
        IOUtils.consume(new PrintWriter(stackTrace), throwable::printStackTrace);
        String message = throwable.getLocalizedMessage();
        if (message == null) {
            return "\n" + stackTrace;
        }
        return message + "\n" + stackTrace;
    }
}
