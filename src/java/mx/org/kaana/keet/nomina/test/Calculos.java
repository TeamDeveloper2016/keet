package mx.org.kaana.keet.nomina.test;

import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.libs.formato.BouncyEncryption;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 27/04/2020
 *@time 09:08:54 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Calculos {

	private static final Log LOG=LogFactory.getLog(Calculos.class);
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
    try {
		  String password = BouncyEncryption.decrypt(Configuracion.getInstance().getPropiedad("sistema.autenticar.contrasenia"));
      Autentifica autentifica= new Autentifica();
			if (autentifica.tieneAccesoBD(Configuracion.getInstance().getPropiedad("sistema.autenticar.cuenta"), password, "127.0.0.1")) {
				autentifica.loadSucursales();
				 Transaccion transaccion= new Transaccion(1L, 74L, autentifica); // contratista patrocinio
				// Transaccion transaccion= new Transaccion(1L, autentifica, 1L); 
				if(transaccion.ejecutar(EAccion.REPROCESAR))
					LOG.info("Se procesó la nómina.");
				else
					LOG.info("Ocurrió un error en el proceso de nómina.");
			} // if
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
		// LOG.info("=ROUND({HOLA})".replace("{HOLA}", "1222.12"));
	}

}
