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
		Individual test = Population.solve();
		
		for(int i=0; i<Individual.SIZE;i++) {
			System.out.println("Verweildauer: "+test.getGene(i));
		}
	}




	

}
