package practicalreasoning;

import sim.util.Int2D;

public class Intention {
	private IntentionType intentionType;
	public IntentionType getIntentionType() {
		return intentionType;
	}

	public Int2D getLocation() {
		return location;
	}

	private Int2D location;
	
	public Intention(IntentionType intentionType, Int2D int2d)
	{
		this.intentionType = intentionType;
		this.location = int2d;
	}
	
	public Intention(IntentionType intentionType, int x, int y)
	{
		this.intentionType = intentionType;
		this.location = new Int2D(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(!(obj instanceof Intention))
			return false;
		Intention other = (Intention) obj;
		if(!other.intentionType.equals(intentionType))
			return false;
		return location.equals(other.location);
	}
}
