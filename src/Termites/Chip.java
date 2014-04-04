/**
 * 
 */
package Termites;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * Chip
 * @author grace
 *
 */
public class Chip {
	//public enum ChipState {PICKED_UP, DROPPED};
	private Grid <Object> grid;
	
	public Chip(ContinuousSpace<Object> space, 
			Grid<Object> grid) {
		this.grid = grid;
	}
	
	public String toString() {
		GridPoint pt = grid.getLocation(this);
		return "Chip at location "+pt.getX()+" "+pt.getY();
	}


}
