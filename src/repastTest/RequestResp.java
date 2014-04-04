package repastTest;

import up.fe.liacc.repacl.Agent;
import up.fe.liacc.repacl.acl.ACLMessage;
import up.fe.liacc.repacl.proto.AchieveREResponder;

public class RequestResp extends AchieveREResponder {

	public RequestResp(Agent agent) {
		super(agent);
	}
	
	@Override
	public void handleRequest(ACLMessage nextMessage) {
		System.out.println(getOwner() + " - Got request.");
	}
	
	@Override
	public void action() {
		super.action();
		System.out.println("Behavior step");
	}

}
