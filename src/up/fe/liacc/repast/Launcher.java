package up.fe.liacc.repast;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.AMSService;
import up.fe.liacc.sajas.wrapper.AgentController;

/**
 * This class is a wrapper to Repast's launcher or "Context Builder".
 * Using this class, direct access to the context should not be needed.
 * @author joaolopes
 *
 */
public abstract class Launcher implements ContextBuilder<Object>{

	private Context<Object> context;

	
	@SuppressWarnings("rawtypes")
	public Context build(Context<Object> context) {
		this.context = context;
		
		// create action for behaviour scheduling
		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1);
		BehaviourAction behaviourAction = new BehaviourAction();
		RunEnvironment.getInstance().getCurrentSchedule().schedule(params, behaviourAction);
		context.add(behaviourAction);
		//
		RepastAgent.setBehaviourAction(behaviourAction);
		
		setup();
		return context;
	}
	
	protected abstract void setup();
	
	/**
	 * Agents are added to the AMS service and are
	 * given and AID. 
	 * @param name
	 * @param ra
	 * @return
	 */
	public void acceptNewAgent(String name, Agent a) {
		a.setAID(new AID(name));
		AMSService.register(a);
		
		context.add(a);
		
		// Return for calling start()
		AgentController ac = new AgentController(a);
		ac.start();
	}
	
}
