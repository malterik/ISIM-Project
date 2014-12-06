package utils;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Trash {
	
	public  void model1() {
		try {
			
			IloCplex cplex = new IloCplex();
			
			//variables
			IloNumVar x = cplex.numVar(0, Double.MAX_VALUE,"x");
			IloNumVar y = cplex.numVar(0, Double.MAX_VALUE,"y");
			
			
			// expressions
			IloLinearNumExpr objective = cplex.linearNumExpr();
			
			objective.addTerm(0.12,	x);
			objective.addTerm(0.15,	y);
			
			// define objectives
			
			cplex.addMinimize(objective);
			
			
			//define constraints
			
			cplex.addGe( cplex.sum(cplex.prod(60, x),cplex.prod(60, y)), 300);
			cplex.addGe( cplex.sum(cplex.prod(12, x),cplex.prod(6, y)), 36);
			cplex.addGe( cplex.sum(cplex.prod(10, x),cplex.prod(30, y)), 90);
			
			cplex.exportModel("Test.lp");
			
			// solve
			if(cplex.solve()) {
				
				System.out.println("obj = "+cplex.getObjValue());
				System.out.println("x = "+cplex.getValue(x));
				System.out.println("y = "+cplex.getValue(y));
				
				
			} else {
				System.out.println("No Solution was found");
			}
			
			
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

}
