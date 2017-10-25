package manu.simulation.gui.chart;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import manu.simulation.result.data.ToolData;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;


public class ToolUtilChart extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6318207292350080422L;
	ArrayList<ToolData> toolGroupStat;
	JFreeChart chart;
	JFreeChart barchart;
	JPanel tp;
	JPanel tb;
	JTree tree;
	PiePlot plot;
	CategoryPlot barplot;
	ArrayList<String> toolGroupName = new ArrayList<String>();
	ToolData allToolData = new ToolData();

	// ActionListener taskPerformer = new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// // TODO Auto-generated method stub
	// updateData();
	// }
	// };
	// Timer timer = new Timer(10, taskPerformer);
	//
	// public void updateData() {
	// toolGroupStat.clear();
	// for (int i = 0; i < toolGroupName.size(); i++) {
	// ToolTimeStat itoolGroupStat = new ToolTimeStat(
	// toolGroupName.get(i), toolGroupName.get(i));
	// int count = 0;
	// for (int j = 0; j < toolTimeStat.length; j++) {
	// if (toolTimeStat[j].toolGroupName.equals(toolGroupName.get(i))) {
	// itoolGroupStat.blockTime += toolTimeStat[j].blockTime;
	// itoolGroupStat.breakdownTime += toolTimeStat[j].breakdownTime;
	// itoolGroupStat.freeTime += toolTimeStat[j].freeTime;
	// itoolGroupStat.maintenanceTime += toolTimeStat[j].maintenanceTime;
	// itoolGroupStat.processTime += toolTimeStat[j].processTime;
	// itoolGroupStat.setupTime += toolTimeStat[j].setupTime;
	// count++;
	// }
	// }
	// itoolGroupStat.blockTime = itoolGroupStat.blockTime / count;
	// itoolGroupStat.breakdownTime = itoolGroupStat.breakdownTime / count;
	// itoolGroupStat.freeTime = itoolGroupStat.freeTime / count;
	// itoolGroupStat.maintenanceTime = itoolGroupStat.maintenanceTime
	// / count;
	// itoolGroupStat.processTime = itoolGroupStat.processTime / count;
	// itoolGroupStat.setupTime = itoolGroupStat.setupTime / count;
	// toolGroupStat.add(itoolGroupStat);
	// }
	// }

	public ToolUtilChart(ToolData[] itoolTimeStat) {
		// ToolData[] toolTimeStat = itoolTimeStat;
		// timer.start();
		setResizable(false);
		getContentPane().setLayout(null);
		setBounds(200, 200, 800, 500);
		createTree(itoolTimeStat);
		PieDataset dataset = getDataset(itoolTimeStat[0].toolName,
				itoolTimeStat);
		tp = new JPanel();
		tp.setBounds(180, 0, 610, 490);
		chart = ChartFactory.createPieChart(itoolTimeStat[0].toolName
				+ " Utilization", dataset, true, true, false);
		plot = (PiePlot) chart.getPlot();
		plot.setLabelLinkStyle(PieLabelLinkStyle.QUAD_CURVE);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		// Specify the colors here
		Color[] colors = { Color.gray, Color.blue, Color.green, Color.yellow,
				Color.red, Color.pink };
		PieRenderer renderer1 = new PieRenderer(colors);
		renderer1.setColor(plot, (DefaultPieDataset) dataset);

		ChartPanel chartPanel = new ChartPanel(chart);
		tp.setLayout(null);
		chartPanel.setBounds(5, 20, 600, 420);
		tp.add(chartPanel);
		tp.setVisible(false);
		getContentPane().add(tp);

		tb = new JPanel();
		tb.setBounds(180, 0, 610, 490);

		barchart = ChartFactory
				.createBarChart(
						"Utilization",
						"State",
						"Ratio(%)",
						getDataset(toolGroupName.toArray(new String[0]),
								itoolTimeStat), PlotOrientation.VERTICAL, true,
						true, false);

		// set the background color for the chart...
		barchart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		barplot = barchart.getCategoryPlot();
		barplot.setBackgroundPaint(Color.lightGray);
		barplot.setDomainGridlinePaint(Color.white);
		barplot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		// final NumberAxis rangeAxis = (NumberAxis) barplot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		final BarRenderer renderer = (BarRenderer) barplot.getRenderer();
		renderer.setDrawBarOutline(false);

		ChartPanel chartPanel1 = new ChartPanel(barchart);
		tb.setLayout(null);
		chartPanel1.setBounds(0, 0, 610, 460);
		tb.add(chartPanel1);
		getContentPane().add(tb);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setVisible(true);
		// }
		// };
		// SwingUtilities.invokeLater(addIt);
	}

	private void createTree(final ToolData[] toolTimeStat) {
		toolGroupStat = new ArrayList<ToolData>();
		allToolData.toolName="All";
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ToolGroup");
		for (int i = 0; i < toolTimeStat.length; i++) {
			if (!toolGroupName.contains(toolTimeStat[i].toolGroupName)
					&& !toolTimeStat[i].toolGroupName.isEmpty()) {
				toolGroupName.add(toolTimeStat[i].toolGroupName);
			}
		}
		for (int i = 0; i < toolGroupName.size(); i++) {
			DefaultMutableTreeNode toolgroup = new DefaultMutableTreeNode(
					toolGroupName.get(i));
			root.add(toolgroup);
			ToolData itoolGroupStat = new ToolData(toolGroupName.get(i),
					toolGroupName.get(i));
			int count = 0;
			for (int j = 0; j < toolTimeStat.length; j++) {

				if (toolTimeStat[j].toolGroupName.equals(toolGroupName.get(i))) {
					DefaultMutableTreeNode tool = new DefaultMutableTreeNode(
							toolTimeStat[j].toolName);

					toolgroup.add(tool);

					itoolGroupStat.blockTime += toolTimeStat[j].blockTime;
					itoolGroupStat.breakdownTime += toolTimeStat[j].breakdownTime;
					itoolGroupStat.freeTime += toolTimeStat[j].freeTime;
					itoolGroupStat.maintenanceTime += toolTimeStat[j].maintenanceTime;
					itoolGroupStat.processTime += toolTimeStat[j].processTime;
					itoolGroupStat.setupTime += toolTimeStat[j].setupTime;

					allToolData.blockTime += toolTimeStat[j].blockTime;
					allToolData.breakdownTime += toolTimeStat[j].breakdownTime;
					allToolData.freeTime += toolTimeStat[j].freeTime;
					allToolData.maintenanceTime += toolTimeStat[j].maintenanceTime;
					allToolData.processTime += toolTimeStat[j].processTime;
					allToolData.setupTime += toolTimeStat[j].setupTime;

					count++;
				}
			}
			itoolGroupStat.blockTime = itoolGroupStat.blockTime / count;
			itoolGroupStat.breakdownTime = itoolGroupStat.breakdownTime / count;
			itoolGroupStat.freeTime = itoolGroupStat.freeTime / count;
			itoolGroupStat.maintenanceTime = itoolGroupStat.maintenanceTime
					/ count;
			itoolGroupStat.processTime = itoolGroupStat.processTime / count;
			itoolGroupStat.setupTime = itoolGroupStat.setupTime / count;
			toolGroupStat.add(itoolGroupStat);

		}

		allToolData.blockTime /= (1.0 * toolGroupName.size());
		allToolData.breakdownTime /= (1.0 * toolGroupName.size());
		allToolData.freeTime /= (1.0 * toolGroupName.size());
		allToolData.maintenanceTime /= (1.0 * toolGroupName.size());
		allToolData.processTime /= (1.0 * toolGroupName.size());
		allToolData.setupTime /= (1.0 * toolGroupName.size());

		tree = new JTree(root);

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {

				ArrayList<String> selectedTool = new ArrayList<String>();
				TreePath[] paths = tree.getSelectionPaths();
				if (paths == null)
					return;

				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					selectedTool.add(node.getUserObject().toString());
				}
				if (selectedTool.size() > 1) {
					tp.setVisible(false);
					tb.setVisible(true);
					barchart.setTitle("Utilization");
					barplot.setDataset(getDataset(
							selectedTool.toArray(new String[0]), toolTimeStat));
				} else if (selectedTool.size() == 1) {
					tb.setVisible(false);
					tp.setVisible(true);
					chart.setTitle(selectedTool.get(0) + " Utilization");
					plot.setDataset(getDataset(selectedTool.get(0),
							toolTimeStat));
				} else {
					return;
				}

			}
		});
		expandTree(tree);
		JScrollPane scrollpane = new JScrollPane(tree);
		scrollpane.setBounds(2, 2, 180, 470);
		getContentPane().add(scrollpane);
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

	private PieDataset getDataset(String toolName, ToolData[] toolTimeStat) {
		ToolData data = null;
		if (toolName.equals("ToolGroup")) {
			data = allToolData;
		} else {
			for (int i = 0; i < toolGroupStat.size(); i++) {
				if (toolGroupStat.get(i).toolName.equals(toolName)) {
					data = toolGroupStat.get(i);
					break;
				}
			}
			if (data == null) {
				for (int i = 0; i < toolTimeStat.length; i++) {
					if (toolTimeStat[i].toolName.equals(toolName)) {
						data = toolTimeStat[i];
						break;
					}
				}
			}
		}
		if (data == null)
			return null;
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Free", data.freeTime);
		dataset.setValue("Setup", data.setupTime);
		dataset.setValue("Process", data.processTime);
		dataset.setValue("Block", data.blockTime);
		dataset.setValue("Breakdown", data.breakdownTime);
		dataset.setValue("Maintenance", data.maintenanceTime);
		// System.out.println(data.totalTime());
		return dataset;

	}

	private CategoryDataset getDataset(String toolName[],
			ToolData[] toolTimeStat) {

		// column keys...
		final String category1 = "Free";
		final String category2 = "Setup";
		final String category3 = "Process";
		final String category4 = "Block";
		final String category5 = "Breakdown";
		final String category6 = "Maintenance";
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		// row keys...
		for (int i = 0; i < toolName.length; i++) {
			// System.out.println(toolName[i]);
			ToolData data = null;
			if (toolName[i].equals("ToolGroup")) {
				data = allToolData;
			} else {
				for (int j = 0; j < toolGroupStat.size(); j++) {
					if (toolGroupStat.get(j).toolName.equals(toolName[i])) {
						data = toolGroupStat.get(j);
						break;
					}
				}
				if (data == null) {
					for (int j = 0; j < toolTimeStat.length; j++) {
						if (toolTimeStat[j].toolName.equals(toolName[i])) {
							data = toolTimeStat[j];
							break;
						}
					}
				}
			}
			if (data == null)
				break;

			// System.out.println(data.data2String());
			dataset.addValue(data.freeTime * 100, data.toolName, category1);
			dataset.addValue(data.setupTime * 100, data.toolName, category2);
			dataset.addValue(data.processTime * 100, data.toolName, category3);
			dataset.addValue(data.blockTime * 100, data.toolName, category4);
			dataset.addValue(data.breakdownTime * 100, data.toolName, category5);
			dataset.addValue(data.maintenanceTime * 100, data.toolName,
					category6);
		}

		return dataset;

	}

	public  class PieRenderer {
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

}
