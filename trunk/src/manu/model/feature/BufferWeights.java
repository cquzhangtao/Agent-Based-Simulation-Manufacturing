package manu.model.feature;

import java.io.Serializable;
import java.util.ArrayList;

public class BufferWeights implements Serializable{
	ArrayList<BufferWeight> bufferWeights = new ArrayList<BufferWeight>();
	ArrayList<String> toolGroupNames = new ArrayList<String>();

	public BufferWeights() {

	}

	public void addBufferWeight(String toolGroupName, boolean isBatchTool) {
		if (toolGroupNames.contains(toolGroupName))
			return;
		BufferWeight bufferWeight = new BufferWeight(toolGroupName);
		bufferWeight.isBatchTool = isBatchTool;
		bufferWeights.add(bufferWeight);
		toolGroupNames.add(toolGroupName);
	}

	public void updateBufferWeight(String toolGroupName, String content) {
		int index = toolGroupNames.indexOf(toolGroupName);
		if (index == -1) {
			System.out.println("model error in updating buffer weight");
			return;
		}
		String str[] = content.split("---");
		String flagstr = "";
		if (bufferWeights.get(index).isBatchTool)
			flagstr = "Batch";
		else
			flagstr = "Single";

		if (!str[0].equals(flagstr)) {
			System.out.println("model error in updating buffer weight");
			return;
		}
		if (bufferWeights.get(index).isBatchTool) {
			bufferWeights.get(index).clearBatchWeights();
			if (str[1].isEmpty())
				return;
			bufferWeights.get(index).bufferWeight = Double.valueOf(str[2]);
			String batchWeights[] = str[1].split("--");
			for (int i = 0; i < batchWeights.length; i++) {
				bufferWeights.get(index).addBatchWeights(batchWeights[i]);
			}

		} else {
			bufferWeights.get(index).bufferWeight = Double.valueOf(str[1]);

		}
	}

	public double getUrgentWeight(String toolGroupName) {

		if (toolGroupName.equals("Last"))
			return 0;
		int index = toolGroupNames.indexOf(toolGroupName);
		return bufferWeights.get(index).bufferWeight;
	}

	public double getUrgentWeight(String toolGroupName, int batchID) {
		if (toolGroupName.equals("Last"))
			return 0.61;
		int index = toolGroupNames.indexOf(toolGroupName);
		return bufferWeights.get(index).getBatchWeight(batchID);
	}

	public String getUrgentWeightString() {
		String s = "";
		for (int i = 0; i < bufferWeights.size(); i++) {
			BufferWeight bw = bufferWeights.get(i);
			s += bw.toolGroupName + ",";
			for (int j = 0; j < bw.batchWeights.size(); j++) {
				s += bw.batchWeights.get(j).batchID + " "
						+ bw.batchWeights.get(j).weight + ",";
			}
			s += "; ";
		}
		return s;
	}

}

class BufferWeight {
	public boolean isBatchTool;
	public String toolGroupName;
	public double bufferWeight;
	public ArrayList<BatchWeight> batchWeights = new ArrayList<BatchWeight>();
	public BatchWeight maxBatchWeight = new BatchWeight();

	public BufferWeight(String toolGroupName) {
		this.toolGroupName = toolGroupName;
		maxBatchWeight.batchID = 0;
		maxBatchWeight.weight = 0;
	}

	public BufferWeight() {
	}

	public void addBatchWeights(String batchw) {
		String temp[] = batchw.split("-");
		BatchWeight batchWeight = new BatchWeight();
		batchWeight.batchID = Integer.valueOf(temp[0]);
		batchWeight.weight = Double.valueOf(temp[1]);
		batchWeights.add(batchWeight);
		if (batchWeight.weight > maxBatchWeight.weight) {
			maxBatchWeight.batchID = batchWeight.batchID;
			maxBatchWeight.weight = batchWeight.weight;
		}
	}

	public void clearBatchWeights() {
		batchWeights.clear();
		maxBatchWeight.batchID = 0;
		maxBatchWeight.weight = 0;
	}

	public int getMaxBatchID() {
		return maxBatchWeight.batchID;
	}

	public double getMaxBatchWeight() {
		return maxBatchWeight.weight;
	}

	public double getBatchWeight(int batchID) {
		for (int i = 0; i < batchWeights.size(); i++) {
			if (batchWeights.get(i).batchID == batchID)
				return batchWeights.get(i).weight;
		}
		return 0;
	}
}

class BatchWeight {
	int batchID;
	double weight;
}
