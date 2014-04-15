package contractNetTest;

import up.fe.liacc.repacl.Agent;
import up.fe.liacc.repacl.acl.ACLMessage;
import up.fe.liacc.repacl.acl.Performative;
import up.fe.liacc.repacl.acl.Protocol;
import up.fe.liacc.repacl.proto.ContractNetResponder;

public class SupplyNetResponder extends ContractNetResponder {

	public SupplyNetResponder(Agent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}
	
	private SupplierAgent getAgent() {
		return (SupplierAgent) getOwner();
	}
	
	@Override
	protected void handleCfp(ACLMessage m) {
		SupplyRequest sr = (SupplyRequest) m.getContent();
		
		int myPrice = getAgent().getPrice(sr);
		
		if (sr.unitsNeeded_flour <= getAgent().getFlourSupply()
			&& sr.unitsNeeded_oats <= getAgent().getOatsSupply()
			&& sr.unitsNeeded_rice <= getAgent().getRiceSupply()
			&& sr.maxPrice <= myPrice) {
			
			ACLMessage proposal = new ACLMessage(Performative.PROPOSE);
			proposal.setProtocol(Protocol.FIPA_CONTRACT_NET);
			proposal.setSender(getOwner());
			proposal.addReceiver(m.getSender());
			proposal.setContent(myPrice);
		}
	}

}
