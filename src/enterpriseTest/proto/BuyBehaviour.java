package enterpriseTest.proto;

import java.util.Iterator;
import java.util.Vector;

import enterpriseTest.SupplyProposal;
import up.fe.liacc.sajas.core.AID;
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


	public BuyBehaviour(Agent agent) {
		super(agent, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void handleAllResponses(Vector responses, Vector acceptances) {
		try {
			int bestPrice = Integer.MAX_VALUE;
			AID bestSeller = null;
			
			
			// Get lowest price FIXME: use trust
			for (Iterator iterator = responses.iterator(); iterator.hasNext();) {
				ACLMessage message = (ACLMessage) iterator.next();
				SupplyProposal proposal;

				proposal = (SupplyProposal) (message).getContentObject();
				if (proposal.myPrice < bestPrice) {
					bestSeller = message.getSender();
					bestPrice = proposal.myPrice;
				}
			}
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	protected void handlePropose(ACLMessage m, @SuppressWarnings("rawtypes") Vector acceptances) {
		try {
			SupplyProposal proposal = (SupplyProposal) m.getContentObject();
			System.out.println("\u25A1 B " + myAgent.getLocalName()
					+ "] " + m.getSender()
					+ " proposes " + proposal.myPrice
					+ " for " + demand + " of " + " product.");

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
