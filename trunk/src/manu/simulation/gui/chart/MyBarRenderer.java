package manu.simulation.gui.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;

public class MyBarRenderer extends BarRenderer {
	// Logger logger = Logger.getLogger(MyXYBarRenderer.class);

	private static final long serialVersionUID = 1;
	protected CategoryDataset dataset;
	BarRenderer oldRenderer;
	public MyBarRenderer(BarRenderer oldRenderer){
		this.setBaseToolTipGenerator(oldRenderer.getBaseToolTipGenerator());
		
	}
//	public  MyBarRenderer(){
//		
//	}

	@Override
	public void drawItem(Graphics2D g2, CategoryItemRendererState state,
			Rectangle2D dataArea, CategoryPlot plot,
			CategoryAxis domainAxis, ValueAxis rangeAxis,
			CategoryDataset dataset, int row, int column, int pass) {
		this.dataset = dataset;
		super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis,
				dataset, row, column, pass);
		
	}

	@Override
	public Paint getItemPaint(int row, int column) {
		String serial = (String) dataset.getRowKey(row);
		if (serial.equals("Average")) {
			return new Color(0xFF4500);
		} else if (serial.equals("Min")) {
			return new Color(0x1E90FF);
		} else if (serial.equals("Max")) {
			return new Color(0xCD3278);
		} else if (serial.equals("Raw")) {
			return new Color(0x96CDCD);
		
		} else if (serial.equals("Free")) {
			return Color.GRAY;
		} else if (serial.equals("Setup")) {
			return  new Color(0x4682B4 );
		} else if (serial.equals("Process")) {
			return Color.green;
		} else if (serial.equals("Block")) {
			return Color.yellow;
		} else if (serial.equals("Breakdown")) {
			return new Color(0xB22222);
		} else if (serial.equals("Maintenance")) {
			return  Color.pink;
		}


		return Color.blue;
	}

}