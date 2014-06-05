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
	static final int NUMBER_OF_BUYERS_W_TRUST 	= 1;
	static final int NUMBER_OF_BUYERS_WO_TRUST 	= 0;
	static final int NUMBER_OF_SELLERS 	= 20;
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
			int[] prices = {r.nextInt(100), r.nextInt(100), r.nextInt(100)};
			//int price = r.nextInt(100);
			String name = String.format("Seller_%02d", i);
			SellerAgent agent = new SellerAgent(products, prices);
			acceptNewAgent(name, agent);
		}

		// CREATE BUYER AGENTS WHO USE TRUST
		for (int i = 0; i < NUMBER_OF_BUYERS_W_TRUST; i++) {
			String name = String.format("Buyer_1%1d", i);
			BuyerAgent agent = new BuyerAgent(products[r.nextInt(products.length)], r.nextInt(100), true);
			acceptNewAgent(name, agent);
		}

		// CREATE BUYER AGENTS WHO DON'T USE TRUST
		for (int i = 0; i < NUMBER_OF_BUYERS_WO_TRUST; i++) {
			String name = String.format("Buyer_0%1d", i);
			BuyerAgent agent = new BuyerAgent(products[r.nextInt(products.length)], r.nextInt(100), false);
			acceptNewAgent(name, agent);
		}
	}

}
