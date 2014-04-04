package repastTest;

import up.fe.liacc.repacl.Agent;
import up.fe.liacc.repacl.acl.ACLMessage;
import up.fe.liacc.repacl.proto.AchieveREInitiator;

public class RequestInit extends AchieveREInitiator {

	public RequestInit(Agent agent, ACLMessage message) {
		super(agent, message);
	}

}
