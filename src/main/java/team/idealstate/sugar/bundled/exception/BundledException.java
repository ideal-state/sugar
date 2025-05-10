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

package team.idealstate.sugar.bundled.exception;

import team.idealstate.sugar.exception.SugarException;

public class BundledException extends SugarException {

    private static final long serialVersionUID = 5167499312311231361L;

    public BundledException() {}

    public BundledException(String message) {
        super(message);
    }

    public BundledException(String message, Throwable cause) {
        super(message, cause);
    }

    public BundledException(Throwable cause) {
        super(cause);
    }

    protected BundledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
