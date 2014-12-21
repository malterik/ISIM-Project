package erik;

import ilog.concert.IloException;
import sebastian.SimpleDB;
import sebastian.TreatmentEntry;
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
		TreatmentEntry entry = db.getEntryByName("data2593.4844");
		if (entry != null) {
			//ScatterDisplay display = new ScatterDisplay(ChartType.BodyType);
			//display.fill(entry.getBodyArray(), entry.getDimensions()[0], entry.getDimensions()[1], entry.getDimensions()[2]);
			//display.display ();			
			
			
			
			Voxel [][][] body = new Voxel[entry.getDimensions()[0]][entry.getDimensions()[1]][entry.getDimensions()[2]];		// This is the body of the "patient"
			LogTool.print("Created Body Array!","notification");
			
			for(int x = 0; x < entry.getDimensions()[0]; x++) {
				
				for(int y = 0; y < entry.getDimensions()[1]; y++) {
					
					for(int z = 0; z < entry.getDimensions()[2]; z++) {
						
						body[x][y][z] = new Voxel(x, y, z);
						
						switch(entry.getBodyArray()[x][y][z].getBodyType())
						{
							case Config.normalType:
							{
								body[x][y][z].setGoalDosis(Config.normalGoalDose);
								body[x][y][z].setBodyType(Config.normalType);
								break;
							}
							case Config.spineType:
							{
								body[x][y][z].setGoalDosis(Config.spineGoalDose);
								body[x][y][z].setBodyType(Config.spineType);
								break;
							}
							case Config.liverType:
							{
								body[x][y][z].setGoalDosis(Config.liverGoalDose);
								body[x][y][z].setBodyType(Config.liverType);
								break;
							}
							case Config.pancreasType:
							{
								body[x][y][z].setGoalDosis(Config.pancreasGoalDose);
								body[x][y][z].setBodyType(Config.pancreasType);
								break;
							}
							case Config.tumorType:
							{
								body[x][y][z].setGoalDosis(Config.tumorGoalDose);
								body[x][y][z].setBodyType(Config.tumorType);
								break;
							}
							default:
							{
								body[x][y][z].setGoalDosis(Config.normalGoalDose);
								body[x][y][z].setBodyType(Config.normalType);
								break;
							}	
						}
						
						
						
						
					}
				}	
			}
			/*
			Seed[] seeds = new Seed[Config.numberOfSeeds];
			
			for(int i=0; i < Config.numberOfSeeds; i++)
			{				
				seeds[i] = new Seed(RandGenerator.randDouble(Config.ptvXLow,Config.ptvXHigh),RandGenerator.randDouble(Config.ptvYLow,Config.ptvYHigh),RandGenerator.randDouble(Config.ptvZLow,Config.ptvZHigh),0);
			}
			
			LogTool.print("Initialized Body Array!","notification");
			
			Solver solver = new Solver(body,seeds);			// The Solver implements the genetic Algorithm
			LogTool.print("Initialized Solver!","notification");
			
			try {
				solver.solveLP();
			} catch (IloException e) {
				LogTool.print("Error in LP: ", "error");
				e.printStackTrace();
			}*/
			
		}
		db.close ();
		
		// Test if it was correctly written
		/*db = new SimpleDB ();
		db.print ();*/
		
		//System.exit (0);
		// End of db test */
	/*
		int ptvSize = 0;
		int oarSize = 0;
		
		Voxel [][][] body = new Voxel[Config.xDIM][Config.yDIM][Config.zDIM];		// This is the body of the "patient"
		LogTool.print("Created Body Array!","notification");*/
		
		/* Initialize the body Array *//*

		for(int x=0;x<Config.xDIM;x++) {
			
			for(int y=0;y<Config.yDIM;y++) {
				
				for(int z=0;z<Config.zDIM;z++) {
					
					
					body[x][y][z] = new Voxel(x, y, z);
					if( (x >= Config.ptvXLow && x <= Config.ptvXHigh) && (y >= Config.ptvYLow && y <= Config.ptvYHigh) && (z >= Config.ptvZLow && z <= Config.ptvZHigh)  ) {		//consider whether the current voxel belongs to the tumor or not
						body[x][y][z].setGoalDosis(Config.tumorGoalDose);
						body[x][y][z].setBodyType(Config.tumorType);
						ptvSize++;
					} else {
						body[x][y][z].setGoalDosis(Config.normalGoalDose);
						body[x][y][z].setBodyType(Config.normalType);
						oarSize++;
					}
						
					
				}
			}	
		}*/
		/* Initialize the Seeds *//*
		 
		Seed[] seeds = new Seed[Config.numberOfSeeds];
		
		for(int i=0; i < Config.numberOfSeeds; i++)
		{				
			seeds[i] = new Seed(RandGenerator.randDouble(Config.ptvXLow,Config.ptvXHigh),RandGenerator.randDouble(Config.ptvYLow,Config.ptvYHigh),RandGenerator.randDouble(Config.ptvZLow,Config.ptvZHigh),0);
		}
		
		LogTool.print("Initialized Body Array!","notification");
		
		Solver solver = new Solver(body,seeds);			// The Solver implements the genetic Algorithm
		LogTool.print("Initialized Solver!","notification");
	
		solver.solveGeneticAlg();
		*/
		// LP:
		/*
		try {
			solver.solveLP();
		} catch (IloException e) {
			LogTool.print("Error in LP: ", "error");
			e.printStackTrace();
		}
		*/
	}
}
