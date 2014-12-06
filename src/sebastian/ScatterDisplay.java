package sebastian;

import java.awt.Canvas;
import java.awt.Panel;
import java.awt.Rectangle;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.global.Settings;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import utils.Coordinate;
import utils.LogTool;
import utils.Voxel;


public class ScatterDisplay{
	/**
	 * Defines the type of colour indication for the chart.
	 * 
	 * CurrentDosis: every pixel gets the same colour (violet); the alpha indicates the level of dosis 
	 *  relative to the total maximum of dosis existent.
	 * MaxDosis: every pixel represents the difference between current dosis and max dosis; if current dosis
	 *  is larger than the max dosis, it gets redder, if it's smaller, it turns green. If it exactly matches
	 *  the current dosis, it is blue.
	 * MinDosis: same as for MaxDosis. if current dosis is smaller than the current dosis, it gets reddish, if
	 *  it's larger it turns greenish
	 * GoalDosis: every pixel represents the difference betweent current dosis and the goal dosis; a weighted
	 *  difference defines the colour change from the base colour (blue) to each side: if the current dosis 
	 *  is smaller than the goal dosis, the colour gets greenish, if it's the other way round, the colour 
	 *  turns reddish.
	 */
	public static enum ChartType {CurrentDosis, MaxDosis, MinDosis, GoalDosis};
	
	private static float TRANSPARENCY = 0.6f;
	
	protected static String CANVAS_TYPE = "awt";
	protected static Rectangle DEFAULT_WINDOW = new Rectangle(0,0,600,600);
    protected static Chart chart;
    
    private Coord3d[] points = null;
    private Color[] colors = null;
    private int scaling_factor = 1000;
    private ChartType type = null;
    private double max_dosis = 0;
	
    //JFrame frame = null;
    //JPanel panel = null;
    
    /**
     * Returns the colour, dependending on the chart type and the respective dosis
     * @param data the data, for which the colour should be calculated
     *  
     * @return Color
     */
    private Color getColour (Voxel data) {
    	Color color = new Color (0, 0, 0, 0.25f);
        float factor = 1;
        
        switch (type) {
        case CurrentDosis:
        	factor = (float) (data.getCurrentDosis() / max_dosis * 0.75);
        	if (data.getCurrentDosis() != 0) {
        	  color = new Color (0.7f, 0, 0.9f, factor + 0.25f);
        	}
        	else {
        	  color = new Color (0, 0, 0, 0);
        	}
        	break;
        case MaxDosis:
        	if (data.getMaxDosis() >= data.getCurrentDosis()) {
        	  factor = (float) ((data.getMaxDosis () - data.getCurrentDosis ()) / data.getMaxDosis () * 0.7f);
        	  color = new Color (0, 0.3f + factor, (0.7f - factor), TRANSPARENCY);
        	}
        	else {
        	  factor = (float) ((data.getCurrentDosis() - data.getMaxDosis ()) / data.getCurrentDosis () * 0.7f);
        	  color = new Color (0.3f + factor, 0, (0.7f - factor), TRANSPARENCY);
        	}
        	break;
        case MinDosis:
        	if (data.getMinDosis() <= data.getCurrentDosis()) {
          	  factor = (float) ((data.getCurrentDosis () - data.getMinDosis ()) / data.getCurrentDosis () * 0.7f);
          	  color = new Color (0, 0.3f + factor, (0.7f - factor), TRANSPARENCY);
          	}
          	else {
          	  factor = (float) ((data.getMinDosis() - data.getCurrentDosis ()) / data.getMinDosis () * 0.7f);
          	  color = new Color (0.3f + factor, 0, (0.7f - factor), TRANSPARENCY);
          	}
        	break;
        case  GoalDosis:
        	if (data.getGoalDosis() >= data.getCurrentDosis()) {
          	  factor = (float) ((data.getGoalDosis () - data.getCurrentDosis ()) / data.getGoalDosis () * 0.7f);
          	  color = new Color (0, 0.3f + factor, (0.7f - factor), TRANSPARENCY);
          	}
          	else {
          	  factor = (float) ((data.getCurrentDosis() - data.getGoalDosis ()) / data.getGoalDosis () * 0.7f);
          	  color = new Color (0.3f + factor, 0, (0.7f - factor), TRANSPARENCY);
          	}
        	break;
        }
    	
    	return color;
    }
    
