package erik;

import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.Config;
import utils.Coordinate;
import utils.RandGenerator;

public class Individual
{
	
	public static final double MAX_DWELL_TIME = 500;
    private double[] genes = new double[Config.numberOfSeeds];
    
    private double fitnessValue;

    public Individual() {
    	
    
    }

    
    public void randGenes() {
        Random rand = new Random();
        for(int i=0; i<Config.numberOfSeeds; ++i) {
           this.setGene(i, RandGenerator.randDouble(0, MAX_DWELL_TIME));
        }
    }

    public void mutate() {
        Random rand = new Random();
        int index = rand.nextInt(Config.numberOfSeeds);
        this.setGene(index, RandGenerator.randDouble(0,this.getGene(index)));    // flip
    }

    public double evaluate() {
    	
    	ExecutorService threadPool = Executors.newFixedThreadPool(4);
    	CompletionService<Double> pool = new ExecutorCompletionService<Double>(threadPool);
    	Coordinate[] start = new Coordinate[Config.numberOfTasks];
    	Coordinate[] end = new Coordinate[Config.numberOfTasks];
    	
    	//TODO bound calculation
    	
    	start[0] = new Coordinate(Config.ptvXLow-10, Config.ptvYLow-10, Config.ptvZLow-10);
    	end[0] = new Coordinate(Config.ptvXLow, Config.ptvYLow, Config.ptvZLow);
    	
    	start[1] = new Coordinate(50, 50, 50);
    	end[1] = new Coordinate(Config.ptvXHigh+10, Config.ptvYHigh+10, Config.ptvZHigh+10);
    	
    	
    	for(int i = 0; i < Config.numberOfTasks; i++){
    	   pool.submit(new DoseEvaluator(start[i],end[i],genes));
    	}
    	double[] partial_result = new double[Config.numberOfTasks];
    	for(int i = 0; i < Config.numberOfTasks; i++){
     	  try {
			partial_result[i] = pool.take().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	}
     	
    	
    	
    	
    	return Math.sqrt(partial_result[0]+partial_result[1]);
    	/*
        double fitness = 0;
        double temp=0;
        double intensity=0;
        for(int x=Config.ptvXLow-10; x < Config.ptvXHigh+10; x++) {
			
			for(int y=Config.ptvYLow-10; y < Config.ptvYHigh+10; y++) {
				
				for(int z=Config.ptvZLow-10; z < Config.ptvZHigh+10; z++) {
					
					Solver.body[x][y][z].setCurrentDosis(0);
					for(int i=0; i<Config.numberOfSeeds;++i) {
						
						
						intensity = Solver.body[x][y][z].radiationIntensity(Solver.seeds[i].getCoordinate(), genes[i]);
						Solver.body[x][y][z].addCurrentDosis(intensity);
						
					}	
					temp += Math.pow((Solver.body[x][y][z].getGoalDosis()-Solver.body[x][y][z].getCurrentDosis()),2);
					
					
				}	
			}
		}
        
        fitness = Math.sqrt(temp);
        this.setFitnessValue(fitness);
        
        return fitness;
        */
    }
    
    
    public double getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    public double getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, double gene) {
        this.genes[index] = gene;
    }
    
}