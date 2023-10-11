package mx.org.kaana.keet.entregas.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.entregas.reglas.AdminEntregas;
import mx.org.kaana.keet.entregas.beans.Material;
import mx.org.kaana.keet.entregas.reglas.Transaccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

@Named(value = "keetEntregasAccion")
@ViewScoped
public class Accion extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 1293667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Accion.class);

  private AdminEntregas orden;
  private EAccion accion;
  private FormatLazyModel lazyDetalle;
  private Long idEntrega;

  public AdminEntregas getOrden() {
    return orden;
  }

  public void setOrden(AdminEntregas orden) {
    this.orden = orden;
  }

  public FormatLazyModel getLazyDetalle() {
    return lazyDetalle;
  }
  
  public Boolean getVisible() {
    return Objects.equals(this.accion, EAccion.AGREGAR);
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			opcion      = (EOpcionesResidente)JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long)JsfBase.getFlashAttribute("idDesarrollo");			
      this.accion = EAccion.AGREGAR;
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("seleccionado", JsfBase.getFlashAttribute("seleccionado"));				
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));				
      this.attrs.put("idCasa", JsfBase.getFlashAttribute("idCasa"));				
      this.attrs.put("idProceso", JsfBase.getFlashAttribute("idProceso"));				
      this.attrs.put("idSubProceso", JsfBase.getFlashAttribute("idSubProceso"));				
      this.attrs.put("idFirstProceso", Objects.equals(this.attrs.get("idProceso"), null));
      this.attrs.put("idFirstSubProceso", Objects.equals(this.attrs.get("idSubProceso"), null));
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("hoy", LocalDate.now());
      this.toLoadProcesos();
      this.idEntrega= -1L;
      this.toLoadAlmacenes();
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
  
  @Override
	public void doLoad() {
    List<Columna> columns    = new ArrayList<>();		
		Map<String, Object>params= new HashMap<>();
    try {   
      params.put("idContratoLote", ((Entity)this.attrs.get("seleccionado")).toLong("idContratoLote"));
      params.put(Constantes.SQL_CONDICION, "is not null");
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("autorizo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("recibio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel= new FormatLazyModel("VistaProcesosDto", "paquetes", params, columns);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  }

	public void doRowToggle(ToggleEvent event) {
		try {
			this.attrs.put("paquete", (Entity) event.getData());
			if (!event.getVisibility().equals(Visibility.HIDDEN)) 
				this.toLoadDetalle();
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} 
  
	private void toLoadDetalle() {
    List<Columna> columns    = new ArrayList<>();		
		Map<String, Object>params= new HashMap<>();
    try {   
      params.put("sortOrder", "order by tc_keet_entregas_detalles.id_entrega_detalle");
      params.put("idEntrega", ((Entity)this.attrs.get("paquete")).toLong("idEntrega"));
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyDetalle= new FormatLazyModel("VistaProcesosDto", "detalle", params, columns);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  }
  
	public String doAceptar() {
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			transaccion= new Transaccion(this.orden);
			if (transaccion.ejecutar(this.accion)) {
        this.idEntrega= -1L;
				regresar      = this.doCancelar();
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agregó": "modificó").concat(" la entrega de material"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la entrega de material", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  }
  
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("opcion", opcion);			
			JsfBase.setFlashAttribute("opcionResidente", opcion);						
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));			
			JsfBase.setFlashAttribute("idCasa", this.attrs.get("idCasa"));			
			JsfBase.setFlashAttribute("idProceso", this.attrs.get("idProceso"));			
			JsfBase.setFlashAttribute("idSubProceso", this.attrs.get("idSubProceso"));			
  		regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 
 
  private void toLoadProcesos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idDesarrollo", ((Entity)this.attrs.get("seleccionado")).toLong("idDesarrollo"));
			params.put("idPrototipo", ((Entity)this.attrs.get("seleccionado")).toLong("idPrototipo"));
      List<UISelectItem> procesos= UISelect.seleccione("VistaProcesosDto", "procesos", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("procesos", procesos);
      if(procesos!= null && !procesos.isEmpty()) 
        if((Boolean)this.attrs.get("idFirstProceso") || Objects.equals(procesos.size(), 1))
          this.attrs.put("idProceso", procesos.get(0).getValue());
        else
          this.attrs.put("idFirstProceso", Boolean.TRUE);
      else
        this.attrs.put("idProceso", -1L);
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
			params.put("idContratoLote", ((Entity)this.attrs.get("seleccionado")).toLong("idContratoLote"));
			params.put("idProceso", this.attrs.get("idProceso"));
      List<UISelectItem> subProcesos= UISelect.seleccione("TcKeetSubProcesosDto", "unicos", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subprocesos", subProcesos);
      if(subProcesos!= null && !subProcesos.isEmpty()) 
        if((Boolean)this.attrs.get("idFirstSubProceso") || Objects.equals(subProcesos.size(), 1))
          this.attrs.put("idSubProceso", subProcesos.get(0).getValue());
        else
          this.attrs.put("idFirstSubProceso", Boolean.TRUE);          
      else
        this.attrs.put("idSubProceso", -1L);
      this.doLoadMateriales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  									
 
  public void doLoadMateriales() {
    try {   
      Entity seleccionado= (Entity)this.attrs.get("seleccionado");
      this.orden= new AdminEntregas(seleccionado.toLong("idDesarrollo"), seleccionado.toLong("idPrototipo"), (Long)this.attrs.get("idProceso"), (Long)this.attrs.get("idSubProceso"));
      this.orden.getEntrega().setIdContratoLote(seleccionado.toLong("idContratoLote"));
      this.attrs.put("articulos", this.orden.getMateriales().size());
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
 }
  
  public void doUpdate(Material row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
  }
 
	public void doAutorizan(SelectEvent event) {
		List<UISelectEntity> empleados= null;
		try {
			empleados= (List<UISelectEntity>) this.attrs.get("autorizan");
      int index= empleados.indexOf((UISelectEntity)event.getObject());
      if(index>= 0)
			  this.orden.setIkSolicita(empleados.get(index));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 
  
	public void doReciben(SelectEvent event) {
		List<UISelectEntity> empleados= null;
		try {
			empleados= (List<UISelectEntity>) this.attrs.get("reciben");
      int index= empleados.indexOf((UISelectEntity)event.getObject());
      if(index>= 0)
			  this.orden.setIkRecibe(empleados.get(index));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 
  
	public List<UISelectEntity> doCompleteAutorizan(String query) {
		return this.toLoadNombresEmpleados(query, "autorizan");
	}	
	
	public List<UISelectEntity> doCompleteReciben(String query) {
		return this.toLoadNombresEmpleados(query, "reciben");
	}	
	
	private List<UISelectEntity> toLoadNombresEmpleados(String query, String name) {
    List<UISelectEntity> regresar= null;
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));			
			query= !Cadena.isVacio(query)? query.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";		
  		params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			params.put("nombreEmpleado", query);	
      regresar= (List<UISelectEntity>) UIEntity.build("VistaPersonasDto", "autoCompletar", params, columns, 20L);
      this.attrs.put(name, regresar);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
    return regresar;
  }	
  
  public String doRowColor(Material row) {
    return Objects.equals(this.accion, EAccion.MODIFICAR) && !Objects.equals(row.getCantidad(), row.getTotal())? "janal-tr-yellow": "";
  }
  
  public String doRowHide(Entity row) {
    return Objects.equals(this.idEntrega, -1L) || !Objects.equals(this.idEntrega, row.toLong("idEntrega"))? "": "janal-display-none";
  }
  
  public void doRowEdit(Entity row) {
    try {      
      this.idEntrega= row.toLong("idEntrega");
      this.accion   = EAccion.MODIFICAR;
      this.orden    = new AdminEntregas(this.idEntrega);
      this.attrs.put("articulos", this.orden.getMateriales().size());
      this.toProcesos(row);
      this.doLoad();
      UIBackingUtilities.execute("janal.renovates([{id: 'contenedorGrupos\\\\:idAutoriza', value: {validaciones: 'libre', mascara: 'libre', grupo: 'general'}}])");
   } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
 
  private void toProcesos(Entity row) {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idDesarrollo", ((Entity)this.attrs.get("seleccionado")).toLong("idDesarrollo"));
			params.put("idPrototipo", ((Entity)this.attrs.get("seleccionado")).toLong("idPrototipo"));
      List<UISelectItem> procesos= UISelect.seleccione("VistaProcesosDto", "procesos", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("procesos", procesos);
      this.attrs.put("idProceso", row.toLong("idProceso"));
      this.toSubprocesos(row);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  private void toSubprocesos(Entity row) {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, "tc_keet_sub_procesos.id_proceso= "+ row.toLong("idProceso"));
      List<UISelectItem> subProcesos= UISelect.seleccione("TcKeetSubProcesosDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subprocesos", subProcesos);
      this.attrs.put("idSubProceso", row.toLong("idSubProceso"));
      this.toAlmacenes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  									
 
	private void toAlmacenes() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      this.attrs.put("almacenes", UIEntity.build("TcKeetDesarrollosAlmacenesDto", "almacen", params, columns));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
      if(almacenes!= null && !almacenes.isEmpty()) {
        int index= almacenes.indexOf(new UISelectEntity(this.orden.getEntrega().getIdAlmacen()));
        if(index>= 0)
  	      this.orden.setIkAlmacen(almacenes.get(index));
        else
  	      this.orden.setIkAlmacen(UIBackingUtilities.toFirstKeySelectEntity(almacenes));
      } // if 
      else
        this.orden.setIkAlmacen(new UISelectEntity(this.orden.getEntrega().getIdAlmacen()));
   } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
  
  public void doEliminar() {
		Transaccion transaccion = null;
		try {
      this.orden = new AdminEntregas(((Entity)this.attrs.get("registro")).toLong("idEntrega"));
			transaccion= new Transaccion(this.orden);
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La entrega de material se ha eliminado correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la entrega de material", ETipoMensaje.ALERTA);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } 
 
  public String doRowStyle(Entity row) {
    return Objects.equals(this.accion, EAccion.MODIFICAR) && !Objects.equals(row.toDouble("cantidad"), row.toDouble("total"))? "janal-tr-yellow": "";
  }
 
	private void toLoadAlmacenes() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      this.attrs.put("almacenes", UIEntity.build("TcKeetDesarrollosAlmacenesDto", "almacen", params, columns));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
  	  this.orden.setIkAlmacen(UIBackingUtilities.toFirstKeySelectEntity(almacenes));
   } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
  
}