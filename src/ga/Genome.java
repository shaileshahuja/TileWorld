/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.util.HashMap;
import practicalreasoning.UtilityParams;
/**
 *
 * @author Prerna
 */
public class Genome {
    public HashMap<String, Double> params = new HashMap<String, Double>();
    
    public static final String index0 = UtilityParams.BUFFER_RATIO;
    public static final String index1 = UtilityParams.DEVIATION_TILES;
    public static final String index2 = UtilityParams.DEVIATION_HOLES;
    public static final String index3 = UtilityParams.DEVIATION_MEM_DECAY;
    public static final String index4 = UtilityParams.DEVIATION_NEIGHBOUR;
    public static final String index5 = UtilityParams.WEIGHT_COMBINATION;
    public static final String index6 = UtilityParams.PICKUP_ZERO_TILES;
    public static final String index7 = UtilityParams.PICKUP_ONE_TILES;
    public static final String index8 = UtilityParams.PICKUP_TWO_TILES;
    public static final String index9 = UtilityParams.PUTDOWN_ONE_TILE;
    public static final String index10 = UtilityParams.PUTDOWN_TWO_TILES;
    public static final String index11 = UtilityParams.PUTDOWN_THREE_TILES;
    public static final String index12 = UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X;
    public static final String index13 = UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y;
    public static final String index14 = UtilityParams.DECAY_MEMORY_AFTER;
    public static final String index15 = UtilityParams.THRESHOLD_EXPLORE;
    
    public Genome (){
    	params.put(UtilityParams.BUFFER_RATIO, 0.1);
		params.put(UtilityParams.DEVIATION_TILES, 0.3);
		params.put(UtilityParams.DEVIATION_HOLES, 0.3);
		params.put(UtilityParams.DEVIATION_MEM_DECAY, 15.0);
		params.put(UtilityParams.DEVIATION_NEIGHBOUR, 0.3);
		params.put(UtilityParams.WEIGHT_COMBINATION, 5.0);
		params.put(UtilityParams.PICKUP_ZERO_TILES, 1.0);
		params.put(UtilityParams.PICKUP_ONE_TILES, 0.66);
		params.put(UtilityParams.PICKUP_TWO_TILES, 0.33);
		params.put(UtilityParams.PUTDOWN_ONE_TILE, 0.33);
		params.put(UtilityParams.PUTDOWN_TWO_TILES, 0.66);
		params.put(UtilityParams.PUTDOWN_THREE_TILES, 1.0);
		params.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X, 3.0);
		params.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y, 3.0);
		params.put(UtilityParams.DECAY_MEMORY_AFTER, 50.0);
		params.put(UtilityParams.THRESHOLD_EXPLORE, 10.0);
    }
    public double getDoubleVal(String str){
        return (double) params.get(str);
    }
    public void setDoubleVal(String str, double newVal){
        params.put(str, newVal);
    }
    public int getNumParams(){
        return params.size();
    }
    public HashMap<String, Double> getAllParams(){
        return params;
    }
    public void clone(Genome g){
        //params = (HashMap) g.getAllParams().clone(); // this gives a shallow copy
        params.put(UtilityParams.BUFFER_RATIO, g.getDoubleVal(UtilityParams.BUFFER_RATIO)); // 0-1 (practically, should not be above 0.5)
        params.put(UtilityParams.DEVIATION_TILES, g.getDoubleVal(UtilityParams.DEVIATION_TILES)); // 0-1
        params.put(UtilityParams.DEVIATION_HOLES, g.getDoubleVal(UtilityParams.DEVIATION_HOLES)); // 0-1
        params.put(UtilityParams.DEVIATION_MEM_DECAY, g.getDoubleVal(UtilityParams.DEVIATION_MEM_DECAY)); // 1-50 or 100
        params.put(UtilityParams.DEVIATION_NEIGHBOUR, g.getDoubleVal(UtilityParams.DEVIATION_NEIGHBOUR)); // 0-1
        params.put(UtilityParams.WEIGHT_COMBINATION, g.getDoubleVal(UtilityParams.WEIGHT_COMBINATION));
        params.put(UtilityParams.PICKUP_ZERO_TILES, g.getDoubleVal(UtilityParams.PICKUP_ZERO_TILES)); // 0-1
        params.put(UtilityParams.PICKUP_ONE_TILES, g.getDoubleVal(UtilityParams.PICKUP_ONE_TILES)); // 0-1
        params.put(UtilityParams.PICKUP_TWO_TILES, g.getDoubleVal(UtilityParams.PICKUP_TWO_TILES)); // 0-1
        params.put(UtilityParams.PUTDOWN_ONE_TILE, g.getDoubleVal(UtilityParams.PUTDOWN_ONE_TILE)); // 0-1
        params.put(UtilityParams.PUTDOWN_TWO_TILES, g.getDoubleVal(UtilityParams.PUTDOWN_TWO_TILES)); // 0-1
        params.put(UtilityParams.PUTDOWN_THREE_TILES, g.getDoubleVal(UtilityParams.PUTDOWN_THREE_TILES)); // 0-1
        params.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X, g.getDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X)); // 1 to 20 or env width (int)
        params.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y, g.getDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y)); // 1 to 20 or env height (int)
        params.put(UtilityParams.DECAY_MEMORY_AFTER, g.getDoubleVal(UtilityParams.DECAY_MEMORY_AFTER)); // 1 to 20 or env width (int)
        params.put(UtilityParams.THRESHOLD_EXPLORE, g.getDoubleVal(UtilityParams.THRESHOLD_EXPLORE)); // 1 to 20 or env height (int)
    }
    public static final String getParamStr(int i){
        String paramStr="";
        switch(i){
            case 0: paramStr=index0; break;
            case 1: paramStr=index1; break;
            case 2: paramStr=index2; break;
            case 3: paramStr=index3; break;
            case 4: paramStr=index4; break;
            case 5: paramStr=index5; break;
            case 6: paramStr=index6; break;
            case 7: paramStr=index7; break;
            case 8: paramStr=index8; break;
            case 9: paramStr=index9; break;
            case 10: paramStr=index10; break;
            case 11: paramStr=index11; break;
            case 12: paramStr=index12; break;
            case 13: paramStr=index13; break;
            case 14: paramStr=index14; break;
            case 15: paramStr=index15; break;
            default: paramStr=""; break;
        }
        return paramStr;
    }
    public void printParams(){
        for(int i=0; i<getNumParams(); i++){
            String paramStr = getParamStr(i);
            double paramVal = getDoubleVal(paramStr);
            //System.out.println("WINNER");
            System.out.println(paramStr+" : "+paramVal);
        }
    }
}
