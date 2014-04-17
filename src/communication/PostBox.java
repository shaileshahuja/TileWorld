package communication;
public class PostBox {
	//implements messaging chanel between two agents
	private static Message MessageFromFirst;
	private static Message MessageFromSecond;
	public static void put(String fromAgent, Message m){
		char c = fromAgent.charAt(0);
		switch(c){
		case 'F': MessageFromFirst = m;
			break;
		case 'S': MessageFromSecond = m;
			break;
		}
	}
	public static Message get(String toAgent){
		char c = toAgent.charAt(0);
		switch(c){
		case 'F': return MessageFromSecond;
		case 'S': return MessageFromFirst;
		}
		return new Message();
	}
}
