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
			
			System.out.println("Setting up body...");
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
			
			System.out.println("Setting up target...");
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
			System.out.println("Setting up seeds...");
			double seed[][] = new double[Config.numberOfSeeds][3];
			
			
			
			for(int i=0; i < Config.numberOfSeeds; i++)
			{
				seed[i][0] = randDouble(xmin,xmax);
				seed[i][1] = randDouble(ymin,ymax);
				seed[i][2] = randDouble(zmin,zmax);
			}
			
			
			
			IloNumExpr dosepart[] = new IloNumExpr[Config.numberOfSeeds];
			
			IloCplex cplex = new IloCplex();
			
			
			//variables
			System.out.println("Setting up time variables...");
			IloNumVar[] time = new IloNumVar[Config.numberOfSeeds];
			
			for(int i = 0; i < Config.numberOfSeeds; i++)
			{
				time[i] = cplex.numVar(0, Double.MAX_VALUE, "t" + i);
			}
			
			//expressions
			IloLinearNumExpr objective = cplex.linearNumExpr();
			
			//iterate over world
			System.out.println("Adding constraints...");
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
								objective.addTerm(dose3D(i, j, l, seed[k][0], seed[k][1], seed[k][2]), time[k]);
							}
							
						}
						else
						{
							//iterate over seeds
							for(int k=0; k < Config.numberOfSeeds; k++)
							{
								dosepart[k] = cplex.prod(dose3D(i, j, k, seed[k][0], seed[k][1], seed[k][2]), time[k]);
							}
							cplex.addLe(cplex.sum(dosepart), Config.normalDose);
						}
					}
				}
			}
			
			cplex.addMaximize(objective);
			System.out.println("Solving...");
			if(cplex.solve())
			{
				System.out.println("solved");
				for(int k=0; k < Config.numberOfSeeds; k++)
				{
					System.out.println("x" + k + " = " + seed[k][0]);
					System.out.println("y" + k + " = " + seed[k][1]);
					System.out.println("y" + k + " = " + seed[k][2]);
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
								dose_eval += dose3D(i,j,k,seed[l][0],seed[l][1],seed[l][2])*time_eval;
							}
						}
						
						body[i][j][k].setCurrentDosis(dose_eval);
					}
				}
			}
			
			System.out.println("Done!");

			
			

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
