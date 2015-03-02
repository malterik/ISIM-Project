package laurin;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import utils.Config;
import utils.Voxel;

/**
 * The Histogram class provides methods to create and visualize a dose volume histogram.
 * 
 * Usage:
 * 	- create new Histogram
 * 	- add one or multiple data sets for different body parts with addDataSet() method
 *  - calculate and visualize with showHistogram() method
 *  
 * @author Laurin Mordhorst	
 */
public class Histogram extends JFrame implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String,double[]> dataSets;
	private String title;
	
	public Histogram (String title)
	{
		super(title);
		this.title = title;
		dataSets = new HashMap<String, double[]>();
	}
	
	/**
	 * Add data set to histogram.
	 * 
	 * @param legend 	legend to describe curve in plot
	 * @param data		values used for histogram creation
	 */
	public void addDataSet(String legend, double[] data)
	{
		dataSets.put(legend, data);
	}
	
	/**
	 * Add data set to histogram.
	 * 
	 * @param legend 	legend to describe curve in plot
	 * @param voxels	voxels whose current dose is used for histogram creation
	 */
	public void addDataSet(String legend, Set<Voxel> voxels)
	{
		double[] data = new double[voxels.size()];
		int counter = 0;
		
		for (Voxel voxel : voxels)
			data[counter++] = voxel.getCurrentDosis();
		
		addDataSet(legend, data);
	}
	
	/**
	 * Creates and displays histogram a new frame.
	 * 
	 * Minimum and maximum value of histogram are calculated as the minimum and maximum
	 * value of all added data sets, respectively.
	 * 
	 * @param normalizationFactor		factor x-axis is normalized by	
	 * @param numBins					number of histogram bins
	 */
	public void display(double normalizationFactor, int numBins)
	{
		display(normalizationFactor, numBins, getMin(), getMax());
	}
	
	/**
	 * Creates and displays histogram a new frame.
	 * 
	 * @param normalizationFactor		factor x-axis is normalized by	
	 * @param numBins					number of histogram bins
	 * @param min						minimum bin value
	 * @param max						maximum bin value
	 */
	public void display(double normalizationFactor, int numBins, double min, double max)
	{
        final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		
        /* calculate histogram data for each anatomy and add to series collection */
		for (Map.Entry<String, double[]> entry : dataSets.entrySet())
		{
			XYSeries xySeries = calcHistogram(entry.getKey(), entry.getValue(), normalizationFactor, numBins, min, max);
			xySeriesCollection.addSeries(xySeries);
		}
		
		/* create chart from series collection and show frame */
		final JFreeChart chart = ChartFactory.createXYLineChart(
	                "Dose Volume Histogram (" + this.title + ")",  // chart title
	                "Dose [Gy]",                     // domain axis label
	                "Volume [%]",                     // range axis label
	                xySeriesCollection,                // data
	                PlotOrientation.VERTICAL,
	                true,                           // include legend
	                true,
	                false
	            );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(550, 270));
        setContentPane(chartPanel);
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
	}
	
	/**
	 * Calculates histogram as x-y-series with
	 * x: bins
	 * y: count of values in bin interval
	 * 
	 * @param legend					curve legend to appear in plot
	 * @param data						values from which the histogram shall be created
	 * @param normalizationFactor		factor x-axis is normalized by		
	 * @param numBins					number of histogram bins
	 * @param min						minimum bin value
	 * @param max						maximum bin value
	 * 
	 * @return histogram as x-y-series
	 */
	public XYSeries calcHistogram(String legend, double[] data, double normalizationFactor, int numBins, double min, double max)
	{
		XYSeries xySeries = new XYSeries(legend);
		int[] result = new int[numBins];
		double binSize = (max - min)/numBins;

		/* assign bin to each value */
		for (double d : data) {
			int bin = (int) ((d - min) / binSize);
			if (bin < 0) { /* this data is smaller than min */ }
			else if (bin >= numBins) { /* this data point is bigger than max */ }
			else 
			{
				result[bin] += 1;
			}
		}
		
		/* calculate inverse cumulative histogram for DVH presentation */
		double[] histData = calcInverseCumulativeData(result);

		
		/* calculate bin values as center of bin interval and add to x-y-series */
		for (int i = 0; i < numBins; i++)
		{
			double binCenter = min + (i-1)*binSize + binSize/2;
			binCenter /= normalizationFactor;
			xySeries.add(binCenter, 100 * histData[i]);
		}
	
		return xySeries;
	}
	
	/**
	 * Calculate normalized histogram data to represent relative frequencies.
	 * 
	 * @param values		values from which the histogram shall be created	
	 * 
	 * @return 	normalized histogram
	 */
	  private double[] calcNormalizedData(int[] values)
	  {
			double [] normHistogram = new double[values.length];
			double sum = 0.0;
			
			/* sum up all values */
			for (int i = 0; i < values.length; i++)
				sum += values[i];

			/* divide all values by their sum */
			for (int i = 0; i < values.length; i++)
			    normHistogram[i] = (double) values[i]/sum;
			
			return normHistogram;
	  }
	  
	  /**
	   * Calculate inverse cumulative histogram data.
	   * 
	   * @param 	normalized values from which the histogram shall be created	
	   * 
	   * @return 	inverse cumulative histogram
	   */
	  private double[] calcInverseCumulativeData(int[] values)
	  {
			double[] normHist = calcNormalizedData(values);
			double[] cumNormHist = new double[normHist.length];
			
			/* cumulate relative frequencies in reverse order */
			cumNormHist[normHist.length-1] = normHist[normHist.length-1];
			for (int i = normHist.length-2; i >= 0; i--)
			{
			    cumNormHist[i] = cumNormHist[i+1] + normHist[i];
			}

			return cumNormHist;
	  }
	
	/**
	 * Calculates minimum value in all added data sets.
	 * 
	 * @return minimum value
	 */
	private double getMin()
	{
		double min = Double.MAX_VALUE;
		for (Map.Entry<String, double[]> entry : dataSets.entrySet())
			for (double number : entry.getValue())
			{
				if (number < min)
					min = number;
			}
		return min;
	}
	
	/**
	 * Calculates maximum value in all added data sets.
	 * 
	 * @return maximum value
	 */
	private double getMax()
	{
		double max = Double.MIN_VALUE;
		for (Map.Entry<String, double[]> entry : dataSets.entrySet())
			for (double number : entry.getValue())
			{
				if (number > max)
					max = number;
			}
		return max;
	}
}
