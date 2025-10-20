package team.idealstate.sugar.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.sugar.validate.Validation;

/**
 * <h3>布尔工具类</h3>
 * <p>提供常用布尔工具方法封装。</p>
 */
public abstract class BooleanUtils {

    /**
     * <h4>判断布尔表达式是否为 {@code null} 或 {@code true}</h4>
     * @param expression 待判断的布尔表达式
     * @return {@code expression == null || expression}
     */
    public static boolean isNullOrTrue(@Nullable Boolean expression) {
        return expression == null || expression;
    }

    /**
     * <h4>判断布尔表达式是否为 {@code true}</h4>
     * @param expression 待判断的布尔表达式
     * @return 等价于 {@code (boolean) expression}
     */
    public static boolean isTrue(@NotNull Boolean expression) {
        Validation.isNotNull(expression, "expression must not be null.");
        return expression;
    }

    /**
     * <h4>判断布尔表达式是否为 {@code null} 或 {@code false}</h4>
     * @param expression 待判断的布尔表达式
     * @return {@code expression == null || !expression}
     */
    public static boolean isNullOrFalse(@Nullable Boolean expression) {
        return expression == null || !expression;
    }

    /**
     * <h4>判断布尔表达式是否为 {@code false}</h4>
     * @param expression 待判断的布尔表达式
     * @return 等价于 {@code (boolean) !expression}
     */
    public static boolean isFalse(@NotNull Boolean expression) {
        Validation.isNotNull(expression, "expression must not be null.");
        return !expression;
    }
}
