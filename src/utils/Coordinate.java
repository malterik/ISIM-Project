package utils;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Coordinate implements Serializable {
	private static final long serialVersionUID = 12L;
	private double x;
	private double y;
	private double z;
	
	public Coordinate(double x, double y , double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	
	/**
	 * Calculates coordinate on line through base and other.
	 * 
	 * @param base		start point
	 * @param other		other point that defines line
	 * @param t			distance to start point in direction of other
	 * 
	 * @return	point on line
	 */
	public static Coordinate getPointOnLine(Coordinate base, Vector3D vDirection, double t)
	{
		if ((vDirection.getX() == 0) && (vDirection.getY() == 0) && (vDirection.getZ() == 0))
			return base;
		
		Vector3D vBase = base.ToVector();
		Vector3D vDirectionNormalized = vDirection.normalize();
		Vector3D vPoint = vBase.add(vDirectionNormalized.scalarMultiply(t));
		return vectorToCoordinate(vPoint);
	}
	
	public static Coordinate vectorToCoordinate(Vector3D vector)
	{
		return new Coordinate(vector.getX(), vector.getY(), vector.getZ());
	}
	
	public Vector3D ToVector()
	{
		return new Vector3D(this.getX(), this.getY(), this.getZ());
	}
	
	public double distanceToCoordiante(Coordinate position) {
		
	    return(( Math.sqrt( Math.pow(this.getX()-position.getX(), 2) + Math.pow(this.getY()-position.getY(), 2) + Math.pow(this.getZ()-position.getZ(), 2) ) ) * Config.gridResolution);
	}
}
