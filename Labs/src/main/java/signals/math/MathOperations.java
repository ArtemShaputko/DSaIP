package signals.math;

import signals.function.Complex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.apache.commons.math3.util.MathArrays.convolve;

public class MathOperations {
    protected static final double sqrt_3 = Math.sqrt( 3 );
    protected static final double denom = 4 * Math.sqrt( 2 );
    protected static final double[] h = {(1 + sqrt_3)/denom, (3 + sqrt_3)/denom, (3 - sqrt_3)/denom, (1 - sqrt_3)/denom};
    protected static final double[] g = {h[3], -h[2], h[1], -h[0]};

    // Метод для дополнения массива нулями до ближайшей степени двойки
    public static Complex[] padToPowerOfTwoComplex(final double[] signal) {
        int N = signal.length;
        if ((N & (N - 1)) == 0) {
            return Arrays.stream(signal).mapToObj(
                    x -> new Complex(x, 0)).toArray(Complex[]::new);
        }
        int nextPowerOfTwo = Integer.highestOneBit(N) * 2; // Ближайшая степень двойки
        return Arrays.stream(Arrays.copyOf(signal, nextPowerOfTwo)).mapToObj(
                x -> new Complex(x, 0)).toArray(Complex[]::new);
    }

    // Метод для дополнения массива нулями до ближайшей степени двойки
    public static double[] padToPowerOfTwoDouble(final double[] signal) {
        int N = signal.length;
        if ((N & (N - 1)) == 0) {
            return signal;
        }
        int nextPowerOfTwo = Integer.highestOneBit(N) << 1; // Ближайшая степень двойки
        return Arrays.copyOf(signal, nextPowerOfTwo);
    }

    public static double[] symmetricPadToPowerOfTwoDouble(final double[] signal) {
        int originalLength = signal.length;
        if ((originalLength & (originalLength - 1)) == 0) {
            return Arrays.copyOf(signal, originalLength);
        }
        int targetLength = 1;
        while (targetLength < originalLength) {
            targetLength <<= 1;
        }
        double[] padded = Arrays.copyOf(signal, targetLength);
        for (int i = originalLength; i < targetLength; i++) {
            int mirroredIndex = 2 * originalLength - i - 1;
            if (mirroredIndex < 0) {
                mirroredIndex = -mirroredIndex - 1;
            }
            padded[i] = signal[mirroredIndex % originalLength];
        }
        return padded;
    }

    public static double[] myCorrelation(double[] signal1, double[] signal2) {
        int length1 = signal1.length;
        int length2 = signal2.length;
        int resultLength = length1 + length2 - 1;
        double[] correlationResult = new double[resultLength];
        for (int t = 0; t < resultLength; t++) {
            int offset = Math.max(0, t - length2 + 1);
            for (int i = offset, j = length2 + offset - t - 1; i < t + 1 && i < length1 && j < length2; i++, j++) {
                correlationResult[t] += signal1[i] * signal2[j];
            }
        }
        return correlationResult;
    }

    public static double[] convolution(double[] a, double[] b) {
        double[] convolved = new double[a.length + b.length - 1];

        for (int i = 0; i < convolved.length; i++) {
            convolved[i] = 0;
            int k = Math.max(i - b.length + 1, 0);

            while (k < i + 1 && k < a.length) {
                convolved[i] += a[k] * b[i - k];
                k++;
            }
        }

        return convolved;
    }

    public static double[] correlation(double[] x, double[] y) {
        double[] result = new double[x.length + y.length - 1];
        int n = result.length;
        int yStart = y.length;
        for (int i = 0; i < n; i++) {
            result[i] = 0;

            int kMin = Math.max(i - (y.length - 1), 0);
            int kMax = Math.min(i, x.length - 1);

            if (i < y.length) {
                yStart--;
            }

            int count = 0;
            for (int k = kMin; k <= kMax; k++) {
                result[i] += x[k] * y[yStart + count];
                count++;
            }
        }
        return result;
    }

    public static double[] myConvolution(double[] signal1, double[] signal2) {
        int length1 = signal1.length;
        int length2 = signal2.length;
        int resultLength = length1 + length2 - 1;
        double[] result = new double[resultLength];
        for (int t = 0; t < resultLength; t++) {
            for (int i = Math.max(0, t - length2 + 1), j = Math.min(t, length2 - 1); i < t + 1 && i < length2 && j >= 0; i++, j--) {
                result[t] += signal1[i] * signal2[j];
                System.out.print(i + "*" + j + " ");
            }
            System.out.println();
        }
        return result;
    }

