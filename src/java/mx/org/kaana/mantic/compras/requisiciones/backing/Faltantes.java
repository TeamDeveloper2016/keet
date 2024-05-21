package mx.org.kaana.mantic.compras.requisiciones.backing;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value= "manticComprasRequisicionesFaltantes")
@ViewScoped
public class Faltantes extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8791667741599428332L;
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idRequisicion", JsfBase.getFlashAttribute("idRequisicion"));
      this.attrs.put("idFaltante", -1L);
			this.toLoadCatalogos();      
      if(!Objects.equals(this.attrs.get("idRequisicion"), null))
        this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_requisiciones.consecutivo, tc_mantic_requisiciones.registro desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("fechaEntregada", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("fechaPedido", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaRequisicionesDto", "faltantes", params, columns);
      UIBackingUtilities.resetDataTable();
 			this.attrs.put("idRequisicion", null);
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
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Compras/Requisiciones/faltantes");		
			JsfBase.setFlashAttribute("idRequisicion", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) ? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Compras/Requisiciones/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder();
    try {
      // ESTO ES UN PARCHE PARA MOSTRAR SOLO LOS REGISTROS DEL VENDEDOR 31/01/2024
      if(!JsfBase.isEncargado())
        sb.append("(tc_mantic_requisiciones.id_usuario= ").append(JsfBase.getIdUsuario()).append(") and ");
      UISelectEntity estatus= (UISelectEntity) this.attrs.get("idRequisicionEstatus");
      if(!Cadena.isVacio(this.attrs.get("idRequisicion")) && !Objects.equals(this.attrs.get("idRequisicion"), -1L)) 
        sb.append("(tc_mantic_requisiciones.id_requisicion=").append(this.attrs.get("idRequisicion")).append(") and ");
			if(!Cadena.isVacio(JsfBase.getParametro("codigo_input")))
				sb.append("upper(tc_mantic_requisiciones_detalles.propio) like upper('%").append(JsfBase.getParametro("codigo_input")).append("%') and ");						
			if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
				sb.append("(tc_mantic_requisiciones_detalles.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(") and ");						
  		else 
	  		if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
					String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
		  		sb.append("(tc_mantic_requisiciones_detalles.nombre regexp '").append(nombre).append(".*') and ");				
				} // if	
      if(!Objects.equals((Long)this.attrs.get("idFaltante"), -1L))
        if(Objects.equals((Long)this.attrs.get("idFaltante"), 1L))
  		    sb.append("(tc_keet_requisiciones_ordenes.id_requisicion_orden is null) and ");
        else 
  		    sb.append("(tc_keet_requisiciones_ordenes.id_requisicion_orden is not null) and ");
      if(!Cadena.isVacio(this.attrs.get("consecutivo")))
        sb.append("(tc_mantic_requisiciones.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
      if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
        sb.append("(date_format(tc_mantic_requisiciones.fecha_pedido, '%Y%m%d')= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
        sb.append("(date_format(tc_mantic_requisiciones.fecha_entregada, '%Y%m%d')= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
      if(estatus!= null && !estatus.getKey().equals(-1L))
        sb.append("(tc_mantic_requisiciones.id_requisicion_estatus= ").append(estatus.getKey()).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)
        sb.append("(tc_mantic_requisiciones.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idContrato")) && ((UISelectEntity)this.attrs.get("idContrato")).getKey()>= 1L)
        sb.append("(tc_mantic_requisiciones.id_contrato= ").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
        regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
      else
        regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(sb.length()== 0)
        regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else	
        regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
    } // try
    catch(Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    }
		return regresar;		
	}
	
	protected void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
      this.attrs.put("clientes", (List<UISelectEntity>) UIEntity.seleccione("VistaRequisicionesDto", "clientes", params, columns, "clave"));
			this.attrs.put("idCliente", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("clientes")));
			columns.clear();
      this.attrs.put("estatusFiltro", (List<UISelectEntity>) UIEntity.seleccione("TcManticRequisicionesEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idRequisicionEstatus", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("estatusFiltro")));
      this.doLoadDesarrollos();
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
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
		  params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("operador", "<=");
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos

	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = new HashMap<>();
		try {
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			contratos= UIEntity.seleccione("VistaContratosDto", "desarrollos", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("idContrato", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("contratos")));			
		} // try
		catch (Exception e) {			
      Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 
  
	public List<UISelectEntity> doCompleteCodigo(String search) {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			search= !Cadena.isVacio(search)? search.trim().toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("sucursales", this.attrs.get("idEmpresa"));
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);			
  		params.put("codigo", search);			
      params.put("idArticuloTipo", "1");	      
      this.attrs.put("codigos", (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porCodigo", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return (List<UISelectEntity>)this.attrs.get("codigos");
	}	

	public List<UISelectEntity> doCompleteArticulo(String search) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			if(!Cadena.isVacio(search)) 
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
			else
				search= "WXYZ";
  		params.put("codigo", search);			        
      params.put("idArticuloTipo", "1");	      
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("articulos", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
    return (List<UISelectEntity>)this.attrs.get("articulos");
	}	
  
}
