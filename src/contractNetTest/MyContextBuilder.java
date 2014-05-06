package contractNetTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;

public class MyContextBuilder implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		
		context.setId("termites");
		ContextWrapper.setContext(context);
		

		try {
			File pricesFile = new File("prices.dat");
			File suppliesFile = new File("supplies.dat");
			Scanner prices = new Scanner(pricesFile);
			Scanner supplies = new Scanner(suppliesFile);
			prices.useDelimiter(",");
			supplies.useDelimiter(",");


			// Create contract net responders
			for (int i = 0; i < 500; i++) {
				new SupplierAgent(
						Integer.valueOf(supplies.next()),
						Integer.valueOf(prices.next()),
						Integer.valueOf(supplies.next()),
						Integer.valueOf(prices.next()),
						Integer.valueOf(supplies.next()),
						Integer.valueOf(prices.next()));
				
			}

			// Create contract net initiators
			for (int i = 0; i < 1; i++) {
				new BuyerAgent();
			}

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
