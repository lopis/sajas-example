package contractNetTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import contractNetTest.repast.Launcher;

public class MyLauncher extends Launcher {

	public void setup() {		

		try {
			File pricesFile = new File("prices.dat");
			File suppliesFile = new File("supplies.dat");
			Scanner prices = new Scanner(pricesFile);
			Scanner supplies = new Scanner(suppliesFile);
			prices.useDelimiter(",");
			supplies.useDelimiter(",");
			
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
			}

			// Create contract net initiators
			BuyerAgent ba = new BuyerAgent();
			acceptNewAgent("Buyer", ba).start();

			prices.close();
			supplies.close();
			
		} catch (NumberFormatException e) {
			System.err.println("Number Format Exception while reading file!");
		} catch (FileNotFoundException e) {
			System.err.println("Data files not found!");
		}
		
	}
}
