package erik;

import ilog.concert.IloException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import laurin.TreatmentAnalyzer;
import sebastian.BodyEntry;
import sebastian.SimpleDB;
import utils.Config;
import utils.Coordinate;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;

public class TreatmentPlanner {
	private static final boolean outputToFile = true;

	private static void planTreatment(String algo, double doubleArgs[]) throws IloException {

		/*
		 * // Start visualization Voxel[][][] testData = new Voxel[1][1][5];
		 * Voxel data; for (int i = 0; i < 5; i++) { data = new Voxel (0, 0, i);
		 * data.setCurrentDosis (0.5 * i + 0.5); data.setGoalDosis(1.5);
		 * testData [0][0][i] = data; } ScatterDisplay display = new
		 * ScatterDisplay(ChartType.MaxDosis); //display.fillTestData ();
		 * display.fill (testData, 1, 1, 5); display.display(); // End of
		 * visualization
		 */

		// Database test
		//Config.useLUT = true;
		//Voxel.setLUT(10, 10000);
		
		SimpleDB db = new SimpleDB();
		
		BodyEntry entry = null;
		
		/*// 0.25ppmm
		db.loadBody("prostate0_25ppmm");
		entry = db.getBodyByName("prostate0_25ppmm");
		Config.gridResolution = 0.4;*/
		
		/*// 0.5ppmm
		db.loadBody("prostate0_5ppmm");
		entry = db.getBodyByName("prostate0_5ppmm");
		Config.gridResolution = 0.2;*/
		
		// 1ppmm
		db.loadBody("prostate1ppmm");
		entry = db.getBodyByName("prostate1ppmm");
		Config.gridResolution = 0.1;
		
		/*// 2ppmm
		db.loadBody("prostate2ppmm");
		entry = db.getBodyByName("prostate2ppmm");
		Config.gridResolution = 0.05;*/
		
		/*// 4ppmm
		db.loadBody("prostate4ppmm");
		entry = db.getBodyByName("prostate4ppmm");
		Config.gridResolution = 0.025;*/
		
    	//db.loadBody("data2593.4844");
		//BodyEntry entry = db.getBodyByName("data2593.4844");
		Voxel[][][] body = null;
		if (entry != null) {

			body = new Voxel[entry.getDimensions()[0]][entry.getDimensions()[1]][entry
					.getDimensions()[2]]; // This is the body of the "patient"
			LogTool.print("Created Body Array!", "notification");

			for (int x = 0; x < entry.getDimensions()[0]; x++) {

				for (int y = 0; y < entry.getDimensions()[1]; y++) {

					for (int z = 0; z < entry.getDimensions()[2]; z++) {

						body[x][y][z] = new Voxel(x, y, z);

						switch (entry.getBodyArray()[x][y][z].getBodyType()) {
						case Config.normalType: {
							body[x][y][z].setGoalDosis(Config.normalGoalDose);
							body[x][y][z].setBodyType(Config.normalType);
							break;
						}
						case Config.bladderType: {
							body[x][y][z].setGoalDosis(Config.bladderGoalDose);
							body[x][y][z].setBodyType(Config.bladderType);
							break;
						}
						case Config.rectumType: {
							body[x][y][z].setGoalDosis(Config.rectumGoalDose);
							body[x][y][z].setBodyType(Config.rectumType);
							break;
						}
						case Config.urethraType: {
							body[x][y][z].setGoalDosis(Config.urethraGoalDose);
							body[x][y][z].setBodyType(Config.urethraType);
							break;
						}
						case Config.tumorType: {
							body[x][y][z].setGoalDosis(Config.tumorGoalDose);
							body[x][y][z].setBodyType(Config.tumorType);
							break;
						}
						default: {
							body[x][y][z].setGoalDosis(Config.normalGoalDose);
							body[x][y][z].setBodyType(Config.normalType);
							break;
						}
						}

					}
				}
			}

		}
		db.close();

		BodyAnalyzer ba = new BodyAnalyzer(body, entry.getDimensions(),Config.treatmentRange);

		/* Initialize the Seeds */

		Seed[] seeds = new Seed[Config.numberOfSeeds];
		int i =0 ;
		while (i < Config.numberOfSeeds ) {
			Coordinate co = new Coordinate(
					RandGenerator.randDouble(ba.getxBoundsTumor(1)[0], ba.getxBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getyBoundsTumor(1)[0], ba.getyBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getzBoundsTumor(1)[0], ba.getzBoundsTumor(1)[1]));
			
			if(body[(int)co.getX()][(int)co.getY()][(int)co.getZ()].getBodyType() == Config.tumorType) {
				seeds[i] = new Seed(co.getX(), co.getY(), co.getZ(), 0);
				i++;
			} 
			
		}

		LogTool.print("Initialized Body Array!", "notification");

		Solver solver = new Solver(body, seeds, entry.getDimensions()); 

		LogTool.print("Initialized Solver!", "notification");

		long start = System.currentTimeMillis();
		
		if(algo.equals("LP"))
		{
			solver.solveLP(doubleArgs[0]);
		}
		else if(algo.equals("LPWS"))
		{
			
		}
		else if(algo.equals("GA"))
		{
			double[] weighting_factors = new double[6];
			weighting_factors[0] = doubleArgs[4];
			weighting_factors[1] = 1;
			weighting_factors[2] = doubleArgs[5];
			weighting_factors[3] = doubleArgs[6];
			weighting_factors[4] = doubleArgs[7];
			weighting_factors[5] = doubleArgs[8];
			Config.setScaleFactor( (int) doubleArgs[10]); 
			solver.solveGeneticAlg((int) doubleArgs[0], (int) doubleArgs[1], doubleArgs[2], doubleArgs[3], weighting_factors,doubleArgs[9]);
			
		
		}
		else if(algo.equals("SA"))
		{
			solver.solveSA(doubleArgs);
                        //ToDo: solveSA so modden dass sie Parameter akzeptiert
                        // In meinem Fall MINDESTENS die Seedzahl
                        // Darueber hinaus gibt es nur varianten der
                        // Sum of Squared Differences...
		}

		long end = System.currentTimeMillis();
		
		Date date = new Date(end - start);
		DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
		String dateFormatted = formatter.format(date);
		System.out.println("Runtime: " + dateFormatted);
		
		
		
		TreatmentAnalyzer ta = new TreatmentAnalyzer(Solver.body,
				entry.getDimensions(), Solver.seeds);
		//ta.analyzeAll();
		ta.setRuntime(date);
		ta.setSAlgorithm(algo);
		ta.setDoubleArgs(doubleArgs);
		ta.setGridResolution(Config.gridResolution);
		String filename = "treatments/";
		filename += algo + "_";
		filename += Config.numberOfSeeds + "_";
		
		for (double dValue : doubleArgs)
		{
			filename += Double.toString(dValue) + "_";
		}
		filename += ".ser";
		ta.writeToFile(filename);
		//ta.printResults();
		// runtime measurement
		

		
		
		
		
		

		
		/*ScatterDisplay display5 = new ScatterDisplay(ChartType.BodyType);
		display5.fill(Solver.body, entry.getDimensions()[0], entry.getDimensions()[1],
				entry.getDimensions()[2]);
		display5.display();/*

		
		// System.exit(0);
		// LP:
		/*
		 * try { solver.solveLP(); } catch (IloException e) {
		 * LogTool.print("Error in LP: ", "error"); e.printStackTrace(); }
		 */
	}

