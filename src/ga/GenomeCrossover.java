/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.util.*;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;
import org.uncommons.maths.number.NumberGenerator;
import ga.Genome;

/**
 *
 * @author Prerna
 */
public class GenomeCrossover extends AbstractCrossover<Genome>{
    
    public GenomeCrossover(){
        this(6); // default 6 crossover points
    }
    
    public GenomeCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }
    
    public GenomeCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }
    
    
    @Override
    protected List<Genome> mate(Genome parent1, Genome parent2, int numberOfCrossoverPoints, Random rng)
    {
        //System.out.println("//CROSSOVER BEGIN");
        if (parent1.getNumParams() != parent2.getNumParams())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        Genome offspring1 = new Genome();
        offspring1.clone(parent1);
        Genome offspring2 = new Genome();
        offspring2.clone(parent2);
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int crossoverIndex = rng.nextInt(parent1.getNumParams()); // b/w 0 and length-1
            String crossoverParam = Genome.getParamStr(crossoverIndex);
            double temp = offspring1.getDoubleVal(crossoverParam);
            offspring1.setDoubleVal(crossoverParam, offspring2.getDoubleVal(crossoverParam));
            offspring2.setDoubleVal(crossoverParam, temp);
        }
        List<Genome> result = new ArrayList<Genome>(2);
        result.add(offspring1);
        result.add(offspring2);
        //System.out.println("//CROSSOVER END");
        return result;
    }
}
