package erik;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import utils.Config;
import utils.Voxel;

/**
 * The Body Analyzer provides methods to get specific values for the body array, e.g. tumor size, tumor center of mass etc...
 * @author Erik Schroeder
 *
 */
public class BodyAnalyzer {
	
	private Voxel[][][] body;
	private int[] dimensions;
	
	private int[] xBoundsTumor;
	private int[] yBoundsTumor;
	private int[] zBoundsTumor;
	
	private int voxelCountTumor;
	
	/**
	 * Public Constructor for the BodyAnalyzer
	 * @param body
	 * The body array which should be analayzed
	 * @param dimensions
	 * the x,y and z dimensions for the body array
	 */
	public BodyAnalyzer(Voxel[][][] body, int[] dimensions) {
		
		this.body = body;
		this.dimensions = dimensions;
		this.setVoxelCountTumor(0);
		
		analyzeBody();
		
	}
	
	/**
	 * All analyzation should be done here.
	 */
	private void analyzeBody() {
		
		int xMin, yMin, zMin;
		int xMax, yMax, zMax;
		
		xMin=yMin=zMin=Integer.MAX_VALUE;
		xMax=yMax=zMax=0;
		
		for(int x = 0; x < dimensions[0]; x++) {
			
			for(int y = 0; y < dimensions[1]; y++) {
				
				for(int z = 0; z < dimensions[2]; z++) {
					
					switch(body[x][y][z].getBodyType())
					{
						case Config.normalType:
						{
							
							break;
						}
						case Config.spineType:
						{
							
							break;
						}
						case Config.liverType:
						{
							
							break;
						}
						case Config.pancreasType:
						{
							
							break;
						}
						case Config.tumorType:
						{
							
							setVoxelCountTumor(getVoxelCountTumor() + 1);
							
							/* update the lower boundaries */
							if(x < xMin){
								xMin = x;
							}
							if(y < yMin){
								yMin = y;
							}
							if(z < zMin){
								zMin = z;
							}
							
							if(x > xMax) {
								xMax = x;
							}
							if(y > yMax) {
								yMax = y;
							}
							
							if(z > zMax) {
								zMax = z;
							}
							
							
							break;
						}
						default:
						{
							
							break;
						}	
					}
					
				}
			}
		}
		
		setxBoundsTumor(new int[] {xMin, xMax});
		setyBoundsTumor(new int[] {yMin, yMax});
		setzBoundsTumor(new int[] {zMin, zMax});
		
	}

	/**
	 * Provides 2 different Types of Bounds.
	 * @param type
	 * 
	 * 1 : The actual bounds of the tumor
	 * 2 : The bounds of the region which should be treated
	 * @return
	 * The type of bounds which are selected or null if something went wrong. 
	 * result[0] = lower bound
	 * result[1] = upper bound
	 */
	public int[] getxBoundsTumor(int type) {
		int lowerBound,upperBound;
		int [] result = {0,0};
		
		lowerBound = upperBound = 0;
		
		if(type == 1) {
			return xBoundsTumor;
		} else if ( type == 2) {
		
			lowerBound = xBoundsTumor[0] - (int) (Config.treatmentRange/Voxel.GRID_RESOLUTION);
			upperBound = xBoundsTumor[1] + (int) (Config.treatmentRange/Voxel.GRID_RESOLUTION);
			
			if(lowerBound > 0) {
				result[0] = lowerBound;
			} else {
				result[0] = 0;
			}
			
			if(upperBound < dimensions[0]) {
				result[1] = upperBound;
			} else {
				result[1] = dimensions[0];
			}
			
			return result;
				
			
		} 
		
		return null;
		
	}

	public void setxBoundsTumor(int[] xBoundsTumor) {
		this.xBoundsTumor = xBoundsTumor;
	}
	
	/**
	 * Provides 2 different Types of Bounds
	 * @param type
	 * 
	 * 1 : The actual bounds of the tumor
	 * 2 : The bounds of the region which should be treated
	 * @return
	 * The type of bounds which are selected
	 */
	public int[] getyBoundsTumor(int type) {
		int lowerBound,upperBound;
		int [] result = {0,0};
		
		if(type == 1) {
			return yBoundsTumor;
		} else if ( type == 2) {
		
			lowerBound = yBoundsTumor[0] - (int) (Config.treatmentRange/Voxel.GRID_RESOLUTION);
			upperBound = yBoundsTumor[1] + (int) (Config.treatmentRange/Voxel.GRID_RESOLUTION);
			
			if(lowerBound > 0) {
				result[0] = lowerBound;
			} else {
				result[0] = 0;
			}
			
			if(upperBound < dimensions[1]) {
				result[1] = upperBound;
			} else {
				result[1] = dimensions[1];
			}
			
			return result;
				
			
		} 
		
		return null;
	}

