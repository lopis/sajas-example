package enterpriseTest.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import enterpriseTest.Contract;
import enterpriseTest.model.CTObject;
import enterpriseTest.model.Pair;
import enterpriseTest.model.SinAlphaModel;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.MessageTemplate;
import up.fe.liacc.sajas.proto.AchieveREResponder;

/**
 * Computational trust agent
 * @author joaolopes
 *
 */
public class CTAgent extends RepastAgent {

	/**
	 * Contains up-to-date values of trust from some or all agents
	 */
	HashMap<AID, Double> trustChache = new HashMap<AID, Double>();
	HashMap<AID, ArrayList<Contract>> contracts = new HashMap<AID, ArrayList<Contract>>();

	public void registerCT() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName("trust");
		sd.setType("trust");
		dfd.addServices(sd);
		dfd.setName(getAID());
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println("Failed to register the CTAgent");
			e.printStackTrace();
		}
	}

	/**
	 * Invoces the SinAlphaModel method `getTrust()` to
	 * calculate the trust of an agent based on his record
	 * of contracts.
	 * @param pair The payload containing the AID of the agent
	 * to get the trust from and the value (initially 0). This
	 * pair is modified by the method.
	 */
	public void getTrust(Pair<AID> pair) {
		if (contracts.containsKey(pair.key)) {
			pair.value = SinAlphaModel.getTrust(contracts.get(pair.key));
		}
	}

	@Override
	protected void setup() {
		// Start trust service responder
		addBehaviour(new TrustService(this, createTemplate()));
		// Register service in the DF
		registerCT();
	}

	public static MessageTemplate createTemplate() {
		MessageTemplate template = new MessageTemplate();
		template.addPerformative(ACLMessage.REQUEST);

		return template;
	}

	/**
	 * Behaviour that will respond to requests of computational
	 * trust information.
	 * @author joaolopes
	 *
	 */
	public class TrustService extends AchieveREResponder {

		protected TrustService(Agent agent, MessageTemplate template) {
			super(agent, template);
		}
		
		@Override
		protected ACLMessage handleRequest(ACLMessage request) {
			try {
				ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
				reply.setSender(myAgent.getAID());
				reply.addReceiver(request.getSender());
				reply.setProtocol(request.getProtocol());
				
				// The CTObject contains an array that shouldn't be null
				CTObject trustObject = (CTObject) request.getContentObject();
				if (trustObject == null || trustObject.trustValues == null)
					return new ACLMessage(ACLMessage.NOT_UNDERSTOOD);


				for (int i = 0; i < trustObject.trustValues.size(); i++) {
					getTrust(trustObject.trustValues.get(i));
				}
				
				reply.setContentObject(trustObject);
				
				return reply;
			} catch (Exception e) {
				return new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
			}
		}
	}
}
