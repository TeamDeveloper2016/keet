package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EBooleanos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.ConceptoExtra;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;
import mx.org.kaana.keet.comun.gps.Distance;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosDestajosExtra")
@ViewScoped
public class Extra extends IBaseAttribute implements Serializable {	

	private static final long serialVersionUID  = 4077399316243366480L;
	private static final Long NIVEL_ESPECIALIDAD= 5L;
		
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Entity figura            = null;
		Entity seleccionado      = null;
		Long idDepartamento      = null;
    try {			
			this.attrs.put("latitud", "21.890563");
  		this.attrs.put("longitud", "-102.252030");
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("claveEstacion", JsfBase.getFlashAttribute("claveEstacion"));
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			figura= (Entity) JsfBase.getFlashAttribute("figura");	
			seleccionado= (Entity) JsfBase.getFlashAttribute("seleccionado");	
			idDepartamento= (Long)JsfBase.getFlashAttribute("idDepartamento");	
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("figura", figura);      
			this.attrs.put("seleccionadoPivote", seleccionado);      			
			this.attrs.put("idDesarrollo", idDesarrollo);      
			this.attrs.put("idDepartamento", idDepartamento);      			
			this.attrs.put("descripcionEstacion", "");      			
			this.attrs.put("importeEstacion", 0L);      			
			this.loadCatalogos();						
			this.doLoad();			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote="+ ((Entity)this.attrs.get("seleccionadoPivote")).getKey());
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
	  
  public void doLoad() {
		Map<String, Object>params        = null;
    List<UISelectEntity> puntosGrupos= null;
    List<UISelectEntity> estaciones  = null;
    try {      			
			params= new HashMap<>();
			params.put("idDepartamento", this.attrs.get("idDepartamento"));
			params.put("idExtra", EBooleanos.SI.getIdBooleano());
			params.put("clave", this.toClaveEstacion());
			params.put("nivel", NIVEL_ESPECIALIDAD);
			puntosGrupos= UIEntity.seleccione("VistaRubrosDto", "byDepartamentoExtra", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "codigo");
			this.attrs.put("conceptos", puntosGrupos);									
			estaciones= UIEntity.seleccione("TcKeetEstacionesDto", "byClaveNivel", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "codigo");
			this.attrs.put("especialidades", estaciones);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(params);
    } // finally		
  } // doLoad	  	
	
	private String toClaveEstacion() {
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(this.attrs.get("idEmpresa").toString(), 3, '0', true));
			regresar.append(((Entity)this.attrs.get("seleccionadoPivote")).toString("ejercicio"));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("ordenContrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion		
	
	public void doAsignaGeoreferencia(String latitud, String longitud){
		this.attrs.put("latitud", latitud);
		this.attrs.put("longitud", longitud);
 		this.attrs.put("punto", new Point(Numero.getDouble(latitud, 21.890563), Numero.getDouble(longitud, -102.252030)));
	} // doAsignaGeoreferencia
	
	private double toDistance() {
		Point gpsA= (Point)this.attrs.get("georreferencia");
		Point gpsB= (Point)this.attrs.get("punto");
		Distance distance= new Distance(gpsA, gpsB);
		return distance.toMt();
	}
	
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;		
    try {						
			transaccion= new Transaccion(this.loadConceptoExtra());
			if(transaccion.ejecutar(EAccion.AGREGAR)) {
				JsfBase.addMessage("Agregar concepto extra", "Se realizó la captura del concepto extra de forma correcta.", ETipoMensaje.INFORMACION);
				regresar= this.doCancelar();
			} // if
			else
				JsfBase.addMessage("Agregar concepto extra", "Ocurrió un error al realizar la captura del concepto extra.", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	private ConceptoExtra loadConceptoExtra() {
		ConceptoExtra regresar        = null;
		List<UISelectEntity> conceptos= null;
		UISelectEntity concepto       = null;
		Entity figura                 = null;
		Entity seleccionado           = null;
		Long idFigura                 = -1L;
		try {
			conceptos= (List<UISelectEntity>) this.attrs.get("conceptos");
			concepto= conceptos.get(conceptos.indexOf((UISelectEntity)this.attrs.get("concepto")));
			figura= (Entity) this.attrs.get("figura");
			seleccionado= (Entity) this.attrs.get("seleccionadoPivote");
			idFigura= figura.toLong("tipo").equals(1L) ? seleccionado.toLong("idContratoLoteContratista") : seleccionado.toLong("idContratoLoteProveedor");			
			regresar= new ConceptoExtra();
			regresar.setIdFigura(idFigura);
			regresar.setTipo(figura.toLong("tipo"));
			regresar.setIdEstacion(Long.valueOf(this.attrs.get("especialidad").toString()));
			regresar.setIdPuntoGrupo(concepto.toLong("idPuntoGrupo"));
			regresar.setIdRubro(concepto.getKey());
			regresar.setIdDepartamento(Long.valueOf(this.attrs.get("idDepartamento").toString()));
			regresar.setDescripcion((String)this.attrs.get("descripcion"));
			regresar.setImporte(Double.valueOf(this.attrs.get("importe").toString()));
			regresar.setLatitud((String)this.attrs.get("latitud")); 
			regresar.setLongitud((String)this.attrs.get("longitud"));
			regresar.setMetros(this.toDistance());
      regresar.setIdContratoLote(((Entity)this.attrs.get("seleccionadoPivote")).getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadConceptoExtra
	
	public String doCancelar() {
    String regresar= null;    		
    try {																					
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));									
			JsfBase.setFlashAttribute("figura", (Entity)this.attrs.get("figura"));									
			JsfBase.setFlashAttribute("seleccionado", (Entity)this.attrs.get("seleccionadoPivote"));									
			JsfBase.setFlashAttribute("idDesarrollo", (Long)this.attrs.get("idDesarrollo"));									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("idDepartamento").toString()));
			JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
			JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
			regresar= "conceptos".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}