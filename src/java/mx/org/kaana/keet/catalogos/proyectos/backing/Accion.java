package mx.org.kaana.keet.catalogos.proyectos.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.proyectos.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import mx.org.kaana.libs.pagina.UIEntity;


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
      loadCombos();
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private void loadCombos(){
		try {
			this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
      this.attrs.put("desarrollos", UIEntity.seleccione("TcKeetDesarrollosDto", "row", this.attrs, "nombre"));
      this.attrs.put("tipoObras", UIEntity.seleccione("VistaTiposObrasDto", "catalogo", this.attrs, "tipoObra"));
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos

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
				regresar =  this.attrs.get("retorno")!=null? this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR): "filtro".concat(Constantes.REDIRECIONAR);
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
    return (String) this.attrs.get("retorno");
  } // doAccion	
}