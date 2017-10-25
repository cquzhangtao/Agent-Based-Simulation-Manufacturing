package manu.simulation.result.data;

import java.io.Serializable;
import java.util.ArrayList;

public class BlockDataset implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9111298664216960155L;
	public String bufferName;
	public double maxBlockTime = 0;
	public double avgBlockTime = 0;
	public int interval = 24 * 3600;
	ArrayList<BlockData> blockDataset = new ArrayList<BlockData>();
	public ArrayList<Double> blockDatasetbyDay = new ArrayList<Double>();

	private int blockDatasetSize;

	public BlockDataset(int period) {
		// TODO Auto-generated constructor stub
		interval = period;
	}

	public void save() {
		blockDatasetSize = blockDataset.size();
	}

	public void recover() {
		while (blockDataset.size() > blockDatasetSize)
			blockDataset.remove(blockDatasetSize);
	}

	public void add(long endBlockTime, long blockTime) {
		blockDataset.add(new BlockData(endBlockTime, blockTime));
	}

	public void reset() {
		maxBlockTime = 0;
		avgBlockTime = 0;
		blockDataset.clear();
		blockDatasetbyDay.clear();
	}

	public void stat() {
		int count = 0;
		int time = interval;
		long sum = 0;
		for (int i = 0; i < blockDataset.size(); i++) {
			if (blockDataset.get(i).endBlockTime < time) {
				count++;
				sum += blockDataset.get(i).blockTime;
			} else {
				if (count == 0)
					blockDatasetbyDay.add(0.0);
				else {
					blockDatasetbyDay.add(1.0 * sum / count / 3600);
					count = 0;
				}
				sum = 0;

				time += interval;
				i = i - 1;
			}
		}
		sum = 0;
		for (int i = 0; i < blockDataset.size(); i++) {
			if (blockDataset.get(i).blockTime > maxBlockTime) {
				maxBlockTime = blockDataset.get(i).blockTime;
			}
			sum += blockDataset.get(i).blockTime;
		}
		if (blockDataset.size() > 0)
			avgBlockTime = 1.0 * sum / blockDataset.size() / 3600;
		else
			avgBlockTime = 0;
		maxBlockTime = 1.0 * maxBlockTime / 3600;
		blockDataset.clear();
		// blockDataset=null;

	}

	public int size() {
		// TODO Auto-generated method stub
		return blockDatasetbyDay.size();
	}

}
