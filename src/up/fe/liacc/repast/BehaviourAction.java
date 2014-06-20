package up.fe.liacc.repast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import repast.simphony.engine.schedule.IAction;
import up.fe.liacc.sajas.core.behaviours.Behaviour;

public class BehaviourAction implements IAction{

	private HashMap<Long, Behaviour> behaviours;

	public BehaviourAction() {
		this.behaviours = new HashMap<Long, Behaviour>();
	}

	public void addBehaviour(Behaviour b) {
		behaviours.put(b.getID(), b);
//		System.err.println("++++++ " + b.getClass() + "@" + b.getID());
	}

	public void removeBehaviour(Behaviour b) {
		behaviours.remove(b.getID());
//		System.err.println("------ " + b.getClass() + "@" + b.getID());
	}

	public void execute() {
		List<Behaviour> schedule = new ArrayList<Behaviour>(behaviours.values());
		Collections.shuffle(schedule); 
		// Must iterate like this because action() may modify this list.
		// May throw ConcurrentModicationException
		int i = 0;
//		System.err.println(schedule.size());
		for (; i < schedule.size(); i++) { 
			schedule.get(i).action();
			if (schedule.get(i).done()) {
				removeBehaviour(schedule.get((i)));
				schedule.get(i).onEnd();
			}
		}
	}

}