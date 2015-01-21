package dawid;

import erik.Solver;
import dawid.Looper;
import sebastian.BodyEntry;
import sebastian.SimpleDB;
import utils.Config;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;

/**
 *
 * @author Dawid G.
 * @version 0.8
 * 
 */
public class TreatmentSA {

        public static int[] dimensions;
	static public int[] xBoundsTumor;
	static public int[] yBoundsTumor;
	static public int[] zBoundsTumor;
	
/**
 *
 * Creates classification 
 * @see #db
 * 
 */    
        
    public static void main(String[] args) {
    
        
        Voxel [][][] body = new Voxel[Config.xDIM][Config.yDIM][Config.zDIM];
        
    
        int ptvSize = 0;
	int oarSize = 0;
        
    /* Init DB */    
        
//        SimpleDB db = new SimpleDB ();
//	db.loadBody("data2593.4844");
//	BodyEntry entry = db.getBodyByName("data2593.4844");
//	Voxel [][][] body = null;
//    
//        if (entry != null) {
//			//ScatterDisplay display = new ScatterDisplay(ChartType.BodyType);
//			//display.fill(entry.getBodyArray(), entry.getDimensions()[0], entry.getDimensions()[1], entry.getDimensions()[2]);
//			//display.display ();			
//			
//		    body = new Voxel[entry.getDimensions()[0]][entry.getDimensions()[1]][entry.getDimensions()[2]];		// This is the body of the "patient"
//			LogTool.print("Created Body Array!","notification");
//			
//			for(int x = 0; x < entry.getDimensions()[0]; x++) {
//				
//				for(int y = 0; y < entry.getDimensions()[1]; y++) {
//					
//					for(int z = 0; z < entry.getDimensions()[2]; z++) {
//						
//						body[x][y][z] = new Voxel(x, y, z);
//						
//						switch(entry.getBodyArray()[x][y][z].getBodyType())
//						{
//							case Config.normalType:
//							{
//								body[x][y][z].setGoalDosis(Config.normalGoalDose);
//								body[x][y][z].setBodyType(Config.normalType);
//								break;
//							}
//							case Config.spineType:
//							{
//								body[x][y][z].setGoalDosis(Config.spineGoalDose);
//								body[x][y][z].setBodyType(Config.spineType);
//								break;
//							}
//							case Config.liverType:
//							{
//								body[x][y][z].setGoalDosis(Config.liverGoalDose);
//								body[x][y][z].setBodyType(Config.liverType);
//								break;
//							}
//							case Config.pancreasType:
//							{
//								body[x][y][z].setGoalDosis(Config.pancreasGoalDose);
//								body[x][y][z].setBodyType(Config.pancreasType);
//								break;
//							}
//							case Config.tumorType:
//							{
//								body[x][y][z].setGoalDosis(Config.tumorGoalDose);
//								body[x][y][z].setBodyType(Config.tumorType);
//								break;
//							}
//							default:
//							{
//								body[x][y][z].setGoalDosis(Config.normalGoalDose);
//								body[x][y][z].setBodyType(Config.normalType);
//								break;
//							}	
//						}			
//					}
//				}	
//			}
//		}
        
    /* Initialize the body Array */  
        
        for(int x=0;x<Config.xDIM;x++) {
			for(int y=0;y<Config.yDIM;y++) {
				for(int z=0;z<Config.zDIM;z++) {
					
					body[x][y][z] = new Voxel(x, y, z);
					if( (x >= Config.ptvXLow && x <= Config.ptvXHigh) && (y >= Config.ptvYLow && y <= Config.ptvYHigh) && (z >= Config.ptvZLow && z <= Config.ptvZHigh)  ) {		//consider whether the current voxel belongs to the tumor or not
						body[x][y][z].setGoalDosis(Config.ptvGoalDose);
						body[x][y][z].setBodyType(Config.tumorType);
						ptvSize++;
					} else {
						body[x][y][z].setGoalDosis(Config.oarGoalDose);
						body[x][y][z].setBodyType(Config.normalType);
						oarSize++;
					}
				}
			}	
		}
        LogTool.print("Initialized Body Array!","notification");
        
    /* Verify body */     
        if (Config.SAdebugbody) {
        LogTool.print("     MaxDose 3,0,1 : " + body[53][50][50].getMaxDosis() + "","notification");
        LogTool.print("     MaxDose 1,1,1 : " + body[5][5][5].getMaxDosis() + "","notification");
        LogTool.print("     Type 3,0,1 : " + body[3][0][1].getBodyType()+ "","notification");
        LogTool.print("     Type 1,1,1 : " + body[5][5][5].getBodyType() + "","notification");
        }
                
    /* Init the seeds */    
        Seed[] seeds = new Seed[Config.SAnumberOfSeeds];

        for(int i=0; i < Config.SAnumberOfSeeds; i++)
        {	
            // Random radiation time
            seeds[i] = new Seed(RandGenerator.randDouble(Config.ptvXLow,Config.ptvXHigh),RandGenerator.randDouble(Config.ptvYLow,Config.ptvYHigh),RandGenerator.randDouble(Config.ptvZLow,Config.ptvZHigh),RandGenerator.randDouble(1, 5));
            // Zero radiation time
//            seeds[i] = new Seed(RandGenerator.randDouble(Config.ptvXLow,Config.ptvXHigh),RandGenerator.randDouble(Config.ptvYLow,Config.ptvYHigh),RandGenerator.randDouble(Config.ptvZLow,Config.ptvZHigh),0);
        }
        LogTool.print("Initialized Seeds!","notification");
//        LogTool.print(System.getProperty("user.dir") + "/datas", "notification");
        if (Config.SAdebugkoords) {
            LogTool.print("     Coordinates:","notification");
        
        //Refactor: put these two in one for loop
            for(int i=0; i < Config.SAnumberOfSeeds; i++)
            {
                LogTool.print(seeds[i].getX() + " " + seeds[i].getY() + " " + seeds[i].getZ(),"notification");    
            }
        }
        
        LogTool.print("     Seedtimes : A)" + seeds[0].getDurationMilliSec() + " B) " + seeds[1].getDurationMilliSec() + " C) " + seeds[2].getDurationMilliSec(),"notification");
        
    /* Attack the problem using SA */
        
        Looper looper = new Looper(body,seeds);	       
        // Dawid inslucde the verbose switch pls
        LogTool.print("Initialized Looper Object!","notification");
        LogTool.print("Beginning Annealing...","notification");
//        LogTool.print("seeds, curstate,newstate" + looper.getCur_state() + " " + looper.getNew_state() + " ","notification");    
       // GlobalState GLS = looper.solveSA();
//        GlobalState GLS = looper;
        LogTool.print("GLC: " + looper.getGlobal_lowest_cost()+ " CURC: " + looper.getCur_cost(),"notification");
//        LogTool.print("FitnessGlobalLow: " + looper.+ " FitnessCURC: " + looper.getcurfitnessValue(),"notification");
        //LogTool.print("GLS external: " + GLS.getGlobal_Lowest_state_string(),"notification");
        LogTool.print("SolveSA: Global Current Best Solution : " + looper.getGlobal_Lowest_state_string(),"notification");
//        looper.rausfindenWarumCUrCostsichnichtaendert();

//        The looper needs to establish an initial solution and temperature
//        run for 1000 runs
//        determine a new state by using the metropolis function with probability jumps
//        determine the cost of the new state
//        Decide whether to switch to new state
//        Keep looping searching for lower state pseudo-randomly
//        Remember last final solution
//        Redo with different start
//        Delete if solution not cheaper
//        FInish with 10 solutions - choose cheapest
    }    
}