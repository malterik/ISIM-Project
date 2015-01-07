package erik;

import java.util.concurrent.Callable;

import utils.Config;
import utils.Coordinate;


public class DoseEvaluator implements Callable<Double> {

	private Coordinate start;
	private Coordinate end;
	
	 private double[] genes = new double[Config.numberOfSeeds];
	
	public DoseEvaluator(Coordinate start, Coordinate end, double[] genes) {
		this.start = start;
		this.end = end;
		this.genes = genes;
	}
	

	
	public double evaluate() {
        double fitness = 0;
        double temp=0;
        double intensity=0;
        for(int x = (int) start.getX(); x < (int) end.getX(); x++) {
			
			for(int y = (int) start.getY(); y < (int) end.getY(); y++) {
				
				for(int z = (int) start.getZ(); z < (int) end.getZ(); z++) {
					
					Solver.body[x][y][z].setCurrentDosis(0);
					for(int i=0; i<Config.numberOfSeeds;++i) {
						
						
						intensity = Solver.body[x][y][z].radiationIntensity(Solver.seeds[i].getCoordinate(), genes[i]);
						Solver.body[x][y][z].addCurrentDosis(intensity);
						
					}	
					temp += Math.pow((Solver.body[x][y][z].getGoalDosis()-Solver.body[x][y][z].getCurrentDosis()),2);
					
					
				}	
			}
		}
        
        //fitness = Math.sqrt(temp);
        
        return temp;
    }

	public Double call() throws Exception {
		double partial_result = evaluate();
		return partial_result;
	}
	
	

}
