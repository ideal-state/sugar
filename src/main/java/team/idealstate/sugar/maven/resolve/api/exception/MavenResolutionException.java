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

package team.idealstate.sugar.maven.resolve.api.exception;

import team.idealstate.sugar.maven.exception.MavenException;

public class MavenResolutionException extends MavenException {
    private static final long serialVersionUID = 7237452261550777793L;

    public MavenResolutionException() {}

    public MavenResolutionException(String message) {
        super(message);
    }

    public MavenResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MavenResolutionException(Throwable cause) {
        super(cause);
    }

    protected MavenResolutionException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
