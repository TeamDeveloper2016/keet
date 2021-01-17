package mx.org.kaana.mantic.inventarios.entradas.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntradaDirecta;
import mx.org.kaana.mantic.inventarios.entradas.reglas.AdminNotas;
import mx.org.kaana.mantic.inventarios.entradas.reglas.Proceso;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.operation.Delete;
import mx.org.kaana.kajool.db.comun.operation.IActions;
import mx.org.kaana.kajool.db.comun.operation.Insert;
import mx.org.kaana.kajool.db.comun.operation.Select;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEmpleado;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaProyecto;
import mx.org.kaana.mantic.inventarios.entradas.enums.EGastos;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticInventariosEntradasDirecta")
@ViewScoped
public class Directa extends IBaseArticulos implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Directa.class);
  private static final long serialVersionUID= 327393488565639367L;
	
	private EAccion accion;	
	private EOrdenes tipoOrden;
	private TcManticProveedoresDto proveedor;
	private Calendar fechaEstimada;
  private NotaProyecto proyecto;
  private NotaEmpleado empleado;

	public String getValidacion() {
		return this.tipoOrden.equals(EOrdenes.NORMAL)? "libre": "requerido";
	}
	
	public Boolean getIsDirecta() {
		return this.tipoOrden.equals(EOrdenes.NORMAL);
	}

	public Boolean getIsAplicar() {
		Boolean regresar= true;
		try {
			regresar= JsfBase.isAdminEncuestaOrAdmin();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return regresar;
	}
	
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}

	public TcManticProveedoresDto getProveedor() {
		return proveedor;
	}

	public Boolean getDiferente() {
	  return this.getEmisor()!= null && this.proveedor!= null &&	!this.getEmisor().getRfc().equals(this.proveedor.getRfc());
	}

  public NotaProyecto getProyecto() {
    return proyecto;
  }

  public void setProyecto(NotaProyecto proyecto) {
    this.proyecto = proyecto;
  }

  public NotaEmpleado getEmpleado() {
    return empleado;
  }

  public void setEmpleado(NotaEmpleado empleado) {
    this.empleado = empleado;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.proyecto= new NotaProyecto(new Long((int)(Math.random()*-10000)));
      this.empleado= new NotaEmpleado(new Long((int)(Math.random()*-10000)));
			// if(JsfBase.getFlashAttribute("accion")== null && JsfBase.getParametro("zOyOxDwIvGuCt")== null)
			//	UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.tipoOrden= EOrdenes.NORMAL;
      this.accion   = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idNotaEntrada", JsfBase.getFlashAttribute("idNotaEntrada")== null? -1L: JsfBase.getFlashAttribute("idNotaEntrada"));
      this.attrs.put("idOrdenCompra", -1L);
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_FACTURA);
			this.fechaEstimada= Calendar.getInstance();
			this.attrs.put("isBanco", Boolean.FALSE);
      this.attrs.put("folio", "");
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
		TcManticOrdenesComprasDto ordenCompra= null;
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.setAdminOrden(new AdminNotas(new NotaEntradaDirecta(-1L, (Long)this.attrs.get("idOrdenCompra")), this.tipoOrden));
          ordenCompra= this.attrs.get("idOrdenCompra").equals(-1L)? new TcManticOrdenesComprasDto(): (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, (Long)this.attrs.get("idOrdenCompra"));
					if(this.tipoOrden.equals(EOrdenes.NORMAL)) {
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(new Entity(-1L)));
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(-1L)));
					} // if
					else {
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIdEmpresa(ordenCompra.getIdEmpresa());
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkDesarrollo(new UISelectEntity(new Entity(ordenCompra.getIdDesarrollo())));
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkCliente(new UISelectEntity(new Entity(ordenCompra.getIdCliente())));
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkContrato(new UISelectEntity(new Entity(ordenCompra.getIdContrato())));
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(ordenCompra.getIdProveedor())));
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedorPago(new UISelectEntity(new Entity(ordenCompra.getIdProveedorPago())));
            if(ordenCompra.getIdBanco()!= null) 
  						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkBanco(new UISelectEntity(new Entity(ordenCompra.getIdBanco())));
            if(ordenCompra.getIdTipoMedioPago()!= null) 
	  					((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkTipoMedioPago(new UISelectEntity(new Entity(ordenCompra.getIdTipoMedioPago())));
            if(ordenCompra.getIdTipoPago()!= null) 
						  ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkTipoPago(new UISelectEntity(new Entity(ordenCompra.getIdTipoPago())));
						this.fechaEstimada.setTimeInMillis(ordenCompra.getRegistro().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
					} // else	
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					NotaEntradaDirecta notaEntrada= (NotaEntradaDirecta)DaoFactory.getInstance().toEntity(NotaEntradaDirecta.class, "TcManticNotasEntradasDto", "detalle", this.attrs);
					ordenCompra   = (TcManticOrdenesComprasDto) DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, notaEntrada.getIdOrdenCompra());
					this.tipoOrden= notaEntrada.getIdNotaTipo().equals(1L)? EOrdenes.NORMAL: EOrdenes.PROVEEDOR;
          this.setAdminOrden(new AdminNotas(notaEntrada, this.tipoOrden));
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
					
          // ESTO ES PARA CARGAR LOS ARTICULOS DE LA FACTURA CUANDO SE ENTRA POR LA OPCION DE MODIFICAR Y VUELVA A HACER LA COMPARACION DE LOS ARTICULOS
					this.doLoadFiles("TcManticNotasArchivosDto", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdNotaEntrada(), "idNotaEntrada", (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
					this.toPrepareDisponibles(false);
          break;
      } // switch
			this.attrs.put("ordenCompra", ordenCompra);
			this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
			//this.doResetDataTable();
			this.toLoadCatalog();
      this.toLoadProveedor();
      this.toLoadBancos();
      this.toLoadTiposMediosPagos();
      this.toLoadTiposPagos();
      this.toLoadEmpresaTipoContacto();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Proceso transaccion    = null;
    String regresar        = null;
    try {			
      if(!Cadena.isVacio((String)this.attrs.get("observaciones"))) {
        if(this.getXml()!= null)
          this.getXml().setObservaciones((String)this.attrs.get("observaciones")); 
        if(this.getPdf()!= null)
          this.getPdf().setObservaciones((String)this.attrs.get("observaciones"));
      } // if
      if(this.getFactura()!= null) {
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDescuentos(0D);
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setExcedentes(0D);
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setImpuestos(Numero.getDouble(this.getFactura().getImpuesto().getTraslado().getImporte(), 0D));
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setSubTotal(Numero.getDouble(this.getFactura().getSubTotal(), 0D));
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setTotal(Numero.getDouble(this.getFactura().getTotal(), 0D));
      } // if  
      if(Cadena.isVacio(this.attrs.get("folio"))) {
        if(!Cadena.isVacio(this.getXml())) {
          if(this.getReceptor().getRfc().equals(this.proveedor.getRfc())) {
            transaccion = new Proceso((NotaEntradaDirecta)this.getAdminOrden().getOrden(), this.getXml(), this.getPdf());
            if (transaccion.ejecutar(this.accion)) {
              if(this.accion.equals(EAccion.AGREGAR)) {
                regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
                if(this.accion.equals(EAccion.AGREGAR))
                  UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la nota de entrada', '"+ ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
                else
                  UIBackingUtilities.execute("jsArticulos.back('aplic\\u00F3 la nota de entrada', '"+ ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
              } // if	
              else
                this.getAdminOrden().toStartCalculate();
              if(!this.accion.equals(EAccion.CONSULTAR)) 
                JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la nota de entrada."), ETipoMensaje.INFORMACION);
              JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdNotaEntrada());
            } // if
            else 
              JsfBase.addMessage("Ocurrió un error al registrar la nota de entrada.", ETipoMensaje.ERROR);      			
          } // if  
          else 
            JsfBase.addMessage("El RFC del proveedor no coincide con el RFC de la factura !", ETipoMensaje.ERROR);
        } // if
        else 
          JsfBase.addMessage("Se tiene que importar el documento XML de la factura !", ETipoMensaje.ERROR);
      } // if
      else 
        JsfBase.addMessage((String)this.attrs.get("folio"), ETipoMensaje.ERROR);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdNotaEntrada());
		JsfBase.setFlashAttribute("xcodigo", this.attrs.get("xcodigo"));	
		if(this.getAdminOrden()!= null && this.getAdminOrden().getOrden()!= null && ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdNotaEntrada()<= 0L) {
			if(this.getXml()!= null && this.getXml().getRuta()!= null) {
			  File oldNameFile= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getXml().getRuta()).concat(this.getXml().getName()));
			  oldNameFile.delete();
			} // if	
			if(this.getPdf()!= null && this.getPdf().getRuta()!= null) {
			  File oldNameFile= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getPdf().getRuta()).concat(this.getPdf().getName()));
			  oldNameFile.delete();
			} // if	
		} // 
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar

	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("idOrdenCompra", this.attrs.get("idOrdenCompra"));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				this.attrs.put("idPedidoSucursal", empresas.get(0));
				if(this.accion.equals(EAccion.AGREGAR))
  				((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(0));
			  else 
				  ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(empresas.indexOf(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkEmpresa())));
			} // if	
  		params.put("sucursales", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkEmpresa());
      columns.remove(0);
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("proveedores", UIEntity.seleccione("VistaOrdenesComprasDto", "moneda", params, columns, "razonSocial"));
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			int index= 0;
			if(!proveedores.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR) && ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedor().getKey().equals(-1L))
			   ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(0));
				else {
				  index= proveedores.indexOf(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedor());
				  ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(index));
				} // else
        UISelectEntity temporal= proveedores.get(index);
				temporal.put("fechaEstimada", new Value("fechaEstimada", Global.format(EFormatoDinamicos.FECHA_CORTA, this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias")))));
		    this.attrs.put("proveedor", temporal);
			  this.proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedor().getKey());
  			if(this.proveedor!= null) 
  				this.toLoadCondiciones(new UISelectEntity(new Entity(this.proveedor.getIdProveedor())));
				if(this.accion.equals(EAccion.AGREGAR))
					this.doCalculateFechaPago();
			} // if	
			this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}

	public void doLoadDesarrollos() {
		List<Columna> columns           = null;
    Map<String, Object> params      = null;
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
    try {
			params= new HashMap<>();			
			if(this.accion.equals(EAccion.AGREGAR))
        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdEmpresa());
			else
				params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
      if(desarrollo!= null && !desarrollos.isEmpty()) {
        desarrollo = desarrollos.get(0);				
        this.attrs.put("desarrollosProyecto", desarrollos);			
        this.attrs.put("desarrollosEmpleado", desarrollos);			
        this.proyecto.setIkDesarrollo(desarrollo);			
        this.proyecto.setIdCliente(desarrollo.toLong("idCliente"));
        this.proyecto.setCliente(desarrollo.toString("razonSocial"));
        this.empleado.setIkDesarrollo(desarrollo);
        this.empleado.setIdCliente(desarrollo.toLong("idCliente"));
        this.empleado.setCliente(desarrollo.toString("razonSocial"));
      } // if
      else {
        this.attrs.put("desarrollosProyecto", null);			
        this.attrs.put("desarrollosEmpleado", null);			
        this.proyecto.setIkDesarrollo(new UISelectEntity(-1L));			
        this.empleado.setIkDesarrollo(new UISelectEntity(-1L));
      } // else
      this.doLoadContratosProyecto();
      this.doLoadContratosEmpleado();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos
	
	public void doUpdateClienteProyecto() {
		List<UISelectEntity> desarrollos= null;
		List<Columna> columns           = null;
		Map<String, Object>params       = null;
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollosProyecto");
      this.proyecto.setIkDesarrollo(desarrollos.get(desarrollos.indexOf(this.proyecto.getIkDesarrollo())));
			this.proyecto.setDesarrollo(this.proyecto.getIkDesarrollo().toString("nombres"));			
			this.proyecto.setIkCliente(new UISelectEntity(this.proyecto.getIkDesarrollo().toLong("idCliente")));
			this.proyecto.setCliente(this.proyecto.getIkDesarrollo().toString("razonSocial"));			
			this.doLoadContratosProyecto();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doUpdateClienteProyecto
  
	public void doUpdateClienteEmpleado() {
		List<UISelectEntity> desarrollos= null;
		List<Columna> columns           = null;
		Map<String, Object>params       = null;
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollosEmpleado");
      this.empleado.setIkDesarrollo(desarrollos.get(desarrollos.indexOf(this.empleado.getIkDesarrollo())));
			this.empleado.setDesarrollo(this.empleado.getIkDesarrollo().toString("nombres"));			
			this.empleado.setIkCliente(new UISelectEntity(this.empleado.getIkDesarrollo().toLong("idCliente")));
			this.empleado.setCliente(this.empleado.getIkDesarrollo().toString("razonSocial"));			
			this.doLoadContratosEmpleado();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doUpdateClienteEmpleado
  
	public void doLoadContratosProyecto() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.proyecto.getIdDesarrollo());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratosProyecto", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR) && Objects.equals(-1L, this.proyecto.getIkContrato().getKey()))
          this.proyecto.setIkContrato(contratos.get(0));
        else  
          this.proyecto.setIkContrato(contratos.get(contratos.indexOf(this.proyecto.getIkContrato())));
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratosProyecto
	
	public void doLoadContratosEmpleado() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.empleado.getIdDesarrollo());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratosEmpleado", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR) && Objects.equals(-1L, this.empleado.getIkContrato().getKey()))
          this.empleado.setIkContrato(contratos.get(0));
        else  
          this.empleado.setIkContrato(contratos.get(contratos.indexOf(this.empleado.getIkContrato())));
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratosEmpleado
	
	public void toLoadProveedor() {
		try {
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			UISelectEntity temporal= ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedor();
			temporal= proveedores.get(proveedores.indexOf(temporal));
			temporal.put("fechaEstimada", new Value("fechaEstimada", this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias"))));
			this.attrs.put("proveedor", temporal);
			this.proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, temporal.getKey());
			this.toLoadCondiciones(proveedores.get(proveedores.indexOf((UISelectEntity)((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedor())));
			this.doUpdatePlazo();
		}	
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 
	
	public void doUpdateProveedor() {
		try {
      this.toLoadProveedor();
		}	
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 
	
	private String toCalculateFechaEstimada(Calendar fechaEstimada, int tipoDia, int dias) {
		fechaEstimada.set(Calendar.DATE, fechaEstimada.get(Calendar.DATE)+ dias);
		if(tipoDia== 2) {
			fechaEstimada.add(Calendar.DATE, ((int)(dias/5)* 2));
			int dia= fechaEstimada.get(Calendar.DAY_OF_WEEK);
			dias= dia== Calendar.SUNDAY? 1: dia== Calendar.SATURDAY? 2: 0;
			fechaEstimada.add(Calendar.DATE, dias);
		} // if
		return Global.format(EFormatoDinamicos.FECHA_CORTA, new Date(fechaEstimada.getTimeInMillis()));
	}

  private void toLoadCondiciones(UISelectEntity proveedor) {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("idProveedor", proveedor.getKey());
      this.attrs.put("condiciones", UIEntity.build("VistaOrdenesComprasDto", "condiciones", params, columns));
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
			if(!condiciones.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR) && ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedorPago()== null)
				  ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				else {
					Entity entity= new UISelectEntity(new Entity(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdProveedorPago()));
					if(condiciones.indexOf(entity)>= 0)
				    ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(condiciones.indexOf(entity)));
 					else
  				  ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				} // if	
				((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDescuento(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
				if(this.accion.equals(EAccion.AGREGAR))
          this.doUpdatePorcentaje();
			} // if
    } // try
    catch (Exception e) {
			Error.mensaje(e);
			//JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "Importar":
     		if(this.attrs.get("disponibles")== null)
		  	  this.doLoadFiles("TcManticNotasArchivosDto", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdNotaEntrada(), "idNotaEntrada", false, this.getAdminOrden().getTipoDeCambio());
        break;
    } // switch    
	}
	
	public void doFileUpload(FileUploadEvent event) {
		this.attrs.put("relacionados", 0);
		if(this.proveedor!= null) {
			this.doFileUpload(event, ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getFechaFactura().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"), this.proveedor.getClave(), (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
			if(event.getFile().getFileName().toUpperCase().endsWith(EFormatos.XML.name())) {
				((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setFactura(this.getFactura().getFolio());
				((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setFechaFactura(Fecha.toLocalDateDefault(this.getFactura().getFecha()));
				((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setOriginal(Numero.toRedondearSat(Double.parseDouble(this.getFactura().getTotal())));
				if(this.tipoOrden.equals(EOrdenes.NORMAL)) {
					int count= 0;
					while(count< this.getAdminOrden().getArticulos().size() && this.getAdminOrden().getArticulos().size()> 1) {
//						if(this.getAdminOrden().getArticulos().get(count).getIdOrdenDetalle()== null)
							this.getAdminOrden().getArticulos().remove(count);
//						count++;
					} // while 
				} // if
				this.toMoveSelectedProveedor();
				this.toPrepareDisponibles(true);
				this.doCheckFolio();
				this.doCalculatePagoFecha();
			} // if
		} // if
		else 
			JsfBase.addMessage("Se tiene que seleccionar un proveedor primero.", ETipoMensaje.ALERTA);      			
	} // doFileUpload	
	
	private void toPrepareDisponibles(boolean checkItems) {
		List<Articulo> disponibles= new ArrayList<>();
		for (Articulo disponible : this.getAdminOrden().getArticulos()) {
			if(disponible.getIdArticulo()> -1L)
				disponibles.add(disponible);
		} // for
		Collections.sort(disponibles);
		this.attrs.put("disponibles", disponibles);
	}

	public void doCheckFolio() {
		Map<String, Object> params=null;
		try {
      this.attrs.put("folio", "");
			params=new HashMap<>();
			params.put("factura", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getFactura());
			params.put("idProveedor", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdProveedor());
			params.put("idNotaEntrada", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdNotaEntrada());
			int month= Calendar.getInstance().get(Calendar.MONTH);
			if(month<= 5) {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0101");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "0630");
			} // if
			else {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0701");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "1231");
			} // else
			Entity entity= (Entity)DaoFactory.getInstance().toEntity("TcManticNotasEntradasDto", "folio", params);
			if(entity!= null && entity.size()> 0) {
				UIBackingUtilities.execute("$('#contenedorGrupos\\\\:factura').val('');janal.show([{summary: 'Error:', detail: 'El folio ["+ ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getFactura()+ "] se registró en la nota de entrada "+ entity.toString("consecutivo")+ ", el dia "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs.'}]);");
        this.attrs.put("folio", "El folio ["+ params.get("factura")+ "] se registró en la nota de entrada con consecutivo "+ entity.toString("consecutivo")+ ", el dia "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs.");        
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}
	
	public void doCalculateFechaPago() {
		LocalDate fechaFactura= ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getFechaFactura();
		if(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getDiasPlazo()== null)
			((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDiasPlazo(1L);
		LocalDate calendar= fechaFactura.plusDays(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getDiasPlazo().intValue()- 1);
		((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setFechaPago(calendar);
	}

	public void doCalculatePagoFecha() {
		LocalDate fechaFactura= ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getFechaFactura();
		LocalDate fechaPago   = ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getFechaPago();
		((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDiasPlazo(DAYS.between(fechaFactura, fechaPago));
	}

	public StreamedContent doFileDownload() {
		return this.doPdfFileDownload(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}	
	
	public void doViewDocument() {
		this.doViewDocument(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}

	public void doViewFile() {
		this.doViewFile(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"));
	}

	public void doUpdateRfc() {
		this.doUpdateRfc(this.proveedor);
	}

  public void doUpdatePlazo() {
		if(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedorPago()!= null) {
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
			int index= condiciones.indexOf(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedorPago());
			if(index>= 0) {
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(index));
		  	((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
        this.doCalculateFechaPago();		
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDescuento(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
			} // if
			else {
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedorPago(null);
		  	((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDiasPlazo(0L);
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDescuento("0");
			} // else
			this.doUpdatePorcentaje();
		}
	}	
	
	public void doLoadXmlFile() {
		try {
			if(this.getXml()!= null) {
				String alias= Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getXml().getRuta()).concat(this.getXml().getName());
				this.toReadFactura(new File(alias), (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
				// VERIFICAR SI ES UNA NOTA DE ENTRADA DIRECTA Y CAMBIAR EL PROVEEDOR QUE SE TIENE POR EL QUE SE CARGANDO DE LA FACTURA 
				// EN CASO DE QUE NO EXISTA MANDAR UN MENSAJE DE QUE ESE PROVEEDOR NO EXISTE EN EL CATALAGO DE PROVEEDORES PARA QUE SE AGREGUE
				this.toMoveSelectedProveedor();
				this.toPrepareDisponibles(true);
			} // if	
	  }	// try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
	}

	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	}
	
	@Override
	public void toSaveRecord() {
    Proceso transaccion= null;
    try {			 
      if(this.getFactura()!= null) {
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setDescuentos(0D);
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setExcedentes(0D);
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setImpuestos(Numero.getDouble(this.getFactura().getImpuesto().getTraslado().getImporte(), 0D));
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setSubTotal(Numero.getDouble(this.getFactura().getSubTotal(), 0D));
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setTotal(Numero.getDouble(this.getFactura().getTotal(), 0D));
      } // if  
			transaccion = new Proceso((NotaEntradaDirecta)this.getAdminOrden().getOrden(), this.getXml(), this.getPdf());
			if (transaccion.ejecutar(this.accion)) {
 			  UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 la nota de entrada', '"+ ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
				this.accion= EAccion.MODIFICAR;
				this.getAdminOrden().getArticulos().add(new Articulo(-1L));
				this.attrs.put("autoSave", Global.format(EFormatoDinamicos.FECHA_HORA, Fecha.getRegistro()));
			} // if	
      if(Objects.equals(null, ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdBanco()))
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIdBanco(-1L);
      if(Objects.equals(null, ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdTipoPago()))
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIdTipoPago(-1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	}
	
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
		if(isViewException && this.getAdminOrden().getArticulos().size()> 0)
		  this.toSaveRecord();
    //UIBackingUtilities.execute("alert('ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA');");
	}
	
  private void toMoveSelectedProveedor() {
		UISelectEntity temporal   = (UISelectEntity)this.attrs.get("proveedor");
	  Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			if(this.tipoOrden.equals(EOrdenes.NORMAL)) {
			  this.getAdminOrden().toCalculate();
				if(temporal== null || !this.getEmisor().getRfc().equals(temporal.toString("rfc"))) {
					params.put("rfc", this.getEmisor().getRfc());
					params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
					TcManticProveedoresDto encontrado= (TcManticProveedoresDto)DaoFactory.getInstance().findFirst(TcManticProveedoresDto.class, "proveedor", params);
					if(encontrado!= null) {
						String newFileName= this.getXml().getRuta().replaceAll("/"+ (this.proveedor.getClave()!= null? this.proveedor.getClave().trim(): "NoDefinido")+ "/", "/"+ (encontrado.getClave()!= null? encontrado.getClave().trim(): "NoDefinido")+ "/");
						this.proveedor= encontrado;
						temporal= new UISelectEntity(new Entity(encontrado.getIdProveedor()));
						((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkProveedor(temporal);
						List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");						
						temporal= proveedores.get(proveedores.indexOf(temporal));
						temporal.put("fechaEstimada", new Value("fechaEstimada", this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias"))));
						this.attrs.put("proveedor", temporal);
						this.toLoadCondiciones(temporal);
						this.doUpdatePlazo();
						File oldFileName= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getXml().getRuta()).concat(this.getXml().getName()));
						FileInputStream source= new FileInputStream(oldFileName);
						File target= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(newFileName).concat(this.getXml().getName()));
						Archivo.toWriteFile(target, source);
						oldFileName.delete();
						this.getXml().setRuta(newFileName);
					} // if	
					else
						JsfBase.addAlert("El proveedor no existe en el catalogo de proveedores,<br/>favor de agregarlo antes al catálogo para generar<br/>la nota de entrada.<br/>RFC ["+ this.getEmisor().getRfc()+ "] Razón social [".concat(this.getEmisor().getNombre()).concat("]<br/>"), ETipoMensaje.ALERTA);
				} // if
		  } // if
	  }	// try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally	
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			this.doCancelar();
		} // try
		finally {
			super.finalize();
		} // finally	
	}

	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(!tiposMediosPagos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(0));
        else  
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(tiposMediosPagos.indexOf(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkTipoMedioPago())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // loadTiposMediosPagos
  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = null;
		List<Columna> columns      = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns= new ArrayList<>();
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.seleccione("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS, "nombre");
			this.attrs.put("bancos", bancos);
      if(!bancos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR) && Objects.equals(-1L, ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkBanco()))
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(0));
        else  
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(bancos.indexOf(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkBanco())));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // loadBancos  
  
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposPagos= UIEntity.seleccione("TcManticTiposPagosDto", "row", params, "nombre");
			this.attrs.put("tiposPagos", tiposPagos);
      if(!tiposPagos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(0));
        else  
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(tiposPagos.indexOf(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkTipoPago())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // loadTiposPagos
  
	private void toLoadEmpresaTipoContacto() {
		List<UISelectEntity> empresaTiposContactos= null;
		Map<String, Object>params                 = null;
		try {
			params= new HashMap<>();
			params.put("idEmpresa", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdEmpresa());
			params.put("idTipoContacto", "9, 10, 11, 15, 16, 17, 18");
			empresaTiposContactos= UIEntity.build("TrManticEmpresaTipoContactoDto", "tipos", params);
			this.attrs.put("empresaTiposContactos", empresaTiposContactos);
      if(!empresaTiposContactos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(0));
        else  
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(empresaTiposContactos.indexOf(((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIkEmpresaTipoContacto())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // loadEmpresaTipoContacto
 
	public void doCheckTipoMedioPago() {
		Long tipoMedioPago= null;
		try {
      UIBackingUtilities.execute(
        "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'libre', mascara: 'libre'});"
      );		
			tipoMedioPago= ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdTipoMedioPago();
			this.attrs.put("isBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago));
      if(!ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago)) 
        UIBackingUtilities.execute(
          "janal.renovate('contenedorGrupos\\\\:idBanco_focus', {validaciones: 'requerido', mascara: 'libre'});"
        );		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doCheckTipoMedioPago
 
  public void onTabChange(TabChangeEvent event) {
//    FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
   switch(event.getTab().getTitle()) {
     case "Factura":
       break;
     case "Indirectos":
       break;
     case "Directos":
       break;
     case "Mano de obra":
       break;
   } // switch
  }
  
  public void doAgregarProyecto() {
    List<UISelectEntity> contratos= (List<UISelectEntity>) this.attrs.get("contratosProyecto");
    this.proyecto.setIkContrato(contratos.get(contratos.indexOf(this.proyecto.getIkContrato())));
    this.proyecto.setContrato(this.proyecto.getIkContrato().toString("clave")+ "-"+ this.proyecto.getIkContrato().toString("etapa"));
    if(!((NotaEntradaDirecta)this.getAdminOrden().getOrden()).isEqualProyecto(this.proyecto)) {
      NotaProyecto clon= this.proyecto;
      ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getProyectos().add(new Insert(this.proyecto));
      this.proyecto= new NotaProyecto(new Long((int)(Math.random()*-10000)));
      this.proyecto.setIkDesarrollo(clon.getIkDesarrollo());
      this.proyecto.setDesarrollo(clon.getDesarrollo());
      this.proyecto.setIkCliente(clon.getIkCliente());
      this.proyecto.setCliente(clon.getCliente());
      this.proyecto.setIkContrato(clon.getIkContrato());
      this.proyecto.setContrato(clon.getContrato());
      ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).toAdd(EGastos.DIRECTOS, clon.getImporte());
			UIBackingUtilities.execute("janal.restore();");
    } // if
    else
			JsfBase.addMessage("El proyecto ya se encuentra registrado en los gastos !", ETipoMensaje.INFORMACION);
  }
            
  public void doEliminarProyecto() {
    IActions clon= (IActions)this.attrs.get("proyecto");
    if(clon!= null) {
      int index= ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getProyectos().indexOf(clon);
      if(index>= 0) {
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getProyectos().remove(index);
        if(clon instanceof Select)
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getProyectos().add(new Delete(clon.getDto()));
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).toRemove(EGastos.DIRECTOS, ((NotaProyecto)clon.getDto()).getImporte());
  			UIBackingUtilities.execute("janal.restore();");
      } // if  
    } // if
  }
         
  public void doAgregarEmpleado() {
    List<UISelectEntity> contratos= (List<UISelectEntity>) this.attrs.get("contratosEmpleado");
    this.empleado.setIkContrato(contratos.get(contratos.indexOf(this.empleado.getIkContrato())));
    this.empleado.setContrato(this.empleado.getIkContrato().toString("clave")+ "-"+ this.empleado.getIkContrato().toString("etapa"));
    if(!((NotaEntradaDirecta)this.getAdminOrden().getOrden()).isEqualEmplado(this.empleado)) {
      NotaEmpleado clon= this.empleado;
      ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getEmpleados().add(new Insert(this.empleado));
      this.empleado= new NotaEmpleado(new Long((int)(Math.random()*-10000)));
      this.empleado.setIkDesarrollo(clon.getIkDesarrollo());
      this.empleado.setDesarrollo(clon.getDesarrollo());
      this.empleado.setIkCliente(clon.getIkCliente());
      this.empleado.setCliente(clon.getCliente());
      this.empleado.setIkContrato(clon.getIkContrato());
      this.empleado.setContrato(clon.getContrato());
      ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).toAdd(EGastos.MANO_DE_OBRA, clon.getImporte());
 			UIBackingUtilities.execute("janal.restore();");
    } // if
    else
			JsfBase.addMessage("El empleado ya se encuentra registrado en los gastos !", ETipoMensaje.INFORMACION);
 }
  
  public void doEliminarEmpleado() {
    IActions clon= (IActions)this.attrs.get("empleado");
    if(clon!= null) {
      int index= ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getEmpleados().indexOf(clon);
      if(index>= 0) {
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getEmpleados().remove(index);
        if(clon instanceof Select)
          ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getEmpleados().add(new Delete(clon.getDto()));
        ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).toRemove(EGastos.MANO_DE_OBRA, ((NotaEmpleado)clon.getDto()).getImporte());
  			UIBackingUtilities.execute("janal.restore();");
      } // if  
    } // if
  }
         
  public String doColorRow(IActions row) {
    return row instanceof Delete? "janal-table-tr-hide": ""; 
  }
 
  public void doLoadEmpleadoDisponible(SelectEvent event) {  
		UISelectEntity seleccion      = null;
		List<UISelectEntity> empleados= null;
		try {
			empleados= (List<UISelectEntity>) this.attrs.get("empleados");
			seleccion= empleados.get(empleados.indexOf((UISelectEntity)event.getObject()));
      this.empleado.setIkEmpresaPersona(seleccion);
      this.empleado.setEmpleado(seleccion.toString("empleado"));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
  } // doLoadEmpleadoDisponible
  
 	public List<UISelectEntity> doCompleteEmpleado(String text) {
 		List<Columna> campos      = null;
    Map<String, Object> params= new HashMap<>();
    try {
			campos= new ArrayList<>();
			campos.add(new Columna("empleado", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("apodo", EFormatoDinamicos.MAYUSCULAS));
  	  params.put("sucursales", ((NotaEntradaDirecta)this.getAdminOrden().getOrden()).getIdEmpresa());
			params.put("nombre", text.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*"));
      this.attrs.put("empleados", UIEntity.build("VistaIngresosDto", "persona", params));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(campos);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("empleados");
	}	// doCompleteEmpleado
          
}