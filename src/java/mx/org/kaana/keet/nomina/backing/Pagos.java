package mx.org.kaana.keet.nomina.backing;

import java.io.InputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.keet.nomina.reglas.Desglose;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetNominasPagos")
@ViewScoped
public class Pagos extends Filtro implements Serializable {

	private static final long serialVersionUID= 6319984968937774151L;
  private static final Log LOG = LogFactory.getLog(Pagos.class);
  
  @Override
  public StreamedContent getPersonas() {
		StreamedContent regresar= null;		
    Desglose personas= null;
		try {
	  	personas   = new Desglose(((Entity)this.attrs.get("seleccionado")).getKey());
      String name= personas.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
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
      super.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
}
	