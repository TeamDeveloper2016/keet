package mx.org.kaana.keet.comun.gps;

import java.io.Serializable;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private static final Log LOG=LogFactory.getLog(Distance.class);

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
    double regresar= 0D;
    try {
      if(this.one!= null && this.two!= null) {
        double earthRadio = 6378.137; // en kil�metros  
        double dLatitud   = Math.toRadians(this.two.getLatitud()- this.one.getLatitud());
        double dLongitud  = Math.toRadians(this.two.getLongitud()- this.one.getLongitud());
        double sinLatitud = Math.sin(dLatitud/ 2);
        double sinLongitud= Math.sin(dLongitud/ 2);
        double a= Math.pow(sinLatitud, 2)+ Math.pow(sinLongitud, 2)* Math.cos(Math.toRadians(this.one.getLatitud()))* Math.cos(Math.toRadians(this.two.getLatitud()));
        double c= 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        regresar= earthRadio* c;
      } // if
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      regresar= 0D;
    } // catch
    return regresar;
	}

	public double toKm(Point one, Point two) {
		this.one=one;
		this.two=two;
		return toKm();
	}

	public double toMt() {
		//aqu� obtienes la distancia en metros por la conversion 1Km= 1000m
		return Numero.toRedondear(this.toKm()* 1000);
	}

	public double toMt(Point one, Point two) {
		return Numero.toRedondear(this.toKm(one, two)* 1000);
	}

	public boolean isValid() {
	  return this.toMt()<= DISTANCE_LIMIT;
	}
	
	public boolean isValid(Point one, Point two) {
		return this.toMt(one, two)<= DISTANCE_LIMIT;
	}
	
	public static void main(String ... args) {
		Distance distance= new Distance(new Point(21.837244, -102.326590), new Point(21.84302, -102.3233));
	  LOG.info(distance.toMt()+ " metros");
	}
	
}
