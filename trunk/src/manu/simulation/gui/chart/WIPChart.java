package manu.simulation.gui.chart;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import manu.simulation.result.data.ReleaseDataset;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class WIPChart extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7437375696808537141L;
	ReleaseDataset releaseDataset;
	JFreeChart chart;
	JFreeChart barchart;
	JFreeChart multiXYchart;
	JPanel tp;
	JPanel tb;
	JTree tree;
	XYPlot xyplot;
	XYPlot multixyplot;
	CategoryPlot barplot;

	JRadioButton both;
	JRadioButton max;
	JRadioButton average;
	String chartTitle;

	int typeIndex = 1;
	int[] selectedBuffer;

	public WIPChart(ReleaseDataset releaseDataset) {

		// timer.start();

		this.releaseDataset = releaseDataset;
		setResizable(false);
		getContentPane().setLayout(null);
		setBounds(200, 200, 800, 500);
		tp = new JPanel();
		tp.setBounds(180, 0, 610, 490);

		createTree(releaseDataset);

		chart = ChartFactory.createXYLineChart(releaseDataset.get(0).product,
				"Time(day)", "WIP Level(lots)", getDataset(0),
				PlotOrientation.VERTICAL, false, true, false);

		xyplot = (XYPlot) chart.getPlot();

		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBounds(2, 2, 600, 470);
		tp.setLayout(null);

		tp.add(chartPanel);

		tp.setVisible(false);
		getContentPane().add(tp);

		tb = new JPanel();
		tb.setBounds(180, 0, 610, 490);
		multiXYchart = ChartFactory.createXYLineChart(
				releaseDataset.get(0).product, "Time(day)", "WIP Level(lots)",
				getDataset(null), PlotOrientation.VERTICAL, true, true, false);

		multixyplot = (XYPlot) multiXYchart.getPlot();

		// we put the chart into a panel
		ChartPanel chartPanel1 = new ChartPanel(multiXYchart);
		chartPanel1.setBounds(2, 2, 600, 220);
		tb.setLayout(null);

		tb.add(chartPanel1);

		barchart = ChartFactory.createBarChart("Average WIP Level", "Product",
				"WIP Level (lots)",
				getDataset(releaseDataset, null, typeIndex),
				PlotOrientation.VERTICAL, true, true, false);
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

		ChartPanel chartPanel2 = new ChartPanel(barchart);
		tb.setLayout(null);
		chartPanel2.setBounds(2, 225, 600, 210);
		tb.add(chartPanel2);

		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(100, 435, 450, 30);
		jp.setBorder(LineBorder.createGrayLineBorder());
		JLabel label = new JLabel("Type");
		label.setBounds(50, 5, 80, 20);
		jp.add(label);
		CustomListener c1 = new CustomListener();
		ButtonGroup type = new ButtonGroup();
		average = new JRadioButton("Average");
		average.setSelected(true);
		average.setBounds(100, 5, 100, 20);
		average.addActionListener(c1);
		max = new JRadioButton("Max");
		max.setBounds(210, 5, 100, 20);
		max.addActionListener(c1);
		both = new JRadioButton("Both");
		both.setBounds(310, 5, 100, 20);
		both.addActionListener(c1);
		type.add(max);
		type.add(average);
		type.add(both);
		jp.add(max);
		jp.add(average);
		jp.add(both);

		tb.add(jp);
		getContentPane().add(tb);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setVisible(true);
		// }
		// };
		// SwingUtilities.invokeLater(addIt);
	}

	private void createTree(final ReleaseDataset releaseDataset) {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Product");

		for (int i = 0; i < releaseDataset.size(); i++) {
			DefaultMutableTreeNode product = new DefaultMutableTreeNode(
					releaseDataset.get(i).product);
			root.add(product);
		}
		tree = new JTree(root);

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {

				// ArrayList<Integer> selectedBuffer = new ArrayList<Integer>();
				selectedBuffer = tree.getSelectionRows();
				if (selectedBuffer == null || selectedBuffer.length < 1)
					return;

				if (selectedBuffer.length > 1) {
					tb.setVisible(true);
					tp.setVisible(false);
					barplot.setDataset(getDataset(releaseDataset,
							selectedBuffer, typeIndex));
					multixyplot.setDataset(getDataset(selectedBuffer));

				} else if (selectedBuffer.length == 1) {
					tb.setVisible(false);
					tp.setVisible(true);
					if (selectedBuffer[0] > 0)
						chart.setTitle(releaseDataset
								.get(selectedBuffer[0] - 1).product);
					else
						chart.setTitle("All");
					xyplot.setDataset(getDataset(selectedBuffer[0] - 1));
				}
				// else if(selectedBuffer.length == 1&&selectedBuffer[0]==0) {
				//
				//
				// tb.setVisible(true);
				// tp.setVisible(false);
				// barplot.setDataset(getDataset(releaseDataset,null,typeIndex));
				// }

				else {
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

	public XYDataset getDataset(int index) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("");
		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if (index == -1) {
			for (int i = 0; i < releaseDataset.allReleaseDataset.wipbyDay
					.size(); i++) {
				series.add(i, releaseDataset.allReleaseDataset.wipbyDay.get(i));
				series.add(i + 1,
						releaseDataset.allReleaseDataset.wipbyDay.get(i));
			}
			dataset.addSeries(series);
			return dataset;
		}

		for (int i = 0; i < releaseDataset.get(index).wipbyDay.size(); i++) {
			series.add(i, releaseDataset.get(index).wipbyDay.get(i));
			series.add(i + 1, releaseDataset.get(index).wipbyDay.get(i));
		}

		dataset.addSeries(series);

		return dataset;
	}

	public XYDataset getDataset(int index[]) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		// series.add(0, 0);
		// series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		if(index==null){
			for(int i=0;i<releaseDataset.size();i++){
				XYSeries series = new XYSeries(releaseDataset.get(i).product);
				for (int j = 0; j < releaseDataset.get(i).wipbyDay.size(); j++) {
					series.add(j, releaseDataset.get(i).wipbyDay.get(j));
					series.add(j + 1,
							releaseDataset.get(i).wipbyDay.get(j));
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
				series = new XYSeries(releaseDataset.get(index[j]-1).product);

				for (int i = 0; i < releaseDataset.get(index[j]-1).wipbyDay
						.size(); i++) {
					series.add(i, releaseDataset.get(index[j]-1).wipbyDay.get(i));
					series.add(i + 1,
							releaseDataset.get(index[j]-1).wipbyDay.get(i));
				}
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	public CategoryDataset getDataset(ReleaseDataset data,
			int[] selectedBuffer, int type) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		if (selectedBuffer == null || selectedBuffer.length < 2) {
			for (int i = 0; i < data.size(); i++) {
				if (type == 0 || type == 1)
					result.addValue(data.get(i).avgWip, series1,
							data.get(i).product);
				if (type == 0 || type == 2)
					result.addValue(data.get(i).maxWip, series2,
							data.get(i).product);
			}
		} else {
			for (int i = 0; i < selectedBuffer.length; i++) {
				if (selectedBuffer[i] == 0) {
					if (type == 0 || type == 1)
						result.addValue(data.allReleaseDataset.avgWip, series1,
								"All");
					if (type == 0 || type == 2)
						result.addValue(data.allReleaseDataset.maxWip, series2,
								"All");
				} else {
					if (type == 0 || type == 1)
						result.addValue(data.get(selectedBuffer[i] - 1).avgWip,
								series1,
								data.get(selectedBuffer[i] - 1).product);
					if (type == 0 || type == 2)
						result.addValue(data.get(selectedBuffer[i] - 1).maxWip,
								series2,
								data.get(selectedBuffer[i] - 1).product);
				}
			}
		}

		return result;
	}

	class CustomListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			JRadioButton rButton = (JRadioButton) arg0.getSource();
			if (rButton == average) {
				typeIndex = 1;
				barchart.setTitle("Average WIP Level");
			} else if (rButton == max) {
				typeIndex = 2;
				barchart.setTitle("Max WIP Level");

			} else {
				typeIndex = 0;
				barchart.setTitle("Max and Average WIP Level");
			}
			tb.setVisible(true);
			tp.setVisible(false);
			barplot.setDataset(getDataset(releaseDataset, selectedBuffer,
					typeIndex));

		}

	}

}
