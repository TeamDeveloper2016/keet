package mx.org.kaana.keet.estaciones.backing;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.formato.Cadena;
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
      this.accion= (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      this.attrs.put("idEstacion", JsfBase.getFlashAttribute("idEstacion"));
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));
      this.attrs.put("idContratoLote", JsfBase.getFlashAttribute("idContratoLote"));
      this.attrs.put("siguiente", JsfBase.getFlashAttribute("siguiente"));
      this.attrs.put("codigo", JsfBase.getFlashAttribute("codigo"));
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
    List<Columna> columns     = null;    
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put("idContratoLote", this.attrs.get("idContratoLote"));      
			this.contrato= (Entity)DaoFactory.getInstance().toEntity("VistaEstacionesDto", "lote", params);
      if(this.contrato!= null && !this.contrato.isEmpty()) {
        columns= new ArrayList<>();		
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
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put("idPrototipo", this.contrato.toLong("idPrototipo"));      
      params.put("idUsuario", JsfBase.getIdUsuario());      
      StringBuilder sb= new StringBuilder();
      sb.append(Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)).
      append(Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)).
      append(Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true));
      params.put("clave", sb.toString());      
      params.put("codigo", this.attrs.get("codigo"));      
			this.estaciones= (List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "VistaEstacionesDto", "costos", params, Constantes.SQL_TODOS_REGISTROS);
	    if(this.estaciones!= null && !this.estaciones.isEmpty()) {
        for (Partida item: this.estaciones) {
          item.setAccion(ESql.SELECT);
        } // for
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadEstaciones
  
  public String doAceptar() {  
    String regresar       = null;
    Transaccion transacion= null;
    try {			
      if(!this.estaciones.isEmpty()) {
        if(this.checkCostos()) {
          transacion= new Transaccion((Long)this.attrs.get("idContratoLote"), this.estaciones);
          if(transacion.ejecutar(EAccion.DESTRANSFORMACION)) {
            JsfBase.setFlashAttribute("estacionProcess", this.attrs.get("estacionProcess"));
            regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);				
            JsfBase.addMessage("Se actualizaron las costos de forma correcta", ETipoMensaje.ALERTA);
          } // if
          else  
            JsfBase.addMessage("Ocurrió un error al registrar las costos", ETipoMensaje.ERROR);      			
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
      if(!Objects.equals(item.getIdEstacion(), row.getIdEstacion()))      
        item.setCosto(row.getCosto()); 
    } // for
  }
  
  public void doReplicarAnticipo(Partida row) {
    for (Partida item : this.estaciones) {
      if(!Objects.equals(item.getIdEstacion(), row.getIdEstacion()))      
        item.setAnticipo(row.getAnticipo()); 
    } // for
  }
  
}