package mx.org.kaana.keet.cajachica.backing;

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
import mx.org.kaana.keet.cajachica.beans.ArchivoGasto;
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.enums.EEstatusGastos;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
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

@Named(value= "keetCajaChicaImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Importar.class);
	private static final long serialVersionUID= 2672741451185244787L;  
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
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");						
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("idGasto", JsfBase.getFlashAttribute("idGasto"));
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_IDENTIFICACION);
			this.attrs.put("file", ""); 
			this.setFile(new Importado());
			this.documentos= new ArrayList<>();
      this.attrs.put("pathPivote", File.separator.concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/").concat("gastos").concat("/"));						
			this.loadCatalogos();			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		    
  } // init
	
	private void loadCatalogos() throws Exception{
		Entity desarrollo        = null;
		Entity gasto             = null;		
		Entity cajaChica         = null;		
		Map<String, Object>params= null;		
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo=".concat(this.attrs.get("idDesarrollo").toString()));
			desarrollo= (Entity) DaoFactory.getInstance().toEntity("VistaDesarrollosDto", "lazy", params);
			this.attrs.put("desarrollo", desarrollo);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_gastos.id_gasto=".concat(this.attrs.get("idGasto").toString()));
			gasto= (Entity) DaoFactory.getInstance().toEntity("TcKeetGastosDto", "row", params);
			this.attrs.put("gasto", gasto);			
			this.attrs.put("acciones", !gasto.toLong("idGastoEstatus").equals(EEstatusGastos.DISPONIBLE.getKey()));
			params.clear();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo").toString());
			cajaChica= (Entity) DaoFactory.getInstance().toEntity("VistaCajaChicaDto", "findDesarrollo", params);
			this.attrs.put("cajaChica", cajaChica);		
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
		try {
			columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
		  this.attrs.put("importados", UIEntity.build("VistaGastosDto", "importados", this.attrs, columns));
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
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("gastos"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(Fecha.getAnioActual());
      temp.append("/");      
      temp.append(((Entity)this.attrs.get("gasto")).toString("consecutivo"));
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
			idArchivo= this.toRegisterFile("gastos");		
      /*UPLOAD*/
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), idArchivo));
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
		Entity gasto= (Entity) this.attrs.get("gasto");
  	String dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
		String url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat("/").concat((String)this.attrs.get("pathPivote"));
		ArchivoGasto regresar= regresar= new ArchivoGasto(
			idArchivo, // idAchivo
			gasto.toString("consecutivo"), // consecutivo
			gasto.toDouble("importe"), // importe
			gasto.toLong("articulos"), // articulos
			gasto.getKey(), // idGasto
			this.getFile().getName(), // archivo
			null, // eliminado
			this.getFile().getRuta(), // ruta
			this.getFile().getFileSize(), // tamanio 
			JsfBase.getIdUsuario(), // idUsuario
			this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), // idTipoArchivo			
			Configuracion.getInstance().getPropiedadSistemaServidor("gastos").concat(this.getFile().getRuta()).concat(this.getFile().getName()), // alias
			-1L, // idgastoArchivo 
			this.getFile().getOriginal(), // nombre
      url.concat(this.getFile().getRuta()).concat(this.getFile().getName())// url      
		); 
		return regresar;
	} // toGastoArchivo
	
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
  
}