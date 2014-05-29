package enterpriseTest.agent;

import java.util.HashMap;
import java.util.Iterator;

import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;
import enterpriseTest.proto.SellDispatcher;

public class SellerAgent extends EnterpriseAgent {


	private HashMap<String, Integer> sells = new HashMap<String, Integer>(); // products -> prices

	public SellerAgent(String name, int[] prices) {
		super(name);
		
		sells.put(RICE , prices[0]);
		sells.put(OATS , prices[1]);
		sells.put(WHEAT, prices[2]);
	}

	@Override
	protected void setup() {
		if (!sells.isEmpty()) {
			setupSell();
		}
	}

	private void setupSell() {
		// Start responder dispatcher
		addBehaviour(new SellDispatcher(this, sells));
		DFAgentDescription dfd = new DFAgentDescription();

		// Register all services/products
		// The name of the service is the name of the product being sold
		for (Iterator<String> iterator = sells.keySet().iterator(); iterator.hasNext();) {
			String product = iterator.next();
			ServiceDescription sd = new ServiceDescription();
			sd.setName(product);
			sd.setType("sell");
			dfd.addServices(sd);
		}

		dfd.setName(getAID());
		try {
			dfd = DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(e.getMessage());
			return;
		}
	}
}
