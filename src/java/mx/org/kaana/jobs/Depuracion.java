package mx.org.kaana.jobs;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.mantic.archivos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Error;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Depuracion implements Job, Serializable {
	
	private static final Log LOG              = LogFactory.getLog(Depuracion.class);
	private static final long serialVersionUID= 338165937836679066L;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {		
		Transaccion transaccion= null;
		try {
			LOG.error("INICIANDO DEPURACION DE ARCHIVOS TEMPORALES");
			transaccion= new Transaccion();
			transaccion.ejecutar(EAccion.DEPURAR);
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
			LOG.error("Ocurrio un error al realizar la depuracion de archivos.");
		} // catch	
    
		try {
			LOG.error("INICIANDO LA VERIFICACION DE ESTACIONES");
			transaccion= new Transaccion();
			transaccion.ejecutar(EAccion.MODIFICAR);
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
			LOG.error("Ocurrio un error en la verifiacion de estaciones");
		} // catch	
	} // execute		
}
