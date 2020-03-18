package mx.org.kaana.keet.catalogos.proyectos.comun;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import mx.org.kaana.keet.catalogos.proyectos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetProyectosArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosGeneradoresDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosPresupuestosDto;
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
			this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
			this.attrs.put("planos", UIEntity.seleccione("VistaPrototiposDto", "getPlanosEspecialidades",this.attrs, "descripcion"));
			this.attrs.put("file", "");
			this.attrs.put("tipoArchivo", tipoArchivo);
			setFile(new Importado());
			loadCombos();
			this.registroProyecto= new RegistroProyecto(Long.valueOf(this.attrs.get("idProyecto").toString()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	}	// initBase	

	private void loadCombos(){
		try {			
      this.attrs.put("desarrollos", UIEntity.seleccione("TcKeetDesarrollosDto", "row", this.attrs, "nombre"));
      this.attrs.put("tipoObras", UIEntity.seleccione("VistaTiposObrasDto", "catalogo", this.attrs, "tipoObra"));
      this.attrs.put("fachadas", UIEntity.seleccione("TcKeetTiposFachadasDto", "row", this.attrs, "nombre"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
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
	
	protected String toClave(){
		String regresar                = null;
		List<UISelectEntity>clientes   = null;
		UISelectEntity clienteSeleccion= null;
		try {
			clientes= (List<UISelectEntity>) this.attrs.get("clientes");
			clienteSeleccion= clientes.get(clientes.indexOf(this.registroProyecto.getProyecto().getIkCliente()));
			regresar= clienteSeleccion.toString("clave");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toClave
	
	public void doFileUpload(FileUploadEvent event) {				
		StringBuilder path     = new StringBuilder();  
		StringBuilder temp     = new StringBuilder();  
		String nameFile        = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result            = null;		
		Long fileSize          = 0L;		
		EArchivosProyectos tipo= null;
		try {			
			tipo= (EArchivosProyectos) this.attrs.get("tipoArchivo");
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor(tipo.getPath()));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(toClave());
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
			switch(tipo){
				case DOCUMENTOS:
					this.registroProyecto.getDocumentos().add((TcKeetProyectosArchivosDto) toArchivo());
					break;
				case GENERADORES:
					this.registroProyecto.getGeneradores().add((TcKeetProyectosGeneradoresDto) toArchivo());
					break;
				case PRESUPUESTOS:
					this.registroProyecto.getPresupuestos().add((TcKeetProyectosPresupuestosDto) toArchivo());
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
	
	public abstract IBaseDto toArchivo();
	
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
}
