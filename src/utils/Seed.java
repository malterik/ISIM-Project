package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import erik.BodyAnalyzer;

/**
 * 
 * @author Erik
 *
 */
public class Seed extends Voxel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Needle needle;
	
	public Seed(double x, double y, double z, double durationMilliSec) {
		super(x, y, z);
		this.durationMilliSec = durationMilliSec;
		
	}
	private double durationMilliSec = 0;
	
	
	
	public double getDurationMilliSec() {
		return durationMilliSec;
	}
	public void setDurationMilliSec(double durationMilliSec) {
		this.durationMilliSec = durationMilliSec;
	}
	
	public void setNeedle(Needle needle)
	{
		this.needle = needle;
	}
	
	public Needle getNeedle()
	{
		return needle;
	}
		
	public static ArrayList<Needle> rearrangeSeeds(Seed[] seeds, int numberOfNeedles, Voxel[][][] body, double maxDistanceToLine)
	{
		ArrayList<Seed> rearrangedSeeds = new ArrayList<Seed>();
		ArrayList<Seed> originalSeeds = new ArrayList<Seed>(Arrays.asList(seeds));
		ArrayList<Needle> needles = new ArrayList<Needle>();
		Voxel[] tumorVoxels = BodyAnalyzer.getOutterVoxels(body, BodyAnalyzer.splitBodyTypes(body).get(Config.tumorType-1));
		
		// remove seeds with zero dwell time
		for (Iterator<Seed> iterator = originalSeeds.iterator(); iterator.hasNext(); ) {
			Seed seed = iterator.next();		    	
	
			if (seed.getDurationMilliSec() == (long) 0)
				iterator.remove();
		}
		
		// sort seeds by descending dwell times
		Collections.sort(originalSeeds, new Comparator<Seed>() {
		    public int compare(Seed s1, Seed s2) {
		    	Double l1 = new Double(s1.getDurationMilliSec());
		    	Double l2 = new Double(s2.getDurationMilliSec());
		        return l2.compareTo(l1);
		    }
		});
		
		
		int needleCounter = 0;
		
		// create needles until no are seeds left or maximum needle number reached
		while (originalSeeds.size() > 0 && needleCounter < numberOfNeedles)
		{
			Needle needle = null;
			if (originalSeeds.size() > 1)
			{
				// create needle from two points with largest dwell times
				Seed seedA = originalSeeds.get(0);
				Seed seedB = originalSeeds.get(1);
				needle = new Needle(seedA, seedB, body);
				needle.addSeed(seedA);
				needle.addSeed(seedB);
				seedA.setNeedle(needle);
				seedB.setNeedle(needle);
		    	rearrangedSeeds.add(seedA);
		    	rearrangedSeeds.add(seedB);
				Vector3D p1 = new Vector3D(seedA.getX(), seedA.getY(), seedA.getZ());
		    	Vector3D p2 = new Vector3D(seedB.getX(), seedB.getY(), seedB.getZ());
		    	
		    	// remove seeds from set
		    	originalSeeds.remove(0);
		    	originalSeeds.remove(0);
		    	
		    	// add seeds within maximum allowed distance to needle
				for (Iterator<Seed> iterator = originalSeeds.iterator(); iterator.hasNext(); ) {
					Seed seed = iterator.next();
			  
					// calculate perpendicular point
					Vector3D q = new Vector3D(seed.getX(), seed.getY(), seed.getZ());
					Vector3D u = p2.subtract(p1);
			    	Vector3D pq = q.subtract(p1);
			    	Vector3D w2 = pq.subtract(u.scalarMultiply(Vector3D.dotProduct(pq, u) / Math.pow(u.getNorm(),2)));
			    	Vector3D perpendicularPoint = q.subtract(w2);
			    	
			    	// add point if within allowed distance and set cooridnate to perpendicular point coordinate
					if (perpendicularPoint.distance(q) < maxDistanceToLine)
					{
						Coordinate coordinate = new Coordinate((int) Math.round(perpendicularPoint.getX()), (int) Math.round(perpendicularPoint.getY()), (int) Math.round(perpendicularPoint.getZ()));
						seed.setCoordinate(coordinate);
						needle.addSeed(seed);
						seed.setNeedle(needle);
						rearrangedSeeds.add(seed);		
						iterator.remove();
					}
				}
			}
			else
			{
		    	// create needle from single point
				rearrangedSeeds.add(originalSeeds.get(0));
				needle.addSeed(originalSeeds.get(0));
				originalSeeds.get(0).setNeedle(needle);
		    	originalSeeds.remove(0);
			}
			needles.add(needle);
			needleCounter++;
		}
		
		return needles;
	}
	
	
	
	/**
	 * Calculates the angle (phi) for dose calculation
	 * 
	 * The angle can only be calculated if the seed is assigned to a needle.
	 * 
	 * @param voxel		voxel for which angle shall be determined
	 * @return			angle in degrees
	 */
	public double getPhi(Voxel voxel)
	{
		if (this.needle == null)
			return 90;
		else
		{
			Vector3D vNeedle = needle.getTumorSurfacePointB().getCoordinate().ToVector().subtract(needle.getTumorSurfacePointB().getCoordinate().ToVector());
			Vector3D vLineSeedToVoxel = (voxel.getCoordinate().ToVector()).subtract(this.getCoordinate().ToVector());
			return (Vector3D.angle(vNeedle, vLineSeedToVoxel) * 180 * Math.PI);
		}
	}

}
