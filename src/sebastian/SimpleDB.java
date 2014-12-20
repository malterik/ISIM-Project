package sebastian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import utils.LogTool;
import utils.Voxel;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

public class SimpleDB {
  private static int id = 0;
  private static final String FILE_NAME = "isim.db";
  ArrayList<TreatmentEntry> treatmentEntries = null;
  
  public SimpleDB () {
	  File dbFile = new File (FILE_NAME);
	  treatmentEntries = new ArrayList<TreatmentEntry> ();
	  
	  if (dbFile.exists() && dbFile.isFile()) {
		  loadDB ();
	  }
	  
	  checkForMatlabFiles ();
  }
  
  public void addEntry (TreatmentEntry entry) {
	  treatmentEntries.add(entry);	  
  }
  
  public void print () {
	  for (TreatmentEntry entry: treatmentEntries) {
		  LogTool.print ("----------\nID: " + entry.getID() + "\n(x|y|z): " + entry.getDimensions()[0] + "|" + entry.getDimensions()[1] + "|" + entry.getDimensions()[2] + "\n", "debug");
	  }
  }
  
  public TreatmentEntry getEntry (int index) {
	  for (int i = 0; i < treatmentEntries.size(); i++) {
		  if (treatmentEntries.get(i).getID() == i) {
			  return treatmentEntries.get(i);
		  }
	  }
	  
	  return null;
  }
  
  public TreatmentEntry getEntryByName (String name) {
	  for (int i = 0; i < treatmentEntries.size (); i++) {
		  if (treatmentEntries.get(i).getName().equals(name)) {
			  return treatmentEntries.get(i);
		  }
	  }
	  
	  return null;
  }
  
  public int getSize () {
	  return treatmentEntries.size ();
  }
  
  public static int getID () {
	  id++;
	  LogTool.print("Returning id " + (id - 1), "debug");
	  return (id - 1) ;
  }
  
  public void deleteEntry (int id) {
	  int delIndex = -1;
	  
	  LogTool.print ("Deleting id " + id, "debug");
	  
	  for (int i = 0; i < treatmentEntries.size(); i++) {
		  if (treatmentEntries.get(i).getID () == id) {
			  delIndex = i;
			  break;
		  }
	  }
	  
	  LogTool.print("id " + id + " found: " + delIndex, "debug");
	  
	  if (delIndex > -1) treatmentEntries.remove(delIndex);
  }
  
  private void loadDB () {
	ObjectInputStream oiStr = null;
	FileInputStream fiStr = null;
	Object obj = null;
	TreatmentEntry entry = null;
	int objCount = 0;
	int maxID = -1;
	
	LogTool.print("Reading database from file", "debug");
	treatmentEntries.clear();
	
	try {
		fiStr = new FileInputStream (FILE_NAME);
		oiStr = new ObjectInputStream(fiStr);
		
		objCount = oiStr.readInt();
		
		for (int i = 0; i < objCount; i++) {
			obj = oiStr.readObject();
			entry = (TreatmentEntry) obj;
			treatmentEntries.add (entry);
			if (maxID < entry.getID()) maxID = entry.getID ();
		}
		id = maxID + 1;
		
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
		
		ooStr.writeInt(treatmentEntries.size());
		
		
		for (int i = 0; i < treatmentEntries.size (); i++) {
			if (treatmentEntries.get(i).doSave()) ooStr.writeObject(treatmentEntries.get(i));
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
	  treatmentEntries = null;
	  id = -1;	  
  }
  
  public void readMatlabFile (File mFolder) {
	  MLArray mlArray = null;
	  MLDouble mlDouble = null;
	  MatFileReader mReader = null;
	  Voxel[][][] bodyArray = null;
	  int x, k = 0; int dims[] = null;
	  
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
						}
					}
				}
			} catch (IOException ioExc) {
				LogTool.print ("Could not read file " + mFile + ": " + ioExc, "error");
			}
		  }
	  }
	  
	  dims = new int[]{x, dims[0], dims[1]};
	  addEntry (new TreatmentEntry(bodyArray, dims, null, mFolder.getName ()));
	  System.out.println(String.format("%d|%d|%d: %s", dims[0], dims[1], dims[2], mFolder.getName ()));
  }
  
  public void checkForMatlabFiles () {	  
	  File folder = new File (System.getProperty("user.dir") + "/datas");
	  File[] listOfFiles = folder.listFiles();
	  	  
	  if (listOfFiles != null) {
		  for (File file: listOfFiles) {
			  if (file.isDirectory ()) {
				  System.out.println (file.getName());
				  readMatlabFile (file);
				  break;
			  }
		  }
	  }
  }
}
