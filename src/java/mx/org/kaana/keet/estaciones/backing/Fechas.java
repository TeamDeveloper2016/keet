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


@Named(value = "keetEstacionesFechas")
@ViewScoped
public class Fechas extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = -7098585471715712054L;

	private EAccion accion;
  private Entity contrato;
  private List<Partida> partidas;
  private List<Partida> estaciones;
  private LocalDate pivote;

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
      this.attrs.put("estacionProcess", JsfBase.getFlashAttribute("estacionProcess"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.pivote= LocalDate.now();
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

  public List<Partida> getPartidas() {
    return partidas;
  }

  public void setPartidas(List<Partida> partidas) {
    this.partidas = partidas;
  }

  public List<Partida> getEstaciones() {
    return estaciones;
  }

  public LocalDate getPivote() {
    return pivote;
  }

  public void setPivote(LocalDate pivote) {
    this.pivote = pivote;
  }

  public String getDayOfWeek() {
    return Partida.NAMES_OF_WEEKS[this.pivote.getDayOfWeek().ordinal()];
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
      this.toLoadPartidas();
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
	
	private void toLoadPartidas() {
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put("idPrototipo", this.contrato.toLong("idPrototipo"));      
      params.put("idUsuario", JsfBase.getIdUsuario());      
			this.partidas= (List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "VistaEstacionesDto", "partidas", params, Constantes.SQL_TODOS_REGISTROS);
	    if(this.partidas!= null && !this.partidas.isEmpty()) {
        for (Partida item: this.partidas) {
          if(item.getIdPrototipoDia()== null) {
            item.setAccion(ESql.INSERT);
            item.setIdPrototipo(this.contrato.toLong("idPrototipo"));
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
      this.toLoadEstaciones();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadPartidas
	
	private void toLoadEstaciones()  {
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put("idPrototipo", this.contrato.toLong("idPrototipo"));      
      params.put("idUsuario", JsfBase.getIdUsuario());      
      StringBuilder sb= new StringBuilder();
      sb.append(Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)).
      append(Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)).
      append(Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true)).
      append(Cadena.rellenar(this.attrs.get("siguiente").toString(), 3, '0', true));
      params.put("clave", sb.toString());      
			this.estaciones= (List<Partida>)DaoFactory.getInstance().toEntitySet(Partida.class, "VistaEstacionesDto", "estaciones", params, Constantes.SQL_TODOS_REGISTROS);
	    if(this.estaciones!= null && !this.estaciones.isEmpty()) {
        for (Partida item: this.estaciones) {
          int index= this.partidas.indexOf(item);
          if(index>= 0) { 
            this.partidas.get(index).setIdEstacion(item.getIdEstacion());
            this.partidas.get(index).setClave(item.getClave());
            this.partidas.get(index).setLote(item.getLote());
          } // if  
        } // for
      } // if  
      this.doProcessDates();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadEstaciones
  
  public void doProcessDates() {
    LocalDate inicio= LocalDate.from(this.pivote);
    try {
      int count    = 0;
      int offsetDay= 0;
      while(count< this.partidas.size()) {
        Partida item= this.partidas.get(count);
        if(item.getIdEstacion()== null || item.getIdEstacion()< 0)
          this.partidas.remove(count);
        else {
          DayOfWeek dayOfWeek= inicio.getDayOfWeek();
          if(dayOfWeek== DayOfWeek.SUNDAY)
            inicio= inicio.plusDays(1L);
          dayOfWeek= inicio.getDayOfWeek();
          offsetDay= 0;
          if(dayOfWeek.ordinal()+ item.getDias()> 6)
            offsetDay= (int)((dayOfWeek.ordinal()+ item.getDias())/ 6);
          item.setInicio(inicio);
          int dias= item.getDias().intValue();
          if(dias< 0) {
            dias= 1;
            item.setDias(new Long(dias));
          } // if  
          else
            dias= dias- 1;
          LocalDate termino= inicio.plusDays(dias+ offsetDay);
          dayOfWeek= termino.getDayOfWeek();
          if(dayOfWeek== DayOfWeek.SUNDAY)
            termino= termino.plusDays(1L);          
          item.setTermino(termino);
          inicio= termino.plusDays(1);
          count++;
        } // if  
      } // while
		} // try
		catch (Exception e) {
			Error.mensaje(e);
      JsfBase.addMessageError(e);
		} // catch		
  }
	
  public String doAceptar() {  
    String regresar       = null;
    Transaccion transacion= null;
    try {			
      if(!this.partidas.isEmpty()) {
        transacion= new Transaccion((Long)this.attrs.get("idContratoLote"), this.partidas);
        if(transacion.ejecutar(EAccion.TRANSFORMACION)) {
          JsfBase.setFlashAttribute("estacionProcess", this.attrs.get("estacionProcess"));
          regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);				
          JsfBase.addMessage("Se actualizaron las fechas de forma correcta", ETipoMensaje.ALERTA);
        } // if
        else  
          JsfBase.addMessage("Ocurrió un error al registrar las fechas", ETipoMensaje.ERROR);      			
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