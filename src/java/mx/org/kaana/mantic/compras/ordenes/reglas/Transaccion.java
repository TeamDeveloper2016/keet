package mx.org.kaana.mantic.compras.ordenes.reglas;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.template.backing.Reporte;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.compras.beans.General;
import mx.org.kaana.keet.compras.beans.Individual;
import mx.org.kaana.keet.db.dto.TcKeetArticulosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetOrdenesCodigosDto;
import mx.org.kaana.keet.db.dto.TcKeetOrdenesContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetOrdenesFamiliasDto;
import mx.org.kaana.keet.db.dto.TcKeetOrdenesMaterialesDto;
import mx.org.kaana.keet.db.dto.TrKeetArticuloProveedorClienteDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorTipoContacto;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.beans.Detalle;
import mx.org.kaana.mantic.compras.ordenes.beans.OrdenCompraProcess;
import mx.org.kaana.mantic.compras.ordenes.beans.OrdenFamilia;
import mx.org.kaana.mantic.compras.ordenes.beans.OrdenLote;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.correos.beans.Attachment;
import mx.org.kaana.mantic.correos.enums.ECorreos;
import mx.org.kaana.mantic.correos.reglas.IBaseAttachment;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticFaltantesDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorTipoContactoDto;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.facturas.beans.Correo;
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
	private static final long serialVersionUID=-3186367186737677670L;
 
	private TcManticOrdenesComprasDto orden;	
	private List<Articulo> articulos;
	private String messageError;
	private TcManticOrdenesBitacoraDto bitacora;
	private Long idFaltante;
	private Correo correo;	
	private List<UISelectEntity> lotes;
	private List<UISelectEntity> familias;
  private TcManticOrdenesComprasDto cloneOrdenCompra;
  private OrdenCompraProcess ordenProcess;
  private String razonSocial;	
  private Long idOrdenCodigo;	

	public Transaccion(Long idProveedor, String razonSocial, Correo correo) {
		super(-1L, idProveedor);
		this.razonSocial= razonSocial;
		this.correo= correo;
	}	// Transaccion
  
	public Transaccion(Correo correo, Long idProveedor) {
		super(-1L, idProveedor);
		this.correo= correo;
    this.cloneOrdenCompra= null;
	}	// Transaccion
	
	public Transaccion(TcManticOrdenesComprasDto orden, TcManticOrdenesBitacoraDto bitacora, Long idOrdenCodigo) {
		this(orden);
		this.bitacora= bitacora;
    this.idOrdenCodigo= idOrdenCodigo;
	} // Transaccion
	
	public Transaccion(TcManticOrdenesComprasDto orden) {
		this(orden, new ArrayList<Articulo>());
	} // Transaccion

	public Transaccion(OrdenCompraProcess ordenProcess) {
		this(ordenProcess.getOrdenCompra(), ordenProcess.getArticulos());
    this.ordenProcess= ordenProcess;
		this.familias= ordenProcess.getFamilias();
		this.lotes   = ordenProcess.getLotes();
	} // Transaccion
	
	public Transaccion(TcManticOrdenesComprasDto orden, List<Articulo> articulos) {
		super(orden.getIdAlmacen(), orden.getIdProveedor());
		this.orden    = orden;		
		this.articulos= articulos;
    this.cloneOrdenCompra= null;
	} // Transaccion

	public Transaccion(Long idFaltante) {
		super(-1L, -1L);
		this.idFaltante= idFaltante;
	} // Transaccion
	
	public String getMessageError() {
		return messageError;
	} // getMessageError

  public TcManticOrdenesComprasDto getCloneOrdenCompra() {
    return cloneOrdenCompra;
  }
  
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = Boolean.FALSE;
    Siguiente consecutivo     = null;
		Map<String, Object> params= new HashMap<>();
		try {
			if(this.orden!= null)
				params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" para la orden de compra");
			if(this.orden!= null) {
        if(this.orden.getIdCliente()!= null && this.orden.getIdCliente()< 0)
				  this.orden.setIdCliente(null);
        if(Objects.equals(-1L, this.orden.getIdBanco()))
          this.orden.setIdBanco(null);
        if(Objects.equals(-1L, this.orden.getIdTipoPago()))
          this.orden.setIdTipoPago(null);
      } // if
			switch(accion) {
				case MOVIMIENTOS:
					if(this.orden.isValid()) {
  					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
	  				this.toFillArticulos(sesion);
					} // if
					else {
						consecutivo= this.toSiguiente(sesion);
						this.orden.setConsecutivo(consecutivo.getConsecutivo());
						this.orden.setOrden(consecutivo.getOrden());
						this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
						regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
						this.toFillArticulos(sesion);
						this.bitacora= new TcManticOrdenesBitacoraDto(this.orden.getIdOrdenEstatus(), "", JsfBase.getIdUsuario(), this.orden.getIdOrdenCompra(), -1L, this.orden.getConsecutivo(), this.orden.getTotal());
						DaoFactory.getInstance().insert(sesion, this.bitacora);
					} // else	
      		for (Articulo articulo: this.articulos) 
						articulo.setModificado(false);
          this.toUpdateRequisicion(sesion);
          regresar= this.registrarLotes(sesion);
					break;
				case AGREGAR:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					if(DaoFactory.getInstance().insert(sesion, this.orden)>= 1L) {
						this.toFillArticulos(sesion);
						this.bitacora= new TcManticOrdenesBitacoraDto(this.orden.getIdOrdenEstatus(), "", JsfBase.getIdUsuario(), this.orden.getIdOrdenCompra(), -1L, this.orden.getConsecutivo(), this.orden.getTotal());
						if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L)
							regresar= this.registrarLotes(sesion);
            this.toUpdateRequisicion(sesion);
					} // if
					break;
				case MODIFICAR:
          if(!Objects.equals(this.ordenProcess, null) && this.ordenProcess.getOrdenCompra().isChangeEmpresa()) {
						consecutivo= this.toSiguiente(sesion);
						this.orden.setConsecutivo(consecutivo.getConsecutivo());
						this.orden.setOrden(consecutivo.getOrden());
						this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
          } // if
					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
					this.toFillArticulos(sesion);
					this.bitacora= (TcManticOrdenesBitacoraDto)DaoFactory.getInstance().findFirst(sesion, TcManticOrdenesBitacoraDto.class, this.orden.toMap(), "ultimo");
					if(!this.bitacora.getImporte().equals(this.orden.getTotal())) {
  					this.bitacora= new TcManticOrdenesBitacoraDto(this.orden.getIdOrdenEstatus(), "", JsfBase.getIdUsuario(), this.orden.getIdOrdenCompra(), -1L, this.orden.getConsecutivo(), this.orden.getTotal());
  					DaoFactory.getInstance().insert(sesion, this.bitacora);
					} // if
          this.toUpdateRequisicion(sesion);
					regresar= this.registrarLotes(sesion);
					break;				
				case ELIMINAR:
					regresar= this.toNotExistsNotas(sesion);
					if(regresar) {
						this.orden.setIdOrdenEstatus(2L);
						// regresar= DaoFactory.getInstance().deleteAll(sesion, TcManticOrdenesDetallesDto.class, params)>= 1L;
						regresar= regresar && DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
						this.bitacora= new TcManticOrdenesBitacoraDto(2L, "", JsfBase.getIdUsuario(), this.orden.getIdOrdenCompra(), -1L, this.orden.getConsecutivo(), this.orden.getTotal());
						regresar= DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L;
            DaoFactory.getInstance().deleteAll(sesion, TcKeetOrdenesContratosLotesDto.class, this.orden.toMap());
					} // if	
					else
       			this.messageError= "No se puede eliminar la orden de compra porque existen notas de entrada asociadas.";
					break;
				case JUSTIFICAR:
          Long idOrdenEstatus= this.orden.getIdOrdenEstatus();
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdOrdenEstatus(this.bitacora.getIdOrdenEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
            // MARCAR EL CODIGO DE AUTORIZACIÓN COMO UTILIZADO POR LA ORDEN DE COMPRA
            if(!Objects.equals(this.idOrdenCodigo, -1L) && Objects.equals(this.orden.getIdOrdenEstatus(), 3L)) {
              TcKeetOrdenesCodigosDto codigo= (TcKeetOrdenesCodigosDto)DaoFactory.getInstance().findById(sesion, TcKeetOrdenesCodigosDto.class, this.idOrdenCodigo);
              if(codigo!= null) {
                codigo.setIdOrdenCompra(this.bitacora.getIdOrdenCompra());
                codigo.setUtilizado(LocalDateTime.now());
                codigo.setIdUsuario(JsfBase.getIdUsuario());
                DaoFactory.getInstance().update(sesion, codigo);
              } // if
            } // if
						if(Objects.equals(this.orden.getIdOrdenEstatus(), 7L)) {
							this.toCommonNotaEntrada(sesion, -1L, this.orden.toMap());
              // ESTO ES PARA GENERAR UNA NUEVA ORDEN DE COMPRA PARTIENDO DE LAS PARTIDAS QUE NO FUERON SURTIDAS O QUE LES FALTO POR SURTIR
              TcManticOrdenesComprasDto clone= (TcManticOrdenesComprasDto)orden.clone();
              clone.setIdOrdenCompra(-1L);
              clone.setIdOrdenEstatus(1L);
              consecutivo= this.toSiguiente(sesion);
              clone.setConsecutivo(consecutivo.getConsecutivo());
              clone.setOrden(consecutivo.getOrden());
              clone.setEjercicio(new Long(Fecha.getAnioActual()));
              clone.setObservaciones((clone.getObservaciones()!= null? clone.getObservaciones()+ ", ": "")+ "ORDEN FUENTE "+ this.orden.getConsecutivo());
              this.cloneOrdenCompra= this.toCreateOrdenCompra(sesion, this.orden.getKey(), clone);
            } // if 
            else  // SI SE CANCELA O ELIMINA UNA ORDEN DE COMPRA NOTIFICAR
  						if(Objects.equals(idOrdenEstatus, 3L) && Objects.equals(this.orden.getIdOrdenEstatus(), 2L)) 
                this.notificarCancelacion();
					} // if
					break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, TcManticFaltantesDto.class, this.idFaltante)>= 1L;
					break;
				case COMPLEMENTAR: 
					regresar= this.agregarContacto(sesion, ETiposContactos.CORREO);
					break;
				case COMPLETO: 
					regresar= this.agregarContacto(sesion, ETiposContactos.CELULAR);
					break;
				case RESTAURAR:
          regresar= this.toCloseRequisicion(sesion);
          break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
			Error.mensaje(e);
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
	  } // catch		
	  finally {
      Methods.clean(params);
    } // finally
	if(this.orden!= null)
			LOG.info("Se genero de forma correcta la orden: "+ this.orden.getConsecutivo());
		return regresar;
	}	// ejecutar

	private void toFillArticulos(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "VistaOrdenesComprasDto", "detalle", this.orden.toMap());
    Map<String, Object> params= new HashMap<>();
    try {      
      for (Articulo item: todos) 
        if(this.articulos.indexOf(item)< 0)
          DaoFactory.getInstance().delete(sesion, item.toOrdenDetalle());
      for (Articulo articulo: this.articulos) {
        if(articulo.isValid()) {
          TcManticOrdenesDetallesDto item= articulo.toOrdenDetalle();
          item.setIdOrdenCompra(this.orden.getIdOrdenCompra());
          if(DaoFactory.getInstance().findIdentically(sesion, TcManticOrdenesDetallesDto.class, item.toMap())== null) 
            DaoFactory.getInstance().insert(sesion, item);
          else
            if(articulo.isModificado())
              DaoFactory.getInstance().update(sesion, item);
          params.put("idArticulo", articulo.getIdArticulo());      
          params.put("idEmpresa", this.orden.getIdEmpresa());      
          params.put("precio", articulo.getCosto());      
          params.put("observaciones", "ARTICULO SOLICITADO EN LA ORDEN DE COMPRA ".concat(this.orden.getConsecutivo()).concat(" EL DIA ").concat(Global.format(EFormatoDinamicos.FECHA_HORA_CORTA, this.orden.getRegistro())));
          DaoFactory.getInstance().updateAll(sesion, TcManticFaltantesDto.class, params);
          // ACTUALIZAR EL PRECIOS BASE Y EL PRECIO DEL PROVEEDOR CON BASE AL PRECIO CAPTURADO EN LA ORDEN DE COMPRA
          DaoFactory.getInstance().updateAll(sesion, TcManticArticulosDto.class, params, "precios");
          params.put("idProveedor", this.orden.getIdProveedor());      
          TcKeetArticulosProveedoresDto precio= (TcKeetArticulosProveedoresDto)DaoFactory.getInstance().toEntity(sesion, TcKeetArticulosProveedoresDto.class, "TcKeetArticulosProveedoresDto", "identically", params);
          if(precio== null) {
            precio= new TcKeetArticulosProveedoresDto(
              this.orden.getIdProveedor(), // Long idProveedor, 
              articulo.getCosto(), // Double precioLista, 
              JsfBase.getIdUsuario(), // Long idUsuario, 
              -1L, // Long idArticuloProveedor, 
              articulo.getIdArticulo(), // Long idArticulo, 
              articulo.getCosto(), // Double precioEspecial, 
              articulo.getCosto(), // Double precioBase, 
              LocalDateTime.now() // LocalDateTime actualizado
            );
            DaoFactory.getInstance().insert(sesion, precio);
          } // if
          else {
            precio.setPrecioEspecial(articulo.getCosto());
            precio.setPrecioLista(articulo.getCosto());
            precio.setPrecioBase(articulo.getCosto());
            precio.setActualizado(LocalDateTime.now());
            DaoFactory.getInstance().update(sesion, precio);
          } // else
          // ACTUALIZAR EL PRECIO CONVENIO SI ES QUE EL CLIENTE Y PROVEEDOR TIENE UN PRECIO PACTADO 17/11/2022
          params.put("idCliente", this.orden.getIdCliente());      
          params.put("idUsuario", JsfBase.getIdUsuario());      
          DaoFactory.getInstance().updateAll(sesion, TrKeetArticuloProveedorClienteDto.class, params, "precio");
          DaoFactory.getInstance().updateAll(sesion, TcKeetArticulosProveedoresDto.class, params);
        } // if
      } // for
      if(this.ordenProcess!= null && this.ordenProcess.getOrdenCompra()!= null)
        if(this.ordenProcess.getOrdenCompra().getIndividual()!= null && !this.ordenProcess.getOrdenCompra().getIndividual().isEmpty()) 
          this.toFillIndividual(sesion);
        else
          this.toFillGeneral(sesion);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
	}

	private void toFillGeneral(Session sesion) throws Exception {
    Map<String, Object> params            = new HashMap<>();
		List<TcKeetOrdenesMaterialesDto> todos= null;
    try {      
      if(this.ordenProcess!= null && this.ordenProcess.getOrdenCompra()!= null) {
        params.put("cuales", "not");
        params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
        DaoFactory.getInstance().deleteAll(sesion, TcKeetOrdenesMaterialesDto.class, params);
        sesion.flush();
        todos= (List<TcKeetOrdenesMaterialesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetOrdenesMaterialesDto.class, "TcKeetOrdenesMaterialesDto", "detalle", params);      
        for (TcKeetOrdenesMaterialesDto item: todos) {
          int index= this.ordenProcess.getOrdenCompra().getGeneral().indexOf(new General(item.getIdOrdenCompra(), item.getIdArticulo()));
          if(index< 0)
            DaoFactory.getInstance().delete(sesion, item);
        } // for  
        if(this.ordenProcess.getOrdenCompra().getGeneral()!= null)
          for (General item: this.ordenProcess.getOrdenCompra().getGeneral()) {
            item.setIdContratoLote(null);
            item.setCantidad(item.getTotal());
            item.setIdUsuario(JsfBase.getIdUsuario());
            if(item.isValid()) {
              item.setRegistro(LocalDateTime.now());
              DaoFactory.getInstance().update(sesion, item);
            } // if  
            else {
              item.setIdOrdenCompra(orden.getIdOrdenCompra());
              DaoFactory.getInstance().insert(sesion, item);
            } // else  
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
  
	private void toFillIndividual(Session sesion) throws Exception {
    Map<String, Object> params            = new HashMap<>();
		List<TcKeetOrdenesMaterialesDto> todos= null;
    try {      
      if(this.ordenProcess!= null && this.ordenProcess.getOrdenCompra()!= null) {
        params.put("cuales", "");
        params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
        DaoFactory.getInstance().deleteAll(sesion, TcKeetOrdenesMaterialesDto.class, params);
        sesion.flush();
        todos= (List<TcKeetOrdenesMaterialesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetOrdenesMaterialesDto.class, "TcKeetOrdenesMaterialesDto", "detalle", params);      
        for (TcKeetOrdenesMaterialesDto item: todos) {
          int index= this.ordenProcess.getOrdenCompra().getIndividual().indexOf(new Individual(item.getIdOrdenCompra(), item.getIdContratoLote(), item.getIdArticulo()));
          if(index< 0)
            DaoFactory.getInstance().delete(sesion, item);
        } // for  
        if(this.ordenProcess.getOrdenCompra().getIndividual()!= null) 
          for (Individual item: this.ordenProcess.getOrdenCompra().getIndividual()) {
            item.setCantidad(item.getTotal());
            item.setIdUsuario(JsfBase.getIdUsuario());
            if(item.isValid()) {
              item.setRegistro(LocalDateTime.now());
              DaoFactory.getInstance().update(sesion, item);
            } // if  
            else {
              item.setIdOrdenCompra(this.orden.getIdOrdenCompra());
              DaoFactory.getInstance().insert(sesion, item);
            } // else  
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
  
  
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getIdEmpresa());
		  params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticOrdenesComprasDto", "siguiente", params, "siguiente");
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
	
	private boolean toNotExistsNotas(Session sesion) throws Exception {
		boolean regresar= true;
		Value total= DaoFactory.getInstance().toField(sesion, "TcManticNotasEntradasDto", "existe", this.orden.toMap(), "total");
		if(total.getData()!= null)
		  regresar= total.toLong()<= 0;
		return regresar;
	}
	
	private boolean agregarContacto(Session sesion, ETiposContactos tipo) throws Exception {
		boolean regresar                         = true;
		List<ProveedorTipoContacto> correos      = null;
		TrManticProveedorTipoContactoDto contacto= null;
		int count                                = 0;
		Long records                             = 1L;
		try {
			correos= this.toProveedoresTipoContacto();
			if(!correos.isEmpty()){
				for(ProveedorTipoContacto tipoContacto: correos){
					if(tipoContacto.getValor().equals(this.correo.getDescripcion())) {
						count++;
            tipoContacto.setIdPreferido(this.correo.getIdPreferido());
            // NOTIFICAR AL PROVEEDOR SI ES QUE CAMBIO SU TIPO DE CONTACTO PREFERIDO
            if(ETiposContactos.CELULAR.equals(tipo) && tipoContacto.getIdPreferido().equals(1L)) {
              Cafu notificar= new Cafu(this.razonSocial, tipoContacto.getValor());
              notificar.setCorreo(ECorreos.COMPRAS.getEmail());
              notificar.doSendProveedor(sesion);
            } // if  
            regresar= DaoFactory.getInstance().update(sesion, tipoContacto)>= 1L;
          } // if  
				} // for				
				records= correos.size() + 1L;
			} // if
			if(count== 0){
				contacto= new TrManticProveedorTipoContactoDto();
				contacto.setIdProveedor(this.idProveedor);
        contacto.setIdTipoContacto(tipo.getKey());
				contacto.setIdUsuario(JsfBase.getIdUsuario());
				contacto.setValor(this.correo.getDescripcion());
				contacto.setIdPreferido(this.correo.getIdPreferido());
				contacto.setOrden(records);
				regresar= DaoFactory.getInstance().insert(sesion, contacto)>= 1L;
        // NOTIFICAR AL PROVEEDOR SI ES QUE CAMBIO SU TIPO DE CONTACTO PREFERIDO
        if(ETiposContactos.CELULAR.equals(tipo) && contacto.getIdPreferido().equals(1L)) {
          Cafu notificar= new Cafu(this.razonSocial, contacto.getValor());
          notificar.setCorreo(ECorreos.COMPRAS.getEmail());
          notificar.doSendProveedor(sesion);
        } // if  
			} // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // agregarContacto
	
	public List<ProveedorTipoContacto> toProveedoresTipoContacto() throws Exception {
		List<ProveedorTipoContacto> regresar= null;
		Map<String, Object>params           = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idProveedor + " and id_tipo_contacto=" + ETiposContactos.CORREO.getKey());
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorTipoContacto.class, "TrManticProveedorTipoContactoDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toClientesTipoContacto
	
	private boolean registrarLotes(Session sesion) throws Exception {
		Boolean regresar                           = Boolean.TRUE;
		TcKeetOrdenesContratosLotesDto contratoLote= null;
		try {
      List<OrdenLote> items= (List<OrdenLote>)DaoFactory.getInstance().toEntitySet(sesion, OrdenLote.class, "TcKeetOrdenesContratosLotesDto", "lotes", this.orden.toMap());
      if(this.lotes!= null) {
        for(Object lote: this.lotes) {
          contratoLote= new TcKeetOrdenesContratosLotesDto();
          contratoLote.setIdContratoLote(((UISelectEntity)lote).getKey());
          contratoLote.setIdOrdenCompra(this.orden.getIdOrdenCompra());
          contratoLote.setIdUsuario(JsfBase.getIdUsuario());
          OrdenLote existe= new OrdenLote(-1L, ((UISelectEntity)lote).getKey(), this.orden.getIdOrdenCompra());
          int index= items.indexOf(existe);
          if(index< 0)
            DaoFactory.getInstance().insert(sesion, contratoLote);
          else
            items.get(index).setExiste(Boolean.TRUE);
        } // for
        for (OrdenLote item: items) {
          if(!item.getExiste())
            DaoFactory.getInstance().delete(sesion, TcKeetOrdenesContratosLotesDto.class, item.getIdOrdenContratoLote());
        } // for
      } // if  
      regresar= this.registrarFamilias(sesion);
		} // try 
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // registrarLotes
  
	private boolean registrarFamilias(Session sesion) throws Exception {
		Boolean regresar                        = Boolean.FALSE;
		TcKeetOrdenesFamiliasDto contratoFamilia= null;
		try {
      List<OrdenFamilia> items= (List<OrdenFamilia>)DaoFactory.getInstance().toEntitySet(sesion, OrdenFamilia.class, "TcKeetOrdenesFamiliasDto", "familias", this.orden.toMap());
      if(this.familias!= null) {
        for(Object familia: this.familias) {
          contratoFamilia= new TcKeetOrdenesFamiliasDto();
          contratoFamilia.setIdFamilia(((UISelectEntity)familia).getKey());
          contratoFamilia.setIdOrdenCompra(this.orden.getIdOrdenCompra());
          contratoFamilia.setIdUsuario(JsfBase.getIdUsuario());
          OrdenFamilia existe= new OrdenFamilia(-1L, ((UISelectEntity)familia).getKey(), this.orden.getIdOrdenCompra());
          int index= items.indexOf(existe);
          if(index< 0)
            DaoFactory.getInstance().insert(sesion, contratoFamilia);
          else
            items.get(index).setExiste(Boolean.TRUE);
        } // for
        for (OrdenFamilia item: items) {
          if(!item.getExiste())
            DaoFactory.getInstance().delete(sesion, TcKeetOrdenesFamiliasDto.class, item.getIdOrdenFamilia());
        } // for
      } // if  
      regresar= Boolean.TRUE;      
		} // try 
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // registrarFamilias

	private Reporte doReporte(String nombre) throws Exception {
    Reporte regresar             = null;
		Parametros comunes           = null;
		Map<String, Object>params    = new HashMap();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
		try{		
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
      params.put("sortOrder", "order by tc_mantic_ordenes_compras.id_empresa, tc_mantic_ordenes_compras.ejercicio, tc_mantic_ordenes_compras.orden");
      reporteSeleccion= EReportes.valueOf(nombre);
      params.put("idOrdenCompra", this.orden.getIdOrdenCompra());
      comunes= new Parametros(this.orden.getIdEmpresa(), this.orden.getIdAlmacen(), this.orden.getIdProveedor(), -1L);
      regresar= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getTitulo().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("/resources/janal/img/sistema/"));
      if(reporteSeleccion.equals(EReportes.ORDEN_DETALLE)) 
        parametros.put("REPORTE_FIRMA", JsfBase.getRealPath("/Paginas/Mantic/Catalogos/Empleados/Firmas/"));
      regresar.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));					
      regresar.doAceptarSimple();			
    } // try
    catch(Exception e) {
      throw e;
    } // catch	
    return regresar;
  } 
  
  private void notificarCancelacion() throws Exception {
		StringBuilder sb          = new StringBuilder("");
		Map<String, Object> params= new HashMap<>();
		String[] emails           = null;
    switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
      case "cafu":
        sb.append("auxadministrativo@cafuconstrucciones.com");
        emails= sb.toString().split("[,]");
        break;
      case "gylvi":
		    emails= sb.toString().split("[,]");
        break;
      case "triana":
     		emails= sb.toString().split("[,]");
        break;
    } // swtich   
		List<Attachment> files= new ArrayList<>(); 
		try {
			params.put("header", "...");
			params.put("footer", "...");
			params.put("empresa", JsfBase.getAutentifica().getEmpresa().getNombre());
			params.put("tipo", "Orden de compra");			
			params.put("razonSocial", "<<< CANCELADO >>>");
			params.put("correo", ECorreos.ORDENES_CANCEL.getEmail());		
      params.put("solucion", Configuracion.getInstance().getEmpresa("titulo"));
      params.put("url", Configuracion.getInstance().getPropiedadServidor("sistema.dns"));
			Reporte reporte= this.doReporte("ORDEN_DETALLE");
			Attachment attachments= new Attachment(reporte.getNombre(), Boolean.FALSE);
			files.add(attachments);
			files.add(new Attachment("logo", ECorreos.ORDENES_CANCEL.getImages().concat(Configuracion.getInstance().getEmpresa("logo")), Boolean.TRUE));
			params.put("attach", attachments.getId());
			for (String item: emails) {
				try {
					if(!Cadena.isVacio(item)) {
					  IBaseAttachment notificar= new IBaseAttachment(ECorreos.ORDENES_CANCEL, ECorreos.ORDENES_CANCEL.getEmail(), item, ECorreos.ORDENES_CANCEL.getBackup(), Configuracion.getInstance().getEmpresa("titulo").concat(" - Orden de compra"), params, files);
					  LOG.info("Enviando correo a la cuenta: "+ item);
					  notificar.send();
					} // if	
				} // try
				finally {
				  if(attachments.getFile().exists()) {
   	  	    LOG.info("Eliminando archivo temporal: "+ attachments.getAbsolute());
				    // user.getFile().delete();
				  } // if	
				} // finally	
			} // for
	  	LOG.info("Se envio el correo de forma exitosa");
		} // try // try
		catch(Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(files);
		} // finally    
  }
  
  private void toUpdateRequisicion(Session sesion) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {      
      for (Detalle item: this.ordenProcess.getOrdenCompra().getDetalles()) {
        switch(item.getSql()) {
          case INSERT:
            item.setIdUsuario(JsfBase.getIdUsuario());
            item.setIdOrdenCompra(this.ordenProcess.getOrdenCompra().getIdOrdenCompra());
            DaoFactory.getInstance().insert(sesion, item);
            break;
          case UPDATE:
            item.setIdUsuario(JsfBase.getIdUsuario());
            item.setRegistro(LocalDateTime.now());
            DaoFactory.getInstance().update(sesion, item);
            break;
          case DELETE:
            DaoFactory.getInstance().delete(sesion, item);
            break;
          case SELECT:
            break;
        } // switch
      } // for
      int index= 0;
      while(index< this.ordenProcess.getOrdenCompra().getDetalles().size()) {
        Detalle item= this.ordenProcess.getOrdenCompra().getDetalles().get(index);
        switch(item.getSql()) {
          case SELECT:
          case INSERT:
          case UPDATE:
            item.setSql(ESql.SELECT);
            index++;
            break;
          case DELETE:
            this.ordenProcess.getOrdenCompra().getDetalles().remove(index);
            break;
          default:
            index++;
            break;
        } // switch
      } // while 
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
	private Boolean toCloseRequisicion(Session sesion) throws Exception {
		Boolean regresar          = Boolean.TRUE;
		Map<String, Object> params= new HashMap<>();
		try {
      DaoFactory.getInstance().updateAll(sesion, TcManticRequisicionesDto.class, params, "completa");
      DaoFactory.getInstance().updateAll(sesion, TcManticRequisicionesDto.class, params, "solicitada");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
  }  
  
} 