package mx.org.kaana.mantic.archivos.reglas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticArchivosDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx{

	
	private static final Log LOG              = LogFactory.getLog(Transaccion.class);
	private static final Long FILE_DEPURACION= 1L;
	private static final Long FILE_ELIMINADO = 3L;
	public List<TcManticArchivosDto> files;

	public Transaccion() {
		this(new ArrayList<>());
	} // Transaccion

	public Transaccion(List<TcManticArchivosDto> files) {
		this.files = files;
	}	// Transaccion
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar= true;
		try {
			switch(accion){
				case DEPURAR:
					this.files= toArchivos(sesion);
					if(!this.files.isEmpty())
						depuracion(sesion);
					break;
				case PROCESAR:
					if(!this.files.isEmpty())
						depuracion(sesion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			
		} // finally
		return regresar;
	} // ejecutar	
	
	private List<TcManticArchivosDto> toArchivos(Session sesion) throws Exception{
		List<TcManticArchivosDto> regresar= null;
		Map<String, Object>params         = null;
		try {
			params= new HashMap<>();
			params.put("idEliminado", FILE_DEPURACION);
			regresar= DaoFactory.getInstance().toEntitySet(sesion, TcManticArchivosDto.class, "TcManticArchivosDto", "depuracion", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toArchivos
	
	public void depuracion(Session sesion) throws Exception {
		File file= null;
		try {
			for(TcManticArchivosDto fileDepuracion: this.files) {
				file= new File(fileDepuracion.getAlias());
				try {
					if(file.exists()) {
						if(file.delete()) {					
							fileDepuracion.setIdEliminado(FILE_ELIMINADO);
							DaoFactory.getInstance().update(sesion, fileDepuracion);
						} // if
					} // if
					else
						LOG.info("No se encontro el archivo: " + fileDepuracion.getAlias());
				} // try
				catch (Exception e) {					
					Error.mensaje(e);					
					LOG.info("Ocurrió un error al eliminar el archivo: " + fileDepuracion.getAlias());
				} // catch				
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // depuracion
}