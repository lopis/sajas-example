package enterpriseTest;

import java.io.Serializable;

public class SupplyRequest implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3653447044975295919L;
	private int amount;
	private String product;	


	public SupplyRequest(String product, int amount) {
		this.setProduct(product);
		this.setAmount(amount);
	}


	public String getProduct() {
		return product;
	}


	public void setProduct(String product) {
		this.product = product;
	}


	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "(SuplyRequest product:" + product
				+ " amount:" + amount + ")";
	}
}
