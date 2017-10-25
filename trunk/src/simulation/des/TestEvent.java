package simulation.des;

import java.util.LinkedList;
import java.util.List;

public class TestEvent extends Event{

	@Override
	public Object[] fire() {
		Object[] obj=new Object[2];
		TestEvent e=new TestEvent();
		e.setTime(getTime()+1);
		List<Event> events=new LinkedList<Event>();
		events.add(e);
		obj[0]=events;
		return obj;
	}

}
