package mx.org.kaana.keet.cajachica.backing;

import java.io.File;
import java.io.Serializable;
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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.cajachica.beans.ArchivoGasto;
import mx.org.kaana.keet.cajachica.beans.Gasto;
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.enums.ETipoVenta;
import mx.org.kaana.mantic.facturas.backing.Catalogos;
import mx.org.kaana.mantic.facturas.beans.FacturaFicticia;
import mx.org.kaana.mantic.ventas.beans.ArticuloVenta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;

@Named(value = "keetCajaChicaAccion")
@ViewScoped
public class Accion extends Catalogos implements Serializable {

	private static final long serialVersionUID= 2847354766000406350L;  			
  private static final Log LOG = LogFactory.getLog(Accion.class);
	
	private List<ArchivoGasto> documentos;	

	public List<ArchivoGasto> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<ArchivoGasto> documentos) {
		this.documentos = documentos;
	}	
  
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
			this.attrs.put("consecutivo", JsfBase.getFlashAttribute("consecutivo"));      						
      this.toLoadResidentes();
			this.doLoad();								
			this.initPadre();			
			this.attrs.put("accion", EAccion.AGREGAR);										
			this.attrs.put("buscaPorCodigo", false);
			if(!Cadena.isVacio(this.attrs.get("retornoInicial")))
				this.attrs.put("retorno", this.attrs.get("retornoInicial"));
			else
				this.attrs.put("retornoInicial", this.attrs.get("retorno"));
			if(!Cadena.isVacio(this.attrs.get("idGasto")))
				this.toLoadExistente();
			this.setFile(new Importado());
      this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_IDENTIFICACION);
			this.documentos= new ArrayList<>();
      this.attrs.put("pathPivote", (Configuracion.getInstance().getEtapaServidor().name().toLowerCase()).concat("/").concat("gastos").concat("/"));						
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void initPadre() throws Exception {
		try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());			
			this.setPrecio(ETipoVenta.MENUDEO.getNombreCampo());			
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
      this.accion= EAccion.AGREGAR;
			super.doLoad();
      this.toLoadTiposPagos();
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
	
	private void toLoadExistente() throws Exception {
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
	
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(tiposMediosPagos!= null && !tiposMediosPagos.isEmpty())
        if(Objects.equals(this.accion, EAccion.AGREGAR))
          ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
        else {
          int index= tiposMediosPagos.indexOf(((FacturaFicticia)this.getAdminOrden().getOrden()).getIkTipoMedioPago());
          if(index>= 0)
            ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(index));          
          else
            ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
        } // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toLoadTiposPagos
  
	@Override
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;				
    try {											
      Gasto gasto= this.toLoadGasto();
			if(gasto!= null && !this.getAdminOrden().getArticulos().isEmpty() && getAdminOrden().getArticulos().size()>0 && getAdminOrden().getArticulos().get(0).isValid()) {				
				transaccion= new Transaccion(gasto, this.documentos);
				if(transaccion.ejecutar((EAccion) this.attrs.get("accion"))) {
					JsfBase.addMessage("Caja chica", "Se registró el gasto de caja chica de forma correcta.", ETipoMensaje.INFORMACION);										
					regresar= this.doCancelar();
					if(Cadena.isVacio(this.attrs.get("retorno")))
						regresar= "accion".concat(Constantes.REDIRECIONAR);											
				} // if
				else
					JsfBase.addMessage("Caja chica", "Ocurrió un error al realizar la captura del gasto.", ETipoMensaje.ERROR);			
			} // if
			else
				JsfBase.addMessage("Caja chica", "No se han capturado el gasto.", ETipoMensaje.ERROR);			
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
			if(!this.getAdminOrden().getArticulos().isEmpty() && getAdminOrden().getArticulos().size()>0 && getAdminOrden().getArticulos().get(0).isValid()) {				
				transaccion= new Transaccion(this.toLoadGasto(), this.documentos);
				if(transaccion.ejecutar((EAccion) this.attrs.get("accion"))) {
					JsfBase.addMessage("Caja chica", "Se registró el gasto de caja chica de forma correcta.", ETipoMensaje.INFORMACION);					
					JsfBase.setFlashAttribute("idGasto", transaccion.getIdGasto());
					JsfBase.setFlashAttribute("retorno", "accion");										
					JsfBase.setFlashAttribute("retornoInicial", this.attrs.get("retornoInicial"));										
					toSetFlash();
					regresar= "resumen".concat(Constantes.REDIRECIONAR);
				} // if
				else
					JsfBase.addMessage("Caja chica", "Ocurrió un error al registrar el gasto.", ETipoMensaje.ERROR);			
			} // if
			else
				JsfBase.addMessage("Caja chica", "No se han capturado el gasto.", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina  
	
	private Gasto toLoadGasto() throws Exception {
		Gasto regresar= null;		
		try {
			if(!Cadena.isVacio(this.attrs.get("cajaChica")) && !((Entity)this.attrs.get("cajaChica")).isEmpty()) {
  			regresar= new Gasto();									
        regresar.setIdCajaChicaCierre(((Entity)this.attrs.get("cajaChica")).getKey());			
        regresar.setIdTipoMedioPago(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdTipoMedioPago());
        regresar.setArticulos(this.getAdminOrden().getArticulos());			
        if(!Cadena.isVacio(this.attrs.get("idGasto"))) 
          regresar.setIdGasto(Long.valueOf(this.attrs.get("idGasto").toString()));
      } // if  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toLoadGasto
	
	private void toSetFlash() {		
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
      this.doGlobalEvent(true);
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
	
	public String doPendientes() {
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
  
	private void toLoadResidentes() {
		List<Entity>residentes   = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			residentes= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "residentes", params);
			this.attrs.put("residentes", residentes);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadResidentes
 
	@Override
  public void doFindArticulo(Integer index) {
		try {
    	List<UISelectEntity> articulos= (List<UISelectEntity>)this.attrs.get("articulos");
	    UISelectEntity articulo  = (UISelectEntity)this.attrs.get("articulo");
	    UISelectEntity encontrado= (UISelectEntity)this.attrs.get("encontrado");
			if(encontrado!= null) {
				articulo= encontrado;
				this.attrs.remove("encontrado");
			} // else
			else 
				if(articulo== null)
					articulo= new UISelectEntity(new Entity(-1L));
				else
					if(articulos.indexOf(articulo)>= 0) 
						articulo= articulos.get(articulos.indexOf(articulo));
					else
						articulo= articulos.get(0);
			this.toMoveData(articulo, index);
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 
 
	public void doFileUpload(FileUploadEvent event) {				
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
		String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;			
		Long idArchivo    = 0L;			
		try {			
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("gastos"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(Fecha.getAnioActual());
      temp.append("/");      
      temp.append(Cadena.rellenar("9", 4, '9', true));
      temp.append(Cadena.rellenar("0", 6, '0', true));
      temp.append("/");      
			path.append(temp.toString());
			result= new File(path.toString());		
			if (!result.exists())
				result.mkdirs();
      path.append(nameFile);
			result = new File(path.toString());
			if (result.exists())
				result.delete();			      
			Archivo.toWriteFile(result, event.getFile().getInputStream());
			fileSize = event.getFile().getSize();						
			idArchivo= this.toSaveFileRecord("gastos");		
      /*UPLOAD*/
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), this.getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), idArchivo));
  		this.attrs.put("file", this.getFile().getName());	
			this.documentos.add(this.toGastoArchivo(idArchivo));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload	
 
	private ArchivoGasto toGastoArchivo(Long idArchivo) {				
  	String dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
		String url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat("/").concat((String)this.attrs.get("pathPivote"));
		ArchivoGasto regresar= new ArchivoGasto(
			idArchivo, // idAchivo
			null, // consecutivo
			0D, // importe
			0L, // articulos
			-1L, // idGasto
			this.getFile().getName(), // archivo
			null, // eliminado
			this.getFile().getRuta(), // ruta
			this.getFile().getFileSize(), // tamanio 
			JsfBase.getIdUsuario(), // idUsuario
			this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), // idTipoArchivo			
			Configuracion.getInstance().getPropiedadSistemaServidor("gastos").concat(this.getFile().getRuta()).concat(this.getFile().getName()), // alias
			-1L, // idGastoArchivo 
			this.getFile().getOriginal(), // nombre
      url.concat(this.getFile().getRuta()).concat(this.getFile().getName())// url      
		); 
		return regresar;
	} // toGastoArchivo
 
	private EFormatos getFileType(String fileName) {
		EFormatos regresar= EFormatos.FREE;
		try {
			if(fileName.contains(".")){
			  fileName= fileName.split("\\.")[fileName.split("\\.").length-1].toUpperCase();
				if (fileName.equals(EFormatos.PDF.name()))
					regresar= EFormatos.PDF;
				if (fileName.equals(EFormatos.ZIP.name()))
					regresar= EFormatos.ZIP;
				if (fileName.equals(EFormatos.DWG.name()))
					regresar= EFormatos.DWG;
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    return regresar;
	} // getFileType	
  
  public void doDelete(ArchivoGasto item) {
    try {
      int index= this.documentos.indexOf(item);
      if(index>= 0) {
        this.documentos.remove(index);
        File file= new File(item.getAlias());
        if(file.exists())
          file.delete();
      } // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
  }

  @Override
  public void doGlobalEvent(Boolean isViewException) {
		super.doGlobalEvent(isViewException);
    try {
  		if(isViewException && this.documentos!= null && this.documentos.size()> 0)
        for (ArchivoGasto item: this.documentos) {
          File file= new File(item.getAlias());
          if(!item.isValid() && file.exists())
            file.delete();
        } // for
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
  }
  
}