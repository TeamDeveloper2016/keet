package mx.org.kaana.keet.procesos.reglas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetProcesosDto;
import mx.org.kaana.keet.db.dto.TcKeetSubProcesosDto;
import mx.org.kaana.keet.procesos.beans.Proceso;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private Proceso proceso;	
	private String messageError;

	public Transaccion(Proceso proceso) {
		this.proceso= proceso;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = Boolean.FALSE;
    TcKeetProcesosDto item    = null;
    TcKeetSubProcesosDto sub  = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.proceso.setIdUsuario(JsfBase.getIdUsuario());
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registro el subproceso");
			switch(accion) {
				case AGREGAR:
				case MODIFICAR:
          if(Objects.equals(this.proceso.getIdProceso(), Constantes.VALUE_OF_LIMIT)) {
            item= new TcKeetProcesosDto(-1L, this.proceso.getProceso(), this.proceso.getProceso());
            DaoFactory.getInstance().insert(sesion, item);
          } // if  
          else {
            item= (TcKeetProcesosDto)DaoFactory.getInstance().findById(sesion, TcKeetProcesosDto.class, this.proceso.getIdProceso());
            if(!Objects.equals(item, null)) {
              item.setNombre(this.proceso.getProceso());
              DaoFactory.getInstance().update(sesion, item);
            } // if  
          } // else
          this.proceso.setIdProceso(item.getIdProceso());
          if(Objects.equals(this.proceso.getIdSubProceso(), Constantes.VALUE_OF_LIMIT)) 
            regresar= DaoFactory.getInstance().insert(sesion, this.proceso)>= 0L;
          else {
            sub= (TcKeetSubProcesosDto)DaoFactory.getInstance().findById(sesion, TcKeetSubProcesosDto.class, this.proceso.getIdSubProceso());
            if(!Objects.equals(sub, null)) {
              sub.setIdProceso(item.getIdProceso());
              sub.setNombre(this.proceso.getNombre());
              regresar= DaoFactory.getInstance().update(sesion, sub)>= 0L;
            } // if  
          } // else
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.proceso)>= 1L;
          params.put("idProceso", this.proceso.getIdProceso());
          Entity size= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetSubProcesosDto", "size", params);
          if(!Objects.equals(size, null) && !size.isEmpty()) {
            if(Objects.equals(size.toLong("total"), 0L))
  					  DaoFactory.getInstance().delete(sesion, TcKeetProcesosDto.class, this.proceso.getIdProceso());
          } // if
  				break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;
	}	// ejecutar
	
}