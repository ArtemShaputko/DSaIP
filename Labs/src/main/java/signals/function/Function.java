package signals.function;

import lombok.Getter;

@Getter
public abstract class Function {
    protected double[] discreteSignal;
    public abstract double getValue(double x);
    public double[] getDiscreteSignal(double startValue, int numPoints, double samplingRate) {
        if (discreteSignal == null) {
            discretize(startValue, numPoints, samplingRate);
        }
        return discreteSignal;
    }

    private void discretize(double startValue, int numPoints, double samplingRate) {
        discreteSignal = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            double t = startValue + i / samplingRate; // Время в секундах
            discreteSignal[i] = getValue(t);
        }
    }
}
