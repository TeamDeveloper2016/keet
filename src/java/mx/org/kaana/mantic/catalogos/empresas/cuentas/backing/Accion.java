package mx.org.kaana.mantic.catalogos.empresas.cuentas.backing;

import java.io.Serializable;
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
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntrada;
import mx.org.kaana.mantic.inventarios.entradas.reglas.AdminNotas;
import mx.org.kaana.mantic.inventarios.entradas.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntradaProcess;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticCatalogosEmpresasCuentasAccion")
@ViewScoped
public class Accion extends IBaseArticulos implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639364L;
	
	private EAccion accion;	
	private boolean aplicar;
	private TcManticProveedoresDto proveedor;

	public String getAgregar() {
		return this.accion.equals(EAccion.COMPLETO)? "none": "";
	}

	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}

	public TcManticProveedoresDto getProveedor() {
		return proveedor;
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
	
	public Boolean getDiferente() {
	  return this.getEmisor()!= null && this.proveedor!= null &&	!this.getEmisor().getRfc().equals(this.proveedor.getRfc());
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.aplicar  =  Boolean.FALSE;
			if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion   = JsfBase.getFlashAttribute("accion")== null? EAccion.COMPLETO: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idProveedor", JsfBase.getFlashAttribute("idProveedor")== null? -1L: JsfBase.getFlashAttribute("idProveedor"));     
      this.attrs.put("idEmpresaDeuda", JsfBase.getFlashAttribute("idEmpresaDeuda")== null? -1L: JsfBase.getFlashAttribute("idEmpresaDeuda"));     
			this.attrs.put("idOrdenCompra", -1L);
      this.attrs.put("idNotaEntrada", JsfBase.getFlashAttribute("idNotaEntrada")== null? -1L: JsfBase.getFlashAttribute("idNotaEntrada"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "saldos": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_FACTURA);
			this.attrs.put("familiasSeleccion", new Object[] {});
      this.attrs.put("lotesSeleccion", new Object[] {});
      this.attrs.put("isBanco", Boolean.FALSE);
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

	@Override
  public void doLoad() {
		Double deuda= 0.0D;
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.equals(EAccion.COMPLETO)? EAccion.AGREGAR.name(): this.accion.equals(EAccion.COMPLEMENTAR)? EAccion.MODIFICAR.name(): this.accion.name()));
      switch (this.accion) {
        case COMPLETO:											
          this.setAdminOrden(new AdminNotas(new NotaEntrada()));
					((NotaEntrada)this.getAdminOrden().getOrden()).setIdNotaTipo(3L);
					((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(new Entity(-1L)));
					((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(-1L)));
					this.doCalculateFechaPagoInit();
          break;
        case COMPLEMENTAR:					
        case CONSULTAR:					
					NotaEntrada notaEntrada= (NotaEntrada)DaoFactory.getInstance().toEntity(NotaEntrada.class, "TcManticNotasEntradasDto", "detalle", this.attrs);
					notaEntrada.setIdOrdenCompra(null);
          this.setAdminOrden(new AdminNotas(notaEntrada));
					((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(new Entity(notaEntrada.getIdAlmacen())));
					((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(new UISelectEntity(new Entity(notaEntrada.getIdProveedor())));          
					deuda= ((NotaEntrada)this.getAdminOrden().getOrden()).getDeuda();
					this.setAdminOrden(new AdminNotas(notaEntrada));
          break;
      } // switch
			this.toLoadCatalog();
      this.toLoadBancos();
      this.toLoadTiposMediosPagos();
      this.toLoadTiposPagos();
      this.toLoadEmpresaTipoContacto();
			if(this.accion.equals(EAccion.COMPLEMENTAR) ||	this.accion.equals(EAccion.CONSULTAR))
				((NotaEntrada)this.getAdminOrden().getOrden()).setDeuda(deuda);
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
    NotaEntradaProcess nota= null;
    String regresar        = null;
    try {			
   		this.aplicar= true;
      if(!Cadena.isVacio((String)this.attrs.get("observaciones"))) {
        if(this.getXml()!= null && Cadena.isVacio(this.getXml().getObservaciones()))
          this.getXml().setObservaciones((String)this.attrs.get("observaciones")); 
        if(this.getPdf()!= null && Cadena.isVacio(this.getPdf().getObservaciones()))
          this.getPdf().setObservaciones((String)this.attrs.get("observaciones"));
      } // if
			((NotaEntrada)this.getAdminOrden().getOrden()).setDescuentos(0D);
			((NotaEntrada)this.getAdminOrden().getOrden()).setExcedentes(0D);
			((NotaEntrada)this.getAdminOrden().getOrden()).setImpuestos(0D);
			((NotaEntrada)this.getAdminOrden().getOrden()).setSubTotal(0D);
			((NotaEntrada)this.getAdminOrden().getOrden()).setOriginal(((NotaEntrada)this.getAdminOrden().getOrden()).getDeuda());
			((NotaEntrada)this.getAdminOrden().getOrden()).setTotal(((NotaEntrada)this.getAdminOrden().getOrden()).getDeuda());
      ((NotaEntrada)this.getAdminOrden().getOrden()).setIdAlmacenista(null);
			nota= new NotaEntradaProcess();
			nota.setNotaEntrada((NotaEntrada)this.getAdminOrden().getOrden());
			nota.setArticulos(this.getAdminOrden().getArticulos());
			nota.setFamilias(Arrays.asList((Object[])this.attrs.get("familiasSeleccion")));
			nota.setLotes(Arrays.asList((Object[])this.attrs.get("lotesSeleccion")));
      if(Cadena.isVacio(this.attrs.get("folio"))) {
        if(Boolean.TRUE || !Cadena.isVacio(this.getXml())) {
          if(Cadena.isVacio(this.getXml()) || this.getEmisor().getRfc().equals(this.proveedor.getRfc())) {
            transaccion = new Transaccion(nota, this.aplicar, this.getXml(), this.getPdf());
            if (transaccion.ejecutar(this.accion)) {
              if(this.accion.equals(EAccion.COMPLETO) || this.aplicar) {
                regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
                if(this.accion.equals(EAccion.COMPLETO))
                  UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la nota de entrada manual', '"+ ((NotaEntrada)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
                else
                  UIBackingUtilities.execute("jsArticulos.back('aplic\\u00F3 la nota de entrada manual', '"+ ((NotaEntrada)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
              } // if	
              else 
                if(!this.accion.equals(EAccion.CONSULTAR)) 
                  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.COMPLETO) ? "agregó" : "modificó").concat(" la nota de entrada"), ETipoMensaje.INFORMACION);
              JsfBase.setFlashAttribute("idEmpresaDeuda", this.attrs.get("idEmpresaDeuda"));
              JsfBase.setFlashAttribute("idProveedor", this.attrs.get("idProveedor"));
              JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
            } // if
            else 
              JsfBase.addMessage("Ocurrió un error al registrar la nota de entrada", ETipoMensaje.ERROR);      			
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
 		JsfBase.setFlashAttribute("idEmpresaDeuda", this.attrs.get("idEmpresaDeuda"));
  	JsfBase.setFlashAttribute("idNotaEntrada", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada());
  	JsfBase.setFlashAttribute("idProveedor", this.attrs.get("idProveedor"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } 

	private void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
  				((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(0));
			  else {
          int index= empresas.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkEmpresa());
          if(index>= 0)
				    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(index));
          else
  				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(0));
        } // if 
			} // if	
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("idOrdenCompra", this.attrs.get("idOrdenCompra"));
      this.attrs.put("almacenes", UIEntity.build("TcManticAlmacenesDto", "almacenes", params, columns));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty() && this.accion.equals(EAccion.COMPLETO)) 
				((NotaEntrada)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
      columns.remove(0);
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params.put("idProveedor", Objects.equals(this.accion, EAccion.COMPLETO)? this.attrs.get("idProveedor"): ((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedor());
      this.attrs.put("proveedores", UIEntity.build("VistaOrdenesComprasDto", "manual", params, columns));
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			int index= 0;
			if(!proveedores.isEmpty()) {
				if(this.accion.equals(EAccion.COMPLETO))
			    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(0));
				else {
				  index= proveedores.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor());
          if(index>= 0)
				    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(index));
          else
  			    ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(0));
				} // else
		    this.attrs.put("proveedor", proveedores.get(index));
			  this.proveedor= (TcManticProveedoresDto)DaoFactory.getInstance().findById(TcManticProveedoresDto.class, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor().getKey());
        this.toUpdateFamilias();
				this.toLoadCondiciones(new UISelectEntity(new Entity(this.proveedor.getIdProveedor())));
        this.doLoadDesarrollos();
			} // if	
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
	
	public void doUpdateProveedor() {
		try {			
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			UISelectEntity temporal= ((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor();
			temporal= proveedores.get(proveedores.indexOf(temporal));			
			this.attrs.put("proveedor", temporal);
			this.toLoadCondiciones(proveedores.get(proveedores.indexOf((UISelectEntity)((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedor())));			
		}	
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 

	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Importar"))
			this.doLoadFiles("TcManticNotasArchivosDto", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdNotaEntrada(), "idNotaEntrada", false, this.getAdminOrden().getTipoDeCambio());
	}
	
	public void doFileUpload(FileUploadEvent event) {
		if(this.proveedor!= null) {
      this.doFileUpload(event, ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("notasentradas"), this.proveedor.getClave(), true, this.getAdminOrden().getTipoDeCambio());
      if(event.getFile().getFileName().toUpperCase().endsWith(EFormatos.XML.name())) {
        if(Cadena.isVacio(this.getFactura().getFolio())) {
          String folio= this.getFactura().getTimbreFiscalDigital().getUuid();
  				((NotaEntrada)this.getAdminOrden().getOrden()).setFactura(folio.indexOf("-")> 0? folio.substring(folio.lastIndexOf("-")+ 1): folio);
        } // if  
        else  
  				((NotaEntrada)this.getAdminOrden().getOrden()).setFactura(this.getFactura().getFolio());
        ((NotaEntrada)this.getAdminOrden().getOrden()).setFechaFactura(Fecha.toLocalDateDefault(this.getFactura().getFecha()));
				((NotaEntrada)this.getAdminOrden().getOrden()).setOriginal(Numero.getDouble(this.getFactura().getTotal(), 0D));
 				((NotaEntrada)this.getAdminOrden().getOrden()).setDeuda(Numero.getDouble(this.getFactura().getTotal(), 0D));
        this.doCheckFolio();
        this.doCalculatePagoFecha();
      } // if
		} // if
		else 
			JsfBase.addMessage("Se tiene que seleccionar un proveedor primero", ETipoMensaje.ALERTA);      			
	} 
	
	public void doCheckFolio() {
		Map<String, Object> params= new HashMap<>();
		try {
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
		this.doCalculateFechaPagoInit();
	} 
	
	public void doCalculateFechaPagoInit() {		
		LocalDate fechaFactura= ((NotaEntrada)this.getAdminOrden().getOrden()).getFechaFactura();
		Calendar calendar= Calendar.getInstance();
		calendar.setTimeInMillis(fechaFactura.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		if(((NotaEntrada)this.getAdminOrden().getOrden()).getDiasPlazo()== null)
			((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(1L);
		calendar.add(Calendar.DATE, ((NotaEntrada)this.getAdminOrden().getOrden()).getDiasPlazo().intValue()- 1);
		((NotaEntrada)this.getAdminOrden().getOrden()).setFechaPago(LocalDate.now());
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

	private void toLoadCondiciones(UISelectEntity proveedor) {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("idProveedor", proveedor.getKey());
      this.attrs.put("condiciones", UIEntity.build("VistaOrdenesComprasDto", "condiciones", params, columns));
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");			
			if(!condiciones.isEmpty()) {				
				if(this.accion.equals(EAccion.COMPLETO))
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				else {
					Entity entity= new UISelectEntity(new Entity(((NotaEntrada)this.getAdminOrden().getOrden()).getIdProveedorPago()));
				  ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(condiciones.indexOf(entity)));
				} // if					
				((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
        ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
        this.doUpdatePorcentaje();
			} // if
			this.doCalculateFechaPagoInit();
    } // try
    catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // toLoadCondiciones
	
	@Override
	public void doUpdatePorcentaje() {
		if(!getAdminOrden().getArticulos().isEmpty())
			super.doUpdatePorcentaje();
	} // doUpdatePorcentaje
	
	public void doUpdatePlazo() {
		if(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago()!= null) {
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
      ((NotaEntrada)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(condiciones.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago())));
			((NotaEntrada)this.getAdminOrden().getOrden()).setDiasPlazo(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toLong("plazo")+ 1);
      this.doCalculateFechaPagoInit();
      ((NotaEntrada)this.getAdminOrden().getOrden()).setDescuento(((NotaEntrada)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
			this.doUpdatePorcentaje();
		} // if
	}	
	
	@Override
	public void doUpdateArticulo(String codigo, Integer index) {
	}
	
	@Override
	public void doDeleteArticulo(Integer index) {
	}

	@Override
  public void doFindArticulo(Integer index) {
	}

	public void doUpdateRfc() {
		this.doUpdateRfc(this.proveedor);
	}
 
	public void doLoadDesarrollos() {
		List<Columna> columns           = new ArrayList<>();
    Map<String, Object> params      = new HashMap<>();
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
    try {
      params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + ((NotaEntrada)this.getAdminOrden().getOrden()).getIdEmpresa());
			params.put("operador", "<=");
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
      this.attrs.put("desarrollos", desarrollos);			
			if(this.accion.equals(EAccion.COMPLETO) && (this.attrs.get("ordenCompra")== null || Objects.equals(-1L, ((TcManticOrdenesComprasDto)this.attrs.get("ordenCompra")).getIdDesarrollo())))
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
      ((NotaEntrada)this.getAdminOrden().getOrden()).setIdCliente(desarrollo.toLong("idCliente"));
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
		List<UISelectEntity> desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
		UISelectEntity desarrollo       = ((NotaEntrada)this.getAdminOrden().getOrden()).getIkDesarrollo();
		List<Columna> columns           = new ArrayList<>();
		Map<String, Object>params       = new HashMap<>();
		try {
      desarrollo= desarrollos.get(desarrollos.indexOf(desarrollo));
			this.attrs.put("cliente", desarrollo.toString("razonSocial"));			
      ((NotaEntrada)this.getAdminOrden().getOrden()).setIdCliente(desarrollo.toLong("idCliente"));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			this.doLoadContratos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} 
  
	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = new HashMap<>();
		try {
			params.put("idDesarrollo", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdDesarrollo());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.COMPLETO) && Objects.equals(-1L, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkContrato().getKey()))
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
	} 

	private void toUpdateFamilias() throws Exception {
		UISelectEntity vendedor      = (UISelectEntity) this.attrs.get("proveedor");
		List<UISelectEntity> familias= null;
		Map<String, Object>params    = new HashMap<>();
    List<Entity> items           = null;
    Object[] list                = null;
		try {
			params.put("idProveedor", vendedor.getKey());
			familias= UIEntity.build("VistaFamiliasProveedoresDto", "row", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("familias", familias);			
      if(!familias.isEmpty())
        if(this.accion.equals(EAccion.COMPLETO)) {
    			params.put("idOrdenCompra", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdOrdenCompra());
          items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetOrdenesFamiliasDto", "familias", params);
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
	} 
  
	public void doLoadLotes() {
		List<UISelectEntity> lotes= null;
		Map<String, Object>params = new HashMap<>();
    List<Entity> items        = null; 
    Object[] list             = null;
		try {
			params.put("idContrato", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdContrato());
			lotes= UIEntity.build("TcKeetContratosLotesDto", "byContratoContratistas", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("lotes", lotes);						
      if(!lotes.isEmpty()) 
        if(this.accion.equals(EAccion.COMPLETO)) { 
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
        for (Entity entity: items) {
          int index= lotes.indexOf(new UISelectEntity(entity.toLong("idContratoLote")));
          if(index>= 0)
            list[count++]= lotes.get(index);
        } // for
      } // if  
      if(list== null)
        list= new Object[] {};
      this.attrs.put("lotesSeleccion", list);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch
		finally {
			Methods.clean(params);
		} // finally
  } 
  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = new HashMap<>();
		List<Columna> columns      = new ArrayList<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.seleccione("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS, "nombre");
			this.attrs.put("bancos", bancos);
      if(!bancos.isEmpty()) 
        if(this.accion.equals(EAccion.COMPLETO) && (Objects.equals(-1L, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkBanco()) || Objects.equals(null, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkBanco())))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(0));
        else  {
          int index= bancos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkBanco());
          if(index>= 0)
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(index));
          else
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(0));
        } // else  
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
 
	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja= 1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(!tiposMediosPagos.isEmpty()) 
        if(this.accion.equals(EAccion.COMPLETO) && (Objects.equals(-1L, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoMedioPago()) || Objects.equals(null, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoMedioPago())))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(0));
        else {
          int index= tiposMediosPagos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoMedioPago());
          if(index>= 0)
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(index));
          else
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(0));
        } // else  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	}   
  
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposPagos= UIEntity.seleccione("TcManticTiposPagosDto", "row", params, "nombre");
			this.attrs.put("tiposPagos", tiposPagos);
      if(!tiposPagos.isEmpty()) 
        if(this.accion.equals(EAccion.COMPLETO) && (Objects.equals(-1L, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoPago()) || Objects.equals(null, ((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoPago())))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(0));
        else  {
          int index= tiposPagos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkTipoPago());
          if(index>= 0)
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(index));
          else
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(0));
        } // else  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
 
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
          "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'requerido', mascara: 'libre'});"
        );		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 
 
	private void toLoadEmpresaTipoContacto() {
		List<UISelectEntity> empresaTiposContactos= null;
		Map<String, Object>params                 = new HashMap<>();
		try {
			params.put("idEmpresa", ((NotaEntrada)this.getAdminOrden().getOrden()).getIdEmpresa());
			params.put("idTipoContacto", "9, 10, 11, 15, 16, 17, 18");
			empresaTiposContactos= UIEntity.build("TrManticEmpresaTipoContactoDto", "tipos", params);
			this.attrs.put("empresaTiposContactos", empresaTiposContactos);
      if(!empresaTiposContactos.isEmpty()) 
        if(this.accion.equals(EAccion.COMPLETO))
          ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(0));
        else  {
          int index= empresaTiposContactos.indexOf(((NotaEntrada)this.getAdminOrden().getOrden()).getIkEmpresaTipoContacto());
          if(index> 0)
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(index));
          else 
            ((NotaEntrada)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(0));
        } // if  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
  
}