package mx.org.kaana.keet.catalogos.proyectos.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.proyectos.beans.Lote;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.proyectos.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;


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
      this.loadCombos();
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private void loadCombos() {
		try {
			this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
      this.attrs.put("desarrollos", UIEntity.seleccione("TcKeetDesarrollosDto", "row", this.attrs, "nombres"));
      this.attrs.put("tipoObras", UIEntity.seleccione("VistaTiposObrasDto", "catalogo", this.attrs, "tipoObra"));
      this.attrs.put("fachadas", UIEntity.seleccione("TcKeetTiposFachadasDto", "row", this.attrs, "nombre"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	public void doLoadPrototipos(){
		try {
			this.loadPrototipos();
			this.proyecto.getProyecto().validaPrototipos((List<UISelectEntity>)this.attrs.get("prototipos"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadPrototipos
	
	private void loadPrototipos(){
	  try {
			this.attrs.put("idCliente", this.proyecto.getProyecto().getIdCliente());
      this.attrs.put("prototipos", UIEntity.seleccione("TcKeetPrototiposDto", "byCliente", this.attrs, "nombre"));
			this.proyecto.getProyecto().validaPrototipos((List<UISelectEntity>)this.attrs.get("prototipos"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadPrototipos

  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.proyecto= new RegistroProyecto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
        case SUBIR:					
          this.proyecto= new RegistroProyecto(Long.valueOf(this.attrs.get("idProyecto").toString()));
					this.loadPrototipos();
          for(Lote item:this.proyecto.getProyecto().getLotes()) {
			      item.setIkPrototipo(((List<UISelectEntity>)this.attrs.get("prototipos")).get(((List<UISelectEntity>)this.attrs.get("prototipos")).indexOf(new UISelectEntity(new Entity(item.getIdPrototipo())))));
			      item.setIkFachada(((List<UISelectEntity>)this.attrs.get("fachadas")).get(((List<UISelectEntity>)this.attrs.get("fachadas")).indexOf(new UISelectEntity(new Entity(item.getIdTipoFachada())))));
			    } // for					
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