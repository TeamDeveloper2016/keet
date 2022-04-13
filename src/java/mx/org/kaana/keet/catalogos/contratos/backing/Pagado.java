package mx.org.kaana.keet.catalogos.contratos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.beans.Fondo;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.ETipoMediosPago;

@Named(value = "keetCatalogosContratosPagado")
@ViewScoped
public class Pagado extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private TcKeetContratosDto contrato;
	private Fondo fondo;

  public TcKeetContratosDto getContrato() {
    return contrato;
  }

  public Fondo getFondo() {
    return fondo;
  }

  public void setFondo(Fondo fondo) {
    this.fondo = fondo;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Keet/Catalogos/Contratos/filtro": JsfBase.getFlashAttribute("retorno"));
      //if(JsfBase.getFlashAttribute("idContrato")== null)
			//	UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      //this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));
      //this.attrs.put("idCliente", JsfBase.getFlashAttribute("idCliente"));
      this.attrs.put("idContrato", 39L);
      this.attrs.put("idCliente", 15L);
      this.attrs.put("idEstimacion", JsfBase.getFlashAttribute("idEstimacion"));
      this.contrato= (TcKeetContratosDto)DaoFactory.getInstance().findById(TcKeetContratosDto.class, (Long)this.attrs.get("idContrato"));
			this.doLoad();
      this.toLoadTiposPagos();
      this.toLoadBancos();
      this.doCheckMedioPago();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idContrato", this.contrato.getIdContrato());
      this.fondo= (Fondo)DaoFactory.getInstance().toEntity(Fondo.class, "TcKeetContratosVecimientosDto", "identically", params);
      if(this.fondo== null)
        this.fondo= new Fondo();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
  	  // transaccion= new Transaccion(this.garantias);
			if (transaccion.ejecutar(EAccion.GENERAR)) {
				regresar= this.doCancelar();
				JsfBase.addMessage("Se registraron la(s) garantía(s) del contrato", ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar las garantía(s)", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idContratoProcess", this.attrs.get("idContrato"));
  	JsfBase.setFlashAttribute("idClienteProcess", this.attrs.get("idCliente"));
  	JsfBase.setFlashAttribute("ikContrato", this.attrs.get("idContrato"));
  	JsfBase.setFlashAttribute("idEstimacion", this.attrs.get("idEstimacion"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar	
	
  public String toColor(Lote row) {
    return row.isMostar()? "": "janal-display-none";
  }
 
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja= 1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(!this.fondo.isValid()) 
  			this.fondo.setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
      else {
        int index= tiposMediosPagos.indexOf(this.fondo.getIkTipoMedioPago());
        if(index>= 0)
    			this.fondo.setIkTipoMedioPago(tiposMediosPagos.get(index));
        else 
    			this.fondo.setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
      } // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toLoadTiposPagos
	  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = null;
		List<Columna> campos       = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			campos= new ArrayList<>();
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.build("TcManticBancosDto", "row", params, campos, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
      if(!this.fondo.isValid()) 
  			this.fondo.setIkBanco(UIBackingUtilities.toFirstKeySelectEntity(bancos));
      else {
        int index= bancos.indexOf(this.fondo.getIkBanco());
        if(index>= 0)
    			this.fondo.setIkBanco(bancos.get(index));
        else
    			this.fondo.setIkBanco(UIBackingUtilities.toFirstKeySelectEntity(bancos));
      } // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadBancos  
  
	public void doCheckMedioPago() {
		Long tipoMedioPago= this.fondo.getIkTipoMedioPago().getKey();
		try {
			this.attrs.put("mostrarBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doCheckMedioPago  
  
}