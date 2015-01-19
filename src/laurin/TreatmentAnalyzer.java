package laurin;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

import utils.Config;
import utils.LogTool;
import utils.Seed;
import utils.Voxel;

public class TreatmentAnalyzer {
	
	private Voxel[][][] body;
	private int[] dimensions;
	private Seed[] seeds;
	
	private double[] minDoses;
	private double[] maxDoses;
	private double[] avgDoses;
	private double conformityIndex;
	private double homogenityIndex;
	
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
	}
	
	/**
	 * Calculates all values needed for treatment evaluation.
	 */
	public void analyzeAll() {
		double[] minDoses = new double[Config.tumorType];
		double[] maxDoses = new double[Config.tumorType];
		double[] avgDoses = new double[Config.tumorType];
		
		for (int i = 0; i < anatomies.size(); i++)
		{
			minDoses[i] = calcMinDose(i+1);
			maxDoses[i] = calcMaxDose(i+1);
			avgDoses[i] = calcAvgDose(i+1);
		}
		
		setMinDoses(minDoses);
		setMaxDoses(maxDoses);
		setAvgDoses(avgDoses);
		setHomogenityIndex(calcHomogenityIndex());
		setConformityIndex(calcConformityIndex());
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
	
	public double getConformityIndex() {
		return conformityIndex;
	}
	
	public double getHomogenityIndex() {
		return homogenityIndex;
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
	
	public void setHomogenityIndex(double homogenityIndex)
	{
		this.homogenityIndex = homogenityIndex;
	}
	
	public void setConformityIndex(double conformityIndex)
	{
		this.conformityIndex = conformityIndex;
	}

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
	
	private double calcAvgDose(int bodyType)
	{
		double averageDose = 0.0;
		
		for (Voxel voxel : anatomies.get(bodyType-1))
			averageDose += voxel.getCurrentDosis();
		
		averageDose /= anatomies.get(bodyType-1).size();
		
		return averageDose;
	}
	
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
	 * Calculates homogenity index.
	 * 
	 * HI specified as maximum dose in tumor divided by goal dose.
	 * Great homogenity results in values close to one.
	 * 
	 * @return homogenity index
	 */
	private double calcHomogenityIndex()
	{
		return (this.maxDoses[Config.tumorType-1] / Config.tumorGoalDose);
	}
	
	/**
	 * Calculates conformity index.
	 * 
	 * CI specified as fraction of voxels whose dose is equal to or higher than the goal dose.
	 * CI will be between 0 and 1.
	 * 
	 * @return homogenity index
	 */
	private double calcConformityIndex()
	{
		int counter = 0;
		Set<Voxel> tumorVoxels = anatomies.get(Config.tumorType - 1);
		
		for (Voxel voxel : tumorVoxels)
		{
			if (voxel.getCurrentDosis() >= voxel.getGoalDosis())
				counter++;
		}
		
		return (counter / (double) tumorVoxels.size());
	}
	
	public void printResults () {
		for (int i = 0; i < anatomies.size(); i++)
		{
			LogTool.print("Body type: " + i + ": minDose: " + minDoses[i] + ", maxDose: " + maxDoses[i] + ", avgDose: " + avgDoses[i],"notification");
			LogTool.print("Tumor conformity index: " + conformityIndex, "notification");
			LogTool.print("Tumor homogenity index: " + homogenityIndex, "notification");
		}
	}
	
}
