package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.TabChangeEvent;

@Named(value= "keetNominasAccion")
@ViewScoped
public class Accion extends IBaseFilter implements Serializable {

	private static final Log LOG= LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 318633488565639323L;

	public Boolean getActivar() {
		Long idTipoNomina   = ((UISelectEntity)this.attrs.get("idTipoNomina")).getKey();
		Long idNominaEstatus= ((Nomina)this.attrs.get("ultima")).getIdNominaEstatus();
		Long tuplas         = (Long)this.attrs.get("tuplas");
	  return idTipoNomina== -1L || tuplas== 0L || (idTipoNomina== 2L && idNominaEstatus!= 4L);
	}
	
	@PostConstruct
  @Override
  protected void init() {		
		this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
		this.attrs.put("idNomina", JsfBase.getFlashAttribute("idNomina")== null? -1L: JsfBase.getFlashAttribute("idNomina"));
		this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
		this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
		this.attrs.put("nomina", new Nomina());
		this.attrs.put("tuplas", 0L);
		this.loadCatalogs();
  } // init
  
	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Detalle")) 
			this.doLoad();
	} // doTabChange		
	
	public String doCancelar() {   
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
	public void doAceptar() {
		Transaccion transaccion= null;
    TcKeetNominasDto nomina= null;
		try {		
			Long idNomina= ((UISelectEntity)this.attrs.get("idNomina")).getKey();
			if(idNomina== null || idNomina== -1L) {
				nomina= new TcKeetNominasDto(
					0D, // Double neto, 
					1L, // Long idNominaEstatus, 
					0D, // Double deducciones, 
					2L, // Long idTipoNomina, 
					0L, // Long personas, 
					0D, // Double aportaciones, 
					-1L, // Long idNomina, 
					((Nomina)this.attrs.get("nomina")).getTermino().plusDays(-1), // LocalDate fechaPago, 
					0L, // Long proveedores, 
					0D, // Double total, 
					((Nomina)this.attrs.get("nomina")).getTermino().plusDays(-2), // LocalDate fechaDispersion, 
					((Nomina)this.attrs.get("nomina")).getIdNominaPeriodo(), // Long idNominaPeriodo, 
					0D, // Double iva, 
					JsfBase.getIdUsuario(), // Long idUsuario, 
					0D, // Double subtotal, 
					((Nomina)this.attrs.get("nomina")).getObservaciones(), // String observaciones, 
					JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
					0D, // Double percepciones
          ((Nomina)this.attrs.get("nomina")).getIdCompleta()// Long idCompleta     
				);
  			transaccion= new Transaccion(nomina, JsfBase.getAutentifica());
			} // if
      else {
   			nomina= (TcKeetNominasDto)DaoFactory.getInstance().findById(TcKeetNominasDto.class, idNomina);
        nomina.setIdCompleta(((Nomina)this.attrs.get("nomina")).getIdCompleta());
  			transaccion= new Transaccion(nomina, JsfBase.getAutentifica());
      } // else  
			if(transaccion.ejecutar(EAccion.AGREGAR))
				JsfBase.addMessage("Se procesó la nómina con éxito.", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Ocurrió un error en el proceso de nómina.", ETipoMensaje.ALERTA);	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} // doAceptar	
  
  public void doCompleto() {
		// JsfBase.addMessage("Detalle del mensaje", "Se proceso correctamente la nómina.", ETipoMensaje.INFORMACION);		
	} // doCompleto

	private void loadCatalogs() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			params= new HashMap<>();
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("tipos", UIEntity.seleccione("TcKeetTiposNominasDto", "row", params, "nombre"));
      this.attrs.put("idTipoNomina", new UISelectEntity(-1L));
			params.put("idTipoNomina", "1");
      this.attrs.put("ultima", DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "ultima", params));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	public void doLoadNominas() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			params= new HashMap<>();
		  params.put("idTipoNomina", this.attrs.get("idTipoNomina"));
		  params.put("ejercicio", Fecha.getAnioActual()- 1);
		  params.put("sucursales", this.attrs.get("sucursales"));
			List<UISelectEntity> nominas= UIEntity.build("VistaNominaDto", "activa", params);
			if(nominas!= null && !nominas.isEmpty())
				this.attrs.put("idNomina", nominas.get(0));
			else {
	      nominas= UIEntity.build("VistaNominaDto", "anterior", params);
				if(nominas!= null && !nominas.isEmpty()) 
          this.attrs.put("idNomina", nominas.get(0));
				else
			    this.attrs.put("idNomina", new UISelectEntity(-1L));
			} // if
      this.attrs.put("nominas", nominas);
			this.doLoadNomina();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	public void doLoadNomina() {
    Map<String, Object> params= new HashMap<>();
    try {
			params= new HashMap<>();
		  params.put("idNomina", ((UISelectEntity)this.attrs.get("idNomina")).getKey());
			params.put("sucursales", this.attrs.get("sucursales"));
			Nomina nomina= (Nomina)DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "nomina", params);
			if(nomina== null) 
			  nomina= (Nomina)DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "complemento", params);
			if(nomina== null)
				nomina= new Nomina();
      this.attrs.put("nomina", nomina);
			if(nomina.getIdTipoNomina()== 2L)
				params.put("idNomina", ((Nomina)this.attrs.get("ultima")).getIdNomina());
			Value value= DaoFactory.getInstance().toField("VistaNominaDto", nomina.getIdTipoNomina()== 1L? "ordinaria": "complementaria", params, "tuplas");
			if(value!= null && value.getData()!= null)
				this.attrs.put("tuplas", value.toLong());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
    } // finally
	}

	@Override
	public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
			params.put("sortOrder", "order by nomina, clave");
      columns= new ArrayList<>();
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaNominaDto", "detalle", params, columns);
      UIBackingUtilities.resetDataTable();
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
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		regresar.put("idNomina", ((Nomina)this.attrs.get("nomina")).getIdNomina());
		regresar.put("nombre", "");
		if(this.attrs.get("nombre")!= null && !Cadena.isVacio(this.attrs.get("nombre"))) {
			String nombre= ((String)this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
  		regresar.put("nombre", nombre);
		} // if
		return regresar;		
	} // toPrepare

  public void doAccion() {
		Transaccion transaccion= null;
		try {
			Entity row= (Entity)this.attrs.get("seleccionado");
			if(row.toLong("idTipoProceso")== 1L) {
				transaccion= new Transaccion(((Nomina)this.attrs.get("nomina")).getIdNomina(), row.toLong("idPersonaReprocesar"), JsfBase.getAutentifica());
			  if(transaccion.ejecutar(EAccion.REPROCESAR))
					JsfBase.addMessage("Se reprocesó con éxito la nómina de "+ row.toString("nombreCompleto"), ETipoMensaje.INFORMACION);
			  else
					JsfBase.addMessage("Ocurrio un error en el reproceso de "+ row.toString("nombreCompleto"), ETipoMensaje.ERROR);
			} // if
			else {
				transaccion= new Transaccion(((Nomina)this.attrs.get("nomina")).getIdNomina(), JsfBase.getAutentifica(), row.toLong("idPersonaReprocesar"));
			  if(transaccion.ejecutar(EAccion.DEPURAR))
					JsfBase.addMessage("Se reprocesó con éxito la nómina del subcontratista "+ row.toString("nombreCompleto"), ETipoMensaje.INFORMACION);
			  else
					JsfBase.addMessage("Ocurrio un error en el reproceso del subcontratista "+ row.toString("nombreCompleto"), ETipoMensaje.ERROR);
			} // if
			this.doLoadNomina();
			this.doLoad();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			transaccion= null;
		} // finally
	}	
	
	
	public String doConsultar(String accion) {
    String regresar= null;
		EAccion eaccion= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase())); 
			JsfBase.setFlashAttribute("idNomina", ((Entity)this.attrs.get("seleccionado")).toLong("idNomina"));
			JsfBase.setFlashAttribute("idPuesto", ((Entity)this.attrs.get("seleccionado")).toLong("idPuesto"));
			JsfBase.setFlashAttribute("idPersona", ((Entity)this.attrs.get("seleccionado")).toLong("idPersona"));
			JsfBase.setFlashAttribute("nomina", ((Entity)this.attrs.get("seleccionado")).toString("nomina"));
			JsfBase.setFlashAttribute("nombreCompleto", ((Entity)this.attrs.get("seleccionado")).toString("nombreCompleto"));
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Nomina/filtro");
			switch (eaccion) {
				case CONSULTAR: // personas
    			JsfBase.setFlashAttribute("idEmpresaPersona", ((Entity)this.attrs.get("seleccionado")).toLong("idPersonaReprocesar"));
				  regresar= "personas".concat(Constantes.REDIRECIONAR);
					break;
				case LISTAR: // proveedores
    			JsfBase.setFlashAttribute("idProveedor", ((Entity)this.attrs.get("seleccionado")).toLong("idPersonaReprocesar"));
				  regresar= "proveedores".concat(Constantes.REDIRECIONAR);
					break;
			} // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion
	
}
