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
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_OBRA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_OBRA;
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
import mx.org.kaana.libs.recurso.Configuracion;
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
  private Map<Long, Double> totalesDia;
  private Map<Long, Double> totalesObra;

  public List<Entity> getDesarrollos() {
    return desarrollos;
  }

  public List<Contrato> getContratos() {
    return contratos;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    if(JsfBase.getFlashAttribute("idNomina")!= null)
		  this.attrs.put("idNomina", new UISelectEntity((Long)JsfBase.getFlashAttribute("idNomina")));
//    if(JsfBase.getFlashAttribute("idNomina")== null)
//      UIBackingUtilities.execute("janal.isPostBack('cancelar')");
		this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
		this.attrs.put("idContrato", -1L);
    this.attrs.put("activar", Boolean.TRUE);
    this.totales    = new HashMap<>();
    this.totalesDia = new HashMap<>();
    this.totalesObra= new HashMap<>();
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
    UISelectEntity idNomina  = null;
    try {
      List<UISelectEntity> nominas= (List<UISelectEntity>)this.attrs.get("nominas");
      if(nominas!= null) {
        int index= nominas.indexOf((UISelectEntity)this.attrs.get("idNomina"));
        if(index>= 0)
          this.attrs.put("idNomina", nominas.get(index));
      } // if
      idNomina= (UISelectEntity)this.attrs.get("idNomina");
      this.attrs.put("activar", !Objects.equals(idNomina.toLong("idNominaEstatus"), 4L));
			params.put("idNomina", idNomina.getKey());
			params.put("sortOrder", "order by tc_keet_nominas_desarrollos.id_desarrollo");
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          break;
        case "gylvi":
          params.put("puestosPorDia", GYLVI_PERSONAL_DIA);      
          params.put("puestosPorObra", GYLVI_PERSONAL_OBRA);      
          break;
        case "triana":
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          break;
        default:  
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          break;
      } // switch
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
      if(this.attrs.get("idNomina")== null)
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
          this.doLoadDesarrollos();
          this.doLoad();
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

  public void doUpdateCostoDia(Contrato row) {
    if(Objects.equals(row.getSql(), ESql.SELECT)) 
      row.setSql(ESql.UPDATE);
    for (Entity item: this.desarrollos) {
      if(Objects.equals(item.toLong("idDesarrollo"), row.getIdDesarrollo())) 
        row.setPorDia(Numero.toRedondearSat(row.getPorcentajeDia()/ 100* item.toDouble("porDiaCosto")));
    } // for  
    row.setTotal(Numero.toRedondearSat(row.getPorDia()+ row.getPorObra()));
    this.toTotales();
  }
  
  public void doUpdateCostoObra(Contrato row) {
    if(Objects.equals(row.getSql(), ESql.SELECT)) 
      row.setSql(ESql.UPDATE);
    for (Entity item: this.desarrollos) {
      if(Objects.equals(item.toLong("idDesarrollo"), row.getIdDesarrollo())) 
        row.setPorObra(Numero.toRedondearSat(row.getPorcentajeObra()/ 100* item.toDouble("porObraCosto")));
    } // for  
    row.setTotal(Numero.toRedondearSat(row.getPorDia()+ row.getPorObra()));
    this.toTotales();
  }
  
  private void toTotales() {
    if(this.contratos!= null && this.contratos.size()> 0) {
      this.totales.clear();
      this.totalesDia.clear();
      this.totalesObra.clear();
      for (Contrato item: this.contratos) {
        if(this.totales.containsKey(item.getIdDesarrollo())) {
          this.totales.put(item.getIdDesarrollo(), Numero.toRedondearSat(this.totales.get(item.getIdDesarrollo())+ item.getTotal()));
          this.totalesDia.put(item.getIdDesarrollo(), Numero.toRedondearSat(this.totalesDia.get(item.getIdDesarrollo())+ item.getPorcentajeDia()));
          this.totalesObra.put(item.getIdDesarrollo(), Numero.toRedondearSat(this.totalesObra.get(item.getIdDesarrollo())+ item.getPorcentajeObra()));
        } // if  
        else {
          this.totales.put(item.getIdDesarrollo(), item.getTotal());
          this.totalesDia.put(item.getIdDesarrollo(), item.getPorcentajeDia());
          this.totalesObra.put(item.getIdDesarrollo(), item.getPorcentajeObra());
        } // else  
      } // for
    } // if
  }

  private Boolean toCheckTotales() {
    Boolean regresar= this.contratos!= null && this.contratos.size()> 0;
    if(regresar) {
      for (Entity item: this.desarrollos) {
//        Double costo= this.totales.get(item.toLong("idDesarrollo"));
//        if(!Objects.equals(Numero.redondea(item.toDouble("totalCosto"), 1), Numero.redondea(costo, 1))) {
        if(this.totales.containsKey(item.toLong("idDesarrollo"))) {
          Double porcentajeDia = this.totalesDia.get(item.toLong("idDesarrollo"));
          Double porcentajeObra= this.totalesObra.get(item.toLong("idDesarrollo"));
          if(!Objects.equals(100.0, porcentajeDia) || !Objects.equals(100.0, porcentajeObra)) {
            JsfBase.addMessage("Los porcentajes de mano de obra [".concat(item.toString("desarrollo")).concat("] no son iguales, por día [100% | ")+ porcentajeDia+ "] por obra [100% | "+ porcentajeObra+ "]", ETipoMensaje.ERROR);
            regresar= Boolean.FALSE;
            break;          
          } // if
        } // if
      } // for
    } // if
    else
      JsfBase.addMessage("No se tiene costos que registrar por desarrollo", ETipoMensaje.ERROR);
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
        Double porDiaCosto   = item.toDouble("porDiaCosto");
        Double porObraCosto  = item.toDouble("porObraCosto");
        idDesarrollo         = item.toLong("idDesarrollo");
        Double porDia        = 0D;
        Double porObra       = 0D;
        Double porcentajeDia = 0D;
        Double porcentajeObra= 0D;
        count= 1;
        for (Contrato contrato: this.contratos) {
          if(Objects.equals(idDesarrollo, contrato.getIdDesarrollo())) {
            contrato.setPorcentajeDia(Numero.toRedondearSat(100/ partes.get(idDesarrollo)));
            contrato.setPorcentajeObra(Numero.toRedondearSat(100/ partes.get(idDesarrollo)));
            if(Objects.equals(count, partes.get(idDesarrollo))) {
              contrato.setPorDia(Numero.toRedondearSat(porDiaCosto- porDia));
              contrato.setPorcentajeDia(Numero.toRedondearSat(100- porcentajeDia));
              contrato.setPorObra(Numero.toRedondearSat(porObraCosto- porObra));
              contrato.setPorcentajeObra(Numero.toRedondearSat(100- porcentajeObra));
            } // if
            else {
              contrato.setPorDia(Numero.toRedondearSat(contrato.getPorcentajeDia()/ 100* porDiaCosto));
              porDia+= contrato.getPorDia();
              porcentajeDia+= contrato.getPorcentajeDia();
              contrato.setPorObra(Numero.toRedondearSat(contrato.getPorcentajeObra()/ 100* porObraCosto));
              porObra+= contrato.getPorObra();
              porcentajeObra+= contrato.getPorcentajeObra();
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
