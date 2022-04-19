package mx.org.kaana.keet.compras.requisiciones.backing;

import java.io.Serializable;
import java.time.LocalDate;
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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.compras.requisiciones.beans.RegistroRequisicion;
import mx.org.kaana.keet.compras.requisiciones.reglas.AdminRequisicion;
import mx.org.kaana.keet.compras.requisiciones.reglas.Transaccion;
import mx.org.kaana.keet.enums.ETiposPrecios;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.requisiciones.beans.TicketRequisicion;
import mx.org.kaana.mantic.enums.ETipoVenta;
import mx.org.kaana.mantic.ventas.beans.ArticuloVenta;

@Named(value= "keetComprasRequisicionesAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.facturas.backing.Catalogos implements Serializable {

	private static final long serialVersionUID = -4260859613485238198L;  
	private RegistroRequisicion registroRequisicion;	

	public Accion() {
		super();
	} // Catalogos
	
	public RegistroRequisicion getRegistroRequisicion() {
		return registroRequisicion;
	}

	public void setRegistroRequisicion(RegistroRequisicion registroRequisicion) {
		this.registroRequisicion = registroRequisicion;
	}	

	@PostConstruct
  @Override
  protected void init() {		
    try {
			setPrecio(ETipoVenta.MENUDEO.getNombreCampo());			
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idRequisicion", JsfBase.getFlashAttribute("idRequisicion")== null? -1L: JsfBase.getFlashAttribute("idRequisicion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? null: JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("isPesos", false);
			this.attrs.put("sinIva", false);
			this.attrs.put("buscaPorCodigo", false);
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("isMatriz", JsfBase.isAdminEncuestaOrAdmin());					
			this.attrs.put("nombreEmpresa", JsfBase.getAutentifica().getEmpresa().getNombre());
			this.attrs.put("solicita", JsfBase.getAutentifica().getPersona().getNombreCompleto());
			this.attrs.put("decuentoAutorizadoActivo", false);
			this.attrs.put("tipoDecuentoAutorizadoActivo", MENUDEO);
			this.attrs.put("familiasSeleccion", new String[]{});
			this.loadDesarrollos();
			this.loadProveedores();
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	public void loadDesarrollos() {
		List<Columna> columns           = null;
    Map<String, Object> params      = null;	
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
    try {
			params= new HashMap<>();		
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
      this.attrs.put("desarrollos", desarrollos);			
			desarrollo= UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos"));
			this.attrs.put("desarrollo", desarrollo);
			this.attrs.put("cliente", desarrollo.toString("razonSocial"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // loadDesarrollos	
	
	private void loadProveedores() {
    List<UISelectEntity> proveedores= null;
		List<Columna> campos            = null;
    Map<String, Object> params      = null;
    try {
      params = new HashMap<>();
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			campos= new ArrayList<>();
			campos.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      proveedores = UIEntity.seleccione("TcManticProveedoresDto", "sucursales", params, campos, Constantes.SQL_TODOS_REGISTROS, "razonSocial");
      this.attrs.put("proveedores", proveedores);			
			this.attrs.put("proveedor", UIBackingUtilities.toFirstKeySelectEntity(proveedores));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(campos);
    } // finally
  } // loadProveedores
	
	public void doLoadFamilias(){
		UISelectEntity proveedor   = null;
		List<UISelectItem> familias= null;
		Map<String, Object>params  = null;
		try {
			proveedor= (UISelectEntity) this.attrs.get("proveedor");
			params= new HashMap<>();
			params.put("idProveedor", proveedor.getKey());
			familias= UISelect.build("VistaFamiliasProveedoresDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("familias", familias);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadFamilias
	
	@Override
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:		
					this.registroRequisicion= new RegistroRequisicion();
					this.registroRequisicion.getRequisicion().setPedido(LocalDate.now());
					this.registroRequisicion.getRequisicion().setEntrega(LocalDate.now());
          this.setAdminOrden(new AdminRequisicion(new TicketRequisicion(-1L)));					
          break;
        case MODIFICAR:			
        case CONSULTAR:	
					this.registroRequisicion= new RegistroRequisicion(Long.valueOf(this.attrs.get("idRequisicion").toString()));					
          this.setAdminOrden(new AdminRequisicion((TicketRequisicion)DaoFactory.getInstance().toEntity(TicketRequisicion.class, "TcManticRequisicionesDto", "detalle", this.attrs)));				
					this.attrs.put("desarrollo", new UISelectEntity(this.registroRequisicion.getRequisicion().getIdDesarrollo()));										
					this.attrs.put("proveedor", new UISelectEntity(this.registroRequisicion.getRequisicion().getIdProveedor()));
					doLoadFamilias();					
					doUpdateCliente();
					this.attrs.put("contrato", new UISelectEntity(this.registroRequisicion.getRequisicion().getIdContrato()));
					doLoadPrototipos();
					this.attrs.put("prototipo", new UISelectEntity(this.registroRequisicion.getRequisicion().getIdPrototipo()));					
          break;
      } // switch
			this.attrs.put("consecutivo", "");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

	@Override
  public String doAceptar() {
    Transaccion transaccion         = null;
    String regresar                 = null;
		EAccion eaccion                 = null;		
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
    try {						
			eaccion= (EAccion) this.attrs.get("accion");						
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo= desarrollos.get(desarrollos.indexOf(((UISelectEntity)this.attrs.get("desarrollo"))));
			this.registroRequisicion.getRequisicion().setDescuento(getAdminOrden().getDescuento());
			this.registroRequisicion.getRequisicion().setDescuentos(this.getAdminOrden().getTotales().getDescuentos());
			this.registroRequisicion.getRequisicion().setImpuestos(this.getAdminOrden().getTotales().getIva());
			this.registroRequisicion.getRequisicion().setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			this.registroRequisicion.getRequisicion().setTotal(this.getAdminOrden().getTotales().getTotal());
			this.registroRequisicion.getRequisicion().setIdDesarrollo(desarrollo.getKey());			
			this.registroRequisicion.getRequisicion().setIdEmpresa(desarrollo.toLong("idEmpresa"));
			this.registroRequisicion.getRequisicion().setIdProveedor(((UISelectEntity)this.attrs.get("proveedor")).getKey());
			this.registroRequisicion.getRequisicion().setIdContrato(((UISelectEntity)this.attrs.get("contrato")).getKey());
			this.registroRequisicion.getRequisicion().setIdPrototipo(((UISelectEntity)this.attrs.get("prototipo")).getKey());
			transaccion = new Transaccion(this.registroRequisicion, this.getAdminOrden().getArticulos());
			toAdjustArticulos();
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.attrs.get("retorno")!= null ? this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR) : null;
				if(eaccion.equals(EAccion.AGREGAR)) { 				  
					UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 cotización', '"+ this.registroRequisicion.getRequisicion().getConsecutivo()+ "');");
				} // if	
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la cotización."), ETipoMensaje.INFORMACION);
				JsfBase.setFlashAttribute("idRequisicion", this.registroRequisicion.getRequisicion().getIdRequisicion());				
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la cotización de compra.", ETipoMensaje.ERROR);      						
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

	public void toAdjustArticulos() {
		int count= 0;
		while(count< getAdminOrden().getArticulos().size()) {
			if(!getAdminOrden().getArticulos().get(count).isValid())
				getAdminOrden().getArticulos().remove(count);
			else{
				if(count> 0 && getAdminOrden().getArticulos().get(count- 1).getKey().equals(getAdminOrden().getArticulos().get(count).getKey())) {
					getAdminOrden().getArticulos().get(count- 1).setCantidad(getAdminOrden().getArticulos().get(count- 1).getCantidad()+ getAdminOrden().getArticulos().get(count).getCantidad());
					getAdminOrden().getArticulos().remove(count);
				} // if
				else
				  count++;
			} // else
		} // while
	} // toAdjustArticulos
	
	@Override
  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idRequisicion", this.registroRequisicion.getRequisicion().getIdRequisicion());
    return this.attrs.get("retorno") != null ? (String)this.attrs.get("retorno") : "filtro";
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
		if(this.attrs.get("prototipo")!= null){
			if(this.attrs.get("proveedor")!= null){
				UISelectEntity prototipo= (UISelectEntity) this.attrs.get("prototipo");			
				UISelectEntity	proveedor= (UISelectEntity) this.attrs.get("proveedor");							
				if(prototipo.getKey()>= 1){
					if(proveedor.getKey()>= 1)
						toMoveData(articulo, index, toClaveMateriales(), true);																			
					else				
						JsfBase.addMessage("Es necesario seleccionar un proveedor.");			
				} // if
				else			
					JsfBase.addMessage("Es necesario seleccionar un prototipo.");					
			} // if
			else			
				JsfBase.addMessage("Es necesario seleccionar un proveedor.");					
		} // if
		else			
			JsfBase.addMessage("Es necesario seleccionar un prototipo.");					
	} // toModeveData
		
	protected void toMoveData(UISelectEntity articulo, Integer index, String claveMaterial, boolean calculate) throws Exception {
		ArticuloVenta temporal= (ArticuloVenta) getAdminOrden().getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
				((TicketRequisicion)((AdminRequisicion)getAdminOrden()).getOrden()).setIdDesarrollo(((UISelectEntity)this.attrs.get("desarrollo")).getKey());
				((TicketRequisicion)((AdminRequisicion)getAdminOrden()).getOrden()).setIdProveedor(((UISelectEntity)this.attrs.get("proveedor")).getKey());
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
				temporal.setCodigo(codigo== null ? (articulo.toString("codigo")!= null ? articulo.toString("codigo") : "") : codigo.toString());
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
				//((AdminRequisicion)getAdminOrden()).loadListaPrecios(claveMaterial);
				temporal.setListaPrecios(((AdminRequisicion)getAdminOrden()).preciosArticulo(articulo.toLong("idArticulo"), claveMaterial));
				temporal.setPrecioLista(UIBackingUtilities.toFirstKeySelectEntity(temporal.getListaPrecios()));
				if(calculate)
					getAdminOrden().toCalculate();
			} // if	
			else
				temporal.setNombre("<span class='janal-color-orange'>EL ARTICULO NO EXISTE EN EL CATALOGO !</span>");
		} // try
		finally {
			Methods.clean(params);
		} // finally
	} // toModeveData
	
	public void doActualizaCosto(){
		getAdminOrden().toCalculate();
	} // doActualizaCosto
	
	@Override
	public void doCalculate(Integer index) {				
		super.doCalculate(index);
		List<UISelectEntity> precios= ((ArticuloVenta)this.getAdminOrden().getArticulos().get(index)).getListaPrecios();
		UISelectEntity precioActual= precios.get(precios.indexOf(((ArticuloVenta)this.getAdminOrden().getArticulos().get(index)).getPrecioLista()));
		if(!Double.valueOf(precioActual.toString("costo")).equals(((ArticuloVenta)this.getAdminOrden().getArticulos().get(index)).getCosto())){
			for(UISelectEntity precio: precios){
				if(precio.getKey().equals(ETiposPrecios.LIBRE.getKey())){
					((ArticuloVenta)this.getAdminOrden().getArticulos().get(index)).setAplicaCosto(false);
					((ArticuloVenta)this.getAdminOrden().getArticulos().get(index)).setPrecioLista(precio);
				} // if
			} // for
		} // if
		((ArticuloVenta)this.getAdminOrden().getArticulos().get(index)).setAplicaCosto(true);		
	}	// doCalculate
	
	public void doUpdateCliente(){
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		try {
			loadContratos();
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo= (UISelectEntity) this.attrs.get("desarrollo");
			this.attrs.put("cliente", desarrollos.get(desarrollos.indexOf(desarrollo)).toString("razonSocial"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doUpdateCliente
	
	private void loadContratos(){
		List<UISelectEntity> contratos= null;
		Map<String, Object> params    = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", ((UISelectEntity) this.attrs.get("desarrollo")).getKey());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "etapa");
			this.attrs.put("contratos", contratos);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // loadContratos
	
	public void doLoadPrototipos(){
		List<UISelectEntity>prototipos= null;
		Map<String, Object>params     = null;
		UISelectEntity contrato       = null;
		try {
			contrato= (UISelectEntity) this.attrs.get("contrato");
			params= new HashMap<>();
			params.put("idContrato", contrato.getKey());
			prototipos= UIEntity.seleccione("VistaPrototiposDto", "findContrato", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "nombre");
			this.attrs.put("prototipos", prototipos);
			this.attrs.put("prototipo", UIBackingUtilities.toFirstKeySelectEntity(prototipos));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // doLoadPrototipos
	
	public void doLoadArticulos(){
		UISelectEntity prototipo = null;		
		UISelectEntity proveedor = null;		
		String clave             = null;
		List<Entity>articulos    = null;
		Map<String, Object>params= null;
		String[] arrayFamilias   = null;
		String familias          = "";
		int count                = 0;
		try {
			prototipo= (UISelectEntity) this.attrs.get("prototipo");			
			proveedor= (UISelectEntity) this.attrs.get("proveedor");			
			arrayFamilias= (String[]) this.attrs.get("familiasSeleccion");
			if(prototipo.getKey()>= 1){
				if(proveedor.getKey()>= 1){
					if(arrayFamilias.length>= 1){
						clave= toClaveMateriales();
						params= new HashMap<>();
						params.put("clave", clave);						
						for(String recordFamilia: arrayFamilias)
							familias= familias.concat(recordFamilia).concat(",");
						params.put("familias", familias.substring(0, familias.length()-1));				
						params.put("idProveedor", proveedor.getKey());				
						articulos= DaoFactory.getInstance().toEntitySet("VistaCapturaMaterialesDto", "materialesClave", params, Constantes.SQL_TODOS_REGISTROS);
						if(!articulos.isEmpty()){
							cleanArticulos();
							for(Entity art: articulos){
								toMoveData(new UISelectEntity(art), count, clave, false);
								count++;
							} // for
							getAdminOrden().toCalculate();
						} // if
						else
							cleanArticulos();
					} // if
					else
						cleanArticulos();											
				} // if
				else{
					cleanArticulos();
					JsfBase.addMessage("Es necesario seleccionar un proveedor.");
				} // else
			} // if
			else{
				cleanArticulos();
				JsfBase.addMessage("Es necesario seleccionar un prototipo.");				
			} // else
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch
		finally {			
			Methods.clean(params);
		} // finally
	} // doLoadArticulos	
	
	private void cleanArticulos() throws Exception{
		this.getAdminOrden().setArticulos(new ArrayList<>());
		this.getAdminOrden().getArticulos().add(new ArticuloVenta(-1L));		
		this.getAdminOrden().toCalculate();
		this.getAdminOrden().cleanPrecioDescuentoArticulo(); 
	} // cleanArticulos
	
	private String toClaveMateriales() throws Exception{
		StringBuilder regresar        = null;
		UISelectEntity contrato       = null;
		List<UISelectEntity> contratos= null;
		try {
			contratos= (List<UISelectEntity>) this.attrs.get("contratos");
			contrato= (UISelectEntity) this.attrs.get("contrato");
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString(), 3, '0', true));
			regresar.append(Fecha.getAnioActual());
			regresar.append(Cadena.rellenar(contratos.get(contratos.indexOf(contrato)).toString("orden"), 3, '0', true));
			regresar.append(toOrdenContratoLote(contrato.getKey()));
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		return regresar.toString();
	} // toClaveMateriales
	
	private String toOrdenContratoLote(Long idContrato) throws Exception{
		String regresar           = null;
		Map<String, Object> params= null;
		Entity contratoLote       = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_contrato=" + idContrato + " and id_prototipo=" + ((UISelectEntity) this.attrs.get("prototipo")).getKey());
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			regresar= Cadena.rellenar(contratoLote.toString("orden"), 3, '0', true);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toOrdenContratoLote
} 