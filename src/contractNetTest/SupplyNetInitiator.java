package contractNetTest;

import java.util.ArrayList;

import up.fe.liacc.repacl.Agent;
import up.fe.liacc.repacl.acl.ACLMessage;
import up.fe.liacc.repacl.acl.Performative;
import up.fe.liacc.repacl.proto.ContractNetInitiator;

public class SupplyNetInitiator extends ContractNetInitiator {

	public SupplyNetInitiator(Agent owner, ACLMessage cfp) {
		super(owner, cfp);
	}
	
	@Override
	protected void handleAllResponses(ArrayList<ACLMessage> responses,
			ArrayList<ACLMessage> acceptances) {
		
		
		int bestOfferPrice = ((BuyerAgent)this.getOwner()).getMaximumPrice();
		Agent bestOfferAgent = null;
		
		for (ACLMessage proposal : responses) {
			if (((int) proposal.getContent()) < bestOfferPrice) {
				// NEW BEST PROPOSAL
				bestOfferPrice = ((int) proposal.getContent());
				if (bestOfferAgent != null) {
					ACLMessage m = new ACLMessage(Performative.REJECT_PROPOSAL);
					m.addReceiver(bestOfferAgent);
					acceptances.add(m);
				}
				bestOfferAgent = proposal.getSender();
			} else {
				// COULDN'T BEAT THE BEST
				ACLMessage m = new ACLMessage(Performative.REJECT_PROPOSAL);
				m.addReceiver(proposal.getSender());
				acceptances.add(m);
			}
		}
		
		// THE FINAL BEST
		ACLMessage m = new ACLMessage(Performative.ACCEPT_PROPOSAL);
		m.addReceiver(bestOfferAgent);
		acceptances.add(m);
	}
}
