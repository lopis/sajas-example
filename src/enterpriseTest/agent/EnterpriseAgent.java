package enterpriseTest.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import enterpriseTest.model.CTObject;
import enterpriseTest.model.Pair;
import enterpriseTest.model.SupplyRequest;
import enterpriseTest.proto.BuyBehaviour;
import enterpriseTest.proto.SellDispatcher;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.core.behaviours.SimpleBehaviour;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.AchieveREInitiator;

public class EnterpriseAgent extends RepastAgent {

	public static final String RICE = "rice";
	public static final String WHEAT = "wheat";
	public static final String OATS = "oats";

	private LinkedList<SupplyRequest> buys = new LinkedList<SupplyRequest>(); // products -> amounts
	private HashMap<String, Integer> sells = new HashMap<String, Integer>(); // products -> prices

	private static final int MAX_RETRIES = 5;
	private int retries;
	private SupplyRequest request;


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
			// But first request information from the Computational Trust agent
			setupNextBuy();
		}


	}

	private void setupSell() {
		// Start responder
		addBehaviour(new SellDispatcher(this, sells));
		DFAgentDescription dfd = new DFAgentDescription();

		// The name of the service is the name of the product being sold
		// Register all services/products
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

	/**
	 * Effectively setup the behaviour
	 * to buy the product. Called from 
	 * setupNextBuy()
	 * @param results 
	 */
	private void setupBuy(ArrayList<AID> results) {
		ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
		cfp.setSender(getAID());
		for (Iterator<AID> iterator = results.iterator(); iterator.hasNext();) {
			cfp.addReceiver( iterator.next());

		}
		try {
			cfp.setContentObject(request);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		cfp.setConversationId("");
		cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		// Start responder
		addBehaviour(new BuyBehaviour(this, cfp, request.getAmount(), request.getProduct()));
	}

	/**
	 * Prepares for setting up a new contract to
	 * buy a product. But first, it sends a request to
	 * the Computational Trust agent to choose which
	 * agents to send the CFP to.
	 */
	public void setupNextBuy() {
		request = buys.pop();
		DFAgentDescription[] results = searchSellers(request);

		if (results.length == 0) {
			if (retries++ < MAX_RETRIES) { // Since no agents were found, retry later some number of times.
				buys.add(request); // Readd the request to the queue
				addBehaviour(new SimpleBehaviour() {
					@Override
					public void action() {
						removeBehaviour(this); 	// Stop this behaviour.
						setupNextBuy(); 		// Setup next behaviour.
					}
				});
			} else {	// No more retries
				System.out.println("[" + getLocalName() + "] No sellers found!");
			}
		} else {
			// Get Trust information
			getTrust(results);
		}

		if (results.length == 0) {
			if (retries++ < MAX_RETRIES) {
				buys.add(request);
				// Since no agents were found, retry later some number of times.
				addBehaviour(new SimpleBehaviour() {
					@Override
					public void action() {
						removeBehaviour(this);	// Stop this behaviour.
						setupNextBuy(); 		// Set it up again.
					}
				});
			} else {
				// No more retries
				System.out.println("[" + getLocalName() + "] No sellers found!");
			}
		} 
		//		else {
		//			System.out.println("[" + getLocalName() + "] Found "
		//					+ results.length + " sellers of " + request.getProduct());
		//			for (int i = 0; i < results.length; i++) {
		//				System.out.println("\t\t" + results[i].getName());
		//			}
		//		}
	}

	private void getTrust(DFAgentDescription[] results) {

		ACLMessage ctRequest = new ACLMessage(ACLMessage.REQUEST);
		ctRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		ctRequest.setSender(this.getAID());
		ctRequest.addReceiver(searchTrustAgent());
		try {
			CTObject ctObject = new CTObject();
			for (int i = 0; i < results.length; i++) {
				ctObject.trustValues.add(new Pair<AID>(results[i].getName(),0.0));
			}
			ctRequest.setContentObject(ctObject);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		ArrayList<AID> aids = new ArrayList<AID>();
		for (int i = 0; i < results.length; i++) {
			aids.add(results[i].getName());
		}

		// Initiate a request to get the trust values
		AchieveREInitiator requestBehaviour = new AchieveREInitiator(this, ctRequest) {
			ArrayList<Pair<AID>> results;
			@Override
			protected void handleInform(ACLMessage reply) {
				try {
					CTObject ct = (CTObject) reply.getContentObject();
					results = ct.trustValues;
					Collections.sort(results);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			@Override
			public int onEnd() {
				ArrayList<AID> aids = new ArrayList<AID>();
				for (int i = 0; i < results.size(); i++) {
					aids.add(results.get(i).key);
				}
				setupBuy(aids);
				return 1;
			}
		};
		addBehaviour(requestBehaviour);
	}

	/**
	 * Asks the DF who is the computational trust agent.
	 * If more than one CTAgent exists, return the first one.
	 * @return The AID of the first trust agent found or null
	 * if none was found.
	 */
	private AID searchTrustAgent() {
		DFAgentDescription dfd = createDFDService("trust", "trust");
		// Search the DF
		try {
			DFAgentDescription[] results = DFService.search(this, dfd);
			if (results.length > 0) {
				return results[0].getName();
			} 
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DFAgentDescription[] searchSellers(SupplyRequest request) {
		DFAgentDescription dfd = createDFDService(request.getProduct(), "sell");

		// Search the DF
		try {
			return DFService.search(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
			return new DFAgentDescription[0];
		}
	}

	/**
	 * Creates a simple DFDescription with a single service
	 * @param name Name field of the service
	 * @param type Type field of the service 
	 * @return
	 */
	private DFAgentDescription createDFDService(String name, String type) {
		DFAgentDescription dfd = new DFAgentDescription(); // Get agents that sell this product from the DF
		ServiceDescription sd = new ServiceDescription();
		sd.setName(name); // The name of the service is the name of the product being sold
		sd.setType("sell");
		dfd.addServices(sd);
		return dfd;
	}

	public void addBuy(String product, int amount) {
		buys.push(new SupplyRequest(product, amount));
	}

	public void addSell(String product, int price) {
		sells.put(product, price);
	}
}
