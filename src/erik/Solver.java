package erik;

import dawid.Looper;
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
	static public int[] xBoundsTumor;
	static public int[] yBoundsTumor;
	static public int[] zBoundsTumor;
	
	
	/**
	 * The Solver Class implements different algorithms to optimize the times a radiation seed stay in the body
	 * @param body
	 * The body of the patient
	 * @param seeds
	 * The seeds which should be optimized
	 */
	public Solver(Voxel [][][] entryBody, Seed[] seeds, int [] dimensions) {
		Solver.seeds = seeds;
		Solver.dimensions = dimensions;
		Solver.body = new Voxel[dimensions[0]][dimensions[1]][dimensions[2]];
		
		for (int x = 0; x < dimensions[0]; x++) {

			for (int y = 0; y < dimensions[1]; y++) {

				for (int z = 0; z < dimensions[2]; z++) {

					Solver.body[x][y][z] = new Voxel(x, y, z);

					switch (entryBody[x][y][z].getBodyType()) {
						case Config.normalType: {
							Solver.body[x][y][z].setGoalDosis(Config.normalGoalDose);
							Solver.body[x][y][z].setBodyType(Config.normalType);
							break;
						}
						case Config.bladderType: {
							Solver.body[x][y][z].setGoalDosis(Config.bladderGoalDose);
							Solver.body[x][y][z].setBodyType(Config.bladderType);
							break;
						}
						case Config.rectumType: {
							Solver.body[x][y][z].setGoalDosis(Config.rectumGoalDose);
							Solver.body[x][y][z].setBodyType(Config.rectumType);
							break;
						}
						case Config.urethraType: {
							Solver.body[x][y][z].setGoalDosis(Config.urethraGoalDose);
							Solver.body[x][y][z].setBodyType(Config.urethraType);
							break;
						}
						case Config.tumorType: {
							Solver.body[x][y][z].setGoalDosis(Config.tumorGoalDose);
							Solver.body[x][y][z].setBodyType(Config.tumorType);
							break;
						}
						default: {
							Solver.body[x][y][z].setGoalDosis(Config.normalGoalDose);
							Solver.body[x][y][z].setBodyType(Config.normalType);
							break;
						}
					
					}

				}
			}
		}
		
		BodyAnalyzer ba = new BodyAnalyzer(body, dimensions, Population.treatmentRange);
		Solver.xBoundsTumor = ba.getxBoundsTumor(2);
		Solver.yBoundsTumor = ba.getyBoundsTumor(2);
		Solver.zBoundsTumor = ba.getzBoundsTumor(2);
		LogTool.print("Solver initialized", "notification");
		
		
	}
	

	
	
	/**
	 * This method solves the optimization problem with a genetic algorithm
	 */
	public void solveGeneticAlg(int elitism, int pop_size, double mutation_rate, double crossover_rate, double[] weightingfactors, double treatmentRange) {
		
		LogTool.print("Created Population","notification");
		Individual test = Population.solve(elitism,pop_size, mutation_rate, crossover_rate, weightingfactors, treatmentRange);
		
		for(int i=0; i<Config.numberOfSeeds;i++) {
			Solver.seeds[i].setDurationMilliSec(test.getGene(i));
			System.out.println("Verweildauer: "+ Solver.seeds[i].getDurationMilliSec() );
		}
	}
        
        public void solveSA(double[] args) {
            Looper looper = new Looper(Solver.body,Solver.seeds,args);
            LogTool.print("Initialized Looper Object!","notification");
            LogTool.print("Beginning Annealing...","notification");
            looper.solveSA();
            LogTool.print("GLC: " + looper.getGlobal_lowest_cost()+ " CURC: " + looper.getCur_cost(),"notification");
            LogTool.print("SolveSA: Global Current Best Solution : " + looper.getGlobal_Lowest_state_string(),"notification");
        
            looper.setFinalSolution();
            Solver.body = looper.getBody();
            Solver.seeds = looper.getSeeds();
            for(int i=0; i<Config.SAnumberOfSeeds;i++) {
//                            Solver.seeds[i].setDurationMilliSec(looper.getGlobal_Lowest_state()[i]);
                            System.out.println("Verweildauer: "+ Solver.seeds[i].getDurationMilliSec() );
                    }
        }
	
	public void solveLP(double relax) throws IloException
	{
		int nonZeroCounter = 0;		
		
		new LPTreatment(body, seeds, xBoundsTumor, yBoundsTumor, zBoundsTumor, dimensions, relax);
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
		/*
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
		System.out.println("au�en oben mitte");
		System.out.println("tumorpoint 47-47-47 dose: " + LPTreatment.getBody()[47][47][47].getCurrentDosis());
		System.out.println("oben mitte zwischen rand und zentrum");
		System.out.println("tumorpoint 50-50-50 dose: " + LPTreatment.getBody()[50][50][50].getCurrentDosis());
		System.out.println("zentrum");
		*/
	}




	

}
