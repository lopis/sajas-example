package contractNetTest;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import up.fe.liacc.repacl.core.Agent;
import up.fe.liacc.repacl.lang.acl.ACLMessage;
import up.fe.liacc.repacl.lang.acl.AID;
import up.fe.liacc.repacl.proto.ContractNetInitiator;

public class SupplyNetInitiator extends ContractNetInitiator {
	
	long startTime;

	public SupplyNetInitiator(Agent owner, ACLMessage cfp) {
		super(owner, cfp);
		startTime = System.currentTimeMillis();
	}
	
	@Override
	@ScheduledMethod(start=1, interval=0.000001)
	public void action() {
		super.action();
	}
	
	@Override
	protected void handleAllResponses(ArrayList<ACLMessage> responses,
			ArrayList<ACLMessage> acceptances) {		  
		
		int bestOfferPrice = ((BuyerAgent)this.getAgent()).getMaximumPrice();
		AID bestOfferAgent = null;
		
		for (ACLMessage proposal : responses) {
			if (proposal.getPerformative() == ACLMessage.REFUSE) {
				
			} else if (proposal.getContentObject() == null) {
				
			} else if (((int) proposal.getContentObject()) < bestOfferPrice) {
				// NEW BEST PROPOSAL
				bestOfferPrice = ((int) proposal.getContentObject());
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
	protected void handlePropose(ACLMessage m) {
		System.out.println("Got propose: " + m.getContentObject());
	}
	
	@Override
	protected void handleRefuse(ACLMessage m) {
		//System.out.println("Got refuse");
	}
	
}
