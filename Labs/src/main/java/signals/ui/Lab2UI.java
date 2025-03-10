package signals.ui;

import lombok.RequiredArgsConstructor;
import signals.function.Function;
import signals.util.DiscreteDrawParams;
import signals.util.FunctionDrawParams;
import signals.util.SignalPlotter;
import signals.function.Constants;
import lombok.AllArgsConstructor;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
public class Lab2UI extends PlotterUI {
    private final SignalPlotter signalPlotter;
    Map<String, DiscreteDrawTemplate> discreteSignals;
    private final Function x;
    private final Function y;

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
            case "Графики x(t) и y(t)":
                signalPlotter.setLabels(
                        "Графики функций x(t) и y(t)",
                        "t",
                        ""
                );
                signalPlotter.setSignal(List.of(
                        new FunctionDrawParams("x(t)", x, 0, Constants.D, 0.001),
                        new FunctionDrawParams("y(t)", y, 0, Constants.D, 0.001)));
                signalPlotter.drawSignals();
                break;
            case null:
                break;
            default:
                if (discreteSignals != null && discreteSignals.containsKey(option)) {
                    drawTemplate(discreteSignals.get(option));
                } else {
                    throw new IllegalStateException("Unexpected value: " + option);
                }
        }
    }

    private void drawTemplate(DiscreteDrawTemplate template) {
        signalPlotter.setLabels(
                template.legend(),
                template.xLabel(),
                template.yLabel()
        );
        signalPlotter.setSignal(
                new DiscreteDrawParams(template.name(), template.signal(), template.startTime(), template.samplingRate()));
        signalPlotter.drawSignals();
    }

    @Override
    protected JComboBox<String> getStringJComboBox() {
        List<String> options = new ArrayList<>(List.of(
                "Графики x(t) и y(t)"
        ));
        if (discreteSignals != null && !discreteSignals.isEmpty()) {
            options.addAll(discreteSignals.keySet());
        }
        String[] optionsArray = options.toArray(new String[0]);
        return new JComboBox<>(optionsArray);
    }
}