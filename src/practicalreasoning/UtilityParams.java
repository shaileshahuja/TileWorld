package practicalreasoning;

import java.util.HashMap;

public class UtilityParams {
	public static final String RATIO_BUFFER = "bufferRatio";
	public static final String DEVIATION_TILES = "deviationTiles";
	public static final String DEVIATION_HOLES = "deviationHoles";
	public static final String DEVIATION_MEM_DECAY = "deviationMemoryDecay";
	public static final String DEVIATION_NEIGHBOUR = "deviationNeighbour";
	public static final String WEIGHT_NEIGHBOUR = "neighbourWeight";
	public static final String PICKUP_ZERO_TILES = "pickup0";
	public static final String PICKUP_ONE_TILES = "pickup1";
	public static final String PICKUP_TWO_TILES = "pickup2";
	public static final String PICKUP_ONE_HOLES = "putdown1";
	public static final String PICKUP_TWO_HOLES = "putdown2";
	public static final String PICKUP_THREE_HOLES = "putdown3";
	public static final String NEIGHBOUR_SEARCH_LIMIT_X = "XSearch";
	public static final String NEIGHBOUR_SEARCH_LIMIT_Y = "YSearch";
	
	public static HashMap<String, Double> defaultParams()
	{
		HashMap<String, Double> params = new HashMap<String, Double>();
		params.put(RATIO_BUFFER, 0.1);
		params.put(DEVIATION_TILES, 0.3);
		params.put(DEVIATION_HOLES, 0.3);
		params.put(DEVIATION_MEM_DECAY, 15.0);
		params.put(DEVIATION_NEIGHBOUR, 0.3);
		params.put(WEIGHT_NEIGHBOUR, 5.0);
		params.put(PICKUP_ZERO_TILES, 1.0);
		params.put(PICKUP_ONE_TILES, 0.66);
		params.put(PICKUP_TWO_TILES, 0.33);
		params.put(PICKUP_ONE_HOLES, 0.33);
		params.put(PICKUP_TWO_HOLES, 0.66);
		params.put(PICKUP_THREE_HOLES, 1.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_X, 3.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_Y, 3.0);
		return params;
	}
	
}
