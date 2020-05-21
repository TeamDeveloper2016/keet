package mx.org.kaana.keet.catalogos.partidas.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.partidas.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetPartidasDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetCatalogosPartidasAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID = 6143816839308093176L;
	private TcKeetPartidasDto partida;
	
	public TcKeetPartidasDto getPartida() {
		return partida;
	}

	public void setPartida(TcKeetPartidasDto partida) {
		this.partida = partida;
	}	
	
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idPartida", JsfBase.getFlashAttribute("idPartida"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			doLoad();
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
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.partida= new TcKeetPartidasDto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					this.partida= (TcKeetPartidasDto) DaoFactory.getInstance().findById(TcKeetPartidasDto.class, (Long)this.attrs.get("idPartida"));
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
			transaccion = new Transaccion(this.partida);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idPartidaProcess", this.partida.getKey());
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el partida de control de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el partida de control.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idPartidaProcess", this.partida.getKey());
    return (String) this.attrs.get("retorno");
  } // doCancelar	
	
	
}