package manu.scheduling.training.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import manu.scheduling.DecisionMaker;
import manu.scheduling.training.BPInfo;
import manu.scheduling.training.ClusterInfo;
import manu.scheduling.training.Kmeans;
import manu.scheduling.training.KmeansField;
import manu.scheduling.training.Normalizer;
import manu.scheduling.training.data.TrainingDataset;

public class KmeansPanel<T> extends JBusyPanel {

	private JTabbedPane dataPane;
	private JTabbedPane chart2Pane;
	private JTabbedPane chart1Panel;
	private TrainingDataset<T> trainingDataset;
	private JSplitPane js2;
	private JSplitPane js3;
	JFrame frame;
	private JPanel silPanel;
	private JPanel eroPanel;
	private DecisionMaker<T> decisionMaker;
	private Kmeans<T> kmeans;
	private JPanel eroPanel1;
	private JPanel numPanel;

	public KmeansPanel(final JTabbedPane tabbedPane, JFrame frame, TrainingDataset<T> dataset, ClusterInfo clusterInfo, final BPInfo bpInfo,
			final DecisionMaker<T> decisionMaker) {
		super();
		this.frame = frame;
		this.decisionMaker = decisionMaker;

		if (decisionMaker.getKmeans() == null) {
			ArrayList<T> normalizedData = new ArrayList<T>();
			T[] minMax = null;
			try {
				minMax = Normalizer.<T> normalize(dataset.getTrainingDataset(), normalizedData, new Class[] { KmeansField.class });
			} catch (Exception e) {
				e.printStackTrace();
			}
			kmeans = new Kmeans<T>(normalizedData, clusterInfo.clone());
			kmeans.setMinMax(minMax);
			kmeans.setUnNormalizedData(dataset.getTrainingDataset());

			decisionMaker.setMinMax(minMax);
			decisionMaker.setDataset(dataset);
			decisionMaker.setKmeans(kmeans);
		} else {
			kmeans = decisionMaker.getKmeans();
		}
		this.setLayout(new BorderLayout());
		JSplitPane js1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		js2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		js3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		final KmeansControlPanel<T> controlPanel = new KmeansControlPanel<T>(kmeans);
		controlPanel.addKmeansListener(new AlgorithmControlListener() {

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub
				kmeans.setFinished(true);
				updateUI(kmeans);
				decisionMaker.getBpNetworks().clear();
				addNetworks(tabbedPane, kmeans, bpInfo);
				setBusy(false);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				setBusy(true);

			}

			@Override
			public void onParameterReady() {
				// TODO Auto-generated method stub
				addNumberVsErrorsChart();
				setBusy(false);
			}
		});
		chart1Panel = new JTabbedPane();
		chart2Pane = new JTabbedPane();
		dataPane = new JTabbedPane();

		dataPane.addTab("All(" + dataset.getTrainingDataset().size() + ")", new JScrollPane(new MyJTable<T>(frame, dataset.getTrainingDataset(),
				KmeansField.class)));

		silPanel = new JPanel();		
		silPanel.setLayout(new BorderLayout());
		eroPanel = new JPanel();
		eroPanel.setLayout(new BorderLayout());
		eroPanel1 = new JPanel();
		eroPanel1.setLayout(new BorderLayout());
		
		numPanel = new JPanel();
		numPanel.setLayout(new BorderLayout());

		chart1Panel.addTab("Silhouette", silPanel);
		chart1Panel.addTab("Epoch Errors", eroPanel);
		chart1Panel.addTab("Iterative Errors", eroPanel1);
		chart1Panel.addTab("Class Number vs. Errors", numPanel);

		js1.setDividerLocation(40);
		js1.setTopComponent(controlPanel);
		js1.setBottomComponent(js2);

		// js2.setDividerLocation(0.5);
		js2.setTopComponent(js3);
		js2.setBottomComponent(dataPane);

