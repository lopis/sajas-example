package enterpriseTest;

import java.util.Random;

import up.fe.liacc.repast.Launcher;

public class MyLauncher extends Launcher {

	/**
	 * The number of agents.
	 */
	static final int NUMBER_OF_AGENTS = 20;
	String[] products = {EnterpriseAgent.OATS, EnterpriseAgent.RICE, EnterpriseAgent.WHEAT};
	Random r = new Random();

	public void setup() {
		// CREATE AGENTS
		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {
			EnterpriseAgent agent = new EnterpriseAgent("Agent_" + i);
			prepareAgent(agent);
			acceptNewAgent("Agent_" + i, agent);
		}
	}

	private void prepareAgent(EnterpriseAgent agent) {
		
		// SETUP SELLS
		for (int i = 0; i < products.length; i++) {
			if (r.nextDouble() < 0.3) {
				agent.addSell(products[i], r.nextInt(100));
			}
		}
		
		// SETUP BUYS
		for (int i = 0; i < products.length; i++) {
			if (r.nextDouble() > 0.3) {
				agent.addBuy(products[i], r.nextInt(100));
			}
		}
	}
	
}
