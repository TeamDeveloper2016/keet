package mx.org.kaana.mantic.incidentes.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.incidentes.reglas.Transaccion;
import mx.org.kaana.mantic.incidentes.beans.Repercusion;
import java.time.LocalDate;

@Named(value = "manticIncidentesIncidencia")
@ViewScoped
public class Incidencia extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
  private Repercusion repercusion;
	private List<Repercusion> incidentes;

  public Repercusion getRepercusion() {
    return repercusion;
  }

  public void setRepercusion(Repercusion repercusion) {
    this.repercusion = repercusion;
  }

  public List<Repercusion> getIncidentes() {
    return incidentes;
  }

  public void setIncidentes(List<Repercusion> incidentes) {
    this.incidentes = incidentes;
  }

  @PostConstruct
  @Override
  protected void init() {
    try {    	
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Mantic/Incidentes/filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("automatico", Boolean.TRUE);
      this.toDefaultPersona();
      this.incidentes= new ArrayList<>();
			this.toLoadTiposIncidentes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void toLoadTiposIncidentes() {
		List<UISelectItem> tipos = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("grupo", 1L);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tipos= UISelect.build("TcManticTiposIncidentesDto", "byGrupo", params, "nombre", " ", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("tipos", tipos);
      if(tipos!= null && tipos.size()> 0) 
        this.toChangeTipo(Boolean.TRUE);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toLoadTiposInicidentes
  
	public List<UISelectEntity> doCompleteNombreEmpleado(String query) {
		this.attrs.put("nombreEmpleado", query);
    this.doUpdateNombresEmpleados();		
		return (List<UISelectEntity>)this.attrs.get("nombres");
	}	// doCompleteNombreEmpleado
	
	public void doUpdateNombresEmpleados() {
		List<Columna> columns        = null;
    Map<String, Object> params   = new HashMap<>();
		List<UISelectEntity> personas= null;		
    try {
			columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));			
			String nombreEmpleado= (String)this.attrs.get("nombreEmpleado"); 
			nombreEmpleado= !Cadena.isVacio(nombreEmpleado)? nombreEmpleado.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";		
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("idEmpresa", this.attrs.get("idEmpresa"));
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			params.put("nombreEmpleado", nombreEmpleado);	
      personas= (List<UISelectEntity>) UIEntity.build("VistaPersonasDto", "autoCompletar", params, columns, 20L);
      this.attrs.put("nombres", personas);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}	// doUpdateNombresEmpleados  
	
	public String doAceptar(){
		String regresar        = null;
		Transaccion transaccion= null;
		try {
			if(this.incidentes.size()> 0) {
				transaccion= new Transaccion(this.incidentes);				
				if(transaccion.ejecutar(EAccion.REPROCESAR)) {
					regresar= this.doCancelar();
				} // if
				else
					JsfBase.addMessage("Ocurri� un error al registrar las incidencias", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Se tiene que agregar una incidencia", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		return regresar;
	} // doAceptar
	
	public String doCancelar() {
		return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
	} // doCancelar

  private void toDefaultPersona() {
    try {
      if(this.repercusion== null) {
        this.repercusion= new Repercusion(-1L);
        this.toChangeTipo(Boolean.TRUE);
      } // if  
      else
        this.repercusion= this.repercusion.clone();
      UISelectEntity persona= new UISelectEntity(-1L);
      persona.put("idEmpresaPesona", new Value("idEmpresaPersona", -1L));
      this.attrs.put("nombre", persona);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
  }
  
  public void doAdd() {
    try {
      if(!Objects.equals(this.repercusion.getNombre(), null) && this.repercusion.getNombre().length()> 0) {
        int index= this.incidentes.indexOf(this.repercusion);
        if(index< 0) {
          // AQUI FALTA VALIDAR SI TIENE UNA INCIDENCIA YA REGISTRADA DE ALGUN TIPO PARA ESTA PERSONA
          if(this.toCheckLocal() && this.toCheckIncidencia())
            this.incidentes.add(this.repercusion);   
        } // if  
        else 
          JsfBase.addMessage("El empleado ya tiene una incidencia del mismo tipo en el mismo d�a", ETipoMensaje.ERROR);
        this.toDefaultPersona();
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doDelete(Repercusion row) {
    this.incidentes.remove(row);
  }
  
  public void doChangeInicio() {
    if(this.repercusion.getInicio()!= null)
      if(this.repercusion.getInicio().isAfter(this.repercusion.getTermino()))
        this.repercusion.setTermino(LocalDate.ofEpochDay(this.repercusion.getInicio().toEpochDay()));
      else
        if(Objects.equals(this.repercusion.getIdTipoIncidente(), 1L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 7L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 8L) || Objects.equals(this.repercusion.getIdTipoIncidente(), 9L)) 
          this.repercusion.setTermino(LocalDate.ofEpochDay(this.repercusion.getInicio().toEpochDay()));
  }
  
  public void doChangeTermino() {
    if(this.repercusion.getTermino()!= null) {
      if(this.repercusion.getTermino().isBefore(this.repercusion.getInicio()))
        this.repercusion.setInicio(LocalDate.ofEpochDay(this.repercusion.getTermino().toEpochDay()));
    } // if
  }
 
  public void doLoadNombres() {
    UISelectEntity persona= (UISelectEntity)this.attrs.get("nombre");  
    if(persona!= null) {
      List<UISelectEntity> personas= (List<UISelectEntity>)this.attrs.get("nombres");
      if(personas!= null && !persona.isEmpty()) {
        int index= personas.indexOf(persona);
        if(index>= 0) {
          persona= personas.get(index);
          this.repercusion.setIdEmpresaPersona(persona.toLong("idEmpresaPersona"));
          this.repercusion.setRfc(persona.toString("rfc"));
          this.repercusion.setNombre(persona.toString("nombreCompleto"));
          this.repercusion.setPuesto(persona.toString("puesto"));
          this.repercusion.setContacto(persona.toString("contacto"));
          if((Boolean)this.attrs.get("automatico"))
            this.doAdd();
        } // if  
      } // if
    } // if
  }
 
  public void doChangeTipo() {
    this.toChangeTipo(Boolean.FALSE);
  }
  
  private void toChangeTipo(Boolean recover) {
		List<UISelectItem> tipos= (List<UISelectItem>)this.attrs.get("tipos");
    if(tipos!= null && !tipos.isEmpty()) {
      if(recover)
        this.repercusion.setIdTipoIncidente((Long)UIBackingUtilities.toFirstKeySelectItem(tipos));
      int index= tipos.indexOf(new UISelectItem(this.repercusion.getIdTipoIncidente()));
      if(index>= 0) {
        UISelectItem tipo= tipos.get(index);
        this.repercusion.setIncidencia(tipo.getLabel());
      } // if  
    } // if  
  }
 
  private Boolean toCheckIncidencia() {
    Boolean regresar          = Boolean.FALSE;
    List<Columna> columns     = null;    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresaPersona", this.repercusion.getIdEmpresaPersona());      
      params.put("idTipoInidencia", this.repercusion.getIdTipoIncidente());      
      params.put("inicio", Global.format(EFormatoDinamicos.FECHA_ESTANDAR, this.repercusion.getInicio()));
      params.put("termino", Global.format(EFormatoDinamicos.FECHA_ESTANDAR, this.repercusion.getTermino()));
      columns = new ArrayList<>();
      columns.add(new Columna("incidencia", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaIncidenciasDto", "incidencia", params);
      if(entity!= null) {
        UIBackingUtilities.toFormatEntity(entity, columns);
			  JsfBase.addMessage("El empleado ya tiene una incidencia ".concat(entity.toString("incidencia")).concat(" registrada del ").concat(entity.toString("inicio")).concat(" al ").concat(entity.toString("termino")), ETipoMensaje.ERROR);
      } // if  
      else
        regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
    return regresar;
  }
  
  private Boolean toCheckLocal() {
    Boolean regresar= Boolean.TRUE;
    try {
      for (Repercusion item: this.incidentes) {
        if(
            Objects.equals(item.getIdEmpresaPersona(), this.repercusion.getIdEmpresaPersona()) &&
           (((item.getInicio().isEqual(this.repercusion.getInicio()) || item.getTermino().isEqual(this.repercusion.getTermino()))
            && item.getInicio().isBefore(this.repercusion.getInicio())
            && item.getTermino().isAfter(this.repercusion.getTermino())))
          ) {
    		  JsfBase.addMessage("El empleado ya tiene una incidencia ".concat(item.getIncidencia()).concat(" registrada del ").concat(item.getIniciox()).concat(" al ").concat(item.getTerminox()), ETipoMensaje.ERROR);
          regresar= Boolean.FALSE;
          break;
        } // if
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }  
  
}