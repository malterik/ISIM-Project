package utils;

import java.io.Serializable;

public class Voxel implements Serializable {
	private static final long serialVersionUID = 21L;
	private double maxDosis;
	private double minDosis;
	private double goalDosis;
	private double currentDosis;
	private Coordinate coordinate;
	
	
	public Voxel(int x, int y, int z) {
		
		coordinate = new Coordinate(x, y, z);
		
	}
	
	/**
	 * distanceToVoxel
	 * @param voxel
	 * The voxel to which the distance shall be measured
	 * @return
	 * The distance to the voxel
	 */
	public double distanceToVoxel (Voxel voxel) {
		
	    return( Math.sqrt( Math.pow(this.getCoordinate().getX()-voxel.getCoordinate().getX(), 2) + Math.pow(this.getCoordinate().getY()-voxel.getCoordinate().getY(), 2) + Math.pow(this.getCoordinate().getZ()-voxel.getCoordinate().getZ(), 2) ) );
				
	}
	
	public double radiationIntensity(double distance, long durationMilliSec){
		return ( Config.alpha * Math.exp( Config.beta * distance)) * durationMilliSec;
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
