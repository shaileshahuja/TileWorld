package practicalreasoning;

import java.util.Iterator;
import java.util.LinkedList;

import tileworld.agent.TWThought;

public class TWPlan implements Iterator<TWThought>{
	private LinkedList<TWThought> plan;
	public TWPlan(LinkedList<TWThought> thoughts)
	{
		this.plan = thoughts;
	}

	@Override
	public boolean hasNext() {
		return plan.size() > 0;
	}

	@Override
	public TWThought next() {
		return plan.pop();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	 
}
