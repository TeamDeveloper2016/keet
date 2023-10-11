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
import mx.org.kaana.keet.entregas.beans.Material;
import mx.org.kaana.keet.entregas.reglas.AdminEntregas;
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

@Named(value = "keetEntregasIndividual")
@ViewScoped
public class Individual extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 1193667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Individual.class);

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
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("hoy", LocalDate.now());
      this.doLoadMateriales();
      this.idEntrega= -1L;
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
      params.put(Constantes.SQL_CONDICION, "is null");
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
      for (Material item: this.orden.getMateriales()) 
        item.setTotal(item.getCantidad());
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
  		regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 
 
  public void doLoadMateriales() {
    try {   
      Entity seleccionado= (Entity)this.attrs.get("seleccionado");
      this.orden= new AdminEntregas();
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
      this.doLoad();
      UIBackingUtilities.execute("janal.renovates([{id: 'contenedorGrupos\\\\:idAutoriza', value: {validaciones: 'libre', mascara: 'libre', grupo: 'general'}}])");
   } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
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
 
  public void doDelete(Material row) {
    try {
      if(row.getKey()< 0L)
        this.orden.getMateriales().remove(row);
      else  
        row.setSql(ESql.DELETE);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
  }

  public void doRecover(Material row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
  }
 
	public List<UISelectEntity> doCompleteArticulo(String query) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			if(!Cadena.isVacio(query)) 
  			query= query.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
			else
				query= "WXYZ";
  		params.put("codigo", query);			        
      params.put("idArticuloTipo", "1");	      
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("materiales", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return articulos;
	}	
 
  public void doArticulo(SelectEvent event) {
		UISelectEntity seleccion    = null;
		List<UISelectEntity> codigos= null;
		try {
			codigos= (List<UISelectEntity>)this.attrs.get("materiales");
      int index= codigos.indexOf((UISelectEntity)event.getObject());
      if(index>= 0)
			  seleccion= codigos.get(index);
			this.attrs.put("idArticulo", seleccion);	
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 

  public void doPlus() {
    UISelectEntity idArticulo= (UISelectEntity)this.attrs.get("idArticulo");
    if(!Objects.equals(idArticulo, null) && !idArticulo.isEmpty()) {
      int index= this.orden.getMateriales().indexOf(new Material(idArticulo.toLong("idArticulo")));
      if(index< 0) {
        Material item= new Material(idArticulo.toLong("idArticulo"));
        item.setCodigo(idArticulo.toString("propio"));
        item.setNombre(idArticulo.toString("nombre"));
        item.setCantidad(1D);
        item.setTotal(1D);
        this.orden.getMateriales().add(item);
        this.attrs.put("articulos", this.orden.getMateriales().size());
        this.attrs.put("idArticulo", null);
      } // if
      else
        JsfBase.addMessage("El material esta dentro de la lista !", ETipoMensaje.ERROR);      			
    } // if
    else
      JsfBase.addMessage("Se tiene que seleccionar un material !", ETipoMensaje.ERROR);      			
  }
}