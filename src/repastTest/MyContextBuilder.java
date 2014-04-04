package repastTest;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import up.fe.liacc.repacl.core.DF;
	
	public class MyContextBuilder implements ContextBuilder<Object> {
	
		@Override
		public Context build(Context context) {
	
			context.setId("termites");
			DF.setContext(context);
			
			// Create agent
			for (int i = 0; i < 10; i++) {
				context.add(new MyAgent());
			}
			
			new InitiatorAgent();
			
			SimpleAgent agent = new SimpleAgent();
			context.add(agent);
			
			return context;
		}
	}
