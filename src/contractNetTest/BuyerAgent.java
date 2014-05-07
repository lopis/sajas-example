package contractNetTest;

import java.util.Collection;
import java.util.Iterator;

import contractNetTest.repast.RepastAgent;
import up.fe.liacc.repacl.core.Agent;
import up.fe.liacc.repacl.domain.DFService;
import up.fe.liacc.repacl.domain.FIPANames;
import up.fe.liacc.repacl.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.repacl.lang.acl.ACLMessage;

/**
 * Let's model an agent that needs to buy some rice, flour and oats.
 * The agent has a certain need for each product and a maximum
 * price it's willing to pay.
 * @author joaolopes
 *
 */
public class BuyerAgent extends RepastAgent {

	private int unitsNeeded_rice;
	private int unitsNeeded_flour;
	private int unitsNeeded_oats;
	
	private int maximumPrice = 3500;
	
	@Override
	public void setup() {
		
		unitsNeeded_rice = 100;
		unitsNeeded_flour = 100;
		unitsNeeded_oats = 100;
		
		ACLMessage cfp = new ACLMessage(ACLMessage.CALL_FOR_PROPOSAL);
		cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		cfp.setSender(this.getAID());
		SupplyRequest supplyRequest = new SupplyRequest(unitsNeeded_rice,
				unitsNeeded_flour, unitsNeeded_oats);
		cfp.setContentObject(supplyRequest );
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addService("supplier");
		DFAgentDescription[] agents = DFService.search(this, dfd);
		System.out.println("Found " + agents.length + " agents in the DF");
		for (int i = 0; i < agents.length; i++) {
			cfp.addReceiver(agents[i].getName());
		}
		addBehavior(new SupplyNetInitiator(this, cfp));
	}

	public int getMaximumPrice() {
		return maximumPrice;
	}
	
	public int getUnitsNeeded_rice() {
		return unitsNeeded_rice;
	}

	public void setUnitsNeeded_rice(int unitsNeeded_rice) {
		this.unitsNeeded_rice = unitsNeeded_rice;
	}

	public int getUnitsNeeded_flour() {
		return unitsNeeded_flour;
	}

	public void setUnitsNeeded_flour(int unitsNeeded_flour) {
		this.unitsNeeded_flour = unitsNeeded_flour;
	}

	public int getUnitsNeeded_oats() {
		return unitsNeeded_oats;
	}

	public void setUnitsNeeded_oats(int unitsNeeded_oats) {
		this.unitsNeeded_oats = unitsNeeded_oats;
	}
}
