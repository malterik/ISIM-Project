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
		LPTreatment();
	}
	
	//TODO use MATLAB body
	public static void LPTreatment() throws IloException
	{
			
			
			Voxel[][][] body = new Voxel[Config.xDIM][Config.yDIM][Config.zDIM];
			
			System.out.println("Setting up body...");
			for(int x = 0; x < Config.xDIM; x++)
			{
				for(int y = 0; y < Config.yDIM; y++)
				{
					for(int z = 0; z < Config.zDIM; z++)
					{
						body[x][y][z] = new Voxel(x, y, z);
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
			for(int x = xmin; x <= xmax; x++)
			{
				for(int y = ymin; y <= ymax; y++)
				{
					for(int z = zmin; z <= zmax; z++)
					{
						body[x][y][z].setBodyType(Config.tumorType);
					}
				}
			}
			
			
					
			
			//positions
			System.out.println("Setting up seeds...");
			
			Seed[] seed = new Seed[Config.numberOfSeeds];
			
			for(int i = 0; i < Config.numberOfSeeds; i++)
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
			for(int x = 0; x < Config.xDIM; x++)
			{
				for(int y = 0; y < Config.yDIM; y++)
				{
					for(int z = 0; z < Config.zDIM; z++)
					{
						//check state of world
						
						switch(body[x][y][z].getBodyType())
						{
							case Config.normalType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].doseFunction(body[x][y][z].getCoordinate()), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.normalGoalDose);
								break;
							}
							case Config.spineType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].doseFunction(body[x][y][z].getCoordinate()), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.spineGoalDose);
								break;
							}
							case Config.liverType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].doseFunction(body[x][y][z].getCoordinate()), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.liverGoalDose);
								break;
							}
							case Config.pancreasType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].doseFunction(body[x][y][z].getCoordinate()), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.pancreasGoalDose);
								break;
							}
							case Config.tumorType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].doseFunction(body[x][y][z].getCoordinate()), time[i]);
									objective.addTerm(seed[i].doseFunction(body[x][y][z].getCoordinate()), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.tumorGoalDose);
								break;
							}
							default:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].doseFunction(body[x][y][z].getCoordinate()), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.normalGoalDose);
								break;
							}
						}						
					}
				}
			}
			
			cplex.addMaximize(objective);
			System.out.println("Solving...");
			if(cplex.solve())
			{
				System.out.println("solved");
				for(int i = 0; i < Config.numberOfSeeds; i++)
				{
					System.out.println("x" + i + " = " + seed[i].getX());
					System.out.println("y" + i + " = " + seed[i].getY());
					System.out.println("y" + i + " = " + seed[i].getZ());
					System.out.println("time" + i + " = " + cplex.getValue(time[i]));
				}
			}
			else
			{
				System.out.println("not solved");
			}
			
			System.out.println("Computing dose.....");
			
			double dose_eval = 0;
			double time_eval = 0;
			
			for(int x = 0; x < Config.xDIM; x++)
			{
				for(int y = 0; y < Config.yDIM; y++)
				{
					for(int z = 0; z < Config.zDIM; z++)
					{
						for(int i = 0; i < Config.numberOfSeeds; i++)
						{
							time_eval = cplex.getValue(time[i]);
							if(time_eval != 0)
							{
								dose_eval += seed[i].doseFunction(body[x][y][z].getCoordinate())*time_eval;
							}
						}
						
						body[x][y][z].setCurrentDosis(dose_eval);
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
