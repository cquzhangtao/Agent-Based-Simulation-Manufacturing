package menu.model.activitynetwork;

import java.util.Map;

public class ResourceRequirements {
	
	private Map<ResourceFunction,AmountLimit> req;

	public Map<ResourceFunction,AmountLimit> getReq() {
		return req;
	}

	public void setReq(Map<ResourceFunction,AmountLimit> req) {
		this.req = req;
	}
}
