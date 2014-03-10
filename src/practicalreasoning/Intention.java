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

	public void setLocation(Int2D loc) {
		location = loc;
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
		
		if(!other.intentionType.equals(this.intentionType))
			return false;
		if(this.intentionType.equals(IntentionType.EXPLORE))
			return true;
		return location.equals(other.location);
	}
	
	@Override
	public String toString() {
		return intentionType.name() + " " + location.toCoordinates();
	}
}
