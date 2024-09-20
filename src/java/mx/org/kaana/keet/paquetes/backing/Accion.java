package mx.org.kaana.keet.paquetes.backing;

import java.io.Serializable;
import java.util.ArrayList;
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
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.paquetes.beans.Material;
import mx.org.kaana.keet.paquetes.beans.Paquete;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.paquetes.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.SelectEvent;


@Named(value = "keetPaquetesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639361L;
	private Paquete paquete;
	private Material material;
  private EAccion accion;

  public Paquete getPaquete() {
    return paquete;
  }

  public void setPaquete(Paquete paquete) {
    this.paquete = paquete;
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
      this.attrs.put("idPaquete", JsfBase.getFlashAttribute("idPaquete")== null? -1L: JsfBase.getFlashAttribute("idPaquete"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
      this.toLoadDesarrollos();
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
      this.material= new Material();
      switch (this.accion) {
        case AGREGAR:											
          this.paquete= new Paquete();
          break;
        case MODIFICAR:					
        case CONSULTAR:
          params.put("idPaquete", this.attrs.get("idPaquete"));
          params.put(Constantes.SQL_CONDICION, "tc_keet_paquetes.id_paquete= "+ this.attrs.get("idPaquete"));
          this.paquete= (Paquete)DaoFactory.getInstance().toEntity(Paquete.class, "TcKeetPaquetesDto", params);
          this.paquete.setMateriales((List<Material>)DaoFactory.getInstance().toEntitySet(Material.class, "TcKeetPaquetesDetallesDto", "igual", params));
          if(this.paquete.getMateriales()!= null) {
            for (Material item: this.paquete.getMateriales()) {
              item.setSql(ESql.SELECT);
            } // for
          } // if                 
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
			transaccion = new Transaccion(this.paquete);
			if (transaccion.ejecutar(this.accion)) {
				regresar = this.doCancelar();
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agregó": "modificó").concat(" el registro del paquete de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el paquete", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idPaquete", this.paquete.getIdPaquete());   
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 
	
  private void toLoadDesarrollos() {
		List<UISelectItem>desarrollos= null;
		Map<String, Object>params    = new HashMap<>();
		try {
      if(JsfBase.isAdminEncuestaOrAdmin() || JsfBase.isEncargado())
		    params.put("idEmpresaPersona", -1);
      else  
		    params.put("idEmpresaPersona", JsfBase.getAutentifica().getEmpresa().getIdEmpresaPersonal());
  		desarrollos= UISelect.seleccione("VistaPaquetesDto", "desarrollos", params, "nombres", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("desarrollos", desarrollos);
      if(desarrollos!= null && !desarrollos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.paquete.setIdDesarrollo((Long)desarrollos.get(0).getValue());
      } // if  
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
		List<UISelectItem>prototipos= null;
    Map<String, Object> params  = new HashMap<>();
    try {
      params.put("idDesarrollo", this.paquete.getIdDesarrollo());
  		prototipos= UISelect.seleccione("VistaPaquetesDto", "prototipos", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      attrs.put("prototipos", prototipos);
      if(prototipos!= null && !prototipos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.paquete.setIdPrototipo((Long)prototipos.get(0).getValue());
      } // if  
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
      if(procesos!= null && !procesos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.paquete.setIdProceso((Long)procesos.get(0).getValue());
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
			params.put(Constantes.SQL_CONDICION, "tc_keet_sub_procesos.id_proceso= "+ this.paquete.getIdProceso());
      List<UISelectItem> subProcesos= UISelect.seleccione("TcKeetSubProcesosDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subprocesos", subProcesos);
      if(subProcesos!= null && !subProcesos.isEmpty()) {
        if(Objects.equals(this.accion, EAccion.AGREGAR)) 
          this.paquete.setIdSubProceso((Long)subProcesos.get(0).getValue());
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
 
  public void doInsert() {
    this.doInsert(Boolean.TRUE);
  }
  
  public void doInsert(Boolean message) {
    if(!Objects.equals(this.material.getIdArticulo(), -1L)) {
      this.material.setIdUsuario(JsfBase.getIdUsuario());
      int index= this.paquete.getMateriales().indexOf(this.material);
      if(index< 0) {
        this.paquete.getMateriales().add(this.material);
        this.attrs.put("seleccionado", null);	
        this.material= new Material(); 
      } // if
      else 
        if(message)
          JsfBase.addMessage("No se puede agregar el articulo porque ya existe !", ETipoMensaje.ERROR);      			
    } // if
    else
      JsfBase.addMessage("Seleccione un articulo primero !", ETipoMensaje.ERROR);
  }
  
  public void doUpdate(Material row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
    row.setObservaciones(!Objects.equals(row.getObservaciones(), null)? row.getObservaciones().toUpperCase(): null);
  }
  
  public void doRecover(Material row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
  }
  
  public void doDelete(Material row) {
    if(row.getKey()< 0L)
      this.paquete.getMateriales().remove(row);
    else  
      row.setSql(ESql.DELETE);
  }
  
	public List<UISelectEntity> doCompleteCodigo(String query) {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    String idXml              = "porNombre";
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      if(query.startsWith(".")) {
        idXml= "porCodigo";
        query= query.substring(1);
      } // if  
			String codigo= !Cadena.isVacio(query)? query.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
      codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);			
  		params.put("codigo", codigo);			
      params.put("idArticuloTipo", "1");	      
      this.attrs.put("codigos", (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", idXml, params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return (List<UISelectEntity>)this.attrs.get("codigos");
	}	
 
	public void doAsignaCodigo(SelectEvent event) {
		UISelectEntity seleccion    = null;
		List<UISelectEntity> codigos= null;
		try {
			codigos  = (List<UISelectEntity>) this.attrs.get("codigos");
			seleccion= codigos.get(codigos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("seleccionado", seleccion);			
      this.material.setIdArticulo(seleccion.toLong("idArticulo"));
      this.material.setCodigo(seleccion.toString("propio"));
      this.material.setNombre(seleccion.toString("nombre"));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 
  
}