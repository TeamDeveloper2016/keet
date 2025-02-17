package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.keet.nomina.reglas.Calculos;
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
	private EAccion accion;	

	public Monitoreo getMonitoreo() {
		return monitoreo;
	}
  
  @PostConstruct
  @Override
  protected void init() {
    Map<String, Object> params= new HashMap<>();
    try {
			Long idNomina = (Long)JsfBase.getFlashAttribute("idNomina");
			this.accion   = (EAccion)JsfBase.getFlashAttribute("accion");
			this.monitoreo= JsfBase.toProgressMonitor().progreso("NOMINA");
			this.attrs.put("idNomina", idNomina);
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("nomina", JsfBase.getFlashAttribute("nomina"));
      this.attrs.put("tuplas", JsfBase.getFlashAttribute("tuplas"));
      this.attrs.put("value", "0");
      this.attrs.put("cancelar", Boolean.FALSE);
			if(!Cadena.isVacio(idNomina)) {
				params.put("idNomina", idNomina);
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
				Nomina nomina= (Nomina)DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "nomina", params);			
				Value value= DaoFactory.getInstance().toField("VistaNominaDto", Objects.equals(nomina.getIdTipoNomina(), 1L)? "ordinaria": "complementaria", params, "tuplas");
				if(value!= null && value.getData()!= null)
					this.attrs.put("tuplas", value.toLong());
        this.attrs.put("nomina", nomina);
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

	public void doCancelar() {
		this.monitoreo.terminar();
    this.attrs.put("cancelar", this.monitoreo.isCorriendo());
		UIBackingUtilities.execute("cancel();");
	}
	
	public String doRegresar() {
		JsfBase.setFlashAttribute("idNomina", this.attrs.get("idNomina"));
		String retorno= (String)this.attrs.get("retorno");
		return retorno== null? "filtro".concat(Constantes.REDIRECIONAR): retorno.concat(Constantes.REDIRECIONAR);
	}

  public void doProgreso() {
		this.attrs.put("value", this.monitoreo.getPorcentaje());
		this.attrs.put("cancelar", this.monitoreo.isCorriendo());
	}	
	
  public void doAceptar() {
		Calculos calculos= null;
		try {		
      if(!this.monitoreo.isCorriendo()) {
        Long idNomina= (Long)this.attrs.get("idNomina");
        calculos     = new Calculos(idNomina, JsfBase.getAutentifica(), ((Nomina)this.attrs.get("nomina")).getIdNotificar(), (Long)this.attrs.get("tuplas"));
        if(calculos.ejecutar(this.accion))
          JsfBase.addMessage("Se procesó la nómina con éxito", ETipoMensaje.INFORMACION);
        else
          JsfBase.addMessage("Ocurrió un error en el proceso de nómina", ETipoMensaje.ALERTA);	
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} // doAceptar

}
