package mx.org.kaana.sakbe.suministros.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
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
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.sakbe.suministros.beans.Evidencia;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.sakbe.suministros.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import mx.org.kaana.sakbe.enums.ECombustiblesEstatus;
import mx.org.kaana.sakbe.suministros.beans.Suministro;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@Named(value= "sakbeSuministrosAccion")
@ViewScoped
public class Accion extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
	
  private EAccion accion;
  private Suministro suministro;
	private List<Evidencia> importados;
	private List<Evidencia> documentos;
  private String pathImage;
  
	public String getAgregar() {
		return Objects.equals(this.accion, EAccion.AGREGAR)? "none": "";
	}

  public Suministro getSuministro() {
    return suministro;
  }

  public void setSuministro(Suministro suministro) {
    this.suministro = suministro;
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
      this.attrs.put("opcionResidente", JsfBase.getFlashAttribute("opcionResidente"));
      this.attrs.put("idDesarrollo", JsfBase.getFlashAttribute("idDesarrollo"));
      this.attrs.put("idTipoCombustible", JsfBase.getFlashAttribute("idTipoCombustible"));
      this.attrs.put("idSuministro", JsfBase.getFlashAttribute("idSuministro"));
      this.attrs.put("porcentaje", JsfBase.getFlashAttribute("porcentaje"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("evidencias", 0L);
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_LOGOTIPOS);
			this.attrs.put("file", ""); 
			this.attrs.put("index", 0); 
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
      params.put("idSuministro", this.attrs.get("idSuministro"));
      switch (this.accion) {
        case AGREGAR:											
          this.suministro= new Suministro(-1L);
          this.suministro.setIdDesarrollo((Long)this.attrs.get("idDesarrollo"));
          this.suministro.setIkDesarrollo(new UISelectEntity(this.suministro.getIdDesarrollo()));
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.suministro= (Suministro)DaoFactory.getInstance().toEntity(Suministro.class, "TcSakbeSuministrosDto", "igual", params);
          this.suministro.setIkDesarrollo(new UISelectEntity(this.suministro.getIdDesarrollo()));
          this.suministro.setIkMaquinaria(new UISelectEntity(this.suministro.getIdMaquinaria()));
    		  this.importados= (List<Evidencia>)DaoFactory.getInstance().toEntitySet(Evidencia.class, "VistaSuministrosDto", "evidencias", params);
          break;
      } // switch
      this.suministro.setIdTipoCombustible((Long)this.attrs.get("idTipoCombustible"));
      if(this.importados== null)
        this.importados= new ArrayList<>();
      else
        for (Evidencia item: this.importados) 
          item.setSql(ESql.SELECT);
      this.toEvidencias();
      this.toLoadMaquinarias();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad  

	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "General":
        break;
      default:
        break;
    }
	} // doTabChange
  
	public void doContinuar() {  
    Map<String, Object> params = new HashMap<>();
    try {      
      this.doAceptar();
      this.setFile(new Importado());
      this.documentos= new ArrayList<>();
			this.attrs.put("evidencias", 0L);
			this.attrs.put("file", ""); 
			this.attrs.put("index", 0); 
      this.attrs.put("porcentaje", toLoadCombustible());
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
	public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    List<Evidencia> todos  = new ArrayList<>();
		try {
      todos.addAll(this.importados);
      todos.addAll(this.documentos);
			transaccion = new Transaccion(this.suministro, todos);
			if (transaccion.ejecutar(this.accion)) {
				if(this.accion.equals(EAccion.AGREGAR)) 
    			UIBackingUtilities.execute("janal.back('gener\\u00F3 el suministro', '"+ this.suministro.getConsecutivo()+ "');");
        else 
 				  if(!this.accion.equals(EAccion.CONSULTAR)) 
    			  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agregó" : "modificó").concat(" el suministro"), ETipoMensaje.INFORMACION);
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
    JsfBase.setFlashAttribute("idDessarrollo", this.attrs.get("idDessarrollo"));
    JsfBase.setFlashAttribute("idSuministro", this.attrs.get("idSuministro"));
    JsfBase.setFlashAttribute("opcion", this.attrs.get("opcionResidente"));			
    JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Combustibles/visor");			
    return (String)this.attrs.get("retorno");
  } // doCancelar
 
	private void toLoadMaquinarias() {
		List<UISelectEntity> maquinarias= null;
		Map<String, Object> params = new HashMap<>();
		List<Columna> columns      = null;
		try {
			params.put("idDesarrollo", this.suministro.getIdDesarrollo());
			columns= new ArrayList<>();
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			maquinarias= UIEntity.seleccione("VistaSuministrosDto", "maquinarias", params, columns, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("maquinarias", maquinarias);
      if(!maquinarias.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.suministro.setIkMaquinaria(maquinarias.get(0));
        else  
          this.suministro.setIkMaquinaria(maquinarias.get(maquinarias.indexOf(this.suministro.getIkMaquinaria())));
      this.toCheckLitros();
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadMaquinarias  
  
	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	} // doAutoSaveOrden

	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			transaccion= new Transaccion(this.suministro);
			if (transaccion.ejecutar(EAccion.AGREGAR)) {
   			UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 suministro', '"+ this.suministro.getConsecutivo()+ "');");
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
		if(isViewException && this.suministro!= null && this.suministro.isComplete())
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
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("suministros"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      temp.append("/");			
      temp.append(Fecha.getAnioActual());
      temp.append("/");      
      temp.append(this.suministro.getIdDesarrollo());
      temp.append("/");      
      temp.append(this.suministro.getIdUsuario());
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
			idArchivo= this.toSaveFileRecord("suministros");							
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
      -1L, // Long idSuministroArchivo, 
      this.getFile().getName(), // String archivo, 
      null, // LocalDateTime eliminado, 
      this.getFile().getRuta(), // String ruta, 
      this.getFile().getFileSize(), // Long tamanio, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), // Long idTipoArchivo, 
      Configuracion.getInstance().getPropiedadSistemaServidor("suministros").concat(this.getFile().getRuta()).concat(this.getFile().getName()), // String alias, 
      this.getFile().getOriginal(), // String nombre  
      this.suministro.getIdSuministro(), // Long idSuministro
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
 
	public void doAsignaGeoreferencia(String latitud, String longitud) {
    if(Objects.equals(this.accion, EAccion.AGREGAR)) {
		  this.suministro.setLatitud(latitud);
		  this.suministro.setLongitud(longitud);
    } // if  
	} // doAsignaGeoreferencia
 
  public void doUpdateLitros() {
    Entity porcentaje= (Entity)this.attrs.get("porcentaje");
    if(porcentaje!= null) {
      double dinamico= porcentaje.toDouble("saldo")- this.suministro.getLitros();
      int calculo = (int)Numero.toRedondearSat(dinamico* 100/ porcentaje.toDouble("litros"));
      if(calculo< 0) {
        dinamico= 0D;
        calculo = 0;
        this.suministro.setLitros(porcentaje.toDouble("saldo"));
      } // if  
      porcentaje.get("dinamico").setData(new BigDecimal(dinamico));
      porcentaje.get("porcentaje").setData(new BigDecimal(calculo));
    } // if  
  }
 
  private Entity toLoadCombustible() throws Exception {
    Entity regresar           = null;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idTipoCombustible", this.attrs.get("idTipoCombustible"));
      params.put("disponibles", ECombustiblesEstatus.ACEPTADO.getKey()+ ","+ ECombustiblesEstatus.EN_PROCESO.getKey());      
      regresar= (Entity)DaoFactory.getInstance().toEntity("VistaCombustiblesDto", "litros", params);
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
 
  private void toCheckLitros() {
    Entity porcentaje= (Entity)this.attrs.get("porcentaje");
    Double saldo     = 80D; 
    Double litros    = 80D; 
    if(porcentaje!= null)
      saldo= porcentaje.toDouble("saldo");
  	List<UISelectEntity> maquinarias= (List<UISelectEntity>)this.attrs.get("maquinarias");
    if(!maquinarias.isEmpty()) {
      int index= maquinarias.indexOf(this.suministro.getIkMaquinaria());      
      if(index>= 0 && this.suministro.getIkMaquinaria().getKey()> 0L) {
        this.suministro.setIkMaquinaria(maquinarias.get(index));
        litros= this.suministro.getIkMaquinaria().toDouble("litros");
      } // if  
    } // if
    if(saldo> litros)
      saldo= litros;
    UIBackingUtilities.execute("janal.renovates([{id: 'contenedorGrupos\\\\:litros', value: {validaciones: 'requerido|flotante|rango({\"min\":1,\"max\":"+ saldo+"})', mascara: 'libre', grupo: 'general', individual: true}}])");
  }
  
}