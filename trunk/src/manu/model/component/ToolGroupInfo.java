package manu.model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import manu.model.component.state.ToolState;
import manu.model.feature.Batch;
import manu.model.feature.BufferWeights;
import manu.model.feature.Interrupt;
import manu.scheduling.DecisionMaker;
import manu.scheduling.training.data.SequencingTrainingData;
import manu.simulation.result.data.ToolData;
import manu.simulation.result.data.ToolGroupData;
import manu.utli.Entity;

public class ToolGroupInfo extends Entity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String toolGroupName;
	private int toolNuminGroup;
	private int bufferCapacity;
	private int dispatchingRule;
	private boolean isBatchTool = false;
	
	private ToolInfo[] toolInfos;
	private BufferInfo bufferInfo;
	
	private DecisionMaker<SequencingTrainingData> deciesionMaker;

	private ArrayList<String> allToolGroupName;
	private ArrayList<Interrupt> interrupts = new ArrayList<Interrupt>();


	private ArrayList<Batch> batch;

	private List<Skill> skills = new ArrayList<Skill>();
	private Map<String, Long> setups = new HashMap<String, Long>();
	
	private ArrayList<String> previousToolGroupName = new ArrayList<String>();
	private BufferWeights nextBufferWeights = new BufferWeights();
	
	//private boolean avoidSetup = false;
	//public MultiObjWeight multiObjWeight = new MultiObjWeight();
	//public Bottleneck bottleneck = new Bottleneck();

	public ToolGroupInfo(String groupName, int toolNum, int rule) {
		super(groupName);
		toolNuminGroup = toolNum;
		toolInfos = new ToolInfo[toolNum];
		toolGroupName = groupName;
		this.dispatchingRule = rule;

	}

	public ToolGroupInfo(String groupName, int toolNum) {
		toolNuminGroup = toolNum;
		toolInfos = new ToolInfo[toolNum];
		toolGroupName = groupName;
		dispatchingRule = 0;
	}

	public DecisionMaker<SequencingTrainingData> getDisicionMaker() {
		return deciesionMaker;
	}

	public void setDecisionMaker(DecisionMaker<SequencingTrainingData> dispatcher) {
		this.deciesionMaker = dispatcher;
	}

	public ArrayList<String> getAllToolGroupName() {
		return allToolGroupName;
	}

	public void setAllToolGroupName(ArrayList<String> allToolGroupName) {
		this.allToolGroupName = allToolGroupName;
	}

	public void addPreTool(String toolName) {
		if (!previousToolGroupName.contains(toolName))
			previousToolGroupName.add(toolName);
	}

	public void addNextBuffer(String toolGroupName, boolean isBatchTool) {
		nextBufferWeights.addBufferWeight(toolGroupName, isBatchTool);
	}

	public void setToolGroup(ArrayList<Batch> batch, int bufferCapacity,
			ArrayList<Interrupt> interrupt) {
		this.bufferCapacity = bufferCapacity;
		this.batch = batch;
		if (batch != null && batch.size() > 0) {
			isBatchTool = true;
		}
		for (Interrupt inter : interrupt) {
			if (inter.applyTo.equals("AllTool")
					|| inter.applyTo.equals("OneTool")) {
				inter.index = interrupts.size();
				inter.setRange("ToolGroup");
				interrupts.add(inter);
			}
		}
		for (int i = 0; i < toolNuminGroup; i++) {
			toolInfos[i] = new ToolInfo(toolGroupName, toolGroupName
					+ String.format("%02d", i), toolGroupName);
			toolInfos[i].toolNuminGroup = toolNuminGroup;
			toolInfos[i].isBatchTool = isBatchTool;
			toolInfos[i].setSetups(setups);
			toolInfos[i].toolLife = 0;
			toolInfos[i].toolIndex = i;
			for (Interrupt inter : interrupt) {
				if (inter.applyTo.equals("EachTool")
						|| inter.applyTo.equals("SpecifyTool")
						&& inter.specifiedToolIndex == i) {
					toolInfos[i].addInterrupt(inter);
					inter.setRange("Tool");
				}
			}
		}
	}

	public String[] getToolsAID() {
		String[] names = new String[toolNuminGroup];
		for (int i = 0; i < toolNuminGroup; i++) {
			names[i] = toolInfos[i].toolName;
		}
		return names;
	}

	public ToolInfo toolInfo(int index) {
		return toolInfos[index];
	}

	public ToolInfo toolInfo(String toolName) {
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].toolName.equals(toolName))
				return toolInfos[i];
		}
		return null;
	}

	public int toolIndex(ToolInfo toolInfo) {
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].toolName.equals(toolInfo.toolName))
				return i;
		}
		return -1;
	}

	public int BatchNumber() {
		if (isBatchTool)
			return batch.get(0).BatchNum;
		else
			return 1;
	}

	//
	public int BatchNumber(ArrayList<Integer> batchIDs) {
		if (batchIDs == null) {
			return 1;
		}

		int sum = 0;
		for (int i = 0; i < batchIDs.size(); i++) {
			sum += getBatchNum(batchIDs.get(i));
		}

		return sum / batchIDs.size();

	}

	public int getBatchNum(int batchID) {
		if (!isBatchTool)
			return 1;
		for (int i = 0; i < batch.size(); i++) {
			if (batch.get(i).BatchID == batchID) {
				return batch.get(i).BatchNum;
			}
		}
		return 1;
	}

	public void setToolStatebyName(String toolName, int state, int toolLife) {
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].toolName.equals(toolName)) {
				toolInfos[i].state = state;
				toolInfos[i].toolLife = toolLife;
				break;
			}
		}
	}

	public void setToolStatebyName(String toolName, int state) {
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].toolName.equals(toolName)) {
				toolInfos[i].state = state;
				break;
			}
		}
	}

	public int[] getToolStatebyName(String toolName) {
		int[] result = new int[2];
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].toolName.equals(toolName)) {
				result[0] = toolInfos[i].state;
				result[1] = toolInfos[i].toolLife;
				break;
			}
		}
		return result;
	}

	public String[] getFreeToolNames() {
		String str = "";
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].state == ToolState.FREE) {
				str = str + "-" + toolInfos[i].toolName;
			}
		}
		if (!str.equals(""))
			str = str.substring(1);
		return str.split("-");
	}

	public ArrayList<ToolInfo> getFreeTools() {
		ArrayList<ToolInfo> tool = new ArrayList<ToolInfo>();
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].state == ToolState.FREE) {
				tool.add(toolInfos[i]);
			}
		}
		return tool;

	}

	public int getFreeToolNumber() {
		int num = 0;
		for (int i = 0; i < toolNuminGroup; i++) {
			if (toolInfos[i].state == ToolState.FREE) {
				num++;
			}
		}
		return num;

	}

	public void reset() {

		for (ToolInfo tool : toolInfos) {
			tool.reset();
		}
		for (Interrupt intr : interrupts)
			intr.reset();
	}

	public Interrupt[] getInterruptofToolGroup() {
		if (interrupts == null)
			return null;
		ArrayList<Interrupt> breakDown = new ArrayList<Interrupt>();
		for (int i = 0; i < interrupts.size(); i++) {
			if (interrupts.get(i).applyTo.equals("AllTool")) {
				breakDown.add(interrupts.get(i));
			}
		}
		return breakDown.toArray(new Interrupt[0]);
	}

	public Interrupt[] getInterruptinOneTool() {
		if (interrupts == null)
			return null;
		ArrayList<Interrupt> breakDown = new ArrayList<Interrupt>();
		for (int i = 0; i < interrupts.size(); i++) {
			if (interrupts.get(i).applyTo.equals("OneTool")) {
				breakDown.add(interrupts.get(i));
			}
		}
		return breakDown.toArray(new Interrupt[0]);
	}

	public Interrupt[] getInterruptinEachTool() {
		if (interrupts == null)
			return null;
		ArrayList<Interrupt> breakDown = new ArrayList<Interrupt>();
		for (int i = 0; i < interrupts.size(); i++) {
			if (interrupts.get(i).applyTo.equals("EachTool")) {
				breakDown.add(interrupts.get(i));
			}
		}
		return breakDown.toArray(new Interrupt[0]);
	}

	public Interrupt[] getInterruptinSpecifiedTool() {
		if (interrupts == null)
			return null;
		ArrayList<Interrupt> breakDown = new ArrayList<Interrupt>();
		for (int i = 0; i < interrupts.size(); i++) {
			if (interrupts.get(i).applyTo.equals("SpecifyTool")) {
				breakDown.add(interrupts.get(i));
			}
		}
		return breakDown.toArray(new Interrupt[0]);
	}

	public Interrupt getInterruptByIndex(int index) {
		return interrupts.get(index);
	}

	public void save() {
		for (ToolInfo tool : toolInfos) {
			tool.save();
		}
		for (Interrupt intr : interrupts)
			intr.save();
	}

	public void recover() {
		for (ToolInfo tool : toolInfos) {
			tool.recover();
		}
		for (Interrupt intr : interrupts)
			intr.recover();
	}

	public void bindDatasettoTool(ToolGroupData toolGroupData, int period) {
		for (int k = 0; k < toolNuminGroup; k++) {
			ToolData data = new ToolData();
			data.interval = period;
			toolGroupData.addTool(data);
			toolInfo(k).setToolData(data);
		}
	}

	public double getUnavailableToolRatioD() {

		int sum1 = 0;
		for (ToolInfo t : toolInfos) {
			if (t.state == ToolState.BREAKDOWN
					|| t.state == ToolState.MAINTENANCE)
				sum1++;
		}
		return 1.0 * sum1 / toolNuminGroup;

	}

	public String getToolGroupName() {
		return toolGroupName;
	}

	public void setToolGroupName(String toolGroupName) {
		this.toolGroupName = toolGroupName;
	}

	public int getToolNuminGroup() {
		return toolNuminGroup;
	}

	public void setToolNuminGroup(int toolNuminGroup) {
		this.toolNuminGroup = toolNuminGroup;
	}


	public int getRule() {
		return dispatchingRule;
	}

	public void setRule(int rule) {
		this.dispatchingRule = rule;
	}

	public boolean isBatchTool() {
		return isBatchTool;
	}

	public void setBatchTool(boolean isBatchTool) {
		this.isBatchTool = isBatchTool;
	}

	public ArrayList<Interrupt> getInterrupts() {
		return interrupts;
	}

	public void setInterrupts(ArrayList<Interrupt> interrupts) {
		this.interrupts = interrupts;
	}

	public ArrayList<String> getPreviousToolGroupName() {
		return previousToolGroupName;
	}

	public void setPreviousToolGroupName(ArrayList<String> previousToolGroupName) {
		this.previousToolGroupName = previousToolGroupName;
	}

	public BufferWeights getNextBufferWeights() {
		return nextBufferWeights;
	}

	public void setNextBufferWeights(BufferWeights nextBufferWeights) {
		this.nextBufferWeights = nextBufferWeights;
	}

