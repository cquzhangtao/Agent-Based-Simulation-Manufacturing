package manu.simulation.gui.component;

import java.awt.Container;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;



class BigPanel extends MyChartPanelBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Container prePanel;
	protected MyChartPanel preChartPanel;

	public BigPanel(JFreeChart chart, LegendTitle legend) {
		
		super(chart,legend);
		// TODO Auto-generated constructor stub
		this.addChartMouseListener(new ChartMouseListener(){

			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				// TODO Auto-generated method stub
				if(event.getTrigger().getClickCount()==2){
					
					BigPanel.this.setVisible(false);
					BigPanel.this.getParent().remove(BigPanel.this);						
					prePanel.setVisible(true);	
					if(BigPanel.this.getChart().getLegend()!=null)
						preChartPanel.jmiLegend.setText("Hide legend");
					else
						preChartPanel.jmiLegend.setText("Show legend");
				}
				
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				// TODO Auto-generated method stub
				
			}});
		
	}

	
	


	
}