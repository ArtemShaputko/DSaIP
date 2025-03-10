package signals.function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Cos extends Function{
    private final double a;
    private final double b;
    public final double v;
    public final double phi;
    @Override
    public double getValue(double t) {
        return a * Math.pow(Math.cos(v * t + phi), b);
    }
}
