package mx.org.kaana.mantic.facturas.backing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.operation.Delete;
import mx.org.kaana.kajool.db.comun.operation.IActions;
import mx.org.kaana.kajool.db.comun.operation.Insert;
import mx.org.kaana.kajool.db.comun.operation.Select;
import mx.org.kaana.kajool.db.comun.operation.Update;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.facturas.beans.Documento;
import mx.org.kaana.mantic.facturas.beans.FacturaFicticia;
import org.primefaces.event.SelectEvent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/02/2021
 *@time 09:03:36 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticFacturasComplemento")
@ViewScoped
public class Complemento extends Catalogos {

  private static final long serialVersionUID = -5851015967077131491L;
  
  private Documento documento;

  public Complemento() {
    super();
    this.idTipo= 2L;
  }

  public Documento getDocumento() {
    return documento;
  }

  public void setDocumento(Documento documento) {
    this.documento = documento;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    super.init();
    this.documento= new Documento(new Long((int)(Math.random()*-10000)));
  }  

	@Override
  public void doLoad() {
    Map<String, Object> params = null;
    try {
      super.doLoad();
      params = new HashMap<>();      
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      params.put("idArticuloTipo", 4L);
      params.put("codigo", "ACT");      
      switch (this.accion) {
        case AGREGAR:											
          Articulo pago= (Articulo)DaoFactory.getInstance().toEntity(Articulo.class, "VistaIngresosDto", "complemento", params);
          this.getAdminOrden().getArticulos().add(pago);
          break;
        case MODIFICAR:			
        case CONSULTAR:			
          break;
      } // switch		
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad
  
	@Override
	public void doAsignaCliente(SelectEvent event) {
		UISelectEntity seleccion     = null;
		List<UISelectEntity> clientes= null;
		try {
			clientes= (List<UISelectEntity>) this.attrs.get("clientes");
			seleccion= clientes.get(clientes.indexOf((UISelectEntity)event.getObject()));
			this.toFindCliente(seleccion);
			this.loadDomicilios(seleccion.getKey());
			this.toLoadFacturas(seleccion.getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
  }

  private void toLoadFacturas(Long idCliente) {
		List<UISelectEntity> facturas= null;
		Map<String, Object>params    = null;
		List<Columna> columns        = null;
		try {
			columns= new ArrayList<>();
			params = new HashMap<>();
			params.put("idCliente", idCliente);
      columns.add(new Columna("moneda", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("saldo", EFormatoDinamicos.MILES_SAT_DECIMALES));
			columns.add(new Columna("total", EFormatoDinamicos.MILES_SAT_DECIMALES));
			columns.add(new Columna("diferencia", EFormatoDinamicos.MILES_SAT_DECIMALES));
			facturas=(List<UISelectEntity>)UIEntity.build("VistaIngresosDto", "facturas", params, columns);
			this.attrs.put("facturas", facturas);
      if(facturas!= null && !facturas.isEmpty()) {
        this.documento.setIkFactura(facturas.get(0));
        this.doLoadFactura();
      } // if
      this.checkLoadFacturas();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // loadDocumentos


  public void doLoadFactura() {
    List<UISelectEntity> facturas= null;
    try {      
      facturas=(List<UISelectEntity>)this.attrs.get("facturas");
      int index= facturas.indexOf(this.documento.getIkFactura());
      if(index>= 0) {
        this.documento.setIkFactura(facturas.get(index));
        this.documento.setId(this.documento.getIkFactura().toString("id"));
        this.documento.setSerie(this.documento.getIkFactura().toString("serie"));
        this.documento.setFolio(this.documento.getIkFactura().toString("folio"));
        this.documento.setMetodoPago(this.documento.getIkFactura().toString("metodoPago"));
        this.documento.setMoneda(this.documento.getIkFactura().toString("moneda"));
        this.documento.setTipoDeCambio(this.documento.getIkFactura().toDouble("tipoDeCambio"));
        this.documento.setSaldo(this.documento.getIkFactura().toDouble("importe"));
        this.documento.setPagado(this.documento.getSaldo());
        this.documento.setInsoluto(0D);
        this.documento.setIdDetalle(this.documento.getIkFactura().toLong("idDetalle"));
        this.documento.setIdCliente(this.documento.getIkFactura().toLong("idCliente"));
        this.documento.setParcialidad(1L);
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  } 

  public void doCheckSaldoInsulto() {
    this.documento.setInsoluto(this.documento.getSaldo()- this.documento.getPagado());
  }  

  public void doCheckSaldoTotal() {
    Double pagado= ((FacturaFicticia)this.getAdminOrden().getOrden()).getTotal();
    Double saldo = ((FacturaFicticia)this.getAdminOrden().getOrden()).getSaldo();
    ((FacturaFicticia)this.getAdminOrden().getOrden()).setDiferencia(pagado- saldo);
  }  

  public void doAgregar() {
    IActions item = ((FacturaFicticia)this.getAdminOrden().getOrden()).isEqual(this.documento);
    Documento clon= this.documento;
    if(item== null) {
      ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().add(new Insert(this.documento));
        ((FacturaFicticia)this.getAdminOrden().getOrden()).toAdd(clon.getPagado());
    } // if
    else
      if(item instanceof Delete) {
        ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().remove(item);
        ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().add(new Update(this.documento));
        ((FacturaFicticia)this.getAdminOrden().getOrden()).toAdd(clon.getPagado());
      } // if
      else
        JsfBase.addMessage("La factura ya se encuentra registrada en el complemento de pago !", ETipoMensaje.INFORMACION);            
    this.documento= new Documento(new Long((int)(Math.random()*-10000)));
    List<UISelectEntity> facturas= (List<UISelectEntity>)this.attrs.get("facturas");
    if(facturas!= null && !facturas.isEmpty())
      this.documento.setIkFactura(facturas.get(0));
    this.doLoadFactura();
    UIBackingUtilities.execute("janal.restore();");
  } 
  
  public void doEliminar() {
    IActions clon= (IActions)this.attrs.get("documento");
    if(clon!= null) {
      int index= ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().indexOf(clon);
      if(index>= 0) {
        ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().remove(index);
        if(clon instanceof Select || clon instanceof Update) {
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().add(new Delete(clon.getDto()));
          this.documento= (Documento)clon.getDto();
          this.documento.setIkFactura(new UISelectEntity(this.documento.getIdDetalle()));
          this.doLoadFactura();
        } // if  
        ((FacturaFicticia)this.getAdminOrden().getOrden()).toRemove(((Documento)clon.getDto()).getPagado());
  			UIBackingUtilities.execute("janal.restore();");
      } // if  
    } // if    
  } 

  public String doColorRow(IActions row) {
    return row instanceof Delete? "janal-table-tr-hide": ""; 
  }

  @Override
  public void doUpdateDesarrollos() {
    super.doUpdateDesarrollos();
		this.toLoadFacturas(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente());
  }  
  
  @Override
  public void doUpdateContratos(AjaxBehaviorEvent event) {
    super.doUpdateContratos(event);
  	this.toLoadFacturas(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente());
  }  

  private void checkLoadFacturas() {
    Long idCliente= ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente();
    IActions item = null;
    int count     = 0;
    while(count< ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().size()) {
      item= ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().get(count);
      if(Objects.equals(idCliente, ((Documento)item.getDto()).getIdCliente())) {
        if(item instanceof Delete) 
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().set(count, new Update(item.getDto()));
        count++;
      } // if  
      else {
        if(item instanceof Insert)
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().remove(count);
        else {
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDocumentos().set(count, new Delete(item.getDto()));
          count++;
        } // if  
      } // else
    } // while
  } 
  
}
