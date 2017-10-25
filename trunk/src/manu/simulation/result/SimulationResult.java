package manu.simulation.result;

import manu.model.ManufactureModel;
import manu.model.component.ToolGroupInfo;
import manu.simulation.info.SimulationInfo;
import manu.simulation.result.data.BufferData;
import manu.simulation.result.data.BufferDataset;
import manu.simulation.result.data.JobDataset;
import manu.simulation.result.data.ReleaseDataset;
import manu.simulation.result.data.ToolGroupData;
import manu.simulation.result.data.ToolGroupDataset;

public class SimulationResult {
	public BufferDataset bufferDataset;
	public JobDataset jobDataset;
	public ReleaseDataset releaseDataset;
	public ToolGroupDataset toolGroupDataset;
	
	public SimulationResult(){
		bufferDataset = new BufferDataset();
		releaseDataset = new ReleaseDataset();
		jobDataset = new JobDataset();
		toolGroupDataset = new ToolGroupDataset();
	}
	
	public void generateStructure(ManufactureModel manuModel,SimulationInfo simulationInfo){

		for (int i = 0; i < manuModel.getToolGroups().size(); i++) {
			ToolGroupInfo toolGroupInfo = manuModel.getToolGroups().get(i);
			BufferData bufferdata = new BufferData(toolGroupInfo.getToolGroupName());
			bufferdata.interval=simulationInfo.dataAcqTime;
			bufferDataset.add(bufferdata);
			ToolGroupData toolgroupData = new ToolGroupData(toolGroupInfo.getToolGroupName(),simulationInfo.dataAcqTime);
			toolGroupDataset.add(toolgroupData,simulationInfo.dataAcqTime);
			toolGroupInfo.bindDatasettoTool(toolgroupData,simulationInfo.dataAcqTime);
		}

		releaseDataset.init(manuModel.getProducts(),simulationInfo.dataAcqTime);
		jobDataset.init(manuModel.getProducts().length);

	}
	public ToolGroupData getToolGroupData(String name){
		return toolGroupDataset.getToolGroupData(name);
	}
	
	public BufferData getBufferData(String name){
		return bufferDataset.getBufferData(name);
	}
	
	public void renew(){
		bufferDataset = new BufferDataset();
		releaseDataset = new ReleaseDataset();
		jobDataset = new JobDataset();
		toolGroupDataset = new ToolGroupDataset();
	}
	
	public void reset(){
		bufferDataset.reset();
		toolGroupDataset.reset();
		releaseDataset.reset();
		jobDataset.reset();
	}
	public void stat(){
		bufferDataset.stat();
		toolGroupDataset.stat();
		releaseDataset.stat();
	}
	public void save(){
		bufferDataset.save();
		toolGroupDataset.save();
		releaseDataset.save();
		jobDataset.save();
	}
	
	public void recover(){
		bufferDataset.recover();
		toolGroupDataset.recover();
		releaseDataset.recover();
		jobDataset.recover();
	}

	public BufferDataset getBufferDataset() {
		return bufferDataset;
	}

	public JobDataset getJobDataset() {
		return jobDataset;
	}

	public ReleaseDataset getReleaseDataset() {
		return releaseDataset;
	}

	public ToolGroupDataset getToolGroupDataset() {
		return toolGroupDataset;
	}

	public void setBufferDataset(BufferDataset bufferDataset) {
		this.bufferDataset = bufferDataset;
	}

	public void setJobDataset(JobDataset jobDataset) {
		this.jobDataset = jobDataset;
	}

	public void setReleaseDataset(ReleaseDataset releaseDataset) {
		this.releaseDataset = releaseDataset;
	}

	public void setToolGroupDataset(ToolGroupDataset toolGroupDataset) {
		this.toolGroupDataset = toolGroupDataset;
	}
}
