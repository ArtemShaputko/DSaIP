package signals.ui;

import signals.util.FunctionGUITemplate;
import signals.util.SignalPlotter;
import lombok.AllArgsConstructor;
import signals.util.params.WaveletDrawParams;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class LabUI extends PlotterUI {
    private final SignalPlotter signalPlotter;
    private final Map<String, FunctionGUITemplate> signals;
    private final List<JPanel> panels = new ArrayList<>();

    @Override
    public void showGUI() {
        processOptions();
        showMainWindow(new WindowProperties(
                "Выбор графика",
                300,
                300,
                "Нарисовать",
                "Выберите график:"
        ));
    }

    private void processOptions() {
        signals.forEach((_,s) -> {
            var params = s.params();
            if(params.getFirst() instanceof WaveletDrawParams) {
                addWaveletPanel(((WaveletDrawParams) params.getFirst()));
            }
        });
    }

    @Override
    protected void processSelectedOption(String option) {
        if (signals != null && signals.containsKey(option)) {
            drawTemplate(signals.get(option));
        } else {
            throw new IllegalStateException("Unexpected value: " + option);
        }
    }

    @Override
    protected Optional<List<JPanel>> addPanels() {
        if (panels.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(panels);
    }

    private void drawTemplate(FunctionGUITemplate template) {
        signalPlotter.setLabels(
                template.name(),
                template.xLabel(),
                template.yLabel()
        );
        signalPlotter.setSignal(template.params());
        signalPlotter.drawSignals();
    }

    private void addWaveletPanel(WaveletDrawParams params) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы вокруг панели

        // Общие настройки для строк
        int labelWidth = 180; // Фиксированная ширина меток
        int fieldWidth = 120; // Фиксированная ширина полей
        int rowHeight = 30;   // Высота строки

        // Панель для выбора уровня детализации
        JPanel intPanel = new JPanel(new BorderLayout(10, 0));
        JLabel intLabel = new JLabel("Детализирующая компонента:");
        intLabel.setPreferredSize(new Dimension(labelWidth, rowHeight));
        intPanel.add(intLabel, BorderLayout.WEST);

        JSpinner intSpinner = new JSpinner(new SpinnerNumberModel(1, 1, params.level(), 1));
        intSpinner.setPreferredSize(new Dimension(fieldWidth, rowHeight));
        intPanel.add(intSpinner, BorderLayout.CENTER);
        panel.add(intPanel);

        // Вертикальный отступ между элементами
        panel.add(Box.createVerticalStrut(10));

        // Панель для модификатора
        JPanel doublePanel = new JPanel(new BorderLayout(10, 0));
        JLabel doubleLabel = new JLabel("Модификатор:");
        doubleLabel.setPreferredSize(new Dimension(labelWidth, rowHeight));
        doublePanel.add(doubleLabel, BorderLayout.WEST);

        JSpinner doubleSpinner = new JSpinner(new SpinnerNumberModel(1.0, -1000.0, 1000.0, 0.1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(doubleSpinner, "#0.0##");
        doubleSpinner.setEditor(editor);
        doubleSpinner.setPreferredSize(new Dimension(fieldWidth, rowHeight));
        doublePanel.add(doubleSpinner, BorderLayout.CENTER);
        panel.add(doublePanel);

        // Отступ перед кнопкой
        panel.add(Box.createVerticalStrut(20));

        // Кнопка применения
        JButton button = new JButton("Применить");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(120, 35)); // Фиксированный размер кнопки
        panel.add(button);

        // Обработчик кнопки
        button.addActionListener(_ ->
                params.modifyComponents(
                        (int) intSpinner.getValue(),
                        (double) doubleSpinner.getValue()
                )
        );

        panels.add(panel);
    }


    @Override
    protected JComboBox<String> getStringJComboBox() {
        List<String> options = new ArrayList<>();
        if (signals != null && !signals.isEmpty()) {
            options.addAll(signals.keySet());
        }
        String[] optionsArray = options.toArray(new String[0]);
        return new JComboBox<>(optionsArray);
    }
}