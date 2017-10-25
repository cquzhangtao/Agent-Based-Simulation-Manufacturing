package manu.scheduling.training.gui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartUtilities {
	public static ChartPanel xyLineChartMultiSeries(String title, String xName,
			String yName, ArrayList<Double>[] data, String[] seriesName) {

		XYSeriesCollection result = new XYSeriesCollection();
		for (int j = 0; j < data.length; j++) {
			XYSeries series1 = new XYSeries(seriesName[j]);
			for (int i = 0; i < data[j].size(); i++) {
				series1.add(i, data[j].get(i));
			}
			result.addSeries(series1);
		}

		JFreeChart chart = ChartFactory.createXYLineChart(title, xName, yName,
				result, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}
	public static ChartPanel xyLineChartSingleSeries(String title, String xName,
			String yName, List<Double> data) {

		XYSeriesCollection result = new XYSeriesCollection();

		XYSeries series1 = new XYSeries("a");
		for (int i = 0; i < data.size(); i++) {
			series1.add(i, data.get(i));
		}

		result.addSeries(series1);

		JFreeChart chart = ChartFactory.createXYLineChart(title, xName, yName,
				result, PlotOrientation.VERTICAL, false, false, false);
		// we put the chart into a panel
		chart.getTitle().setFont(new Font("Time New Rome", Font.BOLD, 15));
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}
}
