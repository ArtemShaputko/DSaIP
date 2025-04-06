package signals.util.params;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
@AllArgsConstructor
public class DiscreteDrawParams implements SignalDrawParams {
    protected String name;
    protected double [] signal;
    private final double startTime;
    private final double samplingRate;
    private final Consumer<Object> functionToChange = null;

    @Override
    public String name() {
        return name;
    }

    @Override
    public double startTime() {
        return startTime;
    }

    public double samplingRate() {
        return samplingRate;
    }

    public double[] signal() {
        return signal;
    }
}
