package utils;

import java.io.Serializable;

public class Voxel implements Serializable {
	private static final long serialVersionUID = 21L;
	private double maxDosis;
	private double minDosis;
	private double goalDosis;
	private double currentDosis;
	private Coordinate coordinate;
	private int bodyType = -1;
	
	
	public Voxel(int x, int y, int z) {
		
		coordinate = new Coordinate(x, y, z);
		
	}
	
	public void setBodyType (int bodyType) {
		this.bodyType = bodyType;
	}
	
	public int getBodyType () {
		return bodyType;
	}
	
	/**
	 * distanceToVoxel
	 * @param voxel
	 * The voxel to which the distance shall be measured
	 * @return
	 * The distance to the voxel
	 */
	public double distanceToVoxel (Coordinate position) {
		
	    return( Math.sqrt( Math.pow(this.getCoordinate().getX()-position.getX(), 2) + Math.pow(this.getCoordinate().getY()-position.getY(), 2) + Math.pow(this.getCoordinate().getZ()-position.getZ(), 2) ) );
				
	}
	public double radiationIntensity(Coordinate position, long durationMilliSec){
		double distance = distanceToVoxel(position);
		double gp = 0;
		double dose= 0;
		
		if(distance > 10) {
			return 0.0;
		}
		double[] coeff = { 		//coefficents for the interpolationspolynom
				  4.889e-06,  
		         -0.0002256,
		          0.00439,
		         -0.04693,
		          0.3003,
		         -1.178,
		          2.793,
		         -3.806,
		          2.544,
		          0.3994 };
		
		//  Interpolation for dose function (point source)
		for(int i = 9; i > 0; i--){
			gp += coeff[i] * Math.pow(distance, i);
		}
		if(distance != 0) {
			dose = (Config.GAMMA_BEST_INDUSTRIES * Config.SK * Math.pow((Config.R0/distance),2) * gp) * durationMilliSec ; //TODO: PHIan
		} else {
			dose = Config.MAX_DOSE * durationMilliSec;
		}
		
		
		
		return dose;
	}
	
	
	
		
	public int getX() {
		return coordinate.getX();
	}
	public void setX(int x) {
		coordinate.setX(x);
	}
	public int getY() {
		return coordinate.getY();
	}
	public void setY(int y) {
		coordinate.setX(y);
	}
	public int getZ() {
		return coordinate.getZ();
	}
	public void setZ(int z) {
		coordinate.setX(z);
	}
	public double getMaxDosis() {
		return maxDosis;
	}
	public void setMaxDosis(double maxDosis) {
		this.maxDosis = maxDosis;
	}
	public double getMinDosis() {
		return minDosis;
	}
	public void setMinDosis(double minDosis) {
		this.minDosis = minDosis;
	}
	public Coordinate getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public double getCurrentDosis() {
		return currentDosis;
	}

	public void setCurrentDosis(double currentDosis) {
		this.currentDosis = currentDosis;
	}
	
	public void addCurrentDosis(double currentDosis) {
		this.currentDosis += currentDosis;
	}

	public double getGoalDosis() {
		return goalDosis;
	}

	public void setGoalDosis(double goalDosis) {
		this.goalDosis = goalDosis;
	}


}
