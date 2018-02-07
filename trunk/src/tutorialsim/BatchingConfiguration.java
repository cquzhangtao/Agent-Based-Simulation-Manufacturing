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
 * BatchingConfiguration
 */	
public class BatchingConfiguration extends Entity implements Serializable {
	
	private int minSize=12;
	private int maxSize=96;
	private Set<Step> suitableSteps=new HashSet<Step>();
	private BatchingPolicy batchingPolicy=BatchingPolicy.NEARFULL;
	private double maximalBatchingTime=100;
    private boolean enableMaxBatchingTime=true;
	/**
     * Default constructor
     */
    public BatchingConfiguration() {
    	
    }
    
    public boolean isEnableMaxBatchingTime(){
    	return enableMaxBatchingTime;
    }
    
    public void setEnableMaxBatchingTime(boolean enable){
    	enableMaxBatchingTime=enable;
    }
    
    public BatchingPolicy getBatchingPolicy(){
    	return batchingPolicy;
    }
    
    public void setBatchingPolicy(BatchingPolicy batchingPolicy){
    	this.batchingPolicy=batchingPolicy;
    }
    
    public double getMaximalBatchingTime(){
    	return maximalBatchingTime;
    }
    
    public void setMaximalBatchingTime(double maximalBatchingTime){
    	this.maximalBatchingTime=maximalBatchingTime;
    }
    
    public int getMinSize(){
    	return minSize;
    }
    
    public int getMaxSize(){
    	return maxSize;
    }
    
    public void setMinSize(int minSize){
    	this.minSize=minSize;
    }
    
    public void setMaxSize(int maxSize){
    	this.maxSize=maxSize;
    }
    
    public Set<Step> getSuitableSteps(){
    	return suitableSteps;
    }
    
    public void addSuitableSteps(Step step){
    	suitableSteps.add(step);
    }

	@Override
	public String toString() {
		return "Min:"+minSize+", Max:"+maxSize;
	}

	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;

} 
