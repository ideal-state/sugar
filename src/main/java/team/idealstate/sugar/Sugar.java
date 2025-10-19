package team.idealstate.sugar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.instrument.Instrumentation;

public interface Sugar {

    static void main(@NotNull String @NotNull ... arguments) {

    }

    static void premain(@Nullable String arguments, @NotNull Instrumentation instrumentation) {

    }

    static void agentmain(@Nullable String arguments, @NotNull Instrumentation instrumentation) {

    }
}
