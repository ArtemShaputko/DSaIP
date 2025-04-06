package signals;

import jwave.exceptions.JWaveException;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.daubechies.Daubechies4;
import signals.function.*;
import signals.math.MathOperations;
import signals.math.FourierTransform;
import signals.ui.*;
import signals.util.*;
import signals.util.params.DiscreteDrawParams;
import signals.util.params.FunctionDrawParams;
import signals.util.params.WaveletDrawParams;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.math3.util.MathArrays.convolve;

public class Main {
    public static void main(String[] args) throws IOException, JWaveException {
        lab3();
    }

    public static void lab3() throws JWaveException {
        Function basis = new Basis();
        var discreteBasis = basis.getDiscreteSignal(Constants.startValue, Constants.numPoints, Constants.samplingRate);
        var basisWithNoise = MathOperations.addNoise(discreteBasis, -1, 1);
/*        var basisWithNoise = new double[64];
        for(int i = 0; i < basisWithNoise.length; i++) {
            basisWithNoise[i] = i;
        }*/
        var dwtL1 = MathOperations.dwtDaubechies4(basisWithNoise);
        var dwtL2 = MathOperations.dwtDaubechies4(dwtL1[0]);
        var dwtL3 = MathOperations.dwtDaubechies4(dwtL2[0]);
        var idwtL1 = MathOperations.idwtDaubechies4(List.of(dwtL1[0], dwtL1[1]), 1);
        var idwtL2 = MathOperations.idwtDaubechies4(List.of(dwtL2[0], dwtL2[1], dwtL1[1]), 2);
        var idwtL3 = MathOperations.idwtDaubechies4(List.of(dwtL3[0], dwtL3[1], dwtL2[1], dwtL1[1]), 3);
        Daubechies4 wavelet = new Daubechies4();
        var paddedSignal = MathOperations.padToPowerOfTwoDouble(basisWithNoise);
        FastWaveletTransform fwt = new FastWaveletTransform(wavelet);
        double[] coeffs = fwt.forward(paddedSignal, 3);
        double[] idwtTest = fwt.reverse(coeffs, 3);

        var discreteSignals = new HashMap<>(Map.of(
                "Базис", new FunctionGUITemplate(
                        "Базис", "t", "",
                        new FunctionDrawParams("f(t)", basis, Constants.startValue, Constants.D, 0.001)
                ),
                "Базис с шумами", new FunctionGUITemplate(
                        "Базис с шумами", "t", "",
                        new DiscreteDrawParams("f'(t)",
                                basisWithNoise,
                                0,
                                1
                        )
                ),
                "ДВП (Уровень 1)", new FunctionGUITemplate(
                        "ДВП (Уровень 1)", "t", "",
                        List.of(
                                new DiscreteDrawParams("Аппроксимация f'(t)",
                                        dwtL1[0],
                                        0,
                                        1
                                ),
                                new DiscreteDrawParams("Детализация f'(t)",
                                        dwtL1[1],
                                        0,
                                        1
                                )
                        )
                ),
                "ДВП (Уровень 2)", new FunctionGUITemplate(
                        "ДВП (Уровень 2)", "t", "",
                        List.of(
                                new DiscreteDrawParams("Аппроксимация f'(t)",
                                        dwtL2[0],
                                        Constants.startValue,
                                        Constants.samplingRate
                                ),
                                new DiscreteDrawParams("Детализация f'(t)",
                                        dwtL2[1],
                                        Constants.startValue,
                                        Constants.samplingRate
                                )
                        )
                ),
                "ДВП (Уровень 3)", new FunctionGUITemplate(
                        "ДВП (Уровень 3)", "t", "",
                        List.of(
                                new DiscreteDrawParams("Аппроксимация f'(t)",
                                        dwtL3[0],
                                        Constants.startValue,
                                        Constants.samplingRate
                                ),
                                new DiscreteDrawParams("Детализация f'(t)",
                                        dwtL3[1],
                                        Constants.startValue,
                                        Constants.samplingRate
                                )
                        )
                ),
                "ДВП (Уровень 3) для проверки", new FunctionGUITemplate(
                        "ДВП (Уровень 3)", "t", "",
                        List.of(
                                new DiscreteDrawParams("f'(t)",
                                        coeffs,
                                        Constants.startValue,
                                        Constants.samplingRate
                                )
                        )
                ),
                "ОДВП сигнала с шумами L3", new FunctionGUITemplate(
                        "ОДВП сигнала с шумами L3", "t", "",
                        List.of(
                                new WaveletDrawParams("^f'(t)",
                                        List.of(dwtL3[0], dwtL3[1], dwtL2[1], dwtL1[1]),
                                        Constants.startValue,
                                        Constants.samplingRate
                                )
                        )
                ),
                "ОДВП сигнала для проверки", new FunctionGUITemplate(
                        "ОДВП сигнала для проверки", "t", "",
                        List.of(
                                new DiscreteDrawParams("^f'(t)",
                                        idwtTest,
                                        Constants.startValue,
                                        Constants.samplingRate
                                )
                        )
                )
        ));

        UI plotterUI = new LabUI(
                new SignalPlotter(),
                discreteSignals
        );
        plotterUI.showGUI();
    }

