package mx.org.kaana.keet.estaciones.masivos.test;

import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 5/05/2020
 * @time 11:05:54 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Masivos {
  
	private static final Log LOG=LogFactory.getLog(Masivos.class);
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		Estaciones estaciones=new Estaciones();
		estaciones.setKeyLevel(String.valueOf(1), 0); // idEmpresa
		estaciones.setKeyLevel(String.valueOf(2020), 1); // ejercicio
		estaciones.setKeyLevel(String.valueOf(1), 2); // orden del contrato
		estaciones.setKeyLevel(String.valueOf(1), 3); // orden de contrato lote
		LOG.info(estaciones.toCode());
		LOG.info(estaciones.toKey(4));
		LOG.info("------------------------------");
		//LOG.info(estaciones.toNextKey(estaciones.toKey(4), 5));
		// 0012020001001000000000000
		// 0012020001001
	}

}
