package simulation.framework;

import java.io.Serializable;
import java.util.LinkedList;

public class Queue implements Serializable{

	private LinkedList<Message> linkedList = new LinkedList<Message>();
	private byte[] lock = new byte[0];
	private String name;
	public Queue(String name){
		this.setName(name);
	}
	public void offer(Message obj) {
		linkedList.addFirst(obj);
		// notifyAll();
	}

	public Message poll() {
		return linkedList.removeLast();
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return linkedList.isEmpty();
	}

	public void clear() {
		linkedList.clear();
	}

	public Message get(int index) {
		return linkedList.get(index);
	}

	public int size() {
		return linkedList.size();
	}

	public void remove(int index) {
		linkedList.remove(index);
	}
	@Override
	public Queue clone(){
		Queue q=new Queue(getName());
		q.setLock(getLock().clone());
		for(Message m:linkedList){
			q.offer(m.clone());
		}
		
		return q;
	}
	public byte[] getLock() {
		return lock;
	}
	public void setLock(byte[] lock) {
		this.lock = lock;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
