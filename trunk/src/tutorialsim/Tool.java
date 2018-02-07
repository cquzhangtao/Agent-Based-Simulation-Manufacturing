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
 * Tool
 */	
public class Tool extends Entity  implements Serializable {
	
	private ToolState state;
	private transient IJob currentJob;
	private ToolGroup toolGroup;
	private Step preProcessedStep;

	
	private double sumOfWorkTime=0;
	private double sumOfIdleTime=0.1;
	private double sumOfBreakTime=0;
	private double sumOfMaintainTime=0;
	private double sumOfSetupTime=0;
	private double sumOfBlockTime=0;
	private double stateStartTime=0;
    /**
     * Default constructor
     */
    public Tool() {
    	state=ToolState.Idle;
    }
    
    public void close(){
    	currentJob=null;
    	super.close();
    }
    
    public void setPreProcessedStep(Step step){
    	preProcessedStep=step;
    }
    
    public Step getPreProcessedStep(){
    	return preProcessedStep;
    }
    
    public double getSumOfWorkTime(){
    	return sumOfWorkTime;
    }
    
    public double getSumOfIdleTime(){
    	return sumOfIdleTime;
    }
    
    public double getSumOfBreakTime(){
    	return sumOfBreakTime;
    }
    
    public double getSumOfBlockTime(){
    	return sumOfBlockTime;
    }
    
    public double getSumOfMaintainTime(){
    	return sumOfMaintainTime;
    }
    
    public double getSumOfSetupTime(){
    	return sumOfSetupTime;
    }
    
    
    public ToolGroup getToolGroup(){
    	return toolGroup;
    }
    
    public void setToolGroup(ToolGroup toolGroup){
    	this.toolGroup=toolGroup;
    }
    
    public void setCurrentJob(IJob job){
    	currentJob=job;
    }
    
    public IJob getCurrentJob(){
    	return currentJob;
    }
    
    public ToolState getState(){
    	return state;
    }
    

    
    public void reset(){
    	state=ToolState.Idle;
    	sumOfWorkTime=0;
    	sumOfIdleTime=0.1;
    	sumOfBreakTime=0;
    	sumOfMaintainTime=0;
    	sumOfSetupTime=0;
    	sumOfBlockTime=0;
    	stateStartTime=0;
    	
    	
    }
    
    public void setState(ToolState state,double time){
    	switch(this.state){
    	case Processing:
    		sumOfWorkTime+=time-stateStartTime;
    		break;
    	case Loading:
    		sumOfWorkTime+=time-stateStartTime;
    		break;
    	case Unloading:
    		sumOfWorkTime+=time-stateStartTime;
    		break;
    	case Blocking:
    		sumOfBlockTime+=time-stateStartTime;
    		break;
    	case Idle:
    		sumOfIdleTime+=time-stateStartTime;
    		break;
    	case Breakdown:
    		sumOfBreakTime+=time-stateStartTime;
    		break;
    	case Maintenance:
    		sumOfMaintainTime+=time-stateStartTime;
    		break;
    	case Setup:
    		sumOfSetupTime+=time-stateStartTime;
    		//System.out.println("setup"+(time-stateStartTime));
    		break;
    	
    	}
    	stateStartTime=time;
    	this.state=state;
    }
    
    public double getStateStartTime(){
    	return stateStartTime;
    }

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;
}

 
