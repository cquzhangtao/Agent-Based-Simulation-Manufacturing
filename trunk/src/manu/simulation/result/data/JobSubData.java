package manu.simulation.result.data;

import java.io.Serializable;

public class JobSubData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4320807771477246260L;
	public String toolName;
	public long transportBeginTime;
	public long waitBeginTime;
	public long processBeginTime;
	public long blockBeginTime;
	public long nextTransportBeginTime;
	public boolean finished=false;

	JobSubData() {

	}
	
	public JobSubData clone(){
		JobSubData subData=new JobSubData();
		subData.toolName=toolName;
		subData.transportBeginTime=transportBeginTime;
		subData. waitBeginTime=waitBeginTime;
		subData. processBeginTime=processBeginTime;
		subData. blockBeginTime=blockBeginTime;
		subData.nextTransportBeginTime=nextTransportBeginTime;
		subData. finished=finished;
		return subData;
	}

	JobSubData(String s) {
		String temp[] = s.split("-");
		int i = 0;
		toolName = temp[i++];
		transportBeginTime = Long.valueOf(temp[i++]);
		waitBeginTime = Long.valueOf(temp[i++]);
		processBeginTime = Long.valueOf(temp[i++]);
		blockBeginTime = Long.valueOf(temp[i++]);
		nextTransportBeginTime = Long.valueOf(temp[i++]);
	}

	@Override
	public String toString() {
		return String.format("%s-%d-%d-%d-%d-%d", toolName, transportBeginTime,
				waitBeginTime, processBeginTime, blockBeginTime,
				nextTransportBeginTime);
	}
}
