package sebastian;

import java.io.Serializable;
import java.util.ArrayList;

import utils.Coordinate;

public class TreatmentEntry implements Serializable {
  private static final long serialVersionUID = 42L;
  private ArrayList<Coordinate> seedPositions = null;
  private String name = "";
  private int[] volumeSizes = null;
  private double[][] volumeCenters = null;
  private double[] tumorCenterDistances = null;
  private double[] tumorClosestDistances = null;
  
  public TreatmentEntry (String name) {
	seedPositions = new ArrayList<Coordinate> ();
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
  
  public void setSeeds (ArrayList<Coordinate> seedPositions) {
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
    
  public ArrayList<Coordinate> getSeeds () {
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
}