	public void setyBoundsTumor(int[] yBoundsTumor) {
		this.yBoundsTumor = yBoundsTumor;
	}
	
	/**
	 * Provides 2 different Types of Bounds
	 * @param type
	 * 
	 * 1 : The actual bounds of the tumor
	 * 2 : The bounds of the region which should be treated
	 * @return
	 * The type of bounds which are selected
	 */
	public int[] getzBoundsTumor(int type) {
		int lowerBound,upperBound;
		int [] result = {0,0};
		
		if(type == 1) {
			return zBoundsTumor;
		} else if ( type == 2) {
		
			lowerBound = zBoundsTumor[0] - (int) (Config.treatmentRange/Voxel.GRID_RESOLUTION);
			upperBound = zBoundsTumor[1] + (int) (Config.treatmentRange/Voxel.GRID_RESOLUTION);
			
			if(lowerBound > 0) {
				result[0] = lowerBound;
			} else {
				result[0] = 0;
			}
			
			if(upperBound < dimensions[2]) {
				result[1] = upperBound;
			} else {
				result[1] = dimensions[2];
			}
			
			return result;
				
			
		} 
		
		return null;
	}

	public void setzBoundsTumor(int[] zBoundsTumor) {
		this.zBoundsTumor = zBoundsTumor;
	}

	public int getVoxelCountTumor() {
		return voxelCountTumor;
	}

	public void setVoxelCountTumor(int voxelCountTumor) {
		this.voxelCountTumor = voxelCountTumor;
	}
	
	/**
	 * Creates sets of voxels that share the same body type.
	 * 
	 * @param body array of body voxels
	 * 
	 * @return ArrayList of sets of voxels that share a body type
	 */
	public static ArrayList<Set<Voxel>> splitBodyTypes (Voxel[][][] body) {
		ArrayList<Set<Voxel>> anatomies = new ArrayList<Set<Voxel>>();
		for (int i = Config.normalType; i <= Config.tumorType; i++)
		{
			anatomies.add(new HashSet<Voxel>());
		}
		
		for(int x = 0; x < body.length; x++) {				
			for(int y = 0; y < body[0].length; y++) {		
				for(int z = 0; z < body[0][0].length; z++) {		
					anatomies.get(body[x][y][z].getBodyType()-1).add(body[x][y][z]);									
				}
			}	
		}	
		return anatomies;
	}
	
	/**
	 * Checks if voxel is outter voxel.
	 * An outter voxel is a voxel that has at least one neighboring pixel (in 6-neighborhood) that either
	 * 		- exceeds global body dimensions
	 * 		- is of another body type
	 * 
	 * @return true if voxel is outter voxel,
	 * 		   false otherwise
	 **/
	private static boolean isOutterVoxel(Voxel[][][] body, Voxel voxel)
	{
		int x = (int) voxel.getX();
		int y = (int) voxel.getY();
		int z = (int) voxel.getZ();
		int bodyType = voxel.getBodyType();
		
		// voxel is outter body pixel
		if (x-1 < 0 
				|| x+1 > body.length
				|| y-1 < 0
				|| y+1 > body[0].length
				|| z-1 < 0
				|| z+1 > body[0][0].length)
		{
			return true;
		}
		
		// voxel is on body part surface
		if (body[x-1][y][z].getBodyType() != bodyType
				|| body[x+1][y][z].getBodyType() != bodyType
				|| body[x][y-1][z].getBodyType() != bodyType
				|| body[x][y+1][z].getBodyType() != bodyType
				|| body[x][y][z-1].getBodyType() != bodyType
				|| body[x][y][z+1].getBodyType() != bodyType)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Finds the outter voxels of a given body part.
	 * 
	 * @param body		array of body voxels
	 * @param voxels 	set of voxels of ONE body part
	 * 
	 * @return			outter voxels of body part
	 */
	public static Voxel[] getOutterVoxels(Voxel[][][]body, Set<Voxel> voxels) {
		Set<Voxel> outterVoxels = new HashSet<Voxel>();
		for (Voxel voxel : voxels)
		{
			if (isOutterVoxel(body, voxel))
			{
				outterVoxels.add(voxel);
			}
		}
		return (outterVoxels.toArray(new Voxel[outterVoxels.size()]));
	}
}
