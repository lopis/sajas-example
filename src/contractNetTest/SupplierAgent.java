package contractNetTest;

import repast.RepastAgent;


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
		super(); // Registers agent in the DF
		
		this.riceSupply = riceSupply;
		this.ricePrice = ricePrice;

		this.flourSupply = flourSupply;
		this.flourPrice = flourPrice;

		this.oatsSupply = oatsSupply;
		this.oatsPrice = oatsPrice;

	}
	
	@Override
	public void setup() {
		addBehavior(new SupplyNetResponder(this));
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
