package mx.org.kaana.mantic.compras.requisiciones.backing;

import java.io.Serializable;
import java.util.ArrayList;
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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.ventas.reglas.MotorBusqueda;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.requisiciones.beans.RegistroRequisicion;
import mx.org.kaana.mantic.compras.requisiciones.beans.TicketRequisicion;
import mx.org.kaana.mantic.compras.requisiciones.reglas.Transaccion;
import mx.org.kaana.mantic.compras.requisiciones.reglas.AdminTickets;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;

@Named(value= "manticComprasRequisicionesAccion")
@ViewScoped
public class Accion extends IBaseArticulos implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
  
	private RegistroRequisicion registroRequisicion;	
	protected FormatLazyModel lazyModelPaquetes;
  private EAccion accion;
	
	public Accion() {
		super("menudeo");
    this.attrs.put("paginator", Boolean.FALSE);
	}

	public RegistroRequisicion getRegistroRequisicion() {
		return registroRequisicion;
	}

	public void setRegistroRequisicion(RegistroRequisicion registroRequisicion) {
		this.registroRequisicion = registroRequisicion;
	}	

  public FormatLazyModel getLazyModelPaquetes() {
    return lazyModelPaquetes;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idRequisicion", JsfBase.getFlashAttribute("idRequisicion")== null? -1L: JsfBase.getFlashAttribute("idRequisicion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("isPesos", Boolean.FALSE);
			this.attrs.put("sinIva", Boolean.FALSE);
			this.attrs.put("buscaPorCodigo", Boolean.FALSE);
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("isMatriz", JsfBase.isAdminEncuestaOrAdmin());					
			this.attrs.put("nombreEmpresa", JsfBase.getAutentifica().getEmpresa().getNombre());
			this.attrs.put("solicita", JsfBase.getAutentifica().getPersona().getNombreCompleto());
			this.doLoad();
			this.toLoadProveedores();
      this.toLoadEmpresas();
      this.attrs.put("cuantos", 1L);
      this.attrs.put("aplicar", Boolean.TRUE);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:		
					this.registroRequisicion= new RegistroRequisicion();
          this.setAdminOrden(new AdminTickets(new TicketRequisicion(-1L)));					
          break;
        case MODIFICAR:			
        case CONSULTAR:	
					this.registroRequisicion= new RegistroRequisicion((Long)this.attrs.get("idRequisicion"));					
          this.setAdminOrden(new AdminTickets((TicketRequisicion)DaoFactory.getInstance().toEntity(TicketRequisicion.class, "TcManticRequisicionesDto", "detalle", this.attrs)));
          if(Objects.equals(this.accion, EAccion.CONSULTAR)) {
            this.getAdminOrden().getArticulos().remove(this.getAdminOrden().getArticulos().size()- 1);
            this.getAdminOrden().getTotales().setArticulos(this.getAdminOrden().getTotales().getArticulos()- 1);
          } // this  
          break;
      } // switch
			this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			if(this.registroRequisicion.validateDuplicateProveedor()) {
				this.toAdjustArticulos();
        this.registroRequisicion.getRequisicion().setDescuento(this.getAdminOrden().getDescuento());
        this.registroRequisicion.getRequisicion().setDescuentos(this.getAdminOrden().getTotales().getDescuentos());
        this.registroRequisicion.getRequisicion().setImpuestos(this.getAdminOrden().getTotales().getIva());
        this.registroRequisicion.getRequisicion().setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
        this.registroRequisicion.getRequisicion().setTotal(this.getAdminOrden().getTotales().getTotal());
        if(Objects.equals(this.registroRequisicion.getRequisicion().getIdContrato(), -1L))
          this.registroRequisicion.getRequisicion().setIdContrato(null);
				transaccion = new Transaccion(this.registroRequisicion, this.getAdminOrden().getArticulos());
				if (transaccion.ejecutar(this.accion)) {
          this.attrs.put("idRequisicion", this.registroRequisicion.getRequisicion().getIdRequisicion());
					regresar= this.doCancelar();
//					if(Objects.equals(this.accion, EAccion.AGREGAR)) 
//						UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 requisición', '"+ this.registroRequisicion.getRequisicion().getConsecutivo()+ "');");
					JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la requsicion"), ETipoMensaje.INFORMACION);
				} // if
				else 
					JsfBase.addMessage("Ocurrió un error al registrar la requisición", ETipoMensaje.ERROR);      			
        if(Objects.equals(this.registroRequisicion.getRequisicion().getIdContrato(), null))
          this.registroRequisicion.getRequisicion().setIdContrato(-1L);
			} // if
			else
				JsfBase.addMessage("Se agregaron proveedores duplicados, favor de verificarlo", ETipoMensaje.ERROR);      						
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

	public void toAdjustArticulos() {
		int count= 0;
		while(count< this.getAdminOrden().getArticulos().size()) {
			if(!this.getAdminOrden().getArticulos().get(count).isValid())
				this.getAdminOrden().getArticulos().remove(count);
			else
				if(count> 0 && this.getAdminOrden().getArticulos().get(count- 1).getKey().equals(this.getAdminOrden().getArticulos().get(count).getKey())) {
					this.getAdminOrden().getArticulos().get(count- 1).setCantidad(this.getAdminOrden().getArticulos().get(count- 1).getCantidad()+ this.getAdminOrden().getArticulos().get(count).getCantidad());
					this.getAdminOrden().getArticulos().remove(count);
				} // if
				else
				  count++;
		} // while
    this.doReCalculatePreciosArticulos(Boolean.TRUE, -1L);
	}
	
  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idRequisicion", this.attrs.get("idRequisicion"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar			
	
	public void doReCalculatePreciosArticulos(Long idCliente){
		this.doReCalculatePreciosArticulos(true, idCliente);
	} // doReCalculatePreciosArticulos
	
	public void doReCalculatePreciosArticulos(boolean descuentoVigente, Long idCliente) {
		MotorBusqueda motor          = null;
		TcManticArticulosDto articulo= null;
		String descuento             = null;
		String sinDescuento          = "0";
		try {
			if(!this.getAdminOrden().getArticulos().isEmpty()){
				for(Articulo beanArticulo: this.getAdminOrden().getArticulos()){
					if(beanArticulo.getIdArticulo()!= null && !beanArticulo.getIdArticulo().equals(-1L)) {
						motor= new MotorBusqueda(beanArticulo.getIdArticulo());
						articulo= motor.toArticulo();
						beanArticulo.setValor((Double) articulo.toValue(this.getPrecio()));
						beanArticulo.setCosto((Double) articulo.toValue(this.getPrecio()));
						if(descuentoVigente){
							descuento= this.toDescuentoVigente(beanArticulo.getIdArticulo(), idCliente);
							if(descuento!= null)
								beanArticulo.setDescuento(descuento);							
						} // if
						else
							beanArticulo.setDescuento(sinDescuento);
					} // if
				} // for					
				if(this.getAdminOrden().getArticulos().size()> 0) {					
					this.getAdminOrden().toCalculate();
					UIBackingUtilities.update("@(.filas) @(.recalculo) @(.informacion)");
				} // if
			} // if			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	@Override
	protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {
		Articulo temporal= this.getAdminOrden().getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
				this.doSearchArticulo(articulo.toLong("idArticulo"), index);
				params.put("idArticulo", articulo.toLong("idArticulo"));
				params.put("idProveedor", getAdminOrden().getIdProveedor());
				params.put("idAlmacen", getAdminOrden().getIdAlmacen());
				temporal.setKey(articulo.toLong("idArticulo"));
				temporal.setIdArticulo(articulo.toLong("idArticulo"));
				temporal.setIdProveedor(getAdminOrden().getIdProveedor());
				temporal.setIdRedondear(articulo.toLong("idRedondear"));
				Value codigo= (Value)DaoFactory.getInstance().toField("TcManticArticulosCodigosDto", "codigo", params, "codigo");
				temporal.setCodigo(codigo== null? "": codigo.toString());
				temporal.setPropio(articulo.toString("propio"));
				temporal.setNombre(articulo.toString("nombre"));
				temporal.setValor(articulo.toDouble(getPrecio()));
				temporal.setCosto(articulo.toDouble(getPrecio()));
				temporal.setIva(articulo.toDouble("iva"));
				temporal.setDescuento(getAdminOrden().getDescuento());
				temporal.setExtras(getAdminOrden().getExtras());
				if(temporal.getCantidad()< 1D)
				  temporal.setCantidad(1D);
				temporal.setUnidadMedida(articulo.toString("unidadMedida"));
				temporal.setPrecio(articulo.toDouble("precio"));				
				temporal.setUltimo(this.attrs.get("ultimo")!= null);
				temporal.setSolicitado(this.attrs.get("solicitado")!= null);
				Value stock= (Value)DaoFactory.getInstance().toField("TcManticInventariosDto", "stock", params, "stock");
				temporal.setStock(stock== null? 0D: stock.toDouble());
				if(index== getAdminOrden().getArticulos().size()- 1) {
					this.getAdminOrden().getArticulos().add(new Articulo(-1L));
					this.getAdminOrden().toAddUltimo(this.getAdminOrden().getArticulos().size()- 1);
					UIBackingUtilities.execute("jsArticulos.update("+ (getAdminOrden().getArticulos().size()- 1)+ ");");
				} // if	
				UIBackingUtilities.execute("jsArticulos.callback('"+ articulo.getKey()+ "');");				
				this.getAdminOrden().toCantidad();
			} // if	
			else
				temporal.setNombre("<span class='janal-color-orange'>EL ARTICULO NO EXISTE EN EL CATALOGO !</span>");
		} // try
		finally {
			Methods.clean(params);
		} // finally
	} // toMoveData
	
	@Override
	public void doCalculate(Integer index) {
		this.getAdminOrden().toCantidad();
	} // doCalculate
	
	private String toDescuentoVigente(Long idArticulo, Long idCliente) throws Exception{
		MotorBusqueda motorBusqueda= null;
		Entity descuentoVigente    = null;
		String regresar            = null;
		try {
			motorBusqueda= new MotorBusqueda(idArticulo, idCliente);
			descuentoVigente= motorBusqueda.toDescuentoGrupo();
			if(descuentoVigente!= null)
				regresar= descuentoVigente.toString("porcentaje");
		} // try
		catch (Exception e) {			
			throw e;			
		} // catch		
		return regresar;
	} // toDescuentoVigente	
	
	@Override
	public void doUpdateArticulos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", this.attrs.get("proveedor")== null? new UISelectEntity(new Entity(-1L)): ((UISelectEntity)this.attrs.get("proveedor")).getKey());
			String search= (String)this.attrs.get("codigo"); 
			if(!Cadena.isVacio(search)) {
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= search.startsWith(".");
				if(buscaPorCodigo)
					search= search.trim().substring(1);
				search= search.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				search= "WXYZ";
  		params.put("codigo", search);
      params.put("idArticuloTipo", "1");	      
			if((boolean)this.attrs.get("buscaPorCodigo") || buscaPorCodigo)
        this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.buildImage("VistaOrdenesComprasDto", "porCodigo", params, columns, 20L));
			else
        this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.buildImage("VistaOrdenesComprasDto", "porNombre", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doUpdateArticulos
	
	private void toLoadProveedores() {
    List<UISelectItem> proveedores= null;
    Map<String, Object> params    = new HashMap<>();
    try {
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      proveedores = UISelect.build("TcManticProveedoresDto", "sucursales", params, "razonSocial", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("proveedoresGeneral", proveedores);
			if(proveedores!= null)
			  this.registroRequisicion.setTotalProveedores(proveedores.size());
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadProveedores
	
	@Override
	public void doDeleteArticulo(Integer index) {
		this.doDeleteArticulo(index, Boolean.TRUE);
	}		

 	protected void toLoadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> empresas= (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
      if(Objects.equals(this.accion, EAccion.AGREGAR))
	      this.attrs.put("idEmpresa", this.toDefaultSucursal(empresas));
      else {
        int index= empresas.indexOf(this.registroRequisicion.getRequisicion().getIkEmpresa());
        if(index>= 0)
  	      this.attrs.put("idEmpresa", empresas.get(index));
        else
  	      this.attrs.put("idEmpresa", this.toDefaultSucursal(empresas));
      } // if  
      this.doLoadDesarrollos();
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

  public void doLoadDesarrollos() {
		List<UISelectEntity>desarrollos= null;
    Map<String, Object> params     = new HashMap<>();
    try {
  		params.put("idPersona", JsfBase.getAutentifica().getPersona().getIdPersona());
      params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.LIQUIDADO.ordinal());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		desarrollos= UIEntity.build("VistaDesarrollosDto", JsfBase.isAdminEncuestaOrAdmin() || JsfBase.isEncargado()? "lazy": "residentes", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("desarrollos", desarrollos);
      if(Objects.equals(this.accion, EAccion.AGREGAR))
	      this.registroRequisicion.getRequisicion().setIkDesarrollo(UIBackingUtilities.toFirstKeySelectEntity(desarrollos));
      else {
        int index= desarrollos.indexOf(this.registroRequisicion.getRequisicion().getIkDesarrollo());
        if(index>= 0)
  	      this.registroRequisicion.getRequisicion().setIkDesarrollo(desarrollos.get(index));
        else
  	      this.registroRequisicion.getRequisicion().setIkDesarrollo(UIBackingUtilities.toFirstKeySelectEntity(desarrollos));
      } // if  
      this.doLoadContratos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
  }

  private void toLoadDesarrollos() {
		List<UISelectEntity>desarrollos= null;
    try {      
      desarrollos= (List<UISelectEntity>)this.attrs.get("desarrollos");
      if(!Objects.equals(desarrollos, null)) {
        int index= desarrollos.indexOf(new UISelectEntity(this.registroRequisicion.getRequisicion().getIkDesarrollo().getKey()));
        if(index>= 0) {
          this.registroRequisicion.getRequisicion().setIkDesarrollo(desarrollos.get(index));
          this.registroRequisicion.getRequisicion().setIkEmpresa(new UISelectEntity(desarrollos.get(index).toLong("idEmpresa")));
        } // if  
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doLoadContratos() {
		List<UISelectEntity>contratos= null;
    Map<String, Object> params   = new HashMap<>();
    try {
      this.toLoadDesarrollos();
      params.put("idDesarrollo", this.registroRequisicion.getRequisicion().getIkDesarrollo().getKey());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		contratos= UIEntity.seleccione("VistaContratosDto", "desarrollos", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      this.attrs.put("contratos", contratos);
      if(Objects.equals(this.accion, EAccion.AGREGAR))
	      this.registroRequisicion.getRequisicion().setIkContrato(UIBackingUtilities.toFirstKeySelectEntity(contratos));
      else {
        int index= contratos.indexOf(this.registroRequisicion.getRequisicion().getIkContrato());
        if(index>= 0)
  	      this.registroRequisicion.getRequisicion().setIkContrato(contratos.get(index));
        else
  	      this.registroRequisicion.getRequisicion().setIkContrato(UIBackingUtilities.toFirstKeySelectEntity(contratos));
      } // if  
      this.toLoadPaquetes();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally    
  }

  private void toLoadPaquetes() {
		List<UISelectEntity>paquetes= null;
    Map<String, Object> params  = new HashMap<>();
    try {
      params.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_prototipos.id_prototipo, tc_keet_procesos.nombre, tc_keet_sub_procesos.nombre");
      params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo= "+ this.registroRequisicion.getRequisicion().getIkDesarrollo().getKey());
  		paquetes= UIEntity.seleccione("VistaPaquetesDto", "lazy", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "proceso");
      this.attrs.put("paquetes", paquetes);
      if(!Objects.equals(paquetes, null) && !paquetes.isEmpty())
        this.registroRequisicion.getRequisicion().setIkPaquete(paquetes.get(0));
      this.doLoadPaquetes();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally    
  }
  
  public void doLoadPaquetes() {
    List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idPaquete", this.registroRequisicion.getRequisicion().getIkPaquete().getKey());
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModelPaquetes = new FormatCustomLazy("TcKeetPaquetesDetallesDto", "igual", params, columns);
      UIBackingUtilities.resetDataTable("paquetes");
 			this.attrs.put("paquete", null);
      this.attrs.put("aplicar", Objects.equals(this.registroRequisicion.getRequisicion().getIkPaquete().getKey(), -1L));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  }

  public void doDefineEmpresa() {
		List<UISelectEntity>contratos= null;
    try {      
      contratos= (List<UISelectEntity>)this.attrs.get("contratos");
      if(!Objects.equals(contratos, null)) {
        int index= contratos.indexOf(new UISelectEntity(this.registroRequisicion.getRequisicion().getIkContrato().getKey()));
        if(index>= 0) 
          this.registroRequisicion.getRequisicion().setIkEmpresa(new UISelectEntity(contratos.get(index).toLong("idEmpresa")));
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public void doAplicar() {
    List<Entity> items        = null;
    Map<String, Object> params= new HashMap<>();
    Long factor               = (Long)this.attrs.get("cuantos");
    try {
      params.put("idPaquete", this.registroRequisicion.getRequisicion().getIkPaquete().getKey());
      items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetPaquetesDetallesDto", "detalle", params, Constantes.SQL_TODOS_REGISTROS);
      if(!Objects.equals(items, null) && !items.isEmpty()) {
        for (Entity item: items) {
          int index= this.getAdminOrden().getArticulos().indexOf(new Articulo(item.toLong("idArticulo")));
          if(index< 0) {
            params.put("idArticulo", item.toLong("idArticulo"));
            params.put("idProveedor", getAdminOrden().getIdProveedor());
            params.put("idAlmacen", getAdminOrden().getIdAlmacen());
            Articulo temporal= new Articulo(item.toLong("idArticulo"));
            temporal.setIdArticulo(item.toLong("idArticulo"));
            temporal.setIdProveedor(getAdminOrden().getIdProveedor());
            temporal.setIdRedondear(item.toLong("idRedondear"));
            temporal.setCodigo(item.toString("codigo"));
            temporal.setPropio(item.toString("propio"));
            temporal.setNombre(item.toString("nombre"));
            temporal.setValor(item.toDouble(getPrecio()));
            temporal.setCosto(item.toDouble(getPrecio()));
            temporal.setIva(item.toDouble("iva"));
            temporal.setDescuento(getAdminOrden().getDescuento());
            temporal.setExtras(getAdminOrden().getExtras());
            temporal.setUnidadMedida(item.toString("unidadMedida"));
            temporal.setPrecio(item.toDouble("precio"));				
            temporal.setUltimo(this.attrs.get("ultimo")!= null);
            temporal.setSolicitado(this.attrs.get("solicitado")!= null);
            Value stock= (Value)DaoFactory.getInstance().toField("TcManticInventariosDto", "stock", params, "stock");
            temporal.setStock(stock== null? 0D: stock.toDouble());
            temporal.setCantidad(item.toDouble("cantidad")* factor);
            this.getAdminOrden().getArticulos().add(this.getAdminOrden().getArticulos().size()- 1, temporal);
          } // if
          else {
            Double cantidad= this.getAdminOrden().getArticulos().get(index).getCantidad();
            this.getAdminOrden().getArticulos().get(index).setCantidad(cantidad+ (item.toDouble("cantidad")* factor));
          } // else  
        } // for
        this.getAdminOrden().toAddUltimo(this.getAdminOrden().getArticulos().size()- 1);
        this.getAdminOrden().toCantidad();
        UIBackingUtilities.execute("jsArticulos.update("+ (getAdminOrden().getArticulos().size()- 1)+ ");");
        UIBackingUtilities.execute("jsArticulos.callback('"+ this.getAdminOrden().getArticulos().get(this.getAdminOrden().getArticulos().size()- 1).getKey()+ "');");				
        UIBackingUtilities.execute("PF('wContenedorGrupos').select(1);");				
        JsfBase.addMessage("Aplicar", "Se agregaron los materiales a la requisición, verifique de favor !");
      } // if  
      else
        JsfBase.addMessage("Aplicar", "No existen materiales que agregar !");        
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally		
  }
  
} 
