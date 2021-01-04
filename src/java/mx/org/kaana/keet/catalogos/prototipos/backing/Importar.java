package mx.org.kaana.keet.catalogos.prototipos.backing;

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
import mx.org.kaana.keet.catalogos.prototipos.beans.Documento;
import mx.org.kaana.keet.catalogos.prototipos.beans.RegistroPrototipo;
import mx.org.kaana.keet.catalogos.prototipos.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
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

@Named(value= "keetCatalogosPrototiposImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Importar.class);
	private static final long serialVersionUID= 2672741451185244787L;
  private RegistroPrototipo prototipo;

	public RegistroPrototipo getPrototipo() {
		return prototipo;
	}

	public void setPrototipo(RegistroPrototipo prototipo) {
		this.prototipo = prototipo;
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {      
			if(JsfBase.getFlashAttribute("idPrototipo")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.attrs.put("idPrototipo", JsfBase.getFlashAttribute("idPrototipo"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_PLANOS);
			setFile(new Importado());
			this.attrs.put("file", ""); 
			this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
			this.loadCombos();
			this.prototipo= new RegistroPrototipo(Long.valueOf(this.attrs.get("idPrototipo").toString()));						
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	public void loadCombos() {
		List<UISelectEntity> especialidades= null;
		UISelectEntity especialidad        = null;
		Map<String, Object>params          = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			especialidades= UIEntity.seleccione("TcKeetEspecialidadesDto", "row", params, "nombre");
      this.attrs.put("especialidades", especialidades);			
      especialidad= UIBackingUtilities.toFirstKeySelectEntity(especialidades);		
			this.attrs.put("especialidad", especialidad);			
			this.doActualizaPlanos();			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadCombos
	
	public void doActualizaPlanos() {
		List<UISelectEntity> planos= null;
		Map<String, Object>params  = null;
		UISelectEntity especialidad= null;
		try {
			params= new HashMap<>();
			especialidad= (UISelectEntity) this.attrs.get("especialidad");
			params.put(Constantes.SQL_CONDICION, "id_especialidad=" + especialidad.getKey());
			planos= UIEntity.seleccione("TcKeetPlanosDto", "row", params, "nombre");
      this.attrs.put("planos", planos);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch
		finally{
			Methods.clean(params);
		} // finally
	} // doActualizaPlanos
	
	@Override
	public void doLoad() {
		List<Columna> columns= null;
		try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
		  this.attrs.put("importados", UIEntity.build("VistaPrototiposDto", "importados", this.attrs, columns));
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
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("prototipos"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(this.toClave());
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
			idArchivo= toRegisterFile("prototipos");							
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), idArchivo));
  		this.attrs.put("file", this.getFile().getName());	
			this.prototipo.getDocumentos().add(toProtipoArchivo(idArchivo));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload	
	
	private String toClave(){
		String regresar                = null;
		List<UISelectEntity>clientes   = null;
		UISelectEntity clienteSeleccion= null;
		try {
			clientes= (List<UISelectEntity>) this.attrs.get("clientes");
			clienteSeleccion= clientes.get(clientes.indexOf(this.prototipo.getPrototipo().getIkCliente()));
			regresar= String.valueOf(clienteSeleccion.getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toClave
	
	private Documento toProtipoArchivo(Long idArchivo){
		UISelectEntity especialidad= (UISelectEntity)this.attrs.get("especialidad");
		List<UISelectEntity> especialidades= (List<UISelectEntity>)this.attrs.get("especialidades");
		if(especialidad!= null && especialidades.indexOf(especialidad)>= 0)
			especialidad= especialidades.get(especialidades.indexOf(especialidad));
		else
			especialidad= especialidades.get(0);
		UISelectEntity plano= (UISelectEntity)this.attrs.get("plano");
		List<UISelectEntity> planos= (List<UISelectEntity>)this.attrs.get("planos");
		if(plano!= null && planos.indexOf(plano)>= 0)
			plano= planos.get(planos.indexOf(plano));
		else
			plano= planos.get(0);
		Documento regresar= null;		
		regresar= new Documento(
			plano.getKey(),
			this.getFile().getName(), 
			this.getFile().getRuta(), 
			this.getFile().getFileSize(), 
			JsfBase.getIdUsuario(), 
			this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), 
			(String)this.attrs.get("observaciones"), 
			-1L, 			
			Configuracion.getInstance().getPropiedadSistemaServidor("prototipos").concat(this.getFile().getRuta()).concat(this.getFile().getName()),
			this.prototipo.getIdPrototipo(), 
			this.getFile().getOriginal(),
			especialidad.toString("nombre"),
			plano.toString("nombre"),
			idArchivo
		);
		return regresar;
	} // toPrototipoArchivo
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Archivos")) 
			this.doLoad();		
	}	// doTabChange	

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idPrototipoProcess", this.prototipo.getIdPrototipo());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
	public String doAceptar() {
		String regresar        = null;
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(this.prototipo);
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