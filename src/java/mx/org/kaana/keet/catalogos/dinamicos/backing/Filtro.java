package mx.org.kaana.keet.catalogos.dinamicos.backing;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.comun.Comun;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.dinamicos.reglas.Transaccion;
import mx.org.kaana.keet.enums.ECatalogosDinamicos;

@Named(value = "keetCatalogosDinamicosFiltro")
@ViewScoped
public class Filtro extends Comun implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
		ECatalogosDinamicos catalogoDinamico= null;
		String unit        = null;			
		Encriptar encriptar= null;		
    try {    	
			encriptar= new Encriptar();
			if(JsfBase.getFlashAttribute("unit")!= null)
				unit= encriptar.desencriptar((String)JsfBase.getFlashAttribute("unit"));							
			else if(JsfBase.getParametro("unit")!= null)
				unit= encriptar.desencriptar(JsfBase.getParametro("unit"));							
			if(unit!= null){			
				catalogoDinamico= ECatalogosDinamicos.valueOf(unit);
				this.attrs.put("estatus", catalogoDinamico.getEstatus());			
				this.attrs.put("idXml", catalogoDinamico.getIdXml());			
				this.attrs.put("catalogoDinamico", catalogoDinamico);			
				this.attrs.put("unit", catalogoDinamico.getClase().getSimpleName());						
				this.attrs.put("titulo", catalogoDinamico.getTitulo());						
				this.attrs.put("nombre", "");
				this.attrs.put("descripcion", "");      						
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= this.toPrepare();
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
      this.lazyModel = new FormatLazyModel(this.attrs.get("unit").toString(), this.attrs.get("idXml").toString(), params, columns);
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

	protected Map<String, Object> toPrepare() {
		Map<String, Object> regresar= null;
		StringBuilder sb            = null;
		try {			
			sb= new StringBuilder("");						
			if(!Cadena.isVacio(this.attrs.get("nombre"))) 
				sb.append("nombre like '%").append(this.attrs.get("nombre")).append("%' and ");						  					
			if(!Cadena.isVacio(this.attrs.get("descripcion"))) 
				sb.append("descripcion like '%").append(this.attrs.get("descripcion")).append("%' and ");						  					
			regresar= new HashMap<>();
			regresar.put("condicion", Cadena.isVacio(sb.toString()) ? Constantes.SQL_VERDADERO : sb.substring(0, sb.length()- 4));			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toCondicion

  public String doAccion(String accion) {
    EAccion eaccion = null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);
      JsfBase.setFlashAttribute("estatus", this.attrs.get("estatus"));
      JsfBase.setFlashAttribute("catalogoDinamico", this.attrs.get("catalogoDinamico"));
      JsfBase.setFlashAttribute("idXml", this.attrs.get("idXml"));            
      JsfBase.setFlashAttribute("unit", this.attrs.get("unit"));            
      JsfBase.setFlashAttribute("idKey", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) || eaccion.equals(EAccion.COPIAR) || eaccion.equals(EAccion.ACTIVAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion	
	
  public void doEliminar() {
		ECatalogosDinamicos catalogoDinamico= null;
    Transaccion transaccion   = null;
    Entity seleccionado       = null;    
		IBaseDto dto              = null;
		Constructor constructor   = null;		
    try {
			catalogoDinamico= (ECatalogosDinamicos) this.attrs.get("catalogoDinamico");
      seleccionado = (Entity) this.attrs.get("seleccionado");
			constructor= catalogoDinamico.getClase().getConstructor(new Class[]{Long.class});
			dto= (IBaseDto) constructor.newInstance(seleccionado.getKey());
      transaccion = new Transaccion(dto);
      if (transaccion.ejecutar(EAccion.ELIMINAR)) 
        JsfBase.addMessage("Eliminar registro", "El registro se ha eliminado correctamente.", ETipoMensaje.ERROR);
      else 
        JsfBase.addMessage("Eliminar registro", "Ocurrió un error al eliminar el registro.", ETipoMensaje.ERROR);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doEliminar	
}