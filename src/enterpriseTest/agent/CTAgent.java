package enterpriseTest.agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
	 * 
	 */
	private static final long serialVersionUID = 4566114197159872281L;
	/**
	 * Contains up-to-date values of trust from some or all agents
	 */
	HashMap<AID, Double> trustChache = new HashMap<AID, Double>();
	HashMap<AID, Vector<Contract>> sellerContracts 			= new HashMap<AID, Vector<Contract>>();
	HashMap<AID, Vector<Contract>> buyerNoTrustContracts 	= new HashMap<AID, Vector<Contract>>();
	HashMap<AID, Vector<Contract>> buyerTrustContracts 		= new HashMap<AID, Vector<Contract>>();
	SinAlphaModel sinAlphaModel = new SinAlphaModel();
	long timeCounter = 0;
	
	static final int COUNT_MAX = 10;
	static final int COUNT_STOP = 200;
	int counter = COUNT_MAX;
	int counter_stop = COUNT_STOP;
	
	
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

	/**
	 * Invoces the SinAlphaModel method `getTrust()` to
	 * calculate the trust of an agent based on his record
	 * of contracts.
	 * @param pair The payload containing the AID of the agent
	 * to get the trust from and the value (initially 0). This
	 * pair is modified by the method.
	 */
	public void getTrust(Pair<AID> pair) {
		if (sellerContracts.containsKey(pair.key)) {
			pair.value = sinAlphaModel.getTrust(sellerContracts.get(pair.key));
		} else if (buyerNoTrustContracts.containsKey(pair.key)) {
			pair.value = sinAlphaModel.getTrust(buyerNoTrustContracts.get(pair.key));
		} else if (buyerTrustContracts.containsKey(pair.key)) {
			pair.value = sinAlphaModel.getTrust(buyerTrustContracts.get(pair.key));
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
		timeCounter = System.currentTimeMillis();
	}

	public static MessageTemplate createTemplate() {
		return MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
	}
	


	public void addContract(Object content) {
		if (content instanceof Contract) {
			Contract contract = (Contract) content;
			
			
			
			if (!sellerContracts.containsKey(contract.getResponder())) {
				sellerContracts.put(contract.getResponder(), new Vector<Contract>());
			}
			sellerContracts.get(contract.getResponder()).add(contract);
			
			
			if (contract.usedTrust()) {
				if (!buyerTrustContracts.containsKey(contract.getInitiator())) {
					buyerTrustContracts.put(contract.getInitiator(), new Vector<Contract>());
				}
				buyerTrustContracts.get(contract.getInitiator()).add(contract);

			} else if (!contract.usedTrust()) {
				if (!buyerNoTrustContracts.containsKey(contract.getInitiator())) {
					buyerNoTrustContracts.put(contract.getInitiator(), new Vector<Contract>());
				}
				buyerNoTrustContracts.get(contract.getInitiator()).add(contract);

			}
			
			printAverageReport();
			//printReport();
		}
	}
	
	private void printReport() {
		if (counter-- < 0) {
			counter = COUNT_MAX;
			System.out.println("------------------------------------------------");
			System.out.println("      Trust Report                              ");

			long time = System.currentTimeMillis();
			for (Iterator<AID> iterator = sellerContracts.keySet().iterator(); iterator.hasNext();) {
				AID aid = iterator.next();
				Report report = new Report("SL");
				countContract(sellerContracts, report);
				Pair<AID> pair = new Pair<AID>(aid, 0);
				getTrust(pair);
				System.out.printf("# [%s][%.2f] %d Contracts: 	%-2d%% F 	%-2d%% Fd 	%-2d%% V\n",
						aid.getLocalName(),
						pair.value,
						sellerContracts.get(aid).size(),
						Math.round(report.f /(0.01*report.total)),
						Math.round(report.fd/(0.01*report.total)),
						Math.round(report.v /(0.01*report.total)));
			}
			System.out.printf("## Report took %dms at time %.2ds\n",
					(System.currentTimeMillis() - time),
					(System.currentTimeMillis() - timeCounter)*0.001);
		}
	}

	private void printAverageReport() {
		if (counter-- < 0) {
			if (counter_stop-- < 0) {
				return;
			}
			counter = COUNT_MAX;
			//System.out.println("# Time\tBTF%\tBTD%\tBTV%\tBNF%\tBND%\tBNV%\tSF%\tSD%\tSV%");
			
			Report reportSellers 	= new Report("SL");
			Report reportBuyTrust 	= new Report("BT");
			Report reportBuyNoTrust = new Report("BN");
			
			countContract(sellerContracts, reportSellers);
			countContract(buyerTrustContracts, reportBuyTrust);
			countContract(buyerNoTrustContracts, reportBuyNoTrust);

			double timeElapsed = (System.currentTimeMillis() - startTime) * 0.001;
			
			System.out.printf("%.2f", timeElapsed);
			//printReportLine(reportSellers, sellerContracts);
			printReportLine(reportBuyNoTrust, buyerNoTrustContracts);
			printReportLine(reportBuyTrust, buyerTrustContracts);
			System.out.println("");
		}
	}

	private void printReportLine(Report report,
			HashMap<AID, Vector<Contract>> map) {
		
		int total = report.total == 0 ? 1 : report.total;
		System.out.printf("\t%2d%%",Math.round(report.f /(0.01*total)));
//		System.out.printf("\t%2d%%\t%2d%%\t%2d%%",
//				Math.round(report.f /(0.01*total)),
//				Math.round(report.fd/(0.01*total)),
//				Math.round(report.v /(0.01*total)));
	}
	
	private class Report {
		public int f  = 0;
		public int fd = 0;
		public int v  = 0;
		public int total = 0;
		public String type = "";
		public Report(String type) {
			this.type = type;
		}
	}

	

	/**
	 * Counts the types of contracts and returns it in an array with 3 values:
	 * { # fullfield, # delayed, # violated}.
	 * @param aid
	 * @return
	 */
	private void countContract(Map<AID, Vector<Contract>> map, Report report) {
		
		for (Iterator<AID> iterator = map.keySet().iterator(); iterator.hasNext();) {
			AID aid = iterator.next();
			for (Iterator<Contract> iterator1 = map.get(aid).iterator(); iterator1.hasNext();) {
				Contract contract = iterator1.next();
				switch (contract.getResult()) {
				case FULLFIELD:
					report.f++;
					break;
				case DELAYED:
					report.fd++;
					break;
				case VIOLATED:
					report.v++;
					break;
				default:
					break;
				}
				report.total++;
			}
		}
	}

	/**
	 * Behaviour that will respond to requests of computational
	 * trust information.
	 * @author joaolopes
	 *
	 */
	public class TrustService extends AchieveREResponder {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8304430053034124470L;

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
				
				trustObject.sortMax();
				reply.setContentObject(trustObject);
				
				return reply;
			} catch (UnreadableException | IOException e) {
				return new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
			}
		}
	}
}