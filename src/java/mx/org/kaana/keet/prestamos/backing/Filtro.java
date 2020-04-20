package mx.org.kaana.keet.prestamos.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosDto;
import mx.org.kaana.keet.prestamos.beans.RegistroPrestamo;
import mx.org.kaana.keet.prestamos.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetPrestamosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 6319984968937774153L;
	LocalDate inicio, termino;

	public LocalDate getInicio() {
		return inicio;
	}

	public void setInicio(LocalDate inicio) {
		this.inicio = inicio;
	}

	public LocalDate getTermino() {
		return termino;
	}

	public void setTermino(LocalDate termino) {
		this.termino = termino;
	}
	

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			cargaCatalogos();
			if(JsfBase.getFlashAttribute("idPrestamoProcess")!= null){
				this.attrs.put("idPrestamoProcess", JsfBase.getFlashAttribute("idPrestamoProcess"));
				this.doLoad();
				this.attrs.put("idPrestamoProcess", null);
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      columns= new ArrayList<>();
      columns.add(new Columna("deudor", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("saldoTotal", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("limite", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaPrestamosDto", params, columns);
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
  } // doLoad

	private void loadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params= new HashMap<>();
			columns= new ArrayList<>();			
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally{
			Methods.clean(params);
		}	// finally	
	} // loadEmpresas

  public String doAccion(String accion) {
    String regresar        = null;
		EAccion eaccion        = null;
		Transaccion transaccion= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idPrestamo", (!eaccion.equals(EAccion.AGREGAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Prestamos/filtro");
			switch (eaccion){
				case SUBIR:
				  regresar= "importar".concat(Constantes.REDIRECIONAR);	
				break;
				case CONSULTAR:
				case MODIFICAR:
				case AGREGAR:
				  regresar= "accion".concat(Constantes.REDIRECIONAR);
					break;
				case DESACTIVAR:
					transaccion= new Transaccion(new RegistroPrestamo(((Entity) this.attrs.get("seleccionado")).getKey()));
					if (transaccion.ejecutar(eaccion)){
						JsfBase.addMessage("El prestamo se cancelo correctamente.");
					} // if
					doLoad();
					break;
			} // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public void doEliminar() {
		doAccion(EAccion.DESACTIVAR.name()); //cancelar prestamo
  } // doEliminar
	
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tr_mantic_empresa_personal.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tr_mantic_empresa_personal.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
		if(this.attrs.get("idPrestamoProcess")!= null && !Cadena.isVacio(this.attrs.get("idPrestamoProcess")))
			sb.append("tc_keet_prestamos.id_prestamo=").append(this.attrs.get("idPrestamoProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
			sb.append("(tc_keet_prestamos.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
		sb.append("cast(tc_keet_prestamos.registro as date) between '").append(inicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("' and '");
		sb.append(termino.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("' and ");
		if(!Cadena.isVacio(this.attrs.get("estatus")) && ((UISelectEntity)this.attrs.get("estatus")).getKey()>= 1L)				
			sb.append("tc_keet_prestamos.id_prestamo_estatus = ").append(((UISelectEntity)this.attrs.get("estatus")).getKey()).append(" and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

	private void cargaCatalogos() {
		try {
			loadEmpresas();
			doLoadDeudores();
			loadEstatus();
			inicio= LocalDate.of(Fecha.getAnioActual(),1, 1);
      termino= LocalDate.now();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
	} // cargaCatalogos
	
	public void doLoadDeudores(){
		UISelectEntity empresa= null;
		List<Columna>campos= null;
	  try {
			campos= new ArrayList<>();
			campos.add(new Columna("dedor", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));
			empresa = (UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  this.attrs.put("sucursales", empresa.getKey());
			else
				this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("deudores", UIEntity.seleccione("VistaDeudoresDto", "byEmpresa", this.attrs, "deudor"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
		finally{
			Methods.clean(campos);
		} // finally
	} // doLoadDeudores
	
	private void loadEstatus(){
	  try {
		  this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("listaEstatus", UIEntity.seleccione("TcKeetPrestamosEstatusDto", "row", this.attrs, "nombre"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadDeudores
}