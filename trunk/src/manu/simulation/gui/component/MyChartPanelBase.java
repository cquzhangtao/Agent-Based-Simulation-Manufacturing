package manu.simulation.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;

public class MyChartPanelBase extends ChartPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4878668380494232427L;
	public LegendTitle legend;
	JMenuItem jmiLegend ;
	public MyChartPanelBase(JFreeChart chart,LegendTitle legend){
		super(chart);
		this.legend=legend;
		if(chart==null)
			return;
		Init(chart);
		if(chart.getLegend()!=null)
//			if(Locale.getDefault()==Locale.CHINESE)
//				jmiLegend.setText("Òþ²ØÍ¼Àý");
//			else if(Locale.getDefault()==Locale.GERMAN)
//				jmiLegend.setText("Hehlen Legende");
//			else
				jmiLegend.setText("Hide legend");
		else
//			if(Locale.getDefault()==Locale.CHINESE)
//				jmiLegend.setText("ÏÔÊ¾Í¼Àý");
//			else if(Locale.getDefault()==Locale.GERMAN)
//				jmiLegend.setText("Zeigen Legende");
//			else
				jmiLegend.setText("Show legend");

	}
	
	public MyChartPanelBase(JFreeChart chart) {
		super(chart);
		
		if(chart==null)
			return;
		//getLocale();
		legend=chart.getLegend();	
		chart.removeLegend();

		Init(chart);
		
	}

	private void Init(JFreeChart chart) {
		JComponent.setDefaultLocale(Locale.ENGLISH);
		Plot p=chart.getPlot();
		if(p.getPlotType().equals("XY Plot")||p.getPlotType().equals("XY Í¼")){
			XYPlot pl=(XYPlot) p;
			pl.setRangePannable(true);
			((XYPlot)p).setDomainPannable(true);
			
		}
		if(p.getPlotType().equals("Category Plot")||p.getPlotType().equals("Àà±ÈÍ¼")){
			((CategoryPlot)p).setRangePannable(true);
			//((CategoryPlot)p).setDomainPannable(true);
		}
		// TODO Auto-generated constructor stub
		JMenuItem jmi = getPopupMenu().add("Export data...");
		class myMenuActionListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Plot plot = MyChartPanelBase.this.getChart().getPlot();
				if (plot.getPlotType().equals("XY Plot")||plot.getPlotType().equals("XY Í¼")) {
					XYPlot xyplot = (XYPlot) plot;
					XYDataset dataset = xyplot.getDataset();
					if (dataset.getSeriesCount() < 1)
						return;
					JFileChooser fc = new JFileChooser(".//model");
					FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"Excel File (*.xls, *.xlsx)", "xls", "xlsx",
							"xlt");
					FileNameExtensionFilter filter1 = new FileNameExtensionFilter(
							"Text File (*.txt)", "txt");
					fc.addChoosableFileFilter(filter);
					fc.addChoosableFileFilter(filter1);
					fc.setFileFilter(filter);
					fc.setDialogType(JFileChooser.SAVE_DIALOG);

					int a = fc.showOpenDialog(MyChartPanelBase.this);
					if (a != JFileChooser.APPROVE_OPTION)
						return;
					File file = fc.getSelectedFile();
					if (file != null) {

						if (fc.getFileFilter().equals(filter)) {
							try {
								WritableWorkbook book;
								WritableSheet sheet = null;
								if (file.getPath().contains(".xls"))
									book = Workbook.createWorkbook(file);
								else
									book = Workbook
											.createWorkbook(new File(file
													.getPath() + ".xls"));
								sheet = book
										.createSheet(MyChartPanelBase.this
												.getChart().getTitle()
												.getText(), 0);
								int maxCountSeries = 0;
								int maxCount = 0;
								for (int i = 0; i < dataset
										.getSeriesCount(); i++) {
									if (dataset.getItemCount(i) > maxCount) {
										maxCount = dataset.getItemCount(i);
										maxCountSeries = i;
									}
								}
								for (int i = 0; i < dataset
										.getSeriesCount(); i++) {
									if (i == maxCountSeries) {
										String head = xyplot
												.getDomainAxis().getLabel();
										if (head != null) {
											Label label = new Label(0, 0,
													head);
											sheet.addCell(label);
										}
										jxl.write.Number numberz = new jxl.write.Number(
												0, 1, 0);
										sheet.addCell(numberz);
									}
									Label label = new Label(i + 1, 0,
											dataset.getSeriesKey(i)
													.toString());
									sheet.addCell(label);
									jxl.write.Number numberz = new jxl.write.Number(
											i + 1, 1, 0.000);
									sheet.addCell(numberz);
									for (int j = 0; j < dataset
											.getItemCount(i); j = j + 2) {
										if (i == maxCountSeries) {
											Number n = dataset.getX(i, j);
											if (n != null
													&& !n.toString()
															.equals("NaN")) {
												jxl.write.Number number = new jxl.write.Number(
														0, j / 2 + 2,
														n.floatValue() );
												sheet.addCell(number);

											}
										}
										Number n = dataset.getY(i, j);
										if (n != null
												&& !n.toString().equals(
														"NaN")) {
											jxl.write.Number number = new jxl.write.Number(
													i + 1, j / 2 + 2,
													n.floatValue() );

											sheet.addCell(number);

										}

									}

								}

								book.write();
								book.close();
								JOptionPane.showMessageDialog(null,
										"Export data sucessfully!");

							} catch (Exception e1) {
								JOptionPane.showMessageDialog(null,
										"Export data unsucessfully!");
							}

						} else if (fc.getFileFilter().equals(filter1)) {
							JOptionPane.showMessageDialog(null,
									"Please export to excel file!");
						} else
							JOptionPane.showMessageDialog(null,
									"Please export to excel file!");
						return;
					} else {
						return;
					}

				}
			}

		}

		jmi.addActionListener(new myMenuActionListener());
		
		jmiLegend = getPopupMenu().add("Show legend");
		class myLMenuActionListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JMenuItem j=(JMenuItem)(e.getSource());
				if(j.getText().equals("Show legend")){
				MyChartPanelBase.this.getChart().addLegend(legend);
				j.setText("Hide legend");
				}
				else{
					MyChartPanelBase.this.getChart().removeLegend();
					//MyChartPanel.this.updateUI();
					j.setText("Show legend");
				}
			}
		}
		
		jmiLegend.addActionListener(new myLMenuActionListener());
	}

}
