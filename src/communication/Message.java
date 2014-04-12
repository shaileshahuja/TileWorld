package communication;
import sim.util.Int2D;
import tileworld.agent.TWAgentPercept;

public class Message { 
	private int x;
	private int y;
	private String request; // M2
	private TWAgentPercept obstacle1; // M2
	private TWAgentPercept response; //M3
	
	public Message(int x, int y, String req, TWAgentPercept percept){
		this.x = x;
		this.y = y;
		request = req;
		response = percept;
		obstacle1 = null; 
	}
	public Message(int x, int y, TWAgentPercept obs, TWAgentPercept percept){
		this.x = x;
		this.y = y;
		request = null;
		response = percept;
		obstacle1 = obs; 
	}
	public Message(){
		this.x = -1;
		this.y = -1;
		request = null;
		response = null;
		obstacle1 = null; 
	}
}