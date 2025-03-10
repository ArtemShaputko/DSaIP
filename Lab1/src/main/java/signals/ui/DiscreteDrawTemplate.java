package signals.ui;

public record DiscreteDrawTemplate(
        String legend,
        String xLabel,
        String yLabel,
        String name,
        double[] signal,
        double startTime,
        double samplingRate
) {
}
