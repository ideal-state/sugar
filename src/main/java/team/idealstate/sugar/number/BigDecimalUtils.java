/*
 *    Copyright 2024 ideal-state
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

package team.idealstate.sugar.number;

import team.idealstate.sugar.datastructure.mapping.MapUtils;
import team.idealstate.sugar.validation.ValidateUtils;
import team.idealstate.sugar.validation.annotation.NotNull;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>BigDecimalUtils</p>
 *
 * <p>创建于 2024/3/23 17:31</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class BigDecimalUtils {
    private static final Map<String, BigDecimal> CACHE = MapUtils.concurrentMapOf();
    public static final BigDecimal ZERO = valueOf("0.0");

    @NotNull
    public static BigDecimal valueOf(@NotNull String number) {
        ValidateUtils.isNumeric(number, "number must be a number");
        int length = number.length();
        if (length > 1) {
            Integer lastOfZeroIndex = null;
            for (int i = 0; i < length; i++) {
                if (number.charAt(i) != '0') {
                    break;
                }
                lastOfZeroIndex = i;
            }
            if (lastOfZeroIndex != null) {
                number = number.substring(lastOfZeroIndex + 1);
            }
        }
        return CACHE.computeIfAbsent(number, BigDecimal::new);
    }

    @NotNull
    public static BigDecimal valueOf(long number) {
        return CACHE.computeIfAbsent(String.valueOf(number), BigDecimal::new);
    }

    @NotNull
    public static BigDecimal add(@NotNull String number, @NotNull String augend) {
        ValidateUtils.isNumeric(number, "number must be a number");
        ValidateUtils.isNumeric(augend, "augend must be a number");
        BigDecimal ret = valueOf(number).add(valueOf(augend));
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal add(@NotNull BigDecimal number, @NotNull BigDecimal augend) {
        ValidateUtils.notNull(number, "number must be a number");
        ValidateUtils.notNull(augend, "augend must be a number");
        BigDecimal ret = number.add(augend);
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal subtract(@NotNull String number, @NotNull String subtrahend) {
        ValidateUtils.isNumeric(number, "number must be a number");
        ValidateUtils.isNumeric(subtrahend, "subtrahend must be a number");
        BigDecimal ret = valueOf(number).subtract(valueOf(subtrahend));
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal subtract(@NotNull BigDecimal number, @NotNull BigDecimal subtrahend) {
        ValidateUtils.notNull(number, "number must be a number");
        ValidateUtils.notNull(subtrahend, "subtrahend must be a number");
        BigDecimal ret = number.subtract(subtrahend);
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal multiply(@NotNull String number, @NotNull String multiplicand) {
        ValidateUtils.isNumeric(number, "number must be a number");
        ValidateUtils.isNumeric(multiplicand, "multiplicand must be a number");
        BigDecimal ret = valueOf(number).multiply(valueOf(multiplicand));
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal multiply(@NotNull BigDecimal number, @NotNull BigDecimal multiplicand) {
        ValidateUtils.notNull(number, "number must be a number");
        ValidateUtils.notNull(multiplicand, "multiplicand must be a number");
        BigDecimal ret = number.multiply(multiplicand);
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal divide(@NotNull String number, @NotNull String divisor) {
        ValidateUtils.isNumeric(number, "number must be a number");
        ValidateUtils.isNumeric(divisor, "divisor must be a number");
        BigDecimal ret = valueOf(number).divide(valueOf(divisor));
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal divide(@NotNull BigDecimal number, @NotNull BigDecimal divisor) {
        ValidateUtils.notNull(number, "number must be a number");
        ValidateUtils.notNull(divisor, "divisor must be a number");
        BigDecimal ret = number.divide(divisor);
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal mod(@NotNull String number, @NotNull String m) {
        ValidateUtils.isNumeric(number, "number must be a number");
        ValidateUtils.isNumeric(m, "m must be a number");
        BigDecimal ret = valueOf(number).divideAndRemainder(valueOf(m))[1];
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal mod(@NotNull BigDecimal number, @NotNull BigDecimal m) {
        ValidateUtils.notNull(number, "number must be a number");
        ValidateUtils.notNull(m, "m must be a number");
        BigDecimal ret = number.divideAndRemainder(m)[1];
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal min(@NotNull String number, @NotNull String val) {
        ValidateUtils.isNumeric(number, "number must be a number");
        ValidateUtils.isNumeric(val, "val must be a number");
        BigDecimal ret = valueOf(number).min(valueOf(val));
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal min(@NotNull BigDecimal number, @NotNull BigDecimal val) {
        ValidateUtils.notNull(number, "number must be a number");
        ValidateUtils.notNull(val, "val must be a number");
        BigDecimal ret = number.min(val);
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal max(@NotNull String number, @NotNull String val) {
        ValidateUtils.isNumeric(number, "number must be a number");
        ValidateUtils.isNumeric(val, "val must be a number");
        BigDecimal ret = valueOf(number).max(valueOf(val));
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }

    @NotNull
    public static BigDecimal max(@NotNull BigDecimal number, @NotNull BigDecimal val) {
        ValidateUtils.notNull(number, "number must be a number");
        ValidateUtils.notNull(val, "val must be a number");
        BigDecimal ret = number.max(val);
        CACHE.putIfAbsent(ret.toString(), ret);
        return ret;
    }
}
