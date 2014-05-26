package enterpriseTest.proto;

import java.util.HashMap;

import enterpriseTest.SupplyRequest;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.behaviours.Behaviour;
import up.fe.liacc.sajas.core.behaviours.SimpleBehaviour;
import up.fe.liacc.sajas.domain.FIPANames;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.MessageTemplate;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
import up.fe.liacc.sajas.proto.SSResponderDispatcher;

public class SellBehaviour extends SSResponderDispatcher {

	private HashMap<String, Integer> myPrices = new HashMap<String, Integer>(); // The key is the product

	public SellBehaviour(Agent agent, HashMap<String, Integer> prices) {
		super(agent, createTemplate());
		this.myPrices = prices;
	}
	
	public void sellProduct(int price, String product) {
		myPrices.put(product, price);
	}
	
	private static MessageTemplate createTemplate() {
		MessageTemplate template = new MessageTemplate();
		template.addPerformative(ACLMessage.CFP);
		template.addProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
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
			};
		}
		
	}

}
