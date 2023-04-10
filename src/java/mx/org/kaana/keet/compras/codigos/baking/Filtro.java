package mx.org.kaana.keet.compras.codigos.baking;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.compras.codigos.reglas.Transaccion;

@Named(value = "keetComprasCodigosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
	private List<UISelectEntity> contratos;
	
  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaTermino() {
    return fechaTermino;
  }

  public void setFechaTermino(LocalDate fechaTermino) {
    this.fechaTermino = fechaTermino;
  }
  
	public List<UISelectEntity> getContratos() {
		return contratos;
	}

	public void setContratos(List<UISelectEntity> desarrollos) {
		this.contratos= desarrollos;
	}	

  @PostConstruct
  @Override
  protected void init() {		
    try {
      this.attrs.put("idUtilizado", 2L);
      this.toLoadCatalogos();
  		this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      params.put("sortOrder", "order by tc_keet_ordenes_codigos.registro asc");
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));			
      columns.add(new Columna("utilizado", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaContratosMaterialesDto", "codigos", params, columns);
      UIBackingUtilities.resetDataTable();
      Entity codigo= (Entity)DaoFactory.getInstance().toEntity("VistaContratosMaterialesDto", "codigos", params);
      if(codigo!= null && !codigo.isEmpty())
        this.attrs.put("ultimo", codigo.toString("codigo"));
      else
        this.attrs.put("ultimo", null);
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

	private Map<String, Object> toPrepare() throws Exception {
		Map<String, Object> regresar = new HashMap<>();
		StringBuilder sb             = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !((UISelectEntity)this.attrs.get("idEmpresa")).getKey().equals(-1L))
		  sb.append("(tc_mantic_empresas.id_empresa= ").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(") and ");	
		if(!Cadena.isVacio((String)this.attrs.get("ordenCompra")))
		  sb.append("(tc_mantic_ordenes_compras.consecutivo like '%").append((String)this.attrs.get("ordenCompra")).append("%') and ");	
		if(!Cadena.isVacio((String)this.attrs.get("codigo")))
		  sb.append("(tc_keet_ordenes_codigos.codigo like '%").append((String)this.attrs.get("codigo")).append("%') and ");	
		if(!Cadena.isVacio((String)this.attrs.get("usuario")))
		  sb.append("(concat(tc_mantic_personas.nombre, ' ', ifnull(tc_mantic_personas.paterno, ' '), ' ', ifnull(tc_mantic_personas.materno, ' ')) like '%").append((String)this.attrs.get("usuario")).append("%') and ");	
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_keet_ordenes_codigos.utilizado, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_keet_ordenes_codigos.utilizado, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idUtilizado")) && !Objects.equals((Long)this.attrs.get("idUtilizado"), -1L))
		  if(Objects.equals((Long)this.attrs.get("idUtilizado"), 1L))
		    sb.append("(tc_keet_ordenes_codigos.utilizado is not null) and ");	
      else 
		    sb.append("(tc_keet_ordenes_codigos.utilizado is null) and ");	
		regresar.put(Constantes.SQL_CONDICION, sb.length()== 0? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()- 4));				
		return regresar;		
	} // toPrepare  
	
  public void toLoadCatalogos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
  	List<UISelectEntity>empresas= null;
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      empresas= (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");			
      this.attrs.put("empresas", empresas);			
      attrs.put("idEmpresa", empresas!= null? UIBackingUtilities.toFirstKeySelectEntity(empresas): new UISelectEntity(-1L));
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
  }
  
	public void doAccion() {
    Transaccion transaccion= null;
    try {
      transaccion = new Transaccion();
      if (transaccion.ejecutar(EAccion.AGREGAR)) {
        JsfBase.addMessage("Agregar", "Se agregaron los códigos correctamente !", ETipoMensaje.ERROR);
        this.doLoad();
      } // if  
      else
        JsfBase.addMessage("Agregar", "Ocurrió un error al agregar los códigos !", ETipoMensaje.ERROR);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
	}	// doAccion()
  
	public void doLimpiar() {
    Transaccion transaccion= null;
    Entity seleccionado    = null;
    try {
      seleccionado= (Entity)this.attrs.get("seleccionado");
      transaccion = new Transaccion(seleccionado.getKey());
      if (transaccion.ejecutar(EAccion.ELIMINAR)) 
        JsfBase.addMessage("Limpiar", "Se limpio él código correctamente !", ETipoMensaje.ERROR);
      else
        JsfBase.addMessage("Limpiar", "Ocurrió un error al limpiar el código !", ETipoMensaje.ERROR);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
	}	// doLimpiar
  
	public String doOrdenCompra() {
    Entity seleccionado= (Entity)this.attrs.get("seleccionado");
		JsfBase.setFlashAttribute("idOrdenCompra", seleccionado.get("idOrdenCompra"));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Compras/Codigos/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/filtro".concat(Constantes.REDIRECIONAR);
	}	// doOrdenCompra
  
}