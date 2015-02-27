package laurin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

import erik.BodyAnalyzer;

import utils.Config;
import utils.LogTool;
import utils.Seed;
import utils.Voxel;

/**
 * The TreatmentAnalyzer provides methods to analyze the treatment quality.
 * 
 * @author Laurin Mordhorst
 */

public class TreatmentAnalyzer implements Serializable {
	
	private transient Voxel[][][] body;
	private int[] dimensions;
	private Seed[] seeds;
	private String title;
	
	private double[] minDoses;
	private double[] maxDoses;
	private double[] avgDoses;
	private double conformalityIndex;
	private double homogenityIndex;
	private double coverage;
	private Histogram histogram;
	
	private transient ArrayList<Set<Voxel>> anatomies;
	
	/**
	 * Public Constructor for the TreatmentAnalyzer
	 * 
	 * @param body			 body to be analyzed
	 * @param dimensions	 x,y and z dimensions of the body
	 */
	public TreatmentAnalyzer(Voxel[][][] body, int[] dimensions, Seed[] seeds) 
	{
		this(body, dimensions, seeds, "");
	}
	
	public TreatmentAnalyzer (Voxel[][][] body, int[] dimensions, Seed[] seeds, String title)
	{
		this.body = body;
		this.dimensions = dimensions;
		this.seeds = seeds;
		this.title = title;
		
		this.anatomies = BodyAnalyzer.splitBodyTypes(body);
		irradiate();
		analyzeAll();
	}
	
	public double[] getMinDoses () {
		return minDoses;
	}
	
	public double[] getMaxDoses () {
		return maxDoses;
	}
	
	public double[] getAvgDoses () {
		return avgDoses;
	}
	
	public double getConformalityIndex() {
		return conformalityIndex;
	}
	
	public double getHomogenityIndex() {
		return homogenityIndex;
	}
	
