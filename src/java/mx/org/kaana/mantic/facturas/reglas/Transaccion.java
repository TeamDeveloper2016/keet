package mx.org.kaana.mantic.facturas.reglas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.operation.IActions;
import mx.org.kaana.kajool.db.comun.operation.Insert;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import static mx.org.kaana.kajool.enums.ESql.DELETE;
import static mx.org.kaana.kajool.enums.ESql.INSERT;
import static mx.org.kaana.kajool.enums.ESql.SELECT;
import static mx.org.kaana.kajool.enums.ESql.UPDATE;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoDomicilio;
import mx.org.kaana.keet.db.dto.TcManticClientesDeudasBitacoraDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.facturama.reglas.CFDIFactory;
import mx.org.kaana.libs.facturama.reglas.CFDIGestor;
import mx.org.kaana.libs.facturama.reglas.Facturama;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.KajoolBaseException;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.beans.ClienteTipoContacto;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesPagosDto;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticFacturasDto;
import mx.org.kaana.mantic.db.dto.TcManticFicticiasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticFicticiasDto;
import mx.org.kaana.mantic.db.dto.TcManticFicticiasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteTipoContactoDto;
import mx.org.kaana.mantic.enums.EEstatusFacturas;
import mx.org.kaana.mantic.enums.EEstatusFicticias;
import mx.org.kaana.mantic.enums.EEstatusVentas;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.facturas.beans.ClienteFactura;
import mx.org.kaana.mantic.facturas.beans.Correo;
import mx.org.kaana.mantic.facturas.beans.Documento;
import mx.org.kaana.mantic.facturas.beans.FacturaFicticia;
import mx.org.kaana.mantic.facturas.beans.Parcial;
import mx.org.kaana.mantic.facturas.enums.EEstatusClientesDeudas;
import mx.org.kaana.mantic.facturas.enums.ETiposComprobantes;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends Facturama {

  private static final Logger LOG    = Logger.getLogger(Transaccion.class);
	private TcManticFicticiasBitacoraDto bitacora;
	private FacturaFicticia orden;	
	private Long idFicticia;
	private List<Articulo> articulos;
	private String messageError;	
	private String justificacion;
	private String correos;
	private String comentarios;	
	private Correo correo;
	private Long idCliente;

	public Transaccion(Correo correo, Long idCliente) {
		this.correo   = correo;
		this.idCliente= idCliente;
	}	// Transaccion
	
	public Transaccion(TcManticFicticiasBitacoraDto bitacora) { 
		this(new FacturaFicticia(), bitacora, "", "");
	} // Transaccion
	
	public Transaccion(FacturaFicticia orden, TcManticFicticiasBitacoraDto bitacora, String correos, String comentarios) {
    this.orden      = orden;
		this.bitacora   = bitacora;
		this.correos    = correos;
		this.comentarios= comentarios;
	} // Transaccion
	
	public Transaccion(FacturaFicticia orden) {
		this(orden, "");
	}
	
	public Transaccion(FacturaFicticia orden, String justificacion) {
		this(orden, new ArrayList<Articulo>(), justificacion);
	} // Transaccion

	public Transaccion(FacturaFicticia orden, List<Articulo> articulos) {		
		this(orden, articulos, "");
	}
	
	public Transaccion(FacturaFicticia orden, List<Articulo> articulos, String justificacion) { 		
		this.orden        = orden;		
		this.articulos    = articulos;
		this.justificacion= justificacion;
	} // Transaccion
	
	public Transaccion(Long idFicticia) {
		this.idFicticia= idFicticia;
	}
	
	public String getMessageError() {
		return messageError;
	} // Transaccion

	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}	
	
	public TcManticFicticiasDto getOrden() {
		return orden;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar             = false;
		Map<String, Object> params   = null;
		Long idEstatusFactura        = null;
		TcManticFacturasDto factura  = null;
		TcManticFicticiasDto ficticia= null;
		Long idFactura               = -1L;
		try {
			idEstatusFactura= EEstatusFicticias.ABIERTA.getIdEstatusFicticia();
			params= new HashMap<>();
			if(this.orden!= null)
				params.put("idFicticia", this.orden.getIdFicticia());
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la factura.");
			switch(accion) {				
				case COPIAR:
					regresar= this.toClonarFiciticia(sesion, idEstatusFactura);
					break;
				case AGREGAR:
				case REGISTRAR:	
				case DESACTIVAR:
					idEstatusFactura= accion.equals(EAccion.AGREGAR) ? EEstatusFicticias.ABIERTA.getIdEstatusFicticia() : (accion.equals(EAccion.DESACTIVAR) ? this.orden.getIdFicticiaEstatus() : idEstatusFactura);
					regresar= this.orden.getIdFicticia()!= null && !this.orden.getIdFicticia().equals(-1L) ? this.actualizarFicticia(sesion, idEstatusFactura) : this.registrarFicticia(sesion, idEstatusFactura);					
					break;
				case MODIFICAR:
					regresar= actualizarFicticia(sesion, EEstatusFicticias.ABIERTA.getIdEstatusFicticia());					
					break;				
				case ELIMINAR:
					idEstatusFactura= EEstatusFicticias.CANCELADA.getIdEstatusFicticia();
					this.orden= (FacturaFicticia) DaoFactory.getInstance().toEntity(sesion, FacturaFicticia.class, "TcManticFicticiasDto", "detalle", this.orden.toMap());
					this.orden.setIdFicticiaEstatus(idEstatusFactura);					
					if(DaoFactory.getInstance().update(sesion, this.orden)>= 1L)
						regresar= registraBitacora(sesion, this.orden.getIdFicticia(), idEstatusFactura, this.justificacion);					
					break;
				case JUSTIFICAR:		
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdFicticiaEstatus(this.bitacora.getIdFicticiaEstatus());						
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
						if((this.bitacora.getIdFicticiaEstatus().equals(EEstatusFicticias.TIMBRADA.getIdEstatusFicticia()) || this.bitacora.getIdFicticiaEstatus().equals(EEstatusVentas.TERMINADA.getIdEstatusVenta())) && this.checkTotal(sesion)) {
							params.put("idVenta", this.orden.getIdVenta());
							factura= (TcManticFacturasDto) DaoFactory.getInstance().toEntity(sesion, TcManticFacturasDto.class, "VistaFicticiasDto", "factura", params);
							if(factura!= null) {
								params.put("correos", this.correos);
								params.put("comentarios", this.comentarios);								
								params.put("timbrado", LocalDateTime.now());		
								params.put("intentos", (factura.getIntentos()+1L));
								DaoFactory.getInstance().update(sesion, TcManticFacturasDto.class, factura.getIdFactura(), params);
								this.generarTimbradoFactura(sesion, factura.getIdFactura(), this.correos);
							} // if
						} // if
						else 
							if(this.bitacora.getIdFicticiaEstatus().equals(EEstatusFicticias.CANCELADA.getIdEstatusFicticia()) || this.bitacora.getIdFicticiaEstatus().equals(EEstatusVentas.ELIMINADA.getIdEstatusVenta())) {
								params.put("idVenta", this.orden.getIdVenta());
								factura= (TcManticFacturasDto) DaoFactory.getInstance().toEntity(sesion, TcManticFacturasDto.class, "VistaFicticiasDto", "factura", params);
								if(factura!= null && factura.getIdFacturama()!= null) {
									CFDIFactory.getInstance().cfdiRemove(factura.getIdFacturama());
									factura.setCancelada(LocalDateTime.now());
									regresar= DaoFactory.getInstance().update(sesion, factura)>= 0;
                  this.cancelDocumentosPagos(sesion);
								} // if
								else
									throw new Exception("No fue posible cancelar el documento, por favor vuelva a intentarlo !");															
							} // else if
					} // if
					break;								
				case REPROCESAR:
					regresar= this.actualizarFicticia(sesion, EEstatusFicticias.TIMBRADA.getIdEstatusFicticia());				
					break;		
				case NO_APLICA:
					params.put("idFicticia", this.orden.getIdFicticia());
					if(DaoFactory.getInstance().deleteAll(sesion, TcManticFicticiasBitacoraDto.class, params)>= 0) {
						if(DaoFactory.getInstance().deleteAll(sesion, TcManticFicticiasDetallesDto.class, params)>= 0)
							regresar= DaoFactory.getInstance().delete(sesion, this.orden)>= 1L;
					} // if					
					break;
				case COMPLEMENTAR: 
					regresar= this.agregarContacto(sesion);
					break;
				case DEPURAR:
					this.messageError= "Ocurrio un error al cancelar la factura.";
					params= new HashMap<>();
					params.put("idFactura", this.orden.getIdFactura());
					factura= (TcManticFacturasDto)DaoFactory.getInstance().toEntity(sesion, TcManticFacturasDto.class, "TcManticFacturasDto", "detalle", params);
					if(factura!= null && factura.getIdFacturama()!= null) {
						CFDIFactory.getInstance().cfdiRemove(factura.getIdFacturama());
						factura.setCancelada(LocalDateTime.now());
						factura.setIdFacturaEstatus(EEstatusFacturas.CANCELADA.getIdEstatusFactura());
						regresar= DaoFactory.getInstance().update(sesion, factura)>= 0;
						this.registrarBitacoraFactura(sesion, factura.getIdFactura(), EEstatusFacturas.CANCELADA.getIdEstatusFactura(), "CANCELADA ".concat(this.justificacion));
            this.cancelDocumentosPagos(sesion);
					} // if
					else
						throw new Exception("No fue posible cancelar la factura, por favor vuelva a intentarlo !");															
				  break;
				case ACTIVAR:
					ficticia= (TcManticFicticiasDto)DaoFactory.getInstance().findById(sesion, TcManticFicticiasDto.class, this.idFicticia);
					if(ficticia!= null) {
						ficticia.setIdTipoDocumento(1L);
						ficticia.setIdFicticiaEstatus(EEstatusFicticias.ABIERTA.getIdEstatusFicticia());
						if(DaoFactory.getInstance().update(sesion, ficticia)> 0){
							idFactura= registrarFactura(sesion);
							ficticia.setIdFactura(idFactura);
							if(DaoFactory.getInstance().update(sesion, ficticia)> 0){
								TcManticFicticiasBitacoraDto bitFicticia= new TcManticFicticiasBitacoraDto(ficticia.getTicket(), "Se cambio la cotización especial para timbrarse", EEstatusFicticias.ABIERTA.getIdEstatusFicticia(), JsfBase.getIdUsuario(), this.idFicticia, -1L, ficticia.getTotal());
								regresar= DaoFactory.getInstance().insert(sesion, bitFicticia)>= 1L;
							} // if
						} // if
					} // if
					break;
				case PROCESAR:
					ficticia= (TcManticFicticiasDto)DaoFactory.getInstance().findById(sesion, TcManticFicticiasDto.class, this.idFicticia);
					if(ficticia!= null && ficticia.isValid()){
						if(ficticia.getIdFactura()!= null && ficticia.getIdFactura()>= 1L){
							factura= (TcManticFacturasDto) DaoFactory.getInstance().findById(sesion, TcManticFacturasDto.class, ficticia.getIdFactura());
							if(factura.getIdFacturaEstatus().equals(EEstatusFacturas.REGISTRADA.getIdEstatusFactura())){
								factura.setIdFacturaEstatus(EEstatusFacturas.AUTOMATICO.getIdEstatusFactura());
								regresar= DaoFactory.getInstance().update(sesion, factura)>= 1L;
							} // if
						} // if
						else{
							idFactura= registrarFactura(sesion, EEstatusFacturas.AUTOMATICO.getIdEstatusFactura());						
							ficticia.setIdFactura(idFactura);
							regresar= DaoFactory.getInstance().update(sesion, ficticia)>= 1L;
						} // else
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
		finally {
			Methods.clean(params);
		} // finally
		if(this.orden!= null)
			LOG.info("Se genero de forma correcta la orden: "+ this.orden.getConsecutivo());
		return regresar;
	}	// ejecutar
	
	private boolean registrarFicticia(Session sesion, Long idEstatusFicticia) throws Exception {
		boolean regresar         = false;
		Siguiente consecutivo    = null;
		Siguiente cuenta         = null;
		Long idFactura           = -1L;
		try {									
			idFactura= this.registrarFactura(sesion);										
			if(idFactura>= 1L) {
				consecutivo= this.toSiguiente(sesion);			
        if(this.getOrden().getIdContrato()!= null && this.getOrden().getIdContrato()<= 0)
          this.getOrden().setIdContrato(null);
				this.orden.setIdExtra(this.getOrden().getIdContrato()== null? 1L: 2L);			
				this.orden.setTicket(consecutivo.getConsecutivo());			
				this.orden.setCticket(consecutivo.getOrden());			
				this.orden.setConsecutivo(consecutivo.getOrden());			
				cuenta= this.toSiguienteCuenta(sesion);			
				this.orden.setOrden(cuenta.getOrden());
				this.orden.setIdFicticiaEstatus(idEstatusFicticia);
				this.orden.setEjercicio(new Long(Fecha.getAnioActual()));						
				this.orden.setIdFactura(idFactura);
				if(DaoFactory.getInstance().insert(sesion, this.orden)>= 1L) {
  				regresar= this.registraBitacora(sesion, this.orden.getIdFicticia(), idEstatusFicticia, "");
					this.toFillArticulos(sesion);
				} // if
        this.checkContratoDomicilio(sesion);
        this.checkComplementoPago(sesion);
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // registrarFicticia
	
	private boolean actualizarFicticia(Session sesion, Long idEstatusFicticia) throws Exception{
		boolean regresar           = false;
		Map<String, Object>params  = null;
		TcManticFacturasDto factura= null;
		try {						
			this.orden.setIdFicticiaEstatus(idEstatusFicticia);						
			regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
			params= new HashMap<>();
			params.put("idVenta", this.orden.getIdVenta());
			factura= (TcManticFacturasDto) DaoFactory.getInstance().toEntity(sesion, TcManticFacturasDto.class, "VistaFicticiasDto", "factura", params);
			factura.setObservaciones(this.justificacion);
			if(DaoFactory.getInstance().update(sesion, factura)>= 1L){
				if(registraBitacora(sesion, this.orden.getIdFicticia(), idEstatusFicticia, "")) {
					params= new HashMap<>();
					params.put("idFicticia", this.orden.getIdFicticia());
					regresar= DaoFactory.getInstance().deleteAll(sesion, TcManticFicticiasDetallesDto.class, params)>= 0;
					this.toFillArticulos(sesion);
				} // if
			} // if
      this.checkContratoDomicilio(sesion);
      this.checkComplementoPago(sesion);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally			
		return regresar;
	} // actualizarFicticia
	
	protected boolean registraBitacora(Session sesion, Long idFicticia, Long idFicticaEstatus, String justificacion) throws Exception {
		TcManticFicticiasBitacoraDto bitFicticia= new TcManticFicticiasBitacoraDto(this.orden.getTicket(), justificacion, idFicticaEstatus, JsfBase.getIdUsuario(), idFicticia, -1L, this.orden.getTotal());
		return DaoFactory.getInstance().insert(sesion, bitFicticia)>= 1L;
	} // registrarBitacora
	
	private void toFillArticulos(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "TcManticFicticiasDetallesDto", "detalle", this.orden.toMap());
    try {
      for (Articulo item: todos) 
        if(this.articulos.indexOf(item)< 0)
          DaoFactory.getInstance().delete(sesion, item);
      for (Articulo articulo: this.articulos) {
        if(articulo.isValid()) {
          TcManticFicticiasDetallesDto item= articulo.toFicticiaDetalle();
          item.setIdFicticia(this.orden.getIdFicticia());
          if(DaoFactory.getInstance().findIdentically(sesion, TcManticFicticiasDetallesDto.class, item.toMap())== null) 
            DaoFactory.getInstance().insert(sesion, item);
          else
            DaoFactory.getInstance().update(sesion, item);
        } // if
      } // for
    } // try
    catch(Exception e) {
      throw e;
    } // catch
    this.toFillParciales(sesion);
	} // toFillArticulos
	
  private void toFillParciales(Session sesion) throws Exception {
    try {
      if(this.orden.getParciales()!= null && !this.orden.getParciales().isEmpty())
        for (Parcial item: this.orden.getParciales()) {
          switch(item.getSqlAccion()) {
            case INSERT:
              item.setIdVenta(this.orden.getIdVenta());
              item.setIdUsuario(JsfBase.getIdUsuario());
              DaoFactory.getInstance().insert(sesion, item);
              break;
            case UPDATE:
              DaoFactory.getInstance().update(sesion, item);
              break;
            case DELETE:
              DaoFactory.getInstance().delete(sesion, item);
              break;
            case SELECT:
              break;
          } // switch
        } // for
    } // try
    catch(Exception e) {
      throw e;
    } // catch
  }
  
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticFicticiasDto", "siguiente", params, "siguiente");
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
	} // toCuenta
	
	private Long registrarFactura(Session sesion) throws Exception {
		return registrarFactura(sesion, EEstatusFacturas.REGISTRADA.getIdEstatusFactura());
	} // registrarFactura
	
	private Long registrarFactura(Session sesion, Long idEstatus) throws Exception {
		Long regresar              = -1L;
		TcManticFacturasDto factura= null;
		try {			
			factura= new TcManticFacturasDto();
			factura.setIdFacturaEstatus(idEstatus);
			factura.setIdUsuario(JsfBase.getIdUsuario());
			factura.setIntentos(0L);
			factura.setCorreos("");
			factura.setObservaciones(this.justificacion);
			regresar= DaoFactory.getInstance().insert(sesion, factura);
		} // try
		finally {
			setMessageError("Error al registrar la factura.");
		} // finally
		return regresar;
	} // registrarFactura
	
	private boolean agregarContacto(Session sesion) throws Exception{
		boolean regresar                       = true;
		List<ClienteTipoContacto> correos      = null;
		TrManticClienteTipoContactoDto contacto= null;
		int count                              = 0;
		Long records                           = 1L;
		try {
			correos= toClientesTipoContacto(sesion);
			if(!correos.isEmpty()){
				for(ClienteTipoContacto tipoContacto: correos){
					if(tipoContacto.getValor().equals(this.correo.getDescripcion()))
						count++;
				} // for				
				records= correos.size() + 1L;
			} // if
			if(count== 0){
				contacto= new TrManticClienteTipoContactoDto();
				contacto.setIdCliente(this.idCliente);
				contacto.setIdTipoContacto(ETiposContactos.CORREO.getKey());
				contacto.setIdUsuario(JsfBase.getIdUsuario());
				contacto.setValor(this.correo.getDescripcion());
				contacto.setOrden(records);
				regresar= DaoFactory.getInstance().insert(sesion, contacto)>= 1L;
			} // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // agregarContacto
	
	public List<ClienteTipoContacto> toClientesTipoContacto(Session sesion) throws Exception {
		List<ClienteTipoContacto> regresar= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cliente=" + this.idCliente + " and id_tipo_contacto=" + ETiposContactos.CORREO.getKey());
			regresar= DaoFactory.getInstance().toEntitySet(sesion, ClienteTipoContacto.class, "TrManticClienteTipoContactoDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toClientesTipoContacto
	
	private void generarTimbradoFactura(Session sesion, Long idFactura, String correos) throws Exception {
		Facturama factura= null;
		CFDIGestor gestor= null;
		try {
			this.actualizarClienteFacturama(sesion, this.orden.getIdFicticia());
			gestor = new CFDIGestor(this.orden.getIdFicticia());			
			factura= new Facturama(gestor.toClienteCfdiFicticia(sesion));
			factura.getCliente().setIdFactura(idFactura);
      if(Objects.equals(ETiposComprobantes.COMPLEMENTO_PAGO.getIdTipoComprobante(), this.orden.getIdTipoComprobante())) {
  			factura.setComplemento(gestor.toClienteComplemento(sesion));
  			factura.setDocumentos(gestor.toDocumentosCfdi(sesion));
  			// factura.generarComplemento(sesion);	
        this.toCheckFacturasPagos(sesion, factura.getDocumentos());
      } // else 
      else {
  			factura.setArticulos(gestor.toArticulosCfdi(sesion));
			  // factura.generarFactura(sesion);	
        this.toRecordDeuda(sesion, this.orden.getTotal());
      } // if  
			try {
				// CFDIFactory.getInstance().toSendMail(correos, factura.getIdFacturamaRegistro());
			} // try
			catch (Exception e) {				
				Error.mensaje(e);				
			} // catch
		} // try 
		catch (Exception e) {			
			this.messageError= "";
			throw e;
		} // catch				
	} // generarTimbradoFactura

	private void actualizarClienteFacturama(Session sesion, Long idFicticia) throws Exception{		
		CFDIGestor gestor= new CFDIGestor(idFicticia);
		ClienteFactura cliente= gestor.toClienteFacturaUpdate(sesion);
		setCliente(cliente);
		if(cliente.getIdFacturama()!= null)
			this.updateCliente(sesion);
		else
			super.procesarCliente(sesion);		
	} // actualizarArticuloFacturama
	
	private boolean checkTotal(Session sesion) throws Exception {
		boolean regresar= false;
		Double value    = 0D;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idVenta", this.orden.getIdFicticia());
			Value detalle= null;
      if(Objects.equals(this.orden.getIdTipoComprobante(), ETiposComprobantes.COMPLEMENTO_PAGO.getIdTipoComprobante()))
        detalle= DaoFactory.getInstance().toField(sesion, "TcKeetVentasPagosDto", "total", params, "total");
      else
        detalle= DaoFactory.getInstance().toField(sesion, "TcManticFicticiasDetallesDto", "total", params, "total");
			if(detalle!= null && detalle.getData()!= null)
				value= detalle.toDouble();
		} // try
		finally {
			Methods.clean(params);
		} // finally
		regresar= Objects.equals(this.orden.getTotal(), value);
		if(!regresar) {
			LOG.warn("Diferencias en los importes del documento: "+ this.orden.getIdFicticia()+ " verificar situacion, total ["+ this.orden.getTotal()+ "] detalle["+ value+ "]");
			throw new KajoolBaseException("No se puede timbrar porque el importe total difiere de los importes del detalle del documento !");	
		} // if	
		return regresar;
	}
	
	private boolean toClonarFiciticia(Session sesion, Long idEstatusFicticia) throws Exception { 
		boolean regresar          = false;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			Siguiente cuenta     = this.toSiguienteCuenta(sesion);			
			Siguiente consecutivo= this.toSiguiente(sesion);			
			params.put("idVenta", this.orden.getKey());
			this.orden.setKey(-1L);
			this.orden.setDia(LocalDate.now());
			this.orden.setTicket(cuenta.toConsecutivo());			
			this.orden.setOrden(cuenta.getOrden());
			this.orden.setTicket(consecutivo.getConsecutivo());			
			this.orden.setCticket(consecutivo.getOrden());
			this.orden.setIdFicticiaEstatus(idEstatusFicticia);
			this.orden.setEjercicio(new Long(Fecha.getAnioActual()));						
			this.orden.setIdUsuario(JsfBase.getIdUsuario());
			this.orden.setObservaciones("");
			this.orden.setRegistro(LocalDateTime.now());
			this.orden.setIdTipoDocumento(1L);
			regresar= DaoFactory.getInstance().insert(sesion, this.orden)> 0L;
			if(regresar) {
				TcManticFacturasDto factura= (TcManticFacturasDto)DaoFactory.getInstance().toEntity(TcManticFacturasDto.class, "VistaFicticiasDto", "factura", params);
				factura.setIdFactura(-1L);
				factura.setCadenaOriginal(null);
				factura.setCertificacion(null);
				factura.setCertificadoDigital(null);
				factura.setCertificadoSat(null);
				factura.setFolio(null);
				factura.setFolioFiscal(null);
				factura.setIdFacturama(null);
				factura.setSelloCfdi(null);
				factura.setSelloSat(null);
				factura.setUltimoIntento(null);
				factura.setTimbrado(null);
				factura.setIntentos(0L);
				factura.setIdUsuario(JsfBase.getIdUsuario());
				factura.setObservaciones(null);
				factura.setRegistro(LocalDateTime.now());
  			regresar= DaoFactory.getInstance().insert(sesion, factura)> 0L;
				if(regresar) {
					this.orden.setIdFactura(factura.getIdFactura());
					if(DaoFactory.getInstance().update(sesion, this.orden)>= 1L){
						regresar= this.registraBitacora(sesion, this.orden.getIdFicticia(), idEstatusFicticia, "");
						if(regresar) {
							this.articulos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "TcManticFicticiasDetallesDto", "detalle", params);
							for (Articulo articulo: this.articulos) 
								articulo.setIdComodin(-1L);
							this.toFillArticulos(sesion);
						} // if	
					} // if	
				} // if	
			} // if
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toClonarFicticia
  
  private void checkContratoDomicilio(Session sesion) throws Exception {
    try {
      if(this.orden.getIdContrato()!=null && this.orden.getIdContrato()> 0L && this.orden.getDomicilioContrato()!= null) {
        ContratoDomicilio item= this.orden.getDomicilioContrato();
        switch(item.getSqlAccion()) {
          case INSERT:
            if(item.getIdDomicilio()< 0L) {
              item.setIdDomicilio(this.toIdDomicilio(sesion, item));
              if(item.getIdDomicilio()< 0L)
                item.setIdDomicilio(this.toUpsertDomicilio(sesion, item));
            } // if
            item.setIdPrincipal(1L); 
            item.setIdContrato(this.orden.getIdContrato());
            item.setIdUsuario(JsfBase.getIdUsuario());
            DaoFactory.getInstance().insert(sesion, item);
            break;
          case UPDATE:
            this.toUpsertDomicilio(sesion, item);
            DaoFactory.getInstance().update(sesion, item);
            break;
          case DELETE:
            DaoFactory.getInstance().delete(sesion, item);
            break;
          case SELECT:
            break;
        } // switch
      } // if   
    } // try
    catch(Exception e) {
      throw e;
    } // catch
  }
  
	private Long toUpsertDomicilio(Session sesion, ContratoDomicilio contratoDomicilio) throws Exception {
		Long regresar= -1L;		
    try {
      TcManticDomiciliosDto domicilio= null;
      if(contratoDomicilio.getIdDomicilio()== null || contratoDomicilio.getIdDomicilio()<= 0L) 
        domicilio= new TcManticDomiciliosDto();
      else
        domicilio= (TcManticDomiciliosDto)DaoFactory.getInstance().findById(sesion, TcManticDomiciliosDto.class, contratoDomicilio.getIdDomicilio());
      domicilio.setIdLocalidad(contratoDomicilio.getIdLocalidad().getKey());
      domicilio.setAsentamiento(contratoDomicilio.getColonia());
      domicilio.setCalle(contratoDomicilio.getCalle());
      domicilio.setCodigoPostal(contratoDomicilio.getCodigoPostal());
      domicilio.setEntreCalle(contratoDomicilio.getEntreCalle());
      domicilio.setIdUsuario(JsfBase.getIdUsuario());
      domicilio.setNumeroExterior(contratoDomicilio.getExterior());
      domicilio.setNumeroInterior(contratoDomicilio.getInterior());
      domicilio.setYcalle(contratoDomicilio.getyCalle());
      if(domicilio.isValid())
        regresar= DaoFactory.getInstance().update(sesion, domicilio);		
      else
        regresar= DaoFactory.getInstance().insert(sesion, domicilio);		
    } // try 
    catch(Exception e) {
      throw e;
    } // catch
		return regresar;
	} // insertDomicilio
  
	private Long toIdDomicilio(Session sesion, ContratoDomicilio contratoDomicilio) throws Exception{
		Long regresar            = null;
    Entity item              = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idLocalidad", contratoDomicilio.getIdLocalidad().getKey());
			params.put("codigoPostal", contratoDomicilio.getCodigoPostal());
			params.put("calle", contratoDomicilio.getCalle());
			params.put("numeroExterior", contratoDomicilio.getExterior());
			params.put("numeroInterior", contratoDomicilio.getInterior());
			params.put("asentamiento", contratoDomicilio.getColonia());
			params.put("entreCalle", contratoDomicilio.getEntreCalle());
			params.put("yCalle", contratoDomicilio.getyCalle());
			item= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcManticDomiciliosDto", "domicilioExiste", params);
      regresar= item!= null? item.getKey(): -1L;
		} // try		
		finally {
			Methods.clean(params);
			Methods.clean(item);
		} // finally
		return regresar;
	} // toDomicilio
  
  private void checkComplementoPago(Session sesion) throws Exception {
    Map<String, Object> params = null;
    try {  
      params = new HashMap<>();      
      if(this.orden.getDocumentos()!= null && !this.orden.getDocumentos().isEmpty())
        for (IActions item: this.orden.getDocumentos()) {
          if(item instanceof Insert) {
            ((Documento)item.getDto()).setIdVenta(this.orden.getIdVenta());
            ((Documento)item.getDto()).setIdUsuario(JsfBase.getIdUsuario());
          } // if  
          item.ejecutar(sesion);
          params.put("idVenta", ((Documento)item.getDto()).getIdDetalle());      
          params.put("saldo", ((Documento)item.getDto()).getInsoluto());      
          params.put("diferencia", Numero.toRedondearSat(((Documento)item.getDto()).getGlobal()- ((Documento)item.getDto()).getInsoluto()));
          DaoFactory.getInstance().updateAll(sesion, TcManticVentasDto.class, params, "saldo");
        } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public void toCheckFacturasPagos(Session sesion, List<Documento> documentos) throws Exception {
    this.toCheckFacturasPagos(sesion, documentos, "");
  }
  
  public void toCheckFacturasPagos(Session sesion, List<Documento> documentos, String leyenda) throws Exception {
    mx.org.kaana.mantic.catalogos.clientes.cuentas.reglas.Transaccion pagos= null;
    try {      
		  TcManticClientesPagosDto pago= new TcManticClientesPagosDto();
      pago.setIdUsuario(JsfBase.getIdUsuario());
      pago.setObservaciones(leyenda+ " COMPLEMENTO DE PAGO "+ this.orden.getTicket()+ " ["+ Global.format(EFormatoDinamicos.FECHA_HORA, this.orden.getRegistro())+ "]");
      pago.setPago(this.orden.getTotal());
      pago.setFechaPago(this.orden.getFechaPago().toLocalDate());
      pago.setIdTipoMedioPago(this.orden.getIdTipoPago());
      pagos= new mx.org.kaana.mantic.catalogos.clientes.cuentas.reglas.Transaccion(
        pago, // TcManticClientesPagosDto pago, 
        this.orden.getIdCliente(), // Long idCliente, 
        this.orden.getIdBanco(), // Long idBanco, 
        this.orden.getReferencia(), // String referencia, 
        Collections.EMPTY_LIST, // List<Entity> cuentas, 
        false // boolean saldar
      );
      pagos.procesarComplementoPago(sesion, documentos);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
  public void cancelDocumentosPagos(Session sesion) throws Exception {
    Map<String, Object> params= null;
  	CFDIGestor gestor         = null;
    try {
      params= new HashMap<>();      
      params.put("idVenta", this.orden.getIdVenta());      
      params.put("idVentaEstatus", EEstatusFicticias.CANCELADA.getIdEstatusFicticia());      
      params.put("observaciones", "CANCELADA "+ Global.format(EFormatoDinamicos.FECHA_HORA, LocalDateTime.now())+ " POR ["+ JsfBase.getAutentifica().getCredenciales().getCuenta()+ "]");
      DaoFactory.getInstance().updateAll(sesion, TcManticVentasDto.class, params, "cancelo");
      if(Objects.equals(ETiposComprobantes.COMPLEMENTO_PAGO.getIdTipoComprobante(), this.orden.getIdTipoComprobante())) {
        gestor= new CFDIGestor(this.orden.getIdFicticia());
        List<Documento> documentos= gestor.toDocumentosCfdi(sesion);
        if(documentos!= null && !documentos.isEmpty()) {
          for(Documento item: documentos) {
            item.setInsoluto(item.getInsoluto()+ item.getPagado());
            item.setPagado(item.getPagado()* -1D);
            params.put("idVenta", item.getIdDetalle());      
            params.put("pagado", item.getPagado());      
            DaoFactory.getInstance().updateAll(sesion, TcManticVentasDto.class, params, "depuro");
          } // for
          this.toCheckFacturasPagos(sesion, documentos, "CANCELADO");
        } // if  
      } // if
      else {
        params.put("idCliente", this.orden.getIdCliente());      
        params.put("idClienteDeudaEstatus", EEstatusClientesDeudas.CANCELADA.getIdClienteDeudaEstatus());
        DaoFactory.getInstance().updateAll(sesion, TcManticClientesDeudasDto.class, params, "cancelo");
        // CHECAR COMO SE ALMACENA EL VALOR DEL SALDO EN LA TABLA DE CLIENTES, SI ES EL LIMITE DE CREDITO LAS VENTAS A CREDITO
        DaoFactory.getInstance().updateAll(sesion, TcManticClientesDto.class, params, "cancelo");
      } // if  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  protected void toRecordDeuda(Session sesion, Double importe) throws Exception {
		TcManticClientesDeudasDto deuda           = null;		
    TcManticClientesDeudasBitacoraDto registro= null;
    try {
      deuda= new TcManticClientesDeudasDto();
      deuda.setIdVenta(this.orden.getIdVenta());
      deuda.setIdCliente(this.orden.getIdCliente());
      deuda.setIdUsuario(JsfBase.getIdUsuario());
      deuda.setImporte(importe);
      deuda.setSaldo(importe);
      deuda.setLimite(this.toLimiteCredito(sesion));
      deuda.setIdClienteDeudaEstatus(EEstatusClientesDeudas.INICIAL.getIdClienteDeudaEstatus()); // INICIADA
      DaoFactory.getInstance().insert(sesion, deuda);		
      registro= new TcManticClientesDeudasBitacoraDto(deuda.getIdClienteDeudaEstatus(), "", JsfBase.getIdUsuario(), deuda.getIdClienteDeuda(), -1L);
      DaoFactory.getInstance().insert(sesion, registro);
			TcManticClientesDto cliente= (TcManticClientesDto) DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, this.orden.getIdCliente());
			cliente.setSaldo(cliente.getSaldo()+ importe);
			DaoFactory.getInstance().update(sesion, cliente);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
	} // registrarDeuda  
  
	public LocalDate toLimiteCredito(Session sesion) throws Exception {
		TcManticClientesDto cliente= (TcManticClientesDto) DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, this.orden.getIdCliente());
		Long addDias= cliente.getPlazoDias();			
		LocalDate regresar= LocalDate.now();			
		regresar.plusDays(addDias.intValue());
		return regresar;
	} // toLimiteCredito
  
} 