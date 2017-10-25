package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JobData1 implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -23727391617557407L;
	public String jobName;
	public String jobType;
	public int jobTypeIndex;
	public long releaseTime;
	public long cycleTime;
	public ArrayList<Long> waitTime=new ArrayList<Long>();
	public ArrayList<String> waitToolGroup=new ArrayList<String>();
	
	public ArrayList<Long> blockTime=new ArrayList<Long>();
	public ArrayList<String> blockToolGroup=new ArrayList<String>();
	
	
	

}
