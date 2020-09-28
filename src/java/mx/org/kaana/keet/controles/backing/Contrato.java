package mx.org.kaana.keet.controles.backing;

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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetControlesDto;
import mx.org.kaana.keet.controles.reglas.Controles;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;

@Named(value = "keetControlesContrato")
@ViewScoped
public class Contrato extends Filtro implements Serializable {

  private static final long serialVersionUID = -514629609713218351L;
	
	@PostConstruct
  @Override
  protected void init() {
    try {
      if(this.controles== null) {
			  this.controles= new Controles();
        this.loadCombos();
        this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      } // if  
			this.controles.cleanLevels();
			if(JsfBase.getFlashAttribute("estacionProcess")!= null) {
				this.current= (TcKeetControlesDto)JsfBase.getFlashAttribute("estacionProcess");
				this.actualizarChildren(1);
			} // if
      else 
        if(JsfBase.getFlashAttribute("idContratoLote")!= null) {
          this.attrs.put("lote", new UISelectEntity((Long)JsfBase.getFlashAttribute("idContratoLote")));
          this.doLoad();
        }
        else {
          this.current=new TcKeetControlesDto();
          this.current.setClave("");
          this.current.setNivel(1L);
          this.actualizarChildren(0, 3);
          this.current.setNivel(3L);
        } // if	
      this.attrs.put("filtroReporte","%");
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	@Override
  public void doLoad() {
		String nodo                 = "";
		TcKeetContratosLotesDto lote= null;
    try {
  		if(!Cadena.isVacio(this.attrs.get("estatus")) && !this.attrs.get("estatus").toString().equals("-1"))
    		this.controles.setEstatus(" and (tc_keet_controles.id_control_estatus= ".concat((String)this.attrs.get("estatus")).concat(")"));
      else
        this.controles.setEstatus("");
			if(this.attrs.get("lote")!= null && ((UISelectEntity)this.attrs.get("lote")).getKey()> 0L) {
				lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(TcKeetContratosLotesDto.class, ((UISelectEntity)this.attrs.get("lote")).getKey());
			  nodo= this.controles.toCodeByIdContrato(lote.getIdContrato());
				this.current=new TcKeetControlesDto();
				this.current.setClave(this.controles.toCode(nodo.concat(lote.getOrden().toString())));
				this.current.setNivel(4L);
				this.actualizarChildren(1);
			} // if
			else 
        if(this.attrs.get("contrato")!=null && ((UISelectEntity)this.attrs.get("contrato")).getKey()>0L) {
          nodo= this.controles.toCodeByIdContrato(((UISelectEntity)this.attrs.get("contrato")).getKey());
          this.current= new TcKeetControlesDto();
          this.current.setClave(nodo);
          this.current.setNivel(3L);
          this.actualizarChildren(1);
        } // else if
        else 
          if(this.attrs.get("idEmpresa")!=null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>0L) {
            nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
            this.current= new TcKeetControlesDto();
            this.current.setClave(this.controles.toCode(nodo));
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
			this.doLoadContratos();
			this.doLoadLotes();
      this.loadEstatus();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	public void doLoadLotes() {
		UISelectEntity contrato = null;
	  try {
			contrato = (UISelectEntity)this.attrs.get("contrato");
			if(contrato!= null && contrato.getKey()> 0L) 
			  this.attrs.put(Constantes.SQL_CONDICION, "id_contrato= ".concat(contrato.getKey().toString()));
			else
				this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("lotes", UIEntity.seleccione("TcKeetContratosLotesDto", "row", this.attrs, "clave"));
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadLotes
	
	public void doLoadContratos() {
		UISelectEntity empresa= null;
	  try {
			empresa = (UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  this.attrs.put("sucursales", empresa.getKey());
			else
				this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("contratos", UIEntity.seleccione("VistaContratosDto", "byEmpresa", this.attrs, "clave"));
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadContratos
	
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
				case SUBIR:
				case BAJAR:
				case ELIMINAR:
          super.doAccion(accion);
				break;
				case LISTAR:
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					this.current=((TcKeetControlesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetControlesDto)this.attrs.get("seleccionado"));
					regresar= "estructura".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("estacionProcess", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/contrato");
					break;
			} // swicth
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  

  @Override
	public String doUpload() {
		JsfBase.setFlashAttribute("ikContratoLote", -1L);
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.CONTROLES.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/contrato");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
  
	public String doUploadIndividual() {
		Long ikContratoLote= -1L;
		if(this.attrs.get("lote")!= null && ((UISelectEntity)this.attrs.get("lote")).getKey()> 0L) 
			ikContratoLote= ((UISelectEntity)this.attrs.get("lote")).getKey();
		JsfBase.setFlashAttribute("ikContratoLote", ikContratoLote);
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.CONTROLES.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/contrato");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}

  public void doUploadContrato(TcKeetControlesDto row) {
    Map<String, Object> params= null;
    try {
      params = new HashMap<>();
      params.put("idContrato", Numero.getLong(this.controles.toValueKey(row.getClave(), 3)));
      params.put("orden", Numero.getLong(this.controles.toValueKey(row.getClave(), 4)));
      Entity contrato = (Entity) DaoFactory.getInstance().toEntity("VistaContratosDto", "contrato", params);
      if(contrato!= null && !contrato.isEmpty())
        this.attrs.put("contrato", contrato);
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }

	public String doGaleria(TcKeetControlesDto row) {
    Map<String, Object> params= null;
    String regresar           = null;    		
    try {			
      params = new HashMap<>();
      params.put("idContrato", Numero.getLong(this.controles.toValueKey(row.getClave(), 3)));
      params.put("orden", Numero.getLong(this.controles.toValueKey(row.getClave(), 4)));
      Entity seleccionado= (Entity) DaoFactory.getInstance().toEntity("VistaContratosDto", "galeria", params);
			JsfBase.setFlashAttribute("", EOpcionesResidente.CONTROLES);
			JsfBase.setFlashAttribute("opcionResidente", EOpcionesResidente.CONTROLES);
			JsfBase.setFlashAttribute("seleccionado", seleccionado);												
			JsfBase.setFlashAttribute("idDesarrollo", seleccionado.toLong("idDesarrollo"));
			JsfBase.setFlashAttribute("idContratoLote", seleccionado.toLong("idContratoLote"));
			JsfBase.setFlashAttribute("georreferencia", new Point(Numero.getDouble(seleccionado.toString("latitud"), 21.890563), Numero.getDouble(seleccionado.toString("longitud"), -102.252030)));				
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/contrato");			
			regresar= "galeria".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			mx.org.kaana.libs.formato.Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } // doPagina

	private void loadEstatus() {
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;
		try {			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			allEstatus= UISelect.seleccione("TcKeetControlesEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", UIBackingUtilities.toFirstKeySelectItem(allEstatus));
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
  
}
