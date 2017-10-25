package manu.scheduling.training;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;

import manu.scheduling.training.gui.MyJTable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class TestBP extends ApplicationFrame{
public TestBP(String title) {
		super(title);
		// TODO Auto-generated constructor stub
		setPreferredSize(new Dimension(800, 400));
		BPInfo info=new BPInfo();
		info.setEpoch(200);
		info.setHiddenNodeNum(3);
		info.setEta(0.1);
		info.setMomentum(0);
		info.setTolerance(0.0005);
		ArrayList<Data> dataset=new ArrayList<Data>();
		ArrayList<Data> odataset=getDataset();
		Data[]minMax=Normalizer.normalize(odataset, dataset, new Class[]{BPField.class});
		BPNetwork<Data> bp=new BPNetwork<Data>(dataset,info);
		bp.train();
		bp.setMinMax(minMax);
		JTabbedPane subTabPane1 = new JTabbedPane();
		//subTabPane.addTab("Network" + index, subTabPane1);
		this.getContentPane().add(subTabPane1);
		JPanel convergencyPanel = new JPanel();
		convergencyPanel.setLayout(new GridBagLayout());
		subTabPane1.addTab("Convergency", convergencyPanel);
		JPanel trainingDataPanel = new JPanel();
		trainingDataPanel.setLayout(new GridBagLayout());
		subTabPane1.addTab("Training Data", trainingDataPanel);
		JPanel testDataPanel = new JPanel();
		testDataPanel.setLayout(new GridBagLayout());
		subTabPane1.addTab("Test Data", testDataPanel);

		setConvergencyPanel(bp, convergencyPanel);
		setTrainingDataPanel(bp, trainingDataPanel);
		if (bp.getTestDataNum() > 0)
			setTestDataPanel(bp, testDataPanel);
		

	}
public ChartPanel xyLineChartSingleSeries(String title, String xName,
		String yName, ArrayList<Double> data) {

	XYSeriesCollection result = new XYSeriesCollection();

	XYSeries series1 = new XYSeries("a");
	for (int i = 0; i < data.size(); i++) {
		series1.add(i, data.get(i));
	}

	result.addSeries(series1);

	JFreeChart chart = ChartFactory.createXYLineChart(title, xName, yName,
			result, PlotOrientation.VERTICAL, false, false, false);
	// we put the chart into a panel
	chart.getTitle().setFont(new Font("Time New Rome", Font.BOLD, 15));
	ChartPanel chartPanel = new ChartPanel(chart);
	return chartPanel;
}
//
	public static void main(String[] args)  {

//		TrainingData data=new TrainingData();
//		double[]a=new double[]{1,2,3};
//		data.setWipD(a);
//		TrainingData dataq=data.clone();
//		data.getWipD()[0]=-999;
//		data.getWipD()[2]=777;
		
//		TestBP scatterplotdemo4 = new TestBP("Test");
//		scatterplotdemo4.pack();
//		RefineryUtilities.centerFrameOnScreen(scatterplotdemo4);
//		scatterplotdemo4.setVisible(true);
	}
	
	public static ArrayList<Data> getDataset(){
		ArrayList<Data> dataset=new ArrayList<Data>();
		Random rnd=new Random(100);
		int num=0;
		while(num<500){
			dataset.add(new Data(rnd.nextInt(5000),rnd.nextInt(5000)));
			num++;
		}
		
		return dataset;
	}
	private void setTrainingDataPanel(BPNetwork<Data> bp,
			JPanel trainingDataPanel) {
		// trainingDataPanel.setLayout(new BorderLayout());
		// JPanel tablePanel = new JPanel();
		// tablePanel.setPreferredSize(new Dimension(580, trainingDataPanel
		// .getHeight()));
		// trainingDataPanel.add(tablePanel, BorderLayout.WEST);

		Double[][] inputData = bp.getUnnormalizedInput();
		Double[][] outputData = bp.getUnnormalizedOutput();
		Object[][] cellData = new Object[inputData.length][bp.getInputNum()
				+ bp.getOutputNum()];

		for (int i = 0; i < bp.getTraningDataNum(); i++) {
			for (int j = 0; j < bp.getInputNum(); j++) {
				cellData[i][j] = inputData[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum()] = outputData[i][j]
						.doubleValue();
			}
		}
		String[] header = new String[bp.getInputNum() + bp.getOutputNum()];
		System.arraycopy(bp.getInputFieldName1().toArray(), 0, header, 0,
				bp.getInputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header,
				bp.getInputNum(), bp.getOutputNum());

		MyJTable table = new MyJTable(this, cellData, header);

		JScrollPane s = new JScrollPane(table);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = .9;
		trainingDataPanel.add(s, c);

		JPanel minMaxPanel = new JPanel();
		minMaxPanel.setBackground(Color.white);
		minMaxPanel.setBorder(LineBorder.createGrayLineBorder());
		JScrollPane js = new JScrollPane(minMaxPanel);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = .1;
		trainingDataPanel.add(js, c);

		JLabel minMaxL = new JLabel();
		minMaxPanel.add(minMaxL);
		String str = "<html><body>";
		str += "Data Number: " + inputData.length + "<hr>";
		str += "Max and Min<br><hr>";
		double[] min = bp.getInputMinMaxMatrix()[0];
		double[] max = bp.getInputMinMaxMatrix()[1];
		for (int i = 0; i < bp.getInputFieldName1().size(); i++) {
			str += String.format("%s<br>(%.2f,%.2f)<br>", bp
					.getInputFieldName1().get(i), min[i], max[i]);
		}
		min = bp.getOutputMinMaxMatrix()[0];
		max = bp.getOutputMinMaxMatrix()[1];
		for (int i = 0; i < bp.getOutputFieldName1().size(); i++) {
			str += String.format("%s<br>(%.2f,%.2f)<br>", bp
					.getOutputFieldName1().get(i), min[i], max[i]);
		}
		str += "</body></html>";
		minMaxL.setText(str);
	}

	private void setTestDataPanel(BPNetwork<Data> bp,
			JPanel testDataPanel) {

		Double[][] inputData = bp.getUnnormalizedTestInput();
		Double[][] outputData = bp.getUnnormalizedTestOutput();
		Double[][] outputDataResult = bp.getUnnormalizedTestResult();

		Object[][] cellData = new Object[inputData.length][bp.getInputNum() + 2
				* bp.getOutputNum()];

		for (int i = 0; i < bp.getTestDataNum(); i++) {
			for (int j = 0; j < bp.getInputNum(); j++) {
				cellData[i][j] = inputData[i][j].doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum()] = outputData[i][j]
						.doubleValue();
			}
			for (int j = 0; j < bp.getOutputNum(); j++) {
				cellData[i][j + bp.getInputNum() + bp.getOutputNum()] = outputDataResult[i][j]
						.doubleValue();
			}
		}
		String[] header = new String[bp.getInputNum() + 2 * bp.getOutputNum()];
		System.arraycopy(bp.getInputFieldName1().toArray(), 0, header, 0,
				bp.getInputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header,
				bp.getInputNum(), bp.getOutputNum());
		System.arraycopy(bp.getOutputFieldName1().toArray(), 0, header,
				bp.getInputNum() + bp.getOutputNum(), bp.getOutputNum());
		for (int i = bp.getInputNum() + bp.getOutputNum(); i < header.length; i++)
			header[i] += "_P";
		MyJTable table = new MyJTable(this, cellData, header);

		// table.setPreferredSize(new Dimension(600,400));
		// table.setPreferredScrollableViewportSize(new Dimension(730, 420));
		JScrollPane s = new JScrollPane(table);
		// s.setPreferredSize(new Dimension(760, 440));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		testDataPanel.add(s, c);
	}

	private void setConvergencyPanel(BPNetwork<Data> bp,
			JPanel convergencyPanel) {
		// convergencyPanel.setLayout(new BorderLayout());
		ChartPanel cp = xyLineChartSingleSeries("Output Layer", "times",
				"error", bp.getErrors());
		// cp.setPreferredSize(new Dimension(610,
		// convergencyPanel.getHeight()));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.45;
		convergencyPanel.add(cp, c);
		cp = xyLineChartSingleSeries("Hidden Layer", "times", "error",
				bp.getHiddenErrors());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.45;
		convergencyPanel.add(cp, c);

		JPanel jp1 = new JPanel();
		jp1.setBackground(Color.white);
		// jp1.setPreferredSize(new Dimension(162,
		// convergencyPanel.getHeight()));
		jp1.setBorder(LineBorder.createGrayLineBorder());
		// jp1.setLayout(new GridBagLayout());

		JLabel jl = new JLabel("ddddddddddddd");

		String str = "<html><body>";
		str += "<hr>";
		str += " input num: " + bp.getInputNum() + "<br>";
		str += " output num: " + bp.getOutputNum() + "<br>";
		str += " hidden node num: " + bp.getHiddenNodeNum() + "<br>";
		str += "<hr>";
//		str += " epoch:" + bp.getEpoch() + "<br>";
//		str += " learning rate:" + bp.getEta() + "<br>";
//		str += " momentum:" + bp.getMomentum() + "<br>";
		str += " training data num:" + bp.getTraningDataNum() + "<br>";
		str += "<hr>";
		str += " test data num:" + bp.getTestDataNum() + "<br>";
	//	str += " error:" + String.format("%.3f", bp.getGlobalError()) + "<br>";
		str += " accuracy:" + String.format("%.3f", bp.getAccuracy()) + "<br>";
		str += "</body></html>";

		jl.setText(str);
		jp1.add(jl);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = .1;
		convergencyPanel.add(jp1, c);

	}


}
class Data{
	@BPField
	double x;
	@BPField
	double y;
	@BPField
	@BPOutput
	double[] z=new double[2];
	public Data(){
		
	}
	public Data(int x,int y){
		this.x=x;
		this.y=y;
		z[0]=x-y;
		z[1]=x+y;
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
}
