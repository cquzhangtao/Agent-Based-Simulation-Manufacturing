package simulation.framework;

public abstract class SimulationBase extends Thread {

	private AgentEnvironment environment;
	private byte[] lock = new byte[0];
	private String name;
	private Queue messageQueue = null;

	private int receivedMsgNum = 0;
	private boolean isRunning = true;
	private long currentTime = 0;
	private boolean printMsg = false;
	private String additionalString = "";
	private boolean pause=false;

	SimulationBase(String name) {
		this.name = name;
		this.setName(name);

	}

	abstract public void onAllAgentReady();

	public void send(Message msg) {
		msg.setSender(name);
		synchronized (getEnvironment().getMessageQueues()) {
			if (getEnvironment().getQueue(msg.getReceiver()) == null) {
				System.out.println(additionalString
						+ " Can not find receiver. " + msg.toString()
						+ ", current time: " + currentTime);
				return;
			}
		}
		synchronized (getEnvironment().getLock(msg.getReceiver())) {

			getEnvironment().addMessage(msg);
			getEnvironment().notifyReceiver(msg.getReceiver());
			getEnvironment().getCount().plus();
		}
		printMsg(msg, false);
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

	Message receive() {
		Message msg;
		synchronized (getMessageQueue().getLock()) {
			msg = getMessageQueue().poll();
		}
		receivedMsgNum++;
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
					printMsg(msg, true);
					return msg;

				}
			}
		}
		return msg;
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
					printMsg(msg, false);
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
						// receivedMsgNum++;
						environment.getCount().minus(1, name);
						// currentTime = msg.getSendTime();
						// System.out.println("    "+name+" "+currentTime()+" Recevie message--"
						// + msg.toString());
						return msg;

					}
				}
			}
		}
		// return msg;
	}

	abstract public void receiveNewActivation();

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

			// getEnvironment().getCount().print(name);
			// System.out.println(name+" is running now.");
			if (getEnvironment().getCount().ready()) {
				onAllAgentReady();
				continue;
			}

			synchronized (getMessageQueue().getLock()) {
				if (getMessageQueue().isEmpty()) {
					if (getEnvironment().getCount().ready()) {
						onAllAgentReady();
						continue;
					}
					try {
						// System.out.println(name + " is waiting now.");
						// System.out.println("***************************************************");
						getMessageQueue().getLock().wait();
						// System.out.println(name + " is awake now.");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
			}
			// System.out.println(name+" is awake now because of new activation point");
			receiveNewActivation();
			if (getEnvironment().getCount().minus(receivedMsgNum, name) == 0) {
				onAllAgentReady();
			}
			receivedMsgNum = 0;
		}

	}

	public void destroyAgent() {

		isRunning = false;
		synchronized (getEnvironment().getMessageQueues()) {
			getEnvironment().deRegister(name);
			setMessageQueue(null);
		}
	}

	public void destroyAgent1() {

		isRunning = false;
	}

	public void reset() {
		synchronized (getMessageQueue()) {
			getMessageQueue().clear();
		}
		isRunning = true;
		receivedMsgNum = 0;
		currentTime = 0;
		getEnvironment().reset();

	}

	public void pauseAgent() {
		synchronized (lock) {
			pause = true;
			if(this.getState()==Thread.State.WAITING){
				wakeUp();
			}
		}
	}

	public void wakeUp() {
		synchronized (getMessageQueue().getLock()) {
			getMessageQueue().getLock().notifyAll();
		}
	}

	public void continueAgent() {
		synchronized (lock) {
			pause = false;
			lock.notifyAll();
		}
	}

	public long currentTime() {
		return currentTime;
	}

	protected void setCurrentTime(long t) {
		currentTime = t;
	}

	public void clone(SimulationBase sim, AgentEnvironment environment) {
		sim.currentTime = currentTime;
		sim.receivedMsgNum = 0;
		sim.isRunning = isRunning;
		sim.setEnvironment(environment);
		sim.setMessageQueue(environment.getQueue(name));
		sim.lock = new byte[0];
		sim.name = name;
		sim.pause=false;

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
