package simulation.framework;

public abstract class Simulation extends SimulationBase {
	private EventList eventList;
	private EventList conditionEventList;
	private EventList currentEventList;
	private EventList currentConEventList;
	private boolean curScanListIsCondition = false;

	private long simulateBeginTime = 0;
	private int simulatorState = 0;
	private boolean runInParrale = false;
	private boolean allActivated = false;
	private boolean conSecondActivated = false;
	private Activation lastActivation=null;


	public Simulation(ActivationPriority pri) {
		super("Simulator");
		// TODO Auto-generated constructor stub
		setEventList(new EventList(pri));
		conditionEventList = new EventList(pri);

	}

	abstract public void onSimStart();

	public void stratSimulation() {
		onSimStart();
		reset();
		setSimulateBeginTime(System.currentTimeMillis());
		simulatorState = 1;
		if (!runInParrale)
			advance();
		beforeActivate();
		nextCurrentEvent();
		if (this.getState() == Thread.State.NEW)
			this.start();
		else if (this.getState() == Thread.State.WAITING) {
			this.wakeUp();
		}
	}

	public void pauseSimulation() {
		simulatorState = 0;
	}

	public void continueSimulation() {
		simulatorState = 1;
		wakeUp();
	}

	public void resetSimulation() {
		simulatorState = 0;
		curScanListIsCondition = false;
		setSimulateBeginTime(0);
		getEventList().clear();
		currentEventList = null;
		conditionEventList.clear();
		currentConEventList = null;
		allActivated = false;
		conSecondActivated = false;
		reset();
	}

	public void endSimulation() {
		onSimEnd();
	}

	public void stopSimulation() {
		simulatorState = 0;
		onSimStop();
	}

	abstract public void onSimStop();

	abstract public void onSimEnd();

	abstract public void onSimAdvance();

	abstract public boolean endCondition();

	abstract public void beforeActivate();

	// @SuppressWarnings("deprecation")
	@Override
	public void onAllAgentReady() {
		// TODO Auto-generated method stub
		if (endCondition()) {
			System.out.println("Simulation End, because of meeting end condition");
			this.setRunning(false);
			endSimulation();
			return;
		}
		if (simulatorState == 1) {
			
			beforeActivate();
			nextCurrentEvent();
			if(this.getAdditionalString().equals("Main Simulation")){
				String act="";
				if(lastActivation!=null){
					act=lastActivation.getString();
				}
			System.out.println("******"+this.getAdditionalString()+" Activation:"+act+"******");
			}
		} else {
			pauseAgent();
		}

	}

	public void addFirstActivation(Activation cus) {
		getEventList().addActivateEvent(cus);
	}

	@Override
	public void receiveNewActivation() {
		// TODO Auto-generated method stub
		Message msg = receive();
		eventHandle(msg);
	}

	public void eventHandle(Message msg) {
		Activation act = new Activation(msg);
		if (act.getType() == 1) {// uncondition
			conditionEventList.addActivateEvent(act);
		} else if (act.getType() == 2) {
			stopSimulation();
			endSimulation();
		} else {
			getEventList().addActivateEvent(act);
			// if(act.activateTime<earliestTime)
			// earliestTime=act.activateTime;
		}

	}

	public int advance() {
		currentEventList = getEventList().getEarliestActivations();
		// earliestTime=999999999;
		if (currentEventList == null)
			return 0;
		if (currentEventList.get(0).getActivateTime() < currentTime()) {
			System.out.println("**********************************");
			System.out.println("****time error********************");
			System.out.println(currentEventList.get(0).getString()
					+ " ,current time: " + currentTime());
			System.out.println("**********************************");
			System.exit(0);
		}
		setCurrentTime(currentEventList.get(0).getActivateTime());
		// System.out.println(currentEventList.get(0).getContentString());
		currentConEventList = conditionEventList.copy();
		conditionEventList.clear();
		onSimAdvance();
		return 1;
	}

	public void nextCurrentEvent() {

		if (runInParrale) {

			if (!allActivated || conditionEventList.size() < 1) {
				if (advance() == 0) {

					System.out
							.println("Simulation End, because of no activation point");
					endSimulation();
				}
				for (int i = 0; i < currentEventList.size(); i++) {
					currentEventList.get(i).active(this);
				}
				for (int i = 0; i < currentConEventList.size(); i++) {
					currentConEventList.get(i).active(this);
				}
				allActivated = true;
				conSecondActivated = false;
				// System.out
				// .println("Acitvate all points including conditional and unconditional points");
				return;
			}

			if (!conSecondActivated) {
				getEnvironment().resetUpdatedState();
				currentConEventList = conditionEventList.copy();
				conditionEventList.clear();
				for (int i = 0; i < currentConEventList.size(); i++) {
					currentConEventList.get(i).active(this);
				}
				conSecondActivated = true;
				// System.out
				// .println("Acitvate conditional points after all activation points are activated");
				return;
			}

			if (!getEnvironment().updated()) {
				allActivated = false;
				// System.out.println("The model is not updated any more");
				if (advance() == 0) {

					System.out
							.println("Simulation End, because of no activation point");
					endSimulation();
				}
				for (int i = 0; i < currentEventList.size(); i++) {
					currentEventList.get(i).active(this);
				}
				for (int i = 0; i < currentConEventList.size(); i++) {
					currentConEventList.get(i).active(this);
				}
				allActivated = true;
				conSecondActivated = false;
				// System.out
				// .println("Acitvate all points including conditional and unconditional points");
				return;

			}
			getEnvironment().resetUpdatedState();
			currentConEventList = conditionEventList.copy();
			conditionEventList.clear();
			for (int i = 0; i < currentConEventList.size(); i++) {
				currentConEventList.get(i).active(this);
			}
			// System.out
			// .println("Acitvate unconditional points because the model is updated");
			return;

		} else {
			

			if (!curScanListIsCondition && currentEventList.active(this) == 0) {
				curScanListIsCondition = true;
			}
			if (curScanListIsCondition && currentConEventList.active(this) == 0) {
				curScanListIsCondition = false;
				if (advance() != 0) {
					nextCurrentEvent();
				} else {
					System.out
							.println("Simulation End, because of no activation point");
					endSimulation();

				}
			}
		}
	}

	public double SimulatedRealTime() {
		return (System.currentTimeMillis() - getSimulateBeginTime()) / 1000.0 / 60.0;
	}

	
	public void clone(Simulation sim,AgentEnvironment environment){
		super.clone(sim,environment);		
		sim.setEventList(getEventList().clone());
		sim.conditionEventList = conditionEventList.clone();
		sim.currentEventList = currentEventList.clone();
		sim.currentConEventList = currentConEventList.clone();
		sim.curScanListIsCondition = curScanListIsCondition;
		sim.simulatorState = simulatorState;
		sim.allActivated = allActivated;
		sim.conSecondActivated = conSecondActivated;
		
		sim.simulateBeginTime = simulateBeginTime;
		sim.runInParrale = runInParrale;
		
	}

	public long getSimulateBeginTime() {
		return simulateBeginTime;
	}

	public void setSimulateBeginTime(long simulateBeginTime) {
		this.simulateBeginTime = simulateBeginTime;
	}

	public EventList getEventList() {
		return eventList;
	}

	public void setEventList(EventList eventList) {
		this.eventList = eventList;
	}

	public Activation getLastActivation() {
		return lastActivation;
	}

	public void setLastActivation(Activation lastActivation) {
		this.lastActivation = lastActivation;
	}
}
