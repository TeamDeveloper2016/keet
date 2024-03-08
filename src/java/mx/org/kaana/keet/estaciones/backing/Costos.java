package mx.org.kaana.keet.estaciones.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.prototipos.reglas.Transaccion;
import mx.org.kaana.keet.estaciones.beans.Partida;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetEstacionesCostos")
@ViewScoped
public class Costos extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = -7098585471715712054L;

	private EAccion accion;
  private Entity contrato;
  private List<Partida> estaciones;

	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("ikEmpresa", JsfBase.getFlashAttribute("ikEmpresa"));
      this.attrs.put("ikDesarrollo", JsfBase.getFlashAttribute("ikDesarrollo"));
      this.attrs.put("ikContrato", JsfBase.getFlashAttribute("ikContrato"));
      this.attrs.put("ikLote", JsfBase.getFlashAttribute("ikLote"));
      this.attrs.put("seleccionado", JsfBase.getFlashAttribute("seleccionado"));
      
      this.accion= (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      this.attrs.put("idEstacion", JsfBase.getFlashAttribute("idEstacion"));
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));
      this.attrs.put("idContratoLote", JsfBase.getFlashAttribute("idContratoLote"));
      this.attrs.put("siguiente", JsfBase.getFlashAttribute("siguiente"));
      this.attrs.put("codigo", JsfBase.getFlashAttribute("codigo"));
      this.attrs.put("xcode", JsfBase.getFlashAttribute("codigo"));
      this.attrs.put("estacionProcess", JsfBase.getFlashAttribute("estacionProcess"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.toLoadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public Entity getContrato() {
    return contrato;
  }

  public List<Partida> getEstaciones() {
    return estaciones;
  }

	private void toLoadCatalogos()  {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idContratoLote", this.attrs.get("idContratoLote"));      
			this.contrato= (Entity)DaoFactory.getInstance().toEntity("VistaEstacionesDto", "lote", params);
      if(this.contrato!= null && !this.contrato.isEmpty()) {
        columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
        columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
        UIBackingUtilities.toFormatEntity(this.contrato, columns);
      } // if  
      this.toLoadEstaciones();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
	} // toLoadCatalogos
	
	private void toLoadEstaciones()  {
    StringBuilder sb          = new StringBuilder();
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idPrototipo", this.contrato.toLong("idPrototipo"));      
      params.put("idUsuario", JsfBase.getIdUsuario());      
      sb.append(Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)).
      append(Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)).
      append(Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true));
      params.put("clave", sb.toString());      
      params.put("codigo", this.attrs.get("codigo"));      
			this.estaciones= (List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "VistaEstacionesDto", "costos", params, Constantes.SQL_TODOS_REGISTROS);
	    if(this.estaciones!= null && !this.estaciones.isEmpty()) {
        int count= 0;
        while(count< this.estaciones.size()) {
          Partida item= this.estaciones.get(count);
          if(item.isCostoEditable()) {
            item.setAccion(ESql.SELECT);
            item.setOrden(new Long(count));
            count++;
          } //   
          else
            this.estaciones.remove(count);
        } // for
      } // if  
      this.toLoadConceptos();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
	} 
  
  public String doAceptar() {  
    String regresar       = null;
    Transaccion transacion= null;
    try {			
      if(!this.estaciones.isEmpty()) {
        if(this.checkCostos()) {
          transacion= new Transaccion((Long)this.attrs.get("idContratoLote"), this.estaciones);
          if(transacion.ejecutar(EAccion.DESTRANSFORMACION)) {
            JsfBase.setFlashAttribute("estacionProcess", this.attrs.get("estacionProcess"));
            regresar= this.doCancelar();				
            JsfBase.addMessage("Se actualizaron los costos de forma correcta", ETipoMensaje.ALERTA);
          } // if
          else  
            JsfBase.addMessage("Ocurrió un error al registrar los costos", ETipoMensaje.ERROR);      			
        } // if
        else
          JsfBase.addMessage("El costo tiene que ser mayor a cero y no puede haber valores negativos", ETipoMensaje.ERROR);      			
      } // if
      else
        JsfBase.addMessage("No se tienen estaciones que actualizar", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    JsfBase.setFlashAttribute("idEmpresa", this.attrs.get("ikEmpresa"));
    JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("ikDesarrollo"));
    JsfBase.setFlashAttribute("idContrato", this.attrs.get("ikContrato"));
    JsfBase.setFlashAttribute("idLote", this.attrs.get("ikLote"));      
    JsfBase.setFlashAttribute("codigo", this.attrs.get("xcode"));      
    JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionado"));      
		JsfBase.setFlashAttribute("estacionProcess", this.attrs.get("estacionProcess"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar	

	public String toColor(Partida row) {
		return row.isCostoEditable()? "": "janal-display-none";
	} // toColor
  
  private Boolean checkCostos() {
    Boolean regresar= Boolean.TRUE;
    for (Partida item: this.estaciones) {
      if(item.getCosto()<= 0D || item.getAnticipo()< 0D) {
        regresar= Boolean.FALSE;
        break;
      } // if  
    } // for
    return regresar;
  }
 
  public void doReplicarCosto(Partida row) {
    for (Partida item : this.estaciones) {
      if(item.isCostoEditable() && !Objects.equals(item.getIdEstacion(), row.getIdEstacion()) && item.getOrden()> row.getOrden())      
        item.setCosto(row.getCosto()); 
    } // for
  }
  
  public void doReplicarAnticipo(Partida row) {
    for (Partida item : this.estaciones) {
      if(item.isCostoEditable() && !Objects.equals(item.getIdEstacion(), row.getIdEstacion()) && item.getOrden()> row.getOrden())
        item.setAnticipo(row.getAnticipo()); 
    } // for
  }
 
  private void toLookRubro() {
		List<UISelectEntity> rubros= null;
		try {
			rubros= (List<UISelectEntity>)this.attrs.get("rubros");
      if(rubros!= null && !rubros.isEmpty()) {
        int index= rubros.indexOf((UISelectEntity)this.attrs.get("idRubro"));
        if(index>= 0)
			    this.attrs.put("idRubro", rubros.get(index));
      } // if  
 		} // try
		catch (Exception e) {			
      Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
  }
  
	private void toLoadConceptos() {
		List<UISelectEntity> rubros= null;
		Map<String, Object>params  = new HashMap<>();
    Estaciones conceptos       = new Estaciones();
    StringBuilder sb           = new StringBuilder();
		try {
      if(this.estaciones!= null && !this.estaciones.isEmpty()) {
        String clave= conceptos.toKey(this.estaciones.get(0).getClave(), 3);
        params.put("clave", clave);
        params.put("nivel", 6L);
        List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetEstacionesDto", "distintos", params);
        if(items!= null && !items.isEmpty())
          for (Entity item: items) {
            sb.append("'").append(item.toString("codigo")).append("', ");  
          } // for
        if(sb.length()> 1)
          sb.delete(sb.length()- 2, sb.length());
        else
          sb.append("WXYZ");
      } // if
			params.put("codigo", this.attrs.get("codigo"));
			params.put("codigos", sb.toString());
			rubros= UIEntity.seleccione("TcKeetRubrosDto", "rubros", params, "nombre");
      if(rubros!= null && !rubros.isEmpty()) {
        this.attrs.put("rubros", rubros);
        int index= 0;
        while(index< rubros.size()  && !Objects.equals(rubros.get(index).toString("codigo"), (String)this.attrs.get("codigo"))) {
          index++;
        } // while
        if(index>= 0 && index< rubros.size())
          this.attrs.put("idRubro", rubros.get(index));
        else
          this.attrs.put("idRubro", UIBackingUtilities.toFirstKeySelectEntity(rubros));
      } // if
      else
        this.attrs.put("idRubro", UIBackingUtilities.toFirstKeySelectEntity(rubros));
 		} // try
		catch (Exception e) {			
      Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
      conceptos= null;
		} // finally
	} 
  
  public void doActualiza() {
    UISelectEntity rubro= null;
    try {      
      this.toLookRubro();
      rubro= (UISelectEntity)this.attrs.get("idRubro");     
      if(rubro.containsKey("codigo")) {
        this.attrs.put("codigo", rubro.toString("codigo"));
        this.toLoadEstaciones();
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}