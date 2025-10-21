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

package team.idealstate.sugar.sort;

import java.io.Serializable;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;
import team.idealstate.sugar.sort.annotation.Order;

/**
 *
 *
 * <h3>顺序序号对象比较器</h3>
 *
 * @param <T> 待比较对象的类型
 * @see Order
 * @see Orderable
 */
public class OrderComparator<T> implements Comparator<T>, Serializable {

    private static final long serialVersionUID = -3214225699550765098L;

    @SuppressWarnings("rawtypes")
    private static final Comparator FORWARD = new OrderComparator(Order.LAST);

    @SuppressWarnings("rawtypes")
    private static final Comparator REVERSE = new OrderComparator(Order.FIRST).reversed();

    /**
     *
     *
     * <h4>获取正序比较器（小到大）</h4>
     *
     * @param <T> 待比较对象的类型
     * @return 正序比较器
     */
    @SuppressWarnings({"unchecked"})
    @NotNull
    public static <T> Comparator<T> forward() {
        return (Comparator<T>) FORWARD;
    }

    /**
     *
     *
     * <h4>获取倒序比较器（大到小）</h4>
     *
     * @param <T> 待比较对象的类型
     * @return 倒序比较器
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Comparator<T> reverse() {
        return (Comparator<T>) REVERSE;
    }

    protected final int defaultOrder;

    /**
     *
     *
     * <h4>构造函数</h4>
     *
     * @param defaultOrder 默认顺序序号
     * @see #orderOf(Object)
     */
    public OrderComparator(int defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    @Override
    public int compare(T first, T second) {
        return orderOf(first) - orderOf(second);
    }

    protected final int orderOf(T orderable) {
        if (orderable instanceof Order) {
            return ((Order) orderable).value();
        }
        if (orderable instanceof Orderable) {
            return ((Orderable) orderable).order();
        }
        if (orderable == null) {
            return defaultOrder;
        }
        Order order = orderable.getClass().getDeclaredAnnotation(Order.class);
        return order == null ? Order.DEFAULT : order.value();
    }
}
