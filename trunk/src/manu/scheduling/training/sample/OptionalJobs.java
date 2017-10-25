package manu.scheduling.training.sample;

import java.util.ArrayList;

import manu.model.component.JobInfo;


public class OptionalJobs{

	/**
	 * 
	 */
	ArrayList<JobInfo> list=new ArrayList<JobInfo>();
	int index=0;
	
	public void set(ArrayList<JobInfo> al){
		list=al;
		index=0;
	}
	
	public void add(JobInfo j){
		list.add(j);
	}
	public JobInfo getJob(){
		JobInfo j=list.get(index);
		index++;
		return j;
	}
	public boolean isFinished(){
		if(index==list.size()){
			return true;
		}
		return false;
	}
	@Override
	public OptionalJobs clone(){
		OptionalJobs op=new OptionalJobs();
		for(JobInfo j:list){
			op.add(j.clone());
		}
		return op;
	}
	public ArrayList<String> cloneString(){
		ArrayList<String> op=new ArrayList<String>();
		for(JobInfo j:list){
			op.add(j.getName());
		}
		return op;
	}
	@Override
	public String toString(){
		String str="";
		for(JobInfo j:list){
			str+=","+j.getName();
		}
		return str;
	}
}
