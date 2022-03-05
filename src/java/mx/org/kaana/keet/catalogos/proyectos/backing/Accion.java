package mx.org.kaana.keet.catalogos.proyectos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
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
import mx.org.kaana.keet.comun.Catalogos;
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
	
	private void toLoadCatalogos() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
      this.toLoadEmpresas();
      this.attrs.put("tipoObras", UIEntity.seleccione("VistaTiposObrasDto", "catalogo", params, "tipoObra"));
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
		List<Columna> columns    = null;
		try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			columns= new ArrayList<>();		
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
      this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("empresas")));
      this.proyecto.getProyecto().setIkEmpresa((UISelectEntity)this.attrs.get("idEmpresa"));
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
    try {
      this.attrs.put("idEmpresa", this.proyecto.getProyecto().getIkEmpresa());
      Catalogos.toLoadClientesEmpresa(this.attrs);
      this.proyecto.getProyecto().setIkCliente((UISelectEntity)this.attrs.get("idCliente"));
      this.doLoadDesarrollos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mensaje(e);			
		} // catch		
  }
  
  public void doLoadDesarrollos() {
    try {
      this.attrs.put("idCliente", this.proyecto.getProyecto().getIkCliente());
      Catalogos.toLoadDesarrollosCliente(this.attrs);
      this.proyecto.getProyecto().setIkDesarrollo((UISelectEntity)this.attrs.get("idDesarrollo"));
      this.toLoadPrototipos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mensaje(e);			
		} // catch		
  }
  
	private void toLoadPrototipos() {
		Map<String, Object>params= new HashMap<>();
    try {
      params.put("idCliente", ((UISelectEntity)attrs.get("idCliente")).getKey());
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
	
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
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
          this.attrs.put("validar", Boolean.FALSE);
          this.toLoadCatalogos();
          this.attrs.put("idEmpresa", this.proyecto.getProyecto().getIkEmpresa()); 
          this.attrs.put("idCliente", this.proyecto.getProyecto().getIkCliente()); 
          this.attrs.put("idDesarrollo", this.proyecto.getProyecto().getIkDesarrollo()); 
          this.toLoadPrototipos();
          for(Lote item:this.proyecto.getProyecto().getLotes()) {
			      item.setIkPrototipo(((List<UISelectEntity>)this.attrs.get("prototipos")).get(((List<UISelectEntity>)this.attrs.get("prototipos")).indexOf(new UISelectEntity(new Entity(item.getIdPrototipo())))));
			      item.setIkFachada(((List<UISelectEntity>)this.attrs.get("fachadas")).get(((List<UISelectEntity>)this.attrs.get("fachadas")).indexOf(new UISelectEntity(new Entity(item.getIdTipoFachada())))));
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
				JsfBase.addMessage("Ocurrió un error al registrar el proyecto.", ETipoMensaje.ERROR);      			
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