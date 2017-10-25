package manu.simulation.gui.component;

import javax.swing.JPanel;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;



public class MyChartPanel extends MyChartPanelBase {
				
		/**
	 * 
	 */
	private static final long serialVersionUID = 5791844509584933188L;

		public MyChartPanel(JFreeChart chart) {
			super(chart);
			//=new LegendTitle();
			//chart.addLegend(legend);

		
			// TODO Auto-generated constructor stub
			this.addChartMouseListener(new ChartMouseListener() {

				@Override
				public void chartMouseClicked(ChartMouseEvent event) {
					// TODO Auto-generated method stub
					if (event.getTrigger().getClickCount() == 2) {

						JPanel mainPanel=(JPanel) MyChartPanel.this.getParent().getParent().getParent();
						
						BigPanel bigPanel = new BigPanel(MyChartPanel.this
								.getChart(),MyChartPanel.this.legend);
						bigPanel.prePanel = MyChartPanel.this.getParent()
								.getParent();
						bigPanel.preChartPanel=MyChartPanel.this;
						bigPanel.setVisible(true);
						bigPanel.setBounds(mainPanel.getSize().width / 5 + 2,
								0, mainPanel.getSize().width / 5 * 4,
								mainPanel.getSize().height);

						mainPanel.add(bigPanel, 0);
						MyChartPanel.this.getParent().setVisible(false);
						// tabbedPane1.setVisible(false);
//						toolTabbedPane.setVisible(false);
//						jobTabbedPane.setVisible(false);
//						productTabbedPane.setVisible(false);
						//2 is tree panel
						for(int i=2;i<mainPanel.getComponentCount();i++)
							mainPanel.getComponent(i).setVisible(false);

					}

				}

				@Override
				public void chartMouseMoved(ChartMouseEvent event) {
					// TODO Auto-generated method stub

				}
			});
		}
	}