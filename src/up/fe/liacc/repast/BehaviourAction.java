package up.fe.liacc.repast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import repast.simphony.engine.schedule.IAction;
import up.fe.liacc.sajas.core.behaviours.Behaviour;

public class BehaviourAction implements IAction{

	private List<Behaviour> behaviours;

	public BehaviourAction() {
		this.behaviours = new ArrayList<Behaviour>();
	}
	
	public void addBehaviour(Behaviour b) {
		behaviours.add(b);
	}
	
	public void removeBehaviour(Behaviour b) {
		behaviours.remove(b);
	}

	public void execute() {
		Collections.shuffle(behaviours);
		for(Behaviour b : behaviours) {
			b.action();
		}
	}
	
}