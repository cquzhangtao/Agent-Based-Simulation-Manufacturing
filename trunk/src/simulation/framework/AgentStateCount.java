package simulation.framework;

import java.io.Serializable;

public class AgentStateCount implements Serializable{
	private int busyAgentNum = 0;

	public synchronized void plus() {
		setBusyAgentNum(getBusyAgentNum() + 1);
		// System.out.println(busyAgentNum);
		// notifyAll();
		// System.out.println("notifyAll");

	}
	public synchronized void set(int num) {
		setBusyAgentNum(num);

	}
	public synchronized int get() {
		return getBusyAgentNum();

	}

	public synchronized int plus(int v) {
		setBusyAgentNum(getBusyAgentNum() + v);
		return getBusyAgentNum();
		// System.out.println(busyAgentNum);
		// notifyAll();
		// System.out.println("notifyAll");

	}

	public synchronized int minus(int num, String name) {
		setBusyAgentNum(getBusyAgentNum() - num);
		//System.out.println(name+":"+busyAgentNum);
		return getBusyAgentNum();
		
	}

	public synchronized void reset() {
		setBusyAgentNum(0);
		// System.out.println(busyAgentNum);
	}

	public synchronized boolean ready() {
		if (0 == getBusyAgentNum()) {
			// notifyAll();
			// System.out.println("Simulation advances");
			return true;
		} else
			return false;
	}

	public synchronized void print(String name) {
		System.out.println(name + ":" + getBusyAgentNum());
	}
	public int getBusyAgentNum() {
		return busyAgentNum;
	}
	public void setBusyAgentNum(int busyAgentNum) {
		this.busyAgentNum = busyAgentNum;
	}
	


}
