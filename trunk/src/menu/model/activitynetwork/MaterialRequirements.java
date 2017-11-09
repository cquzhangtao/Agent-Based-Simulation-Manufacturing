package menu.model.activitynetwork;

import java.util.Map;

public class MaterialRequirements {
	private Map<MaterialProperty,AmountLimit> req;

	public Map<MaterialProperty,AmountLimit> getReq() {
		return req;
	}

	public void setReq(Map<MaterialProperty,AmountLimit> req) {
		this.req = req;
	}

}
