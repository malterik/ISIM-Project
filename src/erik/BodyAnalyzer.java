package erik;

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

}
