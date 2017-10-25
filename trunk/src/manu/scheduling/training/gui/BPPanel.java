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
import manu.scheduling.training.BPField;
import manu.scheduling.training.BPInfo;
import manu.scheduling.training.BPNetwork;
import manu.scheduling.training.ClusterInfo;
import manu.scheduling.training.Kmeans;
import manu.scheduling.training.KmeansField;
import manu.scheduling.training.Normalizer;
import manu.scheduling.training.data.TrainingDataset;

public class BPPanel<T> extends JBusyPanel {

	// private TrainingDataset<T> trainingDataset;
	private JFrame frame;
	private JPanel convPanel;
	private JPanel eroPanel;
	private JPanel failPanel;
	private JPanel accuracyPanel;
	private JPanel trainDataPanel;
	private JPanel validateDataPanel;
	private JPanel testDataPanel;
	private DecisionMaker<T> decisionMaker;
	private BPNetwork<T> network;
	

	public BPPanel(JFrame frame, List<T> dataset, BPInfo bpInfo, DecisionMaker<T> decisionMaker, int classIndex) {
		super();
		this.frame = frame;
		this.decisionMaker = decisionMaker;
		if (decisionMaker.getBpNetworks().size()<=classIndex) {
			ArrayList<T> normalizedData = new ArrayList<T>();
			T[] minMax = null;
			try {
				minMax = Normalizer.<T> normalize(dataset, normalizedData, new Class[] { BPField.class });
			} catch (Exception e) {
				e.printStackTrace();
			}
			network = new BPNetwork<T>(normalizedData, bpInfo.clone());
			network.setMinMax(minMax);
			decisionMaker.addNetwork(network);
		} else {
			network = decisionMaker.getBpNetworks().get(classIndex);
		}

		this.setLayout(new BorderLayout());
		JSplitPane js1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		final JSplitPane js2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		final JSplitPane js3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		final BPControlPanel<T> controlPanel = new BPControlPanel<T>(network);
		controlPanel.addBPListener(new AlgorithmControlListener() {

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub
				network.setFinished(true);
				updateUI(network);
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
				
			}
		});

		JTabbedPane dataPane = new JTabbedPane();
		dataPane.addTab("All(" + dataset.size() + ")", new JScrollPane(new MyJTable<T>(frame, dataset, BPField.class)));
		trainDataPanel = new JPanel();
		validateDataPanel = new JPanel();
		testDataPanel = new JPanel();
		trainDataPanel.setLayout(new BorderLayout());
		testDataPanel.setLayout(new BorderLayout());
		validateDataPanel.setLayout(new BorderLayout());
		dataPane.addTab("Trainin Data(" + network.getTraningDataNum() + ")", trainDataPanel);
		 dataPane.addTab("Validate Data(" + network.getValidateDataNum() + ")",
		 validateDataPanel);
		dataPane.addTab("Test Data(" + network.getTestDataNum() + ")", testDataPanel);

		JTabbedPane chart1Pane = new JTabbedPane();
		convPanel = new JPanel();
		convPanel.setLayout(new BorderLayout());
		chart1Pane.addTab("Convergence", convPanel);

		JTabbedPane chart2Pane = new JTabbedPane();
		accuracyPanel = new JPanel();
		accuracyPanel.setLayout(new BorderLayout());
		chart2Pane.addTab("Accuracy", accuracyPanel);
		failPanel = new JPanel();
		failPanel.setLayout(new BorderLayout());
		chart2Pane.addTab("Fail Number", failPanel);
		eroPanel = new JPanel();
		eroPanel.setLayout(new BorderLayout());
		chart2Pane.addTab("Hidden Errors", eroPanel);

		js1.setDividerLocation(75);
		js1.setTopComponent(controlPanel);
		js1.setBottomComponent(js2);

		// js2.setDividerLocation(0.5);
		js2.setTopComponent(js3);
		js2.setBottomComponent(dataPane);

		js3.setLeftComponent(chart1Pane);
		js3.setRightComponent(chart2Pane);

		this.getContentPane().add(js1, BorderLayout.CENTER);

