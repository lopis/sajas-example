package contractNetTest;

import up.fe.liacc.repacl.Agent;
import up.fe.liacc.repacl.acl.ACLMessage;
import up.fe.liacc.repacl.acl.Performative;

/**
 * Let's model an agent that needs to buy some rice, flour and oats.
 * The agent has a certain need for each product and a maximum
 * price it's willing to pay.
 * @author joaolopes
 *
 */
public class BuyerAgent extends Agent{

	private int unitsNeeded_rice = 100;
	private int unitsNeeded_flour = 100;
	private int unitsNeeded_oats = 100;
	
	private int maximumPrice = 20;

	public BuyerAgent() {
		super();
	}
	
	@Override
	public void setup() {
		ACLMessage cfp = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
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
