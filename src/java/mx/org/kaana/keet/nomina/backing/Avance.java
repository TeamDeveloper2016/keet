package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 17/12/2018
 *@time 07:51:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "keetNominasAvance")
@ViewScoped
public class Avance extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 8793667741599428311L;
  private static final Log LOG = LogFactory.getLog(Avance.class);
  
	private Monitoreo monitoreo;	

	public Monitoreo getMonitoreo() {
		return monitoreo;
	}

  public Boolean getAdmin() {
    Boolean regresar= Boolean.TRUE;
    try {      
      regresar= JsfBase.isAdmin() && !Objects.equals(this.monitoreo, null) && this.monitoreo.isCorriendo();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  } 
  
  @PostConstruct
  @Override
  protected void init() {
    try {
			this.monitoreo= JsfBase.toProgressMonitor().progreso("NOMINA");
      this.attrs.put("tuplas", this.monitoreo.getTotal());
      if(this.monitoreo.isCorriendo())
        UIBackingUtilities.execute("startTask()");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	public String doCancelar() {
    String regresar= "filtro";
    try {      
      if(this.monitoreo.isCorriendo()) {
        this.monitoreo.cancelar();
        this.attrs.put("cancelar", this.monitoreo.isCorriendo());
        UIBackingUtilities.execute("cancel()");
        LOG.error("SE CANCELO LA NOMINA POR <<<< ".concat(JsfBase.getAutentifica().getPersona().getNombreCompleto()).concat(" >>>>>"));
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar.concat(Constantes.REDIRECIONAR);
	}
	
	public String doRegresar() {
    String regresar= "filtro";
    try {      
      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar.concat(Constantes.REDIRECIONAR);
	}
	
  public void doProgreso() {
		this.attrs.put("value", this.monitoreo.getPorcentaje());
		this.attrs.put("cancelar", this.monitoreo.isCorriendo());
	}	

}
