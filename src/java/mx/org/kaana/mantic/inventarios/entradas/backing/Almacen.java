package mx.org.kaana.mantic.inventarios.entradas.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import static mx.org.kaana.kajool.enums.EAccion.AGREGAR;
import static mx.org.kaana.kajool.enums.EAccion.CONSULTAR;
import static mx.org.kaana.kajool.enums.EAccion.MODIFICAR;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Cifrar;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntradaProcess;
import mx.org.kaana.mantic.inventarios.entradas.reglas.AdminNotas;
import mx.org.kaana.mantic.libs.factura.beans.Concepto;
import mx.org.kaana.mantic.inventarios.entradas.reglas.Transaccion;
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

@Named(value= "manticInventariosEntradasAlmacen")
@ViewScoped
public class Almacen extends IBaseArticulos implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Almacen.class);
  private static final long serialVersionUID= 327393488565639368L;
	
	private EAccion accion;	
	private EOrdenes tipoOrden;
	private boolean aplicar;
	private TcManticProveedoresDto proveedor;
	private Calendar fechaEstimada;

	public String getValidacion() {
		return this.tipoOrden.equals(EOrdenes.DIRECTA)? "libre": "requerido";
	}
	
	public String getTitulo() {
		return this.tipoOrden.equals(EOrdenes.DIRECTA)? "(DIRECTA)": "";
	}

	public Boolean getIsDirecta() {
		return this.tipoOrden.equals(EOrdenes.DIRECTA);
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
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("idTipoComparacion", 1);
			this.aplicar=  false;
			this.attrs.put("xcodigo", JsfBase.getFlashAttribute("xcodigo"));	
			if(JsfBase.getFlashAttribute("accion")== null && JsfBase.getParametro("zOyOxDwIvGuCt")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
			this.tipoOrden= JsfBase.getParametro("zOyOxDwIvGuCt")== null || JsfBase.getFlashAttribute("idOrdenCompra")== null? EOrdenes.DIRECTA: EOrdenes.valueOf(Cifrar.descifrar(JsfBase.getParametro("zOyOxDwIvGuCt")));
      this.accion   = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idNotaEntrada", JsfBase.getFlashAttribute("idNotaEntrada")== null? -1L: JsfBase.getFlashAttribute("idNotaEntrada"));
      this.attrs.put("idOrdenCompra", JsfBase.getFlashAttribute("idOrdenCompra")== null? -1L: JsfBase.getFlashAttribute("idOrdenCompra"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "consulta": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("isPesos", false);
			this.attrs.put("sinIva", false);
			this.attrs.put("buscaPorCodigo", false);
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_FACTURA);
			this.fechaEstimada= Calendar.getInstance();
			this.attrs.put("familiasSeleccion", new Object[] {});
			this.attrs.put("lotesSeleccion", new Object[] {});
			this.attrs.put("isBanco", Boolean.FALSE);
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
          this.setAdminOrden(new AdminNotas(new NotaEntrada(-1L, (Long)this.attrs.get("idOrdenCompra")), this.tipoOrden));
          ordenCompra= this.attrs.get("idOrdenCompra").equals(-1L)? new TcManticOrdenesComprasDto(): (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, (Long)this.attrs.get("idOrdenCompra"));
          this.attrs.put("idOrdenCompra", ordenCompra.getIdOrdenCompra());
					if(this.tipoOrden.equals(EOrdenes.DIRECTA)) {
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(new Entity(-1L)));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(-1L)));
					} // if
					else {
						((NotaEntrada)this.getAdminOrden().getOrden()).setIdEmpresa(ordenCompra.getIdEmpresa());
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkDesarrollo(new UISelectEntity(new Entity(ordenCompra.getIdDesarrollo())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkCliente(new UISelectEntity(new Entity(ordenCompra.getIdCliente())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkContrato(new UISelectEntity(new Entity(ordenCompra.getIdContrato())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(new Entity(ordenCompra.getIdAlmacen())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacenista(new UISelectEntity(new Entity(ordenCompra.getIdAlmacenista())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(ordenCompra.getIdProveedor())));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(new UISelectEntity(new Entity(ordenCompra.getIdProveedorPago())));
            if(ordenCompra.getIdBanco()!= null) 
  						((NotaEntrada)this.getAdminOrden().getOrden()).setIkBanco(new UISelectEntity(new Entity(ordenCompra.getIdBanco())));
            if(ordenCompra.getIdTipoMedioPago()!= null) 
	  					((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoMedioPago(new UISelectEntity(new Entity(ordenCompra.getIdTipoMedioPago())));
            if(ordenCompra.getIdTipoPago()!= null) 
						  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoPago(new UISelectEntity(new Entity(ordenCompra.getIdTipoPago())));
						this.fechaEstimada.setTimeInMillis(ordenCompra.getRegistro().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
					} // else	
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					NotaEntrada notaEntrada= (NotaEntrada)DaoFactory.getInstance().toEntity(NotaEntrada.class, "TcManticNotasEntradasDto", "detalle", this.attrs);
          this.attrs.put("deuda", notaEntrada.getDeuda());
					ordenCompra   = (TcManticOrdenesComprasDto) DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, notaEntrada.getIdOrdenCompra());
          this.attrs.put("idOrdenCompra", ordenCompra.getIdOrdenCompra());
					this.tipoOrden= notaEntrada.getIdNotaTipo().equals(1L)? EOrdenes.DIRECTA: EOrdenes.PROVEEDOR;
          this.setAdminOrden(new AdminNotas(notaEntrada, this.tipoOrden));
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
          // ESTO ES PARA CARGAR LOS ARTICULOS DE LA FACTURA CUANDO SE ENTRA POR LA OPCION DE MODIFICAR Y VUELVA A HACER LA COMPARACION DE LOS ARTICULOS
					this.doLoadFiles("TcManticNotasArchivosDto", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada(), "idNotaEntrada", (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
					this.toPrepareDisponibles(false);
          break;
      } // switch
			this.attrs.put("ordenCompra", ordenCompra);
			this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
			this.toLoadCatalog();
      this.toLoadProveedor();
      this.toLoadBancos();
      this.toLoadTiposMediosPagos();
      this.toLoadTiposPagos();
      this.toLoadAlmacenistas();
      this.toLoadEmpresaTipoContacto();
			this.doFilterRows();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAplicar() {  
		this.aplicar= true;
		return this.doAceptar();
	}
	
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    NotaEntradaProcess nota= null;
    try {			
      if(!Cadena.isVacio((String)this.attrs.get("observaciones"))) {
        if(this.getXml()!= null && Cadena.isVacio(this.getXml().getObservaciones()))
          this.getXml().setObservaciones((String)this.attrs.get("observaciones")); 
        if(this.getPdf()!= null && Cadena.isVacio(this.getPdf().getObservaciones()))
          this.getPdf().setObservaciones((String)this.attrs.get("observaciones"));
      } // if
			((NotaEntrada)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuento());
			((NotaEntrada)this.getAdminOrden().getOrden()).setExcedentes(this.getAdminOrden().getTotales().getExtra());
			((NotaEntrada)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
			((NotaEntrada)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			((NotaEntrada)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
			this.getAdminOrden().toAdjustArticulos();
			nota= new NotaEntradaProcess();
			nota.setNotaEntrada((NotaEntrada)this.getAdminOrden().getOrden());
			nota.setArticulos(this.getAdminOrden().getArticulos());
			nota.setFamilias(Arrays.asList((Object[])this.attrs.get("familiasSeleccion")));
			nota.setLotes(Arrays.asList((Object[])this.attrs.get("lotesSeleccion")));
      
      if(Cadena.isVacio(this.attrs.get("folio"))) {
        if(!Cadena.isVacio(this.getXml()) && !this.getIsDirecta()) {
          if(this.getEmisor().getRfc().equals(this.proveedor.getRfc()) && !this.getIsDirecta()) {
            transaccion = new Transaccion(nota, this.aplicar, this.getXml(), this.getPdf());
            if (transaccion.ejecutar(this.accion)) {
              if(this.accion.equals(EAccion.AGREGAR) || this.aplicar) {
                if(this.doCheckCodigoBarras(((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada())) {
                  JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");
                  regresar=  "/Paginas/Mantic/Catalogos/Articulos/codigos".concat(Constantes.REDIRECIONAR);
                } // if
                else
                  regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
                if(this.accion.equals(EAccion.AGREGAR))
                  UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la nota de entrada', '"+ ((NotaEntrada)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
                else
                  UIBackingUtilities.execute("jsArticulos.back('aplic\\u00F3 la nota de entrada', '"+ ((NotaEntrada)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
              } // if	
              else
                this.getAdminOrden().toStartCalculate();
              if(!this.accion.equals(EAccion.CONSULTAR)) 
                JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la nota de entrada."), ETipoMensaje.INFORMACION);
              JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
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
  	JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
		JsfBase.setFlashAttribute("xcodigo", this.attrs.get("xcodigo"));	
		if(this.getAdminOrden()!= null && this.getAdminOrden().getOrden()!= null && ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada()<= 0L) {
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
 			List<UISelectEntity> almacenes= UIEntity.seleccione("TcManticAlmacenesDto", "almacenes", params, columns, "clave");
      this.attrs.put("almacenes", almacenes);
			if(!almacenes.isEmpty() && this.accion.equals(EAccion.AGREGAR)) 
				if(((NotaEntrada)this.getAdminOrden().getOrden()).getIdAlmacen()== null)
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
				else {
					int index= almacenes.indexOf(new UISelectEntity(((NotaEntrada)this.getAdminOrden().getOrden()).getIdAlmacen()));
					if(index>= 0)
					  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(index));
					else
  				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
				} // else	
      columns.remove(0);
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			List<UISelectEntity> proveedores= UIEntity.seleccione("VistaOrdenesComprasDto", "moneda", params, columns, "razonSocial");
      this.attrs.put("proveedores", proveedores);
			int index= 0;
			if(proveedores!= null && !proveedores.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR) && ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor().getKey().equals(-1L))
			   ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(0));
				else {
				  index= proveedores.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor());
          if(index>= 0)
				    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(index));
          else
   			    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(0));
				} // else
        UISelectEntity temporal= proveedores.get(index);
				temporal.put("fechaEstimada", new Value("fechaEstimada", Global.format(EFormatoDinamicos.FECHA_CORTA, this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias")))));
		    this.attrs.put("proveedor", temporal);
			  this.proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor().getKey());
  			if(this.proveedor!= null) 
  				this.toLoadCondiciones(new UISelectEntity(new Entity(this.proveedor.getIdProveedor())));
				if(this.accion.equals(EAccion.AGREGAR))
					this.doCalculateFechaPago();
			} // if	
			if(this.attrs.get("idOrdenCompra")!= null) {
        columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
        this.attrs.put("ordenes", UIEntity.build("VistaNotasEntradasDto", "ordenes", params, columns));
			  List<UISelectEntity> ordenes= (List<UISelectEntity>)this.attrs.get("ordenes");
			  if(!ordenes.isEmpty()) 
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkOrdenCompra(ordenes.get(0));
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
        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + ((NotaEntrada)this.getAdminOrden().getOrden()).getIdEmpresa());
			else
				params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
      this.attrs.put("desarrollos", desarrollos);			
			if(this.accion.equals(EAccion.AGREGAR) && (this.attrs.get("ordenCompra")== null || Objects.equals(-1L, ((TcManticOrdenesComprasDto)this.attrs.get("ordenCompra")).getIdDesarrollo())))
				desarrollo= UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos"));				
      else {
        int index= ((TcManticOrdenesComprasDto)this.attrs.get("ordenCompra")).getIdDesarrollo()== null? -1: desarrollos.indexOf(new UISelectEntity((((TcManticOrdenesComprasDto)this.attrs.get("ordenCompra")).getIdDesarrollo())));
        if(index< 0)
				  desarrollo= desarrollos.get(0);			
        else  
				  desarrollo= desarrollos.get(index);			
      } // if  
			this.attrs.put("desarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));			
			this.attrs.put("cliente", desarrollo.toString("razonSocial"));
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos
	
	public void doUpdateCliente() {
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		List<Columna> columns           = null;
		Map<String, Object>params       = null;
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo = ((NotaEntrada)this.getAdminOrden().getOrden()).getIkDesarrollo();
			this.attrs.put("cliente", desarrollos.get(desarrollos.indexOf(desarrollo)).toString("razonSocial"));			
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_mantic_almacenes.id_desarrollo=" + desarrollo.getKey());
      this.attrs.put("almacenes", UIEntity.build("TcManticAlmacenesDto", "row", params, columns));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty()) 
			  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
			this.doLoadContratos();
      this.toLoadAlmacenistas();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doUpdateCliente
  
	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdDesarrollo());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR) && Objects.equals(-1L, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkContrato().getKey()))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkContrato(contratos.get(0));
        else  
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkContrato(contratos.get(contratos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkContrato())));
			this.doLoadLotes();
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
	
	public void doLoadLotes() {
		List<UISelectEntity> lotes= null;
		Map<String, Object>params = null;
    List<Entity> items        = null; 
    Object[] list             = null;
		try {
			params = new HashMap<>();
			params.put("idContrato", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdContrato());
			lotes= UIEntity.build("TcKeetContratosLotesDto", "byContratoContratistas", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("lotes", lotes);						
      if(!lotes.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR)) { 
    			params.put("idOrdenCompra", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdOrdenCompra());
          items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetOrdenesContratosLotesDto", "lotes", params);
        } // if  
        else {
    			params.put("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
          items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetNotasContratosLotesDto", "lotes", params);
        } // if
      if(items!= null && !items.isEmpty()) {
        list     = new Object[items.size()];
        int count= 0;
        for (Entity entity : items) {
          int index= lotes.indexOf(new UISelectEntity(entity.toLong("idContratoLote")));
          if(index>= 0)
            list[count++]= lotes.get(index);
        } // for
      } // if  
      if(list== null)
        list= new Object[]{};
      this.attrs.put("lotesSeleccion", list);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadLotes
  
	public void toLoadProveedor() {
		try {
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			UISelectEntity temporal= ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor();
			temporal= proveedores.get(proveedores.indexOf(temporal));
			temporal.put("fechaEstimada", new Value("fechaEstimada", this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias"))));
			this.attrs.put("proveedor", temporal);
			this.proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, temporal.getKey());
			this.toUpdateFamilias();
			this.toLoadCondiciones(proveedores.get(proveedores.indexOf((UISelectEntity)((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor())));
			this.doUpdatePlazo();
			this.toCheckProveedor(true);
		}	
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 
	
	public void doUpdateProveedor() {
		try {
			if(this.tipoOrden.equals(EOrdenes.PROVEEDOR)) {
				this.getAdminOrden().getArticulos().clear();
				this.getAdminOrden().toCalculate();
			} // if	
      this.toLoadProveedor();
		}	
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 
	
	private void toUpdateFamilias() throws Exception {
		UISelectEntity vendedor      = null;
		List<UISelectEntity> familias= null;
		Map<String, Object>params    = null;
    List<Entity> items           = null;
    Object[] list                = null;
		try {
			vendedor= (UISelectEntity) this.attrs.get("proveedor");
			params= new HashMap<>();
			params.put("idProveedor", vendedor.getKey());
			familias= UIEntity.build("VistaFamiliasProveedoresDto", "row", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("familias", familias);			
      if(!familias.isEmpty())
        if(this.accion.equals(EAccion.AGREGAR)) {
    			params.put("idOrdenCompra", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdOrdenCompra());
          items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetOrdenesContratosLotesDto", "lotes", params);
        } // if
        else {
    			params.put("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
          items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetNotasContratosLotesDto", "lotes", params);
        } // else  
      if(items!=null && !items.isEmpty()) {
        list     = new Object[items.size()];
        int count= 0;
        for (Entity entity: items) {
          int index= familias.indexOf(new UISelectEntity(entity.toLong("idFamilia")));
          if(index>= 0)
            list[count++]= familias.get(index);
        } // for
      } // if  
      if(list== null)
        list= new Object[]{};
      this.attrs.put("familiasSeleccion",  list);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // toUpdateFamilias
  
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
				if(this.accion.equals(EAccion.AGREGAR) && ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago()== null)
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				else {
					Entity entity= new UISelectEntity(new Entity(((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedorPago()));
					if(condiciones.indexOf(entity)>= 0)
				    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(condiciones.indexOf(entity)));
 					else
  				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				} // if	
				((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
        ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
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

	public void doUpdateAlmacen() {
		List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
		int index= almacenes.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkAlmacen());
		if(index>= 0) {
	  	((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(index));
  		((NotaEntrada)this.getAdminOrden().getOrden()).setIdEmpresa(((NotaEntrada)this.getAdminOrden().getOrden()).getIkAlmacen().toLong("idEmpresa"));
		} // if
		if(this.tipoOrden.equals(EOrdenes.ALMACEN)) {
  		this.getAdminOrden().getArticulos().clear();
			this.getAdminOrden().toCalculate();
		} // if	
	}
	
	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "Articulos":
        Object[] familias= (Object[])this.attrs.get("familiasSeleccion");
        Object[] lotes   = (Object[])this.attrs.get("lotesSeleccion");
        if((familias.length> 0 && lotes.length> 0) || this.getIsDirecta()) {
          // this.toLoadArticulos(Arrays.asList(familias), Arrays.asList(lotes));
          UIBackingUtilities.update("contenedorGrupos:sinIva");
          UIBackingUtilities.update("contenedorGrupos:paginator");
        } // if
        else {
          getAdminOrden().getArticulos().clear();
          UIBackingUtilities.execute("janal.show([{summary: 'Contrato:', detail: 'No tiene definido un lote.'},{summary: 'Proveedor:', detail: 'No tiene definido una familia.'}]);"); 
        } // else			
        break;
      case "Importar":
     		if(this.attrs.get("faltantes")== null)
		  	  this.doLoadFiles("TcManticNotasArchivosDto", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada(), "idNotaEntrada", (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
        break;
      case "Historial":
        this.doLoadHistorico();
        break;
    } // switch    
	}
	
  public void doLoadHistorico() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idContrato", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdContrato());
      columns= new ArrayList<>();
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("etapa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("familia", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.attrs.put("historico", UIEntity.build("VistaFamiliasProveedoresDto", "lazy", params, columns, Constantes.SQL_TODOS_REGISTROS));
    } // try
    catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    }// finally
	}
  
	public void doFileUpload(FileUploadEvent event) {
		this.attrs.put("relacionados", 0);
		if(this.proveedor!= null) {
			this.doFileUpload(event, ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"), this.proveedor.getClave(), (boolean)this.attrs.get("sinIva"), this.getAdminOrden().getTipoDeCambio());
			if(event.getFile().getFileName().toUpperCase().endsWith(EFormatos.XML.name())) {
				((NotaEntrada)this.getAdminOrden().getOrden()).setFactura(this.getFactura().getFolio());
				((NotaEntrada)this.getAdminOrden().getOrden()).setFechaFactura(Fecha.toLocalDateDefault(this.getFactura().getFecha()));
				((NotaEntrada)this.getAdminOrden().getOrden()).setOriginal(Numero.toRedondearSat(Double.parseDouble(this.getFactura().getTotal())));
				if(this.tipoOrden.equals(EOrdenes.DIRECTA)) {
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
    this.toCheckArticulos(checkItems);
	}

	private boolean isEqualsItems(Integer idTipoComparacion, Articulo faltante, Articulo disponible) {
	  boolean regresar= false;
		switch(idTipoComparacion) {
			case 1: // COMPRAR AMBOS CODIGO Y NOMBRE
				regresar= (faltante.getCodigo()!= null && disponible.getCodigo()!= null && faltante.getCodigo().length()> 0 &&  Cadena.toEqualsString(faltante.getCodigo(), disponible.getCodigo())) || 
			            (faltante.getNombre()!= null && disponible.getOrigen()!= null && faltante.getNombre().length()> 0 &&  Cadena.toEqualsString(faltante.getNombre(), disponible.getOrigen()));
			case 2: // COMPRAR CODIGO
				regresar= (faltante.getCodigo()!= null && disponible.getCodigo()!= null && faltante.getCodigo().length()> 0 &&  Cadena.toEqualsString(faltante.getCodigo(), disponible.getCodigo()));
			case 3: // COMPRAR NOMBRE
				regresar= (faltante.getNombre()!= null && disponible.getOrigen()!= null && faltante.getNombre().length()> 0 &&  Cadena.toEqualsString(faltante.getNombre(), disponible.getOrigen()));
				break;
		} // switch
		return regresar;
	}
	
	private void toCheckArticulos(boolean checkItems) {
		Articulo faltante, disponible= null;
		int relacionados             = 0;
		try {
			Integer idTipoComparacion= (Integer)this.attrs.get("idTipoComparacion");
		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
			int x= 0;
			while(faltantes!= null && x< faltantes.size()) {
				faltante= faltantes.get(x);
  		  List<Articulo> disponibles= (List<Articulo>)this.attrs.get("disponibles");
				int y        = 0;
  			boolean found= false;
				while (y< disponibles.size()) {
					disponible= disponibles.get(y);
					if(this.isEqualsItems(idTipoComparacion, faltante, disponible)) {
						relacionados++;
  				  LOG.info(relacionados+ ".- Relacionados ["+ disponible.getCodigo()+ "] "+ disponible.getNombre());
						found= true;
      			faltantes.remove(faltante);
    			  disponibles.remove(disponible);
						if(checkItems)
    			    this.toMoveArticulo(disponible, faltante);
						disponible.setDisponible(false);
						break;
					} // if	
					else
					  y++;
				} // for
				// EL ARTICULO FUE BUSCADO POR CODIGO EN EL PROVEDOR
				if(!found) {
					x++;
   				LOG.info(x+ ".- NO ENCONTRADO ["+ faltante.getCodigo()+ "] "+ faltante.getNombre());
				} // if	
				// YA NO HAY MAS ARTICULOS QUE BUSCAR TODOS FUERON ASIGNADOS
				if(disponibles.isEmpty())
					break;
			} // for
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
		this.attrs.put("relacionados", relacionados);
		this.toCheckProveedor(checkItems);
	}
	
	private void toCheckProveedor(boolean checkItems) {
		Articulo faltante        = null;
		int relacionados         = this.attrs.get("relacionados")== null? 0: (int)this.attrs.get("relacionados");
		Integer idTipoComparacion= (Integer)this.attrs.get("idTipoComparacion");
	  Map<String, Object> params=null;
		try {
			params= new HashMap<>();
			params.put("idProveedor", this.getAdminOrden().getIdProveedor());
		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
			int x= 0;
			while(faltantes!= null && x< faltantes.size()) {
				faltante= faltantes.get(x);
				switch(idTipoComparacion) {
			   case 1: // COMPRAR CODIGO
    			 params.put(Constantes.SQL_CONDICION, "upper(tc_mantic_articulos_codigos.codigo)= upper('"+ faltante.getCodigo()+"')");
					 break;
  			 case 2: // COMPRAR AMBOS CODIGO Y NOMBRE
    			 params.put(Constantes.SQL_CONDICION, "(upper(tc_mantic_articulos_codigos.codigo)= upper('"+ faltante.getCodigo()+"') or upper(tc_mantic_notas_detalles.origen)= upper('"+ faltante.getOrigen()+"'))");
					 break;
				 case 3: // COMPRAR NOMBRE
    			 params.put(Constantes.SQL_CONDICION, "upper(tc_mantic_notas_detalles.origen)= upper('"+ faltante.getOrigen()+"')");
					 break;
				 default:
				   params.put(Constantes.SQL_CONDICION, "tc_mantic_articulos_codigos.codigo= 'null'");
  			} // switch
				List<UISelectEntity> disponibles= UIEntity.build("VistaNotasEntradasDto", "proveedor", params, Collections.EMPTY_LIST); 
				if(disponibles!= null && !disponibles.isEmpty()) {
					relacionados++;
					faltantes.remove(faltante);
					if(checkItems) {
						disponibles.get(0).put("sat", new Value("sat", faltante.getSat()));
						disponibles.get(0).put("codigo", new Value("codigo", faltante.getCodigo()));
						disponibles.get(0).put("costo", new Value("costo", faltante.getCosto()));
						disponibles.get(0).put("cantidad", new Value("cantidad", faltante.getCantidad()));
						disponibles.get(0).put("descuento", new Value("descuento", faltante.getDescuento()));
						disponibles.get(0).put("iva", new Value("iva", faltante.getIva()));
						disponibles.get(0).put("unidadMedida", new Value("unidadMedida", faltante.getUnidadMedida()!= null? faltante.getUnidadMedida().toUpperCase(): ""));
						disponibles.get(0).put("origen", new Value("origen", faltante.getNombre()));
						disponibles.get(0).put("facturado", new Value("facturado", true));
						disponibles.get(0).put("disponible", new Value("disponible", false));
						this.attrs.put("encontrado", disponibles.get(0));
						this.attrs.put("omitirMensaje", disponibles.get(0).toLong("idArticulo"));
						this.doFindArticulo(this.getAdminOrden().getArticulos().size()- 1);
					} // if
				} // if	
				else
					x++;
			} // while
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch
		finally {
			Methods.clean(params);
		} // finally
		this.attrs.put("relacionados", relacionados);
	}
	
	@Override
	public void doFaltanteArticulo() {
		Articulo faltante  = (Articulo)this.attrs.get("faltante");
		Articulo disponible= (Articulo)this.attrs.get("disponible");
		try {
		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
   	  faltantes.remove(faltante);
		  List<Articulo> disponibles= (List<Articulo>)this.attrs.get("disponibles");
		  disponibles.remove(disponible);
			if(faltante!= null && disponible!= null)
			  this.toMoveArticulo(disponible, faltante);
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	}

	private void toMoveArticulo(Articulo disponible, Articulo faltante) {
 		disponible.setSat(faltante.getSat());
		disponible.setCodigo(faltante.getCodigo());
		disponible.setCosto(faltante.getCosto());
		disponible.setCantidad(faltante.getCantidad());
		disponible.setDescuento("0");
		disponible.setIva(faltante.getIva());
		disponible.setUnidadMedida(faltante.getUnidadMedida());
		disponible.setOrigen(faltante.getOrigen());
		disponible.setDisponible(false);
		this.getAdminOrden().toCalculate();
	}

	public void doCheckFolio() {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("factura", ((NotaEntrada)this.getAdminOrden().getOrden()).getFactura());
			params.put("idProveedor", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedor());
			params.put("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
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
			if(entity!= null && entity.size()> 0) 
				UIBackingUtilities.execute("$('#contenedorGrupos\\\\:factura').val('');janal.show([{summary: 'Error:', detail: 'El folio ["+ ((NotaEntrada)this.getAdminOrden().getOrden()).getFactura()+ "] se registró en la nota de entrada "+ entity.toString("consecutivo")+ ", el dia "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs.'}]);");
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
		LocalDate fechaFactura= ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura();
		if(((NotaEntrada)this.getAdminOrden().getOrden()).getDiasPlazo()== null)
			((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(1L);
		LocalDate calendar= fechaFactura.plusDays(((NotaEntrada)this.getAdminOrden().getOrden()).getDiasPlazo().intValue()- 1);
		((NotaEntrada)this.getAdminOrden().getOrden()).setFechaPago(calendar);
	}

	public void doCalculatePagoFecha() {
		LocalDate fechaFactura= ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura();
		LocalDate fechaPago   = ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaPago();
		((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(DAYS.between(fechaFactura, fechaPago));
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

	@Override
	public void doUpdateArticulo(String codigo, Integer index) {
		super.doUpdateArticulo(codigo, index);
    this.doFilterRows();
	}
	
	@Override
	public void doDeleteArticulo(Integer index) {
		super.doDeleteArticulo(index);
    this.doFilterRows();
	}

	@Override
  public void doFindArticulo(Integer index) {
		super.doFindArticulo(index);
		this.doFilterRows();
	}

	public void doUpdateRfc() {
		this.doUpdateRfc(this.proveedor);
	}

	public void doDblClickArticulo(SelectEvent event) {
		this.attrs.put("buscado", event.getObject());
		this.doFindOutArticulo();
	}
	
	public void doRowSelectArticulo(SelectEvent event) {
		this.attrs.put("buscado", event.getObject());
	}
	
	public void doFindOutArticulo() {
		Articulo faltante= (Articulo)this.attrs.get("faltante");
		Entity  buscado  = (Entity)this.attrs.get("buscado");
		if(faltante!= null) { 
			if(buscado== null) {
				FormatCustomLazy list= (FormatCustomLazy)this.attrs.get("lazyModel");
				List<IBaseDto> items = (List<IBaseDto>)list.getWrappedData();
				if(items.size()> 0) {
					faltante.setIdArticulo(items.get(0).getKey());
				} // if	
			} // else
			else
				faltante.setIdArticulo(buscado.getKey());
      int position= this.getAdminOrden().getArticulos().indexOf(faltante);
			if(position< 0) {
  		  List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
     	  faltantes.remove(faltante);
			} // if
			if(buscado!= null) {
				buscado.put("sat", new Value("sat", faltante.getSat()));
				buscado.put("codigo", new Value("codigo", faltante.getCodigo()));
				buscado.put("costo", new Value("costo", faltante.getCosto()));
				buscado.put("cantidad", new Value("cantidad", faltante.getCantidad()));
				buscado.put("descuento", new Value("descuento", faltante.getDescuento()));
				buscado.put("iva", new Value("iva", faltante.getIva()));
				buscado.put("unidadMedida", new Value("unidadMedida", faltante.getUnidadMedida()!= null? faltante.getUnidadMedida().toUpperCase(): ""));
				buscado.put("origen", new Value("origen", faltante.getNombre()));
				buscado.put("disponible", new Value("disponible", false));
			} // if	
			this.attrs.put("encontrado", new UISelectEntity(buscado));
		} // if
	}

	private boolean doCheckCodigoBarras(Long idNotaEntrada) throws Exception {
		boolean regresar          = false;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idNotaEntrada", idNotaEntrada);
			Value value= DaoFactory.getInstance().toField("VistaNotasEntradasDto", "codigos", params);
			if(value.getData()!= null)
			  regresar= value.toLong()> 0;
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	  return regresar;	
	}
	
  public void doUpdatePlazo() {
		if(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago()!= null) {
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
			int index= condiciones.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago());
			if(index>= 0) {
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(index));
		  	((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
        this.doCalculateFechaPago();		
        ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
			} // if
			else {
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(null);
		  	((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(0L);
        ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento("0");
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
				// EN CASO DE QUE NO EXISTA MANDAR UN MENSAJE DE QUE ESE PROVEEDOR NO EXISTE EN EL CATALGO DE PROVEEDORES PARA QUE SE AGREGUE
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
    Transaccion transaccion= null;
    try {			
			((NotaEntrada)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuento());
			((NotaEntrada)this.getAdminOrden().getOrden()).setExcedentes(this.getAdminOrden().getTotales().getExtra());
			((NotaEntrada)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
			((NotaEntrada)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			((NotaEntrada)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
			transaccion = new Transaccion(((NotaEntrada)this.getAdminOrden().getOrden()), this.getAdminOrden().getArticulos(), false, this.getXml(), this.getPdf());
			this.getAdminOrden().toAdjustArticulos();
			if (transaccion.ejecutar(this.accion)) {
 			  UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 la nota de entrada', '"+ ((NotaEntrada)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
				this.accion= EAccion.MODIFICAR;
				this.getAdminOrden().getArticulos().add(new Articulo(-1L, Boolean.FALSE, Boolean.FALSE));
				this.attrs.put("autoSave", Global.format(EFormatoDinamicos.FECHA_HORA, Fecha.getRegistro()));
			} // if	
      if(Objects.equals(null, ((NotaEntrada)this.getAdminOrden().getOrden()).getIdBanco()))
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIdBanco(-1L);
      if(Objects.equals(null, ((NotaEntrada)this.getAdminOrden().getOrden()).getIdTipoPago()))
        ((NotaEntrada)this.getAdminOrden().getOrden()).setIdTipoPago(-1L);
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
	
	public static void main(String ... args) {
	  LOG.info("H2-111109".replaceAll(Constantes.CLEAN_STR, ""));	
	  LOG.info("H2 111109".replaceAll(Constantes.CLEAN_STR, ""));	
	  LOG.info(Cadena.toEqualsString("H2 111109", "H2-111109"));	
	}

	private void doResetArticuloFromXmlFile(String origen) {
		Articulo item           = null;
		List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
		if(this.getFactura()!= null && faltantes!= null) {
			try {
				for (Concepto concepto: this.getFactura().getConceptos()) {
          if(origen.equals(concepto.getDescripcion())) {
						if(item== null) {
							item= new Articulo(
								(boolean)this.attrs.get("sinIva"), // sinIva
								this.getAdminOrden().getTipoDeCambio(), // tipoDeCambio
								concepto.getDescripcion(), // nombre
								concepto.getNoIdentificacion(), // codigo
								Numero.toRedondearSat((Double.parseDouble(concepto.getTraslado().getBase()))/ Double.parseDouble(concepto.getCantidad())), // costo
								"", // descuento,
								-1L, // idOrdenCompra
								"", // extras
								0D, // importe
								"", // propio
								Double.parseDouble(concepto.getTraslado().getTasaCuota())* 100, // iva
								0D, // totalImpuesto 
								Numero.toRedondearSat(Double.parseDouble(concepto.getTraslado().getBase())), // subTotal
								Double.parseDouble(concepto.getCantidad()), // cantidad
								-1L, // idOrdenDetalle 
								new Random().nextLong(), // idArticulo 
								0D, // totalDescuentos
								-1L, // idProveedor
								false, // ultimo
								false, // solicitado
								0D, // stock
								0D, // excedentes
								concepto.getClaveProdServ(), // sat
								concepto.getUnidad(), // unidadMedida
								2L, // idAplicar
								concepto.getDescripcion(), // origen
                Boolean.TRUE // especial
							);
						} // if
						else {
							item.setCantidad(item.getCantidad()+ Double.parseDouble(concepto.getCantidad()));
							item.setCosto(Numero.toRedondearSat((item.getSubTotal()+ Double.parseDouble(concepto.getTraslado().getBase()))/ item.getCantidad()));
							item.setSubTotal(item.getSubTotal()+ Double.parseDouble(concepto.getTraslado().getBase()));
						} // else
					} // if
				} // for
				if(item!= null)
  				faltantes.add(0, item);
			} // try	
			catch (Exception e) {
				Error.mensaje(e);
				JsfBase.addMessageError(e);
			} // catch
	  } // if 
	}
	
  public void doDesasociarArticulo(Articulo row, Integer index) {
		if(!row.isDisponible()) {
 		  this.doResetArticuloFromXmlFile(row.getOrigen());
			if(row.getIdOrdenDetalle()!= null && row.getIdOrdenDetalle()> 0L) {
		    row.setDisponible(true);
		    row.setCodigo("");
		    row.setOrigen("");
			} // if
			else {
				this.doDeleteArticulo(index);
				UIBackingUtilities.execute("janal.reset();");
				UIBackingUtilities.execute("jsArticulos.move();");
			} // else	
		} // if
		else
			JsfBase.addMessage("El articulo aun no se encuentra asociado a una partida de la factura (XML) !", ETipoMensaje.ALERTA);
	}	

	@Override
	public void doSearchArticulo(Long idArticulo, Integer index) {
		this.attrs.put("idAlmacen", ((NotaEntrada)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		super.doSearchArticulo(idArticulo, index);
	}

  private void toMoveSelectedProveedor() {
		UISelectEntity temporal   = (UISelectEntity)this.attrs.get("proveedor");
	  Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			if(this.tipoOrden.equals(EOrdenes.DIRECTA)) {
			  this.getAdminOrden().toCalculate();
				if(temporal== null || !this.getEmisor().getRfc().equals(temporal.toString("rfc"))) {
					params.put("rfc", this.getEmisor().getRfc());
					params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
					TcManticProveedoresDto encontrado= (TcManticProveedoresDto)DaoFactory.getInstance().findFirst(TcManticProveedoresDto.class, "proveedor", params);
					if(encontrado!= null) {
						String newFileName= this.getXml().getRuta().replaceAll("/"+ (this.proveedor.getClave()!= null? this.proveedor.getClave().trim(): "NoDefinido")+ "/", "/"+ (encontrado.getClave()!= null? encontrado.getClave().trim(): "NoDefinido")+ "/");
						this.proveedor= encontrado;
						temporal= new UISelectEntity(new Entity(encontrado.getIdProveedor()));
						((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(temporal);
						List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");						
						temporal= proveedores.get(proveedores.indexOf(temporal));
						temporal.put("fechaEstimada", new Value("fechaEstimada", this.toCalculateFechaEstimada(this.fechaEstimada, temporal.toInteger("idTipoDia"), temporal.toInteger("dias"))));
						this.attrs.put("proveedor", temporal);
						this.toLoadCondiciones(temporal);
						this.doUpdatePlazo();
						this.toCheckProveedor(true);
						File oldFileName= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(this.getXml().getRuta()).concat(this.getXml().getName()));
						FileInputStream source= new FileInputStream(oldFileName);
						File target= new File(Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas").concat(newFileName).concat(this.getXml().getName()));
						Archivo.toWriteFile(target, source);
						oldFileName.delete();
						this.getXml().setRuta(newFileName);
					} // if	
					else
						JsfBase.addAlert("El proveedor no existe en el catalogo de proveedores,<br/>favor de agregarlo antes al catálogo para generar la nota de entrada.<br/><br/> RFC ["+ this.getEmisor().getRfc()+ "] ".concat(this.getEmisor().getNombre()).concat("<br/>"), ETipoMensaje.ALERTA);
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
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(0));
        else  
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(tiposMediosPagos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoMedioPago())));
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
        if(this.accion.equals(EAccion.AGREGAR) && Objects.equals(-1L, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkBanco()))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(0));
        else  
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(bancos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkBanco())));
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
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(0));
        else  
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(tiposPagos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoPago())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // loadTiposPagos
  
	private void toLoadAlmacenistas() {
		List<UISelectEntity> almacenistas= null;
		Map<String, Object>params        = null;
		try {
			params= new HashMap<>();
 			params.put("campoLlave", "tc_mantic_personas.id_persona");
			params.put("idDesarrollo", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdDesarrollo());
			params.put(Constantes.SQL_CONDICION, "tr_mantic_empresa_personal.id_puesto= 1");
  	  almacenistas= UIEntity.build("VistaContratosDto", "personalAsignado", params);
			this.attrs.put("almacenistas", almacenistas);
      if(!almacenistas.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacenista(almacenistas.get(0));
        else  
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacenista(almacenistas.get(almacenistas.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkAlmacenista())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // loadAlmacenistas
  
	private void toLoadEmpresaTipoContacto() {
		List<UISelectEntity> empresaTiposContactos= null;
		Map<String, Object>params                 = null;
		try {
			params= new HashMap<>();
			params.put("idEmpresa", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdEmpresa());
			params.put("idTipoContacto", "9, 10, 11, 15, 16, 17, 18");
			empresaTiposContactos= UIEntity.build("TrManticEmpresaTipoContactoDto", "tipos", params);
			this.attrs.put("empresaTiposContactos", empresaTiposContactos);
      if(!empresaTiposContactos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(0));
        else  
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(empresaTiposContactos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkEmpresaTipoContacto())));
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
			tipoMedioPago= ((NotaEntrada)this.getAdminOrden().getOrden()).getIdTipoMedioPago();
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
 
  public void doEraseArticulos() {
    this.getAdminOrden().getArticulos().clear();
    if(this.getAdminOrden().getArticulos().size()> 0)
      this.getAdminOrden().toCalculate();
  }
 
  @Override
  protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {  
    super.toMoveData(articulo, index);
    Articulo temporal         = this.getAdminOrden().getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
				params.put("idArticulo", articulo.toLong("idArticulo"));
				params.put("idProveedor", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedor());
				params.put("idCliente", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdCliente());
        // RECUPERAR EL PRECIO CONVENIO O EL PRECIO DE LISTA O EL PRECIO BASE
        Entity precios= (Entity)DaoFactory.getInstance().toEntity("VistaOrdenesComprasDto", "precios", params);
        if(precios!= null && !precios.isEmpty()) {
          temporal.setValor(precios.toDouble("precio"));
          temporal.setPrecio(precios.toDouble("precio"));
          if(!articulo.containsKey("costo")) 
			      temporal.setCosto(precios.toDouble("precio"));
          this.getAdminOrden().toCalculate(index);
        } // if  
      } // if   
		} // try
		finally {
			Methods.clean(params);
		}
  }
  
}