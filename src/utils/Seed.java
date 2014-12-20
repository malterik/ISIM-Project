package utils;

/**
 * 
 * @author Erik
 *
 */
public class Seed extends Voxel {
	
	
	public Seed(double x, double y, double z, double durationMilliSec) {
		super(x, y, z);
		this.durationMilliSec = durationMilliSec;
		
	}
	private double durationMilliSec = 0;
	
	
	
	public double getDurationMilliSec() {
		return durationMilliSec;
	}
	public void setDurationMilliSec(double durationMilliSec) {
		this.durationMilliSec = durationMilliSec;
	}

}
