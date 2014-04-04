package repastTest;

import repast.simphony.engine.schedule.ScheduledMethod;
import up.fe.liacc.repacl.Agent;
import up.fe.liacc.repacl.acl.ACLMessage;
import up.fe.liacc.repacl.acl.Performative;
import up.fe.liacc.repacl.core.DF;

public class InitiatorAgent extends Agent {

	@Override
	@ScheduledMethod(start=1, interval=1000)
	public void step() {
		Object[] agents = DF.getAgents().values().toArray();
		
		ACLMessage message = new ACLMessage(Performative.REQUEST);
		for (int i = 0; i < agents.length; i++) {
			message.addReceiver((Agent) agents[i]);
		}
		
		addBehavior(new RequestInit(this, message));
	}
}
