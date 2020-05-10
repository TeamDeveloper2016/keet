package mx.org.kaana.keet.comun.gps;

import java.io.Serializable;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 9/05/2020
 * @time 09:49:40 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public final class Distance implements Serializable {

	private static final long serialVersionUID= -2303254494579255847L;
	private static final long DISTANCE_LIMIT  = 20; // en metros

	private Point one;
	private Point two;

	public Distance(Point one, Point two) {
		this.one=one;
		this.two=two;
	}

	public Point getOne() {
		return one;
	}

	public Point getTwo() {
		return two;
	}

	public double toKm() {
    //double earthRadio= 3958.75; // en millas  
		double earthRadio = 6378.137; // en kilómetros  
		double dLatitud   = Math.toRadians(two.getLatitud()- one.getLatitud());
		double dLongitud  = Math.toRadians(two.getLongitud()- one.getLongitud());
		double sinLatitud = Math.sin(dLatitud/ 2);
		double sinLongitud= Math.sin(dLongitud/ 2);
		double a= Math.pow(sinLatitud, 2)+ Math.pow(sinLongitud, 2)* Math.cos(Math.toRadians(one.getLatitud()))* Math.cos(Math.toRadians(two.getLatitud()));
		double c= 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return earthRadio* c;
	}

	public double toKm(Point one, Point two) {
		this.one=one;
		this.two=two;
		return toKm();
	}

	public double toMt() {
		//aquí obtienes la distancia en metros por la conversion 1Km= 1000m
		return this.toKm()*1000;
	}

	public double toMt(Point one, Point two) {
		return this.toKm(one, two)*1000;
	}

	public boolean isValid() {
	  return this.toMt()<= DISTANCE_LIMIT;
	}
	
	public boolean isValid(Point one, Point two) {
		return this.toMt(one, two)<= DISTANCE_LIMIT;
	}
	
}
