package mx.org.kaana.keet.prestamos.pagos.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosPagosDto;
import mx.org.kaana.keet.prestamos.pagos.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;


@Named(value = "keetPrestamosPagosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID = 1673182892612012760L;
	private TcKeetPrestamosPagosDto pagoDto;

	public TcKeetPrestamosPagosDto getPagoDto() {
		return pagoDto;
	}

	public void setPagoDto(TcKeetPrestamosPagosDto pagoDto) {
		this.pagoDto = pagoDto;
	}

	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idPrestamo", JsfBase.getFlashAttribute("idPrestamo"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.pagoDto= new TcKeetPrestamosPagosDto();
			this.pagoDto.setIdPrestamo((Long)JsfBase.getFlashAttribute("idPrestamo"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init


  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion) this.attrs.get("accion");      
			transaccion = new Transaccion(this.pagoDto);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idPrestamoProcess", this.attrs.get("idPrestamo"));
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el pago de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el pago.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion
	
	
   public String doCancelar() {   
		JsfBase.setFlashAttribute("idPrestamoProcess", this.attrs.get("idPrestamo"));
    return (String) this.attrs.get("retorno");
  } // doCancelar	
	
}