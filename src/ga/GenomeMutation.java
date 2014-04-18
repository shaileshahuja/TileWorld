/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.util.*;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import practicalreasoning.UtilityParams;
/**
 *
 * @author Prerna
 */
public class GenomeMutation implements EvolutionaryOperator<Genome> {
    private HashMap<String, Double> upperrange = new HashMap<String, Double>();
    public HashMap<String, Double> lowerrange = new HashMap<String, Double>();
    //private double probability=0.02;
    private double probability=0.1;
    private boolean mutation=false;
    //private int mutationCount = 2; // we mutate 2 params
    private int mutationCount = 3; 
    private int numOfParams;
    
    public GenomeMutation(){
        //upperrange.put(UtilityParams.BUFFER_RATIO, 0.4); // 0-1 (practically, should not be above 0.5)
        upperrange.put(UtilityParams.DEVIATION_TILES, 1.0); // 0-1
        upperrange.put(UtilityParams.DEVIATION_HOLES, 1.0); // 0-1
        upperrange.put(UtilityParams.DEVIATION_MEM_DECAY, 100.0); // 1-50 or 100
        //upperrange.put(UtilityParams.DEVIATION_NEIGHBOUR, 1.0); // 0-1
        upperrange.put(UtilityParams.WEIGHT_COMBINATION, 20.0); // 0-1
        upperrange.put(UtilityParams.PICKUP_ZERO_TILES, 1.0); // 0-1
        upperrange.put(UtilityParams.PICKUP_ONE_TILES, 1.0); // 0-1
        upperrange.put(UtilityParams.PICKUP_TWO_TILES, 1.0); // 0-1
        upperrange.put(UtilityParams.PUTDOWN_ONE_TILE, 1.0); // 0-1
        upperrange.put(UtilityParams.PUTDOWN_TWO_TILES, 1.0); // 0-1
        upperrange.put(UtilityParams.PUTDOWN_THREE_TILES, 1.0); // 0-1
        upperrange.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X, 5.0); // 1 to 20 or env width (int)
        upperrange.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y, 5.0); // 1 to 20 or env height (int)
        //upperrange.put(UtilityParams.DECAY_MEMORY_AFTER, 500.0); // 500 for now, else 5000, depends on time steps
        upperrange.put(UtilityParams.THRESHOLD_EXPLORE, 50.0); 
        upperrange.put(UtilityParams.LENGTH_SNAPS, 100.0);
        upperrange.put(UtilityParams.UTILITY_STICKY, 20.0);
        
        //lowerrange.put(UtilityParams.BUFFER_RATIO, 0.0); // 0-1 (practically, should not be above 0.5)
        lowerrange.put(UtilityParams.DEVIATION_TILES, 0.0); // 0-1
        lowerrange.put(UtilityParams.DEVIATION_HOLES, 0.0); // 0-1
        lowerrange.put(UtilityParams.DEVIATION_MEM_DECAY, 0.0); // 1-50 or 100
        //lowerrange.put(UtilityParams.DEVIATION_NEIGHBOUR, 0.0); // 0-1
        lowerrange.put(UtilityParams.WEIGHT_COMBINATION, 0.0); // 0-1
        lowerrange.put(UtilityParams.PICKUP_ZERO_TILES, 0.0); // 0-1
        lowerrange.put(UtilityParams.PICKUP_ONE_TILES, 0.0); // 0-1
        lowerrange.put(UtilityParams.PICKUP_TWO_TILES, 0.0); // 0-1
        lowerrange.put(UtilityParams.PUTDOWN_ONE_TILE, 0.0); // 0-1
        lowerrange.put(UtilityParams.PUTDOWN_TWO_TILES, 0.0); // 0-1
        lowerrange.put(UtilityParams.PUTDOWN_THREE_TILES, 0.0); // 0-1
        lowerrange.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X, 1.0); // 1 to 20 or env width (int)
        lowerrange.put(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y, 1.0); // 1 to 20 or env height (int)
        //lowerrange.put(UtilityParams.DECAY_MEMORY_AFTER, 1.0); // 500 for now, else 5000, depends on time steps
        lowerrange.put(UtilityParams.THRESHOLD_EXPLORE, 0.0); 
        lowerrange.put(UtilityParams.LENGTH_SNAPS, 5.0); 
        lowerrange.put(UtilityParams.UTILITY_STICKY, 5.0); 
        
        numOfParams = upperrange.size();
    }
    
    @Override
    public List<Genome> apply(List<Genome> selectedCandidates, Random rng) {
    	System.out.println("Applying Mutation");
        for(int i=0; i<selectedCandidates.size(); i++){
            double chance = rng.nextDouble();
            if(chance<=probability){
                Genome mutated = mutate(selectedCandidates.get(i), rng);
                selectedCandidates.set(i, mutated);
            }
        }
        
        return selectedCandidates;
    }
    private Genome mutate(Genome g, Random rng){
        for(int c=0; c<mutationCount; c++){ // mutate 2 params (can mutate same twice too)
            int randI = rng.nextInt(numOfParams);
            String paramToMutate = Genome.getParamStr(randI);
            double currVal = g.getDoubleVal(paramToMutate);
            double upperVal = upperrange.get(paramToMutate);
            //System.out.println("Mutating cand-1 "+paramToMutate);
            double lowerVal = lowerrange.get(paramToMutate);
            //System.out.println("Mutating cand-2");
            double newVal = generateNewVal(currVal, upperVal, lowerVal, rng);
            g.setDoubleVal(paramToMutate, newVal);
        }
        return g;
    } 
    private double generateNewVal(double curr, double upper, double lower, Random rng){
    	System.out.println("Generating rand val for mutation");
        int addOrNot = rng.nextInt(2);
        boolean add=true;
        if(addOrNot==0){add=false;}
        double newVal;
        if(upper>1.0){
            // int increments
            if(add && curr<upper){ // add
                newVal = (int)curr + rng.nextInt((int)upper-(int)curr+1);
            }
            else{ // subtract
                newVal = (int)lower + rng.nextInt((int)curr-(int)lower+1);
            }
        }
        else{
            // double increments
            if(add && curr<upper){ //add
                newVal = (int)curr + ((int)upper-(int)curr) * rng.nextDouble();
            }
            else{ // subtract
                newVal = (int)lower + ((int)curr-(int)lower) * rng.nextDouble();
            }
        }
        return newVal;
    }
}