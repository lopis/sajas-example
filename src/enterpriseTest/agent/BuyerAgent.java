package enterpriseTest.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.AchieveREInitiator;
import enterpriseTest.model.CTObject;
import enterpriseTest.model.Contract;
import enterpriseTest.model.Pair;
import enterpriseTest.model.SupplyRequest;
import enterpriseTest.proto.BuyBehaviour;

public class BuyerAgent extends EnterpriseAgent {


	int index = 0;
	private int amount;
	private String product;
	private ArrayList<AID> sellers = new ArrayList<AID>();
	public AID ctAgent;
	private ACLMessage cfp;
	private ACLMessage ctRequest;

	public BuyerAgent(String name, String product, int amount) {
		super(name);
		this.product = product;
		this.amount = amount;
		System.out.println("[" + getLocalName() + "]"
				+ " I'm buying:\n\t\t" + product + "\t" + amount + " units");
	}

	@Override
	protected void setup() {

		// Ask DF who is the CTR
		this.ctAgent = searchTrustAgent();
		this.ctRequest = new ACLMessage(ACLMessage.REQUEST);
		this.ctRequest.addReceiver(ctAgent);
		
		
		try {
			// Search sellers in the DF
			DFAgentDescription dfd = createDFDService(product, "sell");
			DFAgentDescription[] sellers = DFService.search(this, dfd);
			for (int i = 0; i < sellers.length; i++) {
				this.sellers.add(sellers[i].getName());
			}
			
			// Create the proposal draft
			this.cfp = new ACLMessage(ACLMessage.CFP);
			cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			cfp.setContentObject(new SupplyRequest(product, amount));
			
			// Start behaviour
			startNextBuy();
		} catch (FIPAException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startNextBuy() {

		// Get the best agents out of the list
		try {
			CTObject cto = new CTObject();
			cto.addAll(sellers);
			this.ctRequest.setContentObject(cto);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Initiate a request to get the trust values
		AchieveREInitiator ctRequestBehaviour = new AchieveREInitiator(this, ctRequest) {
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
				// When the result arrives, stat the contract net
				startContractNet();
				return 1;
			}
		};
		addBehaviour(ctRequestBehaviour);
	}

	protected void startContractNet() {
		cfp.clearAllReceiver();
		for (Iterator<AID> iterator = sellers.iterator(); iterator.hasNext();) {
			cfp.addReceiver(iterator.next());
		}
		addBehaviour(new BuyBehaviour(this, cfp, amount, product));
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

	/**
	 * Sends contract outcome to the trust agent
	 * @param contract
	 */
	public void submitContractOutcome(Contract contract) {
		try {
			ACLMessage outcomeMessage = new ACLMessage(ACLMessage.INFORM);
			outcomeMessage.setContentObject(contract);
			outcomeMessage.addReceiver(ctAgent);
			send(outcomeMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
