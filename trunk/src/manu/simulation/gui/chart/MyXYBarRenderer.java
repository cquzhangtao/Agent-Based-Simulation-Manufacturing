package manu.simulation.gui.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.XYDataset;

public class MyXYBarRenderer extends XYBarRenderer {

	private static final long serialVersionUID = 1;
	protected XYDataset dataset;

	@Override
	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass) {
		this.dataset = dataset;
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
				dataset, series, item, crosshairState, pass);

	}

	@Override
	public void drawItemLabel(java.awt.Graphics2D g2, XYDataset dataset,
			int series, int item, XYPlot plot, XYItemLabelGenerator generator,
			java.awt.geom.Rectangle2D bar, boolean negative) {
		if(bar.getWidth()>50){
		
		super.drawItemLabel(g2, dataset,
				series, item, plot, generator,
				bar, negative);}
	}

	@Override
	public Paint getItemPaint(int row, int column) {
		Paint result;
		XYTaskDataset tds = (XYTaskDataset) dataset;
		TaskSeriesCollection tsc = tds.getTasks();
		TaskSeries ts = tsc.getSeries(row);
		Task t = ts.get(column);
		result = getCategoryPaint(t.getDescription());

		return result;
	}

	private Paint getCategoryPaint(String description) {
		Paint result = Color.black;
		if (description.equals("Transporting")) {
			result = Color.gray;
		} else if (description.equals("Waiting")) {
			result = Color.blue;
		} else if (description.equals("Processing")) {
			result = Color.green;
		} else if (description.equals("Blocking")) {
			result = Color.yellow;

		} else if (description.equals("Setup")) {
			result = Color.blue;
		} else if (description.equals("Interrupted")) {
			result = Color.red;
		}

		// logger.debug(description);
		return result;
	}
}