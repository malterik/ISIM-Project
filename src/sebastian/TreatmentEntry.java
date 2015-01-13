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
  
  public TreatmentEntry (String name) {
	seedPositions = new ArrayList<Coordinate> ();
	this.name = name;
  }
  
  public String toString () {
	  String s = "";
		
		s += "TreatmentEntry-------------------\n";
		s += "Name: " + name + "\n";
		s += "Volume sizes: [" + volumeSizes[0];
		for (int i = 1; i < volumeSizes.length; i++) {
			s += "|" + volumeSizes[i];
		}
		s += "]\nVolume centers: [";
		for (int i = 0; i < volumeCenters.length; i++) {
			s += String.format ("(%.2f|%.2f|%.2f)", volumeCenters[i][0], volumeCenters[i][1], volumeCenters[i][2]);
		}
		s += "\n";
		
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
}
