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
	
	// Parameters for classification
	public static final double VOXEL_DIST = 1; // in cm
	public static final int TUMOR = 5; // BodyType for Tumor
	
        // Parameters for the Simulated Annealing algorithm
        
        public static final double NumberOfMetropolisRounds = 100;
        public static final double StartTemp = NumberOfMetropolisRounds;
        public static final int SAnumberOfSeeds = 3;
        
        
	// Parameters for the genetic algorithm
	
	public static final int numberOfSeeds = 50;
	public static final int numberOfIterations = 10;
	
	// outputs
	public static final boolean errors = true;
	public static final boolean debug = true;
	public static final boolean warnings = true;
	public static final boolean notification = true;
	
	// Parameters for Dosefunction
	public static final double SK = 47;
	public static final double LAMBDA = 63.52;
	public static final double Gl_r0_phi0 = 53.71;
	public static final double L = 0.35;
	public static final double R0 = 1;
	public static final double MAX_DOSE = 55;
	
	
	//states
	public static final int normalType 		= 1; //low dose
	public static final int spineType 		= 2; //no dose
	public static final int liverType 		= 3; //low-medium dose
	public static final int pancreasType 	= 4; // no dose
	public static final int tumorType 		= 5; // high dose
	
	//Multithreading
	
	public static final int numberOfThreads = 4;
	
	public static final int treatmentRange = 10; // indicates the range in cm for the region around the tumor which shall be treated by radiation
	
	public static final int scaleFactor = 5;
	
	
	
	

}
