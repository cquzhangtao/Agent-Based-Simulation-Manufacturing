package manu.simulation.result.data;

import java.io.Serializable;

public class BlockData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2125324664719188024L;
	long endBlockTime;
	long blockTime;
	public BlockData(long endBlockTime, long blockTime) {
		// TODO Auto-generated constructor stub
		this.endBlockTime=endBlockTime;
		this.blockTime=blockTime;		
	}

}
