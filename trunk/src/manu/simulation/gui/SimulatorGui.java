package manu.simulation.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import manu.model.component.ReleasePolicy;
import manu.simulation.SimController;
import manu.simulation.SimulatorListener;
import manu.simulation.gui.report.ReportInfo;
import manu.simulation.gui.report.ReportXmlData;
import manu.simulation.info.SimulationInfo;
import manu.simulation.others.CommonMethods;
import manu.simulation.result.data.BufferDataset;
import manu.simulation.result.data.JobDataset;
import manu.simulation.result.data.ReleaseDataset;
import manu.simulation.result.data.ToolGroupDataset;



public class SimulatorGui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SimController controller;
	JButton startButton;
	JButton pauseButton;
	JButton stopButton;
	JButton showButton;
	JButton openButton;
	JButton closeButton;
	JLabel realTimeLabel;
	JLabel simulatedTimeLabel;
	JToolBar state;
	JLabel stateStr;

	static Point origin = new Point();

	public SimulatorGui(final SimController controller) {
		
		Runnable addIt = new Runnable() {
			@Override
			public void run() {
				SimulatorGui.this.controller=controller;
				
				setTitle("SIMULATOR");
				setResizable(false);
				setAlwaysOnTop(true);
				getContentPane().setLayout(null);
				setBounds(320, 300, 310, 245);
				// setSize(100,260);
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
				controller.addSimulationListener(new SimulatorListener(){

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						SimulatorGui.this.startSim();
					}

					@Override
					public void onEnd() {
						// TODO Auto-generated method stub
						SimulatorGui.this.endSim();
					}

					@Override
					public void onReset() {
						// TODO Auto-generated method stub
						SimulatorGui.this.resetSim();
					}

					@Override
					public void onStateChange(String state) {
						// TODO Auto-generated method stub
						SimulatorGui.this.updateState(state);
					}

					@Override
					public void onTimeAdvance(long currentTime,long beginTime) {
						// TODO Auto-generated method stub
						SimulatorGui.this.updateTime(currentTime, beginTime);
					}});

				JPanel p = new JPanel();
				p.setLayout(null);
				p.setBorder(LineBorder.createGrayLineBorder());

				p.setBounds(5, 5, 290, 190);
				// p.setLayout(new GridLayout(4, 1, 2, 5));
				Border titleBorder1 = BorderFactory.createTitledBorder("");
				p.setBorder(titleBorder1);

				JPanel p1 = new JPanel();
				p1.setLayout(null);
				// p1.setBorder(LineBorder.createGrayLineBorder());
				p1.setBounds(10, 10, 270, 90);
				// p.setLayout(new GridLayout(4, 1, 2, 5));
				Border titleBorder11 = BorderFactory
						.createTitledBorder("Control");
				p1.setBorder(titleBorder11);

				p.add(p1);

				int y = 25;

				openButton = new JButton("Open");
				openButton.setBounds(15, y, 80, 25);
				showButton = new JButton("Analyze");
				showButton.setBounds(94, y, 80, 25);
				//showButton.setEnabled(false);
				closeButton = new JButton("Close");
				closeButton.setBounds(173, y, 80, 25);
				closeButton.setEnabled(false);

				y = y + 25;
				startButton = new JButton("Start");
				startButton.setBounds(15, y, 80, 25);
				startButton.setEnabled(false);
				pauseButton = new JButton("Pause");
				pauseButton.setBounds(94, y, 80, 25);
				pauseButton.setEnabled(false);

				stopButton = new JButton("Stop");
				stopButton.setEnabled(false);
				stopButton.setBounds(173, y, 80, 25);

				JPanel jp = new JPanel();
				jp.setLayout(null);
				Border titleBorder111 = BorderFactory
						.createTitledBorder("Time");
				jp.setBorder(titleBorder111);
				// jp.setBorder(LineBorder.createGrayLineBorder());
				jp.setBounds(10, 105, 270, 75);

				JLabel jl2 = new JLabel("Simulated:");
				jl2.setBounds(15, 20, 100, 20);
				jp.add(jl2);
				simulatedTimeLabel = new JLabel("00:00:00");
				simulatedTimeLabel.setBounds(105, 20, 150, 20);
				jp.add(simulatedTimeLabel);

				JLabel jl1 = new JLabel("RealWorld:");
				jl1.setBounds(15, 40, 100, 20);
				jp.add(jl1);

				realTimeLabel = new JLabel("00 day 00 hour 00 minute");
				realTimeLabel.setBounds(105, 40, 150, 20);
				jp.add(realTimeLabel);

				p.add(jp);
				state=new JToolBar();
				stateStr=new JLabel("Please open a model or analyze results");

				stateStr.setFont(new Font("Serif", Font.PLAIN, 12)); 
				state.setBounds(0, 202, 310, 15);
				state.add(stateStr);
				state.setVisible(true);
				add(state,BorderLayout.SOUTH);

				closeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						closeButton.setEnabled(false);
						controller.closeModel();
						resetState();
						openButton.setEnabled(true);
						startButton.setEnabled(false);
						startButton.setText("Start");
						stopButton.setEnabled(false);
						pauseButton.setEnabled(false);
						pauseButton.setText("Pause");
//						if(viewer!=null)
//							viewer.setVisible(false);

					}
				});
				openButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						File temp=openModel();
						if(temp==null)
							return;
						int r=controller.openModel(temp);
						if(r==0)
							return;

						startButton.setEnabled(true);
						startButton.setText("Start");
						stopButton.setEnabled(false);
						pauseButton.setEnabled(false);
						pauseButton.setText("Pause");
						closeButton.setEnabled(true);
						openButton.setEnabled(false);
						//showButton.setEnabled(false);
						closeButton.setEnabled(true);

					}
				});
				p1.add(startButton);
				p1.add(pauseButton);
				p1.add(stopButton);
				p1.add(showButton);
				p1.add(closeButton);
				p1.add(openButton);
				getContentPane().add(p, BorderLayout.CENTER);
				startButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						if (startButton.getText().equals("Reset")) {
							controller.resetSim();
							resetSim();
						} else {
							controller.startSim();
							startSim();
						}
					}
					
				});
				stopButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						startButton.setEnabled(true);
						stopButton.setEnabled(false);
						controller.stopSim();
						pauseButton.setEnabled(false);
						pauseButton.setText("Pause");
						startButton.setText("Reset");
						//showButton.setEnabled(true);
						closeButton.setEnabled(true);
					}
				});
				pauseButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						if (pauseButton.getText().equals("Pause")) {
							controller.pauseSim();
							pauseButton.setText("Continue");
							closeButton.setEnabled(true);
						} else {
							controller.continueSim();
							pauseButton.setText("Pause");
							closeButton.setEnabled(false);
						}
					}
				});
				showButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						showButton.setEnabled(false);
						LoadData ld=new LoadData();
						ld.start();
						
					}
				});

				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						origin.x = e.getX();
						origin.y = e.getY();
					}
				});
				addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseDragged(MouseEvent e) {

						Point p = getLocation();
						setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
								- origin.y);
					}
				});

			}
		};
		SwingUtilities.invokeLater(addIt);

	}

	public void endSim() {
		startButton.setText("Reset");
		startButton.setEnabled(true);
		pauseButton.setEnabled(false);
		pauseButton.setText("Pause");
		stopButton.setEnabled(false);
		closeButton.setEnabled(true);
	}

	public void updateTime(long currentTime,long beginTime) {
		realTimeLabel.setText(CommonMethods
				.GetDateStringFromMinutes(currentTime/ 60.0));
		long simulatedTime = (System.currentTimeMillis() - beginTime) / 1000;
		int hour1 = (int) Math.floor(simulatedTime / 3600);
		int minute1 = (int) Math.floor((simulatedTime - hour1 * 3600) / 60);
		int second1 = (int) (simulatedTime - hour1 * 3600 - minute1 * 60);
			
		simulatedTimeLabel.setText(String.format("%3d : %02d : %02d", hour1,
				minute1, second1));

	}
	public void updateState(final String s){

		stateStr.setText(s);
	}
	public void resetState(){

		stateStr.setText("Please open a model or analyze results");
	}
	class LoadData extends Thread
	{
		@Override
		public void run()
		{
			resultViewer();
			showButton.setEnabled(true);
		}
	}
	public void resetSim() {
		startButton.setText("Start");
		simulatedTimeLabel.setText("00:00:00");
		realTimeLabel.setText("00 day 00 hour 00 minute");
//		if(viewer!=null)
//			viewer.setVisible(false);
	}
	public void startSim() {
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		pauseButton.setEnabled(true);
		closeButton.setEnabled(false);
	}
	public File openModel() {

		JFileChooser fc = new JFileChooser(".//model");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Factory Explore Model (*.xls, *.xlsx)", "xls", "xlsx", "xlt");
		FileNameExtensionFilter filter1 = new FileNameExtensionFilter(
				"Process Model (*.mod)", "mod");
		fc.addChoosableFileFilter(filter);
		fc.addChoosableFileFilter(filter1);
		fc.setFileFilter(filter1);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		int a = fc.showOpenDialog(this);
		if (a != JFileChooser.APPROVE_OPTION)
			return null;
		File file = fc.getSelectedFile();
		if (file != null) 
			return file;
		return null;
	}
	public int resultViewer() {

		JFileChooser fc = new JFileChooser(".//model");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Simulation Result File (*.rst)", "rst");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setMultiSelectionEnabled(true);
		int a = fc.showOpenDialog(this);
		if (a != JFileChooser.APPROVE_OPTION)
			return 0;
		File[] files = fc.getSelectedFiles();
		if (files != null && files.length > 0) {
			updateState("Loading simulation results... 0%");
			ReleasePolicy[] rPolicy=new ReleasePolicy[files.length];
			SimulationInfo[] sInfo = new SimulationInfo[files.length];
			ReleaseDataset[] rDataset = new ReleaseDataset[files.length];
			BufferDataset[] bDataset = new BufferDataset[files.length];
			ToolGroupDataset[] tDataset = new ToolGroupDataset[files.length];
			JobDataset[] jDataset = new JobDataset[files.length];
			FileInputStream freader;
			String[] modelName = new String[files.length];
			try {
				String filestr = "";

				for (int i = 0; i < files.length; i++) {
					freader = new FileInputStream(files[i]);
					ObjectInputStream objectInputStream = new ObjectInputStream(
							freader);
					rPolicy[i]=(ReleasePolicy) objectInputStream
					.readObject();
					sInfo[i] = (SimulationInfo) objectInputStream
							.readObject();

					updateState(String.format(
							"Loading simulation results... %d%%",
							(int) (90.0 / files.length * (i + 0.05))));
					rDataset[i] = (ReleaseDataset) objectInputStream
							.readObject();
					updateState(String.format(
							"Loading simulation results... %d%%",
							(int) (90 / files.length * (i + 0.2))));
					bDataset[i] = (BufferDataset) objectInputStream
							.readObject();
					updateState(String.format(
							"Loading simulation results... %d%%",
							(int) (90 / files.length * (i + 0.3))));
					tDataset[i] = (ToolGroupDataset) objectInputStream
							.readObject();
					updateState(String.format(
							"Loading simulation results... %d%%",
							(int) (90 / files.length * (i + 0.65))));
					jDataset[i] = (JobDataset) objectInputStream.readObject();
					updateState(String.format(
							"Loading simulation results... %d%%",
							(90 / files.length * (i + 1))));
					objectInputStream.close();
					sInfo[i].currentModelName = files[i].getName().substring(0,
							files[i].getName().lastIndexOf("."));
					sInfo[i].currentModelPath = files[i].getParent() + "\\";
					filestr += "," + sInfo[i].currentModelName;
					modelName[i] = sInfo[i].currentModelName;
				}
				if (files.length == 1)
					ReportXmlData.saveResult(rPolicy[0],sInfo[0], rDataset[0], bDataset[0],
							tDataset[0]);
				else
					ReportXmlData.saveResult(rPolicy,sInfo, rDataset, bDataset, tDataset);
				ReportInfo rInfo = new ReportInfo();
				rInfo.periodStr=sInfo[0].dataAcqTimeStr;
				updateState("Loading simulation results... 90%");
				rInfo.productResultFile = sInfo[0].currentModelPath
						+ sInfo[0].currentModelName + "_ProductResult.xml";
				rInfo.toolResultFile = sInfo[0].currentModelPath
						+ sInfo[0].currentModelName + "_ToolResult.xml";
				rInfo.summaryResultFile = sInfo[0].currentModelPath
						+ sInfo[0].currentModelName + "_SummaryResult.xml";
				ResultViewerM rf;
				ResultViewer rfm;
				if (files.length == 1) {
					rf = new ResultViewerM(rInfo, rDataset[0], tDataset[0],
							bDataset[0], jDataset[0]);

					rf.setTitle("SimResult - " + sInfo[0].currentModelName);
				} else {
					rfm = new ResultViewer(modelName, rInfo, rDataset,
							tDataset, bDataset, jDataset);
					rfm.setTitle("SimResult - " + filestr.substring(1));
				}
				updateState("Loading simulation results... 100%");
				updateState("Simulation results are loaded");

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} 

		return 0;
	}
}
