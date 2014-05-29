package enterpriseTest.model;

import java.io.Serializable;



public class SupplyProposal implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3902843741931288840L;
	public String product;
	public int myPrice;

	public SupplyProposal(String product, int amount, int unitPrice) {
		this.product = product;
		this.myPrice = amount * unitPrice;
	}

	@Override
	public String toString() {
		return "(SuplyProposal product:" + product
				+ " price:" + myPrice + ")";
	}

}
