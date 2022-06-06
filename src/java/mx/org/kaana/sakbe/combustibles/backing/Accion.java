package mx.org.kaana.sakbe.combustibles.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.sakbe.combustibles.beans.Evidencia;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.sakbe.combustibles.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import mx.org.kaana.sakbe.combustibles.beans.Combustible;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@Named(value= "sakbeComprasAccion")
@ViewScoped
public class Accion extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
	
  private EAccion accion;
  private Combustible combustible;
	private List<Evidencia> importados;
	private List<Evidencia> documentos;
  private String pathImage;
  
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

  public Combustible getCombustible() {
    return combustible;
  }

  public void setCombustible(Combustible combustible) {
    this.combustible = combustible;
  }
	
	public List<Evidencia> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Evidencia> documentos) {
		this.documentos = documentos;
	}	

  public List<Evidencia> getImportados() {
    return importados;
  }

  public void setImportados(List<Evidencia> importados) {
    this.importados = importados;
  }

  public String getPathImage() {
    return pathImage;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idCombustible", JsfBase.getFlashAttribute("idCombustible")== null? -1L: JsfBase.getFlashAttribute("idCombustible"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("isBanco", Boolean.FALSE);
			this.attrs.put("evidencias", 0L);
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_LOGOTIPOS);
			this.attrs.put("file", ""); 
			this.setFile(new Importado());
			this.documentos= new ArrayList<>();
      this.pathImage= Configuracion.getInstance().getPropiedadServidor("sistema.dns").concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()).concat("/archivos/");
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      params.put("idCombustible", this.attrs.get("idCombustible"));
      switch (this.accion) {
        case AGREGAR:											
          this.combustible= new Combustible(-1L);
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.combustible= (Combustible)DaoFactory.getInstance().toEntity(Combustible.class, "TcSakbeCombustiblesDto", "igual", params);
          this.combustible.setIkEmpresa(new UISelectEntity(this.combustible.getIdEmpresa()));
          this.combustible.setIkTipoCombustible(new UISelectEntity(this.combustible.getIdTipoCombustible()));
          this.combustible.setIkTipoMedioPago(new UISelectEntity(this.combustible.getIdTipoMedioPago()));
          this.combustible.setIkBanco(new UISelectEntity(this.combustible.getIdBanco()));
    		  this.importados= (List<Evidencia>)DaoFactory.getInstance().toEntitySet(Evidencia.class, "VistaCombustiblesDto", "evidencias", params);
          break;
      } // switch
      if(this.importados== null)
        this.importados= new ArrayList<>();
      else
        for (Evidencia item: this.importados) 
          item.setSql(ESql.SELECT);
      this.toEvidencias();
			this.toLoadCatalog();
      this.toLoadBancos();
      this.toLoadTiposMediosPagos();
      this.toLoadTiposCombustibles();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad  

	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				this.attrs.put("idPedidoSucursal", empresas.get(0));
				if(this.accion.equals(EAccion.AGREGAR))
  				this.combustible.setIkEmpresa(empresas.get(0));
			  else 
				  this.combustible.setIkEmpresa(empresas.get(empresas.indexOf(this.combustible.getIkEmpresa())));
			} // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // toLoadCatalog

	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "General":
        break;
      default:
        break;
    }
	} // doTabChange
  
	public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    List<Evidencia> todos  = new ArrayList<>();
		try {
      todos.addAll(this.importados);
      todos.addAll(this.documentos);
			transaccion = new Transaccion(this.combustible, todos);
			if (transaccion.ejecutar(this.accion)) {
				if(this.accion.equals(EAccion.AGREGAR)) 
    			UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 el ticket de compra', '"+ this.combustible.getConsecutivo()+ "');");
        else 
 				  if(!this.accion.equals(EAccion.CONSULTAR)) 
    			  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el ticket de compra"), ETipoMensaje.INFORMACION);
   			regresar= this.doCancelar();
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el ticket de compra.", ETipoMensaje.ALERTA);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(todos);
    } // finally
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idCombustible", this.combustible.getIdCombustible());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
 
	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(!tiposMediosPagos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.combustible.setIkTipoMedioPago(tiposMediosPagos.get(0));
        else  
          this.combustible.setIkTipoMedioPago(tiposMediosPagos.get(tiposMediosPagos.indexOf(this.combustible.getIkTipoMedioPago())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadTiposMediosPagos
  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = null;
		List<Columna> columns      = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns= new ArrayList<>();
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.seleccione("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS, "nombre");
			this.attrs.put("bancos", bancos);
      if(!bancos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.combustible.setIkBanco(bancos.get(0));
        else  
          this.combustible.setIkBanco(bancos.get(bancos.indexOf(this.combustible.getIkBanco())));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadBancos  
  
	public void doCheckTipoMedioPago() {
		Long tipoMedioPago= null;
		try {
      UIBackingUtilities.execute(
        "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'libre', mascara: 'libre'});"
      );		
			tipoMedioPago= this.combustible.getIdTipoMedioPago();
			this.attrs.put("isBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago));
      if(!ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago)) 
        UIBackingUtilities.execute(
          "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'requerido', mascara: 'libre'});"
        );		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doCheckTipoMedioPago
 
	private void toLoadTiposCombustibles() {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposCombustibles= UIEntity.build("TcSakbeTiposCombustiblesDto", "row", params);
			this.attrs.put("tiposCombustibles", tiposCombustibles);
      if(!tiposCombustibles.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.combustible.setIkTipoCombustible(tiposCombustibles.get(0));
        else  
          this.combustible.setIkTipoCombustible(tiposCombustibles.get(tiposCombustibles.indexOf(this.combustible.getIkTipoCombustible())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadTiposCombustibles  
  
	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	} // doAutoSaveOrden

	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			transaccion= new Transaccion(this.combustible);
			if (transaccion.ejecutar(EAccion.AGREGAR)) {
   			UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 orden de compra', '"+ this.combustible.getConsecutivo()+ "');");
				this.accion= EAccion.MODIFICAR;
				this.attrs.put("autoSave", Global.format(EFormatoDinamicos.FECHA_HORA, Fecha.getRegistro()));
			} // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	} // toSaveRecord

	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA");
		if(isViewException && this.combustible!= null && this.combustible.isComplete())
		  this.toSaveRecord();
	} // doGlobalEvent
  
	public void doFileUpload(FileUploadEvent event) {				
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
    String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;			
		Long idArchivo    = 0L;			
		try {			
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("combustibles"));
      temp.append(this.combustible.getIdEmpresa());
      temp.append("/");			
      temp.append(Fecha.getAnioActual());
      temp.append("/");      
      temp.append(this.combustible.getIdUsuario());
      temp.append("/");      
      temp.append(Fecha.getHoyEstandar());
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
			idArchivo= this.toSaveFileRecord("combustibles");							
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), this.getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " BYTES": " KB", temp.toString(), (String)this.attrs.get("comentarios"), event.getFile().getFileName().toUpperCase(), idArchivo));
  		this.attrs.put("file", this.getFile().getName());	
			this.documentos.add(this.toEvidencia(idArchivo));
      this.toEvidencias();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload		
	
	private Evidencia toEvidencia(Long idArchivo) {		
		Evidencia regresar= new Evidencia(
      -1L, // Long idCombustibleArchivo, 
      this.getFile().getName(), // String archivo, 
      null, // LocalDateTime eliminado, 
      this.getFile().getRuta(), // String ruta, 
      this.getFile().getFileSize(), // Long tamanio, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), // Long idTipoArchivo, 
      Configuracion.getInstance().getPropiedadSistemaServidor("combustibles").concat(this.getFile().getRuta()).concat(this.getFile().getName()), // String alias, 
      this.getFile().getOriginal(), // String nombre  
      this.combustible.getIdCombustible(), // Long idCombustible
      (String)this.attrs.get("comentarios"), // observaciones      
			JsfBase.getAutentifica().getPersona().getNombreCompleto(), // String usuario
      idArchivo, // idAchivo
      this.getFile().getFormat().name() // tipo
		); // estatus						
		return regresar;
	} // toEvidencia
  
	private EFormatos getFileType(String fileName) {
		EFormatos regresar= EFormatos.JPG;
		try {
			if(fileName.contains(".")) {
			  fileName= fileName.split("\\.")[fileName.split("\\.").length-1].toUpperCase();
				if (fileName.equals(EFormatos.PNG.name()))
					regresar= EFormatos.PNG;
				if (fileName.equals(EFormatos.JPG.name()))
					regresar= EFormatos.JPG;
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    return regresar;
	} // getFileType
 
  public void doDelete(Evidencia row) {
    try {
      int index= this.documentos.indexOf(row);
      if(index>= 0) {
        this.documentos.remove(index);
        File file= new File(row.getAlias());
        if(file.exists())
          file.delete();
      } // if
      this.toEvidencias();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
  }
	
  public void doEliminar(Evidencia row) {
    try {
      if(row.getKey()< 0L)
        this.documentos.remove(row);
      else  
        row.setSql(ESql.DELETE);
      this.toEvidencias();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
  }

  public void doRecuperar(Evidencia row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
    this.toEvidencias();
  }
 
	public StreamedContent doFileDownload(Evidencia file) {
		StreamedContent regresar= null;
		try {
			File reference= new File(file.getAlias());
			if(reference.exists()) {
				InputStream stream = new FileInputStream(reference);
				if(Objects.equals(file.getIdTipoArchivo(), 1L))
					regresar= DefaultStreamedContent.builder().contentType(EFormatos.XML.getContent()).name(file.getNombre()).stream(() -> stream).build();
				else
					regresar= DefaultStreamedContent.builder().contentType(EFormatos.PDF.getContent()).name(file.getNombre()).stream(() -> stream).build();
			} // if	
			else {
				LOG.warn("No existe el archivo: "+ file.getAlias());
        JsfBase.addMessage("No existe el archivo "+ file.getNombre()+ ", favor de verificarlo.");
			} // else	
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		return regresar;
	}
 
  private void toEvidencias() {
    int count= 0;
    for (Evidencia item: this.importados) {
      if(!Objects.equals(item.getSql(), ESql.DELETE))
        count++;
    } // for
    for (Evidencia item: this.documentos) {
      if(!Objects.equals(item.getSql(), ESql.DELETE))
        count++;
    } // for
    this.attrs.put("evidencias", count);    
  }
  
}