package mx.org.kaana.keet.paquetes.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.paquetes.beans.Paquete;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.paquetes.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;

@Named(value = "keetPaquetesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793167742599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idPaquete", JsfBase.getFlashAttribute("idPaquete"));
      if(this.attrs.get("idPaquete")!= null) 
			  this.doLoad();
      this.toLoadDesarrollos();
      this.toLoadProcesos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_keet_paquetes.registro desc");
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("prototipo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("proceso", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("subProceso", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaPaquetesDto", params, columns);
      UIBackingUtilities.resetDataTable();
			this.attrs.put("idPaquete", null);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idPaquete")) && !this.attrs.get("idPaquete").toString().equals("-1"))
  		sb.append("(tc_keet_paquetes.id_paquete=").append(this.attrs.get("idPaquete")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !Objects.equals(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey(), -1L))
  		sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(this.attrs.get("idDesarrollo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idPrototipo")) && !Objects.equals(((UISelectEntity)this.attrs.get("idPrototipo")).getKey(), -1L))
  		sb.append("(tc_keet_prototipos.id_prototipo= ").append(this.attrs.get("idPrototipo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idProceso")) && !Objects.equals((Long)this.attrs.get("idProceso"), -1L))
  		sb.append("(tc_keet_procesos.id_proceso= ").append(this.attrs.get("idProceso")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idSubProceso")) && !Objects.equals((Long)this.attrs.get("idSubProceso"), -1L))
  		sb.append("(tc_keet_sub_procesos.id_sub_proceso= ").append(this.attrs.get("idSubProceso")).append(") and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;
	}

  public String doAccion(String accion) {
		String regresar= "accion";
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
		  JsfBase.setFlashAttribute("retorno", "filtro");		
		  JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("idPaquete", (eaccion.equals(EAccion.MODIFICAR)||eaccion.equals(EAccion.CONSULTAR)) ? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  } 

  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			transaccion= new Transaccion(new Paquete(seleccionado.getKey()));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El paquete se ha eliminado correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el paquete", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } 

  private void toLoadDesarrollos() {
		Map<String, Object>params= new HashMap<>();
		try {
      if(JsfBase.isAdminEncuestaOrAdmin())
		    params.put("idEmpresaPersona", -1);
      else  
		    params.put("idEmpresaPersona", JsfBase.getAutentifica().getEmpresa().getIdEmpresaPersonal());
			List<UISelectEntity> desarrollos= UIEntity.build("VistaPaquetesDto", "desarrollos", params);
      this.attrs.put("desarrollos", desarrollos);
      this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));
      this.doLoadPrototipos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
    finally {
			Methods.clean(params);
		}	// finally
	} 

  public void doLoadPrototipos() {
		List<UISelectEntity>prototipos= null;
    Map<String, Object> params    = new HashMap<>();
    try {
      params.put("idDesarrollo", ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey());
  		prototipos= UIEntity.seleccione("VistaPaquetesDto", "prototipos", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      attrs.put("prototipos", prototipos);
      this.attrs.put("idPrototipo", UIBackingUtilities.toFirstKeySelectEntity(prototipos));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
  }
  
  private void toLoadProcesos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectItem> procesos= UISelect.seleccione("TcKeetProcesosDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("procesos", procesos);
      this.attrs.put("idProceso", UIBackingUtilities.toFirstKeySelectItem(procesos));
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
			params.put(Constantes.SQL_CONDICION, "tc_keet_sub_procesos.id_proceso= "+ this.attrs.get("idProceso"));
      List<UISelectItem> subProcesos= UISelect.seleccione("TcKeetSubProcesosDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subprocesos", subProcesos);
      this.attrs.put("idSubProceso", UIBackingUtilities.toFirstKeySelectItem(subProcesos));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }    
  
}
