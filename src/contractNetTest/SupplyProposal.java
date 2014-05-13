package contractNetTest;

import java.io.Serializable;



public class SupplyProposal implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3902843741931288840L;
	public int myPrice;

	public SupplyProposal(int myPrice) {
		this.myPrice = myPrice;
	}

}
