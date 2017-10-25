package simulation.framework;

import java.util.ArrayList;
import java.util.Arrays;

public class EventList {

	private ArrayList<Activation> activationList;
	private ActivationPriority priority;

	public EventList(ActivationPriority prio) {
		activationList = new ArrayList<Activation>();
		priority = prio;
	}

	public void addActivateEvent(Message msg) {
		Activation a = new Activation(msg);
		if (priority != null)
			a.setPriority(priority.getPriority(a.getTitle()));
		activationList.add(a);
	}

	public void addActivateEvent(Activation act) {
		if (priority != null)
			act.setPriority(priority.getPriority(act.getTitle()));
		activationList.add(act);
	}

	public Activation get(int index) {
		return activationList.get(index);

	}

	private EventList sort() {
		EventList sortedList = new EventList(priority);
		Activation[] activations = activationList.toArray(new Activation[0]);
		Arrays.sort(activations);
		for (int i = 0; i < activations.length; i++)
			sortedList.addActivateEvent(activations[i]);
		return sortedList;
	}

	public EventList copy() {
		EventList result = new EventList(priority);
		for (int i = 0; i < activationList.size(); i++) {
			result.addActivateEvent(activationList.get(i));
		}
		// System.out.println("CAL size:"+activationList.size());

		return result;
	}

	public EventList getEarliestActivations() {
		// System.out.println("e Begin:"+System.currentTimeMillis());
		if (activationList.size() < 1)
			return null;
		EventList result = new EventList(priority);
		Activation min = activationList.get(0);
		result.addActivateEvent(activationList.get(0));
		for (int i = 1; i < activationList.size(); i++) {
			if (activationList.get(i).compareBy(min) == -1) {
				min = activationList.get(i);
				result.clear();
				result.addActivateEvent(min);
			} else if (activationList.get(i).compareBy(min) == 0) {
				result.addActivateEvent(activationList.get(i));
			}
		}
		activationList.removeAll(result.activationList);
		// System.out.println("FAL size:"+activationList.size()+"CAL size:"+result.size());

		return result.sort();
		// return result;
	}

	public EventList getEarliestActivations(long earliestTime) {
		// System.out.println("e Begin:"+System.currentTimeMillis());
		if (activationList.size() < 1)
			return null;
		EventList result = new EventList(priority);
		for (int i = 1; i < activationList.size(); i++) {
			if (activationList.get(i).getActivateTime() == earliestTime) {
				result.addActivateEvent(activationList.get(i));
				activationList.remove(i);
				i = i - 1;
			}
		}
		// activationList.removeAll(result.activationList);
		// System.out.println("FAL size:"+activationList.size()+"CAL size:"+result.size());

		return result.sort();
		// return result;
	}

	public int size() {
		return activationList.size();
	}

	public void clear() {
		activationList.clear();
	}

	public void print() {
		for (int i = 0; i < activationList.size(); i++) {
			System.out.print("  " + i + "  ");
			activationList.get(i).print();
		}
	}

	public void remove(int index) {
		activationList.remove(index);
	}

	public int active(Simulation simulator) {
		// System.out.println("aaaaa");
		if (activationList.size() < 1)
			return 0;
		//System.out.println("Current event is "+activationList.get(0).getString());
		activationList.get(0).active(simulator);	
		simulator.setLastActivation(activationList.get(0));
		activationList.remove(0);
		return 1;
	}
	
	@Override
	public EventList clone(){
		EventList list=new EventList(priority);
		for(Activation act:activationList){
			list.addActivateEvent(act.clone());
		}
		
		return list;
	}

}
