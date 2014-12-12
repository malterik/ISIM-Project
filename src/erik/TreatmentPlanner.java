package erik;

import sebastian.SimpleDB;
import utils.Config;
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
		
		//System.exit (0);
		// End of db test
	
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
		
		/* Initialize the Seeds */
		 
		Seed[] seeds = new Seed[Config.numberOfSeeds];
		
		for(int i=0; i < Config.numberOfSeeds; i++)
		{				
			seeds[i] = new Seed(RandGenerator.randDouble(Config.ptvXLow,Config.ptvXHigh),RandGenerator.randDouble(Config.ptvYLow,Config.ptvYHigh),RandGenerator.randDouble(Config.ptvZLow,Config.ptvZHigh),0);
		}
		
		LogTool.print("Initialized Body Array!","notification");
		
		Solver solver = new Solver(body,seeds);			// The Solver implements the genetic Algorithm
		LogTool.print("Initialized Solver!","notification");
	
		solver.solveGeneticAlg();
	
		
		
		
		
		
	


	}
	
	
	

}
