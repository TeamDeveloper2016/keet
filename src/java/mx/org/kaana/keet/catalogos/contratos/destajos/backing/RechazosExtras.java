package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;
import mx.org.kaana.libs.pagina.JsfBase;

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
			puntos= toPuntos();
			this.selecteds= new Entity[puntos.size()];
			for(int count=0; count<puntos.size(); count++)
				this.selecteds[count]= puntos.get(count);
			if(this.selecteds.length>=1){				
				transaccion= new Transaccion(loadRevision());
				if(transaccion.ejecutar(EAccion.ELIMINAR)){
					JsfBase.addMessage("Rechazo de puntos de revisión", "Se realizó el rechazo de los puntos de revision de forma correcta.", ETipoMensaje.INFORMACION);
					regresar= doCancelar();
				} // if
				else
					JsfBase.addMessage("Rechazo de puntos de revisión", "Ocurrió un error al realizar el rechazo de los puntos de revision.", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Rechazo de puntos de revisión", "Es necesario seleccionar por lo menos un punto de revisión.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	private List<Entity> toPuntos() throws Exception{
		List<Entity> regresar= null;
		try {
			regresar= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", "puntosRechazos", toPrepare());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPuntos
}