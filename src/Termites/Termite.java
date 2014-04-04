/**
 * 
 */
package Termites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
//import up.fe.liacc.repacl.Agent;


/**
 * Termite
 * @author grace
 *
 */
public class Termite { //extends Agent{

//	// turn on/off debug info
//	private boolean DEBUG = true; 
//	
//	public enum TermiteState {NO_CHIP, HAS_CHIP};
//	private TermiteState state;
//	private ContinuousSpace <Object> space;
//	private Grid <Object> grid;
//	
//	public Termite(ContinuousSpace<Object> space, 
//			Grid<Object> grid) {
//		this.space = space;
//		this.grid = grid;
//		this.state = TermiteState.NO_CHIP;
//	}
//	
//	
//	// Find empty sites at north, south, east, and west only
//	private List<GridPoint> findEmptySites(int xc, int yc) {
//		GridDimensions dims = grid.getDimensions();
//		int height = dims.getHeight();
//		int width = dims.getWidth();
//		
//		List<GridPoint> emptySites = new ArrayList<GridPoint>();
//		if (yc+1<height && !grid.getObjectsAt(xc, yc+1).iterator().hasNext()) // north
//			emptySites.add(new GridPoint(xc,yc+1));
//		if (xc+1<width && !grid.getObjectsAt(xc+1, yc).iterator().hasNext()) // east
//			emptySites.add(new GridPoint(xc+1,yc));
//		if (yc-1>=0 && !grid.getObjectsAt(xc, yc-1).iterator().hasNext()) // south
//			emptySites.add(new GridPoint(xc,yc-1));
//		if (xc-1>=0 && !grid.getObjectsAt(xc-1, yc).iterator().hasNext()) // west
//			emptySites.add(new GridPoint(xc-1,yc));
//		Collections.shuffle(emptySites);
//		return emptySites;
//	}
//	
//	// Find empty sites in all 8 directions
//	// An option to count chip as an empty site (True if we want to count chip
//	// as an empty site)
//	private List<GridPoint> findEmptySites2(boolean includeChip, int xc, int yc){
//		List<GridPoint> emptySites = new ArrayList<GridPoint>();
//		
//		GridDimensions dims = grid.getDimensions();
//		int height = dims.getHeight();
//		int width = dims.getWidth();
//		
//		// Find empty Moore neighbors
//		for (int x=xc-1; x<=xc+1; x++) {
//			for (int y=yc-1; y<=yc+1; y++) {
//				if (x==xc && y==yc)
//					continue;
//				if (x>=0 && x<width && y>=0 && y<height) {
//					if (!grid.getObjectsAt(x,y).iterator().hasNext())
//						emptySites.add(new GridPoint(x,y));
//					else if (includeChip && (grid.getObjectAt(x,y) instanceof Chip))
//						emptySites.add(new GridPoint(x,y));
//				}
//			}
//		}
//		
//		Collections.shuffle(emptySites);
//		return emptySites;
//	}
//	
//	// Drop chip near the location of the object located at xobj and yobj
//	private void dropChip(int xobj, int yobj) {
//		// find empty sites north/east/south/west
//		List<GridPoint> emptySitesObj = findEmptySites(xobj, yobj); 
//		// create a chip
//		Chip c = new Chip(this.space, this.grid);
//		// add to context
//		Context<Object> context = ContextUtils.getContext(this);
//		context.add(c);
//
//		boolean moved = false;
//		
//		// drop chip at one of the empty sites
//		for (int i=0; i<emptySitesObj.size(); i++) {
//			int xdrop = emptySitesObj.get(i).getX();
//			int ydrop = emptySitesObj.get(i).getY();
//			if (this.grid.moveTo(c, xdrop, ydrop)==true) {
//				if (DEBUG)
//					System.out.println("Termite's chip dropped at "+xdrop+" "+ydrop);
//				this.state = TermiteState.NO_CHIP;
//				moved = true;
//			}
//		}
//		// if couldn't drop it anywhere, then remove new chip that was created
//		// (assume termite still carries it).
//		if (!moved) {
//			if (DEBUG)
//				System.out.println("Termite did not drop chip");
//			context.remove(c);
//		}
//	}
	
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		System.out.println("Ama termite!");
//		GridPoint thisPt = grid.getLocation(this);
//		int xc = thisPt.getX();
//		int yc = thisPt.getY();
//		
//		// find empty locations
//		// first with chips as empty sites, second without chip as empty sites
//		List<GridPoint> emptyAndChipSites = findEmptySites2(true, xc, yc);
//		List<GridPoint> emptySites = findEmptySites2(false, xc, yc);
//
//		// since the empty sites were randomized, just pick the first one
//		// on the list as the site to move to
//		int xobj = emptyAndChipSites.get(0).getX();
//		int yobj = emptyAndChipSites.get(0).getY();		
//		Object obj = this.grid.getObjectAt(xobj, yobj);
//
//		Context<Object> context = ContextUtils.getContext(obj);
//		
//		// if the "empty" site contains a chip, and
//		//     if this termite is already carring chip, then drop the chip near it
//		//     otherwise, pick up the chip by
//		//			1. set its state to HAS_CHIP, and
//		//          2. remove chip from context, and
//		//			3. move the termite to picked up chip
//		if (obj instanceof Chip) { // found chip
//			if (DEBUG)
//				System.out.println("Termite at "+xc+" "+yc+" found a chip at "+xobj+" "+yobj);
//			if (this.state==TermiteState.HAS_CHIP) {
//				// this termite already carries chip... drop it nearby
//				if (DEBUG)
//					System.out.println("Termite carries a chip and needs to drop it");
//				dropChip(xobj, yobj);
//			} else { 
//				// this termite not carrying any chip
//				if (DEBUG)
//					System.out.println("Termite not carrying chip");
//				// remove chip from context and set termite state to HAS_CHIP
//				if (context.remove(obj)==true) { // removed successfully
//					this.state = TermiteState.HAS_CHIP; // termite now has chip
//					// move termite to chip location
//					if (grid.moveTo(this, xobj, yobj)==true) {
//						if (DEBUG)
//							System.out.println("Termite picks up chip and move to "+xobj+" "+yobj);
//					} else
//						if (DEBUG)
//							System.out.println("Termite picks up chip but did not move to "+xobj+" "+yobj);
//				}
//			}
//		} else { // no chip found; just move to empty site
//			if (DEBUG)
//				System.out.println("Termite at "+thisPt.getX()+" "+thisPt.getY()+" found no chip");
//			if (grid.moveTo(this, xobj, yobj)==true) { // move this termite
//				if (DEBUG)
//					System.out.println("Termite moved to "+xobj+" "+yobj);
//			}
//		}
	}
	
	public String toString() {
//		GridPoint pt = grid.getLocation(this);
		return "Termite :)";
	}
}
