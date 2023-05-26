package mx.org.kaana.sakbe.catalogos.tipos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.catalogos.tipos.beans.TipoMaquinaria;
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
	private static final long serialVersionUID=-3186327186737673670L;
 
	private TipoMaquinaria maquinaria;	
	private String messageError;

	public Transaccion(TipoMaquinaria maquinaria) {
    this.maquinaria= maquinaria;
	} // Transaccion

	public String getMessageError() {
		return messageError;
	} // getMessageError

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
		try {
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la maquinaria");
			switch(accion) {
				case AGREGAR:
					regresar= DaoFactory.getInstance().insert(sesion, this.maquinaria)> 0L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.maquinaria)> 0L;
					break;				
				case ELIMINAR:
          params.put("idTipoMaquinaria", this.maquinaria.getIdTipoMaquinaria());
          regresar= DaoFactory.getInstance().delete(sesion, this.maquinaria)> 0L;
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
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar
  
} 