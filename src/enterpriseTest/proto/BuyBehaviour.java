package enterpriseTest.proto;

import java.util.Vector;

import enterpriseTest.SupplyProposal;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.ContractNetInitiator;

public class BuyBehaviour extends ContractNetInitiator {

	String product;
	int demand;

	public BuyBehaviour(Agent agent, ACLMessage cfp, int demand, String product) {
		super(agent, cfp);
		this.demand = demand;
		this.product = product;
	}


	@Override
	protected void handlePropose(ACLMessage m, @SuppressWarnings("rawtypes") Vector acceptances) {
		try {
			SupplyProposal proposal = (SupplyProposal) m.getContentObject();
			System.out.println("[B " + myAgent.getLocalName()
					+ "] Propose for " + demand 
					+ " of " + product 
					+ ": " + proposal.myPrice);

		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	private ACLMessage createRefuse(ACLMessage m) {
		ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
		refuse.setProtocol(m.getProtocol());
		refuse.setSender(myAgent.getAID());

		return refuse;
	}

}
