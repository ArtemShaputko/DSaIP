package signals.util;

import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import signals.util.params.DiscreteDrawParams;
import signals.util.params.FunctionDrawParams;
import signals.util.params.SignalDrawParams;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;


public class SignalPlotter {
    @Setter
    @Getter
    private String legend;
    @Setter
    @Getter
    private String yLabel;
    @Setter
    @Getter
    private String xLabel;
    private final List<XYSeries> signals = new ArrayList<>();

    public void freeSignals() {
        signals.clear();
    }

    public void setLabels(String legend, String xLabel, String ylabel) {
        this.legend = legend;
        this.yLabel = ylabel;
        this.xLabel = xLabel;
    }

    public void setSignal(List<? extends SignalDrawParams> signals) {
        freeSignals();
        for (SignalDrawParams param : signals) {
            if (param instanceof FunctionDrawParams fp) {
                addSignal(fp);
            } else if (param instanceof DiscreteDrawParams dp) {
                addSignal(dp);
            } else {
                throw new IllegalArgumentException("Unsupported parameter type");
            }
        }
    }
    public void setSignal(FunctionDrawParams params) {
        freeSignals();
        addSignal(params);
    }

    public void setSignal(String label, final double[] signal, final double[] x) {
        freeSignals();
        addSignal(label, signal, x);
    }

    public void setSignal(DiscreteDrawParams params) {
        freeSignals();
        addSignal(params);
    }

    public void addSignal(FunctionDrawParams params) {
        XYSeries series = new XYSeries(params.name());
        for (double t = params.startTime(); t <= params.time(); t += params.step()) {
            series.add(t, params.function().getValue(t));
        }
        signals.add(series);
    }

    public void addSignal(String label, final double[] signal, final double[] x) {
        XYSeries series = new XYSeries(label);
        for (int i = 0; i < signal.length; i++) {
            series.add(x[i], signal[i]);
        }
        signals.add(series);
    }

    public void addSignal(DiscreteDrawParams params) {
        XYSeries series = new XYSeries(params.name());
        double time = params.startTime();
        for (double value : params.signal()) {
            series.add(time, value);
            time += 1/ params.samplingRate();
        }
        signals.add(series);
    }

    public void drawSignals() {

        XYSeriesCollection dataset = new XYSeriesCollection();
        signals.forEach(dataset::addSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                legend, // Название графика
                xLabel,                  // Подпись оси X
                yLabel,               // Подпись оси Y
                dataset,              // Данные
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // Ориентация графика
                true,                 // Включить легенду
                true,                 // Включить tooltips
                false                 // Включить URLs
        );
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = getXyLineAndShapeRenderer(plot);

        // Добавляем линии x=0 и y=0
        ValueMarker xZeroMarker = new ValueMarker(0);
        xZeroMarker.setPaint(Color.BLACK); // Цвет линии x=0
        xZeroMarker.setStroke(new BasicStroke(0.5f));
        plot.addDomainMarker(xZeroMarker); // Вертикальная линия x=0

        ValueMarker yZeroMarker = new ValueMarker(0);
        yZeroMarker.setPaint(Color.BLACK); // Цвет линии y=0
        yZeroMarker.setStroke(new BasicStroke(0.5f));
        plot.addRangeMarker(yZeroMarker); // Горизонтальная линия y=0

        plot.setBackgroundPaint(Color.WHITE);
        for (int i = 0; i < signals.size(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(1.0f));
            renderer.setSeriesShape(i, new Ellipse2D.Double(-0.75, -0.75, 1.5, 1.5));
        }

        plot.setRenderer(renderer);

        ChartFrame frame = new ChartFrame(legend, chart);
        frame.pack();
        frame.setVisible(true);
    }

    private static XYLineAndShapeRenderer getXyLineAndShapeRenderer(XYPlot plot) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // Настройка осей
        ValueAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();

        // Добавляем сетку для улучшения видимости
        xAxis.setTickMarkPaint(Color.LIGHT_GRAY);
        yAxis.setTickMarkPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        return renderer;
    }
}