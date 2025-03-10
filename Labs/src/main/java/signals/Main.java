package signals;

import signals.function.*;
import signals.math.ExtraOperations;
import signals.math.FourierTransform;
import signals.ui.*;
import signals.util.SignalPlotter;
import signals.util.SignalSaver;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.math3.util.MathArrays.convolve;

public class Main {
    public static void main(String[] args) throws IOException {
        SignalSaver signalSaver = new SignalSaver("file", Constants.samplingRate);

        Function x = new Sin(
                Constants.a1,
                Constants.b1,
                Constants.v1,
                Constants.phi0);

        Function y = new Cos(
                Constants.a2,
                Constants.b2,
                Constants.v2,
                Constants.phi0);

        var discreteY = y.getDiscreteSignal(Constants.startValue, Constants.numPoints, Constants.samplingRate);
        var discreteX = x.getDiscreteSignal(Constants.startValue, Constants.numPoints, Constants.samplingRate);

        double[] convolutionResult = ExtraOperations.convolution(discreteX, discreteY);
        signalSaver.setFileName("convolution.wav");
        signalSaver.writeWavFile(convolutionResult);
        double[] correlationResult = ExtraOperations.correlation(discreteX, discreteY);
        double[] convolutionFFTResult = ExtraOperations.convolutionFFT(discreteX, discreteY);
        double[] correlationFFTResult = ExtraOperations.correlationFFT(discreteX, discreteY);

        double lineStartTime = 0;
        double lineSignalLength = 5;

        Function line1 = new Line(1, 2, 1);
        Function line2 = new Line(3, 4,-2);
        double[] discreteLine1 = line1.getDiscreteSignal(lineStartTime, (int)(lineSignalLength*Constants.samplingRate), Constants.samplingRate);
        double[] discreteLine2 = line2.getDiscreteSignal(lineStartTime, (int)(lineSignalLength*Constants.samplingRate), Constants.samplingRate);

        var discreteSignals = new HashMap<>(Map.of(
                "График свёртки", new DiscreteDrawTemplate(
                        "График свёртки", "t", "", "z1(t)",
                        convolutionResult,
                        Constants.startValue,
                        Constants.samplingRate
                ),
                "График корреляции", new DiscreteDrawTemplate(
                        "График корреляции", "t", "", "^z1(t)",
                        correlationResult,
                        Constants.startValue,
                        Constants.samplingRate
                ),
                "График свёртки для проверки", new DiscreteDrawTemplate(
                        "График свёртки для проверки", "t", "", "z1(t)",
                        convolve(discreteX, discreteY),
                        Constants.startValue,
                        Constants.samplingRate),
                "График свёртки БПФ", new DiscreteDrawTemplate(
                        "График свёртки БПФ", "t", "", "z2(t)",
                        convolutionFFTResult,
                        Constants.startValue,
                        Constants.samplingRate
                ),
                "График корреляции БПФ", new DiscreteDrawTemplate(
                        "График корреляции БПФ", "t", "", "^z2(t)",
                        correlationFFTResult,
                        Constants.startValue,
                        Constants.samplingRate
                ),
                "График свёртки БПФ line1", new DiscreteDrawTemplate(
                        "График свёртки БПФ line1", "t", "", "l1(t)",
                        ExtraOperations.convolutionFFT(discreteLine1, discreteLine1),
                        0,
                        Constants.samplingRate
                ),
                "График корреляции БПФ line1", new DiscreteDrawTemplate(
                        "График корреляции БПФ line1", "t", "", "^l1(t)",
                        ExtraOperations.correlationFFT(discreteLine1, discreteLine1),
                        0,
                        Constants.samplingRate
                ),
                "График свёртки БПФ line2", new DiscreteDrawTemplate(
                        "График свёртки БПФ line2", "t", "", "l2(t)",
                        ExtraOperations.convolutionFFT(discreteLine2, discreteLine2),
                        0,
                        Constants.samplingRate
                ),
                "График корреляции БПФ line2", new DiscreteDrawTemplate(
                        "График корреляции БПФ line2", "t", "", "^l2(t)",
                        ExtraOperations.correlationFFT(discreteLine2, discreteLine2),
                       0,
                        Constants.samplingRate
                )));

        discreteSignals.putAll(
                Map.of(
                        "График свёртки line1", new DiscreteDrawTemplate(
                                "График свёртки line1", "t", "", "l1(t)",
                                ExtraOperations.convolution(discreteLine1, discreteLine1),
                                0,
                                Constants.samplingRate
                        ),
                        "График корреляции line1", new DiscreteDrawTemplate(
                                "График корреляции line1", "t", "", "^l1(t)",
                                ExtraOperations.correlation(discreteLine1, discreteLine1),
                                0,
                                Constants.samplingRate
                        ),
                        "График свёртки line2", new DiscreteDrawTemplate(
                                "График свёртки line2", "t", "", "l2(t)",
                                ExtraOperations.convolution(discreteLine2, discreteLine2),
                                0,
                                Constants.samplingRate
                        ),
                        "График корреляции line2", new DiscreteDrawTemplate(
                                "График корреляции line2", "t", "", "^l2(t)",
                                ExtraOperations.correlation(discreteLine2, discreteLine2),
                                0,
                                Constants.samplingRate
                        )
                )
        );

        UI plotterUI = new Lab2UI(
                new SignalPlotter(),
                discreteSignals,
                x,
                y
        );
        plotterUI.showGUI();

    }

    public void fourierTransformCheck() throws IOException {
        Basis basis = new Basis();
        double[] discreteSignal = basis.getDiscreteSignal(Constants.startValue, Constants.numPoints, Constants.samplingRate);
        if (basis.isOrthogonal()) {
            System.out.println("Базис является ортогональным");
        } else {
            System.out.println("Базис не является ортодоксальным");
        }
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

        startTime = System.nanoTime();
        Complex[] dftSignal = FourierTransform.idft(dftSpectrum);
        endTime = System.nanoTime();
        System.out.println("Время выполнения ОДПФ: " + (endTime - startTime) / 1e6 + " мс");

        startTime = System.nanoTime();
        Complex[] fftSignal = FourierTransform.ifft(fftSpectrum);
        endTime = System.nanoTime();
        System.out.println("Время выполнения ОБПФ: " + (endTime - startTime) / 1e6 + " мс");

        double[] idftSignal = Arrays.stream(dftSignal).mapToDouble(x -> x.real).toArray();
        signalSaver.setFileName("idft.wav");
        signalSaver.writeWavFile(idftSignal);

        double[] ifftSignal = Arrays.copyOf(Arrays.stream(fftSignal).mapToDouble(x -> x.real).toArray(),
                discreteSignal.length);
        signalSaver.setFileName("ifft.wav");
        signalSaver.writeWavFile(ifftSignal);

        PlotterUI plotterUI = new Lab1UI(
                new SignalPlotter(),
                basis,
                dftSpectrum,
                fftSpectrum,
                idftSignal,
                ifftSignal,
                FourierTransform
                        .highPassFilter(dftSpectrum, 0.8, Constants.samplingRate),
                Arrays.stream(
                                FourierTransform.idft(FourierTransform.reduceAmplitude(dftSpectrum, 5)))
                        .mapToDouble(x -> x.real).toArray()

        );
        plotterUI.showGUI();
    }
}