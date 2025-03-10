package signals.function;

public class Basis extends Function {

    private final Function U1 = new Cos(
            Constants.a1,
            Constants.b1,
            Constants.v1,
            Constants.phi0
    );

    private final Function U2 = new Cos(
            Constants.a2,
            Constants.b2,
            Constants.v2,
            Constants.phi0
    );

    // Метод для вычисления f(t)
    @Override
    public double getValue(double t) {
        return U1.getValue(t) + U1.getValue(t);
    }

    // Метод для численного интегрирования методом трапеций
    private double integrate(double start, double end, int steps) {
        double stepSize = (end - start) / steps;
        double integral = 0.0;

        for (int i = 0; i < steps; i++) {
            double t1 = start + i * stepSize;
            double t2 = start + (i + 1) * stepSize;
            double value1 = U1.getValue(t1) * U2.getValue(t1);
            double value2 = U1.getValue(t2) * U2.getValue(t2);
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
