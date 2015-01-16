/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dawid;

import erik.Solver;
import dawid.Looper;
import utils.Config;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;

/**
 *
 * @author testo-san
 */
public class main {
    
    public static void main(String[] args) {
    
        Voxel [][][] body = new Voxel[Config.xDIM][Config.yDIM][Config.zDIM];
    
        int ptvSize = 0;
	int oarSize = 0;
        
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
        LogTool.print("     MaxDose 3,0,1 : " + body[53][50][50].getMaxDosis() + "","notification");
        LogTool.print("     MaxDose 1,1,1 : " + body[5][5][5].getMaxDosis() + "","notification");
        LogTool.print("     Type 3,0,1 : " + body[3][0][1].getBodyType()+ "","notification");
        LogTool.print("     Type 1,1,1 : " + body[5][5][5].getBodyType() + "","notification");
                
    /* Init the seeds */    
        Seed[] seeds = new Seed[Config.SAnumberOfSeeds];

        for(int i=0; i < Config.SAnumberOfSeeds; i++)
        {				
            seeds[i] = new Seed(RandGenerator.randDouble(Config.ptvXLow,Config.ptvXHigh),RandGenerator.randDouble(Config.ptvYLow,Config.ptvYHigh),RandGenerator.randDouble(Config.ptvZLow,Config.ptvZHigh),RandGenerator.randDouble(1, 5));
        }
        LogTool.print("Initialized Seeds!","notification");
        LogTool.print("     Coordinates:","notification");
        //Refactor: put these two in one for loop
            for(int i=0; i < Config.SAnumberOfSeeds; i++)
            {
                LogTool.print(seeds[i].getX() + " " + seeds[i].getY() + " " + seeds[i].getZ(),"notification");
            }
//        LogTool.print("     Seedtimes : A)" + seeds[0].getDurationMilliSec() + " B) " + seeds[1].getDurationMilliSec() + " C) " + seeds[2].getDurationMilliSec(),"notification");
        
    /* Attack the problem using SA */
        
        Looper looper = new Looper(body,seeds);			
        LogTool.print("Initialized Looper Object! -> ","notification");
        LogTool.print("Beginning Annealing...","notification");
//        LogTool.print("seeds, curstate,newstate" + looper.getCur_state() + " " + looper.getNew_state() + " ","notification");
        
        looper.solveSA(); 

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