package enterpriseTest.agent;

import java.io.IOException;
import java.util.ArrayList;
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


	public final static int MAX_AGENTS = 2; // Constraint for trust results

	int index = 0;
	private int amount;
	private String product;
	private ArrayList<AID> sellers = new ArrayList<AID>();
	public AID ctAgent;
	private ACLMessage cfp;
	private ACLMessage ctRequest;

	public boolean useTrust;

	public BuyerAgent(String product, int amount, boolean useTrust) {
		this.product = product;
		this.amount = amount;
		this.useTrust = useTrust;
	}

	@Override
	protected void setup() {
		
//		System.out.println("[" + getLocalName() + "]"
//				+ " I'm buying:\n\t\t" + product + "\t" + amount + " units");

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
			CTObject cto = new CTObject(sellers, MAX_AGENTS);
			this.ctRequest.setContentObject(cto);
			this.ctRequest.setConversationId(getLocalName() + "-" + System.nanoTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Initiate a request to get the trust values
		AchieveREInitiator ctRequestBehaviour = new AchieveREInitiator(this, ctRequest) {

			ArrayList<Pair<AID>> sellersTrust;
			@Override
			protected void handleInform(ACLMessage reply) {
				try {
					CTObject ct = (CTObject) reply.getContentObject();
					this.sellersTrust = ct.trustValues;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public int onEnd() {
				if (sellersTrust != null) {
					cfp.clearAllReceiver();
					
					if (!useTrust) {
						// Ignore what the CT agent said
						cfp.clearAllReceiver();
						for (Iterator<AID> iterator = sellers.iterator(); iterator.hasNext();) {
							cfp.addReceiver(iterator.next());
						}
						addBehaviour(new BuyBehaviour(this.myAgent, cfp, amount, product));
					} else {
						for (int i = 0; i < sellersTrust.size(); i++) {
							cfp.addReceiver(sellersTrust.get(i).key);
						}
						
						// When the request protocol ends, start the contract net
						addBehaviour(new BuyBehaviour(this.myAgent, cfp, amount, product));
					}
					sellersTrust = null;			
					
				}
				return 1;
			}
		};
		addBehaviour(ctRequestBehaviour);
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
			ACLMessage outcomeMessage = new ACLMessage(ACLMessage.REQUEST);
			outcomeMessage.setContentObject(contract);
			outcomeMessage.addReceiver(ctAgent);
			send(outcomeMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
