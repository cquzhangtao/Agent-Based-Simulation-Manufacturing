package manu.sbo.release.multiperiod;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

public class GraphicShow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	XYPlot plot;
	Order order;

	public GraphicShow(Order order) {
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				// System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
		this.order = order;
		JFreeChart chart = ChartFactory.createXYLineChart(null, "Time",
				"Product", null, PlotOrientation.VERTICAL, false, true, true);
		// chart.getLegend().setFrame(BlockBorder.NONE);

		plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);
		plot.setDataset(getDataset(order));
		plot.setDomainAxis(new DateAxis("Time"));
		plot.setOutlineVisible(false);
		
		//renderer.setStroke(new BasicStroke(3));
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinesVisible(false);
		plot.setDomainGridlinesVisible(false);
		LegendTitle lt = new LegendTitle(plot);
		lt.setItemFont(new Font("Dialog", Font.PLAIN, 9));
		lt.setBackgroundPaint(new Color(200, 200, 255, 100));
		lt.setFrame(new BlockBorder(Color.white));
		lt.setPosition(RectangleEdge.BOTTOM);
		XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.02, lt,
				RectangleAnchor.BOTTOM_RIGHT);
		ta.setMaxWidth(0.2);
		plot.addAnnotation(ta);

		String[] pname = new String[order.getProducts().size()];
		int index = 0;
		for (Product p : order.getProducts()) {
			pname[index++] = "Product" + p.getId();
		}
		plot.setRangeAxis(new SymbolAxis("Product", pname));

		// renderer.setBaseItemLabelGenerator(new XYItemLabelGenerator() {
		//
		// @Override
		// public String generateLabel(XYDataset arg0, int arg1, int arg2) {
		// XYSeriesCollection dataset = (XYSeriesCollection) arg0;
		// Object obj = dataset.getSeries(arg1).getItems().get(arg2);
		// if (obj instanceof MyXYDataItem) {
		// MyXYDataItem item = (MyXYDataItem) obj;
		// return item.getLabel();
		// } else {
		// return "";
		// }
		// }
		// });
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesLinesVisible(2, false);
		renderer.setSeriesVisibleInLegend(0, true);
		renderer.setSeriesVisibleInLegend(1, true);
		renderer.setSeriesVisibleInLegend(2, true);
		// renderer.setSeriesShape(order.getProducts().size()+1, new
		// Line2D.Double(0, -2, 0, 2));
		renderer.setBaseItemLabelsVisible(true);

		this.getContentPane().add(new ChartPanel(chart));
		this.pack();
		this.setVisible(true);
	}

	private XYDataset getDataset(Order order) {
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
				.getRenderer();
		XYSeriesCollection dataset = new XYSeriesCollection();

		XYSeries series1 = new XYSeries("Earliest Start Date");
		XYSeries series2 = new XYSeries("Latest Start Date");
		XYSeries series3 = new XYSeries("Due Date");
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		for (Product product : order.getProducts()) {
			XYSeries series = new XYSeries(product.getId() + "D");
			XYSeries seriesa = new XYSeries(product.getId() + "a");
			MyXYDataItem item = new MyXYDataItem(product.getEarliesStartDate(),
					product.getId() - 1, "Earliest Time");
			series1.add(item);
			seriesa.add(item);
			item = new MyXYDataItem(product.getStartDate(),
					product.getId() - 1, "Latest Time");
			series2.add(item);
			series.add(item);
			seriesa.add(item);
			item = new MyXYDataItem(product.getDueDate(), product.getId() - 1,
					"Due Date");
			series3.add(item);
			series.add(item);
			dataset.addSeries(series);
			//dataset.addSeries(seriesa);
			renderer.setSeriesStroke(dataset.indexOf(series), new BasicStroke(1.2f));
			renderer.setSeriesVisibleInLegend(dataset.indexOf(series), false);
			renderer.setSeriesShapesVisible(dataset.indexOf(series), false);
//			renderer.setSeriesVisibleInLegend(dataset.indexOf(seriesa), false);
//			renderer.setSeriesShapesVisible(dataset.indexOf(seriesa), false);
//			Stroke dashed = new BasicStroke(1.2f, BasicStroke.CAP_BUTT,
//					BasicStroke.JOIN_MITER, 10.0f, new float[] { 5.0f }, 0.0f);
//			renderer.setSeriesStroke(dataset.indexOf(seriesa), dashed);
		}

		return dataset;
	}

	class MyXYDataItem extends XYDataItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String label;

		public MyXYDataItem(double x, double y, String label) {
			super(x, y);
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}

	public void update() {
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
				.getRenderer();
		XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();

		 for (Product product : order.getProducts()) {
		 XYSeries series = new XYSeries(product.getId() + "d");
		 for (TimeSlice ts : product.getTimeSlice()) {
		 for (int i = 0; i < ts.getArrangedJobNum(); i++) {
		 series.add(ts.getStart() + i * ts.getReleaseInterval() * 60
		 * 1000, product.getId() - 1);
		 }
		 }
		 dataset.addSeries(series);
		 renderer.setSeriesShape(dataset.indexOf(series), new Line2D.Double(0,
		 -20, 0, 20));
		 //renderer.setSeriesLinesVisible(dataset.indexOf(series), false);
		 renderer.setSeriesVisibleInLegend(dataset.indexOf(series), false);
		 }
		XYSeries series = new XYSeries("line");
		long end = Long.MIN_VALUE;
		for (Product product : order.getProducts()) {

			for (long x : product.getTimeSliceIndex()) {
				if (!series.getItems().contains(x)) {
					series.add(x, -1);
					series.add(x, order.getProducts().size() + 1);
					series.add(x, null);
				}
			}
			TimeSlice first = product.getTimeSlice().get(0);
			if (first != null) {
				if (end < first.getEnd()) {
					end = first.getEnd();
				}
			}
		}
		series.add(end, -1);
		series.add(end, order.getProducts().size() + 1);
		series.add(end, null);
		dataset.addSeries(series);
		renderer.setSeriesVisibleInLegend(dataset.indexOf(series), false);
		renderer.setSeriesShapesVisible(dataset.indexOf(series), false);
		renderer.setSeriesStroke(dataset.indexOf(series), new BasicStroke(0.3f));
		renderer.setSeriesPaint(dataset.indexOf(series), new Color(150,150,150));
	}

}
