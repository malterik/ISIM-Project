package laurin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
public class ResultReader {

	/**
	 * @param args
	 */
	
	public static HashMap<TreatmentAnalyzer, String> getFiles(String folderName, String regex)
	{
		ArrayList<TreatmentAnalyzer> treatmentAnalyzers = new ArrayList<TreatmentAnalyzer>();
		
		HashMap<TreatmentAnalyzer, String> treatments = new HashMap<TreatmentAnalyzer, String>();
		
		final File folder = new File(folderName);		
		File[] files = listFilesMatching(folder, regex);

		
		for (File file : files)
		{
			String filename = folderName + file.getName();
            treatments.put(TreatmentAnalyzer.readFromFile(filename), file.getName());
		}
	    
	    return treatments;
	}
	
	public static void compareHistogramsByBodyType(HashMap<TreatmentAnalyzer, String> treatments, String bodyType)
	{
		Histogram histogramComparison = new Histogram("Comparison of " + bodyType);
		
		for (Map.Entry<TreatmentAnalyzer, String> treatment : treatments.entrySet())
		{
			Histogram histogram = treatment.getKey().getHistogram();
			histogramComparison.addDataSet(treatment.getValue(), histogram.getDataSets().get(bodyType));
			HashMap<String, double[]> datasets = histogram.getDataSets();
		}
		
		histogramComparison.display(100, 5000);
	}
	
	public static File[] listFilesMatching(File root, String regex) {
	    if(!root.isDirectory()) {
	        throw new IllegalArgumentException(root+" is no directory.");
	    }
	    final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
	    return root.listFiles(new FileFilter(){
	        @Override
	        public boolean accept(File file) {
	            return p.matcher(file.getName()).matches();
	        }
	    });
	}
	
	public static void printTreatmentComparison(HashMap<TreatmentAnalyzer, String> treatments, boolean showHistograms)
	{
		ArrayList<TreatmentAnalyzer> treatmentAnalyzers = new ArrayList<TreatmentAnalyzer>();
		
		for (Map.Entry<TreatmentAnalyzer, String> treatment : treatments.entrySet())
		{
			treatmentAnalyzers.add(treatment.getKey());
			treatment.getKey().setTitle(treatment.getValue());
		}
		
		TreatmentAnalyzer.printTreatmentComparison(treatmentAnalyzers, showHistograms);
	}
	
	public static void main(String[] args) {
		HashMap<TreatmentAnalyzer, String> treatments = getFiles("treatments/", "LP_50_.*");   
		compareHistogramsByBodyType(treatments, "Rectum");
		printTreatmentComparison(treatments, false);
		
		
	}

}
