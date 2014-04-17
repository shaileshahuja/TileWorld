

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tileworld.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import javax.rmi.CORBA.Util;

import practicalreasoning.Intention;
import practicalreasoning.IntentionType;
import practicalreasoning.TWPlan;
import practicalreasoning.UtilityParams;
import sim.util.Int2D;
import tileworld.Parameters;
import tileworld.environment.TWDirection;
import tileworld.environment.TWEntity;
import tileworld.environment.TWEnvironment;
import tileworld.environment.TWHole;
import tileworld.environment.TWObject;
import tileworld.environment.TWObstacle;
import tileworld.environment.TWTile;
import tileworld.exceptions.CellBlockedException;
import tileworld.planners.AstarPathGenerator;
import tileworld.planners.TWPath;
import tileworld.planners.TWPathStep;
import tileworld.planners.TWRefuelPathGenerator;
import communication.*;


/**
 * TWContextBuilder
 *
 * @author michaellees
 * Created: Feb 6, 2011
 *
 * Copyright michaellees Expression year is undefined on line 16, column 24 in Templates/Classes/Class.java.
 *
 *
 * Description:
 *
 */

public class UtilityAgent2 extends TWAgent{
	private static final long serialVersionUID = 1L;
	private static boolean pathLengthAdjustment = true;
	private HashMap<String, Double> parameters; // not to be confused with a parameter object
	private PriorityQueue<TWHole> holes;
	private PriorityQueue<TWTile> tiles;
	private TWPlan currentPlan = null;
	private Intention currIntention = null;
	private AstarPathGenerator pathGenerator;
	private TWRefuelPathGenerator fuelPathGen;
	private LinkedList<Int2D> mySnaps, otherSnaps;
	private boolean intentionChanged = false;
	private int xboundary1;
	private int yboundary1;
	private int xboundary2;
	private int yboundary2;
	private int upperboundx;
	private int upperboundy;
	private int lowerboundx;
	private int lowerboundy;
	// Communication variables
	private Message msgReceived;
	private Message msgToSend;
	private String curRequest;
	private int locotherx = 0, locothery = 0;
	private int loctargetx = 0, loctargety = 0;
	TWAgentPercept resp = null;
	boolean flag = false;

	//private ReactivePathGenerator reactivePathGen;
	private String name;
	public UtilityAgent2(String name, int xpos, int ypos, TWEnvironment env, double fuelLevel, HashMap<String, Double> parameters) {
		super(xpos,ypos,env,fuelLevel);
		pathGenerator = new AstarPathGenerator(env, this, Integer.MAX_VALUE);
		fuelPathGen = new TWRefuelPathGenerator(this);
		this.parameters = parameters;
		this.name = name;
		this.mySnaps = new LinkedList<Int2D>();
		this.otherSnaps = new LinkedList<Int2D>();

	}

	protected void sense()
	{
		receiveMsg();
		sensor.sense();
	}

	protected TWThought think() {

		//store past locations
		Int2D snap = snapToCheckpoint(x, y);
		if(mySnaps.size() == 0 || !mySnaps.peekFirst().equals(snap))
		{
			mySnaps.addFirst(snap);
			if(mySnaps.size() > parameters.get(UtilityParams.LENGTH_SNAPS))
				mySnaps.removeLast();
		}
		//store other agents locations
		snap = snapToCheckpoint(locotherx, locothery);
		if(otherSnaps.size() == 0 || !otherSnaps.peekFirst().equals(snap))
		{
			otherSnaps.addFirst(snap);
			if(otherSnaps.size() > parameters.get(UtilityParams.LENGTH_SNAPS))
				otherSnaps.removeLast();
		}

		//High prio reactive
		TWEntity current = (TWEntity) getMemory().getObjectAt(x, y);		
		if(carriedTiles.size() < 3 & current instanceof TWTile)
			return new TWThought(TWAction.PICKUP, null);
		if(hasTile() && current instanceof TWHole)
			return new TWThought(TWAction.PUTDOWN, null);
		if(x == getEnvironment().getFuelingStation().getX() && y == getEnvironment().getFuelingStation().getY() && getFuelLevel() != Parameters.defaultFuelLevel)
			return new TWThought(TWAction.REFUEL, null);
		if(surrounded())
			return new TWThought(null, null);
		//add reaction wait() if obstacles on all four sides
		//Start of rational part

		computeUtilities();
		if(impossible(currentPlan))
		{
			HashMap<IntentionType, Double> utilities = options();
			currIntention = filter(utilities);	
			currentPlan = plan(currIntention);
			intentionChanged = true;
		}
		else
		{
			Intention newIntention = null;
			if(reconsider(currentPlan, currIntention))  
			{
				HashMap<IntentionType, Double> utilities = options();
				newIntention = filter(utilities);	
			}
			if(!sound(currentPlan, newIntention))	 
			{
				currentPlan = plan(newIntention);
				currIntention = newIntention;
				intentionChanged = true;
			}
		}
		if(true)
		{
			System.out.print(name + " ");
			System.out.println(currIntention);
			if(currentPlan.peek().getAction()!=null)
				System.out.println("Current plan's next action is" + currentPlan.peek());
			else System.out.println("Current Plan is to wait");
			System.out.println(this.getScore());
			System.out.println(this.getFuelLevel());
		}

		return currentPlan.next();
	}

