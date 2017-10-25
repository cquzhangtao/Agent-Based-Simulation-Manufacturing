package manu.scheduling.training.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TrainingDatasetMap<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String,TrainingDataset<T>> trainingDatasetMap=new HashMap<String,TrainingDataset<T>>();
	
	public TrainingDataset<T> getDataset(String groupName){
		return trainingDatasetMap.get(groupName);
	}
	public void putDataset(String groupName,TrainingDataset<T> dataset){
		trainingDatasetMap.put(groupName, dataset);
	}
	public ArrayList<String[]> getDataNumbers(){
		ArrayList<String[]> names=new ArrayList<String[]>();
		for(TrainingDataset<T> dataset:trainingDatasetMap.values()){
			names.add(new String[]{dataset.getGroupName(),String.valueOf(dataset.size())});
		}
		return names;
	}
	
	public void saveToXls(){
		for(TrainingDataset<T> dataset:trainingDatasetMap.values()){
			dataset.saveToXls();
		}
	}
	public int size(){
		return trainingDatasetMap.size();
	}
	
	public Collection<TrainingDataset<T>> values(){
		return (Collection<TrainingDataset<T>>) trainingDatasetMap.values();
	}
	public void combine(TrainingDatasetMap<T> map) {
		// TODO Auto-generated method stub
		
		for(TrainingDataset<T> dataset:trainingDatasetMap.values()){
			dataset.add(map.getDataset(dataset.getGroupName()));
		}
	}
	public Map<String, TrainingDataset<T>> getTrainingDatasetMap() {
		return trainingDatasetMap;
	}
	public void setTrainingDatasetMap(
			Map<String, TrainingDataset<T>> trainingDatasetMap) {
		this.trainingDatasetMap = trainingDatasetMap;
	}
}
