package enterpriseTest.proto;

import java.util.ArrayList;
import java.util.Vector;

import enterpriseTest.agent.BuyerAgent;
import enterpriseTest.model.Contract;
import enterpriseTest.model.Contract.OutcomeType;
import enterpriseTest.model.SupplyProposal;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.ContractNetInitiator;

public class BuyBehaviour extends ContractNetInitiator {

	String product;
	int demand;
	
	long startTime;

	public BuyBehaviour(Agent agent, ACLMessage cfp, int demand, String product) {
		super(agent, cfp);
		this.demand = demand;
		this.product = product;
	}
	
	@Override
	public ArrayList<ACLMessage> prepareCfps(ACLMessage cfp) {
		startTime = System.currentTimeMillis();
		return super.prepareCfps(cfp);
	}

//	public BuyBehaviour(Agent agent) {
//		super(agent, null);
//	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void handleAllResponses(Vector responses, Vector acceptances) {
		
		int bestOfferPrice = Integer.MAX_VALUE;
		AID bestOfferAgent = null;

		for (Object obj : responses) {
			ACLMessage proposal = (ACLMessage) obj;
			if (proposal.getPerformative() == ACLMessage.REFUSE) {

			} else {
				try {
					if (proposal.getContentObject() == null) {

					} else if (((SupplyProposal) proposal.getContentObject()).myPrice < bestOfferPrice) {
						// NEW BEST PROPOSAL
						bestOfferPrice = ((SupplyProposal) proposal.getContentObject()).myPrice;
						if (bestOfferAgent != null) {
							ACLMessage m = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
							m.setSender(this.getAgent().getAID());
							m.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
							m.addReceiver(bestOfferAgent);
							acceptances.add(m);
						}
						bestOfferAgent = proposal.getSender();
					} else {
						// COULDN'T BEAT THE BEST
						ACLMessage m = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						m.addReceiver(proposal.getSender());
						m.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
						m.setConversationId(this.cfp.getConversationId());
						acceptances.add(m);
					}
				} catch (UnreadableException e) {
					System.err.println("Failed to read ACLMessage content.");
					return;
				}
			}
		}

		// THE FINAL BEST
		ACLMessage m = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		m.setSender(this.getAgent().getAID());
		m.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		long timeTaken = System.currentTimeMillis() - startTime;
		System.out.println("\t\t\t\t" + timeTaken + "ms");
		m.setConversationId(this.cfp.getConversationId());
		m.addReceiver(bestOfferAgent);
		acceptances.add(m);
	}

	@Override
	protected void handleInform(ACLMessage inform) {
		String outcome = inform.getContent();
		OutcomeType outcomeType;
		
		if (outcome == null) {
			return;
		}
		
		switch (outcome) {
		case "FULLFIELD":
			outcomeType = Contract.OutcomeType.FULLFIELD;
			break;
		case "DELAYED":
			outcomeType = Contract.OutcomeType.DELAYED;
			break;
		case "VIOLATED":
			outcomeType = Contract.OutcomeType.VIOLATED;
			break;
		default:
			return;
		}
		((BuyerAgent) myAgent).submitContractOutcome(new Contract(myAgent.getAID(), outcomeType));
		this.onEnd();
	}

//	@Override
//	protected void handlePropose(ACLMessage m, @SuppressWarnings("rawtypes") Vector acceptances) {
//		try {
//			SupplyProposal proposal = (SupplyProposal) m.getContentObject();
//			System.out.println("\u25A1 B " + myAgent.getLocalName()
//					+ "] " + m.getSender()
//					+ " proposes " + proposal.myPrice
//					+ " for " + demand + " of " + " product.");
//
//		} catch (UnreadableException e) {
//			e.printStackTrace();
//		}
//	}

	@SuppressWarnings("unused")
	private ACLMessage createRefuse(ACLMessage m) {
		ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
		refuse.setProtocol(m.getProtocol());
		refuse.setSender(myAgent.getAID());

		return refuse;
	}
	
	@Override
	/**
	 * On end, deregister this and initiate the next
	 * protocol calling nextBuy() on myAgent. The superclass
	 * will already have removed this behaviour from the 
	 * agent's set of behaviours when this method is called.
	 */
	public int onEnd() {
		((BuyerAgent) myAgent).startNextBuy();
		return 1; // 1 means finished
	}
	

}
