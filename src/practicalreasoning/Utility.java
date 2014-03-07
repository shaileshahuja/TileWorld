package practicalreasoning;

import tileworld.Parameters;
import tileworld.agent.TWAgent;
import tileworld.agent.TWAgentPercept;
import tileworld.environment.TWEnvironment;
import tileworld.environment.TWHole;
import tileworld.environment.TWObstacle;
import tileworld.environment.TWTile;

public class Utility {

	public static double bufferRatio = 0.1;
	public static double deviationTiles = 0.3;
	public static double deviationHoles = 0.3;
	public static double deviationMemoryDecay = 15;
	public static double deviationNeighbour = 0.3;
	public static double neighbourWeight = 5;
	public static double pickup2 = .33, pickup1 = .66, pickup0 = 1;
	public static double putdown1 = .33, putdown2 = .66, putdown3 = 1;
	public static TWTile selectedTile = null;
	public static TWHole selectedHole = null;
	private static double selTileUtility = 0.0, selHoleUtility = 0.0;
	public static int XSearch = 3, YSearch = 3;
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
			return 99.99;
		return distance / fuelLevel * 100;
	}

	public static void updateNeighbourBasedUtility(TWAgent agent, TWEnvironment environment)
	{
		//List<Tiles, UT>
		//params: memory decay, distance
		//combination function -> clustering

		//Overall Utitlity -> final utility
		int x = environment.getxDimension();
		int y = environment.getyDimension();
		Double[][] utilities = new Double[x][y];
		int maxDistance = x + y;
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				TWAgentPercept percept = agent.getMemory().getObjects()[i][j];
				if(percept == null || percept.getO() instanceof TWObstacle)
					continue;
				double distance = agent.getDistanceTo(percept.getO());		
				double howOld = environment.schedule.getTime() - percept.getT();
				double decayMultiplier = normalDistribution(1, 0, deviationMemoryDecay, howOld);
				utilities[i][j] = normalDistribution(100, 0, deviationTiles, (distance/maxDistance)) * decayMultiplier;
			}
		}
//		Double[][] combinedUtilities = new Double[x][y];
		
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				if(utilities[i][j] == null)
					continue;
				double neighbourScore = 0.0;
				for(int k = i - XSearch; k < i + XSearch; k++)
				{
					for(int l = j - YSearch; l < l + YSearch; l++)
					{
						if(!environment.isValidLocation(x, y) || utilities[k][l] == null)
							continue;
						double distance = environment.getDistance(i, j, k, l);
						//TODO: improve this function
						double distScore = normalDistribution(neighbourWeight, 0, deviationNeighbour, (distance + utilities[k][l])/ (maxDistance+ utilities[k][l]));
						neighbourScore += distScore;
					}
				}
//				double finalScore = Math.min(100, (1 - neighbourWeight) * utilities[i][j] + neighbourWeight * neighbourScore);
				double finalScore = Math.min(100, utilities[i][j] + neighbourScore);
				if(agent.getMemory().getObjects()[i][j].getO() instanceof TWTile)
				{				
					if(finalScore > selTileUtility)
					{
						selTileUtility = finalScore;
						selectedTile = (TWTile) agent.getMemory().getObjects()[i][j].getO();
					}
				}
				else
				{
					if(finalScore > selHoleUtility)
					{
						selHoleUtility = finalScore;
						selectedHole = (TWHole) agent.getMemory().getObjects()[i][j].getO();
					}
				}
				
			}
		}
	}

	private static double normalDistribution(double a, double b, double c, double x)
	{
		return a*Math.exp(-1*(Math.pow(x - b, 2)) / (2 * Math.pow(c, 2)));
	}

	public static double pickUpTile(TWAgent agent, TWEnvironment environment)
	{
		switch(agent.numberOfCarriedTiles())
		{
		case 0:
			return selTileUtility * pickup0;
		case 1:
			return selTileUtility * pickup1;
		case 2:
			return selTileUtility * pickup2;
		default:
			return 0;
		}
	}

	public static double putInHole(TWAgent agent, TWEnvironment environment)
	{
		switch(agent.numberOfCarriedTiles())
		{
		case 3:
			return selHoleUtility * putdown3;
		case 2:
			return selHoleUtility * putdown2;
		case 1:
			return selHoleUtility * putdown1;
		default:
			return 0;
		}
	}
}
