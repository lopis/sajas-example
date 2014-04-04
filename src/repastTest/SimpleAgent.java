package repastTest;

import repast.simphony.engine.schedule.ScheduledMethod;

public class SimpleAgent {

	@ScheduledMethod(start = 1, interval=500)
	private void step() {
		System.out.println("I'm simple agent");
	}
}
