package manu.simulation.gui.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class WaitTimeChart extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2129634524261835433L;
	JFreeChart chart;
	JRadioButton both;
	JRadioButton max;
	JRadioButton average;
	String chartTitle;
	double[] maxdata;
	double[] avgdata;
	String[] name;
	CategoryPlot plot;

	public WaitTimeChart(double[] maxdata, double[] avgdata, String[] name) {
		chartTitle = "Wait Time";
		this.avgdata = avgdata;
		this.maxdata = maxdata;
		this.name = name;

		setResizable(false);
		setBounds(300, 200, 720, 480);
		getContentPane().setLayout(null);
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(100, 405, 560, 40);
		jp.setBorder(LineBorder.createGrayLineBorder());
		getContentPane().add(jp);

		JLabel label = new JLabel("Type");
		label.setBounds(20, 10, 80, 20);

		jp.add(label);
		CustomListener c1 = new CustomListener();
		ButtonGroup type = new ButtonGroup();

		average = new JRadioButton("Average");
		average.setSelected(true);
		average.setBounds(100, 10, 100, 20);
		average.addActionListener(c1);
		max = new JRadioButton("Max");
		max.setBounds(210, 10, 100, 20);
		max.addActionListener(c1);
		both = new JRadioButton("Both");
		both.setBounds(310, 10, 100, 20);
		both.addActionListener(c1);
		type.add(max);
		type.add(average);
		type.add(both);
		jp.add(max);
		jp.add(average);
		jp.add(both);

		chart = ChartFactory.createBarChart("Average " + chartTitle,
				"Tool Group", "Wait Time hours/lot",
				getDataset("Average", name, avgdata), PlotOrientation.VERTICAL,
				false, true, false);
		plot = chart.getCategoryPlot();
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBounds(10, 0, 700, 400);
		getContentPane().add(chartPanel);

	}

	public CategoryDataset getDataset(String[] type, double[] max, double[] avg) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		for (int i = 0; i < type.length; i++) {
			result.addValue(avg[i], series1, type[i]);
			result.addValue(max[i], series2, type[i]);
		}

		return result;
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

	class CustomListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			JRadioButton rButton = (JRadioButton) arg0.getSource();
			if (rButton == average) {
				chart.setTitle("Average " + chartTitle);
				plot.setDataset(getDataset("Average", name, avgdata));

			} else if (rButton == max) {
				chart.setTitle("Max " + chartTitle);
				plot.setDataset(getDataset("Max", name, maxdata));

			} else {
				chart.setTitle(chartTitle);
				plot.setDataset(getDataset(name, maxdata, avgdata));

			}
		}

	}

}
