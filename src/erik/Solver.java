package erik;

import utils.Config;
import utils.LogTool;
import utils.Seed;
import utils.Voxel;

public class Solver {
	
	public static Voxel [][][] body;
	public static Seed[] seeds = new Seed[Config.numberOfSeeds];
	
	
	/**
	 * The Solver Class implements different algorithms to optimize the times a radiation seed stay in the body
	 * @param body
	 * The body of the patient
	 * @param seeds
	 * The seeds which should be optimized
	 */
	public Solver(Voxel [][][] body, Seed[] seeds) {
		this.body = body;
		this.seeds = seeds;
	}
	

	
	
	/**
	 * This method solves the optimization problem with a genetic algorithm
	 */
	public void solveGeneticAlg() {
		
		LogTool.print("Created Population","notification");
		Individual test = Population.solve();
		
		for(int i=0; i<Config.numberOfSeeds;i++) {
			System.out.println("Verweildauer: "+test.getGene(i));
		}
	}




	

}
