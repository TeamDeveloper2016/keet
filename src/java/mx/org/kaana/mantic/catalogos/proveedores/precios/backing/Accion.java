package mx.org.kaana.mantic.catalogos.proveedores.precios.backing;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.db.dto.TcKeetArticulosProveedoresDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.proveedores.precios.reglas.Transaccion;
import org.primefaces.event.SelectEvent;

@Named(value = "manticCatalogosProveedoresPreciosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private UISelectEntity ikProveedor;
	private UISelectEntity ikArticulo;
	private TcKeetArticulosProveedoresDto precio;

  public TcKeetArticulosProveedoresDto getPrecio() {
    return precio;
  }

  public void setPrecio(TcKeetArticulosProveedoresDto precio) {
    this.precio = precio;
  }

	public UISelectEntity getIkProveedor() {
		return ikProveedor;
	}

	public void setIkProveedor(UISelectEntity ikProveedor) {
		this.ikProveedor=ikProveedor;
		if(this.ikProveedor!= null)
		  this.precio.setIdProveedor(this.ikProveedor.getKey());
	}

  public UISelectEntity getIkArticulo() {
    return ikArticulo;
  }

  public void setIkArticulo(UISelectEntity ikArticulo) {
    this.ikArticulo = ikArticulo;
		if(this.ikArticulo!= null)
		  this.precio.setIdArticulo(this.ikArticulo.getKey());
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idArticuloProveedor", JsfBase.getFlashAttribute("idArticuloProveedor"));
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("existe", Boolean.FALSE);
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.precio= new TcKeetArticulosProveedoresDto();
          this.precio.setIdUsuario(JsfBase.getIdUsuario());
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.precio= (TcKeetArticulosProveedoresDto)DaoFactory.getInstance().findById(TcKeetArticulosProveedoresDto.class, (Long)this.attrs.get("idArticuloProveedor"));
          Entity proveedor= (Entity)DaoFactory.getInstance().toEntity("TcManticProveedoresDto", "igual", this.precio.toMap());
          this.setIkProveedor(new UISelectEntity(proveedor));
          Entity articulo= (Entity)DaoFactory.getInstance().toEntity("TcManticArticulosDto", "igual", this.precio.toMap());
          this.setIkArticulo(new UISelectEntity(articulo));
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
      eaccion    = this.precio.isValid()? EAccion.MODIFICAR: EAccion.AGREGAR;
      this.precio.setActualizado(LocalDateTime.now());
			transaccion= new Transaccion(this.precio);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
        JsfBase.setFlashAttribute("idArticuloProveedor", this.precio.getKey());
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agreg�" : "modific�").concat(" el precio de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurri� un error al registrar el precio", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idArticuloProveedor", this.attrs.get("idArticuloProveedor"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doAccion

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

  public List<UISelectEntity> doCompleteArticulo(String query) {
		this.attrs.put("existeFiltro", null);
		this.attrs.put("codigoFiltro", query);
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
		return articulos;
	}	// doCompleteArticulo
  
  public void doUpdatePrecios() {
    if(Objects.equals(this.precio.getPrecioLista(), 0D))
      this.precio.setPrecioLista(this.precio.getPrecioBase());
    if(Objects.equals(this.precio.getPrecioEspecial(), 0D))
      this.precio.setPrecioEspecial(this.precio.getPrecioBase());
  }

	public void doLookForPrecioProveedor(SelectEvent event) {
		UISelectEntity seleccion        = null;
		List<UISelectEntity> proveedores= null;
		try {
			proveedores= (List<UISelectEntity>) this.attrs.get("proveedores");
			seleccion= proveedores.get(proveedores.indexOf((UISelectEntity)event.getObject()));
			this.setIkProveedor(seleccion);
      this.toLookForPrecio();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doLookForPrecioProveedor	
  
	public void doLookForPrecioArticulo(SelectEvent event) {
		UISelectEntity seleccion      = null;
		List<UISelectEntity> articulos= null;
		try {
			articulos= (List<UISelectEntity>) this.attrs.get("articulos");
			seleccion= articulos.get(articulos.indexOf((UISelectEntity)event.getObject()));
			this.setIkArticulo(seleccion);
      this.toLookForPrecio();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doLookForPrecioArticulo	
  
  private void toLookForPrecio() {
    try {
      TcKeetArticulosProveedoresDto existe= (TcKeetArticulosProveedoresDto)DaoFactory.getInstance().findIdentically(TcKeetArticulosProveedoresDto.class, this.precio.toMap());
      if(existe!= null) 
        this.precio= existe;
      this.attrs.put("existe", existe!= null);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
  }
          
}