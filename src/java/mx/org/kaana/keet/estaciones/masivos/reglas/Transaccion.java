package mx.org.kaana.keet.estaciones.masivos.reglas;

import mx.org.kaana.mantic.catalogos.masivos.reglas.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesUbicacionesDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosCodigosDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosEspecificacionesDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticEgresosDto;
import mx.org.kaana.mantic.db.dto.TcManticInventariosDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticMovimientosDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.db.dto.TcManticTrabajosDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorTipoContactoDto;
import static org.apache.commons.io.Charsets.ISO_8859_1;
import static org.apache.commons.io.Charsets.UTF_8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
  
  private static final Log LOG = LogFactory.getLog(Transaccion.class);
  private TcManticMasivasArchivosDto masivo;	
	private ECargaMasiva categoria;
	private Long idContatoLote;
	private int errores;
	private int procesados;
	private String messageError;
  
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria) {
		this(masivo, categoria, -1L);
	}
	
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria, Long idContatoLote) {
		this.masivo     = masivo;		
		this.categoria  = categoria;
		this.errores    = 0;
		this.procesados = 0;
		this.idContatoLote= idContatoLote;
	} // Transaccion

	protected void setMessageError(String messageError) {
		this.messageError= messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	public int getErrores() {
		return errores;
	}

	public int getProcesados() {
		return procesados;
	}
  
	@Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception{
    boolean regresar= false;
    try {
      this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" catalogo de forma masiva.");
      switch (accion) {
				case PROCESAR: 
 					regresar= this.toProcess(sesion);
					break;
				case ELIMINAR: 
					// regresar = DaoFactory.getInstance().delete(sesion, this.masivo)> -1L;
					break;
      } // swtich 
      if (!regresar) {
        throw new Exception(messageError);
      } // if
    } // tyr
		catch (Exception e) {
      throw new Exception(messageError.concat("<br/>") + e.getMessage());
    } // catch
    return regresar;
  }
  
	protected boolean toProcess(Session sesion) throws Exception {
		boolean regresar                   = false;  
		TcManticMasivasBitacoraDto bitacora= null;
		File file= new File(this.masivo.getAlias());
		if(file.exists()) {
	 		if(!this.masivo.isValid()) {
			  DaoFactory.getInstance().updateAll(sesion, TcManticMasivasArchivosDto.class, this.masivo.toMap());
		    DaoFactory.getInstance().insert(sesion, this.masivo);
				bitacora= new TcManticMasivasBitacoraDto(
					"", // String justificacion, 
					this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
					JsfBase.getIdUsuario(), // Long idUsuario, 
					-1L, // Long idMasivaBitacora, 
					0L, // Long procesados, 
					1L // Long idMasivaEstatus
				);
				DaoFactory.getInstance().insert(sesion, bitacora);
				this.toDeleteXls();
			} // if
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			monitoreo.comenzar(0L);
			monitoreo.setTotal(this.masivo.getTuplas());
			monitoreo.setId(file.getName().toUpperCase());
			try {
				switch (this.categoria) {
					case ESTACIONES:
						this.toEstaciones(sesion, file);
						break;
				} // swtich
			} // try
			finally {
				monitoreo.terminar();
				monitoreo.setProgreso(0L);
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 3L);
				DaoFactory.getInstance().insert(sesion, bitacora);
			} // catch
			regresar= true;
		} // if	
		else
			LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ this.masivo.getNombre());
    return regresar;
	} // toProcess
	
	private Long toFindEntidad(Session sesion, String entidad) throws Exception {
		Long regresar= -1L;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			String codigo= entidad.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim();
			params.put("descripcion", codigo.replaceAll("(,| |\\t)+", ".*.*"));
			Value value= DaoFactory.getInstance().toField(sesion, "TcJanalEntidadesDto", "entidad", params, "idEntidad");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
			else {
				value= DaoFactory.getInstance().toField(sesion, "TcJanalEntidadesDto", "primero", params, "idEntidad");
  			if(value!= null && value.getData()!= null)
	  			regresar= value.toLong();
			} // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindEntidad

	private Long toFindMunicipio(Session sesion, Long idEntidad, String municipio) throws Exception {
		Long regresar= -1L;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idEntidad", idEntidad);
			params.put("descripcion", municipio!= null? municipio.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*"): "XYZ");
			Value value= DaoFactory.getInstance().toField(sesion, "TcJanalMunicipiosDto", "municipio", params, "idMunicipio");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
			else {
				value= DaoFactory.getInstance().toField(sesion, "TcJanalMunicipiosDto", "primero", params, "idMunicipio");
  			if(value!= null && value.getData()!= null)
	  			regresar= value.toLong();
			} // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindMunicipio
	
	private Long toFindLocalidad(Session sesion, Long idMunicipio, String localidad) throws Exception {
		Long regresar= -1L;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idMunicipio", idMunicipio);
			params.put("descripcion", localidad!= null? localidad.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*"): "XYZ");
			Value value= DaoFactory.getInstance().toField(sesion, "TcJanalLocalidadesDto", "localidad", params, "idLocalidad");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
			else {
				value= DaoFactory.getInstance().toField(sesion, "TcJanalLocalidadesDto", "primero", params, "idLocalidad");
  			if(value!= null && value.getData()!= null)
	  			regresar= value.toLong();
			} // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindLocalidad
	
	private TcManticProveedoresDto toFindProveedor(Session sesion, String rfc) {
		TcManticProveedoresDto regresar= null;
		Map<String, Object> params  = null;
		try {
			params=new HashMap<>();
			params.put("rfc", rfc);
			regresar= (TcManticProveedoresDto)DaoFactory.getInstance().toEntity(sesion, TcManticProveedoresDto.class, "VistaCargasMasivasDto", "proveedor", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindProveedor

	private Long toFindUnidadMedida(Session sesion, String codigo) {
		Long regresar= 1L;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("codigo", codigo);
			Value value= DaoFactory.getInstance().toField(sesion, "VistaCargasMasivasDto", "empaque", params, "idEmpaqueUnidadMedida");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindUnidadMedida
	
  private Boolean toEstaciones(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		TcManticArticulosCodigosDto codigos= null;
		TcManticMasivasBitacoraDto bitacora= null;
		try {
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
							String contenido= new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							// 0       1      2     3         4          5     
							//MANZANA|LOTE|CODIGO|NOMBRE|COSTOS/IVA|UNIDADMEDIDA
							String manzana= new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							String lote   = new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							String codigo = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
							double costo  = Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
						  codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8);
							codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
							if(!Cadena.isVacio(codigo) && codigo.length()> 0 && costo> 0) {
								// this.toFindUnidadMedida(sesion, sheet.getCell(5, fila).getContents());

									DaoFactory.getInstance().insert(sesion, null);
									
								monitoreo.incrementar();
								if(fila% this.categoria.getTuplas()== 0) {
									if(bitacora== null) {
										bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, new Long(fila), 2L);
										DaoFactory.getInstance().insert(sesion, bitacora);
									} // if
									else {
										bitacora.setProcesados(new Long(fila));
										bitacora.setRegistro(LocalDateTime.now());
										DaoFactory.getInstance().update(sesion, bitacora);
									} // else
									this.commit();
									this.procesados= fila;
									LOG.warn("Realizando proceso de commit en la fila "+ this.procesados);
								} // if
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ ": ["+ codigo+ "] costo: ["+ costo+ "] manzana: ["+ manzana+ "] lote: ["+ lote+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL CODIGO ["+ codigo+ "] COSTO["+ costo+ "], MANZANA["+ manzana+ "], LOTE["+ lote+ "] ESTAN EN CEROS" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++;
						} // if	
	//					if(fila> 500)
	//						throw new KajoolBaseException("Este error fue provocado intencionalmente !");
					} // try
					catch(Exception e) {
            LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
					} // catch
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				if(bitacora== null) {
					bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  				DaoFactory.getInstance().insert(sesion, bitacora);
				} // if
			  else {
					bitacora.setProcesados(this.masivo.getTuplas());
					bitacora.setRegistro(LocalDateTime.now());
					DaoFactory.getInstance().update(sesion, bitacora);
				} // if
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toArticulos		

	public void toDeleteXls() throws Exception {
		List<TcManticMasivasArchivosDto> list= (List<TcManticMasivasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticMasivasArchivosDto.class, this.masivo.toMap(), "all");
		if(list!= null)
			for (TcManticMasivasArchivosDto item: list) {
				LOG.info("Catalogo importado: "+ item.getIdMasivaArchivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				file.delete();
			} // for
	}	// toDeleteXls 	

}