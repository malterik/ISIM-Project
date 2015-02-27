package erik;

import ilog.concert.IloException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import laurin.TreatmentAnalyzer;
import sebastian.BodyEntry;
import sebastian.ScatterDisplay;
import sebastian.ScatterDisplay.ChartType;
import sebastian.SimpleDB;
import utils.Config;
import utils.Coordinate;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;

public class TreatmentPlanner {

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
		SimpleDB db = new SimpleDB();
		db.loadBody("data2593.4844");
		BodyEntry entry = db.getBodyByName("data2593.4844");
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
						case Config.spineType: {
							body[x][y][z].setGoalDosis(Config.spineGoalDose);
							body[x][y][z].setBodyType(Config.spineType);
							break;
						}
						case Config.liverType: {
							body[x][y][z].setGoalDosis(Config.liverGoalDose);
							body[x][y][z].setBodyType(Config.liverType);
							break;
						}
						case Config.pancreasType: {
							body[x][y][z].setGoalDosis(Config.pancreasGoalDose);
							body[x][y][z].setBodyType(Config.pancreasType);
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

		BodyAnalyzer ba = new BodyAnalyzer(body, entry.getDimensions());

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
			solver.solveGeneticAlg();
		}
		else if(algo.equals("SA"))
		{
			solver.solveSA();
                        //ToDo: solveSA so modden dass sie Parameter akzeptiert
                        // In meinem Fall MINDESTENS die Seedzahl
                        // Darueber hinaus gibt es nur varianten der
                        // Sum of Squared Differences...
		}

		long end = System.currentTimeMillis();
		TreatmentAnalyzer ta = new TreatmentAnalyzer(Solver.body,
				entry.getDimensions(), Solver.seeds);
		//ta.analyzeAll();
		String filename = "";
		filename += algo + "_";
		filename += Config.numberOfSeeds + "_";
		
		for (double dValue : doubleArgs)
		{
			filename += Double.toString(dValue) + "_";
		}
		filename += ".ser";
		ta.writeToFile(filename);
		ta.printResults();
		// runtime measurement
		
		Date date = new Date(end - start);
		DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
		String dateFormatted = formatter.format(date);
		System.out.println("Runtime: " + dateFormatted);
		
		

		
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

		printTreatmentData();
		planTreatment(algo, doubleArgs);
	}
}

