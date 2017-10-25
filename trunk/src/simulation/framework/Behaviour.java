package simulation.framework;

import java.io.Serializable;

public abstract class Behaviour implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type="";
	abstract public int action();
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	

}
