package contractNetTest;

import java.util.Vector;

import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.ContractNetInitiator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SupplyNetInitiator extends ContractNetInitiator {
	
	long startTime;

	public SupplyNetInitiator(Agent owner, ACLMessage cfp) {
		super(owner, cfp);
		startTime = System.currentTimeMillis();
	}
	
	@Override
	protected void handleAllResponses(Vector responses, Vector acceptances) {		  
		
		int bestOfferPrice = ((BuyerAgent)this.getAgent()).getMaximumPrice();
		AID bestOfferAgent = null;
		
		for (Object obj : responses) {
			ACLMessage proposal = (ACLMessage) obj;
			if (proposal.getPerformative() == ACLMessage.REFUSE) {
				
			} else
				try {
					if (proposal.getContentObject() == null) {
						
					} else if (((SupplyProposal) proposal.getContentObject()).myPrice < bestOfferPrice) {
						// NEW BEST PROPOSAL
						bestOfferPrice = ((SupplyProposal) proposal.getContentObject()).myPrice;
						if (bestOfferAgent != null) {
							ACLMessage m = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
							m.addReceiver(bestOfferAgent);
							acceptances.add(m);
						}
						bestOfferAgent = proposal.getSender();
					} else {
						// COULDN'T BEAT THE BEST
						ACLMessage m = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						m.addReceiver(proposal.getSender());
						acceptances.add(m);
					}
				} catch (UnreadableException e) {
					System.err.println("Failed to read ACLMessage content.");
					return;
				}
		}
		
		// THE FINAL BEST
		ACLMessage m = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		System.out.println("Best offer: " + bestOfferPrice);
		long timeTaken = System.currentTimeMillis() - startTime;
		System.out.println("Time taken:" + timeTaken + "ms");
		m.addReceiver(bestOfferAgent);
		acceptances.add(m);
	}
	
	@Override
	protected void handlePropose(ACLMessage m, Vector acceptances) {
		//System.out.println("Got propose");
	}
	
	@Override
	protected void handleRefuse(ACLMessage m) {
		//System.out.println("Got refuse");
	}
	
}
