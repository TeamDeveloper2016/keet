package mx.org.kaana.keet.estaciones.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.keet.estaciones.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;

@Named(value = "keetEstacionesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
	private List<TcKeetEstacionesDto> estacionesHijas;
	protected TcKeetEstacionesDto current;
	protected Estaciones estaciones;

	public List<TcKeetEstacionesDto> getEstacionesHijas() {
		return estacionesHijas;
	}

	public void setEstacionesHijas(List<TcKeetEstacionesDto> estacionesHijas) {
		this.estacionesHijas = estacionesHijas;
	}

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
				this.current.setClave(this.estaciones.toCode("0012020999"));
				this.current.setNivel(3L);
				actualizarChildren(1);
			} // if	
		loadCombos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
		String nodo                  = "";
		TcKeetPrototiposDto prototipo= null;
    try {
			if(this.attrs.get("idPrototipo")!=null && ((UISelectEntity)this.attrs.get("idPrototipo")).getKey()>0L){
				prototipo= (TcKeetPrototiposDto)DaoFactory.getInstance().findById(TcKeetPrototiposDto.class, ((UISelectEntity)this.attrs.get("idPrototipo")).getKey());
				this.current= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, prototipo.getIdEstacion());
				actualizarChildren(1);
			} // if
			else if(this.attrs.get("idEmpresa")!=null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>0L) {
				nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
				this.current= new TcKeetEstacionesDto();
				this.current.setClave(this.estaciones.toCode(nodo));
				this.current.setNivel(1L);
				actualizarChildren(1,2);
				this.current.setNivel(3L);
			} // else if
			else
				doInicio();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad

  public String doAccion(String accion) {
    EAccion eaccion        = null;
		String regresar        = null;
		Transaccion transaccion= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
			switch(eaccion){
				case REGISTRAR:
				  this.current=((TcKeetEstacionesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetEstacionesDto)this.attrs.get("seleccionado"));
					eaccion= EAccion.AGREGAR;
				case AGREGAR:
				case MODIFICAR:
				case CONSULTAR:
					regresar= "accion".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("accion", eaccion);      
					JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(eaccion.name()));      
					JsfBase.setFlashAttribute("idEstacion", eaccion.equals(EAccion.AGREGAR) ? -1L : ((TcKeetEstacionesDto) this.attrs.get("seleccionado")).getKey());
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/filtro");
					break;
				case SUBIR:
				case BAJAR:
				case ELIMINAR:
					transaccion= new Transaccion(new RegistroEstacion(((TcKeetEstacionesDto) this.attrs.get("seleccionado")).getKey()));
					if (transaccion.ejecutar(eaccion)){
					  actualizarChildren(0);
						JsfBase.addMessage("La estación se ".concat(eaccion.getTitle()).concat(" correctamente."));
					} // if
				break;
				case LISTAR:
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					this.current=((TcKeetEstacionesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetEstacionesDto)this.attrs.get("seleccionado"));
					regresar= "estructura".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("estacionProcess", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/filtro");
					break;
			} // swicth
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
	
	public List<TcKeetEstacionesDto> getVisitados() {
		List<TcKeetEstacionesDto> regresar=null;
		try {
			regresar= this.estaciones.toFather(((TcKeetEstacionesDto)this.attrs.get("seleccionado"))== null? this.current.getClave() : ((TcKeetEstacionesDto)this.attrs.get("seleccionado")).getClave());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return regresar;
	} // getVisitados

	protected void actualizarChildren(int nivel) throws Exception {
		actualizarChildren(nivel, 0);
	}
	
	protected void actualizarChildren(int nivel, int aumentarNivel) throws Exception {
		try {
			this.current        = ((TcKeetEstacionesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetEstacionesDto)this.attrs.get("seleccionado"));
			this.estacionesHijas= this.estaciones.toChildren(aumentarNivel, this.current.getClave(), this.current.getNivel().intValue()+nivel, 0);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
	} // actualizarChildren

	public void doActualizarChildren() {
		try {
			this.current= ((TcKeetEstacionesDto)this.attrs.get("seleccionado"));
			actualizarChildren(1);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // doActualizar

	public void doVisitado(TcKeetEstacionesDto estacionesDto) {
		try {
			this.current= estacionesDto;
			actualizarChildren(1);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // doVisitado
	
	public void doInicio() {
		JsfBase.setFlashAttribute("current", null);
		init();
	} // doInicio

	protected void loadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			params= new HashMap<>();
			columns= new ArrayList<>();		
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("empresas")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally				
	} // loadEmpresas
	
	
	public void doLoadPrototipos(){
		UISelectEntity cliente= null;
	  try {
			cliente = (UISelectEntity)this.attrs.get("cliente");
			if(cliente!= null && cliente.getKey()> 0L) 
			  this.attrs.put(Constantes.SQL_CONDICION, "id_cliente= ".concat(cliente.getKey().toString()));
			else
				this.attrs.put(Constantes.SQL_CONDICION, "id_estacion is not null");
      this.attrs.put("prototipos", UIEntity.seleccione("TcKeetPrototiposDto", "row", this.attrs, "nombre"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadPrototipos
	
	public void doLoadClientes() {
		UISelectEntity empresa= null;
	  try {
			empresa = (UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  this.attrs.put("sucursales", empresa.getKey());
			else
				this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	protected void loadCombos() {
		try {
			this.loadEmpresas();
			this.doLoadClientes();
			this.doLoadPrototipos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	public String doUpload() {
		JsfBase.setFlashAttribute("ikContratoLote", -1L);
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.PLANTILLAS.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/filtro");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
	
}