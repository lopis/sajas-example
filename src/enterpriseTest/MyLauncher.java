package enterpriseTest;

import java.util.Properties;
import java.util.Random;

import enterpriseTest.agent.BuyerAgent;
import enterpriseTest.agent.CTAgent;
import enterpriseTest.agent.EnterpriseAgent;
import enterpriseTest.agent.SellerAgent;
import up.fe.liacc.repast.Launcher;

public class MyLauncher extends Launcher {

	/**
	 * The number of agents.
	 */
	static final int NUMBER_OF_BUYERS_W_TRUST 	= 20; // AGents using trust
	static final int NUMBER_OF_BUYERS_N_TRUST 	= 20; // Agents with no use of trust
	static final int NUMBER_OF_SELLERS 	= 80;
	String[] products = {EnterpriseAgent.OATS, EnterpriseAgent.RICE, EnterpriseAgent.WHEAT};
	Random r = new Random();


	public void setup() {
		Properties properties = System.getProperties();
		properties.containsKey("debug");

		// CREATE COMP. TRUST AGENT
		acceptNewAgent("CTAgent", new CTAgent());

		// CREATE SELLER AGENTS
		Random r = new Random();
		for (int i = 0; i < NUMBER_OF_SELLERS; i++) {
			int price = (int) Math.round(100 * r.nextDouble());
			int[] prices = {price, price, price};
			//int price = r.nextInt(100);
			String name = String.format("Seller_%02d", i);
			SellerAgent agent = new SellerAgent(products, prices, (1.0*i)/NUMBER_OF_SELLERS);
			acceptNewAgent(name, agent);
		}

		// CREATE BUYER AGENTS WHO USE TRUST
		for (int i = 0; i < NUMBER_OF_BUYERS_W_TRUST; i++) {
			String name = String.format("Buyer_T%02d", i);
			BuyerAgent agent = new BuyerAgent(products[r.nextInt(products.length)], r.nextInt(100), true);
			acceptNewAgent(name, agent);
		}

		// CREATE BUYER AGENTS WHO DON'T USE TRUST
		for (int i = 0; i < NUMBER_OF_BUYERS_N_TRUST; i++) {
			String name = String.format("Buyer_N%02d", i);
			BuyerAgent agent = new BuyerAgent(products[r.nextInt(products.length)], r.nextInt(100), false);
			acceptNewAgent(name, agent);
		}
	}

}
