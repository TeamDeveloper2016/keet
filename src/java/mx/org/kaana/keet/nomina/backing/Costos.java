package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.nomina.beans.Contrato;
import mx.org.kaana.keet.nomina.reglas.Almacenar;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.TabChangeEvent;

@Named(value= "keetNominasCostos")
@ViewScoped
public class Costos extends IBaseFilter implements Serializable {

	private static final Log LOG= LogFactory.getLog(Costos.class);
  private static final long serialVersionUID= 313633488565639323L;
  
	private List<Entity> desarrollos;
	private List<Contrato> contratos;
  private Map<Long, Double> totales;

  public List<Entity> getDesarrollos() {
    return desarrollos;
  }

  public List<Contrato> getContratos() {
    return contratos;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
		//this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
		//this.attrs.put("idNomina", JsfBase.getFlashAttribute("idNomina")== null? -1L: JsfBase.getFlashAttribute("idNomina"));
		this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
		this.attrs.put("idContrato", -1L);
    this.totales= new HashMap<>();
		this.toLoadCatalogos();
		this.doLoad();
  } // init
  
	@Override
	public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
			params.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_contratos.id_contrato");
      columns= new ArrayList<>();
      columns.add(new Columna("porcentajeDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porcentajeObra", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porObra", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porElDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("diferencia", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaCostosContratosDto", "lazy", params, columns);
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
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idContrato")) && (Long)this.attrs.get("idContrato")>= 1L)				
			sb.append("tc_keet_contratos.id_contrato_estatus<= 10 and ");
    else
			sb.append("tc_keet_contratos.id_contrato_estatus<= 8 and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

	public void doLoadDesarrollos() {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    try {
			params.put("idNomina", this.attrs.get("idNomina"));
			params.put("sortOrder", "order by tc_keet_nominas_desarrollos.id_desarrollo");
      columns= new ArrayList<>();
      columns.add(new Columna("porDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porObra", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.desarrollos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosContratosDto", "desarrollos", params);
      if(this.desarrollos!= null)
        UIBackingUtilities.toFormatEntitySet(this.desarrollos, columns);
      UIBackingUtilities.resetDataTable("desarrollos");
      this.toLoadContratos();
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
  
	private void toLoadContratos() {
		Map<String, Object>params= new HashMap<>();
    try {
      List<UISelectEntity> nominas= (List<UISelectEntity>)this.attrs.get("nominas");
      if(nominas!= null) {
        int index= nominas.indexOf((UISelectEntity)this.attrs.get("idNomina"));
        if(index>= 0) {
          this.attrs.put("idNomina", nominas.get(index));
  			  params.put("semana", ((UISelectEntity)this.attrs.get("idNomina")).toString("semana"));
        } // if
        else
          params.put("semana", "1900-01");
      } // if
      else
        params.put("semana", "1900-01");
			params.put("idNomina", this.attrs.get("idNomina"));
      this.contratos= (List<Contrato>)DaoFactory.getInstance().toEntitySet(Contrato.class, "VistaCostosContratosDto", "contratos", params);
      int count= 0;
      if(this.contratos!= null) {
        for (Contrato item: this.contratos) {
          if(item.isValid()) 
            item.setSql(ESql.SELECT);
          else {
            item.setSql(ESql.INSERT);
            item.setIdNomina(((UISelectEntity)this.attrs.get("idNomina")).getKey());            
            count++;
          } // if  
        } // for  
        if(Objects.equals(count, this.contratos.size()))
          this.toDivideCostos();
        this.toTotales();
      } // if    
      UIBackingUtilities.resetDataTable("desarrollos");
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally		
	}
  
	public void doTabChange(TabChangeEvent event) {
		// if(event.getTab().getTitle().equals("Detalle")) 
	} // doTabChange		

	private void toLoadCatalogos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idTipoNomina", "1");
      this.attrs.put("nominas", UIEntity.build("VistaNominaDto", "ultima", params));
  		this.attrs.put("idNomina", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("nominas")));
      this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
    } // finally
	}

  public String doAceptar() {
    Almacenar transaccion= null;
    String regresar      = null;
    try {			
      if(this.toCheckTotales()) {
        transaccion= new Almacenar(this.contratos);
        if (transaccion.ejecutar(EAccion.GENERAR)) {
          // regresar= this.doCancelar();
          JsfBase.addMessage("Se registraron los costos de mano de obra", ETipoMensaje.INFORMACION);
        } // if
        else 
          JsfBase.addMessage("Ocurrió un error al registrar los costos de mano de obra", ETipoMensaje.ERROR);      			
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
	} // doAceptar	

 	public String doCancelar() {   
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar

  public void doUpdateCosto(Contrato row) {
    if(Objects.equals(row.getSql(), ESql.SELECT)) {
      row.setSql(ESql.UPDATE);
    } // if 
    row.setTotal(row.getPorDia()+ row.getPorObra());
    this.toTotales();
  }
  
  private void toTotales() {
    if(this.contratos!= null && this.contratos.size()> 0) {
      this.totales.clear();
      for (Contrato item: this.contratos) {
        if(this.totales.containsKey(item.getIdDesarrollo()))
          this.totales.put(item.getIdDesarrollo(), this.totales.get(item.getIdDesarrollo())+ item.getTotal());
        else
          this.totales.put(item.getIdDesarrollo(), item.getTotal());
      } // for
    } // if
  }

  private Boolean toCheckTotales() {
    Boolean regresar= Boolean.FALSE;
    if(this.contratos!= null && this.contratos.size()> 0) {
      for (Entity item: this.desarrollos) {
        Double costo= this.totales.get(item.toLong("idDesarrollo"));
        if(!Objects.equals(Numero.redondearSat(item.toDouble("totalCosto")), Numero.redondearSat(costo))) {
          JsfBase.addMessage("Los costos de mano de obra del desarrollo [".concat(item.toString("desarrollo")).concat("] no son iguales ")+ item.toDouble("totalCosto")+ " | "+ costo, ETipoMensaje.ERROR);
          regresar= Boolean.FALSE;
          break;          
        } // if
      } // for
    } // if
    return regresar;
  } 

  private void toDivideCostos() {
    Map<Long, Integer> partes= new HashMap<>();
    try {
      int count= 0;
      Long idDesarrollo= -1L;
      for (Contrato item: this.contratos) {
        if(!Objects.equals(idDesarrollo, -1L) && !Objects.equals(idDesarrollo, item.getIdDesarrollo())) {
          partes.put(idDesarrollo, count);
          count= 0;
        } // if
        idDesarrollo= item.getIdDesarrollo();
        count++;  
      } // for
      partes.put(idDesarrollo, count);
      for (Entity item: this.desarrollos) {
        Double porDiaCosto = item.toDouble("porDiaCosto");
        Double porObraCosto= item.toDouble("porObraCosto");
        idDesarrollo  = item.toLong("idDesarrollo");
        Double porDia = 0D;
        Double porObra= 0D;
        count= 1;
        for (Contrato contrato: this.contratos) {
          if(Objects.equals(idDesarrollo, contrato.getIdDesarrollo())) {
            contrato.setPorcentajeDia(Numero.toRedondearSat(100/ partes.get(idDesarrollo)));
            contrato.setPorcentajeObra(Numero.toRedondearSat(100/ partes.get(idDesarrollo)));
            if(Objects.equals(count, partes.get(idDesarrollo))) {
              contrato.setPorDia(Numero.toRedondearSat(porDiaCosto- porDia));
              contrato.setPorObra(Numero.toRedondearSat(porObraCosto- porObra));
            } // if
            else {
              contrato.setPorDia(Numero.toRedondearSat(contrato.getPorcentajeDia()/ 100* porDiaCosto));
              contrato.setPorObra(Numero.toRedondearSat(contrato.getPorcentajeObra()/ 100* porObraCosto));
              porDia+= contrato.getPorDia();
              porObra+= contrato.getPorObra();
            } // else
            contrato.setTotal(Numero.toRedondearSat(contrato.getPorDia()+ contrato.getPorObra()));
            count++;
          } // if  
        } // for
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(partes);
    } // finally
  } 
  
}
