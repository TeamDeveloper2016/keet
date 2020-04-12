package mx.org.kaana.keet.estaciones.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.keet.estaciones.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIEntity;


@Named(value = "keetEstacionesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private RegistroEstacion estacion;

	public RegistroEstacion getEstacion() {
		return estacion;
	}

	public void setEstacion(RegistroEstacion estacion) {
		this.estacion = estacion;
	}	
	
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idEstacion", JsfBase.getFlashAttribute("idEstacion"));
      this.attrs.put("estacionPadre", JsfBase.getFlashAttribute("estacionPadre"));
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
			this.attrs.put("clientes", UIEntity.seleccione("VistaEmpaqueUnidadDto", "row", this.attrs, "empaque"));
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
          this.estacion= new RegistroEstacion();
					this.estacion.getEstacion().setClave(((TcKeetEstacionesDto)this.attrs.get("estacionPadre")).getClave());
					this.estacion.getEstacion().setNivel(((TcKeetEstacionesDto)this.attrs.get("estacionPadre")).getNivel()); // nivel hijo
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					this.estacion= new RegistroEstacion((Long)this.attrs.get("idEstacion"));
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
			transaccion = new Transaccion(this.estacion);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("estacionProcess", ((TcKeetEstacionesDto)this.attrs.get("estacionPadre")));
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el estacion de forma correcta."), ETipoMensaje.INFORMACION);
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
		JsfBase.setFlashAttribute("estacionProcess", ((TcKeetEstacionesDto)this.attrs.get("estacionPadre")));
    return (String) this.attrs.get("retorno");
  } // doCancelar	


}