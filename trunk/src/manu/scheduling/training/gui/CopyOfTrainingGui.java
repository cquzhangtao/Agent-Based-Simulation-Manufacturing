package manu.scheduling.training.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import manu.scheduling.DecisionMaker;
import manu.scheduling.training.BPNetwork;
import manu.scheduling.training.Kmeans;
import manu.scheduling.training.KmeansField;
import manu.scheduling.training.Training;
import manu.scheduling.training.data.FlowControlTrainingDataset;
import manu.scheduling.training.data.TrainingDatasetMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CopyOfTrainingGui<T> extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Training<T> training;
	JTabbedPane mainTabbedPane;
	Map<String, DecisionMaker<T>> decisonMakers;
	String filePath;
	String fileName = "";
	ProgressFrame pf;

	public CopyOfTrainingGui() {
		Runnable rrr = new Runnable() {
			private JLabel tip;
			JLabel jl;

			public void run() {
				setTitle("Training");
				// setResizable(false);
				 setBounds(200, 100, 800, 600);
				// setPreferredSize(new Dimension(800, 600));
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
				JMenuItem read = new JMenuItem("Read...");
				read.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						JFileChooser fc = new JFileChooser(".//model");
						FileNameExtensionFilter filter = new FileNameExtensionFilter(
								"Training Data (*.tnd)", "tnd");
						fc.setFileFilter(filter);
						fc.setDialogType(JFileChooser.OPEN_DIALOG);
						fc.setMultiSelectionEnabled(true);
						int a = fc.showOpenDialog(CopyOfTrainingGui.this);
						if (a != JFileChooser.APPROVE_OPTION)
							return;
						final File[] file = fc.getSelectedFiles();
						if (file == null|| file.length < 1)
							return;
						ProgressBarMe progress = new ProgressBarMe(
								CopyOfTrainingGui.this) {
							@Override
							public void task() {
								// TODO Auto-generated method stub
								TrainingDatasetMap<T> d = readData(file);
								if (d == null) {
									JOptionPane.showMessageDialog(null,
											"Read data unsucessfully!");
									return;
								}
								setTitle("Training - " + fileName);
								training = new Training<T>(d);
								ArrayList<String[]> names=d.getDataNumbers();
								String str= "<html><body><pre>";
								str+="&nbsp;&nbsp;Group&#9;number<br>";
								for(String[]s:names){
									str+="&nbsp;&nbsp;"+s[0]+"&#9;"+s[1]+"<br>";
								}
								str+="&nbsp;&nbsp;Please start to train!<br>";
								str+= "</pre></html></body>";
								tip.setText(str);
								CopyOfTrainingGui.this.getContentPane().remove(jl);
							}
						};
						progress.start();

					}
				});
				
				JMenuItem generator = new JMenuItem("Generator...");
				generator.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						JFileChooser fc = new JFileChooser(".//model");
						FileNameExtensionFilter filter = new FileNameExtensionFilter(
								"Raw Data (*.rtd)", "rtd");
						fc.setFileFilter(filter);
						fc.setDialogType(JFileChooser.OPEN_DIALOG);
						fc.setMultiSelectionEnabled(true);
						int a = fc.showOpenDialog(CopyOfTrainingGui.this);
						if (a != JFileChooser.APPROVE_OPTION)
							return;
						final File[] file = fc.getSelectedFiles();
						if (file == null|| file.length < 1)
							return;
						ProgressBarMe progress = new ProgressBarMe(
								CopyOfTrainingGui.this) {
							@Override
							public void task() {
								// TODO Auto-generated method stub
								FlowControlTrainingDataset d = readRawData(file);
								if (d == null) {
									JOptionPane.showMessageDialog(null,
											"Read data unsucessfully!");
									return;
								}
								String sd = new java.text.SimpleDateFormat("_yyyyMMddHHmmss")
								.format(new Date());
							
								try{
//								FileOutputStream outStream = new FileOutputStream(
//										agentModel.getReleaseAgent().getSimulatorInfo().currentModelPath
//												+ agentModel.getReleaseAgent().getSimulatorInfo().currentModelName + "//"
//												+ agentModel.getReleaseAgent().getSimulatorInfo().currentModelName + sd
//												+ ".rtd");
//								ObjectOutputStream objectOutputStream = new ObjectOutputStream(
//										outStream);
//								objectOutputStream.writeObject(trainingDataset);
//								
//								outStream.close();
								
								FileOutputStream outStream = new FileOutputStream(
										filePath+ "//"
												+ fileName 
												+ "sequencing.tnd");
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(
										outStream);
								objectOutputStream.writeObject(d.createSequencingTrainingDataset());
								
								outStream.close();
								
								}catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								


							}
						};
						progress.start();

					}
				});


				JMenuItem start = new JMenuItem("Start");
				start.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						if (training == null) {
							JOptionPane.showMessageDialog(null,
									"Please read data first!");
							return;
						}
						mainTabbedPane.removeAll();

						pf = new ProgressFrame(CopyOfTrainingGui.this);
						pf.setVisible(true);
						new MyThread().start();

					}
				});
				JMenuItem open = new JMenuItem("Open...");
				open.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						JFileChooser fc = new JFileChooser(".//model");
						FileNameExtensionFilter filter = new FileNameExtensionFilter(
								"Decision Maker (*.mak)", "mak");
						fc.setFileFilter(filter);
						fc.setDialogType(JFileChooser.OPEN_DIALOG);
						int a = fc.showOpenDialog(CopyOfTrainingGui.this);
						if (a != JFileChooser.APPROVE_OPTION)
							return;
						final File file = fc.getSelectedFile();
						if (file == null)
							return;

						ProgressBarMe s = new ProgressBarMe(CopyOfTrainingGui.this) {
							@Override
							public void task() {
								// TODO Auto-generated method stub
								decisonMakers = openFile(file);
								if (decisonMakers == null) {
									JOptionPane.showMessageDialog(null,
											"Open data unsucessfully!");
									return;
								}
								setTitle("Training - " + fileName);
								mainTabbedPane.removeAll();
								createGraphic();
							}
						};
						s.start();
					}
				});
				JMenuItem exit = new JMenuItem("Exit");
				exit.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						System.exit(0);
					}
				});
				// JPopupMenu pm=new JPopupMenu();

				JMenu menue = new JMenu("Training");
				// menue.add(read);
				menue.add(start);
				menue.add(new JMenuItem("Setting"));

				// menue.add(exit);

				JMenu system = new JMenu("System");
				system.add(open);
				system.add(exit);
				JMenu data = new JMenu("Data");
				data.add(generator);
				data.add(read);
				JMenuBar mbar = new JMenuBar();
				mbar.add(system);
				mbar.add(data);
				mbar.add(menue);
				mbar.add(new JMenu("Window"));
				mbar.add(new JMenu("Help"));

				setJMenuBar(mbar);
				mainTabbedPane = new JTabbedPane();
				JPanel jp = new JPanel();
				jp.setLayout(new BorderLayout());
				jp.setBorder(LineBorder.createGrayLineBorder());
				jp.setBackground(Color.white);
				mainTabbedPane.addTab("Start", jp);
				ImageIcon img = new ImageIcon(".\\picture\\home.jpg");
				jl = new JLabel(img); 
				tip = new JLabel("  Please read the data!");
				//tip.setSize(new Dimension(200,jp.getHeight()));
				jp.add(tip, BorderLayout.PAGE_START);

				jp.add(jl, BorderLayout.CENTER);
				getContentPane().add(mainTabbedPane);
			}
		};
		SwingUtilities.invokeLater(rrr);

	}
	
	public FlowControlTrainingDataset readRawData(File[] files) {

		filePath = files[0].getParent();
		fileName = files[0].getName().substring(0, files[0].getName().lastIndexOf("."));
		FileInputStream freader = null;
		ObjectInputStream objectInputStream = null;
		FlowControlTrainingDataset trainingDatasetMap = null;
		for(File file:files){
		try {
			freader = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(freader);
			if(trainingDatasetMap==null){
				trainingDatasetMap=(FlowControlTrainingDataset) objectInputStream.readObject();
			}
			else
			{
				//trainingDatasetMap.combine(FlowControlTrainingDataset) objectInputStream.readObject());
			}
			objectInputStream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		

		return trainingDatasetMap;

	}

	public TrainingDatasetMap<T> readData(File[] files) {

		filePath = files[0].getParent();
		fileName = files[0].getName().substring(0, files[0].getName().lastIndexOf("."));
		FileInputStream freader = null;
		ObjectInputStream objectInputStream = null;
		TrainingDatasetMap<T> trainingDatasetMap = null;
		for(File file:files){
		try {
			freader = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(freader);
			if(trainingDatasetMap==null){
				trainingDatasetMap=(TrainingDatasetMap<T>) objectInputStream.readObject();
			}
			else
			{
				trainingDatasetMap.combine((TrainingDatasetMap<T>) objectInputStream.readObject());
			}
			objectInputStream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		

		return trainingDatasetMap;

	}

	@SuppressWarnings("unchecked")
	public Map<String, DecisionMaker<T>> openFile(File file) {

		// filePath = file.getParent();
		fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
		FileInputStream freader = null;
		try {
			freader = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(freader);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Map<String, DecisionMaker<T>> dr = (Map<String, DecisionMaker<T>>) objectInputStream.readObject();
			objectInputStream.close();
			return dr;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private void createGraphic() {

		for (DecisionMaker<T> dispatcher : decisonMakers.values()) {
			JTabbedPane subTabPane = new JTabbedPane();
			this.mainTabbedPane.addTab(" " + dispatcher.getGroupName()
					+ " ", subTabPane);
			if (dispatcher.getKmeans() != null) {
				Kmeans<T> kmeans = dispatcher.getKmeans();
				//kmeans.removeEmptyClass();
				JTabbedPane subTabPane1 = new JTabbedPane();
				subTabPane.addTab("Cluster", subTabPane1);
				JPanel convergencyPanel = new JPanel();
				convergencyPanel.setLayout(new GridBagLayout());
				subTabPane1.addTab("Convergency", convergencyPanel);
				setKmeansConvergencyPanel(kmeans, convergencyPanel);
				// JPanel clusterCenterPanel = new JPanel();
				// clusterCenterPanel.setLayout(new GridBagLayout());
				// subTabPane1.addTab("Class", clusterCenterPanel);
				// setCenterDataPanel(kmeans, clusterCenterPanel);
				JPanel clusterDataPanel = new JPanel();
				clusterDataPanel.setLayout(new GridBagLayout());
				subTabPane1.addTab("Data", clusterDataPanel);
				setClusterDataPanel(kmeans, clusterDataPanel);

				for (int i = 0; i < kmeans.getFinalClassNumber(); i++) {
					JPanel classDataPanel = new JPanel();
					classDataPanel.setLayout(new GridBagLayout());
					subTabPane1.addTab("Data" + (i + 1), classDataPanel);
					setClassDataPanel(kmeans, i, classDataPanel);
				}

			}
			int index = 1;
			for (BPNetwork<T> bp : dispatcher.getBpNetworks()) {
				JTabbedPane subTabPane1 = new JTabbedPane();
				subTabPane.addTab("Network" + index, subTabPane1);
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
				index++;
			}
		}
	}

	// private void setCenterDataPanel(Kmeans<TrainingData> kmeans,
	// JPanel clusterCenterPanel) {
	// Object[][]cellData=new
	// Object[kmeans.getClassNum()][kmeans.getFeatureNum()+2];
	//
	// Double[][] centers = kmeans.getUnnormalizedCenter();
	// for (int i = 0; i < kmeans.getClassNum(); i++) {
	// cellData[i][0]=(i+1);
	// cellData[i][1]=kmeans.getResult()[i].size();
	// if(kmeans.getResult()[i].size()>0){
	// for (int j = 0; j < kmeans.getFieldNum(); j++) {
	// cellData[i][j+2]=centers[i][j];
	// }
	// }
	// }
	// Object[] header=new Object[kmeans.getFeatureNum()+2];
	// System.arraycopy(kmeans.getFieldNames().toArray(), 0, header, 2,
	// kmeans.getFieldNum());
	// header[0]="Class";
	// header[1]="DataNum";
	// MyJTable table=new MyJTable(this,cellData,header);
	// JScrollPane s = new JScrollPane(table);
	// GridBagConstraints c = new GridBagConstraints();
	// c.fill=GridBagConstraints.BOTH;
	// c.weighty=1;
	// c.weightx=1;
	// clusterCenterPanel.add(s,c);
	//
	//
	// }

	private void setClassDataPanel(Kmeans<T> kmeans, int classIndex,
			JPanel classDataPanel) {
		java.util.List<String> headerList = kmeans.getFieldNames();
		// kmeans.getFieldNameStringIncBP(kmeans.getMinMax()[0]);
		Object[][] cellData = new Object[kmeans.getResult()[classIndex].size()][headerList
				.size()];
		// create the binding from List to MyJTable
		//Field[] fields = new TrainingData().getClass().getDeclaredFields();
		Field[] fields = kmeans.getResult()[0].get(0).getClass().getDeclaredFields();
		int rowIndex = 0;
		for (T data : kmeans.getResult()[classIndex]) {
			int colIndex = 0;
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					if (field.getAnnotation(KmeansField.class) != null)
					/* ||field.getAnnotation(BPField.class) != null) */{
						if (field.get(data).getClass().isArray()) {
							double[] d = (double[]) field.get(data);
							for (int i = 0; i < d.length; i++) {
								cellData[rowIndex][colIndex] = d[i];
								colIndex++;
							}
						} else {
							cellData[rowIndex][colIndex] = field
									.getDouble(data);
							colIndex++;
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			rowIndex++;
		}
		Object[] header;
		header = headerList.toArray();

		MyJTable table = new MyJTable(this, cellData, header);
		// table.setPreferredScrollableViewportSize(new Dimension(760, 420));
		JScrollPane s = new JScrollPane(table);
		// s.setPreferredSize(new Dimension(760, 440));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = .7;
		classDataPanel.add(s, c);

		@SuppressWarnings("unchecked")
		ChartPanel cp = xyLineChartSingleSeries(
				"",
				"times  Class" + (classIndex + 1),
				"radius",
				new ArrayList[] { kmeans.getMaxRadius()[classIndex],
						kmeans.getAvgRadius()[classIndex] }, new String[] {
						"max", "avg" });
		cp.setPreferredSize(new Dimension(
				(int) (classDataPanel.getWidth() * 0.3), classDataPanel
						.getHeight() ));
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.3;
		classDataPanel.add(cp, c);



	}

	private void setClusterDataPanel(Kmeans<T> kmeans,
			JPanel clusterDataPanel) {
		// JPanel tablePanel = new JPanel();
		Double[][] inputData = kmeans.getUnnormalizedData();
		Object[][] cellData = new Object[inputData.length][kmeans
				.getFeatureNum()];

		for (int i = 0; i < inputData.length; i++) {
			for (int j = 0; j < kmeans.getFeatureNum(); j++) {
				cellData[i][j] = inputData[i][j].doubleValue();
			}
		}
		String[] header = new String[kmeans.getFeatureNum()];
		System.arraycopy(kmeans.getFieldNames().toArray(), 0, header, 0,
				kmeans.getFeatureNum());

		MyJTable table = new MyJTable(this, cellData, header);

		JScrollPane s = new JScrollPane(table);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.8;
		clusterDataPanel.add(s, c);

		JPanel minMaxPanel = new JPanel();
		minMaxPanel.setBackground(Color.white);
		minMaxPanel.setBorder(LineBorder.createGrayLineBorder());
		JScrollPane js = new JScrollPane(minMaxPanel);
		// js.setPreferredSize(new Dimension(170, 400));
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.2;
		clusterDataPanel.add(js, c);

		JLabel minMaxL = new JLabel();
		minMaxPanel.add(minMaxL);
		String str = "<html><body>";
		str += "Data Number: " + inputData.length + "<hr>";
		str += "Max and Min<br><hr>";
		double[] min = kmeans.getMinMaxMatrix()[0];
		double[] max = kmeans.getMinMaxMatrix()[1];
		for (int i = 0; i < kmeans.getFeatureNum(); i++) {

			try {
				String name = kmeans.getFieldNames().get(i);
				double minv = min[i];
				double maxv = max[i];
				str += String.format("%s<br>(%.2f,%.2f)<br>", name, minv, maxv);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		str += "</body></html>";
		minMaxL.setText(str);

	}

	private void setKmeansConvergencyPanel(Kmeans<T> kmeans,
			JPanel convergencyPanel) {

		Object[][] cellData = new Object[kmeans.getFinalClassNumber()][kmeans
				.getFeatureNum() + 2];

		Double[][] centers = kmeans.getUnnormalizedCenter();
		for (int i = 0; i < kmeans.getFinalClassNumber(); i++) {
			cellData[i][0] = (i + 1);
			cellData[i][1] = kmeans.getResult()[i].size();
			if (kmeans.getResult()[i].size() > 0) {
				for (int j = 0; j < kmeans.getFieldNum(); j++) {
					cellData[i][j + 2] = centers[i][j];
				}
			}
		}
		Object[] header = new Object[kmeans.getFeatureNum() + 2];
		System.arraycopy(kmeans.getFieldNames().toArray(), 0, header, 2,
				kmeans.getFieldNum());
		header[0] = "Class";
		header[1] = "DataNum";
		MyJTable table = new MyJTable(this, cellData, header);
		JScrollPane s = new JScrollPane(table);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = .7;
		convergencyPanel.add(s, c);

		ChartPanel cp = xyLineChartSingleSeries("Convergency", "times",
				"Error", kmeans.getErrors());

		cp.setPreferredSize(new Dimension(
				(int) (convergencyPanel.getWidth() * 0.31), convergencyPanel
						.getHeight() ));
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.3;
		convergencyPanel.add(cp, c);


	}

	private void setTrainingDataPanel(BPNetwork<T> bp,
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

	private void setTestDataPanel(BPNetwork<T> bp,
			JPanel testDataPanel) {
		// testDataPanel.setLayout(new BorderLayout());
		// JPanel tablePanel = new JPanel();
		// tablePanel.setPreferredSize(new Dimension(780, testDataPanel
		// .getHeight()));
		// testDataPanel.add(tablePanel, BorderLayout.WEST);
		// JPanel minMaxPanel = new JPanel();
		// minMaxPanel
		// .setPreferredSize(new Dimension(5, testDataPanel.getHeight()));
		//
		// testDataPanel.add(minMaxPanel, BorderLayout.EAST);

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

	private void setConvergencyPanel(BPNetwork<T> bp,
			JPanel convergencyPanel) {
		// convergencyPanel.setLayout(new BorderLayout());
		ArrayList<Double>[] dataset=new ArrayList[3];
		dataset[0]=bp.getErrors();
		dataset[1]=bp.getOutputTestErrors();
		dataset[2]=bp.getOutputValidateErrors();
		ChartPanel cp = xyLineChartSingleSeries("Output Layer", "times",
				"error",dataset,new String[]{"Train","Test","Validate"} );
		// cp.setPreferredSize(new Dimension(610,
		// convergencyPanel.getHeight()));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 0.45;
		c.gridheight=3;
		convergencyPanel.add(cp, c);
		cp = xyLineChartSingleSeries("Hidden Layer", "times", "error",
				bp.getHiddenErrors());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.33;
		c.weightx = 0.45;
		c.gridy=0;
		convergencyPanel.add(cp, c);
		
		cp = xyLineChartSingleSeries("Fail Number", "times", "Fail Number",
				bp.getIncreasedNumList());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.33;
		c.weightx = 0.45;
		c.gridy=1;
		convergencyPanel.add(cp, c);
		cp = xyLineChartSingleSeries("Accuracy", "times", "Accuracy",
				bp.getAccuracyList());
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.33;
		c.weightx = 0.45;
		c.gridy=2;
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
//		str += " max epoch:" + bp.getEpoch() + "<br>";
//		str += " learning rate:" + bp.getEta() + "<br>";
//		str += " momentum:" + bp.getMomentum() + "<br>";
//		str += " min Gradient:" + String.format("%.6f",bp.getGradient())+ "<br>";
//		str += " max fail num :" + String.format("%d",bp.getMaxIncreasedNum())+ "<br>";
//		str += "<hr>";
//		str += " training data num:" + bp.getTraningDataNum() + "<br>";	
//		str += " test data num:" + bp.getTestDataNum() + "<br>";
//		str += " validating data num:" + bp.getValidateDataNum() + "<br>";
//		str += " test error:" + String.format("%.3f", bp.getGlobalError()) + "<br>";
//		str += " test accuracy:" + String.format("%.3f", bp.getAccuracy()) + "<br>";
//		str += "<hr>";
		str += bp.getEndReason() + "<br>";	
		str += "</body></html>";

		jl.setText(str);
		jp1.add(jl);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = .1;
		c.gridheight=3;
		convergencyPanel.add(jp1, c);

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

	public ChartPanel xyLineChartSingleSeries(String title, String xName,
			String yName, ArrayList<Double>[] data, String[] seriesName) {

		XYSeriesCollection result = new XYSeriesCollection();
		for (int j = 0; j < data.length; j++) {
			XYSeries series1 = new XYSeries(seriesName[j]);
			for (int i = 0; i < data[j].size(); i++) {
				series1.add(i, data[j].get(i));
			}
			result.addSeries(series1);
		}

		JFreeChart chart = ChartFactory.createXYLineChart(title, xName, yName,
				result, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}

	public void saveDispatchers() {
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(filePath + "//" + fileName
					+ ".mak");
			ObjectOutputStream objectOutputStream = null;
			objectOutputStream = new ObjectOutputStream(outStream);
			objectOutputStream.writeObject(decisonMakers);
			outStream.close();
			
			for(DecisionMaker<T> dis:decisonMakers.values()){
				dis.clear();
			}
			outStream = new FileOutputStream(filePath + "//" + fileName
					+ "light.mak");
			objectOutputStream = new ObjectOutputStream(outStream);
			objectOutputStream.writeObject(decisonMakers);
			outStream.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	class MyThread extends Thread {

		public void run() {
			decisonMakers = training.train(pf);
			//createGraphic();
			pf.setProgess(97);
			saveDispatchers();
			pf.setProgess(100);
			pf.setVisible(false);
		}
	}
}
