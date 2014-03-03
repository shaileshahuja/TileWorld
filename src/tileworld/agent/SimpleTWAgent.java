/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tileworld.agent;

import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import practicalreasoning.Intention;
import practicalreasoning.Utility;
import tileworld.environment.TWDirection;
import tileworld.environment.TWEntity;
import tileworld.environment.TWEnvironment;
import tileworld.environment.TWHole;
import tileworld.environment.TWTile;
import tileworld.exceptions.CellBlockedException;

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
public class SimpleTWAgent extends TWAgent{
	public static double exploreThreshold = 10;
	public SimpleTWAgent(int xpos, int ypos, TWEnvironment env, double fuelLevel) {
		super(xpos,ypos,env,fuelLevel);
	}

	protected TWThought think() {
		//        getMemory().getClosestObjectInSensorRange(Tile.class);
//		System.out.println("Simple Score: " + this.score);
		HashMap<Intention, Double> utilities = options();
		Intention intention = filter(utilities);
		
		TWEntity current = (TWEntity) getMemory().getMemoryGrid().get(x,  y);		
		if(carriedTiles.size() < 3 & current instanceof TWTile)
			return new TWThought(TWAction.PICKUP, null);
		else if(hasTile() && current instanceof TWHole)
			return new TWThought(TWAction.PUTDOWN, null);
		else
			return new TWThought(TWAction.MOVE,getRandomDirection());
	}

	private Intention filter(HashMap<Intention, Double> utilities) {
		boolean explore = true;
		for(Double value: utilities.values())
		{
			if(value > exploreThreshold)
				explore = false;
		}
		if (explore)
			return Intention.EXPLORE;
		
		if(utilities.get(Intention.REFUEL) >= utilities.get(Intention.PICKUPTILE) && 
				utilities.get(Intention.REFUEL) >= utilities.get(Intention.FILLHOLE))
			return Intention.REFUEL;
		if(utilities.get(Intention.PICKUPTILE) > utilities.get(Intention.FILLHOLE))
			return Intention.PICKUPTILE;
		return Intention.FILLHOLE;
	}

	private HashMap<Intention, Double> options() {
		HashMap<Intention, Double> utilities = new HashMap<Intention, Double>();
		utilities.put(Intention.REFUEL, Utility.fueling(this, getEnvironment()));
		utilities.put(Intention.PICKUPTILE, Utility.pickUpTile(this, getEnvironment()));
		utilities.put(Intention.FILLHOLE, Utility.putInHole(this, getEnvironment()));
		return utilities;
	}

	@Override
	protected void act(TWThought thought) {

		//You can do:
		//move(thought.getDirection())
		//pickUpTile(Tile)
		//putTileInHole(Hole)
		//refuel()

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
				this.getMemory().getMemoryGrid().set(x, y, null);
				break;
			case PUTDOWN:
				TWHole hole = (TWHole)getEnvironment().getObjectGrid().get(x, y);
				this.putTileInHole(hole);
				this.getMemory().removeObject(hole);
				this.getMemory().getMemoryGrid().set(x, y, null);
			case REFUEL:
				break;
			default:
				break;			
			}
		}  catch (CellBlockedException ex) {

			// Cell is blocked, replan?
		}
	}


	private TWDirection getRandomDirection(){

		TWDirection randomDir = TWDirection.values()[this.getEnvironment().random.nextInt(5)];

		if(this.getX()>=this.getEnvironment().getxDimension() ){
			randomDir = TWDirection.W;
		}else if(this.getX()<=1 ){
			randomDir = TWDirection.E;
		}else if(this.getY()<=1 ){
			randomDir = TWDirection.S;
		}else if(this.getY()>=this.getEnvironment().getxDimension() ){
			randomDir = TWDirection.N;
		}

		return randomDir;

	}

	@Override
	public String getName() {
		return "Dumb Agent";
	}
}
