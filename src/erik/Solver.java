package erik;

import java.util.List;

import utils.Config;
import utils.Coordinate;
import utils.GeneticAlg;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;

public class Solver {
	
	public static Voxel [][][] body;
	
	public Solver(Voxel [][][] body) {
		this.body = body;
		
	}
	

	
	
	
	public void solveGeneticAlg() {
		
		LogTool.print("Created Population","notification");
		Population.solve();
	}




	

}