//	public boolean isAvoidSetup() {
//		return avoidSetup;
//	}
//
//	public void setAvoidSetup(boolean avoidSetup) {
//		this.avoidSetup = avoidSetup;
//	}
//
//	public MultiObjWeight getMultiObjWeight() {
//		return multiObjWeight;
//	}
//
//	public void setMultiObjWeight(MultiObjWeight multiObjWeight) {
//		this.multiObjWeight = multiObjWeight;
//	}



	public ArrayList<Batch> getBatch() {
		return batch;
	}

	public void setBatch(ArrayList<Batch> batch) {
		this.batch = batch;
	}

	public ToolInfo[] getToolInfos() {
		return toolInfos;
	}

	public void setToolsInfo(ToolInfo[] toolsInfo) {
		this.toolInfos = toolsInfo;
	}

	public Batch getBatch(int batchID) {
		for (Batch b : batch) {
			if (b.getBatchID() == batchID) {
				return b;
			}
		}
		return null;

	}

	public BufferInfo getBufferInfo() {
		return bufferInfo;
	}

	public void setBufferInfo(BufferInfo bufferInfo) {
		this.bufferInfo = bufferInfo;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	public Map<String, Long> getSetups() {
		return setups;
	}

	public void setSetups(Map<String, Long> setups) {
		this.setups = setups;
	}

	public ToolGroupInfo() {

	}

	public ToolGroupInfo clone(BufferInfo bufferIn,ToolGroupData tgData) {
		ToolGroupInfo tgi = new ToolGroupInfo();

		tgi.toolGroupName = toolGroupName;
		tgi.toolNuminGroup = toolNuminGroup;
		tgi.bufferCapacity = bufferCapacity;
		tgi.dispatchingRule = dispatchingRule;
		tgi.isBatchTool = isBatchTool;
		tgi.allToolGroupName = allToolGroupName;
		tgi.skills = skills;
		tgi.setups = setups;
		tgi.bufferInfo = bufferIn;

		// public ArrayList<Interrupt> interrupts = new ArrayList<Interrupt>();

		tgi.toolInfos = new ToolInfo[toolNuminGroup];
		int index = 0;
		for (ToolInfo ti : toolInfos) {
			tgi.toolInfos[index] = ti.clone();
			ToolData tData=new ToolData();
			tgData.addTool(tData);
			tgi.toolInfos[index].setToolData(tData);
			index++;
		}
		return tgi;

	}

	public int getBufferCapacity() {
		return bufferCapacity;
	}

	public int getDispatchingRule() {
		return dispatchingRule;
	}

	public void setBufferCapacity(int bufferCapacity) {
		this.bufferCapacity = bufferCapacity;
	}

	public void setDispatchingRule(int dispatchingRule) {
		this.dispatchingRule = dispatchingRule;
	}
	
//	public double getSetupTime(JobInfo jobInfo){
//		for(String str:setups.keySet()){
//			if(str.substring(beginIndex))
//		}
//	}
	
	public double getUtilization(){
		double sum=0;
		for(ToolInfo tool:toolInfos){
			sum+=tool.getUtilization();
		}
		
		return sum/toolInfos.length;
	}
}
