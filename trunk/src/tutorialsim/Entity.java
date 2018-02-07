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
 * Entity
 */	
import java.io.ObjectInputStream;
import java.io.IOException;
public class Entity implements Serializable {
	
	private String id;
	private String name;
	private int index;
	//private transient Agent agent;
	
	

    /**
     * Default constructor
     */
    public Entity() {
    }
    
   public void close(){
    	//agent=null;
    }
    
    public void setId(String id){
    	this.id=id;
    }
    
    public String getId(){
    	return id;
    }
    
    public int getIndex(){
    	return index;
    }
    
    public void setIndex(int index){
    	this.index=index;
    }
    
   // public void setAgent(Agent agent){
   // 	this.agent=agent;
   // }
    
   // public Agent getAgent(){
   // 	return agent;
   // }
    
    public void setName(String name){
    	this.name=name;
    }
    
    public String getName(){
    	return name;
    }

	@Override
	public String toString() {
		return name;
	}
	

    private void readObject(ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException
    {
        inputStream.defaultReadObject();
       // System.out.println(name+"is readed");
    }    


	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;

} 
