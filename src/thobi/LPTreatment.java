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
	static int[] xBoundsTumor;
	static int[] yBoundsTumor;
	static int[] zBoundsTumor;
	static int[] dim;
	
	static boolean[] seedUsed = new boolean[Config.numberOfSeeds];
	
	public LPTreatment(Voxel[][][] body, Seed[] seed, int[] x, int[] y, int[] z, int[] dim)
	{
		LPTreatment.body = body;
		LPTreatment.seed = seed;
		LPTreatment.xBoundsTumor = x;
		LPTreatment.yBoundsTumor = y;
		LPTreatment.zBoundsTumor = z;
		LPTreatment.dim = dim;
	}
	
	public static Seed[] getSeed()
	{
		return seed;
	}
	
	public static Voxel[][][] getBody()
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
							cplex.addGe(cplex.sum(dosepart), Config.normalGoalDose-10);
							//cplex.addLe(cplex.sum(dosepart), Config.tumorGoalDose*2);
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
	
	public static void solveLPMin() throws IloException
	{
		
		IloCplex cplex = new IloCplex();
		
		IloNumExpr dosepart[] = new IloNumExpr[Config.numberOfSeeds];
		
		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			dosepart[i] = cplex.constant(0);
		}
		
		
		//variables
		System.out.println("Setting up time variables...");
		System.out.println("Number of Seeds = " + Config.numberOfSeeds);
		IloNumVar[] time = new IloNumVar[Config.numberOfSeeds];
		
		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			time[i] = cplex.numVar(0, Double.MAX_VALUE, "t" + i);
		}
		
		//expressions
		IloLinearNumExpr objective = cplex.linearNumExpr();
		
		//iterate over world
		System.out.println("Adding constraints...");
		for(int x = xBoundsTumor[0]; x < xBoundsTumor[1]; x++)
		{
			for(int y = yBoundsTumor[0]; y < yBoundsTumor[1]; y++)
			{
				for(int z = zBoundsTumor[0]; z < zBoundsTumor[1]; z++)
				{
					//check state of world
					
					if(body[x][y][z].getBodyType() == Config.tumorType)
					{
						for(int i = 0; i < Config.numberOfSeeds; i++)
						{
							if(seedUsed[i])
							{
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
							}
						}
						cplex.addGe(cplex.sum(dosepart), body[x][y][z].getGoalDosis());
					}
					else
					{
						for(int i = 0; i < Config.numberOfSeeds; i++)
						{
							if(seedUsed[i])
							{
								if(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1) > 0)
								{
									objective.addTerm(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);							
								}		
							}
						}
					}
				}
			}
		}
		
		cplex.addMinimize(objective);
		System.out.println("Solving...");
		if(cplex.solve())
		{
			System.out.println("solved");
			for(int i = 0; i < Config.numberOfSeeds; i++)
			{
				if(seedUsed[i])
				{
					seed[i].setDurationMilliSec(cplex.getValue(time[i]));
				}
				else
				{
					seed[i].setDurationMilliSec(0);
				}
			}
			
			double dose_eval = 0;
			double time_eval = 0;
			
			for(int x = 0; x < dim[0]; x++)
			{
				for(int y = 0; y < dim[1]; y++)
				{
					for(int z = 0; z < dim[2]; z++)
					{
						for(int i = 0; i < Config.numberOfSeeds; i++)
						{
							time_eval = seed[i].getDurationMilliSec();
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
		
		int useCounter = 0;
		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			
			if(LPTreatment.getSeed()[i].getDurationMilliSec() == 0)
			{
				seedUsed[i] = false;
			}
			else
			{
				useCounter++;
			}
		}
		
		System.out.println(useCounter + " used");
		
		System.out.println("Done!");
	}
	
	public static void solveLPMax() throws IloException
	{
		
		
		IloCplex cplex = new IloCplex();
		
		IloNumExpr dosepart[] = new IloNumExpr[Config.numberOfSeeds];
		
		

		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			dosepart[i] = cplex.constant(0);
		}
		
		
		
		//variables
		System.out.println("Setting up time variables...");
		System.out.println("Number of Seeds = " + Config.numberOfSeeds);
		IloNumVar[] time = new IloNumVar[Config.numberOfSeeds];
		
		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			time[i] = cplex.numVar(0, Double.MAX_VALUE, "t" + i);
		}
		
		//expressions
		IloLinearNumExpr objective = cplex.linearNumExpr();
		int tumor = 0;
		System.out.println("Adding constraints...");
		for(int x = xBoundsTumor[0]; x < xBoundsTumor[1]; x++)
		{
			for(int y = yBoundsTumor[0]; y < yBoundsTumor[1]; y++)
			{
				for(int z = zBoundsTumor[0]; z < zBoundsTumor[1]; z++)
				{
					//check state of world
					
					if(body[x][y][z].getBodyType() == Config.tumorType)
					{
						for(int i = 0; i < Config.numberOfSeeds; i++)
						{
							if(seedUsed[i])
							{
								objective.addTerm(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);							
							}
						}
					}
					else
					{
						for(int i = 0; i < Config.numberOfSeeds; i++)
						{
							if(seedUsed[i])
							{
								dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
							}
						}
						cplex.addLe(cplex.sum(dosepart), body[x][y][z].getRelaxedGoalDosis());
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
				if(seedUsed[i])
				{
					seed[i].setDurationMilliSec(cplex.getValue(time[i]));
				}
				else
				{
					seed[i].setDurationMilliSec(0);
				}
			}
			
			double dose_eval = 0;
			double time_eval = 0;
			
			for(int x = 0; x < dim[0]; x++)
			{
				for(int y = 0; y < dim[1]; y++)
				{
					for(int z = 0; z < dim[2]; z++)
					{
						for(int i = 0; i < Config.numberOfSeeds; i++)
						{
							time_eval = seed[i].getDurationMilliSec();
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
		System.out.println("tumor: " +tumor);
		int useCounter = 0;
		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			
			if(LPTreatment.getSeed()[i].getDurationMilliSec() == 0)
			{
				seedUsed[i] = false;
			}
			else
			{
				useCounter++;
			}
		}
		
		System.out.println(useCounter + " used");
		System.out.println("Done!");
		
	}
	
	public static void solveLPIT() throws IloException
	{
		for(int i = 0; i < Config.numberOfSeeds; i++)
		{
			seedUsed[i] = true;
		}
		
		solveLPMin();
		//solveLPMax();
	}

}
