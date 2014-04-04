package repastTest;

import up.fe.liacc.repacl.Agent;
import up.fe.liacc.repacl.acl.ACLMessage;
import up.fe.liacc.repacl.proto.AchieveREInitiator;
import up.fe.liacc.repacl.proto.Behavior;

public class MyBehavior extends AchieveREInitiator{
	
	public MyBehavior(Agent owner, ACLMessage message) {
		super(owner, message); // Sets the owner of this behavior.
	}
	

}
