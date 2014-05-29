package enterpriseTest.model;

import java.util.ArrayList;
import java.util.Iterator;

import enterpriseTest.Contract;

/**
 * Utility class that implements the sinAlphaModel to
 * calculate computation trust of an agent based
 * on a record of its past contracts. 
 * @author joaolopes
 *
 */
public class SinAlphaModel {
	
	/**
	 * TODO this is not sinalpha
	 * @param contracts
	 * @return
	 */
	public static double getTrust(ArrayList<Contract> contracts) {
		double trust = 1.0;
		for (Iterator<Contract> iterator = contracts.iterator(); iterator.hasNext();) {
			Contract contract = (Contract) iterator.next();
			switch (contract.getRespFullfielment()) {
			case Contract.FULLFIELD:
				trust = trust * 1.01; // gain 1% of trust for fullfielment
				break;
			case Contract.VIOLATED:
				trust = trust * 0.9; // lose 10% of trust for each fault
				break;
			case Contract.DELAYED:
				trust = trust * 0.99; // lose 1% of trust for delayed
				break;
			default:
				break; 
			}
		}
		return trust;
	}
}
