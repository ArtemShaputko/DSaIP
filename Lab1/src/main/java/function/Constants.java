package function;

public class Constants {
    public static final double a1 = 0.5;
    public static final double a2 = 2.0;
    public static final double b1 = 3;
    public static final double b2 = 5;
    public static final double v1 = 2;
    public static final double v2 = 4;
    public static final double phi0 = 1.5;
    public static final double D = 30; // Длительность сигнала
    public static final int d = 256 ; // Точек на каждые 2π секунд
    public static final double samplingRate = d / (2 * Math.PI); // Частота дискретизации
    public static int numPoints = (int) (Constants.D * Constants.samplingRate);
}
