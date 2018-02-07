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
 * Product
 */	
public class Product extends Entity  implements Serializable {
	private Route route;
	private double interarrivalTime;
	private int lotSize;
	private double cycleTimeOverRawProcessingTime=2.5;
	private double correlationParameter=0;
	private boolean correlatedRelease;
	private int releaseQueueCapacity=10;
	private Random rnd=new Random();
	private double travelTime;


    /**
     * Default constructor
     */
    public Product(String name) {
    	super.setName(name);
    }
    
 
    
    public int getReleaseQueueCapacity(){
    	return releaseQueueCapacity;
    }
    
    public void setReleaseQueueCapacity(int releaseQueueCapacity){
    	this.releaseQueueCapacity=releaseQueueCapacity;
    }
    
    
    public boolean isCorrelatedRelease(){
    	return correlatedRelease;
    }
    
    public void setCorrelatedRelease(boolean value){
    	correlatedRelease=value; 
    }
    
    public void setRoute(Route route){
    	this.route=route;
    }
    
    public Route getRoute(){
    	return route;
    }
    
    public void setCTOverRPT(double value){
    	cycleTimeOverRawProcessingTime=value;
    }
    
    public double getCTOverRPT(){
    	return cycleTimeOverRawProcessingTime;
    }
    
    public void setCorrelationParameter(double parameter){
    	correlationParameter=parameter;
    }
    
    public double getCorrelationParameter(){
    	return correlationParameter;
    }
    
    public double getTravelTime(){
    	return travelTime;
    }
    
    public void setTravelTime(double time){
    	travelTime=time;
    }

    
    public void setInterarrivalTime(double time){
    	interarrivalTime=time;
    }
    
    public double getInterarrivalTime(){
    	return interarrivalTime;
    }
    
    public int getLotSize(){
    	return lotSize;
    }
    
    public void setLotSize(int size){
    	lotSize=size;
    }
    
    private static Map<Product,Color>colors=new HashMap<Product,Color>();
    
    public Color get2DColor(){
    	Color color=colors.get(this);
    	if(color!=null){
    		
    		return color;
    	}
    	if(getIndex()==0){
    		color= Color.blue;
    	}
    	else if(getIndex()==1){
    		color=Color.magenta;
    	}else{
    		color=new Color(rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256));
    	}
    	colors.put(this,color);
    	return color;
    }
    
    public double getRawProcessingTime(){
    	double time=0;
    	for(Step step:route.getSteps()){   
    		time+=	getRawProcessingTimeAtStep(step);
    	}
    	return time;
    }
    
    private double getRawProcessingTimeAtStep(Step step){
    	double time=0;
    	
		if(step.getProcessType()==ProcessType.Lot){
			time+=step.getStepTime();
		}else if(step.getProcessType()==ProcessType.Wafer){
			time+=step.getWorkTime()*this.getLotSize()/step.getToolGroup().getToolNum()+step.getPrepareTime();
		}else{
			double btime;
			if(this.getLotSize()<step.getBatchConfig().getMinSize()){
				btime=step.getStepTime();
			}else if(this.getLotSize()>step.getBatchConfig().getMaxSize()){
				double temp=this.getLotSize()/step.getBatchConfig().getMaxSize();
				int num=(int)temp;
				if(temp-num>0){
					num++;
				}
				btime=step.getWorkTime()*num/step.getToolGroup().getToolNum()+step.getPrepareTime();
			}else{
				btime=step.getWorkTime()*this.getLotSize()/(step.getBatchConfig().getMaxSize()+step.getBatchConfig().getMinSize())/2/step.getToolGroup().getToolNum()+step.getPrepareTime();
		    	
			}
			time+=	btime;
		}
    		
    	
    	return time;
    }
    public double getRawProcessingTimeFromStep(int index){
    	double time=0;
    	for(int i=index;i<route.getSteps().size();i++){
    		Step step=route.getSteps().get(i);
    		time+=	getRawProcessingTimeAtStep(step);
    	}
    	return time;
    }
    public double getRawProcessingTimeFromStep(Step step){
    	
    	return getRawProcessingTimeFromStep(route.getSteps().indexOf(step));
    }
    
    public double getMaxReleaseRate(){
    	Map<ToolGroup,Double> times=new HashMap<ToolGroup,Double>();
    	for(Step step:route.getSteps()){
    		double timePerWafer=0;
    		if(step.getProcessType()==ProcessType.Lot){
    			timePerWafer=step.getProcessingTime()/lotSize;
    		}
    		if(!times.containsKey(step.getToolGroup())){
    			times.put(step.getToolGroup(),timePerWafer);
    		}else{
    			times.put(step.getToolGroup(),times.get(step.getToolGroup())+timePerWafer);
    		}
    	}
    	return 0;
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
