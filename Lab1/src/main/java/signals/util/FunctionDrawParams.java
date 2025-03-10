package signals.util;

import signals.function.Function;

public record FunctionDrawParams(
        String name,
        Function function,
        double startTime,
        double time,
        double step) implements SignalDrawParams {
}
