package enterpriseTest;

import java.io.Serializable;

import up.fe.liacc.sajas.core.AID;

/**
 * A record of a contract established between
 * two agents.
 * @author joaolopes
 *
 */
public class Contract implements Serializable {
	
	/** Agreement was fullfield. */
	public static final int FULLFIELD = 0;
	/** Agreement was fullfield with delay. */
	public static final int DELAYED = 1;
	/** Agreement was not fullfield. */
	public static final int VIOLATED = 2;

	private static final long serialVersionUID = -5908507099292269099L;
	private AID initiator;
	private AID responder;
	private int initFullfielment; // fullfielment of the initiator
	private int respFullfielment; // fullfielment of the responder
	
	/**
	 * 
	 * @param initiator The aid of the agent that initiated the contract
	 * @param responder The aid of the agents that was contacted by the initiator
	 * @param initFullfield True if the initiator fullfield the agreement
	 * @param respFullfield True if the responder fullfield the agreement
	 */
	public Contract(AID initiator, AID responder, int initFullfield, int respFullfield) {
		super();
		this.initiator = initiator;
		this.responder = responder;
		this.initFullfielment = initFullfield;
		this.respFullfielment = respFullfield;

	}

	public AID getInitiator() {
		return initiator;
	}

	public void setInitiator(AID initiator) {
		this.initiator = initiator;
	}

	public AID getResponder() {
		return responder;
	}

	public void setResponder(AID responder) {
		this.responder = responder;
	}

	/**
	 * @return FULLFIELD, DELAYED or VIOLATED
	 */
	public int getInitFullfielment() {
		return initFullfielment;
	}

	/**
	 * @param initFullfielment Should be FULLFIELD, DELAYED or VIOLATED
	 */
	public void setInitFullfielment(int initFullfielment) {
		this.initFullfielment = initFullfielment;
	}

	/**
	 * @return FULLFIELD, DELAYED or VIOLATED
	 */
	public int getRespFullfielment() {
		return respFullfielment;
	}

	/**
	 * @param respFullfielment Should be FULLFIELD, DELAYED or VIOLATED
	 */
	public void setRespFullfielment(int respFullfielment) {
		this.respFullfielment = respFullfielment;
	}
}
