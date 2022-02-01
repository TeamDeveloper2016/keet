package mx.org.kaana.keet.estaciones.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;

@Named(value = "keetEstacionesContrato")
@ViewScoped
public class Contrato extends Filtro implements Serializable {

  private static final long serialVersionUID = -2149414807855567759L;
	
	@PostConstruct
  @Override
  protected void init() {
    try {
			this.estaciones= new Estaciones();
			this.estaciones.cleanLevels();
      this.attrs.put("idEmpresa", new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
			if(JsfBase.getFlashAttribute("estacionProcess")!= null){
				this.current= (TcKeetEstacionesDto)JsfBase.getFlashAttribute("estacionProcess");
				this.actualizarChildren(1);
			} // if
      else {
				this.current=new TcKeetEstacionesDto();
				this.current.setClave("");
				this.current.setNivel(1L);
				this.actualizarChildren(0, 3);
				this.current.setNivel(3L);
			} // if	
		  this.loadCombos();
      this.attrs.put("filtroReporte","%");
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	@Override
  public void doLoad() {
		String nodo                 = null;
		TcKeetContratosLotesDto lote= null;
    try {
			if(this.attrs.get("idLote")!= null && ((UISelectEntity)this.attrs.get("idLote")).getKey()> 0L) {
				lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(TcKeetContratosLotesDto.class, ((UISelectEntity)this.attrs.get("idLote")).getKey());
			  nodo= estaciones.toCodeByIdContrato(lote.getIdContrato());
        this.attrs.put("seleccionado", null);
				this.current=new TcKeetEstacionesDto();
				this.current.setClave(estaciones.toCode(nodo.concat(lote.getOrden().toString())));
				this.current.setNivel(4L);
				this.actualizarChildren(1);
			} // if
			else 
        if(this.attrs.get("idContrato")!=null && ((UISelectEntity)this.attrs.get("idContrato")).getKey()> 0L) {
          nodo= estaciones.toCodeByIdContrato(((UISelectEntity)this.attrs.get("idContrato")).getKey());
          this.attrs.put("seleccionado", null);
          this.current= new TcKeetEstacionesDto();
          this.current.setClave(nodo);
          this.current.setNivel(3L);
          this.actualizarChildren(1);
        } // else if
        else 
          if(this.attrs.get("idEmpresa")!=null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>0L) {
            nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
            this.attrs.put("seleccionado", null);
            this.current= new TcKeetEstacionesDto();
            this.current.setClave(estaciones.toCode(nodo));
            this.current.setNivel(1L);
            this.actualizarChildren(1,2);
            this.current.setNivel(3L);
          } // else if
          else
            this.doInicio();
      this.attrs.put("filtroReporte", this.current.getClave().isEmpty()? "%": this.current.getClave().length()< 13? this.current.getClave().concat("%"): this.current.getClave().substring(0,13).concat("%"));
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad

	@Override
	protected void loadCombos() {
		try {
			this.loadEmpresas();
      this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	public void doLoadLotes() {
		UISelectEntity contrato = null;
	  try {
			contrato = (UISelectEntity)this.attrs.get("idContrato");
			if(contrato!= null && !contrato.isEmpty()) 
			  this.attrs.put(Constantes.SQL_CONDICION, "id_contrato= ".concat(contrato.getKey().toString()));
			else
				this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_FALSO);
      List<UISelectEntity> lotes= UIEntity.seleccione("TcKeetContratosLotesDto", "row", this.attrs, "clave");
      if(lotes!= null && !lotes.isEmpty())
        this.attrs.put("idLote", lotes.get(0));
      else
        this.attrs.put("idLote", new UISelectEntity(-1L)); 
      this.attrs.put("lotes", lotes);
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadLotes
	
	@Override
	public String doAccion(String accion) {
    EAccion eaccion= null;
		String regresar= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
			switch(eaccion){
				case REGISTRAR:
				case AGREGAR:
				case MODIFICAR:
				case CONSULTAR:
          regresar= super.doAccion(accion);
					JsfBase.setFlashAttribute("pivote", this.getVisitados().get(this.getVisitados().size()-1));
					JsfBase.setFlashAttribute("isEstacion", Boolean.FALSE);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/contrato");
          break;
				case SUBIR:
				case BAJAR:
				case ELIMINAR:
          regresar= super.doAccion(accion);
          this.doLoad();
  				break;
				case LISTAR:
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					this.current=((Entity)this.attrs.get("seleccionado"))== null? this.current: (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, ((Entity)this.attrs.get("seleccionado")).toLong("idEstacion"));
					regresar= "estructura".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("estacionProcess", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/contrato");
					break;
			} // swicth
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
	
	public String doUploadIndividual() {
		Long ikContratoLote= -1L;
		if(this.attrs.get("idLote")!= null && ((UISelectEntity)this.attrs.get("idLote")).getKey()> 0L) 
			ikContratoLote= ((UISelectEntity)this.attrs.get("idLote")).getKey();
		JsfBase.setFlashAttribute("ikContratoLote", ikContratoLote);
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.ESTACIONES.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/contrato");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}

  public String doUploadContrato(TcKeetEstacionesDto row) {
    Map<String, Object> params= null;
    try {
      params = new HashMap<>();
      params.put("idContrato", Numero.getLong(this.estaciones.toValueKey(row.getClave(), 3)));
      params.put("orden", Numero.getLong(this.estaciones.toValueKey(row.getClave(), 4)));
      Entity contrato = (Entity) DaoFactory.getInstance().toEntity("VistaContratosDto", "contrato", params);
      this.attrs.put("idContrato", new UISelectEntity(contrato));
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
    return "";
  }

  public void doLoadDesarrollos() {
    try {
     Catalogos.toLoadDesarrollos(this.attrs);
     this.doLoadContratos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mx.org.kaana.libs.formato.Error.mensaje(e);			
		} // catch		
  }

  public void doLoadContratos() {
    try {
     Catalogos.toLoadContratos(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey(), attrs);
     this.doLoadLotes();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mx.org.kaana.libs.formato.Error.mensaje(e);			
		} // catch		
  }

  @Override
	public String doUpload() {
		JsfBase.setFlashAttribute("ikContratoLote", -1L);
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.PLANTILLAS.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/contrato");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}

  public String doCheckDates(Entity row) {
    String regresar= null;
    try {      
      JsfBase.setFlashAttribute("idEstacion", row.toLong("idEstacion"));
      JsfBase.setFlashAttribute("idContrato", row.toLong("idContrato"));
      JsfBase.setFlashAttribute("idContratoLote", row.toLong("idContratoLote"));
      JsfBase.setFlashAttribute("siguiente", row.toString("siguiente"));
      JsfBase.setFlashAttribute("estacionProcess", this.current);
      JsfBase.setFlashAttribute("accion", EAccion.MODIFICAR);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/contrato");
      regresar= "fechas".concat(Constantes.REDIRECIONAR);
    } // try
    catch (Exception e) {
			JsfBase.addMessageError(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }  
  
  public String doCheckCostos(Entity row) {
    String regresar= null;
    try {      
      JsfBase.setFlashAttribute("idEstacion", row.toLong("idEstacion"));
      JsfBase.setFlashAttribute("idContrato", row.toLong("idContrato"));
      JsfBase.setFlashAttribute("idContratoLote", row.toLong("idContratoLote"));
      JsfBase.setFlashAttribute("siguiente", row.toString("siguiente"));
      JsfBase.setFlashAttribute("codigo", row.toString("codigo"));
      JsfBase.setFlashAttribute("estacionProcess", this.current);
      JsfBase.setFlashAttribute("accion", EAccion.MODIFICAR);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/contrato");
      regresar= "costos".concat(Constantes.REDIRECIONAR);
    } // try
    catch (Exception e) {
			JsfBase.addMessageError(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }  
  
}

