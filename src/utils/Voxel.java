package utils;

import java.io.Serializable;

public class Voxel implements Serializable {
	private static final long serialVersionUID = 21L;
	
	public static final double GRID_RESOLUTION = 0.2;  // Distance between two voxel in cm
	private double maxDosis;
	private double minDosis;
	private double goalDosis;
	private double currentDosis;
	private Coordinate coordinate;
	private int bodyType = -1;
	
	
	public Voxel(double x, double y, double z) {
		
		coordinate = new Coordinate(x, y, z);
		
	}
	
	/**
	 * distanceToVoxel
	 * @param voxel
	 * The voxel to which the distance shall be measured
	 * @return
	 * The distance to the voxel
	 */
	public double distanceToVoxel (Coordinate position) {
		
	    return(( Math.sqrt( Math.pow(this.getCoordinate().getX()-position.getX(), 2) + Math.pow(this.getCoordinate().getY()-position.getY(), 2) + Math.pow(this.getCoordinate().getZ()-position.getZ(), 2) ) ) * GRID_RESOLUTION);
				
	}
	public double radiationIntensity(Coordinate position, double durationMilliSec){
		double distance = distanceToVoxel(position);
		double gp = 0;
		double dose= 0;
		
		if(distance > 10) {
			return 0.0;
		}
		double[] coeff = { 		//coefficents for the interpolationspolynom
				0.000339677417477463,  
				-0.00967651536238707,
				0.101436431655208,
				-0.466125245197087,
				0.745586768135480,
		        0.635278099796227 };
		
		
		//  Interpolation for dose function (point source)
		int i,j=0;
		for( i = 0,  j = 5 ; i < 6; i++,j--){
			gp += coeff[i] * Math.pow(distance,j);
			//System.out.println("koeffizient: "+ i+ " pow: "+gp);
		}
		if(distance > 1) {
			//System.out.println("dose: "+Config.GAMMA_BEST_INDUSTRIES * Config.SK * Math.pow((Config.R0/distance),2) * gp+ " distance: "+ distance+ " gp: "+ gp);
			dose = (Config.GAMMA_BEST_INDUSTRIES * Config.SK * Math.pow((Config.R0/distance),2) * gp) * durationMilliSec ; //TODO: PHIan
		} else {
			dose = Config.MAX_DOSE * durationMilliSec;
		}
		//System.out.println(dose);
		
		
		
		
		return dose;
	}
	
	public double doseFunction(Coordinate position){
		double distance = distanceToVoxel(position);
		double gp = 0;
		
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
		for(int i = -1; i >= 0; i--){
			gp += coeff[i] * Math.pow(distance, i);
		}
		if(distance > 1) {
			System.out.println(Config.GAMMA_BEST_INDUSTRIES * Config.SK * Math.pow((Config.R0/distance),2) * gp);
			return (Config.GAMMA_BEST_INDUSTRIES * Config.SK * Math.pow((Config.R0/distance),2) * gp); //TODO: PHIan
		} else {
			return Config.MAX_DOSE;
		}
	}
	
	
	
		
	public double getX() {
		return coordinate.getX();
	}
	public void setX(double x) {
		coordinate.setX(x);
	}
	public double getY() {
		return coordinate.getY();
	}
	public void setY(double y) {
		coordinate.setX(y);
	}
	public double getZ() {
		return coordinate.getZ();
	}
	public void setZ(double z) {
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
	
	public void setBodyType (int bodyType) {
		this.bodyType = bodyType;
	}
	
	public int getBodyType () {
		return bodyType;
	}

}
