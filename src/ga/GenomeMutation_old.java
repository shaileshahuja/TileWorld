/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
/**
 *
 * @author Prerna
 */
public class GenomeMutation_old implements EvolutionaryOperator<Genome> {
    private HashMap<String, Double> upperrange = new HashMap<String, Double>();
    public HashMap<String, Double> lowerrange = new HashMap<String, Double>();
    private double probability=0.02;
    private double range=10;
    private boolean mutation;
    
    public GenomeMutation_old(){
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
    }
    
    @Override
    public List<Genome> apply(List<Genome> selectedCandidates, Random rng) {
        System.out.println("MUTATE START");
        String selectedProperty="";
        int mutationCount=0 ;
        int propertyIndex=0;
        int sizeCands=selectedCandidates.size();
        for(int i=0;i<sizeCands;i++) // loop through all candidates
        {
            Genome selectedCandidate=selectedCandidates.get(i);
            double random=rng.nextDouble();
            if(random<=probability){
                mutation=true; 
            }
            else{
                mutation=false;
            }
            
            if(mutation)  
            {
                mutationCount = 1 + rng.nextInt(4);  // values 0+1 to 3+1 are generated (i.e. 1 to 4 mutations)
                System.out.println("Starting to Mutate cand"+i+", "+mutationCount+" times.");
                while(mutationCount>0)
                {
                    propertyIndex = rng.nextInt(upperrange.size());
                    selectedProperty = Genome.getParamStr(propertyIndex);
                    
                    System.out.println("count left "+mutationCount);
                    
                    double mutationAmount = (rng.nextInt(10))/10;
                    double mutationAmountI = (double)1+rng.nextInt(4); // for int increments
                    boolean intInc = false;
                    
                    double finalmutation=0;
                    if(selectedProperty==Genome.DEVIATION_MEM_DECAY || selectedProperty==Genome.NEIGHBOUR_SEARCH_LIMIT_X || selectedProperty==Genome.NEIGHBOUR_SEARCH_LIMIT_Y){
                        intInc=true;
                    }
                    
                    int operation=rng.nextInt(2); // will generate either 0 or 1
                    if(operation==1) // try addition
                    {
                        if(intInc){
                            finalmutation=selectedCandidate.getDoubleVal(selectedProperty) + mutationAmountI;
                        }
                        else{
                            finalmutation=selectedCandidate.getDoubleVal(selectedProperty) + mutationAmount;
                        }
                        if(upperrange.get(selectedProperty)<=finalmutation && finalmutation>=lowerrange.get(selectedProperty)){
                            selectedCandidate.setDoubleVal(selectedProperty, finalmutation);
                            mutationCount--;
                        }
                    }
                    else // try subtraction
                    {
                        if(intInc){
                            finalmutation=selectedCandidate.getDoubleVal(selectedProperty) - mutationAmountI;
                        }
                        else {
                            finalmutation=selectedCandidate.getDoubleVal(selectedProperty) - mutationAmount;
                        }
                        if(upperrange.get(selectedProperty)<=finalmutation && finalmutation>=lowerrange.get(selectedProperty)){
                            selectedCandidate.setDoubleVal(selectedProperty, finalmutation);
                            mutationCount--;
                        }
                    }
                }
            }
            selectedCandidates.set(i, selectedCandidate); 
        }
        System.out.println("MUTATE END");
        return selectedCandidates;
    }
}
    
    
    
    
    
    
    
