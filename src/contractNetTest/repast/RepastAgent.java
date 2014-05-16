package contractNetTest.repast;

import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.Behaviour;

public class RepastAgent extends Agent {
	
	private static BehaviourAction behaviourAction;

	public static void setBehaviourAction(BehaviourAction behaviourAction) {
		RepastAgent.behaviourAction = behaviourAction;
	}

	@Override
	protected void addBehaviour(Behaviour behaviour) {
		behaviourAction.addBehaviour(behaviour);
		getBehaviours().add(behaviour);
	}
	
	@Override
	protected void removeBehaviour(Behaviour b) {
		// unschedule the behaviour
		RepastAgent.behaviourAction.removeBehaviour(b);
		getBehaviours().remove(b);
	}

}
