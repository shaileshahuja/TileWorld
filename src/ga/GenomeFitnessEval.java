/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.maths.random.MersenneTwisterRNG;

import sim.engine.SimState;
import tileworld.Parameters;
import tileworld.environment.TWEnvironment;
import ga.Genome;

import java.util.*;
/**
 *
 * @author Prerna
 */
public class GenomeFitnessEval implements FitnessEvaluator <Genome> {
private HashMap<String, Double> weightPoints = new HashMap<String, Double>();

    public GenomeFitnessEval () //function needs to be called in the start, as the env is dynamic
    {
        Random rng = new MersenneTwisterRNG();
        weightPoints.put(Genome.RATIO_BUFFER, rng.nextDouble()); // 0-1 (practically, should not be above 0.5)
	weightPoints.put(Genome.DEVIATION_TILES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.DEVIATION_HOLES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.DEVIATION_MEM_DECAY, rng.nextDouble()); // 1-50 or 100
	weightPoints.put(Genome.DEVIATION_NEIGHBOUR, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.PICKUP_ZERO_TILES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.PICKUP_ONE_TILES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.PICKUP_TWO_TILES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.PUTDOWN_ONE_HOLES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.PUTDOWN_TWO_HOLES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.PUTDOWN_THREE_HOLES, rng.nextDouble()); // 0-1
	weightPoints.put(Genome.NEIGHBOUR_SEARCH_LIMIT_X, rng.nextDouble()); // 1 to 20 or env width (int)
	weightPoints.put(Genome.NEIGHBOUR_SEARCH_LIMIT_Y, rng.nextDouble()); // 1 to 20 or env height (int)
        printWeightsPoints();
    }
    private void printWeightsPoints(){
        for(int i=0;i<weightPoints.size();i++){
            System.out.println("Initial Wt for "+Genome.getParamStr(i)+" is "+ weightPoints.get(Genome.getParamStr(i)));
        }
    }

    @Override
    public double getFitness(Genome candidate, List<? extends Genome> population) 
    {
//        double fitness=0;
//        double total=0;
//        double den=0;
//        for(int i=0;i<weightPoints.size();i++)
//        {
//            total = total + (candidate.getDoubleVal(Genome.getParamStr(i))*weightPoints.get(Genome.getParamStr(i)));
//            den = den + weightPoints.get(Genome.getParamStr(i));
//        }
//        fitness=total/den;
//        //System.out.println("FITNESS : "+fitness);
//        return fitness;
    	ArrayList<HashMap<String, Double>> parameters = new ArrayList<HashMap<String,Double>>();
    	parameters.add(candidate.params);
    	parameters.add(candidate.params);
    	TWEnvironment tw = new TWEnvironment(parameters);
        tw.start();
      
        long steps = 0;	

        while (steps < Parameters.endTime) {
            if (!tw.schedule.step(tw)) {
                break;
            }
        }
        tw.finish();
        return tw.getScore();
    }

    @Override
    public boolean isNatural() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return true;
    }
    
}