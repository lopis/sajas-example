package contractNetTest.repast;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
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
public abstract class Launcher implements ContextBuilder<Object> {

	private Context<Object> context;

	@SuppressWarnings("rawtypes")
	@Override
	public Context build(Context<Object> context) {
		this.context = context;
		RepastAgent.setContext(context, RunEnvironment.getInstance().getCurrentSchedule());
		setup();
		return context;
	}
	
	protected abstract void setup();
	
	protected void setContextId(String id) {
		context.setId(id);;
	}
	
	public Context<Object> getContext() {
		return context;
	}

	
	/**
	 * Agents are added to the AMS service and are
	 * given and AID. 
	 * @param name
	 * @param ra
	 * @return
	 */
	public AgentController acceptNewAgent(String name, Agent a) {
		a.setAID(new AID(name));
		AMSService.register(a);
		
		context.add(a);
		
		// Return for calling start()
		AgentController ac = new AgentController(a);
		return ac;
	}
	
}
