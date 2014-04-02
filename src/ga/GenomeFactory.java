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
    @Override
    public Genome generateRandomCandidate(Random rng) {
        int randVal = rng.nextInt(2); // generates either 0 or 1 with prob of 0.5 each
        System.out.println("Candidate Factory: GENERATING RANDOM CANDIDATE");
        if(randVal==0){
            return new Genome();
        }
        else{
            return randGenome(new Genome(), rng);
        }
    }    
    private Genome randGenome(Genome g, Random rng){
        g.setDoubleVal(UtilityParams.BUFFER_RATIO, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.DEVIATION_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.DEVIATION_HOLES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.DEVIATION_MEM_DECAY, (double)(1+rng.nextInt(101-1))); //0+1 to 99+1 = 1 to 100 generated 
        g.setDoubleVal(UtilityParams.DEVIATION_NEIGHBOUR, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.WEIGHT_COMBINATION, (double)(1+rng.nextInt(21-1)));
        g.setDoubleVal(UtilityParams.PICKUP_ZERO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PICKUP_ONE_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PICKUP_TWO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PUTDOWN_ONE_TILE, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PUTDOWN_TWO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.PUTDOWN_THREE_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X, (double)(1+rng.nextInt(21-1))); //0+1 to 19+1 = 1 to 20 generated
        g.setDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y, (double)(1+rng.nextInt(21-1))); //0+1 to 19+1 = 1 to 20 generated
        g.setDoubleVal(UtilityParams.DECAY_MEMORY_AFTER, (double)(1+rng.nextInt(501-1)));
        g.setDoubleVal(UtilityParams.THRESHOLD_EXPLORE, (double)(1+rng.nextInt(101-1)));
        return g;
    }
}
