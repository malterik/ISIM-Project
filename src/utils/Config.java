package utils;

public class Config {
	
	
	//  Oar: 33 Tumor: 35-45 Gray
	// Body dimensions and tumor position, may be obsolete when the mat files are included
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
	
	public static final double normalGoalDose = 20; //TODO evaluate
	public static final double spineGoalDose = 0;
	public static final double liverGoalDose = 30;
	public static final double pancreasGoalDose = 0;
	public static final double tumorGoalDose = 50;
	
        // Parameters for the Simulated Annealing algorithm
        
        public static final double NumberOfMetropolisRounds = 10;
        
	// Parameters for the genetic algorithm
	
	public static final int numberOfSeeds = 500;
	public static final int numberOfIterations = 500;
	
	// outputs
	public static final boolean errors = true;
	public static final boolean debug = true;
	public static final boolean warnings = true;
	public static final boolean notification = true;
	
	// Parameters for Dosefunction
	public static final double SK = 55;
	public static final double GAMMA_BEST_INDUSTRIES = 1.018;
	public static final double R0 = 1;
	public static final double MAX_DOSE = 55;
	
	
	//states
	public static final int normalType 		= 1; //low dose
	public static final int spineType 		= 2; //no dose
	public static final int liverType 		= 3; //low-medium dose
	public static final int pancreasType 	= 4; // no dose
	public static final int tumorType 		= 5; // high dose
	
	
	
	

}
