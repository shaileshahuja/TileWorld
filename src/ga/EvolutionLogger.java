/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import ga.Genome;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
/**
 *
 * @author Prerna
 */
public class EvolutionLogger implements EvolutionObserver<Genome>
{
    public void populationUpdate(PopulationData<? extends Genome> data)
    {
        System.out.printf("\nGeneration %d, Pop Size %d\n", data.getGenerationNumber(), data.getPopulationSize());
        data.getBestCandidate().printParams();
        System.out.printf("BEST FITNESS %f:\n", data.getBestCandidateFitness());
        System.out.printf("Elapsed Time %d:\n", data.getElapsedTime());
    }
}
