package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosDestajosRechazosExtras")
@ViewScoped
public class RechazosExtras extends Rechazos implements Serializable {

	private static final long serialVersionUID = 154600879172477099L;	 
	
	@Override
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;	
		List<Entity>puntos     = null;
    try {						
			puntos= this.toPuntos();
			this.selecteds= new Entity[puntos.size()];
			for(int count=0; count<puntos.size(); count++)
				this.selecteds[count]= puntos.get(count);
			if(this.selecteds.length>=1) {				
				transaccion= new Transaccion(this.loadRevision(), EEstacionesEstatus.EN_PROCESO.getKey());
				if(transaccion.ejecutar(EAccion.ELIMINAR)) {
					JsfBase.addMessage("Rechazo de puntos de revisión", "Se realizó el rechazo de los puntos de revision de forma correcta", ETipoMensaje.INFORMACION);
					regresar= this.doCancelar();
				} // if
				else
					JsfBase.addMessage("Rechazo de puntos de revisión", "Ocurrió un error al realizar el rechazo de los puntos de revision", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Rechazo de puntos de revisión", "Es necesario seleccionar por lo menos un punto de revisión", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	private List<Entity> toPuntos() throws Exception{
		List<Entity> regresar     = null;
		Map<String, Object> params= null;
		try {
      params= this.toPrepare();
      Entity figura= (Entity)this.attrs.get("figura");
      params.put("idProveedor", -1L);
      params.put("idEmpresaPersona", -1L);
      if(figura!= null)
        if(Objects.equals(figura.toLong("tipo"), 1L))
          params.put("idEmpresaPersona", new Long(figura.getKey().toString().substring(4)));
        else
          params.put("idProveedor", new Long(figura.getKey().toString().substring(4)));
			regresar= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", "puntosRechazos", params);
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