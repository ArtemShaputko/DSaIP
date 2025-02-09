package function;

public class Basis implements Function {
    private double[] discreteSignal;
    // Метод для вычисления U1(t)
    public double getU1(double t) {
        return Constants.a1 * Math.pow(Math.sin(Constants.v1 * t + Constants.phi0), Constants.b1);
    }

    // Метод для вычисления U2(t)
    public double getU2(double t) {
        return Constants.a2 * Math.pow(Math.cos(Constants.v2 * t + Constants.phi0), Constants.b2);
    }
    // Метод для вычисления f(t)
    public double getValue(double t) {
        return getU1(t) + getU2(t);
    }
    
    public void discretize() {
        discreteSignal = new double[Constants.numPoints];
        for (int i = 0; i < Constants.numPoints; i++) {
            double t = i / Constants.samplingRate; // Время в секундах
            discreteSignal[i] = getValue(t);
        }
    }

    public double[] getDiscreteSignal() {
        return discreteSignal;
    }

    // Метод для численного интегрирования методом трапеций
    private double integrate(double start, double end, int steps) {
        double stepSize = (end - start) / steps;
        double integral = 0.0;

        for (int i = 0; i < steps; i++) {
            double t1 = start + i * stepSize;
            double t2 = start + (i + 1) * stepSize;
            double value1 = getU1(t1) * getU2(t1);
            double value2 = getU1(t2) * getU2(t2);
            integral += (value1 + value2) * stepSize / 2.0;
        }

        return integral;
    }

    public boolean isOrthogonal() {
        // Интервал интегрирования
        double start = Constants.phi0;
        double end = Constants.phi0 + 2 * Math.PI;

        // Количество шагов для численного интегрирования
        int steps = 100000;

        // Вычисление интеграла
        double scalarProduct = integrate(start, end, steps);

        System.out.println("Скалярное произведение: " + scalarProduct);

        // Проверка ортогональности
        return Math.abs(scalarProduct) < 1e-6;
    }
}
