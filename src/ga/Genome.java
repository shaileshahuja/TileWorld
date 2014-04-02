/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.util.HashMap;

/**
 *
 * @author Prerna
 */
public class Genome {
    public HashMap<String, Double> params = new HashMap<String, Double>();
    
    public static final String RATIO_BUFFER = "bufferRatio";
    public static final String DEVIATION_TILES = "deviationTiles";
    public static final String DEVIATION_HOLES = "deviationHoles";
    public static final String DEVIATION_MEM_DECAY = "deviationMemoryDecay";
    public static final String DEVIATION_NEIGHBOUR = "deviationNeighbour";
    public static final String PICKUP_ZERO_TILES = "pickup0";
    public static final String PICKUP_ONE_TILES = "pickup1";
    public static final String PICKUP_TWO_TILES = "pickup2";
    public static final String PUTDOWN_ONE_HOLES = "putdown1";
    public static final String PUTDOWN_TWO_HOLES = "putdown2";
    public static final String PUTDOWN_THREE_HOLES = "putdown3";
    public static final String NEIGHBOUR_SEARCH_LIMIT_X = "XSearch";
    public static final String NEIGHBOUR_SEARCH_LIMIT_Y = "YSearch";
    
    public static final String index0 = RATIO_BUFFER;
    public static final String index1 = DEVIATION_TILES;
    public static final String index2 = DEVIATION_HOLES;
    public static final String index3 = DEVIATION_MEM_DECAY;
    public static final String index4 = DEVIATION_NEIGHBOUR;
    public static final String index5 = PICKUP_ZERO_TILES;
    public static final String index6 = PICKUP_ONE_TILES;
    public static final String index7 = PICKUP_TWO_TILES;
    public static final String index8 = PUTDOWN_ONE_HOLES;
    public static final String index9 = PUTDOWN_TWO_HOLES;
    public static final String index10 = PUTDOWN_THREE_HOLES;
    public static final String index11 = NEIGHBOUR_SEARCH_LIMIT_X;
    public static final String index12 = NEIGHBOUR_SEARCH_LIMIT_Y;
    
    
    public Genome (){
        params.put(RATIO_BUFFER, 0.1); // 0-1 (practically, should not be above 0.5)
	params.put(DEVIATION_TILES, 0.3); // 0-1
	params.put(DEVIATION_HOLES, 0.3); // 0-1
	params.put(DEVIATION_MEM_DECAY, 15.0); // 1-50 or 100
	params.put(DEVIATION_NEIGHBOUR, 0.3); // 0-1
	params.put(PICKUP_ZERO_TILES, 1.0); // 0-1
	params.put(PICKUP_ONE_TILES, 0.66); // 0-1
	params.put(PICKUP_TWO_TILES, 0.33); // 0-1
	params.put(PUTDOWN_ONE_HOLES, 0.33); // 0-1
	params.put(PUTDOWN_TWO_HOLES, 0.66); // 0-1
	params.put(PUTDOWN_THREE_HOLES, 1.0); // 0-1
	params.put(NEIGHBOUR_SEARCH_LIMIT_X, 3.0); // 1 to 20 or env width (int)
	params.put(NEIGHBOUR_SEARCH_LIMIT_Y, 3.0); // 1 to 20 or env height (int)
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
        params.put(RATIO_BUFFER, g.getDoubleVal(RATIO_BUFFER)); // 0-1 (practically, should not be above 0.5)
	params.put(DEVIATION_TILES, g.getDoubleVal(DEVIATION_TILES)); // 0-1
	params.put(DEVIATION_HOLES, g.getDoubleVal(DEVIATION_HOLES)); // 0-1
	params.put(DEVIATION_MEM_DECAY, g.getDoubleVal(DEVIATION_MEM_DECAY)); // 1-50 or 100
	params.put(DEVIATION_NEIGHBOUR, g.getDoubleVal(DEVIATION_NEIGHBOUR)); // 0-1
	params.put(PICKUP_ZERO_TILES, g.getDoubleVal(PICKUP_ZERO_TILES)); // 0-1
	params.put(PICKUP_ONE_TILES, g.getDoubleVal(PICKUP_ONE_TILES)); // 0-1
	params.put(PICKUP_TWO_TILES, g.getDoubleVal(PICKUP_TWO_TILES)); // 0-1
	params.put(PUTDOWN_ONE_HOLES, g.getDoubleVal(PUTDOWN_ONE_HOLES)); // 0-1
	params.put(PUTDOWN_TWO_HOLES, g.getDoubleVal(PUTDOWN_TWO_HOLES)); // 0-1
	params.put(PUTDOWN_THREE_HOLES, g.getDoubleVal(PUTDOWN_THREE_HOLES)); // 0-1
	params.put(NEIGHBOUR_SEARCH_LIMIT_X, g.getDoubleVal(NEIGHBOUR_SEARCH_LIMIT_X)); // 1 to 20 or env width (int)
	params.put(NEIGHBOUR_SEARCH_LIMIT_Y, g.getDoubleVal(NEIGHBOUR_SEARCH_LIMIT_Y)); // 1 to 20 or env height (int)
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
