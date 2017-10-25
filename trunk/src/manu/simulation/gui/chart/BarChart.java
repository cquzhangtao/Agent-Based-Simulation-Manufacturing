package manu.simulation.gui.chart;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChart extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFreeChart chart;

	public BarChart() {
		setResizable(false);
		setBounds(300, 200, 700, 400);
	}

	public void BarChartSingleSeries(String title, String xName, String yName,
			int[] data, String[] name) {

		chart = ChartFactory.createBarChart(title, xName, yName,
				getDataset(title, name, data), PlotOrientation.VERTICAL, false,
				true, false);
		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);
	}

	public CategoryDataset getDataset(String title, String[] type, int[] value) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = title;
		for (int i = 0; i < type.length; i++) {
			result.addValue(value[i], series1, type[i]);
		}

		return result;
	}

	public CategoryDataset getDataset(String title, String[] type,
			double[] value) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = title;
		for (int i = 0; i < type.length; i++) {
			result.addValue(value[i], series1, type[i]);
		}

		return result;
	}

	public void BarChartSingleSeries(String title, String xName, String yName,
			double[] data, String[] name) {

		chart = ChartFactory.createBarChart(title, xName, yName,
				getDataset(title, name, data), PlotOrientation.VERTICAL, false,
				true, false);
		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		setContentPane(chartPanel);
	}

}
