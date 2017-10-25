package manu.scheduling;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

import manu.agent.AgentBasedModel;
import manu.agent.component.ToolGroup;
import manu.model.component.Product;
import manu.model.component.ReleasePolicy;
import manu.scheduling.training.data.FlowControlTrainingDataset;
import manu.simulation.Simulation;
import manu.simulation.SimulationListener;
import manu.simulation.result.SimulationResult;
import manu.simulation.result.data.ReleaseData;
import simulation.framework.Agent;

public class SubSimulation {

	private AgentBasedModel agentModel;
	private Simulation simulator;
	private AgentBasedModel agentModelNew;
	private Simulation simulatorNew;
	private SimulationResult results;
//	private Agent currentAgent;
//	private Object decision;
	private Boolean isRunning=false;
	private FlowControlTrainingDataset trainingDataset;
	private boolean collectData=false;

	public SubSimulation(AgentBasedModel agentModel, Simulation simulator) {
		this.agentModel = agentModel;
		this.simulator = simulator;
	}

	public void cloneSim() {
		results = new SimulationResult();
		results.jobDataset
				.init(agentModel.getReleaseAgent().getProducts().length);
		results.releaseDataset.init(agentModel.getReleaseAgent().getProducts(),
				agentModel.getReleaseAgent().getSimulatorInfo().getDataAcqTime());
		
		agentModelNew = agentModel.clone(results,"Main");

		simulatorNew = simulator.clone(agentModelNew.getEnvironment());

	}
	public void saveData(){
		String sd = new java.text.SimpleDateFormat("_yyyyMMddHHmmss")
		.format(new Date());
	
		try{
		FileOutputStream outStream = new FileOutputStream(
				agentModel.getReleaseAgent().getSimulatorInfo().currentModelPath
						+ agentModel.getReleaseAgent().getSimulatorInfo().currentModelName + "//"
						+ agentModel.getReleaseAgent().getSimulatorInfo().currentModelName + sd
						+ ".rtd");
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				outStream);
		objectOutputStream.writeObject(trainingDataset);
		
		outStream.close();
		
		outStream = new FileOutputStream(
				agentModel.getReleaseAgent().getSimulatorInfo().currentModelPath
						+ agentModel.getReleaseAgent().getSimulatorInfo().currentModelName + "//"
						+ agentModel.getReleaseAgent().getSimulatorInfo().currentModelName + sd
						+ ".tnd");
		objectOutputStream = new ObjectOutputStream(
				outStream);
		objectOutputStream.writeObject(trainingDataset.createSequencingTrainingDataset());
		
		outStream.close();
		
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public SubSimulation clone(){
		SubSimulation subSim=new SubSimulation(agentModel,simulator);
		subSim.results = new SimulationResult();
		subSim.results.jobDataset
				.init(agentModel.getReleaseAgent().getProducts().length);
		subSim.results.releaseDataset.init(agentModel.getReleaseAgent().getProducts(),
				agentModel.getReleaseAgent().getSimulatorInfo().getDataAcqTime());
		subSim.agentModelNew = agentModelNew.clone(subSim.results,"Sub");

		subSim.simulatorNew = simulatorNew.clone(subSim.agentModelNew.getEnvironment());
		
		return subSim;
	}
	
	public AgentBasedModel cloneAgentModel(){
		SimulationResult results = new SimulationResult();
		results.jobDataset
				.init(agentModel.getReleaseAgent().getProducts().length);
		results.releaseDataset.init(agentModel.getReleaseAgent().getProducts(),
				agentModel.getReleaseAgent().getSimulatorInfo().getDataAcqTime());
		return agentModel.clone(results,"Sub");
	}
	
	public ToolGroup getToolGroupAgent(String name){
		return agentModel.getGoolGroupAgent(name);
	}
	
	public void initHistoryDecisions(){
		agentModel.initHistoryDecisions();
	}
	public void setHistoryDecisions(){
		agentModelNew.setHistoryDecisions(agentModel);
	}
	public void pauseMainSimulation(){
		agentModel.pauseAgents();
		simulator.pauseAgent();
		simulator.pauseSimulation();
	}
	
	public void continueMainSimulation(){
		agentModel.continueAgents();
		simulator.continueAgent();
		simulator.continueSimulation();
	}
	
	public void setDecision(String decisonAgent,Object decision){
		agentModelNew.setDecision(decisonAgent,decision);
	}

	public void run(final Agent agent, Object decision) {
		
		agentModelNew.setDecision(agent.getAgentName(),decision);

		//System.out
		//		.println("********************Subsimulaion starts********************");
		// agent.send(agent.getName(), "Subsimulation Done", results);
		// System.out
		// .println("********************Subsimulaion ends********************");
		final int wip = agentModelNew.getReleaseAgent().getReleaseJobNum() + 3
				* agentModelNew.getJobAgents().size();
		simulatorNew.addSimulationListener(new SimulationListener() {

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub
				results.stat();
				agentModelNew.destroy();
				simulatorNew.destroyAgent1();
				simulatorNew.continueAgent();
				agent.send(agent.getAgentName(), "Subsimulation Done", results.getReleaseDataset().getAllReleaseDataset().avgCycleTime);

				//System.out
						//.println("********************Subsimulaion ends********************");

			}

			@Override
			public void onTimeAdvance() {
				// TODO Auto-generated method stub
//				System.out
//				.println("********************Subsimulaion Advanced********************");
			}

			@Override
			public boolean onEndDecision() {
				return agentModelNew.getReleaseAgent().getReleaseJobNum() > wip;
			}
		});

		agentModelNew.start();
		simulatorNew.start();

	

	}
	public void runR(final Agent agent, Object decision,final int jobTypeIndex) {
		agentModelNew.setDecision(agent.getAgentName(),decision);
		
//		ReleasePolicy releasePolicy=new ReleasePolicy();
//		releasePolicy.releasePolicy=ReleasePolicy.ConstantTimeDetail;
//		agentModelNew.getReleaseAgent().setReleasePolicy(releasePolicy);		
//		for(Product product:agentModelNew.getReleaseAgent().getProducts()){
//			product.getReleaseIntervalTimeR().setParameter("mean", product.getReleaseIntervalTimeR().getParameter("mean")*2.5);
//			Activation act=new Activation(agent.currentTime(), agentModelNew.getReleaseAgent().getName(),
//					CustomActivation.JobRelease,String.valueOf(product.getIndex()));
//			if(product.getIndex()==jobTypeIndex){			
//				act.setObjectContent(decision);			
//			}
//			simulatorNew.addActivation(act);
			
//		}

		//System.out
		//		.println("********************Subsimulaion starts********************");
		final int wip = agentModelNew.getReleaseAgent().getReleaseJobNum() + 30+40
				* agentModelNew.getJobAgents().size();
		simulatorNew.addSimulationListener(new SimulationListener() {

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub
				//long simTime=simulatorNew.currentTime()-agent.currentTime();
				results.stat();
				agentModelNew.destroy();
				simulatorNew.destroyAgent1();
				simulatorNew.continueAgent();
				
				double ratio=0.5;
				double[] targetThroughput=new double[2];
				targetThroughput[0]=16;
				targetThroughput[1]=28;
				
				double[] flowFactor=new double[2];
				flowFactor[0]=2.5;
				flowFactor[1]=1.7;
				
				double p=0;
				double r=0;
				int index=0;
				for(ReleaseData data:results.getReleaseDataset().getReleaseDataset()){
					//p+=((targetThroughput[index]-data.productivity)+5)/10;
					p+=Math.abs(data.productivity/targetThroughput[index]-1);
					//p+=Math.abs(targetThroughput[index]-data.productivity);
					r+=(data.avgCycleTime-data.rawProcessTime)/((flowFactor[index]-1)*data.rawProcessTime);
					index++;
				}
//				p=(results.getReleaseDataset().getReleaseDataset().get(jobTypeIndex).productivity-targetThroughput[jobTypeIndex]+5)/10;
//				if(results.getReleaseDataset().getAllReleaseDataset().avgCycleTime==0){
//					System.out.println("Cycletime error");
//					System.exit(0);
//				}
//				p=Math.abs(1-results.getReleaseDataset().getReleaseDataset().get(jobTypeIndex).productivity/targetThroughput[jobTypeIndex]);
//				System.out.println(jobTypeIndex +" productivity: "+results.getReleaseDataset().getReleaseDataset().get(jobTypeIndex).productivity);
		//		 r=(results.getReleaseDataset().getAllReleaseDataset().avgWip-3)/5;
				double criteria=ratio*(r)
						+(1-ratio)*p;
				if(Double.isInfinite(criteria)){
					System.out.println("value error");
					System.exit(0);
				}
				agent.send(agent.getAgentName(), "Subsimulation Done", 
						criteria);

				//System.out
						//.println("********************Subsimulaion ends********************");

			}

			@Override
			public void onTimeAdvance() {
				// TODO Auto-generated method stub
//				System.out
//				.println("********************Subsimulaion Advanced********************");
			}

			@Override
			public boolean onEndDecision() {
				return agentModelNew.getReleaseAgent().getReleaseJobNum() > wip;
			}
		});

		agentModelNew.start();
		simulatorNew.start();
		
	}

//	public Agent getCurrentAgent() {
//		return currentAgent;
//	}
//
//	public void setCurrentAgent(Agent currentAgent) {
//		this.currentAgent = currentAgent;
//	}
//
//	public Object getDecision() {
//		return decision;
//	}
//
//	public void setDecision(Object decision) {
//		this.decision = decision;
//	}

	public synchronized Boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Simulation getSimulator() {
		return simulator;
	}

	public void setSimulator(Simulation simulator) {
		this.simulator = simulator;
	}

	public FlowControlTrainingDataset getTrainingDataset() {
		return trainingDataset;
	}

	public void setTrainingDataset(FlowControlTrainingDataset trainingDataset) {
		this.trainingDataset = trainingDataset;
	}

	public AgentBasedModel getAgentModel() {
		return agentModel;
	}

	public void setAgentModel(AgentBasedModel agentModel) {
		this.agentModel = agentModel;
	}

	public boolean isCollectData() {
		return collectData;
	}

	public void setCollectData(boolean collectData) {
		this.collectData = collectData;
	}



}
