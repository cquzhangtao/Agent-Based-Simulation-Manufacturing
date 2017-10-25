package manu.scheduling.training;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class TestKmeans extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Container jpanel;

	public TestKmeans(String title) {
		super(title);
		jpanel=new JPanel();
		jpanel.setPreferredSize(new Dimension(800, 400));
		jpanel.setLayout(new GridBagLayout());
		setContentPane(jpanel);
		cluster(createDataset());
	}

	public List<Point>[] cluster(ArrayList<Point> dataset){
		ClusterInfo info=new ClusterInfo();
		info.setClassNum(3);
		info.setEpoch(500);
		info.setTolerance(.01);
		ArrayList<Point> dataset1=new ArrayList<Point>();
		try {
			Point[] minMax = Normalizer.normalize(dataset,dataset1,new Class[]{KmeansField.class});
			Kmeans<Point> kmeans=new Kmeans<Point>(dataset1,info);
			kmeans.setMinMax(minMax);
			kmeans.setUnNormalizedData(dataset);
			List<Point>[] a = kmeans.comput();
			drawChart(a,kmeans.getErrors());

			return a;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
		
	}
	public static void main(String[] args) {
		

		TestKmeans scatterplotdemo4 = new TestKmeans("Test");
		scatterplotdemo4.pack();
		RefineryUtilities.centerFrameOnScreen(scatterplotdemo4);
		scatterplotdemo4.setVisible(true);

	}

	private ArrayList<Point> createDataset() {
		ArrayList<Point> dataset = new ArrayList<Point>();
		createOneClassData(new Point(100, 180), 50, dataset);
		createOneClassData(new Point(210, 100), 50, dataset);
		createOneClassData(new Point(400, 270), 50, dataset);
		createOneClassData(new Point(500, 100), 50, dataset);
		createOneClassData(new Point(800, 230), 50, dataset);
		createOneClassData(new Point(0, 0), 50, dataset);
		return dataset;
	}

	public XYDataset getDataset(List<Point>[] dataset) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();

		for (int i = 0; i < dataset.length; i++) {
			XYSeries series = new XYSeries(i);
			for (Point p : dataset[i]) {
				series.add(p.getX(), p.getY());
			}
			xySeriesCollection.addSeries(series);
		}
		return xySeriesCollection;

	}

	private void drawChart( List<Point>[] a, ArrayList<Double> b) {
		JFreeChart jfreechart = ChartFactory.createScatterPlot(
				"Test", "X", "Y", getDataset(a),
				PlotOrientation.VERTICAL, true, true, false);
		// Shape cross = ShapeUtilities.createDiagonalCross(3, 1);

		Shape shape = new Ellipse2D.Double(0, 0, 3, 3);
		XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
		XYItemRenderer renderer = xyPlot.getRenderer();
		renderer.setBaseShape(shape);
		renderer.setBasePaint(Color.red);
		GridBagConstraints		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.5;
		jpanel.add(new ChartPanel(jfreechart),c);
		
		ChartPanel cp = xyLineChartSingleSeries("Convergency", "times",
				"Error", b);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.5;
		jpanel.add(cp,c);

	}

	private void createOneClassData(Point center, double radius,
			ArrayList<Point> data) {
		Random rnd = new Random();
		for (int i = 0; i < 5000; i++) {
			double dx = (2 * rnd.nextDouble() - 1) * radius;
			double dy = (2 * rnd.nextDouble() - 1) * radius;
			Point p = new Point();
			p.setX(center.getX() + dx);
			p.setY(center.getY() + dy);
			data.add(p);
		}

		return;
	}
	
	public ChartPanel xyLineChartSingleSeries(String title, String xName,
			String yName, ArrayList<Double> data) {

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

class Point implements Cloneable, Serializable{
	//@KmeansField
	double x;
	double xWeight=.5;
	//@KmeansField
	double y;
	double yWeight=.5;
	@KmeansField
	double[]d=new double[2];
	double dWeight=1;

	public Point() {

	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
		d[0]=x;
		d[1]=y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
		d[0]=x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		d[1]=y;
	}
	public Point clone() {
		try {
			return (Point) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
