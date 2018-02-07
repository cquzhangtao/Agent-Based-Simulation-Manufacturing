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
 * Lot
 */	
public class Lot extends WaferCollection implements IJob,Serializable {
	
	private Product product;
	private double releaseTime;
	private double completeTime;
	
	private int finishedWaferNumAtCurStep=0;
	private int startedWaferNumAtCurStep=0;
	
	private int wafersArriveForBatching=0;
	
	
	
	
	public void oneWaferArriveForBatching(){
		wafersArriveForBatching++;
	}
	
	public boolean isAllWaferArriveForBatching(){
		return wafersArriveForBatching==product.getLotSize();
	}
	
	public double getCTOverRPT(){
		//System.out.println(getCycleTime()+","+product.getRawProcessingTime()+","+product.getTravelTime());
		return getCycleTime()/(product.getRawProcessingTime()+product.getTravelTime());
	}

    /**
     * Default constructor
     */
    public Lot(Product product) {
    	this.product=product;
    	//addWafers(product.getLotSize());
    }
    
    public Product getProduct(){
    	return product;
    }
    
    public double getTardiness(){
    	double lateness=getLateness();
    	return lateness<0?0:lateness;
    }
    
    public double getLateness(){
    	return getCycleTime()-(product.getRawProcessingTime()+product.getTravelTime())*product.getCTOverRPT();
    }
    
    public ProcessType getJobType(){
    	return ProcessType.Lot;
    }
    
    public int getInitialSize(){
    	
    	return product.getLotSize();
    }
   
    
    public double getCompleteTime(){
    	return completeTime;
    }
    
    public void setCompleteTime(double time){
    	completeTime=time;
    }
    
 
    
    public double getReleaseTime(){
    	return releaseTime;
    }
    
    public void setReleaseTime(double time){
    	this.releaseTime=time;
    }
    
    public double getCycleTime(){
    	return completeTime-releaseTime;
    }
    
    public void addWafers(int num){
    	
    	super.addWafer(num);
    	for(Wafer wafer:getWafers()){
    		wafer.setProduct(product);
    		wafer.setLot(this);
    		//WaferAgent agent=new WaferAgent();
    		//agent.setWafer(wafer);
    		//agent.create();
    		//agent.start();
    		//agent.init();
    		//
    		//
    		//wafer.setName("Wafer_"+wafer.getIndex()+"_"+this.getName());
    	}
    	
    }
    
    public boolean isFinished(){
    	return this.getWafers().get(0).getCurrentStepIndex()==product.getRoute().getSteps().size();
    }
    
    public Step getCurrentStep(){
    	
    	return this.getWafers().get(0).getCurrentStep();
    }
    public int getCurrentStepIndex(){
    	
    	return this.getWafers().get(0).getCurrentStepIndex();
    }
    
    public Step getPreviousStep(){
    	return this.getWafers().get(0).getPreviousStep();
    }
    
    public Step getNextStep(){
    	return this.getWafers().get(0).getNextStep();
    }
    
    public void moveToNextStep(){
    	finishedWaferNumAtCurStep=0;
    	startedWaferNumAtCurStep=0;
    	wafersArriveForBatching=0;
    	for(Wafer wafer:getWafers()){
    		wafer.nextStep();
    	}
    	
    }
    
    public void finishedOneWaferAtCurStep(){
    	finishedWaferNumAtCurStep++;
    }
    
    public void finishedAllWaferAtCurStep(){
    	finishedWaferNumAtCurStep=product.getLotSize();
    }
    
    public boolean isAllWafersFinishedAtCurStep(){
    	return finishedWaferNumAtCurStep==getWafers().size();
    }
    
    public void startedOneWaferAtCurStep(){
    	startedWaferNumAtCurStep++;
    }
    
    public void startedAllWaferAtCurStep(){
    	startedWaferNumAtCurStep=product.getLotSize();
    }
    
    public boolean isAllWafersStartededAtCurStep(){
    	return startedWaferNumAtCurStep==getWafers().size();
    }
    
    public int getStartedWaferNumAtCurStep(){
    	return startedWaferNumAtCurStep;
    }
    
    public int getFinishedWaferNumAtCurStep(){
    	return finishedWaferNumAtCurStep;
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
