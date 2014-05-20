package contractNetTest;

import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;



/**
 * This is a model of a supplier agent. He can sell
 * rice, flour and oats. He has a limited supply
 * capability of each product. He refuses to send
 * price proposes when the demand can't be met.
 * @author joaolopes
 *
 */
public class SupplierAgent extends RepastAgent{

	private int riceSupply;
	private int ricePrice;
	private int flourSupply;
	private int oatsPrice;
	private int oatsSupply;
	private int flourPrice;

	public SupplierAgent(int riceSupply, int ricePrice,
			int flourSupply, int flourPrice,
			int oatsSupply, int oatsPrice) {
		
		this.riceSupply = riceSupply;
		this.ricePrice = ricePrice;

		this.flourSupply = flourSupply;
		this.flourPrice = flourPrice;

		this.oatsSupply = oatsSupply;
		this.oatsPrice = oatsPrice;

	}
	
	@Override
	public void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addProtocols(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		ServiceDescription sd = new ServiceDescription();
		sd.setName("supplier");
		sd.setType("supplier");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(e.getMessage());
		}
		addBehaviour(new SupplyNetResponder(this));
	}
	
	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			System.err.println(e.getMessage());
			System.err.println("[DF] Failed to deregister agent.");
			//e.printStackTrace();
		}
	}
	
	public int getRiceSupply() {
		return riceSupply;
	}

	public void setRiceSupply(int riceSupply) {
		this.riceSupply = riceSupply;
	}

	public int getRicePrice() {
		return ricePrice;
	}

	public void setRicePrice(int ricePrice) {
		this.ricePrice = ricePrice;
	}

	public int getFlourSupply() {
		return flourSupply;
	}

	public void setFlourSupply(int flourSupply) {
		this.flourSupply = flourSupply;
	}

	public int getOatsPrice() {
		return oatsPrice;
	}

	public void setOatsPrice(int oatsPrice) {
		this.oatsPrice = oatsPrice;
	}

	public int getOatsSupply() {
		return oatsSupply;
	}

	public void setOatsSupply(int oatsSupply) {
		this.oatsSupply = oatsSupply;
	}

	public int getFlourPrice() {
		return flourPrice;
	}

	public void setFlourPrice(int flourPrice) {
		this.flourPrice = flourPrice;
	}

	public int getPrice(SupplyRequest sr) {
		return sr.unitsNeeded_flour * flourPrice +
				sr.unitsNeeded_oats * oatsPrice + 
				sr.unitsNeeded_rice * ricePrice;
	}


}
