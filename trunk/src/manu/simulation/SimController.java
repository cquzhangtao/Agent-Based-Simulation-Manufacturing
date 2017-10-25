package manu.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import manu.agent.AgentBasedModel;
import manu.agent.component.ToolGroup;
import manu.model.ManufactureModel;
import manu.simulation.gui.ResultViewerM;
import manu.simulation.gui.report.ReportInfo;
import manu.simulation.gui.report.ReportXmlData;
import manu.simulation.info.SimulationInfo;
import manu.simulation.others.FileRead;
import manu.simulation.result.SimulationResult;

public class SimController {

	private Simulation simulationAgent;
	private ManufactureModel manuModel;
	private AgentBasedModel agentModel;
	private SimulationResult results;
	private boolean isCollectData = true;
	private boolean showResult = true;
	private int currentSimTimes = 0;
	private SimulationInfo simulatorInfo;

	private List<SimulatorListener> simulationListener = new ArrayList<SimulatorListener>();

	public SimController(final Simulation simulationAgent,
			ManufactureModel manuModel, final AgentBasedModel agentModel,
			SimulationResult results) {
		this.simulationAgent = simulationAgent;
		this.agentModel = agentModel;
		this.manuModel = manuModel;
		this.results = results;
		simulatorInfo = new SimulationInfo();
		FileRead.simulatorInfo(simulatorInfo);
		simulationAgent.addSimulationListener(new SimulationListener() {

			@Override
			public void onEnd() {
				
				SimController.this.onEndSim();
				if(simulationAgent.getSubSimulation()!=null&&simulationAgent.getSubSimulation().isCollectData()){
					updateState("Saving training data ...");
					simulationAgent.getSubSimulation().saveData();
					updateState("Training data saved!");
				}
			}

			@Override
			public void onTimeAdvance() {
				// TODO Auto-generated method stub
				SimController.this.updateTime();
				agentModel.setCurrentTime(simulationAgent.currentTime());
				System.out.println("============================"+simulationAgent.getAdditionalString()+" Advanced==================");
			}

			@Override
			public boolean onEndDecision() {
				// TODO Auto-generated method stub
				if (simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlanTime
						&& (simulatorInfo.planTime * 24 * 60 * 60 < simulationAgent
								.currentTime()))
					return true;
				return false;
			}
		});
	}

	protected void onEndSim() {
		// TODO Auto-generated method stub
		simulatorInfo.runTime = System.currentTimeMillis()
				- simulatorInfo.simulateBeginTime;
		simulatorInfo.simulationTime = simulationAgent.currentTime();
		onEndSim(simulatorInfo, currentSimTimes);
		currentSimTimes++;
		if (currentSimTimes < simulatorInfo.simulationTimes) {
			resetSim();
			startSim();
		} else {
			currentSimTimes = 0;
			simulationAgent.pauseAgent();
		}
	}

	public int openModel(File file) {
		int r = manuModel.loadModel(file);
		if (r != 0) {
			new File(manuModel.getResultFilePath()).mkdirs();
			// 2014
			// simulationAgent.collector.initSampleDataset();
			//
			results.generateStructure(manuModel, simulatorInfo);
			agentModel.generateModel(manuModel, simulatorInfo, results,
					simulationAgent.getSubSimulation());
			//simulationAgent.loadToolBreakdownEvent(manuModel);
			simulatorInfo.currentModelName = manuModel.getModelName();
			simulatorInfo.currentModelPath = manuModel.getModelFilePath();
			updateState("Please start simulation or open a new model");
			//BottelneckUtil.init(manuModel);
			return 1;
		} else {
			return 0;
		}
	}

	public void closeModel() {
		agentModel.close();
		simulationAgent.closeModel();
		manuModel.renew();
		results.renew();
		currentSimTimes = 0;
	}

	public void startSim() {

		for (SimulatorListener sl : simulationListener) {
			sl.onStart();
		}
		simulationAgent.stratSimulation();
		simulatorInfo.simulateBeginTime = System.currentTimeMillis();
		updateState("Simulation is running...");
	}

	public void stopSim() {
		simulationAgent.stopSimulation();
		updateState("Please reset model or open a new model");
	}

	public void resetSim() {
		// simulatorAgent.resetSimulation(manuModel);
		agentModel.reset();
		results.reset();
		simulationAgent.reset(manuModel);
		updateState("Please run again or open a new model");
		for (SimulatorListener sl : simulationListener) {
			sl.onReset();
		}
	}

	public void pauseSim() {
		simulationAgent.pauseSimulation();
		updateState("Simulation is paused, please continue");
	}

