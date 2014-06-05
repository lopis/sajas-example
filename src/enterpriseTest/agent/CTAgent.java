package enterpriseTest.agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import enterpriseTest.model.CTObject;
import enterpriseTest.model.Contract;
import enterpriseTest.model.Pair;
import enterpriseTest.model.SinAlphaModel;
import up.fe.liacc.repast.RepastAgent;
import up.fe.liacc.sajas.core.AID;
import up.fe.liacc.sajas.core.Agent;
import up.fe.liacc.sajas.core.MessageQueue;
import up.fe.liacc.sajas.domain.DFService;
import up.fe.liacc.sajas.domain.FIPAException;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.DFAgentDescription;
import up.fe.liacc.sajas.domain.FIPAAgentManagement.ServiceDescription;
import up.fe.liacc.sajas.lang.acl.ACLMessage;
import up.fe.liacc.sajas.lang.acl.MessageTemplate;
import up.fe.liacc.sajas.lang.acl.UnreadableException;
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
	HashMap<AID, Vector<Contract>> contracts = new HashMap<AID, Vector<Contract>>();
	SinAlphaModel sinAlphaModel = new SinAlphaModel();
	
	static final int COUNT_MAX = 10;
	int counter = COUNT_MAX;
	
	
	public long startTime = System.currentTimeMillis();

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
	
	@Override
	protected MessageQueue getMailBox() {
		// TODO Auto-generated method stub
		MessageQueue mailbox = super.getMailBox();
		return mailbox;
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
			pair.value = sinAlphaModel.getTrust(contracts.get(pair.key));
		} else {
			pair.value = 1.0;
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
	


	public void addContract(Object content) {
		if (content instanceof Contract) {
			Contract contract = (Contract) content;
			if (!contracts.containsKey(contract.getResponder())) {
				contracts.put(contract.getResponder(), new Vector<Contract>());
			}
			if (!contracts.containsKey(contract.getInitiator())) {
				contracts.put(contract.getInitiator(), new Vector<Contract>());
			}
			contracts.get(contract.getResponder()).add(contract);
			contracts.get(contract.getInitiator()).add(contract);
			long timeTaken = System.currentTimeMillis() - startTime;
			System.out.println("[CTR] " + contract.getResponder().getLocalName()
					+ " " + contract.getResult() + "\t\t\t\t" + timeTaken + "ms");
			if (counter-- < 0) {
				counter = COUNT_MAX;
				System.out.println("------------------------------------------------");
				System.out.println("      Trust Report                              ");

				long time = System.currentTimeMillis();
				for (Iterator<AID> iterator = contracts.keySet().iterator(); iterator.hasNext();) {
					AID aid = iterator.next();
					int[] contractCount = countContract(aid);
					Pair<AID> pair = new Pair<AID>(aid, 0);
					getTrust(pair);
					System.out.printf("# [%s][%.2f] %d Contracts: 	%-2d%% F 	%-2d%% Fd 	%-2d%% V\n",
							aid.getLocalName(),
							pair.value,
							contracts.get(aid).size(),
							100 * contractCount[0] / contracts.get(aid).size(),
							100 * contractCount[1] / contracts.get(aid).size(),
							100 * contractCount[2] / contracts.get(aid).size());
				}
				System.out.printf("## Report took " + (System.currentTimeMillis() - time) + "ms\n");
			}
		}
	}

	private int[] countContract(AID aid) {
		int[] contractCount = {0,0,0};
		for (Iterator<Contract> iterator = contracts.get(aid).iterator(); iterator.hasNext();) {
			Contract contract = iterator.next();
			switch (contract.getResult()) {
			case FULLFIELD:
				contractCount[0]++;
				break;
			case DELAYED:
				contractCount[1]++;
				break;
			case VIOLATED:
				contractCount[2]++;
				break;
			default:
				break;
			}
		}
		return contractCount;
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
				if (request.getContentObject() != null) {
					addContract(request.getContentObject());
					return null;
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			ACLMessage accept = new ACLMessage(ACLMessage.AGREE);
			accept.setSender(myAgent.getAID());
			accept.addReceiver(request.getSender());
			accept.setProtocol(request.getProtocol());
			return accept;
		}
		
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			try {
				if (!(request.getContentObject() instanceof CTObject)) {
					return null;
				}
				
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
				
				trustObject.sortMax();
				
				return reply;
			} catch (UnreadableException | IOException e) {
				return new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
			}
		}
	}
}
