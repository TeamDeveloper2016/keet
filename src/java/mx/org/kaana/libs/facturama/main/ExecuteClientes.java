package mx.org.kaana.libs.facturama.main;

import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.facturama.reglas.Facturama;
import mx.org.kaana.libs.formato.Error;

public class ExecuteClientes {

	public static void main(String[] args) {
		Facturama transaccion= null;
		try {
			transaccion= new Facturama();
			transaccion.ejecutar(EAccion.PROCESAR);				
		} // try // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	}	// main
}