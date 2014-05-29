package up.fe.liacc.repast;

import up.fe.liacc.sajas.MTS;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.Behaviour;
import up.fe.liacc.sajas.lang.acl.ACLMessage;

public class RepastAgent extends Agent {
	
	private static BehaviourAction behaviourAction;

	public static void setBehaviourAction(BehaviourAction behaviourAction) {
		RepastAgent.behaviourAction = behaviourAction;
	}

	@Override
	public void addBehaviour(Behaviour behaviour) {
		behaviourAction.addBehaviour(behaviour);
		getBehaviours().add(behaviour);
	}
	
	@Override
	public void removeBehaviour(Behaviour b) {
		// unschedule the behaviour
		RepastAgent.behaviourAction.removeBehaviour(b);
		getBehaviours().remove(b);
	}
	
	public void send(ACLMessage message) {
		message.setSender(getAID());
		MTS.send(message);
	}

}
