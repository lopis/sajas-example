package enterpriseTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import enterpriseTest.proto.BuyBehaviour;
import enterpriseTest.proto.SellDispatcher;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.behaviours.SimpleBehaviour;
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
	
	private static final int MAX_RETRIES = 5;
	private int retries;


	public EnterpriseAgent(String name) {
		super();
		setLocalName(name);
	}

	@Override
	protected void setup() {
		
		if (!sells.isEmpty()) {
			setupSell();
		}

		if (!buys.isEmpty()) {
			// Start initiator with the first item to buy
			setupNextBuy();
		}
		
		
	}

	private void setupSell() {
		// Start responder
		addBehaviour(new SellDispatcher(this, sells));
		DFAgentDescription dfd1 = new DFAgentDescription();
		
		// The name of the service is the name of the product being sold
		// Register all services/products
		for (Iterator<String> iterator = sells.keySet().iterator(); iterator.hasNext();) {
			String product = iterator.next();
			ServiceDescription sd = new ServiceDescription();
			sd.setName(product);
			sd.setType("sell");
			dfd1.addServices(sd);
		}
			
		dfd1.setName(getAID());
		try {
			dfd1 = DFService.register(this, dfd1);
		} catch (FIPAException e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	public void setupNextBuy() {
		SupplyRequest request = buys.pop();
		DFAgentDescription dfd2 = new DFAgentDescription(); // Get agents that sell this product from the DF
		ServiceDescription sd = new ServiceDescription();
		sd.setName(request.getProduct()); // The name of the service is the name of the product being sold
		sd.setType("sell");
		dfd2.addServices(sd);
		DFAgentDescription[] results = {};
		try {
			// Search the DF
			results = DFService.search(this, dfd2);
			
			if (results.length == 0) {
				if (retries++ < MAX_RETRIES) {
					buys.add(request); //Try again later
					addBehaviour(new SimpleBehaviour() {
						@Override
						public void action() {
							// On the next tick, try to setup a new behaviour
							setupNextBuy();
							// Stop this behaviour.
							removeBehaviour(this);
						}
					});
				} else {
					System.out.println("[" + getLocalName() + "] No sellers found!");
				}
			} else {
				System.out.println("[" + getLocalName() + "] Found "
						+ results.length + " sellers of " + request.getProduct());
				for (int i = 0; i < results.length; i++) {
					System.out.println("\t\t" + results[i].getName());
				}
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.setSender(this.getAID());
				for (int i = 0; i < results.length; i++) {
					cfp.addReceiver(results[i].getName());
				}
				cfp.setContentObject(request);
				cfp.setConversationId("");
				cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
				// Start responder
				addBehaviour(new BuyBehaviour(this, cfp, request.getAmount(), request.getProduct()));
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (FIPAException e) {
			System.err.println("Failed to search the DF");
			return;
		}
	}

	public void addBuy(String product, int amount) {
		buys.push(new SupplyRequest(product, amount));
	}

	public void addSell(String product, int price) {
		sells.put(product, price);
	}
}
