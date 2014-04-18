/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ga;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import ga.Genome;
import practicalreasoning.UtilityParams;

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
    	int nG = data.getGenerationNumber();
    	int popSize = data.getPopulationSize();
        System.out.printf("\nGeneration %d, Pop Size %d\n", nG, popSize);
        Genome bestCand = data.getBestCandidate();
        bestCand.printParams();
        double fit = data.getBestCandidateFitness();
        System.out.printf("BEST FITNESS %f:\n", fit);
        double dev = data.getFitnessStandardDeviation();
        double mean = data.getMeanFitness();
        System.out.printf("Standard Deviation %f:\n", dev );
        System.out.printf("Mean FITNESS %f:\n", mean);
        long time = data.getElapsedTime();
        System.out.printf("Elapsed Time %d:\n", time);
        saveToFile(time, popSize, nG, bestCand, fit, dev, mean);
    }
    public void saveToFile(long time, int popSize, int nG, Genome best, double fitness, double dev, double mean){
    	try{
    		String out = "sensor3env1.csv";
    		//String out = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'.csv'").format(new Date());
    		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out, true)));
    		// generation number
    		writer.println(time+","+popSize+","+nG+","+fitness+","+dev+","+mean+","+best.getDoubleVal(UtilityParams.BUFFER_RATIO)+","+best.getDoubleVal(UtilityParams.DECAY_MEMORY_AFTER)+","+best.getDoubleVal(UtilityParams.DEVIATION_HOLES)+","+best.getDoubleVal(UtilityParams.DEVIATION_MEM_DECAY)+","+best.getDoubleVal(UtilityParams.DEVIATION_NEIGHBOUR)+","+best.getDoubleVal(UtilityParams.DEVIATION_TILES)+","+best.getDoubleVal(UtilityParams.LENGTH_SNAPS)+","+best.getDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_X)+","+best.getDoubleVal(UtilityParams.NEIGHBOUR_SEARCH_LIMIT_Y)+","+best.getDoubleVal(UtilityParams.PICKUP_ONE_TILES)+","+best.getDoubleVal(UtilityParams.PICKUP_TWO_TILES)+","+best.getDoubleVal(UtilityParams.PICKUP_ZERO_TILES)+","+best.getDoubleVal(UtilityParams.PUTDOWN_ONE_TILE)+","+best.getDoubleVal(UtilityParams.PUTDOWN_THREE_TILES)+","+best.getDoubleVal(UtilityParams.PUTDOWN_TWO_TILES)+","+best.getDoubleVal(UtilityParams.THRESHOLD_EXPLORE)+","+best.getDoubleVal(UtilityParams.UTILITY_STICKY)+","+best.getDoubleVal(UtilityParams.WEIGHT_COMBINATION));
        	writer.close();
    	}
    	catch(Exception e){
    		System.out.println("File write exception!");
    	}
    }
}
