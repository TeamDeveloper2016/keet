package mx.org.kaana.mantic.compras.ordenes.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "manticComprasOrdenesConsulta")
@ViewScoped 
public class Consulta extends Filtro implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Consulta.class);
  private static final long serialVersionUID= 8793667741599428331L;
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      super.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
   protected void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
      this.attrs.put("clientes", (List<UISelectEntity>) UIEntity.seleccione("VistaOrdenesComprasDto", "clientes", params, columns, "clave"));
			this.attrs.put("idCliente", new UISelectEntity("-1"));
      this.attrs.put("proveedores", (List<UISelectEntity>) UIEntity.seleccione("VistaOrdenesComprasDto", "proveedores", params, columns, "clave"));
			this.attrs.put("idProveedor", new UISelectEntity("-1"));
			columns.remove(0);
			params.put(Constantes.SQL_CONDICION, "id_orden_estatus>= 3");
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.build("TcManticOrdenesEstatusDto", "row", params, columns));
			this.attrs.put("idOrdenEstatus", new UISelectEntity("-1"));
			this.doLoadDesarrollos();
    } // try
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

  @Override
  public String doNotasEntradas(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
			JsfBase.setFlashAttribute("idOrdenCompra", ((Entity)this.attrs.get("seleccionado")).getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Inventarios/Entradas/almacen?zOyOxDwIvGuCt=zNyLxMwAvCuEtAs".concat(Constantes.REDIRECIONAR_AMPERSON);
  } // doNotasEntradas  
   
}
