package laurin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import laurin.LPStepwiseGUI.DoseGoalType;

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
	
	public static enum SlackType {PER_VOI, PER_VOXEL};
	public static enum GoalType {MAXIMIZE, MINIMIZE};
	public static enum BoundType {LOWER_BOUND, UPPER_BOUND};
	
	private Voxel[][] voxels;
	private Seed[] seeds;
	double[] dwellTimes;
	
	private IloCplex cplex;
	
	private IloLinearNumExpr c_all_objective; // objective for dwell times
	private IloLinearNumExpr[] c_s_lower_voi_objectives; // obj. single lower slacks
	private IloLinearNumExpr[] c_s_upper_voi_objectives; // obj. single upper slacks
	private IloLinearNumExpr[] c_t_lower_voi_objectives; // obj. PTV lower slacks
	private IloLinearNumExpr[] c_t_upper_voi_objectives; // obj. PTV upper slacks
	
	private IloNumVar[] t; // dwell times
	private IloNumVar[][] s_lower; // single lower slacks
	private IloNumVar[][] s_upper; // single upper slack
	private IloNumVar[] t_lower; // lower slacks for body parts
	private IloNumVar[] t_upper; // upper slacks for body parts
	
	private IloRange[][] s_lower_ranges; // single lower slack range 
	private IloRange[][] s_upper_ranges; // single upper slack range
	private IloRange[] t_lower_ranges; // lower slack range per body part
	private IloRange[] t_upper_ranges; // upper slack range per body part
	private IloRange[][] voi_lower_ranges; // lower dose ranges
	private IloRange[][] voi_upper_ranges; // upper dose ranges


	public CplexSolver(Voxel[][] voxels, Seed[] seeds) throws IloException, IOException
	{
		this.voxels = voxels;
		this.seeds = seeds;
		initialize();
	}
	
	public void exportModel(String fileName) throws IloException
	{
		cplex.exportModel(fileName);
	}
	
	public double[] getBounds(int bodyType, BoundType boundType) throws IloException
	{
		int bodyTypeIndex = bodyType-1;
		double min = Double.MAX_VALUE;
		double max = 0.0;
		
		if (boundType == BoundType.LOWER_BOUND)
		{
			for (int j = 0; j < voxels[bodyTypeIndex].length; j++)
			{
				if (voi_lower_ranges[bodyTypeIndex][j].getLB() < min)
					min = voi_lower_ranges[bodyTypeIndex][j].getLB();
				if(voi_lower_ranges[bodyTypeIndex][j].getLB() > max)
					max = voi_lower_ranges[bodyTypeIndex][j].getLB();
			}
		}
		
		if (boundType == BoundType.UPPER_BOUND)
		{
			for (int j = 0; j < voxels[bodyTypeIndex].length; j++)
			{
				if (voi_upper_ranges[bodyTypeIndex][j].getUB() < min)
					min = voi_upper_ranges[bodyTypeIndex][j].getUB();
				if(voi_upper_ranges[bodyTypeIndex][j].getUB() > max)
					max = voi_upper_ranges[bodyTypeIndex][j].getUB();
			}
		}
		
		return (new double[]{min,max});
	}
	
	public double[] getUpperBounds() throws IloException
	{
		double[] upperBounds = new double[Config.tumorType];
		for (int i = 0; i < (Config.tumorType); i++)
		{
			upperBounds[i] = getBounds(i+1, BoundType.UPPER_BOUND)[1];
		}
		return upperBounds;
	}
	
	public double[] getLowerBounds() throws IloException
	{
		double[] lowerBounds = new double[Config.tumorType];
		for (int i = 0; i < (Config.tumorType); i++)
		{
			lowerBounds[i] = getBounds(i+1, BoundType.LOWER_BOUND)[0];
		}
		return lowerBounds;
	}
	
	public void setNewBounds(int bodyType, BoundType boundType, double dose) throws IloException
	{	
		int bodyTypeIndex = bodyType-1;
		
		for (int j = 0; j < voxels[bodyTypeIndex].length; j++)
		{
			if (boundType == BoundType.LOWER_BOUND)
				voi_lower_ranges[bodyTypeIndex][j].setLB(dose);
			else if (boundType == BoundType.UPPER_BOUND)
				voi_upper_ranges[bodyTypeIndex][j].setUB(dose);
		}
	}
	
	public void printBounds() throws IloException
	{
		for (int i = 0; i < (Config.tumorType); i++)
		{
				double[] lower = getBounds(i+1, BoundType.LOWER_BOUND);
				double[] upper = getBounds(i+1, BoundType.UPPER_BOUND);
				
				System.out.println(Config.bodyTypeDescriptions[i] + ": [" + lower[0] + "," + lower[1] + "]\t[" + upper[0] + "," + upper[1] + "]");
		}
	}
	
	public void doOptimization(int bodyType, GoalType goalType, SlackType slackType, BoundType boundType, double goalDose) throws IloException
	{
		int bodyTypeIndex = bodyType - 1;
		IloLinearNumExpr expression = null;
		IloRange[] slackRanges = null;
		IloRange[] boundRanges = null;
		IloObjective objective = null;
		
		switch (boundType)
		{
			case LOWER_BOUND:
				boundRanges = voi_lower_ranges[bodyTypeIndex];
				if (slackType == SlackType.PER_VOXEL)
				{
					slackRanges = s_lower_ranges[bodyTypeIndex];
					expression = c_s_lower_voi_objectives[bodyTypeIndex];
				}
					
				else if (slackType == SlackType.PER_VOI)
				{
					slackRanges = new IloRange[]{t_lower_ranges[bodyTypeIndex]};
					expression = c_t_lower_voi_objectives[bodyTypeIndex];
				}
				break;
				
			case UPPER_BOUND:
				boundRanges = voi_upper_ranges[bodyTypeIndex];
				if (slackType == SlackType.PER_VOXEL)
				{
					slackRanges = s_upper_ranges[bodyTypeIndex];
					expression = c_s_upper_voi_objectives[bodyTypeIndex];
				}
				else if (slackType == SlackType.PER_VOI)
				{
					slackRanges = new IloRange[]{t_upper_ranges[bodyTypeIndex]};
					expression = c_t_upper_voi_objectives[bodyTypeIndex];
				}
				break;
		}
		
		
		switch (goalType)
		{
			case MAXIMIZE:
				objective = cplex.addMaximize(expression);
				break;
			
			case MINIMIZE:
				objective = cplex.addMinimize(expression);
				break;
		}		
		
		if (slackType == SlackType.PER_VOXEL)
		{
			for (int j = 0; j < voxels[bodyTypeIndex].length; j++)
			{
				// set difference to PTV_goalDose and PTV lower bound
				double difference = 0.0;
				if (boundType == BoundType.LOWER_BOUND)
				{
					difference = goalDose - boundRanges[j].getLB();
					if (difference > 0)
					{
						slackRanges[j].setUB(difference);
						boundRanges[j].setLB(goalDose);
					}
				}
				else if (boundType == BoundType.UPPER_BOUND)
				{
					difference = boundRanges[j].getUB() - goalDose;
					if (difference > 0)
					{
						slackRanges[j].setUB(difference);
						boundRanges[j].setUB(goalDose);
					}
				}
			}
		}
		else if (slackType == SlackType.PER_VOI)
		{
			if (boundType == BoundType.LOWER_BOUND)
			{
				t_lower_ranges[bodyTypeIndex].setUB(goalDose);
			}
			else if (boundType == BoundType.UPPER_BOUND)
			{
				t_upper_ranges[bodyTypeIndex].setUB(voi_upper_ranges[bodyTypeIndex][0].getUB()-goalDose);
			}
		}
		cplex.exportModel("step.lp");
		if(cplex.solve())
		{				
			// set new lower ptv bound to old lower bound minus slack value
			dwellTimes = cplex.getValues(t);
			
			double[][] s_lower_values = new double[Config.tumorType][];
			double[][] s_upper_values = new double[Config.tumorType][];
			double[] t_lower_values = new double[Config.tumorType];
			double[] t_upper_values = new double[Config.tumorType];
			double obj_value = cplex.getObjValue();
			
			switch (boundType)
			{
				case LOWER_BOUND:
					if (slackType == SlackType.PER_VOXEL)
						s_lower_values[bodyTypeIndex]  = cplex.getValues(s_lower[bodyTypeIndex]);				
					else if (slackType == SlackType.PER_VOI)
						t_lower_values[bodyTypeIndex]  = cplex.getValue(t_lower[bodyTypeIndex]);		
					break;
					
				case UPPER_BOUND:
					if (slackType == SlackType.PER_VOXEL)
						s_upper_values[bodyTypeIndex]  = cplex.getValues(s_upper[bodyTypeIndex]);				
					else if (slackType == SlackType.PER_VOI)
						t_upper_values[bodyTypeIndex]  = cplex.getValue(t_upper[bodyTypeIndex]);		
					break;
			}	
			
			if (slackType == SlackType.PER_VOXEL)
				for (int j = 0; j < voxels[bodyTypeIndex].length; j++)
				{
					double difference = 0.0;
					if (boundType == BoundType.LOWER_BOUND)
					{	
						// set new bound to old bound - slack
						difference = boundRanges[j].getLB() - s_lower_values[bodyTypeIndex][j];
						boundRanges[j].setLB(difference);
						s_lower_ranges[bodyTypeIndex][j].setUB(0);
					}
					else if (boundType == BoundType.UPPER_BOUND)
					{
						// set new bound to goal + slack
						boundRanges[j].setUB(goalDose + s_upper_values[bodyTypeIndex][j]);
						s_lower_ranges[bodyTypeIndex][j].setUB(0);
					}
				}
			else if (slackType == SlackType.PER_VOI)
			{
				if (cplex.getObjValue() > 0)
				{
					if (boundType == BoundType.LOWER_BOUND)
					{	
						for (int j = 0; j < voxels[bodyTypeIndex].length; j++)
						{
							voi_lower_ranges[bodyTypeIndex][j].setLB(voi_lower_ranges[bodyTypeIndex][j].getLB() - obj_value);
						}
						t_lower_ranges[bodyTypeIndex].setUB(0);
					}
					else if (boundType == BoundType.UPPER_BOUND)
					{
						for (int j = 0; j < voxels[bodyTypeIndex].length; j++)
						{
							voi_upper_ranges[bodyTypeIndex][j].setUB(voi_upper_ranges[bodyTypeIndex][j].getLB() + obj_value);
						}
						t_upper_ranges[bodyTypeIndex].setUB(0);
					}
				}
				else 
				{
					// feasible -> nothing to do here
				}

			}
		}
		cplex.remove(objective);	
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
		t = new IloNumVar[seeds.length];
		t_lower = new IloNumVar[Config.tumorType];
		t_upper = new IloNumVar[Config.tumorType];

		s_lower = new IloNumVar[Config.tumorType][];
		s_upper = new IloNumVar[Config.tumorType][];
		
		for (int i = 0; i < Config.tumorType; i++)
		{
			s_lower[i] = new IloNumVar[voxels[i].length];
			s_upper[i] = new IloNumVar[voxels[i].length];
			t_lower[i] = cplex.numVar(0.0, Double.MAX_VALUE, "tLower" + i);
			t_upper[i] = cplex.numVar(0.0, Double.MAX_VALUE, "TUpper" + i);
			for (int j = 0; j < voxels[i].length; j++)
			{
				s_lower[i][j] = cplex.numVar(0.0, Double.MAX_VALUE, "sLower[" + voxels[i][j].getX() + "," + voxels[i][j].getY() + "," + voxels[i][j].getZ() + "]");
				s_upper[i][j] = cplex.numVar(0.0, Double.MAX_VALUE, "sUpper[" + voxels[i][j].getX() + "," + voxels[i][j].getY() + "," + voxels[i][j].getZ() + "]");				
			}
		}

		
		// initialize and constraint variables
		for(int i = 0; i < seeds.length; i++)
		{
			t[i] = cplex.numVar(0.0, Double.MAX_VALUE, "t" + i);
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
		s_lower_ranges = new IloRange[Config.tumorType][];
		s_upper_ranges = new IloRange[Config.tumorType][];
		voi_lower_ranges = new IloRange[Config.tumorType][];
		voi_upper_ranges = new IloRange[Config.tumorType][];
		
		for (int i = 0; i < Config.tumorType; i++)
		{	
			IloRange[] s_lower_range = new IloRange[voxels[i].length];
			IloRange[] s_upper_range = new IloRange[voxels[i].length];
			IloRange[] voi_lower_range = new IloRange[voxels[i].length];
			IloRange[] voi_upper_range = new IloRange[voxels[i].length];
			
			for (int j = 0; j < voxels[i].length; j++)
			{
				IloNumExpr t_doses[] = new IloNumExpr[seeds.length];					
				
				for(int k = 0; k < seeds.length; k++)
				{
					double intensity = voxels[i][j].radiationIntensity(seeds[k].distanceToVoxel(voxels[i][j].getCoordinate()), 1);
					t_doses[k] = cplex.prod(intensity, t[k]);
				}
				
				c_s_lower_voi_objectives[i].addTerm(1.0, s_lower[i][j]);
				c_s_upper_voi_objectives[i].addTerm(1.0, s_upper[i][j]);

						
				// constraints		
				IloNumExpr t_lower_constraint = cplex.sum(cplex.sum(cplex.sum(t_doses),s_lower[i][j]), t_lower[i]);
				IloNumExpr t_upper_constraint = cplex.sum(cplex.sum(cplex.sum(t_doses),cplex.negative(s_upper[i][j])), cplex.negative(t_upper[i]));
				
				// determine lower and upper bound from body type
				double b_voi_lower = 0.0;
				double b_voi_upper = 0.0;
				switch (voxels[i][j].getBodyType())
				{
					case Config.tumorType: 
						b_voi_lower = Config.tumorMinDose;
						b_voi_upper = Config.tumorMaxDose;
						break;
					case Config.normalType: 
						b_voi_lower = Config.normalMinDose;;
						b_voi_upper = Config.normalMaxDose;
						break;
					case Config.bladderType: 
						b_voi_lower = Config.bladderMinDose;
						b_voi_upper = Config.bladderMaxDose;
						break;
					case Config.rectumType: 
						b_voi_lower = Config.rectumMinDose;
						b_voi_upper = Config.rectumMaxDose;
						break;
					case Config.urethraType: 
						b_voi_lower = Config.urethraMinDose;
						b_voi_upper = Config.urethraMaxDose;
						break;
				}
				
				// set lower bounds for voxel
				voi_lower_range[j] = cplex.addGe(t_lower_constraint, b_voi_lower);
				voi_upper_range[j] = cplex.addLe(t_upper_constraint, b_voi_upper);
				
				// initialize slacks with zero
				s_lower_range[j] = cplex.addLe(s_lower[i][j], 0);
				s_upper_range[j] = cplex.addLe(s_upper[i][j], 0);
			}
			
			voi_lower_ranges[i] = voi_lower_range;
			voi_upper_ranges[i] = voi_upper_range;
			s_lower_ranges[i] = s_lower_range;
			s_upper_ranges[i] = s_upper_range;
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
			double obj_val = cplex.getObjValue();
			dwellTimes = cplex.getValues(t);
			System.out.println("obj: " + cplex.getObjValue());
			if (obj_val > 0)
			{							
				for (int j = 0; j < voxels[Config.tumorType-1].length; j++)
				{
					voi_lower_ranges[Config.tumorType-1][j].setLB(voi_lower_ranges[Config.tumorType-1][j].getLB() - obj_val);
				}
			}
			else
			{
				// lower bounds are feasible -> nothing to do here
			}
			
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
		for (int i = 0; i < Config.tumorType; i++)
		{	
			for (int j = 0; j < voxels[i].length; j++)
			{
				if (voxels[i][j].getBodyType() == Config.tumorType)
				{
					// set slack to difference between upper and lower bound
					double difference = voi_upper_ranges[i][j].getUB() - voi_lower_ranges[i][j].getLB();
					s_lower_ranges[i][j].setUB(difference);
					
					// set lower bound to upper bound
					voi_lower_ranges[i][j].setLB(voi_upper_ranges[i][j].getUB());
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
			double[] slackValues = cplex.getValues(s_lower[Config.tumorType]);
			
			// set new PTV lower bounds (b_PTV_lower) to difference between lower bounds and slack values 
			// -> highest lower bound possible 
			// -> b_PTV_lower = b_PTV_lower - b_s_PTV_lower
			for (int i = 0; i < Config.tumorType; i++)
			{	
				for (int j = 0; j < voxels[i].length; j++)
				{
					if (voxels[i][j].getBodyType() == Config.tumorType)
					{
						// set lower bound to difference between PTV lower bound and slack
						double difference = voi_lower_ranges[i][j].getLB() - slackValues[j];
						voi_lower_ranges[i][j].setLB(difference);
						
						// set slack bound back to zero
						s_lower_ranges[i][j].setUB(0);
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
		for (int i = 0; i < Config.tumorType; i++)
		{	
			for (int j = 0; j < voxels[i].length; j++)
			{
				if (voxels[i][j].getBodyType() == Config.tumorType)
				{
					// set difference to PTV_goalDose and PTV lower bound
					double difference = Config.tumorGoalDose - voi_lower_ranges[i][j].getLB();
					if (difference > 0)
					{
						s_lower_ranges[i][j].setUB(difference);
					
						// set PTV lower bound to goal dose
						voi_lower_ranges[i][j].setLB(Config.tumorGoalDose);
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
			double[] slackValues = cplex.getValues(s_lower[Config.tumorType-1]);
					
			// set new PTV lower bounds (b_PTV_lower) to difference between lower bounds and slack values 
			// -> highest lower bound possible 
			// -> b_PTV_lower = b_PTV_lower - b_s_PTV_lower
			for (int i = 0; i < Config.tumorType; i++)
			{	
				for (int j = 0; j < voxels[i].length; j++)
				{
					if (voxels[i][j].getBodyType() == Config.tumorType)
					{
						// set lower bound to difference between PTV lower bound and slack
						double difference = voi_lower_ranges[i][j].getLB() - slackValues[j];
						voi_lower_ranges[i][j].setLB(difference);
						
						// set slack bound back to zero
						s_lower_ranges[i][j].setUB(0);
					}
				}
			}			
		}
		cplex.remove(objective);
    	return;
	}
	
}
