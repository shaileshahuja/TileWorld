/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.util.*;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.termination.ElapsedTime;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.TerminationCondition;
import ga.EvolutionLogger;
/**
 *
 * @author Prerna
 */
public class GA {

    /**
     * @param args the command line arguments
     */
    private static int populationSize = 10;
    private static int eliteCount = 4; // fittest candidates copied to the next gen as is.
    public static void main(String[] args) {
        Random rng = new MersenneTwisterRNG();
        List<EvolutionaryOperator<Genome>> operators = new ArrayList<EvolutionaryOperator<Genome>>(2);
        operators.add(new GenomeCrossover(6));
        operators.add(new GenomeMutation()); // @CHETNA - TO EDIT
        EvolutionaryOperator<Genome> pipeline = new EvolutionPipeline<Genome>(operators);
        SelectionStrategy <Object> selectionStrategy = new SigmaScaling(new RouletteWheelSelection());
        EvolutionEngine<Genome> engine = new GenerationalEvolutionEngine<Genome>(new GenomeFactory(), pipeline, new GenomeFitnessEval(), selectionStrategy, rng);
        engine.addEvolutionObserver(new EvolutionLogger());
        Genome winner = engine.evolve(populationSize, eliteCount, new ElapsedTime(120000), new GenerationCount(20000), new Stagnation(3000, true));
        System.out.println("\n\nWINNER");
        winner.printParams();
        List <TerminationCondition> termConds = engine.getSatisfiedTerminationConditions();
        printTermConds(termConds);
    }
    private static void printTermConds(List <TerminationCondition> termConds){
        for(int i=0; i<termConds.size(); i++){
            String className = termConds.get(i).getClass().getSimpleName();
            System.out.println("Termination Conditions:");
            System.out.println(className);
        }
    }
}
