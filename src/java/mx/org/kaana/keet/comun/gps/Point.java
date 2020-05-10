package mx.org.kaana.keet.comun.gps;

import java.io.Serializable;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 9/05/2020
 *@time 09:47:29 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Point implements Serializable {

	private static final long serialVersionUID=7662024314300301977L;

	private double latitud;
	private double longitud;

	public Point(double latitud, double longitud) {
		this.latitud=latitud;
		this.longitud=longitud;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud=latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud=longitud;
	}

	@Override
	public int hashCode() {
		int hash=7;
		hash=37*hash+(int) (Double.doubleToLongBits(this.latitud)^(Double.doubleToLongBits(this.latitud)>>>32));
		hash=37*hash+(int) (Double.doubleToLongBits(this.longitud)^(Double.doubleToLongBits(this.longitud)>>>32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null) {
			return false;
		}
		if (getClass()!=obj.getClass()) {
			return false;
		}
		final Point other=(Point) obj;
		if (Double.doubleToLongBits(this.latitud)!=Double.doubleToLongBits(other.latitud)) {
			return false;
		}
		if (Double.doubleToLongBits(this.longitud)!=Double.doubleToLongBits(other.longitud)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Point{"+"latitud="+latitud+", longitud="+longitud+'}';
	}
	
}
