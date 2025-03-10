package signals.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
public class SignalSaver {
    private String fileName;
    private double samplingRate;

    public SignalSaver(String fileName, double samplingRate) {
        this.fileName = fileName;
        this.samplingRate = samplingRate;
    }

    public void writeWavFile(final double[] signal) throws IOException {
        // Проверка входных данных
        if (signal == null || signal.length == 0) {
            throw new IllegalArgumentException("Сигнал не может быть null или пустым.");
        }
        if (samplingRate <= 0) {
            throw new IllegalArgumentException("Частота дискретизации должна быть положительной.");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть null или пустым.");
        }

        int numSamples = signal.length;
        // Создание массива байтов для 16-bit PCM
        byte[] audioData = new byte[numSamples * 2];
        for (int i = 0; i < numSamples; i++) {
            short sample = (short) (signal[i] * Short.MAX_VALUE); // Преобразуем в 16-битное значение
            audioData[2 * i] = (byte) (sample & 0xFF); // Младший байт
            audioData[2 * i + 1] = (byte) ((sample >> 8) & 0xFF); // Старший байт
        }

        // Создание потока аудиоданных
        try (AudioInputStream audioStream = new AudioInputStream(
                new java.io.ByteArrayInputStream(audioData),
                new AudioFormat((float) samplingRate, 16, 1, true, false),
                numSamples)) {

            // Запись в WAV-файл
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File(fileName));
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }
}