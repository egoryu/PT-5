
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.*;
import static org.knowm.xchart.style.lines.SeriesLines.SOLID;

public class Methods {
    private static final TreeMap<Double, Double> p = new TreeMap<>();
    private static final TreeMap<Double, Integer> count = new TreeMap<>();
    public static void analyze(ArrayList<Double> points) {
        AtomicReference<Double> m = new AtomicReference<>((double) 0);
        AtomicReference<Double> d = new AtomicReference<>((double) 0);

        points.forEach((x) -> count.put(x, count.getOrDefault(x, 0) + 1));
        count.forEach((x, y) -> p.put(x, y * 1.0 / points.size()));
        p.forEach((x, y) -> m.updateAndGet(v -> v + x * y));
        p.forEach((x, y) -> d.updateAndGet(v -> v + x * x * y));
        d.updateAndGet(v -> v - m.get() * m.get());

        System.out.println("Статистический ряд:");
        count.forEach((x, y) -> System.out.printf("%-5.2f ", x));
        System.out.println();
        count.forEach((x, y) -> System.out.printf("%-5d ", y));
        System.out.println();
        p.forEach((x, y) -> System.out.printf("%-5.2f ", y));
        System.out.println();
        System.out.printf("Мат ожидание: %.3f\n", m.get());
        System.out.printf("Дисперсия: %.3f\n", d.get());
        System.out.printf("Исправленная Дисперсия: %.3f\n", d.get() * points.size() / (points.size() - 1));
        System.out.printf("Среднеквадратическое отклонение: %.3f\n", sqrt(d.get()));
        System.out.printf("Исправленное Среднеквадратическое отклонение: %.3f\n\n", sqrt(d.get()) * points.size() / (points.size() - 1));
    }

    public static void empiricFunction() {
        AtomicReference<Double> prev = new AtomicReference<>((double) -100);
        AtomicReference<Double> v = new AtomicReference<>((double) 0);

        System.out.println("Эмпирическая функция:");

        p.forEach((x, y) -> {
            if (v.get() == 0.0) {
                System.out.printf("%.2f , x <= %.2f\n", v.get(), x);
            } else {
                System.out.printf("%.2f , %.2f < x <= %.2f\n", v.get(), prev.get(), x);
            }

            v.updateAndGet(q -> q + y);
            prev.set(x);
        });

        System.out.println(1.00 + " , x > " + prev.get() + '\n');
    }

    public static void drawEmpiricFunction() {
        XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.Matlab).title("Эмпирическая функция").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setSeriesLines(new BasicStroke[]{SOLID});
        chart.getStyler().setMarkerSize(0);

        AtomicReference<Double> prev = new AtomicReference<>((double) -100);
        AtomicReference<Double> v = new AtomicReference<>((double) 0);

        p.forEach((x, y) -> {
            if (prev.get() == -100) {
                chart.addSeries(String.format("%.2f , x <= %.2f\n", v.get(), x), new double[]{x - 0.5, x}, new double[]{v.get(), v.get()});
            } else {
                chart.addSeries(String.format("%.2f , %.2f < x <= %.2f\n", v.get(), prev.get(), x), new double[]{prev.get(), x}, new double[]{v.get(), v.get()});
            }

            v.updateAndGet(q -> q + y);
            prev.set(x);
        });

        chart.addSeries(String.format("1.00 , x > %.2f\n", prev.get()), new double[]{prev.get(), prev.get() + 0.5}, new double[]{v.get(), v.get()});

        try {
            BitmapEncoder.saveBitmap(chart, "src/main/resources/output/EmpiricFunction", BitmapEncoder.BitmapFormat.JPG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double getStep(ArrayList<Double> points) {
        return (points.get(points.size() - 1) - points.get(0)) / (1 + (log(points.size()) / Math.log(2)));
    }

    public static int getCount(ArrayList<Double> points) {
        return (int) Math.ceil(1 + (Math.log(points.size()) / Math.log(2)));
    }

    public static void drawFrequencyPolygon(ArrayList<Double> points) {
        XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.Matlab).title("Полигон частот").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setSeriesLines(new BasicStroke[]{SOLID}).setLegendVisible(false);
        ArrayList<Double> x = new ArrayList<>(), y = new ArrayList<>();

        System.out.println("Группированный ряд:");

        double step = getStep(points), x_start = points.get(0) - step / 2;
        for (int i = 0; i < getCount(points); i++) {
            int count = 0;

            for (double value : points) {
                if (value >= x_start && value < (x_start + step)) {
                    count++;
                }
            }

            System.out.printf("[%.3f ; %.3f) - %d - %.3f", x_start, (x_start + step), count, (count * 1.0 / points.size() / step));
            System.out.println();
            x.add(Double.parseDouble(String.format("%.2f", x_start + step / 2.0).replace(',', '.')));
            y.add(count * 1.0);
            x_start += step;
        }

        chart.addSeries("y", x, y);
        try {
            BitmapEncoder.saveBitmap(chart, "src/main/resources/output/FrequencyPolygon", BitmapEncoder.BitmapFormat.JPG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void drawHistogram(ArrayList<Double> points) {
        XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.Matlab).title("Гистограмма частот").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area).setLegendVisible(false).setMarkerSize(0);
        ArrayList<Double> x = new ArrayList<>(), y = new ArrayList<>();

        double step = getStep(points), x_start = points.get(0) - step / 2;
        for (int i = 0; i < getCount(points); i++) {
            int count = 0;

            for (double value : points) {
                if (value >= x_start && value < (x_start + step)) {
                    count++;
                }
            }

            x.add(Double.parseDouble(String.format("%.2f", x_start).replace(',', '.')));
            x.add(Double.parseDouble(String.format("%.2f", x_start + step).replace(',', '.')));
            y.add(count * 1.0 / points.size() / step);
            y.add(count * 1.0 / points.size() / step);
            x_start += step;
        }

        chart.addSeries("y", x, y);
        try {
            BitmapEncoder.saveBitmap(chart, "src/main/resources/output/Histogram", BitmapEncoder.BitmapFormat.JPG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
