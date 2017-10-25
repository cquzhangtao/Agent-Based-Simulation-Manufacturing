package manu.agent.component;

import manu.model.component.JobInfo;
import manu.model.component.Product;
import manu.simulation.CustomActivation;
import manu.simulation.result.data.JobData;
import manu.simulation.result.data.JobDataset;
import simulation.framework.Activation;
import simulation.framework.AgentEnvironment;
import simulation.framework.Behaviour;
import simulation.framework.Message;

public class Job extends SimAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String release;
	private JobInfo jobInfo;
	private JobData jobData;

	protected Job(String agentName, Object[] args) {
		super(agentName);

		jobInfo = new JobInfo(agentName, (Product) args[2]);
	
		release = (String) args[0];
		jobData = (JobData) args[1];
		jobData.releaseTime = (Long) args[3];
		jobInfo.setReleaseTime((Long) args[3]);
		setCurrentTime(jobData.releaseTime);
		jobData.jobType = jobInfo.getProductName();
		jobData.jobName = jobInfo.getName();
		
		this.setAdditionalString((String) args[4]);

		addBehaviour(new JobReleaseServer());
		addBehaviour(new CheckBlockServer());
		addBehaviour(new TransportEndServer());
		addBehaviour(new StartProcessServer());
		addBehaviour(new ProcessFinishServer());
	}
	
	public void simpleClone(Job agent){
		super.simpleClone(agent);
		agent.release=release;
		agent.jobInfo=jobInfo;
		agent.jobData=jobData;
		agent.setCollectData(isCollectData());
	}

	public JobInfo getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}

	@Override
	public void start() {
		super.start();
		sendMessage(getAgentName(), CustomActivation.JobRelease, "");
	}

	public void start1() {
		super.start();
	}

	public Job(String name) {
		// TODO Auto-generated constructor stub
		super(name);
		addBehaviour(new JobReleaseServer());
		addBehaviour(new CheckBlockServer());
		addBehaviour(new TransportEndServer());
		addBehaviour(new StartProcessServer());
		addBehaviour(new ProcessFinishServer());
		
	}

	protected void destroyMe() {
		release = null;
		//jobInfo = null;
		destroyAgent();
	}

	// protected String getCurrentToolName() {
	// if (jobInfo.currentTool == null)
	// return "";
	// else
	// return jobInfo.currentTool;
	// }

	private class JobReleaseServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			// TODO Auto-generated method stub
			Message reply = receive(CustomActivation.JobRelease);
			if (reply != null) {
				debugMsg(getAdditionalString()+" ,job "+getAgentName()+" released, time: "+currentTime());
				
//				RequestToolProcess requestToolProcess = new RequestToolProcess();
//				addBehaviour(requestToolProcess);
				requestTool();
			}
			return 0;
		}

	}

	private class CheckBlockServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			Message reply = receive(CustomActivation.IsBlockYet);
			if (reply != null) {
				if (jobInfo.isBlocked()) {
					// the job starts new search to next tool
//					if (requestToolProcess != null) {
//						removeBehaviour(requestToolProcess);
//						requestToolProcess = null;
//					}
//					requestToolProcess = new RequestToolProcess();
//					addBehaviour(new RequestToolProcess());
					requestTool();
					// Logging Flag// if (simulatorInfo.logging)
					// Logging Flag//
					// writeLog("Receive IsBlockYet activation from simulator, search tool or buffer again");
				} else {

				}

			}
			return 1;
		}
	}

