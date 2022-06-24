package mx.org.kaana.keet.catalogos.contratos.entregas.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.contratos.entregas.beans.Evidencia;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetCatalogosContratosEntregasImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Importar.class);
	private static final long serialVersionUID= 2672741451185244787L;
  
  private TcKeetContratosLotesDto contratoLote;		
	private List<Evidencia> importados;
	private List<Evidencia> documentos;
  private String pathImage;

  public TcKeetContratosLotesDto getContratoLote() {
    return contratoLote;
  }

  public void setContratoLote(TcKeetContratosLotesDto contratoLote) {
    this.contratoLote = contratoLote;
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
		EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {			
			if(JsfBase.getFlashAttribute("idDesarrollo")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("idContratoLote", JsfBase.getFlashAttribute("idContratoLote"));
			this.attrs.put("idEmpresa", JsfBase.getFlashAttribute("idEmpresa"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("seleccionado", JsfBase.getFlashAttribute("seleccionado"));
			this.attrs.put("contrato", JsfBase.getFlashAttribute("contrato"));
			this.attrs.put("manzana", JsfBase.getFlashAttribute("manzana"));
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_LOGOTIPOS);
			this.attrs.put("file", ""); 
			this.setFile(new Importado());
			this.documentos= new ArrayList<>();
			this.toLoadContrato();
      this.doLoad();
      this.pathImage= Configuracion.getInstance().getPropiedadServidor("sistema.dns").concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()).concat("/archivos/");
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		    
  } // init
	
	private void toLoadContrato() throws Exception {
		try {
      this.contratoLote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(TcKeetContratosLotesDto.class, (Long)this.attrs.get("idContratoLote"));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toLoadEmpleado			
	
	@Override
	public void doLoad() {
    Map<String, Object>params= new HashMap<>();
		try {
      params.put("idContratoLote", this.contratoLote.getIdContratoLote());
		  this.importados= (List<Evidencia>)DaoFactory.getInstance().toEntitySet(Evidencia.class, "VistaCapturaDestajosDto", "evidencias", params);
      if(this.importados== null)
        this.importados= new ArrayList<>();
      else
        for (Evidencia item: this.importados) {
          item.setSql(ESql.SELECT);
        } // for
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // doLoad
	
	public void doFileUpload(FileUploadEvent event) {				
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
    String nameFile   = Archivo.toFormatNameFile(((Entity)this.attrs.get("seleccionado")).toString("descripcionLote").concat("_").concat(event.getFile().getFileName().toUpperCase()));
    File result       = null;		
		Long fileSize     = 0L;			
		Long idArchivo    = 0L;			
		try {			
      String source= Configuracion.getInstance().getPropiedadSistemaServidor("entregas");
      path.append(source);
      temp.append(String.valueOf((Long)this.attrs.get("idEmpresa")));
      temp.append("/");			
      temp.append(Fecha.getAnioActual());
      temp.append("/");      
      temp.append(this.contratoLote.getIdContrato());
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
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), this.getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " BYTES": " KB", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), -1L));
			idArchivo= this.toSaveFileArchivo(source);
  		this.attrs.put("file", this.getFile().getName());	
			this.documentos.add(this.toEvidencia(idArchivo));
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
      -1L, // Long idContratoLoteArchivo, 
      this.getFile().getName(), // String archivo, 
      null, // LocalDateTime eliminado, 
      this.getFile().getRuta(), // String ruta, 
      this.getFile().getFileSize(), // Long tamanio, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), // Long idTipoArchivo, 
      this.contratoLote.getIdContratoLote(), // Long idContratoLote, 
      Configuracion.getInstance().getPropiedadSistemaServidor("entregas").concat(this.getFile().getRuta()).concat(this.getFile().getName()), // String alias, 
      this.getFile().getOriginal(), // String nombre  
      (String)this.attrs.get("observaciones"), // observaciones      
			JsfBase.getAutentifica().getPersona().getNombreCompleto(), // String usuario
      idArchivo, // idAchivo
      this.getFile().getFormat().name() // tipo
		); // estatus						
		return regresar;
	} // toEvidencia
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Archivos")) 
			this.doLoad();		
	}	// doTabChange	

  public String doCancelar() {
    String regresar= null;    		
    try {			
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente) this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));			
			JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));			
			regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
	
	public String doAceptar() {
		String regresar        = null;
		Transaccion transaccion= null;
    List<Evidencia> todos  = new ArrayList<>();
		try {
      todos.addAll(this.importados);
      todos.addAll(this.documentos);
      this.contratoLote.setIdEntrego(JsfBase.getIdUsuario());
			transaccion= new Transaccion(this.contratoLote, todos);
      if(transaccion.ejecutar(EAccion.DESACTIVAR)) {
      	UIBackingUtilities.execute("janal.alert('Se actualizó y se importaron los archivos de forma correcta !');");
        // this.doLoad();
				regresar= this.doCancelar();
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    finally {
      Methods.clean(todos);
    } // finally
    return regresar;
	} // doAceptar
	
	public String doLimpiar() {
		String regresar        = null;
		Transaccion transaccion= null;
    List<Evidencia> todos  = new ArrayList<>();
		try {
      this.contratoLote.setEntrega(null);
			transaccion= new Transaccion(this.contratoLote, todos);
      if(transaccion.ejecutar(EAccion.COMPLETO)) {
      	UIBackingUtilities.execute("janal.alert('Se limpió de forma correcta el lote terminado');");
        // this.doLoad();
				regresar= this.doCancelar();
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    return regresar;
	} // doLimpiar
	
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
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
  }

  public void doRecuperar(Evidencia row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
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
  
}