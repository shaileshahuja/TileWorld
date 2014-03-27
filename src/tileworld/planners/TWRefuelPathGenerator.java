package tileworld.planners;
import practicalreasoning.Utility;
import tileworld.Parameters;
import tileworld.agent.SimpleTWAgent;
import tileworld.agent.TWAgent;
import tileworld.agent.UtilityAgent;
import tileworld.environment.TWEnvironment;


/**
 * find an a* path to the 0,0. If there is no path, find it to the next nearest tiles, 
 * if there is no path, find it to the next nearest tiles, and so on... 
 */

public class TWRefuelPathGenerator
{
	private AstarPathGenerator astarObject;
	private TWEnvironment environment;
	private UtilityAgent agent;
	
	public TWRefuelPathGenerator(UtilityAgent agent)
	{
		this.agent = agent;
		this.astarObject = new AstarPathGenerator(this.agent.getEnvironment(), this.agent, Integer.MAX_VALUE);
		this.environment = agent.getEnvironment();
	} 
	
	public TWPath generateRefuelPath(){
		
		int rows = environment.getxDimension() -1;
		int columns = environment.getyDimension() -1;
		int levels = rows + columns - 1; 
		int l;
		int x = 0;
		int y = 0;
		TWPath pathToReturn;
		
		for(l=0; l<=levels; l++)
		{
			if(isAgentinLevel(l, rows, columns))
			{
				return null;
			}
			
			if(l<=rows)
			{	y=0;
				x=l;
			}
			
			else if(l>rows)
			{
				//thresh = level-rows;
				x = rows;
				y = l - rows;	
			}
			
			while(y<=l && y<= columns)
			{
				
				pathToReturn = astarObject.findPath(agent.getX(), agent.getY(), x, y);
				if(pathToReturn != null)
				{
					double distance1 = pathToReturn.getpath().size();
					double distance2 = environment.getDistance(x, y, 0, 0);
					if (distance1 + distance2 <= agent.getFuelLevel())
					{
						return pathToReturn;
					}
				}
				
				x--;
				y++;
				
			}
			
			
		}
		
		
		return null;
	}
	
	
	private boolean isAgentinLevel(int level, int rows, int columns){
		int y = 0;
		int x = 0;
		if(level<=rows)
		{	y=0;
			x=level;
		}
		else if(level>rows)
		{
			//thresh = level-rows;
			x = rows;
			y = level - rows;	
		}
		
		{
			while(y<=level && y<= columns)
			{
				int agentx = agent.getX();
				int agenty = agent.getY();
				if(agentx == x && agenty ==y)
				{
					return true;
				}
				x--;
				y++;
				
			}
		}
		
			
		return false;
	}
		
	


}
