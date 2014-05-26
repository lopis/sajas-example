package enterpriseTest.proto;

import java.io.IOException;

import enterpriseTest.SupplyProposal;
import enterpriseTest.SupplyRequest;
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

	public SellResponder(Agent a, ACLMessage cfp, String product, int myPrice) {
		super(a, cfp);
	}

	@Override
	protected ACLMessage handleCfp(ACLMessage m) {
		try {
			SupplyRequest request = (SupplyRequest) m.getContentObject();
			if (request == null) {
				System.err.println("Supply request is null.");
			}
			System.out.println("[S " + myAgent.getLocalName()
					+ "] Propose for " + request.getAmount() 
					+ " of " + request.getProduct() 
					+ ": " + myPrice);
			
			return createProposal(m, request);
		} catch (UnreadableException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	private ACLMessage createProposal(ACLMessage m, SupplyRequest request) {
		ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
		proposal.setSender(myAgent.getAID());
		proposal.addReceiver(m.getSender());
		proposal.setProtocol(m.getProtocol());
		try {
			proposal.setContentObject(new SupplyProposal(request.getProduct(), request.getAmount(), myPrice));
			return proposal;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
