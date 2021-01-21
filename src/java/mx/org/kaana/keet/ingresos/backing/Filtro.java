package mx.org.kaana.keet.ingresos.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TcKeetIngresosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetIngresosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.ingresos.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticVentasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
import mx.org.kaana.mantic.enums.ETipoMovimiento;

@Named(value = "keetIngresosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID=1368701967796774746L;
	
  private LocalDate fechaInicio;
  private LocalDate fechaTermino;

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaTermino() {
    return fechaTermino;
  }

  public void setFechaTermino(LocalDate fechaTermino) {
    this.fechaTermino = fechaTermino;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idVenta", JsfBase.getFlashAttribute("idVenta"));
			this.toLoadCatalog();
      if(this.attrs.get("idVenta")!= null) 
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
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_ventas.id_empresa, tc_mantic_ventas.registro desc");
      columns = new ArrayList<>();
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaIngresosDto", params, columns);
      UIBackingUtilities.resetDataTable();
			this.attrs.put("idVenta", null);
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

  public String doAccion(String accion) {
		String regresar= "accion";
    EAccion eaccion= null;
		Long idVenta   = -1L;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
      if(!EAccion.AGREGAR.equals(eaccion))
        idVenta= ((Entity)this.attrs.get("seleccionado")).getKey();
		  JsfBase.setFlashAttribute("accion", eaccion);
			JsfBase.setFlashAttribute("retorno", "filtro");
			JsfBase.setFlashAttribute("idVenta", eaccion.equals(EAccion.AGREGAR)? -1L: idVenta);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			transaccion= new Transaccion((TcManticVentasDto)DaoFactory.getInstance().findById(TcManticVentasDto.class, seleccionado.getKey()));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La factura se ha eliminado correctamente.", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la factura.", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idVenta")) && !this.attrs.get("idVenta").toString().equals("-1"))
  		sb.append("(tc_mantic_ventas.id_venta=").append(this.attrs.get("idVenta")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !this.attrs.get("idDesarrollo").toString().equals("-1"))
  		sb.append("(tc_mantic_ventas.id_desarrollo= ").append(this.attrs.get("idDesarrollo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idCliente")) && !this.attrs.get("idCliente").toString().equals("-1"))
  		sb.append("(tc_mantic_ventas.id_cliente= ").append(this.attrs.get("idCliente")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_mantic_ventas.consecutivo= '").append(this.attrs.get("consecutivo")).append("') and ");
		if(!Cadena.isVacio(this.attrs.get("factura")))
  		sb.append("(tc_mantic_facturas.folio like '%").append(this.attrs.get("factura")).append("%') and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_mantic_ventas.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_mantic_ventas.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("montoInicio")))
		  sb.append("(tc_mantic_ventas.total>= ").append((Double)this.attrs.get("montoInicio")).append(") and ");			
		if(!Cadena.isVacio(this.attrs.get("montoTermino")))
		  sb.append("(tc_mantic_ventas.total<= ").append((Double)this.attrs.get("montoTermino")).append(") and ");			
		if(!Cadena.isVacio(this.attrs.get("idVentaEstatus")) && !this.attrs.get("idVentaEstatus").toString().equals("-1"))
  		sb.append("(tc_mantic_ventas.id_venta_estatus= ").append(this.attrs.get("idVentaEstatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;
	}
	
	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", new UISelectEntity("-1"));
      columns.remove(0);
			params.put("idTipoDocumento", 1L);
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.seleccione("TcManticVentasEstatusDto", "rows", params, columns, "nombre"));
			this.attrs.put("idVentaEstatus", new UISelectEntity("-1"));
			this.doLoadDesarrollos();
			this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
		UISelectEntity empresa    = null;
    try {
			params= new HashMap<>();			
			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
			if(empresa.getKey()>= 1L)
        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
			else
				params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "nombres"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadDesarrollos
	
	public void doLoadContratos() {
		List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
		UISelectEntity empresa   = null;
    List<UISelectEntity> clientes = null;
    List<UISelectEntity> contratos= null;
	  try {
			columns= new ArrayList<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			empresa= (UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  params.put("sucursales", empresa.getKey());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !this.attrs.get("idDesarrollo").toString().equals("-1")) {
			  contratos= UIEntity.build("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
        clientes = UIEntity.build("VistaIngresosDto", "clientes", params, columns, Constantes.SQL_TODOS_REGISTROS);
      } // if  
      else {
        contratos= UIEntity.seleccione("VistaContratosDto", "byEmpresa", params, "clave");
        clientes = UIEntity.seleccione("TcManticClientesDto", "sucursales", params, columns, "razonSocial");
      } // else   
      this.attrs.put("clientes", clientes);
      if(clientes!= null && !clientes.isEmpty())
        this.attrs.put("idCliente", clientes.get(0));
      this.attrs.put("contratos", contratos);
      if(contratos!= null && !contratos.isEmpty())
        this.attrs.put("idContrato", contratos.get(0));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadContratos
  
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params= new HashMap<>();
			params.put("idTipoDocumento", 1L);
			params.put("estatusAsociados", seleccionado.toString("estatusAsociados"));
			allEstatus= UISelect.build("TcManticVentasEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public void doActualizarEstatus() {
		Transaccion transaccion           = null;
		TcManticVentasBitacoraDto bitacora= null;
		Entity seleccionado               = null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			TcManticVentasDto orden= (TcManticVentasDto)DaoFactory.getInstance().findById(TcManticVentasDto.class, seleccionado.getKey());
			bitacora   = new TcManticVentasBitacoraDto(-1L, (String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), seleccionado.getKey(), Long.valueOf(this.attrs.get("estatus").toString()), orden.getCticket(), orden.getTotal());
			transaccion= new Transaccion(orden, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizó el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
		} // finally
	}	// doActualizaEstatus
	
	public String doImportar() {
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Ingresos/filtro");		
		JsfBase.setFlashAttribute("idVenta",((Entity)this.attrs.get("seleccionado")).getKey());
		return "importar".concat(Constantes.REDIRECIONAR);
	}
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.INGRESOS);
		JsfBase.setFlashAttribute(ETipoMovimiento.INGRESOS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Ingresos/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

	public String doRecordPagos() {
		JsfBase.setFlashAttribute("idVenta", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("idCliente", ((Entity)this.attrs.get("seleccionado")).toLong("idCliente"));
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Ingresos/filtro");
		return "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos".concat(Constantes.REDIRECIONAR);
	}

  public void doMontoUpdate() {
	  if(this.attrs.get("montoInicio")!= null && this.attrs.get("montoTermino")== null)
			this.attrs.put("montoTermino", this.attrs.get("montoInicio"));
	  if(this.attrs.get("montoTermino")!= null && this.attrs.get("montoInicio")== null)
			this.attrs.put("montoInicio", this.attrs.get("montoTermino"));
	}
  
}
