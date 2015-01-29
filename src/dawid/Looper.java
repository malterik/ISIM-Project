package dawid;

import erik.DoseEvaluator;
import erik.Solver;
import utils.Config;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;
import java.lang.Math;
import java.util.Arrays;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Dawid
 */
public class Looper {
    
    /**
 * Creates 
 *  <strong> Using the default constructor will not work.</strong>
 * Use <i>new Looper(body,seeds);   </i>
 * @param  seeds Seed[][][] Necessary for constructor
 * @param  body Voxel[][][]Necessary for constructor
 * @return      the image at the specified URL
 * @see         Image
 */
    
    private  Voxel [][][] body; //Thobi hat das als ganz einfache Variable in seiner Loesermethode...nicht so OOP
    private  Seed[] seeds = new Seed[Config.SAnumberOfSeeds];
    private  Voxel [][][] body2; //Thobi hat das als ganz einfache Variable in seiner Loesermethode...nicht so OOP
    private  Seed[] seeds2 = new Seed[Config.SAnumberOfSeeds];
    public double[] Cur_state = new double[Config.SAnumberOfSeeds];
    public double[] New_state = new double[Config.SAnumberOfSeeds]; // Do I even need this ?
    public double[] Global_Lowest_state = new double[Config.SAnumberOfSeeds]; // Do I even need this ?
    public GlobalState GLowestState;
    
    private double Cur_cost, New_cost, Global_lowest_cost, temperature, newfitnessValue, curfitnessValue;
    
    /**
     * WHAT THIS DOES
     * @param body
     * The body of the patient
     * @param seeds
     * The seeds which should be optimized
     */
    
    public Looper(Voxel [][][] body, Seed[] seeds) {
        this.temperature = Config.StartTemp;
        this.body = body;
        this.seeds = seeds;
        this.body2 = body;
        this.seeds2 = seeds;
//                this.Cur_cost = Cur_cost;
//                this.New_cost = New_cost;
//                this.temperature = temperature;
//                this.Cur_state = Cur_state;
//                this.New_state = New_state;
                this.Global_lowest_cost = Double.MAX_VALUE;
//                this.Global_Lowest_state = Global_Lowest_state;
//                LogTool.print("Cur_state : " + Cur_state + " Zeit 2 : " + Zeit2 + " body : " + New_state, "notification");
//                this.dwelltimes = Looper.seeds().;
                        }

    /**
 * Copies the Durations from the seeds object that Looper has been constructed with
 * (i.e. call by value)
 * @param  Cur_state  a double[] containing the dwelltimes for each seed
 * @return      Writes to Cur_state, doesnt return anything
 */
    private void initState() {
        double metaintensity=0;
        double diffr=0;
         for(int ii=0; ii < Config.SAnumberOfSeeds; ii++)
            {
                Cur_state[ii] = this.seeds[ii].getDurationMilliSec();
//                Zeit2 = this.seeds[1].getDurationMilliSec();
//                Zeit3 = this.seeds[2].getDurationMilliSec();
//                LogTool.print("Zeit 1 : " + Zeit1 + "Zeit 2 : " + Zeit2 + "Zeit 3 : " + Zeit3, "notification");
//                LogTool.print("initState: Dwelltime Seed  " + ii + " : " + Cur_state[ii], "notification");
//                Cur_state[0] = Zeit1;
//                Cur_state[1] = Zeit2;
//                Cur_state[2] = Zeit3;
            }
        for(int x=0; x < Config.xDIM; x++) {
//        for(int x=Solver.xBoundsTumor[0]; x < Solver.xBoundsTumor[1]; x++) {
            for(int y=0; y < Config.yDIM; y++) {
//            for(int y=Solver.yBoundsTumor[0]; y < Solver.yBoundsTumor[1]; y++) {
                for(int z=0; z < Config.zDIM; z++) {
//                for(int z=Solver.zBoundsTumor[0]; z < Solver.zBoundsTumor[1]; z++) {

//                    this.body2[x][y][z].setCurrentDosis(0.0);  //Set currentPtvVoxel Dose to 0 
                    this.body2[x][y][z].metavalue = 0.0;
                        for(int i=0; i<Config.SAnumberOfSeeds;++i) { 
                            // Calculate intensity based based on current dwelltime
                            metaintensity = this.body2[x][y][z].radiationIntensityNoTime((this.seeds2[i].getCoordinate()));
    //                                radiationIntensityNoTime(this.seeds2[i].getCoordinate(), New_state[i]);
                                                    if (metaintensity>0) {
    //                                                LogTool.print("Cost: Intensity :" + intensity + "@ " + x + " " + y + " " + z,"notification");
                                                    }
    //                        this.body2[x][y][z].addCurrentDosis(metaintensity);
                              this.body2[x][y][z].metavalue += metaintensity;                      
                        }   
                }    
            }
        }
        this.body = this.body2;
        diffr = (this.body[2][2][3].metavalue-this.body2[2][2][3].metavalue);
        LogTool.print("BODYDIFFR CHECK AT INIT!!!!!!!!!! :" + diffr + "@ 2,2,3 ","notification");
        
    }
    /**
    * Works. Randomly determines a new state vector
    * @param New_state Contains randomized dwelltimes
    * @param Config.SAnumberOfSeeds Contains randomized dwelltimes
    */
    