    /**
     * Calculates and prints the total amount of max_dosis, necessary for CurrentDosis-Charts
     * @param data data to check
     */
    private void calculate_TotalDosis (Voxel[][][] data, int dimX, int dimY, int dimZ) {
    	for (int x = 0; x < dimX; x++) {
    		for (int y = 0; y < dimY; y++) {
    			for (int z = 0; z < dimZ; z++) {
    				if (data[x][y][z].getCurrentDosis() > max_dosis) {
    					max_dosis = data[x][y][z].getCurrentDosis();
    				}
    			}
    		}
    	}
    	LogTool.print(" -- Display - Max_Dosis: " + max_dosis, "notification");
    }
    
    /**
     * Sets up the display. Activates hardware acceleration.
     */
	public ScatterDisplay (ChartType type) {
		this.type = type;
		Settings.getInstance().setHardwareAccelerated(true);
		//frame = new JFrame ("ScatterDisplay");
		//frame.setSize(600, 600);
		//frame.setLayout(new FlowLayout ());
		//panel = new JPanel ();
		//panel.setOpaque(false);
	}
	
	/**
	 * Fills the data with the voxels
	 * @param data 3-dimensional matrix including the voxels for each pixel
	 * @param dimX dimension in x-direction
	 * @param dimY dimension in y-direction
	 * @param dimZ dimension in z-direction
	 */
	public void fill (Voxel[][][] data, int dimX, int dimY, int dimZ) {
		points = new Coord3d[dimX*dimY*dimZ];
		colors = new Color[dimX*dimY*dimZ];
		
		max_dosis = 0;
		if (type == ChartType.CurrentDosis) {
		  calculate_TotalDosis(data, dimX, dimY, dimZ);
		}
		
		int i = 0;
		Coordinate coord;
		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				for (int z = 0; z < dimZ; z++) {
					coord = data[x][y][z].getCoordinate();
					points[i] = new Coord3d(coord.getX() * scaling_factor, coord.getY() * scaling_factor, coord.getZ() * scaling_factor);
					colors[i] = getColour (data[x][y][z]);
					i++;
				}
			}
		}
		
		setupChart ();
	}
	
	/**
	 * Creates test data with 1000 equidistant voxels (dimension 10x10x10). Each voxel has a random current
	 *  dosis inbetween 1 and 10.
	 * The minimum dosis is set to 3, maximum dosis to 7. Goal dosis is 5.
	 */
	public void fillTestData(){
        int dim = 10;
        
        Voxel[][][] values = new Voxel[dim][dim][dim];
        
        for (int x = 0; x < dim; x++) {
        	for (int y = 0; y < dim; y++) {
        		for (int z = 0; z < dim; z++) {
        			values[x][y][z] = new Voxel (x, y, z);
        			values[x][y][z].setCurrentDosis(Math.random() * 10);
        			values[x][y][z].setGoalDosis(5);
        			values[x][y][z].setMinDosis(3);
        			values[x][y][z].setMaxDosis(7);
        		}
        	}
        }
        
        fill (values, dim, dim, dim);
    }
	
	/**
	 * Adds the scatter to the chart.
	 */
	private void setupChart () {
		Scatter scatter = new Scatter (points, colors);		
		chart = new Chart (Quality.Advanced, CANVAS_TYPE);
		chart.getScene().add(scatter);
		scatter.setWidth (7);
		
	}
	
	/**
	 * Displays the given data in a new frame.
	 */
	public void display () {
		chart.getCanvas();
		if (chart.getCanvas() instanceof Canvas) {
			//panel.add ((Canvas) chart.getCanvas ());
		}
		else if (chart.getCanvas() instanceof Panel) {
			//panel.add ((Panel) chart.getCanvas ());
		}
		else {
			System.out.println ("Unknown class: " + chart.getCanvas ().getClass());
		}
		//frame.setContentPane(panel);
		//frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
		//frame.pack ();		
		//frame.setVisible(true);
		//chart.setAxeDisplayed(false);
		ChartLauncher.openChart(chart, DEFAULT_WINDOW, this.getClass ().getSimpleName ());
	}
}
