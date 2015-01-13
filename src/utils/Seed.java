package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * 
 * @author Erik
 *
 */
public class Seed extends Voxel {
	
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
	
	/**
	 * Rearranges seed positions by fitting lines through most influential seeds.
	 * 
	 * Needles (lines) are defined by the the two seeds with the largest remaining dwell times until maximum allowed number of needles is reached.
	 * For each needle the remaining seeds' positions are changed to their perpendicular point on the needle when within a certain distance.
	 *
	 * @param seeds					original seeds
	 * @param numberOfNeedles 		maximum number of needles
	 * @param maxDistanceToLine		allowed distance between seed and line
	 * @param needles				set to hold needles
	 * 	
     * @return seeds in rearranged positions
	 */
	public static Seed[] rearrangeSeeds(Seed[] seeds, int numberOfNeedles, double maxDistanceToLine, Set<Needle> needles)
	{
		ArrayList<Seed> rearrangedSeeds = new ArrayList<Seed>();
		ArrayList<Seed> originalSeeds = new ArrayList<Seed>(Arrays.asList(seeds));
		
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
			System.out.println(originalSeeds.size());
			Needle needle = new Needle();
			if (originalSeeds.size() > 1)
			{
				// create needle from two points with largest dwell times
				Seed seedA = originalSeeds.get(0);
				Seed seedB = originalSeeds.get(1);
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
		
		return rearrangedSeeds.toArray(new Seed[rearrangedSeeds.size()]);
	}

}
