package mx.org.kaana.sakbe.catalogos.maquinaria.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.catalogos.maquinaria.beans.Maquinaria;
import mx.org.kaana.sakbe.db.dto.TcSakbeMaquinariasBitacoraDto;
import mx.org.kaana.sakbe.db.dto.TrSakbeMaquinariaDesarrolloDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx implements Serializable {

  private static final Log LOG = LogFactory.getLog(Transaccion.class);
	private static final long serialVersionUID=-3186367186737673670L;
 
	private Maquinaria maquinaria;	
	private String messageError;
	private TcSakbeMaquinariasBitacoraDto bitacora;

	public Transaccion(Maquinaria maquinaria, TcSakbeMaquinariasBitacoraDto bitacora) {
		this(maquinaria);
		this.bitacora= bitacora;
	} // Transaccion
	
	public Transaccion(Maquinaria maquinaria) {
    this.maquinaria= maquinaria;
	} // Transaccion

	public String getMessageError() {
		return messageError;
	} // getMessageError

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                      = false;
		TcSakbeMaquinariasBitacoraDto registro= null;
    Map<String, Object> params            = new HashMap<>();
		try {
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la maquinaria");
			switch(accion) {
				case AGREGAR:
					DaoFactory.getInstance().insert(sesion, this.maquinaria);
					registro= new TcSakbeMaquinariasBitacoraDto(this.maquinaria.getIdMaquinaria(), "", JsfBase.getIdUsuario(), this.maquinaria.getIdMaquinariaEstatus(), -1L);
          DaoFactory.getInstance().insert(sesion, registro);
          regresar= this.toChangeDesarrollo(sesion);
					break;
				case MODIFICAR:
					DaoFactory.getInstance().update(sesion, this.maquinaria);
					registro= new TcSakbeMaquinariasBitacoraDto(this.maquinaria.getIdMaquinaria(), "", JsfBase.getIdUsuario(), this.maquinaria.getIdMaquinariaEstatus(), -1L);
				  DaoFactory.getInstance().insert(sesion, registro);
          regresar= this.toChangeDesarrollo(sesion);
					break;				
				case ELIMINAR:
          params.put("idMaquinaria", this.maquinaria.getIdMaquinaria());
          DaoFactory.getInstance().deleteAll(sesion, TcSakbeMaquinariasBitacoraDto.class, params);
          DaoFactory.getInstance().deleteAll(sesion, TrSakbeMaquinariaDesarrolloDto.class, params);
          regresar= DaoFactory.getInstance().delete(sesion, this.maquinaria)> 0L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.maquinaria.setIdMaquinariaEstatus(this.bitacora.getIdMaquinariaEstatus());
						DaoFactory.getInstance().update(sesion, this.maquinaria);
  					registro= new TcSakbeMaquinariasBitacoraDto(this.maquinaria.getIdMaquinaria(), "", JsfBase.getIdUsuario(), this.maquinaria.getIdMaquinariaEstatus(), -1L);
	  			  regresar= DaoFactory.getInstance().insert(sesion, registro)>= 1L;
					} // if
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
			Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar
  
  private Boolean toChangeDesarrollo(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {    
      if(!Objects.equals(this.maquinaria.getIdDesarrollo(), this.maquinaria.getIkDesarrollo().getKey()) && !Objects.equals(this.maquinaria.getIdDesarrollo(), -1L)) {
        params.put("idDesarrollo", this.maquinaria.getIdDesarrollo());
        params.put("idMaquinaria", this.maquinaria.getIdMaquinaria());      
        Entity item= (Entity)DaoFactory.getInstance().toEntity("TrSakbeMaquinariaDesarrolloDto", "buscar", params);
        if(item== null || item.isEmpty()) {
          TrSakbeMaquinariaDesarrolloDto value= new TrSakbeMaquinariaDesarrolloDto(
            this.maquinaria.getIdMaquinaria(), // Long idMaquinaria, 
            this.maquinaria.getIdDesarrollo(), // Long idDesarrollo, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            -1L // Long idMaquinariaDesarrollo
          );
          DaoFactory.getInstance().insert(value);
        } // if  
        else {
          params.put("idMaquinariaDesarrollo", item.getKey());      
          DaoFactory.getInstance().updateAll(TrSakbeMaquinariaDesarrolloDto.class, params);
        } // if
        TcSakbeMaquinariasBitacoraDto registro= new TcSakbeMaquinariasBitacoraDto(this.maquinaria.getIdMaquinaria(), "CAMBIO DE DESARROLLO", JsfBase.getIdUsuario(), this.maquinaria.getIdMaquinariaEstatus(), -1L);
        regresar= DaoFactory.getInstance().insert(sesion, registro)>= 1L;
      }  // if
      else
        regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
} 