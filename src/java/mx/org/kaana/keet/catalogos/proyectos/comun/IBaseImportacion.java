package mx.org.kaana.keet.catalogos.proyectos.comun;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.proyectos.beans.Documento;
import mx.org.kaana.keet.catalogos.proyectos.beans.Generador;
import mx.org.kaana.keet.catalogos.proyectos.beans.Presupuesto;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import mx.org.kaana.keet.catalogos.proyectos.reglas.Transaccion;
import mx.org.kaana.keet.enums.EArchivosProyectos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

public abstract class IBaseImportacion extends IBaseImportar implements Serializable{
	
	private static final long serialVersionUID = 270325995498493885L;
	protected RegistroProyecto registroProyecto;

	public RegistroProyecto getRegistroProyecto() {
		return registroProyecto;
	}

	public void setRegistroProyecto(RegistroProyecto registroProyecto) {
		this.registroProyecto = registroProyecto;
	}
	
	protected void initBase(EArchivosProyectos tipoArchivo){
		try {
			if(JsfBase.getFlashAttribute("idProyecto")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.attrs.put("idProyecto", JsfBase.getFlashAttribute("idProyecto"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null ? "filtro" : JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_PLANOS);
			this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			this.attrs.put("file", "");
			this.attrs.put("tipoArchivo", tipoArchivo);
			setFile(new Importado());			
			this.registroProyecto= new RegistroProyecto(Long.valueOf(this.attrs.get("idProyecto").toString()));
			loadCombos();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	}	// initBase	

	private void loadCombos(){
		Map<String, Object>params          = null;
		UISelectEntity especialidad        = null;
		List<UISelectEntity> especialidades= null;
		List<UISelectEntity> desarrollos   = null;
		List<UISelectEntity> tiposObras    = null;
		List<UISelectEntity> fachadas      = null;		
		List<UISelectEntity> prototipos    = null;		
		List<UISelectEntity> estaciones    = null;		
		List<UISelectEntity> presupuestos  = null;		
		TcManticClientesDto cliente        = null;
		UISelectEntity estacion            = null;
		try {			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			estaciones= UIEntity.seleccione("TcKeetEstacionesDto", "row", params, "nombre");
      this.attrs.put("estaciones", estaciones);
			presupuestos= UIEntity.seleccione("TcKeetTiposPresupuestosDto", "row", params, "nombre");
      this.attrs.put("presupuestos", presupuestos);
			estacion= UIBackingUtilities.toFirstKeySelectEntity(estaciones);
			this.attrs.put("estacion", estacion);
			doActualizaTiposGeneradores();
			desarrollos= UIEntity.seleccione("TcKeetDesarrollosDto", "row", params, "nombre");
      this.attrs.put("desarrollos", desarrollos);
			tiposObras= UIEntity.seleccione("VistaTiposObrasDto", "catalogo", params, "tipoObra");
      this.attrs.put("tipoObras", tiposObras);
			fachadas= UIEntity.seleccione("TcKeetTiposFachadasDto", "row", params, "nombre");
      this.attrs.put("fachadas", fachadas);					
			especialidades= UIEntity.seleccione("TcKeetEspecialidadesDto", "row", params, "nombre");
      this.attrs.put("especialidades", especialidades);			
      especialidad= UIBackingUtilities.toFirstKeySelectEntity(especialidades);		
			this.attrs.put("especialidad", especialidad);			
			doActualizaPlanos();			
			params.clear();
			params.put(Constantes.SQL_CONDICION, "id_cliente=" + this.registroProyecto.getProyecto().getIdCliente());
			prototipos= UIEntity.seleccione("TcKeetPrototiposDto", "row", params, "nombre");
      this.attrs.put("prototipos", prototipos);	
			cliente= (TcManticClientesDto) DaoFactory.getInstance().findById(TcManticClientesDto.class, this.registroProyecto.getProyecto().getIdCliente());
			this.attrs.put("claveCliente", cliente.getClave());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadCombos
	
	public void doActualizaPlanos(){
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
	
	public void doActualizaTiposGeneradores(){
		List<UISelectEntity> tiposGeneradores= null;
		Map<String, Object>params            = null;
		UISelectEntity estacion              = null;
		try {
			params= new HashMap<>();
			estacion= (UISelectEntity) this.attrs.get("estacion");
			params.put(Constantes.SQL_CONDICION, "id_estacion=" + estacion.getKey());
			tiposGeneradores= UIEntity.seleccione("TcKeetTiposGeneradoresDto", "row", params, "nombre");
      this.attrs.put("tiposGeneradores", tiposGeneradores);			
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
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Archivos")) 
			this.doLoad();		
	}	// doTabChange	
	
	@Override
	public void doLoad() {
		List<Columna> columns         = null;
		EArchivosProyectos tipoArchivo= null;
		try {
			tipoArchivo= (EArchivosProyectos) this.attrs.get("tipoArchivo");
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
		  this.attrs.put("importados", UIEntity.build(tipoArchivo.getUnit(), tipoArchivo.getIdXml(), this.attrs, columns));
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
		StringBuilder path     = new StringBuilder();  
		StringBuilder temp     = new StringBuilder();  
		String nameFile        = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result            = null;		
		Long fileSize          = 0L;		
		Long idArchivo         = 0L;		
		EArchivosProyectos tipo= null;
		try {			
			tipo= (EArchivosProyectos) this.attrs.get("tipoArchivo");
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor(tipo.getPath()));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(this.attrs.get("claveCliente").toString());
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
			idArchivo= this.toSaveFileRecord(tipo.getPath());
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), idArchivo));
  		this.attrs.put("file", this.getFile().getName());		
			switch(tipo){ 
				case DOCUMENTOS:
					this.registroProyecto.getDocumentos().add((Documento) toArchivo(idArchivo));
					break;
				case GENERADORES:
					this.registroProyecto.getGeneradores().add((Generador) toArchivo(idArchivo));
					break;
				case PRESUPUESTOS:
					this.registroProyecto.getPresupuestos().add((Presupuesto) toArchivo(idArchivo));
					break;
			} // switch			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload	
	
	public abstract IBaseDto toArchivo(Long idArchivo);
	
	protected EFormatos getFileType(String fileName){
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
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(this.registroProyecto, (EArchivosProyectos) this.attrs.get("tipoArchivo"));
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
	
	public String doCancelar() {   
		JsfBase.setFlashAttribute("idProyecto", this.attrs.get("idProyecto"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
	protected UISelectEntity getDataCombo(String llave, String nombreLista){
		UISelectEntity regresar        = null;
		try {
			regresar= (UISelectEntity)this.attrs.get(llave);
			List<UISelectEntity> items= (List<UISelectEntity>)this.attrs.get(nombreLista);
			if(regresar!= null && items.indexOf(regresar)>= 0)
				regresar= items.get(items.indexOf(regresar));
			else
				regresar= items.get(0);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    return regresar;
	} // getDataCombo	
}