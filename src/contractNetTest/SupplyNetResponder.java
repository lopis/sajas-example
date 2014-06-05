package contractNetTest;

import java.io.IOException;

import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.ContractNetResponder;

public class SupplyNetResponder extends ContractNetResponder {

	public SupplyNetResponder(Agent agent) {
		super(agent, null);
	}
	
	@Override
	protected ACLMessage handleCfp(ACLMessage m) {
		SupplyRequest sr;
		int myPrice;
		SupplierAgent owner;
		
		try {
			sr = (SupplyRequest) m.getContentObject();
			owner = (SupplierAgent) getAgent();
			myPrice = owner.getPrice(sr);
		} catch (UnreadableException e1) {
			System.err.println("Failed to read ACLMessage content.");
			return null;
		}
		
		if (sr.unitsNeeded_flour <= owner.getFlourSupply()
			&& sr.unitsNeeded_oats <= owner.getOatsSupply()
			&& sr.unitsNeeded_rice <= owner.getRiceSupply()) {
			
			ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
			proposal.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			proposal.setSender(getAgent().getAID());
			proposal.addReceiver(m.getSender());
			try {
				proposal.setContentObject(new SupplyProposal(myPrice));
			} catch (IOException e) { /*never fails*/ }
			//System.out.println("Got CFP. My Proposal: " + myPrice);
			return proposal;
		} else {
			ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
			refuse.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			refuse.setSender(getAgent().getAID());
			refuse.addReceiver(m.getSender());
			return refuse;
		}
	}

}
