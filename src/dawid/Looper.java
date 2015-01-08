/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dawid;

import erik.Solver;
import utils.Config;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;
import java.lang.Math;

/**
 *
 * @author Dawid
 */
public class Looper {
    
    /**
     *
     * @param args
     */
    
    public static Voxel [][][] body;
    public static Seed[] seeds = new Seed[Config.numberOfSeeds];
//    private double cost;
    public static double[] dwelltimes;
    private double Cur_cost, New_cost, temperature;
    private double[] Cur_state;
    
    /**
	 * WHAT THIS DOES
	 * @param body
	 * The body of the patient
	 * @param seeds
	 * The seeds which should be optimized
	 */
	
    public Looper(Voxel [][][] body, Seed[] seeds) {
		this.body = body;
		this.seeds = seeds;
//                this.cost = cost;
                this.temperature = temperature;
                this.dwelltimes = dwelltimes;
	}
    
    public static Seed[] getSeed()
	{
		return seeds;
	}
    /**
	 * This method solves the optimization problem with simulated annealing
	 */
    public void solveSA() {
        Cur_cost = this.cost();
        New_cost = this.cost();
        
	for(int x=0;x<Config.NumberOfMetropolisRounds;x++) {    
           if ((Cur_cost - New_cost)<0) {
                    dwelltimes = Cur_state;
                    
                    return;
//                } else if ( Math.exp((Cur_cost - New_cost)/temperature) )> RandGenerator.randDouble(0.01, 0.99) {
               
                }
 
        LogTool.print("It runs lol","notification");
        }
        
    }
    
    public Seed NewState() {
        return null;
        
    }
    
    public Seed CurState() {
        return null;
        
    }
    public double cost() {
//        double Energy = 0;
        double diff=0;
        double intensity=0;
        
        
        for(int x=Config.ptvXLow-10; x < Config.ptvXHigh+10; x++) {
			
			for(int y=Config.ptvYLow-10; y < Config.ptvYHigh+10; y++) {
				
				for(int z=Config.ptvZLow-10; z < Config.ptvZHigh+10; z++) {
					
					Solver.body[x][y][z].setCurrentDosis(0);  //What does this line do ?
					for(int i=0; i<Config.numberOfSeeds;++i) { 
						
						intensity = Solver.body[x][y][z].radiationIntensity(Solver.seeds[i].getCoordinate(), dwelltimes[i]);
						Solver.body[x][y][z].addCurrentDosis(intensity);
					}	
					diff += Math.pow((Solver.body[x][y][z].getGoalDosis()-Solver.body[x][y][z].getCurrentDosis()),2);		
				}	
			}
		}
//        this.setFitnessValue(cost);
        return Math.sqrt(diff);
    }

//    private void setFitnessValue(double cost) {
//        try {
//            
//            return cost;
//            
//        } catch (Exception e) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//        
//    }
    
    
}