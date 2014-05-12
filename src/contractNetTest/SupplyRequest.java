package contractNetTest;

import java.io.Serializable;

public class SupplyRequest implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3653447044975295919L;
	public int unitsNeeded_rice;
	public int unitsNeeded_flour;
	public int unitsNeeded_oats;
	
	
	public SupplyRequest(int rice, int flour, int oats) {
		this.unitsNeeded_rice = rice;
		this.unitsNeeded_flour = flour;
		this.unitsNeeded_oats = oats;
	}

}
