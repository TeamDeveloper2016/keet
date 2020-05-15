package mx.org.kaana.keet.estaciones.masivos.reglas;

import java.io.File;
import java.time.LocalDate;
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
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.keet.estaciones.masivos.beans.Estacion;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.KajoolBaseException;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticMasivasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import static org.apache.commons.io.Charsets.ISO_8859_1;
import static org.apache.commons.io.Charsets.UTF_8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends mx.org.kaana.mantic.incidentes.reglas.Transaccion {
  
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
		super(new Incidente());
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
		try {
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
						case PERSONAL:
							this.toPersonal(sesion, file);
							break;
						case PLANTILLAS:
							this.toPlantillas(sesion, file);
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
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
	} // toProcess
	
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
			if(regresar!= null)
				regresar.setCosto(0D);
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
		Estacion parcial        = null;
		Estacion estacion       = null; 
		TcManticMasivasBitacoraDto bitacora= null;
		try {
			Semanas semanas= new Semanas();
			int semana= semanas.getSemana(sesion);
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
								// 0       1      2     3        4         5         6				  7			  8
								//MANZANA|LOTE|CODIGO|NOMBRE|CANTIDAD|COSTOS/IVA|UNIDADMEDIDA|INICIO|TERMINO
								String manzana = sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String lote    = sheet.getCell(1, fila).getContents()!= null? new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String codigo  = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String nombre  = new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String inicio  = sheet.getCell(7, fila).getContents()!= null? new String(sheet.getCell(7, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
								String termino = sheet.getCell(8, fila).getContents()!= null? new String(sheet.getCell(8, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
								double cantidad= Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								double costo   = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8);
								codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									if(!Objects.equals(manzana, "*") && !Objects.equals(contrato.toString("manzana"), manzana))
										throw new RuntimeException("El archivo contiene un numero de manzana incorrecto");
									if(!Objects.equals(lote, "*") && !Objects.equals(contrato.toString("lote"), lote))
										throw new RuntimeException("El archivo contiene un numero de lote incorrecto");
									Entity item= this.toRubro(sesion, codigo);
									if(item!= null && !item.isEmpty()) {
										if(count== 0 && item.toLong("nivel")!= 5L)
											throw new RuntimeException("El archivo no comienza con una estación correcta");
										// EL NIVEL 5 ES UNA ESTACION POR LO TANTO EL COSTO SE INICIA EN CERO
										if(item.toLong("nivel")== 5L) {
											if(!Objects.equals(codigoPartida, codigo)) {
												codigoPartida= codigo;
												codigoRubro  = "";
												rubro        = 0;
												costo        = 0D;
												partida++;
												// ESTO ACTUALIZA EL COSTO TOTAL DE LOS CONCEPTOS
												if(parcial!= null) {
													Methods.setValueSubClass(parcial, "abono"+ semana, new Object[] {parcial.getCosto()});
													DaoFactory.getInstance().update(sesion, parcial);
												} // if
											} // if
											else 
												throw new RuntimeException("El archivo tiene una estacion duplicada ["+ codigo+ "] {"+ nombre+ "}");
										} // if
										else {
											if(!Objects.equals(codigoRubro, codigo)) {
												codigoRubro= codigo;
												rubro++;
											} // if
											else 
												throw new RuntimeException("El archivo tiene un concepto duplicado ["+ codigo+ "] {"+ nombre+ "}");
										} // else
									} // else
									else 
										throw new RuntimeException("El archivo tiene un codigo que no existe ["+ codigo+ "] {"+ nombre+ "}");
									// SE SUMA EL COSTO POR CADA CONCEPTO EXCEPTO LA QUE SON ESTACIONES PORQUE SE LIMPIO SU VALOR
									concepto.setCosto(concepto.getCosto()+ costo);
									estaciones.setKeyLevel(String.valueOf(partida), 4); // consecutivo de la estacion
									estaciones.setKeyLevel(String.valueOf(rubro), 5); // consecutivo del concepto
									if(Objects.equals(this.idLimpiar, 1L))
  									estacion= concepto.clone();
									else {
										estacion= this.toConcepto(sesion, estaciones.toKey(4), codigo);
										if(estacion== null)
											estacion= concepto.clone();
									} // if
									// SI EL RUBRO ES CERO SIGNIFICA QUE INICIO LA NUEVA ESTACION POR LO TANTO SE COMIENZA CON LA SUMA EN CEROS
									if(rubro== 0L) {
										parcial= estacion;
										parcial.setCosto(0D);
									}
									else
										if(parcial!= null)
										  parcial.setCosto(parcial.getCosto()+ costo);
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
										Methods.setValueSubClass(estacion, "abono"+ semana, new Object[] {costo});
										if(Cadena.isVacio(inicio))
										  estacion.setInicio(contrato.toDate("inicio"));
										else
										  estacion.setInicio(Fecha.toLocalDate(inicio));
										if(Cadena.isVacio(termino))
  										estacion.setTermino(contrato.toDate("termino"));
										else
  										estacion.setTermino(Fecha.toLocalDate(termino));
										estacion.setIdUsuario(JsfBase.getIdUsuario());
									  DaoFactory.getInstance().insert(sesion, estacion);
									} // if
									else {
										estacion.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
										estacion.setCantidad(cantidad);
										estacion.setCosto(costo);
										Methods.setValueSubClass(estacion, "abono"+ semana, new Object[] {costo});
										estacion.setInicio(contrato.toDate("inicio"));
										estacion.setTermino(contrato.toDate("termino"));
										estacion.setIdUsuario(JsfBase.getIdUsuario());
									  DaoFactory.getInstance().update(sesion, estacion);
									} // if
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
  				if(parcial!= null) {
						Methods.setValueSubClass(parcial, "abono"+ semana, new Object[] {parcial.getCosto()});
						DaoFactory.getInstance().update(sesion, parcial);
					} // if
  				if(concepto!= null) {
						Methods.setValueSubClass(concepto, "abono"+ semana, new Object[] {concepto.getCosto()});
  					DaoFactory.getInstance().update(sesion, concepto);
	  				if(Cadena.isVacio(contrato.toLong("idEstacion"))) {
    					TcKeetContratosLotesDto lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, contrato.toLong("idContratoLote"));
							lote.setIdEstacion(concepto.getIdEstacion());
							DaoFactory.getInstance().update(sesion, lote);
						} // if
					} // if
				} // if
				else 
					throw new RuntimeException("El lote no existe en el contrato, por favor verifique");
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				// this.toUpdateEstaciones(sesion, estaciones.toKey(4));
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
	} // toEstaciones

	private void toLoadIncidente(Long idActivo, Long idEmpresaPersona) throws Exception {
		this.incidente.setTipoIncidente(idActivo== 1L? ETiposIncidentes.ALTA.name(): ETiposIncidentes.BAJA.name());
		this.incidente.setIdTipoIncidente(idActivo== 1L? ETiposIncidentes.ALTA.getKey(): ETiposIncidentes.BAJA.getKey());
		this.incidente.setVigenciaInicio(LocalDate.now());
		this.incidente.setVigenciaFin(LocalDate.now());
		this.incidente.setIdIncidenteEstatus(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
		this.incidente.setIdEmpresaPersona(idEmpresaPersona);
		this.incidente.setObservaciones("INCIDENCIA REGISTRADA POR EL PROCESO DE CARGA MASIVA");
		this.incidente.setCosto(0D);
	}
	
  private Boolean toPersonal(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		TcManticMasivasBitacoraDto bitacora= null;
		Map<String, Object> params         = null;
		try {
			params=new HashMap<>();
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
				if(contrato!= null) {
					for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
						try {
							if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
								// 0       1      2     3           4         5         6           7
								//CLAVE|NOMBRE|ACTIVO|SUELDO|REINGRESO/BAJA|PUESTO|DEPARTAMENTO|CONTRATISTA
								String clave  = sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
								String activo = sheet.getCell(2, fila).getContents()!= null? new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
								double sueldo = Numero.getDouble(sheet.getCell(3, fila).getContents()!= null? sheet.getCell(3, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								String fecha  = sheet.getCell(4, fila).getContents()!= null? new String(sheet.getCell(4, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): Fecha.getHoy();
								String puesto = sheet.getCell(5, fila).getContents()!= null? new String(sheet.getCell(5, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String departamento= sheet.getCell(6, fila).getContents()!= null? new String(sheet.getCell(6, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String contratista = sheet.getCell(7, fila).getContents()!= null? new String(sheet.getCell(7, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								if(!Cadena.isVacio(clave) && !Cadena.isVacio(activo)) {
									params.put("clave", clave);
									TrManticEmpresaPersonalDto persona= (TrManticEmpresaPersonalDto)DaoFactory.getInstance().toEntity(sesion, TrManticEmpresaPersonalDto.class, "TrManticEmpresaPersonalDto", "igual", params);
									if(persona!= null) {
										// FALTA EL CODIGO NECESARIO PARA REGISTRAR EL INCIDENTE
										if(Objects.equals(activo.toUpperCase(), "SI")) {
											if(persona.getIdActivo()== 2L) {
												persona.setIdActivo(1L);
												persona.setIngreso(Fecha.toLocalDate(fecha));
												this.toLoadIncidente(persona.getIdActivo(), persona.getIdEmpresaPersona());
												super.ejecutar(sesion, EAccion.AGREGAR);	
											} // if
										} // if
										else 
											if(Objects.equals(activo.toUpperCase(), "NO")) {
												if(persona.getIdActivo()== 1L) {
													persona.setIdActivo(2L);
													persona.setBaja(Fecha.toLocalDate(fecha));
  												this.toLoadIncidente(persona.getIdActivo(), persona.getIdEmpresaPersona());
	  											super.ejecutar(sesion, EAccion.AGREGAR);	
		  									} // if
											} // if
										if(sueldo> 0) {
										  persona.setSueldoSemanal(sueldo);
										  persona.setSueldoMensual(Numero.toRedondearSat(sueldo* 4));
										} // if
										if(!Cadena.isVacio(puesto) && !Objects.equals(puesto, "*")) {
											Long idPuesto= this.toLookForPuesto(sesion, puesto);
											if(idPuesto!= null)
										    persona.setIdPuesto(idPuesto);
										} // if
										if(!Cadena.isVacio(departamento) && !Objects.equals(departamento, "*")) {
											Long idDepartamento= this.toLookForDepartamento(sesion, departamento);
											if(idDepartamento!= null)
	  									  persona.setIdDepartamento(idDepartamento);
										} // if
										if(Cadena.isVacio(contratista))
    									persona.setIdContratista(null);
										else
											if(!Objects.equals(contratista, "*")) {
												Long idContratista= this.toLookForContratista(sesion, contratista);
												if(idContratista!= null)
  										    persona.setIdContratista(idContratista);
											} // if
										DaoFactory.getInstance().update(sesion, persona);
									} // if
									monitoreo.incrementar();
								} // if
								else {
									this.errores++;
									LOG.warn(fila+ ": ["+ clave+ "] activo: ["+ activo+ "] sueldo: ["+ sueldo+ "] fecha: ["+ fecha+ "] puesto: ["+ puesto+ "]");
									TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
										sheet.getCell(0, fila).getContents(), // String codigo, 
										-1L, // Long idMasivaDetalle, 
										this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
										"EL CLAVE ["+ clave+ "] ACTIVO["+ activo+ "] SUELDO["+ sueldo+ "], FECHA["+ fecha+ "], PUESTO["+ puesto+ "] ESTAN EN CEROS O VACIO" // String observaciones
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
					throw new RuntimeException("La clave del empleado no existe en el catalogo, por favor verifique");
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    finally {
			Methods.clean(params);
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toPersonal		

	public void toDeleteXls() throws Exception {
		List<TcManticMasivasArchivosDto> list= (List<TcManticMasivasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticMasivasArchivosDto.class, this.masivo.toMap(), "all");
		if(list!= null)
			for (TcManticMasivasArchivosDto item: list) {
				LOG.info("Catalogo importado: "+ item.getIdMasivaArchivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				file.delete();
			} // for
	}	// toDeleteXls 	

	private Long toLookForPuesto(Session sesion, String nombre) throws Exception {
		Long regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("nombre", nombre);
  		Value value= (Value)DaoFactory.getInstance().toField(sesion, "TcManticPuestosDto", "igual", params, "idPuesto");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}

	private Long toLookForDepartamento(Session sesion, String nombre) throws Exception {
		Long regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("nombre", nombre);
  		Value value= (Value)DaoFactory.getInstance().toField(sesion, "TcKeetDepartamentosDto", "igual", params, "idDepartamento");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}

	private Long toLookForContratista(Session sesion, String contratista) throws Exception {
		Long regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("clave", contratista);
  		Value value= (Value)DaoFactory.getInstance().toField(sesion, "TrManticEmpresaPersonalDto", "igual", params, "idEmpresaPersona");
			if(value!= null && value.getData()!= null)
				regresar= value.toLong();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}

	private void toUpdateEstaciones(Session sesion, String clave) throws Exception {
		Map<String, Object> params=null;
		try {
			sesion.flush();
			params=new HashMap<>();
			Semanas semanas= new Semanas();
			params.put("semana", semanas.getSemana(sesion));
			// RECORRER TODAS LAS EMPRESAS PORQUE LA CLAVE DE LA ESTACION TIENE EL ID DE LA EMPRESA
			params.put("clave", clave);
			DaoFactory.getInstance().updateAll(sesion, TcKeetEstacionesDto.class, params, "abonos");
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}

  private Boolean toPlantillas(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		Estacion concepto       = null;
		Estacion parcial        = null;
		Estacion estacion       = null; 
		TcManticMasivasBitacoraDto bitacora= null;
		try {
			Semanas semanas= new Semanas();
			int semana= semanas.getSemana(sesion);
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
								// 0       1      2     3        4         5         6				  7			  8
								//MANZANA|LOTE|CODIGO|NOMBRE|CANTIDAD|COSTOS/IVA|UNIDADMEDIDA|INICIO|TERMINO
								String manzana = sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String lote    = sheet.getCell(1, fila).getContents()!= null? new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String codigo  = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String nombre  = new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String inicio  = sheet.getCell(7, fila).getContents()!= null? new String(sheet.getCell(7, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
								String termino = sheet.getCell(8, fila).getContents()!= null? new String(sheet.getCell(8, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
								double cantidad= Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								double costo   = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8);
								codigo= codigo.replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									if(!Objects.equals(manzana, "*") && !Objects.equals(contrato.toString("manzana"), manzana))
										throw new RuntimeException("El archivo contiene un numero de manzana incorrecto");
									if(!Objects.equals(lote, "*") && !Objects.equals(contrato.toString("lote"), lote))
										throw new RuntimeException("El archivo contiene un numero de lote incorrecto");
									Entity item= this.toRubro(sesion, codigo);
									if(item!= null && !item.isEmpty()) {
										if(count== 0 && item.toLong("nivel")!= 5L)
											throw new RuntimeException("El archivo no comienza con una estación correcta");
										// EL NIVEL 5 ES UNA ESTACION POR LO TANTO EL COSTO SE INICIA EN CERO
										if(item.toLong("nivel")== 5L) {
											if(!Objects.equals(codigoPartida, codigo)) {
												codigoPartida= codigo;
												codigoRubro  = "";
												rubro        = 0;
												costo        = 0D;
												partida++;
												// ESTO ACTUALIZA EL COSTO TOTAL DE LOS CONCEPTOS
												if(parcial!= null) {
													Methods.setValueSubClass(parcial, "abono"+ semana, new Object[] {parcial.getCosto()});
													DaoFactory.getInstance().update(sesion, parcial);
												} // if
											} // if
											else 
												throw new RuntimeException("El archivo tiene una estacion duplicada ["+ codigo+ "] {"+ nombre+ "}");
										} // if
										else {
											if(!Objects.equals(codigoRubro, codigo)) {
												codigoRubro= codigo;
												rubro++;
											} // if
											else 
												throw new RuntimeException("El archivo tiene un concepto duplicado ["+ codigo+ "] {"+ nombre+ "}");
										} // else
									} // else
									else 
										throw new RuntimeException("El archivo tiene un codigo que no existe ["+ codigo+ "] {"+ nombre+ "}");
									// SE SUMA EL COSTO POR CADA CONCEPTO EXCEPTO LA QUE SON ESTACIONES PORQUE SE LIMPIO SU VALOR
									concepto.setCosto(concepto.getCosto()+ costo);
									estaciones.setKeyLevel(String.valueOf(partida), 4); // consecutivo de la estacion
									estaciones.setKeyLevel(String.valueOf(rubro), 5); // consecutivo del concepto
									if(Objects.equals(this.idLimpiar, 1L))
  									estacion= concepto.clone();
									else {
										estacion= this.toConcepto(sesion, estaciones.toKey(4), codigo);
										if(estacion== null)
											estacion= concepto.clone();
									} // if
									// SI EL RUBRO ES CERO SIGNIFICA QUE INICIO LA NUEVA ESTACION POR LO TANTO SE COMIENZA CON LA SUMA EN CEROS
									if(rubro== 0L) {
										parcial= estacion;
										parcial.setCosto(0D);
									}
									else
										if(parcial!= null)
										  parcial.setCosto(parcial.getCosto()+ costo);
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
										Methods.setValueSubClass(estacion, "abono"+ semana, new Object[] {costo});
										if(Cadena.isVacio(inicio))
										  estacion.setInicio(contrato.toDate("inicio"));
										else
										  estacion.setInicio(Fecha.toLocalDate(inicio));
										if(Cadena.isVacio(termino))
  										estacion.setTermino(contrato.toDate("termino"));
										else
  										estacion.setTermino(Fecha.toLocalDate(termino));
										estacion.setIdUsuario(JsfBase.getIdUsuario());
									  DaoFactory.getInstance().insert(sesion, estacion);
									} // if
									else {
										estacion.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
										estacion.setCantidad(cantidad);
										estacion.setCosto(costo);
										Methods.setValueSubClass(estacion, "abono"+ semana, new Object[] {costo});
										estacion.setInicio(contrato.toDate("inicio"));
										estacion.setTermino(contrato.toDate("termino"));
										estacion.setIdUsuario(JsfBase.getIdUsuario());
									  DaoFactory.getInstance().update(sesion, estacion);
									} // if
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
  				if(parcial!= null) {
						Methods.setValueSubClass(parcial, "abono"+ semana, new Object[] {parcial.getCosto()});
						DaoFactory.getInstance().update(sesion, parcial);
					} // if
  				if(concepto!= null) {
						Methods.setValueSubClass(concepto, "abono"+ semana, new Object[] {concepto.getCosto()});
  					DaoFactory.getInstance().update(sesion, concepto);
	  				if(Cadena.isVacio(contrato.toLong("idEstacion"))) {
    					TcKeetContratosLotesDto lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, contrato.toLong("idContratoLote"));
							lote.setIdEstacion(concepto.getIdEstacion());
							DaoFactory.getInstance().update(sesion, lote);
						} // if
					} // if
				} // if
				else 
					throw new RuntimeException("El lote no existe en el contrato, por favor verifique");
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				// this.toUpdateEstaciones(sesion, estaciones.toKey(4));
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
	} // toPlantillas

}