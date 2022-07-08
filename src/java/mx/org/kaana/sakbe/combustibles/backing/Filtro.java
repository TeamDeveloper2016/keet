package mx.org.kaana.sakbe.combustibles.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
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
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.sakbe.combustibles.reglas.Transaccion;
import mx.org.kaana.sakbe.combustibles.beans.Combustible;
import mx.org.kaana.sakbe.db.dto.TcSakbeCombustiblesBitacoraDto;
import mx.org.kaana.sakbe.enums.ECombustiblesEstatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "sakbeCombustiblesFiltro")
@ViewScoped 
public class Filtro extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Filtro.class);
  private static final long serialVersionUID= 8793667741599428338L;
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
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idCombustible", JsfBase.getFlashAttribute("idCombustible"));
      this.attrs.put("ikTipoCombustible", JsfBase.getFlashAttribute("ikTipoCombustible")== null? 1L: JsfBase.getFlashAttribute("ikTipoCombustible"));
      this.toLoadTiposCombustibles();
			this.toLoadCatalog();
      if(this.attrs.get("idCombustible")!= null) 
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
      params.put("isAdministrador", JsfBase.isAdminEncuestaOrAdmin()? Constantes.SQL_VERDADERO: Constantes.SQL_FALSO);
      params.put("idUsuario", JsfBase.getIdUsuario());
      params.put("sortOrder", "order by tc_sakbe_combustibles.registro desc");
      columns = new ArrayList<>();
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("litros", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("precioLitro", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.DIA_FECHA_CORTA));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaCombustiblesDto", params, columns);
			this.attrs.put("idCombustible", null);
      this.reset();
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

  protected void reset() {
    UIBackingUtilities.resetDataTable();
  }
  
  public String doModificar(Entity row) {
    String pagina= "accion";
    this.attrs.put("seleccionado", row);
    switch(row.toLong("idTipoInsumo").intValue()) {
      case 1:
        pagina= "accion";
        break;
      case 2:
      case 3:
        pagina= "lubricante";
        break;
      case 4:
        pagina= "herramienta";
        break;
    } // switch
    return this.doAccion("MODIFICAR", pagina);
  }
  
  public String doAccion(String accion, String pagina) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", this.toPagina());		
			JsfBase.setFlashAttribute("idCombustible", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Sakbe/Combustibles/".concat(pagina).concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public String toPagina() {
    return "/Paginas/Sakbe/Combustibles/filtro";
  }
  
  public void doEliminar(Entity seleccionado) {
    this.attrs.put("seleccionado", seleccionado);
    this.doEliminar();
  }
  
  public void doEliminar() {
		Transaccion transaccion   = null;
		Entity seleccionado       = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idCombustible", seleccionado.getKey());
			transaccion= new Transaccion((Combustible)DaoFactory.getInstance().toEntity(Combustible.class, "TcSakbeCombustiblesDto", "igual", params));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El ticket de compra se ha eliminado correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el ticket de combustible", ETipoMensaje.ALERTA);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } // doEliminar

  public void doRecuperar(Entity seleccionado) {
		Transaccion transaccion   = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idCombustible", seleccionado.getKey());
      Combustible combustible= (Combustible)DaoFactory.getInstance().toEntity(Combustible.class, "TcSakbeCombustiblesDto", "igual", params);
			transaccion= new Transaccion(combustible);
			if(transaccion.ejecutar(EAccion.COMPLEMENTAR))
				JsfBase.addMessage("Eliminar", "El ticket de compra se recuperó correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al recuperar el ticket de compra", ETipoMensaje.ALERTA);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } // doRecuperar

	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idCombustible")) && !this.attrs.get("idCombustible").toString().equals("-1"))
  		sb.append("(tc_sakbe_combustibles.id_combustible=").append(this.attrs.get("idCombustible")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("ixTipoCombustible")) && !this.attrs.get("ixTipoCombustible").toString().equals("-1"))
  		sb.append("(tc_sakbe_combustibles.id_tipo_combustible=").append(this.attrs.get("ixTipoCombustible")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_sakbe_combustibles.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_sakbe_combustibles.fecha, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_sakbe_combustibles.fecha, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("ticket")))
  		sb.append("(tc_sakbe_combustibles.ticket=").append(((UISelectEntity)this.attrs.get("ticket")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("lugar")))
  		sb.append("(tc_sakbe_combustibles.lugar like '%").append(this.attrs.get("lugar")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idCombustibleEstatus")) && !this.attrs.get("idCombustibleEstatus").toString().equals("-1"))
  		sb.append("(tc_sakbe_combustibles.id_combustible_estatus= ").append(this.attrs.get("idCombustibleEstatus")).append(") and ");
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
	
	protected void toLoadCatalog() {
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
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.seleccione("TcSakbeCombustiblesEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idCombustibleEstatus", new UISelectEntity("-1"));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
    } // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params.put(Constantes.SQL_CONDICION, "id_combustible_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcSakbeCombustiblesEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
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
		Transaccion transaccion                = null;
    TcSakbeCombustiblesBitacoraDto bitacora= null;
		Entity seleccionado       = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idCombustible", seleccionado.toMap());
			Combustible orden= (Combustible)DaoFactory.getInstance().toEntity(Combustible.class, "TcSakbeCombustiblesDto", "igual", params);
  	  bitacora= new TcSakbeCombustiblesBitacoraDto((String) this.attrs.get("justificacion"), -1L, JsfBase.getIdUsuario(), Long.valueOf((String)this.attrs.get("estatus")), seleccionado.getKey());
			transaccion = new Transaccion(orden, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR)) {
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta.", ETipoMensaje.INFORMACION);
      } // if  
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			this.attrs.put("justificacion", "");
			Methods.clean(params);
		} // finally
	}	// doActualizaEstatus
	
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
	}

  public String doSuministros(Entity row) {
    this.attrs.put("combustible", row.clone());
    this.attrs.put("idTipoCombustible", new UISelectEntity(row.toLong("idTipoCombustible")));
    return this.doSuministros();
  }
  
  public String doSuministros() {
    String regresar= null;
    try {      
      JsfBase.setFlashAttribute("seguimiento", "/Paginas/Sakbe/Combustibles/visor");
      JsfBase.setFlashAttribute("ikTipoCombustible", ((UISelectEntity)this.attrs.get("idTipoCombustible")).getKey());
      JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Combustibles/visor");
      Entity row= (Entity)this.attrs.get("combustible");
      if(row==null || Objects.equals(row.toLong("idTipoInsumo"), 1L))
        regresar= "/Paginas/Sakbe/Combustibles/desarrollos.jsf".concat(Constantes.REDIRECIONAR).concat("&opcion=52df68e378f074");
      else
        regresar= "/Paginas/Sakbe/Combustibles/desarrollos.jsf".concat(Constantes.REDIRECIONAR).concat("&opcion=aabf54d864e06de06ef006");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
	}

	private void toLoadTiposCombustibles() {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = new HashMap<>();
		try {
			params.put("idTipoInsumo", "1,2,3,4");
			this.attrs.put("itemsCombustibles", UIEntity.seleccione("TcSakbeTiposCombustiblesDto", "grupo", params, "nombre"));
			tiposCombustibles= UIEntity.build("TcSakbeTiposCombustiblesDto", "grupo", params);
			this.attrs.put("tiposCombustibles", tiposCombustibles);
			this.attrs.put("idTipoCombustible", tiposCombustibles.get(0));
      if(!tiposCombustibles.isEmpty()) 
  			this.attrs.put("idTipoCombustible", tiposCombustibles.get(0));
      else  
  			this.attrs.put("idTipoCombustible", new UISelectEntity(-1L));
      this.doLoadPorcentajes();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadTiposCombustibles 
  
  private Entity toLoadCombustible() {
    Entity regresar           = null;
    Map<String, Object> params= new HashMap<>();
    try {   
      params.put("idTipoCombustible", this.attrs.get("ikTipoCombustible"));      
      params.put("disponibles", ECombustiblesEstatus.ACEPTADO.getKey()+ ","+ ECombustiblesEstatus.EN_PROCESO.getKey());      
      regresar= (Entity)DaoFactory.getInstance().toEntity("VistaCombustiblesDto", "litros", params);
      if(regresar== null || regresar.isEmpty()) {
        regresar= new Entity(-1L);
        regresar.put("idTipoInsumo", new Value("idTipoInsumo", ((UISelectEntity)this.attrs.get("idTipoCombustible")).toLong("idTipoInsumo")));
        regresar.put("saldo", new Value("saldo", 0D));
        regresar.put("litros", new Value("litros", 0D));
        regresar.put("tickets", new Value("tickets", 0D));
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }

  public void doLoadPorcentajes() {
    try {
      List<UISelectEntity> insumos= (List<UISelectEntity>) this.attrs.get("tiposCombustibles");
      if (insumos!= null && !insumos.isEmpty()) {
        int index = insumos.indexOf((UISelectEntity) this.attrs.get("idTipoCombustible"));
        if (index >= 0) 
          this.attrs.put("idTipoCombustible", insumos.get(index));
      } // if
      this.attrs.put("ikTipoCombustible", ((UISelectEntity)this.attrs.get("idTipoCombustible")).getKey());
      this.attrs.put("porcentaje", this.toLoadCombustible());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.COMBUSTIBLES);
		JsfBase.setFlashAttribute(ETipoMovimiento.COMBUSTIBLES.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Sakbe/Suministros/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}
  
}