	private static void createClassification() {
		SimpleDB db = new SimpleDB();
		db.classifyAll();
		db.close();
	}

	private static void printTreatmentData() {
		SimpleDB db = new SimpleDB();
		db.printTreatments();
		db.close();
	}

	public static void main(String[] args) throws IloException {
		
		
		
		String algo = args[0];
		int seedNumber = Integer.parseInt(args[1]);
		
		Config.setNumberOfSeeds(seedNumber);

		double[] doubleArgs = new double[args.length-2];

	      for (int i = 2; i < args.length; i++) {
	         try {
	            doubleArgs[i-2] = Double.parseDouble(args[i]);
	         } catch (NumberFormatException e) {
	            System.err.println("Failed trying to parse a non-numeric argument, " + args[i]);
	         }
	      }
	    if( outputToFile) {
	        String filename = "logs/";
			filename += algo + "_";
			filename += Config.numberOfSeeds + "_";
			
			for (double dValue : doubleArgs)
			{
				filename += Double.toString(dValue) + "_";
			}
			filename += ".txt";
			
			
			try {
			    System.setOut(new PrintStream(new File(filename)));
			} catch (Exception e) {
			     e.printStackTrace();
			}
	    }
		printTreatmentData();
		planTreatment(algo, doubleArgs);
		System.exit(0);				// necessary to terminate all threads 
	}
}