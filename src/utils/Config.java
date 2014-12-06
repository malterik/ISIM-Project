package utils;

public class Config {
	
	
	//  Oar: 33 Tumor: 35-45 Gray
	public static final int xDIM = 100;
	public static final int yDIM = 100;
	public static final int zDIM = 100;
	
	public static final int	ptvXLow = 45;
	public static final int ptvXHigh = 55;
	public static final int	ptvYLow = 45;
	public static final int ptvYHigh = 55;
	public static final int	ptvZLow = 45;
	public static final int ptvZHigh = 55;
	
	public static final double ptvMinDose = 6;
	public static final double ptvMaxDose = 10;
	public static final double ptvGoalDose = 8;
	
	public static final double oarMinDose = 0;
	public static final double oarMaxDose = 3;
	public static final double oarGoalDose = 0;

	public static final double alpha = 10;			// coefficents for the intensity function
	public static final double beta = -0.5; 		//I(r,t)=alpa * exp(beta * r) * t
	
	public static final int numberOfGenes = 4;   //must be an even value
	public static final int numberOfSeeds = 5;
	public static final int numberOfIterations = 500;
	// outputs
	public static final boolean errors = true;
	public static final boolean debug = true;
	public static final boolean warnings = true;
	public static final boolean notification = true;
	

}
