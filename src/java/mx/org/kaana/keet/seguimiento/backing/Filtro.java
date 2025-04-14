package mx.org.kaana.keet.seguimiento.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "keetSeguimientoFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428172L;			
  private static final Log LOG = LogFactory.getLog(Filtro.class);
  
  private List<Entity> rowsModel;
  private List<Entity> egresosModel;
  private Map<Long, Entity> ponderadores;

  public List<Entity> getRowsModel() {
    return rowsModel;
  }

  public List<Entity> getEgresosModel() {
    return egresosModel;
  }
  
  public Collection<Entity> getPonderados() {
    return ponderadores.values();
  }
  
  @PostConstruct
  @Override
  protected void init() {		
    try {
      this.ponderadores= new HashMap<>();
      this.attrs.put("ponderados", Boolean.FALSE);
      this.toLoadCatalogos();
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
		Map<String, Object>params= new HashMap<>();
    List<Columna> columns    = new ArrayList<>();		
    Object[] contratos       = (Object[])this.attrs.get("idContrato");
    StringBuilder sb         = new StringBuilder();
    Double total             = 0D;
    try {   
      if(!Objects.equals(contratos, null) && contratos.length> 0) {
        for (Object item : contratos) {
          sb.append((String)item).append(", ");
        } // for
        sb.append("-1");
      } // if
      else
        sb.append("63, 61, 58");
      if(sb.length()> 0) {
        this.toLoadPonderadores();
        params.put("contratos", sb.toString());
        columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));                  
        columns.add(new Columna("presupuesto", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("total", EFormatoDinamicos.MILES_SIN_DECIMALES));                  
        columns.add(new Columna("cuantos", EFormatoDinamicos.MILES_SIN_DECIMALES));                  
        columns.add(new Columna("normales", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("cobrado", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("uno", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("cuales", EFormatoDinamicos.MILES_SIN_DECIMALES));                  
        columns.add(new Columna("extras", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("recuperado", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("dos", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("tres", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("faltante", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("proporcion", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        this.rowsModel = (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosContratosDto", "row", params);
        // CALCULAR LOS TOTALES DE TODOS LOS CONTRATOS
        if(!Objects.equals(this.rowsModel, null) && !this.rowsModel.isEmpty()) {
          Entity totales= new Entity(0L);
          for (Entity item: this.rowsModel) {
            for (String key: item.keySet()) {
              if(Objects.equals(key, "idKey"))
                totales.put(key, new Value(key, 0L));
              else 
                if(Objects.equals(key, "contrato"))
                  totales.put(key, new Value(key, "TOTAL:"));
                else {
                  Double value= item.toDouble(key);
                  if(totales.containsKey(key))
                    value+= totales.toDouble(key);
                  totales.put(key, new Value(key, value));
                } // if  
            } // for
            total+= item.toDouble("presupuesto");
          } // for
          // PROMEDIAR LOS PORCENTAJES DE TODOS LOS CONTRATOS
          for (String key: totales.keySet()) {
            if(Objects.equals(key, "uno") || Objects.equals(key, "dos") || Objects.equals(key, "tres")) {
              Double value= totales.toDouble(key);
              totales.getValue(key).setData(value/ this.rowsModel.size());
            } // if  
          } // for
          this.rowsModel.add(totales);
          // CALCULAR LA PROPORCION QUE REPRESENTA CADA CONTRATO CON RESPECTO AL TOTAL GENERAL
          for (Entity item: this.rowsModel) {
            Double ponderado= this.ponderadores.containsKey(item.getKey())? this.ponderadores.get(item.getKey()).toDouble("ponderado"): 0D;
            item.add("proporcion", new Value("proporcion", ponderado));
          } // for
          UIBackingUtilities.toFormatEntitySet(this.rowsModel, columns);
        } // if  
        UIBackingUtilities.resetDataTable();
        this.doLoadEgresos(total);
      } // if
      else 
        JsfBase.addMessage("Seleccionar al menos un contrato");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } // doLoad	

  public void doLoadEgresos(Double total) {
		Map<String, Object>params= new HashMap<>();
    List<Columna> columns    = new ArrayList<>();		
    Object[] contratos       = (Object[])this.attrs.get("idContrato");
    StringBuilder sb         = new StringBuilder();
    Double cajaChica         = 0D;    
    try {   
      if(!Objects.equals(contratos, null) && contratos.length> 0) {
        for (Object item : contratos) {
          sb.append((String)item).append(", ");
        } // for
        sb.append("-1");
      } // if
      else
        sb.append("63, 61, 58");
      if(sb.length()> 0) {
        params.put("idDesarrollo", ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey());
        params.put("contratos", sb.toString());
        columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));                  
        columns.add(new Columna("presupuesto", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("compras", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_SIN_DECIMALES));                  
        columns.add(new Columna("ordinarias", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("otras", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("extras", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("reposiciones", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("garantias", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("especiales", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("destajos", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("contratistas", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("proveedores", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("personal", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("porDia", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("porObra", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("cajaChica", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));                  
        this.egresosModel = (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosContratosDto", "ordenesCompra", params);
        if(!Objects.equals(this.egresosModel, null) && !this.egresosModel.isEmpty()) {
          // AGREGAR COLUMNAS DE DESTAJOS
          this.toLoadColumns("contratistas", new String[] {"destajos", "contratistas"}, params);
          this.toLoadColumns("proveedores", new String[] {"proveedores"}, params);
          // AGREGAR COLUMNAS DE PERSONAL POR EL DIA Y DE OBRA CALCULANDO CON BASE A LA PROPORCION
          this.toLoadColumns("empleados", new String[] {"personal", "porDia", "porObra"}, params);
          // AGREGAR COLUMNAS DE CAJA CHICA CALCULANDO CON BASE A LA PROPORCION
          Entity caja= (Entity)DaoFactory.getInstance().toEntity("VistaCostosContratosDto", "cajaChica", params);
          if(!Objects.equals(caja, null) && !caja.isEmpty()) 
            cajaChica= caja.toDouble("cajaChica");
          // AGREGAR LA COLUMNA DE TOTAL POR CONTRARO Y SU PORCENTAJE
          Entity totales= new Entity(0L);
          for (Entity item: this.egresosModel) {
            Double ponderado= this.ponderadores.containsKey(item.getKey())? this.ponderadores.get(item.getKey()).toDouble("ponderado"): 0D;
            item.put("cajaChica", new Value("total", (ponderado/ 100)* cajaChica));
            Double destajos  = item.toDouble("destajos")+ item.toDouble("proveedores");
            item.get("destajos").setData(destajos);
            Double value     = item.toDouble("compras")+ item.toDouble("destajos")+ item.toDouble("personal")+ item.toDouble("cajaChica");
            Double porcentaje= item.toDouble("presupuesto")<= 0D? 100: value* 100/ item.toDouble("presupuesto");
            item.put("porcentaje", new Value("porcentaje", porcentaje));
            item.put("total", new Value("total", value));
            for (String key: item.keySet()) {
              if(Objects.equals(key, "idKey"))
                totales.put(key, new Value(key, 0L));
              else 
                if(Objects.equals(key, "contrato"))
                  totales.put(key, new Value(key, "TOTAL:"));
                else 
                  if(Objects.equals(key, "registro"))
                    totales.put(key, item.get(key));
                  else {
                    value= item.toDouble(key);
                    if(totales.containsKey(key))
                      value+= totales.toDouble(key);
                    totales.put(key, new Value(key, value));
                  } // if  
            } // for
          } // for
          // AGREGAR EL TOTAL GENERAL POR COLUMNA
          for (String key: totales.keySet()) {
            if(Objects.equals(key, "porcentaje")) {
              Double value= totales.toDouble(key);
              totales.getValue(key).setData(value/ this.egresosModel.size());
            } // if  
          } // for
          this.egresosModel.add(totales);
          UIBackingUtilities.toFormatEntitySet(this.egresosModel, columns);
        } // if  
        UIBackingUtilities.resetDataTable("egresos");
      } // if
      else 
        JsfBase.addMessage("Seleccionar al menos un contrato");
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

  private void toLoadColumns(String idXml, String[] columns, Map<String, Object> params) {
    try {      
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosContratosDto", idXml, params);
      if(!Objects.equals(items, null) && !items.isEmpty()) {
        for (Entity item: this.egresosModel) {
          int index= this.toIndexOf(items, item.getKey());  
          if(index>= 0) {
            Entity row= items.get(index);
            for (String key: row.keySet()) {
              if(!Objects.equals(key, "idKey"))
                item.put(key, row.get(key));
            } // for
          } // if
          else 
            for (String key: columns) {
              item.put(key, new Value(key, 0D));
            } // for
        } // for
      } // if
      else {
        // SE METE UNA FILA CON CEROS POR CADA COLUMNA
        for (Entity item: this.egresosModel) {
          for (String key: columns) {
            item.put(key, new Value(key, 0D));
          } // for
        } // for
      } // else
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  } 
 
  private int toIndexOf(List<Entity> items, Long key) {
    int regresar= 0;
    try {
      for (Entity item: items) {
        if(Objects.equals(item.getKey(), key)) 
          break;
        regresar++;
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar>= items.size()? -1: regresar;
  } 
  
  
  public void doCheckBuscar() {
    LOG.info(this.attrs.get("idContrato"));
  }
  
	private void toLoadCatalogos() {		
    try {			
			this.toLoadEmpresas();
    } // try
    catch (Exception e) {
      throw e;
    } // catch       
	} // loadCatalog
  
	private void toLoadEmpresas() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));			
      this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // loadEmpresas	
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in ("+ ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()+ ")");			
			params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
  }  
    
	public void doLoadContratos() {
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> contratos= null;
    try {      
      params.put("idDesarrollo", ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey());
			contratos= UIEntity.build("VistaTableroDto", "contratos", params);
			this.attrs.put("contratos", contratos);
      if(!Objects.equals(contratos, null) && !contratos.isEmpty()) {
        int x= 0;
        Object[] items= new Object[contratos.size()];
        for (UISelectEntity item: contratos) {
          items[x++]= item;
        } // if
  			this.attrs.put("idContrato", items);
      } // if
      else
  			this.attrs.put("idContrato", new Object[] {});
      // this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
 
	public String doRowColor(Entity row) {
	  return Objects.equals(row.toString("contrato"), "TOTAL:")? "janal-tr-yellow janal-font-bold": "";
	} 
  
	public String doRowTinte(Entity row) {
	  return Objects.equals(row.toString("contrato"), "TOTAL:")? "janal-tr-yellow janal-font-bold": "";
	} 

  private void toLoadPonderadores() {
    Map<String, Object> params= new HashMap<>();
    List<Columna> columns     = new ArrayList<>();		
    try { 
      this.ponderadores.clear();
      params.put("idDesarrollo", ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey());
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));                  
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_CON_DECIMALES));                  
      List<Entity> contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosContratosDto", "ponderadores", params);
      // CALCULAR LOS PONDERADORES DE TODOS LOS CONTRATOS
      if(!Objects.equals(contratos, null) && !contratos.isEmpty()) {
        Double total= 0D;
        for (Entity item: contratos) 
          total+= item.toDouble("presupuesto");
        for (Entity item: contratos) {
          item.put("ponderado", new Value("ponderado", total<= 0 || item.toDouble("presupuesto")<= 0? 0D: item.toDouble("presupuesto")* 100/ total));
          item.put("porcentaje", new Value("porcentaje", total<= 0 || item.toDouble("presupuesto")<= 0? 0D: item.toDouble("presupuesto")* 100/ total));
        } // for
        UIBackingUtilities.toFormatEntitySet(contratos, columns);
        for (Entity item: contratos) 
          this.ponderadores.put(item.getKey(), item);
      } // if
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
 
  public void doView() {
    this.attrs.put("ponderados", Boolean.TRUE);
  }
  
  public void doHide() {
    this.attrs.put("ponderados", Boolean.FALSE);
  }
  
  @Override
  protected void finalize() throws Throwable {
    super.finalize(); 
    Methods.clean(this.ponderadores);
  }
  
}