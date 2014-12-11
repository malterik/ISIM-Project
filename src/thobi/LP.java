package thobi;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.Random;

import utils.Config;
import utils.Voxel;

public class LP {
	
	public static void main(String[] args) throws IloException
	{
		
		model3D();
	}
	
	
	public static void model3D() throws IloException
	{
			
			
			Voxel[][][] body = new Voxel[Config.xDIM][Config.yDIM][Config.zDIM];
			
			
			for(int i = 0; i<Config.xDIM; i++)
			{
				for(int j=0; j<Config.yDIM; j++)
				{
					for(int l=0; l < Config.zDIM; l++)
					{
						body[i][j][l] = new Voxel(i, j, l);
					}
				}
			}
			
			

			
			//setting target
			
			int xmin = 5;
			int xmax = 15;
			int ymin = 20;
			int ymax = 30;
			int zmin = 50;
			int zmax = 60;			
			
			for(int i=xmin; i<=xmax; i++)
			{
				for(int j=ymin; j<=ymax; j++)
				{
					for(int l=zmin; l<=zmax; l++)
					{
						body[i][j][l].setBodyType(Config.tumor);
					}
				}
			}
					
			
			//positions
			double x[][] = new double[Config.numberOfSeeds][3];
			
			for(int i=0; i < Config.numberOfSeeds; i++)
			{
				x[i][0] = randDouble(xmin,xmax);
				x[i][1] = randDouble(ymin,ymax);
				x[i][2] = randDouble(zmin,zmax);
			}
			
			
			
			IloNumExpr dosepart[] = new IloNumExpr[Config.numberOfSeeds];
			
			IloCplex cplex = new IloCplex();
			
			
			//variables
			
			IloNumVar[] time = new IloNumVar[Config.numberOfSeeds];
			
			for(int i = 0; i < Config.numberOfSeeds; i++)
			{
				time[i] = cplex.numVar(0, Double.MAX_VALUE, "t" + i);
			}
			
			//expressions
			IloLinearNumExpr objective = cplex.linearNumExpr();
			
			//iterate over world
			for(int i = 0; i<Config.xDIM; i++)
			{
				for(int j=0; j<Config.yDIM; j++)
				{
					for(int l=0; l < Config.zDIM; l++)
					{
						//check state of world
						if(body[i][j][l].getBodyType()==Config.tumor)
						{
							//iterate over seeds
							for(int k=0; k < Config.numberOfSeeds; k++)
							{
								objective.addTerm(dose3D(i, j, l, x[k][0], x[k][1], x[k][2]), time[k]);
							}
							
						}
						else
						{
							//iterate over seeds
							for(int k=0; k < Config.numberOfSeeds; k++)
							{
								dosepart[k] = cplex.prod(dose(i, j, x[k][0], x[k][1]), time[k]);
							}
							cplex.addLe(cplex.sum(dosepart), Config.normalDose);
						}
					}
				}
			}
			
			cplex.addMaximize(objective);
			
			if(cplex.solve())
			{
				System.out.println("solved");
				for(int k=0; k < Config.numberOfSeeds; k++)
				{
					System.out.println("x" + k + " = " + x[k][0]);
					System.out.println("y" + k + " = " + x[k][1]);
					System.out.println("y" + k + " = " + x[k][2]);
					System.out.println("time" + k + " = " + cplex.getValue(time[k]));
				}
			}
			else
			{
				System.out.println("not solved");
			}
			
			System.out.println("Computing dose world.....");
			
			double dose_eval = 0;
			double time_eval = 0;
			
			for(int i = 0; i < Config.xDIM; i++)
			{
				for(int j = 0; j < Config.yDIM; j++)
				{
					for(int k = 0; k < Config.zDIM; k++)
					{
						for(int l = 0; l < Config.numberOfSeeds; l++)
						{
							time_eval = cplex.getValue(time[l]);
							if(time_eval != 0)
							{
								dose_eval += dose3D(i,j,k,x[l][0],x[l][1],x[l][2])*time_eval;
							}
						}
						
						body[i][j][k].setCurrentDosis(dose_eval);
					}
				}
			}
			
			System.out.println("Done!");

			
			

	}
	
