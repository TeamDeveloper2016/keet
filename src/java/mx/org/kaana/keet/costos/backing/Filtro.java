package mx.org.kaana.keet.costos.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.contratos.beans.Contrato;
import mx.org.kaana.keet.catalogos.contratos.beans.RegistroContrato;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.db.dto.TcKeetContratosBitacoraDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.nomina.reglas.Egresos;
import mx.org.kaana.keet.nomina.reglas.Estimados;
import mx.org.kaana.libs.formato.Numero;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetCostosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());			
      this.attrs.put("total", "$0.00");
			this.toLoadEmpresas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
//    List<Columna> columns    = new ArrayList<>();
//		Map<String, Object>params= null;
//    try {
//      params= this.toPrepare();	
//      params.put("sortOrder", "order by tc_keet_contratos.registro desc");
//      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
//      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
//      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
//      columns.add(new Columna("etapa", EFormatoDinamicos.MAYUSCULAS));
//      columns.add(new Columna("proyecto", EFormatoDinamicos.MAYUSCULAS));
//      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
//      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
//      this.lazyModel = new FormatCustomLazy("VistaContratosDto", params, columns);
//      UIBackingUtilities.resetDataTable();
//    } // try
//    catch (Exception e) {
//      Error.mensaje(e);
//      JsfBase.addMessageError(e);
//    } // catch
//    finally {
//      Methods.clean(params);
//      Methods.clean(columns);
//    } // finally		
  } // doLoad

	private Map<String, Object> toPrepare() {
		return new HashMap<>();		
	} // toPrepare

	private void toLoadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
    List<UISelectEntity> empresas= null;
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      empresas= (List<UISelectEntity>)UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");
      this.attrs.put("empresas", empresas);
			this.attrs.put("idEmpresa", this.toDefaultSucursal(empresas));
      this.doLoadClientes();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
			Methods.clean(params);
		}	// finally	
	} 
  
	public void doLoadClientes() {
		UISelectEntity empresa   = null;
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
    List<UISelectEntity> clientes= null;
	  try {
			empresa= (UISelectEntity)this.attrs.get("idEmpresa");
			params.put("sucursales", empresa.getKey());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      clientes= UIEntity.seleccione("TcManticClientesDto", "sucursales", params, columns, "clave");      
      this.attrs.put("clientes", clientes);
			this.attrs.put("idCliente", UIBackingUtilities.toFirstKeySelectEntity(clientes));
      this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
			Methods.clean(columns);
			Methods.clean(params);
		}	// finally	
	} 
  
	public void doLoadDesarrollos() {
    Map<String, Object> params= new HashMap<>();
		List<Columna> columns     = new ArrayList<>();
		UISelectEntity cliente    = null;
    List<UISelectEntity> desarrollos= null;
    try {
			cliente= (UISelectEntity) this.attrs.get("idCliente");
			params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_cliente= "+ cliente.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
      this.attrs.put("desarrollos", desarrollos);			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));			
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} 
       
  public void doLoadContratos() {
    Map<String, Object> params   = new HashMap<>();
    List<Columna> columns        = new ArrayList<>();
		UISelectEntity desarrollo    = null;
		List<UISelectEntity>contratos= null;
    try {
      desarrollo= ((UISelectEntity)this.attrs.get("idDesarrollo"));
			columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("vence", EFormatoDinamicos.FECHA_CORTA));
      if(Objects.equals(desarrollo.getKey(), -1L))
        params.put("idDesarrollo", 0L);
      else
        params.put("idDesarrollo", desarrollo.getKey());
      params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato_estatus<= "+ EContratosEstatus.TERMINADO.getKey());
      contratos= (List<UISelectEntity>) UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, columns, "clave");
      this.attrs.put("contratos", contratos);
      this.attrs.put("idContrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
      if(!Objects.equals(contratos, null) && !contratos.isEmpty()) {
        double sum= 0D;
        for (UISelectEntity item: contratos) {
          if(item.containsKey("valor"))
            sum+= item.toDouble("valor");
        } // for
        this.attrs.put("total", Numero.formatear(Numero.MONEDA_CON_DECIMALES, sum));
      } // if
      else
        this.attrs.put("total", "$0.00");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }
  
}