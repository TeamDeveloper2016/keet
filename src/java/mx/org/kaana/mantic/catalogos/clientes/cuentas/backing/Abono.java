package mx.org.kaana.mantic.catalogos.clientes.cuentas.backing;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.catalogos.clientes.cuentas.reglas.Transaccion;
import mx.org.kaana.mantic.catalogos.comun.IBasePagos;
import mx.org.kaana.mantic.db.dto.TcManticArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesPagosArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesPagosDto;
import mx.org.kaana.mantic.enums.EEstatusClientes;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import org.primefaces.event.FileUploadEvent;

@Named(value = "manticCatalogosClientesCuentasAbono")
@ViewScoped
public class Abono extends IBasePagos implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;	
	
  @PostConstruct
  @Override
  protected void init() {
    try {			
      this.attrs.put("idCliente", JsfBase.getFlashAttribute("idCliente"));     
      this.attrs.put("idClienteDeuda", JsfBase.getFlashAttribute("idClienteDeuda"));    
			this.attrs.put("cliente", DaoFactory.getInstance().findById(TcManticClientesDto.class, Long.valueOf(this.attrs.get("idCliente").toString())));			
			this.attrs.put("saldar", "2");						
			this.attrs.put("fechaPago", LocalDate.now());
			this.initValues();
			this.loadClienteDeuda();
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init	
	
	private void loadClienteDeuda() throws Exception {
		Entity deuda             = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idCliente", this.attrs.get("idCliente"));			
			params.put("sortOrder", "order by	tc_mantic_clientes_deudas.registro desc");
			params.put(Constantes.SQL_CONDICION, " tc_mantic_clientes_deudas.id_cliente_deuda=" + this.attrs.get("idClienteDeuda"));
			deuda= (Entity) DaoFactory.getInstance().toEntity("VistaClientesDto", "cuentas", params);
			this.attrs.put("permitirPago", deuda.toLong("idClienteDeudaEstatus").equals(EEstatusClientes.FINALIZADA.getIdEstatus()));
			this.attrs.put("deuda", deuda);
			this.attrs.put("pago", deuda.toDouble("saldo"));			
      if(!deuda.toLong("idClienteDeudaEstatus").equals(EEstatusClientes.FINALIZADA.getIdEstatus())) 
        UIBackingUtilities.execute("janal.bloquear(); PF('dlgPago').show();");
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally{
			Methods.clean(params);
		} // finally
	} // loadClienteDeuda
	
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
	  Map<String, Object> params= null;	
    try {  	  
			params= new HashMap<>();
			params.put("idClienteDeuda", this.attrs.get("idClienteDeuda"));			
      columns= new ArrayList<>();  
			columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			columns.add(new Columna("pago", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("persona", EFormatoDinamicos.MAYUSCULAS));
			this.lazyModel = new FormatCustomLazy("VistaClientesDto", "pagosDeuda", params, columns);
      UIBackingUtilities.resetDataTable();		
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad

	public String doRegresar() {	  
		JsfBase.setFlashAttribute("idCliente", this.attrs.get("idCliente"));
		JsfBase.setFlashAttribute("idClienteDeuda", this.attrs.get("idClienteDeuda"));
		return "saldos".concat(Constantes.REDIRECIONAR);
	} // doRegresar
	
	public void doRegistrarPago() {
		Transaccion transaccion      = null;
		TcManticClientesPagosDto pago= null;
		boolean tipoPago             = false;
		boolean saldar               = false;
		try {
      saldar= ((Long)this.attrs.get("saldar")).equals(1L);
      pago= new TcManticClientesPagosDto();
      pago.setIdClienteDeuda((Long)this.attrs.get("idClienteDeuda"));
      pago.setIdUsuario(JsfBase.getIdUsuario());
      pago.setObservaciones((String)this.attrs.get("observaciones"));
      pago.setPago((Double)this.attrs.get("pago"));
			pago.setFechaPago((LocalDate)this.attrs.get("fechaPago"));
      pago.setIdTipoMedioPago(((UISelectEntity)this.attrs.get("tipoPago")).getKey());
      tipoPago= pago.getIdTipoMedioPago().equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago());
      transaccion= new Transaccion(pago, (Long)this.attrs.get("idCliente"), Long.valueOf(this.attrs.get("idEmpresa").toString()), tipoPago? -1L: ((UISelectEntity)this.attrs.get("banco")).getKey(), tipoPago ? "" : (String)this.attrs.get("referencia"), saldar);
      if(transaccion.ejecutar(EAccion.AGREGAR)){
        JsfBase.addMessage("Registrar pago", "Se registro el pago de forma correcta", ETipoMensaje.INFORMACION);
        this.loadClienteDeuda();
      } // if
      else
        JsfBase.addMessage("Registrar pago", "Ocurri� un error al registrar el pago", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doRegistrarPago
	
	@Override
	public void doLoadImportados() {
		List<Columna> columns                 = null;
		TcManticClientesDeudasDto clienteDeuda= null;
		try {
			columns= new ArrayList<>();
      columns.add(new Columna("ruta", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			clienteDeuda= (TcManticClientesDeudasDto)DaoFactory.getInstance().findById(TcManticClientesDeudasDto.class, (Long) this.attrs.get("idClienteDeuda"));
		  this.attrs.put("importados", UIEntity.build("VistaClientesDto", "importados", clienteDeuda.toMap(), columns));
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    }// finally
  } // doLoadImportados	
	
	@Override
	public void doLoadFiles() {
		TcManticClientesPagosArchivosDto tmp= null;
		if((Long) this.attrs.get("idClienteDeuda") > 0L) {
			Map<String, Object> params=null;
			try {
				params=new HashMap<>();
				params.put("idClienteDeuda", this.attrs.get("idClienteDeuda"));				
				params.put("idTipoArchivo", 2L);
				tmp= (TcManticClientesPagosArchivosDto) DaoFactory.getInstance().toEntity(TcManticClientesPagosArchivosDto.class, "VistaClientesDto", "existsPagos", params); 
				if(tmp!= null) {
					setFile(new Importado(tmp.getNombre(), "PDF", EFormatos.PDF, 0L, tmp.getTamanio(), "", tmp.getRuta(), tmp.getObservaciones(), -1L));
  				this.attrs.put("file", getFile().getName()); 
				} // if	
			} // try
			catch (Exception e) {
				Error.mensaje(e);
				JsfBase.addMessageError(e);
			} // catch
			finally {
				Methods.clean(params);
			} // finally
		} // if
	} // doLoadFiles	
	
	@Override
	public void doLoadPagosArchivos(){
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params = new HashMap<>();
			columns= new ArrayList<>();      
      columns.add(new Columna("persona", EFormatoDinamicos.MAYUSCULAS));
			params.put("idClienteDeuda", this.attrs.get("idClienteDeuda"));
			this.attrs.put("pagos", UIEntity.build("VistaClientesDto", "pagosDeuda", params, columns));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadPagosArchivos	
	
	public String doImportar() {
		String regresar        = null;
		Transaccion transaccion= null;
		TcManticClientesDeudasDto clienteDeuda= null;
		try {
			clienteDeuda= (TcManticClientesDeudasDto)DaoFactory.getInstance().findById(TcManticClientesDeudasDto.class, (Long) this.attrs.get("idClienteDeuda"));
			transaccion= new Transaccion(getFile(), clienteDeuda, ((Entity)this.attrs.get("pagoCombo")).getKey());
      if(transaccion.ejecutar(EAccion.SUBIR)) {
      	UIBackingUtilities.execute("janal.alert('Se importaron los archivos de forma correcta !');");				
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
		finally{
			setFile(new Importado());
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_IDENTIFICACION);
			this.attrs.put("xml", ""); 
			this.attrs.put("file", ""); 
		} // finally
    return regresar;
	} // doAceptar
	
	public void doFileUpload(FileUploadEvent event) {
		StringBuilder path= new StringBuilder();  
		StringBuilder temp= new StringBuilder();  
		String nameFile   = Archivo.toFormatNameFile(event.getFile().getFileName().toUpperCase());
    File result       = null;		
		Long fileSize     = 0L;
		try {			
      path.append(Configuracion.getInstance().getPropiedadSistemaServidor("cobros"));
      temp.append(JsfBase.getAutentifica().getEmpresa().getNombreCorto().replaceAll(" ", ""));
      temp.append("/");
      temp.append(((TcManticClientesDto)this.attrs.get("cliente")).getClave());
      temp.append("/");      
			path.append(temp.toString());
			result= new File(path.toString());		
			if (!result.exists())
				result.mkdirs();
      String ruta= path.toString();
      path.append(nameFile);
			result = new File(path.toString());
			if (result.exists())
				result.delete();			      
			Archivo.toWriteFile(result, event.getFile().getInputStream());
			fileSize= event.getFile().getSize();						
      /*UPLOAD*/
			setFile(new Importado(nameFile, event.getFile().getContentType(), EFormatos.PDF, event.getFile().getSize(), fileSize.equals(0L) ? fileSize: fileSize/1024, event.getFile().equals(0L)? " Bytes": " Kb", temp.toString(), (String)this.attrs.get("observaciones"), event.getFile().getFileName().toUpperCase(), this.toSaveFileRecord(event.getFile().getFileName().toUpperCase(), ruta, path.toString(), nameFile, 2L)));
  		this.attrs.put("file", getFile().getName()); 			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessage("Importar:", "El archivo no pudo ser importado !", ETipoMensaje.ERROR);
			if(result!= null)
			  result.delete();
		} // catch
	} // doFileUpload		
  
  private Long toSaveFileRecord(String archivo, String ruta, String alias, String nombre, Long idEliminado) throws Exception {
		Long regresar= -1L;
		TcManticArchivosDto registro= new TcManticArchivosDto(
			archivo, // String archivo, 
			idEliminado, // Long idEliminado 1 es igual a eliminar, 2 es igual a no eliminar
			ruta, // String ruta, 
			JsfBase.getIdUsuario(), // Long idUsuario, 
			alias, // String alias, 
			-1L, // Long idArchivo, 
			nombre // String nombre
		);
		regresar= DaoFactory.getInstance().insert(registro);
		if(regresar <= 0)
			throw new RuntimeException("Ocurri� un error al registrar el archivo.");
		return regresar;
	}
	
}