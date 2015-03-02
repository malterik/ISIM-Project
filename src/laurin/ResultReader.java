package laurin;

import java.io.File;
import java.util.ArrayList;
public class ResultReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*TreatmentAnalyzer treatmentAnalyzer = TreatmentAnalyzer.readFromFile("treatments/LP_50_5.0_.ser");
		//TreatmentAnalyzer treatmentAnalyzer = TreatmentAnalyzer.readFromFile("treatments/GA_50_2.0_10.0_0.55_0.8_1.0_1.0_1.0_1.0_1.0_2.0_5.0_.ser");
		treatmentAnalyzer.printResults();
		System.out.println(treatmentAnalyzer.getSAlgorithm());*/
		
		ArrayList<TreatmentAnalyzer> treatmentAnalyzers = new ArrayList<TreatmentAnalyzer>();
		
		final File folder = new File("treatments/");
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.getName().startsWith("LP")) {
	        	String filename = "treatments/" + fileEntry.getName();
	            treatmentAnalyzers.add(TreatmentAnalyzer.readFromFile(filename));
	        }
	    }
	    
	    TreatmentAnalyzer.printTreatmentComparison(treatmentAnalyzers, true);
	}

}
