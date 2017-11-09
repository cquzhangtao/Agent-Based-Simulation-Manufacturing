package menu.model.activitynetwork;

import java.util.List;

public interface OutputMatierals {
	public List<MaterialAndQuantity> getOutput(List<MaterialAndQuantity> mq,List<ResourceAndQuantity> rq,Amount time);
}
