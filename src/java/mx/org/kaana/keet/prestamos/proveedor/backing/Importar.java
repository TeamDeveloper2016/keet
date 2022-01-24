package mx.org.kaana.keet.prestamos.proveedor.backing;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.List;
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
import mx.org.kaana.keet.prestamos.proveedor.beans.Documento;
import mx.org.kaana.keet.prestamos.proveedor.beans.RegistroAnticipo;
import mx.org.kaana.keet.prestamos.proveedor.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;


@Named(value= "keetPrestamosProveedorImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Importar.class);
	private static final long serialVersionUID= 2672741451185244787L;
  private RegistroAnticipo prestamo;

	public RegistroAnticipo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(RegistroAnticipo prestamo) {
		this.prestamo = prestamo;
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {      
			if(JsfBase.getFlashAttribute("idAnticipo")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.attrs.put("idAnticipo", JsfBase.getFlashAttribute("idAnticipo"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR);
			this.setFile(new Importado());
			this.attrs.put("file", ""); 
			this.loadCombos();
			this.prestamo= new RegistroAnticipo((Long)this.attrs.get("idAnticipo"));
      this.toLoadDisponible();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	public void loadCombos() {
		List<Columna>campos= null;
		try {
			campos= new ArrayList<>();
			campos.add(new Columna("deudor", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));
			this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("deudores", UIEntity.seleccione("VistaMorososDto", "byEmpresa", this.attrs, campos, "deudor"));
			campos.clear();
			campos.add(new Columna("abono", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.attrs.put("pagos", UIEntity.seleccione("TcKeetAnticiposPagosDto", "byIdAnticipo", this.attrs, campos, "consecutivo"));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally{
			Methods.clean(campos);
		} // finally
	} // loadCombos
	
	@Override
	public void doLoad() {
		List<Columna> columns= null;
		try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("abono", EFormatoDinamicos.MILES_CON_DECIMALES));
		  this.attrs.put("importados", UIEntity.build("VistaAnticiposDto", "importados", this.attrs, columns));
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    }// finally
	} // doLoad
	
  private void toLoadDisponible() {  
		Entity entity = null;
    try {			
			this.attrs.put("idMoroso", this.prestamo.getPrestamo().getIkDeudor().getKey());
			entity= (Entity)DaoFactory.getInstance().toEntity("VistaMorososDto", "byIdDeudor", this.attrs);
			this.attrs.put("disponible", Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.getDouble(entity.toString("disponible"))));	
			this.attrs.put("limite", Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.getDouble(entity.toString("limite"))));	
			this.attrs.put("fecha", Fecha.formatear(Fecha.FECHA_CORTA, entity.toDate("ingreso")));		
			this.attrs.put("antiguedad", DAYS.between(entity.toDate("ingreso"), LocalDate.now()));	
			this.attrs.put("dias", Fecha.toFormatSecondsToHour(DAYS.between(entity.toDate("ingreso"), LocalDate.now())* 86400));	
      this.getPrestamo().getPrestamo().setIkDeudor(new UISelectEntity(entity));
      List<UISelectEntity> deudores= new ArrayList<>();
      deudores.add(this.getPrestamo().getPrestamo().getIkDeudor());
      this.attrs.put("deudores", deudores); 
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadDisponible
  
	public void doFileUpload(FileUploadEvent event) {				
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
		String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;			
		Long idArchivo    = 0L;			
		try {			
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("anticipos"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString());
      temp.append("/");			
      temp.append(this.getPrestamo().getPrestamo().getIdMoroso());
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
			idArchivo= this.toSaveFileRecord("anticipos");							
      /*UPLOAD*/
			this.setFile(new Importado(nameFile, event.getFile().getContentType(), getFileType(nameFile), event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), idArchivo));
  		this.attrs.put("file", this.getFile().getName());	
			this.prestamo.getDocumentos().add(this.toPrestamoArchivo(idArchivo));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload	
	
	
	private Documento toPrestamoArchivo(Long idArchivo){
		Documento regresar= null;		
		regresar= new Documento(
			this.getFile().getName(), 
			this.getFile().getRuta(), 
			this.getFile().getFileSize(), 
			JsfBase.getIdUsuario(), 
			this.getFile().getFormat().getIdTipoArchivo()< 0L ? 1L : this.getFile().getFormat().getIdTipoArchivo(), 
			(String)this.attrs.get("observaciones"), 
			-1L, 			
			Configuracion.getInstance().getPropiedadSistemaServidor("anticipos").concat(this.getFile().getRuta()).concat(this.getFile().getName()),
			((UISelectEntity)this.attrs.get("pago")).getKey(), 
			this.getFile().getOriginal(),
			idArchivo
		);
		return regresar;
	} // toPrestamoArchivo
	
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Archivos")) 
			this.doLoad();		
	}	// doTabChange	

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idAnticipoProcess", this.prestamo.getPrestamo().getIdAnticipo());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
	public String doAceptar() {
		String regresar        = null;
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(this.prestamo);
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