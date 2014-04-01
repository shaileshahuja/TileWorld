package tileworld.planners;

import java.util.Random;

import tileworld.agent.TWAgent;
import tileworld.environment.TWDirection;

public class WavyRandomPathGenerator{

	TWAgent agent;
	
	private int waveX, waveY;
	Random r;
	
	public WavyRandomPathGenerator(TWAgent agent)
	{
		this.agent = agent;
		this.waveX = agent.getEnvironment().getxDimension() / 4 + 1;
		this.waveY = agent.getEnvironment().getyDimension() / 4 + 1;	
		r = new Random();
	}

	public TWPath generatePath() {

		
		int curX = agent.getX();
		int curY = agent.getY();
		
		boolean nearLeft = curX < agent.getEnvironment().getxDimension() / 2;
		
		TWDirection vertical = curY < agent.getEnvironment().getyDimension() / 2 ? TWDirection.S: TWDirection.N;
		TWPath path = new TWPath(0, 0);
		while(curY > 0 || curY < agent.getEnvironment().getyDimension())
		{
			for(int i = 0; i < curX; i++)
			{
				
			}
//			pathStep.
		}
		TWPathStep pathStep = new TWPathStep(curX, curY, TWDirection.E);
		
		
		
		return null;
	}

}
