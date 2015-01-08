/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dawid;

import utils.Config;
import utils.LogTool;
import utils.Seed;
import utils.Voxel;

/**
 *
 * @author Dawid
 */
public class Cost {
    
    /**
     *
     * @param args
     */
    
    public static Voxel [][][] body;
    public static Seed[] seeds = new Seed[Config.numberOfSeeds];
    
    /**
	 * WHAT THIS DOES
	 * @param body
	 * The body of the patient
	 * @param seeds
	 * The seeds which should be optimized
	 */
	
    public Cost(Voxel [][][] body, Seed[] seeds) {
		this.body = body;
		this.seeds = seeds;
	}
/**
	 * This method solves the optimization problem with simulated annealing
	 */
    public void CalcCost() {
	for(int x=0;x<Config.xDIM;x++) {

                for(int y=0;y<Config.yDIM;y++) {

                        for(int z=0;z<Config.zDIM;z++) {
                                
                            
                            
                            
                            
                            
//                                body[x][y][z] = new Voxel(x, y, z);
//                                if( (x >= Config.ptvXLow && x <= Config.ptvXHigh) && (y >= Config.ptvYLow && y <= Config.ptvYHigh) && (z >= Config.ptvZLow && z <= Config.ptvZHigh)  ) {		//consider whether the current voxel belongs to the tumor or not
//                                        body[x][y][z].setGoalDosis(Config.ptvGoalDose);
//                                        ptvSize++;
//                                } else {
//                                        body[x][y][z].setGoalDosis(Config.oarGoalDose);
//                                        oarSize++;
//                                }


                        }
                }	
        }
    }
}