package contractNetTest.repast;

import java.util.Collection;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.util.ContextUtils;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.Behaviour;

@SuppressWarnings("rawtypes")
public class RepastAgent extends Agent {
	
	private static Collection context;

	@Override
	protected void addBehaviour(Behaviour behaviour) {
		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1);
		Schedule schedule = new Schedule();
		schedule.schedule(params, behaviour, "action");
		
		context.add(behaviour);
		getBehaviours().add(behaviour);
	}
	
	public static void setContext(Collection context) {
		RepastAgent.context = context;
	}

	@Override
	protected void removeBehaviour(Behaviour behaviour) {
		ContextUtils.getContext(this).remove(behaviour); // unschedule the behaviour
		getBehaviours().remove(behaviour);
	}

}
