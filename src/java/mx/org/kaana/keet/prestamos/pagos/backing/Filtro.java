package mx.org.kaana.keet.prestamos.pagos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetPrestamosPagosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 6319984968937774153L;

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

  @PostConstruct
  @Override
  protected void init() {
    try {
			if(JsfBase.getFlashAttribute("idPrestamo")!= null){
				this.attrs.put("idPrestamo", JsfBase.getFlashAttribute("idPrestamo"));
			  this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
				this.doLoad();
			} // if
			else
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
    try {
      columns= new ArrayList<>();
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("pago", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("abono", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("cambio", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaPrestamosPagosDto", this.attrs, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad

  	
	public String doCancelar() {   
		JsfBase.setFlashAttribute("idPrestamoProcess", this.attrs.get("idPrestamo"));
    return (String) this.attrs.get("retorno");
  } // doCancelar	
	
}