package simulation.framework;

public class Activation implements Comparable<Activation> {
	private long activateTime;
	private String receiver;
	private String title;
	private String content = "";
	private int type = 0;// uncondition
	private int priority = 0;
	private Object objectContent;


	public Activation(long activateTime, String receiver, String title) {
		this.setActivateTime(activateTime);
		this.receiver = receiver;
		this.setTitle(title);
	}

	public Activation(long activateTime, String receiver, String title,
			String content) {
		this.setActivateTime(activateTime);
		this.receiver = receiver;
		this.setTitle(title);
		this.content = content;
	}

	public Activation(Message msg) {
		String s[] = msg.getContent().split("-");
		setActivateTime(Long.valueOf(s[0]));
		receiver = s[1].trim();
		setTitle(s[2]);
		this.content = s[3].trim();
		this.setType(Integer.valueOf(s[4]));
		this.setObjectContent(msg.getObjectContent());
		
	}

	public Activation() {
		// TODO Auto-generated constructor stub
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getString() {
		return String.format("%d-%s-%s-%s-%d", getActivateTime(), receiver,// keep
																		getTitle(), content, getType());
	}

	@Override
	public int compareTo(Activation arg0) {
		// TODO Auto-generated method stub
		if (getPriority() > arg0.getPriority())
			return 1;
		else if (getPriority() == arg0.getPriority())
			return 0;
		else
			return -1;
	}

	public int compareBy(Activation arg0) {
		// TODO Auto-generated method stub
		if (getActivateTime() > arg0.getActivateTime())
			return 1;
		else if (getActivateTime() == arg0.getActivateTime())
			return 0;
		else
			return -1;
	}

	public void active(Simulation simulator) {
		Message msg = new Message();
		msg.setReceiver(receiver);
		msg.setTitle(getTitle());
		msg.setSendTime(simulator.currentTime());
		msg.setContent(content);
		msg.setObjectContent(objectContent);
		
		simulator.send(msg);
		// System.out.println("Activate "+getContentString());
	

	}

	public void print() {
		System.out.println(String.format(
				"ActivateTime:%d,receiver:%s,title:%s", getActivateTime(), receiver,
				getTitle()));
	}
	@Override
	public Activation clone(){
		Activation act=new Activation();
		act.setActivateTime(this.getActivateTime());
		act.content=this.content;
		act.setPriority(this.getPriority());
		act.receiver=this.receiver;
		act.setType(this.getType());
		act.setTitle(this.getTitle());
		act.objectContent=objectContent;
		return act;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getActivateTime() {
		return activateTime;
	}

	public void setActivateTime(long activateTime) {
		this.activateTime = activateTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getObjectContent() {
		return objectContent;
	}

	public void setObjectContent(Object objectContent) {
		this.objectContent = objectContent;
	}


}