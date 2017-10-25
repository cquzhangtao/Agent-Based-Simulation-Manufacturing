package manu.agent.component;

import manu.model.component.JobInfo;
import simulation.framework.AgentEnvironment;
import simulation.framework.Behaviour;
import simulation.framework.Message;


public abstract class Buffer extends SimAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int transportBuffer = 0;	


	protected abstract boolean acceptJobCondition();

	protected abstract void onTransportRequest(long blockBeginTime);
	
	protected Buffer(String agentName) {
		super(agentName);
	}
	
	public void simpleClone(Buffer buffer){
		super.simpleClone(buffer);
		buffer.transportBuffer=transportBuffer;
	}

	protected class TransportRequestServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message msg = receive("Request Tool Process");
			if (msg != null) {
				Message reply=msg.createReply();
				if (acceptJobCondition()) {// many
					setTransportBuffer(getTransportBuffer() + 1);
					onTransportRequest(Long.valueOf(msg.getContent()));
					reply.setContent("Accept");
				} else {
					reply.setContent("Refuse");
				}
				send(reply);

			}
			return 1;
		}
	}

	protected abstract void onWaitRequest(JobInfo jobInfo);

	protected class WaitRequestServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message msg = receive("Wait Command");
			if (msg != null) {

				setTransportBuffer(getTransportBuffer() - 1);
				onWaitRequest((JobInfo) msg.getObjectContent());

			}
			return 1;
		}
	}
	
	
	public void clone(Buffer buffer,AgentEnvironment environment){
		super.clone(buffer, environment);
		buffer.setTransportBuffer(transportBuffer);
	}

	public int getTransportBuffer() {
		return transportBuffer;
	}

	public void setTransportBuffer(int transportBuffer) {
		this.transportBuffer = transportBuffer;
	}


}
