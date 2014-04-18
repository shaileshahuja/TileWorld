/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// 0.5 prob that genome is initialised based on the Genome() constructor

package ga;
import java.util.Random; // even the uncommons Math random number generators extend java.util.Random
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import practicalreasoning.UtilityParams;
/**
 *
 * @author Prerna
 */
public class GenomeFactory extends AbstractCandidateFactory <Genome> {
	long numGen = 0;
    @Override
    public Genome generateRandomCandidate(Random rng) {
        //int randVal = rng.nextInt(2); // generates either 0 or 1 with prob of 0.5 each
        if(numGen%2==0){
        	numGen++;
        	return new Genome();
            //return randGenome(new Genome(), rng);
        }
        else{
        	numGen++;
        	System.out.println("Candidate Factory: GENERATING RANDOM CANDIDATE");
        	//return new Genome();
        	return randGenome(new Genome(), rng);
        }
    }    
    private Genome randGenome(Genome g, Random rng){
        //g.setDoubleVal(UtilityParams.BUFFER_RATIO, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.DEVIATION_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.DEVIATION_HOLES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.DEVIATION_MEM_DECAY, (double)(1+rng.nextInt(101-1))); //0+1 to 99+1 = 1 to 100 generated 
        //g.setDoubleVal(UtilityParams.DEVIATION_NEIGHBOUR, 0 + (1 - 0) * rng.nextDouble());
        //g.setDoubleVal(UtilityParams.WEIGHT_COMBINATION, (double)(1+rng.nextInt(21-1)));
        g.setDoubleVal(UtilityParams.PICKUP_ZERO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PICKUP_ONE_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PICKUP_TWO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PUTDOWN_ONE_TILE, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PUTDOWN_TWO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PUTDOWN_THREE_TILES, 0 + (1 - 0) * rng.nextDouble());
        //g.setDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X, (double)(1+rng.nextInt(21-1))); //0+1 to 19+1 = 1 to 20 generated
        //g.setDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y, (double)(1+rng.nextInt(21-1))); //0+1 to 19+1 = 1 to 20 generated
        //g.setDoubleVal(UtilityParams.DECAY_MEMORY_AFTER, (double)(1+rng.nextInt(501-1)));
        g.setDoubleVal(UtilityParams.THRESHOLD_EXPLORE, (double)(0+rng.nextInt(51)));  // 0 to 100
        g.setDoubleVal(UtilityParams.LENGTH_SNAPS, (double)(5+rng.nextInt(96))); // 5 to 100
        g.setDoubleVal(UtilityParams.UTILITY_STICKY, (double)(5+rng.nextInt(16))); // 5 to 20
        return g;
    }
}