    /**
     * Works.Randomly determines a new state vector
     * @param New_state Contains randomized dwelltimes
     * @param Config .SAnumberOfSeeds Contains randomized dwelltimes
     * @return
     */
    public double[] newstater (){
        double[] styles = new double[Config.SAnumberOfSeeds];
        for(int iii=0; iii < Config.SAnumberOfSeeds; iii++)
            {
                styles[iii] = RandGenerator.randDouble(0, 5);
        }
        return styles;
    }
    
    
    private void newState() {
         for(int iii=0; iii < Config.SAnumberOfSeeds; iii++)
            {
                New_state[iii] = RandGenerator.randDouble(0, 10);
//                LogTool.print("newState: Dwelltime Seed  " + iii + " : " + Cur_state[iii], "notification");
//                LogTool.print("newState: Zeit " + iii + " : " + New_state[iii], "notification");
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

    public void setGlobal_Lowest_state(double[] Global_Lowest_state) {
        this.Global_Lowest_state = Global_Lowest_state;
    }

    public void setGlobal_lowest_cost(double Global_lowest_cost) {
        this.Global_lowest_cost = Global_lowest_cost;
    }

    public double getGlobal_lowest_cost() {
        return Global_lowest_cost;
    }

    public double[] getGlobal_Lowest_state() {
        return Global_Lowest_state;
    }
    
    public String getGlobal_Lowest_state_string() {
        String Global_Lowest_state_string = new String();
        for (int cc = 0; cc < Config.SAnumberOfSeeds; cc++) {
            Global_Lowest_state_string = Global_Lowest_state_string.concat(" " + cc + ") " + Global_Lowest_state[cc]);
            }
        return Global_Lowest_state_string;
    }

    public GlobalState getGLowestState() {
        return GLowestState;
    }
    
    public void setCur_cost(double Cur_cost) {
        this.Cur_cost = Cur_cost;
    }
/**
 * 
 * @return String of the new state vector entries 
 */
    public String getNew_state_string() {
        String NewState = new String();
        for (int aa = 0; aa < Config.SAnumberOfSeeds; aa++) {
            NewState = NewState.concat(" " + aa + ") " + New_state[aa]);
            }
//        NewState = "1: " + New_state[0] + " 2: " + New_state[1] + " 3: " + New_state[2] + "";
        return NewState;
    }
/**
 * 
 * @param New_state 
 */
    public void setNew_state(double[] New_state) {
        this.New_state = New_state;
    }
    
    public double[] getNew_state(double[] New_state) {
        return New_state;
    }
    
/**
 * 
 * @return String containing a <i>formatted</i> output of the dwelltimes
 */
    public String getCur_state_string() {
        String CurState = new String();
//        CurState = "1: " + Cur_state[0] + " 2: " + Cur_state[1] + " 3: " + Cur_state[2] + "";
        for (int aa = 0; aa < Config.SAnumberOfSeeds; aa++) {
            CurState = CurState.concat(" " + aa + ") " + Cur_state[aa]);
            }
        return CurState;
    }
// following works    
    public void setCur_state(double[] Cur_state) {
        this.Cur_state = Cur_state;
    }
    
    public double[] getCur_state(double[] Cur_state) {
        return Cur_state;
    }
    
/**
 * 
 * @return The Seed[] of seeds
 */
    public Seed[] getSeed()
    {
        return seeds;
    }
    
    /**
     * This method solves the optimization problem with simulated annealing
         * It contains the metropolis loop
     */
    public void solveSA() {
        initState();
        for (int ab = 0; ab < Config.NumberOfMetropolisResets; ab++) {
            LogTool.print("==================== START CALC FOR OUTER ROUND " + ab + "=========================","notification");
            LogTool.print("SolveSA: Cur_State Read before Metropolis : A)" + Cur_state[0] + " B) " + Cur_state[1] + " C) " + Cur_state[2],"notification");

                if (Config.SAverboselvl>1) {
                    LogTool.print("SolveSA: Cur_State Read before Metropolis : A)" + Cur_state[0] + " B) " + Cur_state[1] + " C) " + Cur_state[2],"notification");
                    LogTool.print("Debug: GLS get before loop only once each reset: " + this.getGlobal_Lowest_state_string(),"notification");
                }
            
            setCur_cost(costCURsuper());
//              setCur_cost(costCUR());
//            setcurfitnessValue(evaluate());

            /* [Newstate] with random dwelltimes */
            New_state = newstater(); 
            if (Config.SAverboselvl>1) {
                LogTool.print("SolveSA: New State before Metropolis: A)" + New_state[0] + " B) " + New_state[1] + " C) " + New_state[2],"notification");
            }
            
            setNew_cost(costNEXsuper());
//            setNew_cost(costNEX());
//            setnewFitnessValue(evaluate());
            
            if (Config.SAverboselvl>1) {
                LogTool.print("SolveSA: New Cost : " + New_cost,"notification");
            }

            /**
                * MetropolisLoop
                * @param Config.NumberOfMetropolisRounds
             */

            for(int x=0;x<Config.NumberOfMetropolisRounds;x++) {   
                LogTool.print("SolveSA Iteration " + x + " Curcost " + Cur_cost + " Newcost " + New_cost,"notification");
               if ((Cur_cost - New_cost)>0) { // ? die Kosten
                   
                   if (Config.SAverboselvl>1) {
                       LogTool.print("Fall 1 START","notification");
                   }
                   
                   if (Config.SAdebug) {                      
                          LogTool.print("SolveSA: (Fall 1) Metropolis NewCost : " + this.getNew_cost(),"notification");
                          LogTool.print("SolveSA: (Fall 1) Metropolis CurCost : " + this.getCur_cost(),"notification");
                          LogTool.print("SolveSA Cost delta " + (Cur_cost - New_cost) + " ","notification");
                   }
                   Cur_state = New_state;
                   Cur_cost = New_cost;
                   if (Config.SAverboselvl>1) {
                   LogTool.print("SolveSA: (Fall 1 nach set) Metropolis NewCost : " + this.getNew_cost(),"notification");
                   LogTool.print("SolveSA: (Fall 1 nach set) Metropolis CurCost : " + this.getCur_cost(),"notification");
                   LogTool.print("SolveSA: (Fall 1 nach set): NewState : " + this.getNew_state_string(),"notification");
                   LogTool.print("SolveSA: (Fall 1 nach set): CurState : " + this.getCur_state_string(),"notification");
                   }
                   New_state = newstater();
                   if (Config.SAdebug) {
                           LogTool.print("SolveSA C1 after generate: NewCost : " + this.getNew_cost(),"notification");
                           LogTool.print("SolveSA C1 after generate: CurCost : " + this.getCur_cost(),"notification");
                           LogTool.print("SolveSA C1 after generate: NewState : " + this.getNew_state_string(),"notification");
                           LogTool.print("SolveSA C1 after generate: CurState : " + this.getCur_state_string(),"notification");
                   }
                   if (Config.SAverboselvl>1) {
                       LogTool.print("Fall 1 STOP ","notification");
                   }
                   } else if (Math.exp(-(New_cost - Cur_cost)/temperature)> RandGenerator.randDouble(0.01, 0.99)) {
                       if (Config.SAverboselvl>1) {
                           LogTool.print("Fall 2 START: Zufallsgenerierter Zustand traegt hoehere Kosten als vorhergehender Zustand. Iteration: " + x,"notification");
                       }
                       if (Config.SAdebug) {
                           LogTool.print("SolveSA C2 before set: NewCost : " + this.getNew_cost(),"notification");
                           LogTool.print("SolveSA C2 before set: CurCost : " + this.getCur_cost(),"notification");
                           LogTool.print("SolveSA C2 before set: NewState : " + this.getNew_state_string(),"notification");
                           LogTool.print("SolveSA C2 before set: CurState : " + this.getCur_state_string(),"notification");
                       }
                       Cur_state = New_state;
                       Cur_cost = New_cost;
                       if (Config.SAdebug) {
                           LogTool.print("SolveSA C2 after set: NewCost : " + this.getNew_cost(),"notification");
                           LogTool.print("SolveSA C2 after set: CurCost : " + this.getCur_cost(),"notification");
                       }
                       New_state = newstater();
                       if (Config.SAdebug) {
                           LogTool.print("SolveSA C2 after generate: NewCost : " + this.getNew_cost(),"notification");
                           LogTool.print("SolveSA C2 after generate: CurCost : " + this.getCur_cost(),"notification");
                           LogTool.print("SolveSA C2 after generate: NewState : " + this.getNew_state_string(),"notification");
                           LogTool.print("SolveSA C2 after generate: CurState : " + this.getCur_state_string(),"notification");
                       }
                       if (Config.SAverboselvl>1) {
                           LogTool.print("Fall 2 STOP: Zufallsgenerierter Zustand traegt hoehere Kosten als vorhergehender Zustand. Iteration: " + x,"notification");
                       }
                   } else {
                       New_state = newstater();
                   }
               temperature = temperature-1;
               if (temperature==0)  {
                   break;
               }
               
               setNew_cost(costNEXsuper());
//               setNew_cost(costNEX());
               if ((x==58)&(ab==0)) {
               LogTool.print("Last internal Iteration Checkpoint","notification");
                if ((Cur_cost - New_cost)>0) {
                  Cur_state = New_state;
                  Cur_cost = New_cost;  
                }
               }
               if ((x>58)&(ab==0)) {
               LogTool.print("Last internal Iteration Checkpoint","notification");
               }
            }
            
//            if (ab==9) {
//                double diff=0;
//            }
            
            // Hier wird kontrolliert, ob das minimalergebnis des aktuellen
            // Metropolisloops kleiner ist als das bsiher kleinste
            
            if (Cur_cost<Global_lowest_cost) {
                this.setGlobal_lowest_cost(Cur_cost);
                GlobalState GLowestState = new GlobalState(this.Cur_state);
                LogTool.print("GLS DEDICATED OBJECT STATE OUTPUT  -- " + GLowestState.getGlobal_Lowest_state_string(),"notification");
                this.setGlobal_Lowest_state(GLowestState.getDwelltimes());
                LogTool.print("READ FROM OBJECT OUTPUT  -- " + this.getGlobal_Lowest_state_string(),"notification");
//                LogTool.print("DEBUG: CurCost direct : " + this.getCur_cost(),"notification");        
//                LogTool.print("Debug: Cur<global CurState get : " + this.getCur_state_string(),"notification");
//                LogTool.print("Debug: Cur<global GLS get : " + this.getGlobal_Lowest_state_string(),"notification");
//                this.setGlobal_Lowest_state(this.getCur_state(Cur_state));
//                LogTool.print("Debug: Cur<global GLS get after set : " + this.getGlobal_Lowest_state_string(),"notification");        
            }
            LogTool.print("SolveSA: Outer Iteration : " + ab,"notification");
            LogTool.print("SolveSA: Last Calculated New State/Possible state inner loop (i.e. 99) : " + this.getNew_state_string(),"notification");
//            LogTool.print("SolveSA: Best Solution : " + this.getCur_state_string(),"notification");
            LogTool.print("SolveSA: GLS after all loops: " + this.getGlobal_Lowest_state_string(),"notification");
            LogTool.print("SolveSA: LastNewCost, unchecked : " + this.getNew_cost(),"notification");
            LogTool.print("SolveSA: CurCost : " + this.getCur_cost() + "i.e. lowest State of this round","notification");        
        }
       // return GLowestState;
    }
/**
 * Sets the irradiation of the Tumor volume by iterating over its volume.
 * 
 * @return Returns the cost as a SSD (Sum of squared differences)
 */
    public double costNEX() {
        double diff=0;
        double intensity=0;
        
        for(int x=Config.ptvXLow-0; x < Config.ptvXHigh+0; x++) {
//        for(int x=Solver.xBoundsTumor[0]; x < Solver.xBoundsTumor[1]; x++) {
            for(int y=Config.ptvYLow-0; y < Config.ptvYHigh+0; y++) {
//            for(int y=Solver.yBoundsTumor[0]; y < Solver.yBoundsTumor[1]; y++) {
                for(int z=Config.ptvZLow-0; z < Config.ptvZHigh+0; z++) {
//                for(int z=Solver.zBoundsTumor[0]; z < Solver.zBoundsTumor[1]; z++) {

                    this.body2[x][y][z].setCurrentDosis(0.0);  //Set currentPtvVoxel Dose to 0 
                    for(int i=0; i<Config.SAnumberOfSeeds;++i) { 
                        // Calculate intensity based based on current dwelltime
                        intensity = this.body2[x][y][z].radiationIntensity(this.seeds2[i].getCoordinate(), New_state[i]);
                                                if (intensity>0) {
//                                                LogTool.print("Cost: Intensity :" + intensity + "@ " + x + " " + y + " " + z,"notification");
                                                }
                        this.body2[x][y][z].addCurrentDosis(intensity);
                    }   
                    diff += Math.pow((this.body2[x][y][z].getGoalDosis()-this.body2[x][y][z].getCurrentDosis()),2);
//                                        LogTool.print(" diffdose " + (Looper.body2[x][y][z].getGoalDosis()-Looper.body2[x][y][z].getCurrentDosis()),"notification");
                }    
            }
        }
        return Math.sqrt(diff);
//        return Math.random();
    }
    
    public double costNEXsuper() {
        double diff=0;
        double intensity=0;
        
        for(int x=Config.ptvXLow-0; x < Config.ptvXHigh+0; x++) {
//        for(int x=Solver.xBoundsTumor[0]; x < Solver.xBoundsTumor[1]; x++) {
            for(int y=Config.ptvYLow-0; y < Config.ptvYHigh+0; y++) {
//            for(int y=Solver.yBoundsTumor[0]; y < Solver.yBoundsTumor[1]; y++) {
                for(int z=Config.ptvZLow-0; z < Config.ptvZHigh+0; z++) {
//                for(int z=Solver.zBoundsTumor[0]; z < Solver.zBoundsTumor[1]; z++) {

                    this.body2[x][y][z].setCurrentDosis(0.0);  //Set currentPtvVoxel Dose to 0 
                    for(int i=0; i<Config.SAnumberOfSeeds;++i) { 
                        // Calculate intensity based based on current dwelltime
                        intensity = this.body2[x][y][z].metavalue * New_state[i];
                                                if (intensity>0) {
//                                                LogTool.print("Cost: Intensity :" + intensity + "@ " + x + " " + y + " " + z,"notification");
                                                }
                        this.body2[x][y][z].addCurrentDosis(intensity);
                    }   
                    diff += Math.pow((this.body2[x][y][z].getGoalDosis()-this.body2[x][y][z].getCurrentDosis()),2);
//                                        LogTool.print(" diffdose " + (Looper.body2[x][y][z].getGoalDosis()-Looper.body2[x][y][z].getCurrentDosis()),"notification");
                }    
            }
        }
        return Math.sqrt(diff);
//        return Math.random();
    }
    
    public double costCUR() {
        double diff=0;
        double intensity=0;
        
        for(int x=Config.ptvXLow-0; x < Config.ptvXHigh+0; x++) {
//        for(int x=Solver.xBoundsTumor[0]; x < Solver.xBoundsTumor[1]; x++) {
            for(int y=Config.ptvYLow-0; y < Config.ptvYHigh+0; y++) {
//            for(int y=Solver.yBoundsTumor[0]; y < Solver.yBoundsTumor[1]; y++) {
                for(int z=Config.ptvZLow-0; z < Config.ptvZHigh+0; z++) {
//                for(int z=Solver.zBoundsTumor[0]; z < Solver.zBoundsTumor[1]; z++) {

                    this.body[x][y][z].setCurrentDosis(0.0);  //Set currentPtvVoxel Dose to 0 
                    for(int i=0; i<Config.SAnumberOfSeeds;++i) { 
                        // Calculate intensity based based on current dwelltime
                        intensity = this.body[x][y][z].radiationIntensity(this.seeds[i].getCoordinate(), Cur_state[i]);
                                                if (intensity>0) {
//                                                LogTool.print("Cost: Intensity :" + intensity + "@ " + x + " " + y + " " + z,"notification");
                                                }
                        this.body[x][y][z].addCurrentDosis(intensity);
                    }   
                    diff += Math.pow((this.body[x][y][z].getGoalDosis()-this.body[x][y][z].getCurrentDosis()),2);
//                                        LogTool.print(" diffdose " + (Looper.body[x][y][z].getGoalDosis()-Looper.body[x][y][z].getCurrentDosis()),"notification");
                }    
            }
        }
        return Math.sqrt(diff);
//        return Math.random();
    }
    
    public double costCURsuper() {
        double diff=0;
        double intensity=0;
        
        for(int x=Config.ptvXLow-0; x < Config.ptvXHigh+0; x++) {
//        for(int x=Solver.xBoundsTumor[0]; x < Solver.xBoundsTumor[1]; x++) {
            for(int y=Config.ptvYLow-0; y < Config.ptvYHigh+0; y++) {
//            for(int y=Solver.yBoundsTumor[0]; y < Solver.yBoundsTumor[1]; y++) {
                for(int z=Config.ptvZLow-0; z < Config.ptvZHigh+0; z++) {
//                for(int z=Solver.zBoundsTumor[0]; z < Solver.zBoundsTumor[1]; z++) {

                    this.body[x][y][z].setCurrentDosis(0.0);  //Set currentPtvVoxel Dose to 0 
                    for(int i=0; i<Config.SAnumberOfSeeds;++i) { 
                        // Calculate intensity based based on current dwelltime
                        intensity = this.body[x][y][z].metavalue * Cur_state[i];
                                                if (intensity>0) {
//                                                LogTool.print("Cost: Intensity :" + intensity + "@ " + x + " " + y + " " + z,"notification");
                                                }
                        this.body[x][y][z].addCurrentDosis(intensity);
                    }   
                    diff += Math.pow((this.body[x][y][z].getGoalDosis()-this.body[x][y][z].getCurrentDosis()),2);
//                                        LogTool.print(" diffdose " + (Looper.body[x][y][z].getGoalDosis()-Looper.body[x][y][z].getCurrentDosis()),"notification");
                }    
            }
        }
        return Math.sqrt(diff);
//        return Math.random();
    }
    
    public double evaluate() {
        double fitness = 0;
        ExecutorService threadPool = Executors.newFixedThreadPool(Config.numberOfThreads);
        CompletionService<Double> pool = new ExecutorCompletionService<Double>(threadPool);

        
        //TODO bound calculation
        // Create tasks and submit them to the pool
        for(int i = Solver.xBoundsTumor[0]; i < Solver.xBoundsTumor[1]; i++){
           pool.submit(new DoseEvaluator(Solver.dimensions, Cur_state,i));
        }
        // The results will be stored here
        double[] partial_result = new double[(Solver.xBoundsTumor[1] - Solver.xBoundsTumor[0])];
        
        for(int i = 0; i < (Solver.xBoundsTumor[1] - Solver.xBoundsTumor[0]) ; i++){
          try {
            partial_result[i] = pool.take().get();
            //System.out.println(i+" Threads finished;");
            fitness += partial_result[i];
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }
        fitness = Math.sqrt(fitness);
        this.setnewFitnessValue(fitness);
        return fitness;
    }
    

    private void setnewFitnessValue(double fitnessValue) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.newfitnessValue = fitnessValue;
    }

    public void setcurfitnessValue(double curfitnessValue) {
        this.curfitnessValue = curfitnessValue;
    }

    public double getNewfitnessValue() {
        return newfitnessValue;
    }

    public double getcurfitnessValue() {
        return curfitnessValue;
    }
    
    
}