	public void continueSim() {
		simulationAgent.continueSimulation();
		updateState("Simulation is running...");
	}

	public void onEndSim(SimulationInfo simulatorInfo, int currentSimTimes) {
		updateState("Simulation ends");
		String sd = new java.text.SimpleDateFormat("_yyyyMMddHHmmss")
				.format(new Date());
		if (isCollectData) {
			updateState("Preparing simulation results...");
			if (currentSimTimes == simulatorInfo.simulationTimes - 1) {
				results.stat();
				for (SimulatorListener sl : simulationListener) {
					sl.onEnd();
				}
				// BottelneckUtil.analyseBufferSize(manuModel, results);
				if (showResult) {

					updateState("Buliding up reports...");
					ReportInfo reportInfo = bulidReport(simulatorInfo);
					reportInfo.periodStr = simulatorInfo.dataAcqTimeStr;
					ResultViewerM viewer = new ResultViewerM(reportInfo,
							results.releaseDataset, results.toolGroupDataset,
							results.bufferDataset, results.jobDataset);

					viewer.setTitle("SimResult - "
							+ simulatorInfo.currentModelName + sd);
					viewer.setVisible(true);
					new File(reportInfo.productResultFile).delete();
					new File(reportInfo.summaryResultFile).delete();
					new File(reportInfo.toolResultFile).delete();

					updateState("Saving simulation results... 0%");
					try {

						FileOutputStream outStream = new FileOutputStream(
								simulatorInfo.currentModelPath
										+ simulatorInfo.currentModelName + "//"
										+ simulatorInfo.currentModelName + sd
										+ ".rst");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(
								outStream);
						objectOutputStream.writeObject(manuModel.getPolicy());
						objectOutputStream.writeObject(simulatorInfo);
						updateState("Saving simulation results... 5%");
						objectOutputStream.writeObject(results.releaseDataset);
						updateState("Saving simulation results... 15%");
						objectOutputStream.writeObject(results.bufferDataset);
						updateState("Saving simulation results... 40%");
						objectOutputStream
								.writeObject(results.toolGroupDataset);
						updateState("Saving simulation results... 60%");
						objectOutputStream.writeObject(results.jobDataset);
						updateState("Saving simulation results... 100%");
						outStream.close();
						System.out.println("successful!");
						updateState("Simulation results are saved");

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// 2014
		// if (simulationAgent.collector.sample) {
		// try {
		// updateState("Saving sample data...");
		// FileOutputStream outStream = new FileOutputStream(
		// simulatorInfo.currentModelPath
		// + simulatorInfo.currentModelName + "//"
		// + simulatorInfo.currentModelName + sd + ".dat");
		// ObjectOutputStream objectOutputStream = new ObjectOutputStream(
		// outStream);
		// objectOutputStream.writeObject(simulationAgent.collector
		// .getTraningDatasetMap());
		// outStream.close();
		// updateState("Sample data is saved");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//

//		for (ToolGroup tg : agentModel.getToolGroupAgents()) {
//			// tg.getDispatcher().getDataset().setToolGroupName(tg.name);
//			if (tg.getDispatcher() != null) {
//				tg.getDispatcher().getDataset().saveToXls();
//				tg.getDispatcher().getDataset().clear();
//			}
//		}


	}

	private ReportInfo bulidReport(SimulationInfo simulatorInfo) {
		ReportInfo reportInfo = ReportXmlData.saveResult(manuModel.getPolicy(),
				simulatorInfo, results.releaseDataset, results.bufferDataset,
				results.toolGroupDataset);
		updateState("Showing simulation results...");
		return reportInfo;
	}

	private void updateState(String s) {
		for (SimulatorListener sl : simulationListener) {
			sl.onStateChange(s);
		}
	}

	public void updateTime() {
		for (SimulatorListener sl : simulationListener) {
			sl.onTimeAdvance(simulationAgent.currentTime(),
					simulationAgent.getSimulateBeginTime());
		}
	}

	public boolean isShowGui() {
		return showResult;
	}

	public void setShowGui(boolean showGui) {
		this.showResult = showGui;
	}

	public void addSimulationListener(SimulatorListener sl) {
		simulationListener.add(sl);
	}

	public AgentBasedModel getAgentModel() {
		return agentModel;
	}

	public void setAgentModel(AgentBasedModel agentModel) {
		this.agentModel = agentModel;
	}

	public SimulationResult getResults() {
		return results;
	}

	public void setResults(SimulationResult results) {
		this.results = results;
	}

	public SimulationInfo getSimulatorInfo() {
		return simulatorInfo;
	}

	public ManufactureModel getManuModel() {
		return manuModel;
	}

}
