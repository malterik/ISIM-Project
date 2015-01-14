package sebastian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import utils.Config;
import utils.Coordinate;
import utils.LogTool;
import utils.Voxel;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

public class SimpleDB {
  File baseDir = new File (System.getProperty("user.dir") + "/datas");
  private static final String FILE_NAME = "isim.db";
  ArrayList<TreatmentEntry> treatments = null;
  ArrayList<BodyEntry> bodies = null;
    
  public SimpleDB () {
	  File dbFile = new File (FILE_NAME);
	  treatments = new ArrayList<TreatmentEntry> ();
	  bodies = new ArrayList<BodyEntry> ();
	  
	  if (dbFile.exists() && dbFile.isFile()) {
		  loadDB ();
	  }
  }
  
  /**
   * Reads all matlab files and calculates the measurements. All already existing calculations will be kept
   */
  public void classifyAll () {
	  // Read all missing bodies
	  for (File folder: baseDir.listFiles()) {
		  if (folder.isDirectory() && getTreatmentByName(folder.getName ()) == null) {
			  // Only read non-existing bodies
			  if (getBodyByName(folder.getName ()) == null) { 
			    LogTool.print ("File read: " + folder, "debug");
				readMatlabFile(folder); 
				classify(getBodyByName (folder.getName ()));
				deleteBody(folder.getName ());
			  }
			  else
			  {
			    classify (getBodyByName (folder.getName ()));
			    
			  }
		  }
		  else {
			  LogTool.print ("Entry already exists: " + folder.getName (),"debug");
		  }
	  }	  
  }
  
