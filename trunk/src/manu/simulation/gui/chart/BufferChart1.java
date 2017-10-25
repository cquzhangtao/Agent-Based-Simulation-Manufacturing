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

import manu.simulation.result.data.BufferDataset;

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


public class BufferChart1 extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6514170000800030833L;
	BufferDataset bufferDataset;
	JFreeChart chart;
	JFreeChart barchart;
	JPanel tp;
	JPanel tb;
	JTree tree;
	XYPlot xyplot;
	CategoryPlot barplot;


	JRadioButton both;
	JRadioButton max;
	JRadioButton average;
	String chartTitle;
	
	int typeIndex=1;
	int[] selectedBuffer;

	public BufferChart1(BufferDataset bufferDataset) {
	
		// timer.start();
		
		this.bufferDataset=bufferDataset;
		setResizable(false);
		getContentPane().setLayout(null);
		setBounds(200, 200, 800, 500);
		tp = new JPanel();
		tp.setBounds(180, 0, 610, 490);
				
		createTree(bufferDataset);

		chart = ChartFactory.createXYLineChart(bufferDataset.get(0).bufferName, "Time(day)", "Buffer size(lots)", getDataset(0),
				PlotOrientation.VERTICAL, false, false, false);
		
		xyplot=(XYPlot) chart.getPlot();
		
		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBounds(2, 2, 600, 470);
		tp.setLayout(null);

		tp.add(chartPanel);

		tp.setVisible(false);
		getContentPane().add(tp);
		
		tb = new JPanel();
		tb.setBounds(180, 0, 610, 490);

		barchart = ChartFactory.createBarChart("Average Buffer Size",
				"Buffer", "Buffer Size (lots)",
				getDataset(bufferDataset,null,typeIndex), PlotOrientation.VERTICAL,
				true, true, false);
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
		chartPanel1.setBounds(2, 2, 600, 420);
		tb.add(chartPanel1);
		
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(100, 425, 450, 30);
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

	private void createTree(final BufferDataset bufferDataset) {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Buffer");

		for (int i = 0; i < bufferDataset.size(); i++) {
			DefaultMutableTreeNode buffer = new DefaultMutableTreeNode(
					bufferDataset.get(i).bufferName);
			root.add(buffer);
		}
		tree = new JTree(root);

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {

				//ArrayList<Integer> selectedBuffer = new ArrayList<Integer>();
				selectedBuffer = tree.getSelectionRows();
				if (selectedBuffer == null||selectedBuffer.length<1)
					return;

				if (selectedBuffer.length > 1) {
					tb.setVisible(true);
					tp.setVisible(false);
					barplot.setDataset(getDataset(bufferDataset,selectedBuffer,typeIndex));
				} else if (selectedBuffer.length == 1&&selectedBuffer[0]>0) {
					tb.setVisible(false);
					tp.setVisible(true);
					chart.setTitle(bufferDataset.get(selectedBuffer[0]-1).bufferName);
					xyplot.setDataset(getDataset(selectedBuffer[0]-1));
				} else if(selectedBuffer.length == 1&&selectedBuffer[0]==0) {
					tb.setVisible(true);
					tp.setVisible(false);
					barplot.setDataset(getDataset(bufferDataset,null,typeIndex));	
				}
				
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
		//series.add(0, 0);
		//series.add(bufferDataset.get(index).changeTime.get(0)/3600.0/24, 0);
		for(int i=0;i<bufferDataset.get(index).bufferSizebyDay.size();i++)
		{
			series.add(i,bufferDataset.get(index).bufferSizebyDay.get(i));
			series.add(i+1,	bufferDataset.get(index).bufferSizebyDay.get(i));
		}

		dataset.addSeries(series);

		return dataset;
	}

	public CategoryDataset getDataset(BufferDataset data,int[]selectedBuffer,int type) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		String series1 = "Average";
		String series2 = "Max";
		if(selectedBuffer==null||selectedBuffer.length<2){
		for (int i = 0; i < data.size(); i++) {
			if(type==0||type==1)
			result.addValue(data.get(i).avgBufferSize, series1, data.get(i).bufferName);
			if(type==0||type==2)
			result.addValue(data.get(i).maxBufferSize, series2, data.get(i).bufferName);
		}}
		else{
			for(int i=0;i<selectedBuffer.length;i++)
			{
				if(selectedBuffer[i]==0){
					//if(type==0||type==1)
						//result.addValue(data.avgBufferSize, series1, "All");
					//if(type==0||type==2)
						//result.addValue(data.maxBufferSize, series2, "All");
					
				}else{
				if(type==0||type==1)
				result.addValue(data.get(selectedBuffer[i]-1).avgBufferSize, series1, data.get(selectedBuffer[i]-1).bufferName);
				if(type==0||type==2)
				result.addValue(data.get(selectedBuffer[i]-1).maxBufferSize, series2, data.get(selectedBuffer[i]-1).bufferName);
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
				typeIndex=1;
				barchart.setTitle("Average Buffer Size");
			} else if (rButton == max) {
				typeIndex=2;
				barchart.setTitle("Max Buffer Size");

			} else {
				typeIndex=0;
				barchart.setTitle("Max and Average Buffer Size");
			}
			tb.setVisible(true);
			tp.setVisible(false);
			barplot.setDataset(getDataset(bufferDataset,selectedBuffer,typeIndex));
			
		}

	}


}
