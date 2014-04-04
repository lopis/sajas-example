package repastTest;

import repast.simphony.engine.schedule.ScheduledMethod;
import up.fe.liacc.repacl.Agent;

public class MyAgent extends Agent{
	
	/**
	 * 
	 */
	public void setup(){
		System.out.println("New agent: " + getAID());
		addBehavior(new RequestResp(this));
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		System.err.println("[A] My agent step #" + getAID());
	}
}
