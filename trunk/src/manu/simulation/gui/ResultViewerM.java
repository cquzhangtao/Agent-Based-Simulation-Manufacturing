package manu.simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.Locale;

import javax.swing.JComponent;
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
import org.jfree.chart.plot.PieLabelLinkStyle;
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
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ResultViewerM extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ReleaseDataset releaseDataset;
	ToolGroupDataset toolGroupDataset;
	BufferDataset bufferDataset;
	JobDataset jobDataset;
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
	int[] selectedProduct;
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

	public ResultViewerM(ReportInfo reportInfo, ReleaseDataset releaseDataset,
			ToolGroupDataset toolGroupDataset, BufferDataset bufferDataset,
			JobDataset jobDataset) {

		JComponent.setDefaultLocale(Locale.ENGLISH);
		this.releaseDataset = releaseDataset;
		this.toolGroupDataset = toolGroupDataset;
		this.bufferDataset = bufferDataset;
		this.jobDataset = jobDataset;
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
		range.setRange(0, bufferDataset.allBufferData.maxBufferSize + 2);
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
		range.setRange(0, toolGroupDataset.allWaitDataset.maxWaitTime);
		range.setTickUnit(new NumberTickUnit(
				(int) (toolGroupDataset.allWaitDataset.maxWaitTime / 10)));
		chartPanel = new MyChartPanel(tWaitTimeChart);
		toolTrend.add(chartPanel);

		JFreeChart tInterruptChart = ChartFactory.createXYLineChart(null,
				"Time(" + reportInfo.periodStr + ")", "Interrupt",
				getInterruptDataset(), PlotOrientation.VERTICAL, true, true,
				false);
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
		range.setRange(0, bufferDataset.allBufferData.maxBufferSize + 2);
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

		JFreeChart sUtiChart = ChartFactory.createPieChart(null,
				getUtiDatasetP(), true, true, false);
		sUtiChart.setTitle(new org.jfree.chart.title.TextTitle("Utilization",
				new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)));
		sUtiPlot = (PiePlot) sUtiChart.getPlot();
		sUtiPlot.setLabelLinkStyle(PieLabelLinkStyle.QUAD_CURVE);
		sUtiPlot.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
		sUtiPlot.setNoDataMessage("No data available");

		// sUtiPlot.setCircular(false);
		// sUtiPlot.setLabelGap(0.02);
		// Specify the colors here

		oneToolPanel = new MyChartPanel(sUtiChart);
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
		range.setRange(0, toolGroupDataset.allWaitDataset.maxWaitTime);
		range.setTickUnit(new NumberTickUnit(
				(int) toolGroupDataset.allWaitDataset.maxWaitTime / 10));

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

		jobTabbedPane.addTab("Job Gannt", jobTrend);
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
				releaseDataset.get(0).product, "Time(" + reportInfo.periodStr
						+ ")", "Cycle Time (Hour)", getCycleTimeDataset(null),
				PlotOrientation.VERTICAL, true, true, false);
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

		XYTaskDataset datasetJob = null;
		if (jobDataset != null && jobDataset.size() > 0
				&& jobDataset.get(0).get(0) != null) {
			// collection = getJobGanntDataset(0,0);
			jobname = jobDataset.get(0).get(0).jobName;
			datasetJob = getJobGanntDataset();

		} else {
			toolName = new String[] { "" };
		}

		jobGanntChart = ChartFactory.createXYBarChart(jobname, "Tools", false,
				"Time", datasetJob, PlotOrientation.HORIZONTAL, true, true,
				false);
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
		//XYTaskDataset dataset = getToolGanntDataset(tools);

		JFreeChart toolGanntChart = ChartFactory.createXYBarChart(null,
				"Tools", true, "Time", null, PlotOrientation.HORIZONTAL,
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
		localXYBarRenderer.setBaseOutlinePaint(Color.gray);
		localXYBarRenderer.setDrawBarOutline(true);
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

			for (int j = 0; j < bufferDataset.size(); j++) {
				XYSeries series = new XYSeries(bufferDataset.get(j).bufferName);
				for (int i = 0; i < bufferDataset.get(j).bufferSizebyDay.size(); i++) {
					series.add(i, bufferDataset.get(j).bufferSizebyDay.get(i));
					series.add(i + 1,
							bufferDataset.get(j).bufferSizebyDay.get(i));
				}
				dataset.addSeries(series);
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroup.size(); i++) {

			BufferData data = bufferDataset.getBufferData(selectedToolGroup
					.get(i));
			if (data == null)
				data = bufferDataset.allBufferData;
			XYSeries series = new XYSeries(data.bufferName);
			for (int j = 0; j < data.bufferSizebyDay.size(); j++) {
				series.add(j, data.bufferSizebyDay.get(j));
				series.add(j + 1, data.bufferSizebyDay.get(j));
			}
			dataset.addSeries(series);
		}

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);

		return dataset;
	}

	private XYDataset getWaitTimeDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		if (selectedToolGroup.size() < 1) {

			for (int j = 0; j < toolGroupDataset.size(); j++) {
				XYSeries series = new XYSeries(
						toolGroupDataset.get(j).toolGroupName);
				WaitDataset waitDataset = toolGroupDataset.get(j).waitDataset;
				for (int i = 0; i < waitDataset.size(); i++) {
					series.add(i, waitDataset.waitDatasetbyDay.get(i));
					series.add(i + 1, waitDataset.waitDatasetbyDay.get(i));
				}
				dataset.addSeries(series);
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroup.size(); i++) {
			WaitDataset waitDataset;
			if (toolGroupDataset.getToolGroupData(selectedToolGroup.get(i)) != null)
				waitDataset = toolGroupDataset
						.getToolGroupData(selectedToolGroup.get(i)).waitDataset;
			else
				waitDataset = toolGroupDataset.allWaitDataset;
			XYSeries series = new XYSeries(waitDataset.bufferName);
			for (int j = 0; j < waitDataset.waitDatasetbyDay.size(); j++) {
				series.add(j, waitDataset.waitDatasetbyDay.get(j));
				series.add(j + 1, waitDataset.waitDatasetbyDay.get(j));
			}
			dataset.addSeries(series);
		}

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);

		return dataset;
	}

	private XYDataset getUtiDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		if (selectedToolGroupU.size() < 1) {

			for (int j = 0; j < toolGroupDataset.size(); j++) {
				XYSeries series = new XYSeries(
						toolGroupDataset.get(j).toolGroupName);
				ToolData data = toolGroupDataset.get(j).toolGroupDataSum;
				for (int i = 0; i < data.utiliztionbyDay.size(); i++) {
					series.add(i, data.utiliztionbyDay.get(i));
					series.add(i + 1, data.utiliztionbyDay.get(i));
				}
				dataset.addSeries(series);
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroupU.size(); i++) {
			if (selectedToolGroupT.get(i) == 0) {
				ToolData data = toolGroupDataset.allToolData;
				XYSeries series = new XYSeries("All");
				for (int j = 0; j < data.utiliztionbyDay.size(); j++) {
					series.add(j, data.utiliztionbyDay.get(j));
					series.add(j + 1, data.utiliztionbyDay.get(j));
				}
				dataset.addSeries(series);
			}
			if (selectedToolGroupT.get(i) == 1) {
				ToolData data = toolGroupDataset.get(selectedToolGroupU.get(i));
				XYSeries series = new XYSeries(data.toolGroupName);
				for (int j = 0; j < data.utiliztionbyDay.size(); j++) {
					series.add(j, data.utiliztionbyDay.get(j));
					series.add(j + 1, data.utiliztionbyDay.get(j));
				}
				dataset.addSeries(series);
			}
			if (selectedToolGroupT.get(i) == 2) {
				String str[] = selectedToolGroupU.get(i).split("%");
				ToolGroupData gdata = toolGroupDataset.getToolGroupData(str[1]);
				ToolData data = gdata.get(str[0]);
				XYSeries series = new XYSeries(data.toolName);
				for (int j = 0; j < data.utiliztionbyDay.size(); j++) {
					series.add(j, data.utiliztionbyDay.get(j));
					series.add(j + 1, data.utiliztionbyDay.get(j));
				}
				dataset.addSeries(series);
			}
		}

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);

		return dataset;
	}

	private PieDataset getUtiDatasetP() {
		if (selectedToolGroupT.size() < 1)
			return null;
		DefaultPieDataset dataset = new DefaultPieDataset();
		ToolData data;
		if (selectedToolGroupT.get(0) == 0) {
			data = toolGroupDataset.allToolData;

		} else if (selectedToolGroupT.get(0) == 1) {
			data = toolGroupDataset.get(selectedToolGroupU.get(0));
		} else {
			String str[] = selectedToolGroupU.get(0).split("%");
			ToolGroupData gdata = toolGroupDataset.getToolGroupData(str[1]);
			data = gdata.get(str[0]);

		}
		dataset.setValue("Free", data.freeTime);
		dataset.setValue("Setup", data.setupTime);
		dataset.setValue("Process", data.processTime);
		dataset.setValue("Block", data.blockTime);
		dataset.setValue("Breakdown", data.breakdownTime);
		dataset.setValue("Maintenance", data.maintenanceTime);
		return dataset;

	}

	private XYDataset getInterruptDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		int temp = 0;
		if (selectedToolGroupU.size() < 1) {// at the begining
			for (int m = 0; m < toolGroupDataset.size(); m++) {
				ToolGroupData data = toolGroupDataset.get(m);

				for (int j = 0; j < data.size(); j++) {
					ToolData tData = data.get(j);

					XYSeries series = new XYSeries(tData.toolName);
					series.add(0, 2 * temp + 1);

					for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
						series.add(tData.beginInterruptTime.get(k)
								/ tData.interval, 2 * temp + 1);
						series.add(tData.beginInterruptTime.get(k)
								/ tData.interval, 2 * (temp + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval,
								2 * (temp + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval, 2 * temp + 1);
					}
					try{
					series.add(
							tData.processTimeDataset.get(
									tData.processTimeDataset.size() - 1)
									.getEnd()
									/ tData.interval, 2 * temp + 1);
					}catch(Exception e){
						
					}
					dataset.addSeries(series);
				}
				temp++;
			}
			return dataset;
		}

		for (int i = 0; i < selectedToolGroupU.size(); i++) {
			if (selectedToolGroupT.get(i) == 0) {// selected the root
				temp++;
				for (int m = 0; m < toolGroupDataset.size(); m++) {
					ToolGroupData data = toolGroupDataset.get(m);
					for (int j = 0; j < data.size(); j++) {
						XYSeries series = new XYSeries(data.toolGroupName + j
								+ "_All");
						series.add(0, 2 * temp + 1);
						ToolData tData = data.get(j);
						for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
							series.add(tData.beginInterruptTime.get(k)
									/ tData.interval, 2 * temp + 1);
							series.add(tData.beginInterruptTime.get(k)
									/ tData.interval, 2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval,
									2 * (temp + 1));
							series.add(
									(tData.beginInterruptTime.get(k) + tData.interruptTime
											.get(k)) / tData.interval,
									2 * temp + 1);
						}
						series.add(
								tData.processTimeDataset.get(
										tData.processTimeDataset.size() - 1)
										.getEnd()
										/ tData.interval, 2 * temp + 1);
						dataset.addSeries(series);
					}
				}
			}
			if (selectedToolGroupT.get(i) == 1// selected tool groups
					&& selectedToolGroupU.size() == 1) {
				ToolGroupData data = toolGroupDataset
						.getToolGroupData(selectedToolGroupU.get(i));
				for (int j = 0; j < data.size(); j++) {
					XYSeries series = new XYSeries(data.get(j).toolName);
					series.add(0, 2 * j + 1);
					ToolData tData = data.get(j);
					for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
						series.add(tData.beginInterruptTime.get(k)
								/ tData.interval, 2 * j + 1);
						series.add(tData.beginInterruptTime.get(k)
								/ tData.interval, 2 * (j + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval, 2 * (j + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval, 2 * j + 1);
					}
					series.add(
							tData.processTimeDataset.get(
									tData.processTimeDataset.size() - 1)
									.getEnd()
									/ tData.interval, 2 * j + 1);
					dataset.addSeries(series);

				}
			}
			if (selectedToolGroupT.get(i) == 1// selected tool groups
					&& selectedToolGroupU.size() > 1) {
				temp++;
				ToolGroupData data = toolGroupDataset
						.getToolGroupData(selectedToolGroupU.get(i));
				for (int j = 0; j < data.size(); j++) {
					XYSeries series = new XYSeries(data.toolGroupName + j);
					series.add(0, 2 * temp + 1);
					ToolData tData = data.get(j);
					for (int k = 0; k < tData.beginInterruptTime.size(); k++) {
						series.add(tData.beginInterruptTime.get(k)
								/ tData.interval, 2 * temp + 1);
						series.add(tData.beginInterruptTime.get(k)
								/ tData.interval, 2 * (temp + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval,
								2 * (temp + 1));
						series.add(
								(tData.beginInterruptTime.get(k) + tData.interruptTime
										.get(k)) / tData.interval, 2 * temp + 1);
					}
					series.add(
							tData.processTimeDataset.get(
									tData.processTimeDataset.size() - 1)
									.getEnd()
									/ tData.interval, 2 * temp + 1);
					dataset.addSeries(series);
				}

			}
			if (selectedToolGroupT.get(i) == 2) {// selected tools
				temp++;
				String str[] = selectedToolGroupU.get(i).split("%");
				ToolGroupData gdata = toolGroupDataset.getToolGroupData(str[1]);
				ToolData tData = gdata.get(str[0]);
				XYSeries series = new XYSeries(tData.toolName);
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
				series.add(
						tData.processTimeDataset.get(
								tData.processTimeDataset.size() - 1)
								.getEnd()
								/ tData.interval, 2 * temp + 1);
				temp++;
				dataset.addSeries(series);
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
		if (selectedToolGroup.size() < 1) {
			for (int i = 0; i < bufferDataset.size(); i++) {
				if (bufferStatFlag[0] == 1)
					result.addValue(bufferDataset.get(i).avgBufferSize,
							series1, bufferDataset.get(i).bufferName);
				if (bufferStatFlag[1] == 1)
					result.addValue(bufferDataset.get(i).maxBufferSize,
							series2, bufferDataset.get(i).bufferName);
			}
		} else {
			for (int i = 0; i < selectedToolGroup.size(); i++) {
				BufferData data = bufferDataset.getBufferData(selectedToolGroup
						.get(i));
				if (data == null) {
					if (bufferStatFlag[0] == 1)
						result.addValue(
								bufferDataset.allBufferData.avgBufferSize,
								series1, "All");
					if (bufferStatFlag[1] == 1)
						result.addValue(
								bufferDataset.allBufferData.maxBufferSize,
								series2, "All");

				} else {
					if (bufferStatFlag[0] == 1)
						result.addValue(data.avgBufferSize, series1,
								data.bufferName);
					if (bufferStatFlag[1] == 1)
						result.addValue(data.maxBufferSize, series2,
								data.bufferName);
				}
			}
		}

		return result;
	}

	private CategoryDataset getWaitTimeDatasetS() {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		if (selectedToolGroup.size() < 1) {
			for (int i = 0; i < toolGroupDataset.size(); i++) {
				if (waitTimeStatFlag[0] == 1)
					result.addValue(
							toolGroupDataset.get(i).waitDataset.avgWaitTime,
							series1, toolGroupDataset.get(i).toolGroupName);
				if (waitTimeStatFlag[1] == 1)
					result.addValue(
							toolGroupDataset.get(i).waitDataset.maxWaitTime,
							series2, toolGroupDataset.get(i).toolGroupName);
			}
		} else {
			for (int i = 0; i < selectedToolGroup.size(); i++) {
				// BufferData data=bufferDataset.get(selectedToolGroup.get(i));

				if (toolGroupDataset.getToolGroupData(selectedToolGroup.get(i)) == null) {
					if (waitTimeStatFlag[0] == 1)
						result.addValue(
								toolGroupDataset.allWaitDataset.avgWaitTime,
								series1, "All");
					if (waitTimeStatFlag[1] == 1)
						result.addValue(
								toolGroupDataset.allWaitDataset.maxWaitTime,
								series2, "All");

				} else {
					WaitDataset data = toolGroupDataset
							.getToolGroupData(selectedToolGroup.get(i)).waitDataset;
					if (waitTimeStatFlag[0] == 1)
						result.addValue(data.avgWaitTime, series1,
								data.bufferName);
					if (waitTimeStatFlag[1] == 1)
						result.addValue(data.maxWaitTime, series2,
								data.bufferName);
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
			ToolData data;
			if (selectedToolGroupT.get(i) == 0) {
				data = toolGroupDataset.allToolData;

			} else if (selectedToolGroupT.get(i) == 1) {
				data = toolGroupDataset.get(selectedToolGroupU.get(i));
			} else {
				String str[] = selectedToolGroupU.get(i).split("%");
				ToolGroupData gdata = toolGroupDataset.getToolGroupData(str[1]);
				data = gdata.get(str[0]);
			}
			if (toolUtiStatFlag[0] == 1)
				dataset.addValue(data.freeTime * 100, category1, data.toolName);
			if (toolUtiStatFlag[1] == 1)
				dataset.addValue(data.setupTime * 100, category2, data.toolName);
			if (toolUtiStatFlag[2] == 1)
				dataset.addValue(data.processTime * 100, category3,
						data.toolName);
			if (toolUtiStatFlag[3] == 1)
				dataset.addValue(data.blockTime * 100, category4, data.toolName);
			if (toolUtiStatFlag[4] == 1)
				dataset.addValue(data.breakdownTime * 100, category5,
						data.toolName);
			if (toolUtiStatFlag[5] == 1)
				dataset.addValue(data.maintenanceTime * 100, category6,
						data.toolName);
		}
		return dataset;
	}

	private CategoryDataset getInterruptDatasetS() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (selectedToolGroupU.size() == 0) {
			for (int i = 0; i < toolGroupDataset.size(); i++) {
				dataset.addValue(
						toolGroupDataset.get(i).toolGroupDataSum.interruptNum,
						"Interruption Times",
						toolGroupDataset.get(i).toolGroupName);
			}

			return dataset;
		}

		for (int i = 0; i < selectedToolGroupU.size(); i++) {
			ToolData data;
			if (selectedToolGroupT.get(i) == 0) {
				data = toolGroupDataset.allToolData;

			} else if (selectedToolGroupT.get(i) == 1) {
				data = toolGroupDataset.get(selectedToolGroupU.get(i));
			} else {
				String str[] = selectedToolGroupU.get(i).split("%");
				ToolGroupData gdata = toolGroupDataset.getToolGroupData(str[1]);
				data = gdata.get(str[0]);
			}

			dataset.addValue(data.interruptNum, "Interruption Times",
					data.toolName);
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

		for (int i = 0; i < toolGroupDataset.size(); i++) {
			ToolData data = toolGroupDataset.get(i).toolGroupDataSum;
			if (toolUtiStatFlag[0] == 1)
				dataset.addValue(data.freeTime * 100, category1, data.toolName);
			if (toolUtiStatFlag[1] == 1)
				dataset.addValue(data.setupTime * 100, category2, data.toolName);
			if (toolUtiStatFlag[2] == 1)
				dataset.addValue(data.processTime * 100, category3,
						data.toolName);
			if (toolUtiStatFlag[3] == 1)
				dataset.addValue(data.blockTime * 100, category4, data.toolName);
			if (toolUtiStatFlag[4] == 1)
				dataset.addValue(data.breakdownTime * 100, category5,
						data.toolName);
			if (toolUtiStatFlag[5] == 1)
				dataset.addValue(data.maintenanceTime * 100, category6,
						data.toolName);

		}

		return dataset;
	}

	private XYDataset getWipDataset(int index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int i = 0; i < releaseDataset.size(); i++) {
				XYSeries series = new XYSeries(releaseDataset.get(i).product);
				for (int j = 0; j < releaseDataset.get(i).wipbyDay.size(); j++) {
					series.add(j, releaseDataset.get(i).wipbyDay.get(j));
					series.add(j + 1, releaseDataset.get(i).wipbyDay.get(j));
				}

				dataset.addSeries(series);
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			XYSeries series;
			if (index[j] == 0) {
				series = new XYSeries("All");
				for (int i = 0; i < releaseDataset.allReleaseDataset.wipbyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.allReleaseDataset.wipbyDay.get(i));
					series.add(i + 1,
							releaseDataset.allReleaseDataset.wipbyDay.get(i));
				}

			} else {
				series = new XYSeries(releaseDataset.get(index[j] - 1).product);

				for (int i = 0; i < releaseDataset.get(index[j] - 1).wipbyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.get(index[j] - 1).wipbyDay.get(i));
					series.add(i + 1,
							releaseDataset.get(index[j] - 1).wipbyDay.get(i));
				}
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	private XYDataset getReleaseDataset(int index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int i = 0; i < releaseDataset.size(); i++) {
				XYSeries series = new XYSeries(releaseDataset.get(i).product);
				for (int j = 0; j < releaseDataset.get(i).releaseLotsbyDay
						.size(); j++) {
					series.add(j, releaseDataset.get(i).releaseLotsbyDay.get(j));
					series.add(j + 1,
							releaseDataset.get(i).releaseLotsbyDay.get(j));
				}

				dataset.addSeries(series);
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			XYSeries series;
			if (index[j] == 0) {
				series = new XYSeries("All");
				for (int i = 0; i < releaseDataset.allReleaseDataset.releaseLotsbyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.allReleaseDataset.releaseLotsbyDay
									.get(i));
					series.add(i + 1,
							releaseDataset.allReleaseDataset.releaseLotsbyDay
									.get(i));
				}

			} else {
				series = new XYSeries(releaseDataset.get(index[j] - 1).product);

				for (int i = 0; i < releaseDataset.get(index[j] - 1).releaseLotsbyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.get(index[j] - 1).releaseLotsbyDay
									.get(i));
					series.add(i + 1,
							releaseDataset.get(index[j] - 1).releaseLotsbyDay
									.get(i));
				}
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	private XYDataset getFinishedLotsDataset(int index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int i = 0; i < releaseDataset.size(); i++) {
				XYSeries series = new XYSeries(releaseDataset.get(i).product);
				for (int j = 0; j < releaseDataset.get(i).finishedLotsbyDay
						.size(); j++) {
					series.add(j,
							releaseDataset.get(i).finishedLotsbyDay.get(j));
					series.add(j + 1,
							releaseDataset.get(i).finishedLotsbyDay.get(j));
				}

				dataset.addSeries(series);
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			XYSeries series;
			if (index[j] == 0) {
				series = new XYSeries("All");
				for (int i = 0; i < releaseDataset.allReleaseDataset.finishedLotsbyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.allReleaseDataset.finishedLotsbyDay
									.get(i));
					series.add(i + 1,
							releaseDataset.allReleaseDataset.finishedLotsbyDay
									.get(i));
				}

			} else {
				series = new XYSeries(releaseDataset.get(index[j] - 1).product);

				for (int i = 0; i < releaseDataset.get(index[j] - 1).finishedLotsbyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.get(index[j] - 1).finishedLotsbyDay
									.get(i));
					series.add(i + 1,
							releaseDataset.get(index[j] - 1).finishedLotsbyDay
									.get(i));
				}
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	private XYDataset getCycleTimeDataset(int index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == null) {
			for (int i = 0; i < releaseDataset.size(); i++) {
				XYSeries series = new XYSeries(releaseDataset.get(i).product);
				for (int j = 0; j < releaseDataset.get(i).cycleTimebyDay.size(); j++) {
					series.add(j, releaseDataset.get(i).cycleTimebyDay.get(j));
					series.add(j + 1,
							releaseDataset.get(i).cycleTimebyDay.get(j));
				}

				dataset.addSeries(series);
			}
			return dataset;

		}

		for (int j = 0; j < index.length; j++) {
			XYSeries series;
			if (index[j] == 0) {
				series = new XYSeries("All");
				for (int i = 0; i < releaseDataset.allReleaseDataset.cycleTimebyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.allReleaseDataset.cycleTimebyDay
									.get(i));
					series.add(i + 1,
							releaseDataset.allReleaseDataset.cycleTimebyDay
									.get(i));
				}

			} else {
				series = new XYSeries(releaseDataset.get(index[j] - 1).product);

				for (int i = 0; i < releaseDataset.get(index[j] - 1).cycleTimebyDay
						.size(); i++) {
					series.add(i,
							releaseDataset.get(index[j] - 1).cycleTimebyDay
									.get(i));
					series.add(i + 1,
							releaseDataset.get(index[j] - 1).cycleTimebyDay
									.get(i));
				}
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	private void createProductTree() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Product");

		for (int i = 0; i < releaseDataset.size(); i++) {
			DefaultMutableTreeNode product = new DefaultMutableTreeNode(
					releaseDataset.get(i).product);
			root.add(product);
		}
		productTree = new JTree(root);

		productTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		productTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				selectedProduct = productTree.getSelectionRows();
				if (selectedProduct == null || selectedProduct.length < 1)
					return;

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

		for (int i = 0; i < toolGroupDataset.size(); i++) {
			DefaultMutableTreeNode toolgroup = new DefaultMutableTreeNode(
					toolGroupDataset.get(i).toolGroupName);
			root.add(toolgroup);
			for (int j = 0; j < toolGroupDataset.get(i).size(); j++) {

				DefaultMutableTreeNode tool = new DefaultMutableTreeNode(
						toolGroupDataset.get(i).get(j).toolName);
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
					oneToolPanel.setVisible(true);
					multiToolPanel.setVisible(false);
					sUtiPlot.setDataset(getUtiDatasetP());
					Color[] colors = { Color.gray, Color.blue, Color.green,
							Color.yellow, Color.red, Color.pink };
					manu.simulation.gui.chart.PieRenderer renderer1 = new manu.simulation.gui.chart.PieRenderer(
							colors);
					renderer1.setColor(sUtiPlot,
							(DefaultPieDataset) getUtiDatasetP());
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

		for (int i = 0; i < jobDataset.size(); i++) {
			DefaultMutableTreeNode toolgroup = null;
			for (int j = 0; j < jobDataset.get(i).size(); j++) {

				if (j == 0) {
					toolgroup = new DefaultMutableTreeNode(new MyNode(0, i,
							jobDataset.get(i).get(j).jobType));
					root.add(toolgroup);
				}

				DefaultMutableTreeNode tool = new DefaultMutableTreeNode(
						new MyNode(i, j, jobDataset.get(i).get(j).jobName));

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

	private CategoryDataset getWipDataset(ReleaseDataset data,
			int[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		if (selectedBuffer == null || selectedBuffer.length < 1) {
			for (int i = 0; i < data.size(); i++) {
				if (wipStatFlag[0] == 1)
					result.addValue(data.get(i).avgWip, series1,
							data.get(i).product);
				if (wipStatFlag[1] == 1)
					result.addValue(data.get(i).maxWip, series2,
							data.get(i).product);
			}
		} else {
			for (int i = 0; i < selectedBuffer.length; i++) {
				if (selectedBuffer[i] == 0) {
					if (wipStatFlag[0] == 1)
						result.addValue(data.allReleaseDataset.avgWip, series1,
								"All");
					if (wipStatFlag[1] == 1)
						result.addValue(data.allReleaseDataset.maxWip, series2,
								"All");
				} else {
					if (wipStatFlag[0] == 1)
						result.addValue(data.get(selectedBuffer[i] - 1).avgWip,
								series1,
								data.get(selectedBuffer[i] - 1).product);
					if (wipStatFlag[1] == 1)
						result.addValue(data.get(selectedBuffer[i] - 1).maxWip,
								series2,
								data.get(selectedBuffer[i] - 1).product);
				}
			}
		}

		return result;
	}

	private CategoryDataset getCycleTimeDataset(ReleaseDataset data,
			int[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		String series3 = "Min";
		String series4 = "Raw";
		if (selectedBuffer == null || selectedBuffer.length < 1) {
			for (int i = 0; i < data.size(); i++) {
				if (cycleTimeStatFlag[1] == 1)
					result.addValue(data.get(i).avgCycleTime, series1,
							data.get(i).product);
				if (cycleTimeStatFlag[2] == 1)
					result.addValue(data.get(i).maxCycleTime, series2,
							data.get(i).product);
				if (cycleTimeStatFlag[0] == 1)
					result.addValue(data.get(i).minCycleTime, series3,
							data.get(i).product);
				if (cycleTimeStatFlag[3] == 1)
					result.addValue(data.get(i).rawProcessTime, series4,
							data.get(i).product);
			}
		} else {
			for (int i = 0; i < selectedBuffer.length; i++) {
				if (selectedBuffer[i] == 0) {
					if (cycleTimeStatFlag[1] == 1)
						result.addValue(data.allReleaseDataset.avgCycleTime,
								series1, "All");
					if (cycleTimeStatFlag[2] == 1)
						result.addValue(data.allReleaseDataset.maxCycleTime,
								series2, "All");
					if (cycleTimeStatFlag[0] == 1)
						result.addValue(data.allReleaseDataset.minCycleTime,
								series3, "All");
					// if (cycleTimeStatFlag[3]==1)
					// result.addValue(data.allReleaseDataset.rawProcessTime,
					// series4,
					// "All");
				} else {
					if (cycleTimeStatFlag[1] == 1)
						result.addValue(
								data.get(selectedBuffer[i] - 1).avgCycleTime,
								series1,
								data.get(selectedBuffer[i] - 1).product);
					if (cycleTimeStatFlag[2] == 1)
						result.addValue(
								data.get(selectedBuffer[i] - 1).maxCycleTime,
								series2,
								data.get(selectedBuffer[i] - 1).product);
					if (cycleTimeStatFlag[0] == 1)
						result.addValue(
								data.get(selectedBuffer[i] - 1).minCycleTime,
								series3,
								data.get(selectedBuffer[i] - 1).product);
					if (cycleTimeStatFlag[3] == 1)
						result.addValue(
								data.get(selectedBuffer[i] - 1).rawProcessTime,
								series4,
								data.get(selectedBuffer[i] - 1).product);
				}
			}
		}

		return result;
	}

	private CategoryDataset getReleaseDataset(ReleaseDataset data,
			int[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Released";
		if (selectedBuffer == null || selectedBuffer.length < 1) {
			for (int i = 0; i < data.size(); i++) {
				result.addValue(data.get(i).releasedLotNum, series1,
						data.get(i).product);
			}
		} else {
			for (int i = 0; i < selectedBuffer.length; i++) {
				if (selectedBuffer[i] == 0) {
					result.addValue(data.allReleaseDataset.releasedLotNum,
							series1, "All");
				} else {

					result.addValue(
							data.get(selectedBuffer[i] - 1).releasedLotNum,
							series1, data.get(selectedBuffer[i] - 1).product);
				}
			}
		}

		return result;
	}

	private CategoryDataset getFinishedLotsDataset(ReleaseDataset data,
			int[] selectedBuffer) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Finished";
		if (selectedBuffer == null || selectedBuffer.length < 1) {
			for (int i = 0; i < data.size(); i++) {
				result.addValue(data.get(i).finishedLotNum, series1,
						data.get(i).product);
			}
		} else {
			for (int i = 0; i < selectedBuffer.length; i++) {
				if (selectedBuffer[i] == 0) {
					result.addValue(data.allReleaseDataset.finishedLotNum,
							series1, "All");
				} else {

					result.addValue(
							data.get(selectedBuffer[i] - 1).finishedLotNum,
							series1, data.get(selectedBuffer[i] - 1).product);
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

			JobData data = jobDataset.get(selectedJobP.get(j)).get(
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

	private XYTaskDataset getToolGanntDataset(ArrayList<String> tools) {
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		ArrayList<String> selectedToolGrouptemp;
		if (selectedToolGroup == null || selectedToolGroup.size() == 0) {
			selectedToolGrouptemp = new ArrayList<String>();
			selectedToolGrouptemp.add(toolGroupDataset.get(0).toolGroupName);
		} else {
			selectedToolGrouptemp = selectedToolGroup;
		}
		for (String toolGroupName : selectedToolGrouptemp) {
			ToolGroupData toolGroupData = toolGroupDataset
					.getToolGroupData(toolGroupName);
			if(toolGroupData==null)
				continue;
			for (ToolData toolData : toolGroupData.getToolData()) {
				final TaskSeries taskSeries = new TaskSeries(toolData.toolName);
				tools.add(toolData.toolName);
				collection.add(taskSeries);
				for (StartEnd d : toolData.setupTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					MyTask t = new MyTask("Setup", date((long) d.getStart()),
							date((long) d.getEnd()));
					taskSeries.add(t);
				}
				for (StartEnd d : toolData.processTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					MyTask t = new MyTask("Processing",
							date((long) d.getStart()), date((long) d.getEnd()));
					t.setJobs(d.getOthers());
					taskSeries.add(t);
				}
				for (StartEnd d : toolData.blockTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					MyTask t = new MyTask("Blocking",
							date((long) d.getStart()), date((long) d.getEnd()));
					taskSeries.add(t);
				}
				for (StartEnd d : toolData.interruptTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					MyTask t = new MyTask("Interrupted",
							date((long) d.getStart()), date((long) d.getEnd()));
					taskSeries.add(t);
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

	private TaskSeriesCollection getToolGanntDataset() {
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		final TaskSeries setuptaskSeries = new TaskSeries("Setup");
		final TaskSeries processtaskSeries = new TaskSeries("Processing");
		final TaskSeries blocktaskSeries = new TaskSeries("Blocking");
		final TaskSeries interrupttaskSeries = new TaskSeries("Interrupted");
		collection.add(setuptaskSeries);
		collection.add(processtaskSeries);
		collection.add(blocktaskSeries);
		collection.add(interrupttaskSeries);
		for (ToolGroupData toolGroupData : toolGroupDataset.toolGroupDataset) {

			for (ToolData toolData : toolGroupData.getToolData()) {
				// final TaskSeries taskSeries = new
				// TaskSeries(toolData.toolName);
				// tools.add(toolData.toolName);
				int index = 0;
				Task t = null;
				for (StartEnd d : toolData.setupTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					if (index == 0) {
						t = new Task(toolData.toolName,
								date((long) d.getStart()),
								date((long) d.getEnd()));
					} else {
						t.addSubtask(new Task(toolData.toolName + index,
								date((long) d.getStart()), date((long) d
										.getEnd())));
					}
					index++;
					setuptaskSeries.add(t);
				}
				index = 0;
				for (StartEnd d : toolData.processTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					if (index == 0) {
						t = new Task(toolData.toolName,
								date((long) d.getStart()),
								date((long) d.getEnd()));
					} else {
						t.addSubtask(new Task(toolData.toolName + index,
								date((long) d.getStart()), date((long) d
										.getEnd())));
					}
					index++;
					processtaskSeries.add(t);
				}
				index = 0;
				for (StartEnd d : toolData.blockTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					if (index == 0) {
						t = new Task(toolData.toolName,
								date((long) d.getStart()),
								date((long) d.getEnd()));
					} else {
						t.addSubtask(new Task(toolData.toolName + index,
								date((long) d.getStart()), date((long) d
										.getEnd())));
					}
					index++;
					blocktaskSeries.add(t);
				}
				index = 0;
				for (StartEnd d : toolData.interruptTimeDataset) {
					if (d.startEqualsToEnd())
						continue;
					if (index == 0) {
						t = new Task(toolData.toolName,
								date((long) d.getStart()),
								date((long) d.getEnd()));
					} else {
						t.addSubtask(new Task(toolData.toolName + index,
								date((long) d.getStart()), date((long) d
										.getEnd())));
					}
					index++;
					interrupttaskSeries.add(t);
				}
			}
		}

		return collection;

	}

	protected Date date(long millis) {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis * 1000 + 43L * 365 * 24 * 3600 * 1000);
		return calendar.getTime();
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
