package signals.util;

public record DiscreteDrawParams(
        String name,
        double[] signal,
        double startTime,
        double samplingRate
) implements SignalDrawParams {
}
