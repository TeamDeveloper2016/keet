package mx.org.kaana.keet.estaciones.backing;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
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
public class Contrato extends Filtro {
	
	@PostConstruct
  @Override
  protected void init() {
    try {
			this.estaciones= new Estaciones();
			this.estaciones.cleanLevels();
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("estacionProcess")!= null){
				this.current= (TcKeetEstacionesDto)JsfBase.getFlashAttribute("estacionProcess");
				actualizarChildren(1);
			} // if
			else{
				this.current=new TcKeetEstacionesDto();
				this.current.setClave("");
				this.current.setNivel(1L);
				actualizarChildren(0, 3);
				this.current.setNivel(3L);
			} // if	
		loadCombos();
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
			if(this.attrs.get("lote")!= null && ((UISelectEntity)this.attrs.get("lote")).getKey()> 0L) {
				lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(TcKeetContratosLotesDto.class, ((UISelectEntity)this.attrs.get("lote")).getKey());
			  nodo= estaciones.toCodeByIdContrato(lote.getIdContrato());
				this.current=new TcKeetEstacionesDto();
				this.current.setClave(estaciones.toCode(nodo.concat(lote.getOrden().toString())));
				this.current.setNivel(4L);
				this.actualizarChildren(1);
			} // if
			else if(this.attrs.get("contrato")!=null && ((UISelectEntity)this.attrs.get("contrato")).getKey()>0L) {
				nodo= estaciones.toCodeByIdContrato(((UISelectEntity)this.attrs.get("contrato")).getKey());
				this.current= new TcKeetEstacionesDto();
				this.current.setClave(nodo);
				this.current.setNivel(3L);
				this.actualizarChildren(1);
			} // else if
			else if(this.attrs.get("idEmpresa")!=null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>0L) {
					nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
					this.current= new TcKeetEstacionesDto();
					this.current.setClave(estaciones.toCode(nodo));
					this.current.setNivel(1L);
					this.actualizarChildren(1,2);
					this.current.setNivel(3L);
				} // else if
				else
					this.doInicio();
        this.attrs.put("filtroReporte",this.current.getClave().isEmpty()? "%": this.current.getClave().length()< 13? this.current.getClave().concat("%"): this.current.getClave().substring(0,13).concat("%"));
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
					this.current=((TcKeetEstacionesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetEstacionesDto)this.attrs.get("seleccionado"));
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
		if(this.attrs.get("lote")!= null && ((UISelectEntity)this.attrs.get("lote")).getKey()> 0L) 
			ikContratoLote= ((UISelectEntity)this.attrs.get("lote")).getKey();
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
      this.attrs.put("contrato", contrato);
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
  
}
