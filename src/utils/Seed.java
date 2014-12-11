package utils;

/**
 * 
 * @author Erik
 *
 */
public class Seed extends Voxel {
	
	
	public Seed(double x, double y, double z, long durationMilliSec) {
		super(x, y, z);
		this.durationMilliSec = durationMilliSec;
		
	}
	private long durationMilliSec = 0;
	
	
	
	public long getDurationMilliSec() {
		return durationMilliSec;
	}
	public void setDurationMilliSec(long durationMilliSec) {
		this.durationMilliSec = durationMilliSec;
	}

}
