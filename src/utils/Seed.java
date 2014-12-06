package utils;

/**
 * 
 * @author Erik
 *
 */
public class Seed extends Voxel {
	
	
	public Seed(int x, int y, int z, long durationMilliSec) {
		super(x, y, z);
		this.durationMilliSec = durationMilliSec;
		
	}
	private long durationMilliSec = 0;
	
	public double radiationIntensity(double distance, long durationMilliSec){
		return ( Config.alpha * Math.exp( Config.beta * distance)) * durationMilliSec;
	}
	
	public long getDurationMilliSec() {
		return durationMilliSec;
	}
	public void setDurationMilliSec(long durationMilliSec) {
		this.durationMilliSec = durationMilliSec;
	}

}
