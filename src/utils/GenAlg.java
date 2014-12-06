package utils;

import java.util.List;





public class GenAlg {
	
	private List<Coordinate> seedPositions;
	private Voxel[][][] body; 
	private int numberOfSeeds;
	private double [] gene;
	private int geneSize;
	public GenAlg(Coordinate[] seedPostions, int numberOfSeeds, double [] gene, int geneSize, Voxel [][][] body  ) {

		this.seedPositions = seedPositions;
		this.numberOfSeeds = numberOfSeeds;
		this.gene = gene;
		this. geneSize = geneSize;
		this.body = body;
		
	}
	
	private double fitnessFunction () {
		Voxel iterativePoint = new Voxel(0,0,0);
		
		double ret = 0;
		for(int x=0; x < Config.xDIM; x++) {
			
			for(int y=0; y < Config.yDIM; y++) {
				
				for(int z=0; z < Config.zDIM; z++) {
					iterativePoint.setCoordinate(new Coordinate(x, y, z));
					Voxel bodyPoint = body[x][y][z];
					bodyPoint.setCurrentDosis(0);
				/*	for(Seed s: input) {
						
						bodyPoint.addCurrentDosis( s.radiationIntensity(s.distanceToVoxel(iterativePoint), s.getDurationMilliSec()));	
						ret += Math.pow((bodyPoint.getCurrentDosis()-bodyPoint.getGoalDosis()),2);
					}*/	
				}	
			}
		}
		
		
		return Math.sqrt(ret);
		
		
	}

}
