package mx.org.kaana.keet.ingresos.reglas;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
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
import mx.org.kaana.keet.ingresos.beans.Factura;
import mx.org.kaana.keet.ingresos.beans.Ingreso;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.reportes.FileSearch;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticFacturasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticFacturasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticFacturasDto;
import mx.org.kaana.mantic.db.dto.TcManticFicticiasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasBitacoraDto;
import mx.org.kaana.mantic.enums.EEstatusFacturas;
import mx.org.kaana.mantic.enums.EEstatusFicticias;
import mx.org.kaana.mantic.enums.EEstatusVentas;
import mx.org.kaana.mantic.facturas.enums.EEstatusClientesDeudas;
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
 
	private Ingreso orden;	
	private Factura comprobante;	
  private List<Articulo> articulos;
	private Importado xml;
	private Importado pdf;
	private String messageError;
	private TcManticVentasBitacoraDto bitacora;

	public Transaccion(Ingreso orden) {
		this(orden, null, Collections.EMPTY_LIST, null, null);
	}

	public Transaccion(Ingreso orden, TcManticVentasBitacoraDto bitacora) {
		this(orden);
		this.bitacora= bitacora;
	}
	
	public Transaccion(Ingreso orden, Factura comprobante, List<Articulo> articulos, Importado xml, Importado pdf) {
		this.orden      = orden;		
		this.comprobante= comprobante;		
    this.articulos  = articulos;
		this.xml        = xml;
		this.pdf        = pdf;
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
		TcManticVentasBitacoraDto bitacoraNota= null;
		Map<String, Object> params            = null;
		Siguiente consecutivo                 = null;
		Siguiente cuenta                      = null;
		try {
			params= new HashMap<>();
      if(this.orden!= null && Objects.equals(-1L, this.orden.getIdContrato()))
        this.orden.setIdContrato(null);
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la factura.");
			switch(accion) {
				case AGREGAR:
          Long idFactura= this.toRegistrarFactura(sesion, EEstatusFacturas.TIMBRADA.getIdEstatusFactura());
          if(idFactura>= 1L) {
            consecutivo= this.toSiguiente(sesion);
            this.orden.setTicket(consecutivo.getConsecutivo());			
            this.orden.setCticket(consecutivo.getOrden());			
            cuenta= this.toSiguienteCuenta(sesion);			
            this.orden.setConsecutivo(cuenta.getOrden());			
            this.orden.setOrden(cuenta.getOrden());
            this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
    				this.orden.setIdFactura(idFactura);
    				this.orden.setIdSerie(this.orden.getIdSerie()== null || this.orden.getIdSerie()<= 0? null: this.orden.getIdSerie());
            DaoFactory.getInstance().insert(sesion, this.orden);
            bitacoraNota= new TcManticVentasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdVenta(), this.orden.getIdVentaEstatus(), this.orden.getCticket(), this.orden.getTotal());
            regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
            this.toFillArticulos(sesion);
            this.toUpdateDeleteXml(sesion);	
            // AGREGAR UNA CUENTA POR COBRAR 
            this.toRecordDeuda(sesion);
          } // if
					break;
				case MODIFICAR:
          regresar= this.toUpdateDeuda(sesion);
					break;				
				case ELIMINAR:
  				params.put("idFactura", this.orden.getIdFactura());
          DaoFactory.getInstance().deleteAll(sesion, TcManticFacturasArchivosDto.class, params);
          DaoFactory.getInstance().delete(sesion, this.orden);
          this.orden.setIdVentaEstatus(13L);
          bitacoraNota= new TcManticVentasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdVenta(), 2L, this.orden.getCticket(), this.orden.getTotal());
          regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
          this.toCheckDeleteFile(sesion);
          // this.toDeleteXmlPdf();	
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdVentaEstatus(this.bitacora.getIdVentaEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
            // AGREGAR UNA CUENTA POR COBRAR 
						if(this.bitacora.getIdVentaEstatus().equals(EEstatusVentas.TIMBRADA.getIdEstatusVenta())) {
              this.comprobante= (Factura)DaoFactory.getInstance().findById(TcManticFacturasDto.class, this.orden.getIdFactura());
              if(this.comprobante!= null) {
                this.comprobante.setIdFacturaEstatus(EEstatusFacturas.TIMBRADA.getIdEstatusFactura());
                DaoFactory.getInstance().update(sesion, this.comprobante);
                TcManticFacturasBitacoraDto registro= new TcManticFacturasBitacoraDto(this.comprobante.getIdFacturaEstatus(), this.comprobante.getIdFactura(), null, JsfBase.getIdUsuario(), -1L);
                DaoFactory.getInstance().insert(sesion, registro);
                this.toRecordDeuda(sesion);
              } // if  
            } // if  
						// SI ES CANCELAR LA CUENTA FACTURA QUE SE INGRESO
						if(this.bitacora.getIdVentaEstatus().equals(EEstatusFicticias.CANCELADA.getIdEstatusFicticia())) {
  						// CANCELAR LA CUENTA POR COBRAR
              this.toCancelDeuda(sesion);
						} // if	
					} // if
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {
      Error.mensaje(e);			
			throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
		} // catch		
		LOG.info("Se registro de forma correcta la factura: "+ this.orden.getConsecutivo());
		return regresar;
	}	// ejecutar

	private Long toRegistrarFactura(Session sesion, Long idEstatus) throws Exception {
		Long regresar= -1L;
		try {			
			this.comprobante.setIdFacturaEstatus(idEstatus);
			this.comprobante.setIdUsuario(JsfBase.getIdUsuario());
			this.comprobante.setIntentos(0L);
			this.comprobante.setCorreos("");
			this.comprobante.setObservaciones("FACTURA REGISTRADA DE FORMA MANUAL");
			this.comprobante.setIdSerie(this.orden.getIdSerie()== null || this.orden.getIdSerie()<= 0? null: this.orden.getIdSerie());
			regresar= DaoFactory.getInstance().insert(sesion, this.comprobante);
      TcManticFacturasBitacoraDto registro= new TcManticFacturasBitacoraDto(this.comprobante.getIdFacturaEstatus(), this.comprobante.getIdFactura(), null, JsfBase.getIdUsuario(), -1L);
      DaoFactory.getInstance().insert(sesion, registro);
		} // try
		finally {
			setMessageError("Error al registrar la factura.");
		} // finally
		return regresar;
	} // toRegistrarFactura
  
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticFicticiasDto", "siguiente", params, "siguiente");
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
	} // toSiguiente
	
	private Siguiente toSiguienteCuenta(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("dia", Fecha.getHoyEstandar());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticFicticiasDto", "cuenta", params, "siguiente");
			if(next!= null && next.getData()!= null)
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
	} // toSiguienteCuenta
  
	private void toDeleteAll(String path, String type, List<Nombres> listado) {
    FileSearch fileSearch = new FileSearch();
    fileSearch.searchDirectory(new File(path), type.toLowerCase());
    if(fileSearch.getResult().size()> 0)
		  for (String matched: fileSearch.getResult()) {
				String name= matched.substring((matched.lastIndexOf("/")< 0? matched.lastIndexOf("\\"): matched.lastIndexOf("/"))+ 1);
				if(listado.indexOf(new Nombres(name))< 0) {
          LOG.warn("Factura: "+ this.orden.getConsecutivo()+ " delete file: ".concat(matched));
				  File file= new File(matched);
				  // file.delete();
				} // if
      } // for
	}
	
	private List<Nombres> toListFile(Session sesion, String path, String name, Long idTipoArchivo) throws Exception {
		List<Nombres> regresar= null;
		Map<String, Object> params=null;
		try {
			params  = new HashMap<>();
			params.put("idTipoArchivo", idTipoArchivo);
			params.put("ruta", path);
			regresar= (List<Nombres>)DaoFactory.getInstance().toEntitySet(sesion, Nombres.class, "TcManticFacturasArchivosDto", "listado", params);
			regresar.add(new Nombres(name));
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
		TcManticFacturasArchivosDto tmp= null;
		if(this.orden.getIdVenta()!= -1L) {
			if(this.xml!= null) {
        tmp= new TcManticFacturasArchivosDto(
          this.comprobante.getIdFactura(), // Long idFactura, 
          Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.xml.getRuta()), // String ruta, 
          null, // LocalDateTime cancelacion, 
          this.xml.getName(), // String nombre, 
          new Long(Calendar.getInstance().get(Calendar.YEAR)), // Long ejercicio, 
          null, // String folioCancelacion, 
          -1L, // Long idFacturaArchivo, 
          this.xml.getFileSize(), // Long tamanio, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          1L, // Long idTipoArchivo, 
          1L, // Long idPrincipal, 
          this.xml.getObservaciones(), // String observaciones, 
          Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.xml.getRuta()).concat(this.xml.getName()), // String alias, 
          new Long(this.comprobante.getTimbrado().getMonthValue()+ 1), // Long mes, 
          null, // String comentarios
          this.xml.getOriginal() // archivo
        );
        this.toSaveFile(sesion, this.xml.getIdArchivo());
				TcManticFacturasArchivosDto exists= (TcManticFacturasArchivosDto)DaoFactory.getInstance().toEntity(TcManticFacturasArchivosDto.class, "TcManticFacturasArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticFacturasArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
        this.toCheckDeleteFile(sesion, this.xml.getName());
				// this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.xml.getRuta()), ".".concat(this.xml.getFormat().name()), this.toListFile(sesion, Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.xml.getRuta()), this.xml.getName(), 1L));
			} // if	
			if(this.pdf!= null) {
        tmp= new TcManticFacturasArchivosDto(
          this.comprobante.getIdFactura(), // Long idFactura, 
          Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.pdf.getRuta()), // String ruta, 
          null, // LocalDateTime cancelacion, 
          this.pdf.getName(), // String nombre, 
          new Long(Calendar.getInstance().get(Calendar.YEAR)), // Long ejercicio, 
          null, // String folioCancelacion, 
          -1L, // Long idFacturaArchivo, 
          this.pdf.getFileSize(), // Long tamanio, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          2L, // Long idTipoArchivo, 
          1L, // Long idPrincipal, 
          this.pdf.getObservaciones(), // String observaciones, 
          Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.pdf.getRuta()).concat(this.pdf.getName()), // String alias, 
          new Long(this.comprobante.getTimbrado().getMonthValue()+ 1), // Long mes, 
          null, // String comentarios
          this.pdf.getOriginal() // archivo
        );
        this.toSaveFile(sesion, this.pdf.getIdArchivo());
				TcManticFacturasArchivosDto exists= (TcManticFacturasArchivosDto)DaoFactory.getInstance().toEntity(TcManticFacturasArchivosDto.class, "TcManticFacturasArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticFacturasArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
        this.toCheckDeleteFile(sesion, this.pdf.getName());
				// this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.pdf.getRuta()), ".".concat(this.pdf.getFormat().name()), this.toListFile(sesion, Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.pdf.getRuta()), this.pdf.getName(), 2L));
			} // if	
  	} // if	
	}

	public void toDeleteXmlPdf() throws Exception {
		List<TcManticFacturasArchivosDto> list= (List<TcManticFacturasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticFacturasArchivosDto.class, this.orden.toMap(), "all");
		if(list!= null)
			for (TcManticFacturasArchivosDto item: list) {
				LOG.info("Factura: "+ this.orden.getConsecutivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				// file.delete();
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
  
  protected void toCancelDeuda(Session sesion) throws Exception {
		TcManticClientesDeudasDto deuda= (TcManticClientesDeudasDto)DaoFactory.getInstance().toEntity(sesion, TcManticClientesDeudasDto.class, "TcManticClientesDeudasDto", "detalle", this.orden.toMap());		
    if(deuda!= null) {
      deuda.setIdClienteDeudaEstatus(5L); // CANCELADA
		  DaoFactory.getInstance().update(sesion, deuda);		
      TcManticClientesDeudasBitacoraDto registro= new TcManticClientesDeudasBitacoraDto(-1L, deuda.getIdClienteDeudaEstatus(), "", JsfBase.getIdUsuario(), deuda.getIdClienteDeuda());
      DaoFactory.getInstance().insert(sesion, registro);		
    } // if
    else
      LOG.error("NO EXISTE LA CUENTA POR COBRAR DE LA FACTURA ["+ this.orden.getConsecutivo()+ "]");
  }
  
  protected void toRecordDeuda(Session sesion) throws Exception {
		TcManticClientesDeudasDto deuda           = null;		
    TcManticClientesDeudasBitacoraDto registro= null;
    try {
      deuda= new TcManticClientesDeudasDto();
      deuda.setIdVenta(this.orden.getIdVenta());
      deuda.setIdCliente(this.orden.getIdCliente());
      deuda.setIdUsuario(JsfBase.getIdUsuario());
      deuda.setImporte(this.orden.getTotal());
      deuda.setSaldo(this.orden.getTotal());
      deuda.setRetencion1(this.orden.getRetencion1());
      deuda.setRetencion2(this.orden.getRetencion2());
      deuda.setRetencion3(this.orden.getRetencion3());
      deuda.setRetencion4(this.orden.getRetencion4());
      deuda.setRetencion5(this.orden.getRetencion5());
      deuda.setRetencion6(this.orden.getRetencion6());
      deuda.setRetencion7(this.orden.getRetencion7());
      //deuda.setLimite(this.toLimiteCredito(sesion));
      deuda.setLimite(this.comprobante.getVencimiento());
      deuda.setIdClienteDeudaEstatus(EEstatusClientesDeudas.INICIAL.getIdClienteDeudaEstatus()); // INICIADA
      DaoFactory.getInstance().insert(sesion, deuda);		
      registro= new TcManticClientesDeudasBitacoraDto(-1L, deuda.getIdClienteDeudaEstatus(), "", JsfBase.getIdUsuario(), deuda.getIdClienteDeuda());
      DaoFactory.getInstance().insert(sesion, registro);		
			TcManticClientesDto cliente= (TcManticClientesDto) DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, this.orden.getIdCliente());
			cliente.setSaldo(cliente.getSaldo()+ this.orden.getTotal());
			DaoFactory.getInstance().update(sesion, cliente);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
	} // registrarDeuda  

  protected boolean toUpdateDeuda(Session sesion) throws Exception {
    boolean regresar= false;
    TcManticClientesDeudasDto deuda           = null;
    TcManticClientesDeudasBitacoraDto registro= null;
    Map<String, Object> params                = null;
    try {
      params = new HashMap<>();      
      params.put("idVenta", this.orden.getIdVenta());      
      params.put("idCliente", this.orden.getIdCliente());      
      deuda = (TcManticClientesDeudasDto)DaoFactory.getInstance().toEntity(sesion, TcManticClientesDeudasDto.class, "TcManticClientesDeudasDto", "deudaVenta", params);
      deuda.setRetencion1(this.orden.getRetencion1());
      deuda.setRetencion2(this.orden.getRetencion2());
      deuda.setRetencion3(this.orden.getRetencion3());
      deuda.setRetencion4(this.orden.getRetencion4());
      deuda.setRetencion5(this.orden.getRetencion5());
      deuda.setRetencion6(this.orden.getRetencion6());
      deuda.setRetencion7(this.orden.getRetencion7());
      DaoFactory.getInstance().update(sesion, deuda);
      registro= new TcManticClientesDeudasBitacoraDto(-1L, deuda.getIdClienteDeudaEstatus(), "SE ACTUALIZARON LAS RETENCIONES", JsfBase.getIdUsuario(), deuda.getIdClienteDeuda());
      regresar= DaoFactory.getInstance().insert(sesion, registro)>= 1L;
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } // toUpdateDeuda  

	public LocalDate toLimiteCredito(Session sesion) throws Exception {
		TcManticClientesDto cliente= (TcManticClientesDto) DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, this.orden.getIdCliente());
		Long addDias= cliente.getPlazoDias();			
		LocalDate regresar= LocalDate.now();			
		regresar.plusDays(addDias.intValue());
		return regresar;
	} // toLimiteCredito
  
	private void toFillArticulos(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "TcManticFicticiasDetallesDto", "detalle", this.orden.toMap());
		for (Articulo item: todos) 
			if(this.articulos.indexOf(item)< 0)
				DaoFactory.getInstance().delete(sesion, item);
		for (Articulo articulo: this.articulos) {
			if(articulo.isValid()) {
				TcManticFicticiasDetallesDto item= articulo.toFicticiaDetalle();
				item.setIdFicticia(this.orden.getIdVenta());
				if(DaoFactory.getInstance().findIdentically(sesion, TcManticFicticiasDetallesDto.class, item.toMap())== null) 
					DaoFactory.getInstance().insert(sesion, item);
				else
					DaoFactory.getInstance().update(sesion, item);
			} // if
		} // for
	} // toFillArticulos
  
} 