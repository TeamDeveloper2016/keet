package mx.org.kaana.keet.controles.backing;

import java.io.File;
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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@Named(value = "keetControlesDetalle")
@ViewScoped
public class Detalle extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 154600879172477092L;	
		
  private RegistroDesarrollo registroDesarrollo;	
	private MapModel model;
  
	public MapModel getModel() {
		return model;
	}

  public RegistroDesarrollo getRegistroDesarrollo() {
    return registroDesarrollo;
  }

  @PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("seleccionado", JsfBase.getFlashAttribute("seleccionado"));
			this.attrs.put("clave", JsfBase.getFlashAttribute("clave"));
			this.attrs.put("idContratoLote", JsfBase.getFlashAttribute("idContratoLote"));	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null ? "filtro" : JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("idDesarrollo", JsfBase.getFlashAttribute("idDesarrollo"));
			this.attrs.put("pathPivote", File.separator.concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/").concat("residentes").concat("/"));						
      this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));
 			this.attrs.put("domicilio", this.toDomicilio());				
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init	
	
  @Override
  public void doLoad() {
		List<Columna> columns= null;		
		try {
			columns= new ArrayList<>();
      columns.add(new Columna("residente", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));			
      this.attrs.put("sortOrder", "order by tc_keet_controles.clave");
      this.lazyModel= new FormatLazyModel("VistaControlesLotesDto", "detalle", this.attrs, columns);
      this.model    = new DefaultMapModel();
      Point georreferencia= (Point)this.attrs.get("georreferencia");
			Entity lote         = (Entity)this.attrs.get("seleccionado");
			String icon         = this.toIcon(lote);
      Marker marker = new Marker(new LatLng(georreferencia.getLatitud(), georreferencia.getLongitud()), "Contrato: ".concat(lote.toString("clave")).concat(", Lote: ").concat(lote.toString("lote")), lote, icon);
      this.model.addOverlay(marker);
      this.attrs.put("coordenadaCentral", georreferencia.getLatitud()+ ","+ georreferencia.getLongitud());
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    }// finally		
  } // doLoad		
	
	public String doCancelar() {
    try {						
			JsfBase.setFlashAttribute("idContratoLote", this.attrs.get("idContratoLote"));
			JsfBase.setFlashAttribute("idDesarrollo", (Long)this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar		
 
	private String toIcon(Entity lote) throws Exception {
		String regresar          = null;
		String imagen            = null;
		String color             = null;
		Map<String, Object>params= null;
		Entity estatus           = null;
		try {
			imagen= JsfBase.getContext().concat("/javax.faces.resource/icon/mapa/").concat("home-{color}-{orden}.png").concat(".jsf?ln=janal");
			color= EEstacionesEstatus.INICIAR.getColor();
			params= new HashMap<>();			
			params.put("clave", this.attrs.get("clave"));
			estatus= (Entity) DaoFactory.getInstance().toEntity("VistaGeoreferenciaLotesDto", "estatusLoteResidente", params);
			if(estatus.toString("total")!= null) {
				this.attrs.put("porcentaje", new Integer(String.valueOf((estatus.toLong("terminado") * 100)/estatus.toLong("total"))));
				if(estatus.toLong("total").equals(estatus.toLong("terminado")))
					color= EEstacionesEstatus.TERMINADO.getColor();
				else if(estatus.toLong("total").equals(estatus.toLong("iniciado")))
					color= EEstacionesEstatus.INICIAR.getColor();
				else
					color= EEstacionesEstatus.EN_PROCESO.getColor();
			} // if	
			else
				this.attrs.put("porcentaje", 0);			
			params.clear();
			params.put("color", color);
			params.put("orden", lote.toString("orden"));
			regresar= Cadena.replaceParams(imagen, params);
		} // try
		finally {
			Methods.clean(params);
		} // finally		
		return regresar;
	} // toIcon
 
	private String toDomicilio() {
		StringBuilder regresar= null;
		try {
			regresar= new StringBuilder();
			regresar.append(this.registroDesarrollo.getDomicilio().getCalle()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroExterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroExterior()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroInterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroInterior()).append(" , ");
			regresar.append(this.registroDesarrollo.getDomicilio().getAsentamiento()).append(" , C.P. ");
			regresar.append(this.registroDesarrollo.getDomicilio().getCodigoPostal());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toDomicilio
  
}