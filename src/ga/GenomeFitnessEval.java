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
    }

    @Override
    public double getFitness(Genome candidate, List<? extends Genome> population) 
    {
    	ArrayList<HashMap<String, Double>> parameters = new ArrayList<HashMap<String,Double>>();
    	parameters.add(candidate.params);
    	parameters.add(candidate.params);
    	TWEnvironment tw = new TWEnvironment(parameters);
        tw.start();
      
        long steps = 0;	
        //System.out.println(Parameters.endTime);

        while (steps < Parameters.endTime) {
        	try{
        		if (!tw.schedule.step(tw)) {
                    break;
                }
        	}
        	catch(Exception e){
        		System.out.println("EXCEPTION!");
        		return 0;
        	}
            steps++;
        }
        tw.finish();
        System.out.println("RUN"+tw.getScore());
        //return (tw.getScore()*1.0) / tw.getTotalHolesCreated();
        return (tw.getScore()*1.0);
    }

    @Override
    public boolean isNatural() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return true;
    }
    
}