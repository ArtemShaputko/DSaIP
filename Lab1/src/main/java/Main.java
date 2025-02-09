import function.Basis;
import function.Complex;
import function.Constants;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        Basis basis = new Basis();
        basis.discretize();
        double[] discreteSignal = basis.getDiscreteSignal();
        if (basis.isOrthogonal()) {
            System.out.println("Базис является ортогональным");
        } else {
            System.out.println("Базис не является ортодоксальным");
        }
        SignalPlotter signalPlotter = new SignalPlotter("График функции", "F(t)", "t");
        signalPlotter.drawSignal(basis, Constants.D, 0.001);
        SignalSaver signalSaver = new SignalSaver("basis.wav", Constants.samplingRate);
        signalSaver.writeWavFile(discreteSignal);

        Complex[] complexSignal = Arrays.stream(discreteSignal).mapToObj(
                x -> new Complex(x, 0)).toArray(Complex[]::new);

        long startTime = System.nanoTime();
        Complex[] dftSpectrum = FourierTransform.dft(complexSignal);
        long endTime = System.nanoTime();
        System.out.println("Время выполнения ДПФ: " + (endTime - startTime) / 1e6 + " мс");

        Complex[] filledSignal = FourierTransform.padToPowerOfTwo(discreteSignal);

        startTime = System.nanoTime();
        Complex[] fftSpectrum = FourierTransform.fft(filledSignal);
        endTime = System.nanoTime();
        System.out.println("Время выполнения БПФ: " + (endTime - startTime) / 1e6 + " мс");

        signalPlotter.setLegend("АЧХ ДПФ");
        signalPlotter.setXlabel("Частота,Гц");
        signalPlotter.setYLabel("Амплитуда");
        signalPlotter.drawSignal(FourierTransform.calculateAmplitudeSpectrum(dftSpectrum),
                FourierTransform.calculateFrequencies(dftSpectrum, Constants.samplingRate));
        signalPlotter.setLegend("АЧХ БПФ");
        signalPlotter.drawSignal(FourierTransform.calculateAmplitudeSpectrum(fftSpectrum),
                FourierTransform.calculateFrequencies(fftSpectrum, Constants.samplingRate));

        startTime = System.nanoTime();
        Complex[] dftSignal = FourierTransform.idft(dftSpectrum);
        endTime = System.nanoTime();
        System.out.println("Время выполнения ОДПФ: " + (endTime - startTime) / 1e6 + " мс");

        startTime = System.nanoTime();
        Complex[] fftSignal = FourierTransform.ifft(fftSpectrum);
        endTime = System.nanoTime();
        System.out.println("Время выполнения ОБПФ: " + (endTime - startTime) / 1e6 + " мс");

        signalPlotter.setLegend("График функции ОДПФ");
        signalPlotter.setXlabel("F(t)");
        signalPlotter.setYLabel("t");
        signalPlotter.drawSignal(Arrays.stream(dftSignal).mapToDouble(x -> x.real).toArray(), Constants.samplingRate);
        signalPlotter.setLegend("График функции ОБПФ");
        signalPlotter.drawSignal(Arrays.copyOf(Arrays.stream(fftSignal).mapToDouble(x -> x.real).toArray(),
                discreteSignal.length), Constants.samplingRate);

        signalPlotter.setLegend("График функции с уменьшенной амплитудой");
        signalPlotter.drawSignal(Arrays.stream(
                        FourierTransform.idft(FourierTransform.reduceAmplitude(dftSpectrum, 5)))
                .mapToDouble(x -> x.real).toArray(), Constants.samplingRate);

        Complex[] filterResult = FourierTransform
                .highPassFilter(dftSpectrum, 0.8, Constants.samplingRate);

        signalPlotter.setLegend("АЧХ после обнуления");
        signalPlotter.setXlabel("Частота,Гц");
        signalPlotter.setYLabel("Амплитуда");
        signalPlotter.drawSignal(FourierTransform
                        .calculateAmplitudeSpectrum(filterResult),
                FourierTransform.calculateFrequencies(filterResult, Constants.samplingRate));

        signalPlotter.setLegend("График функции после обнуления");
        signalPlotter.setXlabel("F(t)");
        signalPlotter.setYLabel("t");
        signalPlotter.drawSignal(Arrays.stream(FourierTransform.idft(filterResult)).mapToDouble(x -> x.real).toArray(), Constants.samplingRate);

    }
}