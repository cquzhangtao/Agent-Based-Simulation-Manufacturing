package tutorialsim;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.Timer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


import static java.lang.Math.*;



/**
 * ToolGroup
 */	
import java.io.ObjectInputStream;
import java.io.IOException;
public class ToolGroup extends Entity implements Serializable {
	

	private transient Map<Integer,Tool> tools=new HashMap<Integer,Tool>();
	private ProcessType processType;
	private int toolNumber;
	private Map<String,Double> setupTimes=new HashMap<String,Double>();
	private List<BatchingConfiguration> batchConfigs=new ArrayList<BatchingConfiguration>();
	private double mtbf=Double.MAX_VALUE;
	private double mttr=0;
	private double mtbm=Double.MAX_VALUE;
	private double mttm=0;
	private int setupConfig=3;
	private int dedicationConfig=3;
	private JobPriorityRule jobPriorityRule=JobPriorityRule.LPT;
	private int maxFrontQueueCapacity=50;
	private int maxEndQueueCapacity=50;
	
	private Set<ToolGroup> predecessors=new HashSet<ToolGroup>();
	
	private Set<ToolGroup> successors=new HashSet<ToolGroup>();
	private boolean firstStep=false;
	private Set<Product> productsStartHere=new HashSet<Product>();
	
	private transient BlockModeChangeListener blockModeChangeListener;
	
    /**
     * Default constructor
     */
    public ToolGroup(String name) {
    	super.setName(name);
    	batchConfigs.add(new BatchingConfiguration());
    }
    
    public void setBlockModeChangeListener(BlockModeChangeListener blockModeChangeListener){
    	this.blockModeChangeListener=blockModeChangeListener;
    }
    
    public BlockModeChangeListener getBlockModeChangeListener(){
    	return blockModeChangeListener;
    }
    
    public boolean isFirstStep(){
    	return firstStep;
    }
    
    public void setFirstStep(boolean value){
    	firstStep=value;
    }
    
    public  Set<Product> getProductsStartHere(){
    	return productsStartHere;
    }
    
    
    public Set<ToolGroup> getPredecessors(){
    	return predecessors;
    }
    public Set<ToolGroup> getSuccessors(){
    	return successors;
    }
    
    public void setMaxFrontQueueCapacity(int maxFrontQueueCapacity){
    	this.maxFrontQueueCapacity=maxFrontQueueCapacity;
    }
    
    public int getMaxFrontQueueCapacity(){
    	return maxFrontQueueCapacity;
    }
    
    public void setMaxEndQueueCapacity(int maxEndQueueCapacity){
    	this.maxEndQueueCapacity=maxEndQueueCapacity;
    }
    
    public long getMaxEndQueueCapacity(){
    	return maxEndQueueCapacity;
    }
    
	public JobPriorityRule getJobPriorityRule() {
		return jobPriorityRule;
	}
	public void setJobPriorityRule(JobPriorityRule jobPriorityRule) {
		this.jobPriorityRule = jobPriorityRule;
	}
    
    public Map<Integer,Tool> getTools(){
    	return tools;
    }
    
    public boolean hasFreeTools(){
    	for(Tool tool:tools.values()){
    		if(tool.getState()==ToolState.Idle){
    			return true;
    		}
    	}
    	return false;
    }
    
    public List<Tool> getFreeTools(){
    	List<Tool> freeTools=new ArrayList<Tool>();
    	for(Tool tool:tools.values()){
    		if(tool.getState()==ToolState.Idle){
    			freeTools.add(tool);
    		}
    	}
    	return freeTools;
    }
    
    public void setSetupConfig(int config){
    	setupConfig=config;
    }
    
    public int getSetupConfig(){
    	return setupConfig;
    }
    
    public void setDedicaitonConfig(int config){
    	dedicationConfig=config;
    }
    
    public int getDedicationConfig(){
    	return dedicationConfig;
    }
    
    public List<BatchingConfiguration> getBatchingConfigs(){
    	return batchConfigs;
    }
    
    public void addBatchingConfig(BatchingConfiguration config){
    	batchConfigs.add(batchConfigs.size()-1,config);
    }
    
