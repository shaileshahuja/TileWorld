

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tileworld.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

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
	private HashMap<String, Double> parameters; // not to be confused with a parameter object
	private PriorityQueue<TWHole> holes;
	private PriorityQueue<TWTile> tiles;
	private TWPlan currentPlan = null;
	private Intention currIntention = null;
	private AstarPathGenerator pathGenerator;
	private TWRefuelPathGenerator fuelPathGen;
	private LinkedList<Int2D> locationSnaps;
	private ArrayList<Int2D> corners;
	
	// Communication variables
	private Message msgReceived;
	private Message msgToSend;
	private String curRequest;
	private Int2D locOther;
	
	//private ReactivePathGenerator reactivePathGen;
	private String name;
	public UtilityAgent2(String name, int xpos, int ypos, TWEnvironment env, double fuelLevel, HashMap<String, Double> parameters) {
		super(xpos,ypos,env,fuelLevel);
		pathGenerator = new AstarPathGenerator(env, this, Integer.MAX_VALUE);
		fuelPathGen = new TWRefuelPathGenerator(this);
		this.parameters = parameters;
		this.name = name;
		this.locationSnaps = new LinkedList<Int2D>();
		this.corners = new ArrayList<Int2D>(4); //doesn't do anything for now. it's use has been commented out. 
		corners.add(new Int2D(Parameters.defaultSensorRange, Parameters.defaultSensorRange));
		corners.add(new Int2D(Parameters.defaultSensorRange, getEnvironment().getyDimension() - Parameters.defaultSensorRange));
		corners.add(new Int2D(getEnvironment().getxDimension() - Parameters.defaultSensorRange, Parameters.defaultSensorRange));
		corners.add(new Int2D(getEnvironment().getxDimension() - Parameters.defaultSensorRange, getEnvironment().getyDimension() - Parameters.defaultSensorRange));
		
	}

	protected TWThought think() {
		receiveMsg();
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
			}
		}
		if(DEBUG)
		{
			System.out.print(name + " ");
			System.out.println(currIntention);
			if(currentPlan.peek().getAction()!=null)
			System.out.println("Current plan's next action is" + currentPlan.peek());
			else System.out.println("Current Plan is to wait");
			System.out.println(this.getScore());
			System.out.println(this.getFuelLevel());
		}
		

		//store past locations
		if(getEnvironment().schedule.getSteps() % parameters.get(UtilityParams.GAP_LOCATION_SNAP).intValue() == 0)
		{
			locationSnaps.add(new Int2D(x, y));
			if(locationSnaps.size() > 5)
				locationSnaps.remove();
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
				this.pickUpTile(tile);
				this.getMemory().removeObject(tile);
				break;
			case PUTDOWN:
				TWHole hole = (TWHole)getEnvironment().getObjectGrid().get(x, y);
				this.putTileInHole(hole);
				this.getMemory().removeObject(hole);
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
		if(msgReceived.getX() >-1 && msgReceived.getY()>-1){
			locOther = new Int2D(msgReceived.getX(), msgReceived.getY());
		}
		if(msgReceived.getRequest()!="" && msgReceived.getRequest()!=null){
			curRequest = msgReceived.getRequest();
		}
		else{ curRequest = null;}
		if(msgReceived.getObs1()!=null && ((msgReceived.getObs1().getO() instanceof TWObstacle) || (msgReceived.getObs1().getO() instanceof TWHole) || (msgReceived.getObs1().getO() instanceof TWTile))){
			int rx = msgReceived.getObs1().getO().getX();
			int ry = msgReceived.getObs1().getO().getY();
			if( msgReceived.getObs1().getT() > this.memory.getPerceptAt(rx, ry).getT()){
				this.getMemory().updateMemory(msgReceived.getObs1());
			}
		}
		if(msgReceived.getResponse()!=null && ((msgReceived.getResponse().getO() instanceof TWObstacle) || (msgReceived.getResponse().getO() instanceof TWHole) || (msgReceived.getResponse().getO() instanceof TWTile))){
			int rx = msgReceived.getResponse().getO().getX();
			int ry = msgReceived.getResponse().getO().getY();
			if( msgReceived.getResponse().getT() > this.memory.getPerceptAt(rx, ry).getT()){
				this.getMemory().updateMemory(msgReceived.getResponse());
			}
		}
	}
	private void sendMsg(){
		// create message to send using a variety of things
		int sendLocX = this.getX();
		int sendLocY = this.getY();
		String myReq="";
		if(this.currIntention.getIntentionType().equals("EXPLORE")){
			if(this.carriedTiles.size()==3) myReq = "HOLE";
			else if(carriedTiles.size()==0) myReq = "TILE";
			else myReq = "ANYTHING";
		}
		if(curRequest!=null && curRequest!=""){
			TWEntity resp;
			switch(curRequest.charAt(0)){
			case 'T': resp = this.getMemory().getNearbyTile(sendLocX, sendLocY, parameters.get(UtilityParams.DEVIATION_MEM_DECAY));
				break;
			case 'H': resp = this.getMemory().getNearbyHole(sendLocX, sendLocY, parameters.get(UtilityParams.DEVIATION_MEM_DECAY));
			default: 
				break;
			}
		}
		this.msgToSend = new Message(); // put values in constructor
		PostBox.put(this.name, msgToSend);
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
		double bufferFuelDeviation = (this.getX()+ this.getY())*parameters.get(UtilityParams.BUFFER_RATIO); 
		double distance = getDistanceTo(getEnvironment().getFuelingStation()); //nothing has been changed after this. 
//		//reactive
		return normalDistribution(100, 0, bufferFuelDeviation, fuelLevel - distance);
//		System.out.println("Distance " + distance + " Buffer fuel " + bufferFuel + "  Fuel " + fuelLevel + " utility " + val); //test code. 
	}

	public void computeUtilities()
	{	
		this.holes = new PriorityQueue<TWHole>();
		this.tiles = new PriorityQueue<TWTile>();
		int x = getEnvironment().getxDimension();
		int y = getEnvironment().getyDimension();
		Double[][] utilities = new Double[x][y];
		ArrayList<Int2D> targets = new ArrayList<Int2D>();
		int maxDistance = x + y;
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				TWObject currObj = (TWObject) getMemory().getObjectAt(i, j);
				if(currObj == null || currObj instanceof TWObstacle)
					continue;
				double time = getMemory().getPerceptAt(i, j).getT();
				targets.add(new Int2D(i, j));
				double distance = getDistanceTo(currObj);		
				double howOld = getEnvironment().schedule.getTime() - time;
				double decayMultiplier = normalDistribution(1, 0, parameters.get(UtilityParams.DEVIATION_MEM_DECAY), howOld);
				if(currObj instanceof TWTile)
					utilities[i][j] = normalDistribution(100, 0, parameters.get(UtilityParams.DEVIATION_TILES), (distance/maxDistance)) * decayMultiplier;
				if(currObj instanceof TWHole)
					utilities[i][j] = normalDistribution(100, 0, parameters.get(UtilityParams.DEVIATION_HOLES), (distance/maxDistance)) * decayMultiplier;
				currObj.setUtility(utilities[i][j]); //maintain a copy of utility
			}
		}
		int xSearchLimit = parameters.get(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X).intValue();
		int ySearchLimit = parameters.get(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y).intValue();
		for(Int2D target: targets)
		{
			int i = target.x;
			int j = target.y;
			TWObject currObj = (TWObject) getMemory().getObjectAt(i, j);
			currObj.setPathTo(pathGenerator.findPath(this.x, this.y, i, j, parameters.get(UtilityParams.DECAY_MEMORY_AFTER).intValue()));				
			if(currObj.getPathTo() == null)
				continue;

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

			//add adjustment for the expected path length
			double pathLengthAdjustment = getDistanceTo(currObj) / currObj.getPathTo().size();
			currObj.setUtility(currObj.getUtility() * pathLengthAdjustment);
			if(currObj instanceof TWTile)
				tiles.add((TWTile) currObj);
			else
				holes.add((TWHole) currObj);
		}
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
		utilities.put(IntentionType.REFUEL, fueling());
		utilities.put(IntentionType.PICKUPTILE, pickUpTile());
		utilities.put(IntentionType.FILLHOLE, putInHole());
		return utilities;
	}

	private Intention filter(HashMap<IntentionType, Double> utilities) {
		boolean explore = true;
		for(Double value: utilities.values())
		{
			if(value > parameters.get(UtilityParams.THRESHOLD_EXPLORE))
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


	private Int2D getExploreLocation() {
		
		if(locationSnaps.size() <= 1)
			return getRandomLocation();
		
		//return end location in a straight line
		Int2D cur = new Int2D(x, y);
		Int2D prev = locationSnaps.getLast();
		Int2D prev2 = locationSnaps.get(locationSnaps.size() - 2);
		if(getEnvironment().getDistance(x, y, prev.x, prev.y) < parameters.get(UtilityParams.GAP_LOCATION_SNAP) / 2)
		{
			Int2D location1 = getLocationByDirection(cur, prev);
			if(location1 != null)
				return location1;
		}
		else
		{
			Int2D location2 = getLocationByDirection(prev, prev2);
			if(location2 != null)
				return location2;
		}
			
		//if we are already at the end		
		Int2D[] from = new Int2D[locationSnaps.size() + 1];
		from = locationSnaps.toArray(from);
		from[locationSnaps.size()] = new Int2D(x, y);
		return getFarthestRandomLocation(from);
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

	private Int2D getFarthestCorner(Int2D[] from) { //function does nothing. Function call has been commented out somewhere above. 
		Int2D farthest = null;
		int maxD = 0;
		for(Int2D corner: corners)
		{
			int distance = 0;
			for(Int2D point: from)
				distance += getEnvironment().getDistance(corner.x, corner.y, point.x, point.y);
			if(distance > maxD)
			{
				farthest = corner;
				maxD = distance;
			}
		}
		return farthest;
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



