package mx.org.kaana.keet.compras.requisiciones.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value= "keetComprasRequisicionesComparativo")
@ViewScoped
public class Comparativo extends IBaseFilter implements Serializable {  

	private static final long serialVersionUID = 2808389228136682361L;
	
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      // this.attrs.put("idRequisicion", JsfBase.getFlashAttribute("idRequisicion"));      
      this.attrs.put("idRequisicion", 2L);      
			if(!Cadena.isVacio(this.attrs.get("idRequisicion")))
				this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= new HashMap<>();
    try {
			params.put("idRequisicion", this.attrs.get("idRequisicion"));
      columns = new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("iva", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("subTotal", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("ultimaCompra", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("precioBase", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("precioLista", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("precioEspecial", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("precioConvenio", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("actualizado", EFormatoDinamicos.FECHA_HORA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaRequisicionesDesarrolloDto", "comparativo", params, columns);
      UIBackingUtilities.resetDataTable();
			columns.clear();
			columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaRequisicionesDesarrolloDto", "generales", params);
			UIBackingUtilities.toFormatEntity(entity, columns);
			this.attrs.put("entity", entity);
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
	
}
