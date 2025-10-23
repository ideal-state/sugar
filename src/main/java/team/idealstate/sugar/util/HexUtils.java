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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>十六进制工具</h3>
 *
 * <p>提供常用的十六进制操作工具方法封装
 */
public abstract class HexUtils {

    private static final char[] HEX_DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    private static final Map<Character, Byte> HEX_DIGIT_TABLE;

    static {
        Map<Character, Byte> hexDigitTable = new HashMap<>(HEX_DIGITS.length);
        for (int i = 0; i < HEX_DIGITS.length; i++) {
            hexDigitTable.put(HEX_DIGITS[i], (byte) i);
        }
        HEX_DIGIT_TABLE = Collections.unmodifiableMap(hexDigitTable);
    }

    /**
     *
     *
     * <h4>将十六进制字符串转换为字节数组</h4>
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     * @see #toHexString(byte[])
     */
    public static byte @NotNull [] toBytes(@NotNull String hexString) {
        Validation.isNotNull(hexString, "hexString must not be null.");
        final int length = hexString.length() / 2;
        byte[] binary = new byte[length];
        for (int i = 0; i < length; i++) {
            int j = i * 2;
            binary[i] = (byte)
                    ((HEX_DIGIT_TABLE.get(hexString.charAt(j)) << 4) + HEX_DIGIT_TABLE.get(hexString.charAt(j + 1)));
        }
        return binary;
    }

    /**
     *
     *
     * <h4>将字节数组转换为十六进制字符串</h4>
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     * @see #toBytes(String)
     */
    @NotNull
    public static String toHexString(byte @NotNull [] bytes) {
        Validation.isNotNull(bytes, "bytes must not be null.");
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(HEX_DIGITS[(b & 0xf0) >> 4]).append(HEX_DIGITS[b & 0x0f]);
        }
        return builder.toString();
    }
}
