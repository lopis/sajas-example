package enterpriseTest.proto;

import java.util.HashMap;

import enterpriseTest.model.SupplyRequest;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.Behaviour;
import up.fe.liacc.sajas.core.behaviours.SimpleBehaviour;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.MessageTemplate;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.SSResponderDispatcher;

public class SellDispatcher extends SSResponderDispatcher {

	private HashMap<String, Integer> myPrices = new HashMap<String, Integer>(); // The key is the product

	public SellDispatcher(Agent agent, HashMap<String, Integer> prices) {
		
		super(agent, createTemplate());
		
		myPrices = prices;
		
//		for (Iterator<String> iterator = myPrices.keySet().iterator(); iterator.hasNext();) {
//			String prod = iterator.next();
//			System.out.println( prod +"  " + myPrices.get(prod) + "§");
//		}
	}
	
	private static MessageTemplate createTemplate() {
		MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		return template;
	}

	@Override
	/**
	 * Returns a behaviour that handles the response
	 * to the contract net. This contract implements the action()
	 * method which handles messages with a given conversation-id;
	 */
	protected Behaviour createResponder(ACLMessage cfp) {
		
		String product;
		try {
			product = ((SupplyRequest) cfp.getContentObject()).getProduct();
			return new SellResponder(myAgent, cfp, product , myPrices.get(product));
		} catch (UnreadableException e) {
			return new SimpleBehaviour() {
				@Override
				public void action() {}

				@Override
				public boolean done() {return true;}
			};
		}
	}

}
