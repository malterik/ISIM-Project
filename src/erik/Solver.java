package erik;

import utils.LogTool;
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
