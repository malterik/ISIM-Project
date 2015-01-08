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
                                        ptvSize++;
                                } else {
                                        body[x][y][z].setGoalDosis(Config.oarGoalDose);
                                        oarSize++;
                                }


                        }
                }	
        }
        LogTool.print("Initialized Body Array!","notification");
                
    /* Init the seeds */    
        Seed[] seeds = new Seed[Config.numberOfSeeds];

        for(int i=0; i < Config.numberOfSeeds; i++)
        {				
            seeds[i] = new Seed(RandGenerator.randDouble(Config.ptvXLow,Config.ptvXHigh),RandGenerator.randDouble(Config.ptvYLow,Config.ptvYHigh),RandGenerator.randDouble(Config.ptvZLow,Config.ptvZHigh),0);
        }
        LogTool.print("Initialized Seeds!","notification");
        
    /* Attack the problem using SA */
        
        Looper looper = new Looper(body,seeds);			
        LogTool.print("Initialized Solver!-> " + seeds[2].getDurationMilliSec() ,"notification");
        looper.solveSA(); 

//        The looper needs to establish an initial solution and temperature
//        run for 1000 runs
//        determine a new state by using the metropolis function with probability jumps
//        determine the cost of the new state       
    }    
}