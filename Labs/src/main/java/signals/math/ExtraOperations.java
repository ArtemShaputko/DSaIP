package signals.math;

import signals.function.Complex;

import java.util.Arrays;

public class ExtraOperations {

    public static double[] myCorrelation(double[] signal1, double[] signal2) {
        int length1 = signal1.length;
        int length2 = signal2.length;
        int resultLength = length1 + length2 - 1;
        double[] correlationResult = new double[resultLength];
        for (int t = 0; t < resultLength; t++) {
            int offset = Math.max(0,t-length2 +1 );
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
        for(int t = 0; t < resultLength; t++) {
            for(int i = Math.max(0, t-length2 + 1), j = Math.min(t, length2 - 1); i < t+1 && i < length2 && j >= 0; i++, j--) {
                result[t] += signal1[i] * signal2[j];
                System.out.print(i+"*"+j+" ");
            }
            System.out.println();
        }
        return result;
    }

    public static double[] convolutionFFT(double[] firstSignal, double[] secondSignal) {
        int initialLength = firstSignal.length + secondSignal.length - 1;

        Complex[] firstPadded = FourierTransform.padToPowerOfTwo(Arrays.copyOf(firstSignal, initialLength));
        Complex[] secondPadded = FourierTransform.padToPowerOfTwo(Arrays.copyOf(secondSignal, initialLength));

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

    public static double[] flip(double[] array) {
        int length = array.length;
        double[] flippedArray = new double[length];
        for (int i = 0; i < length; i++) {
            flippedArray[i] = array[length - i - 1];
        }
        return flippedArray;
    }
}
