package manu.model.component;

import java.io.Serializable;
import java.util.ArrayList;

import manu.agent.AgentBasedModel;
import manu.model.feature.DispatchingRules;
import manu.simulation.result.data.WaitDataset;

public class BufferInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int bufferCapacity;
	private int currentRule;
	private boolean isBatchTool = false;
	private ArrayList<JobInfo> jobsinBuffer;
	private WaitDataset waitDataset;

	// private Random rand = new Random(100);
	// private int[] arraybatch = new int[500];
	// public ArrayList<Batch> batch;
	// dynamic members
	// public BufferWeights nextBufferWeights;
	// public MultiObjWeight multiObjWeight;

	public BufferInfo(int dispatchingRule, int bufferCapacity,boolean isBatchTool, WaitDataset waitDataset) {
		// multiObjWeight = toolGroupInfo.multiObjWeight;
		currentRule = dispatchingRule;
		jobsinBuffer = new ArrayList<JobInfo>();
		setBatchTool(isBatchTool);
		// this.batch = toolGroupInfo.batch;
		setBufferCapacity(bufferCapacity);
		// nextBufferWeights = toolGroupInfo.nextBufferWeights;
		this.setWaitDataset(waitDataset);

		// if (isBatchTool) {
		// for (int i = 0; i < batch.size(); i++) {
		// arraybatch[batch.get(i).BatchID] = batch.get(i).BatchNum;
		// }
		// }

	}

	public BufferInfo() {
		// TODO Auto-generated constructor stub
	}

	public void offer1(JobInfo rv) {
		jobsinBuffer.add(rv);
	}

	public void offer(JobInfo jobInfo) {
		// JobinBuffer job = new JobinBuffer(jobInfo);
		jobsinBuffer.add(jobInfo);
	}

	public boolean IsFull() {
		return jobsinBuffer.size() >= getBufferCapacity();
	}

	// public ArrayList<Integer> getFinishedBatchIDs() {
	// int[] numofJobsbyBatchID = new int[500];
	// for (int i = 0; i < jobsinBuffer.size(); i++) {
	// for (int j = 0; j < jobsinBuffer.get(i).batchID.size(); j++) {
	// numofJobsbyBatchID[jobsinBuffer.get(i).batchID.get(j)]++;
	// }
	// }
	// // find finished batches
	// ArrayList<Integer> finishedBatchIDs = new ArrayList<Integer>();
	// for (int j = 0; j < numofJobsbyBatchID.length; j++) {
	// if (numofJobsbyBatchID[j]>0&& numofJobsbyBatchID[j]>= arraybatch[j]) {
	// finishedBatchIDs.add(j);
	// }
	// }
	// return finishedBatchIDs;
	// }

	public int validateSize() {
		// -1 cannot start processing
		// 0 can start singleprocessing
		// >0 can start batching processing, this is batch ID
		if (!isBatchTool()) {
			if (size() == 0) {
				return -1;
			} else {
				return 0;
			}
		}
		return 0;

		// ArrayList<Integer> finishedBatchIDs = getFinishedBatchIDs();
		// if (finishedBatchIDs.size() == 0) {
		// return -1;
		// }
		// ArrayList<Integer> selectedBatchID = new ArrayList<Integer>();
		// int maxBatchSize = 0;
		// for (int id : finishedBatchIDs) {
		// if (arraybatch[id] > maxBatchSize) {
		// selectedBatchID.clear();
		// maxBatchSize = arraybatch[id];
		// selectedBatchID.add(id);
		// } else if (arraybatch[id] == maxBatchSize) {
		// selectedBatchID.add(id);
		// }
		// }
		// int fbatchID = selectedBatchID
		// .get(rand.nextInt(selectedBatchID.size()));
		// return fbatchID;

		// int validSize = numofJobsbyBatchID[0];
		// ArrayList<Integer> validBatchID = new ArrayList<Integer>();
		// validBatchID.add(0);
		// // TODO
		// // here has a problem, must select batches which have finished first
		// for (int j = 1; j < numofJobsbyBatchID.length; j++) {
		// if (validSize < numofJobsbyBatchID[j]) {
		// validBatchID.clear();
		// validSize = numofJobsbyBatchID[j];
		// validBatchID.add(j);
		// } else if (validSize == numofJobsbyBatchID[j]) {
		// validBatchID.add(j);
		// }
		// }
		// if (currentRule != Rule.LookAhead || validBatchID.size() == 1) {
		// int i = rand.nextInt(validBatchID.size());
		// return new int[] { validBatchID.get(i),
		// numofJobsbyBatchID[validBatchID.get(i)],
		// arraybatch[validBatchID.get(i)] };
		// }
		// else if (currentRule == Rule.LookAhead) {
		// @SuppressWarnings("unchecked")
		// ArrayList<Integer>[] indexJob = new ArrayList[500];
		//
		// for (int i = 0; i < 500; i++)
		// indexJob[i] = new ArrayList<Integer>();
		// for (int i = 0; i < jobsinBuffer.size(); i++) {
		// for (int j = 0; j < jobsinBuffer.get(i).batchID.size(); j++) {
		// indexJob[jobsinBuffer.get(i).batchID.get(j)].add(j);
		// }
		// }
		// int bestBatchID = validBatchID.get(0);
		// int minProcessTime = 500 * 24 * 60 * 60;
		// for (int i = 0; i < validBatchID.size(); i++) {
		// int iProcessTime = 0;
		// ArrayList<String> nextgroupname = new ArrayList<String>();
		// for (int j = 0; j < indexJob[validBatchID.get(i)].size(); j++) {
		// int index = indexJob[validBatchID.get(i)].get(j);
		// JobinBuffer job = jobsinBuffer.get(index);
		// if (!nextgroupname.contains(job.nextToolGroupName)) {
		// nextgroupname.add(job.nextToolGroupName);
		// iProcessTime += nextBufferWeights
		// .getUrgentWeight(job.nextToolGroupName);
		// }
		// }
		// if (iProcessTime < minProcessTime) {
		// minProcessTime=iProcessTime;
		// bestBatchID = validBatchID.get(i);
		// }
		// }
		// return new int[] { bestBatchID, numofJobsbyBatchID[bestBatchID],
		// arraybatch[bestBatchID] };
		// } else {
		//
		// System.out.println("Look ahead Error from best batch id selection");
		// return null;
		//
		// }
	}

	// public String bufferWeight() {
	// double sum = 0;
	// for (int i = 0; i < jobsinBuffer.size(); i++) {
	// sum += jobsinBuffer.get(i).processingTime;
	// }
	// if (!isBatchTool) {
	// return "Single---" + String.valueOf(sum);
	// }
	//
	// int[] r = new int[500];
	// for (int i = 0; i < jobsinBuffer.size(); i++)
	// for (int j = 0; j < jobsinBuffer.get(i).batchID.size(); j++) {
	// r[jobsinBuffer.get(i).batchID.get(j)]++;
	// }
	//
	// String batchWeights = "";
	// for (int j = 1; j < r.length; j++) {
	// if (r[j] > 0) {
	// batchWeights += String.format("%d-%f--", j,
	// (r[j] % arraybatch[j]) / (1.0 * arraybatch[j]));
	// }
	// }
	// if (!batchWeights.isEmpty())
	// batchWeights = batchWeights.substring(0, batchWeights.length() - 2);
	// return "Batch---" + batchWeights + "---" + String.valueOf(sum);
	//
	// }

	// public double batchingUrgency(ArrayList<Integer> batchID) {
	//
	// int[] r = new int[500];
	// for (int i = 0; i < jobsinBuffer.size(); i++)
	// for (int j = 0; j < jobsinBuffer.get(i).batchID.size(); j++) {
	// r[jobsinBuffer.get(i).batchID.get(j)]++;
	// }
	// double sum = 0;
	// for (int i = 0; i < batchID.size(); i++) {
	// sum += (r[batchID.get(i)] % arraybatch[batchID.get(i)])
	// / (1.0 * arraybatch[batchID.get(i)]);
	// }
	// return sum / batchID.size();
	// }
	// public long getMaxWaitingTime(long currentTime){
	// long max=0;
	// for(JobinBuffer job:jobsinBuffer){
	// if(max<currentTime-job.getBeginWaitTime()){
	// max=currentTime-job.getBeginWaitTime();
	// }
	// }
	// return max;
	// }

	public ArrayList<JobInfo> poll(ToolInfo requestTool, long currentTime,
			int batchID, int currentRule) {

		// String[] result;
		ArrayList<JobInfo> result = new ArrayList<JobInfo>();
		// buffer size=1,single processing tool
		if (!isBatchTool() && jobsinBuffer.size() == 1) {
			// result = getReturnStringArray(jobsinBuffer.get(0), currentTime);
			getWaitDataset().add(jobsinBuffer.get(0).getName(), currentTime,
					currentTime - jobsinBuffer.get(0).getStateStartTime());
			result.add(jobsinBuffer.get(0));
			jobsinBuffer.remove(0);
			return result;
		}
		//
		// MaxMin[] rulePriorityMaxMin = getPrioirtyMinmax(requestTool,
		// currentTime,currentRule);
		// buffer size>1, single processing tool
		if (!isBatchTool()) {
			JobInfo selectedJob = null;
			if (currentRule != DispatchingRules.LookAhead) {
				selectedJob = getIndexofBestJob(requestTool, currentTime,
						jobsinBuffer, currentRule);
			}
			// single processing tool and look ahead rule
			else {
				// selectedJob = getBestJobLookAhead(requestTool, currentTime,
				// rulePriorityMaxMin);
			}
			// result = getReturnStringArray(selectedJob, currentTime);
			getWaitDataset().add(selectedJob.getName(), currentTime,
					currentTime - selectedJob.getStateStartTime());
			result.add(selectedJob);
			jobsinBuffer.remove(selectedJob);

			return result;

		}
		// batch processing, maybe lookahead rule, maybe not
		// return the earlier jobs in batch, do not consider the priorties of
		// two batches

		// for (int i = 0; i < jobsinBuffer.size(); i++) {
		//
		// if (jobsinBuffer.get(i).batchID.contains(batchID)) {
		// result.add(jobsinBuffer.get(i));
		// waitDataset.add(jobsinBuffer.get(i).jobName, currentTime,
		// currentTime - jobsinBuffer.get(i).beginWaitTime);
		// jobsinBuffer.remove(i);
		// i = i - 1;
		// if (result.size() == arraybatch[batchID])
		// break;
		// }
		// }
		return result;

	}
	
	public String getJobs(){
		String jobs="";
		for(JobInfo job:jobsinBuffer){
			jobs+=job.getName()+",";
		}
		return jobs;
	}

	// private String[] getReturnStringArray(JobinBuffer job, long currentTime)
	// {
	// String[] result;
	// result = new String[3];
	// result[0] = job.jobType;
	// result[1] = job.curStepName;
	// result[2] = job.jobName;
	// return result;
	// }

	// private JobinBuffer getBestJobLookAhead(ToolInfo requestTool,
	// long currentTime, MaxMin[] rulePriorityMaxMin) {
	// double minUrgency = 500 * 24 * 3600;
	// double maxBatch = 0;
	// ArrayList<JobinBuffer> jobsSingle = new ArrayList<JobinBuffer>();
	// ArrayList<JobinBuffer> jobsBatch = new ArrayList<JobinBuffer>();
	//
	// for (int i = 0; i < jobsinBuffer.size(); i++) {
	// JobinBuffer job = jobsinBuffer.get(i);
	// // the next step is a single processing tool
	// // the current step is not the last step
	// if (job.nextBatchID.size() == 0
	// && !job.nextToolGroupName.equals("Last")) {
	// double urgentWeight = nextBufferWeights
	// .getUrgentWeight(job.nextToolGroupName);
	// if (minUrgency > urgentWeight) {
	// jobsSingle.clear();
	// minUrgency = urgentWeight;
	// jobsSingle.add(job);
	// } else if (minUrgency == urgentWeight) {
	// // store jobs with the same urgent weight
	// jobsSingle.add(job);
	// }
	// // the current step is the last step(maybe single
	// // processing tool or batch processing tool)
	// } else if (job.nextToolGroupName.equals("Last")) {
	// double t = 0.4;
	// if (maxBatch < t) {
	// jobsBatch.clear();
	// maxBatch = t;
	// jobsBatch.add(job);
	// } else if (maxBatch == t) {
	// jobsBatch.add(job);
	// }
	// }
	// // the next step is batch processing tool
	// else if (job.nextBatchID.size() > 0) {
	// for (int j = 0; j < job.nextBatchID.size(); j++) {
	// double urgentWeight = nextBufferWeights.getUrgentWeight(
	// job.nextToolGroupName, job.nextBatchID.get(j));
	// if (maxBatch < urgentWeight) {
	// jobsBatch.clear();
	// maxBatch = urgentWeight;
	// jobsBatch.add(job);
	// } else if (maxBatch == urgentWeight) {
	// jobsBatch.add(job);
	// }
	//
	// }
	// } else {
	// System.out.println("Look ahead error");
	// System.exit(0);
	// }
	// }
	//
	// ArrayList<JobinBuffer> selectedJobs = null;
	// // the jobs whoes next setp is batch processing tool will be
	// // dispatched
	// if (maxBatch > 0.3 || (jobsSingle.size() == 0 && jobsBatch.size() > 0)) {
	//
	// selectedJobs = jobsBatch;
	// }
	// // The jobs whoes next step is single processing tool will be
	// // dispatched
	// // because the jobs whoes next setp is batch processing tool
	// // have low urgent weights
	// else {
	// selectedJobs = jobsSingle;
	// }
	// return getIndexofBestJobformSelectedJobs(requestTool, currentTime,
	// rulePriorityMaxMin, selectedJobs, Rule.FIFO);
	//
	// }

	private JobInfo getIndexofBestJob(ToolInfo requestTool, long currentTime,
			ArrayList<JobInfo> selectedJobs, int rule) {
		int fIndex = 0;

		double max = DispatchingRule.getPriorityValue(rule, currentTime,
				selectedJobs.get(0), requestTool);

		for (int j = 1; j < selectedJobs.size(); j++) {

			double va = DispatchingRule.getPriorityValue(rule, currentTime,
					selectedJobs.get(j), requestTool);
			if (max < va) {
				max = va;
				fIndex = j;
			}
		}
		return selectedJobs.get(fIndex);
	}

	// private JobinBuffer getIndexofBestJobformSelectedJobs(ToolInfo
	// requestTool,
	// long currentTime, MaxMin[] rulePriorityMaxMin,
	// ArrayList<JobinBuffer> selectedJobs, int rule) {
	// int fIndex = 0;
	// double st = requestTool.getSetupTime(selectedJobs.get(0));
	// double max = getMultiObjPriority(rulePriorityMaxMin, selectedJobs
	// .get(0).getValue(rule, currentTime), -1 * st,
	// selectedJobs.get(0).priority, selectedJobs.get(0)
	// .getCriticalTimePrio(currentTime));
	//
	// for (int j = 1; j < selectedJobs.size(); j++) {
	// st = requestTool.getSetupTime(selectedJobs.get(j));
	// double va = getMultiObjPriority(rulePriorityMaxMin, selectedJobs
	// .get(j).getValue(rule, currentTime), -1 * st,
	// selectedJobs.get(j).priority, selectedJobs.get(j)
	// .getCriticalTimePrio(currentTime));
	// if (max < va) {
	// max = va;
	// fIndex = j;
	// }
	// }
	// return selectedJobs.get(fIndex);
	// }
	//
	// private JobinBuffer getIndexofBestJob(ToolInfo requestTool,
	// long currentTime, MaxMin[] rulePriorityMaxMin,int currentRule) {
	// return getIndexofBestJobformSelectedJobs(requestTool, currentTime,
	// rulePriorityMaxMin, jobsinBuffer, currentRule);
	// }
	//
	// private MaxMin[] getPrioirtyMinmax(ToolInfo requestTool, long
	// currentTime,int currentRule) {
	// // max and min priority for multi-objectives
	// // for normalization
	// MaxMin[] rulePriorityMaxMin = new MaxMin[4];
	// for (int i = 0; i < rulePriorityMaxMin.length; i++) {
	// rulePriorityMaxMin[i] = new MaxMin();
	// }
	//
	// if(jobsinBuffer.size()>5){
	// int a=0;
	// a=1;
	// }
	// for (int i = 0; i < jobsinBuffer.size(); i++) {
	// double v = jobsinBuffer.get(i).getValue(currentRule, currentTime);
	// if (currentRule == Rule.LookAhead) {
	// v = jobsinBuffer.get(i).getValue(Rule.FIFO, currentTime);
	// }
	// if (v > rulePriorityMaxMin[0].max)
	// rulePriorityMaxMin[0].max = v;
	// if (v < rulePriorityMaxMin[0].min)
	// rulePriorityMaxMin[0].min = v;
	// v = -1 * requestTool.getSetupTime(jobsinBuffer.get(i));
	// if (v > rulePriorityMaxMin[1].max)
	// rulePriorityMaxMin[1].max = v;
	// if (v < rulePriorityMaxMin[1].min)
	// rulePriorityMaxMin[1].min = v;
	// v = jobsinBuffer.get(i).priority;
	// if (v > rulePriorityMaxMin[2].max)
	// rulePriorityMaxMin[2].max = v;
	// if (v < rulePriorityMaxMin[2].min)
	// rulePriorityMaxMin[2].min = v;
	// v = jobsinBuffer.get(i).getCriticalTimePrio(currentTime);
	// if (v > rulePriorityMaxMin[3].max)
	// rulePriorityMaxMin[3].max = v;
	// if (v < rulePriorityMaxMin[3].min)
	// rulePriorityMaxMin[3].min = v;
	// }
	// // end multi-objectives
	// return rulePriorityMaxMin;
	// }

	// public String[] arrayPoll(ToolInfo rtool, long currentTime, int batchID)
	// {
	// return poll(rtool, currentTime, batchID);
	// }

	public int size() {
		return jobsinBuffer.size();
	}

	// public boolean NoBuffer() {
	// return bufferCapacity == 0;
	// }

	public void clear() {
		jobsinBuffer.clear();
		// waitSetupNum = 0;
	}

	// public String getJobNameinBuffer() {
	// String s = "";
	// for (int i = 0; i < jobsinBuffer.size(); i++) {
	// String str = jobsinBuffer.get(i).jobName;
	// s += str + ",";
	// }
	// return s;
	// }
	//
	// public String getRulePriority(Long currentTime,int currentRule) {
	// String s = "";
	// for (int i = 0; i < jobsinBuffer.size(); i++) {
	// JobinBuffer job = jobsinBuffer.get(i);
	// s += job.jobName + " "
	// + jobsinBuffer.get(i).getValue(currentRule, currentTime)
	// + ", ";
	//
	// }
	// return s;
	// }
	//
	// public double getMultiObjPriority(MaxMin[] maxMin, double rulePrio,
	// double avoidSetupPrio, double hotLotPrio, double criticalTimePrio) {
	//
	// double ruleP = maxMin[0].Min2Max() == 0 ? 0 : multiObjWeight.ruleWeight
	// * (rulePrio - maxMin[0].min) / maxMin[0].Min2Max();
	// double avoidSetupP = maxMin[1].Min2Max() == 0 ? 0
	// : multiObjWeight.avoidSetupWeight
	// * (avoidSetupPrio - maxMin[1].min)
	// / maxMin[1].Min2Max();
	// double hotLotP = maxMin[2].Min2Max() == 0 ? 0
	// : multiObjWeight.hotLotWeight * (hotLotPrio - maxMin[2].min)
	// / maxMin[2].Min2Max();
	// double criticalTimeP = maxMin[3].Min2Max() == 0 ? 0
	// : multiObjWeight.criticalTimeWeight
	// * (criticalTimePrio - maxMin[3].min)
	// / maxMin[3].Min2Max();
	//
	// return ruleP + avoidSetupP + hotLotP + criticalTimeP;
	//
	// }

	public BufferInfo clone(AgentBasedModel agentModel, WaitDataset waitDataset) {
		BufferInfo bufferInfo = new BufferInfo();
		bufferInfo.setBufferCapacity(bufferCapacity);
		bufferInfo.currentRule = currentRule;
		bufferInfo.setBatchTool(isBatchTool);
		bufferInfo.jobsinBuffer = new ArrayList<JobInfo>();
		bufferInfo.setWaitDataset(waitDataset);

		for (JobInfo job : jobsinBuffer) {
			if(agentModel.getJobAgent(job.getName())==null){
				System.out.println("9999999999999999999999999999999999999999999999999999");
				System.out.println("9999999999999999999999999999999999999999999999999999");
				System.out.println(job.getName());
			}
			bufferInfo.jobsinBuffer.add(agentModel.getJobAgent(job.getName())
					.getJobInfo());
		}

		return bufferInfo;
	}

	public ArrayList<JobInfo> getJobsinBufferClone() {
		ArrayList<JobInfo> iJobsinBuffer = new ArrayList<JobInfo>();
		for (JobInfo j : jobsinBuffer) {
			iJobsinBuffer.add(j.clone());
		}
		return iJobsinBuffer;
	}

	//
	public ArrayList<JobInfo> getJobsinBuffer() {
		return jobsinBuffer;
	}

	//
	public void remove(JobInfo job) {
		jobsinBuffer.remove(job);
	}

	public int remove(String job) {
		int num=0;
		for (int i = 0; i < jobsinBuffer.size(); i++) {
			if (jobsinBuffer.get(i).getName().equals(job)) {
				jobsinBuffer.remove(i);
				i--;
				num++;
			}
		}
		return num;

	}


	public double getBufferTime() {
		double sum = 0;
		for (int i = 0; i < jobsinBuffer.size(); i++) {

			sum += jobsinBuffer.get(i).getCurrentProcessTime();
		}
		return sum;
	}

	//
	public double getTotalWaitingTime(long currentTime) {
		double sum = 0;
		for (int i = 0; i < jobsinBuffer.size(); i++) {

			sum += currentTime - jobsinBuffer.get(i).getStateStartTime();
		}
		return sum;
	}

	public ArrayList<JobInfo> poll(ToolInfo toolInfo, long currentTime,
			int bufferState) {
		// TODO Auto-generated method stub
		return poll(toolInfo, currentTime, bufferState, currentRule);
	}

	public int getBufferCapacity() {
		return bufferCapacity;
	}

	public void setBufferCapacity(int bufferCapacity) {
		this.bufferCapacity = bufferCapacity;
	}

	public WaitDataset getWaitDataset() {
		return waitDataset;
	}

	public void setWaitDataset(WaitDataset waitDataset) {
		this.waitDataset = waitDataset;
	}

	public boolean isBatchTool() {
		return isBatchTool;
	}

	public void setBatchTool(boolean isBatchTool) {
		this.isBatchTool = isBatchTool;
	}

}
