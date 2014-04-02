/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// 0.5 prob that genome is initialised based on the Genome() constructor

package ga;
import java.util.Random; // even the uncommons Math random number generators extend java.util.Random
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;


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
        g.setDoubleVal(Genome.RATIO_BUFFER, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.DEVIATION_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.DEVIATION_HOLES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.DEVIATION_MEM_DECAY, (double)(1+rng.nextInt(51-1))); //0+1 to 49+1 = 1 to 50 generated 
        g.setDoubleVal(Genome.DEVIATION_NEIGHBOUR, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.PICKUP_ZERO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.PICKUP_ONE_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.PICKUP_TWO_TILES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.PUTDOWN_ONE_HOLES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.PUTDOWN_TWO_HOLES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.PUTDOWN_THREE_HOLES, 0 + (1 - 0) * rng.nextDouble());
        g.setDoubleVal(Genome.NEIGHBOUR_SEARCH_LIMIT_X, (double)(1+rng.nextInt(21-1))); //0+1 to 19+1 = 1 to 20 generated
        g.setDoubleVal(Genome.NEIGHBOUR_SEARCH_LIMIT_Y, (double)(1+rng.nextInt(21-1))); //0+1 to 19+1 = 1 to 20 generated
        return g;
    }
}
