package laurin;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

import utils.Config;
import utils.LogTool;
import utils.Seed;
import utils.Voxel;

/**
 * The TreatmentAnalyzer provides methods to analyze the treatment quality.
 * 
 * @author Laurin Mordhorst
 */

public class TreatmentAnalyzer {
	
	private Voxel[][][] body;
	private int[] dimensions;
	private Seed[] seeds;
	
	private double[] minDoses;
	private double[] maxDoses;
	private double[] avgDoses;
	private double conformalityIndex;
	private double homogenityIndex;
	private double coverage;
	private Histogram histogram;
	
	private ArrayList<Set<Voxel>> anatomies;
	
	/**
	 * Public Constructor for the TreatmentAnalyzer
	 * 
	 * @param body			 body to be analyzed
	 * @param dimensions	 x,y and z dimensions of the body
	 */
	public TreatmentAnalyzer(Voxel[][][] body, int[] dimensions, Seed[] seeds) {
		
		this.body = body;
		this.dimensions = dimensions;
		this.seeds = seeds;
		splitBodyTypes();
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
	 * Creates sets of voxels for each body type
	 */
	private void splitBodyTypes () {
		ArrayList<Set<Voxel>> anatomies = new ArrayList<Set<Voxel>>();
		for (int i = Config.normalType; i <= Config.tumorType; i++)
		{
			anatomies.add(new HashSet<Voxel>());
		}
		
		for(int x = 0; x < this.dimensions[0]; x++) {				
			for(int y = 0; y < this.dimensions[1]; y++) {		
				for(int z = 0; z < this.dimensions[2]; z++) {		
					anatomies.get(this.body[x][y][z].getBodyType()-1).add(body[x][y][z]);									
				}
			}	
		}
		
		this.anatomies = anatomies;
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
						dose += body[x][y][z].radiationIntensity(seed.getCoordinate(), seed.getDurationMilliSec());
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
				maxDose = voxel.getCurrentDosis();
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
		Histogram histogram = new Histogram("Dose Volume Histogram");
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
		for (int i = 0; i < anatomies.size(); i++)
		{
			LogTool.print("Body type: " + i + ": minDose: " + minDoses[i] + ", maxDose: " + maxDoses[i] + ", avgDose: " + avgDoses[i],"notification");
		}
		LogTool.print("Tumor conformality index: " + conformalityIndex, "notification");
		LogTool.print("Tumor homogenity index: " + homogenityIndex, "notification");
		LogTool.print("Tumor coverage: " + coverage, "notification");
		
		LogTool.print("Calculating histogram", "Notification");
		histogram.display(1.0, 100);
	}
	
}