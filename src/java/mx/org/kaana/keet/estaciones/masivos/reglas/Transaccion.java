package mx.org.kaana.keet.estaciones.masivos.reglas;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import mx.org.kaana.keet.controles.reglas.Controles;
import mx.org.kaana.keet.db.dto.TcKeetArticulosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetControlesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetMaterialesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.keet.db.dto.TrKeetArticuloProveedorClienteDto;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.keet.estaciones.masivos.beans.Control;
import mx.org.kaana.keet.estaciones.masivos.beans.Estacion;
import mx.org.kaana.keet.estaciones.masivos.beans.Material;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
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
	private Long idPrototipo;
	private Long idLimpiar;
	private int errores;
	private int procesados;
	private String messageError;
  
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria) {
		this(masivo, categoria, -1L, 1L, -1L);
	}
	
	public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria, Long idContatoLote, Long idLimpiar) {
		 this(masivo, categoria, idContatoLote, idLimpiar, -1L);
	}
	
	public Transaccion(TcManticMasivasArchivosDto masivo, Long idPrototipo, ECargaMasiva categoria, Long idLimpiar) {
		 this(masivo, categoria, -1L, idLimpiar, idPrototipo);
	}
	
  public Transaccion(TcManticMasivasArchivosDto masivo, ECargaMasiva categoria, Long idContatoLote, Long idLimpiar, Long idPrototipo) {
		super(new Incidente());
		this.masivo       = masivo;		
		this.categoria    = categoria;
		this.errores      = 0;
		this.procesados   = 0;
		this.idContatoLote= idContatoLote;
		this.idLimpiar    = idLimpiar;
		this.idPrototipo  = idPrototipo;
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
							if(this.idLimpiar== 1L)
							  this.toEstaciones(sesion, file);
							else
							  this.toUpdateEstaciones(sesion, file);
							break;
						case PERSONAL:
							this.toPersonal(sesion, file);
							break;
						case PLANTILLAS:
							if(this.idLimpiar== 1L)
  							this.toPlantillas(sesion, file);
							else
	  						this.toUpdatePlantilla(sesion, file);
							break;
						case MATERIALES:
							if(this.idLimpiar== 1L)
							  this.toMateriales(sesion, file);
							else
							  this.toUpdateMateriales(sesion, file);
							break;
						case PRECIOS:
							this.toPreciosProveedor(sesion, file);
						  break;
						case PRECIOS_CONVENIO:
							this.toPreciosClientes(sesion, file);
						  break;
						case CONTROLES:
							if(this.idLimpiar== 1L)
							  this.toControles(sesion, file);
							else
							  this.toUpdateControles(sesion, file);
							break;
					} // swtich
				} // try
        catch(Exception e) {
          throw e;
        } // catch
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
    catch(Exception e) {
      throw e;
    } // catch
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
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private TcKeetPrototiposDto toPrototipo(Session sesion) throws Exception {
		return (TcKeetPrototiposDto)DaoFactory.getInstance().findById(sesion, TcKeetPrototiposDto.class, this.idPrototipo);
	}
	
	private Estacion toEstacion(Session sesion, String clave) throws Exception {
		Estacion regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			regresar= (Estacion)DaoFactory.getInstance().toEntity(sesion, Estacion.class, "VistaContratosLotesDto", "estacion", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Control toControlEstacion(Session sesion, String clave) throws Exception {
		Control regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			regresar= (Control)DaoFactory.getInstance().toEntity(sesion, Estacion.class, "VistaControlesLotesDto", "estacion", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Material toPartida(Session sesion, String clave, Long nivel) throws Exception {
		Material regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			params.put("nivel", nivel);
			regresar= (Material)DaoFactory.getInstance().toEntity(sesion, Material.class, "VistaContratosLotesMaterialesDto", "estacion", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
		
	private Estacion toDeleteEstaciones(Session sesion, Estaciones estaciones, String prototipo) throws Exception {
		Estacion regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", estaciones.toKey(4));
			if(Objects.equals(this.idLimpiar, 1L))
	  		DaoFactory.getInstance().deleteAll(sesion, TcKeetEstacionesDto.class, params);
  		regresar= (Estacion)DaoFactory.getInstance().toEntity(sesion, Estacion.class, "VistaContratosLotesDto", "estaciones", params);
			if(regresar!= null)
				regresar.setCosto(0D);
			else {
				regresar= new Estacion(estaciones.toCode(), "&A", prototipo, 4L, 2L, 87L, JsfBase.getIdUsuario());
    		DaoFactory.getInstance().insert(sesion, regresar);
			} // else
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Material toDeleteMateriales(Session sesion, Estaciones estaciones, String prototipo) throws Exception {
		Material regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", estaciones.toKey(4));
			if(Objects.equals(this.idLimpiar, 1L))
	  		DaoFactory.getInstance().deleteAll(sesion, TcKeetMaterialesDto.class, params);
  		regresar= (Material)DaoFactory.getInstance().toEntity(sesion, Material.class, "VistaContratosLotesMaterialesDto", "estaciones", params);
			if(regresar!= null)
				regresar.setCosto(0D);
			else {
				regresar= new Material(estaciones.toCode(), "&A", prototipo, 4L, 2L, 87L, JsfBase.getIdUsuario());
    		DaoFactory.getInstance().insert(sesion, regresar);
			} // else
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
		
	private Entity toRubro(Session sesion, String codigo) throws Exception {
    return this.toRubro(sesion, "VistaContratosLotesDto", codigo);
	}
	
	private Entity toRubro(Session sesion, String proceso, String codigo) throws Exception {
		Entity regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("codigo", codigo);
			regresar= (Entity)DaoFactory.getInstance().toEntity(sesion, proceso, "buscar", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Estacion toConcepto(Session sesion, String clave, String codigo) throws Exception {
		Estacion regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			params.put("codigo", codigo);
			regresar= (Estacion)DaoFactory.getInstance().toEntity(sesion, Estacion.class, "VistaContratosLotesDto", "concepto", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Control toControlConcepto(Session sesion, String clave, String codigo) throws Exception {
		Control regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			params.put("codigo", codigo);
			regresar= (Control)DaoFactory.getInstance().toEntity(sesion, Estacion.class, "VistaControlesLotesDto", "concepto", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private Material toMaterial(Session sesion, String clave, String codigo) throws Exception {
		Material regresar         = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", clave);
			params.put("codigo", codigo);
			regresar= (Material)DaoFactory.getInstance().toEntity(sesion, Material.class, "VistaContratosLotesMaterialesDto", "material", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
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
					concepto= this.toDeleteEstaciones(sesion, estaciones, contrato.toString("prototipo"));
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
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
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
									estacion= concepto.clone();
									// SI EL RUBRO ES CERO SIGNIFICA QUE INICIO LA NUEVA ESTACION POR LO TANTO SE COMIENZA CON LA SUMA EN CEROS
									if(rubro== 0L) {
										parcial= estacion;
										parcial.setCosto(0D);
									}
									else
										if(parcial!= null)
											parcial.setCosto(parcial.getCosto()+ costo);
									if(item.toLong("nivel")== 5L || (item.toLong("nivel")== 6L && costo> 0 && cantidad> 0)) {
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
    catch(Exception e) {
      throw e;
    } // catch
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toEstaciones

  private Boolean toUpdateEstaciones(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		Estacion raiz           = null;
		Estacion estacion       = null;
		Estacion concepto       = null; 
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
				if(contrato!= null) {
					estaciones.setKeyLevel(contrato.toString("idEmpresa"), 0); // idEmpresa
					estaciones.setKeyLevel(contrato.toString("ejercicio"), 1); // ejercicio
					estaciones.setKeyLevel(contrato.toString("contrato"), 2); // orden del contrato
					estaciones.setKeyLevel(contrato.toString("orden"), 3); // orden de contrato lote
					raiz= this.toDeleteEstaciones(sesion, estaciones, contrato.toString("prototipo"));
					for(int fila= 1; raiz!= null && fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
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
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									if(!Objects.equals(manzana, "*") && !Objects.equals(contrato.toString("manzana"), manzana))
										throw new RuntimeException("El archivo contiene un numero de manzana incorrecto");
									if(!Objects.equals(lote, "*") && !Objects.equals(contrato.toString("lote"), lote))
										throw new RuntimeException("El archivo contiene un numero de lote incorrecto");
									Entity item= this.toRubro(sesion, codigo);
									if(item!= null && !item.isEmpty() && item.toLong("nivel")== 6L) {
										concepto= this.toConcepto(sesion, estaciones.toKey(4), codigo);
										if(concepto!= null && concepto.isValid()) {
											double diferencia= costo- concepto.getCosto();
											concepto.setNombre(nombre);
											concepto.setDescripcion(nombre);
											concepto.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
											concepto.setCantidad(cantidad);
											concepto.setCosto(costo);
											if(Cadena.isVacio(inicio))
												concepto.setInicio(contrato.toDate("inicio"));
											else
												concepto.setInicio(Fecha.toLocalDate(inicio));
											if(Cadena.isVacio(termino))
												concepto.setTermino(contrato.toDate("termino"));
											else
												concepto.setTermino(Fecha.toLocalDate(termino));
											concepto.setIdUsuario(JsfBase.getIdUsuario());
											Methods.setValueSubClass(concepto, "abono"+ semana, new Object[] {costo});
											DaoFactory.getInstance().update(sesion, concepto);
											
											// RECUPERAR LA ESTACION PADRE Y REALIZAR LA SUMA O RESTA SEGUN APLIQUE EL COSTO
											if(estacion== null || !Objects.equals(estacion.getClave(), estaciones.toCode(concepto.getClave(), 5)))
											  estacion= this.toEstacion(sesion, estaciones.toCode(concepto.getClave(), 5));
											if(estacion!= null) {
												estacion.setCosto(estacion.getCosto()+ diferencia);
												Methods.setValueSubClass(estacion, "abono"+ semana, new Object[] {estacion.getCosto()});
												DaoFactory.getInstance().update(sesion, estacion);
											} // if
	  									LOG.warn(count+ ".-  <"+ concepto.getNivel()+ "> ["+ concepto.getClave()+ "] ("+ concepto.getCodigo()+ ") {"+ concepto.getCosto()+ "} "+ concepto.getDescripcion());
										} // if
									} // if
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
						} // try
						catch(Exception e) {
							LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
							Error.mensaje(e);
							throw e;
						} // catch
						this.procesados= count;
						LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
					} // for
					if(raiz!= null && Cadena.isVacio(contrato.toLong("idEstacion"))) {
						TcKeetContratosLotesDto lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, contrato.toLong("idContratoLote"));
						lote.setIdEstacion(raiz.getIdEstacion());
						DaoFactory.getInstance().update(sesion, lote);
					} // if
				} // if
				else 
					throw new RuntimeException("El lote no existe en el contrato, por favor verifique");
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    catch(Exception e) {
      throw e;
    } // catch
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toUpdateEstaciones

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
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
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
					} // try
					catch(Exception e) {
						LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
						throw e;
					} // catch
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    catch(Exception e) {
      throw e;
    } // catch
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
		Boolean regresar	                 = false;
		Workbook workbook	                 = null;
		Sheet sheet                        = null;
		Estacion raiz                      = null;
		Estacion parcial                   = null;
		Estacion concepto                  = null; 
		TcManticMasivasBitacoraDto bitacora= null;
		String nivlePrototipo              = null;
		TcKeetPrototiposDto prototipo      = null;
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
				prototipo= this.toPrototipo(sesion);
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				int partida = 0; 
				int rubro   = 0; 
				String codigoPartida= "";
				String codigoRubro  = "";
				if(prototipo!= null) {
					nivlePrototipo= Cadena.rellenar(this.idPrototipo.toString(), 3, '0', true);
				  nivlePrototipo= nivlePrototipo.substring(nivlePrototipo.length()-3, nivlePrototipo.length());
					estaciones.setKeyLevel(prototipo.getIdEmpresa().toString(), 0); // idEmpresa
					estaciones.setKeyLevel(String.valueOf(this.getCurrentYear()), 1); // ejercicio
					estaciones.setKeyLevel("999", 2); // 999
					estaciones.setKeyLevel(nivlePrototipo, 3); // idPrototipo a 3 digitos
					raiz= this.toDeleteEstaciones(sesion, estaciones, prototipo.getNombre());
					for(int fila= 1; raiz!= null && fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
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
												if(parcial!= null)
													DaoFactory.getInstance().update(sesion, parcial);
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
									raiz.setCosto(raiz.getCosto()+ costo);
									estaciones.setKeyLevel(String.valueOf(partida), 4); // consecutivo de la estacion
									estaciones.setKeyLevel(String.valueOf(rubro), 5); // consecutivo del concepto
 									concepto= raiz.clone();
									// SI EL RUBRO ES CERO SIGNIFICA QUE INICIO LA NUEVA ESTACION POR LO TANTO SE COMIENZA CON LA SUMA EN CEROS
									if(rubro== 0L) {
										parcial= concepto;
										parcial.setCosto(0D);
									}
									else
										if(parcial!= null)
										  parcial.setCosto(parcial.getCosto()+ costo);
									concepto.setNivel(item.toLong("nivel"));
									concepto.setUltimo(item.toLong("ultimo"));
									concepto.setClave(estaciones.toCode());
									concepto.setCodigo(codigo);
									concepto.setNombre(nombre);
									concepto.setDescripcion(nombre);
									concepto.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
									concepto.setCantidad(cantidad);
									concepto.setCosto(costo);
									if(Cadena.isVacio(inicio))
										concepto.setInicio(LocalDate.now());
									else
										concepto.setInicio(Fecha.toLocalDate(inicio));
									if(Cadena.isVacio(termino))
										concepto.setTermino(LocalDate.now().plusDays(prototipo.getDiasConstruccion()));
									else
										concepto.setTermino(Fecha.toLocalDate(termino));
									concepto.setIdUsuario(JsfBase.getIdUsuario());
									DaoFactory.getInstance().insert(sesion, concepto);
									LOG.warn(count+ ".-  <"+ concepto.getNivel()+ "> ["+ concepto.getClave()+ "] ("+ concepto.getCodigo()+ ") {"+ concepto.getCosto()+ "} "+ concepto.getDescripcion());
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
						} // try
						catch(Exception e) {
							LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
							Error.mensaje(e);
							throw e;
						} // catch
						this.procesados= count;
						LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
					} // for
  				if(parcial!= null) 
						DaoFactory.getInstance().update(sesion, parcial);
  				if(raiz!= null) {
  					DaoFactory.getInstance().update(sesion, raiz);
	  				if(Cadena.isVacio(prototipo.getIdEstacion())) {
							prototipo.setIdEstacion(raiz.getIdEstacion());
							DaoFactory.getInstance().update(sesion, prototipo);
						} // if
					} // if
				} // if
				else 
					throw new RuntimeException("El portotipo no existe, por favor verifique");
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
	} // toPlantillas

  private Boolean toUpdatePlantilla(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		Estacion raiz           = null;
		Estacion estacion       = null;
		Estacion concepto       = null; 
		String nivlePrototipo   = null;
		TcManticMasivasBitacoraDto bitacora= null;
		TcKeetPrototiposDto prototipo      = null;
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
				prototipo= this.toPrototipo(sesion);
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				if(prototipo!= null) {
					nivlePrototipo= Cadena.rellenar(this.idPrototipo.toString(), 3, '0', true);
				  nivlePrototipo= nivlePrototipo.substring(nivlePrototipo.length()-3, nivlePrototipo.length());
					estaciones.setKeyLevel(prototipo.getIdEmpresa().toString(), 0); // idEmpresa
					estaciones.setKeyLevel(String.valueOf(this.getCurrentYear()), 1); // ejercicio
					estaciones.setKeyLevel("999", 2); // 999
					estaciones.setKeyLevel(nivlePrototipo, 3); // idPrototipo a 3 digitos
					raiz= this.toDeleteEstaciones(sesion, estaciones, prototipo.getNombre());
					for(int fila= 1; raiz!= null && fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
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
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									Entity item= this.toRubro(sesion, codigo);
									if(item!= null && !item.isEmpty() && item.toLong("nivel")== 6L) {
										concepto= this.toConcepto(sesion, estaciones.toKey(4), codigo);
										if(concepto!= null && concepto.isValid()) {
											double diferencia= costo- concepto.getCosto();
											concepto.setNombre(nombre);
											concepto.setDescripcion(nombre);
											concepto.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
											concepto.setCantidad(cantidad);
											concepto.setCosto(costo);
											if(Cadena.isVacio(inicio))
												concepto.setInicio(LocalDate.now());
											else
												concepto.setInicio(Fecha.toLocalDate(inicio));
											if(Cadena.isVacio(termino))
												concepto.setTermino(LocalDate.now().plusDays(prototipo.getDiasConstruccion()));
											else
												concepto.setTermino(Fecha.toLocalDate(termino));
											concepto.setIdUsuario(JsfBase.getIdUsuario());
											Methods.setValueSubClass(concepto, "abono"+ semana, new Object[] {costo});
											DaoFactory.getInstance().update(sesion, concepto);
											
											// RECUPERAR LA ESTACION PADRE Y REALIZAR LA SUMA O RESTA SEGUN APLIQUE EL COSTO
											if(estacion== null || !Objects.equals(estacion.getClave(), estaciones.toCode(concepto.getClave(), 5)))
											  estacion= this.toEstacion(sesion, estaciones.toCode(concepto.getClave(), 5));
											if(estacion!= null) {
												estacion.setCosto(estacion.getCosto()+ diferencia);
												Methods.setValueSubClass(estacion, "abono"+ semana, new Object[] {estacion.getCosto()});
												DaoFactory.getInstance().update(sesion, estacion);
											} // if
	  									LOG.warn(count+ ".-  <"+ concepto.getNivel()+ "> ["+ concepto.getClave()+ "] ("+ concepto.getCodigo()+ ") {"+ concepto.getCosto()+ "} "+ concepto.getDescripcion());
										} // if
									} // if
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
						} // try
						catch(Exception e) {
							LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
							Error.mensaje(e);
							throw e;
						} // catch
						this.procesados= count;
						LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
					} // for
  				if(raiz!= null) {
  					DaoFactory.getInstance().update(sesion, raiz);
	  				if(Cadena.isVacio(prototipo.getIdEstacion())) {
							prototipo.setIdEstacion(raiz.getIdEstacion());
							DaoFactory.getInstance().update(sesion, prototipo);
						} // if
					} // if
				} // if
				else 
					throw new RuntimeException("El lote no existe en el contrato, por favor verifique");
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
	} // toUpdatePlantilla
	
	
  private Boolean toMateriales(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		Material concepto       = null;
		Material parcial        = null;
		Material medio          = null;
		Material estacion       = null; 
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
				int material= 0; 
				String codigoPartida = "";
				String codigoRubro   = "";
				String codigoMaterial= "";
				if(contrato!= null) {
					estaciones.setKeyLevel(contrato.toString("idEmpresa"), 0); // idEmpresa
					estaciones.setKeyLevel(contrato.toString("ejercicio"), 1); // ejercicio
					estaciones.setKeyLevel(contrato.toString("contrato"), 2); // orden del contrato
					estaciones.setKeyLevel(contrato.toString("orden"), 3); // orden de contrato lote
					concepto= this.toDeleteMateriales(sesion, estaciones, contrato.toString("prototipo"));
					for(int fila= 1; concepto!= null && fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
						try {
							if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
								// 0       1      2     3        4         5         6			
								//MANZANA|LOTE|CODIGO|NOMBRE|CANTIDAD|COSTOS/IVA|UNIDADMEDIDA
								String manzana = sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String lote    = sheet.getCell(1, fila).getContents()!= null? new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String codigo  = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String nombre  = new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								double cantidad= Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								double costo   = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									if(!Objects.equals(manzana, "*") && !Objects.equals(contrato.toString("manzana"), manzana))
										throw new RuntimeException("El archivo contiene un numero de manzana incorrecto");
									if(!Objects.equals(lote, "*") && !Objects.equals(contrato.toString("lote"), lote))
										throw new RuntimeException("El archivo contiene un numero de lote incorrecto");
									Entity item= this.toRubro(sesion, "VistaContratosLotesMaterialesDto", codigo);
									if(item!= null && !item.isEmpty()) {
										if(count== 0 && item.toLong("nivel")!= 5L)
											throw new RuntimeException("El archivo no comienza con una estación correcta");
										// EL NIVEL 5 ES UNA ESTACION POR LO TANTO EL COSTO SE INICIA EN CERO
										switch(item.toLong("nivel").intValue()) {
											case 5:
												if(!Objects.equals(codigoPartida, codigo)) {
													codigoPartida = codigo;
													codigoRubro   = "";
													codigoMaterial= "";
													rubro         = 0;
													material      = 0;
													costo         = 0D;
													partida++;
													// ESTO ACTUALIZA EL COSTO TOTAL DE LOS CONCEPTOS
													if(parcial!= null) {
														Methods.setValueSubClass(parcial, "abono"+ semana, new Object[] {parcial.getCosto()});
														DaoFactory.getInstance().update(sesion, parcial);
													} // if
												} // if
												else 
													throw new RuntimeException("El archivo tiene una estacion duplicada ["+ codigo+ "] {"+ nombre+ "}");
												break;
											case 6:
												if(!Objects.equals(codigoRubro, codigo)) {
													codigoRubro   = codigo;
													codigoMaterial= "";
													material      = 0;
													costo         = 0D;
													rubro++;
													// ESTO ACTUALIZA EL COSTO TOTAL DE LOS MATERIALES
													if(medio!= null) {
														Methods.setValueSubClass(medio, "abono"+ semana, new Object[] {medio.getCosto()});
														DaoFactory.getInstance().update(sesion, medio);
													} // if
												} // if
												else 
													throw new RuntimeException("El archivo tiene un concepto duplicado ["+ codigo+ "] {"+ nombre+ "}");
												break;
											case 7:
												if(!Objects.equals(codigoMaterial, codigo)) {
													codigoMaterial= codigo;
													material++;
												} // if
												else 
													throw new RuntimeException("El archivo tiene un material duplicado ["+ codigo+ "] {"+ nombre+ "}");
												break;
										} // switch
									} // else
									else 
										throw new RuntimeException("El archivo tiene un codigo que no existe ["+ codigo+ "] {"+ nombre+ "}");
									// SE SUMA EL COSTO POR CADA CONCEPTO EXCEPTO LA QUE SON ESTACIONES PORQUE SE LIMPIO SU VALOR
									concepto.setCosto(concepto.getCosto()+ (costo* cantidad));
									estaciones.setKeyLevel(String.valueOf(partida), 4); // consecutivo de la estacion
									estaciones.setKeyLevel(String.valueOf(rubro), 5); // consecutivo del concepto
									estaciones.setKeyLevel(String.valueOf(material), 6); // consecutivo del material
 									estacion= concepto.clone();
									// SI EL MAERIAL ES CERO SIGNIFICA QUE INICIO LA NUEVO CONCEPTO POR LO TANTO SE COMIENZA CON LA SUMA EN CEROS
                  if(item.toLong("nivel")== 5L && material== 0L)
										medio= null;
								  else
										if(item.toLong("nivel")== 6L && material== 0L) {
											medio= estacion;
											medio.setCosto(0D);
										}
										else
											if(medio!= null)
												medio.setCosto(medio.getCosto()+ (costo* cantidad));
									// SI EL RUBRO ES CERO SIGNIFICA QUE INICIO LA NUEVA ESTACION POR LO TANTO SE COMIENZA CON LA SUMA EN CEROS
									if(rubro== 0L) {
										parcial= estacion;
										parcial.setCosto(0D);
									}
									else
										if(parcial!= null)
										  parcial.setCosto(parcial.getCosto()+ (costo* cantidad));
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
									estacion.setInicio(contrato.toDate("inicio"));
									estacion.setTermino(contrato.toDate("termino"));
									estacion.setIdUsuario(JsfBase.getIdUsuario());
									DaoFactory.getInstance().insert(sesion, estacion);
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
  				if(medio!= null) {
						Methods.setValueSubClass(medio, "abono"+ semana, new Object[] {medio.getCosto()});
						DaoFactory.getInstance().update(sesion, medio);
					} // if
  				if(concepto!= null) {
						Methods.setValueSubClass(concepto, "abono"+ semana, new Object[] {concepto.getCosto()});
  					DaoFactory.getInstance().update(sesion, concepto);
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
    catch(Exception e) {
      throw e;
    } // catch
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toMateriales

  private Boolean toUpdateMateriales(Session sesion, File archivo) throws Exception {
		Boolean regresar	      = false;
		Workbook workbook	      = null;
		Sheet sheet             = null;
		Material raiz           = null;
		Material estacion       = null;
		Material partida        = null;
		Material concepto       = null; 
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
				if(contrato!= null) {
					estaciones.setKeyLevel(contrato.toString("idEmpresa"), 0); // idEmpresa
					estaciones.setKeyLevel(contrato.toString("ejercicio"), 1); // ejercicio
					estaciones.setKeyLevel(contrato.toString("contrato"), 2); // orden del contrato
					estaciones.setKeyLevel(contrato.toString("orden"), 3); // orden de contrato lote
					raiz= this.toDeleteMateriales(sesion, estaciones, contrato.toString("prototipo"));
					for(int fila= 1; raiz!= null && fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
						try {
							if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
								// 0       1      2     3        4         5         6			
								//MANZANA|LOTE|CODIGO|NOMBRE|CANTIDAD|COSTOS/IVA|UNIDADMEDIDA
								String manzana = sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String lote    = sheet.getCell(1, fila).getContents()!= null? new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String codigo  = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String nombre  = new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								double cantidad= Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								double costo   = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, ]", ""): "0", 0D);
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									if(!Objects.equals(manzana, "*") && !Objects.equals(contrato.toString("manzana"), manzana))
										throw new RuntimeException("El archivo contiene un numero de manzana incorrecto");
									if(!Objects.equals(lote, "*") && !Objects.equals(contrato.toString("lote"), lote))
										throw new RuntimeException("El archivo contiene un numero de lote incorrecto");
									Entity item= this.toRubro(sesion, "VistaContratosLotesMaterialesDto", codigo);
									if(item!= null && !item.isEmpty() && item.toLong("nivel")== 7L) {
										concepto= this.toMaterial(sesion, estaciones.toKey(4), codigo);
										if(concepto!= null && concepto.isValid()) {
											double diferencia= costo- concepto.getCosto();
											concepto.setNombre(nombre);
											concepto.setDescripcion(nombre);
											concepto.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
											concepto.setCantidad(cantidad);
											concepto.setCosto(costo);
											concepto.setInicio(contrato.toDate("inicio"));
											concepto.setTermino(contrato.toDate("termino"));
											concepto.setIdUsuario(JsfBase.getIdUsuario());
											Methods.setValueSubClass(concepto, "abono"+ semana, new Object[] {costo});
											DaoFactory.getInstance().update(sesion, concepto);
											
											// RECUPERAR EL CONCEPTO PADRE Y REALIZAR LA SUMA O RESTA SEGUN APLIQUE EL COSTO
											if(partida== null || !Objects.equals(partida.getClave(), estaciones.toCode(concepto.getClave(), 6)))
											  partida= this.toPartida(sesion, estaciones.toCode(concepto.getClave(), 6), 6L);
											if(partida!= null) {
												partida.setCosto(estacion.getCosto()+ (diferencia* cantidad));
												Methods.setValueSubClass(partida, "abono"+ semana, new Object[] {estacion.getCosto()});
												DaoFactory.getInstance().update(sesion, partida);
											} // if
											// RECUPERAR LA ESTACION PADRE Y REALIZAR LA SUMA O RESTA SEGUN APLIQUE EL COSTO
											if(estacion== null || !Objects.equals(estacion.getClave(), estaciones.toCode(concepto.getClave(), 5)))
											  estacion= this.toPartida(sesion, estaciones.toCode(concepto.getClave(), 5), 5L);
											if(estacion!= null) {
												estacion.setCosto(estacion.getCosto()+ (diferencia* cantidad));
												Methods.setValueSubClass(estacion, "abono"+ semana, new Object[] {estacion.getCosto()});
												DaoFactory.getInstance().update(sesion, estacion);
											} // if
	  									LOG.warn(count+ ".-  <"+ concepto.getNivel()+ "> ["+ concepto.getClave()+ "] ("+ concepto.getCodigo()+ ") {"+ concepto.getCosto()+ "} "+ concepto.getDescripcion());
										} // if
									} // if
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
					throw new RuntimeException("El lote no existe en el contrato, por favor verifique");
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    catch(Exception e) {
      throw e;
    } // catch
    finally {
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toUpdateMateriales

	private TcManticClientesDto toFindCliente(Session sesion, String rfc) {
		TcManticClientesDto regresar= null;
		Map<String, Object> params  = null;
		try {
			params=new HashMap<>();
			params.put("rfc", rfc);
			regresar= (TcManticClientesDto)DaoFactory.getInstance().toEntity(sesion, TcManticClientesDto.class, "VistaCargasMasivasDto", "cliente", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindCliente
	
  private TcManticProveedoresDto toFindProveedor(Session sesion, String rfc) {
		TcManticProveedoresDto regresar= null;
		Map<String, Object> params     = null;
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

	private TcManticArticulosDto toFindArticulo(Session sesion, String codigo, String auxiliar, Long idArticuloTipo) {
		TcManticArticulosDto regresar= null;
		Map<String, Object> params   = null;
		try {
			params=new HashMap<>();
			params.put("codigo", codigo);
			params.put("auxiliar", auxiliar);
			params.put("idArticuloTipo", idArticuloTipo);
			regresar= (TcManticArticulosDto)DaoFactory.getInstance().toEntity(sesion, TcManticArticulosDto.class, "VistaCargasMasivasDto", "ambos", params);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFindArticulo
	
  private Boolean toPreciosProveedor(Session sesion, File archivo) throws Exception {
		Boolean regresar = false;
		Workbook workbook= null;
		Sheet sheet      = null;
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
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
							// 0       1        2      3        4         5          6            7			
							//RFC|RAZONSOCIAL|CLAVE|AUXILIAR|MATERIAL|PRECIOBASE|PRECIOLISTA|PRECIOESPECIAL
							String rfc     = sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							String clave   = sheet.getCell(2, fila).getContents()!= null? new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							String auxiliar= sheet.getCell(3, fila).getContents()!= null? new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							double base    = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, *]", ""): "0", 0D);
							double lista   = Numero.getDouble(sheet.getCell(6, fila).getContents()!= null? sheet.getCell(6, fila).getContents().replaceAll("[$, *]", ""): "0", 0D);
							double especial= Numero.getDouble(sheet.getCell(7, fila).getContents()!= null? sheet.getCell(7, fila).getContents().replaceAll("[$, *]", ""): "0", 0D);
							if(!Cadena.isVacio(rfc) && !Cadena.isVacio(clave)) {
								TcManticProveedoresDto proveedor= null;
								if(Objects.equals(rfc, "*") || this.idContatoLote!= Constantes.TOP_OF_ITEMS)
									proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, this.idContatoLote);
								else
									proveedor= this.toFindProveedor(sesion, rfc);
								TcManticArticulosDto articulo   = this.toFindArticulo(sesion, clave, auxiliar, 1L);
								if(proveedor!= null && articulo!= null) {
									params.put("idProveedor", proveedor.getIdProveedor());
									params.put("idArticulo", articulo.getIdArticulo());
									TcKeetArticulosProveedoresDto precios= (TcKeetArticulosProveedoresDto)DaoFactory.getInstance().toEntity(sesion, TcKeetArticulosProveedoresDto.class, "TcKeetArticulosProveedoresDto", "identically", params);
									if(precios== null) {
										precios= new TcKeetArticulosProveedoresDto(
											proveedor.getIdProveedor(), // Long idProveedor, 
											lista, // Double precioLista, 
											JsfBase.getIdUsuario(), // Long idUsuario, 
											-1L, // Long idArticuloProveedor, 
											articulo.getIdArticulo(), // Long idArticulo, 
											especial, // Double precioEspecial, 
											base, // Double precioBase, 
											null // LocalDateTime actualizado
										);
									  DaoFactory.getInstance().insert(sesion, precios);
									} // if
									else {
										if(!"*".equals(sheet.getCell(5, fila).getContents()))
										  precios.setPrecioBase(base);
										if(!"*".equals(sheet.getCell(6, fila).getContents()))
    	  							precios.setPrecioLista(lista);
										if(!"*".equals(sheet.getCell(7, fila).getContents()))
   										precios.setPrecioEspecial(especial);
										precios.setActualizado(LocalDateTime.now());
										DaoFactory.getInstance().update(sesion, precios);
									} // else
								} // if
								monitoreo.incrementar();
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ ": rfc: ["+ rfc+ "] codigo: ["+ clave+ "] precio base: ["+ base+ "] precio lista: ["+ lista+ "] precio especial: ["+ especial+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL RFC ["+ rfc+ "] CODIGO["+ clave+ "] PRECIO BASE["+ base+ "], PRECIO LISTA["+ lista+ "], PRECIO ESPECIAL["+ especial+ "] ESTAN EN CEROS O VACIO" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++;
						} // if	
					} // try
					catch(Exception e) {
						LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
						throw e;
					} // catch
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= true;
			} // if
		} // try
    catch(Exception e) {
      throw e;
    } // catch
    finally {
			Methods.clean(params);
      if(workbook!= null) {
        workbook.close();
        workbook = null;
      } // if
    } // finally
		return regresar;
	} // toPreciosProveedor	

  private Boolean toPreciosClientes(Session sesion, File archivo) throws Exception {
		Boolean regresar = false;
		Workbook workbook= null;
		Sheet sheet      = null;
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
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
					try {
						if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
							//      0          1          2        3      4      5        6          7
							//RFCPROVEEDOR|PROVEEDOR|RFCCLIENTE|CLIENTE|CLAVE|AUXILIAR|MATERIAL|PRECIOCONVENIO
							String rfcProveedor= sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							String rfcCliente  = sheet.getCell(2, fila).getContents()!= null? new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							String clave   = sheet.getCell(3, fila).getContents()!= null? new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							String auxiliar= sheet.getCell(4, fila).getContents()!= null? new String(sheet.getCell(4, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): null;
							double precio  = Numero.getDouble(sheet.getCell(7, fila).getContents()!= null? sheet.getCell(7, fila).getContents().replaceAll("[$, *]", ""): "0", 0D);
							if(!Cadena.isVacio(rfcProveedor) && !Cadena.isVacio(rfcCliente) && !Cadena.isVacio(clave)) {
								TcManticProveedoresDto proveedor= null;
								if(Objects.equals(rfcProveedor, "*") || this.idContatoLote!= Constantes.TOP_OF_ITEMS)
									proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, this.idContatoLote);
								else
									proveedor= this.toFindProveedor(sesion, rfcProveedor);
								TcManticClientesDto cliente= null;
								if(Objects.equals(rfcCliente, "*") || this.idPrototipo!= Constantes.TOP_OF_ITEMS)
									cliente= (TcManticClientesDto)DaoFactory.getInstance().findById(TcManticClientesDto.class, this.idPrototipo);
								else
									cliente= this.toFindCliente(sesion, rfcCliente);
								TcManticArticulosDto articulo   = this.toFindArticulo(sesion, clave, auxiliar, 1L);
								if(proveedor!= null && cliente!= null && articulo!= null) {
									params.put("idProveedor", proveedor.getIdProveedor());
									params.put("idCliente", cliente.getIdCliente());
									params.put("idArticulo", articulo.getIdArticulo());
									TrKeetArticuloProveedorClienteDto precios= (TrKeetArticuloProveedorClienteDto)DaoFactory.getInstance().toEntity(sesion, TrKeetArticuloProveedorClienteDto.class, "TrKeetArticuloProveedorClienteDto", "igual", params);
									if(precios== null) {
										precios= new TrKeetArticuloProveedorClienteDto(
											precio, // Double precioConvenio, 
											proveedor.getIdProveedor(), // Long idProveedor, 
											cliente.getIdCliente(), // Long idCliente, 
											JsfBase.getIdUsuario(), // Long idUsuario, 
											-1L, // Long idArticuloProveedorCliente, 
											0D, // Double precioAnterior, 
											articulo.getIdArticulo(), // Long idArticulo, 
											null // LocalDateTime actualizado
										);
									  DaoFactory.getInstance().insert(sesion, precios);
									} // if
									else {
										if(!"*".equals(sheet.getCell(7, fila).getContents())) {
											precios.setPrecioAnterior(precios.getPrecioConvenio());
										  precios.setPrecioConvenio(precio);
										} // if	
										precios.setActualizado(LocalDateTime.now());
										DaoFactory.getInstance().update(sesion, precios);
									} // else
								} // if
								monitoreo.incrementar();
							} // if
							else {
								this.errores++;
								LOG.warn(fila+ ": proveedor: ["+ rfcProveedor+ "] cliente: ["+ rfcCliente+ "] codigo: ["+ clave+ "] precio convenio: ["+ precio+ "]");
								TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
									sheet.getCell(0, fila).getContents(), // String codigo, 
									-1L, // Long idMasivaDetalle, 
									this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
									"EL PROVEEDOR ["+ rfcProveedor+ "] CLIENTE["+ rfcCliente+ "] CODIGO["+ clave+ "], PRECIO CONVENIO["+ precio+ "] ESTAN EN CEROS O VACIO" // String observaciones
								);
								DaoFactory.getInstance().insert(sesion, detalle);
							} // else	
							count++;
						} // if	
					} // try
					catch(Exception e) {
						LOG.error("[--->>> ["+ fila+ "] {"+ sheet.getCell(0, fila).getContents().toUpperCase()+ "} {"+ sheet.getCell(2, fila).getContents().toUpperCase()+ "} <<<---]");
						Error.mensaje(e);
						throw e;
					} // catch
					this.procesados= count;
					LOG.warn("Procesando el registro "+ count+ " de "+ monitoreo.getTotal()+ "  ["+ Numero.toRedondear(monitoreo.getProgreso()* 100/ monitoreo.getTotal())+ " %]");
				} // for
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
	} // toPreciosClientes

	private Control toDeleteControles(Session sesion, Controles controles, String prototipo) throws Exception {
		Control regresar          = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("clave", controles.toKey(4));
			if(Objects.equals(this.idLimpiar, 1L))
	  		DaoFactory.getInstance().deleteAll(sesion, TcKeetControlesDto.class, params);
  		regresar= (Control)DaoFactory.getInstance().toEntity(sesion, Control.class, "VistaControlesLotesDto", "estaciones", params);
			if(regresar!= null)
				regresar.setCosto(0D);
			else {
				regresar= new Control(controles.toCode(), "&A", prototipo, 4L, 2L, 87L, JsfBase.getIdUsuario());
    		DaoFactory.getInstance().insert(sesion, regresar);
			} // else
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}  
  private Boolean toControles(Session sesion, File archivo) throws Exception {
		Boolean regresar = false;
		Workbook workbook= null;
		Sheet sheet      = null;
		Control concepto = null;
		Control parcial  = null;
		Control control  = null; 
		TcManticMasivasBitacoraDto bitacora= null;
		try {
			Semanas semanas= new Semanas();
			int semana= semanas.getSemana(sesion);
			Controles controles= new Controles();
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
					controles.setKeyLevel(contrato.toString("idEmpresa"), 0); // idEmpresa
					controles.setKeyLevel(contrato.toString("ejercicio"), 1); // ejercicio
					controles.setKeyLevel(contrato.toString("contrato"), 2); // orden del contrato
					controles.setKeyLevel(contrato.toString("orden"), 3); // orden de contrato lote
					concepto= this.toDeleteControles(sesion, controles, contrato.toString("prototipo"));
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
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
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
									controles.setKeyLevel(String.valueOf(partida), 4); // consecutivo de la estacion
									controles.setKeyLevel(String.valueOf(rubro), 5); // consecutivo del concepto
									control= concepto.clone();
									// SI EL RUBRO ES CERO SIGNIFICA QUE INICIO LA NUEVA ESTACION POR LO TANTO SE COMIENZA CON LA SUMA EN CEROS
									if(rubro== 0L) {
										parcial= control;
										parcial.setCosto(0D);
									}
									else
										if(parcial!= null)
											parcial.setCosto(parcial.getCosto()+ costo);
									if(item.toLong("nivel")== 5L || (item.toLong("nivel")== 6L && costo> 0 && cantidad> 0)) {
										control.setNivel(item.toLong("nivel"));
										control.setUltimo(item.toLong("ultimo"));
										control.setClave(controles.toCode());
										control.setCodigo(codigo);
										control.setNombre(nombre);
										control.setDescripcion(nombre);
										control.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
										control.setCantidad(cantidad);
										control.setCosto(costo);
										Methods.setValueSubClass(control, "abono"+ semana, new Object[] {costo});
										if(Cadena.isVacio(inicio))
											control.setInicio(contrato.toDate("inicio"));
										else
											control.setInicio(Fecha.toLocalDate(inicio));
										if(Cadena.isVacio(termino))
											control.setTermino(contrato.toDate("termino"));
										else
											control.setTermino(Fecha.toLocalDate(termino));
										control.setIdUsuario(JsfBase.getIdUsuario());
  									DaoFactory.getInstance().insert(sesion, control);
									} // if	
									LOG.warn(count+ ".-  <"+ control.getNivel()+ "> ["+ control.getClave()+ "] ("+ control.getCodigo()+ ") {"+ control.getCosto()+ "} "+ control.getDescripcion());
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
					} // if
				} // if
				else 
					throw new RuntimeException("El lote no existe en el contrato, por favor verifique");
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
	} // toControles

  private Boolean toUpdateControles(Session sesion, File archivo) throws Exception {
		Boolean regresar = false;
		Workbook workbook= null;
		Sheet sheet      = null;
		Control raiz     = null;
		Control control  = null;
		Control concepto = null; 
		TcManticMasivasBitacoraDto bitacora= null;
		try {
			Semanas semanas= new Semanas();
			int semana     = semanas.getSemana(sesion);
			Controles controles= new Controles();
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
					controles.setKeyLevel(contrato.toString("idEmpresa"), 0); // idEmpresa
					controles.setKeyLevel(contrato.toString("ejercicio"), 1); // ejercicio
					controles.setKeyLevel(contrato.toString("contrato"), 2); // orden del contrato
					controles.setKeyLevel(contrato.toString("orden"), 3); // orden de contrato lote
					raiz= this.toDeleteControles(sesion, controles, contrato.toString("prototipo"));
					for(int fila= 1; raiz!= null && fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
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
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									if(!Objects.equals(manzana, "*") && !Objects.equals(contrato.toString("manzana"), manzana))
										throw new RuntimeException("El archivo contiene un numero de manzana incorrecto");
									if(!Objects.equals(lote, "*") && !Objects.equals(contrato.toString("lote"), lote))
										throw new RuntimeException("El archivo contiene un numero de lote incorrecto");
									Entity item= this.toRubro(sesion, codigo);
									if(item!= null && !item.isEmpty() && item.toLong("nivel")== 6L) {
										concepto= this.toControlConcepto(sesion, controles.toKey(4), codigo);
										if(concepto!= null && concepto.isValid()) {
											double diferencia= costo- concepto.getCosto();
											concepto.setNombre(nombre);
											concepto.setDescripcion(nombre);
											concepto.setIdEmpaqueUnidadMedida(this.toFindUnidadMedida(sesion, sheet.getCell(6, fila).getContents()));
											concepto.setCantidad(cantidad);
											concepto.setCosto(costo);
											if(Cadena.isVacio(inicio))
												concepto.setInicio(contrato.toDate("inicio"));
											else
												concepto.setInicio(Fecha.toLocalDate(inicio));
											if(Cadena.isVacio(termino))
												concepto.setTermino(contrato.toDate("termino"));
											else
												concepto.setTermino(Fecha.toLocalDate(termino));
											concepto.setIdUsuario(JsfBase.getIdUsuario());
											Methods.setValueSubClass(concepto, "abono"+ semana, new Object[] {costo});
											DaoFactory.getInstance().update(sesion, concepto);
											
											// RECUPERAR LA ESTACION PADRE Y REALIZAR LA SUMA O RESTA SEGUN APLIQUE EL COSTO
											if(control== null || !Objects.equals(control.getClave(), controles.toCode(concepto.getClave(), 5)))
											  control= this.toControlEstacion(sesion, controles.toCode(concepto.getClave(), 5));
											if(control!= null) {
												control.setCosto(control.getCosto()+ diferencia);
												Methods.setValueSubClass(control, "abono"+ semana, new Object[] {control.getCosto()});
												DaoFactory.getInstance().update(sesion, control);
											} // if
	  									LOG.warn(count+ ".-  <"+ concepto.getNivel()+ "> ["+ concepto.getClave()+ "] ("+ concepto.getCodigo()+ ") {"+ concepto.getCosto()+ "} "+ concepto.getDescripcion());
										} // if
									} // if
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
					throw new RuntimeException("El lote no existe en el contrato, por favor verifique");
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
	} // toUpdateControles
  
}