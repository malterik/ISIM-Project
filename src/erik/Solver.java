package erik;

import ilog.concert.IloException;
import thobi.LPTreatment;
import utils.Config;
import utils.LogTool;
import utils.Seed;
import utils.Voxel;

public class Solver {
	
	public static Voxel [][][] body;
	public static Seed[] seeds = new Seed[Config.numberOfSeeds];
	public static int[] dimensions;
	
	
	/**
	 * The Solver Class implements different algorithms to optimize the times a radiation seed stay in the body
	 * @param body
	 * The body of the patient
	 * @param seeds
	 * The seeds which should be optimized
	 */
	public Solver(Voxel [][][] body, Seed[] seeds, int [] dimensions) {
		Solver.body = body;
		Solver.seeds = seeds;
		Solver.dimensions = dimensions;
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
	
	public void solveLP() throws IloException
	{
		int nonZeroCounter = 0;
		new LPTreatment(body, seeds);
		//LPTreatment.solveLP();
		//LPTreatment.solveLPMin();
		LPTreatment.solveLPIT();
		
		
		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			if(LPTreatment.getSeed()[i].getDurationMilliSec() != 0)
			{
				System.out.println("Seed");
				System.out.println("x" + i + " : " + LPTreatment.getSeed()[i].getX());
				System.out.println("y" + i + " : " + LPTreatment.getSeed()[i].getY());
				System.out.println("z" + i + " : " + LPTreatment.getSeed()[i].getZ());
				System.out.println("time" + i + " : " + LPTreatment.getSeed()[i].getDurationMilliSec());
				nonZeroCounter++;
			}
		}
		System.out.println("Non zero elements: " + nonZeroCounter);
		System.out.println("tumorpoint 45-45-45 dose: " + LPTreatment.getBody()[45][45][45].getCurrentDosis());
		System.out.println("Ecke");
		System.out.println("tumorpoint 45-50-50 dose: " + LPTreatment.getBody()[45][50][50].getCurrentDosis());
		System.out.println("rand oben mitte");
		System.out.println("tumorpoint 44-50-50 dose: " + LPTreatment.getBody()[44][50][50].getCurrentDosis());
		System.out.println("tumorpoint 43-50-50 dose: " + LPTreatment.getBody()[43][50][50].getCurrentDosis());
		System.out.println("tumorpoint 42-50-50 dose: " + LPTreatment.getBody()[42][50][50].getCurrentDosis());
		System.out.println("tumorpoint 41-50-50 dose: " + LPTreatment.getBody()[41][50][50].getCurrentDosis());
		System.out.println("tumorpoint 40-50-50 dose: " + LPTreatment.getBody()[40][50][50].getCurrentDosis());
		System.out.println("außen oben mitte");
		System.out.println("tumorpoint 47-47-47 dose: " + LPTreatment.getBody()[47][47][47].getCurrentDosis());
		System.out.println("oben mitte zwischen rand und zentrum");
		System.out.println("tumorpoint 50-50-50 dose: " + LPTreatment.getBody()[50][50][50].getCurrentDosis());
		System.out.println("zentrum");
	}




	

}
