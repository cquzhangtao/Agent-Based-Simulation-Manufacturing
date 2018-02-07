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
 * Job
 */	
public abstract class Job extends Entity implements IJob,Serializable {

    /**
     * Default constructor
     */
	private Tool assignedTool;
	private JobState state=JobState.Travelling;
	private double enterTime;
	private double exitTime;
	private double startTime;
	private double endTime;
	
    public Job() {
    }
    
    public double getEndTime(){
		return endTime;
	}
	
	public void setEndTime(double time){
			endTime=time;
		}
   
	
	public double getStartTime(){
		return startTime;
	}
	
	public void setStartTime(double time){
			startTime=time;
		}
	
	public void setEnterTime(double time){
		enterTime=time;
	}
	
	public double getFrontWaitingTime(){
		return startTime-enterTime;
	}
	public double getEndWaitingTime(){
		return exitTime-endTime;
	}
	
	public double getEnterTime(){
		return enterTime;
	}
	
	public void setExitTime(double time){
		exitTime=time;
	}
	
	public double getExitTime(){
		return exitTime;
	}
	
	public double getLocalCT(){
		return exitTime-enterTime;
	}
	
	private List<JobStateChangeListener> stateChangeListeners=new ArrayList<JobStateChangeListener>();
	
	public void addStateChangeListener(JobStateChangeListener lis){
	    stateChangeListeners.add(lis);
	}
	
	public List<JobStateChangeListener> getStateChangeListeners(){
		return stateChangeListeners;
	}
	public void setState(JobState state,double time){
    	if(this.state!=state){
    		for(JobStateChangeListener lis:stateChangeListeners){
    			lis.onStateChanged(this.state,state,time);
    		}
    	}
    	this.state=state;
	}
	
	 public JobState getState(){
	    	return state;
	    }
	    
	 
	
   public Tool getAssignedTool(){
    	return assignedTool;
    }
    public void setAssignedTool(Tool assignedTool){
    	this.assignedTool=assignedTool;
    	
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
