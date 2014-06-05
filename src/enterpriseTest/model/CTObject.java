package enterpriseTest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import up.fe.liacc.sajas.core.AID;

/**
 * Payload of a REQUEST/INFORM to/from the
 * Computational Trust service. Contains an arrray of AIDs and,
 * when inside an INFORM, their respective trust values.
 * @author joaolopes
 *
 */
public class CTObject implements Serializable {

	private static final long serialVersionUID = -2745180962768235071L;
	public int maxAgents;
	public ArrayList<Pair<AID>> trustValues = new ArrayList<Pair<AID>>();
	
	/**
	 * 
	 * @param array
	 * @param maxAgents
	 */
	public CTObject(ArrayList<AID> array, int maxAgents) {
		if (maxAgents > array.size()) {
			this.maxAgents = array.size();
		} else {
			this.maxAgents = maxAgents;
		}
		
		if(array!=null) {
			addAll(array);
		}
	}
	
	private void addAll(ArrayList<AID> list) {
		for (Iterator<AID> iterator = list.iterator(); iterator.hasNext();) {
			AID aid = (AID) iterator.next();
			trustValues.add(new Pair<AID>(aid, 0.0));
		}
	}

	/**
	 * Sorts and retains the ``maxAgents`` number of agents.
	 */
	public void sortMax() {
		Collections.sort(trustValues);
		trustValues = new ArrayList<Pair<AID>>(trustValues.subList(0, maxAgents));
	}
}
