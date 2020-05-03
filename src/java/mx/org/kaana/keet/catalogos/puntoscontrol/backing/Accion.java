package mx.org.kaana.keet.catalogos.puntoscontrol.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.puntoscontrol.beans.RegistroPunto;
import mx.org.kaana.keet.catalogos.puntoscontrol.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;


@Named(value = "keetCatalogosPuntosControlAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private RegistroPunto punto;
	
	public RegistroPunto getDesarrollo() {
		return punto;
	}

	public void setDesarrollo(RegistroPunto punto) {
		this.punto = punto;
	}	
	
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idPuntoGrupo", JsfBase.getFlashAttribute("idPuntoGrupo"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());      
      this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      loadCatalogos();
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos(){
		List<Columna>campos= null;
		try {
			this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
	} // loadCatalogos
	
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.punto= new RegistroPunto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					this.punto= new RegistroPunto((Long)this.attrs.get("idPuntoGrupo"));
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
			transaccion = new Transaccion(this.punto);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idPuntoGrupoProcess", this.punto.getPuntoGrupo().getIdPuntoGrupo());
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el punto de forma correcta."), ETipoMensaje.INFORMACION);
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
		JsfBase.setFlashAttribute("idPuntoGrupoProcess", this.punto.getPuntoGrupo().getIdPuntoGrupo());
    return (String) this.attrs.get("retorno");
  } // doCancelar	
	
}