	/*
	public static void model2() throws IloException
	{
		try{
			
			int i,j,k;
			
			final int TARGET = 1;
			final int RISK = 2;
			
			int world[][] = new int[10][10];
			for(i = 0; i<10; i++)
			{
				for(j=0; j<10; j++)
				{
					world[i][j] = 0;
				}
			}
			
			world[5][4] = TARGET;
			world[5][5] = TARGET;
			world[5][6] = TARGET;
			world[6][4] = TARGET;
			world[6][5] = TARGET;
			world[6][6] = TARGET;
			world[7][4] = TARGET;
			world[7][5] = TARGET;
			world[7][6] = TARGET;
			
			IloNumExpr dosepart[] = new IloNumExpr[5];
			
			IloCplex cplex = new IloCplex();
			
			//variables
			
			IloNumVar[][] seed = new IloNumVar[5][3];
			
			for(i = 0; i < 5; i++)
			{
				for(j = 0; j < 3; j++)
				{
					if(j==0) //x-coordinate
					{
						seed[i][j] = cplex.numVar(0, 10, "x" + i);
					}
					else if(j==1) //y-coordinate
					{
						seed[i][j] = cplex.numVar(0, 10, "y" + i);
					}
					else if(j==2) //time
					{
						seed[i][j] = cplex.numVar(0, Double.MAX_VALUE, "t" + i);
					}
				}
			}
			
			//expressions
			IloLinearNumExpr objective = cplex.linearNumExpr();
			
			//iterate over world
			for(i = 0; i<10; i++)
			{
				for(j=0; j<10; j++)
				{
					//check state of world
					if(world[i][j]==1)
					{
						//iterate over seeds
						for(k=0; k<=4; k++)
						{
							objective.addTerm(dose(i, j, x[k][0], x[k][1]), time[k]);
							objective.addTerm
						}
						
					}
					else
					{
						//iterate over seeds
						for(k=0; k<=4; k++)
						{
							dosepart[k] = cplex.prod(dose(i, j, x[k][0], x[k][1]), time[k]);
						}
						cplex.addLe(cplex.sum(dosepart), 100);
					}
				}
			}
			
			cplex.addMaximize(objective);
			
			if(cplex.solve())
			{
				System.out.println("solved");
				for(k=0; k <=4; k++)
				{
					System.out.println("time" + k + " = " + cplex.getValue(time[k]));
				}
			}
			else
			{
				System.out.println("not solved");
			}
			
			
			
		}
		catch (IloException e) {
			System.err.println("Concert exception '" + e + "' caught");
		}

	}
*/	
	public static double dose(double centre1,double centre2,double x1,double x2)
	{
		double result;
		double distance = distance2D(centre1, centre2, x1, x2);
		
		//compute does
		result = 2/(distance+1);
		
		if(result < 0)
			result = 0;
		
		return result;
	}
	
	public static double distance2D(double centre1,double centre2,double x1,double x2)
	{
		return Math.sqrt((centre1-x1)*(centre1-x1)+(centre2-x2)*(centre2-x2));
	}
	
	public static int randInt(int min, int max) {

	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public static double randDouble(double min, double max)
	{
		Random r = new Random();
		double randomValue = min + (max - min) * r.nextDouble();
		return randomValue;
	}
	
	
	public static double dose3D(double centre1, double centre2, double centre3, double x1, double x2, double x3)
	{
		double result;
		double distance = distance3D(centre1, centre2, centre3, x1, x2, x3);
		
		//compute does
		result = 2/(distance+1);
		
		if(result < 0)
			result = 0;
		
		return result;
	}	
	
	public static double distance3D(double centre1, double centre2, double centre3, double x1, double x2, double x3)
	{
		return Math.sqrt((centre1-x1)*(centre1-x1)+(centre2-x2)*(centre2-x2)+(centre3-x3)*(centre3-x3));
	}
	
	

}

/*
public static void main(String[] args) throws IloException{

      try {
         // Create the modeler/solver object
         IloCplex cplex = new IloCplex();

         IloNumVar[][] var = new IloNumVar[1][];
         IloRange[][]  rng = new IloRange[1][];


         populateByColumn(cplex, var, rng);
         
         // write model to file
         cplex.exportModel("lpex1.lp");

         // solve the model and display the solution if one was found
         if ( cplex.solve() ) {
            double[] x     = cplex.getValues(var[0]);
            double[] dj    = cplex.getReducedCosts(var[0]);
            double[] pi    = cplex.getDuals(rng[0]);
            double[] slack = cplex.getSlacks(rng[0]);

            cplex.output().println("Solution status = " + cplex.getStatus());
            cplex.output().println("Solution value  = " + cplex.getObjValue());

            int nvars = x.length;
            for (int j = 0; j < nvars; ++j) {
               cplex.output().println("Variable " + j +
                                      ": Value = " + x[j] +
                                      " Reduced cost = " + dj[j]);
            }

            int ncons = slack.length;
            for (int i = 0; i < ncons; ++i) {
               cplex.output().println("Constraint " + i +
                                     ": Slack = " + slack[i] +
                                     " Pi = " + pi[i]);
            }
         }
         cplex.end();
      }
      catch (IloException e) {
         System.err.println("Concert exception '" + e + "' caught");
      }
    }

   
    
    
    
    
    
    static void populateByColumn(IloMPModeler model,
                                IloNumVar[][] var,
                                IloRange[][] rng) throws IloException {
      IloObjective obj = model.addMaximize();

      rng[0] = new IloRange[4];
      rng[0][0] = model.addRange(2, Double.MAX_VALUE, "c1");
      rng[0][1] = model.addRange(1,Double.MAX_VALUE, "c2");
      rng[0][2] = model.addRange(4,Double.MAX_VALUE, "c3");
      rng[0][3] = model.addRange(-7,Double.MAX_VALUE, "c4");

      IloRange r0 = rng[0][0];
      IloRange r1 = rng[0][1];
      IloRange r2 = rng[0][2];
      IloRange r3 = rng[0][3];

      var[0] = new IloNumVar[2];
      var[0][0] = model.numVar(model.column(obj,  2.0).and(
                               model.column(r0,   1.0).and(
                               model.column(r1,   0.0).and(
                               model.column(r2,   1.0).and(
                               model.column(r3,  -1.0))))),
                               0.0, Double.MAX_VALUE, "x1");
      var[0][1] = model.numVar(model.column(obj,  3.0).and(
                               model.column(r0,   0.0).and(
                               model.column(r1,   1.0).and(
                               model.column(r2,   1.0).and(
                               model.column(r3,  -1.0))))),
                               0.0, Double.MAX_VALUE, "x2");
   }*/
