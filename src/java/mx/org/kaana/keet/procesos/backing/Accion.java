package mx.org.kaana.keet.procesos.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.procesos.beans.Proceso;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.procesos.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetProcesosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639361L;
	private Proceso proceso;
  private EAccion accion;
  
  public Proceso getProceso() {
    return proceso;
  }

  public void setProceso(Proceso proceso) {
    this.proceso = proceso;
  }

  public Boolean getConsulta() {
    return Objects.equals(this.accion, EAccion.AGREGAR) ||  Objects.equals(this.accion, EAccion.MODIFICAR);
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      this.attrs.put("idSubProceso", JsfBase.getFlashAttribute("idSubProceso")== null? -1L: JsfBase.getFlashAttribute("idSubProceso"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
      this.toLoadProcesos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

  private void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      switch (this.accion) {
        case AGREGAR:											
          this.proceso= new Proceso();
          break;
        case MODIFICAR:					
        case CONSULTAR:
          params.put(Constantes.SQL_CONDICION, "tc_keet_sub_procesos.id_sub_proceso= "+ this.attrs.get("idSubProceso"));
          this.proceso= (Proceso)DaoFactory.getInstance().toEntity(Proceso.class, "TcKeetSubProcesosDto", params);
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally          
  }

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			transaccion = new Transaccion(this.proceso);
			if (transaccion.ejecutar(this.accion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agregó": "modificó").concat(" el registro del sub proceso de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el sub proceso", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idSubProceso", this.proceso.getIdSubProceso());   
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
	
  private void toLoadProcesos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> procesos= UISelect.otra("TcKeetProcesosDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("procesos", procesos);
      if(procesos!= null && !procesos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) {
          this.proceso.setIdProceso((Long)procesos.get(procesos.size()- 1).getValue());
          this.proceso.setProceso("");
        } // if  
        else {
          int index= procesos.indexOf(new UISelectItem(this.proceso.getIdProceso()));
          if(index>= 0)
            this.proceso.setProceso(procesos.get(index).getLabel());
        } // else
      } // if  
      this.doLoadSubprocesos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public void doLoadSubprocesos() {
    Map<String, Object> params= new HashMap<>();
    try {
      List<UISelectItem> procesos= (List<UISelectItem>)this.attrs.get("procesos");
      if(procesos!= null && !procesos.isEmpty()) {
        if(!Objects.equals(this.proceso.getIdProceso(), 999999L)) {
          int index= procesos.indexOf(new UISelectItem(this.proceso.getIdProceso()));
          if(index>= 0)
            this.proceso.setProceso(procesos.get(index).getLabel());
        } //if  
        else
          this.proceso.setProceso("");
      } // if
			params.put("idProceso", this.proceso.getIdProceso());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> subProcesos= UISelect.otra("TcKeetSubProcesosDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subprocesos", subProcesos);
      if(subProcesos!= null && !subProcesos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) { 
          this.proceso.setIdSubProceso((Long)subProcesos.get(subProcesos.size()- 1).getValue());
          this.proceso.setNombre("");
        } // if  
        else {
          int index= subProcesos.indexOf(new UISelectItem(this.proceso.getIdSubProceso()));
          if(index>= 0)
            this.proceso.setNombre(subProcesos.get(index).getLabel());
        } // else
      } // if  
      else
        this.proceso.setNombre("");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public void doLoadItem() {
    try {
      List<UISelectItem> subProcesos= (List<UISelectItem>)this.attrs.get("subprocesos");
      if(subProcesos!= null && !subProcesos.isEmpty()) {
        if(!Objects.equals(this.proceso.getIdSubProceso(), 999999L)) {
          int index= subProcesos.indexOf(new UISelectItem(this.proceso.getIdSubProceso()));
          if(index>= 0)
            this.proceso.setNombre(subProcesos.get(index).getLabel());
        } //if  
        else
          this.proceso.setNombre("");
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  }  
  
}