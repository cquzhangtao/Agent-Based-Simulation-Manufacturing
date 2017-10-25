package manu.utli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SortMap<K, V extends Comparable<V> >  {
	
	List<K> sortedKeys;
	List<V> sortedValues;
	public SortMap(){
		sortedKeys=new ArrayList<K>();
		sortedValues=new ArrayList<V>();
	}
	@SuppressWarnings("unchecked")
	public  void sort(Map<K,V > map){
		if(!(map.values().toArray()[0] instanceof Comparable) ){
			return ;
		}
		List<KeyValue<K,V>> temp=new ArrayList<KeyValue<K,V>>();
		for(K k:map.keySet()){
			temp.add(new KeyValue<K,V>(k,map.get(k)));
		}
		Object[] temp1=new Object[temp.size()];
		temp1=temp.toArray();
		Arrays.sort(temp1);
		for(Object o:temp1){
			sortedKeys.add(((KeyValue<K,V>) o).getKey());
			sortedValues.add(((KeyValue<K,V>) o).getValue());
		}
		
		
	}
	public List<K> getSortedKeys() {
		return sortedKeys;
	}
	public List<V> getSortedValues() {
		return sortedValues;
	}
	public void print(){
		int index=0;
		for(K k:sortedKeys){
			System.out.println(k.toString()+" , "+sortedValues.get(index).toString());
			index++;			
		}
	}


	
	
	}

