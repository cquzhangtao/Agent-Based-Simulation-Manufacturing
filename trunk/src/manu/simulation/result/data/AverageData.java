package manu.simulation.result.data;

public class AverageData {
	
	public static WaitDataset average(WaitDataset[] wds){
		WaitDataset wd=new WaitDataset(0);
		int num=wds.length;
		for(int i=0;i<num;i++){
			wd.avgWaitTime+=wds[i].avgWaitTime;
			wd.maxWaitTime+=wds[i].maxWaitTime;
		}
		wd.avgWaitTime/=num;
		wd.maxWaitTime/=num;
		
		return wd;
	}
	
	
	public static BlockDataset average(BlockDataset[] wds){
		BlockDataset wd=new BlockDataset(0);
		int num=wds.length;
		for(int i=0;i<num;i++){
			wd.avgBlockTime+=wds[i].avgBlockTime;
			wd.maxBlockTime+=wds[i].maxBlockTime;
		}
		wd.avgBlockTime/=num;
		wd.maxBlockTime/=num;
		
		return wd;
	}
	
	public static BufferData average(BufferData[] wds,String name){
		BufferData wd=new BufferData(name);
		int num=wds.length;
		for(int i=0;i<num;i++){
			wd.avgBufferSize+=wds[i].avgBufferSize;
			wd.maxBufferSize+=wds[i].maxBufferSize;
		}
		wd.avgBufferSize/=num;
		wd.maxBufferSize/=num;
		
		return wd;
	}
	
	public static ToolData average(ToolData[] tds){
		ToolData td=new ToolData();
		td.blockTime=0;
		td.freeTime=0;
		td.processTime=0;
		td.breakdownTime=0;
		td.maintenanceTime=0;
		td.interruptNum=0;
		td.setupTime=0;
		td.totalSetupTime=0;
		
		int num=tds.length;
		for(int i=0;i<num;i++){
			td.blockTime+=tds[i].blockTime;
			td.freeTime+=tds[i].freeTime;
			td.processTime+=tds[i].processTime;
			td.breakdownTime+=tds[i].breakdownTime;
			td.maintenanceTime+=tds[i].maintenanceTime;
			td.interruptNum+=tds[i].interruptNum;
			td.setupTime+=tds[i].setupTime;
			td.totalSetupTime+=tds[i].totalSetupTime;
		}
		td.blockTime/=num;
		td.freeTime/=num;
		td.processTime/=num;
		td.breakdownTime/=num;
		td.maintenanceTime/=num;
		td.interruptNum/=num;
		td.setupTime/=num;
		td.totalSetupTime/=num;
		
		return td;
	}


	public  static ReleaseData average(ReleaseData[]rds,String product){
		ReleaseData rd=new ReleaseData(product);
		rd.minCycleTime=0;
		int num=rds.length;
		for(int i=0;i<num;i++){
			rd.avgCycleTime+=rds[i].avgCycleTime;
			rd.avgWip+=rds[i].avgWip;
			rd.finishedLotNum+=rds[i].finishedLotNum;
			rd.maxCycleTime+=rds[i].maxCycleTime;
			rd.minCycleTime+=rds[i].minCycleTime;
			rd.maxWip+=rds[i].maxWip;
			rd.productivity+=rds[i].productivity;
			rd.releasedLotNum+=rds[i].releasedLotNum;
			rd.releaseRatio+=rds[i].releaseRatio;
		}
		rd.avgCycleTime/=num;
		rd.avgWip/=num;
		rd.finishedLotNum/=num;
		rd.maxCycleTime/=num;
		rd.minCycleTime/=num;
		rd.maxWip/=num;
		rd.productivity/=num;
		rd.releasedLotNum/=num;
		rd.releaseRatio/=num;
		rd.rawProcessTime=rds[0].rawProcessTime;
		//rd.product=rds[0].product;
		return rd;
		
	}
}
