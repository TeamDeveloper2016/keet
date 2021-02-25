package mx.org.kaana.keet.controles.backing;

import java.io.File;
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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.controles.beans.DestajoResidenteArchivo;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseDestajoArchivo;
import mx.org.kaana.keet.controles.reglas.Seguimiento;
import mx.org.kaana.keet.enums.EControlesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

@Named(value = "keetControlesImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final long serialVersionUID = 154600879172477099L;
	List<IBaseDestajoArchivo> documentos;

	public List<IBaseDestajoArchivo> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<IBaseDestajoArchivo> documentos) {
		this.documentos = documentos;
	}
		
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
			this.attrs.put("figura", (Entity) JsfBase.getFlashAttribute("figura"));
			this.attrs.put("seleccionadoPivote", (Entity) JsfBase.getFlashAttribute("seleccionado"));
			this.attrs.put("idDepartamento", (Long) JsfBase.getFlashAttribute("idDepartamento"));
			this.attrs.put("concepto", (Entity)JsfBase.getFlashAttribute("concepto"));      			
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_LOGOTIPOS);
			this.attrs.put("idControl", ((Entity)this.attrs.get("concepto")).getKey());
			this.attrs.put("pathPivote", "/".concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/residentes/"));
			this.attrs.put("idContratoLoteResidente", ((Entity)this.attrs.get("seleccionadoPivote")).toLong("idContratoLoteResidente"));
			this.attrs.put("file", ""); 
			setFile(new Importado());
			this.documentos= new ArrayList<>();
			loadCatalogos();	
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(((Entity)this.attrs.get("seleccionadoPivote")).getKey().toString()));
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			this.attrs.put("contratoLote", contratoLote);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally	
	} // loadCatalogos	
	
  @Override
  public void doLoad() {
		List<Columna> columns= null;
		String idXml         = null;		
		try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombrePersona", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombreUsuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));			
		  this.attrs.put("importados", UIEntity.build("VistaCapturaDestajosDto", "importadosResidente", this.attrs, columns));
			this.doLoadFiles();
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    }// finally		
  } // doLoad		
	
	private void doLoadFiles() {
		List<Entity>importados= null;		
		String dns            = null;
		String url            = null;
		try {
			dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
			importados= (List<Entity>) this.attrs.get("importados");
			for(Entity importado: importados) {
				url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat(this.attrs.get("pathPivote").toString()).concat(importado.toString("ruta")).concat(importado.toString("archivo"));
				importado.put("url", new Value("url", url));
			} // for
			this.attrs.put("importados", importados);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch		
	} // doLoadFiles
	
	public void doFileUpload(FileUploadEvent event) {				
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
		String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;			
		Long idArchivo    = 0L;			
		Entity figura     = null;
		try {			
			figura= (Entity) this.attrs.get("figura");
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("residentes"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(Fecha.getAnioActual());
      temp.append("/");      
      temp.append(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContrato")); // contrato
      temp.append("/");      
      temp.append(figura.toLong("tipo")); 
      temp.append("/");      			
      temp.append(((Entity)this.attrs.get("concepto")).getKey()); // estacion
			temp.append("/");      
      temp.append(this.toClaveEstacion()); // codigoEstacion
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
			idArchivo= this.toRegisterFile("residentes");							
      /*UPLOAD*/
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), idArchivo));
  		this.attrs.put("file", this.getFile().getName());	
			this.documentos.add(toDestajoArchivo(idArchivo, figura.toLong("tipo")));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload		
	
	private String toClaveEstacion() {
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(this.attrs.get("idEmpresa").toString(), 3, '0', true));
			regresar.append(((Entity)this.attrs.get("seleccionadoPivote")).toString("ejercicio"));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("ordenContrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion	
	
	private IBaseDestajoArchivo toDestajoArchivo(Long idArchivo, Long tipo) throws Exception{												
		return toDestajoResidenteArchivo(idArchivo, tipo);
	} // toPrototipoArchivo
	
	private IBaseDestajoArchivo toDestajoResidenteArchivo(Long idArchivo, Long tipo) throws Exception{					
		DestajoResidenteArchivo regresar= new DestajoResidenteArchivo(
			idArchivo, // idAchivo
			tipo, // tipo
			((Entity)this.attrs.get("concepto")).toString("departamento"), // especialidad
			((Entity)this.attrs.get("concepto")).toString("nombre"), // concepto			
			this.toClaveEstacion(), // consecutivo
			-1L,
			this.getFile().getName(), // archivo			
			null, // eliminado
			this.getFile().getRuta(), // ruta			
			this.getFile().getFileSize(), // tamanio 			
			JsfBase.getIdUsuario(), // idUsuario			
			this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), // idTipoArchivo			
			(String)this.attrs.get("observaciones"), // observaciones 			
			Configuracion.getInstance().getPropiedadSistemaServidor("residentes").concat(this.getFile().getRuta()).concat(this.getFile().getName()), // alias
			toIdContratoDestajoFigura(), // idContratoDestajoResidente
			this.getFile().getOriginal() // nombre
		); 		
		return regresar;
	} // toDestajoResidenteArchivo
	
	private Long toIdContratoDestajoFigura() throws Exception {
		Entity destajoFigura     = null;
		Map<String, Object>params= null;
		Long regresar            = -1L;
		try {
			params= new HashMap<>();
			params.put("idControl", this.attrs.get("idControl"));			
			params.put("idControlEstatus", EControlesEstatus.CANCELADO.getKey());			
			params.put("idContratoLoteResidente", this.attrs.get("idContratoLoteResidente"));			
			destajoFigura= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosDestajosResidentesDto", "evidenciaDestajo", params);
			regresar= destajoFigura.getKey();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toIdContratoDestajoFigura
	
	public void doTabChange(TabChangeEvent event) {
		//if(event.getTab().getTitle().equals("Archivos") || event.getTab().getTitle().equals("Galeria"))
			//this.doLoad();					
	}	// doTabChange	
	
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
	
	public String doAceptar() {
    String regresar        = null;
		Seguimiento transaccion= null;
		try {
			transaccion= new Seguimiento(this.documentos);
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
  } // doPagina
	
	public String doCancelar() {
    String regresar= null;    
    try {						
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));									
			JsfBase.setFlashAttribute("figura", (Entity)this.attrs.get("figura"));									
			JsfBase.setFlashAttribute("seleccionado", (Entity)this.attrs.get("seleccionadoPivote"));									
			JsfBase.setFlashAttribute("idDesarrollo", (Long)this.attrs.get("idDesarrollo"));									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("idDepartamento").toString()));			
			JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
			JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
			regresar= "conceptos".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
  
}
