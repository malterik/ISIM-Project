package laurin;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import sebastian.BodyEntry;
import sebastian.SimpleDB;
import utils.Config;
import utils.Coordinate;
import utils.Needle;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;
import erik.BodyAnalyzer;

public class LPStepwise {

	public static Voxel[][][] getVoxelArray(BodyEntry entry) 
	{
		Voxel[][][] body = new Voxel[entry.getDimensions()[0]][entry.getDimensions()[1]][entry.getDimensions()[2]];
		
		for (int x = 0; x < entry.getDimensions()[0]; x++) 
		{
			for (int y = 0; y < entry.getDimensions()[1]; y++)
			{
				for (int z = 0; z < entry.getDimensions()[2]; z++)
				{
					body[x][y][z] = new Voxel(x, y, z);
					
					switch (entry.getBodyArray()[x][y][z].getBodyType()) {
						case Config.normalType: {
							body[x][y][z].setGoalDosis(Config.normalGoalDose);
							body[x][y][z].setBodyType(Config.normalType);
							break;
						}
						case Config.spineType: {
							body[x][y][z].setGoalDosis(Config.spineGoalDose);
							body[x][y][z].setBodyType(Config.spineType);
							break;
						}
						case Config.liverType: {
							body[x][y][z].setGoalDosis(Config.liverGoalDose);
							body[x][y][z].setBodyType(Config.liverType);
							break;
						}
						case Config.pancreasType: {
							body[x][y][z].setGoalDosis(Config.pancreasGoalDose);
							body[x][y][z].setBodyType(Config.pancreasType);
							break;
						}
						case Config.tumorType: {
							body[x][y][z].setGoalDosis(Config.tumorGoalDose);
							body[x][y][z].setBodyType(Config.tumorType);
							break;
						}
						default: {
							body[x][y][z].setGoalDosis(Config.normalGoalDose);
							body[x][y][z].setBodyType(Config.normalType);
							break;
						}
					}	
				}
			}
		}
		return body;
	}
	
	public static Voxel[][] sampleDownBetter(Voxel[][][] body, ArrayList<Set<Voxel>> bodyParts, Voxel[] tumorSurface, int factor, int[] xBounds, int yBounds[], int zBounds[])
    {    	
    	Set<Voxel> tumorVoxelSet = new HashSet<Voxel>();
    	ArrayList<Voxel[]> voxelsByBodyPart = new ArrayList<Voxel[]>();
    	
    	ArrayList<ArrayList<Entry<Voxel,Double>>> distanceMaps = new ArrayList<ArrayList<Entry<Voxel,Double>>>();
	    for (int i = 0; i < (Config.tumorType-1); i++)
	    	distanceMaps.add(new ArrayList<Entry<Voxel,Double>>());
    	
    	
    	for (int x = xBounds[0]; x < xBounds[1]; x++)
    	{
        	for (int y = yBounds[0]; y < yBounds[1]; y ++)
        	{
            	for (int z = zBounds[0]; z < zBounds[1]; z++)
            	{
            		if (body[x][y][z].getBodyType() != Config.tumorType)
            		{
            			
            			Entry<Voxel,Double> entry = new AbstractMap.SimpleEntry<Voxel,Double>(body[x][y][z], new Double(getMinDistanceToTumor(tumorSurface, body[x][y][z])));
            			distanceMaps.get(body[x][y][z].getBodyType()-1).add(entry);
            		}
            		else if (body[x][y][z].getBodyType() == Config.tumorType)
            		{
            			tumorVoxelSet.add(body[x][y][z]);
            		}
            	}
        	}
    	}
    	
		Comparator<Entry<Voxel,Double>> distanceToTumorSurfaceComparator = new Comparator<Entry<Voxel,Double>>() {
		   public int compare(Entry<Voxel,Double> e1, Entry<Voxel,Double> e2) {    
		        return e1.getValue().compareTo(e2.getValue());
		    }
	    };
	    
	    for (int i = 0; i < (Config.tumorType-1); i++)
	    {
	    	Collections.sort(distanceMaps.get(i), distanceToTumorSurfaceComparator);
	    	
	    	int arraySize;
	    	/*if (distanceMaps.get(i).size() > 1*tumorVoxelSet.size())
	    		arraySize = (int) 0.5*tumorVoxelSet.size();
	    	else if (distanceMaps.get(i).size() < (int) 0.5*tumorVoxelSet.size())
	    		arraySize = distanceMaps.get(i).size();
	    	else 
	    		arraySize = (int) Math.round(distanceMaps.get(i).size()/factor);*/
	    	
	    	if (distanceMaps.get(i).size() > 1*tumorVoxelSet.size())
	    		arraySize = tumorVoxelSet.size();
	    	else
	    		arraySize = distanceMaps.get(i).size();
	    	
	    				
	    	Voxel[] bodyPartVoxels = new Voxel[arraySize];
	    	for (int j = 0; j < arraySize; j++)
	    	{ 		
	    		bodyPartVoxels[j] = distanceMaps.get(i).get(j).getKey();
	    	}
	    	voxelsByBodyPart.add(bodyPartVoxels);
	    }  
    	voxelsByBodyPart.add(tumorVoxelSet.toArray(new Voxel[tumorVoxelSet.size()]));
    	
    	Voxel[][] voxelsByBodyPartArray = new Voxel[Config.tumorType][];
    	for (int i = 0; i < (Config.tumorType); i++) 
    		voxelsByBodyPartArray[i] = voxelsByBodyPart.get(i);
    	
    	return voxelsByBodyPartArray;
    }
    
