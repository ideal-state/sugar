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

import team.idealstate.sugar.sort.annotation.Order;

/**
 *
 *
 * <h3>序号排序接口</h3>
 *
 * <p>实现此接口以支持序号排序。
 *
 * @see Order
 * @see OrderComparator
 */
public interface Orderable {

    /**
     *
     *
     * <h4>获取序号</h4>
     *
     * @return 序号
     * @see Order#DEFAULT
     */
    default int order() {
        return Order.DEFAULT;
    }
}
