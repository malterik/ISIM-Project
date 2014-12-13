package thobi;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import utils.Config;
import utils.Seed;
import utils.Voxel;

public class LPTreatment {
	
	static Voxel[][][] body;
	static Seed[] seed;
	
	public LPTreatment(Voxel[][][] body, Seed[] seed)
	{
		LPTreatment.body = body;
		LPTreatment.seed = seed;
	}
	
	public static Seed[] getSeed()
	{
		return seed;
	}
	
	public Voxel[][][] getBody()
	{
		return body;
	}
	
	public static void solveLP() throws IloException
	{
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
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
								
							}
							cplex.addLe(cplex.sum(dosepart), Config.normalGoalDose);
							break;
						}
						case Config.spineType:
						{
							for(int i = 0; i < Config.numberOfSeeds; i++)
							{
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
							}
							cplex.addLe(cplex.sum(dosepart), Config.spineGoalDose);
							break;
						}
						case Config.liverType:
						{
							for(int i = 0; i < Config.numberOfSeeds; i++)
							{
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
							}
							cplex.addLe(cplex.sum(dosepart), Config.liverGoalDose);
							break;
						}
						case Config.pancreasType:
						{
							for(int i = 0; i < Config.numberOfSeeds; i++)
							{
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
							}
							cplex.addLe(cplex.sum(dosepart), Config.pancreasGoalDose);
							break;
						}
						case Config.tumorType:
						{
							for(int i = 0; i < Config.numberOfSeeds; i++)
							{
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
								objective.addTerm(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
							}
							cplex.addLe(cplex.sum(dosepart), Config.tumorGoalDose);
							break;
						}
						default:
						{
							for(int i = 0; i < Config.numberOfSeeds; i++)
							{
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
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
				seed[i].setDurationMilliSec(cplex.getValue(time[i]));
				
			}
			
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
								dose_eval += seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1)*time_eval;
							}
						}	
						body[x][y][z].setCurrentDosis(dose_eval);
						dose_eval = 0;
					}
				}
			}
		}
		else
		{
			System.out.println("not solved");
		}
		
		System.out.println("Computing dose.....");
		
		System.out.println("Done!");
	}

}