		this.addComponentListener(new ComponentListener() {

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
				controlPanel.setCancled(true);
				//setBusy(false);
				
			}});
		if (network.isFinished()) {
			updateUI(network);
		}

	}

	public void updateUI(BPNetwork<T> bp) {

		// chart1Panel.removeAll();
		convPanel.removeAll();
		ArrayList<Double>[] dataset = new ArrayList[3];
		dataset[0] = bp.getErrors();
		dataset[2] = bp.getOutputTestErrors();
		dataset[1] = bp.getOutputValidateErrors();
		ChartPanel cp = ChartUtilities.xyLineChartMultiSeries(null, "times (" + bp.getEndReason() + ")", "error", dataset, new String[] { "Train", "Validate",
				"Test" });
		convPanel.add(cp, BorderLayout.CENTER);

		eroPanel.removeAll();
		cp = ChartUtilities.xyLineChartSingleSeries("", "Times", "error", bp.getHiddenErrors());
		eroPanel.add(cp, BorderLayout.CENTER);

		failPanel.removeAll();
		cp = ChartUtilities.xyLineChartSingleSeries("", "Times", "Fail Number", bp.getIncreasedNumList());
		failPanel.add(cp, BorderLayout.CENTER);

		accuracyPanel.removeAll();
		cp = ChartUtilities.xyLineChartSingleSeries("", "Times (last:" + String.format("%.2f)", bp.getAccuracy()), "Accuracy", bp.getAccuracyList());
		accuracyPanel.add(cp, BorderLayout.CENTER);

		trainDataPanel.removeAll();
		Double[][] inputData = bp.getUnnormalizedInput();
		Double[][] outputData = bp.getUnnormalizedOutput();
		Object[][] cellData = new Object[inputData.length][bp.getInputNum() + bp.getOutputNum()];

		for (int i = 0; i < bp.getTraningDataNum(); i++) {
			for (int j = 0; j < bp.getInputNum(); j++) {
				cellData[i][j] = inputData[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum()] = outputData[i][j].doubleValue();
			}
		}
		String[] header = new String[bp.getInputNum() + bp.getOutputNum()];
		System.arraycopy(bp.getInputFieldName1().toArray(), 0, header, 0, bp.getInputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header, bp.getInputNum(), bp.getOutputNum());

		trainDataPanel.add(new JScrollPane(new MyJTable<T>(frame, cellData, header)), BorderLayout.CENTER);

		testDataPanel.removeAll();
		inputData = bp.getUnnormalizedTestInput();
		outputData = bp.getUnnormalizedTestOutput();
		Double[][] outputDataResult = bp.getUnnormalizedTestResult();

		cellData = new Object[inputData.length][bp.getInputNum() + 3 * bp.getOutputNum()];

		for (int i = 0; i < bp.getTestDataNum(); i++) {
			for (int j = 0; j < bp.getInputNum(); j++) {
				cellData[i][j] = inputData[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum()] = outputData[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum() + bp.getOutputNum()] = outputDataResult[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum() + 2*bp.getOutputNum()] = (Math.abs(outputDataResult[i][j].doubleValue()-outputData[i][j].doubleValue()))/outputData[i][j].doubleValue();
			}
		}
		header = new String[bp.getInputNum() + 3 * bp.getOutputNum()];
		System.arraycopy(bp.getInputFieldName1().toArray(), 0, header, 0, bp.getInputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header, bp.getInputNum(), bp.getOutputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header, bp.getInputNum() + bp.getOutputNum(), bp.getOutputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header, bp.getInputNum() + 2*bp.getOutputNum(), bp.getOutputNum());
		
		for (int i = bp.getInputNum() + bp.getOutputNum(); i < header.length-bp.getOutputNum(); i++)
			header[i] += "_P";
		for (int i = bp.getInputNum() + 2*bp.getOutputNum(); i < header.length; i++)
			header[i] += "_E";
		testDataPanel.add(new JScrollPane(new MyJTable<T>(frame, cellData, header)), BorderLayout.CENTER);
		
		validateDataPanel.removeAll();
		inputData = bp.getUnnormalizedValidateInput();
		outputData = bp.getUnnormalizedValidateOutput();
		outputDataResult = bp.getUnnormalizedValidateResult();

		cellData = new Object[inputData.length][bp.getInputNum() + 3 * bp.getOutputNum()];

		for (int i = 0; i < bp.getValidateDataNum(); i++) {
			for (int j = 0; j < bp.getInputNum(); j++) {
				cellData[i][j] = inputData[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum()] = outputData[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum() + bp.getOutputNum()] = outputDataResult[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum() + 2*bp.getOutputNum()] = (Math.abs(outputDataResult[i][j].doubleValue()-outputData[i][j].doubleValue()))/outputData[i][j].doubleValue();
			}
		}
		header = new String[bp.getInputNum() + 3 * bp.getOutputNum()];
		System.arraycopy(bp.getInputFieldName1().toArray(), 0, header, 0, bp.getInputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header, bp.getInputNum(), bp.getOutputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header, bp.getInputNum() + bp.getOutputNum(), bp.getOutputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header, bp.getInputNum() + 2*bp.getOutputNum(), bp.getOutputNum());

		for (int i = bp.getInputNum() + bp.getOutputNum(); i < header.length-bp.getOutputNum(); i++)
			header[i] += "_P";
		for (int i = bp.getInputNum() + 2*bp.getOutputNum(); i < header.length; i++)
			header[i] += "_E";
		validateDataPanel.add(new JScrollPane(new MyJTable<T>(frame, cellData, header)), BorderLayout.CENTER);

		
		
		//this.updateUI();

	}

}
