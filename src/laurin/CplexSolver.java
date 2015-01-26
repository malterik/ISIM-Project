package laurin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import utils.Config;
import utils.Seed;
import utils.Voxel;

public class CplexSolver {
	
	private Voxel[][][] body;
	private Seed[] seeds;
	private int xSize;
	private int ySize;
	private int zSize;
	double[] dwellTimes;
	
	IloCplex cplex;
	
	IloLinearNumExpr c_all_objective; // objective for dwell times
	IloLinearNumExpr[] c_s_lower_voi_objectives; // obj. single lower slacks
	IloLinearNumExpr[] c_s_upper_voi_objectives; // obj. single upper slacks
	IloLinearNumExpr[] c_t_lower_voi_objectives; // obj. PTV lower slacks
	IloLinearNumExpr[] c_t_upper_voi_objectives; // obj. PTV upper slacks
	
	IloNumVar[] t; // dwell times
	IloNumVar[] s_lower; // single lower slacks
	IloNumVar[] s_upper; // single upper slack
	IloNumVar[] t_lower; // lower slacks for body parts
	IloNumVar[] t_upper; // upper slacks for body parts
	
	IloRange[][][] s_lower_ranges; // single lower slack range 
	IloRange[][][] s_upper_ranges; // single upper slack range
	IloRange[] t_lower_ranges; // lower slack range per body part
	IloRange[] t_upper_ranges; // upper slack range per body part
	IloRange[][][] voi_lower_ranges; // lower dose ranges
	IloRange[][][] voi_upper_ranges; // upper dose ranges

	public CplexSolver(Voxel[][][] body, Seed[] seeds) throws IloException, IOException
	{
		this.body = body;
		this.seeds = seeds;
		this.xSize = body.length;
		this.ySize = body[0].length;
		this.zSize = body[0][0].length;
		initialize();
	}
	
	private void initialize() throws IloException, IOException
	{
		// create cplex instance
		cplex = new IloCplex();
		createVariables();
		createObjectives();
	}	
	
	public Seed[] getCurrentSeeds()
	{
		Set<Seed> seedSet = new HashSet<Seed>();
		for (int i = 0; i < dwellTimes.length; i++)
		{
			if (dwellTimes[i] != 0.0)
			{
				seeds[i].setDurationMilliSec(dwellTimes[i]);
				seedSet.add(seeds[i]);
			}
		}
		return (seedSet.toArray(new Seed[seedSet.size()]));
	}
	