	private boolean surrounded() {
		return getMemory().isCellBlocked(x, y + 1, -1) && getMemory().isCellBlocked(x, y - 1, -1)
				&& getMemory().isCellBlocked(x + 1, y, -1) && getMemory().isCellBlocked(x - 1, y, -1);
	}

	private boolean reconsider(TWPlan currentPlan, Intention intention)
	{
		return true;
	}

	private boolean sound(TWPlan currentPlan2, Intention newIntention) {
		if(newIntention == null)
			return true;		
		return newIntention.equals(currIntention);
	}

	@Override
	protected void act(TWThought thought) {
		//case when we wait
		if(thought.getAction() == null)
			return;
		try {
			switch(thought.getAction())
			{
			case MOVE:
				this.move(thought.getDirection());
				break;
			case PICKUP:
				TWTile tile = (TWTile)getEnvironment().getObjectGrid().get(x, y);
				if(tile != null)
				{this.pickUpTile(tile);
				this.getMemory().removeObject(tile);}
				break;
			case PUTDOWN:
				TWHole hole = (TWHole)getEnvironment().getObjectGrid().get(x, y);
				if(hole != null)
				{this.putTileInHole(hole);
				this.getMemory().removeObject(hole);}
				break;
			case REFUEL:
				refuel();
				break;
			default:
				break;			
			}
			// Agents sends its message
			sendMsg();
		}  catch (CellBlockedException ex) {
			ex.printStackTrace();
		}
	}
	private void receiveMsg(){
		this.msgReceived = PostBox.get(this.name);
		// put message into internal memory
		if(msgReceived != null){
			//System.out.println("We have received a message");
			if(msgReceived.getX() >-1 && msgReceived.getY()>-1){
				locotherx = msgReceived.getX();
				locothery = msgReceived.getY();
				//System.out.println("We have received the other's location");
			}
			if(msgReceived.getX2() >-1 && msgReceived.getY2() >-1)
			{
				loctargetx = msgReceived.getX2();
				loctargety = msgReceived.getY2();
				//System.out.println("We have received the other's target");
				if(currIntention != null &&currIntention.getIntentionType() != IntentionType.REFUEL && loctargetx == currIntention.getLocation().getX() && loctargety == currIntention.getLocation().getY())
					System.out.println("INTENTION CLASH - LOCTARGETX,Y = " + loctargetx + "," + loctargety + "Our intention = " + currIntention.getLocation());

			}

			if(msgReceived.getRequest()!="" && msgReceived.getRequest()!=null){
				curRequest = msgReceived.getRequest();
				//System.out.println("We have received the other's request");
			}
			else{ curRequest = null;}
			if(msgReceived.getObs1()!=null){
				int rx = msgReceived.getObs1().getO().getX();
				int ry = msgReceived.getObs1().getO().getY();
				if(this.memory.getPerceptAt(rx, ry)!=null) 
				{if(msgReceived.getObs1().getT() > this.memory.getPerceptAt(rx, ry).getT()){
					this.getMemory().addAgentPercept(msgReceived.getObs1());
				}
				else{ flag = true;
					  resp = this.memory.getPerceptAt(rx,ry);
					}
				}
				else
					this.getMemory().addAgentPercept(msgReceived.getObs1());
				//System.out.println("We have received the other's object that's sent");

			}
			if(msgReceived.getResponse()!=null){
				int rx = msgReceived.getResponse().getO().getX();
				int ry = msgReceived.getResponse().getO().getY();
				if(this.memory.getPerceptAt(rx, ry) !=null)
				{if(msgReceived.getResponse().getT() > this.memory.getPerceptAt(rx, ry).getT()){
					this.getMemory().addAgentPercept(msgReceived.getResponse());
				}
				else {
					flag = true;
					if(msgReceived.getResponse().getO() instanceof TWTile || msgReceived.getResponse().getO() instanceof TWHole)
					resp = this.memory.getPerceptAt(rx,ry);
					}
				}
				else this.getMemory().addAgentPercept(msgReceived.getResponse());
				//System.out.println("We have received the other's response to request");

			}
		}
	}
	private void CheckBoundary()
	{
		xboundary1= locotherx;
		yboundary1= locothery;
		xboundary2= loctargetx;
		yboundary2= loctargety;

		if(xboundary1==xboundary2 )
		{
			xboundary1=xboundary1-20;
			xboundary2=xboundary2+20;
		}
		else if(yboundary1==yboundary2)
		{
			yboundary1=yboundary1-20;
			yboundary2=yboundary2+20;;
		}
		else
		{
			if(xboundary1>xboundary2)
			{
				xboundary1=xboundary1+20;
				xboundary2=xboundary2-20;
			}
			else if(xboundary1<xboundary2)
			{
				xboundary1 = xboundary1 - 20;
				xboundary2 = xboundary2 + 20;
			}
		}
		if(xboundary1>xboundary2)
		{
			upperboundx = xboundary1;
			lowerboundx = xboundary2;
		}
		else
		{
			upperboundx = xboundary2;
			lowerboundx = xboundary1;
		}

		if(yboundary1>yboundary2)
		{
			upperboundy = yboundary1;
			lowerboundy = yboundary2;
		}
		else 
		{
			upperboundy = yboundary2;
			lowerboundy = yboundary1;
		}

		if(upperboundy>this.getEnvironment().getyDimension())
			upperboundy=this.getEnvironment().getyDimension();
		if(lowerboundy<0)
			lowerboundy=0;
		if(upperboundx>this.getEnvironment().getxDimension())
			upperboundx=this.getEnvironment().getxDimension();
		if(lowerboundx<0)
			lowerboundx=0;
		if(upperboundx-lowerboundx==0)
			upperboundx=upperboundx+1;
		if(upperboundy-lowerboundy==0)
			upperboundy++;

	}
	private void sendMsg(){
		// create message to send using a variety of things
		//System.out.println("Entering Send");
		int sendLocX2;
		int sendLocY2;
		String myReq="";
		Message msg = null;
		
		
		if(flag==false)
		{
		if(curRequest!=null && curRequest!=""){
			TWEntity respx;
			TWEntity tile;
			TWEntity hole;
			switch(curRequest.charAt(0)){
			case 'T': respx = this.getMemory().getNearbyTile(locotherx, locothery, 0);
			if(respx != null)	
			{	  if(respx.getX() == currIntention.getLocation().getX() && respx.getY() == currIntention.getLocation().getY())
				respx = (TWObstacle) this.getMemory().getNearbyObject(this.getX(), this.getY(), 0, TWObstacle.class);
			if(respx != null)
				resp = this.getMemory().getPerceptAt(respx.getX(), respx.getY());

			}

			break;
			case 'H': respx = this.getMemory().getNearbyHole(locotherx, locothery, 0);
			if(respx != null){
				if(respx.getX() == currIntention.getLocation().getX() && respx.getY() == currIntention.getLocation().getY())
					respx = (TWObstacle) this.getMemory().getNearbyObject(this.getX(), this.getY(), 0, TWObstacle.class);
				if(respx != null)
					resp = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
			}
			break;
			case 'A': tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
			hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
			double tiledistance;
			if(tile !=null && tile.getX() == currIntention.getLocation().getX() && tile.getY() == currIntention.getLocation().getY())
			{tiledistance = tile.getDistanceTo(locotherx, locothery);}
			else tiledistance = 10000000;
			double holedistance;
			if(hole != null && hole.getX() == currIntention.getLocation().getX() && hole.getY() == currIntention.getLocation().getY())
			{holedistance = hole.getDistanceTo(locotherx, locothery);}
			else holedistance = 10000000;
			if(tiledistance>=holedistance)
				respx = hole;
			else respx = tile; 
			if (respx != null)
				resp = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
			break;
			default: 
				break;
			}
		}

		if(curRequest == null || curRequest == "")
		{
			CheckBoundary();
			Random r = new Random(Parameters.seed);
			TWObstacle obst;
			double distance;
			double mintbyd=-1;
			TWObstacle bestobst = null;
			double obtimestamp;

			for(int i=0;i<10;i++)
			{
				int randomX=r.nextInt(upperboundx - lowerboundx + 1) + lowerboundx;
				int randomY=r.nextInt(upperboundy - lowerboundy + 1) + lowerboundy;
				//System.out.println("upperboundX = " + upperboundx + " upperboundy = " + upperboundy + "lowerboundx = " + lowerboundx + "lowerboundy = " + lowerboundy);
				//System.out.println("randomX = " + randomX + " randomY = " + randomY);
				obst = (TWObstacle) this.getMemory().getNearbyObject(randomX, randomY, 0, TWObstacle.class);
				if(obst!=null)
				{
					distance=obst.getDistanceTo(this.loctargetx, this.loctargety);
					obtimestamp = this.getMemory().getPerceptAt(obst.getX(), obst.getY()).getT();
					if(obtimestamp/distance>mintbyd)
					{
						mintbyd=obtimestamp/distance;
						bestobst=obst;
					}	
				}
			}
			if(bestobst != null)
			{
				resp = this.getMemory().getPerceptAt(bestobst.getX(), bestobst.getY());
			}


		}
		}
		//System.out.println("Intention in sendmsg " + this.currIntention.getIntentionType());
		if(this.currIntention.getIntentionType().equals(IntentionType.EXPLORE)){
			//System.out.println("Entering explore");
			TWEntity tile;
			TWEntity hole;
			TWEntity respx;
			TWAgentPercept obj = null;
			if(this.carriedTiles.size()==3) myReq = "HOLE";
			else if(carriedTiles.size()==0) myReq = "TILE";
			else myReq = "ANYTHING";
			//if(this.getMemory().getSimulationTime()%3==0)  /////////////LOWER NUMBER INSTEAD OF 30- MORE CLASHES RECOGNIZED. BUT FEWER REQUESTS. THERE IS A TRADE OFF. change modulus 30 based on environment size. 
			if(this.intentionChanged == true)
			{
				this.intentionChanged = false;
				sendLocX2 = this.currIntention.getLocation().getX();
				sendLocY2 = this.currIntention.getLocation().getY();
				if(this.getMemory().getSimulationTime()%5==0)
					msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
				else
				{
					tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
					hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
					double tiledistance;
					if(tile !=null)
					{tiledistance = tile.getDistanceTo(locotherx, locothery);}
					else tiledistance = 10000000;
					double holedistance;
					if(hole != null)
					{holedistance = hole.getDistanceTo(locotherx, locothery);}
					else holedistance = 10000000;
					if(tiledistance>=holedistance)
						respx = hole;
					else respx = tile; 
					if(respx != null) 
						obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
					msg = new Message(obj, resp, sendLocX2, sendLocY2);
				}

			}
			else if(this.getMemory().getSimulationTime()%5 == 0)
				msg = new Message(this.getX(), this.getY(), myReq, resp);
			else
			{
				tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
				hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
				double tiledistance;
				if(tile !=null)
				{tiledistance = tile.getDistanceTo(locotherx, locothery);}
				else tiledistance = 10000000;
				double holedistance;
				if(hole != null)
				{holedistance = hole.getDistanceTo(locotherx, locothery);}
				else holedistance = 10000000;
				if(tiledistance>=holedistance)
					respx = hole;
				else respx = tile; 
				if(respx != null)
					obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
				msg = new Message(obj, myReq, resp);

			}

		}
		if(this.currIntention.getIntentionType().equals(IntentionType.PICKUPTILE))
		{
			//System.out.println("Entering pickup");
			TWEntity tile;
			TWEntity hole;
			TWEntity respx;
			TWAgentPercept obj = null;
			//if(this.memory.getSimulationTime() % 3 == 0) /////LOWER NUMBER - Recognizes more clashes but fewer requests made. hence, there is a trade off. change modulus to higher numbers for bigger environments. 
			if(this.intentionChanged == true)
			{
				this.intentionChanged = false;
				sendLocX2 = this.tiles.peek().getX();
				sendLocY2 = this.tiles.peek().getY();
				if(this.getMemory().getSimulationTime()%5==0)
					msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
				else
				{
					tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
					hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
					double tiledistance;
					if(tile !=null)
					{tiledistance = tile.getDistanceTo(locotherx, locothery);}
					else tiledistance = 10000000;
					double holedistance;
					if(hole != null)
					{holedistance = hole.getDistanceTo(locotherx, locothery);}
					else holedistance = 10000000;
					if(tiledistance>=holedistance)
						respx = hole;
					else respx = tile; 
					if(respx != null) 
						obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
					msg = new Message(obj, resp, sendLocX2, sendLocY2);
				}

			}
			else if(this.carriedTiles.size()==2)
			{
				if(this.holes.size() <= 2)
				{
					myReq = "HOLE";	
					if(this.getMemory().getSimulationTime()%5 == 0)
						msg = new Message(this.getX(), this.getY(), myReq, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null)
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, myReq, resp);

					}
				}
				else 
				{
					sendLocX2 = this.tiles.peek().getX();
					sendLocY2 = this.tiles.peek().getY();
					if(this.getMemory().getSimulationTime()%5==0)
						msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, resp, sendLocX2, sendLocY2);
					}
				}
			}
			else if(this.carriedTiles.size()==1)
			{
				if(this.holes.size() <= 1)
				{
					myReq = "ANYTHING";	
					if(this.getMemory().getSimulationTime()%5 == 0)
						msg = new Message(this.getX(), this.getY(), myReq, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, myReq, resp);

					}

				}
				else 
				{
					sendLocX2 = this.tiles.peek().getX();
					sendLocY2 = this.tiles.peek().getY();
					if(this.getMemory().getSimulationTime()%5==0)
						msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, resp, sendLocX2, sendLocY2);
					}

				}
			}
			else if(this.carriedTiles.size()==0)
			{
				if(this.holes.size() <= 0)
				{
					myReq = "ANYTHING";	
					if(this.getMemory().getSimulationTime()%5 == 0)
						msg = new Message(this.getX(), this.getY(), myReq, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null)
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, myReq, resp);

					}
				}
				else 
				{
					sendLocX2 = this.tiles.peek().getX();
					sendLocY2 = this.tiles.peek().getY();
					if(this.getMemory().getSimulationTime()%5==0)
						msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, resp, sendLocX2, sendLocY2);
					}
				}
			}				
		}
		if(this.currIntention.getIntentionType().equals(IntentionType.FILLHOLE))
		{
			//System.out.println("Entering fill");

			TWEntity tile;
			TWEntity hole;
			TWEntity respx;
			TWAgentPercept obj = null;
			//if(this.memory.getSimulationTime() %3 == 0)  ////lower number means they recognize intention clashes more often BUT they send fewer requests. There is a trade off. 
			if(this.intentionChanged==true)
			{
				this.intentionChanged=false;
				sendLocX2 = this.holes.peek().getX();
				sendLocY2 = this.holes.peek().getY();
				if(this.getMemory().getSimulationTime()%5==0)
					msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
				else
				{
					tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
					hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
					double tiledistance;
					if(tile !=null)
					{tiledistance = tile.getDistanceTo(locotherx, locothery);}
					else tiledistance = 10000000;
					double holedistance;
					if(hole != null)
					{holedistance = hole.getDistanceTo(locotherx, locothery);}
					else holedistance = 10000000;
					if(tiledistance>=holedistance)
						respx = hole;
					else respx = tile; 
					if(respx != null) 
						obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
					msg = new Message(obj, resp, sendLocX2, sendLocY2);
				}

			}

			else if(this.carriedTiles.size()==3)
			{
				if(this.holes.size() <=2)
				{
					myReq = "ANYTHING";			
					if(this.getMemory().getSimulationTime()%5 == 0)
						msg = new Message(this.getX(), this.getY(), myReq, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, myReq, resp);

					}
				}
				else 
				{
					sendLocX2 = this.holes.peek().getX();
					sendLocY2 = this.holes.peek().getY();
					if(this.getMemory().getSimulationTime()%5==0)
						msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, resp, sendLocX2, sendLocY2);
					}
				}
			}
			else if(this.carriedTiles.size()==2)
			{
				if(this.holes.size() <= 1)
				{
					myReq = "ANYTHING";	
					if(this.getMemory().getSimulationTime()%5 == 0)
						msg = new Message(this.getX(), this.getY(), myReq, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, myReq, resp);

					}
				}
				else 
				{
					sendLocX2 = this.holes.peek().getX();
					sendLocY2 = this.holes.peek().getY();
					if(this.getMemory().getSimulationTime()%5==0)
						msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null)
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, resp, sendLocX2, sendLocY2);
					}
				}
			}
			else if(this.carriedTiles.size()==1)
			{
				if(this.tiles.size() < 1)
				{
					myReq = "TILE";
					if(this.getMemory().getSimulationTime()%5 == 0)
						msg = new Message(this.getX(), this.getY(), myReq, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null) 
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, myReq, resp);

					}
				}
				else 
				{
					sendLocX2 = this.holes.peek().getX();
					sendLocY2 = this.holes.peek().getY();
					if(this.getMemory().getSimulationTime()%5==0)
						msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
					else
					{
						tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
						hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
						double tiledistance;
						if(tile !=null)
						{tiledistance = tile.getDistanceTo(locotherx, locothery);}
						else tiledistance = 10000000;
						double holedistance;
						if(hole != null)
						{holedistance = hole.getDistanceTo(locotherx, locothery);}
						else holedistance = 10000000;
						if(tiledistance>=holedistance)
							respx = hole;
						else respx = tile; 
						if(respx != null)
							obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
						msg = new Message(obj, resp, sendLocX2, sendLocY2);
					}
				}
			}			

		}
		if(this.currIntention.getIntentionType().equals(IntentionType.REFUEL))
		{
			if(DEBUG)
				System.out.println("Entering refuel");
			TWEntity tile;
			TWEntity hole;
			TWEntity respx;
			TWAgentPercept obj = null;

			sendLocX2 = 0;
			sendLocY2 = 0; 
			if(this.getMemory().getSimulationTime()%5==0)
				msg = new Message(this.getX(), this.getY(), sendLocX2, sendLocY2, resp);
			else
			{
				tile = this.getMemory().getNearbyTile(locotherx, locothery, 0);
				hole = this.getMemory().getNearbyHole(locotherx, locothery, 0);
				double tiledistance;
				if(tile !=null)
				{tiledistance = tile.getDistanceTo(locotherx, locothery);}
				else tiledistance = 10000000;
				double holedistance;
				if(hole != null)
				{holedistance = hole.getDistanceTo(locotherx, locothery);}
				else holedistance = 10000000;
				if(tiledistance>=holedistance)
					respx = hole;
				else respx = tile; 
				if(respx != null) 
					obj = this.getMemory().getPerceptAt(respx.getX(), respx.getY());
				msg = new Message(obj, resp, sendLocX2, sendLocY2);
			}
		}
		flag=false;
		PostBox.put(this.name, msg);

	}


	@Override
	public String getName() {
		return name;
	}

	public HashMap<String, Double> getParameters(){
		return this.parameters;
	}

	private boolean impossible(TWPlan currentPlan) {
		if (currentPlan == null || currentPlan.peek() == null || !currentPlan.hasNext())
			return true;
		TWDirection next = currentPlan.peek().getDirection();
		return getMemory().isCellBlocked(x + next.dx, y + next.dy, -1); 
	}

	public double fueling() //calculates fuel utility
	{
		double fuelLevel = getFuelLevel();
		if((Parameters.endTime - getEnvironment().schedule.getTime()) <= fuelLevel)
			return 0;
		double bufferFuelDeviation = (x + y)*parameters.get(UtilityParams.BUFFER_RATIO); 
		double distance = getDistanceTo(getEnvironment().getFuelingStation()); //nothing has been changed after this. 
		//		//reactive
		double utility =  normalDistribution(100, 0, bufferFuelDeviation, fuelLevel - distance);
		if(x + y <= Parameters.defaultSensorRange * 2 && fuelLevel/Parameters.defaultFuelLevel <= 0.5)
			return Math.max(utility, 70.0);
		return utility;
		//		System.out.println("Distance " + distance + " Buffer fuel " + bufferFuel + "  Fuel " + fuelLevel + " utility " + val); //test code. 
	}

	public void computeUtilities()
	{	
		this.holes = new PriorityQueue<TWHole>();
		this.tiles = new PriorityQueue<TWTile>();
		int xEnv = getEnvironment().getxDimension();
		int yEnv = getEnvironment().getyDimension();
		Double[][] utilities = new Double[xEnv][yEnv];
		int maxDistance = xEnv + yEnv;
		HashSet<Int2D> locations = getMemory().getTilesAndHoles();
		for(Int2D loc: locations)
		{
			TWAgentPercept percept = getMemory().getPerceptAt(loc.x,  loc.y);
			TWObject currObj = (TWObject) percept.getO();
			if(!(currObj instanceof TWTile || currObj instanceof TWHole))
				continue;
			double time = percept.getT();
			double distance = getDistanceTo(currObj);		
			double howOld = getEnvironment().schedule.getTime() - time;
			int i = currObj.getX();
			int j = currObj.getY();
			double decayMultiplier = normalDistribution(1, 0, parameters.get(UtilityParams.DEVIATION_MEM_DECAY), howOld);
			if(currObj instanceof TWTile)
				utilities[i][j] = normalDistribution(100, 0, parameters.get(UtilityParams.DEVIATION_TILES), (distance/maxDistance)) * decayMultiplier;
			if(currObj instanceof TWHole)
				utilities[i][j] = normalDistribution(100, 0, parameters.get(UtilityParams.DEVIATION_HOLES), (distance/maxDistance)) * decayMultiplier;
			currObj.setUtility(utilities[i][j]); //maintain a copy of utility
		}
		int xSearchLimit = parameters.get(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X).intValue();
		int ySearchLimit = parameters.get(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y).intValue();
		for(Int2D loc: locations)
		{
			TWAgentPercept percept = getMemory().getPerceptAt(loc.x,  loc.y);
			TWObject currObj = (TWObject) percept.getO();
			if(!(currObj instanceof TWTile || currObj instanceof TWHole))
				continue;
			int i = currObj.getX();
			int j = currObj.getY();

			if(utilities[i][j] < parameters.get(UtilityParams.THRESHOLD_EXPLORE))
				continue;
			if(pathLengthAdjustment)
			{
				currObj.setPathTo(pathGenerator.findPath(this.x, this.y, i, j, parameters.get(UtilityParams.DECAY_MEMORY_AFTER).intValue()));				
				if(currObj.getPathTo() == null)
				{
					currObj.setUtility(0.0);
					continue;
				}
			}
			// search nearby instead of searching the entire target array
			for(int k = i - xSearchLimit; k < i + xSearchLimit; k++)
			{
				for(int l = j - ySearchLimit; l < j + ySearchLimit; l++)
				{
					if((i == k && j == l) || !getEnvironment().isValidLocation(k, l) || utilities[k][l] == null)
						continue;
					double distance = getEnvironment().getDistance(i, j, k, l);

					//use utility from the unmodified utility array
					double neightbourUtility = normalDistribution(utilities[k][l], 0, parameters.get(UtilityParams.DEVIATION_NEIGHBOUR), distance / maxDistance);

					//update the object utility
					currObj.setUtility(combineUtilities(currObj.getUtility(), neightbourUtility));
				}
			}
			double pathFactor = 0;
			//add adjustment for the expected path length
			if(pathLengthAdjustment)				
				pathFactor = getDistanceTo(currObj) / currObj.getPathTo().size();

			if (currObj.getX() == loctargetx && currObj.getY() == loctargety)
			{
				//System.out.println("INTENTION CLASH!!");
				currObj.setUtility(0.0); 
			}
			else
			{
				if(pathLengthAdjustment)
					currObj.setUtility(currObj.getUtility() * pathFactor);
				else
					currObj.setUtility(currObj.getUtility());
			}
			if(currObj instanceof TWTile)
				tiles.add((TWTile) currObj);
			else 
				holes.add((TWHole) currObj);

		}
		//if(tiles.size() != 0 && holes.size() != 0)
		//System.out.println("Utility of top tile: " + tiles.peek().getUtility() + "Utility of top hole: " + holes.peek().getUtility());
	}

	public double pickUpTile()
	{
		if (this.tiles.peek() == null)
		{
			return 0;
		}
		switch(numberOfCarriedTiles())
		{
		case 0:
			return tiles.peek().getUtility() * parameters.get(UtilityParams.PICKUP_ZERO_TILES);
		case 1:
			return tiles.peek().getUtility() * parameters.get(UtilityParams.PICKUP_ONE_TILES);
		case 2:
			return tiles.peek().getUtility() * parameters.get(UtilityParams.PICKUP_TWO_TILES);
		default:
			return 0;
		}
	}

	public double putInHole()
	{
		if (this.holes.peek() == null)
		{
			return 0;
		}
		switch(numberOfCarriedTiles())
		{
		case 3:
			return holes.peek().getUtility() * parameters.get(UtilityParams.PUTDOWN_THREE_TILES);
		case 2:
			return holes.peek().getUtility() * parameters.get(UtilityParams.PUTDOWN_TWO_TILES);
		case 1:
			return holes.peek().getUtility() * parameters.get(UtilityParams.PUTDOWN_ONE_TILE);
		default:
			return 0;
		}
	}


	private HashMap<IntentionType, Double> options() {
		HashMap<IntentionType, Double> utilities = new HashMap<IntentionType, Double>();
		double sticky = parameters.get(UtilityParams.UTILITY_STICKY);
		IntentionType curInt = currIntention == null? IntentionType.OTHER: currIntention.getIntentionType();
		utilities.put(IntentionType.REFUEL, Math.min(100, fueling() + (curInt.equals(IntentionType.REFUEL)? sticky: 0)));
		utilities.put(IntentionType.PICKUPTILE, Math.min(100, pickUpTile() + (curInt.equals(IntentionType.PICKUPTILE)? sticky: 0)));
		utilities.put(IntentionType.FILLHOLE, Math.min(100, putInHole() + (curInt.equals(IntentionType.FILLHOLE)? sticky: 0)));
		return utilities;
	}

	private Intention filter(HashMap<IntentionType, Double> utilities) {
		boolean explore = true;
		IntentionType curInt = currIntention == null? IntentionType.OTHER: currIntention.getIntentionType();
		double threshold = parameters.get(UtilityParams.THRESHOLD_EXPLORE) + (curInt.equals(IntentionType.EXPLORE)? parameters.get(UtilityParams.UTILITY_STICKY): 0);
		for(Double value: utilities.values())
		{
			if(value > threshold)
				explore = false;
		}
		if (explore)
		{
			if(this.currIntention != null && currIntention.getIntentionType().equals(IntentionType.REFUEL) && 
					this.fuelLevel != Parameters.defaultFuelLevel)
				return currIntention;
			else{
				Int2D location = getExploreLocation();
				return new Intention(IntentionType.EXPLORE, location);
			}				

		}

		if(utilities.get(IntentionType.REFUEL) >= utilities.get(IntentionType.PICKUPTILE) && 
				utilities.get(IntentionType.REFUEL) >= utilities.get(IntentionType.FILLHOLE))
		{
			return new Intention(IntentionType.REFUEL, getEnvironment().getFuelingStation().getX(), 
					getEnvironment().getFuelingStation().getY());
		}
		if(utilities.get(IntentionType.PICKUPTILE) > utilities.get(IntentionType.FILLHOLE))
		{
			TWTile selected = tiles.peek();
			return new Intention(IntentionType.PICKUPTILE, selected.getX(),
					selected.getY());
		}
		TWHole selected  = holes.peek();
		return new Intention(IntentionType.FILLHOLE, selected.getX(),
				selected.getY());
	}

	private TWPlan plan(Intention intention) {
		LinkedList<TWThought> thoughts = new LinkedList<TWThought>();			//
		//
		//This is where we should use different pathgenerators based on which intention we have.
		//And make sure that we don't return null values.
		//
		TWPath path = null;
		switch(intention.getIntentionType())
		{
		case EXPLORE:
			// we pass 1 as decay, because we only want to consider the obstacles in the current sensor range
			path = pathGenerator.findPath(x, y, intention.getLocation().x, intention.getLocation().y, 1);
			break;
		case FILLHOLE:
			path = holes.peek().getPathTo();
			break;
		case PICKUPTILE:
			path = tiles.peek().getPathTo();
			break;
		case REFUEL:
			path = fuelPathGen.generateRefuelPath();
			if(DEBUG)
			{	
				if(path == null) //test code
					System.out.print("No path available");
				else {
					for (int z = 0; z<path.size(); z++)
					{
						System.out.print(path.getStep(z).getDirection());
					}
					break;
				} //end of test code
			}
		}
		if(path == null || !path.hasNext())
		{
			switch(intention.getIntentionType())
			{
			case REFUEL:
				thoughts.add(new TWThought(null, null));
				break;
			default: thoughts.add(new TWThought(TWAction.MOVE, findReactiveDirection(intention.getLocation().getX(), intention.getLocation().getY())));
			}
		}
		else
		{
			for(TWPathStep pathStep: path.getpath())
				thoughts.add(new TWThought(TWAction.MOVE, pathStep.getDirection()));
		}
		return new TWPlan(thoughts);
	}

	//	private Int2D getExploreLocation_old()
	//	{
	//		if(locationSnaps.size() <= 1)
	//			return getRandomLocation();
	//		
	//		//return end location in a straight line
	//		Int2D cur = new Int2D(x, y);
	//		Int2D prev = locationSnaps.getLast();
	//		Int2D prev2 = locationSnaps.get(locationSnaps.size() - 2);
	//		if(getEnvironment().getDistance(x, y, prev.x, prev.y) < parameters.get(UtilityParams.GAP_LOCATION_SNAP) / 2)
	//		{
	//			Int2D location1 = getLocationByDirection(cur, prev);
	//			if(location1 != null)
	//				return location1;
	//		}
	//		else
	//		{
	//			Int2D location2 = getLocationByDirection(prev, prev2);
	//			if(location2 != null)
	//				return location2;
	//		}
	//			
	//		//if we are already at the end		
	//		Int2D[] from = new Int2D[locationSnaps.size() + 1];
	//		from = locationSnaps.toArray(from);
	//		from[locationSnaps.size()] = new Int2D(x, y);
	//		return getFarthestRandomLocation(from);
	//	}

	private Int2D getExploreLocation() {
		Int2D locother = snapToCheckpoint(locotherx, locothery);
		Int2D targetOther = snapToCheckpoint(loctargetx, loctargety);
		ArrayList<Int2D> neighbourSnaps = getNeighbourSnaps();
		int maxAt = 0;
		int maxPriority = Integer.MIN_VALUE;
		for(int i = 0; i < neighbourSnaps.size(); i++)
		{
			int priority = getSnapPriority(neighbourSnaps.get(i), locother, targetOther);
			if(priority > maxPriority)
			{
				maxPriority = priority;
				maxAt = i;
			}
		}		
		return neighbourSnaps.get(maxAt);
	}

	private int getSnapPriority(Int2D snap, Int2D locother, Int2D targetOther)
	{
		if(!getEnvironment().isInBounds(snap.x, snap.y))
			return Integer.MIN_VALUE;
		if(snap.equals(locother) || snap.equals(targetOther))
			return 0;
		int myIndex = mySnaps.indexOf(snap);
		int otherIndex = otherSnaps.indexOf(snap);
		if(myIndex == -1)
			myIndex = Integer.MAX_VALUE;
		if(otherIndex == -1)
			otherIndex = Integer.MAX_VALUE;
		return Math.min(myIndex, otherIndex) - 1;
	}

	private ArrayList<Int2D> getNeighbourSnaps()
	{
		int length = Parameters.defaultSensorRange * 2 + 1;
		Int2D current = mySnaps.peekFirst();
		ArrayList<Int2D> snaps = new ArrayList<Int2D>(Arrays.asList(new Int2D[] {snapToCheckpoint(current.x + length, current.y), snapToCheckpoint(current.x - length, current.y), 
				snapToCheckpoint(current.x, current.y + length), snapToCheckpoint(current.x, current.y - length)}));
		Collections.shuffle(snaps);
		return snaps;
	}
	private Int2D snapToCheckpoint(int xp, int yp)
	{
		int range = Parameters.defaultSensorRange;
		int length = 2*range + 1;
		int xCheck, yCheck;
		if(xp >= getEnvironment().getxDimension() - length)
			xCheck = getEnvironment().getxDimension() - length;
		else
			xCheck = xp/length * length;
		if(yp >= getEnvironment().getyDimension() - length)
			yCheck = getEnvironment().getyDimension() - length;
		else
			yCheck = yp/length * length;
		return new Int2D(xCheck + range, yCheck + range);

	}
	private Int2D getFarthestRandomLocation(Int2D[] from) {
		Int2D farthest = null;
		int maxD = 0;
		for(int i = 0; i < 5; i++)
		{
			Int2D location = getRandomLocation();
			int distance = 0;
			for(Int2D point: from)
				distance += getEnvironment().getDistance(location.x, location.y, point.x, point.y);
			if(distance > maxD)
			{
				farthest = location;
				maxD = distance;
			}
		}
		return farthest;
	}

	private Int2D getLocationByDirection(Int2D cur, Int2D prev) {
		double speedX = cur.x - prev.x;
		double speedY = cur.y - prev.y;
		int endX, endY;
		if(speedX >= 0)
			endX = getEnvironment().getxDimension() - Parameters.defaultSensorRange;
		else
			endX = Parameters.defaultSensorRange;
		if(speedY >= 0)
			endY = getEnvironment().getyDimension() - Parameters.defaultSensorRange;
		else
			endY = Parameters.defaultSensorRange;

		double timeX = (endX - cur.x) / speedX;
		double timeY = (endY - cur.y) / speedY;
		if(timeX != 0 && timeY != 0 && endX - cur.x > 2 && endY - cur.y > 2)
		{
			if(timeX <= timeY)
				return new Int2D(endX, cur.y + (int)Math.round(speedY * timeX));
			return new Int2D(cur.x + (int)Math.round(speedX * timeY), endY);
		}
		return null;
	}

	private Int2D getRandomLocation()
	{
		Int2D location = getEnvironment().generateFarRandomLocation(getX(), getY(), 
				(getEnvironment().getxDimension() + getEnvironment().getyDimension()) / 2);
		while(getMemory().isCellBlocked(location.x, location.y, parameters.get(UtilityParams.DECAY_MEMORY_AFTER).intValue()))
			location = getEnvironment().generateFarRandomLocation(getX(), getY(), 
					(getEnvironment().getxDimension() + getEnvironment().getyDimension()) / 2);
		return location;
	}
	public TWDirection findReactiveDirection(int tx, int ty) {
		int sx = x;
		int sy = y;
		//try to find the best direction according to the given target
		if(tx > sx)
		{
			if(!getMemory().isCellBlocked(sx + 1, sy, -1))
				return TWDirection.E;
			if(ty > sy && !getMemory().isCellBlocked(sx, sy + 1, -1))
				return TWDirection.S;
			if(ty < sy && !getMemory().isCellBlocked(sx, sy + 1, -1))
				return TWDirection.N;			
		}
		else if(tx < sx)
		{
			if(!getMemory().isCellBlocked(sx - 1, sy, -1))
				return TWDirection.W;
			if(ty > sy && !getMemory().isCellBlocked(sx, sy + 1, -1))
				return TWDirection.S;
			if(ty < sy && !getMemory().isCellBlocked(sx, sy + 1, -1))
				return TWDirection.N;	
		}
		else
		{
			if(ty > sy && !getMemory().isCellBlocked(sx, sy + 1, -1))
				return TWDirection.S;
			if(ty < sy && !getMemory().isCellBlocked(sx, sy + 1, -1))
				return TWDirection.N;	
		}		

		//give up and return any unblocked side
		// at least one side is unblocked, as the code checks for it in the beginning
		if(!getMemory().isCellBlocked(sx, sy + 1, -1))
			return TWDirection.S;
		if(!getMemory().isCellBlocked(sx, sy - 1, -1))
			return TWDirection.N;
		if(!getMemory().isCellBlocked(sx + 1, sy, -1))
			return TWDirection.E;
		else
			return TWDirection.W;
	}


	/**
	 * Returns y value corresponding to the input x from the normal distribution function
	 * Consider using exponential function as well.
	 */
	private double normalDistribution(double peak, double mean, double std, double x)
	{
		return peak*Math.exp(-1*(Math.pow(x - mean, 2)) / (2 * Math.pow(std, 2)));
	}

	public  double combineUtilities (double x, double y) {
		double d = parameters.get(UtilityParams.WEIGHT_COMBINATION);
		x /= 100.0;
		y /= 100.0;
		double result = Math.pow(Math.tanh(atanh(Math.pow(x, d)) + atanh(Math.pow(y, d))),1/d);
		return result * 100;
	}
	private  double atanh(double x) {
		double result = 0.5 *(Math.log(1+x)-Math.log(1-x));
		return result;
	}

}



