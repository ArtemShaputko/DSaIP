package signals.ui;

import lombok.AllArgsConstructor;
import signals.math.FourierTransform;
import signals.util.params.DiscreteDrawParams;
import signals.util.params.FunctionDrawParams;
import signals.util.SignalPlotter;
import signals.function.Basis;
import signals.function.Complex;
import signals.function.Constants;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class Lab1UI extends PlotterUI {
    private final SignalPlotter signalPlotter;
    private final Basis basis;
    private final Complex[] dftSpectrum;
    private final Complex[] fftSpectrum;
    private final double[] idftSignal;
    private final double[] ifftSignal;
    private final Complex[] filteredSignal;
    private final double[] reducedAmplitudeSignal;

    @Override
    public void showGUI() {
        showMainWindow(new WindowProperties(
                "Выбор графика",
                400,
                200,
                "Нарисовать",
                "Выберите график:"
        ));
    }

    @Override
    protected void processSelectedOption(String option) {
        switch (option) {
            case "График суммы":
                signalPlotter.setLabels(
                        "График суммы",
                        "t",
                        ""
                );
                signalPlotter.setSignal(new FunctionDrawParams("F(t)", basis, Constants.startValue, Constants.D, 0.001));
                signalPlotter.drawSignals();
                break;
            case "АЧХ ДПФ":
                signalPlotter.setLabels(
                        "АЧХ ДПФ",
                        "Частота, Гц",
                        "Амплитуда"
                );
                signalPlotter.setSignal(
                        "АЧХ F(t)",
                        FourierTransform.calculateAmplitudeSpectrum(dftSpectrum),
                        FourierTransform.calculateFrequencies(dftSpectrum, Constants.samplingRate)
                );
                signalPlotter.drawSignals();
                break;
            case "АЧХ БПФ":
                signalPlotter.setLabels(
                        "АЧХ БПФ",
                        "Частота, Гц",
                        "Амплитуда"
                );
                signalPlotter.setSignal(
                        "АЧХ F(t)",
                        FourierTransform.calculateAmplitudeSpectrum(fftSpectrum),
                        FourierTransform.calculateFrequencies(fftSpectrum, Constants.samplingRate)
                );
                signalPlotter.drawSignals();
                break;
            case "График функции ОДПФ":
                signalPlotter.setLabels(
                        "График функции ОДПФ",
                        "t",
                        "F(t)"
                );
                signalPlotter.setSignal(
                        new DiscreteDrawParams("F(t)", idftSignal, Constants.startValue, Constants.samplingRate));
                signalPlotter.drawSignals();
                break;
            case "График функции ОБПФ":
                signalPlotter.setLabels(
                        "График функции ОБПФ",
                        "t",
                        ""
                );
                signalPlotter.setSignal(
                        new DiscreteDrawParams("F(t)", ifftSignal, Constants.startValue, Constants.samplingRate));
                signalPlotter.drawSignals();
                break;
            case "График функции с уменьшенной амплитудой":
                signalPlotter.setLabels(
                        "График функции с уменьшенной амплитудой",
                        "t",
                        ""
                );
                signalPlotter.setSignal(
                        new DiscreteDrawParams(
                                "F(t)", reducedAmplitudeSignal, Constants.startValue, Constants.samplingRate));
                signalPlotter.drawSignals();
                break;
            case "АЧХ после обнуления":
                signalPlotter.setLabels(
                        "АЧХ после обнуления",
                        "Частота, Гц",
                        "Амплитуда"
                );
                signalPlotter.setSignal(
                        "АЧХ F(t)",
                        FourierTransform.calculateAmplitudeSpectrum(filteredSignal),
                        FourierTransform.calculateFrequencies(filteredSignal, Constants.samplingRate));
                signalPlotter.drawSignals();
                break;
            case "График функции после обнуления":
                signalPlotter.setLabels(
                        "График функции после обнуления",
                        "t",
                        ""
                );
                signalPlotter.setSignal(
                        new DiscreteDrawParams("F(t)",
                                Arrays.stream(FourierTransform.idft(filteredSignal))
                                        .mapToDouble(x -> x.real).toArray(), Constants.startValue, Constants.samplingRate));
                signalPlotter.drawSignals();
                break;
            case null:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }
    }

    @Override
    protected Optional<List<JPanel>> addPanels() {
        return Optional.empty();
    }

    @Override
    protected JComboBox<String> getStringJComboBox() {
        String[] options = {
                "График суммы",
                "АЧХ ДПФ",
                "АЧХ БПФ",
                "График функции ОДПФ",
                "График функции ОБПФ",
                "График функции с уменьшенной амплитудой",
                "АЧХ после обнуления",
                "График функции после обнуления",
        };

        return new JComboBox<>(options);
    }
}