	public double getCoverage() {
		return coverage;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setMinDoses(double[] minDoses) {
		this.minDoses = minDoses;
	}

	public void setMaxDoses(double[] maxDoses) {
		this.maxDoses = maxDoses;
	}
	
	public void setAvgDoses(double[] avgDoses) {
		this.avgDoses = avgDoses;
	}
	
	public void setHistogram(Histogram histogram)
	{
		this.histogram = histogram;
	}
	
	public Set<Voxel> getAnatomy(int bodyType)
	{
		return anatomies.get(bodyType-1);
	}
	
	public void setHomogenityIndex(double homogenityIndex)
	{
		this.homogenityIndex = homogenityIndex;
	}
	
	public void setConformalityIndex(double conformityIndex)
	{
		this.conformalityIndex = conformityIndex;
	}
	
	public void setCoverage(double coverage) 
	{
		this.coverage = coverage;
	}
	
	/**
	 * Calculates dose for each voxel.
	 */
	private void irradiate() {
		for(int x = 0; x < this.dimensions[0]; x++) {				
			for(int y = 0; y < this.dimensions[1]; y++) {		
				for(int z = 0; z < this.dimensions[2]; z++) {
					double dose = 0.0;
					for (Seed seed : seeds)
					{
						//dose += seed.radiationIntensityLUT(body[x][y][z].distanceToVoxel(seed.getCoordinate()), seed.getDurationMilliSec());
						dose += body[x][y][z].radiationIntensityLUT(seed.getCoordinate(), seed.getDurationMilliSec(), 90);
					}
					body[x][y][z].setCurrentDosis(dose);									
				}
			}	
		}
	}
	
	
	
	/**
	 * Calculates average dose for specified body part
	 * 
	 * @param bodyType	body part
	 * @return average dose for body part voxels
	 */
	private double calcAvgDose(int bodyType)
	{
		double averageDose = 0.0;
		
		for (Voxel voxel : anatomies.get(bodyType-1))
			averageDose += voxel.getCurrentDosis();
		
		averageDose /= anatomies.get(bodyType-1).size();
		
		return averageDose;
	}
	
	/**
	 * Finds minimum dose in specified body part
	 * 
	 * @param bodyType	body part
	 * @return minimum dose occurred for body part voxels
	 */
	private double calcMinDose(int bodyType)
	{
		double minDose = Double.MAX_VALUE;
		
		for (Voxel voxel : anatomies.get(bodyType-1))
		{
			if (voxel.getCurrentDosis() < minDose)
				minDose = voxel.getCurrentDosis();
		}
		
		return minDose;
	}
	
	/**
	 * Finds maximum dose in specified body part
	 * 
	 * @param bodyType	body part
	 * @return maximum dose occurred for body part voxels
	 */
	private double calcMaxDose(int bodyType)
	{
		double maxDose = 0.0;
		
		for (Voxel voxel : anatomies.get(bodyType-1))
		{
			if (voxel.getCurrentDosis() > maxDose)
			{
				maxDose = voxel.getCurrentDosis();
			}
		}
		return maxDose;
	}
	
	/**
	 * Calculates homogeneity index.
	 * 
	 * HI specified as maximum dose in tumor divided by the minimum dose.
	 * Great homogeneity results in values close to one.
	 * 
	 * @return homogeneity index
	 */
	private double calcHomogeneityIndex()
	{
		return (this.maxDoses[Config.tumorType-1] / this.minDoses[Config.tumorType-1]);
	}
	
	/**
	 * Calculates coverage.
	 * 
	 * Coverage specified as fraction of PTV voxels whose dose is equal to or higher than the goal dose.
	 * Coverage will be between 0 and 1 and values close to 1 indicate greater coverage.
	 * 
	 * @return coverage
	 */
	private double calcCoverage()
	{
		/* count PTV-voxels whose dose is equal or greater to the prescribed dose */
		int counter = 0;
		Set<Voxel> tumorVoxels = anatomies.get(Config.tumorType - 1);
		for (Voxel voxel : tumorVoxels)
		{
			if (voxel.getCurrentDosis() >= voxel.getGoalDosis())
				counter++;
		}
		
		return (counter / ((double) tumorVoxels.size()));
	}
	
	/**
	 * Calculates conformality index.
	 * 
	 * CI specified as total volume that received at least PTV goal dose over 
	 * PTV volume that received at least the goal dose.
	 * CI close to 1 indicates greater conformality.
	 * 
	 * @return conformality index
	 */
	private double calcConformalityIndex()
	{
		/* count non-PTV voxels whose dose is equal or greater to the prescribed dose */
		int nonPTVCounter = 0;
		for (int i = 0; i < (Config.tumorType - 1); i++)
		{
			Set<Voxel> nonPTVVoxels = anatomies.get(i);
			for (Voxel voxel : nonPTVVoxels)
			{
				if (voxel.getCurrentDosis() >= Config.tumorGoalDose)
					nonPTVCounter++;
			}
		}
		
		/* count PTV-voxels whose dose is equal or greater to the prescribed dose */
		int pTVCounter = 0;
		Set<Voxel> tumorVoxels = anatomies.get(Config.tumorType - 1);
		for (Voxel voxel : tumorVoxels)
		{
			if (voxel.getCurrentDosis() >= Config.tumorGoalDose)
				pTVCounter++;
		}
		
		if (pTVCounter == 0)
			return 0;
		else
			return ((nonPTVCounter + pTVCounter) / ((double) pTVCounter));
	}
	
	/**
	 * Calculates all values needed for treatment evaluation.
	 */
	public void analyzeAll() {
		double[] minDoses = new double[Config.tumorType];
		double[] maxDoses = new double[Config.tumorType];
		double[] avgDoses = new double[Config.tumorType];
		
		LogTool.print("Calculating min, max and avg dose", "Notification");
		for (int i = 0; i < anatomies.size(); i++)
		{
			minDoses[i] = calcMinDose(i+1);
			maxDoses[i] = calcMaxDose(i+1);
			avgDoses[i] = calcAvgDose(i+1);
		}
		setMinDoses(minDoses);
		setMaxDoses(maxDoses);
		setAvgDoses(avgDoses);
		
		LogTool.print("Calculating homogeneity index", "Notification");
		setHomogenityIndex(calcHomogeneityIndex());
		LogTool.print("Calculating conformity index", "Notification");
		setConformalityIndex(calcConformalityIndex());
		LogTool.print("Calculating coverage", "Notification");
		setCoverage(calcCoverage());
		
		LogTool.print("Adding histogram data", "Notification");
		Histogram histogram = new Histogram(this.title);
		histogram.addDataSet("Normal", this.getAnatomy(Config.normalType));
		histogram.addDataSet("Spine", this.getAnatomy(Config.spineType));
		histogram.addDataSet("Liver", this.getAnatomy(Config.liverType));
		histogram.addDataSet("Pancreas", this.getAnatomy(Config.pancreasType));
		histogram.addDataSet("Tumor", this.getAnatomy(Config.tumorType));
		setHistogram(histogram);
	}
	
	/**
	 * Prints all results analyzed.
	 */
	public void printResults() {
		for (int i = 0; i < Config.tumorType - 1; i++)
		{
			LogTool.print("Body type " + i + ": minDose: " + minDoses[i] + ", maxDose: " + maxDoses[i] + ", avgDose: " + avgDoses[i],"notification");
		}
		LogTool.print("Tumor conformality index: " + conformalityIndex, "notification");
		LogTool.print("Tumor homogenity index: " + homogenityIndex, "notification");
		LogTool.print("Tumor coverage: " + coverage, "notification");
		
		LogTool.print("Calculating histogram", "Notification");
		histogram.display(1.0, 5000);
	}
	
	/**
	 * Print tabular comparison of multiple treatments
	 * 
	 * @param treatmentAnalyzers
	 */
	public static void printTreatmentComparison(ArrayList<TreatmentAnalyzer> treatmentAnalyzers, boolean showHistograms)
	{
		String spacing = String.format("%5s","");
		String headLine = String.format("%-15s%s", "Treatment", spacing);
		String minLines[] = new String[Config.tumorType];
		String maxLines[] = new String[Config.tumorType];
		String avgLines[] = new String[Config.tumorType];
		String conformalityLine = String.format("%-15s%s", "Conformality", spacing);
		String homogeinityLine = String.format("%-15s%s", "Homogeinity", spacing);
		String coverageLine = String.format("%-15s%s", "Coverage", spacing);
		DecimalFormat decimalFormat = new DecimalFormat("#####0.0000");
		
		for(int i = 0; i < Config.tumorType; i++)
		{
			minLines[i] = "";
			maxLines[i] = "";
			avgLines[i] = "";
		}
		
		for(int i = 0; i < Config.tumorType; i++)
		{
			minLines[i] += String.format("%-15s%s", Config.bodyTypeDescriptions[i], spacing);
			maxLines[i] += String.format("%-15s%s", Config.bodyTypeDescriptions[i], spacing); 
			avgLines[i] += String.format("%-15s%s", Config.bodyTypeDescriptions[i], spacing); 
		}
		
		for (TreatmentAnalyzer ta : treatmentAnalyzers)
		{
			headLine += String.format("%15s%s", ta.getTitle(), spacing);	
			for(int i = 0; i < Config.tumorType; i++)
			{
				minLines[i] += String.format("%15s%s", decimalFormat.format(ta.getMinDoses()[i]), spacing);
				maxLines[i] += String.format("%15s%s", decimalFormat.format(ta.getMaxDoses()[i]), spacing); 
				avgLines[i] += String.format("%15s%s", decimalFormat.format(ta.getAvgDoses()[i]), spacing);			
			}
			conformalityLine += String.format("%15s%s", decimalFormat.format(ta.getConformalityIndex()), spacing);
			homogeinityLine += String.format("%15s%s", decimalFormat.format(ta.getHomogenityIndex()), spacing);
			coverageLine += String.format("%15s%s", decimalFormat.format(ta.getCoverage()), spacing);
		}
		
		
		System.out.println(headLine);
		System.out.println();
		
		System.out.println("Min doses:");
		for(int i = 0; i < Config.tumorType; i++)
			System.out.println(minLines[i]);
		System.out.println();
		
		System.out.println("Max doses:");
		for(int i = 0; i < Config.tumorType; i++)
			System.out.println(maxLines[i]);
		System.out.println();
		
		System.out.println("Avg doses:");
		for(int i = 0; i < Config.tumorType; i++)
			System.out.println(avgLines[i]);
		System.out.println();
		
		System.out.println(conformalityLine);
		System.out.println(homogeinityLine);
		System.out.println(coverageLine);
		System.out.println();
		
		if (showHistograms)
		{
			for (TreatmentAnalyzer ta : treatmentAnalyzers)
				ta.histogram.display(1.0, 5000);
		}
	}
	
	public static void printTreatmentComparison(ArrayList<TreatmentAnalyzer> treatmentAnalyzers)
	{
		printTreatmentComparison(treatmentAnalyzers, true);
	}
	
	public void writeToFile(String filename)
	{
		try
	    {
			FileOutputStream fileOut = new FileOutputStream(filename);//creates a card serial file in output stream
			ObjectOutputStream out = new ObjectOutputStream(fileOut);//routs an object into the output stream.
			out.writeObject(this);// we designate our array of cards to be routed
			out.close();// closes the data paths
			fileOut.close();// closes the data paths
	    }
		catch(IOException i)//exception stuff
		{
	      i.printStackTrace();
		}
	}
	
	public static TreatmentAnalyzer readFromFile(String filename)
	{
		TreatmentAnalyzer treatmentAnalyzer = null;
		try// If this doesnt work throw an exception
        {
           FileInputStream fileIn = new FileInputStream(filename);// Read serial file.
           ObjectInputStream in = new ObjectInputStream(fileIn);// input the read file.
           treatmentAnalyzer = (TreatmentAnalyzer) in.readObject();// allocate it to the object file already instanciated.
           in.close();//closes the input stream.
           fileIn.close();//closes the file data stream.
       }catch(IOException i)//exception stuff
       {
           i.printStackTrace();
           return null;
       }catch(ClassNotFoundException c)//more exception stuff
       {
           System.out.println("Error");
           c.printStackTrace();
           return null;
       }
	   return treatmentAnalyzer;
	}
	
}
