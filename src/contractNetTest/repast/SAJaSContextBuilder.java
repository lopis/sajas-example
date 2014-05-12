package contractNetTest.repast;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.AMSService;
import up.fe.liacc.sajas.domain.AgentControler;

public abstract class SAJaSContextBuilder implements ContextBuilder<Object> {

	@SuppressWarnings("rawtypes")
	@Override
	public abstract Context build(Context<Object> context);

	
	/**
	 * Agents are added to the AMS service and are
	 * given and AID. 
	 * @param name
	 * @param ra
	 * @return
	 */
	public AgentControler acceptNewAgent(String name, Agent a) {
		a.setAID(new AID(name));
		AMSService.register(a);
		
		// Return for calling start()
		AgentControler ac = new AgentControler(a);
		return ac;
	}
	
}
