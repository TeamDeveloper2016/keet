package mx.org.kaana.mantic.inventarios.entradas.reglas;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetNotasContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetNotasFacturasDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetNotasFacturasDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.reportes.FileSearch;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.reglas.Inventarios;
import mx.org.kaana.mantic.db.dto.TcManticEmpresasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticEmpresasDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticFaltantesDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.inventarios.entradas.beans.Nombres;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntradaProcess;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaLoteFamilia;
import mx.org.kaana.mantic.libs.factura.beans.ComprobanteFiscal;
import mx.org.kaana.mantic.libs.factura.beans.Concepto;
import mx.org.kaana.mantic.libs.factura.reglas.Reader;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends Inventarios implements Serializable {

  private static final Logger LOG = Logger.getLogger(Transaccion.class);
	private static final long serialVersionUID=-6069204157451117549L;
 
	protected TcManticNotasEntradasDto orden;	
	private List<Articulo> articulos;
	protected boolean aplicar;
	protected Importado xml;
	protected Importado pdf;
	protected String messageError;
	protected TcManticNotasBitacoraDto bitacora;
	private List<UISelectEntity> lotes;
	private List<UISelectEntity> familias;
  private ComprobanteFiscal factura;

	public Transaccion(TcManticNotasEntradasDto orden) {
		this(orden, new ArrayList<Articulo>(), false, null, null);
	}

	public Transaccion(TcManticNotasEntradasDto orden, TcManticNotasBitacoraDto bitacora) {
		this(orden);
		this.xml= null;
		this.pdf= null;
		this.bitacora= bitacora;
	}
  
	public Transaccion(TcManticNotasEntradasDto orden, List<Articulo> articulos, boolean aplicar, Importado xml, Importado pdf) {
		super(orden.getIdAlmacen(), orden.getIdProveedor());
		this.orden    = orden;		
		this.articulos= articulos;
		this.aplicar  = aplicar;
		this.xml      = xml;
		this.pdf      = pdf;
    this.factura  = null;
	} // Transaccion

	public Transaccion(NotaEntradaProcess notaProcess, boolean aplicar, Importado xml, Importado pdf) {
		this(notaProcess.getNotaEntrada(), notaProcess.getArticulos(), aplicar, xml, pdf);
		this.familias= notaProcess.getFamilias();
		this.lotes   = notaProcess.getLotes();
    this.factura = notaProcess.getFactura();
	} // Transaccion
  
	protected void setMessageError(String messageError) {
		this.messageError=messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                     = false;
		TcManticNotasBitacoraDto bitacoraNota= null;
		Map<String, Object> params           = new HashMap<>();
		Siguiente consecutivo                = null;
		try {
			if(!Objects.equals(this.orden, null)) {
				params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
				params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
        if(Objects.equals(-1L, this.orden.getIdBanco()))
          this.orden.setIdBanco(null);
        if(Objects.equals(-1L, this.orden.getIdTipoPago()))
          this.orden.setIdTipoPago(null);
			} // if
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la nota de entrada");
			switch(accion) {
				case MOVIMIENTOS:
					if(this.orden.isValid()) {
  					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
  					// this.toRemoveOrdenDetalle(sesion);
					} // if
					else {
					  consecutivo= this.toSiguiente(sesion);
					  this.orden.setConsecutivo(consecutivo.getConsecutivo());
					  this.orden.setOrden(consecutivo.getOrden());
					  this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					  if(this.orden.getIdNotaTipo().equals(1L))
						  this.orden.setIdOrdenCompra(null);
					  regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
					  bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
						regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
       	    this.toUpdateDeleteXml(sesion);	
					} // else	
					this.toFillArticulos(sesion);
      		for (Articulo articulo: this.articulos) 
						articulo.setModificado(false);
          this.registrarFamiliasLotes(sesion);
          this.toProcesarFacturas(sesion, this.orden.getIdNotaEntrada()); 
      		LOG.info("Se gener� de forma correcta la nota de entrada: "+ this.orden.getConsecutivo());
					break;
				case COMPLETO:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					this.orden.setIdOrdenCompra(null);					
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
					bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
  				if(this.aplicar) 
						this.toApplyNotaEntrada(sesion);
	   	    this.toUpdateDeleteXml(sesion);	
          this.registrarFamiliasLotes(sesion);
          this.toProcesarFacturas(sesion, this.orden.getIdNotaEntrada()); 
      		LOG.info("Se gener� de forma correcta la nota de entrada: "+ this.orden.getConsecutivo());
					break;
				case AGREGAR:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					if(this.orden.getIdNotaTipo().equals(1L))
						this.orden.setIdOrdenCompra(null);
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
					bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
          this.registrarFamiliasLotes(sesion);
					this.toFillArticulos(sesion);
					this.toCheckOrden(sesion);
     	    this.toUpdateDeleteXml(sesion);	
          this.toProcesarFacturas(sesion, this.orden.getIdNotaEntrada()); 
      		LOG.info("Se gener� de forma correcta la nota de entrada: "+ this.orden.getConsecutivo());
					break;
				case COMPLEMENTAR:
					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
  				if(this.aplicar) 
						this.toApplyNotaEntrada(sesion);
	   	    this.toUpdateDeleteXml(sesion);	
					break;				
				case MODIFICAR:
  				if(this.aplicar) {
						this.orden.setIdNotaEstatus(3L);
  					bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
	  				regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
					} // if	
					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
          this.registrarFamiliasLotes(sesion);
					// this.toRemoveOrdenDetalle(sesion);
					this.toFillArticulos(sesion);
					this.toCheckOrden(sesion);
     	    this.toUpdateDeleteXml(sesion);	
          this.toProcesarFacturas(sesion, this.orden.getIdNotaEntrada()); 
      		LOG.info("Se gener� de forma correcta la nota de entrada: "+ this.orden.getConsecutivo());
					break;				
				case ELIMINAR:
					regresar= this.toNotExistsArticulosBitacora(sesion);
					if(regresar) {
						this.toRemoveOrdenDetalle(sesion);
						regresar= DaoFactory.getInstance().deleteAll(sesion, TcManticNotasArchivosDto.class, params)>= 1L;
						regresar= DaoFactory.getInstance().deleteAll(sesion, TcManticNotasDetallesDto.class, params)>= 1L;
						regresar= DaoFactory.getInstance().delete(sesion, this.orden)>= 1L;
            this.orden.setIdNotaEstatus(2L);
						bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), 2L, this.orden.getConsecutivo(), this.orden.getTotal());
						regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
						this.toCheckOrden(sesion);
            this.toCheckDeleteFile(sesion);
       	    // this.toDeleteXmlPdf();	
            DaoFactory.getInstance().deleteAll(sesion, TcKeetNotasContratosLotesDto.class, this.orden.toMap());
					} // if
					else
       			this.messageError= "No se puede eliminar la nota de entrada porque ya fue aplicada en los precios de los articulos";
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdNotaEstatus(this.bitacora.getIdNotaEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
						if(this.bitacora.getIdNotaEstatus().equals(2L)) {
							this.toRemoveOrdenDetalle(sesion);
							regresar= DaoFactory.getInstance().deleteAll(sesion, TcManticNotasDetallesDto.class, params)>= 1L;
							regresar= DaoFactory.getInstance().delete(sesion, this.orden)>= 1L;
							this.orden.setIdNotaEstatus(2L);
							bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), 2L, this.orden.getConsecutivo(), this.orden.getTotal());
							regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
              this.toCheckOrden(sesion);
						} // if	
					} // if
					break;
				case GENERAR:
          regresar= this.toProcesarFacturas(sesion, this.orden.getIdNotaEntrada());
          break;
				case PROCESAR:
          regresar= this.toProcesarFacturas(sesion, this.orden.getIdNotaEntrada());
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

	private void toFillArticulos(Session sesion) throws Exception {
	  StringBuilder error = new StringBuilder();
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaNotasEntradasDto", "detalle", this.orden.toMap());
		Map<String, Object> params= new HashMap<>();
		try {
			for (Articulo item: todos) 
				if(this.articulos.indexOf(item)< 0) {
          this.toAffectOrdenDetalle(sesion, item);
					DaoFactory.getInstance().delete(sesion, TcManticNotasDetallesDto.class, item.getIdComodin());
        } // if  
			for (Articulo articulo: this.articulos) {
				TcManticNotasDetallesDto item= articulo.toNotaDetalle();
				item.setIdNotaEntrada(this.orden.getIdNotaEntrada());
				if(item.getDiferencia()!= 0)
					error.append("[").append(item.getNombre()!= null && item.getNombre().length()> 20? item.getNombre().substring(0, 20): item.getNombre()).append(" - ").append(item.getDiferencia()).append("]</br> ");
        if(articulo.isModificado())
				  this.toAffectOrdenDetalle(sesion, articulo, todos);
        if(articulo.getCantidad()> 0D || articulo.getCosto()> 0D) {
					if(!item.isValid()) 
						DaoFactory.getInstance().insert(sesion, item);
          else
      			if(articulo.isModificado())
	    				DaoFactory.getInstance().update(sesion, item);
					articulo.setObservacion("ARTICULO SURTIDO EN LA NOTA DE ENTRADA ".concat(this.orden.getConsecutivo()).concat(" EL DIA ").concat(Global.format(EFormatoDinamicos.FECHA_HORA_CORTA, this.orden.getRegistro())));
					// QUITAR DE LAS VENTAS PERDIDAS LOS ARTICULOS QUE FUERON YA SURTIDOS EN EL ALMACEN
					params.put("idArticulo", articulo.getIdArticulo());
					params.put("idEmpresa", this.orden.getIdEmpresa());
					params.put("observaciones", "ESTE ARTICULO FUE SURTIDO CON NO. NOTA DE ENTRADA "+ this.orden.getConsecutivo()+ " EL DIA "+ Global.format(EFormatoDinamicos.FECHA_HORA_CORTA, this.orden.getRegistro()));
					DaoFactory.getInstance().updateAll(sesion, TcManticFaltantesDto.class, params);
				} // if
			} // for
		} // try
		finally {
			Methods.clean(todos);
			Methods.clean(params);
		} // finally
	}

	private void toRemoveOrdenDetalle(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaNotasEntradasDto", "detalle", this.orden.toMap());
		for (Articulo articulo: todos) {
			if(articulo.getIdOrdenDetalle()!= null && articulo.getIdOrdenDetalle()> 0L) {
				TcManticOrdenesDetallesDto detalle= (TcManticOrdenesDetallesDto)DaoFactory.getInstance().findById(sesion, TcManticOrdenesDetallesDto.class, articulo.getIdOrdenDetalle());
				detalle.setCantidades(detalle.getCantidades()+ articulo.getCantidad());
				detalle.setImportes(Numero.toRedondearSat(detalle.getImportes()+ articulo.getImporte()));
				detalle.setPrecios(Numero.toRedondearSat(detalle.getCosto()- articulo.getCosto()));
				DaoFactory.getInstance().update(sesion, detalle);
			} // if
		} // for
	}
	
	private void toAffectOrdenDetalle(Session sesion, Articulo articulo) throws Exception {
		if(articulo.getIdOrdenDetalle()!= null && articulo.getIdOrdenDetalle()> 0L) {
			TcManticOrdenesDetallesDto detalle= (TcManticOrdenesDetallesDto)DaoFactory.getInstance().findById(sesion, TcManticOrdenesDetallesDto.class, articulo.getIdOrdenDetalle());
      detalle.setCantidades(detalle.getCantidades()+ articulo.getCantidad());
      detalle.setImportes(Numero.toRedondearSat(detalle.getImportes()+ articulo.getImporte()));
      detalle.setPrecios(Numero.toRedondearSat(articulo.getValor()- articulo.getCosto()));
  		DaoFactory.getInstance().update(sesion, detalle);
		} // if
	}
  
	private void toAffectOrdenDetalle(Session sesion, Articulo articulo, List<Articulo> todos) throws Exception {
    if(articulo.getIdOrdenDetalle()!= null && articulo.getIdOrdenDetalle()> 0L) {
      if(todos!= null && !todos.isEmpty()) {
        int index= todos.indexOf(articulo);
        if(index>= 0) {
          Articulo item= todos.get(index);
          TcManticOrdenesDetallesDto detalle= (TcManticOrdenesDetallesDto)DaoFactory.getInstance().findById(sesion, TcManticOrdenesDetallesDto.class, articulo.getIdOrdenDetalle());
          detalle.setCantidades(detalle.getCantidades()+ (item.getCantidad()- articulo.getCantidad()));
          if(!Objects.equals(item.getCantidad(), articulo.getCantidad())) {
            Double importe= 0D;
            if(!Objects.equals(item.getImporte(), articulo.getImporte()))
              importe= item.getImporte();
            detalle.setImportes(Numero.toRedondearSat(detalle.getImportes()+ (importe- articulo.getImporte())));
          } // if  
          detalle.setPrecios(Numero.toRedondearSat(articulo.getValor()- articulo.getCosto()));
          DaoFactory.getInstance().update(sesion, detalle);
        } // if
      } // if
    } // if
	}
	
	protected Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticNotasEntradasDto", "siguiente", params, "siguiente");
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
	
	protected void toCheckOrden(Session sesion) throws Exception {
		try {
			sesion.flush();
			if(this.orden.getIdNotaTipo().equals(2L)) {
        TcManticOrdenesComprasDto ordenCompra= (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(sesion, TcManticOrdenesComprasDto.class, this.orden.getIdOrdenCompra());
  		  Value errors= DaoFactory.getInstance().toField(sesion, "VistaNotasEntradasDto", "errores", this.orden.toMap(), "total");
			  if(errors.toLong()!= null && errors.toLong()== 0) {
				  ordenCompra.setIdOrdenEstatus(6L); // TERMINADA
					this.toApplyNotaEntrada(sesion);
				} // if	
				else {
					ordenCompra.setIdOrdenEstatus(5L); // INCOMPLETA
   				if(this.aplicar)
  					this.toApplyNotaEntrada(sesion);
				} // else	
				DaoFactory.getInstance().update(sesion, ordenCompra);
				TcManticOrdenesBitacoraDto estatus= new TcManticOrdenesBitacoraDto(ordenCompra.getIdOrdenEstatus(), "", JsfBase.getIdUsuario(), ordenCompra.getIdOrdenCompra(), -1L, ordenCompra.getConsecutivo(), this.orden.getTotal());
				DaoFactory.getInstance().insert(sesion, estatus);
			} // if
			else
			  if(this.orden.getIdNotaTipo().equals(1L) && this.aplicar) 
					this.toApplyNotaEntrada(sesion);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
	} 
	
	private boolean toNotExistsArticulosBitacora(Session sesion) throws Exception {
		boolean regresar= true;
		Value total= DaoFactory.getInstance().toField(sesion, "TcManticArticulosBitacoraDto", "existe", this.orden.toMap(), "total");
		if(total.getData()!= null)
		  regresar= total.toLong()<= 0;
		return regresar;
	}
	
	private void toApplyNotaEntrada(Session sesion) throws Exception {
		Map<String, Object> params= new HashMap<>();
		try {
			for (Articulo articulo: this.articulos) {
				TcManticNotasDetallesDto item= articulo.toNotaDetalle();
				item.setIdNotaEntrada(this.orden.getIdNotaEntrada());
				// Si la cantidad es mayor a cero realizar todo el proceso para el articulos, si no ignorarlo porque el articulo no se surtio
				if(articulo.getCantidad()> 0L)
					this.toAffectAlmacenes(sesion, this.orden.getConsecutivo(), this.orden.getIdNotaEntrada(), item, articulo);
			} // for
			this.orden.setIdNotaEstatus(3L);
			DaoFactory.getInstance().update(sesion, this.orden);

			// Una vez que la nota de entrada es cambiada a terminar se registra la cuenta por cobrar
			TcManticEmpresasDeudasDto deuda= null;
			if(Objects.equals(1L, this.orden.getIdTipoMedioPago()) && this.orden.getDiasPlazo()<= 1) // EFECTIVO 
				deuda= new TcManticEmpresasDeudasDto(3L, JsfBase.getIdUsuario(), -1L, "ESTE DEUDA FUE LIQUIDADA EN EFECTIVO", JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), this.orden.getDeuda(), this.orden.getIdNotaEntrada(), this.orden.getFechaPago(), this.orden.getDeuda(), this.orden.getDeuda()- this.orden.getExcedentes(), 2L, Cadena.isVacio(this.orden.getFactura())? 1L: 2L, null, null, null);
			else
				deuda= new TcManticEmpresasDeudasDto(1L, JsfBase.getIdUsuario(), -1L, "", JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), this.orden.getDeuda()- this.orden.getExcedentes(), this.orden.getIdNotaEntrada(), this.orden.getFechaPago(), this.orden.getDeuda(), this.orden.getDeuda()- this.orden.getExcedentes(), 2L, Cadena.isVacio(this.orden.getFactura())? 1L: 2L, null, null, null);
			DaoFactory.getInstance().insert(sesion, deuda);
      this.addSaldoProveedor(sesion, this.orden.getIdProveedor(), deuda.getSaldo());
      TcManticEmpresasBitacoraDto movimiento= new TcManticEmpresasBitacoraDto(
        "SE REGISTRO LA CUENTA POR PAGAR", // String justificacion, 
        deuda.getIdEmpresaEstatus(), // Long idEmpresaEstatus, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        deuda.getIdEmpresaDeuda(), // Long idEmpresaDeuda
        -1L // Long idEmpresaBitacora, 
      );
      DaoFactory.getInstance().insert(sesion, movimiento);
			params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
			params.put("idNotaEstatus", this.orden.getIdNotaEstatus());
			TcManticNotasBitacoraDto registro= (TcManticNotasBitacoraDto)DaoFactory.getInstance().findFirst(TcManticNotasBitacoraDto.class, "igual", params);
			if(registro== null) {
				registro= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
				DaoFactory.getInstance().insert(sesion, registro);
			} // if
			if(!this.orden.getIdNotaTipo().equals(3L))
				this.toCommonNotaEntrada(sesion, this.orden.getIdNotaEntrada(), this.orden.toMap());
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void toDeleteAll(String path, String type, List<Nombres> listado) {
    FileSearch fileSearch = new FileSearch();
    fileSearch.searchDirectory(new File(path), type.toLowerCase());
    if(fileSearch.getResult().size()> 0)
		  for (String matched: fileSearch.getResult()) {
				String name= matched.substring((matched.lastIndexOf("/")< 0? matched.lastIndexOf("\\"): matched.lastIndexOf("/"))+ 1);
				if(listado.indexOf(new Nombres(name))< 0) {
          LOG.warn("Nota cr�dito: "+ this.orden.getConsecutivo()+ " delete file: ".concat(matched));
				  File file= new File(matched);
				  // file.delete();
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
			regresar= (List<Nombres>)DaoFactory.getInstance().toEntitySet(sesion, Nombres.class, "TcManticNotasArchivosDto", "listado", params);
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
		//		this(idNotaArchivo, ruta, tamanio, idUsuario, idTipoArchivo, alias, mes, idNotaEntrada, nombre, observacion, ejercicio);
		TcManticNotasArchivosDto tmp= null;
		if(this.orden.getIdNotaEntrada()!= -1L) {
			if(this.xml!= null) {
				tmp= new TcManticNotasArchivosDto(
					-1L,
					this.xml.getRuta(),
					this.xml.getFileSize(),
					JsfBase.getIdUsuario(),
					1L,
					Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.xml.getRuta()).concat(this.xml.getName()),
					new Long(Calendar.getInstance().get(Calendar.MONTH)+ 1),
					this.orden.getIdNotaEntrada(),
					this.xml.getName(),
					this.xml.getObservaciones(),
					new Long(Calendar.getInstance().get(Calendar.YEAR)),
					1L,
					this.xml.getOriginal(),
          13L,
          2L
				);
        this.toSaveFile(this.xml.getIdArchivo());
				TcManticNotasArchivosDto exists= (TcManticNotasArchivosDto)DaoFactory.getInstance().toEntity(TcManticNotasArchivosDto.class, "TcManticNotasArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticNotasArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
        this.toCheckDeleteFile(sesion, this.xml.getName());
				// this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.xml.getRuta()), ".".concat(this.xml.getFormat().name()), this.toListFile(sesion, this.xml, 1L));
			} // if	
			if(this.pdf!= null) {
				tmp= new TcManticNotasArchivosDto(
					-1L,
					this.pdf.getRuta(),
					this.pdf.getFileSize(),
					JsfBase.getIdUsuario(),
					2L,
					Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.pdf.getRuta()).concat(this.pdf.getName()),
					new Long(Calendar.getInstance().get(Calendar.MONTH)+ 1),
					this.orden.getIdNotaEntrada(),
					this.pdf.getName(),
					this.pdf.getObservaciones(),
					new Long(Calendar.getInstance().get(Calendar.YEAR)),
					1L,
					this.pdf.getOriginal(),
          13L,
          2L
				);
        this.toSaveFile(this.pdf.getIdArchivo());
				TcManticNotasArchivosDto exists= (TcManticNotasArchivosDto)DaoFactory.getInstance().toEntity(TcManticNotasArchivosDto.class, "TcManticNotasArchivosDto", "identically", tmp.toMap());
				File file= new File(tmp.getAlias());
				if(exists== null && file.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticNotasArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else
				  if(!file.exists())
						LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
				sesion.flush();
        this.toCheckDeleteFile(sesion, this.pdf.getName());
				// this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.pdf.getRuta()), ".".concat(this.pdf.getFormat().name()), this.toListFile(sesion, this.pdf, 2L));
			} // if	
  	} // if	
	}

	public void toDeleteXmlPdf() throws Exception {
		List<TcManticNotasArchivosDto> list= (List<TcManticNotasArchivosDto>)DaoFactory.getInstance().findViewCriteria(TcManticNotasArchivosDto.class, this.orden.toMap(), "all");
		if(list!= null)
			for (TcManticNotasArchivosDto item: list) {
				LOG.info("Nota entrada: "+ this.orden.getConsecutivo()+ " delete file: "+ item.getAlias());
				File file= new File(item.getAlias());
				// file.delete();
			} // for
	}	

	private boolean registrarFamiliasLotes(Session sesion) throws Exception {
		boolean regresar                         = true;
		TcKeetNotasContratosLotesDto contratoLote= null;
		try {
      List<NotaLoteFamilia> items= (List<NotaLoteFamilia>)DaoFactory.getInstance().toEntitySet(sesion, NotaLoteFamilia.class, "TcKeetNotasContratosLotesDto", "lotes", this.orden.toMap());
      if(this.lotes!= null && this.familias!= null) {
        for(Object lote: this.lotes) {
          for(Object familia: this.familias) {
            contratoLote= new TcKeetNotasContratosLotesDto();
            contratoLote.setIdContratoLote(((UISelectEntity)lote).getKey());
            contratoLote.setIdFamilia(((UISelectEntity)familia).getKey());
            contratoLote.setIdNotaEntrada(this.orden.getIdNotaEntrada());
            contratoLote.setIdUsuario(JsfBase.getIdUsuario());
            NotaLoteFamilia existe= new NotaLoteFamilia(-1L, ((UISelectEntity)familia).getKey(), ((UISelectEntity)lote).getKey(), this.orden.getIdNotaEntrada());
            int index= items.indexOf(existe);
            if(index< 0) {
              DaoFactory.getInstance().insert(sesion, contratoLote);
              existe.setExiste(Boolean.TRUE);
              items.add(existe);
            } // if  
            else
              items.get(index).setExiste(Boolean.TRUE);
          } // for
        } // for
        for (NotaLoteFamilia item: items) {
          if(!item.getExiste())
            DaoFactory.getInstance().delete(sesion, TcKeetNotasContratosLotesDto.class, item.getIdNotaContratoLote());
        } // for
      } // if  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // registrarFamiliasLotes  
  
  private void addSaldoProveedor(Session sesion, Long idProveedor, Double saldo) throws Exception {
    try {      
      TcManticProveedoresDto proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(sesion, TcManticProveedoresDto.class, idProveedor);
      if(proveedor!= null) {
        proveedor.setSaldo(proveedor.getSaldo()+ saldo);
        DaoFactory.getInstance().update(sesion, proveedor);
      } // if
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
  }
  
  private boolean registrarFactura(Session sesion, Long idNotaEntrada) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {      
      TcKeetNotasFacturasDto item= new TcKeetNotasFacturasDto(
        -1L, // Long idNotaFactura, 
        null, // String cadenaOriginal, 
        this.factura.getTimbreFiscalDigital().getUuid(), // String folioFiscal, 
        this.factura.getSello(), // String selloDigital, 
        this.factura.getFormaPago(), // String formaPago, 
        this.factura.getEmisor().getNombre(), // String razonSocial, 
        idNotaEntrada, // Long idNotaEntrada, 
        this.factura.getLugarExpedicion(), // String lugarExpedicion, 
        this.factura.getVersion(), // String version, 
        this.factura.getTimbreFiscalDigital().getSelloCfd(), // String certificado, 
        this.factura.getEmisor().getRfc(), // String rfc, 
        Numero.getDouble(this.factura.getTotal(), 0D), // Double total, 
        this.factura.getFolio(), // String factura, 
        Fecha.toLocalDateTime(this.factura.getTimbreFiscalDigital().getFechaTimbrado()), // LocalDateTime timbrado, 
        Numero.getDouble(this.factura.getImpuesto().getTotalImpuestosTrasladados(), 0D), // Double iva, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        Numero.getDouble(this.factura.getSubTotal(), 0D), // Double subtotal, 
        this.factura.getSerie(), // String serie, 
        this.factura.getMoneda(), // String moneda, 
        this.factura.getTipoCambio(), // String tipoCambio, 
        this.factura.getMetodoPago(), // String metodoPago, 
        this.factura.getEmisor().getRegimenFiscal() // String regimen
      );
      TcKeetNotasFacturasDto existe= (TcKeetNotasFacturasDto)DaoFactory.getInstance().findIdentically(sesion, TcKeetNotasFacturasDto.class, item.toMap());
      if(Objects.equals(existe, null))
        DaoFactory.getInstance().insert(sesion, item);
      else {
        item.setIdNotaFactura(existe.getIdNotaFactura());
        DaoFactory.getInstance().update(sesion, item);
      } // if  
      for (Concepto concepto: this.factura.getConceptos()) {
        TcKeetNotasFacturasDetallesDto detalle= new TcKeetNotasFacturasDetallesDto(
          concepto.getDescripcion(), // String descripcion, 
          null, // String codigo, 
          item.getIdNotaFactura(), // Long idNotaFactura, 
          concepto.getUnidad(), // String unidadMedida, 
          concepto.getDescuento(), // String descuento, 
          concepto.getClaveProdServ(), // String sat, 
          -1L, // Long idNotaFacturaDetalle, 
          Numero.getDouble(concepto.getValorUnitario(), 0D), // Double unitario, 
          Numero.getDouble(concepto.getTraslado().getBase(), 0D), // Double total, 
          concepto.getClaveUnidad(), // String claveUnidad, 
          Numero.getDouble(concepto.getTraslado().getImporte(), 0D), // Double iva, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          Numero.getDouble(concepto.getTraslado().getBase(), 0D)- Numero.getDouble(concepto.getTraslado().getImporte(), 0D), // Double subtotal, 
          Numero.getDouble(concepto.getCantidad(), 0D), // Double cantidad, 
          concepto.getNoIdentificacion() // String identificador     
        ); 
        TcKeetNotasFacturasDetallesDto encontrado= (TcKeetNotasFacturasDetallesDto)DaoFactory.getInstance().findIdentically(sesion, TcKeetNotasFacturasDetallesDto.class, detalle.toMap());
        if(Objects.equals(encontrado, null))
          DaoFactory.getInstance().insert(sesion, detalle);
        else {
          detalle.setIdNotaFacturaDetalle(encontrado.getIdNotaFacturaDetalle());
          DaoFactory.getInstance().update(sesion, detalle);
        } // if  
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
    return regresar;
  }

  protected boolean toProcesarFacturas(Session sesion, Long idNotaEntrada) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Reader reader             = null;
    Map<String, Object> params= new HashMap<>();
    Monitoreo monitoreo       = JsfBase.getAutentifica().getMonitoreo();
    try {      
      sesion.flush();
      monitoreo.comenzar(0L);
      params.put("idNotaEntrada", idNotaEntrada);      
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaFacturasDto", params, Constantes.SQL_TODOS_REGISTROS);
      if(!Objects.equals(items, null) && !items.isEmpty()) {
  			monitoreo.setTotal(new Long(items.size()));
	  		monitoreo.setId("PROCESAR NOTAS DE ENTRADA");
        LOG.error("INICIANDO.....................................................................");
        for (Entity item: items) {
          try {
            File file= new File(item.toString("alias"));
            if(file.exists()) {
              reader= new Reader(file.getAbsolutePath());
              this.factura= reader.execute();
              Entity exist= (Entity)DaoFactory.getInstance().toEntity(sesion, "VistaFacturasDto", "exists", params);
              // SI LA FACTURA EXISTE PARA ESTA NOTA DE ENTRADA PERO ES DIFERENTE LA FACTURA ENTONCES ELIMINAR LA FACTURA
              if(!Objects.equals(exist, null) && !exist.isEmpty() && 
                 !Objects.equals(exist.toString("factura"), item.toString("factura")) && 
                 !Objects.equals(exist.toString("rfc"), item.toString("rfc"))) {
                DaoFactory.getInstance().deleteAll(sesion, TcKeetNotasFacturasDto.class, params);
                DaoFactory.getInstance().deleteAll(sesion, TcKeetNotasFacturasDetallesDto.class, params);
              } // if
              this.registrarFactura(sesion, item.toLong("idNotaEntrada"));
            } // if  
            else 
              LOG.error("EL ARCHIVO NO EXISTE [".concat(item.toString("alias")).concat("]"));
            monitoreo.incrementar();
            LOG.error("PRECESADOS: "+ monitoreo.getProgreso()+ " de "+ monitoreo.getTotal());
          } // try
          catch(Exception e ) {
            LOG.error("Error: "+ e);
          } // catch
        } // for
        LOG.error("TERMNINADO.......................................................");
      } // if
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;      
    } // catch	
    finally {
      monitoreo.terminar();
			monitoreo.setProgreso(0L);			
			Methods.clean(params);
		} // finally    
    return regresar;
  }
  
} 