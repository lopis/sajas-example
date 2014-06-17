package enterpriseTest.proto;

import java.io.IOException;
import java.util.Random;

import enterpriseTest.agent.SellerAgent;
import enterpriseTest.model.SupplyProposal;
import enterpriseTest.model.SupplyRequest;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.SSContractNetResponder;

/**
 * This is a single session contract net responder.
 * This behaviour has 
 * @author joaolopes
 *
 */
public class SellResponder extends SSContractNetResponder {

	private int myPrice;
	private double trust; // Odds of 
	private static Random rand = new Random();;

	public SellResponder(Agent a, ACLMessage cfp, String product, int myPrice) {
		super(a, cfp);

		this.myPrice = myPrice;
		this.trust = ((SellerAgent) myAgent).trust;
	}

	@Override
	protected ACLMessage handleCfp(ACLMessage m) {
		try {
			SupplyRequest request = (SupplyRequest) m.getContentObject();
			if (request == null) {
				System.err.println("Supply request is null.");
			}
//			System.out.println("[" + myAgent.getLocalName()
//					+ "] Propose to " + m.getSender().getLocalName()
//					+ " for " + request.getAmount() 
//					+ " of " + request.getProduct() 
//					+ ": " + (myPrice * request.getAmount()) + "$" );

			return createProposal(m, request);
		} catch (UnreadableException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,
			ACLMessage accept) {
//		System.out.println("[" + myAgent.getLocalName()
//				+ "]\tMy proposal to " + cfp.getSender().getLocalName() + " was accepted.");

		//myAgent.removeBehaviour(this);
		// Prepare INFORM message. 
		ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
		reply.setContent(getFullfield());
		reply.addReceiver(cfp.getSender());
		reply.setProtocol(cfp.getProtocol());
		reply.setConversationId(cfp.getConversationId());
		return reply;
	}

//	@Override
//	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose,
//			ACLMessage reject) {
//		System.out.println("[" + myAgent.getLocalName() + "]\tMy proposal was rejected.");
//	}

	private String getFullfield() {
	
		double d = rand.nextDouble() * 0.5 +  trust * 0.5;
		if (d > 0.60) {
			return "FULLFIELD";
		} else if (d < 0.25) {
			return "VIOLATED";
		} else {
			return "DELAYED";
		}
	}

	private ACLMessage createProposal(ACLMessage m, SupplyRequest request) {
		ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
		proposal.setSender(myAgent.getAID());
		proposal.addReceiver(m.getSender());
		proposal.setProtocol(m.getProtocol());
		proposal.setConversationId(m.getConversationId());
		try {
			proposal.setContentObject(new SupplyProposal(request.getProduct(), request.getAmount(), myPrice));
			return proposal;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
