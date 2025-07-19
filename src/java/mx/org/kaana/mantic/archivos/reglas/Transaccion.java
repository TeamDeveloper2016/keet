package mx.org.kaana.mantic.archivos.reglas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticArchivosDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG              = LogFactory.getLog(Transaccion.class);
	private static final Long FILE_DEPURACION = 2L;
	private static final Long FILE_ELIMINADO  = 3L;
	private static final Long FILE_NO_ENCONTRAADO = 4L;
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
					this.files= this.toArchivos(sesion);
					if(!this.files.isEmpty())
						this.depuracion(sesion);
					break;
				case PROCESAR:
					if(!this.files.isEmpty())
						this.depuracion(sesion);
					break;
        case MODIFICAR:
          this.toCheckEstaciones(sesion);
          break;
			} // switch
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
			throw e;
		} // catch
		return regresar;
	} // ejecutar	
	
	private List<TcManticArchivosDto> toArchivos(Session sesion) throws Exception {
		List<TcManticArchivosDto> regresar= null;
		Map<String, Object>params         = new HashMap<>();
		try {
			params.put("idEliminado", FILE_DEPURACION);
			regresar= DaoFactory.getInstance().toEntitySet(sesion, TcManticArchivosDto.class, "TcManticArchivosDto", "eliminar", params, Constantes.SQL_TODOS_REGISTROS);
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
		File eliminar= null;
		try {
			for(TcManticArchivosDto item: this.files) {
				eliminar= new File(item.getAlias());
				try {
					if(eliminar.exists()) {
						if(eliminar.delete())
							item.setIdEliminado(FILE_ELIMINADO);
            else
							item.setIdEliminado(FILE_NO_ENCONTRAADO);
					  DaoFactory.getInstance().update(sesion, item);
					} // if
					else
						LOG.info("No se encontró el archivo: " + item.getAlias());
				} // try
				catch (Exception e) {					
					Error.mensaje(e);					
					LOG.info("Ocurrió un error al eliminar el archivo: " + item.getAlias());
				} // catch				
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // depuracion
  
  private void toCheckEstaciones(Session sesion) throws Exception {
    try {   
      LOG.error("VERIFICANDO ESTACIONES DE LOS CONTRATISTAS");
      this.toCheckEstaciones(sesion, "constratistas");
      LOG.error("VERIFICANDO ESTACIONES DE LOS PROVEEDORES");
      this.toCheckEstaciones(sesion, "proveedores");
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
    
  private void toCheckEstaciones(Session sesion, String idXml) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {      
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaEstacionesDto", idXml, params);
      if(!Objects.equals(items, null) && !items.isEmpty()) {
        for (Entity item: items) {
          LOG.error("Check estacion: "+ item.getKey()+ " | "+ item.toDouble("porcentaje")+ " | "+ item.toTimestamp("registro"));
          params.put("idEstacion", item.getKey());      
          DaoFactory.getInstance().updateAll(sesion, TcKeetEstacionesDto.class, params, "estatus");
        } // for
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
    
}