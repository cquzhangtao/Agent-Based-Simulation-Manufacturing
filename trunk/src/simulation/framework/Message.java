package simulation.framework;

public class Message {
	private String sender;
	private String receiver;
	private String content;
	private  String title;
	private  long sendTime;
	private String replyWith = "";
	private Object objectContent;

	Message(String sender, String receiver, String title, String content) {
		this.setSender(sender);
		this.receiver = receiver;
		this.content = content;
		this.setTitle(title);
	}

	public Message(String receiver, String title, String content) {
		this.receiver = receiver;
		this.content = content;
		this.setTitle(title);
	}

	public Message(String receiver, String title) {
		this.receiver = receiver;
		this.setTitle(title);
	}

	public Message() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return String.format("Sender:%s,Receiver:%s,title:%s,Content:%s,Time:%d ",
				getSender(), receiver, getTitle(), content,sendTime);
	}

	public void setReceiver(String r) {
		receiver = r;
	}

	public void setTitle(String t) {
		title = t;
	}

	public void setContent(String c) {
		content = c;
	}

	public void setSendTime(long t) {
		sendTime = t;
	}

	public String getContent() {
		return content;
	}

	public String getSender() {
		// TODO Auto-generated method stub
		return sender;
	}

	public Message createReply() {
		Message msg = new Message();
		msg.receiver = getSender();
		msg.setTitle(title);
		return msg;
	}

	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}

	public String getReplyWith() {
		return replyWith;
	}
	@Override
	public Message clone(){
		Message msg=new Message();
		msg.setSender(sender);
		msg.receiver=receiver;
		msg.content=content;
		msg.setTitle(title);
		msg.sendTime=sendTime;
		msg.replyWith=replyWith ;
		return msg;
	}

	public Object getObjectContent() {
		return objectContent;
	}

	public void setObjectContent(Object objectContent) {
		this.objectContent = objectContent;
	}

	public String getReceiver() {
		return receiver;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTitle() {
		return title;
	}
}
