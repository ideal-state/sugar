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

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.validate.Validation;

/**
 *
 *
 * <h3>UUID 工具</h3>
 *
 * <p>提供常用 UUID 工具方法封装。
 *
 * @see UUID
 */
public abstract class UUIDUtils {

    /**
     *
     *
     * <h4>默认的交换标志（不交换）</h4>
     */
    public static final boolean DEFAULT_SWAP_FLAG = false;

    /**
     *
     *
     * <h4>以字节数组表示 UUID 时的固定长度</h4>
     */
    private static final int BYTES_LEN = 16;
    /**
     *
     *
     * <h4>以字节数组表示 UUID 时的后缀长度</h4>
     */
    private static final int BYTES_SUFFIX = 8;
    /**
     *
     *
     * <h4>以字节数组表示 UUID 时的交换位置</h4>
     */
    private static final int[] SWAP = {6, 4, 0, 2};
    /**
     *
     *
     * <h4>以字节数组表示 UUID 时的交换位置（用于恢复）</h4>
     */
    private static final int[] RESTORE = {4, 6, 2, 0};

    /**
     *
     *
     * <h4>从字节数组中恢复 UUID</h4>
     *
     * @param bytes 以字节数组表示的 UUID
     * @return 恢复后的 UUID
     * @see #DEFAULT_SWAP_FLAG
     * @see #fromBytes(byte[], boolean)
     */
    @NotNull
    public static UUID fromBytes(byte @NotNull [] bytes) {
        Validation.isNotNull(bytes, "bytes must not be null.");
        return fromBytes(bytes, DEFAULT_SWAP_FLAG);
    }

    /**
     *
     *
     * <h4>从字节数组中恢复 UUID</h4>
     *
     * @param bytes 以字节数组表示的 UUID
     * @param swapFlag 是否交换字节数组的顺序
     * @return 恢复后的 UUID
     */
    @NotNull
    public static UUID fromBytes(byte @NotNull [] bytes, boolean swapFlag) {
        Validation.isNotNull(bytes, "bytes must not be null.");
        Validation.isTrue(bytes.length == BYTES_LEN, "bytes's length must be equal to " + BYTES_LEN + ".");
        if (swapFlag) {
            byte[] copiedBytes = new byte[BYTES_LEN];
            for (int i = 0, j = 0; j < SWAP.length; j++) {
                int k = SWAP[j];
                copiedBytes[i++] = bytes[k++];
                copiedBytes[i++] = bytes[k];
            }
            System.arraycopy(bytes, BYTES_SUFFIX, copiedBytes, BYTES_SUFFIX, BYTES_LEN - BYTES_SUFFIX);
            bytes = copiedBytes;
        }
        long mostBit = ((((long) bytes[0] & 0xFF) << 56)
                | (((long) bytes[1] & 0xFF) << 48)
                | (((long) bytes[2] & 0xFF) << 40)
                | (((long) bytes[3] & 0xFF) << 32)
                | (((long) bytes[4] & 0xFF) << 24)
                | (((long) bytes[5] & 0xFF) << 16)
                | (((long) bytes[6] & 0xFF) << 8)
                | (((long) bytes[7] & 0xFF)));
        long leastBit = ((((long) bytes[8] & 0xFF) << 56)
                | (((long) bytes[9] & 0xFF) << 48)
                | (((long) bytes[10] & 0xFF) << 40)
                | (((long) bytes[11] & 0xFF) << 32)
                | (((long) bytes[12] & 0xFF) << 24)
                | (((long) bytes[13] & 0xFF) << 16)
                | (((long) bytes[14] & 0xFF) << 8)
                | (((long) bytes[15] & 0xFF)));
        return new UUID(mostBit, leastBit);
    }

    /**
     *
     *
     * <h4>将 UUID 转换为字节数组</h4>
     *
     * @param uuid 待转换的 UUID
     * @return 以字节数组表示的 UUID
     * @see #DEFAULT_SWAP_FLAG
     * @see #toBytes(UUID, boolean)
     */
    public static byte @NotNull [] toBytes(@NotNull UUID uuid) {
        Validation.isNotNull(uuid, "uuid must not be null.");
        return toBytes(uuid, DEFAULT_SWAP_FLAG);
    }

    /**
     *
     *
     * <h4>将 UUID 转换为字节数组</h4>
     *
     * @param uuid 待转换的 UUID
     * @param swapFlag 是否交换字节数组的顺序
     * @return 以字节数组表示的 UUID
     */
    public static byte @NotNull [] toBytes(@NotNull UUID uuid, boolean swapFlag) {
        Validation.isNotNull(uuid, "uuid must not be null.");
        long mostBit = uuid.getMostSignificantBits();
        byte[] bytes = new byte[16];
        bytes[0] = (byte) ((mostBit >> 56) & 0xFF);
        bytes[1] = (byte) ((mostBit >> 48) & 0xFF);
        bytes[2] = (byte) ((mostBit >> 40) & 0xFF);
        bytes[3] = (byte) ((mostBit >> 32) & 0xFF);
        bytes[4] = (byte) ((mostBit >> 24) & 0xFF);
        bytes[5] = (byte) ((mostBit >> 16) & 0xFF);
        bytes[6] = (byte) ((mostBit >> 8) & 0xFF);
        bytes[7] = (byte) (mostBit & 0xFF);
        long leastBit = uuid.getLeastSignificantBits();
        bytes[8] = (byte) ((leastBit >> 56) & 0xFF);
        bytes[9] = (byte) ((leastBit >> 48) & 0xFF);
        bytes[10] = (byte) ((leastBit >> 40) & 0xFF);
        bytes[11] = (byte) ((leastBit >> 32) & 0xFF);
        bytes[12] = (byte) ((leastBit >> 24) & 0xFF);
        bytes[13] = (byte) ((leastBit >> 16) & 0xFF);
        bytes[14] = (byte) ((leastBit >> 8) & 0xFF);
        bytes[15] = (byte) (leastBit & 0xFF);
        if (swapFlag) {
            for (int i = 0, j = 0; j < RESTORE.length; j++) {
                int k = RESTORE[j];
                byte b = bytes[i];
                bytes[i++] = bytes[k];
                bytes[k++] = b;
                b = bytes[i];
                bytes[i++] = bytes[k];
                bytes[k] = b;
            }
        }
        return bytes;
    }
}
