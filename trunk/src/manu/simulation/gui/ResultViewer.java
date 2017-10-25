package manu.simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import manu.simulation.gui.chart.MyBarRenderer;
import manu.simulation.gui.chart.MyXYBarRenderer;
import manu.simulation.gui.component.MyChartPanel;
import manu.simulation.gui.component.MyJCheckBox;
import manu.simulation.gui.component.MyNode;
import manu.simulation.gui.report.ReportInfo;
import manu.simulation.gui.report.ReportPanel;
import manu.simulation.result.data.BufferData;
import manu.simulation.result.data.BufferDataset;
import manu.simulation.result.data.JobData;
import manu.simulation.result.data.JobDataset;
import manu.simulation.result.data.JobSubData;
import manu.simulation.result.data.ReleaseDataset;
import manu.simulation.result.data.StartEnd;
import manu.simulation.result.data.ToolData;
import manu.simulation.result.data.ToolGroupData;
import manu.simulation.result.data.ToolGroupDataset;
import manu.simulation.result.data.WaitDataset;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ResultViewer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ReleaseDataset[] releaseDataset;
	ToolGroupDataset[] toolGroupDataset;
	BufferDataset[] bufferDataset;
	JobDataset[] jobDataset;
	String[] modelName;
	int times = 1;
	ChartPanel chartPanel;
	JFreeChart jobGanntChart;
	XYPlot tWipPlot;
	XYPlot tReleasePlot;
	XYPlot tFinishedLotsPlot;
	XYPlot tCycleTimePlot;
	JTabbedPane tabbedPane1;
	XYPlot jobGanntPlot;
	final JTabbedPane productTabbedPane;
	final JTabbedPane jobTabbedPane;
	final JTabbedPane toolTabbedPane;
	// BigPanel bigPanel;
	CategoryPlot sWipPlot;
	CategoryPlot sReleasePlot;
	CategoryPlot sFinishedLotsPlot;
	CategoryPlot sCycleTimePlot;
	JTree productTree;
	JTree toolTree;
	JTree jobTree;
	JPanel mainPanel;
	MyChartPanel oneToolPanel;
	MyChartPanel multiToolPanel;
	XYPlot tBufferPlot;
	CategoryPlot sBufferPlot;
	XYPlot tWaitTimePlot;
	CategoryPlot sWaitTimePlot;
	XYPlot tUtiPlot;
	PiePlot sUtiPlot;
	CategoryPlot sUtiPlotZ;
	XYPlot tInterruptPlot;
	CategoryPlot sInterruptPlot;
	String[] selectedProduct;
	ArrayList<String> selectedToolGroup = new ArrayList<String>();
	ArrayList<String> selectedToolGroupU = new ArrayList<String>();
	ArrayList<Integer> selectedToolGroupT = new ArrayList<Integer>();
	ArrayList<String> selectedTool = new ArrayList<String>();
	ArrayList<Integer> selectedJobP = new ArrayList<Integer>();
	ArrayList<Integer> selectedJob = new ArrayList<Integer>();
	int[] wipStatFlag = new int[] { 1, 1 };
	int[] cycleTimeStatFlag = new int[] { 1, 1, 1, 1 };
	int[] bufferStatFlag = new int[] { 1, 1 };
	int[] waitTimeStatFlag = new int[] { 1, 1 };
	int[] toolUtiStatFlag = new int[] { 1, 0, 1, 0, 1, 1 };
	String[] toolName;
	private XYPlot toolGanntPlot;

	public ResultViewer(String[] modelName, ReportInfo reportInfo,
			ReleaseDataset[] releaseDataset,
			ToolGroupDataset[] toolGroupDataset, BufferDataset[] bufferDataset,
			JobDataset[] jobDataset) {
		this.releaseDataset = releaseDataset;
		this.toolGroupDataset = toolGroupDataset;
		this.bufferDataset = bufferDataset;
		this.jobDataset = jobDataset;
		this.modelName = modelName;
		times = jobDataset.length;
		this.setLayout(null);
		this.setTitle("Simulation Result");
		setState(Frame.NORMAL);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		setSize(dimension);

		// this.setLocationRelativeTo(null);
		// this.setSize(1010, 610);
		this.centerWindow(this);
		mainPanel = new JPanel();
		mainPanel.setLocation(5, 5);
		mainPanel
				.setSize(this.getSize().width - 30, this.getSize().height - 50);
		mainPanel.setLayout(null);
		this.getContentPane().add(mainPanel);

		tabbedPane1 = new JTabbedPane();
		tabbedPane1.setBounds(0, 0, mainPanel.getSize().width / 5 - 2,
				mainPanel.getSize().height);

		mainPanel.add(tabbedPane1);

		createProductTree();
		JScrollPane scrollpane = new JScrollPane(productTree);
		scrollpane.setBounds(0, 0, tabbedPane1.getSize().width - 2,
				tabbedPane1.getSize().height);
		tabbedPane1.addTab("Product", scrollpane);

		productTree.setBounds(0, 0, scrollpane.getSize().width - 2,
				scrollpane.getSize().height);

		createToolTree();
		scrollpane = new JScrollPane(toolTree);
		scrollpane.setBounds(0, 0, tabbedPane1.getSize().width - 2,
				tabbedPane1.getSize().height);
		tabbedPane1.addTab("Tool", scrollpane);
		toolTree.setBounds(0, 0, scrollpane.getSize().width - 2,
				scrollpane.getSize().height);

		createJobTree();
		scrollpane = new JScrollPane(jobTree);
		scrollpane.setBounds(0, 0, tabbedPane1.getSize().width - 2,
				tabbedPane1.getSize().height);
		tabbedPane1.addTab("Tool", scrollpane);
		jobTree.setBounds(0, 0, scrollpane.getSize().width - 2,
				scrollpane.getSize().height);
		tabbedPane1.addTab("Job", scrollpane);
		mainPanel.add(tabbedPane1);

		toolTabbedPane = new JTabbedPane();
		toolTabbedPane.setBounds(mainPanel.getSize().width / 5 + 2, 0,
				mainPanel.getSize().width / 5 * 4, mainPanel.getSize().height);
		toolTabbedPane.setVisible(false);
		mainPanel.add(toolTabbedPane);
		ReportPanel report1 = new ReportPanel(reportInfo.toolResultFile,
				reportInfo.toolJasperReport, ".", reportInfo.subReportPath);

		report1.setBounds(0, 0, toolTabbedPane.getWidth() - 5,
				toolTabbedPane.getHeight() - 30);
		toolTabbedPane.addTab("Report", report1);

		JPanel toolTrend = new JPanel();
		toolTrend.setLayout(new GridLayout(2, 2));
		toolTabbedPane.addTab("Trend", toolTrend);
		JPanel toolStat = new JPanel();
		toolStat.setLayout(null);
		toolStat.setBounds(0, 0, toolTabbedPane.getWidth() - 5,
				toolTabbedPane.getHeight() - 30);
		toolTabbedPane.addTab("Statistics", toolStat);
		JPanel toolGannt = new JPanel();
		toolGannt.setLayout(null);
		toolGannt.setBounds(0, 0, toolTabbedPane.getWidth() - 5,
				toolTabbedPane.getHeight() - 30);
		toolTabbedPane.addTab("Gannt Chart", toolGannt);
		// bigPanel=new BigPanel();
		// bigPanel.setBounds(mainPanel.getSize().width / 5 + 2, 0,
		// mainPanel.getSize().width / 5 * 4, mainPanel.getSize().height);
		//
		// bigPanel.setVisible(false);
		// mainPanel.add(bigPanel);

		JFreeChart tBufferChart = ChartFactory
				.createXYLineChart(null, "Time(" + reportInfo.periodStr + ")",
						"Buffer size(lots)", getBufferDataset(),
						PlotOrientation.VERTICAL, true, true, false);
		tBufferChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Buffer Size", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		tBufferPlot = (XYPlot) tBufferChart.getPlot();

		NumberAxis range = (NumberAxis) tBufferPlot.getRangeAxis();
		range.setRange(0, bufferDataset[0].allBufferData.maxBufferSize + 2);
		// range.setTickUnit(new
		// NumberTickUnit((int)(bufferDataset.allBufferData.maxBufferSize/10)));
		MyChartPanel chartPanel = new MyChartPanel(tBufferChart);
		toolTrend.add(chartPanel);

		JFreeChart tUtiChart = ChartFactory.createXYLineChart(null, "Time("
				+ reportInfo.periodStr + ")", "Utilization(%)",
				getUtiDataset(), PlotOrientation.VERTICAL, true, true, false);
		tUtiChart.setTitle(new org.jfree.chart.title.TextTitle("Utilization",
				new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)));
		tUtiPlot = (XYPlot) tUtiChart.getPlot();
		range = (NumberAxis) tUtiPlot.getRangeAxis();
		range.setRange(0, 1);
		range.setTickUnit(new NumberTickUnit(0.1));
		chartPanel = new MyChartPanel(tUtiChart);
		toolTrend.add(chartPanel);

		JFreeChart tWaitTimeChart = ChartFactory.createXYLineChart(null,
				"Time(" + reportInfo.periodStr + ")", "Wait Time (Hour)",
				getWaitTimeDataset(), PlotOrientation.VERTICAL, true, true,
				false);
		tWaitTimeChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Wait Time", new java.awt.Font("SansSerif", java.awt.Font.BOLD,
						14)));
		tWaitTimePlot = (XYPlot) tWaitTimeChart.getPlot();
		range = (NumberAxis) tWaitTimePlot.getRangeAxis();
		range.setRange(0, toolGroupDataset[0].allWaitDataset.maxWaitTime);
		range.setTickUnit(new NumberTickUnit(
				(int) (toolGroupDataset[0].allWaitDataset.maxWaitTime / 10)));
		chartPanel = new MyChartPanel(tWaitTimeChart);
		toolTrend.add(chartPanel);

		JFreeChart tInterruptChart = ChartFactory.createXYLineChart(null,
				"Time(" + reportInfo.periodStr + ")", "Interrupt", getInterruptDataset(),
				PlotOrientation.VERTICAL, true, true, false);
		tInterruptChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Interruption", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		tInterruptPlot = (XYPlot) tInterruptChart.getPlot();
		chartPanel = new MyChartPanel(tInterruptChart);
		chartPanel.setRangeZoomable(false);
		tInterruptPlot.setRangePannable(false);
		toolTrend.add(chartPanel);

		JFreeChart sBufferChart = ChartFactory.createBarChart(null, "Buffer",
				"Buffer Size (lots)", getBufferDatasetS(),
				PlotOrientation.VERTICAL, true, true, false);
		sBufferChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Buffer Size", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));

		sBufferPlot = sBufferChart.getCategoryPlot();
		sBufferPlot.setBackgroundPaint(Color.lightGray);
		sBufferPlot.setDomainGridlinePaint(Color.white);
		sBufferPlot.setRangeGridlinePaint(Color.white);
		range = (NumberAxis) sBufferPlot.getRangeAxis();
		range.setRange(0, bufferDataset[0].allBufferData.maxBufferSize + 2);
		// range.setTickUnit(new
		// NumberTickUnit((int)(bufferDataset.allBufferData.maxBufferSize/10)));
		// set the range axis to display integers only...
		// final NumberAxis rangeAxis = (NumberAxis) barplot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		sBufferPlot.setRenderer(new MyBarRenderer((BarRenderer) sBufferPlot
				.getRenderer()));
		BarRenderer renderer = (BarRenderer) sBufferPlot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);

		chartPanel = new MyChartPanel(sBufferChart);

		chartPanel.setBounds(0, 0, (toolStat.getWidth() - 5) / 2,
				(toolStat.getHeight() - 5) / 2);
		chartPanel.add(new Lenged(chartPanel, new String[] { "Avg", "Max" },
				bufferStatFlag, "bufferSize"));
		chartPanel.setLayout(null);
		toolStat.add(chartPanel);

		// JFreeChart sUtiChart = ChartFactory.createPieChart(null,
		// getUtiDatasetP(), true, true, false);
		// sUtiChart.setTitle(new org.jfree.chart.title.TextTitle("Utilization",
		// new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)));
		// sUtiPlot = (PiePlot) sUtiChart.getPlot();
		// sUtiPlot.setLabelLinkStyle(PieLabelLinkStyle.QUAD_CURVE);
		// sUtiPlot.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
		// sUtiPlot.setNoDataMessage("No data available");

		// sUtiPlot.setCircular(false);
		// sUtiPlot.setLabelGap(0.02);
		// Specify the colors here

		oneToolPanel = new MyChartPanel(null);
		oneToolPanel.setBounds((toolStat.getWidth() - 5) / 2 + 30, 5,
				(toolStat.getWidth() - 5) / 2 - 50,
				(toolStat.getHeight() - 5) / 2 - 50);
		oneToolPanel.setVisible(false);
		toolStat.setBackground(Color.white);
		toolStat.add(oneToolPanel);

		JFreeChart sUtiChartZ = ChartFactory.createBarChart(null, "Tool",
				"Ratio(%)", getUtiDatasetAll(), PlotOrientation.VERTICAL, true,
				true, false);
		sUtiChartZ.setTitle(new org.jfree.chart.title.TextTitle("Utilization",
				new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)));
		sUtiPlotZ = sUtiChartZ.getCategoryPlot();
		range = (NumberAxis) sUtiPlotZ.getRangeAxis();
		range.setRange(0, 100);
		range.setTickUnit(new NumberTickUnit(10));

		sUtiPlotZ.setBackgroundPaint(Color.lightGray);
		sUtiPlotZ.setDomainGridlinePaint(Color.white);
		sUtiPlotZ.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		// final NumberAxis rangeAxis = (NumberAxis) barplot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		sUtiPlotZ.setRenderer(new MyBarRenderer((BarRenderer) sUtiPlotZ
				.getRenderer()));
		renderer = (BarRenderer) sUtiPlotZ.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);

		multiToolPanel = new MyChartPanel(sUtiChartZ);

		multiToolPanel.setBounds((toolStat.getWidth() - 5) / 2, 0,
				(toolStat.getWidth() - 5) / 2, (toolStat.getHeight() - 5) / 2);
		multiToolPanel.add(new Lenged(multiToolPanel, new String[] { "Free",
				"Setup", "Proc.", "Block", "Brea.", "Main." }, toolUtiStatFlag,
				"toolUti"));
		multiToolPanel.setLayout(null);
		multiToolPanel.setVisible(true);
		toolStat.add(multiToolPanel);

		JFreeChart sWaitTimeChart = ChartFactory.createBarChart(null,
				"Tool Group", "Wait Time (Hour)", getWaitTimeDatasetS(),
				PlotOrientation.VERTICAL, true, true, false);
		sWaitTimeChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Wait Time", new java.awt.Font("SansSerif", java.awt.Font.BOLD,
						14)));
		sWaitTimePlot = sWaitTimeChart.getCategoryPlot();

		sWaitTimePlot.setBackgroundPaint(Color.lightGray);
		sWaitTimePlot.setDomainGridlinePaint(Color.white);
		sWaitTimePlot.setRangeGridlinePaint(Color.white);
		range = (NumberAxis) sWaitTimePlot.getRangeAxis();
		range.setRange(0, toolGroupDataset[0].allWaitDataset.maxWaitTime);
		range.setTickUnit(new NumberTickUnit(
				(int) toolGroupDataset[0].allWaitDataset.maxWaitTime / 10));

		// set the range axis to display integers only...
		// final NumberAxis rangeAxis = (NumberAxis) barplot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		sWaitTimePlot.setRenderer(new MyBarRenderer((BarRenderer) sWaitTimePlot
				.getRenderer()));
		renderer = (BarRenderer) sWaitTimePlot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);
		chartPanel = new MyChartPanel(sWaitTimeChart);
		chartPanel.setBounds(0, (toolStat.getHeight() - 5) / 2,
				(toolStat.getWidth() - 5) / 2, (toolStat.getHeight() - 5) / 2);
		chartPanel.add(new Lenged(chartPanel, new String[] { "Avg", "Max" },
				waitTimeStatFlag, "waitTime"));
		chartPanel.setLayout(null);
		toolStat.add(chartPanel);

		JFreeChart sInterruptChart = ChartFactory.createBarChart(null,
				"Tool Group", "Interruption Times", getInterruptDatasetS(),
				PlotOrientation.VERTICAL, true, true, false);
		sInterruptChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Interruption", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		sInterruptPlot = sInterruptChart.getCategoryPlot();

		sInterruptPlot.setBackgroundPaint(Color.lightGray);
		sInterruptPlot.setDomainGridlinePaint(Color.white);
		sInterruptPlot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		// final NumberAxis rangeAxis = (NumberAxis) barplot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...

		renderer = (BarRenderer) sInterruptPlot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);

		chartPanel = new MyChartPanel(sInterruptChart);

		chartPanel.setBounds((toolStat.getWidth() - 5) / 2,
				(toolStat.getHeight() - 5) / 2, (toolStat.getWidth() - 5) / 2,
				(toolStat.getHeight() - 5) / 2);
		// chartPanel.add(new Lenged(chartPanel,new
		// String[]{"Avg","Max"},bufferStatFlag,"bufferSize"));
		chartPanel.setLayout(null);
		toolStat.add(chartPanel);

		productTabbedPane = new JTabbedPane();
		productTabbedPane.setBounds(mainPanel.getSize().width / 5 + 2, 0,
				mainPanel.getSize().width / 5 * 4, mainPanel.getSize().height);
		mainPanel.add(productTabbedPane);

		ReportPanel report = new ReportPanel(reportInfo.productResultFile,
				reportInfo.productJasperReport, "/Products/Product",
				reportInfo.subReportPath);

		report.setBounds(0, 0, productTabbedPane.getWidth() - 5,
				productTabbedPane.getHeight() - 30);
		productTabbedPane.addTab("Report", report);
		JPanel productTrend = new JPanel();
		productTrend.setLayout(new GridLayout(2, 2));
		productTabbedPane.addTab("Trend", productTrend);
		JPanel productStat = new JPanel();
		productStat.setLayout(null);
		productStat.setBounds(0, 0, productTabbedPane.getWidth() - 5,
				productTabbedPane.getHeight() - 30);
		productTabbedPane.addTab("Statistics", productStat);

		jobTabbedPane = new JTabbedPane();
		jobTabbedPane.setBounds(mainPanel.getSize().width / 5 + 2, 0,
				mainPanel.getSize().width / 5 * 4, mainPanel.getSize().height);
		mainPanel.add(jobTabbedPane);
		report = new ReportPanel(reportInfo.summaryResultFile,
				reportInfo.summaryJasperReport, "/Simulation",
				reportInfo.subReportPath);
		report.setBounds(0, 0, jobTabbedPane.getWidth() - 5,
				jobTabbedPane.getHeight() - 30);
		jobTabbedPane.addTab("Report", report);
		JPanel jobTrend = new JPanel();
		jobTrend.setLayout(null);
		jobTrend.setBounds(0, 0, jobTabbedPane.getWidth() - 5,
				jobTabbedPane.getHeight() - 30);

		jobTabbedPane.addTab("Gannt", jobTrend);
		JPanel jobStat = new JPanel();
		jobStat.setLayout(null);
		jobStat.setBounds(0, 0, jobTabbedPane.getWidth() - 5,
				jobTabbedPane.getHeight() - 30);
		jobTabbedPane.addTab("Statistics", jobStat);

		tabbedPane1.addChangeListener(new ChangeListener() {
			// This method is called whenever the selected tab changes
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane pane = (JTabbedPane) evt.getSource();

				// Get current tab
				int sel = pane.getSelectedIndex();
				if (sel == 0) {
					productTabbedPane.setVisible(true);
					toolTabbedPane.setVisible(false);
					jobTabbedPane.setVisible(false);
				} else if (sel == 1) {
					productTabbedPane.setVisible(false);
					toolTabbedPane.setVisible(true);
					jobTabbedPane.setVisible(false);

				} else {
					productTabbedPane.setVisible(false);
					toolTabbedPane.setVisible(false);
					jobTabbedPane.setVisible(true);
				}
				if (mainPanel.getComponentCount() == 5) {
					mainPanel.remove(0);
				}
			}
		});

		JFreeChart tReleaseChart = ChartFactory.createXYLineChart(null, "Time("
				+ reportInfo.periodStr + ")", "Released  Lot(lots)",
				getReleaseDataset(null), PlotOrientation.VERTICAL, true, true,
				false);
		tReleasePlot = tReleaseChart.getXYPlot();
		tReleaseChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Release Rate", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		// range = (NumberAxis) tReleasePlot.getRangeAxis();
		// range.setRange(0, releaseDataset.allReleaseDataset.);
		// range.setTickUnit(new NumberTickUnit(5));
		chartPanel = new MyChartPanel(tReleaseChart);
		productTrend.add(chartPanel);

		JFreeChart tFinishedLotsChart = ChartFactory.createXYLineChart(null,
				"Time(" + reportInfo.periodStr + ")", "Finised Lot (lots)",
				getFinishedLotsDataset(null), PlotOrientation.VERTICAL, true,
				true, false);
		tFinishedLotsChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Productivity", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		tFinishedLotsPlot = tFinishedLotsChart.getXYPlot();
		chartPanel = new MyChartPanel(tFinishedLotsChart);
		productTrend.add(chartPanel);

		JFreeChart tWipChart = ChartFactory.createXYLineChart(null, "Time("
				+ reportInfo.periodStr + ")", "WIP Level(lots)",
				getWipDataset(null), PlotOrientation.VERTICAL, true, true,
				false);
		tWipChart.setTitle(new org.jfree.chart.title.TextTitle("WIP Level",
				new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)));
		tWipPlot = tWipChart.getXYPlot();

		chartPanel = new MyChartPanel(tWipChart);
		productTrend.add(chartPanel);

		JFreeChart tCycleTimeChart = ChartFactory.createXYLineChart(
				releaseDataset[0].get(0).product, "Time("
						+ reportInfo.periodStr + ")", "Cycle Time (Hour)",
				getCycleTimeDataset(null), PlotOrientation.VERTICAL, true,
				true, false);
		tCycleTimeChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Cycle Time", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		tCycleTimePlot = tCycleTimeChart.getXYPlot();
		chartPanel = new MyChartPanel(tCycleTimeChart);
		productTrend.add(chartPanel);

		JFreeChart sReleaseChart = ChartFactory.createBarChart(null, "Product",
				"Released Lots (lot)", getReleaseDataset(releaseDataset, null),
				PlotOrientation.VERTICAL, true, true, false);
		sReleaseChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Released Lot", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		sReleasePlot = sReleaseChart.getCategoryPlot();
		sReleasePlot.setBackgroundPaint(Color.lightGray);
		sReleasePlot.setDomainGridlinePaint(Color.white);
		sReleasePlot.setRangeGridlinePaint(Color.white);
		// set the range axis to display integers only...
		NumberAxis rangeAxis = (NumberAxis) sReleasePlot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// disable bar outlines...
		renderer = (BarRenderer) sReleasePlot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);
		chartPanel = new MyChartPanel(sReleaseChart);
		chartPanel.setBounds(0, 0, (productStat.getWidth() - 5) / 2,
				(productStat.getHeight() - 5) / 2);
		chartPanel.setLayout(null);
		productStat.add(chartPanel);
		// stat.add(new Lenged(chartPanel));

		JFreeChart sFinishedLotsChart = ChartFactory.createBarChart(null,
				"Product", "Released Lots (lot)",
				getFinishedLotsDataset(releaseDataset, null),
				PlotOrientation.VERTICAL, true, true, false);
		sFinishedLotsChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Finished Lots", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		sFinishedLotsPlot = sFinishedLotsChart.getCategoryPlot();
		sFinishedLotsPlot.setBackgroundPaint(Color.lightGray);
		sFinishedLotsPlot.setDomainGridlinePaint(Color.white);
		sFinishedLotsPlot.setRangeGridlinePaint(Color.white);
		// set the range axis to display integers only...
		rangeAxis = (NumberAxis) sFinishedLotsPlot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// disable bar outlines...
		renderer = (BarRenderer) sFinishedLotsPlot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);
		chartPanel = new MyChartPanel(sFinishedLotsChart);
		chartPanel.setBounds((productStat.getWidth() - 5) / 2, 0,
				(productStat.getWidth() - 5) / 2,
				(productStat.getHeight() - 5) / 2);
		chartPanel.setLayout(null);
		productStat.add(chartPanel);

		JFreeChart sWipChart = ChartFactory.createBarChart(null, "Product",
				"WIP Level (lot)", getWipDataset(releaseDataset, null),
				PlotOrientation.VERTICAL, true, true, false);
		sWipChart.setTitle(new org.jfree.chart.title.TextTitle("WIP Level",
				new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)));
		sWipPlot = sWipChart.getCategoryPlot();
		sWipPlot.setBackgroundPaint(Color.lightGray);
		sWipPlot.setDomainGridlinePaint(Color.white);
		sWipPlot.setRangeGridlinePaint(Color.white);
		// set the range axis to display integers only...
		rangeAxis = (NumberAxis) sWipPlot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// disable bar outlines...
		sWipPlot.setRenderer(new MyBarRenderer((BarRenderer) sWipPlot
				.getRenderer()));
		renderer = (BarRenderer) sWipPlot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);
		chartPanel = new MyChartPanel(sWipChart);
		chartPanel.setBounds(0, (productStat.getHeight() - 5) / 2,
				(productStat.getWidth() - 5) / 2,
				(productStat.getHeight() - 5) / 2);
		chartPanel.setLayout(null);
		chartPanel.add(new Lenged(chartPanel, new String[] { "Avg", "Max" },
				wipStatFlag, "wip"));
		productStat.add(chartPanel);

		JFreeChart sCycleTimeChart = ChartFactory.createBarChart(null,
				"Product", "Cycle Time (Hour)",
				getCycleTimeDataset(releaseDataset, null),
				PlotOrientation.VERTICAL, true, true, false);
		sCycleTimeChart.setTitle(new org.jfree.chart.title.TextTitle(
				"Cycle Time", new java.awt.Font("SansSerif",
						java.awt.Font.BOLD, 14)));
		sCycleTimePlot = sCycleTimeChart.getCategoryPlot();
		sCycleTimePlot.setBackgroundPaint(Color.lightGray);
		sCycleTimePlot.setDomainGridlinePaint(Color.white);
		sCycleTimePlot.setRangeGridlinePaint(Color.white);
		// set the range axis to display integers only...
		rangeAxis = (NumberAxis) sCycleTimePlot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// disable bar outlines...
		sCycleTimePlot.setRenderer(new MyBarRenderer(
				(BarRenderer) sCycleTimePlot.getRenderer()));
		renderer = (BarRenderer) sCycleTimePlot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.1);
		chartPanel = new MyChartPanel(sCycleTimeChart);
		chartPanel.setBounds((productStat.getWidth() - 5) / 2,
				(productStat.getHeight() - 5) / 2,
				(productStat.getWidth() - 5) / 2,
				(productStat.getHeight() - 5) / 2);
		chartPanel.setLayout(null);
		chartPanel.add(new Lenged(chartPanel, new String[] { "Min", "Avg",
				"Max", "Raw" }, cycleTimeStatFlag, "cycleTime"));
		productStat.add(chartPanel);

		String jobname = "";

		XYTaskDataset dataset = null;
		if (jobDataset != null && jobDataset[0].size() > 0
				&& jobDataset[0].get(0).get(0) != null) {
			// collection = getJobGanntDataset(0,0);
			jobname = jobDataset[0].get(0).get(0).jobName;
			dataset = getJobGanntDataset();

		} else {
			toolName = new String[] { "" };
		}

		jobGanntChart = ChartFactory.createXYBarChart(jobname, "Tools", false,
				"Time", dataset, PlotOrientation.HORIZONTAL, true, true, false);
		jobGanntChart.setBackgroundPaint(Color.white);
		jobGanntPlot = jobGanntChart.getXYPlot();

		SymbolAxis localSymbolAxis = new SymbolAxis("Tools", toolName);
		localSymbolAxis.setGridBandsVisible(false);
		jobGanntPlot.setDomainAxis(localSymbolAxis);
		jobGanntPlot.setRenderer(new MyXYBarRenderer());
		XYBarRenderer localXYBarRenderer = (XYBarRenderer) jobGanntPlot
				.getRenderer();
		localXYBarRenderer.setUseYInterval(true);
		localXYBarRenderer.setMargin(0.8);
		// localXYBarRenderer.setBarPainter(new myXYBarPainer());
		jobGanntPlot.setRangeAxis(new DateAxis("Time"));

		XYToolTipGenerator tgdd = new XYToolTipGenerator() {

			@Override
			public String generateToolTip(XYDataset arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				XYTaskDataset dataset = (XYTaskDataset) arg0;
				Task task = dataset.getTasks().getSeries(arg1).get(arg2);
				DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
				String str = task.getDescription() + " "
						+ df.format(task.getDuration().getStart()) + "-"
						+ df.format(task.getDuration().getEnd());
				return str;
			}

		};
		localXYBarRenderer.setBaseToolTipGenerator(tgdd);
		XYItemLabelGenerator a = new XYItemLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				XYTaskDataset dataset = (XYTaskDataset) arg0;
				MyTask task = (MyTask) dataset.getTasks().getSeries(arg1)
						.get(arg2);
				String str = task.getJobs();
				return str;
			}

		};
		localXYBarRenderer.setBaseItemLabelGenerator(a);
		localXYBarRenderer.setBaseItemLabelsVisible(true);
		ChartUtilities.applyCurrentTheme(jobGanntChart);

		chartPanel = new MyChartPanel(jobGanntChart);
		chartPanel.setDomainZoomable(false);
		jobGanntPlot.setDomainPannable(false);
		chartPanel.setBounds(0, 0, jobTrend.getWidth(), jobTrend.getHeight());
		jobTrend.add(chartPanel);

		// tool gantt
		ArrayList<String> tools = new ArrayList<String>();
		dataset = getToolGanntDataset(tools);

		JFreeChart toolGanntChart = ChartFactory.createXYBarChart(null,
				"Tools", true, "Time", dataset, PlotOrientation.HORIZONTAL,
				false, true, false);
		toolGanntChart.setBackgroundPaint(Color.white);
		toolGanntPlot = toolGanntChart.getXYPlot();
		localSymbolAxis = new SymbolAxis("Tools", tools.toArray(new String[0]));
		localSymbolAxis.setGridBandsVisible(false);
		toolGanntPlot.setDomainAxis(localSymbolAxis);
		toolGanntPlot.setRenderer(new MyXYBarRenderer());
		localXYBarRenderer = (XYBarRenderer) toolGanntPlot.getRenderer();
		localXYBarRenderer.setUseYInterval(true);
		localXYBarRenderer.setMargin(0.8);
		localXYBarRenderer.setDrawBarOutline(true);
		localXYBarRenderer.setBaseOutlinePaint(Color.gray);
		// toolGanntPlot.get
		tgdd = new XYToolTipGenerator() {

			@Override
			public String generateToolTip(XYDataset arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				XYTaskDataset dataset = (XYTaskDataset) arg0;
				Task task = dataset.getTasks().getSeries(arg1).get(arg2);
				DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
				String str = task.getDescription() + " "
						+ df.format(task.getDuration().getStart()) + "-"
						+ df.format(task.getDuration().getEnd());
				return str;
			}

		};
		localXYBarRenderer.setBaseToolTipGenerator(tgdd);
		a = new XYItemLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				XYTaskDataset dataset = (XYTaskDataset) arg0;
				MyTask task = (MyTask) dataset.getTasks().getSeries(arg1)
						.get(arg2);
				String str = task.getJobs();
				return str;
			}

		};
		localXYBarRenderer.setBaseItemLabelGenerator(a);
		localXYBarRenderer.setBaseItemLabelsVisible(true);

		toolGanntPlot.setRangeAxis(new DateAxis("Time"));
		ChartUtilities.applyCurrentTheme(toolGanntChart);

		// jfreeganntchart
		// TaskSeriesCollection dataset = getToolGanntDataset();
		// JFreeChart toolGanntChart = ChartFactory
		// .createGanttChart(null, "Tools","Time", dataset, true, true, true);
		// toolGanntChart.setBackgroundPaint(Color.white);

		chartPanel = new MyChartPanel(toolGanntChart);
		chartPanel.setDomainZoomable(false);
		toolGanntPlot.setDomainPannable(false);
		chartPanel.setBounds(0, 0, toolGannt.getWidth(), toolGannt.getHeight());
		toolGannt.add(chartPanel);

		this.setVisible(true);

	}

	private XYDataset getBufferDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		if (selectedToolGroup.size() < 1) {
			for (int t = 0; t < times; t++) {
				for (int j = 0; j < 1/* bufferDataset[t].size() */; j++) {
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ bufferDataset[t].get(j).bufferName);
					for (int i = 0; i < bufferDataset[t].get(j).bufferSizebyDay
							.size(); i++) {
						series.add(i,
								bufferDataset[t].get(j).bufferSizebyDay.get(i));
						series.add(i + 1,
								bufferDataset[t].get(j).bufferSizebyDay.get(i));
					}
					dataset.addSeries(series);
				}
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroup.size(); i++) {
			for (int t = 0; t < times; t++) {
				BufferData data = bufferDataset[t]
						.getBufferData(selectedToolGroup.get(i));
				if (data == null)
					data = bufferDataset[t].allBufferData;
				XYSeries series = new XYSeries(modelName[t] + "_"
						+ data.bufferName);
				for (int j = 0; j < data.bufferSizebyDay.size(); j++) {
					series.add(j, data.bufferSizebyDay.get(j));
					series.add(j + 1, data.bufferSizebyDay.get(j));
				}
				dataset.addSeries(series);
			}
		}

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);

		return dataset;
	}

	private XYDataset getWaitTimeDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		if (selectedToolGroup.size() < 1) {
			for (int t = 0; t < times; t++) {
				for (int j = 0; j < 1/* j < toolGroupDataset[t].size() */; j++) {
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ toolGroupDataset[t].get(j).toolGroupName);
					WaitDataset waitDataset = toolGroupDataset[t].get(j).waitDataset;
					for (int i = 0; i < waitDataset.size(); i++) {
						series.add(i, waitDataset.waitDatasetbyDay.get(i));
						series.add(i + 1, waitDataset.waitDatasetbyDay.get(i));
					}
					dataset.addSeries(series);
				}
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroup.size(); i++) {
			for (int t = 0; t < times; t++) {
				WaitDataset waitDataset;
				if (toolGroupDataset[t].getToolGroupData(selectedToolGroup
						.get(i)) != null)
					waitDataset = toolGroupDataset[t]
							.getToolGroupData(selectedToolGroup.get(i)).waitDataset;
				else
					waitDataset = toolGroupDataset[t].allWaitDataset;
				XYSeries series = new XYSeries(modelName[t] + "_"
						+ waitDataset.bufferName);
				for (int j = 0; j < waitDataset.waitDatasetbyDay.size(); j++) {
					series.add(j, waitDataset.waitDatasetbyDay.get(j));
					series.add(j + 1, waitDataset.waitDatasetbyDay.get(j));
				}
				dataset.addSeries(series);
			}
		}

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);

		return dataset;
	}

	private XYDataset getUtiDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		if (selectedToolGroupU.size() < 1) {
			for (int t = 0; t < times; t++) {
				for (int j = 0; j < 1/* j < toolGroupDataset[t].size() */; j++) {
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ toolGroupDataset[t].get(j).toolGroupName);
					ToolData data = toolGroupDataset[t].get(j).toolGroupDataSum;
					for (int i = 0; i < data.utiliztionbyDay.size(); i++) {
						series.add(i, data.utiliztionbyDay.get(i));
						series.add(i + 1, data.utiliztionbyDay.get(i));
					}
					dataset.addSeries(series);
				}
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroupU.size(); i++) {
			for (int t = 0; t < times; t++) {
				if (selectedToolGroupT.get(i) == 0) {
					ToolData data = toolGroupDataset[t].allToolData;
					XYSeries series = new XYSeries(modelName[t] + "_" + "All");
					for (int j = 0; j < data.utiliztionbyDay.size(); j++) {
						series.add(j, data.utiliztionbyDay.get(j));
						series.add(j + 1, data.utiliztionbyDay.get(j));
					}
					dataset.addSeries(series);
				}
				if (selectedToolGroupT.get(i) == 1) {
					ToolData data = toolGroupDataset[t].get(selectedToolGroupU
							.get(i));
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ data.toolGroupName);
					for (int j = 0; j < data.utiliztionbyDay.size(); j++) {
						series.add(j, data.utiliztionbyDay.get(j));
						series.add(j + 1, data.utiliztionbyDay.get(j));
					}
					dataset.addSeries(series);
				}
				if (selectedToolGroupT.get(i) == 2) {
					String str[] = selectedToolGroupU.get(i).split("%");
					ToolGroupData gdata = toolGroupDataset[t]
							.getToolGroupData(str[1]);
					ToolData data = gdata.get(str[0]);
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ data.toolName);
					for (int j = 0; j < data.utiliztionbyDay.size(); j++) {
						series.add(j, data.utiliztionbyDay.get(j));
						series.add(j + 1, data.utiliztionbyDay.get(j));
					}
					dataset.addSeries(series);
				}
			}
		}

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);

		return dataset;
	}

	// private PieDataset getUtiDatasetP() {
	// if (selectedToolGroupT.size() < 1)
	// return null;
	// DefaultPieDataset dataset = new DefaultPieDataset();
	// ToolData data;
	// if (selectedToolGroupT.get(0) == 0) {
	// data = toolGroupDataset.allToolData;
	//
	// } else if (selectedToolGroupT.get(0) == 1) {
	// data = toolGroupDataset.get(selectedToolGroupU.get(0));
	// } else {
	// String str[] = selectedToolGroupU.get(0).split("%");
	// ToolGroupData gdata = toolGroupDataset.getT(str[1]);
	// data = gdata.get(str[0]);
	//
	// }
	// dataset.setValue("Free", data.freeTime);
	// dataset.setValue("Setup", data.setupTime);
	// dataset.setValue("Process", data.processTime);
	// dataset.setValue("Block", data.blockTime);
	// dataset.setValue("Breakdown", data.breakdownTime);
	// dataset.setValue("Maintenance", data.maintenanceTime);
	// return dataset;
	//
	// }

	private XYDataset getInterruptDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		int temp = 0;
		if (selectedToolGroupU.size() < 1) {

			for (int m = 0; m < toolGroupDataset[0].size(); m++) {
				for (int t = 0; t < times; t++) {
					ToolGroupData data = toolGroupDataset[t].get(m);
					for (int j = 0; j < data.size(); j++) {
						ToolData tData = data.get(j);
						XYSeries series = new XYSeries(modelName[t] + "_"
								+ tData.toolName);
						series.add(0, 2 * temp + 1);

						for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
							series.add(
									tData.beginInterruptTime.get(k) / tData.interval,
									2 * temp + 1);
							series.add(
									tData.beginInterruptTime.get(k) / tData.interval,
									2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval, 2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval, 2 * temp + 1);
						}
						if(tData.processTimeDataset.size()>0){
						series.add(
								tData.processTimeDataset.get(
										tData.processTimeDataset.size() - 1)
										.getEnd()
										/ tData.interval, 2 * temp + 1);
						}
						dataset.addSeries(series);
						

					}
					temp++;
				}
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroupU.size(); i++) {

			if (selectedToolGroupT.get(i) == 0) {// selected root
				for (int t = 0; t < times; t++) {
					for (int m = 0; m < toolGroupDataset[t].size(); m++) {
						ToolGroupData data = toolGroupDataset[t].get(m);
						for (int j = 0; j < data.size(); j++) {
							XYSeries series = new XYSeries(modelName[t]
									+ data.toolGroupName + j + "_" + "All");
							series.add(0, 2 * temp + 1);
							ToolData tData = data.get(j);
							for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
								series.add(
										tData.beginInterruptTime.get(k) / tData.interval,
										2 * temp + 1);
								series.add(
										tData.beginInterruptTime.get(k) / tData.interval,
										2 * (temp + 1));
								series.add(
										(tData.beginInterruptTime.get(k) + tData.interruptTime
												.get(k)) / tData.interval,
										2 * (temp + 1));
								series.add(
										(tData.beginInterruptTime.get(k) + tData.interruptTime
												.get(k)) / tData.interval, 2 * temp + 1);
							}
							if(tData.processTimeDataset.size()>0){
							series.add(
									tData.processTimeDataset.get(
											tData.processTimeDataset.size() - 1)
											.getEnd()
											/ tData.interval, 2 * temp + 1);
							}
							dataset.addSeries(series);
						}
					}
					temp++;
				}
			}
			if (selectedToolGroupT.get(i) == 1
					&& selectedToolGroupU.size() == 1) {
				for (int j = 0; j < toolGroupDataset[0].getToolGroupData(
						selectedToolGroupU.get(i)).size(); j++) {
					for (int t = 0; t < times; t++) {
						ToolGroupData data = toolGroupDataset[t]
								.getToolGroupData(selectedToolGroupU.get(i));
						XYSeries series = new XYSeries(modelName[t] + "_"
								+ data.get(j).toolName);
						series.add(0, 2 * temp + 1);
						ToolData tData = data.get(j);
						for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
							series.add(
									tData.beginInterruptTime.get(k) / tData.interval,
									2 * temp + 1);
							series.add(
									tData.beginInterruptTime.get(k) / tData.interval,
									2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval, 2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval, 2 * temp + 1);
						}
						if(tData.processTimeDataset.size()>0){
						series.add(
								tData.processTimeDataset.get(
										tData.processTimeDataset.size() - 1)
										.getEnd()
										/ tData.interval, 2 * temp + 1);
						}
						dataset.addSeries(series);
						temp++;
					}
				}
			}
			if (selectedToolGroupT.get(i) == 1 && selectedToolGroupU.size() > 1) {
				for (int t = 0; t < times; t++) {
					ToolGroupData data = toolGroupDataset[t]
							.getToolGroupData(selectedToolGroupU.get(i));
					for (int j = 0; j < data.size(); j++) {
						XYSeries series = new XYSeries(modelName[t] + "_"
								+ data.toolGroupName + j);
						series.add(0, 2 * temp + 1);
						ToolData tData = data.get(j);
						for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
							series.add(
									tData.beginInterruptTime.get(k) / tData.interval,
									2 * temp + 1);
							series.add(
									tData.beginInterruptTime.get(k) / tData.interval,
									2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval, 2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval, 2 * temp + 1);
						}
						if(tData.processTimeDataset.size()>0){
						series.add(
								tData.processTimeDataset.get(
										tData.processTimeDataset.size() - 1)
										.getEnd()
										/ tData.interval, 2 * temp + 1);
						}
						dataset.addSeries(series);
					}
					temp++;
				}
			}

			if (selectedToolGroupT.get(i) == 2) {
				String str[] = selectedToolGroupU.get(i).split("%");
				for (int t = 0; t < times; t++) {
					ToolGroupData gdata = toolGroupDataset[t]
							.getToolGroupData(str[1]);
					ToolData tData = gdata.get(str[0]);
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ tData.toolName);
					series.add(0, 2 * temp + 1);
					for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
						series.add(tData.beginInterruptTime.get(k) / tData.interval,
								2 * temp + 1);
						series.add(tData.beginInterruptTime.get(k) / tData.interval,
								2 * (temp + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval, 2 * (temp + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval, 2 * temp + 1);
					}
					if(tData.processTimeDataset.size()>0){
					series.add(
							tData.processTimeDataset.get(
									tData.processTimeDataset.size() - 1)
									.getEnd()
									/ tData.interval, 2 * temp + 1);
					}

					dataset.addSeries(series);
					temp++;
				}
			}
		}

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);

		return dataset;
	}

	private CategoryDataset getBufferDatasetS() {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		for (int t = 0; t < times; t++) {
			if (selectedToolGroup.size() < 1) {
				for (int i = 0; i < bufferDataset[t].size(); i++) {
					if (bufferStatFlag[0] == 1)
						result.addValue(bufferDataset[t].get(i).avgBufferSize,
								series1,
								modelName[t] + "_"
										+ bufferDataset[t].get(i).bufferName);
					if (bufferStatFlag[1] == 1)
						result.addValue(bufferDataset[t].get(i).maxBufferSize,
								series2,
								modelName[t] + "_"
										+ bufferDataset[t].get(i).bufferName);
				}
			} else {
				for (int i = 0; i < selectedToolGroup.size(); i++) {
					BufferData data = bufferDataset[t]
							.getBufferData(selectedToolGroup.get(i));
					if (data == null) {
						if (bufferStatFlag[0] == 1)
							result.addValue(
									bufferDataset[t].allBufferData.avgBufferSize,
									series1, modelName[t] + "_" + "All");
						if (bufferStatFlag[1] == 1)
							result.addValue(
									bufferDataset[t].allBufferData.maxBufferSize,
									series2, modelName[t] + "_" + "All");

					} else {
						if (bufferStatFlag[0] == 1)
							result.addValue(data.avgBufferSize, series1,
									modelName[t] + "_" + data.bufferName);
						if (bufferStatFlag[1] == 1)
							result.addValue(data.maxBufferSize, series2,
									modelName[t] + "_" + data.bufferName);
					}
				}
			}
		}

		return result;
	}

	private CategoryDataset getWaitTimeDatasetS() {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		for (int t = 0; t < times; t++) {
			if (selectedToolGroup.size() < 1) {
				for (int i = 0; i < toolGroupDataset[t].size(); i++) {
					if (waitTimeStatFlag[0] == 1)
						result.addValue(
								toolGroupDataset[t].get(i).waitDataset.avgWaitTime,
								series1,
								modelName[t]
										+ "_"
										+ toolGroupDataset[t].get(i).toolGroupName);
					if (waitTimeStatFlag[1] == 1)
						result.addValue(
								toolGroupDataset[t].get(i).waitDataset.maxWaitTime,
								series2,
								modelName[t]
										+ "_"
										+ toolGroupDataset[t].get(i).toolGroupName);
				}
			} else {
				for (int i = 0; i < selectedToolGroup.size(); i++) {
					// BufferData
					// data=bufferDataset.get(selectedToolGroup.get(i));

					if (toolGroupDataset[t].getToolGroupData(selectedToolGroup
							.get(i)) == null) {
						if (waitTimeStatFlag[0] == 1)
							result.addValue(
									toolGroupDataset[t].allWaitDataset.avgWaitTime,
									series1, modelName[t] + "_" + "All");
						if (waitTimeStatFlag[1] == 1)
							result.addValue(
									toolGroupDataset[t].allWaitDataset.maxWaitTime,
									series2, modelName[t] + "_" + "All");

					} else {
						WaitDataset data = toolGroupDataset[t]
								.getToolGroupData(selectedToolGroup.get(i)).waitDataset;
						if (waitTimeStatFlag[0] == 1)
							result.addValue(data.avgWaitTime, series1,
									modelName[t] + "_" + data.bufferName);
						if (waitTimeStatFlag[1] == 1)
							result.addValue(data.maxWaitTime, series2,
									modelName[t] + "_" + data.bufferName);
					}
				}
			}
		}
		return result;
	}

	private CategoryDataset getUtiDatasetZ() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		final String category1 = "Free";
		final String category2 = "Setup";
		final String category3 = "Process";
		final String category4 = "Block";
		final String category5 = "Breakdown";
		final String category6 = "Maintenance";

		for (int i = 0; i < selectedToolGroupU.size(); i++) {
			for (int t = 0; t < times; t++) {
				ToolData data;
				if (selectedToolGroupT.get(i) == 0) {
					data = toolGroupDataset[t].allToolData;

				} else if (selectedToolGroupT.get(i) == 1) {
					data = toolGroupDataset[t].get(selectedToolGroupU.get(i));
				} else {
					String str[] = selectedToolGroupU.get(i).split("%");
					ToolGroupData gdata = toolGroupDataset[t]
							.getToolGroupData(str[1]);
					data = gdata.get(str[0]);
				}
				if (toolUtiStatFlag[0] == 1)
					dataset.addValue(data.freeTime * 100, category1,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[1] == 1)
					dataset.addValue(data.setupTime * 100, category2,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[2] == 1)
					dataset.addValue(data.processTime * 100, category3,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[3] == 1)
					dataset.addValue(data.blockTime * 100, category4,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[4] == 1)
					dataset.addValue(data.breakdownTime * 100, category5,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[5] == 1)
					dataset.addValue(data.maintenanceTime * 100, category6,
							modelName[t] + "_" + data.toolName);
			}
		}
		return dataset;
	}

	private CategoryDataset getInterruptDatasetS() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (selectedToolGroupU.size() == 0) {
			for (int t = 0; t < times; t++) {
				for (int i = 0; i < toolGroupDataset[t].size(); i++) {
					dataset.addValue(
							toolGroupDataset[t].get(i).toolGroupDataSum.interruptNum,
							"Interruption Times", modelName[t] + "_"
									+ toolGroupDataset[t].get(i).toolGroupName);
				}
			}

			return dataset;
		}

		for (int i = 0; i < selectedToolGroupU.size(); i++) {
			for (int t = 0; t < times; t++) {
				ToolData data;
				if (selectedToolGroupT.get(i) == 0) {
					data = toolGroupDataset[t].allToolData;

				} else if (selectedToolGroupT.get(i) == 1) {
					data = toolGroupDataset[t].get(selectedToolGroupU.get(i));
				} else {
					String str[] = selectedToolGroupU.get(i).split("%");
					ToolGroupData gdata = toolGroupDataset[t]
							.getToolGroupData(str[1]);
					data = gdata.get(str[0]);
				}

				dataset.addValue(data.interruptNum, "Interruption Times",
						modelName[t] + "_" + data.toolName);
			}
		}
		return dataset;
	}

	private CategoryDataset getUtiDatasetAll() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		final String category1 = "Free";
		final String category2 = "Setup";
		final String category3 = "Process";
		final String category4 = "Block";
		final String category5 = "Breakdown";
		final String category6 = "Maintenance";
		for (int t = 0; t < times; t++) {
			for (int i = 0; i < toolGroupDataset[t].size(); i++) {

				ToolData data = toolGroupDataset[t].get(i).toolGroupDataSum;
				if (toolUtiStatFlag[0] == 1)
					dataset.addValue(data.freeTime * 100, category1,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[1] == 1)
					dataset.addValue(data.setupTime * 100, category2,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[2] == 1)
					dataset.addValue(data.processTime * 100, category3,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[3] == 1)
					dataset.addValue(data.blockTime * 100, category4,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[4] == 1)
					dataset.addValue(data.breakdownTime * 100, category5,
							modelName[t] + "_" + data.toolName);
				if (toolUtiStatFlag[5] == 1)
					dataset.addValue(data.maintenanceTime * 100, category6,
							modelName[t] + "_" + data.toolName);
			}

		}

		return dataset;
	}

	private XYDataset getWipDataset(String index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int t = 0; t < times; t++) {
				for (int i = 0; i < releaseDataset[t].size(); i++) {
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ releaseDataset[t].get(i).product);
					for (int j = 0; j < releaseDataset[t].get(i).wipbyDay
							.size(); j++) {
						series.add(j, releaseDataset[t].get(i).wipbyDay.get(j));
						series.add(j + 1,
								releaseDataset[t].get(i).wipbyDay.get(j));
					}

					dataset.addSeries(series);
				}
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			for (int t = 0; t < times; t++) {
				XYSeries series;
				if (index[j].equals("All")) {
					series = new XYSeries(modelName[t] + "_" + "All");
					for (int i = 0; i < releaseDataset[t].allReleaseDataset.wipbyDay
							.size(); i++) {
						series.add(i,
								releaseDataset[t].allReleaseDataset.wipbyDay
										.get(i));
						series.add(i + 1,
								releaseDataset[t].allReleaseDataset.wipbyDay
										.get(i));
					}

				} else {
					series = new XYSeries(modelName[t] + "_" + index[j]);
					for (int k = 0; k < releaseDataset[t].size(); k++) {
						if (releaseDataset[t].get(k).product.equals(index[j])) {
							for (int i = 0; i < releaseDataset[t].get(k).wipbyDay
									.size(); i++) {
								series.add(i, releaseDataset[t].get(k).wipbyDay
										.get(i));
								series.add(i + 1,
										releaseDataset[t].get(k).wipbyDay
												.get(i));
							}
							break;
						}

					}
				}

				dataset.addSeries(series);
			}
		}

		return dataset;
	}

	private XYDataset getReleaseDataset(String index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int t = 0; t < times; t++) {
				for (int i = 0; i < releaseDataset[t].size(); i++) {
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ releaseDataset[t].get(i).product);
					for (int j = 0; j < releaseDataset[t].get(i).releaseLotsbyDay
							.size(); j++) {
						series.add(j, releaseDataset[t].get(i).releaseLotsbyDay
								.get(j));
						series.add(j + 1,
								releaseDataset[t].get(i).releaseLotsbyDay
										.get(j));
					}

					dataset.addSeries(series);
				}
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			for (int t = 0; t < times; t++) {
				XYSeries series;
				if (index[j].equals("All")) {
					series = new XYSeries(modelName[t] + "_" + "All");
					for (int i = 0; i < releaseDataset[t].allReleaseDataset.releaseLotsbyDay
							.size(); i++) {
						series.add(
								i,
								releaseDataset[t].allReleaseDataset.releaseLotsbyDay
										.get(i));
						series.add(
								i + 1,
								releaseDataset[t].allReleaseDataset.releaseLotsbyDay
										.get(i));
					}

				} else {
					series = new XYSeries(modelName[t] + "_" + index[j]);
					for (int k = 0; k < releaseDataset[t].size(); k++) {
						if (releaseDataset[t].get(k).product.equals(index[j])) {

							for (int i = 0; i < releaseDataset[t].get(k).releaseLotsbyDay
									.size(); i++) {
								series.add(
										i,
										releaseDataset[t].get(k).releaseLotsbyDay
												.get(i));
								series.add(
										i + 1,
										releaseDataset[t].get(k).releaseLotsbyDay
												.get(i));
							}
							break;
						}
					}
				}

				dataset.addSeries(series);
			}
		}

		return dataset;
	}

	private XYDataset getFinishedLotsDataset(String index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int t = 0; t < times; t++) {
				for (int i = 0; i < releaseDataset[t].size(); i++) {
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ releaseDataset[t].get(i).product);
					for (int j = 0; j < releaseDataset[t].get(i).finishedLotsbyDay
							.size(); j++) {
						series.add(j,
								releaseDataset[t].get(i).finishedLotsbyDay
										.get(j));
						series.add(j + 1,
								releaseDataset[t].get(i).finishedLotsbyDay
										.get(j));
					}

					dataset.addSeries(series);
				}
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			for (int t = 0; t < times; t++) {
				XYSeries series;
				if (index[j].equals("All")) {
					series = new XYSeries(modelName[t] + "_" + "All");
					for (int i = 0; i < releaseDataset[t].allReleaseDataset.finishedLotsbyDay
							.size(); i++) {
						series.add(
								i,
								releaseDataset[t].allReleaseDataset.finishedLotsbyDay
										.get(i));
						series.add(
								i + 1,
								releaseDataset[t].allReleaseDataset.finishedLotsbyDay
										.get(i));
					}

				} else {
					series = new XYSeries(modelName[t] + "_" + index[j]);
					for (int k = 0; k < releaseDataset[t].size(); k++) {
						if (releaseDataset[t].get(k).product.equals(index[j])) {
							for (int i = 0; i < releaseDataset[t].get(k).finishedLotsbyDay
									.size(); i++) {
								series.add(
										i,
										releaseDataset[t].get(k).finishedLotsbyDay
												.get(i));
								series.add(
										i + 1,
										releaseDataset[t].get(k).finishedLotsbyDay
												.get(i));
							}
							break;
						}

					}
				}

				dataset.addSeries(series);
			}
		}

		return dataset;
	}

	private XYDataset getCycleTimeDataset(String index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int t = 0; t < times; t++) {
				for (int i = 0; i < releaseDataset[t].size(); i++) {
					XYSeries series = new XYSeries(modelName[t] + "_"
							+ releaseDataset[t].get(i).product);
					for (int j = 0; j < releaseDataset[t].get(i).cycleTimebyDay
							.size(); j++) {
						series.add(j,
								releaseDataset[t].get(i).cycleTimebyDay.get(j));
						series.add(j + 1,
								releaseDataset[t].get(i).cycleTimebyDay.get(j));
					}

					dataset.addSeries(series);
				}
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			for (int t = 0; t < times; t++) {
				XYSeries series;
				if (index[j].equals("All")) {
					series = new XYSeries(modelName[t] + "_" + "All");
					for (int i = 0; i < releaseDataset[t].allReleaseDataset.cycleTimebyDay
							.size(); i++) {
						series.add(
								i,
								releaseDataset[t].allReleaseDataset.cycleTimebyDay
										.get(i));
						series.add(
								i + 1,
								releaseDataset[t].allReleaseDataset.cycleTimebyDay
										.get(i));
					}

				} else {
					series = new XYSeries(modelName[t] + "_" + index[j]);
					for (int k = 0; k < releaseDataset[t].size(); k++) {
						if (releaseDataset[t].get(k).product.equals(index[j])) {
							for (int i = 0; i < releaseDataset[t].get(k).cycleTimebyDay
									.size(); i++) {
								series.add(i,
										releaseDataset[t].get(k).cycleTimebyDay
												.get(i));
								series.add(i + 1,
										releaseDataset[t].get(k).cycleTimebyDay
												.get(i));
							}
							break;
						}
					}
				}

				dataset.addSeries(series);
			}
		}

		return dataset;
	}

	private void createProductTree() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Product");

		for (int i = 0; i < releaseDataset[0].size(); i++) {
			DefaultMutableTreeNode product = new DefaultMutableTreeNode(
					releaseDataset[0].get(i).product);
			root.add(product);
		}
		productTree = new JTree(root);

		productTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		productTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				// selectedProduct;// = productTree.getSelectionRows();
				TreePath[] paths = productTree.getSelectionPaths();
				if (paths == null || paths.length < 1)
					return;
				selectedProduct = new String[paths.length];
				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					if (node.isRoot()) {
						selectedProduct[i] = "All";
					} else {
						selectedProduct[i] = node.toString();
					}
				}

				// if (selectedProduct == null || selectedProduct.length < 1)
				// return;

				// //if (selectedBuffer.length > 1) {
				tWipPlot.setDataset(getWipDataset(selectedProduct));
				tReleasePlot.setDataset(getReleaseDataset(selectedProduct));
				tFinishedLotsPlot
						.setDataset(getFinishedLotsDataset(selectedProduct));
				tCycleTimePlot.setDataset(getCycleTimeDataset(selectedProduct));
				sReleasePlot.setDataset(getReleaseDataset(releaseDataset,
						selectedProduct));
				sFinishedLotsPlot.setDataset(getFinishedLotsDataset(
						releaseDataset, selectedProduct));
				sWipPlot.setDataset(getWipDataset(releaseDataset,
						selectedProduct));
				sCycleTimePlot.setDataset(getCycleTimeDataset(releaseDataset,
						selectedProduct));

			}
		});
		expandTree(productTree);
	}

	private void createToolTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ToolGroup");

		for (int i = 0; i < toolGroupDataset[0].size(); i++) {
			DefaultMutableTreeNode toolgroup = new DefaultMutableTreeNode(
					toolGroupDataset[0].get(i).toolGroupName);
			root.add(toolgroup);

			for (int j = 0; j < toolGroupDataset[0].get(i).size(); j++) {

				DefaultMutableTreeNode tool = new DefaultMutableTreeNode(
						toolGroupDataset[0].get(i).get(j).toolName);
				toolgroup.add(tool);
			}
		}
		toolTree = new JTree(root);

		toolTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		toolTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				selectedToolGroup.clear();
				selectedToolGroupU.clear();
				selectedToolGroupT.clear();
				TreePath[] paths = ((JTree) evt.getSource())
						.getSelectionPaths();
				if (paths == null)
					return;

				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					if (node.isLeaf()) {
						if (!selectedToolGroup.contains(node.getParent()
								.toString()))
							selectedToolGroup.add(node.getParent().toString());
						selectedToolGroupU.add(node.toString() + "%"
								+ node.getParent().toString());
						selectedTool.add(node.toString() + "%"
								+ node.getParent().toString());
						selectedToolGroupT.add(2);

					} else if (!node.isRoot()) {
						if (!selectedToolGroup.contains(node.toString()))
							selectedToolGroup.add(node.toString());
						selectedToolGroupU.add(node.toString());
						selectedToolGroupT.add(1);
					} else {
						selectedToolGroup.add(node.toString());
						selectedToolGroupU.add(node.toString());
						selectedToolGroupT.add(0);
					}
					// selectedTool.add(node.getUserObject().toString());
				}
				tBufferPlot.setDataset(getBufferDataset());
				sBufferPlot.setDataset(getBufferDatasetS());
				tWaitTimePlot.setDataset(getWaitTimeDataset());
				tUtiPlot.setDataset(getUtiDataset());
				tInterruptPlot.setDataset(getInterruptDataset());
				sInterruptPlot.setDataset(getInterruptDatasetS());
				sWaitTimePlot.setDataset(getWaitTimeDatasetS());
				Thread thread=new Thread(){
					public void run(){
						synchronized(toolGanntPlot){
						toolGanntPlot
						.setDataset(getToolGanntDataset(new ArrayList<String>()));
						}
					}
				};
				thread.start();
				if (selectedToolGroupU.size() == 1) {
					// oneToolPanel.setVisible(true);
					// multiToolPanel.setVisible(false);
					// sUtiPlot.setDataset(getUtiDatasetP());
					// Color[] colors = { Color.gray, Color.blue, Color.green,
					// Color.yellow, Color.red, Color.pink };
					// Chart.PieRenderer renderer1 = new
					// Chart.PieRenderer(colors);
					// renderer1.setColor(sUtiPlot,
					// (DefaultPieDataset) getUtiDatasetP());
					oneToolPanel.setVisible(false);
					multiToolPanel.setVisible(true);
					sUtiPlotZ.setDataset(getUtiDatasetZ());
				} else if (selectedToolGroupU.size() == 0) {
					oneToolPanel.setVisible(false);
					multiToolPanel.setVisible(true);
					sUtiPlotZ.setDataset(getUtiDatasetAll());
				} else {
					oneToolPanel.setVisible(false);
					multiToolPanel.setVisible(true);
					sUtiPlotZ.setDataset(getUtiDatasetZ());
				}

			}
		});
		expandTree(toolTree);

	}

	private void createJobTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new MyNode(0,
				0, "Job"));

		for (int i = 0; i < jobDataset[0].size(); i++) {
			DefaultMutableTreeNode toolgroup = null;
			for (int j = 0; j < jobDataset[0].get(i).size(); j++) {

				if (j == 0) {
					toolgroup = new DefaultMutableTreeNode(new MyNode(0, i,
							jobDataset[0].get(i).get(j).jobType));
					root.add(toolgroup);
				}

				DefaultMutableTreeNode tool = new DefaultMutableTreeNode(
						new MyNode(i, j, jobDataset[0].get(i).get(j).jobName));

				toolgroup.add(tool);
			}
		}
		jobTree = new JTree(root);

		jobTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		jobTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				selectedJobP.clear();
				selectedJob.clear();

				String chartName = "";
				TreePath[] paths = ((JTree) evt.getSource())
						.getSelectionPaths();
				if (paths == null || paths.length < 1)
					return;
				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					MyNode mn = (MyNode) node.getUserObject();
					if (node.isLeaf()) {
						selectedJobP.add(mn.parentIndex);
						selectedJob.add(mn.index);
						chartName = mn.name;
						// jobGanntChart.setTitle(mn.name);
					} else if (!node.isRoot() && paths.length == 1) {
						MyNode m = (MyNode) (((DefaultMutableTreeNode) node
								.getChildAt(0)).getUserObject());
						selectedJobP.add(m.parentIndex);
						selectedJob.add(m.index);
						chartName = m.name;
						// jobGanntChart.setTitle(m.name);
					}
				}
				jobGanntPlot.setDataset(getJobGanntDataset());
				jobGanntChart.setTitle(chartName);

			}
		});
		expandTree(toolTree);

	}

	private void expandTree(JTree tree) {

		TreeNode root = (TreeNode) tree.getModel().getRoot();

		expandAll(tree, new TreePath(root), true);

	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {

		TreeNode node = (TreeNode) parent.getLastPathComponent();

		if (node.getChildCount() >= 0) {

			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {

				TreeNode n = (TreeNode) e.nextElement();

				TreePath path = parent.pathByAddingChild(n);

				expandAll(tree, path, expand);

			}

		}

		if (expand) {

			tree.expandPath(parent);

		} else {

			tree.collapsePath(parent);

		}

	}

	private void centerWindow(JFrame frame) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();

		frame.setLocation(scmSize.width / 2 - (frame.getSize().width / 2),
				scmSize.height / 2 - (frame.getSize().height / 2));
	}

	private CategoryDataset getWipDataset(ReleaseDataset[] data,
			String[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		for (int t = 0; t < times; t++) {
			if (selectedBuffer == null || selectedBuffer.length < 1) {
				for (int i = 0; i < data[t].size(); i++) {
					if (wipStatFlag[0] == 1)
						result.addValue(data[t].get(i).avgWip, series1,
								modelName[t] + "_" + data[t].get(i).product);
					if (wipStatFlag[1] == 1)
						result.addValue(data[t].get(i).maxWip, series2,
								modelName[t] + "_" + data[t].get(i).product);
				}
			} else {
				for (int i = 0; i < selectedBuffer.length; i++) {
					if (selectedBuffer[i].equals("All")) {
						if (wipStatFlag[0] == 1)
							result.addValue(data[t].allReleaseDataset.avgWip,
									series1, modelName[t] + "_" + "All");
						if (wipStatFlag[1] == 1)
							result.addValue(data[t].allReleaseDataset.maxWip,
									series2, modelName[t] + "_" + "All");
					} else {
						for (int k = 0; k < releaseDataset[t].size(); k++) {
							if (releaseDataset[t].get(k).product
									.equals(selectedBuffer[i])) {
								if (wipStatFlag[0] == 1)
									result.addValue(data[t].get(k).avgWip,
											series1, modelName[t] + "_"
													+ data[t].get(k).product);
								if (wipStatFlag[1] == 1)
									result.addValue(data[t].get(k).maxWip,
											series2, modelName[t] + "_"
													+ data[t].get(k).product);
								break;
							}

						}
					}
				}
			}
		}

		return result;
	}

	private CategoryDataset getCycleTimeDataset(ReleaseDataset[] data,
			String[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		String series3 = "Min";
		String series4 = "Raw";
		for (int t = 0; t < times; t++) {
			if (selectedBuffer == null || selectedBuffer.length < 1) {
				for (int i = 0; i < data[t].size(); i++) {
					if (cycleTimeStatFlag[1] == 1)
						result.addValue(data[t].get(i).avgCycleTime, series1,
								modelName[t] + "_" + data[t].get(i).product);
					if (cycleTimeStatFlag[2] == 1)
						result.addValue(data[t].get(i).maxCycleTime, series2,
								modelName[t] + "_" + data[t].get(i).product);
					if (cycleTimeStatFlag[0] == 1)
						result.addValue(data[t].get(i).minCycleTime, series3,
								modelName[t] + "_" + data[t].get(i).product);
					if (cycleTimeStatFlag[3] == 1)
						result.addValue(data[t].get(i).rawProcessTime, series4,
								modelName[t] + "_" + data[t].get(i).product);
				}
			} else {
				for (int i = 0; i < selectedBuffer.length; i++) {
					if (selectedBuffer[i].equals("All")) {
						if (cycleTimeStatFlag[1] == 1)
							result.addValue(
									data[t].allReleaseDataset.avgCycleTime,
									series1, modelName[t] + "_" + "All");
						if (cycleTimeStatFlag[2] == 1)
							result.addValue(
									data[t].allReleaseDataset.maxCycleTime,
									series2, modelName[t] + "_" + "All");
						if (cycleTimeStatFlag[0] == 1)
							result.addValue(
									data[t].allReleaseDataset.minCycleTime,
									series3, modelName[t] + "_" + "All");
						// if (cycleTimeStatFlag[3]==1)
						// result.addValue(data.allReleaseDataset.rawProcessTime,
						// series4,
						// "All");
					} else {
						for (int k = 0; k < releaseDataset[t].size(); k++) {
							if (releaseDataset[t].get(k).product
									.equals(selectedBuffer[i])) {
								if (cycleTimeStatFlag[1] == 1)
									result.addValue(
											data[t].get(k).avgCycleTime,
											series1, modelName[t] + "_"
													+ data[t].get(k).product);
								if (cycleTimeStatFlag[2] == 1)
									result.addValue(
											data[t].get(k).maxCycleTime,
											series2, modelName[t] + "_"
													+ data[t].get(k).product);
								if (cycleTimeStatFlag[0] == 1)
									result.addValue(
											data[t].get(k).minCycleTime,
											series3, modelName[t] + "_"
													+ data[t].get(k).product);
								if (cycleTimeStatFlag[3] == 1)
									result.addValue(
											data[t].get(k).rawProcessTime,
											series4, modelName[t] + "_"
													+ data[t].get(k).product);
								break;
							}
						}

					}
				}
			}
		}
		return result;
	}

	private CategoryDataset getReleaseDataset(ReleaseDataset[] data,
			String[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Released";
		for (int t = 0; t < times; t++) {
			if (selectedBuffer == null || selectedBuffer.length < 1) {
				for (int i = 0; i < data[t].size(); i++) {
					result.addValue(data[t].get(i).releasedLotNum, series1,
							modelName[t] + "_" + data[t].get(i).product);
				}
			} else {
				for (int i = 0; i < selectedBuffer.length; i++) {
					if (selectedBuffer[i].equals("All")) {
						result.addValue(
								data[t].allReleaseDataset.releasedLotNum,
								series1, modelName[t] + "_" + "All");
					} else {
						for (int k = 0; k < releaseDataset[t].size(); k++) {
							if (releaseDataset[t].get(k).product
									.equals(selectedBuffer[i])) {
								result.addValue(data[t].get(k).releasedLotNum,
										series1,
										modelName[t] + "_"
												+ data[t].get(k).product);
								break;
							}

						}
					}
				}
			}
		}

		return result;
	}

	private CategoryDataset getFinishedLotsDataset(ReleaseDataset[] data,
			String[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Finished";
		for (int t = 0; t < times; t++) {
			if (selectedBuffer == null || selectedBuffer.length < 1) {
				for (int i = 0; i < data[t].size(); i++) {
					result.addValue(data[t].get(i).finishedLotNum, series1,
							modelName[t] + "_" + data[t].get(i).product);
				}
			} else {
				for (int i = 0; i < selectedBuffer.length; i++) {
					if (selectedBuffer[i].equals("All")) {
						result.addValue(
								data[t].allReleaseDataset.finishedLotNum,
								series1, modelName[t] + "_" + "All");
					} else {
						for (int k = 0; k < releaseDataset[t].size(); k++) {
							if (releaseDataset[t].get(k).product
									.equals(selectedBuffer[i])) {

								result.addValue(data[t].get(k).finishedLotNum,
										series1,
										modelName[t] + "_"
												+ data[t].get(k).product);
								break;
							}
						}

					}
				}
			}
		}

		return result;

	}

	class Lenged extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String id;
		int value[];

		private Lenged(ChartPanel c, String[] name, int[] v, String id) {
			this.id = id;
			value = v;
			this.setOpaque(false);
			setLayout(null);
			if (v.length < 3) {
				setBounds(c.getWidth() - name.length * 50 - 30, 8,
						name.length * 50 + 15, 20);
			} else {
				setBounds(c.getWidth() - name.length * 50 - 30, 30,
						name.length * 50 + 15, 20);
			}
			setBorder(LineBorder.createGrayLineBorder());
			// CustomListener c1 = new CustomListener();
			for (int i = 0; i < name.length; i++) {

				MyJCheckBox cb = new MyJCheckBox(name[i], value[i] == 1 ? true
						: false, i);
				if (name[i].equals("Avg")) {
					cb.setForeground(new Color(0xFF4500));

				} else if (name[i].equals("Min")) {
					cb.setForeground(new Color(0x1E90FF));
				} else if (name[i].equals("Max")) {
					cb.setForeground(new Color(0xCD3278));
				} else if (name[i].equals("Raw")) {
					cb.setForeground(new Color(0x96CDCD));

				} else if (name[i].equals("Free")) {
					cb.setForeground(Color.GRAY);
				} else if (name[i].equals("Setup")) {
					cb.setForeground(new Color(0x4682B4));
				} else if (name[i].equals("Proc.")) {
					cb.setForeground(Color.green);
				} else if (name[i].equals("Block")) {
					cb.setForeground(Color.yellow);
				} else if (name[i].equals("Brea.")) {
					cb.setForeground(new Color(0xB22222));
				} else if (name[i].equals("Main.")) {
					cb.setForeground(Color.pink);
				}
				int a = 0;
				if (i == 0)
					a = 2;
				cb.setBounds(i * (50 + 5) + a, 2, 50, 14);
				cb.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent evt) {

						if (evt.getStateChange() == ItemEvent.SELECTED) {
							value[((MyJCheckBox) evt.getItem()).index] = 1;
						} else {
							value[((MyJCheckBox) evt.getItem()).index] = 0;
						}
						Lenged m = (Lenged) ((MyJCheckBox) evt.getItem())
								.getParent();
						if (m.id.equals("wip")) {
							sWipPlot.setDataset(getWipDataset(releaseDataset,
									selectedProduct));
						} else if (m.id.equals("cycleTime")) {
							sCycleTimePlot.setDataset(getCycleTimeDataset(
									releaseDataset, selectedProduct));

						} else if (m.id.equals("bufferSize")) {
							sBufferPlot.setDataset(getBufferDatasetS());
						} else if (m.id.equals("waitTime")) {
							sWaitTimePlot.setDataset(getWaitTimeDatasetS());
						} else if (m.id.equals("toolUti")) {
							if (selectedToolGroupU.size() == 0)
								sUtiPlotZ.setDataset(getUtiDatasetAll());
							else
								sUtiPlotZ.setDataset(getUtiDatasetZ());
						}

					}
				});

				add(cb);
			}
		}
	}

	private XYTaskDataset getJobGanntDataset() {
		ArrayList<String> tools = new ArrayList<String>();

		final TaskSeriesCollection collection = new TaskSeriesCollection();
		if (selectedJobP.size() < 1) {
			selectedJobP.add(0);
			selectedJob.add(0);
		}

		for (int j = 0; j < selectedJobP.size(); j++) {

			JobData data = jobDataset[0].get(selectedJobP.get(j)).get(
					selectedJob.get(j));

			for (int i = 0; i <= data.processIndex; i++) {
				JobSubData jobStateTime = data.jobSubData[i];
				if (!jobStateTime.finished)
					continue;

				int index = collection.indexOf(jobStateTime.toolName);

				if (index == -1) {
					final TaskSeries s2 = new TaskSeries(jobStateTime.toolName);
					collection.add(s2);
					tools.add(jobStateTime.toolName);
					index = collection.indexOf(jobStateTime.toolName);
				}
				final MyTask t21 = new MyTask("Transporting",
						date(jobStateTime.transportBeginTime),
						date(jobStateTime.waitBeginTime));
				final MyTask t22 = new MyTask("Waiting",
						date(jobStateTime.waitBeginTime),
						date(jobStateTime.processBeginTime));

				final MyTask t23 = new MyTask("Processing",
						date(jobStateTime.processBeginTime),
						date(jobStateTime.blockBeginTime));
					t23.setJobs(data.jobName);
				final MyTask t24 = new MyTask("Blocking",
						date(jobStateTime.blockBeginTime),
						date(jobStateTime.nextTransportBeginTime));
				collection.getSeries(index).add(t21);
				collection.getSeries(index).add(t22);
				collection.getSeries(index).add(t23);
				collection.getSeries(index).add(t24);

			}
		}
		toolName = tools.toArray(new String[0]);

		// Arrays.sort(toolName);
		XYTaskDataset xyDataSet = new XYTaskDataset(collection);
		if (jobGanntPlot != null) {
			SymbolAxis localSymbolAxis = new SymbolAxis("Tools", toolName);
			localSymbolAxis.setGridBandsVisible(false);
			jobGanntPlot.setDomainAxis(localSymbolAxis);
		}

		return xyDataSet;

	}

	protected Date date(long millis) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis * 1000);
		return calendar.getTime();
	}

	private XYTaskDataset getToolGanntDataset(ArrayList<String> tools) {
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		ArrayList<String> selectedToolGrouptemp;
		if (selectedToolGroup == null || selectedToolGroup.size() == 0) {
			selectedToolGrouptemp = new ArrayList<String>();
			selectedToolGrouptemp.add(toolGroupDataset[0].get(0).toolGroupName);
		} else {
			selectedToolGrouptemp = selectedToolGroup;
		}

		for (String toolGroupName : selectedToolGrouptemp) {
			if(toolGroupDataset[0]
					.getToolGroupData(toolGroupName)==null)
				continue;
			for (int j = 0; j < toolGroupDataset[0]
					.getToolGroupData(toolGroupName).toolNum; j++) {
				for (int i = 0; i < times; i++) {
					ToolGroupData toolGroupData = toolGroupDataset[i]
							.getToolGroupData(toolGroupName);
					ToolData toolData = toolGroupData.get(j);
					final TaskSeries taskSeries = new TaskSeries(
							toolData.toolName + "_" + (i + 1));
					tools.add(toolData.toolName + "_" + (i + 1));
					collection.add(taskSeries);
					for (StartEnd d : toolData.setupTimeDataset) {
						if (d.startEqualsToEnd())
							continue;
						MyTask t = new MyTask("Setup",
								date((long) d.getStart()),
								date((long) d.getEnd()));
						// t.setDescription("Setup "+t.getDuration().getStart().toString()+"-"+t.getDuration().getEnd().toString());

						taskSeries.add(t);
					}
					for (StartEnd d : toolData.processTimeDataset) {
						if (d.startEqualsToEnd())
							continue;
						MyTask t = new MyTask("Processing",
								date((long) d.getStart()),
								date((long) d.getEnd()));
						t.setJobs(d.getOthers());
						taskSeries.add(t);
					}
					for (StartEnd d : toolData.blockTimeDataset) {
						if (d.startEqualsToEnd())
							continue;
						MyTask t = new MyTask("Blocking",
								date((long) d.getStart()),
								date((long) d.getEnd()));
						taskSeries.add(t);
					}
					for (StartEnd d : toolData.interruptTimeDataset) {
						if (d.startEqualsToEnd())
							continue;
						MyTask t = new MyTask("Interrupted",
								date((long) d.getStart()),
								date((long) d.getEnd()));
						taskSeries.add(t);
					}
				}
			}
		}

		XYTaskDataset xyDataSet = new XYTaskDataset(collection);

		if (toolGanntPlot != null) {
			toolName = tools.toArray(new String[0]);
			SymbolAxis localSymbolAxis = new SymbolAxis("Tools", toolName);
			localSymbolAxis.setGridBandsVisible(false);
			toolGanntPlot.setDomainAxis(localSymbolAxis);
		}

		return xyDataSet;

	}

	class MyTask extends Task {
		String jobs = "";

		public String getJobs() {
			return jobs;
		}

		public void setJobs(String jobs) {
			this.jobs = jobs;
		}

		public MyTask(String description, Date start, Date end) {
			super(description, start, end);
			// TODO Auto-generated constructor stub
		}

	}

}
