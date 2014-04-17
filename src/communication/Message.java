package communication;
import sim.util.Int2D;
import tileworld.agent.TWAgentPercept;


//According to the message requirement, different message objects are created
public class Message { 
	private int x;
	private int y;
	private String request; // M2
	private TWAgentPercept object1; // M2
	private TWAgentPercept response; //M3
	private int x2; 
	private int y2; 
	
	public Message(int x, int y, String req, TWAgentPercept percept){ // location, request, response
		this.x = x;
		this.y = y;
		request = req;
		response = percept;
		object1 = null; 
		x2 = -1;
		y2 = -1;
	}
	public Message(TWAgentPercept obj, String req, TWAgentPercept percept)
	{
		this.object1 = obj;
		this.request = req;
		this.response = percept;
		x = -1;
		y = -1;
		x2 = -1;
		y2 = -1;
	}
	public Message(int x, int y, TWAgentPercept obj, TWAgentPercept percept){ // location, obstacle, response/obstacle
		this.x = x;
		this.y = y;
		request = null;
		this.response = percept;
		this.object1 = obj; 
		this.x2 = -1;
		this.y2 = -1;
	}
	public Message(int x, int y, int x2, int y2, TWAgentPercept percept){ // location, targetlocation, response/obstacle
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		this.response = percept; 
		this.request = null;
		this.object1 = null;
		
	}
	public Message(TWAgentPercept obj, TWAgentPercept percept, int x2, int y2) //obstacle, response/obstacle/, targetlocatin
	{
		this.object1 = obj;
		this.response = percept;
		this.x2 = x2;
		this.y2 = y2; 
		this.x = -1;
		this.y = -1;
		this.request = null;
		
	}
	public Message(){
		this.x = -1;
		this.y = -1;
		this.request = null;
		this.response = null;
		this.object1 = null; 
		this.x2 = -1;
		this.y2 = -1;
	}
	public int getX(){return x;}
	public int getY(){return y;}
	public String getRequest(){return request;}
	public TWAgentPercept getObs1(){return object1;}
	public TWAgentPercept getResponse(){return response;}
	public int getX2(){return x2;}
	public int getY2(){return y2;}
}