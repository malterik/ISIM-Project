package erik;

import java.util.concurrent.Callable;

import utils.Config;


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
        double[] weightingFactor = Population.weighting_factors;

        
			
		for(int y =Solver.yBoundsTumor[0]; y < Solver.yBoundsTumor[1] ; y+= Config.scaleFactor) {
			
			for(int z = Solver.zBoundsTumor[0]; z < Solver.zBoundsTumor[1]; z+=Config.scaleFactor) {
				
				Solver.body[x][y][z].setCurrentDosis(0);
				for(int i=0; i<Config.numberOfSeeds;++i) {
					
					
					intensity = Solver.body[x][y][z].radiationIntensity(Solver.seeds[i].getCoordinate(), genes[i]);
					Solver.body[x][y][z].addCurrentDosis(intensity);
					
				}	
				temp += Math.pow((Solver.body[x][y][z].getGoalDosis()-Solver.body[x][y][z].getCurrentDosis()),2) * weightingFactor[Solver.body[x][y][z].getBodyType()];
				
			}	
		}
		
        
        return temp;
    }

	public Double call() throws Exception {
		double partial_result = evaluate();
		return partial_result;
	}
	
	

}

