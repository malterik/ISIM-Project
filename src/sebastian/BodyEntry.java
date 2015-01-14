package sebastian;

import utils.Voxel;

public class BodyEntry {
	private Voxel[][][] body = null;
	private int[] dims = null;
	private String name = "";
	private int maxType = -1;
	
	@Override
	public String toString () {
		String s = "";
		
		s += "BodyEntry-------------------\n";
		s += "Name: " + name + "\n";
		s += String.format ("Dimensions: [%d|%d|%d]\n", dims[0], dims[1], dims[2]);
		
		return s;
	}
	
	public BodyEntry (String name, Voxel[][][] body, int[] dims, int maxType) {
		this.body = body;
		this.name = name;
		this.dims = dims;
		this.maxType = maxType;
	}
	
	public int getMaxType () {
		return maxType;
	}
	
	public String getName () {
		return name;
	}
	
	public Voxel[][][] getBodyArray () {
		return body;
	}
	
	public int[] getDimensions () {
		return dims;
	}
}
