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
	
	
	
	/**
	 * This method provides the value of radiation intensity
	 * @param position
	 * @param durationMilliSec
	 * @return
	 */
	public double radiationIntensity(Coordinate position, double durationMilliSec){
		double distance = distanceToVoxel(position);
		double phi = 90;  //TODO Magic number
		double gp = 0;
		double dose= 0;
		
		if(distance > 10) {
			return 0.0;
		}
		
		
		
		//  Interpolation for dose function (point source)
		
		if(distance > 1) {
			
			dose = (Config.LAMBDA * Config.SK * (GL(distance, phi)/Config.Gl_r0_phi0)* gl(distance) ) * durationMilliSec ; //TODO: Fr()
		} else {
			dose = Config.MAX_DOSE * durationMilliSec;
		}
		
		
		
		
		
		return dose;
	}
	
	public static void main(String args[]) {
		Voxel v = new Voxel(0, 0, 0);
		for(int i=0; i<10; i++) {
			System.out.println(v.gl(i));
		}
		
		System.exit(0);
		
	}
	
	/*  Mathematical help functions to calculate all partial resultes for the dose */ 
	
	
	private double gl(double r) {
		double gl = 0;
		
		double[] coeff = { 		//coefficents for the interpolationspolynom
				7.093621846112430e-09,  
				-7.781185037105809e-07,
				2.447013834626613e-05,
				-3.532001495663614e-04,
				2.671000325534424e-03,
				-1.080919296055047e-02,
				2.067027695478220e-02,
				-1.095883686414140e-02,
				9.984496716251168e-01
				};
		
		int i,j=0;
		for( i = 0,  j = 8 ; i < 9; i++,j--){
			gl += coeff[i] * Math.pow(r,j);
			//System.out.println("koeffizient: "+ i+ " pow: "+gp);
		}
		
		return gl;
		
	}
	
	private double GL(double r, double phi) {
		double res = 0; 
		if(phi == 0) {
			
			res = 1 / ( Math.pow(r,2) - (Math.pow( Config.L,2)/4 ) );
			
		} else {
			res = beta(r, phi) / ( Config.L * r * Math.sin(phi));
		}
		
		return res;
	}
	
	private double beta(double r, double phi) {
		double res = 0;
		
		double part_res1 = 0;
		double part_res2 = 0;
		
		part_res1 = Math.acos( (r - (Config.L / 2) * Math.cos(phi) )  / (Math.sqrt( (Math.pow(Config.L,2)/4 )+ Math.pow(r,2)- Config.L * r * Math.cos(phi) )) )   ; 
		part_res2 = Math.acos( (r + (Config.L / 2) * Math.cos(phi) )  / (Math.sqrt( (Math.pow(Config.L,2)/4 )+ Math.pow(r,2)+ Config.L * r * Math.cos(phi) )) )   ;
		res = part_res1 + part_res2;
		return res;
	}
	
	
	
	/*public double doseFunction(Coordinate position){
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
			System.out.println(Config.LAMBDA * Config.SK * Math.pow((Config.R0/distance),2) * gp);
			return (Config.LAMBDA * Config.SK * Math.pow((Config.R0/distance),2) * gp); //TODO: PHIan
		} else {
			return Config.MAX_DOSE;
		}
	}*/
	
	
	
		
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
