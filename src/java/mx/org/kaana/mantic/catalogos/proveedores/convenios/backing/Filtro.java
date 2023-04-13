package mx.org.kaana.mantic.catalogos.proveedores.convenios.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TrKeetArticuloProveedorClienteDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.catalogos.proveedores.reglas.Gestor;
import mx.org.kaana.mantic.catalogos.proveedores.convenios.reglas.Transaccion;
import org.primefaces.event.SelectEvent;

@Named(value = "manticCatalogosProveedoresConveniosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("sortOrder", "order by tc_mantic_proveedores.razon_social, tc_mantic_clientes.razon_social, tc_mantic_articulos.nombre");
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.toLoadCatalog();
      this.loadTiposProveedores();
			if(JsfBase.getFlashAttribute("idArticuloProveedorCliente")!= null){
				this.attrs.put("idArticuloProveedorCliente", JsfBase.getFlashAttribute("idArticuloProveedorCliente"));
				this.doLoad();
				this.attrs.put("idArticuloProveedorCliente", null);
			} // if			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

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
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
      this.attrs.put("familias", (List<UISelectEntity>) UIEntity.seleccione("TcKeetFamiliasDto", "familias", params, Collections.EMPTY_LIST, "nombre"));
			this.attrs.put("idFamilia", new UISelectEntity("-1"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
  private void loadTiposProveedores() throws Exception {
    Gestor gestor = new Gestor();
    gestor.loadTiposProveedores();
    this.attrs.put("tiposProveedores", gestor.getTiposProveedores());
    this.attrs.put("tipoProveedor", UIBackingUtilities.toFirstKeySelectEntity(gestor.getTiposProveedores()));
  }
  
  @Override
  public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params = this.toPrepare();	
      columns= new ArrayList<>();
      columns.add(new Columna("prfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("prazonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("crfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("crazonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("material", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("precio", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaProveedoresDto", "convenios", params, columns);
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
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("retorno", "filtro");      
      JsfBase.setFlashAttribute("idArticuloProveedorCliente", (eaccion.equals(EAccion.MODIFICAR)||eaccion.equals(EAccion.CONSULTAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion

  public void doEliminar() {
    Transaccion transaccion = null;
    try {
      transaccion = new Transaccion(new TrKeetArticuloProveedorClienteDto(((Entity)this.attrs.get("seleccionado")).getKey()));
      transaccion.ejecutar(EAccion.ELIMINAR);
      JsfBase.addMessage("Eliminar precio", "El precio del articulo para el proveedor se ha eliminado correctamente", ETipoMensaje.INFORMACION);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doEliminar
	
	public List<UISelectEntity> doCompleteProveedor(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= new String(codigo).replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("proveedores");
	}	
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= new String(codigo).replaceAll(Constantes.CLEAN_SQL, "").trim();
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
	}	
  
	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder();
	  UISelectEntity proveedor    = (UISelectEntity)this.attrs.get("proveedor");
		List<UISelectEntity>provedores= (List<UISelectEntity>)this.attrs.get("proveedores");
		UISelectEntity cliente        = (UISelectEntity)this.attrs.get("cliente");
		List<UISelectEntity>clientes  = (List<UISelectEntity>)this.attrs.get("clientes");
		if(this.attrs.get("idProveedorProcess")!= null && !Cadena.isVacio(this.attrs.get("idProveedorProcess")))
			sb.append("tr_keet_articulo_proveedor_cliente.id_articulo_proveedor_cliente =").append(this.attrs.get("idProveedorProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("pclave")))
			sb.append("(tc_mantic_proveedores.clave like '%").append(this.attrs.get("pclave")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("pgrupo")))
			sb.append("(tc_mantic_proveedores.grupo like '%").append(this.attrs.get("pgrupo")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("prfc")))
			sb.append("(tc_mantic_proveedores.rfc like '%").append(this.attrs.get("prfc")).append("%') and ");
		if(provedores!= null && proveedor!= null && provedores.indexOf(proveedor)>= 0) 
			sb.append("(tc_mantic_proveedores.razon_social like '%").append(provedores.get(provedores.indexOf(proveedor)).toString("razonSocial")).append("%') and ");
		else
 		  if(!Cadena.isVacio(JsfBase.getParametro("prazonSocial_input")))
			  sb.append("(tc_mantic_proveedores.razon_social like '%").append(JsfBase.getParametro("prazonSocial_input")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoProveedor")) && !this.attrs.get("idTipoProveedor").toString().equals("-1"))
  		sb.append("(tc_mantic_proveedores.id_tipo_proveedor= ").append(this.attrs.get("idTipoProveedor")).append(") and ");
    
    if(clientes!= null && cliente!= null && clientes.indexOf(cliente)>= 0) 
      sb.append("tc_mantic_clientes.razon_social regexp '.*").append(clientes.get(clientes.indexOf(cliente)).toString("razonSocial").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*")).append(".*' and ");				
    else 
      if(!Cadena.isVacio(JsfBase.getParametro("crazonSocial_input"))) 
        sb.append("tc_mantic_clientes.razon_social regexp '.*").append(JsfBase.getParametro("crazonSocial_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*")).append(".*' and ");				
    if(!Cadena.isVacio(this.attrs.get("cgrupo")))
      sb.append("tc_mantic_clientes.grupo like '%").append(this.attrs.get("cgrupo")).append("%' and ");
    if(!Cadena.isVacio(this.attrs.get("crfc")))
      sb.append("tc_mantic_clientes.rfc like '%").append(this.attrs.get("crfc")).append("%' and ");
    if(!Cadena.isVacio(this.attrs.get("cclave")))
      sb.append("tc_mantic_clientes.clave like '%").append(this.attrs.get("cclave")).append("%' and ");
    
    if(!Cadena.isVacio(JsfBase.getParametro("codigo_input")))
      sb.append("upper(tc_mantic_articulos_codigos.codigo) like upper('%").append(JsfBase.getParametro("codigo_input")).append("%') and ");						
    if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
      sb.append("tc_mantic_articulos.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
    else 
      if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
        String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
        sb.append("(tc_mantic_articulos.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_articulos.descripcion regexp '.*").append(nombre).append(".*') and ");				
      } // if	
    if(!Cadena.isVacio(this.attrs.get("idFamilia")) && !this.attrs.get("idFamilia").toString().equals("-1"))
      sb.append("(tc_mantic_articulos.id_familia= ").append(this.attrs.get("idFamilia").toString()).append(") and ");
		if(this.attrs.get("idEmpresa")!= null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)
		  regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Catalogos/Proveedores/filtro");
    JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.PROVEEDORES.getId());
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}

	public String doUploadProveedor() {
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.PRECIOS.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Catalogos/Proveedores/filtro");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
  
	public String doUploadCliente() {
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.PRECIOS_CONVENIO.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Catalogos/Proveedores/filtro");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}

	public void doUpdateCodigos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			String search= (String)this.attrs.get("codigoCodigo"); 
			search= !Cadena.isVacio(search) ? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
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
	}	// doUpdateCodigos

	public List<UISelectEntity> doCompleteCodigo(String query) {
		this.attrs.put("codigoCodigo", query);
    this.doUpdateCodigos();		
		return (List<UISelectEntity>)this.attrs.get("codigos");
	}	// doCompleteCodigo

	public void doAsignaCodigo(SelectEvent event) {
		UISelectEntity seleccion    = null;
		List<UISelectEntity> codigos= null;
		try {
			codigos= (List<UISelectEntity>) this.attrs.get("codigos");
			seleccion= codigos.get(codigos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("codigoSeleccion", seleccion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCodigo	  

	public void doUpdateArticulos() {
		List<Columna> columns         = null;
    Map<String, Object> params    = null;
		List<UISelectEntity> articulos= null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params= new HashMap<>();
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			String search= (String) this.attrs.get("codigoFiltro"); 
			if(!Cadena.isVacio(search)) 
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
			else
				search= "WXYZ";
  		params.put("codigo", search);			        
      params.put("idArticuloTipo", "1");	      
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("articulosFiltro", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}	// doUpdateArticulos
  
	public List<UISelectEntity> doCompleteArticulo(String query) {
		this.attrs.put("existeFiltro", null);
		this.attrs.put("codigoFiltro", query);
    this.doUpdateArticulos();
		return (List<UISelectEntity>)this.attrs.get("articulosFiltro");
	}	// doCompleteArticulo

}
