package mx.org.kaana.keet.catalogos.materiales.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.materiales.beans.Material;
import mx.org.kaana.keet.catalogos.materiales.reglas.Transaccion;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;

@Named(value = "keetCatalogosMaterialesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
	private List<UISelectEntity> contratos;
	
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
  
	public List<UISelectEntity> getContratos() {
		return contratos;
	}

	public void setContratos(List<UISelectEntity> desarrollos) {
		this.contratos= desarrollos;
	}	

  @PostConstruct
  @Override
  protected void init() {		
    try {
      this.toLoadCatalogos();
			if(JsfBase.getFlashAttribute("idMaterialProcess")!= null) {
				this.doLoad();
				this.attrs.put("idMaterialProcess", null);
			} // if
			else
				this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      params.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_contratos_materiales.id_contrato, tc_keet_contratos_materiales.id_prototipo, tc_keet_contratos_materiales.codigo");
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("prototipo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("precioUnitario", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("expansion", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));			
      this.lazyModel = new FormatCustomLazy("VistaContratosMaterialesDto", params, columns);
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

	private Map<String, Object> toPrepare() throws Exception {
		Map<String, Object> regresar = new HashMap<>();
		StringBuilder sb             = new StringBuilder();
		if(this.attrs.get("idMaterialProcess")!= null && !Cadena.isVacio(this.attrs.get("idMaterialProcess")))
			sb.append("(tc_keet_contratos_materiales.id_contrato_material=").append(this.attrs.get("idMaterialProcess")).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !((UISelectEntity)this.attrs.get("idDesarrollo")).getKey().equals(-1L))
		  sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("idContrato")) && !((UISelectEntity)this.attrs.get("idContrato")).getKey().equals(-1L))
		  sb.append("(tc_keet_contratos_materiales.id_contrato= ").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("idPrototipo")) && !((UISelectEntity)this.attrs.get("idPrototipo")).getKey().equals(-1L))
		  sb.append("(tc_keet_contratos_materiales.id_prototipo= ").append(((UISelectEntity)this.attrs.get("idPrototipo")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("idArticulo")) && !((UISelectEntity)this.attrs.get("idArticulo")).getKey().equals(-1L))
		  sb.append("(tc_keet_contratos_materiales.id_articulo= ").append(((UISelectEntity)this.attrs.get("idArticulo")).getKey()).append(") and ");		
    if(this.attrs.get("codigo")!= null && ((UISelectEntity)this.attrs.get("codigo")).getKey()> 0L) 
      sb.append("tc_keet_contratos_materiales.id_articulo=").append(((UISelectEntity)this.attrs.get("codigo")).getKey()).append(" and ");						
    else 
      if(!Cadena.isVacio(JsfBase.getParametro("codigo_input"))) { 
        String codigo= JsfBase.getParametro("codigo_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
        sb.append("(tc_keet_contratos_materiales.codigo regexp '.*").append(codigo).append(".*') and ");				
      } // if	
    if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
      sb.append("tc_keet_contratos_materiales.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
    else 
      if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
        String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
        sb.append("(tc_keet_contratos_materiales.nombre regexp '.*").append(nombre).append(".*') and ");				
      } // if	
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_keet_contratos_materiales.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_keet_contratos_materiales.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		regresar.put(Constantes.SQL_CONDICION, sb.length()== 0 ? Constantes.SQL_VERDADERO : sb.substring(0, sb.length()- 4));				
		return regresar;		
	} // toPrepare  
	
  public void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
  	List<UISelectEntity>empresas= null;
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      empresas= (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");			
      this.attrs.put("empresas", empresas);			
      attrs.put("idEmpresa", empresas!= null? UIBackingUtilities.toFirstKeySelectEntity(empresas): new UISelectEntity(-1L));
      this.doLoadDesarrollos();
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
  }
  
  public void doLoadDesarrollos() {
    try {
     Catalogos.toLoadDesarrollosEmpresa(this.attrs);
     this.doLoadContratos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }
  
  public void doLoadContratos() {
    try {
     Catalogos.toLoadContratos(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey(), attrs);
     this.doLoadPrototipos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }

  public void doLoadPrototipos() {
    try {
     Catalogos.toLoadPrototipos(((UISelectEntity)this.attrs.get("idContrato")).getKey(), attrs);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }

  public String doAccion(String accion) {
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Materiales/filtro");
      JsfBase.setFlashAttribute("idContratoMaterial", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) || eaccion.equals(EAccion.COPIAR) || eaccion.equals(EAccion.ACTIVAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return "accion".concat(Constantes.REDIRECIONAR);
  }
  
  public void doEliminar() {
    Transaccion transaccion= null;
    Entity seleccionado    = null;
    Map<String, Object> params= new HashMap<>();
    try {
      seleccionado = (Entity)this.attrs.get("seleccionado");
      params.put("idContratoMaterial", seleccionado.getKey());
      transaccion = new Transaccion((Material)DaoFactory.getInstance().toEntity(Material.class, "TcKeetContratosMaterialesDto", "igual", params));
      if (transaccion.ejecutar(EAccion.ELIMINAR)) 
        JsfBase.addMessage("Eliminar material", "El material se ha eliminado correctamente.", ETipoMensaje.ERROR);
      else
        JsfBase.addMessage("Eliminar material", "Ocurrió un error al eliminar el material.", ETipoMensaje.ERROR);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public String doMasivo() {
    try {
      JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.PLANEACION.getId());
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Materiales/filtro");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return "importar".concat(Constantes.REDIRECIONAR);
  }
  
	public List<UISelectEntity> doCompleteArticulo(String query) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			if(!Cadena.isVacio(query)) 
  			query= query.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
			else
				query= "WXYZ";
  		params.put("codigo", query);			        
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
    }// finally
    return (List<UISelectEntity>)this.attrs.get("articulos");
	}	// doCompleteArticulo
  
	public List<UISelectEntity> doCompleteCodigos(String query) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
		boolean buscaPorCodigo        = false;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			if(!Cadena.isVacio(query)) {
  			query= query.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= query.startsWith(".");
				if(buscaPorCodigo)
					query= query.trim().substring(1);
				query= query.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				query= "WXYZ";
  		params.put("codigo", query);
      params.put("idArticuloTipo", "1");	      
			if((boolean)this.attrs.get("buscaPorCodigo") || buscaPorCodigo)
        articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porCodigo", params, columns, 40L);
			else
        articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombre", params, columns, 40L);
      this.attrs.put("codigos", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("codigos");
	}	// doCompleteArticulo
  
}