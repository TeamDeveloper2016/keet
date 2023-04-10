package mx.org.kaana.keet.catalogos.materiales.reglas;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.catalogos.materiales.beans.Material;
import mx.org.kaana.keet.catalogos.materiales.beans.Prototipo;
import mx.org.kaana.keet.db.dto.TcKeetContratosMaterialesDto;
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
import static org.apache.commons.io.Charsets.ISO_8859_1;
import static org.apache.commons.io.Charsets.UTF_8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG = LogFactory.getLog(Transaccion.class);
  
	private Material material;	
  private TcManticMasivasArchivosDto masivo;	
	private ECargaMasiva categoria;
	private Long idContrato;
	private Long idArchivo;
	private Long idLimpiar;
	private int errores;
	private int procesados;
	private String messageError;

	public Transaccion(Material material) {
		this.material= material;		
	} // Transaccion

  public Transaccion(TcManticMasivasArchivosDto masivo, Long idContrato, ECargaMasiva categoria, Long idLimpiar, Long idArchivo) {
		this.masivo    = masivo;		
		this.categoria = categoria;
		this.errores   = 0;
		this.procesados= 0;
		this.idContrato= idContrato;
		this.idLimpiar = idLimpiar;
    this.idArchivo = idArchivo;
	} // Transaccion
  
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
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= Boolean.FALSE;
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registro el material");
			switch(accion) {
				case AGREGAR:
          this.material.setIdUsuario(JsfBase.getIdUsuario());
          this.material.setRegistro(LocalDateTime.now());
					regresar= DaoFactory.getInstance().insert(sesion, this.material)>= 1L;
					break;
				case MODIFICAR:
          this.material.setIdUsuario(JsfBase.getIdUsuario());
          this.material.setRegistro(LocalDateTime.now());
					regresar= DaoFactory.getInstance().update(sesion, this.material)>= 1L;
					break;				
				case ELIMINAR:
          this.material.setIdUsuario(JsfBase.getIdUsuario());
          this.material.setRegistro(LocalDateTime.now());
					regresar= DaoFactory.getInstance().delete(sesion, this.material)>= 1L;
					break;
				case PROCESAR:
					regresar= this.toProcess(sesion);
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;
	}	// ejecutar
	
	protected boolean toProcess(Session sesion) throws Exception {
		boolean regresar                   = Boolean.FALSE;  
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
				  this.toUpdateMateriales(sesion, file);
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
				regresar= Boolean.TRUE;
			} // if	
			else
				LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ this.masivo.getNombre());
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
	} // toProcess

  public void toDeleteXls() throws Exception {
		List<TcManticMasivasArchivosDto> list= (List<TcManticMasivasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticMasivasArchivosDto.class, this.masivo.toMap(), "all");
		if(list!= null)
			for (TcManticMasivasArchivosDto item: list) {
				LOG.info("Catalogo importado: "+ item.getIdMasivaArchivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				file.delete();
			} // for
	}	// toDeleteXls 	

	private Entity toContrato(Session sesion) throws Exception {
		Entity regresar           = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idContrato", this.idContrato);
			regresar= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetContratosDto", "byId", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
  
	private List<Prototipo> toPrototipos(Session sesion) throws Exception {
		List<Prototipo> regresar  = new ArrayList<>();
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idContrato", this.idContrato);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaContratosDto", "findPrototipos", params);
      if(items!= null && !items.isEmpty())
        for (Entity item: items) {
          regresar.add(new Prototipo(item.toLong("idPrototipo"), item.toString("nombre")));
        } // for
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
  
	private void toDeleteMateriales(Session sesion) throws Exception {
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idContrato", this.idContrato);
			if(Objects.equals(this.idLimpiar, 1L))
	  		DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosMaterialesDto.class, params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
	}

	private Entity toArticulo(Session sesion, String codigo) throws Exception {
		Entity regresar           = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("codigo", codigo);
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			params.put("sortOrder", "");
			regresar= (Entity)DaoFactory.getInstance().toEntity(sesion, "VistaGaleriaDto", "porCodigo", params);
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
  
  private Boolean toUpdateMateriales(Session sesion, File archivo) throws Exception {
		Boolean regresar	        = Boolean.FALSE;
		Workbook workbook	        = null;
		Sheet sheet               = null;
    Entity contrato           = null; 
    List<Prototipo> prototipos= null;
    Map<String, Object> params= new HashMap<>();
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
				contrato  = this.toContrato(sesion);
        prototipos= this.toPrototipos(sesion);
				//LOG.info("<-------------------------------------------------------------------------------------------------------------->");
				LOG.info("Filas del documento: "+ sheet.getRows());
				this.errores= 0;
				int count   = 0; 
				if(contrato!= null) {
					this.toDeleteMateriales(sesion);
					for(int fila= 1; fila< sheet.getRows() && monitoreo.isCorriendo(); fila++) {
						try {
							if(!Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(0, fila).getContents()) && !Cadena.isVacio(sheet.getCell(2, fila).getContents()) && !sheet.getCell(0, fila).getContents().toUpperCase().startsWith("NOTA")) {
								//    0       1         2     3        4         5        
								//CONTRATO|PROTOTIPO|CODIGO|NOMBRE|CANTIDAD|COSTOUNITARIO
								String clave    = sheet.getCell(0, fila).getContents()!= null? new String(sheet.getCell(0, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String prototipo= sheet.getCell(1, fila).getContents()!= null? new String(sheet.getCell(1, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1): "*";
								String codigo  = new String(sheet.getCell(2, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								String nombre  = new String(sheet.getCell(3, fila).getContents().toUpperCase().getBytes(UTF_8), ISO_8859_1);
								double cantidad= Numero.getDouble(sheet.getCell(4, fila).getContents()!= null? sheet.getCell(4, fila).getContents().replaceAll("[$, \"]", ""): "1", 0D);
								double costo   = Numero.getDouble(sheet.getCell(5, fila).getContents()!= null? sheet.getCell(5, fila).getContents().replaceAll("[$, \"]", ""): "0", 0D);
								codigo= new String(codigo.getBytes(ISO_8859_1), UTF_8).replaceAll(Constantes.CLEAN_ART, "").trim();
								nombre= nombre.replaceAll(Constantes.CLEAN_ART, "").trim();
								if(!Cadena.isVacio(codigo) && codigo.length()> 0) {
									if(!Objects.equals(contrato.toString("clave"), clave))
										throw new RuntimeException("El archivo contiene un número de contrato incorrecto [".concat(clave).concat("]"));
                  int index= prototipos.indexOf(new Prototipo(prototipo));
									if(index< 0)
										throw new RuntimeException("El archivo contiene un nombre de prototipo incorrecto [".concat(prototipo).concat("]"));
                  // FALTA RECUPERAR LA UNIDAD DE MEDIDA PARA GENERAR EL VALOR DE LA EXPANSIÓN BASADO EN LA CANTIDAD PARA OBTENER UN VALOR EN UNIDADES
                  Double factor= 100D;
                  Entity articulo= this.toArticulo(sesion, codigo);
									if(articulo== null || articulo.isEmpty())
										throw new RuntimeException("El archivo contiene un código de material incorrecto [".concat(codigo).concat("]"));
                  params.put("idContrato", contrato.toLong("idContrato"));
                  params.put("idPrototipo", prototipos.get(index).getIdPrototipo());
                  params.put("idArticulo", articulo.toLong("idArticulo"));
                  this.material= (Material)DaoFactory.getInstance().toEntity(sesion, Material.class, "TcKeetContratosMaterialesDto", "identically", params);
                  if(this.material== null) {
                    this.material= new Material(
                      codigo, // String codigo, 
                      -1L, // Long idContratoMaterial, 
                      clave, // String contrato, 
                      nombre, // String nombre, 
                      cantidad* factor, // Double expansion, 
                      costo, // Double precioUnitario, 
                      prototipo, // String prototipo, 
                      JsfBase.getIdUsuario(), // Long idUsuario, 
                      contrato.toLong("idContrato"), // Long idContrato, 
                      prototipos.get(index).getIdPrototipo(), // Long idPrototipo, 
                      this.idArchivo,//  Long idArchivo, 
                      cantidad, // Double cantidad, 
                      articulo.toLong("idArticulo") // Long idArticulo
                    );
                    DaoFactory.getInstance().insert(sesion, this.material);
                  } // if  
                  else {
                    this.material.setIdArchivo(this.idArchivo);
                    this.material.setCantidad(cantidad);
                    this.material.setPrecioUnitario(costo);
                    this.material.setExpansion(cantidad* factor);
                    this.material.setRegistro(LocalDateTime.now());
                    if(cantidad<= 0D || costo<= 0D)
                      DaoFactory.getInstance().delete(sesion, this.material);
                    else
                      DaoFactory.getInstance().update(sesion, this.material);
                  } // else
									monitoreo.incrementar();
								} // if
								else {
									this.errores++;
									LOG.warn(fila+ ": contrato["+ contrato+ "] prototipo["+ prototipo+ "] codigo["+ codigo+ "] cantidad["+ cantidad+ "] costo["+ costo+ "]");
									TcManticMasivasDetallesDto detalle= new TcManticMasivasDetallesDto(
										sheet.getCell(0, fila).getContents(), // String codigo, 
										-1L, // Long idMasivaDetalle, 
										this.masivo.getIdMasivaArchivo(), // Long idMasivaArchivo, 
										"EL CONTRATO["+ contrato+ "], PROTOTIPO["+ prototipo+ ", CODIGO["+ codigo+ "], CANTIDAD["+ cantidad+ "] COSTO["+ costo+ "] SON INCORRECTOS" // String observaciones
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
					throw new RuntimeException("El contrato no existe en el catalogo, por favor verifique");
				bitacora= new TcManticMasivasBitacoraDto("", this.masivo.getIdMasivaArchivo(), JsfBase.getIdUsuario(), -1L, this.masivo.getTuplas(), 2L);
  			DaoFactory.getInstance().insert(sesion, bitacora);
				LOG.warn("Cantidad de filas con error son: "+ this.errores);
 				this.procesados= count;
				regresar= Boolean.TRUE;
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
	} // toUpdateMateriales  

}