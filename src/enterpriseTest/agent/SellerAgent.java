package enterpriseTest.agent;

import java.util.HashMap;
import java.util.Iterator;

import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;
import enterpriseTest.proto.SellDispatcher;

public class SellerAgent extends EnterpriseAgent {

	public static int MAX_PRICE = 0;

	private HashMap<String, Integer> sells = new HashMap<String, Integer>(); // products -> prices

	public double trust = 0.0;


	public SellerAgent(String[] products, int[] prices, double trust) {
		super();

		sells.put(products[0], prices[0]);
		sells.put(products[1], prices[1]);
		sells.put(products[2], prices[2]);

		this.trust = trust;
		
		MAX_PRICE = Math.max(MAX_PRICE, prices[0]);
		MAX_PRICE = Math.max(MAX_PRICE, prices[1]);
		MAX_PRICE = Math.max(MAX_PRICE, prices[2]);
	}

	@Override
	protected void setup() {
		if (!sells.isEmpty()) {
			setupSell();
		}
	}

	private void setupSell() {
//		System.out.println("[" + getLocalName() + "][" + trust + "] Selling ");
//		for (Iterator<String> iterator = sells.keySet().iterator(); iterator.hasNext();) {
//			String prod = iterator.next();
//			System.out.println( prod +"  " + sells.get(prod) + "§");
//		}
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
