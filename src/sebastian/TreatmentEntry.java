package sebastian;

import java.io.Serializable;
import java.util.ArrayList;

import utils.Voxel;
import utils.Coordinate;

public class TreatmentEntry implements Serializable {
  private static final long serialVersionUID = 42L;
  private Voxel[][][] bodyArray = null;
  private int[] dims = null;
  private ArrayList<Coordinate> seedPositions = null;
  private int id = -1;
  
  public TreatmentEntry(Voxel[][][] bodyArray, int[] dims, ArrayList<Coordinate> seedPositions) {
	  this.bodyArray = bodyArray;
	  this.dims = dims;
	  this.seedPositions = seedPositions;
	  this.id = SimpleDB.getID ();
  }
  
  public Voxel[][][] getBodyArray () {
	  return bodyArray;
  }
  
  public ArrayList<Coordinate> getSeeds () {
	  return seedPositions;
  }
  
  public int[] getDimensions () {
	  return dims;
  }
  
  public int getID () {
	  return id;
  }
}
