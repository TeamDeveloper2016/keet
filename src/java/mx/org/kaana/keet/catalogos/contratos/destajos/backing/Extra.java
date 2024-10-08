package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

import java.io.Serializable;
import java.util.Collections;
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
import mx.org.kaana.kajool.enums.EBooleanos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.ConceptoExtra;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;
import mx.org.kaana.libs.recurso.Configuracion;
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
  
  private EAccion accion;
  private Entity concepto;
  
  public String getValidacion() {
    String regresar= "libre";
    switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
      case "gylvi": 
        regresar= "requerido";					
        break;
      case "cafu":
      case "triana":
      default:
        regresar= "libre";					
        break;
    } // swtich
    return regresar;
  }
  
  public Boolean getEnable() {
      return Objects.equals(this.accion, EAccion.MODIFICAR);  
  }
  
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
      this.accion  = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.concepto= JsfBase.getFlashAttribute("concepto")== null? null: (Entity)JsfBase.getFlashAttribute("concepto");
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
			this.attrs.put("semana", JsfBase.getFlashAttribute("semana"));
      this.attrs.put("contrato", JsfBase.getFlashAttribute("contrato"));
			this.attrs.put("manzana", JsfBase.getFlashAttribute("manzana"));	
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
		Map<String, Object>params= new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contratos", contrato);
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
	} 
	  
  public void doLoad() {
		Map<String, Object>params      = new HashMap<>();
    List<UISelectEntity> conceptos = null;
    List<UISelectEntity> estaciones= null;
    try {      			
			params.put("idDepartamento", this.attrs.get("idDepartamento"));
			params.put("idExtra", EBooleanos.SI.getIdBooleano());
      if(!Objects.equals(concepto, null) && Objects.equals(this.accion, EAccion.MODIFICAR)) {
			  params.put("clave", concepto.toString("clave").substring(0, 16));
        this.attrs.put("descripcion", concepto.toString("descripcion"));
        this.attrs.put("importe", concepto.toDouble("importe"));
        this.attrs.put("justificacion", concepto.toString("justificacion"));
      } // if  
      else  
			  params.put("clave", this.toClaveEstacion());
			params.put("nivel", NIVEL_ESPECIALIDAD);
			params.put("cuales", Objects.equals(this.accion, EAccion.MODIFICAR)? "": "not");
			conceptos= UIEntity.seleccione("VistaRubrosDto", "byDepartamentoExtra", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "codigo");
			this.attrs.put("conceptos", conceptos);									
      if(!Objects.equals(conceptos, null) && !Objects.equals(concepto, null) && Objects.equals(this.accion, EAccion.MODIFICAR)) {
        int index= conceptos.indexOf(new UISelectEntity(concepto.toLong("idRubro")));
        if(index< 0)
          this.attrs.put("concepto", conceptos.get(0));
        else
          this.attrs.put("concepto", conceptos.get(index));
      } // if
			estaciones= UIEntity.seleccione("TcKeetEstacionesDto", "byClaveNivel", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "codigo");
			this.attrs.put("especialidades", estaciones);
      if(!Objects.equals(estaciones, null) && !Objects.equals(concepto, null) && Objects.equals(this.accion, EAccion.MODIFICAR) && estaciones.size()> 1) 
        this.attrs.put("especialidad", estaciones.get(1));
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
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("idEmpresa"), 3, '0', true));
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
			if(transaccion.ejecutar(this.accion)) {
				JsfBase.addMessage("Agregar extra", "Se realiz� el registro del extra de forma correcta", ETipoMensaje.INFORMACION);
				regresar= this.doCancelar();
			} // if
			else
				JsfBase.addMessage("Agregar extra", "Ocurri� un error al realizar el registro", ETipoMensaje.ERROR);			
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
		UISelectEntity rubro          = null;
		Entity figura                 = null;
		Entity seleccionado           = null;
		Long idFigura                 = -1L;
		try {
			conceptos= (List<UISelectEntity>) this.attrs.get("conceptos");
			rubro    = conceptos.get(conceptos.indexOf((UISelectEntity)this.attrs.get("concepto")));
			figura   = (Entity) this.attrs.get("figura");
			seleccionado= (Entity) this.attrs.get("seleccionadoPivote");
			idFigura= figura.toLong("tipo").equals(1L) ? seleccionado.toLong("idContratoLoteContratista") : seleccionado.toLong("idContratoLoteProveedor");			
			regresar= new ConceptoExtra();
			regresar.setIdFigura(idFigura);
			regresar.setTipo(figura.toLong("tipo"));
			regresar.setIdEstacion(Long.valueOf(this.attrs.get("especialidad").toString()));
			regresar.setIdPuntoGrupo(rubro.toLong("idPuntoGrupo"));
			regresar.setIdRubro(rubro.getKey());
			regresar.setIdDepartamento(Long.valueOf(this.attrs.get("idDepartamento").toString()));
			regresar.setDescripcion((String)this.attrs.get("descripcion"));
			regresar.setImporte(Double.valueOf(this.attrs.get("importe").toString()));
			regresar.setLatitud((String)this.attrs.get("latitud")); 
			regresar.setLongitud((String)this.attrs.get("longitud"));
			regresar.setMetros(this.toDistance());
      regresar.setIdContratoLote(((Entity)this.attrs.get("seleccionadoPivote")).getKey());
			regresar.setJustificacion((String)this.attrs.get("justificacion"));
      if(!Objects.equals(concepto, null) && Objects.equals(this.accion, EAccion.MODIFICAR)) {
  			regresar.setIdEstacion(this.concepto.toLong("idEstacion"));
        regresar.setIdPivote(this.concepto.toLong("idPivote"));
      } // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 
	
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
			JsfBase.setFlashAttribute("semana", this.attrs.get("semana"));			
			JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));			
			JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));	
			regresar= "conceptos".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 
 
}