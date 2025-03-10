package signals.ui;

import javax.swing.*;

public abstract class PlotterUI implements UI {

    protected void showMainWindow(
            WindowProperties windowProperties) {
        JFrame frame = new JFrame(windowProperties.frameName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(windowProperties.width(), windowProperties.height());

        JComboBox<String> comboBox = getStringJComboBox();
        JButton drawButton = new JButton(windowProperties.buttonText());

        JPanel panel = new JPanel();
        panel.add(new JLabel(windowProperties.panelText()));
        panel.add(comboBox);
        panel.add(drawButton);

        frame.add(panel);
        frame.setVisible(true);
        drawButton.addActionListener(_ -> {
            String selectedOption = (String) comboBox.getSelectedItem();
            processSelectedOption(selectedOption);
        });
    }

    protected abstract JComboBox<String> getStringJComboBox();
    protected abstract void processSelectedOption(String option);
}
