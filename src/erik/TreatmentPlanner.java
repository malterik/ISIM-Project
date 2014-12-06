package erik;

import java.util.ArrayList;
import java.util.List;

import sebastian.SimpleDB;
import sebastian.TreatmentEntry;
import utils.Config;
import utils.Coordinate;
import utils.GeneticAlg;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;



public class TreatmentPlanner {

	public static void main(String[] args) {
		
		/*
	    // Start visualization
	    Voxel[][][] testData = new Voxel[1][1][5];
	    Voxel data;
	    for (int i = 0; i < 5; i++) {
	    	data = new Voxel (0, 0, i);
	    	data.setCurrentDosis (0.5 * i + 0.5);
	    	data.setGoalDosis(1.5);
	    	testData [0][0][i] = data;
	    }	    
	    ScatterDisplay display = new ScatterDisplay(ChartType.MaxDosis);
	    //display.fillTestData ();
	    display.fill (testData, 1, 1, 5);
	    display.display();
	    // End of visualization
	    */
		
		// Database test
		SimpleDB db = new SimpleDB ();
		/*Voxel[][][] testData = new Voxel[10][10][10];
		ArrayList<Coordinate> seedPositions = new ArrayList<Coordinate> (5);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				for (int k = 0; k < 10; k++) {
					testData[i][j][k] = new Voxel(i, j, k);
					testData[i][j][k].setCurrentDosis(Math.random () * 10 + 3);
					testData[i][j][k].setGoalDosis(Math.random () * 10);
					testData[i][j][k].setMinDosis(Math.random () * 5);
					testData[i][j][k].setMaxDosis(Math.random () * 3 + 8);
				}	
			}	
		}
		for (int i = 0; i < 5; i++) {
			seedPositions.add(new Coordinate ((int) Math.random () * 10, (int) Math.random () * 10, (int) Math.random () * 10));
		}
		db.addEntry(new TreatmentEntry (testData, new int[] {10, 10, 10}, seedPositions));
		db.addEntry(new TreatmentEntry (testData, new int[] {10, 10, 10}, seedPositions));
		db.deleteEntry(db.getSize() - 2);*/
		db.close ();
		
		// Test if it was correctly written
		/*db = new SimpleDB ();
		db.print ();*/
		
		System.exit (0);
		// End of db test
		
		List<Seed> gene1 = new ArrayList<Seed>();
		List<Seed> gene2 = new ArrayList<Seed>();
		List<Seed> gene3 = new ArrayList<Seed>();
		
		List<List<Seed>> geneList = new ArrayList<List<Seed>>();
		int ptvSize = 0;
		int oarSize = 0;
		
		Voxel [][][] body = new Voxel[Config.xDIM][Config.yDIM][Config.zDIM];		// This is the body of the "patient"
		LogTool.print("Created Body Array!","notification");
		
		/* Initialize the body Array */

		for(int x=0;x<Config.xDIM;x++) {
			
			for(int y=0;y<Config.yDIM;y++) {
				
				for(int z=0;z<Config.zDIM;z++) {
					
					
					body[x][y][z] = new Voxel(x, y, z);
					if( (x >= Config.ptvXLow && x <= Config.ptvXHigh) && (y >= Config.ptvYLow && y <= Config.ptvYHigh) && (z >= Config.ptvZLow && z <= Config.ptvZHigh)  ) {		//consider whether the current voxel belongs to the tumor or not
						body[x][y][z].setGoalDosis(Config.ptvGoalDose);
						ptvSize++;
					} else {
						body[x][y][z].setGoalDosis(Config.oarGoalDose);
						oarSize++;
					}
						
					
				}
			}	
		}
		LogTool.print("PTV Size: "+ptvSize+" OAR Size: "+oarSize,"debug");
		LogTool.print("Initialized Body Array!","notification");
		
		Solver solver = new Solver(body);			// The Solver implements the genetic Algorithm
		LogTool.print("Initialized Solver!","notification");
		
		// Create the seeds & genes
		for(int i=0; i< Config.numberOfGenes; i++)  {
			List<Seed> gene = new ArrayList<Seed>();
			
			for(int j=0; j < Config.numberOfSeeds; j++) {
				Seed seed = new Seed(RandGenerator.randInt(Config.ptvXLow, Config.ptvXHigh),RandGenerator.randInt(Config.ptvYLow, Config.ptvYHigh),RandGenerator.randInt(Config.ptvZLow, Config.ptvZHigh),RandGenerator.randInt(0,(int) Config.ptvMaxDose));
				LogTool.print(seed.getX()+" "+seed.getY()+" "+seed.getY()+" "+seed.getDurationMilliSec(),"debug"); 
				gene.add(seed);
			}
			System.out.println("  ");
			geneList.add(gene);
			
		}
		
		
		
		List<GeneticAlg<Seed>.EvaluatedGene> templist;
		
		// optimize
		for(int i=0; i < Config.numberOfIterations; i++) {
			LogTool.print("Selection", "debug");
			templist = solver.selection(geneList);
			LogTool.printGene(templist);
			
			
			LogTool.print("Cross-Over", "debug");
			templist = solver.crossOver(templist);
			LogTool.printGene(templist);
			
			LogTool.print("Mutation", "debug");
			templist = solver.mutation(templist);
			LogTool.printGene(templist);
			
			LogTool.print("Fitness Value : " + solver.fitnessFunction(geneList.get(0)) , "notification");
			//LogTool.print("Fitness Value gene1: " + solver.fitnessFunction(gene1) , "debug");
			
		}
		
		LogTool.print("Radiation at Seed Position: " + body[2][2][2].getCurrentDosis() , "debug");
		
	


	}
	
	
	

}