//	private class RequestToolProcess extends Behaviour {
//		// private int step = 0;
//
//		public RequestToolProcess() {
//			// type="RequestToolProcess";
//		}
//
//		@Override
		public int requestTool() {
			// request transport
			Message allocationmsg = new Message();
			allocationmsg.setReceiver(jobInfo.getCurrentProcess().getSkill());
			allocationmsg.setTitle("Select Tool Group");
			allocationmsg.setObjectContent(jobInfo);
			send (allocationmsg);
			Message rep = blockingReceive("Select Tool Group");
			jobInfo.setCurrentToolGroupName(rep.getContent());
			

			Message order = new Message();
			order.setReceiver(jobInfo.getCurrentToolGroupName());
			if (jobInfo.isFirstProcess()) {
				order.setContent(String.valueOf(jobInfo.getReleaseTime()));
			} else {
				if (isCollectData()) {
					order.setContent(String
							.valueOf(jobData.jobSubData[jobData.processIndex - 1].blockBeginTime));
				} else {
					order.setContent("0");
				}

			}
			order.setTitle("Request Tool Process");
			send(order);

			Message reply = blockingReceive("Request Tool Process");
			if (reply == null) {
				// break;
			}
			// tool or buffer accept the job. the job will be transporting
			if (reply.getContent().equals("Accept")) {

				// /model update flag
				if (jobInfo.isBlocked())
					getEnvironment().update();
				// /end model update flag 22082012

				if (isCollectData())
					jobData.transportBeginTime(currentTime());

				jobInfo.setPreTool(jobInfo.getCurrentTool());
				jobInfo.setCurrentTool(reply.getSender());
				// if the job has been processed more than one operation
				if (!jobInfo.isFirstProcess()) {
					// inform current tool that the job has finished and
					// found the next tool and current tool must become free
					if (jobInfo.getCurrentTransTime() > 0){
					sendMessage(
							jobInfo.getPreTool(),
							"Job Finish",
							jobInfo.getCurrentToolIndex() + "-"
									+ jobInfo.getProductIndex());
					
					debugMsg(this.getAdditionalString()+", Job "+getAgentName()+", finished step "+jobInfo.getPreProcessName()+" on tool "+jobInfo.getPreTool()+", start on tool "+jobInfo.getCurrentTool()+", time:"+currentTime());
					if (isCollectData()) {
						jobData.nextTransportBeginTime(currentTime());
						jobData.previewStepFinished();
					}
					}

				} else {// if the job is just released and find the first
						// tool
					if (isCollectData())
						jobData.firstTransportBeginTime = currentTime();
					if (jobInfo.isBlocked()) {
						// send unblock message to release agent.
						// if the job is blocked, release agent will
						// update block job number in release buffer
						sendMessage(release, "UnBlock", "");
					} else {
					}

				}
				// because the job has found the next tool, reset block
				// state
				jobInfo.setBlocked(false);

				if (jobInfo.getCurrentTransTime() > 0) {
					// tool or buffer accept the job. the job will be
					// transporting
					// if it takes time to the job moving to next tool,
					// send activate(transport end ) to simulator agent
					debugMsg(this.getAdditionalString()+", Job "+getAgentName()+", finished step "+jobInfo.getPreProcessName()+" on tool "+jobInfo.getPreTool()+", start on tool "+jobInfo.getCurrentTool()+", time:"+currentTime()+",starts transportaion");
					
					Activation msg = new Activation(currentTime()
							+ jobInfo.getCurrentTransTime(), getAgentName(),
							CustomActivation.TransportEnd);
					sendSimulationMsg(msg);
				} else {
					// if there is no transporting time moving to next
					// tool, request buffer or tool to be waiting
					sendWaitInform();
					if(!jobInfo.isFirstProcess()){
					sendMessage(
							jobInfo.getPreTool(),
							"Job Finish",
							jobInfo.getCurrentToolIndex() + "-"
									+ jobInfo.getProductIndex());
					
					debugMsg(this.getAdditionalString()+", Job "+getAgentName()+", finished step "+jobInfo.getPreProcessName()+" on tool "+jobInfo.getPreTool()+", start on tool "+jobInfo.getCurrentTool()+", time:"+currentTime());
					if (isCollectData()) {
						jobData.nextTransportBeginTime(currentTime());
						jobData.previewStepFinished();
					}
					}
				}
				// step++;
				// break;
			}
			// buffer is full,refuse job
			// because other jobs have entered the buffer before the job
			// requests
			// the job can not get the next tool and has to stay at current
			// position.
			// if the job is not blocked before. only run once
			else if (reply.getContent().equals("Refuse")) {

				jobInfo.setBlocked(true);
				if (!jobInfo.isFirstProcess()) {
					sendMessage(jobInfo.getCurrentTool(), "Job Block",
							jobInfo.getCurrentToolIndex());
					// Logging Flag// if (simulatorInfo.logging)
					// Logging Flag//
					// Job.this.writeLog("block begin in tool "
					// Logging Flag// + currentTool.getName());
				} else {
					sendMessage(release, "Block", "");
					// Logging Flag// if (simulatorInfo.logging)
					// Logging Flag//
					// Job.this.writeLog("block begin in release "
					// Logging Flag// + release.getName());
				}
				// send activation(is block yet?) to simulator
				Activation msg = new Activation(currentTime(), getAgentName(),
						CustomActivation.IsBlockYet);
				msg.setType(1);
				sendSimulationMsg(msg);
			} else {
				System.out.println("Job request tool error!");
			}
			//requestToolProcess = null;
			return 0;

		}

	//}

	private class TransportEndServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			Message reply = receive(CustomActivation.TransportEnd);
			if (reply != null) {
				debugMsg(getAgentName()+",transportation ends, waiting before tool group "+jobInfo.getCurrentTool());
				sendWaitInform();
			}
			return 1;
		}
	}

	private void sendWaitInform() {
		if (isCollectData())
			jobData.waitBeginTime(currentTime());
		jobInfo.setStateStartTime(currentTime());
//		String content = String.format(
//				"%s-%s-%d-%f-%f-%d-%d-%d-%s-%s-%s-%s-%d-%f-%d-%d-%d", name,
//				jobInfo.getProductName(), currentTime(),
//				jobInfo.getRemainTime(), jobInfo.getTotalTime(),
//				jobInfo.getReleaseTime(), jobInfo.getCurrentProcessTime(),
//				jobInfo.getRemainStep(), jobInfo.currentStepName(),
//				jobInfo.currentBatchID(), jobInfo.nextStepName(),
//				jobInfo.nextBatchID(),
//				jobInfo.getJobPriority() > 0 ? jobInfo.getJobPriority() : 1,
//				jobInfo.currentCriticalTime(), jobInfo.processNum(),
//				jobInfo.getProductIndex(), jobInfo.currentProcess);
		Message msg=new Message();
		msg.setObjectContent(jobInfo);
		msg.setReceiver(jobInfo.getCurrentTool());
		msg.setTitle("Wait Command");
		send(msg);
	}

	private class StartProcessServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			// receive start processing message

			Message reply = receive("Start Process");
			if (reply == null) {
				return 1;
			}
			// receive start process message
			// Logging Flag// if (simulatorInfo.logging)
			// Logging Flag// writeLog("Receive start process message from "
			// Logging Flag// + reply.getSender().getLocalName());
			jobInfo.setCurrentTool(reply.getSender());
			// String temp[] = reply.getContent().split("--");
			// int num = Integer.valueOf(temp[1]);
			// number of the jobs processed at the same time by the tool
			// flag that refers what causes start process event
			// 0.the tool finished all job, and then request new job to process
			// 1.the buffer size is changed, the free tool request job to
			// process
			// String[]str=reply.getContent().split("%");//gannt chart needs
			// this
			jobInfo.setCurrentToolIndex(reply.getContent());
			// if (temp.length == 4) {
			// currentToolIndex = temp[3];
			// } else {
			// currentToolIndex = "";
			// }

			// toolFlow[currentProcessIndex()] = currentTool;
			// processBeginTime = currentTime();
			if (isCollectData()) {
				jobData.setToolName(jobInfo.getCurrentToolGroupName());
				jobData.processBeginTime(currentTime());
			}
			Activation msg = new Activation(currentTime()
					+ jobInfo.getCurrentProcessTime(), getAgentName(),
					CustomActivation.ProessFinish);
			sendSimulationMsg(msg);
			// System.out.println(name
			// +" starts to process in the tool "+currentTool+","+currentTime()+","+msg.getString()+","+Job.this.toString());
			return 1;

		}
	}

	private class ProcessFinishServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			Message reply = receive(CustomActivation.ProessFinish);
			if (reply != null) {
				onProcessFinish();
			}
			return 1;

		}
	}

	public void onProcessFinish() {
		if (isCollectData())
			jobData.blockBeginTime(currentTime());
		// job has not finished
		if (!jobInfo.isFinished()) {
			jobInfo.goNextProcess();
			if (isCollectData())
				jobData.processIndex = jobInfo.getCurrentProcessIndex();
			jobInfo.setBlocked(false);
			requestTool();
			return;
		}
		// finished all process
		sendMessage(
				jobInfo.getCurrentTool(),
				"Job Finish",
				jobInfo.getCurrentToolIndex() + "-"
						+ String.valueOf(jobInfo.getProductIndex()));

		sendMessage(release, "Finish",
				String.valueOf(jobInfo.getProductIndex()) + "a"
						+ (currentTime() - jobInfo.getReleaseTime()));


		if (isCollectData()) {
			jobData.currentStepFinished();
			jobData.jobSubData[jobInfo.getCurrentProcessIndex()].nextTransportBeginTime = currentTime();
			jobData.cycleTime = currentTime() - jobInfo.getReleaseTime();
		}
		debugMsg(this.getAdditionalString()+" ,"+getAgentName()+" finished, time: "+currentTime()+" "+jobInfo.getPath());
		destroyMe();

	}

	

	public Job clone(AgentEnvironment environment,JobDataset jobDataset) {
		Job job = new Job(this.getAgentName());
		super.clone(job, environment);
		job.jobInfo = jobInfo.clone();
		job.release = this.release;
		job.jobData = this.jobData.clone();
		jobDataset.add(jobInfo.getProductIndex(), job.jobData);
		//job.requestToolProcess=this.requestToolProcess;
		job.setAdditionalString("Sub Simulation");
		
		


		return job;
	}

	@Override
	public void afterAgentDestroy() {
		// TODO Auto-generated method stub
		if (isForceDestroied())
			send("Release", "Reset Confirm", "");
	}

	@Override
	protected void onSimulatorStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSimulatorReset() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSimulatorEnd() {
		// TODO Auto-generated method stub
		// isForceDestroied=true;
		// destroyMe();
	}
	
	private void debugMsg(String msg){
		//System.out.println(msg);
	}

}
