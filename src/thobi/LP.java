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
						body[i][j][l].setBodyType(Config.tumorType);
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
						
						switch(body[i][j][l].getBodyType())
						{
							case Config.normalType:
							{
								for(int k=0; k < Config.numberOfSeeds; k++)
								{
									dosepart[k] = cplex.prod(seed[k].radiationIntensity(body[i][j][l].getCoordinate(),1), time[k]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.normalGoalDose);
								break;
							}
							case Config.spineType:
							{
								for(int k=0; k < Config.numberOfSeeds; k++)
								{
									dosepart[k] = cplex.prod(seed[k].radiationIntensity(body[i][j][l].getCoordinate(),1), time[k]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.spineGoalDose);
								break;
							}
							case Config.liverType:
							{
								for(int k=0; k < Config.numberOfSeeds; k++)
								{
									dosepart[k] = cplex.prod(seed[k].radiationIntensity(body[i][j][l].getCoordinate(),1), time[k]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.liverGoalDose);
								break;
							}
							case Config.pancreasType:
							{
								for(int k=0; k < Config.numberOfSeeds; k++)
								{
									dosepart[k] = cplex.prod(seed[k].radiationIntensity(body[i][j][l].getCoordinate(),1), time[k]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.pancreasGoalDose);
								break;
							}
							case Config.tumorType:
							{
								for(int k=0; k < Config.numberOfSeeds; k++)
								{
									dosepart[k] = cplex.prod(seed[k].radiationIntensity(body[i][j][l].getCoordinate(),1), time[k]);
									objective.addTerm(seed[k].radiationIntensity(body[i][j][l].getCoordinate(),1), time[k]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.tumorGoalDose);
								break;
							}
							default:
							{
								for(int k=0; k < Config.numberOfSeeds; k++)
								{
									dosepart[k] = cplex.prod(seed[k].radiationIntensity(body[i][j][l].getCoordinate(),1), time[k]);
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
								dose_eval += seed[l].radiationIntensity(body[i][j][k].getCoordinate(),1)*time_eval;
							}
						}
						
						body[i][j][k].setCurrentDosis(dose_eval);
						System.out.println("dose:" + dose_eval);
						dose_eval = 0;
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
