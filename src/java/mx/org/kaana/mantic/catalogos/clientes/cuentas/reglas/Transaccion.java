package mx.org.kaana.mantic.catalogos.clientes.cuentas.reglas;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.facturama.reglas.Facturama;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.reportes.FileSearch;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.catalogos.clientes.reglas.NotificaCliente;
import mx.org.kaana.mantic.correos.enums.ECorreos;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesPagosArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesPagosControlesDto;
import mx.org.kaana.mantic.db.dto.TcManticClientesPagosDto;
import mx.org.kaana.mantic.db.dto.TcManticFacturasDto;
import mx.org.kaana.mantic.db.dto.TcManticInventariosDto;
import mx.org.kaana.mantic.db.dto.TcManticMovimientosDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
import mx.org.kaana.mantic.enums.EEstatusClientes;
import mx.org.kaana.mantic.enums.EEstatusFacturas;
import mx.org.kaana.mantic.enums.EEstatusFicticias;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.facturas.beans.Documento;
import mx.org.kaana.mantic.inventarios.entradas.beans.Nombres;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends Facturama {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	
	private List<Entity> cuentas;
	private String messageError;
	private TcManticClientesPagosDto pago;
	private Long idCliente;
  private Long idCaja;
	private Long idEmpresa;
	private Long idCierreActivo;
	private Long idBanco;
	private String referencia;
	private Double pagoGeneral;
	private boolean saldar;
	private TcManticClientesDeudasDto clienteDeuda;
	private Long idClientePago;
	private Importado file;
	private TcManticClientesPagosControlesDto control;
	private LocalDate limite;
	private Importado xml;
	private Importado pdf;

  public Transaccion(TcManticClientesPagosDto pago, Long idEmpresa, Long idBanco, String referencia, boolean saldar) {
		this(pago, -1L, idEmpresa, idBanco, referencia, saldar);
	} // Transaccion
	
	public Transaccion(TcManticClientesPagosDto pago, Long idCliente, Long idEmpresa, Long idBanco, String referencia, boolean saldar) {
		this(pago, idCliente, idEmpresa, idBanco, referencia, null, saldar);
	}
	
	public Transaccion(TcManticClientesPagosDto pago, Long idCliente, Long idEmpresa, Long idBanco, String referencia, List<Entity> cuentas, boolean saldar) {
		this.pago      = pago;
		this.idCliente = idCliente;
		this.idCaja    = -1L;
		this.idEmpresa = idEmpresa;
		this.idBanco   = idBanco;
		this.referencia= referencia;
		this.cuentas   = cuentas;
		this.saldar    = saldar;
	} // Transaccion

	public Transaccion(TcManticClientesPagosDto pago, Long idCliente, Long idEmpresa, List<Entity> cuentas, Importado xml, Importado pdf, boolean saldar) {
    this(pago, idCliente, idEmpresa, pago.getIdBanco(), pago.getReferencia(), cuentas, saldar);
    this.xml  = xml; 
    this.pdf  = pdf; 
  }
  
	public Transaccion(Importado file, TcManticClientesDeudasDto clienteDeuda, Long idClientePago) {
		this.file         = file;
		this.clienteDeuda = clienteDeuda;
		this.idClientePago= idClientePago;
	} // Transaccion

	public Transaccion(Long idClientePago, String referencia, LocalDate limite) {
    this.idClientePago= idClientePago;
    this.referencia   = referencia;
    this.limite       = limite;
  }
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar= false;
    try {			
			if(this.pago!= null)
				this.pagoGeneral= this.pago.getPago();
      switch (accion) {
        case AGREGAR:					
					regresar= this.procesarPago(sesion);
          break;       
        case PROCESAR:					
					regresar= this.procesarPagoGeneral(sesion);
          break;       
				case COMPLEMENTAR: 
					regresar= this.procesarPagoSegmento(sesion);
					break;
				case SUBIR:
					regresar= true;
					this.toUpdateDeleteFilePago(sesion);
					break;
				case ELIMINAR:
					regresar= this.toDeletePagos(sesion);
					break;
				case DEPURAR:
					regresar= this.toDeleteCuenta(sesion);
					break;
				case MODIFICAR:
					regresar= this.toUpdateVencimiento(sesion);
					break;
				case COMPLETO:
					regresar= this.procesarPagoCuentas(sesion);
          for(Entity cuenta: this.cuentas) {
            if(cuenta.toBoolean("activo")) {
              this.clienteDeuda = new TcManticClientesDeudasDto(cuenta.getKey());
              this.idClientePago= cuenta.toLong("idClientePago");
              if(this.xml!= null) {
                this.file= this.xml;
                this.toUpdateDeleteFilePago(sesion);
              } // if  
              if(this.pdf!= null) {
                this.file= this.pdf;
                this.toUpdateDeleteFilePago(sesion);
              } // if  
            } // if  
          } // for
					break;
      } // switch
      if (!regresar) 
        throw new Exception("");      
    } // try
    catch (Exception e) {
      throw new Exception(this.messageError.concat("<br/>")+ e);
    } // catch		
    return regresar;
	} // ejecutar
	
	private boolean procesarPago(Session sesion) throws Exception {
		boolean regresar               = false;
		TcManticClientesDeudasDto deuda= null;
		Double saldo                   = 0D;
		Siguiente orden                = null;
		try {
			if(this.toCierreCaja(sesion, this.pago.getPago())) {
        // INSERTAR EN LA TABLA DE CONTROL DE PAGOS CON EL ESTATUS DE ACTIVO PARA PODER CANCELAR UN PAGO
        this.control= new TcManticClientesPagosControlesDto(
          -1L, // Long idClientePagoControl, 
          1L, // Long idActivo, 
          "INDIVIDUAL", // String tipo, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          this.pago.getPago(), // Double pago
          this.pago.getObservaciones() // String Observaciones
        );
        DaoFactory.getInstance().insert(sesion, this.control);
        this.pago.setIdClientePagoControl(this.control.getIdClientePagoControl());
        
				this.pago.setIdCierre(this.idCierreActivo);				
				if(!this.pago.getIdTipoMedioPago().equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago())){
					this.pago.setReferencia(this.referencia);
					this.pago.setIdBanco(this.idBanco);
				} // if
				orden= this.toSiguiente(sesion, this.idCliente);
				this.pago.setOrden(orden.getOrden());
				this.pago.setConsecutivo(orden.getConsecutivo());
				this.pago.setEjercicio(new Long(Fecha.getAnioActual()));
				if(DaoFactory.getInstance().insert(sesion, this.pago)>= 1L) {
					deuda= (TcManticClientesDeudasDto) DaoFactory.getInstance().findById(sesion, TcManticClientesDeudasDto.class, this.pago.getIdClienteDeuda());
          this.pago.setComentarios(
            "PAGO INDIVIDUAL $".concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.pago.getPago())).concat(
            " [").concat(Global.format(EFormatoDinamicos.FECHA_HORA, this.pago.getRegistro())).concat(
            "] DEUDA $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, deuda.getImporte())).concat(
            "] SALDO $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, deuda.getSaldo())).concat(
            " NUEVO SALDO $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, Numero.redondearSat(deuda.getSaldo()- this.pago.getPago()))));
																									 
					saldo= deuda.getSaldo()- this.pago.getPago();
					deuda.setSaldo(saldo);
					deuda.setIdClienteDeudaEstatus(this.saldar? EEstatusClientes.SALDADA.getIdEstatus(): saldo.equals(0D)? EEstatusClientes.FINALIZADA.getIdEstatus(): EEstatusClientes.PARCIALIZADA.getIdEstatus());
					regresar= DaoFactory.getInstance().update(sesion, deuda)>= 1L;
          TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
            -1L, // Long idClienteDeudaBitacora, 
            deuda.getIdClienteDeudaEstatus(), // Long idClienteDeudaEstatus, 
            "SE REGISTRO UN PAGO [".concat(String.valueOf(this.pago.getPago())).concat("]"), // String justificacion, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            deuda.getIdClienteDeuda() // Long idClienteDeuda
          );
          DaoFactory.getInstance().insert(sesion, bitacora);
					this.actualizarSaldoCatalogoCliente(sesion, deuda.getIdCliente(), this.pago.getPago(), false);
          DaoFactory.getInstance().update(sesion, this.pago);
				} // if
        
        sesion.flush();
        NotificaCliente notifica= new NotificaCliente(
          sesion, // sesion
          this.idCliente, // Long idCliente, 
          EReportes.PAGOS_CUENTAS_POR_COBRAR, // EReportes reportes, 
          ECorreos.PAGOS // ECorreos correo
        );
        notifica.setIdClientePagoControl(this.control.getIdClientePagoControl());
        notifica.doSendMail();
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally{
			this.messageError= "Error al registrar el pago";
		} // finally
		return regresar;
	} // procesarPago
	
  private boolean procesarPagoSegmento(Session sesion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;
		Double saldoDeuda        = 0D;				
		Double pagoParcial       = 0D;				
		Double abono             = 0D;		
		Long idEstatus           = -1L;
		try {
      // INSERTAR EN LA TABLA DE CONTROL DE PAGOS CON EL ESTATUS DE ACTIVO PARA PODER CANCELAR UN PAGO
      this.control= new TcManticClientesPagosControlesDto(
        -1L, // Long idClientePagoControl, 
        1L, // Long idActivo, 
        "SEGMENTO", // String tipo, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        this.pago.getPago(), // Double pago
        this.pago.getObservaciones() // String Observaciones
      );
      DaoFactory.getInstance().insert(sesion, this.control);
      for(Entity item: this.cuentas) {
        if(item.toBoolean("activo")) {
          saldoDeuda= Numero.toRedondearSat(item.toDouble("saldo"));
          if(saldoDeuda< item.toDouble("pago")) {
            pagoParcial= saldoDeuda;
            this.pago.setPago(Numero.toRedondearSat(item.toDouble("pago")- saldoDeuda));
            abono= 0D;
            idEstatus= EEstatusClientes.FINALIZADA.getIdEstatus();
          } // if
          else {						
            pagoParcial= item.toDouble("pago");
            abono= Numero.toRedondearSat(saldoDeuda- item.toDouble("pago"));
            idEstatus= this.saldar? EEstatusClientes.SALDADA.getIdEstatus(): (saldoDeuda.equals(item.toDouble("pago"))? EEstatusClientes.FINALIZADA.getIdEstatus(): EEstatusClientes.PARCIALIZADA.getIdEstatus());
          } /// else
          if(this.registrarPago(sesion, item, pagoParcial, this.control.getIdClientePagoControl(), "SEGMENTO")) {
            params= new HashMap<>();
            params.put("saldo", abono);
            params.put("idClienteDeudaEstatus", idEstatus);
            DaoFactory.getInstance().update(sesion, TcManticClientesDeudasDto.class, item.getKey(), params);
            TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
              -1L, // Long idClienteDeudaBitacora, 
              idEstatus, // Long idClienteDeudaEstatus, 
              "SE REGISTRO UN PAGO [".concat(String.valueOf(pagoParcial)).concat("]"), // String justificacion, 
              JsfBase.getIdUsuario(), // Long idUsuario, 
              item.getKey() // Long idClienteDeuda
            );
            DaoFactory.getInstance().insert(sesion, bitacora);
            this.actualizarSaldoCatalogoCliente(sesion, this.idCliente, pagoParcial, false);
          }	// if				
          else 
            if (this.saldar) {
              if(this.registrarPago(sesion, item, 0D, this.control.getIdClientePagoControl(), "SEGMENTO")) {
                params= new HashMap<>();
                params.put("saldo", 0);
                params.put("idClienteDeudaEstatus", EEstatusClientes.SALDADA.getIdEstatus());
                DaoFactory.getInstance().update(sesion, TcManticClientesDeudasDto.class, item.getKey(), params);
                TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
                  -1L, // Long idClienteDeudaBitacora, 
                  EEstatusClientes.FINALIZADA.getIdEstatus(), // Long idClienteDeudaEstatus, 
                  null, // String justificacion, 
                  JsfBase.getIdUsuario(), // Long idUsuario, 
                  item.getKey() // Long idClienteDeuda
                );
                DaoFactory.getInstance().insert(sesion, bitacora);
              }	// if				
            } // if
        } // if
			} // for
      
      sesion.flush();
      NotificaCliente notifica= new NotificaCliente(
        sesion, // sesion
        this.idCliente, // Long idCliente, 
        EReportes.PAGOS_CUENTAS_POR_COBRAR, // EReportes reportes, 
        ECorreos.PAGOS // ECorreos correo
      );
      notifica.setIdClientePagoControl(this.control.getIdClientePagoControl());
      notifica.doSendMail();
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		finally{
			this.messageError= "Error al registrar el pago";
			Methods.clean(params);			
		} // finally
		return regresar;
	} // procesarPagoSegmento
	
  private boolean procesarPagoCuentas(Session sesion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;
		Double saldoDeuda        = 0D;				
		Double pagoParcial       = 0D;				
		Double abono             = 0D;		
		Long idEstatus           = -1L;
		try {
      // INSERTAR EN LA TABLA DE CONTROL DE PAGOS CON EL ESTATUS DE ACTIVO PARA PODER CANCELAR UN PAGO
      this.control= new TcManticClientesPagosControlesDto(
        -1L, // Long idClientePagoControl, 
        1L, // Long idActivo, 
        "REFERENCIADO", // String tipo, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        this.pago.getPago(), // Double pago
        this.pago.getObservaciones() // String Observaciones
      );
      DaoFactory.getInstance().insert(sesion, this.control);
      for(Entity cuenta: this.cuentas) {
        if(cuenta.toBoolean("activo")) {
          saldoDeuda= Numero.toRedondearSat(cuenta.toDouble("saldo"));
          if(saldoDeuda< cuenta.toDouble("pago")) {
            pagoParcial= saldoDeuda;
            this.pago.setPago(Numero.toRedondearSat(cuenta.toDouble("pago")- saldoDeuda));
            abono= 0D;
            idEstatus= EEstatusClientes.FINALIZADA.getIdEstatus();
          } // if
          else {						
            pagoParcial= cuenta.toDouble("pago");
            abono= Numero.toRedondearSat(saldoDeuda- cuenta.toDouble("pago"));
            idEstatus= this.saldar? EEstatusClientes.SALDADA.getIdEstatus(): (saldoDeuda.equals(cuenta.toDouble("pago"))? EEstatusClientes.FINALIZADA.getIdEstatus(): EEstatusClientes.PARCIALIZADA.getIdEstatus());
          } /// else
          if(this.registrarPago(sesion, cuenta, pagoParcial, this.control.getIdClientePagoControl(), "NOTA CREDITO")) {
            params= new HashMap<>();
            params.put("saldo", abono);
            params.put("idClienteDeudaEstatus", idEstatus);
            DaoFactory.getInstance().update(sesion, TcManticClientesDeudasDto.class, cuenta.getKey(), params);
            TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
              -1L, // Long idClienteDeudaBitacora, 
              idEstatus, // Long idClienteDeudaEstatus, 
              "SE REGISTRO UN PAGO [".concat(String.valueOf(pagoParcial)).concat("]"), // String justificacion, 
              JsfBase.getIdUsuario(), // Long idUsuario, 
              cuenta.getKey() // Long idClienteDeuda
            );
            DaoFactory.getInstance().insert(sesion, bitacora);
            this.actualizarSaldoCatalogoCliente(sesion, this.idCliente, pagoParcial, false);
          }	// if				
          else 
            if (this.saldar) {
              if(this.registrarPago(sesion, cuenta, 0D, this.control.getIdClientePagoControl(), "NOTA CREDITO")) {
                params= new HashMap<>();
                params.put("saldo", 0);
                params.put("idClienteDeudaEstatus", EEstatusClientes.SALDADA.getIdEstatus());
                DaoFactory.getInstance().update(sesion, TcManticClientesDeudasDto.class, cuenta.getKey(), params);
                TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
                  -1L, // Long idClienteDeudaBitacora, 
                  EEstatusClientes.FINALIZADA.getIdEstatus(), // Long idClienteEstatus, 
                  null, // String justificacion, 
                  JsfBase.getIdUsuario(), // Long idUsuario, 
                  cuenta.getKey() // Long idClienteDeuda
                );
                DaoFactory.getInstance().insert(sesion, bitacora);
              }	// if				
            } // if
        } // if
      } // for
      
      sesion.flush();
      NotificaCliente notifica= new NotificaCliente(
        sesion, // sesion
        this.idCliente, // Long idCliente, 
        EReportes.PAGOS_CUENTAS_POR_COBRAR, // EReportes reportes, 
        ECorreos.PAGOS // ECorreos correo
      );
      notifica.setIdClientePagoControl(this.control.getIdClientePagoControl());
      // notifica.doSendMail();
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		finally{
			this.messageError= "Error al registrar el pago";
			Methods.clean(params);			
		} // finally
		return regresar;
	} // procesarPagoCuentas
	
	private boolean procesarPagoGeneral(Session sesion) throws Exception {		
		boolean regresar         = true;
		List<Entity> deudas      = null;		
		Map<String, Object>params= null;
		Double saldo             = 1D;
		Double saldoDeuda        = 0D;				
		Double pagoParcial       = 0D;				
		Double abono             = 0D;		
		Long idEstatus           = -1L;
		try {
			deudas= this.toDeudas(sesion);
      // INSERTAR EN LA TABLA DE CONTROL DE PAGOS CON EL ESTATUS DE ACTIVO PARA PODER CANCELAR UN PAGO
      this.control= new TcManticClientesPagosControlesDto(
        -1L, // Long idClientePagoControl, 
        1L, // Long idActivo, 
        "GENERAL", // String tipo, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        this.pago.getPago(), // Double pago
        this.pago.getObservaciones() // String Observaciones
      );
      DaoFactory.getInstance().insert(sesion, this.control);
			for(Entity deuda: deudas) {
				if(saldo> 0) {	
					saldoDeuda= Numero.toRedondear(deuda.toDouble("saldo"));
					if(saldoDeuda< this.pago.getPago()){
						pagoParcial= saldoDeuda;
						saldo= Numero.toRedondearSat(this.pago.getPago() - saldoDeuda);						
						this.pago.setPago(saldo);
						abono= 0D;
						idEstatus= EEstatusClientes.FINALIZADA.getIdEstatus();
					} // if
          else {		
						pagoParcial= this.pago.getPago();
						saldo= 0D;
						abono= Numero.toRedondearSat(saldoDeuda- this.pago.getPago());
						idEstatus= this.saldar? EEstatusClientes.SALDADA.getIdEstatus(): (saldoDeuda.equals(this.pago.getPago())? EEstatusClientes.FINALIZADA.getIdEstatus() : EEstatusClientes.PARCIALIZADA.getIdEstatus());
					} /// else
					if(this.registrarPago(sesion, deuda, pagoParcial, this.control.getIdClientePagoControl(), "GENERAL")) {
						params= new HashMap<>();
						params.put("saldo", abono);
						params.put("idClienteDeudaEstatus", idEstatus);
						DaoFactory.getInstance().update(sesion, TcManticClientesDeudasDto.class, deuda.getKey(), params);
            TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
              -1L, // Long idClienteDeudaBitacora, 
              idEstatus, // Long idClienteDeudaEstatus, 
              "SE REGISTRO UN PAGO [".concat(String.valueOf(pagoParcial)).concat("]"), // String justificacion, 
              JsfBase.getIdUsuario(), // Long idUsuario, 
              deuda.getKey() // Long idClienteDeuda
            );
            DaoFactory.getInstance().insert(sesion, bitacora);
						this.actualizarSaldoCatalogoCliente(sesion, this.idCliente, pagoParcial, false);
					}	// if				
				} // if
			} // for
      
      sesion.flush();
      NotificaCliente notifica= new NotificaCliente(
        sesion, // sesion
        this.idCliente, // Long idCliente, 
        EReportes.PAGOS_CUENTAS_POR_COBRAR, // EReportes reportes, 
        ECorreos.PAGOS // ECorreos correo
      );
      notifica.setIdClientePagoControl(this.control.getIdClientePagoControl());
      notifica.doSendMail();
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		finally{
			this.messageError= "Error al registrar el pago";
			Methods.clean(params);			
		} // finally
		return regresar;
	} // procesarPagoGeneral
	
  private boolean registrarPago(Session sesion, Entity deuda, Double pagoParcial, Long idClientePagoControl, String tipo) throws Exception {
	  TcManticClientesPagosDto registroPago= null;
		boolean regresar                     = false;
		Siguiente orden	                     = null;
		try {
			if(this.toCierreCaja(sesion, pagoParcial)) {
				registroPago= new TcManticClientesPagosDto();
        registroPago.setIdClientePagoControl(idClientePagoControl);
				registroPago.setIdClienteDeuda(deuda.getKey());
				registroPago.setIdUsuario(JsfBase.getIdUsuario());
				registroPago.setFechaPago(this.pago.getFechaPago());
				registroPago.setComentarios(
          "PAGO ".concat(tipo).concat(" $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.pagoGeneral)).concat(
          " [").concat(Global.format(EFormatoDinamicos.FECHA_HORA, registroPago.getRegistro())).concat(
          "] DEUDA $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, deuda.toDouble("importe"))).concat(
          " SALDO $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, deuda.toDouble("saldo"))).concat(
          " APLICADO $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, pagoParcial)).concat(
          " NUEVO SALDO $").concat(Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, Numero.redondearSat(deuda.toDouble("saldo")- pagoParcial))));
				
				registroPago.setPago(pagoParcial);
				registroPago.setIdTipoMedioPago(this.pago.getIdTipoMedioPago());
				registroPago.setIdCierre(this.idCierreActivo);
				if(!this.pago.getIdTipoMedioPago().equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago())) {
					registroPago.setIdBanco(this.idBanco);
					registroPago.setReferencia(this.referencia);
				} // if
				orden= this.toSiguiente(sesion, this.idCliente);
				registroPago.setOrden(orden.getOrden());
				registroPago.setConsecutivo(orden.getConsecutivo());
				registroPago.setEjercicio(new Long(Fecha.getAnioActual()));
				registroPago.setFolio(this.pago.getFolio());
				regresar= DaoFactory.getInstance().insert(sesion, registroPago)>= 1L;
        deuda.put("idClientePago", new Value("idClientePago", registroPago.getKey()));
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarPago
	
	private List<Entity> toDeudas(Session sesion) throws Exception {
		List<Entity> regresar    = null;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("idCliente", this.idCliente);
			params.put(Constantes.SQL_CONDICION, " tc_mantic_clientes_deudas.saldo> 0 and tc_mantic_clientes_deudas.id_cliente_deuda_estatus in(1, 2)");			
			params.put("sortOrder", "order by tc_mantic_clientes_deudas.registro asc");
			regresar= DaoFactory.getInstance().toEntitySet(sesion, "VistaClientesDto", "cuentas", params, Constantes.SQL_TODOS_REGISTROS);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toDeudas	
	
	private boolean toCierreCaja(Session sesion, Double pago) throws Exception {
//		mx.org.kaana.mantic.ventas.caja.reglas.Transaccion cierre= null;
//		VentaFinalizada datosCierre= null;
//		boolean regresar= false;
//		try {
//			datosCierre= new VentaFinalizada();
//			datosCierre.getTicketVenta().setIdEmpresa(this.idEmpresa);
//			datosCierre.setIdCaja(this.idCaja);
//			datosCierre.getTotales().setEfectivo(pago);
//			cierre= new mx.org.kaana.mantic.ventas.caja.reglas.Transaccion(datosCierre);
//			if(cierre.verificarCierreCaja(sesion)) {
//				this.idCierreActivo= cierre.getIdCierreVigente();
//				regresar= cierre.alterarCierreCaja(sesion, this.pago.getIdTipoMedioPago());
//			} // if
//		} // try
//		catch (Exception e) {			
//			throw e; 
//		} // catch		
//		return regresar;
		return true;
	} // toCierreCaja
	
	protected void toUpdateDeleteFilePago(Session sesion) throws Exception {
		TcManticClientesPagosArchivosDto tmp= null;
		if(this.clienteDeuda.getIdClienteDeuda()!= -1L) {			
			if(this.file!= null) {
				tmp= new TcManticClientesPagosArchivosDto(
					this.file.getRuta(), // String ruta
					this.file.getFileSize(), // Long tamanio
					JsfBase.getIdUsuario(), // Long idUsuario
					Objects.equals(file.getFormat(), EFormatos.XML)? 1L: Objects.equals(file.getFormat(), EFormatos.PDF)? 2L: 12L, // Long idTipoArchivo
					1L, // Long idPrincipal
					this.file.getObservaciones(), // String observaciones
					this.idClientePago,	// Long idClientePago
					Configuracion.getInstance().getPropiedadSistemaServidor("cobros").concat(this.file.getRuta()).concat(this.file.getName()), // String alias
					-1L, // Long idClientePagoArchivo
					this.file.getOriginal(), // String nombre
          this.file.getName() // String archivo
				);
				TcManticClientesPagosArchivosDto exists= (TcManticClientesPagosArchivosDto)DaoFactory.getInstance().toEntity(TcManticClientesPagosArchivosDto.class, "TcManticClientesPagosArchivosDto", "identically", tmp.toMap());
				File reference= new File(tmp.getAlias());
				if(exists== null && reference.exists()) {
					DaoFactory.getInstance().updateAll(sesion, TcManticClientesPagosArchivosDto.class, tmp.toMap());
					DaoFactory.getInstance().insert(sesion, tmp);
				} // if
				else {
					if(exists!= null)
						DaoFactory.getInstance().delete(sesion, exists);
					DaoFactory.getInstance().insert(sesion, tmp);
				} // else
				sesion.flush();
        this.toSaveFile(this.file.getIdArchivo());
        this.toCheckDeleteFile(sesion, this.file.getName());
				// this.toDeleteAll(Configuracion.getInstance().getPropiedadSistemaServidor("cobros").concat(this.file.getRuta()), ".".concat(this.file.getFormat().name()), this.toListFile(sesion, this.file, 2L));
			} // if	
  	} // if	
	} // toUpdateDeleteXml
	
	private void toDeleteAll(String path, String type, List<Nombres> listado) {
    FileSearch fileSearch = new FileSearch();
    fileSearch.searchDirectory(new File(path), type.toLowerCase());
    if(fileSearch.getResult().size()> 0) {
		  for (String matched: fileSearch.getResult()) {
				String name= matched.substring((matched.lastIndexOf("/")< 0? matched.lastIndexOf("\\"): matched.lastIndexOf("/"))+ 1);
				if(listado.indexOf(new Nombres(name))< 0) {
				  File existe= new File(matched);
          if(existe.exists()) {
            LOG.warn("Delete factura: ".concat(matched));
				    // existe.delete();
          } // if   
				} // if
      } // for
		} // if
	} // toDeleteAll
	
	private List<Nombres> toListFile(Session sesion, Importado tmp, Long idTipoArchivo) throws Exception {
		List<Nombres> regresar    = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idTipoArchivo", idTipoArchivo);
			params.put("ruta", tmp.getRuta());
			regresar= (List<Nombres>)DaoFactory.getInstance().toEntitySet(sesion, Nombres.class, "TcManticClientesPagosArchivosDto", "listado", params);
			regresar.add(new Nombres(tmp.getName()));
		} // try  // try 
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toListFile
	
	private Siguiente toSiguiente(Session sesion, Long idCliente) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", ((TcManticClientesDto)DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, idCliente)).getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "VistaTcManticClientesPagosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	private Boolean toDeletePagos(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idClientePagoControl", this.idClientePago);      
      List<Entity> items = (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaClientesDto", "eliminarPagos", params);
      if(items!= null && !items.isEmpty()) {
        TcManticClientesDto cliente= (TcManticClientesDto)DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, items.get(0).toLong("idCliente"));
        for (Entity item: items) {
          cliente.setSaldo(Numero.toRedondearSat(cliente.getSaldo()+ item.toDouble("abonado")));
          // borrar todos los archivos asociados a los pagos 
          List<TcManticClientesPagosArchivosDto> archivos= (List<TcManticClientesPagosArchivosDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticClientesPagosArchivosDto.class, "TcManticClientesPagosArchivosDto", "pago", item.toMap());
          if(archivos!= null && !archivos.isEmpty()) {
            for (TcManticClientesPagosArchivosDto archivo: archivos) {
              // SI EXISTE MAS DE UNA REFERENCIA AL MISMO ARCHIVO ENTONCES NO ELIMINARLO
              if(archivo.getTamanio()<= 1L) {
                File evidencia= new File(archivo.getAlias());
                if(evidencia.exists())
                  evidencia.delete();
              } // if  
            } // for
            DaoFactory.getInstance().deleteAll(sesion, TcManticClientesPagosArchivosDto.class, item.toMap());
          } // if  
          DaoFactory.getInstance().delete(sesion, TcManticClientesPagosDto.class, item.toLong("idClientePago"));
          TcManticClientesDeudasDto deuda= (TcManticClientesDeudasDto)DaoFactory.getInstance().findById(sesion, TcManticClientesDeudasDto.class, item.toLong("idClienteDeuda"));
          deuda.setSaldo(Numero.toRedondearSat(deuda.getSaldo()+ item.toDouble("abonado")));
          if(Objects.equals(deuda.getImporte(), deuda.getSaldo()))
            deuda.setIdClienteDeudaEstatus(EEstatusClientes.INICIADA.getIdEstatus());
          else
            if(!Objects.equals(deuda.getImporte(), deuda.getSaldo()))
              deuda.setIdClienteDeudaEstatus(EEstatusClientes.PARCIALIZADA.getIdEstatus());
          DaoFactory.getInstance().update(sesion, deuda);
          TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
            -1L, // Long idClienteDeudaBitacora, 
            deuda.getIdClienteDeudaEstatus(), // Long idClienteDeudaEstatus, 
            this.referencia.concat(", SE ELIMINO EL PAGO [").concat(String.valueOf(item.toDouble("abonado"))).concat("]"), // String justificacion, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            deuda.getIdClienteDeuda() // Long idClienteDeuda
          );
          DaoFactory.getInstance().insert(sesion, bitacora);
        } // for
        DaoFactory.getInstance().delete(sesion, TcManticClientesPagosControlesDto.class, this.idClientePago);
        DaoFactory.getInstance().update(sesion, cliente);
      } // if
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
 
  private Boolean toDeleteCuenta(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idClienteDeuda", this.idClientePago);      
      TcManticClientesDeudasDto deuda= (TcManticClientesDeudasDto)DaoFactory.getInstance().findById(sesion, TcManticClientesDeudasDto.class, this.idClientePago);
      deuda.setIdClienteDeudaEstatus(EEstatusClientes.CANCELADA.getIdEstatus());
      DaoFactory.getInstance().update(sesion, deuda);
      TcManticClientesDeudasBitacoraDto bitacora= new TcManticClientesDeudasBitacoraDto(
        -1L, // Long idClienteDeudaBitacora, 
        deuda.getIdClienteDeudaEstatus(), // Long idClienteDeudaEstatus, 
        this.referencia, // String justificacion, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        deuda.getIdClienteDeuda() // Long idClienteDeuda
      );
      DaoFactory.getInstance().insert(sesion, bitacora);
      TcManticClientesDto cliente= (TcManticClientesDto)DaoFactory.getInstance().findById(sesion, TcManticClientesDto.class, deuda.getIdCliente());
      cliente.setSaldo(Numero.toRedondearSat(cliente.getSaldo()- deuda.getImporte()));
      DaoFactory.getInstance().update(sesion, cliente);
      TcManticVentasDto venta= (TcManticVentasDto)DaoFactory.getInstance().findById(sesion, TcManticVentasDto.class, deuda.getIdVenta());
      venta.setIdVentaEstatus(EEstatusFicticias.CANCELADA.getIdEstatusFicticia());
      DaoFactory.getInstance().update(sesion, venta);
      TcManticFacturasDto factura= (TcManticFacturasDto)DaoFactory.getInstance().findById(sesion, TcManticFacturasDto.class, venta.getIdFactura());
      factura.setIdFacturaEstatus(EEstatusFacturas.CANCELADA.getIdEstatusFactura());
      factura.setCancelada(LocalDateTime.now());
      DaoFactory.getInstance().update(sesion, factura);
      TcManticVentasBitacoraDto movimiento= new TcManticVentasBitacoraDto(
        -1L, // Long idVentaBitacora, 
        this.referencia.concat(", SE CANCELO LA CUENTA POR COBRAR"), // String justificacion, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        venta.getIdVenta(), // Long idVenta, 
        venta.getIdVentaEstatus(), // Long idVentaEstatus, 
        venta.getCticket(), // Long consecutivo, 
        venta.getTotal() // Double importe
      );
      DaoFactory.getInstance().insert(sesion, movimiento);
      // FALTA AFECTAR LAS EXITENCIAS EN EL ALMACEN RESPECTIVO PORQUE LA VENTA SE CANCELO
      // this.toAffectAlmacenes(sesion, venta);
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } 
  
  public void toAffectAlmacenes(Session sesion, TcManticVentasDto venta) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {      
      Double stock= null;
      params.put("idVenta", venta.getIdVenta());      
      List<TcManticVentasDetallesDto> items= (List<TcManticVentasDetallesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticVentasDetallesDto.class, "TcManticVentasDetallesDto", "detalle", params);
      if(items!= null && !items.isEmpty()) {
        for (TcManticVentasDetallesDto item : items) {
          params.put("idAlmacen", venta.getIdAlmacen());
			    params.put("idArticulo", item.getIdArticulo());
          // ACTUALIZAR EL STOCK DEL ALMACEN 
			    TcManticAlmacenesArticulosDto ubicacion= (TcManticAlmacenesArticulosDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesArticulosDto.class,  params, "ubicacion");
          stock= ubicacion.getStock();
          ubicacion.setStock(Numero.toRedondearSat(ubicacion.getStock()+ item.getCantidad()));
				  DaoFactory.getInstance().update(sesion, ubicacion);
          // generar un registro en la bitacora de movimientos de los articulos 
          TcManticMovimientosDto entrada= new TcManticMovimientosDto(
            venta.getTicket(), // String consecutivo, 
            3L, // Long idTipoMovimiento, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            venta.getIdAlmacen(), // Long idAlmacen, 
            -1L, // Long idMovimiento, 
            item.getCantidad(), // Double cantidad, 
            item.getIdArticulo(), // Long idArticulo, 
            stock, // Double stock, 
            ubicacion.getStock(), // Double calculo
            "FUE UN ERROR DE CAPTURA" // String observaciones
          );
          DaoFactory.getInstance().insert(sesion, entrada);
          // ACTUALIZAR EL STOCK DEL INVENTARIO DEL ARTICULO POR ALMACEN
    			TcManticInventariosDto inventario= (TcManticInventariosDto)DaoFactory.getInstance().findFirst(sesion, TcManticInventariosDto.class, "inventario", params);
          inventario.setEntradas(inventario.getEntradas()+ item.getCantidad());
          inventario.setStock((inventario.getStock()< 0D? 0D: inventario.getStock())+ item.getCantidad());
          DaoFactory.getInstance().update(sesion, inventario);
          // ACTUALIZAR EL STOCK GLOBAL DEL ARTICULO
     			TcManticArticulosDto global= (TcManticArticulosDto)DaoFactory.getInstance().findById(sesion, TcManticArticulosDto.class, item.getIdArticulo());
          global.setActualizado(LocalDateTime.now());
          global.setStock(global.getStock()+ item.getCantidad());
          DaoFactory.getInstance().update(sesion, global);
        } // for
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  private boolean toUpdateVencimiento(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idClienteDeuda", this.idClientePago);      
      TcManticClientesDeudasDto deuda= (TcManticClientesDeudasDto)DaoFactory.getInstance().findById(sesion, TcManticClientesDeudasDto.class, this.idClientePago);
      deuda.setLimite(this.limite);
      regresar= DaoFactory.getInstance().update(sesion, deuda)> 0L;  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
	 
	public boolean procesarComplementoPago(Session sesion, List<Documento> documentos) throws Exception {		
		boolean regresar         = Boolean.TRUE;
		Map<String, Object>params= new HashMap<>();
		try {
      DaoFactory.getInstance().insert(sesion, this.control);
      this.pago.setIdClientePagoControl(this.control.getIdClientePagoControl());
      for(Documento cuenta: documentos) {
        Entity deuda= this.toDeudaParticular(sesion, cuenta.getIdDetalle());
        if(deuda!= null && !deuda.isEmpty()) {
          Long idEstatus= cuenta.getInsoluto()<= 0D? EEstatusClientes.FINALIZADA.getIdEstatus(): EEstatusClientes.PARCIALIZADA.getIdEstatus();
          if(this.registrarPago(sesion, deuda, cuenta.getPagado(), -1L, "COMPLEMENTO")) {
            params.put("saldo", cuenta.getInsoluto());
            params.put("idClienteDeudaEstatus", idEstatus);
            DaoFactory.getInstance().update(sesion, TcManticClientesDeudasDto.class, deuda.getKey(), params);
            this.actualizarSaldoCatalogoCliente(sesion, this.idCliente, cuenta.getPagado(), false);
          }	// if				
        } // if
			} // for
		} // try
		catch (Exception e) {			
			this.messageError= "Error al registrar el pago";
			throw e; 
		} // catch		
		finally {
			Methods.clean(params);			
		} // finally
		return regresar;
	} // procesarComplementoPago
 
	private Entity toDeudaParticular(Session sesion, Long idVenta) throws Exception {
		Entity regresar          = null;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("idCliente", this.idCliente);
			params.put("idVenta", idVenta);
			params.put(Constantes.SQL_CONDICION, " tc_mantic_clientes_deudas.saldo> 0 and tc_mantic_clientes_deudas.id_cliente_deuda_estatus not in(1, 2)");			
			regresar= (Entity)DaoFactory.getInstance().toEntity(sesion, "VistaClientesDto", "particular", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
		return regresar;
	} // toDeudaParticular	
  
}