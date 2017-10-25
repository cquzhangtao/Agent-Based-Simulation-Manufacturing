package manu.scheduling.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import manu.scheduling.DecisionMaker;
import manu.scheduling.training.data.TrainingDataset;
import manu.scheduling.training.data.TrainingDatasetMap;
import manu.scheduling.training.gui.ProgressFrame;
import manu.simulation.others.FileRead;

public class Training<T> {
	TrainingDatasetMap<T> trainingDatasetMap;
	BPInfo bpInfo = new BPInfo();
	ClusterInfo clusterInfo = new ClusterInfo();

	public Training(TrainingDatasetMap<T> trainingDatasetMap) {
		this.trainingDatasetMap = trainingDatasetMap;
		FileRead.trainingInfo(clusterInfo, bpInfo);
	}

	public Training() {
		FileRead.trainingInfo(clusterInfo, bpInfo);
	}

	public Map<String, DecisionMaker<T>> train() {
		return train(null);
	}

	public DecisionMaker<T> oneGroupTrain(TrainingDataset<T> trainingDataset) {
		trainingDatasetMap = new TrainingDatasetMap<T>();
		trainingDatasetMap.putDataset(trainingDataset.getGroupName(), trainingDataset);
		return (DecisionMaker<T>) train(null).values().toArray()[0];
	}

	@SuppressWarnings("unchecked")
	public Map<String, DecisionMaker<T>> train(ProgressFrame pf) {
		final Map<String, DecisionMaker<T>> dispatchers = new HashMap<String, DecisionMaker<T>>();
		int classNum = clusterInfo.getClassNum();
		int temp = 0;
		float interval = (float) (90.0 / trainingDatasetMap.size());
		for (TrainingDataset<T> dataset : trainingDatasetMap.values()) {
			DecisionMaker<T> dispatcher = new DecisionMaker<T>(dataset.getGroupName());
			ArrayList<T> normalizedData = new ArrayList<T>();
			T[] minMax = null;
			try {
				minMax = Normalizer.<T> normalize(dataset.getTrainingDataset(), normalizedData, new Class[] { KmeansField.class });
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (pf != null)
				pf.setProgess((int) (temp * interval + interval / (classNum + 5) * 1));
			dispatcher.setMinMax(minMax);
			Kmeans<T> kmeans = new Kmeans<T>(normalizedData, clusterInfo);
			kmeans.setMinMax(minMax);
			kmeans.setUnNormalizedData(dataset.getTrainingDataset());
			ArrayList<T>[] results = kmeans.comput();
			dispatcher.setKmeans(kmeans);
			if (pf != null)
				pf.setProgess((int) (temp * interval + interval / (classNum + 5) * 6));

			int temp1 = 0;
			double sum = 0;
			double sum1 = 0;
			for (ArrayList<T> bpData : results) {
				if (bpData.size() > 0) {
					ArrayList<T> normalizedDataBP = new ArrayList<T>();
					try {
						T[] minMaxBP = Normalizer.<T> normalize(bpData, normalizedDataBP, new Class[] { BPField.class });
						BPNetwork<T> bpNetwork = new BPNetwork<T>(normalizedDataBP, bpInfo);
						if (bpNetwork.getInputData().length > 0) {
							bpNetwork.setMinMax(minMaxBP);
							bpNetwork.train();
							dispatcher.addNetwork(bpNetwork);
							sum += bpNetwork.getAccuracy() * bpNetwork.getInputNum();
							sum1 += bpNetwork.getInputNum();
							if (pf != null)
								pf.setProgess((int) (temp * interval + interval / (classNum + 5) * (6 + temp1)));
							temp1++;
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
			System.out.println("Avg accuracy:" + sum / sum1);

			dispatchers.put(dataset.getGroupName(), dispatcher);
			temp++;

		}

		return dispatchers;

	}

}