    public static double[] convolutionFFT(double[] firstSignal, double[] secondSignal) {
        int initialLength = firstSignal.length + secondSignal.length - 1;

        Complex[] firstPadded = padToPowerOfTwoComplex(Arrays.copyOf(firstSignal, initialLength));
        Complex[] secondPadded = padToPowerOfTwoComplex(Arrays.copyOf(secondSignal, initialLength));

        int paddedLength = firstPadded.length;

        Complex[] fftX = FourierTransform.fft(firstPadded);
        Complex[] fftH = FourierTransform.fft(secondPadded);

        Complex[] product = new Complex[paddedLength];
        for (int i = 0; i < paddedLength; i++) {
            product[i] = fftX[i].multiply(fftH[i]);
        }

        Complex[] ifftResult = FourierTransform.ifft(product);

        return Arrays.stream(Arrays.copyOf(ifftResult, initialLength)).mapToDouble(num -> num.real).toArray();
    }

    public static double[] correlationFFT(double[] firstSignal, double[] secondSignal) {
        return convolutionFFT(firstSignal, flip(secondSignal));
    }

    public static double[] addNoise(double[] signal, double minNoise, double maxNoise) {
        Random random = new Random();
        double diff = Math.abs(maxNoise - minNoise);
        return Arrays.stream(signal).map(s -> s + random.nextDouble() * (diff) + minNoise).toArray();
    }

    public static double[] idwtDaubechies4(List<double[]> dwtCoefficients, int level) {
        double[] currentA = dwtCoefficients.getFirst();
        for (int i = 1; i <= level; i++) {
            double[] B = dwtCoefficients.get(i);
            currentA = idwtDaubechies4(currentA, B);
        }
        return currentA;
    }

    public static double[] idwtDaubechies4(List<double[]> dwt, int level, int originalLength) {
        return Arrays.copyOf(idwtDaubechies4(dwt, level), originalLength);
    }

    public static double[] idwtDaubechies4(double[] A, double[] B) {
        // Синтезирующие фильтры (обратные к анализаторам)

        // Up-sampling коэффициентов
        double[] upsampledA = upsample(A);
        double[] upsampledB = upsample(B);

        // Применение свертки с синтезирующими фильтрами
        double[] convH = convolution(upsampledA, h);
        double[] convG = convolution(upsampledB, g);

        int length = upsampledA.length; // Должно совпадать с upsampledB.length

        double[] partH = Arrays.copyOf(convH,length);
        double[] partG = Arrays.copyOf(convG, length);

        double[] reconstructed = new double[length];
        reconstructed[0] = convH[0] + convG[0] + convH[length] + convG[length];
        reconstructed[1] = convH[1] + convG[1] + convH[length+1] + convG[length+1];
        for (int i = 2; i < length; i++) {
            reconstructed[i] = partH[i] + partG[i];
        }

        return reconstructed; // Удаление дополнения
    }

    // Вставка нулей между элементами (up-sampling)
    private static double[] upsample(double[] coeffs) {
        double[] upsampled = new double[coeffs.length * 2];
        for (int i = 0; i < coeffs.length; i++) {
            upsampled[2 * i] = coeffs[i];
            upsampled[2 * i + 1] = 0;
        }
        return upsampled;
    }

    public static List<double[]> dwtDaubechies4(double[] signal, int level) {
        var result = new ArrayList<double[]>();
        var dwt = dwtDaubechies4(signal);
        for (int i = 0; i < level - 1; i++) {
            result.add(level - i - 1, dwt[1]);
            dwt = dwtDaubechies4(dwt[0]);
        }
        result.addFirst(dwt[0]);
        result.add(dwt[1]);
        return result;
    }

    public static double[][] dwtDaubechies4(double[] signal) {
        double[] A = applyWavelet(signal, h);
        double[] B = applyWavelet(signal, g);

        return new double[][]{A, B};
    }

    private static double[] applyWavelet(double[] signal, double[] wavelet) {
        double[] result = new double[signal.length / 2];
        for (int i = 0; i < signal.length / 2; i++) {
            result[i] = 0;
            for (int k = 0; k < wavelet.length; k++) {
                int index = 2 * i + k;
                if (index >= signal.length) {
                    index -= signal.length;
                }
                result[i] += wavelet[k] * signal[index];
            }
        }
        return result;
    }

    private static int reflectIndex(int index, int length) {
        if (index < 0) return -index - 1;
        if (index >= length) return 2 * length - index - 1;
        return index;
    }

    public static double[] flip(double[] array) {
        int length = array.length;
        double[] flippedArray = new double[length];
        for (int i = 0; i < length; i++) {
            flippedArray[i] = array[length - i - 1];
        }
        return flippedArray;
    }
}
