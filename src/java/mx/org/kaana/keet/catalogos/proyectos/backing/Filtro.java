package mx.org.kaana.keet.catalogos.proyectos.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.proyectos.beans.Proyecto;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosEstatusDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.proyectos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetProyectosDto;

@Named(value = "keetCatalogosProyectosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
		List<UISelectEntity> tiposObras= null;
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			tiposObras= UIEntity.seleccione("VistaTiposObrasDto", "catalogo", "tipoObra");
			this.attrs.put("tipoObras", tiposObras);
			this.attrs.put("tipoObra", UIBackingUtilities.toFirstKeySelectEntity(tiposObras));
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
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("etapa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaProyectoDto", params, columns);
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

  public String doAccion(String accion) {
    EAccion eaccion= null;
		String regresar= null; 
    try {
			regresar= "accion".concat(Constantes.REDIRECIONAR);
      eaccion = EAccion.valueOf(accion.toUpperCase());
			switch(eaccion){
				case SUBIR:
					regresar= "importar".concat(Constantes.REDIRECIONAR);
					break;
				case PROCESAR:
					regresar= "generadores".concat(Constantes.REDIRECIONAR);
					break;
				case COPIAR:
					regresar= "presupuestos".concat(Constantes.REDIRECIONAR);
					break;
			} // switch
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idProyecto", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Proyectos/filtro");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= null;
		boolean buscaPorCodigo    = false;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params= new HashMap<>();
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	// doCompleteCliente	
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
    UISelectEntity cliente        = (UISelectEntity)this.attrs.get("cliente");
    List<UISelectEntity>provedores= (List<UISelectEntity>)this.attrs.get("clientes");
		if(!Cadena.isVacio(this.attrs.get("clave")))
			sb.append("(tc_keet_proyectos.clave like '%").append(this.attrs.get("clave")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("etapa")))
			sb.append("(tc_keet_proyectos.etapa like '%").append(this.attrs.get("etapa")).append("%') and ");
    if(provedores!= null && cliente!= null && provedores.indexOf(cliente)>= 0) 
			sb.append("(tc_mantic_clientes.razon_social like '%").append(provedores.get(provedores.indexOf(cliente)).toString("razonSocial")).append("%') and ");
		else{
 		  if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
			  sb.append("(tc_mantic_clientes.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%') and ");    
		} // else
		if(!Cadena.isVacio(this.attrs.get("viviendasMenor")))
			sb.append("tc_keet_proyectos.no_viviendas < ").append(this.attrs.get("viviendasMenor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("viviendasMayor")))
			sb.append("tc_keet_proyectos.no_viviendas > ").append(this.attrs.get("viviendasMayor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("tipoObra")) && ((UISelectEntity)this.attrs.get("tipoObra")).getKey()>= 1L)				
			sb.append("tc_keet_proyectos.id_tipos_obras=").append(((UISelectEntity)this.attrs.get("tipoObra")).getKey()).append(" and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Proyectos/filtro"); 
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
	
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proyecto_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcKeetProyectosEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public void doActualizarEstatus() {
		Transaccion transaccion            = null;
		TcKeetProyectosBitacoraDto bitacora= null;
		Entity seleccionado                = null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			Proyecto proyecto= (Proyecto)DaoFactory.getInstance().toEntity(Proyecto.class, "byId", seleccionado.toMap());
			bitacora= new TcKeetProyectosBitacoraDto(
				(String)this.attrs.get("justificacion"), // String justificacion, 
				((UISelectEntity)this.attrs.get("estatus")).getKey(),  // Long idProyectoEstatus, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				-1L, // Long idProyectoBitacora, 
				seleccionado.getKey() // Long idProyecto 
			);
			transaccion= new Transaccion(new RegistroProyecto(proyecto), bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
		} // finally
	}	// doActualizaEstatus
	
}