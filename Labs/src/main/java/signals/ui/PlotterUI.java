package signals.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public abstract class PlotterUI implements UI {

    protected void showMainWindow(WindowProperties windowProperties) {
        var frame = new JFrame(windowProperties.frameName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // Основной layout для фрейма
        frame.setSize(windowProperties.width(), windowProperties.height());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlPanel.add(new JLabel(windowProperties.panelText()));

        JComboBox<String> comboBox = getStringJComboBox();
        controlPanel.add(comboBox);

        JButton drawButton = new JButton(windowProperties.buttonText());
        controlPanel.add(drawButton);
        mainPanel.add(controlPanel);
        frame.add(mainPanel, BorderLayout.CENTER);
        var extraPanels = addPanels();
        extraPanels.ifPresent(panels -> {
            if(!panels.isEmpty()) frame.add(panels.get(0), BorderLayout.NORTH);
            if(panels.size() > 1) frame.add(panels.get(1), BorderLayout.SOUTH);
        });

        // Обработчик кнопки
        drawButton.addActionListener(_ -> {
            String selectedOption = (String) comboBox.getSelectedItem();
            processSelectedOption(selectedOption);
        });

        frame.setVisible(true);
    }

    protected abstract JComboBox<String> getStringJComboBox();
    protected abstract void processSelectedOption(String option);
    protected abstract Optional<List<JPanel>> addPanels();
}
