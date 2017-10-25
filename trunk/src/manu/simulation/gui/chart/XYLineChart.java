package manu.simulation.gui.chart;

import java.awt.BasicStroke;
import java.util.ArrayList;

import javax.swing.JFrame;

import manu.simulation.others.Point;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class XYLineChart extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6189535829293414823L;
	JFreeChart chart;

	public XYLineChart() {
		setResizable(false);
		setBounds(400, 200, 500, 400);
	}

	public void XYLineChartSingleSeries(String title, String xName,
			String yName, ArrayList<Double> data) {

		XYSeriesCollection result = new XYSeriesCollection();

		XYSeries series1 = new XYSeries("a");
		for (int i = 0; i < data.size(); i++) {
			series1.add(i, data.get(i));
		}

		result.addSeries(series1);

		chart = ChartFactory.createXYLineChart(title, xName, yName, result,
				PlotOrientation.VERTICAL, false, false, false);
		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);
	}

	public void XYLineChartDouble(String title, String xName, String yName,
			ArrayList<Double>[] data, String[] name) {
		setResizable(false);
		XYSeriesCollection result = new XYSeriesCollection();
		for (int j = 0; j < data.length; j++) {
			XYSeries series1;
			series1 = new XYSeries(name[j]);
			for (int i = 0; i < data[j].size(); i++) {
				if (data[j].get(i) == -1.0)
					series1.add(i, null);
				else
					series1.add(i, data[j].get(i));
			}

			result.addSeries(series1);
		}

		chart = ChartFactory.createXYLineChart(title, xName, yName, result,
				PlotOrientation.VERTICAL, true, false, false);
		for (int i = 0; i < data.length; i++)
			setSeriesStyle(chart, i, STYLE_LINE);
		ChartPanel chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);
	}

	public void XYLineChartInteger(String title, String xName, String yName,
			ArrayList<Integer>[] data, String[] name) {
		setResizable(false);
		XYSeriesCollection result = new XYSeriesCollection();
		for (int j = 0; j < data.length; j++) {
			XYSeries series1;
			series1 = new XYSeries(name[j]);
			for (int i = 0; i < data[j].size(); i++) {
				series1.add(i, data[j].get(i));
			}

			result.addSeries(series1);
		}

		chart = ChartFactory.createXYLineChart(title, xName, yName, result,
				PlotOrientation.VERTICAL, true, false, false);
		for (int i = 0; i < data.length; i++)
			setSeriesStyle(chart, i, STYLE_LINE);
		ChartPanel chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);
	}

	public XYLineChart(String title, String xName, String yName,
			ArrayList<Point>[] data, String[] name) {
		setResizable(false);
		XYSeriesCollection result = new XYSeriesCollection();
		for (int j = 0; j < data.length; j++) {
			XYSeries series1;
			series1 = new XYSeries(name[j]);
			for (int i = 0; i < data[j].size(); i++) {
				series1.add(data[j].get(i).x, data[j].get(i).y);
			}

			result.addSeries(series1);
		}

		chart = ChartFactory.createXYLineChart(title, xName, yName, result,
				PlotOrientation.VERTICAL, true, false, false);
		for (int i = 0; i < data.length; i++)
			setSeriesStyle(chart, i, STYLE_LINE);
		ChartPanel chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);
	}

	/** Line style: line */
	public static final String STYLE_LINE = "line";
	/** Line style: dashed */
	public static final String STYLE_DASH = "dash";
	/** Line style: dotted */
	public static final String STYLE_DOT = "dot";

	/**
	 * Convert style string to stroke object.
	 * 
	 * @param style
	 *            One of STYLE_xxx.
	 * @return Stroke for <i>style</i> or null if style not supported.
	 */
	private BasicStroke toStroke(String style) {
		BasicStroke result = null;

		if (style != null) {
			float lineWidth = 2f;
			float dash[] = { 5.0f };
			float dot[] = { lineWidth };

			if (style.equalsIgnoreCase(STYLE_LINE)) {
				result = new BasicStroke(lineWidth);
			} else if (style.equalsIgnoreCase(STYLE_DASH)) {
				result = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
			} else if (style.equalsIgnoreCase(STYLE_DOT)) {
				result = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 2.0f, dot, 0.0f);
			}
		}// else: input unavailable

		return result;
	}// toStroke()

	/**
	 * Set color of series.
	 * 
	 * @param chart
	 *            JFreeChart.
	 * @param seriesIndex
	 *            Index of series to set color of (0 = first series)
	 * @param style
	 *            One of STYLE_xxx.
	 */
	public void setSeriesStyle(JFreeChart chart, int seriesIndex, String style) {
		if (chart != null && style != null) {
			BasicStroke stroke = toStroke(style);

			Plot plot = chart.getPlot();
			if (plot instanceof CategoryPlot) {
				CategoryPlot categoryPlot = chart.getCategoryPlot();
				CategoryItemRenderer cir = categoryPlot.getRenderer();
				try {
					cir.setSeriesStroke(seriesIndex, stroke); // series line
																// style
				} catch (Exception e) {
					System.err.println("Error setting style '" + style
							+ "' for series '" + seriesIndex + "' of chart '"
							+ chart + "': " + e);
				}
			} else if (plot instanceof XYPlot) {
				XYPlot xyPlot = chart.getXYPlot();
				XYItemRenderer xyir = xyPlot.getRenderer();
				try {
					xyir.setSeriesStroke(seriesIndex, stroke); // series line
																// style
				} catch (Exception e) {
					System.err.println("Error setting style '" + style
							+ "' for series '" + seriesIndex + "' of chart '"
							+ chart + "': " + e);
				}
			} else {
				System.out
						.println("setSeriesColor() unsupported plot: " + plot);
			}
		}// else: input unavailable
	}// setSeriesStyle()

}
