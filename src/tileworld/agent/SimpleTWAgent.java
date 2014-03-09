/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tileworld.agent;

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
		TWEntity current = (TWEntity) getMemory().getObjectAt(x, y);		
		if(carriedTiles.size() < 3 & current instanceof TWTile)
			return new TWThought(TWAction.PICKUP, null);
		else if(hasTile() && current instanceof TWHole)
			return new TWThought(TWAction.PUTDOWN, null);
		else
			return new TWThought(TWAction.MOVE,getRandomDirection());
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
				break;
			case PUTDOWN:
				TWHole hole = (TWHole)getEnvironment().getObjectGrid().get(x, y);
				this.putTileInHole(hole);
				this.getMemory().removeObject(hole);
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
