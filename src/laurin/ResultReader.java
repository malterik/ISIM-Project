package laurin;

public class ResultReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TreatmentAnalyzer treatmentAnalyzer = TreatmentAnalyzer.readFromFile("treatments/LP_50_5.0_.ser");
		//TreatmentAnalyzer treatmentAnalyzer = TreatmentAnalyzer.readFromFile("treatments/GA_50_2.0_10.0_0.55_0.8_1.0_1.0_1.0_1.0_1.0_2.0_5.0_.ser");
		treatmentAnalyzer.printResults();
		System.out.println(treatmentAnalyzer.getSAlgorithm());
	}

}
