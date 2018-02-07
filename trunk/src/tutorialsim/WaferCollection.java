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
 * WaferCollection
 */	
public abstract class WaferCollection extends Job implements Serializable {
	
	private List<Wafer> wafers=new ArrayList<Wafer>();
	
    /**
     * Default constructor
     */
    public WaferCollection() {
    }
    
  
    public List<Wafer> getWafers(){
    	return wafers;
    }
    
    public void addWafer(Wafer wafer){
    	wafer.setCurrentContainer(this);
    	wafers.add(wafer);
    }
    
    public void addWafer(int num){
    	for(int i=0;i<num;i++){
    		Wafer wafer=new Wafer();   		
    		wafer.setName("Wafer_"+(i+1)+"_"+this.getName());
    		wafer.setCurrentContainer(this);
    		wafers.add(wafer);
    	}
    }
    
    public Wafer getWafer(int index){
    	return wafers.get(index);
    }
    
    public int getSize(){
    	return wafers.size();
    }
    
    public void setAssignedTool(Tool assignedTool){
    	super.setAssignedTool(assignedTool);
    	for(Wafer wafer:wafers){
    		wafer.setAssignedTool(assignedTool);
    	}
    	
    }
    public void setStartTime(double time){
		super.setStartTime(time);
		for(Wafer wafer:wafers){
    		wafer.setStartTime(time);
    	}
	}

	public void setEnterTime(double time){
		super.setEnterTime(time);
		for(Wafer wafer:wafers){
    		wafer.setEnterTime(time);
    	}
	}
	
	public double getAvgFrontWaitingTimeofWafers(){
		double sum=0;
		for(Wafer wafer:wafers){
    		sum+=wafer.getFrontWaitingTime();
    	}
		return sum/wafers.size();
	}
	
	public double getAvgEndWaitingTimeofWafers(){
		double sum=0;
		for(Wafer wafer:wafers){
    		sum+=wafer.getEndWaitingTime();
    	}
		return sum/wafers.size();
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
