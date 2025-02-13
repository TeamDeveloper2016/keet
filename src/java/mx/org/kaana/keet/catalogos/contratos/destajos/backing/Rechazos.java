package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Revision;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilterMultiple;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosDestajosRechazos")
@ViewScoped
public class Rechazos extends IBaseFilterMultiple implements Serializable {

	private static final long serialVersionUID = 154600879172477099L;
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
		Long idDesarrollo        = (Long) JsfBase.getFlashAttribute("idDesarrollo");
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "conceptos": JsfBase.getFlashAttribute("retorno"));		
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("claveEstacion", JsfBase.getFlashAttribute("claveEstacion"));
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
			this.attrs.put("figura", (Entity) JsfBase.getFlashAttribute("figura"));
			this.attrs.put("seleccionadoPivote", (Entity) JsfBase.getFlashAttribute("seleccionado"));
			this.attrs.put("idDepartamento", (Long) JsfBase.getFlashAttribute("idDepartamento"));
			this.attrs.put("concepto", (Entity)JsfBase.getFlashAttribute("concepto"));      			
			this.attrs.put("nombreConcepto", JsfBase.getFlashAttribute("nombreConcepto"));
			this.attrs.put("semana", JsfBase.getFlashAttribute("semana"));
      this.attrs.put("contrato", JsfBase.getFlashAttribute("contrato"));
			this.attrs.put("manzana", JsfBase.getFlashAttribute("manzana"));	
			this.loadCatalogos();
			this.doLoad();
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= new HashMap<>();
		try {
      Entity item= (Entity)this.attrs.get("seleccionadoPivote");
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(item.toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contratos", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(item.toString("idContratoLote")));
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			this.attrs.put("contratoLote", contratoLote);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally	
	} 
	
  @Override
  public void doLoad() {
		Map<String, Object>params= this.toPrepare();
    List<Columna> columns    = new ArrayList<>();				
    Entity figura            = (Entity)this.attrs.get("figura");
    try {      			
      params.put("idProveedor", -1L);
      params.put("idEmpresaPersona", -1L);
      if(figura!= null)
        if(Objects.equals(figura.toLong("tipo"), 1L))
          params.put("idEmpresaPersona", new Long(figura.getKey().toString().substring(4)));
        else
          params.put("idProveedor", new Long(figura.getKey().toString().substring(4)));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("factor", EFormatoDinamicos.NUMERO_SIN_DECIMALES));                  
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));                  
	    this.lazyModel= new FormatLazyModel("VistaCapturaDestajosDto", "puntosRechazos", params, columns);			
			UIBackingUtilities.resetDataTable();
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
	
	protected Map<String, Object> toPrepare() {
		Map<String, Object> regresar= new HashMap<>();
		try {
			regresar.put("idDepartamento", this.attrs.get("idDepartamento"));
			regresar.put("idPuntoGrupo", ((Entity)this.attrs.get("concepto")).toLong("idPuntoGrupo"));
			regresar.put("idEstacion", ((Entity)this.attrs.get("concepto")).getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare
	
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;		
    try {						
			if(this.selecteds.length>= 1) {
				transaccion= new Transaccion(this.loadRevision(), EEstacionesEstatus.EN_PROCESO.getKey());
				if(transaccion.ejecutar(EAccion.REPROCESAR)) {
					JsfBase.addMessage("Puntos de revisi�n", "Se realiz� el rechazo de forma correcta", ETipoMensaje.INFORMACION);
					regresar= this.doCancelar();
				} // if
				else
					JsfBase.addMessage("Puntos de revisi�n", "Ocurri� un error al realizar el rechazo", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Puntos de revisi�n", "Es necesario seleccionar por lo menos un punto de revisi�n", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	protected Revision loadRevision() {
		Revision regresar  = new Revision();
		Entity figura      = (Entity) this.attrs.get("figura");
		Entity seleccionado= (Entity) this.attrs.get("seleccionadoPivote");
		Long idFigura      = -1L;
		try {
			idFigura= figura.toLong("tipo").equals(1L) ? seleccionado.toLong("idContratoLoteContratista") : seleccionado.toLong("idContratoLoteProveedor");
			regresar.setIdFigura(idFigura);
			regresar.setTipo(figura.toLong("tipo"));
			regresar.setIdEstacion(((Entity) this.attrs.get("concepto")).getKey());
			regresar.setPuntosRevision(this.selecteds);
			regresar.setObservaciones((String)this.attrs.get("observaciones"));			
			regresar.setIdContratoLote(((Entity)this.attrs.get("contratoLote")).getKey());
			regresar.setClave((String)this.attrs.get("claveEstacion"));
			regresar.setIdDepartamento((Long)this.attrs.get("idDepartamento"));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadRevision
	
	public String doCancelar() {
    String regresar= null;    
    try {						
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));									
			JsfBase.setFlashAttribute("figura", (Entity)this.attrs.get("figura"));									
			JsfBase.setFlashAttribute("idFigura", (Entity)this.attrs.get("figura"));									
			JsfBase.setFlashAttribute("seleccionado", (Entity)this.attrs.get("seleccionadoPivote"));									
			JsfBase.setFlashAttribute("idDesarrolloProcess", (Long)this.attrs.get("idDesarrollo"));									
			JsfBase.setFlashAttribute("idDesarrollo", (Long)this.attrs.get("idDesarrollo"));									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("idDepartamento").toString()));
			JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
			JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
			JsfBase.setFlashAttribute("nombreConcepto", this.attrs.get("nombreConcepto"));			
			JsfBase.setFlashAttribute("semana", this.attrs.get("semana"));			
			JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));			
			JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));	
			regresar= ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 
  
}