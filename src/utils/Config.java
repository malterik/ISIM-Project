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
	
	public static final double normalGoalDose = 10; 	//max
	public static final double bladderGoalDose = 0; 		//max
	public static final double rectumGoalDose = 30; 		//max
	public static final double urethraGoalDose = 0; 	//max
	public static final double tumorGoalDose = 50; 		//min
	
	public static final double relaxDose = 5;
	
	public static final double normalMinDose = 0;
	public static final double bladderMinDose = 0;
	public static final double rectumMinDose = 0;
	public static final double urethraMinDose = 0;
	public static final double tumorMinDose = 30;
	
	public static final double normalMaxDose = 40;
	public static final double bladderMaxDose = 30;
	public static final double rectumMaxDose = 50;
	public static final double urethraMaxDose = 30;
	public static final double tumorMaxDose = 70;
	
	public static final String[] bodyTypeDescriptions = {"Normal", "Spine", "Liver", "Pancreas", "Tumor"};
	
	
	// Parameters for classification
	public static final double VOXEL_DIST = 0.1; // in cm
	public static final int TUMOR = 5; // BodyType for Tumor
	
        // Parameters for the Simulated Annealing algorithm
        
        // SAostFunctionType legend
        // 0: fast using the Solver.TumorBounds
        // 1: fast using ptv static bounds above
        // 2: slow using the Solver.TumorBounds
        // 3: slow using the static bounds above
        // in case of errors keep changing, option 3 is SAFE
        
        public static final int SACostFunctionType = 0; //see above comments 
        public static final boolean SAResets = false;
        public static final int NumberOfMetropolisResets = 1;
        public static final int NumberOfMetropolisRounds = 60;
        public static final double StartTemp = NumberOfMetropolisRounds;
        public static final int SAnumberOfSeeds = 50;
        public static final boolean SAdebug = true;
        public static final boolean SAdebugkoords = true;
        public static final boolean SAdebugbody = true;
        public static final int SAverboselvl = 2;
        
	// Parameters for the genetic algorithm
	
	public static int numberOfSeeds = 50;
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
	public static final double L = 35;
	public static final double R0 = 1;
	public static final double MAX_DOSE = 55;
	
	
	//states
	public static final int normalType 		= 1; //low dose
	public static final int bladderType 	= 2; //no dose (spine)
	public static final int rectumType 		= 3; //low-medium dose (liver)
	public static final int urethraType 	= 4; // no dose (pancreas)
	public static final int tumorType 		= 5; // high dose
	
	//Multithreading
	
	public static final int numberOfThreads = 4;
	
	public static final int treatmentRange = 8; // indicates the range in cm for the region around the tumor which shall be treated by radiation
	
	public static int scaleFactor = 1;
	
	public static final double cancelValue = 1;

	//other
	public static boolean useLUT = false;
	public static final int LUTSize = 10000; // size of lookup table for fast dose approximation
	
	public static void setNumberOfSeeds(int number)
	{
		numberOfSeeds = number;
	}
	
	public static void setScaleFactor(int number) {
		
		scaleFactor = number;
		
	}
	
	public static double gridResolution = 0.05;
	
	
	
	

}

