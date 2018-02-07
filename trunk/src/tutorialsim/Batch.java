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
 * Batch
 */	
public class Batch extends WaferCollection implements IJob,Serializable {
	
	private BatchingConfiguration batchConfig;
	private static long ID=1;
	
    /**
     * Default constructor
     */
    public Batch() {
    	setId(String.valueOf(ID++));
    	setName("Batch"+getId());
    }
    
    public void setBatchConfig(BatchingConfiguration batchConfig){
    	
    	this.batchConfig=batchConfig;
    }
    public BatchingConfiguration getBatchingConfiguration(){
    	return batchConfig;
    }
 
    public boolean canPutWaferIn(Wafer wafer){
    	return wafer.getCurrentStep().getBatchConfig()==batchConfig
    			&&getWafers().size()<batchConfig.getMaxSize();
    }
    
    public boolean isBatchReady(){
    	//System.out.println(getWafers().size()+batchConfig.toString());
    	return getWafers().size()>=batchConfig.getMinSize();
    }
    
    public boolean isFull(){
    	return  getWafers().size()>=batchConfig.getMaxSize();
    }
    
    public boolean isTimeout(double time){
    	for(Wafer wafer: getWafers()){
    		if(time-wafer.getEnterTime()>batchConfig.getMaximalBatchingTime()){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean canProcessOnOneOfTools(List<Tool> tools){
    	Set<Integer> dtools=this.getWafer(0).getCurrentStep().getDedicatedTools();
    	for(Tool tool:tools){
    		for(int idx:dtools){
    			if(tool.getIndex()==idx){
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public Step getCurrentStep(){
    	return getWafers().get(0).getCurrentStep();
    }
    
    public int getCurrentStepIndex(){
    	return -1;//getWafers().get(0).getCurrentStep().getIndex();
    }
    
    public Product getProduct(){
    	return null;
    }
    
    public ProcessType getJobType(){
    	return ProcessType.Batch;
    }
    
    public Map<Lot,Integer> getLots(){
    	Map<Lot,Integer> lots=new HashMap<Lot,Integer>();
		for(Wafer wafer:getWafers()){
			if(!lots.containsKey(wafer.getLot())){
				lots.put(wafer.getLot(),1);
			}else{
				lots.put(wafer.getLot(),lots.get(wafer.getLot())+1);
			}
		}
		return lots;
    }

	@Override
	public String toString() {
		Map<Lot,Integer> lots=new HashMap<Lot,Integer>();
		for(Wafer wafer:getWafers()){
			if(!lots.containsKey(wafer.getLot())){
				lots.put(wafer.getLot(),1);
			}else{
				lots.put(wafer.getLot(),lots.get(wafer.getLot())+1);
			}
		}
		
		String str="Id:"+this.getId()+"\r\nSize:"+this.getSize()+"\r\n";
		
		for(Lot lot:lots.keySet()){
			str+=lots.get(lot)+" wafers from Lot "+lot.getId()+"\r\n";
		}
		return str;
	}

	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;

} 
