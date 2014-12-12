package thobi;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.Random;

import utils.Config;
import utils.Seed;
import utils.Voxel;

public class LP {
	
	public static void main(String[] args) throws IloException
	{
		model3D();
	}
	
	//TODO use MATLAB body
	//TODO use right dose function
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
			
			Seed[] seed = new Seed[Config.numberOfSeeds];
			
			for(int i=0; i < Config.numberOfSeeds; i++)
			{				
				seed[i] = new Seed(randDouble(xmin,xmax),randDouble(ymin,ymax),randDouble(zmin,zmax),0);
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
								objective.addTerm(seed[k].doseFunction(body[i][j][l].getCoordinate()), time[k]);
							}
							
						}
						else
						{
							//iterate over seeds
							for(int k=0; k < Config.numberOfSeeds; k++)
							{
								dosepart[k] = cplex.prod(seed[k].doseFunction(body[i][j][l].getCoordinate()), time[k]);
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
					System.out.println("x" + k + " = " + seed[k].getX());
					System.out.println("y" + k + " = " + seed[k].getY());
					System.out.println("y" + k + " = " + seed[k].getZ());
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
								dose_eval += seed[l].doseFunction(body[i][j][k].getCoordinate());
							}
						}
						
						body[i][j][k].setCurrentDosis(dose_eval);
					}
				}
			}
			
			System.out.println("Done!");

			
			

	}
	
	public static double randDouble(double min, double max)
	{
		Random r = new Random();
		double randomValue = min + (max - min) * r.nextDouble();
		return randomValue;
	}
}
