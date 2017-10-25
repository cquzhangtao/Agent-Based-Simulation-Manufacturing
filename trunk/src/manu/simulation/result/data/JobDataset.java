package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class JobDataset implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 812812008662064158L;

	public ArrayList<JobData>[] jobDataset;
	
	private int[] preSize;
	
	public void save(){
		for(int i=0;i<jobDataset.length;i++){
			preSize[i]=jobDataset[i].size();
		}
	}
	public void recover(){
		for(int i=0;i<jobDataset.length;i++){
			while(jobDataset[i].size()>preSize[i])
			 jobDataset[i].remove(preSize[i]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void init(int typeNum){
		jobDataset=new ArrayList[typeNum];
		preSize=new int[typeNum];
		for (int i = 0; i < typeNum; i++) {
			jobDataset[i] = new ArrayList<JobData>();
		}
	}
	public void add(int index,JobData data){
		jobDataset[index].add(data);
	}
	public int size() {
		// TODO Auto-generated method stub
		return jobDataset.length;
	}
	public ArrayList<JobData> get(int i) {
		// TODO Auto-generated method stub
		return jobDataset[i];
	}
	public void reset(){
		for(int i=0;i<jobDataset.length;i++){
			jobDataset[i].clear();
		}
	}
}
