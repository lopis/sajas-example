package contractNetTest.repast;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.util.ContextUtils;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.Behaviour;

@SuppressWarnings("rawtypes")
public class RepastAgent extends Agent {
	
	private static Context context;
	private static ISchedule schedule;

	@SuppressWarnings("unchecked")
	@Override
	protected void addBehaviour(Behaviour behaviour) {
		context.add(behaviour);
		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1);
		//schedule.schedule(params, behaviour, "action");
		schedule.schedule(params, new BehaviourAction(behaviour));
		getBehaviours().add(behaviour);
	}
	
	public static void setContext(Context context, ISchedule iSchedule) {
		RepastAgent.context = context;
		RepastAgent.schedule = iSchedule;
	}

	@Override
	protected void removeBehaviour(Behaviour behaviour) {
		ContextUtils.getContext(this).remove(behaviour); // unschedule the behaviour
		getBehaviours().remove(behaviour);
	}
	
	public class BehaviourAction implements IAction{

		private Behaviour behaviour;

		public BehaviourAction(Behaviour behaviour) {
			this.behaviour = behaviour;
		}
		
		public void execute() {
			behaviour.action();
		}
		
	}

}