  private void classify (BodyEntry bEntry) {
	  if (getTreatmentByName (bEntry.getName ()) != null) {
		  LogTool.print ("Entry already exists: " + bEntry.getName(),"debug");
		  return;
	  }
	  
	  LogTool.print ("Measuring entry " + bEntry.getName (), "debug");
	  
	  int[] typeCount = new int[bEntry.getMaxType () + 1];
	  double[] tcoDists = new double[bEntry.getMaxType () +  1];
	  double[] tclDists = new double[bEntry.getMaxType () + 1];
	  double[][] typeSum = new double[bEntry.getMaxType () + 1][3];
	  ArrayList<double[]> tumorVoxel = new ArrayList<double[]> ();
	  
	  // Measure stuff
	  LogTool.print ("Counting voxel", "debug");
	  Coordinate c = null;
	  for(int i = 0; i < bEntry.getDimensions()[0]; i++) {
		  for (int j = 0; j < bEntry.getDimensions ()[1]; j++) {
			  for (int k = 0; k < bEntry.getDimensions ()[2]; k++) {
				  // Plain count of each type
				  typeCount[bEntry.getBodyArray()[i][j][k].getBodyType()]++;
				  // Sum up for center of volume
				  typeSum[bEntry.getBodyArray()[i][j][k].getBodyType ()][0] += bEntry.getBodyArray()[i][j][k].getCoordinate().getX();
				  typeSum[bEntry.getBodyArray()[i][j][k].getBodyType ()][1] += bEntry.getBodyArray()[i][j][k].getCoordinate().getY();
				  typeSum[bEntry.getBodyArray()[i][j][k].getBodyType ()][2] += bEntry.getBodyArray()[i][j][k].getCoordinate().getZ();
				  if (bEntry.getBodyArray()[i][j][k].getBodyType () == Config.TUMOR) {
					  c = bEntry.getBodyArray() [i][j][k].getCoordinate();
					  tumorVoxel.add(new double[] {c.getX(), c.getY (), c.getZ()});
				  }
			  }
		  }
	  }
	  
	  // Calculate center of volume
	  LogTool.print ("Calculating volume center", "debug");
	  for (int i = 0; i < typeSum.length; i++) {
		  typeSum[i][0] = typeSum[i][0] / typeCount[i];
		  typeSum[i][1] = typeSum[i][1] / typeCount[i];
		  typeSum[i][2] = typeSum[i][2] / typeCount[i];
	  }
	  
	  // Calculate distance between tumor center and other volumes center
	  LogTool.print ("Calculating center distances", "debug");
	  for (int i = 0; i < tcoDists.length; i++) {
		  tcoDists[i] = getDistance (typeSum[Config.TUMOR], typeSum[i]);
	  }
	  
	  // Calculate closest distance between tumor and other volumes
	  LogTool.print ("Calculating closest distances", "debug");
	  int type = -1;
	  double dist = -1;
	  for (int i = 0; i < tclDists.length; i++) {
		  tclDists[i] = 50000;
	  }
	  for (int i = 0; i < bEntry.getDimensions()[0]; i++) {
		  for (int j = 0; j < bEntry.getDimensions()[1]; j++) {
			  for (int k = 0; k < bEntry.getDimensions()[2]; k++) {
				  for (int l = 0; l < tumorVoxel.size(); l++) {
					  type = bEntry.getBodyArray()[i][j][k].getBodyType();
					  dist = getDistance (bEntry.getBodyArray()[i][j][k].getCoordinate(), tumorVoxel.get(l));
					  if (tclDists[type] > dist) {
						  tclDists[type] = dist;
					  }
				  }
			  }
		  }
	  }
	  
	  
	  TreatmentEntry tEntry = new TreatmentEntry (bEntry.getName());
	  tEntry.setVolumeCenters(typeSum);
	  tEntry.setVolumeSizes(typeCount);
	  tEntry.setTumorCenterDistances (tcoDists);
	  tEntry.setTumorClosestDistances(tclDists);
	  addTreatmentEntry(tEntry);
	  LogTool.print ("New entry:\n" + tEntry.toString(), "debug");
	  
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

  public void addTreatmentEntry (TreatmentEntry entry) {
	  treatments.add(entry);	  
  }
  
  public void loadBody (String name) {
	  File mFolder = new File (baseDir, name);
	  
	  if (getBodyByName(name) == null) {
	    readMatlabFile(mFolder);
	  }
  }
  
  public void printBodies () {
	  for (BodyEntry entry: bodies) {
		  LogTool.print (entry.toString(), "debug");
	  }
  }
  
  public void printTreatments () {
	  for (TreatmentEntry entry: treatments) {
		  LogTool.print (entry.toString(), "debug");
	  }
  }
  
  public TreatmentEntry getTreatmentByName (String name) {
	  for (int i = 0; i < treatments.size (); i++) {
		  if (treatments.get(i).getName().equals(name)) {
			  return treatments.get(i);
		  }
	  }
	  
	  return null;
  }
  
  public BodyEntry getBodyByName (String name) {
	  for (int i = 0; i < bodies.size (); i++) {
		  if (bodies.get(i).getName().equals(name)) {
			  return bodies.get(i);
		  }
	  }
	  
	  return null;
  }
    
  public int getBodySize () {
	  return bodies.size ();
  }
  
  public int getTreatmentSize () {
	  return treatments.size ();
  }
    
  public void deleteBody (String name) {
	  int delIndex = -1;
	  
	  LogTool.print ("Deleting body " + name, "debug");
	  
	  for (int i = 0; i < bodies.size(); i++) {
		  if (bodies.get(i).getName ().equals (name)) {
			  delIndex = i;
			  break;
		  }
	  }
	  
	  LogTool.print("Body " + name + " found: " + delIndex, "debug");
	  
	  if (delIndex > -1) bodies.remove(delIndex);
  }
  
  private void loadDB () {
	ObjectInputStream oiStr = null;
	FileInputStream fiStr = null;
	Object obj = null;
	TreatmentEntry entry = null;
	int objCount = 0;
	
	LogTool.print("Reading database from file", "debug");
	treatments.clear();
	
	try {
		fiStr = new FileInputStream (FILE_NAME);
		oiStr = new ObjectInputStream(fiStr);
		
		objCount = oiStr.readInt();
		
		for (int i = 0; i < objCount; i++) {
			obj = oiStr.readObject();
			entry = (TreatmentEntry) obj;
			treatments.add (entry);
		}
		
		oiStr.close();
		fiStr.close();
	} catch (FileNotFoundException fnfExc) {
		LogTool.print("Could not find database file " + FILE_NAME + ": " + fnfExc, "error");
	} catch (IOException ioExc) {
		LogTool.print("Could not read database: " + ioExc, "error");
	} catch (ClassNotFoundException cnfExc) {
		LogTool.print ("Could not find/cast class: " + cnfExc, "error");
	}
  }
  
  private void storeDB () {
	  ObjectOutputStream ooStr = null;
	  FileOutputStream foStr = null;
	  File dbFile = new File (FILE_NAME);
	  LogTool.print("Writing database to file", "debug");
	  
	  if (dbFile.exists() && dbFile.isFile()) {
		  dbFile.delete();		  
	  }
	  
	  try {
		dbFile.createNewFile();
	  } catch (IOException ioExc) {
	    LogTool.print("Could not create database file: " + ioExc, "error");
      }

	  try {
		foStr = new FileOutputStream (FILE_NAME);
		ooStr = new ObjectOutputStream(foStr);
		
		ooStr.writeInt(treatments.size());
		
		
		for (int i = 0; i < treatments.size (); i++) {
			ooStr.writeObject(treatments.get(i));
		}
		
		ooStr.close();
		foStr.close();
	} catch (FileNotFoundException fnfExc) {
		LogTool.print("Could not find database file " + fnfExc +  ": " + fnfExc, "error");
	} catch (IOException ioExc) {
		LogTool.print ("Could not write to database: " + ioExc, "error");
	}

  }
  
  /**
   * Stores all hold treatment entries in the file and clears resources.
   */
  public void close () {
	  storeDB ();
	  treatments = null;
	  bodies = null;
  }
  
  public void readMatlabFile (File mFolder) {
	  MLArray mlArray = null;
	  MLDouble mlDouble = null;
	  MatFileReader mReader = null;
	  Voxel[][][] bodyArray = null;
	  int x, k = 0; int dims[] = null;
	  int maxType = 0;
	  
	  x = mFolder.listFiles().length;
	  for (File mFile: mFolder.listFiles()) {
		  if (mFile.isFile() && mFile.getName().endsWith(".mat")) {
			  try {
				k = Integer.parseInt(mFile.getName().substring(0, mFile.getName().indexOf(".mat")));
				mReader = new MatFileReader (mFile);
				mlArray = mReader.getMLArray("cpy_array");
								
				if (mlArray != null) {
					if (bodyArray == null)  {
						dims = mlArray.getDimensions();
						bodyArray = new Voxel[x][dims[0]][dims[1]];						
					}
					mlDouble = (MLDouble) mlArray;
					for (int i = 0; i < dims[0]; i++) {
						for (int j = 0; j < dims[1]; j++) {
							bodyArray[k - 1][i][j] = new Voxel(k - 1, i, j);
							bodyArray[k - 1][i][j].setBodyType((int)(double) mlDouble.get(i, j));
							if (maxType < bodyArray[k - 1][i][j].getBodyType()) {
								maxType = bodyArray[k - 1][i][j].getBodyType();
							}
						}
					}
				}
			} catch (IOException ioExc) {
				LogTool.print ("Could not read file " + mFile + ": " + ioExc, "error");
			}
		  }
	  }
	  
	  dims = new int[]{x, dims[0], dims[1]};
	  BodyEntry entry = new BodyEntry(mFolder.getName (), bodyArray, dims, maxType);
	  addBody (entry);
  }
  
  private void addBody(BodyEntry entry) {
	 bodies.add (entry);
  }
}
