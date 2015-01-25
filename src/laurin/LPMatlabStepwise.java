package laurin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import sebastian.BodyEntry;
import sebastian.SimpleDB;
import sebastian.TreatmentEntry;
import utils.Config;
import utils.Coordinate;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;
import erik.BodyAnalyzer;

public class LPMatlabStepwise {
	
	public static Voxel[][][] getVoxelArray(BodyEntry entry) 
	{
		Voxel[][][] body = new Voxel[entry.getDimensions()[0]][entry.getDimensions()[1]][entry.getDimensions()[2]];
		
		for (int x = 0; x < entry.getDimensions()[0]; x++) 
		{
			for (int y = 0; y < entry.getDimensions()[1]; y++)
			{
				for (int z = 0; z < entry.getDimensions()[2]; z++)
				{
					body[x][y][z] = new Voxel(x, y, z);
					
					switch (entry.getBodyArray()[x][y][z].getBodyType()) {
						case Config.normalType: {
							body[x][y][z].setGoalDosis(Config.normalGoalDose);
							body[x][y][z].setBodyType(Config.normalType);
							break;
						}
						case Config.spineType: {
							body[x][y][z].setGoalDosis(Config.spineGoalDose);
							body[x][y][z].setBodyType(Config.spineType);
							break;
						}
						case Config.liverType: {
							body[x][y][z].setGoalDosis(Config.liverGoalDose);
							body[x][y][z].setBodyType(Config.liverType);
							break;
						}
						case Config.pancreasType: {
							body[x][y][z].setGoalDosis(Config.pancreasGoalDose);
							body[x][y][z].setBodyType(Config.pancreasType);
							break;
						}
						case Config.tumorType: {
							body[x][y][z].setGoalDosis(Config.tumorGoalDose);
							body[x][y][z].setBodyType(Config.tumorType);
							break;
						}
						default: {
							body[x][y][z].setGoalDosis(Config.normalGoalDose);
							body[x][y][z].setBodyType(Config.normalType);
							break;
						}
					}	
				}
			}
		}
		return body;
	}
	
    public static Voxel[][][] sampleDown(Voxel[][][] body, int factor, int[] xBounds, int yBounds[], int zBounds[])
    {
    	int xLength = xBounds[1]-xBounds[0];
    	int yLength = yBounds[1]-yBounds[0];
    	int zLength = zBounds[1]-zBounds[0];
	
    	int xSize = (int) Math.ceil(xLength/(double)factor);
    	int ySize = (int) Math.ceil(yLength/(double)factor);
    	int zSize = (int) Math.ceil(zLength/(double)factor);
    	
    	Voxel[][][] downSampledBody = new Voxel[xSize][ySize][zSize];
    	
    	for (int x = xBounds[0]; x < xBounds[1]; x += factor)
    	{
        	for (int y = yBounds[0]; y < yBounds[1]; y += factor)
        	{
            	for (int z = zBounds[0]; z < zBounds[1]; z += factor)
            	{
            		downSampledBody[(x-xBounds[0])/factor][(y-yBounds[0])/factor][(z-zBounds[0])/factor] = body[x][y][z];
            	}
        	}
    	}
    	return downSampledBody;
    }
    
	public static Seed[] createSeeds(Voxel[][][] body, BodyAnalyzer ba)
	{
		Seed[] seeds = new Seed[Config.numberOfSeeds];
		int i =0 ;
		while (i < Config.numberOfSeeds ) {
			Coordinate co = new Coordinate(
					RandGenerator.randDouble(ba.getxBoundsTumor(1)[0], ba.getxBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getyBoundsTumor(1)[0], ba.getyBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getzBoundsTumor(1)[0], ba.getzBoundsTumor(1)[1]));
			
			if(body[(int)co.getX()][(int)co.getY()][(int)co.getZ()].getBodyType() == Config.tumorType) {
				seeds[i] = new Seed(co.getX(), co.getY(), co.getZ(), 0);
				i++;
			}
		}
		return seeds;
	}
	
	/**
	 * @param args
	 * @throws IloException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IloException, IOException {
		// TODO Auto-generated method stub
		SimpleDB db = new SimpleDB ();
    	db.loadBody("data2593.4844");
		BodyEntry entry = db.getBodyByName("data2593.4844");
		Voxel[][][] body = getVoxelArray(entry);
		
		int[] dimensions = new int[]{body.length, body[0].length, body[0][0].length};
		BodyAnalyzer ba = new BodyAnalyzer(body, dimensions);
		Seed[] seeds = createSeeds(body, ba);

		
		Voxel[][][] downSampledBody = sampleDown(body, 4, ba.getxBoundsTumor(2), ba.getyBoundsTumor(2), ba.getzBoundsTumor(2));
		CplexSolver cplexSolver = new CplexSolver(downSampledBody, seeds);
		System.out.println("Solving IS-Step");
		cplexSolver.initialSolution();
		Seed[] isSeeds = cplexSolver.getCurrentSeeds();
		System.out.println("Solving HI-Step");
		cplexSolver.optimizePTVHomogeinity();
		Seed[] hiSeeds = cplexSolver.getCurrentSeeds();
		System.out.println("Solving CO-Step");
		cplexSolver.optimizeCoverage();
		Seed[] coSeeds = cplexSolver.getCurrentSeeds();
		
		System.out.println("Analyzing IS-Step");
		TreatmentAnalyzer taIS = new TreatmentAnalyzer(body, dimensions, isSeeds, "IS-step");
		taIS.printResults();
		System.out.println("Analyzing HI-Step");
		TreatmentAnalyzer taHI = new TreatmentAnalyzer(body, dimensions, hiSeeds, "HI-step");
		taHI.printResults();
		System.out.println("Analyzing CO-Step");
		TreatmentAnalyzer taCO = new TreatmentAnalyzer(body, dimensions, coSeeds, "CO-step");
		taCO.printResults();
		
		ArrayList<TreatmentAnalyzer> treatmentAnalyzers = new ArrayList<TreatmentAnalyzer>();
		treatmentAnalyzers.add(taIS);
		treatmentAnalyzers.add(taHI);
		treatmentAnalyzers.add(taCO);
		TreatmentAnalyzer.printTreatmentComparison(treatmentAnalyzers);

		db.close();
	}

}