    public static double getMinDistanceToTumor(Voxel[] tumorSurface, Voxel voxel)
    {
    	Coordinate coordinate = voxel.getCoordinate();
    	double minDistance = Double.MAX_VALUE;
    	
    	for (Voxel surfaceVoxel : tumorSurface)
    	{	
    		if (surfaceVoxel.distanceToVoxel(coordinate) < minDistance)
    		{
    			minDistance = surfaceVoxel.distanceToVoxel(voxel.getCoordinate());
    		}
    	}
    	return minDistance;
    }
    
    public static Seed[] createSeeds(Voxel[][][] body, BodyAnalyzer ba)
	{
		Seed[] seeds = new Seed[Config.numberOfSeeds];
		int i =0 ;
		while (i < Config.numberOfSeeds ) {
			Coordinate co = new Coordinate(
					RandGenerator.randDouble(ba.getxBoundsTumor(1)[0], ba.getxBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getyBoundsTumor(1)[0], ba.getyBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getzBoundsTumor(1)[0], ba.getzBoundsTumor(1)[1]));
			
			if(body[(int)co.getX()][(int)co.getY()][(int)co.getZ()].getBodyType() == Config.tumorType) {
				seeds[i] = new Seed(co.getX(), co.getY(), co.getZ(), 0);
				i++;
			}
		}
		return seeds;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws IloException 
	 */
	public static void main(String[] args) throws IloException, IOException {
		Voxel.setLUT(10, Config.LUTSize);
		SimpleDB db = new SimpleDB ();
    	db.loadBody("data2593.4844");
		BodyEntry entry = db.getBodyByName("data2593.4844");
		Voxel[][][] body = getVoxelArray(entry);
		
		int[] dimensions = new int[]{body.length, body[0].length, body[0][0].length};
		BodyAnalyzer ba = new BodyAnalyzer(body, dimensions);
		int[] xEntryBounds = ba.getxBoundsTumor(1);
		int[] yEntryBounds = ba.getyBoundsTumor(1);
		int[] zEntryBounds = new int[]{0,0};
		
		Voxel[] entryVoxels = Needle.getEntryVoxles(body, xEntryBounds, yEntryBounds, zEntryBounds);
		ArrayList<Set<Voxel>> bodyParts = BodyAnalyzer.splitBodyTypes(body);
		Set<Voxel> tumorVoxels = bodyParts.get(Config.tumorType-1);
		Voxel[] tumorVoxelArray = tumorVoxels.toArray(new Voxel[tumorVoxels.size()]);	
		ArrayList<Needle> needles = Needle.createNeedlesThroughEntry(entryVoxels, tumorVoxelArray, body, 25, 2);
		//Seed[] seeds = Needle.needlesToSeeds(needles);
		Seed[] seeds = createSeeds(body, ba);
		
		Voxel[][] downSampledBody = sampleDownBetter(body, bodyParts, BodyAnalyzer.getOutterVoxels(body, tumorVoxels), 10, ba.getxBoundsTumor(2), ba.getyBoundsTumor(2), ba.getzBoundsTumor(2));

		CplexSolver cplexSolverSampled = new CplexSolver(downSampledBody, seeds);
		new LPStepwiseGUI(cplexSolverSampled, body);
		//db.close();

	}

}
