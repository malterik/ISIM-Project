package thobi;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import utils.Config;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;

public class LP {	
	
	public static void main(String[] args) throws IloException
	{
		LPcalc();
	}
	
	//TODO use MATLAB body
	public static void LPcalc() throws IloException
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
			
			int xmin = 45;
			int xmax = 55;
			int ymin = 45;
			int ymax = 55;
			int zmin = 45;
			int zmax = 55;			
			
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
				seed[i] = new Seed(	RandGenerator.randDouble(xmin,xmax),
									RandGenerator.randDouble(ymin,ymax),
									RandGenerator.randDouble(zmin,zmax),0);
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
									dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
									
								}
								cplex.addLe(cplex.sum(dosepart), Config.normalGoalDose);
								break;
							}
							case Config.bladderType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.bladderGoalDose);
								break;
							}
							case Config.rectumType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.rectumGoalDose);
								break;
							}
							case Config.urethraType:
							{
								for(int i = 0; i < Config.numberOfSeeds; i++)
								{
									dosepart[i] = cplex.prod(seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1), time[i]);
								}
								cplex.addLe(cplex.sum(dosepart), Config.urethraGoalDose);
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
								dose_eval += seed[i].radiationIntensity(body[x][y][z].getCoordinate(),1)*time_eval;
							}
						}
						
						body[x][y][z].setCurrentDosis(dose_eval);
						dose_eval = 0;
					}
				}
			}
			
			System.out.println("Done!");

			
			

	}
}
