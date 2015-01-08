package erik;

import java.util.concurrent.Callable;

import utils.Config;
import utils.Coordinate;


public class DoseEvaluator implements Callable<Double> {


	private int x;
	private int[] dimensions;
	
	 private double[] genes = new double[Config.numberOfSeeds];
	
	public DoseEvaluator(int[] dimensions, double[] genes, int x) {

		this.genes = genes;
		this.x = x;
		this.dimensions = dimensions;
	}
	

	
	public double evaluate() {
        double temp=0;
        double intensity=0;
        
			
		for(int y =0; y < dimensions[1] ; y++) {
			
			for(int z = 0; z < dimensions[2]; z++) {
				
				Solver.body[x][y][z].setCurrentDosis(0);
				for(int i=0; i<Config.numberOfSeeds;++i) {
					
					
					intensity = Solver.body[x][y][z].radiationIntensity(Solver.seeds[i].getCoordinate(), genes[i]);
					Solver.body[x][y][z].addCurrentDosis(intensity);
					
				}	
				temp += Math.pow((Solver.body[x][y][z].getGoalDosis()-Solver.body[x][y][z].getCurrentDosis()),2);
				
				
			}	
		}
		
        
        return temp;
    }

	public Double call() throws Exception {
		double partial_result = evaluate();
		return partial_result;
	}
	
	

}
