package mx.org.kaana.keet.prestamos.pagos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosPagosDto;
import mx.org.kaana.keet.prestamos.pagos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetPrestamosPagosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID = 1673182892612012760L;
	private TcKeetPrestamosPagosDto pagoDto;
	protected FormatLazyModel lazyModel;
	
	public FormatLazyModel getLazyModel() {
		return lazyModel;
	}

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
      this.attrs.put("idDeudor", JsfBase.getFlashAttribute("idDeudor"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("isLiquidar", JsfBase.getFlashAttribute("isLiquidar"));
			this.pagoDto= new TcKeetPrestamosPagosDto();
			this.cargarDatosDeudor();
			this.pagoDto.setIdPrestamo((Long)JsfBase.getFlashAttribute("idPrestamo"));
			doLoad();
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
				this.attrs.put("numeroPrestamos", transaccion.getPrestamosPagados());
				this.attrs.put("cambio", transaccion.getCambio());
				JsfBase.addMessage("Se registró el pago de forma correcta.", ETipoMensaje.INFORMACION);
				//JsfBase.
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

	private void cargarDatosDeudor() throws Exception{
		String idXml = "byIdPrestamo";
		if((Boolean)this.attrs.get("isLiquidar"))
			idXml="byIdDeudor";
		Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaPrestamosDto", idXml, this.attrs);
		this.attrs.put("empleado", entity.toString("empleado"));
		this.attrs.put("prestamo", entity.toString("prestamo"));
		this.attrs.put("saldo", entity.toString("saldo"));
		this.attrs.put("pagos", entity.toString("pagos"));
		this.attrs.put("numeroPrestamos", entity.toString("numeroPrestamos"));
		this.attrs.put("sortOrder", entity.toString("numeroPrestamos"));
		this.pagoDto.setPago(Numero.getDouble(entity.toString("saldo"), 0D));
	}
	
  public void doLoad() {
		Map<String, Object> params= new HashMap<>();
    List<Columna> columns= null;
		String idXml         = null;
    try {
      columns= new ArrayList<>();
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("pago", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("abono", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("cambio", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			params.put("sortOrder", " order by tc_keet_prestamos.consecutivo desc, tc_keet_prestamos_pagos.consecutivo desc");
			params.put("idPrestamo", this.attrs.get("idPrestamo"));
			params.put("idDeudor", this.attrs.get("idDeudor"));
			if((Boolean)this.attrs.get("isLiquidar"))
				 idXml="persona";
			 else
				 idXml="lazy";
      this.lazyModel = new FormatCustomLazy("VistaPrestamosPagosDto", idXml, params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad
	
	 public String getCalculate() {
		Double abonos= 0D;
		Double saldo = 0D;
		for (IBaseDto item: (List<IBaseDto>)this.lazyModel.getWrappedData()) {
			Entity row= (Entity)item;
		  abonos+= new Double(row.toString("abonos"));
		  saldo  = new Double(row.toString("saldo"));
		} // for
	  this.attrs.put("abonos", Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, abonos));
	  this.attrs.put("saldo", Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, saldo));
		return "";
	}	
	
}