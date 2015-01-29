package sebastian;

import java.io.Serializable;

import utils.LogTool;
import utils.Seed;

public class TreatmentEntry implements Serializable {
	/* Weights for different body type influences
	   % 1     -> "normal" body    <- low dose
	   % 2     -> spine            <- no dose
       % 3     -> liver            <- low-medium dose
       % 4     -> pancreas         <- no dose
       % 5     -> tumor            <- high dose	    
	 * */
  private static final double[] weights = new double[] {0.0, 1.0, 5.0, 0.5, 5.0, 3.0};
	// Weights for different error influences
  //  1: volume sizes            -> relative
  //  2: volume centers          -> relative
  //  3: tumor center distances  -> absolute
  //  4: tumor closest distances -> absolute
  private static final double[] errors = new double[] {0.1, 0.2, 0.2, 0.5};
  private static final long serialVersionUID = 42L;
  private Seed[] seedPositions = null;
  private String name = "";
  private int[] volumeSizes = null;
  private double[][] volumeCenters = null;
  private double[] tumorCenterDistances = null;
  private double[] tumorClosestDistances = null;
  private double[] coverage = null;
  
  public TreatmentEntry (String name) {
	this.name = name;
  }
  
  public String toString () {
	  String s = "";
		
		s += "TreatmentEntry-------------------\n";
		s += "Name: " + name + "\n";
		if (volumeSizes != null) {
		  s += "Volume sizes: [" + volumeSizes[0];
		  for (int i = 1; i < volumeSizes.length; i++) {
			s += "|" + volumeSizes[i];
		  }
		  s += "]\n";
		}
		if (volumeCenters != null) {
		  s += "Volume centers: [";
		  for (int i = 0; i < volumeCenters.length; i++) {
			s += String.format ("(%.2f|%.2f|%.2f)", volumeCenters[i][0], volumeCenters[i][1], volumeCenters[i][2]);
		  }
		  s += "\n";
		}
		if (tumorCenterDistances != null) {
		  s += "TumorCenterDistances: [" + tumorCenterDistances[0];
		  for (int i = 1; i < tumorCenterDistances.length; i++) {
			s += "|" + tumorCenterDistances[i];
		  }
		  s += "]\n";
		}
		if (tumorClosestDistances != null) {
		  s += "TumorClosestDistances: [" + tumorClosestDistances[0];
		  for (int i = 1; i < tumorClosestDistances.length; i++) {
			s += "|" + tumorClosestDistances[i];
		  }
		  s += "]\n";
		}
		
		return s;
  }
  
  public void setVolumeSizes (int[] volumeSizes) {
	  this.volumeSizes = volumeSizes;
  }
  
  public void setVolumeCenters (double[][] volumeCenters) {
	  this.volumeCenters = volumeCenters;
  }
  
  public void setSeeds (Seed[] seedPositions) {
	  this.seedPositions = seedPositions;
  }
  
  public double[][] getVolumeCenters () {
	  return volumeCenters;
  }
  
  public int[] getVolumeSizes () {
	  return volumeSizes;
  }
    
  public String getName () {
	  return name;
  }
    
  public Seed[] getSeeds () {
	  return seedPositions;
  }

  public void setTumorCenterDistances(double[] tumorCenterDistances) {
	  this.tumorCenterDistances = tumorCenterDistances;
  }
  
  public double[] getTumorCenterDistances () {
	  return tumorCenterDistances;		  
  }
  
  public void setTumorClosestDistances (double[] tumorClosestDistances) {
	  this.tumorClosestDistances = tumorClosestDistances;
  }
  
  public double[] getTumorClosestDistances () {
	  return tumorClosestDistances;
  }

  /**
   * Compares this entry with the given entry.
   * @param tEntry
   * @return Error in percent
   */
  public double compare(TreatmentEntry tEntry) {
	double res = 1;
	double[] sizeErrors = new double[volumeSizes.length];
	double[] centerErrors = new double[volumeCenters.length];
	double[] centerDistsErrors = new double[tumorCenterDistances.length];
	double[] closestDistsErrors = new double[tumorClosestDistances.length];
	
	for (int i = 0; i < sizeErrors.length; i++) {
		// relativer Fehler
		sizeErrors[i] = Math.abs (volumeSizes[i] - tEntry.getVolumeSizes()[i]) / volumeSizes[i];
		// relativer Fehler
		centerErrors[i] = Math.sqrt (Math.pow(volumeCenters[i][0] - tEntry.getVolumeCenters()[i][0], 2) + Math.pow(volumeCenters[i][1] - tEntry.getVolumeCenters()[i][1], 2) + Math.pow(volumeCenters[i][2] - tEntry.getVolumeCenters()[i][2], 2));
		// absoluter Fehler
		centerDistsErrors[i] = Math.abs (tumorCenterDistances[i] - tEntry.getTumorCenterDistances()[i]);
		// absoluter Fehler
		closestDistsErrors[i] = Math.abs (tumorClosestDistances[i] - tEntry.getTumorClosestDistances()[i]);
 	}
	
	// calculate weighted error for comparison
	for (int i = 0; i < sizeErrors.length; i++) {
		res += sizeErrors[i] * weights[i] * errors[0];
		res += centerErrors[i] * weights[i] * errors[1];
		res += centerDistsErrors[i] * weights[i] * errors[2];
		res += closestDistsErrors[i] * weights[i] * errors[3];
	}
	res = res / sizeErrors.length / centerErrors.length / centerDistsErrors.length / closestDistsErrors.length;
	
	/*LogTool.print("Entry " + tEntry.getName(), "debug");
	LogTool.print(String.format("Size: (%f|%f|%f|%f|%f|%f)", sizeErrors[0],sizeErrors[1],sizeErrors[2],sizeErrors[3],sizeErrors[4],sizeErrors[5]), "debug");
	LogTool.print(String.format("Centers: (%f|%f|%f|%f|%f|%f)", centerErrors[0], centerErrors[1],centerErrors[2],centerErrors[3],centerErrors[4],centerErrors[5]), "debug");
	LogTool.print(String.format("CenterDists: (%f|%f|%f|%f|%f|%f)", centerDistsErrors[0], centerDistsErrors[1],centerDistsErrors[2],centerDistsErrors[3],centerDistsErrors[4],centerDistsErrors[5]), "debug");
	LogTool.print(String.format("ClosestDists: (%f|%f|%f|%f|%f|%f)", closestDistsErrors[0], closestDistsErrors[1],closestDistsErrors[2],closestDistsErrors[3],closestDistsErrors[4],closestDistsErrors[5]), "debug");
	LogTool.print (String.format ("Error: %f\n", res), "debug");*/
	return res;
  }
}
