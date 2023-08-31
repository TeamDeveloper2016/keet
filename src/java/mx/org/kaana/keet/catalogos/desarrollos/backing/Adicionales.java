package mx.org.kaana.keet.catalogos.desarrollos.backing;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import mx.org.kaana.keet.catalogos.desarrollos.beans.Etapa;
import mx.org.kaana.keet.catalogos.contratos.reglas.IDatoContrato;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetDesarrollosDto;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosDesarrollosAdicionales")
@ViewScoped
public class Adicionales extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private TcKeetDesarrollosDto desarrollo;
	private Etapa etapa;
	private List<IDatoContrato> etapas;

  public TcKeetDesarrollosDto getDesarrollo() {
    return desarrollo;
  }

  public Etapa getEtapa() {
    return etapa;
  }

  public void setEtapa(Etapa etapa) {
    this.etapa = etapa;
  }

  public List<IDatoContrato> getEtapas() {
    return this.etapas;
  }

  public Double getImporte() {
    return this.etapa.getMateriales()+ this.etapa.getDestajos()+ this.etapa.getSubcontratados()+ this.etapa.getPorElDia()+ this.etapa.getAdministrativos()+ this.etapa.getMaquinaria();
  }

  public void setImporte(Double importe) {
    
  }

  public Integer getSize() {
    return this.etapas.size();
  }

  public void setSize(Integer size) {
    
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Keet/Catalogos/Desarrollos/filtro": JsfBase.getFlashAttribute("retorno"));
//      if(JsfBase.getFlashAttribute("idContrato")== null)
//				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
//      this.attrs.put("idDesarrollo", JsfBase.getFlashAttribute("idContrato"));
      this.attrs.put("idDesarrollo", 15L);
      this.desarrollo= (TcKeetDesarrollosDto)DaoFactory.getInstance().findById(TcKeetDesarrollosDto.class, (Long)this.attrs.get("idDesarrollo"));
      this.toLoadEtapas();
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
      this.etapa= new Etapa(this.desarrollo.getIdDesarrollo());
      params.put("idDesarrollo", this.desarrollo.getIdDesarrollo());      
      this.etapas= (List<IDatoContrato>)DaoFactory.getInstance().toEntitySet(Etapa.class, "TcKeetDesarrollosEtapasDto", "desarrollo", params);
      if(this.etapas!= null) {
        for (IDatoContrato item: this.etapas) 
          item.setSql(ESql.SELECT);
      } // if    
      else
        this.etapas= new ArrayList<>();
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

  private void toLoadEtapas() {
    Map<String, Object> params = new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("etapas", UISelect.seleccione("TcKeetEtapasDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
      if(!Objects.equals(this.etapa.getSql(), ESql.INSERT))
        this.doInsert(Boolean.FALSE);
  	  transaccion= new Transaccion(this.etapas);
			if (transaccion.ejecutar(EAccion.CALCULAR)) {
				regresar= this.doCancelar();
				JsfBase.addMessage("Se registraron lo(s) extras la desarrollo", ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar los extras", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar	
	
  private void toCalculate() {
    double sum= 0D;
    for (IDatoContrato item: this.etapas) {
      Etapa dato= (Etapa)item;
      if(!Objects.equals(item.getSql(), ESql.DELETE)) 
        sum+= dato.getMateriales()+ dato.getDestajos()+ dato.getSubcontratados()+ dato.getPorElDia()+ dato.getAdministrativos()+ dato.getMaquinaria();
    } // for  
    this.attrs.put("total", Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(sum)));
  }
 
  public void doInsert() {
    this.doInsert(Boolean.TRUE);
  }
  
  public void doInsert(Boolean message) {
    this.etapa.setIdUsuario(JsfBase.getIdUsuario());
    int index= this.etapas.indexOf(this.etapa);
    this.toAddLegend();
    if(index< 0) {
      this.etapas.add(this.etapa);
      this.etapa= new Etapa(this.desarrollo.getIdDesarrollo()); 
    } // if
    else 
      if(Objects.equals(this.etapas.get(index).getKey(), this.etapa.getKey())) {
        this.etapas.add(this.etapa);
        this.etapa= new Etapa(this.desarrollo.getIdDesarrollo()); 
      } // else
      else 
        if(message)
          JsfBase.addMessage("No se puede agregar este extra, ya existe", ETipoMensaje.ERROR);      			
    this.toCalculate(); 
  }
  
  public void doEdit(Etapa row) {
    this.etapa= row; 
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
    this.etapas.remove(row);
    this.toCalculate(); 
  }
  
  public void doRecover(Etapa row) {
    if(row.getKey()> 0L)
      row.setSql(ESql.UPDATE);
    this.toCalculate(); 
  }
  
  public void doDelete(Etapa row) {
    if(row.getKey()< 0L)
      this.etapas.remove(row);
    else  
      row.setSql(ESql.DELETE);
    this.toCalculate(); 
  }

  private void toAddLegend() {
    List<UISelectItem> list= (List<UISelectItem>)this.attrs.get("etapas");
    if(list!= null && !list.isEmpty()) {
      int index= list.indexOf(new UISelectItem(this.etapa.getIdEtapa()));
      if(index>= 0)
        this.etapa.setEtapa(list.get(index).getLabel());
    } // if        
  }
  
  public String toFecha(LocalDateTime registro) {
    return Global.format(EFormatoDinamicos.FECHA_HORA_CORTA, registro);
  }
  
}