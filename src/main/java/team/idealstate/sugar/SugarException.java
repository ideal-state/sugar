package team.idealstate.sugar;

import org.jetbrains.annotations.NotNull;

public class SugarException extends RuntimeException {
    private static final long serialVersionUID = 4894244095571633722L;

    public SugarException() {
        super();
    }

    public SugarException(@NotNull String message) {
        super(message);
    }

    public SugarException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    public SugarException(@NotNull Throwable cause) {
        super(cause);
    }

    protected SugarException(@NotNull String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
