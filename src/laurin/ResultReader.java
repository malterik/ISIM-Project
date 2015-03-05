package laurin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * ResultReader
 * 
 * Provides methods for comparison of results
 * @author Laurin Mordhorst
 */
public class ResultReader {

	/**
	 * Get files in folder that match regular expression.
	 * 
	 * @param folderName 	name of folder relative to workspace
	 * @param regex			regular expression
	 * 
	 * @return filenames and corresponding TreatmentAnalyzers
	 */
	public static HashMap<TreatmentAnalyzer, String> getFiles(String folderName, String regex)
	{
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
	
	/**
	 * Create Histogram for given body part from multiple treatment analyzers
	 * 
	 * @param treatments		set of treatments
	 * @param bodyType			body type
	 */
	public static void compareHistogramsByBodyType(HashMap<TreatmentAnalyzer, String> treatments, String bodyType)
	{
		Histogram histogramComparison = new Histogram("Comparison of " + bodyType);
		
		for (Map.Entry<TreatmentAnalyzer, String> treatment : treatments.entrySet())
		{
			Histogram histogram = treatment.getKey().getHistogram();
			histogramComparison.addDataSet(treatment.getValue(), histogram.getDataSets().get(bodyType));
		}
		
		histogramComparison.display(1, 5000);
	}
	
	private static File[] listFilesMatching(File root, String regex) {
	    if(!root.isDirectory()) {
	        throw new IllegalArgumentException(root+" is no directory.");
	    }
	    final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
	    return root.listFiles(new FileFilter(){
	        public boolean accept(File file) {
	            return p.matcher(file.getName()).matches();
	        }
	    });
	}
	
	/**
	 * Print comparison of treatments
	 * 
	 * @param treatments		set of treatments
	 * @param showHistograms	if true, histograms will be displayed
	 */
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
		HashMap<TreatmentAnalyzer, String> treatments = getFiles("treatments/", "LP_100_[0-3][.]{1}.*");   
		compareHistogramsByBodyType(treatments, "Tumor");
		compareHistogramsByBodyType(treatments, "Urethra");
		compareHistogramsByBodyType(treatments, "Rectum");
		compareHistogramsByBodyType(treatments, "Bladder");
		printTreatmentComparison(treatments, false);
	}

}
