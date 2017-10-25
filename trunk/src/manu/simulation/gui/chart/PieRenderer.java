package manu.simulation.gui.chart;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class PieRenderer {
	private Color[] color;

	public PieRenderer(Color[] color) {
		this.color = color;
	}

	public void setColor(PiePlot plot, DefaultPieDataset dataset) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Comparable> keys = dataset.getKeys();
		int aInt;

		for (int i = 0; i < keys.size(); i++) {
			aInt = i % color.length;
			plot.setSectionPaint(keys.get(i), color[aInt]);
		}
	}
}