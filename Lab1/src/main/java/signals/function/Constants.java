package signals.function;

public class Constants {
    public static final double a1 = 0.5;
    public static final double a2 = 2.0;
    public static final double b1 = 3;
    public static final double b2 = 5;
    public static final double v1 = 2;
    public static final double v2 = 4;
    public static final double phi0 = 1.5;
    public static final double D = 30; // Длительность сигнала
    public static final int d = 2000 ; // Точек на каждые 2π секунд
    public static final double samplingRate = d / (2 * Math.PI); // Частота дискретизации
    public static final double samplingRate2 = 2000 / (2 * Math.PI);
    public static final double startValue = -Math.PI;
    public static int numPoints = (int) (3*Math.PI * Constants.samplingRate);
    public static int numPoints2 = (int) (Math.PI * Constants.samplingRate2);
}
