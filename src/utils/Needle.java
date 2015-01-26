package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import erik.BodyAnalyzer;

public class Needle implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Seed> seeds;
	
	public Needle()
	{
		seeds = new ArrayList<Seed>();
	}
	
	public ArrayList<Seed> getSeeds()
	{
		return seeds;
	}
	
	public void addSeed(Seed seed)
	{
		seeds.add(seed);
	}
	
	public int getSize()
	{
		return this.seeds.size();
	}
	
	/**
	 * Calculates direction of needle as difference between second and first seeds' coordinates.
	 * 
	 * @return direction of needle
	 */
	public Vector3D getDirection()
	{
		if (this.getSize() < 2)
			return null;
		else
		{
			// subtract second coordinate from first
			return (seeds.get(1).getCoordinate().ToVector()).subtract(seeds.get(0).getCoordinate().ToVector());
		}
	}
	
	/**
	 * Create array of seeds from needles
	 * 
	 * All seeds in list of needles will be placed in a single array.
	 * 
	 * @param needles 		needles whose seeds shall be collected
	 * 
	 * @return				seeds
	 */
	public static Seed[] needlesToSeeds(ArrayList<Needle> needles)
	{
		Set<Seed> seeds = new HashSet<Seed>();
		
		for (Needle needle : needles)
		{
			for (Seed seed : needle.getSeeds())
			{
				seeds.add(seed);
			}
		}
		return seeds.toArray(new Seed[seeds.size()]);
	}
	
	/**
	 * Gets best needles from set of needles.
	 * 
	 * The best needles are specified as the numNeedles needles 
	 * with the largest sum of its assigned seeds' dwell times.
	 * 
	 * @param numNeedles      number of best needles to be returned
	 * 
	 * @return best needles
	 */
	public static ArrayList<Needle> getBestNeedles(ArrayList<Needle> needles, int numNeedles )
	{
		ArrayList<Needle> bestNeedles = new ArrayList<Needle>();

		Collections.sort(needles, new Comparator<Needle>() {
		    public int compare(Needle n1, Needle n2) {    	
		    	Double needleSum1 = new Double(0.0);
		    	Double needleSum2 = new Double(0.0);
		    	for (Seed seed : n1.getSeeds())
		    		needleSum1 += seed.getDurationMilliSec();
		    	for (Seed seed : n2.getSeeds())
		    		needleSum2 += seed.getDurationMilliSec();
	
		        return needleSum2.compareTo(needleSum1);
		    }
		});
		
		int maxNeedle = (numNeedles > needles.size()) ? (needles.size()) : numNeedles;
		for (int i = 0; i < maxNeedle; i++)
			bestNeedles.add(needles.get(i));
		
		return bestNeedles;
	}
	
	/**
	 * Creates random needles with seeds through tumor.
	 * 
	 * Needles are created so that:
	 * 		- each needle holds at least minSeedsPerNeedle seeds
	 * 		- the spacing between seeds on needle is seedSpacing
	 * 		- the sum of all seeds on the created needles is numSeeds
	 * 
	 * @param body					array of body voxels
	 * @param xBounds				x bounds of smallest cuboid around tumor
	 * @param yBounds				y bounds of smallest cuboid around tumor
	 * @param zBounds				z bounds of smallest cuboid around tumor
	 * @param numSeeds				number of seeds to place
	 * @param minSeedsPerNeedle		minimum number of seeds per needle
	 * @param seedSpacing			spacing between seeds
	 * 
	 * @return needles that hold seeds
	 */
	public static ArrayList<Needle> createRandomNeedles(Voxel[][][] body, int[] xBounds, int[] yBounds, int[] zBounds, int numSeeds, int minSeedsPerNeedle, double seedSpacing)
	{
		ArrayList<Needle> needles = new ArrayList<Needle>();
		ArrayList<Set<Voxel>> bodyParts = BodyAnalyzer.splitBodyTypes(body);
		Voxel[] tumorOutterVoxels = BodyAnalyzer.getOutterVoxels(body, bodyParts.get(Config.tumorType - 1)); // tumor surface voxels
		
		int seedCount = 0;
		while (seedCount < numSeeds)
		{
			// pick two random points from tumor surface
			Coordinate a = tumorOutterVoxels[RandGenerator.randInt(0, tumorOutterVoxels.length - 1)].getCoordinate();
			Coordinate b = tumorOutterVoxels[RandGenerator.randInt(0, tumorOutterVoxels.length - 1)].getCoordinate();
			
			// set first seed position to somewhere between the surface point and seed spacing
			Coordinate base = Coordinate.getPointOnLine(a, b, RandGenerator.randDouble(0, seedSpacing));
			
			// create needle
			Needle needle = new Needle();
			
			// create seed when either
			// - distance between first point and other point on tumor surface allows to place at least minSeedsPerNeedle
			// - less than minSeedsPerNeedle are required to reach numSeeds
			double distance = base.distanceToCoordiante(b);
			if ((distance > (minSeedsPerNeedle * seedSpacing)) || ((numSeeds - seedCount) < minSeedsPerNeedle))
			{
				for (double d = 0; d < distance; d += seedSpacing )
				{
					Coordinate next = Coordinate.getPointOnLine(base, b, d);
					
					// tumor possibly not convex
					if (body[(int) Math.round(next.getX())][(int) Math.round(next.getY())][(int) Math.round(next.getZ())].getBodyType() == Config.tumorType)
					{
						Seed seed = new Seed(next.getX(), next.getY(), next.getZ(), 0);
						needle.addSeed(seed);
						seed.setNeedle(needle);
					}
				}
				
				// add needle to collection of needles if it holds enough seeds
				if ((needle.getSize() > minSeedsPerNeedle)  || ((numSeeds - seedCount) < minSeedsPerNeedle))
				{
					needles.add(needle);
					seedCount += needle.getSize();
				}
			}
		}
		return needles;
	}
}