	private void createVariables() throws IloException
	{
		// create variables
		ArrayList<IloNumVar> s_lower_list = new ArrayList<IloNumVar>();
		ArrayList<IloNumVar> s_upper_list = new ArrayList<IloNumVar>();
		
		t = new IloNumVar[seeds.length];
		t_lower = new IloNumVar[seeds.length];
		t_upper = new IloNumVar[seeds.length];
		
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					s_lower_list.add(cplex.numVar(0.0, Double.MAX_VALUE, "sLower[" + x + "," + y + "," + z + "]"));
					s_upper_list.add(cplex.numVar(0.0, Double.MAX_VALUE, "sUpper[" + x + "," + y + "," + z + "]"));
				}
			}
		}
		
		s_lower = s_lower_list.toArray(new IloNumVar[s_lower_list.size()]);
		s_upper = s_upper_list.toArray(new IloNumVar[s_upper_list.size()]);
		
		// initialize and constraint variables
		for(int i = 0; i < seeds.length; i++)
		{
			t[i] = cplex.numVar(0.0, Double.MAX_VALUE, "t" + i);
			t_lower[i] = cplex.numVar(0.0, Double.MAX_VALUE, "tLower" + i);
			t_upper[i] = cplex.numVar(0.0, Double.MAX_VALUE, "TUpper" + i);
		}
	}
	
	private void createObjectives() throws IloException
	{
		// create objectives
		c_all_objective = cplex.linearNumExpr();	
		c_s_lower_voi_objectives = new IloLinearNumExpr[Config.tumorType];
		c_s_upper_voi_objectives = new IloLinearNumExpr[Config.tumorType];
		c_t_lower_voi_objectives = new IloLinearNumExpr[Config.tumorType];
		c_t_upper_voi_objectives = new IloLinearNumExpr[Config.tumorType];
		
		
		// initialize objectives
		for (int i = 0; i < c_s_lower_voi_objectives.length; i++)
		{
			c_s_lower_voi_objectives[i] = cplex.linearNumExpr();
			c_s_upper_voi_objectives[i] = cplex.linearNumExpr();
			c_t_lower_voi_objectives[i] = cplex.linearNumExpr();
			c_t_upper_voi_objectives[i] = cplex.linearNumExpr();
		}
		
		for (int j = 0; j < seeds.length; j++)
		{	
			c_all_objective.addTerm(1.0, t[j]);	
		}
		
		// create ranges for constraints
		t_lower_ranges = new IloRange[Config.tumorType];
		t_upper_ranges = new IloRange[Config.tumorType];
		s_lower_ranges = new IloRange[body.length][body[0].length][body[0][0].length];
		s_upper_ranges = new IloRange[body.length][body[0].length][body[0][0].length];
		voi_lower_ranges = new IloRange[body.length][body[0].length][body[0][0].length];
		voi_upper_ranges = new IloRange[body.length][body[0].length][body[0][0].length];
		
		
		// fill objectives
		int counter = 0;
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					
					IloNumExpr t_doses[] = new IloNumExpr[seeds.length];					
					int bodyTypeIndex = body[x][y][z].getBodyType() - 1;
					
					for(int j = 0; j < seeds.length; j++)
					{
						double intensity = body[x][y][z].radiationIntensity(seeds[j].distanceToVoxel(body[x][y][z].getCoordinate()), 1);
						t_doses[j] = cplex.prod(intensity, t[j]);
					}
					
					c_s_lower_voi_objectives[bodyTypeIndex].addTerm(1.0, s_lower[counter]);
					c_s_upper_voi_objectives[bodyTypeIndex].addTerm(1.0, s_upper[counter]);

							
					// constraints		
					IloNumExpr t_lower_constraint = cplex.sum(cplex.sum(cplex.sum(t_doses),s_lower[counter]), t_lower[bodyTypeIndex]);
					IloNumExpr t_upper_constraint = cplex.sum(cplex.sum(cplex.sum(t_doses),cplex.negative(s_upper[counter])), cplex.negative(t_upper[bodyTypeIndex]));
					
					// determine lower and upper bound from body type
					double b_voi_lower = 0.0;
					double b_voi_upper = 0.0;
					switch (body[x][y][z].getBodyType())
					{
						case Config.tumorType: 
							b_voi_lower = Config.tumorMinDose;
							b_voi_upper = Config.tumorMaxDose;
							break;
						case Config.normalType: 
							b_voi_lower = Config.normalMinDose;;
							b_voi_upper = Config.normalMaxDose;
							break;
						case Config.spineType: 
							b_voi_lower = Config.spineMinDose;
							b_voi_upper = Config.spineMaxDose;
							break;
						case Config.liverType: 
							b_voi_lower = Config.liverMinDose;
							b_voi_upper = Config.liverMaxDose;
							break;
						case Config.pancreasType: 
							b_voi_lower = Config.pancreasMinDose;
							b_voi_upper = Config.pancreasMaxDose;
							break;
					}
					
					// set lower bounds for voxel
					voi_lower_ranges[x][y][z] = cplex.addGe(t_lower_constraint, b_voi_lower);
					voi_upper_ranges[x][y][z] = cplex.addLe(t_upper_constraint, b_voi_upper);			
					
					// initialize slacks with zero
					s_lower_ranges[x][y][z] = cplex.addLe(s_lower[counter], 0);
					s_upper_ranges[x][y][z] = cplex.addLe(s_upper[counter], 0);
					
					counter++;
				}
			}
		}
		
		for (int i = 0; i < Config.tumorType; i++)
		{
			c_t_lower_voi_objectives[i].addTerm(1.0, t_lower[i]);
			c_t_upper_voi_objectives[i].addTerm(1.0, t_upper[i]);
			
			// initialize slacks with zero
			t_lower_ranges[i] = cplex.addLe(t_lower[i], 0);
			t_upper_ranges[i] = cplex.addLe(t_upper[i], 0);
		}

	}
	
	public void initialSolution () throws IloException
	{	
		// set PTV-slack variable for lower bound to lower bound
		// -> b_t_PTV_lower = b_PTV_lower
		t_lower_ranges[Config.tumorType-1].setUB(Config.tumorMinDose);
		
		// minimize PTV-slack variable
		IloObjective objective = cplex.addMinimize(c_t_lower_voi_objectives[Config.tumorType-1]);
		
		cplex.exportModel("step_is.lp");
		if(cplex.solve())
		{	
			// Slack can't be minimized to zero -> lower bound infeasible
			// Set new lower bound to maximum possible lower bound
			// -> b_lower_PTV = b_lower_PTV - g
			if (cplex.getObjValue() > 0)
			{				
				for (int x = 0; x < xSize; x++) {
					for (int y = 0; y < ySize; y++) {
						for (int z = 0; z < zSize; z++) {
							if (body[x][y][z].getBodyType() == Config.tumorType)
							{
								voi_lower_ranges[x][y][z].setLB(voi_lower_ranges[x][y][z].getLB() - cplex.getObjValue());
							}
						}
					}
				}
			}
			else
			{
				// lower bounds are feasible -> nothing to do here
			}
			
			// get dwell times
			dwellTimes = cplex.getValues(t);
		}
		
		// remove objectives
		cplex.remove(objective);

		// set slack variable back to zero
		t_lower_ranges[Config.tumorType-1].setUB(0);
		
    	return;
	}
	
	/*
	 * Optimizes PTV homogenity by minimizing difference between maximum and minimum value in PTV
	 */
	public void optimizePTVHomogeinity() throws IloException
	{
		// PRIOR TO SOLVING
		// Raise PTV lower bound to upper bound and set according slack variables to difference
		// between PTV upper and lower bound.
		// -> b_s_PTV_lower = b_PTV_upper - b_PTV_lower
		// -> b_PTV_lower = b_PTV_upper 
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					if (body[x][y][z].getBodyType() == Config.tumorType)
					{
						// set slack to difference between upper and lower bound
						double difference = voi_upper_ranges[x][y][z].getUB() - voi_lower_ranges[x][y][z].getLB();
						s_lower_ranges[x][y][z].setUB(difference);
						
						// set lower bound to upper bound
						voi_lower_ranges[x][y][z].setLB(voi_upper_ranges[x][y][z].getUB());
					}
				}
			}
		}
		
		// Set objective to minimize PTV lower slack variables
		// -> s_PTV_lower -> min 
		IloObjective objective = cplex.addMinimize(c_s_lower_voi_objectives[Config.tumorType-1]);

		cplex.exportModel("step_hi.lp");
		if(cplex.solve())
		{	
			System.out.println("Obj. value: " + cplex.getObjValue());
			
			// get values from cplex
			dwellTimes = cplex.getValues(t);
			double[] slackValues = cplex.getValues(s_lower);
			
			// set new PTV lower bounds (b_PTV_lower) to difference between lower bounds and slack values 
			// -> highest lower bound possible 
			// -> b_PTV_lower = b_PTV_lower - b_s_PTV_lower
			int counter = 0;
			for (int x = 0; x < xSize; x++) {
				for (int y = 0; y < ySize; y++) {
					for (int z = 0; z < zSize; z++) {				
						if (body[x][y][z].getBodyType() == Config.tumorType)
						{
							// set lower bound to difference between PTV lower bound and slack
							double difference = voi_lower_ranges[x][y][z].getLB() - slackValues[counter];
							voi_lower_ranges[x][y][z].setLB(difference);
							
							// set slack bound back to zero
							s_lower_ranges[x][y][z].setUB(0);
						}
						counter++;
					}
				}
			}
		}
		/* remove objective */
		cplex.remove(objective);
	}
	
	public void optimizeCoverage() throws IloException
	{
		// PRIOR TO SOLVING
		// Raise PTV lower bound to PTV goal dose and set according slack variables to difference
		// between PTV upper and lower bound.
		// -> b_s_PTV_lower = PTV_goalDose - b_PTV_lower
		// -> b_PTV_lower = PTV_goalDose
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					if (body[x][y][z].getBodyType() == Config.tumorType)
					{
						// set difference to PTV_goalDose and PTV lower bound
						double difference = Config.tumorGoalDose - voi_lower_ranges[x][y][z].getLB();
						if (difference > 0)
						{
							s_lower_ranges[x][y][z].setUB(difference);
						
							// set PTV lower bound to goal dose
							voi_lower_ranges[x][y][z].setLB(Config.tumorGoalDose);
						}
					}
				}
			}
		}
		// Set objective to minimize single lower slack variables for PTV
		// -> s_PTV_lower -> min 
		IloObjective objective = cplex.addMinimize(c_s_lower_voi_objectives[Config.tumorType-1]);
	
		cplex.exportModel("step_co.lp");
		if(cplex.solve())
		{	
			System.out.println("CO Obj. value: " + cplex.getObjValue());
			
			// set new lower ptv bound to old lower bound minus slack value
			dwellTimes = cplex.getValues(t);
			double[] slackValues = cplex.getValues(s_lower);
					
			// set new PTV lower bounds (b_PTV_lower) to difference between lower bounds and slack values 
			// -> highest lower bound possible 
			// -> b_PTV_lower = b_PTV_lower - b_s_PTV_lower
			int counter = 0;
			for (int x = 0; x < xSize; x++) {
				for (int y = 0; y < ySize; y++) {
					for (int z = 0; z < zSize; z++) {	
						if (body[x][y][z].getBodyType() == Config.tumorType)
						{
							// set lower bound to difference between PTV lower bound and slack
							double difference = voi_lower_ranges[x][y][z].getLB() - slackValues[counter];
							voi_lower_ranges[x][y][z].setLB(difference);
							
							// set slack bound back to zero
							s_lower_ranges[x][y][z].setUB(0);
						}
						counter++;
					}
				}
			}
		}
		cplex.remove(objective);
    	return;
	}
	
}
