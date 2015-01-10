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
import com.rits.cloning.Cloner;

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
    public double Zeit1,Zeit2,Zeit3;
//    public static Seed[] Cur_state = new Seed[Config.SAnumberOfSeeds];
//    public static Seed[] New_state = new Seed[Config.SAnumberOfSeeds];
    public double[] Cur_state = new double[Config.SAnumberOfSeeds];
    public double[] New_state = new double[Config.SAnumberOfSeeds]; // Do I even need this ?
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
                initState();
//                LogTool.print("Cur_state : " + Cur_state + " Zeit 2 : " + Zeit2 + " body : " + New_state, "notification");
//                this.dwelltimes = Looper.seeds().;
                        }
 
//    Idea to implement a copy constructor....but this would have to be in the seed class...
//  Looper copyFoo (Foo foo){
//  Foo f = new Foo();
//  //for all properties in FOo
//  f.set(foo.get());
//  return f;
//}

    private void initState() {
         for(int ii=0; ii < Config.SAnumberOfSeeds; ii++)
            {
                Cur_state[ii] = this.seeds[ii].getDurationMilliSec();
//                Zeit2 = this.seeds[1].getDurationMilliSec();
//                Zeit3 = this.seeds[2].getDurationMilliSec();
//                LogTool.print("Zeit 1 : " + Zeit1 + "Zeit 2 : " + Zeit2 + "Zeit 3 : " + Zeit3, "notification");
                LogTool.print("Zeit " + ii + " : " + Cur_state[ii], "notification");
//                Cur_state[0] = Zeit1;
//                Cur_state[1] = Zeit2;
//                Cur_state[2] = Zeit3;
            }
    }
    
    private void newState() {
         for(int iii=0; iii < Config.SAnumberOfSeeds; iii++)
            {
                New_state[iii] = RandGenerator.randDouble(0, 10);
                LogTool.print("Zeit " + iii + " : " + New_state[iii], "notification");
            }
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
    
    public String getNew_state() {
        String NewState = new String();
        NewState = "1: " + New_state[0] + " 2: " + New_state[1] + " 3: " + New_state[2] + "";
        return NewState;
    }

    public void setNew_state(double[] New_state) {
        this.New_state = New_state;
    }

    public String getCur_state() {
        String CurState = new String();
        CurState = "1: " + Cur_state[0] + " 2: " + Cur_state[1] + " 3: " + Cur_state[2] + "";
        return CurState;
    }
    
    public void setCur_state(double[] Cur_state) {
        this.Cur_state = Cur_state;
    }
    
    public static Seed[] getSeed()
	{
		return seeds;
	}
    
    /**
	 * This method solves the optimization problem with simulated annealing
         * It contains the metropolis loop
	 */
    public void solveSA() {
        Cur_cost = cost();
//        this.initState();
//        LogTool.print("SolveSA: Initial State : A)" + Looper.seeds[0].getDurationMilliSec() + " B) " + seeds[1].getDurationMilliSec() + " C) " + seeds[2].getDurationMilliSec(),"notification");
        LogTool.print("SolveSA: Initial State : A)" + Cur_state[0] + " B) " + Cur_state[1] + " C) " + Cur_state[2],"notification");
     //[Newstate] with random dwelltimes
        newState(); 
        LogTool.print("SolveSA: New State : A)" + New_state[0] + " B) " + New_state[1] + " C) " + New_state[2],"notification");
        New_cost = cost();
        LogTool.print("SolveSA: New Cost : A)" + New_cost,"notification");
        

        
        //[Cur=New]
        //[]
        
//        this.Cur_State = 
        
        /**
            * MetropolisLoop - 
	 */
	for(int x=0;x<Config.NumberOfMetropolisRounds;x++) {   
           if ((Cur_cost - New_cost)>0) { //Minimiert die Kosten
                    Cur_state = New_state;
//                    LogTool.print("Cost CurisNull ? ->  " + Cur_state + "","notification");
                    LogTool.print("SolveSA Iteration " + x + "","notification");
                } else if (Math.exp((Cur_cost - New_cost)/temperature)> RandGenerator.randDouble(0.01, 0.99)) {
                    Cur_state = New_state; 
                }
           temperature = temperature-1;
        }
        LogTool.print("SolveSA: Solution : A)" + this.getCur_state(),"notification");
        //
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
//        return Math.sqrt(diff);
        return Math.random();
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