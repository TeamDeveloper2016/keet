package mx.org.kaana.keet.catalogos.contratos.backing;

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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetCatalogosContratosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
		List<UISelectEntity> tiposObras= null;
    Map<String, Object> params     = new HashMap<>();		
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());			
			this.toLoadEmpresas();
			tiposObras= UIEntity.seleccione("VistaTiposObrasDto", "catalogo", "tipoObra");
			this.attrs.put("tipoObras", tiposObras);
			this.attrs.put("tipoObra", UIBackingUtilities.toFirstKeySelectEntity(tiposObras));
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("catalogo", UIEntity.seleccione("TcKeetContratosEstatusDto", "row", params, Collections.EMPTY_LIST, "nombre"));
			this.attrs.put("idContratoEstatus", new UISelectEntity("-1"));
			if(JsfBase.getFlashAttribute("idContratoProcess")!= null) {
				this.attrs.put("idContratoProcess", JsfBase.getFlashAttribute("idContratoProcess"));
				this.doLoad();
				this.attrs.put("idContratoProcess", null);
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    }// finally
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      params.put("sortOrder", "order by tc_keet_contratos.registro desc");
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("etapa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("proyecto", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("materiales", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("destajos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("subcontratados", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porElDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("administrativos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("maquinaria", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("indirecto", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaContratosDto", params, columns);
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

  public String doAccion(String accion) {
    EAccion eaccion                = null;
		String regresar                = null; 
		TcKeetEstacionesDto estacionDto= null;
		Estaciones estaciones          = null; 
    try {
			regresar= "accion".concat(Constantes.REDIRECIONAR);
      eaccion = EAccion.valueOf(accion.toUpperCase());
			switch(eaccion){
				case SUBIR:
					regresar= "importar".concat(Constantes.REDIRECIONAR);
					break;
				case PROCESAR:
					regresar= "generadores".concat(Constantes.REDIRECIONAR);
					break;
				case COPIAR:
					regresar= "presupuestos".concat(Constantes.REDIRECIONAR);
					break;
				case COMPLEMENTAR:
					estaciones= new Estaciones();
					estacionDto= new TcKeetEstacionesDto();
					estacionDto.setClave(estaciones.toCodeByIdContrato(((Entity) this.attrs.get("seleccionado")).getKey()));
					estacionDto.setNivel(3L);
					JsfBase.setFlashAttribute("estacionProcess", estacionDto);
					regresar= "/Paginas/Keet/Estaciones/contrato".concat(Constantes.REDIRECIONAR);
					break;
			} // switch
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idContrato", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	// doCompleteCliente	
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
    UISelectEntity cliente        = (UISelectEntity)this.attrs.get("cliente");
    List<UISelectEntity>provedores= (List<UISelectEntity>)this.attrs.get("clientes");
		if(this.attrs.get("idContratoProcess")!= null)
			sb.append("tc_keet_contratos.id_contrato=").append(this.attrs.get("idContratoProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tc_mantic_clientes.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tc_mantic_clientes.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
			sb.append("(tc_keet_contratos.clave like '%").append(this.attrs.get("clave")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("nombre")))
			sb.append("(tc_keet_contratos.nombre like '%").append(this.attrs.get("nombre")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("etapa")))
			sb.append("(tc_keet_contratos.etapa like '%").append(this.attrs.get("etapa")).append("%') and ");
    if(provedores!= null && cliente!= null && provedores.indexOf(cliente)>= 0) 
			sb.append("(tc_mantic_clientes.razon_social like '%").append(provedores.get(provedores.indexOf(cliente)).toString("razonSocial")).append("%') and ");
		else{
 		  if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
			  sb.append("(tc_mantic_clientes.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%') and ");    
		} // else
		if(!Cadena.isVacio(this.attrs.get("viviendasMenor")))
			sb.append("tc_keet_contratos.no_viviendas < ").append(this.attrs.get("viviendasMenor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("viviendasMayor")))
			sb.append("tc_keet_contratos.no_viviendas > ").append(this.attrs.get("viviendasMayor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("tipoObra")) && ((UISelectEntity)this.attrs.get("tipoObra")).getKey()>= 1L)				
			sb.append("tc_keet_contratos.id_tipo_obra=").append(((UISelectEntity)this.attrs.get("tipoObra")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idContratoEstatus")) && !this.attrs.get("idContratoEstatus").toString().equals("-1"))
  		sb.append("(tc_keet_contratos.id_contrato_estatus= ").append(this.attrs.get("idContratoEstatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)				
			sb.append("tc_keet_proyectos.id_desarrollo=").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(" and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro"); 
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
	
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params.put(Constantes.SQL_CONDICION, "id_contrato_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcKeetContratosEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
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
		Transaccion transaccion            = null;
		TcKeetContratosBitacoraDto bitacora= null;
		Entity seleccionado                = null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			Contrato contrato= (Contrato)DaoFactory.getInstance().toEntity(Contrato.class, "TcKeetContratosDto", "byId", seleccionado.toMap());
			bitacora= new TcKeetContratosBitacoraDto(
				(String)this.attrs.get("justificacion"), // String justificacion, 
				(Long)this.attrs.get("estatus"),  // Long idContratoEstatus, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				-1L, // Long idContratoBitacora, 
				seleccionado.getKey() // Long idContrato 
			);	
      if(contrato!= null) {
        if(Objects.equals(contrato.getIdTipoMedioPago(), -1L))
          contrato.setIdTipoMedioPago(null);
        if(Objects.equals(contrato.getIdBanco(), -1L))
          contrato.setIdBanco(null);
      } // if
			transaccion= new Transaccion(new RegistroContrato(contrato), bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
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
	
	private void toLoadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("empresas")));
      this.doLoadDesarrollos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally{
			Methods.clean(params);
		}	// finally	
	} // toLoadEmpresas
  
	public void doLoadDesarrollos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		UISelectEntity empresa    = null;
    try {
			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
			if(empresa.getKey()>= 1L)
        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
      else
			  params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} 
  
  public String doGarantias() {
    JsfBase.setFlashAttribute("idContrato", ((Entity) this.attrs.get("seleccionado")).getKey());
    JsfBase.setFlashAttribute("idCliente", ((Entity) this.attrs.get("seleccionado")).toLong("idCliente"));
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    return "/Paginas/Keet/Catalogos/Contratos/garantias".concat(Constantes.REDIRECIONAR);
  }
  
  public String doExtras() {
    JsfBase.setFlashAttribute("idContrato", ((Entity) this.attrs.get("seleccionado")).getKey());
    JsfBase.setFlashAttribute("idCliente", ((Entity) this.attrs.get("seleccionado")).toLong("idCliente"));
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    return "/Paginas/Keet/Catalogos/Contratos/extras".concat(Constantes.REDIRECIONAR);
  }
  
  public String doAdicionales() {
    JsfBase.setFlashAttribute("idContrato", ((Entity) this.attrs.get("seleccionado")).getKey());
    JsfBase.setFlashAttribute("idCliente", ((Entity) this.attrs.get("seleccionado")).toLong("idCliente"));
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    return "/Paginas/Keet/Catalogos/Contratos/adicionales".concat(Constantes.REDIRECIONAR);
  }
  
  public String doFondoGarantia() {
    JsfBase.setFlashAttribute("idContrato", ((Entity) this.attrs.get("seleccionado")).getKey());
    JsfBase.setFlashAttribute("idCliente", ((Entity) this.attrs.get("seleccionado")).toLong("idCliente"));
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    return "/Paginas/Keet/Catalogos/Contratos/pagado".concat(Constantes.REDIRECIONAR);
  }
 
	public StreamedContent getEgresos() {
		StreamedContent regresar = null;		
		Entity seleccionado      = null;				
    Egresos egresos          = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");						
	  	egresos     = new Egresos(-1L, seleccionado.getKey());
      String name= egresos.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getEgresos
  
	public StreamedContent getEstimados() {
		StreamedContent regresar = null;		
		Entity seleccionado      = null;				
    Estimados estimados      = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");						
	  	estimados   = new Estimados(seleccionado.getKey());
      String name = estimados.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getEstimados
  
	public StreamedContent getGlobalPagados() {
		StreamedContent regresar = null;		
    Egresos egresos          = null;
		try {
	  	egresos    = new Egresos();
      String name= egresos.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getGlobalPagados
  
	public StreamedContent getGlobalEstimados() {
		StreamedContent regresar= null;		
    Estimados estimados     = null;
		try {
	  	estimados  = new Estimados();
      String name= estimados.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getGlobalEstimados
 
  public String doMateriales() {
    JsfBase.setFlashAttribute("idContrato", -1L);
    JsfBase.setFlashAttribute("idCliente", -1L);
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    return "/Paginas/Keet/Catalogos/Materiales/filtro".concat(Constantes.REDIRECIONAR);
  }
 
  public void doHola() {
 		Transaccion transaccion= null;
		Entity seleccionado    = null;
		try {
			seleccionado     = (Entity)this.attrs.get("seleccionado");
			Contrato contrato= (Contrato)DaoFactory.getInstance().toEntity(Contrato.class, "TcKeetContratosDto", "byId", seleccionado.toMap());
 			transaccion      = new Transaccion(new RegistroContrato(contrato), new TcKeetContratosBitacoraDto());
			transaccion.ejecutar(EAccion.DESTRANSFORMACION);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } 
        
}