package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 17/12/2018
 *@time 07:51:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "keetNominasProgreso")
@ViewScoped
public class Progreso extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 8793667741599428311L;

	private Monitoreo monitoreo;	
	
  @PostConstruct
  @Override
  protected void init() {
    Map<String, Object> params= new HashMap<>();
    try {
      params= new HashMap<>(); 
			Long idNomina= (Long)JsfBase.getFlashAttribute("idNomina");
			this.attrs.put("idNomina", idNomina);
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.monitoreo= JsfBase.getAutentifica().getMonitoreo();
      this.attrs.put("value", "0");
      this.attrs.put("cancelar", false);
			if(!Cadena.isVacio(idNomina)) {
				params.put("idNomina", idNomina);
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
				Nomina nomina= (Nomina)DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "nomina", params);			
				Value value= DaoFactory.getInstance().toField("VistaNominaDto", nomina.getIdTipoNomina()== 1L? "ordinaria": "complementaria", params, "tuplas");
				if(value!= null && value.getData()!= null)
					this.attrs.put("tuplas", value.toLong());
				UIBackingUtilities.execute("procesar();");
			} // if
			else 
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // init

	public Monitoreo getMonitoreo() {
		return monitoreo;
	}
 
	public void doCancelar() {
		this.monitoreo.terminar();
    this.attrs.put("cancelar", this.monitoreo.isCorriendo());
		UIBackingUtilities.execute("cancel();");
	}
	
	public String doRegresar() {
		String retorno= (String)this.attrs.get("retorno");
		return retorno== null? "filtro".concat(Constantes.REDIRECIONAR): retorno.concat(Constantes.REDIRECIONAR);
	}

  public void doProgreso() {
		this.attrs.put("value", this.monitoreo.getPorcentaje());
		this.attrs.put("cancelar", this.monitoreo.isCorriendo());
	}	
	
  public void doAceptar() {
		Transaccion transaccion= null;
		try {		
			Long idNomina= (Long)this.attrs.get("idNomina");
 			transaccion= new Transaccion(idNomina, JsfBase.getAutentifica());
			if(transaccion.ejecutar(EAccion.AGREGAR))
				JsfBase.addMessage("Se procesó la nómina con éxito.", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Ocurrió un error en el proceso de nómina.", ETipoMensaje.ALERTA);	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} // doAceptar

}
