package enterpriseTest.agent;

import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;

public class EnterpriseAgent extends RepastAgent {

	public static final String RICE = "rice";
	public static final String WHEAT = "wheat";
	public static final String OATS = "oats";

	public EnterpriseAgent() {
		super();
	}

	/**
	 * Creates a simple DFDescription with a single service
	 * @param name Name field of the service
	 * @param type Type field of the service 
	 * @return
	 */
	protected DFAgentDescription createDFDService(String name, String type) {
		DFAgentDescription dfd = new DFAgentDescription(); // Get agents that sell this product from the DF
		ServiceDescription sd = new ServiceDescription();
		sd.setName(name); // The name of the service is the name of the product being sold
		sd.setType(type);
		dfd.addServices(sd);
		return dfd;
	}
}
