package mx.org.kaana.keet.ingresos.reglas;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetIngresosArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetIngresosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetIngresosDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.reportes.FileSearch;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.db.dto.TcManticArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasDetallesDto;
import mx.org.kaana.mantic.inventarios.entradas.beans.Nombres;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx implements Serializable {

  private static final Logger LOG = Logger.getLogger(Transaccion.class);
	private static final long serialVersionUID=-6069204157451117549L;
 
	private TcKeetIngresosDto orden;	
	private Importado xml;
	private Importado pdf;
	private String messageError;
	private TcKeetIngresosBitacoraDto bitacora;

	public Transaccion(TcKeetIngresosDto orden) {
		this(orden, null, null);
	}

	public Transaccion(TcKeetIngresosDto orden, TcKeetIngresosBitacoraDto bitacora) {
		this(orden);
		this.bitacora= bitacora;
	}
	
	public Transaccion(TcKeetIngresosDto orden, Importado xml, Importado pdf) {
		this.orden    = orden;		
		this.xml      = xml;
		this.pdf      = pdf;
	} // Transaccion

	protected void setMessageError(String messageError) {
		this.messageError=messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                      = false;
		TcKeetIngresosBitacoraDto bitacoraNota= null;
		Map<String, Object> params            = null;
		Siguiente consecutivo                 = null;
		try {
			params= new HashMap<>();
      if(this.orden!= null && Objects.equals(-1L, this.orden.getIdContrato()))
        this.orden.setIdContrato(null);
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la factura.");
			switch(accion) {
				case AGREGAR:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					this.orden.setIdIngresoEstatus(1L);
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
				  bitacoraNota= new TcKeetIngresosBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdIngreso(), this.orden.getIdIngresoEstatus());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
     	    this.toUpdateDeleteXml(sesion);	
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
     	    this.toUpdateDeleteXml(sesion);	
					break;				
				case ELIMINAR:
  				params.put("idIngreso", this.orden.getIdIngreso());
          regresar= DaoFactory.getInstance().deleteAll(sesion, TcKeetIngresosArchivosDto.class, params)>= 1L;
          regresar= DaoFactory.getInstance().delete(sesion, this.orden)>= 1L;
          this.orden.setIdIngresoEstatus(2L);
          bitacoraNota= new TcKeetIngresosBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdIngreso(), 2L);
          regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
          this.toDeleteXmlPdf();	
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdIngresoEstatus(this.bitacora.getIdIngresoEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
						if(this.bitacora.getIdIngresoEstatus().equals(3L)) {
  						// AGREGAR UNA CUENTA POR COBRAR 
              this.toRecordDeuda(sesion, this.orden.getTotal());
						} // if	
					} // if
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {
      Error.mensaje(e);			
			throw new Exception(this.messageError.concat("<br/>")+ e);
		} // catch		
		LOG.info("Se registro de forma correcta la factura: "+ this.orden.getConsecutivo());
		return regresar;
	}	// ejecutar

	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetIngresosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
			  regresar= new Siguiente(next.toLong());
			else
			  regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private void toDeleteAll(String path, String type, List<Nombres> listado) {
    FileSearch fileSearch = new FileSearch();
    fileSearch.searchDirectory(new File(path), type.toLowerCase());
    if(fileSearch.getResult().size()> 0)
		  for (String matched: fileSearch.getResult()) {
				String name= matched.substring((matched.lastIndexOf("/")< 0? matched.lastIndexOf("\\"): matched.lastIndexOf("/"))+ 1);
				if(listado.indexOf(new Nombres(name))< 0) {
          LOG.warn("Factura: "+ this.orden.getConsecutivo()+ " delete file: ".concat(matched));
				  File file= new File(matched);
				  file.delete();
				} // if
      } // for
	}
	
	private List<Nombres> toListFile(Session sesion, Importado tmp, Long idTipoArchivo) throws Exception {
		List<Nombres> regresar= null;
		Map<String, Object> params=null;
		try {
			params  = new HashMap<>();
			params.put("idTipoArchivo", idTipoArchivo);
			params.put("ruta", tmp.getRuta());
			regresar= (List<Nombres>)DaoFactory.getInstance().toEntitySet(sesion, Nombres.class, "TcKeetIngresosArchivosDto", "listado", params);
			regresar.add(new Nombres(tmp.getName()));
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	protected void toUpdateDeleteXml(Session sesion) throws Exception {
		TcKeetIngresosArchivosDto tmp= null;
		if(this.orden.getIdIngreso()!= -1L) {
			if(this.xml!= null) {
				tmp= new TcKeetIngresosArchivosDto(
					-1L, // idNotaArchivo 
					this.xml.getRuta(), // ruta
					this.xml.getFileSize(), // tamanio
					JsfBase.getIdUsuario(), // idUsuario
					1L, // idTipoArchivo
					Configuracion.getInstance().getPropiedadSistemaServidor("ingresos").concat(this.xml.getRuta()).concat(this.xml.getName()), // alias
					new Long(Calendar.getInstance().get(Calendar.MONTH)+ 1), // mes
					this.orden.getIdIngreso(), // idIngreso
					this.xml.getName(), // nombre
					this.xml.getObservaciones(), // observacion
					new Long(Calendar.getInstance().get(Calendar.YEAR)), // ejercicio
					1L, // idPrincipal
					this.xml.getOriginal() // archivo
				);
        this.toSaveFile(sesion, this.xml.getIdArchivo());
				TcKeetIngresosArchivosDto exists= (TcKeetIngresosArchivosDto)DaoFactory.getInstance().toEntity(TcKeetIngresosArchivosDto.class, "TcKeetIngresosArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcKeetIngresosArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
				this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("ingresos").concat(this.xml.getRuta()), ".".concat(this.xml.getFormat().name()), this.toListFile(sesion, this.xml, 1L));
			} // if	
			if(this.pdf!= null) {
        // Long idIngreso, String archivo, String ruta, String nombre, Long ejercicio, Long tamanio, Long idUsuario, Long idTipoArchivo, Long idIngresoArchivo, Long idPrincipal, String observaciones, String alias, Long mes
				tmp= new TcKeetIngresosArchivosDto(
					-1L,
					this.pdf.getRuta(),
					this.pdf.getFileSize(),
					JsfBase.getIdUsuario(),
					2L,
					Configuracion.getInstance().getPropiedadSistemaServidor("ingresos").concat(this.pdf.getRuta()).concat(this.pdf.getName()),
					new Long(Calendar.getInstance().get(Calendar.MONTH)+ 1),
					this.orden.getIdIngreso(),
					this.pdf.getName(),
					this.pdf.getObservaciones(),
					new Long(Calendar.getInstance().get(Calendar.YEAR)),
					1L,
					this.pdf.getOriginal()
				);
        this.toSaveFile(sesion, this.pdf.getIdArchivo());
				TcKeetIngresosArchivosDto exists= (TcKeetIngresosArchivosDto)DaoFactory.getInstance().toEntity(TcKeetIngresosArchivosDto.class, "TcKeetIngresosArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcKeetIngresosArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
				this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("ingresos").concat(this.pdf.getRuta()), ".".concat(this.pdf.getFormat().name()), this.toListFile(sesion, this.pdf, 2L));
			} // if	
  	} // if	
	}

	public void toDeleteXmlPdf() throws Exception {
		List<TcKeetIngresosArchivosDto> list= (List<TcKeetIngresosArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcKeetIngresosArchivosDto.class, this.orden.toMap(), "all");
		if(list!= null)
			for (TcKeetIngresosArchivosDto item: list) {
				LOG.info("Factura: "+ this.orden.getConsecutivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				file.delete();
			} // for
	}	

	protected boolean toSaveFile(Session sesion, Long idKey) throws Exception {
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idEliminado", 2L);
			regresar= DaoFactory.getInstance().update(TcManticArchivosDto.class, idKey, params)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally				
		return regresar;
	} // toSaveFile  
  
  protected void toRecordDeuda(Session sesion, Double importe) throws Exception {
		TcManticClientesDeudasDto deuda= null;		
		deuda= new TcManticClientesDeudasDto();
		deuda.setIdIngreso(this.orden.getIdIngreso());
		deuda.setIdCliente(this.orden.getIdCliente());
		deuda.setIdUsuario(JsfBase.getIdUsuario());
		deuda.setImporte(importe);
		deuda.setSaldo(importe);
		deuda.setLimite(this.toLimiteCredito(sesion));
		deuda.setIdClienteEstatus(1L);
		DaoFactory.getInstance().insert(sesion, deuda);		
	} // registrarDeuda  
  
	public LocalDate toLimiteCredito(Session sesion) throws Exception {
		TcManticClientesDto cliente= (TcManticClientesDto) DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, this.orden.getIdCliente());
		Long addDias= cliente.getPlazoDias();			
		LocalDate regresar= LocalDate.now();			
		regresar.plusDays(addDias.intValue());
		return regresar;
	} // toLimiteCredito
  
} 