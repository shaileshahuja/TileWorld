/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.util.*;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
/**
 *
 * @author Prerna
 */

public class GenomeMutation implements EvolutionaryOperator<Genome> {
    private HashMap<String, Double> upperrange = new HashMap<String, Double>();
    public HashMap<String, Double> lowerrange = new HashMap<String, Double>();
    private double probability=0.02;
    private boolean mutation=false;
    private int mutationCount = 2; // we mutate 2 params
    private int numOfParams;
    
    public GenomeMutation(){
        upperrange.put(Genome.RATIO_BUFFER, 1.0); // 0-1 (practically, should not be above 0.5)
	upperrange.put(Genome.DEVIATION_TILES, 1.0); // 0-1
	upperrange.put(Genome.DEVIATION_HOLES, 1.0); // 0-1
	upperrange.put(Genome.DEVIATION_MEM_DECAY, 50.0); // 1-50 or 100
	upperrange.put(Genome.DEVIATION_NEIGHBOUR, 1.0); // 0-1
	upperrange.put(Genome.PICKUP_ZERO_TILES, 1.0); // 0-1
	upperrange.put(Genome.PICKUP_ONE_TILES, 1.0); // 0-1
	upperrange.put(Genome.PICKUP_TWO_TILES, 1.0); // 0-1
	upperrange.put(Genome.PUTDOWN_ONE_HOLES, 1.0); // 0-1
	upperrange.put(Genome.PUTDOWN_TWO_HOLES, 1.0); // 0-1
	upperrange.put(Genome.PUTDOWN_THREE_HOLES, 1.0); // 0-1
	upperrange.put(Genome.NEIGHBOUR_SEARCH_LIMIT_X, 20.0); // 1 to 20 or env width (int)
	upperrange.put(Genome.NEIGHBOUR_SEARCH_LIMIT_Y, 20.0); // 1 to 20 or env height (int)
        
        lowerrange.put(Genome.RATIO_BUFFER, 0.0); // 0-1 (practically, should not be above 0.5)
	lowerrange.put(Genome.DEVIATION_TILES, 0.0); // 0-1
	lowerrange.put(Genome.DEVIATION_HOLES, 0.0); // 0-1
	lowerrange.put(Genome.DEVIATION_MEM_DECAY, 1.0); // 1-50 or 100
	lowerrange.put(Genome.DEVIATION_NEIGHBOUR, 0.0); // 0-1
	lowerrange.put(Genome.PICKUP_ZERO_TILES, 0.0); // 0-1
	lowerrange.put(Genome.PICKUP_ONE_TILES, 0.0); // 0-1
	lowerrange.put(Genome.PICKUP_TWO_TILES, 0.0); // 0-1
	lowerrange.put(Genome.PUTDOWN_ONE_HOLES, 0.0); // 0-1
	lowerrange.put(Genome.PUTDOWN_TWO_HOLES, 0.0); // 0-1
	lowerrange.put(Genome.PUTDOWN_THREE_HOLES, 0.0); // 0-1
	lowerrange.put(Genome.NEIGHBOUR_SEARCH_LIMIT_X, 1.0); // 1 to 20 or env width (int)
	lowerrange.put(Genome.NEIGHBOUR_SEARCH_LIMIT_Y, 1.0); // 1 to 20 or env height (int)
        
        numOfParams = upperrange.size();
    }
    
    @Override
    public List<Genome> apply(List<Genome> selectedCandidates, Random rng) {
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
            double lowerVal = lowerrange.get(paramToMutate);
            double newVal = generateNewVal(currVal, upperVal, lowerVal, rng);
            g.setDoubleVal(paramToMutate, newVal);
        }
        return g;
    } 
    private double generateNewVal(double curr, double upper, double lower, Random rng){
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
    
    
    
    
    
    
    
