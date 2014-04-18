package practicalreasoning;

import java.util.HashMap;

public class UtilityParams {
	
	/**
	 * Range 0 to 1. Expected not more than 0.3.
	 */
	public static final String BUFFER_RATIO = "bufferRatio";
	
	/**
	 * Range 0 to 1. 
	 */
	public static final String DEVIATION_TILES = "deviationTiles";
	
	/**
	 * Range 0 to 1
	 */
	public static final String DEVIATION_HOLES = "deviationHoles";
	
	/**
	 * Range 0 to lifetime of the environment (5000 steps). Expected less than 100.
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
	 * Range 0 to 1 for all pickup and putdown utilities
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
	 */
	public static final String DECAY_MEMORY_AFTER = "memoryDecay";
	
	/**
	 * Double range 0 to 100. 
	 */
	public static final String THRESHOLD_EXPLORE = "exploreThreshold";

	/**
	 * Interger Range 5 to 100. 
	 */
	public static final String LENGTH_SNAPS = "snapsLength";
	
	/**
	 * Integer Range 5 to 20
	 */
	public static final String UTILITY_STICKY = "stickyUtility";
	
	public static HashMap<String, Double> defaultParams()
	{
		HashMap<String, Double> params = new HashMap<String, Double>();
		/// DEFAULT
		/*params.put(BUFFER_RATIO, 0.2);
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
		params.put(LENGTH_SNAPS, 20.0);
		params.put(UTILITY_STICKY, 5.0);*/
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
		params.put(LENGTH_SNAPS, 20.0);
		params.put(UTILITY_STICKY, 5.0);
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
		params.put(LENGTH_SNAPS, 20.0);
		params.put(UTILITY_STICKY, 5.0);
		return params;
	}
	public static HashMap<String, Double> bestParamsEnv1()
	{
		/// For sensor = 2, SCORE = 79
		HashMap<String, Double> params = new HashMap<String, Double>();
		params.put(BUFFER_RATIO, 0.2);
		params.put(DEVIATION_TILES, 0.7815); // changed
		params.put(DEVIATION_HOLES, 0.3);
		params.put(DEVIATION_MEM_DECAY, 4.0); // changed 
		params.put(DEVIATION_NEIGHBOUR, 0.3);
		params.put(WEIGHT_COMBINATION, 5.0);
		params.put(PICKUP_ZERO_TILES, 1.0);
		params.put(PICKUP_ONE_TILES, 0.5927 ); // changed
		params.put(PICKUP_TWO_TILES, 0.3300); // changed
		params.put(PUTDOWN_ONE_TILE, 0.3300); //changed
		params.put(PUTDOWN_TWO_TILES, 0.1856); // changed
		params.put(PUTDOWN_THREE_TILES, 0.6850); //changed
		params.put(NEIGHBOUR_SEARCH_LIMIT_X, 3.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_Y, 3.0);
		params.put(DECAY_MEMORY_AFTER, 50.0);
		params.put(THRESHOLD_EXPLORE, 10.0);
		params.put(LENGTH_SNAPS, 20.0);
		params.put(UTILITY_STICKY, 5.0);
		return params;
	}
	public static HashMap<String, Double> bestParamsEnv2()
	{
		/// For sensor = 2, BEST SCORE: 653
		HashMap<String, Double> params = new HashMap<String, Double>();
		params.put(BUFFER_RATIO, 0.2);
		params.put(DEVIATION_TILES, 0.3);
		params.put(DEVIATION_HOLES, 0.3282); //changed
		params.put(DEVIATION_MEM_DECAY, 15.0); 
		params.put(DEVIATION_NEIGHBOUR, 0.3);
		params.put(WEIGHT_COMBINATION, 5.0);
		params.put(PICKUP_ZERO_TILES, 1.0);
		params.put(PICKUP_ONE_TILES, 0.8829 ); //changed
		params.put(PICKUP_TWO_TILES, 0.3300); //changed
		params.put(PUTDOWN_ONE_TILE, 0.3300); // changed
		params.put(PUTDOWN_TWO_TILES, 0.6600 ); // changed
		params.put(PUTDOWN_THREE_TILES, 1.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_X, 3.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_Y, 3.0);
		params.put(DECAY_MEMORY_AFTER, 50.0);
		params.put(THRESHOLD_EXPLORE, 10.0);
		params.put(LENGTH_SNAPS, 20.0);
		params.put(UTILITY_STICKY, 5.0);
		return params;
	}
	public static HashMap<String, Double> bestParamsEnv3()
	{
		/// For sensor = 2
		HashMap<String, Double> params = new HashMap<String, Double>();
		params.put(BUFFER_RATIO, 0.2);
		params.put(DEVIATION_TILES, 0.7147); // changed
		params.put(DEVIATION_HOLES, 0.1245); //changed
		params.put(DEVIATION_MEM_DECAY, 98.0000); // changed
		params.put(DEVIATION_NEIGHBOUR, 0.3);
		params.put(WEIGHT_COMBINATION, 5.0);
		params.put(PICKUP_ZERO_TILES, 1.0);
		params.put(PICKUP_ONE_TILES, 0.4928); // changed
		params.put(PICKUP_TWO_TILES, 0.9876); // changed
		params.put(PUTDOWN_ONE_TILE, 0.6484); // changed
		params.put(PUTDOWN_TWO_TILES, 0.6600); // changed
		params.put(PUTDOWN_THREE_TILES, 0.3171); // changed
		params.put(NEIGHBOUR_SEARCH_LIMIT_X, 3.0);
		params.put(NEIGHBOUR_SEARCH_LIMIT_Y, 3.0);
		params.put(DECAY_MEMORY_AFTER, 50.0);
		params.put(THRESHOLD_EXPLORE, 18.0000); // changed
		params.put(LENGTH_SNAPS, 71.0000 ); // changed
		params.put(UTILITY_STICKY, 11.0000); //changed
		return params;
	}
}
