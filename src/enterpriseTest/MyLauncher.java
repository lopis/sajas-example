package enterpriseTest;

import java.util.Properties;
import java.util.Random;

import contractNetTest.BuyerAgent;
import enterpriseTest.agent.CTAgent;
import enterpriseTest.agent.EnterpriseAgent;
import enterpriseTest.agent.SellerAgent;
import up.fe.liacc.repast.Launcher;

public class MyLauncher extends Launcher {

	/**
	 * The number of agents.
	 */
	static final int NUMBER_OF_BUYERS 	= 5;
	static final int NUMBER_OF_SELLERS 	= 5;
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
			SellerAgent agent = new SellerAgent("Agent_" + i, prices);
			acceptNewAgent("Agent_" + i, agent);
		}
		
		// CREATE BUYER AGENTS
		for (int i = 0; i < NUMBER_OF_BUYERS; i++) {
			BuyerAgent agent = new BuyerAgent();
			acceptNewAgent("Agent_" + i, agent);
		}
	}
	
}
