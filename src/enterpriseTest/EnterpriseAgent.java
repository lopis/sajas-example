package enterpriseTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import enterpriseTest.proto.SellBehaviour;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;
import up.fe.liacc.sajas.lang.acl.ACLMessage;

public class EnterpriseAgent extends RepastAgent {

	public static final String RICE = "rice";
	public static final String WHEAT = "wheat";
	public static final String OATS = "oats";

	private LinkedList<SupplyRequest> buys = new LinkedList<SupplyRequest>(); // products -> amounts
	private HashMap<String, Integer> sells = new HashMap<String, Integer>(); // products -> prices


	public EnterpriseAgent(String name) {
		super();
		setLocalName(name);
	}

	@Override
	protected void setup() {
		super.setup();
		
		// Start responder
		addBehaviour(new SellBehaviour(this, sells));
		
		// Start initiator with the first item to buy
		SupplyRequest request = buys.pop();
		DFAgentDescription dfd = new DFAgentDescription(); // Get agents that sell this product from the DF
		ServiceDescription sd = new ServiceDescription();
		sd.setName(request.getProduct()); // The name of the service is the name of the product being sold
		dfd.addServices(sd);
		DFAgentDescription[] results = {};
		try {
			// Search the DF
			results = DFService.search(this, dfd);
			System.out.println("[B " + getLocalName() + "] Found "
					+ results.length + " sellers in the DF");
			if (results.length == 0) {
				System.out.println("No sellers found!");
			}
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			for (int i = 0; i < results.length; i++) {
				cfp.addReceiver(results[i].getName());
			}
			cfp.setContentObject(request);
			cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (FIPAException e) {
			System.err.println("Failed to search the DF");
			return;
		}
	}

	public void addBuy(String product, int amount) {
		buys.add(new SupplyRequest(product, amount));
	}

	public void addSell(String product, int price) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName(product); // The name of the service is the name of the product being sold
		dfd.addServices(sd);
		dfd.setName(getAID());
		try {
			dfd = DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(e.getMessage());
			return;
		}

		sells.put(product, price);
	}
}