    public static void lab2() throws IOException {
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

        double[] convolutionResult = MathOperations.convolution(discreteX, discreteY);
        signalSaver.setFileName("convolution.wav");
        signalSaver.writeWavFile(convolutionResult);
        double[] correlationResult = MathOperations.correlation(discreteX, discreteY);
        double[] convolutionFFTResult = MathOperations.convolutionFFT(discreteX, discreteY);
        double[] correlationFFTResult = MathOperations.correlationFFT(discreteX, discreteY);

        double lineStartTime = 0;
        double lineSignalLength = 5;

        Function line1 = new Line(1, 2, 1);
        Function line2 = new Line(3, 4, -2);
        double[] discreteLine1 = line1.getDiscreteSignal(lineStartTime, (int) (lineSignalLength * Constants.samplingRate), Constants.samplingRate);
        double[] discreteLine2 = line2.getDiscreteSignal(lineStartTime, (int) (lineSignalLength * Constants.samplingRate), Constants.samplingRate);

        var discreteSignals = new HashMap<>(Map.of(
                "График свёртки", new FunctionGUITemplate(
                        "График свёртки", "t", "",
                        new DiscreteDrawParams("z1(t)",
                                convolutionResult,
                                Constants.startValue,
                                Constants.samplingRate
                        )
                ),
                "График корреляции", new FunctionGUITemplate(
                        "График корреляции", "t", "",
                        new DiscreteDrawParams("^z1(t)",
                                correlationResult,
                                Constants.startValue,
                                Constants.samplingRate
                        )
                ),
                "График свёртки для проверки", new FunctionGUITemplate(
                        "График свёртки для проверки", "t", "",
                        new DiscreteDrawParams("z1(t)",
                                convolve(discreteX, discreteY),
                                Constants.startValue,
                                Constants.samplingRate
                        )
                ),
                "График свёртки БПФ", new FunctionGUITemplate(
                        "График свёртки БПФ", "t", "",
                        new DiscreteDrawParams("z2(t)",
                                convolutionFFTResult,
                                Constants.startValue,
                                Constants.samplingRate
                        )
                ),
                "График корреляции БПФ", new FunctionGUITemplate(
                        "График корреляции БПФ", "t", "",
                        new DiscreteDrawParams("^z2(t)",
                                correlationFFTResult,
                                Constants.startValue,
                                Constants.samplingRate
                        )
                ),
                "График свёртки БПФ line1", new FunctionGUITemplate(
                        "График свёртки БПФ line1", "t", "",
                        new DiscreteDrawParams("l1(t)",
                                MathOperations.convolutionFFT(discreteLine1, discreteLine1),
                                0,
                                Constants.samplingRate
                        )
                ),
                "График корреляции БПФ line1", new FunctionGUITemplate(
                        "График корреляции БПФ line1", "t", "",
                        new DiscreteDrawParams("^l1(t)",
                                MathOperations.correlationFFT(discreteLine1, discreteLine1),
                                0,
                                Constants.samplingRate
                        )
                ),
                "График свёртки БПФ line2", new FunctionGUITemplate(
                        "График свёртки БПФ line2", "t", "",
                        new DiscreteDrawParams("l2(t)",
                                MathOperations.convolutionFFT(discreteLine2, discreteLine2),
                                0,
                                Constants.samplingRate
                        )
                ),
                "График корреляции БПФ line2", new FunctionGUITemplate(
                        "График корреляции БПФ line2", "t", "",
                        new DiscreteDrawParams("^l2(t)",
                                MathOperations.correlationFFT(discreteLine2, discreteLine2),
                                0,
                                Constants.samplingRate
                        )
                )));

        discreteSignals.putAll(
                Map.of(
                        "График свёртки line1", new FunctionGUITemplate(
                                "График свёртки line1", "t", "",
                                new DiscreteDrawParams("l1(t)",
                                        MathOperations.convolution(discreteLine1, discreteLine1),
                                        0,
                                        Constants.samplingRate
                                )
                        ),
                        "График корреляции line1", new FunctionGUITemplate(
                                "График корреляции line1", "t", "",
                                new DiscreteDrawParams("^l1(t)",
                                        MathOperations.correlation(discreteLine1, discreteLine1),
                                        0,
                                        Constants.samplingRate
                                )
                        ),
                        "График свёртки line2", new FunctionGUITemplate(
                                "График свёртки line2", "t", "",
                                new DiscreteDrawParams("l2(t)",
                                        MathOperations.convolution(discreteLine2, discreteLine2),
                                        0,
                                        Constants.samplingRate
                                )
                        ),
                        "График корреляции line2", new FunctionGUITemplate(
                                "График корреляции line2", "t", "",
                                new DiscreteDrawParams("^l2(t)",
                                        MathOperations.correlation(discreteLine2, discreteLine2),
                                        0,
                                        Constants.samplingRate
                                )
                        ),
                        "Графики x(t) и y(t)", new FunctionGUITemplate(
                                "Графики x(t) и y(t)", "t", "",
                                List.of(
                                        new FunctionDrawParams("x(t)", x, 0, Constants.D, 0.001),
                                        new FunctionDrawParams("y(t)", y, 0, Constants.D, 0.001)

                                )
                        )
                )
        );

        UI plotterUI = new LabUI(
                new SignalPlotter(),
                discreteSignals
        );
        plotterUI.showGUI();

    }

    public void lab1() throws IOException {
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

        Complex[] filledSignal = MathOperations.padToPowerOfTwoComplex(discreteSignal);

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