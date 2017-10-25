package manu.simulation.result.data;

import java.io.Serializable;

public class WaitData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -591162995029772382L;
	String jobName;
	long endWaitTime;
	long waitTime;
	public WaitData(String jobName, long endWaitTime, long waitTime) {
		// TODO Auto-generated constructor stub
		this.jobName=jobName;
		this.endWaitTime=endWaitTime;
		this.waitTime=waitTime;
		
	}

	

}
