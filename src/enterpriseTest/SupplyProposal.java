package enterpriseTest;

import java.io.Serializable;



public class SupplyProposal implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3902843741931288840L;
	public String product;
	public int amount;
	public int myPrice;

	public SupplyProposal(String product, int amount, int myPrice) {
		this.product = product;
		this.amount = amount;
		this.myPrice = myPrice;
	}

}
