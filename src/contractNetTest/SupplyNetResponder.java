package contractNetTest;

import repast.simphony.engine.schedule.ScheduledMethod;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.proto.ContractNetResponder;

public class SupplyNetResponder extends ContractNetResponder {

	public SupplyNetResponder(Agent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}
	
//	@Override
//	@ScheduledMethod(start=1, interval=0.0001)
//	public void action() {
//		super.action();
//	}
	
	@Override
	protected ACLMessage handleCfp(ACLMessage m) {
		SupplyRequest sr = (SupplyRequest) m.getContentObject();
		SupplierAgent owner = (SupplierAgent) getAgent();
		int myPrice = owner.getPrice(sr);
		
		if (sr.unitsNeeded_flour <= owner.getFlourSupply()
			&& sr.unitsNeeded_oats <= owner.getOatsSupply()
			&& sr.unitsNeeded_rice <= owner.getRiceSupply()) {
			
			ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
			proposal.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			proposal.setSender(getAgent().getAID());
			proposal.addReceiver(m.getSender());
			proposal.setContentObject(myPrice);
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
