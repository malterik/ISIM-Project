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
	private Voxel tumorSurfacePointA;
	private Voxel tumorSurfacePointB;
	
	private Needle()
	{
		this.seeds = new ArrayList<Seed>();
	}
	
	public Needle(Voxel insideTumor, Voxel outsideTumor, Voxel[][][] body)
	{
		this();
		setTumorSurfacePointsFromVoxels (insideTumor, outsideTumor, body);
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
	
	public Voxel getTumorSurfacePointA() 
	{
		return tumorSurfacePointA;
	}
	
	public Voxel getTumorSurfacePointB() 
	{
		return tumorSurfacePointB;
	}
	
	/**
	 * Sorts seeds by their distance from the first intersection with the tumor surface
	 */
	public void sortSeedsByDistanceToTumorSurfacePointA() 
	{
		Collections.sort(this.seeds, new Comparator<Seed>() {
		    public int compare(Seed s1, Seed s2) {    
		    	Needle needle = s1.getNeedle();
		    	Double s1Dist = new Double(s1.distanceToVoxel(needle.tumorSurfacePointA.getCoordinate()));
		    	Double s2Dist = new Double(s1.distanceToVoxel(needle.tumorSurfacePointA.getCoordinate()));
		        return s1Dist.compareTo(s2Dist);
		    }
		});
	}
	
	/**
	 * Calculates the needles' intersecting points with the tumor from one point inside the tumor and one point outside
	 * @param insideVoxel
	 * @param otherVoxel
	 * @param body
	 */
	private void setTumorSurfacePointsFromVoxels (Voxel insideVoxel, Voxel otherVoxel, Voxel[][][] body)
	{	
		final double STEP_SIZE = 0.1;
		Coordinate coordinate = insideVoxel.getCoordinate();
		Coordinate other = otherVoxel.getCoordinate();
		Vector3D vDirection = other.ToVector().subtract(coordinate.ToVector());
		
		
		while (voxelInBodyType(body, coordinate, Config.tumorType))
		{
			coordinate = Coordinate.getPointOnLine(coordinate, vDirection, STEP_SIZE);
		}
		
		this.tumorSurfacePointA = new Voxel(coordinate.getX(), coordinate.getY(), coordinate.getZ());
		
		coordinate = insideVoxel.getCoordinate();
		while (voxelInBodyType(body, coordinate, Config.tumorType))
		{	
			coordinate = Coordinate.getPointOnLine(coordinate, vDirection, (-1.0 * STEP_SIZE));
		}
		
		this.tumorSurfacePointB = new Voxel(coordinate.getX(), coordinate.getY(), coordinate.getZ());
	}
	
	/**
	 * Checks if coordinate is of body type bodyType
	 * 
	 * @param body
	 * @param c
	 * @param bodyType
	 * @return
	 */
	public static boolean voxelInBodyType(Voxel[][][] body, Coordinate c, int bodyType)
	{
		int iX = (int) Math.round(c.getX());
		int iY = (int) Math.round(c.getY());
		int iZ = (int) Math.round(c.getZ());
		
		if (coordinateInBounds(body, iX, iY, iZ))
		{
			if (body[iX][iY][iZ].getBodyType() == bodyType)
			{
				return true;
			}
			return false;
		}
		return false;
	}
	
	/*
	 * Checks if coordinate is in bounds of body
	 */
	public static boolean coordinateInBounds(Voxel[][][] body, int x, int y, int z)
	{
		if (x >= 0
				&& x < body.length
				&& y >= 0
				&& y < body[0].length
				&& z >= 0
				&& z < body[0][0].length)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Creates a seed at a random position within a needles intersections with the tumor
	 * @return
	 */
	private Seed createRandomSeedInTumor()
	{
		double distance = this.getTumorSurfacePointA().distanceToVoxel(this.getTumorSurfacePointB().getCoordinate());
		Vector3D vDirection = this.tumorSurfacePointB.getCoordinate().ToVector().subtract(this.tumorSurfacePointA.getCoordinate().ToVector());
		Coordinate seedCoordinate = Coordinate.getPointOnLine(this.getTumorSurfacePointA().getCoordinate(), vDirection, RandGenerator.randDouble(0, Config.gridResolution));
		Seed seed = new Seed(seedCoordinate.getX(), seedCoordinate.getY(), seedCoordinate.getZ(), 0);
		return seed;
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
	 * Calculates a line between two arrays of points.
	 * Within the array the point is picked randomly.
	 * 
	 * @return direction of line  
	 **/
	public static Vector3D getLineThroughAreas(Voxel[] a, Voxel[] b)
	{
		Vector3D vA = a[RandGenerator.randInt(0, a.length-1)].getCoordinate().ToVector();
		Vector3D vB = b[RandGenerator.randInt(0, a.length-1)].getCoordinate().ToVector();
		return vB.subtract(vA);
	}
	
	/**
	 * Fills up seeds on a list of needles so that the sum of seeds on all needles is numSeeds.
	 * 
	 * @param needles
	 * @param numSeeds
	 * @return
	 */
	public static ArrayList<Needle> placeMoreSeedsOnNeedles(ArrayList<Needle> needles, int numSeeds)
	{
		int seedsPlaced = 0;
		for (Needle needle : needles)
		{
			seedsPlaced += needle.getSize();
		}
		

		while (seedsPlaced < numSeeds)
		{
			Seed seed = getNeedlePositionWithGreatestDistance(needles);
			seedsPlaced++;
		}
		return needles;
	}
	
	/**
	 * Calculates the coordinate within a list of needles that and the tumor that has the greatest distance
	 * to any other seeds. A seed is placed at the described coordinate.
	 * 
	 * @param needles
	 * @return	Seed at calculated coordinate
	 */
	public static Seed getNeedlePositionWithGreatestDistance(ArrayList<Needle> needles)
	{
		final double STEP_SIZE = 0.1;
		Coordinate maxDistCoordinate = null;
		double maxDist = 0.0;
		Needle maxNeedle = null;
		
		
		Seed[] seeds = Needle.needlesToSeeds(needles);
		

		for (Needle needle : needles)
		{
			double distanceInTumor = needle.getTumorSurfacePointA().distanceToVoxel(needle.getTumorSurfacePointB().getCoordinate());
			Vector3D vDirection =  needle.getTumorSurfacePointB().getCoordinate().ToVector().subtract(needle.getTumorSurfacePointA().getCoordinate().ToVector());
			for (double d = 0.3; d <= (distanceInTumor-0.3); d += STEP_SIZE)
			{
				Coordinate coordinate = Coordinate.getPointOnLine(needle.getTumorSurfacePointA().getCoordinate(), vDirection, d);
				
				double minDist = Double.MAX_VALUE;
				for (Seed seed : seeds)
				{				
					if (coordinate.distanceToCoordiante(seed.getCoordinate()) < minDist)
					{
						minDist = coordinate.distanceToCoordiante(seed.getCoordinate());
					}
				}
				if (minDist > maxDist)
				{
					maxDist = minDist;
					maxDistCoordinate = coordinate;
					maxNeedle = needle;
				}
			}
		}
		
		Seed seed = new Seed(maxDistCoordinate.getX(), maxDistCoordinate.getY(), maxDistCoordinate.getZ(), 0);
		seed.setNeedle(maxNeedle);
		maxNeedle.addSeed(seed);
		
		return seed;
	}
	
	/**
	 * Creates cuboid entry area from bounds.
	 * 
	 * @param body
	 * @param xBounds
	 * @param yBounds
	 * @param zBounds
	 * @return
	 */
	public static Voxel[] getEntryVoxles(Voxel[][][] body, int[] xBounds, int yBounds[], int[] zBounds)
	{
		Set<Voxel> entryVoxels = new HashSet<Voxel>();
				
		for (int x = xBounds[0]; x <= xBounds[1]; x++)
		{
			for (int y = yBounds[0]; y <= yBounds[1]; y++)
			{
				for (int z = zBounds[0]; z <= zBounds[1]; z++)
				{
					entryVoxels.add(body[x][y][z]);
				}	
			}
		}
				
		return entryVoxels.toArray(new Voxel[entryVoxels.size()]);
	}
	
	/**
	 * Creates needles from that is defined by one point through an entry area and one point inside the tumor.
	 * The points within the entry area and the tumor are randomly picked.
	 * numSeedsPerNeedle seeds will be placed randomly on the needle inside the tumor.
	 * 
	 * @param entryVoxels			
	 * @param tumorVoxels
	 * @param body
	 * @param numNeedles
	 * @param numSeedsPerNeedle
	 * @return
	 */
	public static ArrayList<Needle> createNeedlesThroughEntry(Voxel[] entryVoxels, Voxel[] tumorVoxels, Voxel[][][] body, int numNeedles, int numSeedsPerNeedle)
	{
		ArrayList<Needle> needles = new ArrayList<Needle>();
		
		for (int i = 0; i < numNeedles; i++)
		{
			Voxel entryVoxel = entryVoxels[RandGenerator.randInt(0, entryVoxels.length - 1)];
			Voxel tumorVoxel = tumorVoxels[RandGenerator.randInt(0, tumorVoxels.length - 1)];
			
			Needle needle = new Needle(tumorVoxel, entryVoxel, body);
			
			for (int j = 0; j < numSeedsPerNeedle; j++)
			{
				Seed seed = needle.createRandomSeedInTumor();
				needle.addSeed(seed);
				seed.setNeedle(needle);
			}
			needles.add(needle);
		}
		
		return needles;
	}

}
