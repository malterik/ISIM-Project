package erik;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.POP;

import utils.Config;
import utils.LogTool;

public class Population
{
    /*
    final static int ELITISM_K =2;
    final static int POP_SIZE = 8 + ELITISM_K;  // population size
    final static int MAX_ITER = 800;             // max number of iterations
    final static double MUTATION_RATE = 0.55;     // probability of mutation
    final static double CROSSOVER_RATE = 0.8;     // probability of crossover */
    
	private static int ELITISM_K;
	private static int POP_SIZE;  // population size             // max number of iterations
    private static double MUTATION_RATE;     // probability of mutation
    private static double CROSSOVER_RATE;     // probability of crossover
    public static double[] weighting_factors;
    public static double treatmentRange;
    
    private static Random m_rand = new Random();  // random-number generator
    private Individual[] m_population;
    private double totalFitness;
   
    
    public Population(int elitism, int pop_size, double mutation_rate, double crossover_rate, double[] weightingfactors, double treatmentRange) {

    	ELITISM_K = elitism;
    	POP_SIZE = pop_size;
    	MUTATION_RATE = mutation_rate;
    	CROSSOVER_RATE = crossover_rate;
    	weighting_factors = weightingfactors;
    	
    	
        m_population = new Individual[POP_SIZE];

        // init population
        for (int i = 0; i < POP_SIZE; i++) {
            m_population[i] = new Individual();
            m_population[i].randGenes();
        }

        // evaluate current population
        this.evaluate();
    }

    public void setPopulation(Individual[] newPop) {
        // this.m_population = newPop;
        System.arraycopy(newPop, 0, this.m_population, 0, POP_SIZE);
    }

    public Individual[] getPopulation() {
        return this.m_population;
    }

    public double evaluate() {
        this.totalFitness = 0.0;
        for (int i = 0; i < POP_SIZE; i++) {
            this.totalFitness += m_population[i].evaluate();
        }
        return this.totalFitness;
    }

    public Individual rouletteWheelSelection() {
        double randNum = m_rand.nextDouble() * this.totalFitness;
        int idx;
        for (idx=0; idx<POP_SIZE && randNum>0; ++idx) {
            randNum -= m_population[idx].getFitnessValue();
            
        }
        
        return m_population[idx-1];
    }

    public Individual findBestIndividual() {
        int idxMax = 0, idxMin = 0;
        double currentMax = 0.0;
        double currentMin = 1.0;
        double currentVal;

        for (int idx=0; idx<POP_SIZE; ++idx) {
            currentVal = m_population[idx].getFitnessValue();
            if (currentMax < currentMin) {
                currentMax = currentMin = currentVal;
                idxMax = idxMin = idx;
            }
            if (currentVal > currentMax) {
                currentMax = currentVal;
                idxMax = idx;
            }
            if (currentVal < currentMin) {
                currentMin = currentVal;
                idxMin = idx;
            }
        }

        return m_population[idxMin];      // minimization
        //return m_population[idxMax];        // maximization
    }

    public static Individual[] crossover(Individual indiv1,Individual indiv2) {
        Individual[] newIndiv = new Individual[2];
        newIndiv[0] = new Individual();
        newIndiv[1] = new Individual();

        int randPoint = m_rand.nextInt(Config.numberOfSeeds);
        int i;
        for (i=0; i<randPoint; ++i) {
            newIndiv[0].setGene(i, indiv1.getGene(i));
            newIndiv[1].setGene(i, indiv2.getGene(i));
        }
        for (; i<Config.numberOfSeeds; ++i) {
            newIndiv[0].setGene(i, indiv2.getGene(i));
            newIndiv[1].setGene(i, indiv1.getGene(i));
        }

        return newIndiv;
    }
    
    public static Individual solve(int elitism, int pop_size, double mutation_rate, double crossover_rate, double[] weightingfactors, double treatmentRange) {
        Population pop = new Population(elitism,pop_size, mutation_rate, crossover_rate, weightingfactors, treatmentRange);
        Individual[] newPop = new Individual[POP_SIZE];
        Individual[] indiv = new Individual[2];
        double bestResult = Double.MAX_VALUE;
        double bestResultOld = 0.0;
        long counter = 0;
        boolean done = true;
        int iter = 0;

        // current population
        System.out.print("Total Fitness = " + pop.totalFitness);
        System.out.println(" ; Best Fitness = " + 
            pop.findBestIndividual().getFitnessValue());

        // main loop
        int count;
        while (done) {
        	long start = System.currentTimeMillis();
            count = 0;
            iter++;

            // Elitism
            for (int i=0; i<ELITISM_K; ++i) {
                newPop[count] = pop.findBestIndividual();
                count++;
            }

            // build new Population
            while (count < POP_SIZE) {
                // Selection
                indiv[0] = pop.rouletteWheelSelection();
                indiv[1] = pop.rouletteWheelSelection();

                // Crossover
                if ( m_rand.nextDouble() < CROSSOVER_RATE ) {
                    indiv = crossover(indiv[0], indiv[1]);
                }

                // Mutation
                if ( m_rand.nextDouble() < MUTATION_RATE ) {
                    indiv[0].mutate();
                }
                if ( m_rand.nextDouble() < MUTATION_RATE ) {
                    indiv[1].mutate();
                }

                // add to new population
                newPop[count] = indiv[0];
                newPop[count+1] = indiv[1];
                count += 2;
            }
            pop.setPopulation(newPop);
            
            // reevaluate current population
            pop.evaluate();
            System.out.print("Iteration : "+iter+" "+"Total Fitness = " + pop.totalFitness);
            System.out.println(" ; Best Fitness = " + pop.findBestIndividual().getFitnessValue()); 
           
            //LogTool.print("Iteration : "+iter, "notification");
            LogTool.print("Improvement: "+Math.abs(bestResultOld-bestResult)+" Cancel counter= " + counter, "debug");
            
            if(pop.findBestIndividual().getFitnessValue() <= bestResult) {
            	bestResultOld = bestResult;
            	bestResult =  pop.findBestIndividual().getFitnessValue();
            }
            
            if(Math.abs(bestResultOld-bestResult) <= Config.cancelValue) {
            	counter++;	
            } else {
            	counter = 0;
            }
            
            if(counter > 5) {
            	done = false;
            }
            long end = System.currentTimeMillis();
            Date date = new Date(end - start);
    		DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
    		String dateFormatted = formatter.format(date);
    		System.out.println("Iteration Runtime: " + dateFormatted);
            
        }
       
        // best indiv
        Individual bestIndiv = pop.findBestIndividual();
        
        	
        return bestIndiv;
    }

}