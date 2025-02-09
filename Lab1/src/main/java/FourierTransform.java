import function.Complex;

import java.util.Arrays;

public class FourierTransform {

    // Метод для дополнения массива нулями до ближайшей степени двойки
    public static Complex[] padToPowerOfTwo(final double[] signal) {
        int N = signal.length;
        if ((N & (N - 1)) == 0) {
            return Arrays.stream(signal).mapToObj(
                    x -> new Complex(x, 0)).toArray(Complex[]::new);
        }
        int nextPowerOfTwo = Integer.highestOneBit(N) * 2; // Ближайшая степень двойки
        return Arrays.stream(Arrays.copyOf(signal, nextPowerOfTwo)).mapToObj(
                x -> new Complex(x, 0)).toArray(Complex[]::new);
    }

    // Дискретное преобразование Фурье (ДПФ)
    public static Complex[] dft(final Complex[] signal) {
        int N = signal.length;
        Complex[] spectrum = new Complex[N];
        for (int k = 0; k < N; k++) {
            spectrum[k] = new Complex(0, 0);
            for (int n = 0; n < N; n++) {
                double angle = -2 * Math.PI * k * n / N;
                Complex exponential = Complex.fromPlane(1, angle);
                spectrum[k] = spectrum[k].add(signal[n].multiply(exponential));
            }
        }
        return spectrum;
    }

    // Быстрое преобразование Фурье (БПФ)
    public static Complex[] fft(final Complex[] signal) {
        int N = signal.length;

        // Базовый случай: если длина массива равна 1, возвращаем одно комплексное число
        if (N == 1) {
            return new Complex[]{signal[0]};
        }

        // Проверка, что длина массива является степенью двойки
        if ((N & (N - 1)) != 0) {
            throw new IllegalArgumentException("Длина массива должна быть степенью двойки.");
        }

        // Разделение на четные и нечетные индексы
        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];
        for (int i = 0; i < N / 2; i++) {
            even[i] = signal[2 * i];
            odd[i] = signal[2 * i + 1];
        }

        // Рекурсивный вызов БПФ
        Complex[] evenFFT = fft(even);
        Complex[] oddFFT = fft(odd);

        // Объединение результатов
        Complex[] spectrum = new Complex[N];

        for (int k = 0; k < N / 2; k++) {
            double angle = -2 * Math.PI * k / N;
            Complex exponential = Complex.fromPlane(1, angle);

            // Вычисление спектра для первой половины
            spectrum[k] = evenFFT[k].add(oddFFT[k].multiply(exponential));

            // Вычисление спектра для второй половины
            spectrum[k + N / 2] = evenFFT[k].subtract(oddFFT[k].multiply(exponential));
        }

        return spectrum;
    }

    // Построение АЧХ
    public static double[] calculateAmplitudeSpectrum(final Complex[] spectrum) {
        double[] amplitudeSpectrum = new double[spectrum.length];
        for (int i = 0; i < spectrum.length; i++) {
            amplitudeSpectrum[i] = 2* spectrum[i].abs() / spectrum.length;
        }
        return amplitudeSpectrum;
    }

    // Построение АЧХ
    public static double[] calculateFrequencies(final Complex[] spectrum, double samplingRate) {
        double[] frequencies = new double[spectrum.length];
        for (int i = 0; i < spectrum.length; i++) {
            frequencies[i] = i * samplingRate / spectrum.length;
        }
        return frequencies;
    }

    public static Complex[] reduceAmplitude(final Complex[] spectrum, double divider) {
        return Arrays.stream(spectrum).map(x -> x.divide(divider)).toArray(Complex[]::new);
    }

    public static Complex[] highPassFilter(final Complex[] spectrum, double thresholdFrequency, double samplingRate) {
        int n = spectrum.length;
        double frequencyResolution = samplingRate / n;
        Complex[] filteredResult = new Complex[n];

        for (int i = 0; i < n; i++) {
            double frequency = i * frequencyResolution;
            if (frequency < thresholdFrequency) {
                filteredResult[i] = Complex.ZERO();
                filteredResult[n-i-1] = Complex.ZERO();
            } else if(filteredResult[i] == null) {
                filteredResult[i] = spectrum[i];
            }
        }
        return filteredResult;
    }

    // Обратное дискретное преобразование Фурье (ОДПФ)
    public static Complex[] idft(final Complex[] spectrum) {
        Complex[] signal = dft(Arrays.stream(spectrum).map(Complex::conjugate).toArray(Complex[]::new));
        Arrays.stream(signal).forEach(c -> {
            c.selfConjugate();
            c.selfDivide(spectrum.length);
        });
        return signal;
    }

    public static Complex[] ifft(final Complex[] spectrum) {
        Complex[] signal = fft(Arrays.stream(spectrum).map(Complex::conjugate).toArray(Complex[]::new));
        Arrays.stream(signal).forEach(c -> {
            c.selfConjugate();
            c.selfDivide(spectrum.length);
        });
        return signal;

    }
}