		js3.setLeftComponent(chart1Panel);
		js3.setRightComponent(chart2Pane);

		this.getContentPane().add(js1, BorderLayout.CENTER);

		js1.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				js2.setDividerLocation(0.5);
				js3.setDividerLocation(0.5);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				js2.setDividerLocation(0.5);
				js3.setDividerLocation(0.5);
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		this.addBusyPanelListener(new BusyPanelListener(){

			@Override
			public void onCancled() {
				// TODO Auto-generated method stub
				controlPanel.setCancled(true);
				//setBusy(false);
			}});
		if(kmeans.isFinished()){
			updateUI(kmeans);
			addNetworks(tabbedPane,kmeans,bpInfo);
		}

	}

	private void addNetworks(JTabbedPane tabbedPane, Kmeans<T> kmeans, BPInfo bpInfo) {
		for (int i = 1; i < tabbedPane.getTabCount();) {
			tabbedPane.remove(i);
		}
		for (int i = 0; i < kmeans.getFinalClassNumber(); i++) {

			tabbedPane.addTab("Network" + String.valueOf(i + 1), new BPPanel<T>(frame, kmeans.getResult()[i], bpInfo, decisionMaker,i));
		}

	}
	
	private void addNumberVsErrorsChart(){
		numPanel.removeAll();
		ChartPanel cp1 = ChartUtilities.xyLineChartSingleSeries("", "Number", "Error",
				kmeans.getClassNumDetermineData());
		numPanel.add(cp1, BorderLayout.CENTER);
		chart1Panel.setSelectedIndex(3);
	}

	public void updateUI(Kmeans<T> kmeans) {

		for (int i = 1; i < dataPane.getTabCount();) {
			dataPane.removeTabAt(i);

		}

		chart2Pane.removeAll();
		// chart1Panel.removeAll();
		silPanel.removeAll();
		eroPanel.removeAll();
		eroPanel1.removeAll();

		silPanel.add(getSilhouetteChart(kmeans.getSilhouette()), BorderLayout.CENTER);
		ChartPanel cp1 = ChartUtilities.xyLineChartSingleSeries("", "Epoch (min:" + String.format("%.2f", kmeans.getMinimalGlobalError()) + ")", "Error",
				kmeans.getGlobalErrors());
		eroPanel.add(cp1, BorderLayout.CENTER);
		
		cp1 = ChartUtilities.xyLineChartSingleSeries("", "Iterate", "Error",
				kmeans.getErrors());
		eroPanel1.add(cp1, BorderLayout.CENTER);
		
		int index = 0;
		for (List<T> data : kmeans.getClassifiedDataset()) {

			dataPane.addTab(String.valueOf(index + 1) + "(" + data.size() + ")", new JScrollPane(new MyJTable<T>(frame, data, KmeansField.class)));
			index++;
		}

		for (int i = 0; i < kmeans.getClassNum(); i++) {
			ChartPanel cp = ChartUtilities.xyLineChartMultiSeries("", "Times  (Class" + (i + 1) + ")", "radius", new ArrayList[] { kmeans.getMaxRadius()[i],
					kmeans.getAvgRadius()[i] }, new String[] { "max", "avg" });
			chart2Pane.addTab(String.valueOf(i + 1), cp);
		}
	}

	private ChartPanel getSilhouetteChart(List<Double>[] dataset) {

		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
		double sum = 0;
		int count = 0;
		for (int i = 0; i < dataset.length; i++) {
			Double[] d = dataset[i].toArray(new Double[0]);
			Arrays.sort(d);

			for (int j = d.length - 1; j > -1; j--) {
				barDataset.addValue(d[j], i + String.valueOf(j), String.valueOf(i + 1));
				sum += d[j];
				count++;
			}

		}

		JFreeChart chart = ChartFactory.createBarChart("", "Class (avg:" + String.format("%.2f)", sum / count), "Value", barDataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				false);

		return new ChartPanel(chart);

	}

}
