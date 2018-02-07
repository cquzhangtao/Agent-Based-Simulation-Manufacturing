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
 * Wafer
 */	
public class Wafer extends Job implements IJob,Serializable {
	
	private Product product;
	private WaferCollection preOwner;
	private WaferCollection curOwner;
	
	private Lot lot;
	private int currentStepIndex=0;
	
    /**
     * Default constructor
     */
    public Wafer() {
    }
  
    public List<Wafer> getWafers(){
    	return null;
    }
    
    public ProcessType getJobType(){
    	return ProcessType.Wafer;
    }
  
    public Lot getLot(){
    	
    	return lot;
    }
    
    public void setLot(Lot lot){
    	this.lot=lot;
    }
    
    public Product getProduct(){
    	
    	return product;
    }
    
    public void setCurrentContainer(WaferCollection c){
    	this.curOwner=c;
    }
    
    public void setPreContainer(WaferCollection c){
    	this.preOwner=c;
    }
    
    public WaferCollection getCurrentContainer(){
    	return curOwner;
    }
    
    public WaferCollection getPreContainer(){
    	return preOwner;
    }
    
    public void setProduct(Product product){
    	this.product=product;
    }
    
    public int getCurrentStepIndex(){
    	return currentStepIndex;
    }
    
    public void setCurrentStepIndex(int index){
    	currentStepIndex=index;
    }
    
    public Step getCurrentStep(){
    	if(currentStepIndex==product.getRoute().getSteps().size()){
    		return product.getRoute().getSteps().get(currentStepIndex-1);
    	}
    	return product.getRoute().getSteps().get(currentStepIndex);
    }
    
    public Step getPreviousStep(){
    	if(currentStepIndex>0){
    		return product.getRoute().getSteps().get(currentStepIndex-1);
    	}else{
    		return null;
    	}
    }
    public Step getNextStep(){
    	if(currentStepIndex<product.getRoute().getSteps().size()-1){
    		return product.getRoute().getSteps().get(currentStepIndex+1);
    	}else{
    		return null;
    	}
    }
    
    
    
    public boolean nextStep(){
    	currentStepIndex++;
    	if(currentStepIndex>product.getRoute().getSteps().size()){
    		return false;
    	}
    	return true;
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
