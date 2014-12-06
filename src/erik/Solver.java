package erik;

import java.util.List;

import utils.Config;
import utils.Coordinate;
import utils.GeneticAlg;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;

public class Solver extends GeneticAlg<Seed> {
	
	private Voxel [][][] body;
	
	public Solver(Voxel [][][] body) {
		this.body = body;
		
	}
	
	
	

	@Override
	public double fitnessFunction(List<Seed> input) {
		Voxel iterativePoint = new Voxel(0,0,0);
		
		double ret = 0;
		for(int x=0; x < Config.xDIM; x++) {
			
			for(int y=0; y < Config.yDIM; y++) {
				
				for(int z=0; z < Config.zDIM; z++) {
					iterativePoint.setCoordinate(new Coordinate(x, y, z));
					Voxel bodyPoint = body[x][y][z];
					bodyPoint.setCurrentDosis(0);
					for(Seed s: input) {
						
						bodyPoint.addCurrentDosis( s.radiationIntensity(s.distanceToVoxel(iterativePoint), s.getDurationMilliSec()));	
						ret += Math.pow((bodyPoint.getCurrentDosis()-bodyPoint.getGoalDosis()),2);
					}	
				}	
			}
		}
		
		
		return Math.sqrt(ret);
	}




	@Override
	public void onePointCrossOver(List<Seed> a, List<Seed> b) {
		
		for(int i = 0; i < a.size()/2; i++) {		//TODO implement some random functionality in here!
			
			long temp = a.get(i).getDurationMilliSec();
			a.get(i).setDurationMilliSec(b.get(i).getDurationMilliSec());
			b.get(i).setDurationMilliSec(temp);
			
			
		}
		
	}




	@Override
	public void mutate(Seed input) {
		
		long temp = input.getDurationMilliSec();
		
		if(RandGenerator.randBoolean()) {
			input.setDurationMilliSec(temp - RandGenerator.randInt(0, (int) temp/2));
		} else {
			
			input.setDurationMilliSec(temp + RandGenerator.randInt(0, (int) temp/2));
		}
		
		
		
	}




	





	

}
