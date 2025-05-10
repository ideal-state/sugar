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

package team.idealstate.sugar.banner.exception;

import team.idealstate.sugar.exception.SugarException;

public class BannerException extends SugarException {

    private static final long serialVersionUID = -4582540870147842039L;

    public BannerException() {}

    public BannerException(String message) {
        super(message);
    }

    public BannerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BannerException(Throwable cause) {
        super(cause);
    }

    protected BannerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
