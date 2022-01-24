package mx.org.kaana.keet.prestamos.proveedor.pagos.backing;

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
import mx.org.kaana.keet.db.dto.TcKeetAnticiposPagosDto;
import mx.org.kaana.keet.prestamos.proveedor.pagos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetPrestamosProveedorPagosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID = 1673182892612012760L;
	private TcKeetAnticiposPagosDto pagoDto;
	protected FormatLazyModel lazyModel;
	
	public FormatLazyModel getLazyModel() {
		return lazyModel;
	}

	public TcKeetAnticiposPagosDto getPagoDto() {
		return pagoDto;
	}

	public void setPagoDto(TcKeetAnticiposPagosDto pagoDto) {
		this.pagoDto = pagoDto;
	}

	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idAnticipo", JsfBase.getFlashAttribute("idAnticipo"));
      this.attrs.put("idMoroso", JsfBase.getFlashAttribute("idMoroso"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("isLiquidar", JsfBase.getFlashAttribute("isLiquidar"));
			this.pagoDto= new TcKeetAnticiposPagosDto();
      this.pagoDto.setIdAfectaNomina(2L);
			this.cargarDatosDeudor();
			this.pagoDto.setIdAnticipo((Long)JsfBase.getFlashAttribute("idAnticipo"));
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
				this.attrs.put("numeroPrestamos", transaccion.getPagos());
				this.attrs.put("cambio", transaccion.getCambio());
				JsfBase.addMessage("Se registró el pago de forma correcta", ETipoMensaje.INFORMACION);
				//JsfBase.
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el pago", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion
	
  public String doCancelar() {   
		JsfBase.setFlashAttribute("idAnticipoProcess", this.attrs.get("idAnticipo"));
    return (String) this.attrs.get("retorno");
  } // doCancelar	

	private void cargarDatosDeudor() throws Exception {
		String idXml = "byIdAnticipo";
		if((Boolean)this.attrs.get("isLiquidar"))
			idXml="byIdDeudor";
		Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaAnticiposDto", idXml, this.attrs);
		this.attrs.put("empleado", entity.toString("empleado"));
		this.attrs.put("prestamo", entity.toString("prestamo"));
		this.attrs.put("saldo", entity.toString("saldo"));
		this.attrs.put("pagos", entity.toString("pagos"));
		this.attrs.put("numeroPrestamos", entity.toString("numeroPrestamos"));
		this.attrs.put("sortOrder", entity.toString("numeroPrestamos"));
    this.pagoDto.setConsecutivo(entity.toString("consecutivo"));
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
			params.put("sortOrder", " order by tc_keet_anticipos.consecutivo desc, tc_keet_anticipos_pagos.consecutivo desc");
			params.put("idAnticipo", this.attrs.get("idAnticipo"));
			params.put("idMoroso", this.attrs.get("idMoroso"));
			if((Boolean)this.attrs.get("isLiquidar"))
				 idXml="persona";
			 else
				 idXml="lazy";
      this.lazyModel = new FormatCustomLazy("VistaAnticiposPagosDto", idXml, params, columns);
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
   
  public void doDelete(Entity item) {
    Transaccion transaccion= null;
    try {			
			transaccion = new Transaccion(item.getKey());
			if (transaccion.ejecutar(EAccion.ELIMINAR)) 
 				JsfBase.addMessage("Se eliminó el pago de forma correcta", ETipoMensaje.INFORMACION);
      else
				JsfBase.addMessage("Ocurrió un error al eliminar el pago", ETipoMensaje.ERROR);      			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
  }   
	
}