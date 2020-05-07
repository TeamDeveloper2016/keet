package mx.org.kaana.keet.estaciones.masivos.reglas;

import java.io.File;
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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.masivos.beans.Estacion;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
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
	private Long idLimpiar;
	private int errores;
	private int procesados;
	private String messageError;
  
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria) {
		this(masivo, categoria, -1L, 1L);
	}
	
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria, Long idContatoLote, Long idLimpiar) {
		this.masivo     = masivo;		
		this.categoria  = categoria;
		this.errores      = 0;
		this.procesados   = 0;
		this.idContatoLote= idContatoLote;
		this.idLimpiar    = idLimpiar;
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
			Value value= DaoFactory.getInstance().toField(sesion, "VistaCargasMasivasDto", "unidad", params, "idEmpaqueUnidadMedida");
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
	
	private TcKeetContratosDto toContrato(Session sesion, Long idContrato) throws Exception {
		TcKeetContratosDto regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idContrato", idContrato);
			regresar= (TcKeetContratosDto)DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDto.class, "TcKeetContratosDto", "byId", params);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private TcKeetContratosLotesDto toContratoLote(Session sesion) throws Exception {
		TcKeetContratosLotesDto regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idContatoLote", this.idContatoLote);
			regresar= (TcKeetContratosLotesDto)DaoFactory.getInstance().toEntity(sesion, TcKeetContratosLotesDto.class, "TcKeetContratosLotesDto", "igual", params);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Entity toContratoDatos(Session sesion) throws Exception {
		Entity regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idContratoLote", this.idContatoLote);
			regresar= (Entity)DaoFactory.getInstance().toEntity(sesion, "VistaContratosLotesDto", "lote", params);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private List<TcKeetEstacionesDto> toEstaciones(Session sesion, String clave) throws Exception {
		List<TcKeetEstacionesDto> regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			regresar= (List<TcKeetEstacionesDto>)DaoFactory.getInstance().toEntity(sesion, TcKeetEstacionesDto.class, "VistaContratosLotesDto", "estaciones", params);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
		
	private Estacion toDeleteEstaciones(Session sesion, String clave) throws Exception {
		Estacion regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			if(Objects.equals(this.idLimpiar, 1L))
	  		DaoFactory.getInstance().deleteAll(sesion, TcKeetEstacionesDto.class, params);
  		regresar= (Estacion)DaoFactory.getInstance().toEntity(sesion, Estacion.class, "VistaContratosLotesDto", "estaciones", params);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
		
	private Entity toRubro(Session sesion, String codigo) throws Exception {
		Entity regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("codigo", codigo);
			regresar= (Entity)DaoFactory.getInstance().toEntity(sesion, "VistaContratosLotesDto", "buscar", params);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Estacion toConcepto(Session sesion, String clave, String codigo) throws Exception {
		Estacion regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			params.put("codigo", codigo);
			regresar= (Estacion)DaoFactory.getInstance().toEntity(sesion, Estacion.class, "VistaContratosLotesDto", "estacion", params);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
  private Boolean toEstaciones(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		Estacion concepto       = null;
		Estacion estacion       = null; 
		TcManticMasivasBitacoraDto bitacora= null;
		try {
			Estaciones estaciones= new Estaciones();
      WorkbookSettings workbookSettings = new WorkbookSettings();
      workbookSettings.setEncoding("Cp1252");	
			workbookSettings.setExcelDisplayLanguage("MX");
      workbookSettings.setExcelRegionalSettings("MX");
      workbookSettings.setLocale(new Locale("es", "MX"));
			workbook= Workbook.getWorkbook(archivo, workbookSettings);
			sheet		= workbook.getSheet(0);
			Monitoreo monitoreo= JsfBase.getAutentifica().getMonitoreo();
			if(sheet != null && sheet.getColumns()>= this.categoria.getColumns() && sheet.getRows()>= 2) {
				Entity contrato= this.toContratoDatos(sesion);
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				int partida = 0; 
				int rubro   = 0; 
				String codigoPartida= "";
				String codigoRubro  = "";
				if(contrato!= null) {
					estaciones.setKeyLevel(contrato.toString("idEmpresa"), 0); // idEmpresa
					estaciones.setKeyLevel(contrato.toString("ejercicio"), 1); // ejercicio
					estaciones.setKeyLevel(contrato.toString("contrato"), 2); // orden del contrato
					estaciones.setKeyLevel(contrato.toString("orden"), 3); // orden de contrato lote
					concepto= this.toDeleteEstaciones(sesion, estaciones.toKey(4));
					for(int fila= 1; concepto!= null && fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
						try {
							if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
								// 0       1      2     3        4         5         6
								//MANZANA|LOTE|CODIGO|NOMBRE|CANTIDAD|COSTOS/IVA|UNIDADMEDIDA
								String manzana = new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String lote    = new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String codigo  = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String nombre  = new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								double cantidad= Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								double costo   = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8);
								codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0 && cantidad> 0 && costo> 0) {
									if(!Objects.equals(contrato.toString("manzana"), manzana))
										throw new RuntimeException("El archivo contiene un numero de manzana incorrecto");
									if(!Objects.equals(contrato.toString("lote"), lote))
										throw new RuntimeException("El archivo contiene un numero de lote incorrecto");
									Entity item= this.toRubro(sesion, codigo);
									if(item!= null && !item.isEmpty()) {
										if(count== 0 && item.toLong("nivel")!= 5L)
											throw new RuntimeException("El archivo no comienza con una estaci�n correcta");
										if(item.toLong("nivel")== 5L) {
											if(codigoPartida!= codigo) {
												codigoPartida= codigo;
												codigoRubro  = "";
												rubro        = 0;
												partida++;
											} // if
											else 
												throw new RuntimeException("El archivo tiene una estacion duplicada ["+ codigo+ "] {"+ nombre+ "}");
										} // if
										else {
											if(codigoRubro!= codigo) {
												codigoRubro= codigo;
												rubro++;
											} // if
											else 
												throw new RuntimeException("El archivo tiene un concepto duplicado ["+ codigo+ "] {"+ nombre+ "}");
										} // else
									} // else
									else 
										throw new RuntimeException("El archivo tiene un codigo que no existe ["+ codigo+ "] {"+ nombre+ "}");
									estaciones.setKeyLevel(String.valueOf(partida), 4); // consecutivo de la estacion
									estaciones.setKeyLevel(String.valueOf(rubro), 5); // consecutivo del concepto
									if(Objects.equals(this.idLimpiar, 1L))
  									estacion= concepto.clone();
									else {
										estacion= this.toConcepto(sesion, estaciones.toKey(4), codigo);
										if(estacion== null)
											estacion= concepto.clone();
									} // if
									if(Objects.equals(this.idLimpiar, 1L) || !estacion.isValid()) {
										estacion.setNivel(item.toLong("nivel"));
										estacion.setUltimo(item.toLong("ultimo"));
										estacion.setClave(estaciones.toCode());
										estacion.setCodigo(codigo);
										estacion.setNombre(nombre);
										estacion.setDescripcion(nombre);
										estacion.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
										estacion.setCantidad(cantidad);
										estacion.setCosto(costo);
										estacion.setInicio(contrato.toDate("inicio"));
										estacion.setTermino(contrato.toDate("termino"));
										estacion.setIdUsuario(JsfBase.getIdUsuario());
									  DaoFactory.getInstance().insert(sesion, estacion);
									}
									else {
										estacion.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
										estacion.setCantidad(cantidad);
										estacion.setCosto(costo);
										estacion.setInicio(contrato.toDate("inicio"));
										estacion.setTermino(contrato.toDate("termino"));
										estacion.setIdUsuario(JsfBase.getIdUsuario());
									  DaoFactory.getInstance().update(sesion, estacion);
									}
									LOG.warn(count+ ".-  <"+ estacion.getNivel()+ "> ["+ estacion.getClave()+ "] ("+ estacion.getCodigo()+ ") {"+ estacion.getCosto()+ "} "+ estacion.getDescripcion());
									monitoreo.incrementar();
								} // if
								else {
									this.errores++;
									LOG.warn(fila+ ": ["+ codigo+ "] cantidad: ["+ cantidad+ "] costo: ["+ costo+ "] manzana: ["+ manzana+ "] lote: ["+ lote+ "]");
									TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
										sheet.getCell(0, fila).getContents(), // String codigo, 
										-1L, // Long idMasivaDetalle, 
										this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
										"EL CODIGO ["+ codigo+ "] CANTIDAD["+ cantidad+ "] COSTO["+ costo+ "], MANZANA["+ manzana+ "], LOTE["+ lote+ "] ESTAN EN CEROS" // String observaciones
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
							throw e;
						} // catch
						this.procesados= count;
						LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
					} // for
				} // if
				else 
					throw new RuntimeException("El lote no exite en el contrato, por favor verifique");
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
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