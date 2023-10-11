package mx.org.kaana.keet.nomina.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Revision;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;

@Named(value = "keetNominaExtras")
@ViewScoped
public class Extras extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = -2149414807855567759L;
	
	@PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getFlashAttribute("idEmpresa")!= null? JsfBase.getFlashAttribute("idEmpresa"): new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
      this.attrs.put("idDesarrollo", JsfBase.getFlashAttribute("idDesarrollo")!= null? JsfBase.getFlashAttribute("idDesarrollo"): null);
      this.attrs.put("idContrato",  JsfBase.getFlashAttribute("idContrato")!= null? JsfBase.getFlashAttribute("idContrato"): null);
      this.attrs.put("idNomina", JsfBase.getFlashAttribute("idNomina")!= null? JsfBase.getFlashAttribute("idNomina"): null);
		  this.toLoadEmpresas();
      this.toLoadNominas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	@Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      Long idNomina= ((UISelectEntity)this.attrs.get("idNomina")).getKey();
      if(Objects.equals(idNomina, -1L)) {
        params.put("contratistas", "(tc_keet_contratos_destajos_contratistas.id_nomina is null)");
        params.put("subcontratistas", "(tc_keet_contratos_destajos_proveedores.id_nomina is null)");
      } // if  
      else {
        params.put("bcontratistas", "(tc_keet_contratos_destajos_contratistas.id_nomina= "+ idNomina+ ")");
        params.put("subcontratistas", "(tc_keet_contratos_destajos_proveedores.id_nomina= "+ idNomina+ ")");
      } // if  
			params.put("sortOrder", "order by tt_keet_temporal.id_persona, tt_keet_temporal.id_desarrollo, tt_keet_temporal.clave, tt_keet_temporal.lote, tt_keet_temporal.codigo");
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("garantia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("iva", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaNominaDto", "extras", params, columns);
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
    StringBuilder sb= new StringBuilder();
		regresar.put("idNomina", ((UISelectEntity)this.attrs.get("idNomina")).getKey());
		regresar.put("nombre", "");
		if(this.attrs.get("nombre")!= null && !Cadena.isVacio(this.attrs.get("nombre"))) {
			String nombre= ((String)this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
  		regresar.put("nombre", nombre);
		} // if
	  if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
      sb.append("(tc_keet_proyectos.id_empresa= ").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(") and ");
	  if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !Objects.equals(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey(), -1L))
      sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
	  if(!Cadena.isVacio(this.attrs.get("idContrato")) && !Objects.equals(((UISelectEntity)this.attrs.get("idContrato")).getKey(), -1L))
      sb.append("(tc_keet_contratos.id_contrato= ").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");
    if(Objects.equals(sb.length(), 0))
      sb.append(Constantes.SQL_VERDADERO);
    else
      sb.delete(sb.length()- 4, sb.length());
    regresar.put(Constantes.SQL_CONDICION, sb.toString());
		return regresar;		
	} 
  
	protected void toLoadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
      this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("empresas")));
      this.doLoadDesarrollos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally				
	} 
  
  public void doLoadDesarrollos() {
		List<UISelectEntity>desarrollos= null;
    Map<String, Object> params     = new HashMap<>();
    try {
      Long idEmpresa= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey();
      params.put("idEmpresa", Objects.equals(idEmpresa, -1L)? Constantes.TOP_OF_ITEMS: idEmpresa);
  		desarrollos= UIEntity.seleccione("TcKeetDesarrollosDto", "empresa", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      this.attrs.put("desarrollos", desarrollos);
      this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));
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
      Long idDesarrollo= ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey();
      params.put("idDesarrollo", Objects.equals(idDesarrollo, -1L)? Constantes.TOP_OF_ITEMS: idDesarrollo);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      this.attrs.put("contratos", contratos);
      this.attrs.put("idContrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally    
  }
  
	private void toLoadNominas() {
    Map<String, Object> params= new HashMap<>();
    try {
		  params.put("idTipoNomina", 1L);
		  params.put("ejercicio", Fecha.getAnioActual());
		  params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			List<UISelectEntity> nominas= UIEntity.seleccione("VistaNominaDto", "nominas", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "nombre");
      this.attrs.put("nominas", nominas);
      this.attrs.put("idNomina", UIBackingUtilities.toFirstKeySelectEntity(nominas));
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
    Long idNomina= row.toLong("idNomina");
		return Objects.equals(idNomina, null)? "janal-tr-yellow": "";
	}

  public void doRechazo() {
		Transaccion transaccion= null;	
    Long idKey             = -1L;
    try {						
      idKey= ((Entity)this.attrs.get("extra")).getKey();
      Revision revision= this.toLoadPuntoRevision();
      if(!Objects.equals(revision, null)) {
        transaccion= new Transaccion(revision, EEstacionesEstatus.EN_PROCESO.getKey());
        if(transaccion.ejecutar(EAccion.ELIMINAR)) {
          JsfBase.addMessage("Rechazo de puntos de revisión", "Se realizó el rechazo de los puntos de forma correcta", ETipoMensaje.INFORMACION);
          ((Entity)this.attrs.get("extra")).setKey(idKey);
          this.attrs.put("justificacion", null);
          this.doLoad();
        } // if  
        else
          JsfBase.addMessage("Rechazo de puntos de revisión", "Ocurrió un error al realizar el rechazo de los puntos de revision", ETipoMensaje.ERROR);
      } // if  
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }

	protected Revision toLoadPuntoRevision() {
		Revision regresar  = new Revision();
		Entity seleccionado= (Entity)this.attrs.get("extra");
		try {
			if(!Objects.equals(seleccionado, null)) {
        seleccionado.setKey(seleccionado.toLong("idPuntoPaquete"));
        regresar.setIdFigura(seleccionado.toLong("idContratoLotePivote"));
        regresar.setTipo(seleccionado.toLong("tipo"));
        regresar.setIdEstacion(seleccionado.toLong("idEstacion"));
        regresar.setPuntosRevision(new Entity[] {seleccionado});
        regresar.setObservaciones((String)this.attrs.get("justificacion"));			
        regresar.setIdContratoLote(seleccionado.toLong("idContratoLote"));
        regresar.setClave(seleccionado.toString("claveEstacion"));
      } // if  
      else
        regresar= null;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 
  
}

