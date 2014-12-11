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
	public static final double ptvGoalDose = 50;
	
	public static final double oarMinDose = 0;
	public static final double oarMaxDose = 3;
	public static final double oarGoalDose = 30;
	
	public static final double normalDose = 10;

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
	
	// Parameters for Dosefunction
	public static final int SK = 55;
	public static final double GAMMA_BEST_INDUSTRIES = 1.018;
	public static final int R0 = 1;
	public static final double MAX_DOSE = 55;
	// f(x) = p1*x^9 + p2*x^8 + p3*x^7 + p4*x^6 +  p5*x^5 + p6*x^4 + p7*x^3 + p8*x^2 + p9*x + p10
    /*
     *    4.889e-06  
         -0.0002256 
          0.00439  
         -0.04693  
          0.3003  
         -1.178  
          2.793  
         -3.806  
          2.544  
          0.3994  
       */
	
	//states
	public static final int normal 		= 1; //low dose
	public static final int spine 		= 2; //no dose
	public static final int liver 		= 3; //low-medium dose
	public static final int pancreas 	= 4; // no dose
	public static final int tumor 		= 5; // high dose
	
	
	
	

}
