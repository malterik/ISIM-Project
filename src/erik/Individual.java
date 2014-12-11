package erik;

import java.util.Random;

import com.sun.prism.image.Coords;

import utils.Config;
import utils.Coordinate;
import utils.RandGenerator;

public class Individual
{
    public static final int SIZE = 40;
    private int[] genes = new int[SIZE];
    private Coordinate[] seedPostions= new Coordinate[SIZE];
    private double fitnessValue;

    public Individual() {
    	
    	for(int i=0; i<SIZE;i++){
    		seedPostions[i] = new Coordinate(RandGenerator.randInt(Config.ptvXLow, Config.ptvXHigh), RandGenerator.randInt(Config.ptvYLow, Config.ptvYHigh), RandGenerator.randInt(Config.ptvZLow, Config.ptvZHigh));
    	}
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int gene) {
        this.genes[index] = gene;
    }

    public void randGenes() {
        Random rand = new Random();
        for(int i=0; i<SIZE; ++i) {
           this.setGene(i, rand.nextInt(2));
        }
    }

    public void mutate() {
        Random rand = new Random();
        int index = rand.nextInt(SIZE);
        this.setGene(index, 1-this.getGene(index));    // flip
    }

    public double evaluate() {
        double fitness = 0;
        double temp=0;
        double intensity=0;
        for(int x=Config.ptvXLow-10; x < Config.ptvXHigh+10; x++) {
			
			for(int y=Config.ptvYLow-10; y < Config.ptvYHigh+10; y++) {
				
				for(int z=Config.ptvZLow-10; z < Config.ptvZHigh+10; z++) {
					
					Solver.body[x][y][z].setCurrentDosis(0);
					for(int i=0; i<SIZE;++i) {
						
						
						intensity = Solver.body[x][y][z].radiationIntensity(seedPostions[i], genes[i]);
						if(Double.isNaN(intensity)) {
							System.out.println("intensity NaN");
							intensity = Solver.body[x][y][z].radiationIntensity(seedPostions[i], genes[i]);
						}
						Solver.body[x][y][z].addCurrentDosis(intensity);
						
					}	
					temp += Math.pow((Solver.body[x][y][z].getGoalDosis()-Solver.body[x][y][z].getCurrentDosis()),2);
					//temp += Solver.body[x][y][z].getGoalDosis() - Solver.body[x][y][z].getCurrentDosis() ;
					
				}	
			}
		}
        
        fitness = Math.sqrt(temp);
        this.setFitnessValue(fitness);
        
        return fitness;
    }
}