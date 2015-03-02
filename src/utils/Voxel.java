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
        public double metavalue;
	private static double[] lut;
	
	
	public Voxel(double x, double y, double z) {
		
		coordinate = new Coordinate(x, y, z);
                
		
	}
	
	/**
	 * LUT creation for fast dose approximation
	 * @param maxDist
	 * @param num
	 */
	public static void setLUT(double maxDist, int num)
	{
		lut = new double[num+1];
		double phi = 90.0;
		
		for (int i = 0; i <= num; i++)
		{
			double distance = i*(maxDist/(double)(num+1));
			lut[i] = ((1.12 * GL(1,90)/100) * Config.SK * (GL(distance, phi)/GL(1,90)) * gl(distance) * F(phi,distance) );  
		}
	}
	
	/**
	 * distanceToVoxel
	 * @param voxel
	 * The voxel to which the distance shall be measured
	 * @return
	 * The distance to the voxel
	 */
	public double distanceToVoxel (Coordinate position) {
	    return this.getCoordinate().distanceToCoordiante(position);
	}
	
	
	
	/**
	 * This method provides the value of radiation intensity
	 * You have to use it as follows : <p>
	 * Voxel bodyVoxel = new Voxel(0,0,0) <br>
	 * Seed seed = new Seed(0,0,0,1)<br>
	 * bodyVoxel.radiationIntensity(seed.getCoordinate());
	 * always this way, because the formula needs the goaldose for the case that the distance is zero between seed and voxel
	 * @param position
	 * The postition where the seed is 
	 * @param durationMilliSec
	 * The time the seed radiates
	 * @param phi
	 * angle phi 
	 * @return
	 */
	public double radiationIntensity(Coordinate position, double durationMilliSec, double phi){
		double distance = distanceToVoxel(position);
		
		double dose= 0;
		
		if(distance > 10) {
			return 0.0;
		}

		//  Interpolation for dose function 
		
		if(distance == 0) {
			dose = this.getGoalDosis();
		} else if (Config.useLUT == true){
			radiationIntensityLUT(position, durationMilliSec, phi);
		} else {
			dose = ((1.12 * GL(1,90)/100) * Config.SK * (GL(distance, phi)/GL(1,90)) * gl(distance) * F(phi,distance) ) * durationMilliSec ; 
		}
		
		return dose;
	}
	
	public double radiationIntensity(Coordinate position, double durationMilliSec){
		return radiationIntensity(position, durationMilliSec, 90);
	}
	
	public double radiationIntensityLUT(Coordinate position, double durationMilliSec, double phi){
		double distance = distanceToVoxel(position);
		
		double dose= 0;
		
		if(distance > 10) {
			return 0.0;
		}

		//  Interpolation for dose function 
		
		if(distance == 0) {
			dose = this.getGoalDosis();
		} else {
			dose = lut[(int)(distance/10.0*Config.LUTSize)] * durationMilliSec;
		}
		
		return dose;
	}
	
	/*public double radiationIntensity(Coordinate position, double durationMilliSec){
            double temp=radiationIntensityNoTimeCALC(position, 90);
             if (temp>0.0) {
//                LogTool.print("Wert in Time " + temp, "notification");
//                LogTool.print("Wert in Time " + temp-, "notification");
            }
		return temp*durationMilliSec;
	}*/
	
        
        public double radiationIntensityNoTime(Coordinate position){
            double temp = radiationIntensityNoTimeCALC(position, 90);
            if (temp>0.0) {
                LogTool.print("Wert in nOtIME " + temp, "notification");
            }
		return temp;
	}
	
	public double radiationIntensity(double r, double durationMilliSec){
		double distance = r;
		
		double phi = 90;  //TODO Magic number
		double dose= 0;
		
		if(distance > 10) {
			return 0.0;
		}

		//  Interpolation for dose function 
		if(distance == 0) {
			dose = this.getGoalDosis();
		} else {
			dose = ( (1.12 * GL(1,90)/100) * Config.SK  * (GL(distance, phi)/GL(1,90)) * gl(distance) * F(phi,distance) ) * durationMilliSec ; 
		}
		
		
		return dose;
	}
	
        /**
         * @author Dawid
         * @param position
         * @param phi
         * @return metavalue
         */
        
        public double radiationIntensityNoTimeCALC(Coordinate position, double phi){
		double distance = distanceToVoxel(position);
		
		double dose= 0;
		
		if(distance > 10) {
			return 0.0;
		}

		//  Interpolation for dose function 
		
		if(distance == 0) {
			dose = this.getGoalDosis();
		} else {
			dose = ((1.12 * GL(1,90)/100) * Config.SK * (GL(distance, phi)/GL(1,90)) * gl(distance) * F(phi,distance) ); 
		}
		
		return dose;
	}
        
	public static void main(String args[]) {
		Voxel v = new Voxel(0, 0, 0);
		
		for (double j = 0; j < 10; j+= 0.01) {
			System.out.println( v.radiationIntensity(j,1));
		}
			
		
		System.exit(0);
		
	}
	
	/*  Mathematical help functions to calculate all partial resultes for the dose */ 
	
	
	private static double F(double phi, double r) {
		
		
		if(phi == 90) {
			return 1.0;
		}
		
		double F = 0.0;
		double[] partials = new double[6];
		
		double[] coeff = {
				
				6.994621358157904e-01,	//p00
				7.158516844452122e-03,	//p10
				-5.954907267564410e-03,	//p01
				-3.839824714237888e-05,	//p20
				-1.279397989781352e-04,	//p11
				1.839889087951225e-03	//p02
		};
		
		// This implements the function f(phi,r) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2
		
		partials[0] = coeff[0];
		partials[1] = coeff[1] * phi;
		partials[2] = coeff[2] * r;
		partials[3] = coeff[3] * Math.pow(phi, 2);
		partials[4] = coeff[4] * phi * r;
		partials[5] = coeff[5] * Math.pow(r,2);
		
		for(int i = 0; i < 6; i++) {
			F += partials[i];
		}

		
		
		
		return F;
	}
	
	private static double gl(double r) {
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
	
	private static double GL(double r, double phi) {
		double res = 0; 
		if(phi == 0) {
			
			res = 1 / ( Math.pow(r,2) - (Math.pow( Config.L,2)/4 ) );
			
		} else {
			res = beta(r, phi) / ( Config.L * r * Math.sin(Math.toRadians(phi)));
		}
		
		return res;
	}
	
	private static double beta(double r, double phi) {
		double res = 0;
		
		double part_res1 = 0;
		double part_res2 = 0;
		
		
		part_res1 = Math.toDegrees(Math.acos( (r - ((Config.L / 2) * Math.cos(Math.toRadians(phi))) )  / Math.sqrt( Math.pow(Config.L,2)/4 + Math.pow(r,2)- Config.L * r * Math.cos(Math.toRadians(phi)) ) ))   ; 
		part_res2 =Math.toDegrees(Math.acos( (r + ((Config.L / 2) * Math.cos(Math.toRadians(phi))) )  / Math.sqrt( Math.pow(Config.L,2)/4 + Math.pow(r,2)+ Config.L * r * Math.cos(Math.toRadians(phi)) ) ) )   ;
		res = part_res1 + part_res2;
		return res;
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
	
	public double getRelaxedGoalDosis() {
		if(goalDosis == 0)
		{
			return Config.relaxDose;
		}
		else
		{
			return goalDosis;
		}
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
