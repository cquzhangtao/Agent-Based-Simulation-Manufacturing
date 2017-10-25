package manu.scheduling.training.data;

import java.util.ArrayList;

public class TrainingDatasetRaw<G,A> extends ArrayList<DataMeta<A>>{
	private G group;

	public G getGroup() {
		return group;
	}

	public void setGroup(G group) {
		this.group = group;
	}
	
	

}
