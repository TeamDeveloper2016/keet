package mx.org.kaana.keet.catalogos.prototipos.backing;

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
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.beans.Estacion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosPrototiposFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			if(JsfBase.getFlashAttribute("idPrototipoProcess")!= null){
				this.attrs.put("idPrototipoProcess", JsfBase.getFlashAttribute("idPrototipoProcess"));
				this.doLoad();
				this.attrs.put("idPrototipoProcess", null);
			} // if
      this.loadEmpresas();
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
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("metros2", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaPrototiposDto", params, columns);
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
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				
	} // loadEmpresas

  public String doAccion(String accion) {
    String regresar= null;
		EAccion eaccion= null;
		TcKeetEstacionesDto estacionDto= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idPrototipo", (!eaccion.equals(EAccion.AGREGAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Prototipos/filtro");
			switch (eaccion){
				case SUBIR:
				  regresar= "importar".concat(Constantes.REDIRECIONAR);	
				break;
				case CONSULTAR:
				case MODIFICAR:
				  regresar= "accion".concat(Constantes.REDIRECIONAR);
					break;
				case COMPLEMENTAR:
					if(((Entity) this.attrs.get("seleccionado")).toLong("idEstacion")== null) {//agregar estacion
					  regresar= "/Paginas/Keet/Estaciones/accion".concat(Constantes.REDIRECIONAR);
						estacionDto= new TcKeetEstacionesDto();
						estacionDto.setNivel(3L);
						estacionDto.setClave("0012020999");//calcular
						JsfBase.setFlashAttribute("idEstacion",  -1L);
						JsfBase.setFlashAttribute("estacionPadre", estacionDto);
						JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/filtro");
					} // if
					else{
						estacionDto= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, ((Entity) this.attrs.get("seleccionado")).toLong("idEstacion"));
						JsfBase.setFlashAttribute("estacionProcess", estacionDto);
						regresar= "/Paginas/Keet/Estaciones/filtro".concat(Constantes.REDIRECIONAR);
					} // else
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
  } // doEliminar
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= new String(codigo).replaceAll(Constantes.CLEAN_SQL, "").trim();
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
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tc_mantic_clientes.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tc_mantic_clientes.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
		if(this.attrs.get("idPrototipoProcess")!= null && !Cadena.isVacio(this.attrs.get("idPrototipoProcess")))
			sb.append("tc_keet_prototipos.id_prototipo=").append(this.attrs.get("idPrototipoProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("nombre")))
			sb.append("(tc_keet_prototipos.nombre like '%").append(this.attrs.get("nombre")).append("%') and ");
    if(provedores!= null && cliente!= null && provedores.indexOf(cliente)>= 0) 
			sb.append("(tc_mantic_clientes.razon_social like '%").append(provedores.get(provedores.indexOf(cliente)).toString("razonSocial")).append("%') and ");
		else{
 		  if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
			  sb.append("(tc_mantic_clientes.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%') and ");
		} // else
    if(!Cadena.isVacio(this.attrs.get("metrosMenor")))
			sb.append("tc_keet_prototipos.metros2 < ").append(this.attrs.get("metrosMenor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("metrosMayor")))
			sb.append("tc_keet_prototipos.metros2 > ").append(this.attrs.get("metrosMayor")).append(" and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Prototipos/filtro");
    //JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.PROVEEDORES.getId());
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	} // doMasivo
}