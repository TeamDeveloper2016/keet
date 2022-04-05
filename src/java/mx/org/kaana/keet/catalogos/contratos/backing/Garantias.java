package mx.org.kaana.keet.catalogos.contratos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.beans.Garantia;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosGarantias")
@ViewScoped
public class Garantias extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private TcKeetContratosDto contrato;
	private Garantia garantia;
	private List<Garantia> garantias;

  public TcKeetContratosDto getContrato() {
    return contrato;
  }

  public Garantia getGarantia() {
    return garantia;
  }

  public void setGarantia(Garantia garantia) {
    this.garantia = garantia;
  }

  public List<Garantia> getGarantias() {
    return garantias;
  }

  public Integer getSize() {
    return this.garantias.size();
  }

  public void setSize(Integer size) {
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Keet/Catalogos/Contratos/filtro": JsfBase.getFlashAttribute("retorno"));
      if(JsfBase.getFlashAttribute("idContrato")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));
      this.attrs.put("idCliente", JsfBase.getFlashAttribute("idCliente"));
      this.attrs.put("idEstimacion", JsfBase.getFlashAttribute("idEstimacion"));
      this.contrato= (TcKeetContratosDto)DaoFactory.getInstance().findById(TcKeetContratosDto.class, (Long)this.attrs.get("idContrato"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {      
      this.garantia= new Garantia(this.contrato.getIdContrato());
      params.put("idContrato", this.contrato.getIdContrato());      
      this.garantias= (List<Garantia>)DaoFactory.getInstance().toEntitySet(Garantia.class, "TcKeetContratosGarantiasDto", "contrato", params);
      if(this.garantias!= null) {
        for (Garantia item: this.garantias) 
          item.setSql(ESql.SELECT);
      } // if    
      else
        this.garantias= new ArrayList<>();
      this.toCalculate();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
      if(!Objects.equals(this.garantia.getSql(), ESql.INSERT))
        this.doInsert(Boolean.FALSE);
  	  transaccion= new Transaccion(this.garantias);
			if (transaccion.ejecutar(EAccion.GENERAR)) {
				regresar= this.doCancelar();
				JsfBase.addMessage("Se registraron la(s) garantía(s) del contrato", ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar las garantía(s)", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idContratoProcess", this.attrs.get("idContrato"));
  	JsfBase.setFlashAttribute("idClienteProcess", this.attrs.get("idCliente"));
  	JsfBase.setFlashAttribute("ikContrato", this.attrs.get("idContrato"));
  	JsfBase.setFlashAttribute("idEstimacion", this.attrs.get("idEstimacion"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar	
	
  public String toColor(Lote row) {
    return row.isMostar()? "": "janal-display-none";
  }
 
  private void toCalculate() {
    double sum= 0D;
    for (Garantia item: this.garantias) 
      if(!Objects.equals(item.getSql(), ESql.DELETE))
        sum+= item.getImporte();
    this.attrs.put("total", Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(sum)));
  }
 
  public void doInsert() {
    this.doInsert(Boolean.TRUE);
  }
  
  public void doInsert(Boolean message) {
    int index= this.garantias.indexOf(this.garantia);
    if(index< 0) {
      this.garantias.add(this.garantia);
      this.garantia= new Garantia(this.contrato.getIdContrato()); 
    } // if
    else 
      if(Objects.equals(this.garantias.get(index).getKey(), this.garantia.getKey())) {
        this.garantias.add(this.garantia);
        this.garantia= new Garantia(this.contrato.getIdContrato()); 
      } // else
      else 
        if(message)
          JsfBase.addMessage("No se puede agregar está garantía, ya existe", ETipoMensaje.ERROR);      			
    this.toCalculate(); 
  }
  
  public void doEdit(Garantia row) {
    this.garantia= row; 
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
    this.garantias.remove(row);
    this.toCalculate(); 
  }
  
  public void doRecover(Garantia row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
    this.toCalculate(); 
  }
  
  public void doDelete(Garantia row) {
    if(row.getKey()< 0L)
      this.garantias.remove(row);
    else  
      row.setSql(ESql.DELETE);
    this.toCalculate(); 
  }
  
}