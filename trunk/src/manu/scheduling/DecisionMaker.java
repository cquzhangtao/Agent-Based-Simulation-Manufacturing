package manu.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import manu.scheduling.training.BPField;
import manu.scheduling.training.BPInfo;
import manu.scheduling.training.BPNetwork;
import manu.scheduling.training.ClusterInfo;
import manu.scheduling.training.Kmeans;
import manu.scheduling.training.KmeansField;
import manu.scheduling.training.Normalizer;
import manu.scheduling.training.data.TrainingDataset;

public class DecisionMaker<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String groupName;
	private Kmeans<T> kmeans;
	private T[] minMax;
	private List<BPNetwork<T>> bpNetworks = new ArrayList<BPNetwork<T>>();
	private TrainingDataset<T> dataset=new TrainingDataset<T>("");
	
	public DecisionMaker(String groupName) {
		this.groupName = groupName;
	}
	
	public void randomInit(T t,BPInfo bpInfo,ClusterInfo clusterInfo){
		kmeans=new Kmeans<T> ();
		kmeans.randomInit( t,clusterInfo);		
		for(int i=0;i<clusterInfo.getClassNum();i++){
			BPNetwork<T> network = new BPNetwork<T>();
			network.randomInit(t, bpInfo);
			bpNetworks.add(network);
		}
		
	}
	
	public void addBPNetwork(BPNetwork<T> network){
		bpNetworks.add(network);
	}

	public TrainingDataset<T> getDataset() {
		return dataset;
	}

	public void setDataset(TrainingDataset<T> dataset) {
		this.dataset = dataset;
	}

	public List<BPNetwork<T>> getBpNetworks() {
		return bpNetworks;
	}
	
	public void clear(){
		kmeans.clear();
		for(BPNetwork<T> bp:bpNetworks){
			bp.clear();
		}
	}

//	public JobInfo getBestJob(ArrayList<JobInfo> jobs, int wip,
//			int[] wipD, int bufferSize, double bufferTime) {
//		TrainingData data = new TrainingData();
//		// data.setWip((wip-minMax[0].getWip())/(minMax[1].getWip()-minMax[0].getWip()));
//		// data.setBuffersize((bufferSize-minMax[0].getBuffersize())/(minMax[1].getBuffersize()-minMax[0].getBuffersize()));
//		// data.setBufferTime((bufferTime-minMax[0].getBufferTime())/(minMax[1].getBufferTime()-minMax[0].getBufferTime()));
//		data.setiWip(wip);
//		data.setBuffersize(bufferSize);
//		data.setBufferTime(bufferTime);
//		// data.normalize(kmeans.getMinMax());
//		Normalizer.normalize(data, kmeans.getMinMax(),
//				new Class[] { KmeansField.class });
//		int classIndex = kmeans.patternRecognition(data);
//		BPNetwork<TrainingData> network = bpNetworks.get(classIndex);
//		double min = Double.MAX_VALUE;
//		int index = 0;
//		int minIndex = 0;
//		for (JobinBuffer job : jobs) {
//			data = new TrainingData();
//			data.setData(job);
//			// data.normalize(network.getMinMax());
//			Normalizer.normalize(data, network.getMinMax(),
//					new Class[] { BPField.class });
//			network.predict(data);
//			if (min > data.getObjective()) {
//				min = data.getObjective();
//				minIndex = index;
//			}
//			index++;
//		}
//		return jobs.get(minIndex);
//		return null;
		// BPNetwork<TrainingData> network=bpNetworks.get(0);
		// double min=Double.MAX_VALUE;
		// int index=0;
		// int minIndex=0;
		// for(JobinBuffer job:jobs){
		// data=new TrainingData();
		// data.setData(job);
		// data.setWip(wip);
		// data.setBuffersize(bufferSize);
		// data.setBufferTime(bufferTime);
		// data.normalize(minMax);
		// network.test(data);
		// if(min>data.getObjective()){
		// min=data.getObjective();
		// minIndex=index;
		// }
		// index++;
		// }
		// return jobs.get(minIndex);

//	}

	@SuppressWarnings("unchecked")
	public int makeDecision(T clusterData, T[] bpData) {
		Normalizer.normalize(clusterData, kmeans.getMinMax(),
				new Class[] { KmeansField.class });
		int classIndex = kmeans.patternRecognition(clusterData);
		BPNetwork<T> network = bpNetworks.get(classIndex);
//		if(network.getAccuracy()<0.8){
//			return -1;
//		}
		
		double min = Double.MAX_VALUE;
		int index = 0;
		int minIndex = 0;
		for (T data : bpData) {
			
			Normalizer.normalize(data, network.getMinMax(),
					new Class[] { BPField.class });
			double r[]=network.predict(data);
			if (min > r[0]) {
				min = r[0];
				minIndex = index;
			}
			index++;
//			Normalizer.unNormalize(data, network.getMinMax(),
//					new Class[] { BPField.class });
//			dataset.add(data);
		}
		return minIndex;
	}

	public void setBpNetworks(List<BPNetwork<T>> bpNetworks) {
		this.bpNetworks = bpNetworks;
	}



	public Kmeans<T> getKmeans() {
		return kmeans;
	}

	public void setKmeans(Kmeans<T> kmeans) {
		this.kmeans = kmeans;
	}

	public void addNetwork(BPNetwork<T> net) {
		bpNetworks.add(net);
	}

	public T[] getMinMax() {
		return minMax;
	}

	public void setMinMax(T[] minMax) {
		this.minMax = minMax;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String toolGroupName) {
		this.groupName = toolGroupName;
	}

}
