package manu.simulation.gui.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import manu.simulation.result.data.JobData;
import manu.simulation.result.data.JobSubData;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;


public class JobGanttChart extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5898966593007907798L;
	ArrayList<JobData> jobTimeStat;
	JFreeChart chart;
	JTree tree;
	XYPlot plot;
	String[] toolName;

	public JobGanttChart(final ArrayList<JobData> ijobTimeStat,
			final String[] jobType) {

		Runnable addIt = new Runnable() {
			@Override
			public void run() {
				jobTimeStat = ijobTimeStat;

				DefaultMutableTreeNode root = new DefaultMutableTreeNode(
						"Product");

				for (int i = 1; i < jobType.length; i++) {
					DefaultMutableTreeNode jobTypeNode = new DefaultMutableTreeNode(
							jobType[i]);
					root.add(jobTypeNode);
					for (int j = 0; j < jobTimeStat.size(); j++) {
						if (jobTimeStat.get(j).jobType.equals(jobType[i])) {
							DefaultMutableTreeNode job = new DefaultMutableTreeNode(
									jobTimeStat.get(j).jobName);

							jobTypeNode.add(job);
						}
					}
				}
				setResizable(false);
				getContentPane().setLayout(null);
				setBounds(200, 100, 1024, 550);
				tree = new JTree(root);
				tree.getSelectionModel().setSelectionMode(
						TreeSelectionModel.SINGLE_TREE_SELECTION);

				tree.addTreeSelectionListener(new TreeSelectionListener() {
					@Override
					public void valueChanged(TreeSelectionEvent evt) {

						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
								.getLastSelectedPathComponent();
						if (node == null)
							// Nothing is selected.
							return;

						TaskSeriesCollection collection = getDataset(node
								.getUserObject().toString());
						if (collection == null)
							return;

						chart.setTitle(node.getUserObject().toString());

						plot.setDataset(new XYTaskDataset(collection));
						SymbolAxis localSymbolAxis = new SymbolAxis("Tools",
								toolName);
						localSymbolAxis.setGridBandsVisible(false);
						plot.setDomainAxis(localSymbolAxis);

					}
				});
				expandTree(tree);
				JScrollPane scrollpane = new JScrollPane(tree);
				scrollpane.setBounds(0, 0, 200, 520);
				getContentPane().add(scrollpane);

				JPanel tp = new JPanel();
				tp.setBounds(200, 0, 830, 540);
				tp.setLayout(null);
				TaskSeriesCollection collection = null;
				String jobname = "";
				if (jobTimeStat != null && jobTimeStat.size() > 0) {
					collection = getDataset(jobTimeStat.get(0).jobName);
					jobname = jobTimeStat.get(0).jobName;

				}
				XYTaskDataset xyDataSet = null;
				if (collection != null)
					xyDataSet = new XYTaskDataset(collection);
				else
					toolName = new String[] { "" };

				chart = ChartFactory.createXYBarChart(jobname, "Tools", false,
						"Time", xyDataSet, PlotOrientation.HORIZONTAL, false,
						true, false);
				chart.setBackgroundPaint(Color.white);
				plot = (XYPlot) chart.getPlot();

				SymbolAxis localSymbolAxis = new SymbolAxis("Tools", toolName);
				localSymbolAxis.setGridBandsVisible(false);
				plot.setDomainAxis(localSymbolAxis);
				// plot.setRenderer(new MyXYBarRenderer());
				XYBarRenderer localXYBarRenderer = (XYBarRenderer) plot
						.getRenderer();
				localXYBarRenderer.setUseYInterval(true);
				plot.setRangeAxis(new DateAxis("Time"));

				// ValueAxis rangeAxis=plot.getRangeAxis();
				// rangeAxis.setRange(0, 5);
				// rangeAxis.setAutoRange(false);
				ChartUtilities.applyCurrentTheme(chart);

				ChartPanel chartPanel = new ChartPanel(chart);
				chartPanel.setBounds(0, 0, 800, 520);
				tp.add(chartPanel);
				getContentPane().add(tp);
				setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

				setVisible(true);
			}
		};
		SwingUtilities.invokeLater(addIt);
	}

	public void expandTree(JTree tree) {

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

	private TaskSeriesCollection getDataset(String jobName) {
		JobData data = null;
		ArrayList<String> tools;
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		for (int i = 0; i < jobTimeStat.size(); i++) {
			if (jobTimeStat.get(i).jobName.equals(jobName)) {
				data = jobTimeStat.get(i);
				break;
			}
		}
		if (data == null)
			return null;
		tools = new ArrayList<String>();
		// final TaskSeries s1 = new TaskSeries("Release");
		// final Task t1 = new Task("Release", date(data.releaseTime),
		// date(data.firstTransportBeginTime));
		// s1.add(t1);
		// tools.add("Release");
		// collection.add(s1);

		for (int i = 0; i < data.processNum; i++) {
			JobSubData jobStateTime = data.jobSubData[i];

			final TaskSeries s1 = new TaskSeries(String.valueOf(i));
			final Task t11 = new Task("Transport",
					date(data.jobSubData[0].transportBeginTime),
					date(data.jobSubData[0].transportBeginTime));
			s1.add(t11);
			collection.add(s1);
			tools.add("");
			final TaskSeries s3 = new TaskSeries(String.valueOf(i));
			final Task t3 = new Task("Transport",
					date(data.jobSubData[0].transportBeginTime),
					date(data.jobSubData[0].transportBeginTime));
			s3.add(t3);
			collection.add(s3);
			tools.add("");

			int index = collection.indexOf(jobStateTime.toolName);

			if (index == -1) {
				final TaskSeries s2 = new TaskSeries(jobStateTime.toolName);
				collection.add(s2);
				tools.add(jobStateTime.toolName);
				index = collection.indexOf(jobStateTime.toolName);
			}
			final Task t21 = new Task("Transport",
					date(jobStateTime.transportBeginTime),
					date(jobStateTime.waitBeginTime));

			final Task t22 = new Task("Wait", date(jobStateTime.waitBeginTime),
					date(jobStateTime.processBeginTime));
			final Task t23 = new Task("Process",
					date(jobStateTime.processBeginTime),
					date(jobStateTime.blockBeginTime));
			final Task t24 = new Task("Block",
					date(jobStateTime.blockBeginTime),
					date(jobStateTime.nextTransportBeginTime));
			collection.getSeries(index).add(t21);
			collection.getSeries(index).add(t22);
			collection.getSeries(index).add(t23);
			collection.getSeries(index).add(t24);

		}
		toolName = tools.toArray(new String[0]);

		return collection;

	}

	protected Date date(long millis) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis * 1000);
		return calendar.getTime();
	}

	// public class MyXYBarRenderer extends XYBarRenderer {
	// Logger logger = Logger.getLogger(MyXYBarRenderer.class);
	//
	// private static final long serialVersionUID = 1;
	// protected XYDataset dataset;
	//
	// @Override
	// public void drawItem(Graphics2D g2, XYItemRendererState state,
	// Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
	// ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
	// int series, int item, CrosshairState crosshairState, int pass) {
	// this.dataset = dataset;
	// super.drawItem(g2, state, dataArea, info, plot, domainAxis,
	// rangeAxis, dataset, series, item, crosshairState, pass);
	// }
	//
	// @Override
	// public Paint getItemPaint(int row, int column) {
	// Paint result;
	// XYTaskDataset tds = (XYTaskDataset) dataset;
	// TaskSeriesCollection tsc = tds.getTasks();
	// TaskSeries ts = tsc.getSeries(row);
	// Task t = ts.get(column);
	// result = getCategoryPaint(t.getDescription());
	// return result;
	// }
	//
	// private Paint getCategoryPaint(String description) {
	// Paint result = Color.black;
	// if (description.equals("Transport")) {
	// result = Color.gray;
	// } else if (description.equals("Wait")) {
	// result = Color.blue;
	// } else if (description.equals("Process")) {
	// result = Color.green;
	// } else if (description.equals("Block")) {
	// result = Color.yellow;
	// }
	//
	// //logger.debug(description);
	// return result;
	// }
	// }

}
