package simulation.framework;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Agent extends Thread implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentEnvironment environment;
	private String name;
	private Queue messageQueue = null;
	private ArrayList<Behaviour> behaviourList;
	private long currentTime = 0;
	private int receivedMsgNum = 0;
	private boolean isRunning = true;
	private Message lastReceivedMessage;
	private boolean printMsg = false;
	private String additionalString = "";
	private byte[] lock = new byte[0];
	private boolean pause=false;
	

	protected Agent(String agentName) {
		this.name = agentName;
		behaviourList = new ArrayList<Behaviour>();
		this.setName(name);

	}



	protected Agent(String agentName, AgentEnvironment environment, int t) {
		this.setEnvironment(environment);
		this.name = agentName;
		setMessageQueue(environment.getQueue(name));
		behaviourList = new ArrayList<Behaviour>();
		this.setName(name);

	}
	
	public void simpleClone(Agent agent){
		agent.environment=environment;
		agent.name=name;
		agent.messageQueue=messageQueue ;
		agent.behaviourList=behaviourList;
		agent.currentTime=currentTime;
		agent.receivedMsgNum=receivedMsgNum;
		agent.isRunning=isRunning;
		agent.lastReceivedMessage=lastReceivedMessage;
		agent.printMsg=printMsg ;
		agent.additionalString=additionalString ;
		agent.lock=lock ;
		agent.pause=pause;
	}

	// abstract public void afterBehaviour();

	public int send(Message msg) {
		msg.setSender(name);
		msg.setSendTime(currentTime);
		synchronized (getEnvironment().getMessageQueues()) {
			if (getEnvironment().getQueue(msg.getReceiver()) == null) {
				System.out.println(additionalString
						+ " ,Can not find receiver!" + msg.toString()+",last message:"+lastReceivedMessage.toString());
				System.out.println(getEnvironment().getAgentNames());
				System.exit(0);
				return 0;
			}
		}
		synchronized (getEnvironment().getLock(msg.getReceiver())) {
			getEnvironment().addMessage(msg);
			getEnvironment().notifyReceiver(msg.getReceiver());
			getEnvironment().getCount().plus();
		}
		printMsg(msg, false);

		// System.out.println("     " + name +
		// " "+currentTime()+"    send message--"+ msg.toString());
		// System.out.println(msg.receiver + " messagequeue add message--"
		// + msg.toString());
		return 1;

	}

	public void send(String receiver, String title, String content) {
		Message msg = new Message();
		msg.setSender(name);
		msg.setReceiver(receiver);
		msg.setTitle(title);
		msg.setContent(content);
		send(msg);
	}

	public void send(String receiver, String title, Object content) {
		Message msg = new Message();
		msg.setSender(name);
		msg.setReceiver(receiver);
		msg.setTitle(title);
		msg.setObjectContent(content);
		send(msg);
	}

	public void keepSynchronous(String key) {

	}

	protected int sendMessage(String receiver, String title, String content) {
		Message jobMsg = new Message();
		jobMsg.setReceiver(receiver);
		jobMsg.setContent(content);
		jobMsg.setTitle(title);
		return send(jobMsg);
	}

	public void sendMessage(String[] receivers, String title, String content) {
		Message jobMsg = new Message();

		jobMsg.setContent(content);
		jobMsg.setTitle(title);
		for (int i = 0; i < receivers.length; i++) {
			jobMsg.setReceiver(receivers[i]);
			send(jobMsg);
		}

	}

	public void sendSimulationMsg(Activation act) {
		Message msg = new Message();
		msg.setSender(name);
		msg.setReceiver("Simulator");
		msg.setTitle(act.getTitle());
		msg.setContent(act.getString());
		msg.setObjectContent(act.getObjectContent());
		send(msg);
	}

	public Message receive() {
		Message msg;
		synchronized (getMessageQueue().getLock()) {
			msg = getMessageQueue().poll();
		}
		// System.out.println("Recevie message--" + msg.toString());

		receivedMsgNum++;
		if (msg.getSendTime() < currentTime()) {
			System.out.println("**********************************");
			System.out.println("****time error(agent)********************");
			System.out.println(msg.toString());
			System.out.println("**********************************");
			System.exit(0);
		}
		currentTime = msg.getSendTime();
		lastReceivedMessage = msg;
		printMsg(msg, true);
		return msg;
	}

	public Message receive(String title) {
		Message msg = null;
		synchronized (getMessageQueue().getLock()) {
			for (int i = 0; i < getMessageQueue().size(); i++) {
				Message tm = (getMessageQueue().get(i));
				if (tm.getTitle().equals(title)) {
					msg = tm;
					getMessageQueue().remove(i);
					receivedMsgNum++;
					currentTime = msg.getSendTime();
					// System.out.println("    "+name+" "+currentTime()+" Recevie message--"
					// + msg.toString());
					lastReceivedMessage = msg;
					printMsg(msg, true);
					return msg;

				}
			}
		}
		return msg;
	}

	public Message blockingReceive(String title) {
		Message msg = null;
		while (true) {
			synchronized (getMessageQueue().getLock()) {
				for (int i = 0; i < getMessageQueue().size(); i++) {
					Message tm = (getMessageQueue().get(i));
					if (tm.getTitle().equals(title)) {
						msg = tm;
						getMessageQueue().remove(i);
						receivedMsgNum++;
						currentTime = msg.getSendTime();
						// System.out.println("    "+name+" "+currentTime()+" Recevie message--"
						// + msg.toString());
						lastReceivedMessage = msg;
						printMsg(msg, true);
						return msg;

					}
				}
			}
		}
		// return msg;
	}

	public Message receive(String title, String content) {
		Message msg = null;
		synchronized (getMessageQueue().getLock()) {
			for (int i = 0; i < getMessageQueue().size(); i++) {
				Message tm = (getMessageQueue().get(i));
				if (tm.getTitle().equals(title)
						&& tm.getContent().equals(content)) {
					msg = tm;
					getMessageQueue().remove(i);
					receivedMsgNum++;
					currentTime = msg.getSendTime();
					// System.out.println("    "+name+" "+currentTime()+" Recevie message--"
					// + msg.toString());
					lastReceivedMessage = msg;
					printMsg(msg, true);
					return msg;
				}
			}
		}
		return msg;

	}

	private void printMsg(Message msg, boolean receiveOrSend) {
		if (printMsg) {
			String str = "";
			if (receiveOrSend) {
				str = "received";
			} else {
				str = "sent";
			}
			System.out.println(additionalString + " ,SMN: "
					+ environment.getCount().getBusyAgentNum() + " ,RMN: "
					+ receivedMsgNum + "  " + name + " " + this.toString()
					+ "  " + currentTime() + " " + str + " message--"
					+ msg.getSendTime() + " " + msg.toString());

		}

	}

	public void addBehaviour(Behaviour b) {
		behaviourList.add(b);
	}

	public void removeBehaviour(Behaviour b) {
		behaviourList.remove(b);
	}

	void behaviourAction() {

		for (int i = 0; i < behaviourList.size(); i++) {
			int temp = behaviourList.get(i).action();
			if (temp == 0) {
				behaviourList.remove(i);
				i--;
			}
		}
	}

	@Override
	public void run() {

		// System.out.println(name);
		while (isRunning) {
			if (pause) {
				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			synchronized (getMessageQueue().getLock()) {
				if (getMessageQueue().isEmpty()) {
					// System.out.println(name + " messagequeue is empty.");
					try {
						// System.out.println(name + " is waiting now.");
						getMessageQueue().getLock().wait();
						// System.out.println(name + " is awake now.");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
			}

			behaviourAction();

			if (getEnvironment().getCount().minus(receivedMsgNum, name) == 0) {//
				synchronized (getEnvironment().getLock("Simulator")) {
					// System.out.println(name + " notify simulator.");
					// count.print(name);
					getEnvironment().notifySimulator();

				}
			}
			// environment.count.print(name);
			receivedMsgNum = 0;
			// afterBehaviour();
		}
		behaviourList = null;
		synchronized (getEnvironment().getMessageQueues()) {
			getEnvironment().deRegister(name);
			setMessageQueue(null);
			// System.out.println(name+" is destroyed!");
		}
		afterAgentDestroy();
	}

	public abstract void afterAgentDestroy();

	// public abstract void afterBehaviour();
	public void destroyAgent() {

		isRunning = false;
		notifyAgent();

	}

	public void pauseAgent() {
		synchronized (lock) {
			pause = true;
			if(this.getState()==Thread.State.WAITING){
				wakeUp();
			}

		}
	}

	public void continueAgent() {
		synchronized (lock) {
			pause = false;
			if(this.getState()==Thread.State.WAITING){
				lock.notifyAll();
			}

		}
	}

	public void wakeUp() {
		synchronized (getMessageQueue().getLock()) {
			getMessageQueue().getLock().notifyAll();
		}
	}

	public void resetAgent() {
		getMessageQueue().clear();
		isRunning = true;
		receivedMsgNum = 0;
		currentTime = 0;
	}

	public String getLocalName() {
		return name;
	}

	public void clearMessageQueue() {
		synchronized (getMessageQueue().getLock()) {
			getMessageQueue().clear();
		}
	}

	public long currentTime() {
		return currentTime;
	}

	protected void setCurrentTime(long t) {
		currentTime = t;
	}

	public void clone(Agent agent, AgentEnvironment environment) {
		agent.currentTime = currentTime;
		agent.receivedMsgNum = 0;
		agent.isRunning = isRunning;
		agent.setEnvironment(environment);
		agent.setMessageQueue(environment.getQueue(name));
		// agent.behaviourList=new ArrayList<Behaviour>(behaviourList);
		agent.lock = new byte[0];
		agent.pause=false;

		agent.name = name;

	}

//	public ArrayList<Behaviour> getBehavior(String type) {
//		ArrayList<Behaviour> list = new ArrayList<Behaviour>();
//		for (Behaviour b : behaviourList) {
//			if (b.getType().equals(type))
//				list.add(b);
//		}
//		return list;
//	}
//
//	public void removeBehaviour(String type) {
//		for (int i = 0; i < behaviourList.size(); i++) {
//
//			if (behaviourList.get(i).getType().equals(type)) {
//				behaviourList.remove(i);
//				i--;
//			}
//		}
//	}

	public void notifyAgent() {
		if (this.getState() == Thread.State.WAITING) {

			synchronized (getMessageQueue().getLock()) {
				getMessageQueue().getLock().notifyAll();
			}
		}
	}
	

	
	public String getAgentName(){
		return name;
	}

	public Message getLastReceivedMessage() {
		return lastReceivedMessage;
	}

	public void setLastReceivedMessage(Message lastReceivedMessage) {
		this.lastReceivedMessage = lastReceivedMessage;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Queue getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(Queue messageQueue) {
		this.messageQueue = messageQueue;
	}

	public AgentEnvironment getEnvironment() {
		return environment;
	}

	public void setEnvironment(AgentEnvironment environment) {
		this.environment = environment;
	}

	public String getAdditionalString() {
		return additionalString;
	}

	public void setAdditionalString(String additionalString) {
		this.additionalString = additionalString;
	}

}
