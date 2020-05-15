package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Revision;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;
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
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("claveEstacion", JsfBase.getFlashAttribute("claveEstacion"));
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
			this.attrs.put("figura", (Entity) JsfBase.getFlashAttribute("figura"));
			this.attrs.put("seleccionadoPivote", (Entity) JsfBase.getFlashAttribute("seleccionado"));
			this.attrs.put("idDepartamento", (Long) JsfBase.getFlashAttribute("idDepartamento"));
			this.attrs.put("concepto", (Entity)JsfBase.getFlashAttribute("concepto"));      			
			loadCatalogos();
			doLoad();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos(){
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(((Entity)this.attrs.get("seleccionadoPivote")).getKey().toString()));
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
	} // loadCatalogos	
	
  @Override
  public void doLoad() {
		Map<String, Object>params= null;
    List<Columna> columns    = null;				
    try {      			
			params= this.toPrepare();
      columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("factor", EFormatoDinamicos.NUMERO_SIN_DECIMALES));                  
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
	
	private Map<String, Object> toPrepare(){
		Map<String, Object> regresar= null;
		try {
			regresar= new HashMap<>();
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
			if(this.selecteds.length>=1){				
				transaccion= new Transaccion(loadRevision());
				if(transaccion.ejecutar(EAccion.REPROCESAR)){
					JsfBase.addMessage("Rechazo de puntos de revisión", "Se realizó el rechazo de los puntos de revision de forma correcta.", ETipoMensaje.INFORMACION);
					regresar= doCancelar();
				} // if
				else
					JsfBase.addMessage("Rechazo de puntos de revisión", "Ocurrió un error al realizar el rechazo de los puntos de revision.", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Rechazo de puntos de revisión", "Es necesario seleccionar por lo menos un punto de revisión.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	private Revision loadRevision(){
		Revision regresar  = null;
		Entity figura      = null;
		Entity seleccionado= null;
		Long idFigura      = -1L;
		try {
			regresar= new Revision();
			figura= (Entity) this.attrs.get("figura");
			seleccionado= (Entity) this.attrs.get("seleccionadoPivote");
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
			JsfBase.setFlashAttribute("seleccionado", (Entity)this.attrs.get("seleccionadoPivote"));									
			JsfBase.setFlashAttribute("idDesarrollo", (Long)this.attrs.get("idDesarrollo"));									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("idDepartamento").toString()));
			JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
			regresar= "conceptos".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}