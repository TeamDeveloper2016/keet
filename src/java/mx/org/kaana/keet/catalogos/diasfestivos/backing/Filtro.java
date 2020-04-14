package mx.org.kaana.keet.catalogos.diasfestivos.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.diasfestivos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetDiasFestivosDto;

@Named(value= "keetCatalogosDiasFestivosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 6445195086151257263L;  
	private LocalDate fechaInicio;
	private LocalDate fechaFin;

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}	
	
  @PostConstruct
  @Override
  protected void init() {
    try {    	      			
			this.fechaInicio= LocalDate.of(Fecha.getAnioActual(), 1, 1);
			this.fechaFin= LocalDate.of(Fecha.getAnioActual()+1, 12, 31);
			this.toLoadCatalog();
			if(JsfBase.getFlashAttribute("idDia")!= null){
				this.attrs.put("idDia", JsfBase.getFlashAttribute("idDia"));
				doLoad();
				this.attrs.put("idDia", null);
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;		
    try {
			columns= new ArrayList<>();			
			params = new HashMap<>();
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", new UISelectEntity("-1"));      			
    } // try
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // toLoadCatalog
	
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= this.toPrepare();
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("dia", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      params.put("sortOrder", "order by dia");
      this.lazyModel = new FormatCustomLazy("VistaDiasFeriadosDto", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad
		
	private Map<String, Object> toPrepare() {
		Map<String, Object> regresar= new HashMap<>();
		StringBuilder sb= new StringBuilder("");			
		if(this.attrs.get("idDia")!= null && !Cadena.isVacio(this.attrs.get("idDia")))
			sb.append("id_dia_festivo=").append(this.attrs.get("idDia")).append(" and ");					
		sb.append("(date_format(tc_keet_dias_festivos.dia, '%Y%m%d')>= date_format('").append(this.fechaInicio.toString()).append("', '%Y%m%d')) and ");					
		sb.append("(date_format(tc_keet_dias_festivos.dia, '%Y%m%d')<= date_format('").append(this.fechaFin.toString()).append("', '%Y%m%d')) and ");			
		if(Cadena.isVacio(sb.toString()))
			regresar.put("condicion", Constantes.SQL_VERDADERO);
		else
			regresar.put("condicion", sb.substring(0, sb.length()- 4));			
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && this.attrs.get("idEmpresa").toString().equals("-1"))
			regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getDependencias());
		else
			regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
		return regresar;
	} // toCondicion
	
	public String doAccion(String accion){
		String regresar= null;
		EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);
			JsfBase.setFlashAttribute("idDia", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity)this.attrs.get("seleccionado")).getKey());
			regresar= "accion".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		return regresar;
	} // diAccion
	
	public void doEliminar(){
		Transaccion transaccion  = null;		
		TcKeetDiasFestivosDto dto= null;
		try {									
			dto= new TcKeetDiasFestivosDto();			
			dto.setIdDiaFestivo(((Entity)this.attrs.get("seleccionado")).getKey());
			transaccion= new Transaccion(dto);
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar día festivo.", "Se eliminó de forma correcta el día festivo.", ETipoMensaje.INFORMACION);							
			else
				JsfBase.addMessage("Eliminar día festivo.", "Ocurrio un error al eliminar el día festivo.", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				
	} // doAceptar
	
	public void doProcesar(){
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(new TcKeetDiasFestivosDto());
			if(transaccion.ejecutar(EAccion.PROCESAR))
				JsfBase.addMessage("Aperturar ejercicio.", "Se aperturo el ejercicio de forma correcta.", ETipoMensaje.INFORMACION);							
			else
				JsfBase.addMessage("Aperturar ejercicio.", "Ocurrio un error al aperturar el ejercicio.", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doProcesar
}