package mx.org.kaana.mantic.compras.ordenes.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.compras.beans.General;
import mx.org.kaana.keet.compras.beans.Individual;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 10:29:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class OrdenCompra extends TcManticOrdenesComprasDto implements Serializable {

	private static final long serialVersionUID=3088884892456452488L;
  private static final String ERROR_ARTICULO_EXCEDE = "summary: '{tipo}:', detail: '[{codigo}] {nombre}, SE EXCEDE CON LA CANTIDAD DE {cantidad}'";
  private static final String ERROR_ARTICULO_DIFIERE= "summary: '{tipo}:', detail: '[{codigo}] {nombre}, DIFIERE SU CANTIDAD DE {cantidad} A {diferencia}'";
  private static final String ERROR_ARTICULO_TODOS  = "summary: '{tipo}:', detail: '[{codigo}] {nombre}, ESTE ARTICULOS NO ESTA PRESUPUESTADO, {cantidad}'";
	
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikAlmacen;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;
	private UISelectEntity ikProveedor;
	private UISelectEntity ikProveedorPago;
	private UISelectEntity ikBanco;
	private UISelectEntity ikTipoMedioPago;
	private UISelectEntity ikTipoPago;
	private UISelectEntity ikAlmacenista;
	private UISelectEntity ikEmpresaTipoContacto;
	private UISelectEntity ikTipoOrden;
  private List<General> general;
  private List<Individual> individual;
  private List<Individual> temporal;
  private List<Detalle> detalles;
  private Long itEmpresa;

	public OrdenCompra() {
		this(-1L);
	}

	public OrdenCompra(Long key) {
		this(-1L, 0D, -1L, -1L, "0.00", Math.abs(new Random().nextLong())* -1L, "0.00", new Long(Calendar.getInstance().get(Calendar.YEAR)), Calendar.getInstance().get(Calendar.YEAR)+ "00000", 2L, 0D, 1L, LocalDate.now(), 1L, -1L, 0D, 0D, 1D, 2L, "", -1L, 1L, 0D);
	}

	public OrdenCompra(Long idProveedorPago, Double descuentos, Long idProveedor, Long idCliente, String descuento, Long idOrdenCompra, String extras, Long ejercicio, String consecutivo, Long idGasto, Double total, Long idOrdenEstatus, LocalDate entregaEstimada, Long idUsuario, Long idAlmacen, Double impuestos, Double subTotal, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes) {
		super(idProveedorPago, descuentos, idProveedor, idCliente, descuento, idOrdenCompra, extras, ejercicio, consecutivo, idGasto, total, idOrdenEstatus, entregaEstimada, idUsuario, idAlmacen, impuestos, subTotal, tipoDeCambio, idSinIva, observaciones, idEmpresa, orden, excedentes, -1L, -1L, -1L, -1L, -1L, null, -1L, -1L, null, 1L);
    this.general   = new ArrayList<>();
    this.individual= new ArrayList<>();
    this.temporal  = new ArrayList<>();
    this.detalles  = new ArrayList<>();
    this.itEmpresa = idEmpresa;
	}

	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
	}

	public UISelectEntity getIkAlmacen() {
		return ikAlmacen;
	}

	public void setIkAlmacen(UISelectEntity ikAlmacen) {
		this.ikAlmacen=ikAlmacen;
		if(this.ikAlmacen!= null)
		  this.setIdAlmacen(this.ikAlmacen.getKey());
	}

	public UISelectEntity getIkProveedor() {
		return ikProveedor;
	}

	public void setIkProveedor(UISelectEntity ikProveedor) {
		this.ikProveedor=ikProveedor;
		if(this.ikProveedor!= null)
		  this.setIdProveedor(this.ikProveedor.getKey());
	}

	public UISelectEntity getIkProveedorPago() {
		return ikProveedorPago;
	}

	public void setIkProveedorPago(UISelectEntity ikProveedorPago) {
		this.ikProveedorPago=ikProveedorPago;
		if(this.ikProveedorPago!= null)
		  this.setIdProveedorPago(this.ikProveedorPago.getKey());
	}

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
		  this.setIdDesarrollo(this.ikDesarrollo.getKey());
  }

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente=ikCliente;
		if(this.ikCliente!= null)
		  this.setIdCliente(this.ikCliente.containsKey("idCliente")? this.ikCliente.toLong("idCliente"): this.ikCliente.getKey());
	}

  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
		if(this.ikContrato!= null) 
		  this.setIdContrato(this.ikContrato.getKey());
  }

  public UISelectEntity getIkBanco() {
    return ikBanco;
  }

  public void setIkBanco(UISelectEntity ikBanco) {
    this.ikBanco = ikBanco;
		if(this.ikBanco!= null)
		  this.setIdBanco(this.ikBanco.getKey());
  }

  public UISelectEntity getIkTipoMedioPago() {
    return ikTipoMedioPago;
  }

  public void setIkTipoMedioPago(UISelectEntity ikTipoMedioPago) {
    this.ikTipoMedioPago = ikTipoMedioPago;
		if(this.ikTipoMedioPago!= null)
		  this.setIdTipoMedioPago(this.ikTipoMedioPago.getKey());
  }

  public UISelectEntity getIkTipoPago() {
    return ikTipoPago;
  }

  public void setIkTipoPago(UISelectEntity ikTipoPago) {
    this.ikTipoPago = ikTipoPago;
		if(this.ikTipoPago!= null)
		  this.setIdTipoPago(this.ikTipoPago.getKey());
  }

  public UISelectEntity getIkAlmacenista() {
    return ikAlmacenista;
  }

  public void setIkAlmacenista(UISelectEntity ikAlmacenista) {
    this.ikAlmacenista = ikAlmacenista;
		if(this.ikAlmacenista!= null)
		  this.setIdAlmacenista(this.ikAlmacenista.getKey());
  }

  public UISelectEntity getIkEmpresaTipoContacto() {
    return ikEmpresaTipoContacto;
  }

  public void setIkEmpresaTipoContacto(UISelectEntity ikEmpresaTipoContacto) {
    this.ikEmpresaTipoContacto = ikEmpresaTipoContacto;
		if(this.ikEmpresaTipoContacto!= null)
		  this.setIdEmpresaTipoContacto(this.ikEmpresaTipoContacto.getKey());
  }
	
  public UISelectEntity getIkTipoOrden() {
    return ikTipoOrden;
  }

  public void setIkTipoOrden(UISelectEntity ikTipoOrden) {
    this.ikTipoOrden = ikTipoOrden;
		if(this.ikTipoOrden!= null)
		  this.setIdTipoOrden(this.ikTipoOrden.getKey());
  }

  public Long getItEmpresa() {
    return itEmpresa;
  }

  public void setItEmpresa(Long itEmpresa) {
    this.itEmpresa = itEmpresa;
  }

	@Override
	public Class toHbmClass() {
		return TcManticOrdenesComprasDto.class;
	}

  public List<General> getGeneral() {
    return general;
  }

  public List<Individual> getIndividual() {
    return individual;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    Methods.clean(this.ikEmpresa);
    Methods.clean(this.ikAlmacen);
    Methods.clean(this.ikDesarrollo);
    Methods.clean(this.ikCliente);
    Methods.clean(this.ikContrato);
    Methods.clean(this.ikProveedor);
    Methods.clean(this.ikProveedorPago);
    Methods.clean(this.ikBanco);
    Methods.clean(this.ikTipoMedioPago);
    Methods.clean(this.ikTipoPago);
    Methods.clean(this.ikAlmacenista);
    Methods.clean(this.ikEmpresaTipoContacto);
    Methods.clean(this.ikTipoOrden);    
    Methods.clean(this.general);
    Methods.clean(this.individual);
  }

  public void toLoadArticulos(String articulos) {
    this.toLoadGeneral(this.getIdContrato(), articulos);
  }
  
  public void toLoadArticulos(String articulos, String lotes) {
    this.toLoadGeneral(this.getIdContrato(), articulos);
    this.toLoadIndividual(this.getIdContrato(), lotes, articulos);
  }
  
  public void toCleanPartidas() {
    this.general.clear();
    this.individual.clear();
  }

  private void toLoadGeneral(Long idContrato, String articulos) {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      Methods.clean(this.general);
      if(!Objects.equals(idContrato, -1L)) {
        params.put("idContrato", idContrato);      
        params.put("articulos", articulos);      
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
        columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
        this.general= (List<General>)DaoFactory.getInstance().toEntitySet(General.class, "VistaContratosMaterialesDto", "general", params, Constantes.SQL_TODOS_REGISTROS);
        if(this.general!= null && !this.general.isEmpty()) 
          this.toValuesGeneral();
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }
  
  private void toLoadIndividual(Long idContrato, String lotes, String articulos) {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {    
      Methods.clean(this.individual);
      if(!Objects.equals(idContrato, -1L)) {
        params.put("idContrato", idContrato);      
        params.put("articulos", articulos);      
        params.put(Constantes.SQL_CONDICION, "(tt_keet_contratos_lotes.id_contrato_lote in (".concat(lotes).concat("))"));      
        columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
        this.individual= (List<Individual>)DaoFactory.getInstance().toEntitySet(Individual.class, "VistaContratosMaterialesDto", "individual", params, Constantes.SQL_TODOS_REGISTROS);
        if(this.individual!= null && !this.individual.isEmpty()) 
          this.toValuesIndividual();
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }

  public void toLoadTemporal() {
    Map<String, Object> params= new HashMap<>();
    StringBuilder articulos   = new StringBuilder(",");
    StringBuilder lotes       = new StringBuilder(",");
    try {
      if(this.isValid()) {
        params.put("idOrdenCompra", this.getIdOrdenCompra());      
        this.temporal= (List<Individual>)DaoFactory.getInstance().toEntitySet(Individual.class, "TcKeetOrdenesMaterialesDto", "detalle", params);      
        if(this.temporal== null)
          this.temporal= new ArrayList<>();
        else {
          for (Individual item: this.temporal) {
            if(articulos.indexOf(","+ item.getIdArticulo()+",")< 0)
              articulos.append(item.getIdArticulo()).append(",");
            if(!Objects.equals(item.getIdContratoLote(), null) && !Objects.equals(item.getIdContratoLote(), -1L))
              if(lotes.indexOf(","+ item.getIdContratoLote()+",")< 0)
                lotes.append(item.getIdContratoLote()).append(",");
          } // for
          if(lotes.length()> 1)
            this.toLoadArticulos(articulos.substring(1, articulos.length()- 1), lotes.substring(1, lotes.length()- 1));
          else
            this.toLoadArticulos(articulos.substring(1, articulos.length()- 1));
        } // if
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public void toLookArticulos(Articulo articulo) {
    Integer count= 0;
    try { 
      for (Individual item: this.individual) {
        if(Objects.equals(articulo.getIdArticulo(), item.getIdArticulo()))
          count++;
      } // for
      // SI SE ENCUENTRA MAS DE UN CONCEPTO REALIZAR EL REEMPLAZO
      if(count> 0) {
        Double sum  = 0D;
        Double value= Numero.toRedondearSat(articulo.getCantidad()/ count);
        for (Individual item: this.individual) {
          if(Objects.equals(articulo.getIdArticulo(), item.getIdArticulo())) {
            if(sum+ value>= articulo.getCantidad())
              item.setTotal(Numero.toRedondearSat(articulo.getCantidad()- sum));
            else  
              item.setTotal(value);
            sum+= value;
          } // if  
        } // for
      } // if  
      for (General item: this.general) {
        if(Objects.equals(articulo.getIdArticulo(), item.getIdArticulo()))
          item.setTotal(articulo.getCantidad());
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
  }
  
  public String toCheckGeneral() {
    StringBuilder regresar    = new StringBuilder();
    Map<String, Object> params= new HashMap<>();
    try {
      for (General item: this.general) {
        if((item.getTotal()> 0D) && (item.getTotal()> item.getDiferencia())) {
          params.put("tipo", "Contrato");
          params.put("codigo", item.getCodigo());
          params.put("nombre", item.getNombre());
          params.put("cantidad", Numero.redondearSat(item.getTotal()- item.getDiferencia()));
          regresar.append("{").append(Cadena.replaceParams(ERROR_ARTICULO_EXCEDE, params)).append("},");
        } // if  
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 1): "";
  }
  
  public String toCheckIndividual(List<Articulo> articulos) {
    StringBuilder regresar    = new StringBuilder();
    Double sum                = null; 
    Boolean find              = null;
    Map<String, Object> params= new HashMap<>();
    try {
      for (Articulo articulo: articulos) {
        sum = 0D;
        find= Boolean.FALSE;
        for (Individual item: this.individual) {
          if(Objects.equals(articulo.getIdArticulo(), item.getIdArticulo())) {
            sum+= item.getTotal();
            find= Boolean.TRUE;
          } // if  
        } // for
        if(!Objects.equals(articulo.getIdArticulo(), -1L) && Numero.toRedondearSat(sum)!= articulo.getCantidad() && find) {
          params.put("tipo", "Partida");
          params.put("codigo", articulo.getPropio());
          params.put("nombre", articulo.getNombre());
          params.put("cantidad", articulo.getCantidad());
          params.put("diferencia", Numero.redondearSat(sum));
          regresar.append("{").append(Cadena.replaceParams(ERROR_ARTICULO_DIFIERE, params)).append("},");
        } // if  
      } // for
      for (Individual item: this.individual) {
        if((item.getTotal()> 0D) && (item.getTotal()> item.getDiferencia())) {
          params.put("tipo", "Lote");
          params.put("codigo", item.getCodigo());
          params.put("nombre", item.getNombre());
          params.put("cantidad", Numero.redondearSat(item.getTotal()- item.getDiferencia()));
          regresar.append("{").append(Cadena.replaceParams(ERROR_ARTICULO_EXCEDE, params)).append("},");
        } // if  
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 1): "";
  }
  
  public String toCheckPartidas(List<Articulo> articulos) {
    StringBuilder regresar    = new StringBuilder();
    Double sum                = null; 
    Boolean find              = null;
    Map<String, Object> params= new HashMap<>();
    try {
      for (Articulo articulo: articulos) {
        sum = 0D;
        find= Boolean.FALSE;
        for (Individual item: this.individual) {
          if(Objects.equals(articulo.getIdArticulo(), item.getIdArticulo())) {
            sum+= item.getTotal();
            find= Boolean.TRUE;
          } // if  
        } // for
        if(!Objects.equals(articulo.getIdArticulo(), -1L) && Numero.toRedondearSat(sum)!= articulo.getCantidad() && find) {
          params.put("tipo", "Partida");
          params.put("codigo", articulo.getPropio());
          params.put("nombre", articulo.getNombre());
          params.put("cantidad", articulo.getCantidad());
          params.put("diferencia", Numero.redondearSat(sum));
          regresar.append("{").append(Cadena.replaceParams(ERROR_ARTICULO_DIFIERE, params)).append("},");
        } // if  
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 1): "";
  }
  
  public String toCheckTodos(List<Articulo> articulos) {
    StringBuilder regresar    = new StringBuilder();
    Map<String, Object> params= new HashMap<>();
    try {
      if(!Objects.equals(this.getIdContrato(), -1L)) 
        for (Articulo articulo: articulos) {
          int index= this.general.indexOf(new General(this.getIdOrdenCompra(), articulo.getIdArticulo()));
          if(!Objects.equals(articulo.getIdArticulo(), -1L) && (index< 0)) {
            params.put("tipo", "No existe");
            params.put("codigo", articulo.getPropio());
            params.put("nombre", articulo.getNombre());
            params.put("cantidad", articulo.getCantidad());
            regresar.append("{").append(Cadena.replaceParams(ERROR_ARTICULO_TODOS, params)).append("},");
          } // if
        } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 1): "";
  }
  
  public void toEraseArticulos(Articulo articulo) {
    int index= 0;
    try {  
      while (index< this.general.size()) {
        if(Objects.equals(articulo.getIdArticulo(), this.general.get(index).getIdArticulo())) 
          this.general.remove(index);
        else
          index++;
      } // while
      index= 0;
      while (index< this.individual.size()) {
        if(Objects.equals(articulo.getIdArticulo(), this.individual.get(index).getIdArticulo())) 
          this.individual.remove(index);
        else
          index++;
      } // while
      this.toDelete(articulo);
    } // try
    catch (Exception e) {
      Error.mensaje(e);   
    } // catch	
  }

  private void toValuesGeneral() {
    Double sum= null;
    for (General item: this.general) {
      sum= 0D;
      for (Individual value: this.temporal) {
        if(Objects.equals(item.getIdArticulo(), value.getIdArticulo())) 
          sum+= value.getCantidad();
      } // for
      item.setIdOrdenCompra(this.getIdOrdenCompra());
      item.setTotal(Numero.toRedondearSat(sum));
    } // for
  }  

  private void toValuesIndividual() {
    for (Individual item: this.individual) {
      int index= this.temporal.indexOf(new Individual(this.getIdOrdenCompra(), item.getIdContratoLote(), item.getIdArticulo()));
      if(index>= 0) {
        item.setIdOrdenMaterial(this.temporal.get(index).getIdOrdenMaterial());
        item.setIdOrdenCompra(this.getIdOrdenCompra());
        item.setTotal(this.temporal.get(index).getCantidad());
        item.setComprados(Numero.toRedondearSat(item.getComprados()- this.temporal.get(index).getCantidad()));
        item.setDiferencia(Numero.toRedondearSat(item.getCantidad()- item.getComprados()));
      } // if        
//      for (Individual value: this.temporal) {
//        if(item.equals(value)) {
//          item.setIdOrdenMaterial(value.getIdOrdenMaterial());
//          item.setIdOrdenCompra(this.getIdOrdenCompra());
//          item.setTotal(value.getCantidad());
//          item.setComprados(Numero.toRedondearSat(item.getComprados()- value.getCantidad()));
//          item.setDiferencia(Numero.toRedondearSat(item.getCantidad()- item.getComprados()));
//          break;
//        } // if  
//      } // for
    } // for
  }  
  
  public Boolean isChangeEmpresa() {
    return !Objects.equals(this.itEmpresa, this.getIdEmpresa()) && !Objects.equals(this.itEmpresa, -1L) && !Objects.equals(this.getIdEmpresa(), -1L);
  }

  public List<Detalle> getDetalles() {
    return detalles;
  }

  public void setDetalles(List<Detalle> detalles) {
    this.detalles = detalles;
  }

  private void toDelete(Articulo articulo) throws Exception {
    try { 
      int index= 0;
      while(index< this.getDetalles().size()) {
        Detalle item= this.getDetalles().get(index);
        if(Objects.equals(articulo.getIdArticulo(), item.getIdArticulo())) 
          if(Objects.equals(item.getSql(), ESql.INSERT))
            this.getDetalles().remove(index);
          else {
            item.setSql(ESql.DELETE);
            item.setIdOrdenDetalle(null);
            item.setIdUsuario(JsfBase.getIdUsuario());
            item.setObservaciones("SE ELIMINO DE FORMA AUTOMATICA");
            index++;
          } // else
        else
          index++;        
      } // for
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }

}
