package practicalreasoning;

import java.util.HashMap;

public class UtilityParams {
	
	/**
	 * Range 0 to 1. Expected not more than 0.4. MEDIUM
	 */
	public static final String BUFFER_RATIO = "bufferRatio";
	
	/**
	 * Range 0 to 1. All DEVIATION ARE IMPORTANT
	 */
	public static final String DEVIATION_TILES = "deviationTiles";
	
	/**
	 * Range 0 to 1
	 */
	public static final String DEVIATION_HOLES = "deviationHoles";
	
	/**
	 * Integer range 0 to lifetime of the environment (5000 steps). Expected less than 100.
	 */
	public static final String DEVIATION_MEM_DECAY = "deviationMemoryDecay";
	
	/**
	 * Range 0 to 1
	 */
	public static final String DEVIATION_NEIGHBOUR = "deviationNeighbour";
	
	/**
	 * Weight given to the inverse combination function. Range 0 to 20
	 * 0 to 1 will have extremely high impact, 1-3 will have moderate impact, 3-20 will have low impact
	 */
	public static final String WEIGHT_COMBINATION = "combinationWeight";
	
	/**
	 * Range 0 to 1 for all pickup and putdown utilities. ALL SIX IMPORTANT
	 */
	public static final String PICKUP_ZERO_TILES = "pickup0";
	public static final String PICKUP_ONE_TILES = "pickup1";
	public static final String PICKUP_TWO_TILES = "pickup2";
	public static final String PUTDOWN_ONE_TILE = "putdown1";
	public static final String PUTDOWN_TWO_TILES = "putdown2";
	public static final String PUTDOWN_THREE_TILES = "putdown3";
	
	/**
	 * Integer range 1 to 20
	 */
	public static final String NEIGHBOUR_SEARCH_LIMIT_X = "XSearch";
	
	/**
	 * Integer range 1 to 20
	 */
	public static final String NEIGHBOUR_SEARCH_LIMIT_Y = "YSearch";
	
	/**
	 * Used for faking memory decay in isCellBlocked() method.
	 * Range depends on the range of the lifetime of an object (Paramters.lifeTime). Expected integer range: 1 - 5000
	 * NOTE: We are not allowed to use Paramters.lifeTime. We need to make a good guess here.
	 * Use communication to guess this value
	 */
	public static final String DECAY_MEMORY_AFTER = "memoryDecay";
	
	/**
	 * Double range 0 to 100. IMPORTANT
	 */
	public static final String THRESHOLD_EXPLORE = "exploreThreshold";

	/**
	 * Range 1 to environment's dimension/3. Currently max can be assumed to be 20.
	 */
	public static final String GAP_LOCATION_SNAP = "locationSnapGap";
	
	public static HashMap<String, Double> defaultParams()
	{
		HashMap<String, Double> params = new HashMap<String, Double>();
		params.put(BUFFER_RATIO, 0.2);
		params.put(DEVIATION_TILES, 0.3);
		params.put(DEVIATION_HOLES, 0.3);
		params.put(DEVIATION_MEM_DECAY, 15.0);
		params.put(DEVIATION_NEIGHBOUR, 0.3);
		params.put(WEIGHT_COMBINATION, 5.0);
		params.put(PICKUP_ZERO_TILES, 1.0);
		params.put(PICKUP_ONE_TILES, 0.66);
		params.put(PICKUP_TWO_TILES, 0.33);
		params.put(PUTDOWN_ONE_TILE, 0.33);
		params.put(PUTDOWN_TWO_TILES, 0.66);
		params.put(PUTDOWN_THREE_TILES, 1.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_X, 3.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_Y, 3.0);
		params.put(DECAY_MEMORY_AFTER, 50.0);
		params.put(THRESHOLD_EXPLORE, 10.0);
		params.put(GAP_LOCATION_SNAP, 10.0);
		return params;
	}
	
	public static HashMap<String, Double> defaultParams2()
	{
		HashMap<String, Double> params = new HashMap<String, Double>();
		params.put(BUFFER_RATIO, 0.2);
		params.put(DEVIATION_TILES, 0.3);
		params.put(DEVIATION_HOLES, 0.3);
		params.put(DEVIATION_MEM_DECAY, 15.0);
		params.put(DEVIATION_NEIGHBOUR, 0.3);
		params.put(WEIGHT_COMBINATION, 5.0);
		params.put(PICKUP_ZERO_TILES, 1.0);
		params.put(PICKUP_ONE_TILES, 1.0);
		params.put(PICKUP_TWO_TILES, 1.0);
		params.put(PUTDOWN_ONE_TILE, 1.0);
		params.put(PUTDOWN_TWO_TILES, 1.0);
		params.put(PUTDOWN_THREE_TILES, 1.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_X, 3.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_Y, 3.0);
		params.put(DECAY_MEMORY_AFTER, 50.0);
		params.put(THRESHOLD_EXPLORE, 10.0);
		params.put(GAP_LOCATION_SNAP, 10.0);
		return params;
	}
}
