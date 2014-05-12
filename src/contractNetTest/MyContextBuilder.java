package contractNetTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import contractNetTest.repast.RepastAgent;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;

public class MyContextBuilder extends contractNetTest.repast.SAJaSContextBuilder {

	@Override
	public Context<Object> build(Context<Object> context) {

		
		context.setId("termites");		

		try {
			File pricesFile = new File("prices.dat");
			File suppliesFile = new File("supplies.dat");
			Scanner prices = new Scanner(pricesFile);
			Scanner supplies = new Scanner(suppliesFile);
			prices.useDelimiter(",");
			supplies.useDelimiter(",");

			RepastAgent.setContext(context, RunEnvironment.getInstance().getCurrentSchedule());
			
			// Create contract net responders
			for (int i = 0; i < 1000; i++) {
				SupplierAgent sa = new SupplierAgent(
						Integer.valueOf(supplies.next()),
						Integer.valueOf(prices.next()),
						Integer.valueOf(supplies.next()),
						Integer.valueOf(prices.next()),
						Integer.valueOf(supplies.next()),
						Integer.valueOf(prices.next()));
				acceptNewAgent("Supplier" + i, sa).start();
				//context.add(sa);
			}

			// Create contract net initiators
			BuyerAgent ba = new BuyerAgent();
			acceptNewAgent("Buyer", ba).start();
			context.add(ba);

			prices.close();
			supplies.close();

			return context;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
