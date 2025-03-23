package mx.org.kaana.keet.catalogos.proyectos.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.proyectos.beans.Lote;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.proyectos.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import static mx.org.kaana.libs.formato.Error.mensaje;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosProyectosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private RegistroProyecto proyecto;

	public RegistroProyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(RegistroProyecto proyecto) {
		this.proyecto = proyecto;
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idProyecto", JsfBase.getFlashAttribute("idProyecto"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("validar", Boolean.FALSE);
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.proyecto= new RegistroProyecto();
          this.proyecto.getProyecto().setIkEmpresa(new UISelectEntity(-1L));
          this.proyecto.getProyecto().setIkCliente(new UISelectEntity(-1L));
          this.proyecto.getProyecto().setIkDesarrollo(new UISelectEntity(-1L));
          this.toLoadCatalogos();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
        case SUBIR:					
          this.proyecto= new RegistroProyecto((Long)this.attrs.get("idProyecto"));
          this.toLoadCatalogos();
          this.toLoadPrototipos();
          List<UISelectEntity> prototipos= (List<UISelectEntity>)this.attrs.get("prototipos");
          List<UISelectEntity> fachadas  = (List<UISelectEntity>)this.attrs.get("fachadas");
          int index= 0;
          for(Lote item:this.proyecto.getProyecto().getLotes()) {
            index= prototipos.indexOf(new UISelectEntity(item.getIdPrototipo()));
            if(index>= 0)
			        item.setIkPrototipo(prototipos.get(index));
            index= fachadas.indexOf(new UISelectEntity(item.getIdTipoFachada()));
            if(index>= 0)
			        item.setIkFachada(fachadas.get(index));
			    } // for					
          this.attrs.put("validar", Boolean.TRUE);
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad
  
	private void toLoadCatalogos() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
      this.toLoadEmpresas();
      List<UISelectEntity> tipoObras= UIEntity.seleccione("VistaTiposObrasDto", "catalogo", params, "tipoObra");
      this.attrs.put("tipoObras", tipoObras);
      if(tipoObras!= null && !tipoObras.isEmpty()) {
        if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR)) {
          this.attrs.put("idTipoObra", UIBackingUtilities.toFirstKeySelectEntity(tipoObras));
          this.proyecto.getProyecto().setIkTipoObra((UISelectEntity)this.attrs.get("idTipoObra"));
        } // if
        else {
          int index= tipoObras.indexOf(this.proyecto.getProyecto().getIkTipoObra());
          if(index< 0) {
            this.attrs.put("idTipoObra", UIBackingUtilities.toFirstKeySelectEntity(tipoObras));
            this.proyecto.getProyecto().setIkTipoObra((UISelectEntity)this.attrs.get("idTipoObra"));
          } // if
          else { 
            this.attrs.put("idTipoObra", tipoObras.get(index));
            this.proyecto.getProyecto().setIkTipoObra(tipoObras.get(index));
          } // else  
        } // else
      } // if
      else
        this.attrs.put("idTipoObra", new UISelectEntity(-1L));       
      this.attrs.put("fachadas", UIEntity.seleccione("TcKeetTiposFachadasDto", "row", params, "nombre"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadCatalogos
	
	protected void toLoadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> empresas= (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
      if(empresas!= null && !empresas.isEmpty()) {
        if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR)) {
          this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("empresas")));
          this.proyecto.getProyecto().setIkEmpresa((UISelectEntity)this.attrs.get("idEmpresa"));
        } // if
        else {
          int index= empresas.indexOf(this.proyecto.getProyecto().getIkEmpresa());
          if(index< 0) {
            this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("empresas")));
            this.proyecto.getProyecto().setIkEmpresa((UISelectEntity)this.attrs.get("idEmpresa"));
          } // if
          else { 
            this.attrs.put("idEmpresa", empresas.get(index));
            this.proyecto.getProyecto().setIkEmpresa(empresas.get(index));
          } // else  
        } // else
      } // if
      else {
        this.attrs.put("idEmpresa", new UISelectEntity(-1L)); 
        this.proyecto.getProyecto().setIkEmpresa((UISelectEntity)this.attrs.get("idEmpresa"));
      } // if   
      this.doLoadClientes();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally				
	} // toLoadEmpresas

  public void doLoadClientes() {
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("sucursales", this.proyecto.getProyecto().getIkEmpresa().getKey());
  		List<UISelectEntity> clientes= UIEntity.build("TcManticClientesDto", "sucursales", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("clientes", clientes);
      if(clientes!= null && !clientes.isEmpty()) {
        if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR)) {      
          this.attrs.put("idCliente", UIBackingUtilities.toFirstKeySelectEntity(clientes));
          this.proyecto.getProyecto().setIkCliente((UISelectEntity)this.attrs.get("idCliente"));
        } // if
        else {
          int index= clientes.indexOf(this.proyecto.getProyecto().getIkCliente());
          if(index< 0) {
            this.attrs.put("idCliente", UIBackingUtilities.toFirstKeySelectEntity(clientes));
            this.proyecto.getProyecto().setIkCliente((UISelectEntity)this.attrs.get("idCliente"));
          } // if
          else { 
            this.attrs.put("idCliente", clientes.get(index));
            this.proyecto.getProyecto().setIkCliente(clientes.get(index));
          } // else  
        } // else
      } // if  
      else {
        this.attrs.put("idCliente", new UISelectEntity(-1L)); 
        this.proyecto.getProyecto().setIkCliente((UISelectEntity)this.attrs.get("idCliente"));
      } // if   
      this.doLoadDesarrollos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mensaje(e);			
		} // catch		
    finally {
      Methods.clean(params);
    } // finally				
  }
  
  public void doLoadDesarrollos() {
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idCliente", this.proyecto.getProyecto().getIkCliente());
      List<UISelectEntity>desarrollos= UIEntity.build("TcKeetDesarrollosDto", "cliente", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("desarrollos", desarrollos);
      if(desarrollos!= null && !desarrollos.isEmpty()) {
        if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR)) {
          this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));
          this.proyecto.getProyecto().setIkDesarrollo((UISelectEntity)this.attrs.get("idDesarrollo"));
        } // if
        else {
          int index= desarrollos.indexOf(this.proyecto.getProyecto().getIkDesarrollo());
          if(index< 0) {
            this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));
            this.proyecto.getProyecto().setIkDesarrollo((UISelectEntity)this.attrs.get("idDesarrollo"));
          } // if
          else { 
            this.attrs.put("idDesarrollo", desarrollos.get(index));
            this.proyecto.getProyecto().setIkDesarrollo(desarrollos.get(index));
          } // else  
        } // else
      } // if
      else {
        this.attrs.put("idDesarrollo", new UISelectEntity(-1L)); 
        this.proyecto.getProyecto().setIkDesarrollo((UISelectEntity)this.attrs.get("idDesarrollo"));
      } // if   
      this.toLoadPrototipos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mensaje(e);			
		} // catch		
    finally {
      Methods.clean(params);
    } // finally				
  }
  
	private void toLoadPrototipos() {
		Map<String, Object>params= new HashMap<>();
    try {
      params.put("idCliente", this.proyecto.getProyecto().getIkCliente());
      this.attrs.put("prototipos", UIEntity.seleccione("TcKeetPrototiposDto", "byCliente", params, "nombre"));
      if((Boolean)this.attrs.get("validar"))
			  this.proyecto.getProyecto().validaPrototipos((List<UISelectEntity>)this.attrs.get("prototipos"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally				
	} // toLoadPrototipos

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion) this.attrs.get("accion");
      this.proyecto.getProyecto().setIdUsuario(JsfBase.getIdUsuario());
			transaccion= new Transaccion(this.proyecto);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idProyectoProcess", this.proyecto.getProyecto().getIdProyecto());
				regresar= "filtro".concat(Constantes.REDIRECIONAR);//this.attrs.get("retorno")!=null? this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR): "filtro".concat(Constantes.REDIRECIONAR);
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el proyecto de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el proyecto", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {  
		JsfBase.setFlashAttribute("idProyectoProcess", this.proyecto.getProyecto().getIdProyecto());
    return "filtro".concat(Constantes.REDIRECIONAR);
  } // doAccion	
	
}