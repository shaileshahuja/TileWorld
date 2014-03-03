package practicalreasoning;

import tileworld.Parameters;
import tileworld.agent.TWAgent;
import tileworld.agent.TWAgentPercept;
import tileworld.environment.TWEntity;
import tileworld.environment.TWEnvironment;
import tileworld.environment.TWTile;

public class Utility {
	
	public static double bufferRatio = 0.1;
	private static double maxUtility = 100;
	public static double deviationTiles = 0.3;
	public static double deviationHoles = 0.3;
	public static double pickup2 = 30, pickup1 = 60, pickup0 = 90;
	public static double putdown1 = 30, putdown2 = 60, putdown3 = 90;
	public static double fueling(TWAgent agent, TWEnvironment environment)
	{
		 double fuelLevel = agent.getFuelLevel();
		 if((Parameters.endTime - environment.schedule.getTime()) <= fuelLevel)
			 return 0;
		 double bufferFuel = (environment.getxDimension() + environment.getyDimension()) * bufferRatio;
		 double distance = agent.getDistanceTo(environment.getFuelingStation());
		 distance += bufferFuel; 
		 
		 //reactive
		 if (fuelLevel < distance)
			 return maxUtility;
		 return distance / fuelLevel * 100;
	}
	
	public static double pickUpTile(TWAgent agent, TWEnvironment environment)
	{
		int numberTiles = agent.numberOfCarriedTiles();
		if(numberTiles == 3)
			return 0;
		//List<Tiles, UT>
		//params: memory decay, distance
		//combination function -> clustering
		
		//Overall Utitlity -> final utility
		int maxDistance = environment.getxDimension() + environment.getyDimension();
		TWEntity entity = agent.getMemory().getClosestObjectInSensorRange(TWTile.class);
		if(entity != null)
		{
			double distance = entity.getDistanceTo(agent);
			if(distance == 0)
				return 99.99;
			return normalDistribution(100, 0, deviationTiles, distance/maxDistance);
		}
		switch(numberTiles)
		{
		case 0:
			return pickup0;
		case 1:
			return pickup1;
		case 2:
			return pickup2;
		default:
			return 0;
		}
	}
	
	private static double normalDistribution(double a, double b, double c, double x)
	{
		return a*Math.exp(-1*(Math.pow(x - b, 2)) / (2 * Math.pow(c, 2)));
	}
	
	public static double putInHole(TWAgent agent, TWEnvironment environment)
	{
		switch(agent.numberOfCarriedTiles())
		{
		case 3:
			return putdown3;
		case 2:
			return putdown2;
		case 1:
			return putdown1;
		default:
			return 0;
		}
	}
}
