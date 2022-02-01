package mx.org.kaana.keet.catalogos.prototipos.backing;

import java.io.Serializable;
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
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.prototipos.reglas.Transaccion;
import mx.org.kaana.keet.estaciones.beans.Partida;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetCatalogosPrototiposDias")
@ViewScoped
public class Dias extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = -7098585471715712054L;

	private EAccion accion;
  private List<Partida> partidas;

	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      this.attrs.put("idPrototipo", JsfBase.getFlashAttribute("idPrototipo"));
      this.attrs.put("prototipo", JsfBase.getFlashAttribute("prototipo"));
      this.attrs.put("cliente", JsfBase.getFlashAttribute("cliente"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.toLoadPartidas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public List<Partida> getPartidas() {
    return partidas;
  }

  public void setPartidas(List<Partida> partidas) {
    this.partidas = partidas;
  }

	private void toLoadPartidas() {
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put("idPrototipo", this.attrs.get("idPrototipo"));      
      params.put("idUsuario", JsfBase.getIdUsuario());      
			this.partidas= (List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "VistaEstacionesDto", "partidas", params, Constantes.SQL_TODOS_REGISTROS);
	    if(this.partidas!= null && !this.partidas.isEmpty()) {
        for (Partida item: this.partidas) {
          if(item.getIdPrototipoDia()== null) {
            item.setAccion(ESql.INSERT);
            item.setIdPrototipo((Long)this.attrs.get("idPrototipo"));
            item.setIdUsuario(JsfBase.getIdUsuario());
          } // if  
          else 
            item.setAccion(ESql.SELECT);
        } // for
      } // if  
      else {
  			this.partidas= (List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "TcKeetPartidasDto", "partidas", params, Constantes.SQL_TODOS_REGISTROS);
  	    if(this.partidas!= null && !this.partidas.isEmpty()) {
          for (Partida item: this.partidas) 
            item.setAccion(ESql.INSERT);
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
	} // toLoadPartidas
	
  public String doAceptar() {  
    String regresar       = null;
    Transaccion transacion= null;
    try {			
      if(!this.partidas.isEmpty()) {
        if(this.checkDias()) {
          transacion= new Transaccion(-1L, this.partidas);
          if(transacion.ejecutar(EAccion.MOVIMIENTOS)) {
            JsfBase.setFlashAttribute("idPrototipoProcess", this.attrs.get("idPrototipo"));
            regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);				
            JsfBase.addMessage("Se actualizaron las fechas de forma correcta", ETipoMensaje.ALERTA);
          } // if
          else  
            JsfBase.addMessage("Ocurrió un error al registrar las fechas", ETipoMensaje.ERROR);      			
        } // if
        else
          JsfBase.addMessage("Los días tienen que se mayores a cero", ETipoMensaje.ERROR);      			
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
		JsfBase.setFlashAttribute("idPrototipoProcess", this.attrs.get("idPrototipo"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar	

  private Boolean checkDias() {
    Boolean regresar= Boolean.TRUE;
    for (Partida item: this.partidas) {
      if(item.getDias()<= 0D) {
        regresar= Boolean.FALSE;
        break;
      } // if  
    } // for
    return regresar;
  }
  
  public void doReplicarDias(Partida row) {
    Boolean update= Boolean.FALSE;
    for (Partida item : this.partidas) {
      if(!update && Objects.equals(item.getCodigo(), row.getCodigo()))
        update= Boolean.TRUE;
      if(update)      
        item.setDias(row.getDias()); 
    } // for
  }  
  
}