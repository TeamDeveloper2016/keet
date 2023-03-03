package mx.org.kaana.keet.catalogos.materiales.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.materiales.beans.Material;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.materiales.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetCatalogosMaterialesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 327393488565639367L;
	private EAccion accion;
	private Material material;

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idContratoMaterial", JsfBase.getFlashAttribute("idContratoMaterial"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
      this.toLoadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.material= new Material();
          this.material.setIkEmpresa(new UISelectEntity(-1L));
          this.material.setIkDesarrollo(new UISelectEntity(-2L));
          this.material.setIkContrato(new UISelectEntity(-2L));
          this.material.setIkPrototipo(new UISelectEntity(-2L));
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          params.put("idContratoMaterial", (Long)this.attrs.get("idContratoMaterial"));
					this.material= (Material)DaoFactory.getInstance().toEntity(Material.class, "TcKeetContratosMaterialesDto", "igual", params);          
          this.material.setIkEmpresa(new UISelectEntity(this.material.getIdEmpresa()));
          this.material.setIkDesarrollo(new UISelectEntity(this.material.getIdDesarrollo()));
          this.material.setIkContrato(new UISelectEntity(this.material.getIdContrato()));
          this.material.setIkPrototipo(new UISelectEntity(this.material.getIdPrototipo()));
          UISelectEntity articulo= new UISelectEntity(this.material.getIdArticulo());
          articulo.put("codigo", new Value("codigo", this.material.getCodigo()));
          articulo.put("nombre", new Value("nombre", this.material.getNombre()));
          this.attrs.put("nombre", articulo);
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			transaccion = new Transaccion(this.material);
      this.doUpdateContrato();
      this.doUpdatePrototipo();
      // FALTA CALCULAR EL CAMPO DE EXPANSION BASADO EN LA UNIDAD DE MEDIDA Y LA CANTIDAD DEL ARCHIVO
      if(this.material.getCantidad()> 0D) 
        this.material.setExpansion(this.material.getCantidad()* 1000);
			if (transaccion.ejecutar(this.accion)) {
				JsfBase.setFlashAttribute("idContratoMaterialProcess", this.material.getIdContratoMaterial());
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el material de forma correcta"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el material", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idContratoMaterialProcess", this.material.getIdContratoMaterial());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar	
  
  public void toLoadCatalogos() {
		List<Columna> columns       = new ArrayList<>();
    Map<String, Object> params  = new HashMap<>();
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
      if(this.accion.equals(EAccion.AGREGAR))
        this.material.setIkEmpresa(empresas.get(0));
      else {
        int index= empresas.indexOf(this.material.getIkEmpresa());
        if(index>= 0)
          this.material.setIkEmpresa(empresas.get(index));
        else
          this.material.setIkEmpresa(empresas.get(0));
      } // if  
      this.doLoadDesarrollos();
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }
  
  public void doLoadDesarrollos() {
		List<UISelectEntity>desarrollos= null;
    Map<String, Object> params     = new HashMap<>();
    try {
      params.put("idEmpresa", Objects.equals(this.material.getIdEmpresa(), -1L)? -2L: this.material.getIdEmpresa());
  		desarrollos= UIEntity.seleccione("TcKeetDesarrollosDto", "empresa", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      this.attrs.put("desarrollos", desarrollos);
      if(this.accion.equals(EAccion.AGREGAR))
        this.material.setIkDesarrollo(desarrollos.get(0));
      else {
        int index= desarrollos.indexOf(this.material.getIkDesarrollo());
        if(index>= 0)
          this.material.setIkDesarrollo(desarrollos.get(index));
        else
          this.material.setIkDesarrollo(desarrollos.get(0));
      } // if  
      this.doLoadContratos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
  }
  
  public void doLoadContratos() {
		List<UISelectEntity>contratos= null;
    Map<String, Object> params   = new HashMap<>();
    try {
      params.put("idDesarrollo", this.material.getIdDesarrollo());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      this.attrs.put("contratos", contratos);
      if(this.accion.equals(EAccion.AGREGAR))
        this.material.setIkContrato(contratos.get(0));
      else {
        int index= contratos.indexOf(this.material.getIkContrato());
        if(index>= 0)
          this.material.setIkContrato(contratos.get(index));
        else
          this.material.setIkContrato(contratos.get(0));
      } // if  
      this.doLoadPrototipos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }

  public void doLoadPrototipos() {
		List<UISelectEntity>prototipos= null;
    Map<String, Object> params    = new HashMap<>();
    try {
      params.put("idContrato", this.material.getIdContrato());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		prototipos= UIEntity.seleccione("VistaContratosDto", "findPrototipos", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      this.attrs.put("prototipos", prototipos);
      if(this.accion.equals(EAccion.AGREGAR))
        this.material.setIkPrototipo(prototipos.get(0));
      else {
        int index= prototipos.indexOf(this.material.getIkPrototipo());
        if(index>= 0)
          this.material.setIkPrototipo(prototipos.get(index));
        else
          this.material.setIkPrototipo(prototipos.get(0));
      } // if  
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }

	public List<UISelectEntity> doCompleteArticulo(String query) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			if(!Cadena.isVacio(query)) 
  			query= query.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
			else
				query= "WXYZ";
  		params.put("codigo", query);			        
      params.put("idArticuloTipo", "1");	      
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("articulos", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
    return (List<UISelectEntity>)this.attrs.get("articulos");
	}	// doCompleteArticulo
 
  public void doUpdateArticulo() {
		List<UISelectEntity> articulos= null;
    try {      
      articulos= (List<UISelectEntity>)this.attrs.get("articulos");
      if(articulos!= null && !articulos.isEmpty()) {
        int index= articulos.indexOf(new UISelectEntity((UISelectEntity)this.attrs.get("nombre")));
        if(index>= 0) {
          this.attrs.put("nombre", articulos.get(index));
          this.material.setCodigo(articulos.get(index).toString("propio"));
          this.material.setNombre(articulos.get(index).toString("nombre"));
          this.material.setIdArticulo(articulos.get(index).toLong("idArticulo"));
        } // if
        else {
          this.attrs.put("nombre", null);            
          this.material.setCodigo("");
          this.material.setIdArticulo(-1L);
        } // if  
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdateContrato() {
		List<UISelectEntity> contratos= null;
    try {      
      contratos= (List<UISelectEntity>)this.attrs.get("contratos");
      if(contratos!= null && !contratos.isEmpty()) {
        int index= contratos.indexOf(this.material.getIkContrato());
        if(index>= 0) {
          this.material.setIkContrato(contratos.get(index));
          this.material.setContrato(contratos.get(index).toString("clave"));
        } // if
        else 
          throw new RuntimeException("No se tiene seleccionado un contrato !");
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdatePrototipo() {
		List<UISelectEntity> prototipos= null;
    try {      
      prototipos= (List<UISelectEntity>)this.attrs.get("prototipos");
      if(prototipos!= null && !prototipos.isEmpty()) {
        int index= prototipos.indexOf(this.material.getIkPrototipo());
        if(index>= 0) {
          this.material.setIkPrototipo(prototipos.get(index));
          this.material.setPrototipo(prototipos.get(index).toString("nombre"));
        } // if
        else 
          throw new RuntimeException("No se tiene seleccionado un contrato !");
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  
}