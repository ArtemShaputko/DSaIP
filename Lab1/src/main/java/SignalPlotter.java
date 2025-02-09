import function.Function;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class SignalPlotter {
    private String legend;
    private String yLabel;
    private String xlabel;

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public String getYLabel() {
        return yLabel;
    }

    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public String getXlabel() {
        return xlabel;
    }

    public void setXlabel(String xlabel) {
        this.xlabel = xlabel;
    }

    public SignalPlotter(String legend, String yLabel, String xlabel) {
        this.legend = legend;
        this.yLabel = yLabel;
        this.xlabel = xlabel;
    }

    public void drawSignal(Function function, double time, double step) {
        XYSeries series = new XYSeries(yLabel);
        for (double t = 0; t <= time; t += step) {
            series.add(t, function.getValue(t));
        }
        drawSignal(series);
    }

    public void drawSignal(final double[] signal, final double[] x) {
        XYSeries series = new XYSeries(yLabel);
        for (int i = 0; i < signal.length; i++) {
            series.add(x[i], signal[i]);
        }
        drawSignal(series);
    }

    public void drawSignal(final double[] signal, double samplingRate) {
        XYSeries series = new XYSeries(yLabel);
        double time = 0;
        for (double value : signal) {
            series.add(time, value);
            time += 1/samplingRate;
        }
        drawSignal(series);
    }

    public void drawSignal(final XYSeries series) {

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                legend, // Название графика
                xlabel,                  // Подпись оси X
                yLabel,               // Подпись оси Y
                dataset,              // Данные
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // Ориентация графика
                true,                 // Включить легенду
                true,                 // Включить tooltips
                false                 // Включить URLs
        );
        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.BLUE); // Цвет линии
        renderer.setBaseLinesVisible(true);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f)); // Толщина линии
        plot.setBackgroundPaint(Color.WHITE);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-1.5, -1.5, 3, 3));

        plot.setRenderer(renderer);

        ChartFrame frame = new ChartFrame("График f(t)", chart);
        frame.pack();
        frame.setVisible(true);
    }
}