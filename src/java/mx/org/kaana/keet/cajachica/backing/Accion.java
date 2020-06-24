package mx.org.kaana.keet.cajachica.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.cajachica.beans.Gasto;
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.ETipoVenta;
import mx.org.kaana.mantic.ventas.beans.ArticuloVenta;

@Named(value = "keetCajaChicaAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.facturas.backing.Accion implements Serializable {

	private static final long serialVersionUID= 2847354766000406350L;  			
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;				
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");												
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));						
			this.attrs.put("retornoInicial", JsfBase.getFlashAttribute("retornoInicial"));						
			this.attrs.put("idGasto", JsfBase.getFlashAttribute("idGasto"));						
			this.attrs.put("opcionResidente", opcion);						
			this.attrs.put("idDesarrollo", idDesarrollo);      						
			doLoad();								
			initPadre();			
			this.attrs.put("accion", EAccion.AGREGAR);										
			this.attrs.put("buscaPorCodigo", false);
			if(!Cadena.isVacio(this.attrs.get("retornoInicial")))
				this.attrs.put("retorno", this.attrs.get("retornoInicial"));
			else
				this.attrs.put("retornoInicial", this.attrs.get("retorno"));
			if(!Cadena.isVacio(this.attrs.get("idGasto")))
				loadExistente();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void initPadre() throws Exception{
		try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());			
			setPrecio(ETipoVenta.MENUDEO.getNombreCampo());			
			this.attrs.put("idVenta", -1L);
      this.attrs.put("accion", EAccion.AGREGAR);      
      this.attrs.put("idCliente", -1L);			
      this.attrs.put("isPesos", false);
			this.attrs.put("sinIva", true);
			this.attrs.put("buscaPorCodigo", true);
			this.attrs.put("activeLogin", false);
			this.attrs.put("autorized", false);
			this.attrs.put("expirada", false);
			this.attrs.put("isIndividual", true);
			this.attrs.put("descuentoIndividual", 0);
			this.attrs.put("descuentoGlobal", 0);
			this.attrs.put("tipoDescuento", MENUDEO);						
			this.attrs.put("busquedaTicketAbierto", "");
			this.attrs.put("decuentoAutorizadoActivo", false);
			this.attrs.put("tipoDecuentoAutorizadoActivo", MENUDEO);
			this.attrs.put("ticketLock", -1L);						
			super.doLoad();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // initPadre
	
  @Override
  public void doLoad() {			
		Entity desarrollo        = null;		
		Entity cajaChica         = null;		
		Map<String, Object>params= null;
		List<Columna>campos      = null;
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("saldo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("acumulado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("pendiente", EFormatoDinamicos.NUMERO_CON_DECIMALES));			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo=".concat(this.attrs.get("idDesarrollo").toString()));
			desarrollo= (Entity) DaoFactory.getInstance().toEntity("VistaDesarrollosDto", "lazy", params);
			this.attrs.put("desarrollo", desarrollo);
			params.clear();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo").toString());
			cajaChica= (Entity) DaoFactory.getInstance().toEntity("VistaCajaChicaDto", "findDesarrollo", params);
			UIBackingUtilities.toFormatEntity(cajaChica, campos);
			this.attrs.put("cajaChica", cajaChica);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
		finally{
			Methods.clean(params);
		} // finally	
  } // doLoad	  	
	
	private void loadExistente() throws Exception{
		List<ArticuloVenta> material= null;
		Map<String, Object>params   = null;
		try {
			params= new HashMap<>();
			params.put("idGasto", this.attrs.get("idGasto"));
			material=(List<ArticuloVenta>)DaoFactory.getInstance().toEntitySet(ArticuloVenta.class, "VistaGastosDetallesDto", "detalle", params);
			material.add(new ArticuloVenta(-1L));
			this.getAdminOrden().setArticulos(material);			
			this.getAdminOrden().toCalculate();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadExistente
	
	@Override
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;				
    try {											
			if(!this.getAdminOrden().getArticulos().isEmpty() && getAdminOrden().getArticulos().size()>0 && getAdminOrden().getArticulos().get(0).isValid()){				
				transaccion= new Transaccion(loadGasto());
				if(transaccion.ejecutar((EAccion) this.attrs.get("accion"))){
					JsfBase.addMessage("Captura de material", "Se realizó la captura de material de forma correcta.", ETipoMensaje.INFORMACION);										
					regresar= doCancelar();
					if(Cadena.isVacio(this.attrs.get("retorno")))
						regresar= "accion".concat(Constantes.REDIRECIONAR);											
				} // if
				else
					JsfBase.addMessage("Captura de materiales", "Ocurrió un error al realizar la captura de material.", ETipoMensaje.ERROR);			
			} // if
			else
				JsfBase.addMessage("Captura de materiales", "No se han capturado materiales.", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina  
	
	public String doAplicar() {
    String regresar        = null;    		
		Transaccion transaccion= null;				
    try {											
			if(!this.getAdminOrden().getArticulos().isEmpty() && getAdminOrden().getArticulos().size()>0 && getAdminOrden().getArticulos().get(0).isValid()){				
				transaccion= new Transaccion(loadGasto());
				if(transaccion.ejecutar((EAccion) this.attrs.get("accion"))){
					JsfBase.addMessage("Captura de material", "Se realizó la captura de material de forma correcta.", ETipoMensaje.INFORMACION);					
					JsfBase.setFlashAttribute("idGasto", transaccion.getIdGasto());
					JsfBase.setFlashAttribute("retorno", "accion");										
					JsfBase.setFlashAttribute("retornoInicial", this.attrs.get("retornoInicial"));										
					toSetFlash();
					regresar= "resumen".concat(Constantes.REDIRECIONAR);
				} // if
				else
					JsfBase.addMessage("Captura de materiales", "Ocurrió un error al realizar la captura de material.", ETipoMensaje.ERROR);			
			} // if
			else
				JsfBase.addMessage("Captura de materiales", "No se han capturado materiales.", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina  
	
	private Gasto loadGasto(){
		Gasto regresar= null;		
		try {
			regresar= new Gasto();									
			regresar.setIdCajaChicaCierre(((Entity)this.attrs.get("cajaChica")).getKey());			
			regresar.setArticulos(this.getAdminOrden().getArticulos());			
			if(!Cadena.isVacio(this.attrs.get("idGasto")))
				regresar.setIdGasto(Long.valueOf(this.attrs.get("idGasto").toString()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadVale
	
	private void toSetFlash(){		
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));											
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));											
	} // toSetFlash
	
	@Override
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;						
    try {						
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));	
			JsfBase.setFlashAttribute("opcionResidente", opcion);								
			if(Cadena.isVacio(this.attrs.get("retorno")))
				regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);						
			else
				regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
	
	@Override
	public void doSearchArticulo(Long idArticulo, Integer index) {}
	
	@Override
	public void doUpdateArticulosPrecioCliente() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		int buscarCodigoPor       = 2;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
  		params.put("idProveedor", this.attrs.get("proveedor")== null? new UISelectEntity(new Entity(-1L)): ((UISelectEntity)this.attrs.get("proveedor")).getKey());
  		params.put("precioCliente", ETipoVenta.fromNombreCampo(getPrecio()).name().toLowerCase());
			String search= (String) this.attrs.get("codigo"); 
			if(!Cadena.isVacio(search)) {
				if((boolean)this.attrs.get("buscaPorCodigo"))
			    buscarCodigoPor= 0;
				if(search.startsWith("."))
					buscarCodigoPor= 2;
				else 
					if(search.startsWith(":"))
						buscarCodigoPor= 1;
				if(search.startsWith(".") || search.startsWith(":"))
					search= search.trim().substring(1);				
				search= search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				search= "WXYZ";
  		params.put("codigo", search);	
			switch(buscarCodigoPor) {      
				case 0: 					
				case 1: 
					this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.build("VistaGastosDetallesDto", "porCodigo", params, columns, 20L));
					break;
				case 2:
          this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.build("VistaGastosDetallesDto", "porNombre", params, columns, 20L));
          break;
			} // switch
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doUpdateArticulosPrecioCliente	
	
	@Override
	protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {
		ArticuloVenta temporal= (ArticuloVenta) getAdminOrden().getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
				this.doSearchArticulo(articulo.toLong("idArticulo"), index);
				params.put("idArticulo", articulo.toLong("idArticulo"));
				params.put("idProveedor", getAdminOrden().getIdProveedor());
				params.put("idAlmacen", getAdminOrden().getIdAlmacen());
				temporal.setKey(articulo.toLong("idArticulo"));
				temporal.setIdArticulo(articulo.toLong("idArticulo"));
				temporal.setIdComodin(articulo.toLong("idArticulo"));
				temporal.setIdProveedor(getAdminOrden().getIdProveedor());
				temporal.setIdRedondear(articulo.toLong("idRedondear"));
				Value codigo= (Value)DaoFactory.getInstance().toField("TcManticArticulosCodigosDto", "codigo", params, "codigo");
				temporal.setCodigo(codigo== null? "": codigo.toString());
				temporal.setPropio(articulo.toString("propio"));
				temporal.setNombre(articulo.toString("nombre"));
				temporal.setValor(articulo.toDouble(getPrecio()));
				temporal.setCosto(articulo.toDouble(getPrecio()));
				temporal.setIva(articulo.toDouble("iva"));				
				temporal.setSat(articulo.get("sat").getData()!= null ? articulo.toString("sat") : "");				
				temporal.setDescuento(getAdminOrden().getDescuento());
				temporal.setExtras(getAdminOrden().getExtras());				
				// SON ARTICULOS QUE ESTAN EN LA FACTURA MAS NO EN LA ORDEN DE COMPRA
				if(articulo.containsKey("descuento")) 
				  temporal.setDescuento(articulo.toString("descuento"));
				if(articulo.containsKey("cantidad")) {
				  temporal.setCantidad(articulo.toDouble("cantidad"));
				  temporal.setSolicitados(articulo.toDouble("cantidad"));
				} // if	
				if(temporal.getCantidad()<= 0D)					
					temporal.setCantidad(1D);
				temporal.setDescripcionPrecio(getPrecio());
				temporal.setMenudeo(articulo.toDouble("menudeo"));				
 				temporal.setDescuentoActivo((Boolean)this.attrs.get("decuentoAutorizadoActivo"));
				temporal.setUltimo(this.attrs.get("ultimo")!= null);
				temporal.setSolicitado(this.attrs.get("solicitado")!= null);
				temporal.setUnidadMedida(articulo.toString("unidadMedida"));
				temporal.setPrecio(articulo.toDouble("precio"));				
				Value stock= (Value)DaoFactory.getInstance().toField("TcManticInventariosDto", "stock", params, "stock");
				temporal.setStock(stock== null? 0D: stock.toDouble());
				if(index== getAdminOrden().getArticulos().size()- 1) {
					this.getAdminOrden().getArticulos().add(new ArticuloVenta(-1L, isCostoLibre()));
					this.getAdminOrden().toAddUltimo(this.getAdminOrden().getArticulos().size()- 1);
					UIBackingUtilities.execute("jsArticulos.update("+ (getAdminOrden().getArticulos().size()- 1)+ ");");
				} // if	
				UIBackingUtilities.execute("jsArticulos.callback('"+ articulo.getKey()+ "');");
				getAdminOrden().toCalculate();
			} // if	
			else
				temporal.setNombre("<span class='janal-color-orange'>EL ARTICULO NO EXISTE EN EL CATALOGO !</span>");
		} // try
		finally {
			Methods.clean(params);
		}
	}
	
	public String doPendientes(){
		String regresar          = null;
		EOpcionesResidente opcion= null;
		try {
			JsfBase.setFlashAttribute("disponibles", true);
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("opcionResidente", opcion);
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("retorno", "accion");
			regresar= "consulta".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch		
		return regresar;
	} // doPendientes
}