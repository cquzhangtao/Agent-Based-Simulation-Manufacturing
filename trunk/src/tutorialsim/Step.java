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
 * Step
 */	
public class Step extends Entity  implements Serializable {
	
	private ToolGroup toolGroup;
	private double processingTime;
	private String setupState;
	private BatchingConfiguration batchConfig=new BatchingConfiguration();
	private Set<Integer> dedicatedTools=new HashSet<Integer>();
	private double loadingTime=1;
	private double unloadingTime=1;
	private double enterQueueTime=1;
	private double exitQueueTime=1;
    /**
     * Default constructor
     */
    public Step() {
    	
    }
    
	public double getLoadingTime() {
		return loadingTime;
	}
	public void setLoadingTime(double loadingTime) {
		this.loadingTime = loadingTime;
	}
	
	public double getEnterQueueTime(){
		return enterQueueTime;
	}
	
	public double getExitQueueTime(){
		return enterQueueTime;
	}
	
	public double getUnloadingTime() {
		return unloadingTime;
	}
	public void setUnloadingTime(double loadingTime) {
		this.unloadingTime = loadingTime;
	}
    
    
    public ToolGroup getToolGroup(){
    	return toolGroup;
    }
    
    public void setToolGroup(ToolGroup toolGroup){
    	for(int i=0;i<toolGroup.getToolNum();i++){
    		dedicatedTools.add(i);
    	}
    	this.toolGroup=toolGroup;
    	batchConfig=toolGroup.getBatchingConfigs().get(0);
    }
    
    public void setSetupState(String state){
    	this.setupState=state;
    }
    
    public String getSetupState(){
    	return setupState;
    }
    
    public void addDedicatedTools(int... indice){
    	dedicatedTools.clear();
    	for(int index:indice){
    		dedicatedTools.add(index);
    	}
    }
    
    public Set<Integer> getDedicatedTools(){
    	return dedicatedTools;
    }
    
    public BatchingConfiguration getBatchConfig(){
    	return batchConfig;
    }
    
    
    public void setBatchConfig(BatchingConfiguration batchConfig){
    	this.batchConfig=batchConfig;
    }
    
    public double getProcessingTime(){
    	return processingTime;
    }
    
    public void setProcessingTime(double time){
    	//loadingTime=time*0.02;
    	//unloadingTime=time*0.02;
    	//enterQueueTime=time*0.02;
    	//exitQueueTime=time*0.02;
    	processingTime=time;
    }
    
    public double getStepTime(){
    	return loadingTime+unloadingTime+enterQueueTime+exitQueueTime+processingTime;
    }
    
    public double getPrepareTime(){
    	return enterQueueTime+exitQueueTime;
    }
    
    public double getWorkTime(){
    	return loadingTime+unloadingTime+processingTime;
    }
    
    public ProcessType getProcessType(){
    	return toolGroup.getProcessType();
    }

	@Override
	public String toString() {
		return toolGroup.getName();
	}

	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;

} 
