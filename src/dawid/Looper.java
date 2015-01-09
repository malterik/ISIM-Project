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
    
    public static Voxel [][][] body; //Thobi hat das als ganz einfache Variable in seiner Loesermethode...nicht so OOP
    public static Seed[] seeds = new Seed[Config.SAnumberOfSeeds];
    public static Seed[] Cur_State = new Seed[Config.SAnumberOfSeeds];
    public static Seed[] New_State = new Seed[Config.SAnumberOfSeeds];
//    private double cost;
//    public  double[] dwelltimes; // Do I even need this ?
    private double Cur_cost, New_cost, temperature; // Cost is a double...
//    public Seed[] Cur_state = (Seed[]) seeds.clone();
    
//    private Seed[] New_state = seeds; //Essentially a copy of seeds
    
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
                this.Cur_cost = Cur_cost;
                this.New_cost = New_cost;
                this.temperature = temperature;
                this.Cur_state = Cur_state;
                this.New_state = New_state;
//                this.dwelltimes = Looper.seeds().;
                        }

    public double getNew_cost() {
        return New_cost;
    }

    public void setNew_cost(double New_cost) {
        this.New_cost = New_cost;
    }
    
    public double getCur_cost() {
        return Cur_cost;
    }

    public void setCur_cost(double Cur_cost) {
        this.Cur_cost = Cur_cost;
    }
    
    public Seed[] getNew_state() {
        return New_state;
    }

    public void setNew_state(Seed[] New_state) {
        this.New_state = New_state;
    }

    public Seed[] getCur_state() {
        return Cur_state;
    }
    
//    public String getState_String() {
////        Cur_State[0].
//                Cur_State[0].
//        return ;
//    }
    
    public void setCur_state(Seed[] Cur_state) {
        this.Cur_state = Cur_state;
    }
    
//    public void initState() {
//        Looper.seeds.clone()
//    }
    
    public static Seed[] getSeed()
	{
		return seeds;
	}
    /**
	 * This method solves the optimization problem with simulated annealing
         * It contains the metropolis loop
	 */
    public void solveSA() {
//        Cur_cost = this.cost();
//        New_cost = this.cost();
        
        LogTool.print("Initial State : A)" + Looper.seeds[0].getDurationMilliSec() + " B) " + seeds[1].getDurationMilliSec() + " C) " + seeds[2].getDurationMilliSec(),"notification");
        LogTool.print("Initial State : A)" + Cur_state[0] + " B) " + seeds[1].getDurationMilliSec() + " C) " + seeds[2].getDurationMilliSec(),"notification");
//        LogTool.print("Initial State : A)" + Looper.Cur_State[0].getDurationMilliSec() + " B) " + seeds[1].getDurationMilliSec() + " C) " + seeds[2].getDurationMilliSec(),"notification");
        //[Newstate] with random dwelltimes
        
        //[Cur=New]
        //[]
        
//        this.Cur_State = 
        
        /**
            * MetropolisLoop - 
	 */
	for(int x=0;x<Config.NumberOfMetropolisRounds;x++) {   
           if (true) {
//                    dwelltimes = Cur_state;
                    LogTool.print("SolveSA Iteration " + Cur_state + "","notification");
                    LogTool.print("SolveSA Iteration " + x + "","notification");
                } else if (Math.exp((Cur_cost - New_cost))> RandGenerator.randDouble(0.01, 0.99)) {
//                    dwelltimes = null; 
                }
           temperature = temperature-1;
        }
//        return New_Cost;
        
    }
    
    
    
    public double cost() {
        double diff=0;
        double intensity=0;
        
//        for(int x=Config.ptvXLow-10; x < Config.ptvXHigh+10; x++) {
//			for(int y=Config.ptvYLow-10; y < Config.ptvYHigh+10; y++) {
//				for(int z=Config.ptvZLow-10; z < Config.ptvZHigh+10; z++) {
//
//					Looper.body[x][y][z].setCurrentDosis(0);  //What does this line do ?
//					for(int i=0; i<Config.SAnumberOfSeeds;++i) { 
//						
//						intensity = Looper.body[x][y][z].radiationIntensity(Looper.seeds[i].getCoordinate(), dwelltimes[i]);
//						Looper.body[x][y][z].addCurrentDosis(intensity);
//					}	
//					diff += Math.pow((Looper.body[x][y][z].getGoalDosis()-Looper.body[x][y][z].getCurrentDosis()),2);		
//				}	
//			}
//		}
////        this.setFitnessValue(cost);
        
        return Math.sqrt(diff);
//        return Math.random();
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