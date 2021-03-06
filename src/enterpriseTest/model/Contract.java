package enterpriseTest.model;

import java.io.Serializable;

import up.fe.liacc.sajas.core.AID;

/**
 * A record of a contract established between
 * two agents.
 * @author joaolopes
 *
 */
public class Contract implements Serializable {
	
	public static enum OutcomeType {FULLFIELD, DELAYED, VIOLATED};
	public static double lambdaFactorPerOutcomeType[] = {0.0, 0.5, 1.0};
	public static double lambdaPerOutcomeType[] = {1.0, -0.5, -2.0};

	private static final long serialVersionUID = -5908507099292269099L;
	private AID responder;
	private OutcomeType result;
	private AID initiator;
	private boolean usedTrust;
	
	public String agentType = "";
	
	/**
	 * 
	 * @param initiator The aid of the agent that initiated the contract
	 * @param responder The aid of the agents that was contacted by the initiator
	 * @param initFullfield True if the initiator fullfield the agreement
	 * @param respFullfield True if the responder fullfield the agreement
	 */
	/**
	 * 
	 * @param responder The aid of the agents that was contacted by the initiator
	 * @param result FULLFIELD, DELAYED or VIOLATED
	 */
	public Contract(AID initiator, AID responder, OutcomeType result, boolean usedTrust) {
		super();
		this.setInitiator(initiator);
		this.setResponder(responder);
		this.result = result;
		this.setUsedTrust(usedTrust);
	}

	public void setResult (Contract.OutcomeType result) {
		this.result = result;
	}
	
	public Contract.OutcomeType getResult () {
		return result;
	}

	public AID getResponder() {
		return responder;
	}

	public void setResponder(AID responder) {
		this.responder = responder;
	}

	public AID getInitiator() {
		return initiator;
	}

	public void setInitiator(AID initiator) {
		this.initiator = initiator;
	}

	public boolean usedTrust() {
		return usedTrust;
	}

	public void setUsedTrust(boolean usedTrust) {
		this.usedTrust = usedTrust;
	}
}
