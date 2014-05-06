/**
 * 
 */
package Termites;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * TermiteContextBuilder
 * @author grace
 *
 */
public class TermiteContextBuilder implements ContextBuilder<Object> {

	
	@Override
	public Context build(Context<Object> context) {
		context.setId("termites");
		
		// get environment variables from GUI
		Parameters p = RunEnvironment.getInstance().getParameters();
		int gridWidth = (Integer)p.getValue("gridWidth");
		int gridHeight = (Integer)p.getValue("gridHeight");
		int initialTermites = (Integer)p.getValue("initialTermites");
		int initialChips = (Integer)p.getValue("initialChips");
		
		// create space
		ContinuousSpaceFactory spaceFactory =
			ContinuousSpaceFactoryFinder . createContinuousSpaceFactory ( null );
		
		ContinuousSpace<Object> space = spaceFactory
				.createContinuousSpace(
						"Space",
						context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.WrapAroundBorders(),
						gridWidth, gridHeight);
		
		// create grid
		Grid<Object> grid = GridFactoryFinder.createGridFactory(null).createGrid("Grid", 
			context, new GridBuilderParameters<Object>(new WrapAroundBorders(), 
				new RandomGridAdder<Object>(), false, gridWidth, gridHeight));

		// create termites
		for (int i = 0; i < initialTermites; i++) {
			Termite t = new Termite(space, grid);
			
			ScheduleParameters params = ScheduleParameters.createRepeating(1, 1);
			Schedule schedule = new Schedule();
			schedule.schedule(params, t, "step");
			context.add(t);
			
		}
		
		// create chips
		for (int i = 0; i < initialChips; i++) {
			Chip c = new Chip(space, grid);
			context.add(c);
		}
		
		// move agents to grid that corresponds to their ContinuousSpace location
		int count = 0;
		for (Object obj : context) {
			GridPoint pt = grid.getLocation(obj);
			grid.moveTo(obj, pt.getX(), pt.getY());
			if (obj instanceof Termite) {
				System.out.println("Termite: "+pt.getX()+" "+pt.getY());
			} else {
				System.out.println("Chip: "+pt.getX()+" "+pt.getY());
			}
			count++;
		}
		System.out.println("Total count is "+count);
		
		return context;
	}

}