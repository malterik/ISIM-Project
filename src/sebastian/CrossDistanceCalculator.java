package sebastian;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import utils.Coordinate;

public class CrossDistanceCalculator implements Callable<double[]> {
    private BodyEntry body = null;
    private int z, max;
    private ArrayList<double[]> tumor = null;
    private double[] partRes = null;
	
	
	public CrossDistanceCalculator (BodyEntry body, int z, int max, ArrayList<double[]> tumor) {
		this.body = body;
		this.tumor = tumor;
		this.z = z;
		this.max = max;
		partRes = new double[body.getMaxType() + 1];
		for (int i = 0; i < partRes.length; i++) {
			partRes[i] = 50000;
		}
	}
	
	private double getDistance(Coordinate coordinate, double[] voxel2) {
		double[] voxel1 = new double[] {coordinate.getX(), coordinate.getY(), coordinate.getZ()};
		return getDistance (voxel1, voxel2);
	  }
	
	/**
	   * Calculates the distance between two volume centers (vc) by euclidian norm
	   * @param vc1 volume center 1
	   * @param vc2 volume center 2
	   * @return distance as double
	   */
	  private double getDistance(double[] vc1, double[] vc2) {
		  double d = -1;
		
		  if (vc1.length == 3 && vc2.length == 3) {
			  d = Math.sqrt ((vc1[0]-vc2[0])*(vc1[0]-vc2[0])+(vc1[1]-vc2[1])*(vc1[1]-vc2[1])+(vc1[2]-vc2[2])*(vc1[2]-vc2[2]));
		  }
		  
		  
		  return d;
	  }
	
	private void crossCalculate () {
		int type = -1;
		double dist = -1;
		
		for (int i = 0; i < body.getDimensions()[0]; i++) {
			for (int j = 0; j < body.getDimensions()[1]; j++) {
					for (int l = 0; l < tumor.size(); l++) {
						type = body.getBodyArray()[i][j][z].getBodyType();
					    dist = getDistance (body.getBodyArray()[i][j][z].getCoordinate(), tumor.get(l));
					    if (partRes[type] > dist) {
						  partRes[type] = dist;
					    }
					}
			}
		}
	}
	
	@Override
	public double[] call() throws Exception {
		System.out.println(z + "/" + max);
		crossCalculate ();		
		return partRes;
	}
}
