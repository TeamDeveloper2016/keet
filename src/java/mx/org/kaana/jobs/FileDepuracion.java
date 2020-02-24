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

public class FileDepuracion implements Job, Serializable {
	
	private static final Log LOG              = LogFactory.getLog(FileDepuracion.class);
	private static final long serialVersionUID= 338165937836679066L;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {		
		Transaccion transaccion= null;
		try {
			LOG.info("Iniciando la depuración de archivos.");
			transaccion= new Transaccion();
			if(transaccion.ejecutar(EAccion.DEPURAR))
				LOG.info("Se finalizo la depuración de archivos de forma correcta.");
			else
				LOG.info("Ocurrio un error al realizar la depuración de archivos.");
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
			LOG.error("Ocurrio un error al realizar la depuracion de archivos.");
		} // catch		
	} // execute		
}