    public void reset(){
    	
    	for(Tool tool:tools.values()){
    		tool.reset();
    	}
    }
    
  public void close(){
    	super.close();
    	for(Tool tool:tools.values()){
    		tool.close();
    	}
    }
    
    public void setMtbf(double time){
    	mtbf=time;
    }
    public void setMttr(double time){
    	mttr=time;
    }
    public void setMtbm(double time){
    	mtbm=time;
    }
    public void setMttm(double time){
    	mttm=time;
    }
    
    public double getMtbf(){
    	return mtbf;
    }
    public double getMttr(){
    	return mttr;
    }
    public double getMtbm(){
    	return mtbm;
    }
    public double getMttm(){
    	return mttm;
    }
    
    public void addSetupTimes(String state1,String state2,double time){
    	setupTimes.put(state1+"-"+state2,time);
    }
    
    public double getSetupTime(Step step1,Step step2){
    	if(step1==null){
    		return 0;
    	}
    	Double steupTime=setupTimes.get(step1.getSetupState()+"-"+step2.getSetupState());
    	if(steupTime==null){
    		return 0;
    	}
    	return steupTime.doubleValue();
    }
    
    public double getSetupTime(String state1,String state2){
    	Double steupTime=setupTimes.get(state1+"-"+state2);
    	if(steupTime==null){
    		return 0;
    	}
    	return steupTime.doubleValue();
    }
    
    public double getSumOfWorkTime(){
    	double sum=0;
    	for(Tool tool:tools.values()){
    		sum+=tool.getSumOfWorkTime();
    	}
    	return sum/tools.size();
    }
    
    public double getSumOfIdleTime(){
    	double sum=0;
    	for(Tool tool:tools.values()){
    		sum+=tool.getSumOfIdleTime();
    	}
    	return sum/tools.size();
    }
    
    public double getSumOfBreakTime(){
    	double sum=0;
    	for(Tool tool:tools.values()){
    		sum+=tool.getSumOfBreakTime();
    	}
    	return sum/tools.size();
    }
    
    public double getSumOfMaintainTime(){
    	double sum=0;
    	for(Tool tool:tools.values()){
    		sum+=tool.getSumOfMaintainTime();
    	}
    	return sum/tools.size();
    }
    
    public double getSumOfSetupTime(){
    	double sum=0;
    	for(Tool tool:tools.values()){
    		sum+=tool.getSumOfSetupTime();
    	}
    	return sum/tools.size();
    }
    
    public double getSumOfBlockTime(){
    	double sum=0;
    	for(Tool tool:tools.values()){
    		sum+=tool.getSumOfBlockTime();
    	}
    	return sum/tools.size();
    }
    
    
    public void addTool(Tool tool){
    	int index=generateToolIndex();
    	tool.setName(getName()+"_"+(index+1));
    	
    	tool.setIndex(index);
    	tool.setToolGroup(this);
    	tools.put(index,tool);
    }
    
    private int generateToolIndex(){
    	for(int i=0;i<tools.size()+1000;i++){
    		if(!tools.containsKey(i)){
    			return i;
    		}
    	}
    	System.out.println("error");
    	return -1;
    }
    
    public void setToolNum(int num){
    	toolNumber=num;
    	//tools.clear();
    	//for(int i=0;i<num;i++){
    	//	Tool tool=new Tool();
	    //	tool.setName(getName()+"_"+(tools.size()+1));
	    //	tool.setIndex(tools.size());
	    //	tool.setToolGroup(this);
	    //	tools.put(tools.size(),tool);  	
    	//}
    }
    
    public int getToolNum(){
    	
    	return toolNumber;
    }
    
    public ProcessType getProcessType(){
    	return processType;
    }
    
    public void setProcessType(ProcessType processType){
    	this.processType=processType;
    }

	@Override
	public String toString() {
		return super.toString();
	}
	
	  private void readObject(ObjectInputStream inputStream)
	            throws IOException, ClassNotFoundException
	    {
	        inputStream.defaultReadObject();
	        tools=new HashMap<Integer,Tool>();
	    } 

	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVers=1l;
}
 
