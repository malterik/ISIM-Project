package erik;

import java.util.Random;

import utils.Config;
import utils.RandGenerator;
import utils.Seed;

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