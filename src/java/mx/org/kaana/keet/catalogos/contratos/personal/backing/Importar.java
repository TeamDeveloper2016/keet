package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.keet.catalogos.contratos.personal.beans.DocumentoIncidencia;
import mx.org.kaana.keet.catalogos.contratos.personal.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetCatalogosContratosPersonalImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Importar.class);
	private static final long serialVersionUID= 2672741451185244787L;
  private ContratoPersonal contratoPersonal;		
	List<DocumentoIncidencia> documentos;

	public ContratoPersonal getContratoPersonal() {
		return contratoPersonal;
	}

	public void setContratoPersonal(ContratoPersonal contratoPersonal) {
		this.contratoPersonal = contratoPersonal;
	}	

	public List<DocumentoIncidencia> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentoIncidencia> documentos) {
		this.documentos = documentos;
	}	
	
	@PostConstruct
  @Override
  protected void init() {		
		EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Long idEmpresaPersona    = null;
    try {			
			if(JsfBase.getFlashAttribute("idDesarrollo")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			idEmpresaPersona= (Long) JsfBase.getFlashAttribute("idEmpresaPersona");			
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			this.attrs.put("idEmpresaPersona", idEmpresaPersona);									
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_IDENTIFICACION);
			this.attrs.put("file", ""); 
			setFile(new Importado());
			this.documentos= new ArrayList<>();
			this.loadEmpleado();
			this.loadCombos();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		    
  } // init
	
	private void loadEmpleado() throws Exception{
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda((Long)this.attrs.get("idEmpresaPersona"));
			this.contratoPersonal= motor.toPersonaIncidencia(false);												
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadEmpleado			
	
	public void loadCombos() {
		List<UISelectEntity> incidencias= null;
		UISelectEntity incidencia       = null;
		Map<String, Object>params       = null;
		try {
			params= new HashMap<>();
			params.put("grupo", ((EOpcionesResidente)this.attrs.get("opcionResidente")).equals(EOpcionesResidente.INCIDENCIAS) ? 1L : 3L);						
			params.put("idEmpresaPersona", (Long)this.attrs.get("idEmpresaPersona"));						
			incidencias= UIEntity.seleccione("VistaIncidentesDto", "incidenciasPersonal", params, "consecutivo");			
      this.attrs.put("incidencias", incidencias);			
      incidencia= UIBackingUtilities.toFirstKeySelectEntity(incidencias);		
			this.attrs.put("incidencia", incidencia);						
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadCombos	
	
	@Override
	public void doLoad() {
		List<Columna> columns= null;
		try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombrePersona", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombreUsuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
		  this.attrs.put("importados", UIEntity.build("VistaIncidenciasDto", "importados", this.attrs, columns));
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    }// finally
	} // doLoad
	
	public void doFileUpload(FileUploadEvent event) {				
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
		String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;			
		Long idArchivo    = 0L;			
		try {			
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("incidencias"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(Fecha.getAnioActual());
      temp.append("/");      
      temp.append(this.contratoPersonal.getIdPersona());
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
			fileSize= event.getFile().getSize();						
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase()));
  		this.attrs.put("file", this.getFile().getName());	
			idArchivo= toRegisterFile("incidencias");							
			this.documentos.add(toIncidenciaArchivo(idArchivo));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload		
	
	private DocumentoIncidencia toIncidenciaArchivo(Long idArchivo){		
		UISelectEntity incidencia= (UISelectEntity)this.attrs.get("incidencia");
		List<UISelectEntity> especialidades= (List<UISelectEntity>)this.attrs.get("incidencias");
		if(incidencia!= null && especialidades.indexOf(incidencia)>= 0)
			incidencia= especialidades.get(especialidades.indexOf(incidencia));
		else
			incidencia= especialidades.get(0);
		DocumentoIncidencia regresar= null;		
		regresar= new DocumentoIncidencia(
			idArchivo, // idAchivo
			incidencia.getKey(), // idIncidencia
			this.getFile().getName(), // archivo
			this.getFile().getRuta(), // ruta
			this.getFile().getFileSize(), // tamanio 
			JsfBase.getIdUsuario(), // idUsuario
			this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), // idTipoArchivo
			(String)this.attrs.get("observaciones"), // observaciones 
			Configuracion.getInstance().getPropiedadSistemaServidor("incidencias").concat(this.getFile().getRuta()).concat(this.getFile().getName()), // alias
			-1L, // idIncidenteArchivo 
			this.getFile().getOriginal(), // nombre
			incidencia.toDate("vigenciaInicio"), // fechaInicio
			incidencia.toDate("vigenciaFin"), // fechaFin	
			incidencia.toLong("ejercicio"), // ejercicio
			incidencia.toString("consecutivo"), // consecutivo
			incidencia.toString("estatus"), // estatus
			incidencia.toString("tipoIncidente")
		); // estatus						
		return regresar;
	} // toPrototipoArchivo
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Archivos")) 
			this.doLoad();		
	}	// doTabChange	

  public String doCancelar() {
    String regresar= null;    		
    try {			
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente) this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", (Long) this.attrs.get("idDesarrollo"));			
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
		try {
			transaccion= new Transaccion(this.documentos);
      if(transaccion.ejecutar(EAccion.SUBIR)) {
      	UIBackingUtilities.execute("janal.alert('Se importaron los archivos de forma correcta !');");
				regresar= this.doCancelar();
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    return regresar;
	} // doAceptar
	
	private EFormatos getFileType(String fileName){
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
}