package mx.org.kaana.keet.controles.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.db.dto.TcKeetControlesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.keet.controles.reglas.Controles;
import mx.org.kaana.keet.controles.beans.RegistroControl;
import mx.org.kaana.keet.controles.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;

@Named(value = "keetControlesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
	private List<TcKeetControlesDto> hijos;
	private List<TcKeetControlesDto> visitados;
	protected TcKeetControlesDto current;
	protected Controles controles;
  protected Reporte reporte;

	public Reporte getReporte() {
		return reporte;
	}

	public List<TcKeetControlesDto> getHijos() {
		return hijos;
	}

	public void setHijos(List<TcKeetControlesDto> hijos) {
		this.hijos = hijos;
	}
	
	public List<TcKeetControlesDto> getVisitados() {
		return this.visitados;
	} // getVisitados

	public String getTitulo() {
		return this.current== null? "Nombre": this.controles.getNiveles().get(this.current.getNivel().intValue()).getDescripcion();
	} // getVisitados

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.visitados= new ArrayList<>();
			this.controles= new Controles();
			this.controles.cleanLevels();
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("estacionProcess")!= null) {
				this.current= (TcKeetControlesDto)JsfBase.getFlashAttribute("estacionProcess");
				this.actualizarChildren(1);
			} // if
      else {
				this.current=new TcKeetControlesDto();
				this.current.setClave(this.controles.toCode("0012020999"));
				this.current.setNivel(3L);
				this.actualizarChildren(1);
			} // if	
	  	this.loadCombos();
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
			if(this.attrs.get("idPrototipo")!= null && ((UISelectEntity)this.attrs.get("idPrototipo")).getKey()> 0L) {
				prototipo= (TcKeetPrototiposDto)DaoFactory.getInstance().findById(TcKeetPrototiposDto.class, ((UISelectEntity)this.attrs.get("idPrototipo")).getKey());
				this.current= (TcKeetControlesDto)DaoFactory.getInstance().findById(TcKeetControlesDto.class, prototipo.getIdEstacion());
				actualizarChildren(1);
			} // if
			else 
        if(this.attrs.get("idEmpresa")!= null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()> 0L) {
          nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
          this.current= new TcKeetControlesDto();
          this.current.setClave(this.controles.toCode(nodo));
          this.current.setNivel(1L);
          this.actualizarChildren(1,2);
          this.current.setNivel(3L);
        } // else if
        else
          this.doInicio();
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
				  this.current=((TcKeetControlesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetControlesDto)this.attrs.get("seleccionado"));
					eaccion= EAccion.AGREGAR;
				case AGREGAR:
				case MODIFICAR:
				case CONSULTAR:
					regresar= "accion".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("accion", eaccion);      
					JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(eaccion.name()));      
					JsfBase.setFlashAttribute("idControl", eaccion.equals(EAccion.AGREGAR) ? -1L : ((TcKeetControlesDto) this.attrs.get("seleccionado")).getKey());
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/filtro");
					break;
				case SUBIR:
				case BAJAR:
				case ELIMINAR:
					transaccion= new Transaccion(new RegistroControl(((TcKeetControlesDto) this.attrs.get("seleccionado")).getKey()));
					if (transaccion.ejecutar(eaccion)){
					  actualizarChildren(0);
						JsfBase.addMessage("La estaci�n se ".concat(eaccion.getTitle()).concat(" correctamente."));
					} // if
				break;
				case LISTAR:
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					this.current=((TcKeetControlesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetControlesDto)this.attrs.get("seleccionado"));
					regresar= "estructura".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("estacionProcess", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/filtro");
					break;
			} // swicth
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
	
	protected void actualizarChildren(int nivel) throws Exception {
		actualizarChildren(nivel, 0);
	}
	
	protected void actualizarChildren(int nivel, int incremento) throws Exception {
		try {
			this.current= ((TcKeetControlesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetControlesDto)this.attrs.get("seleccionado"));
			if((this.current.getNivel().intValue()+ nivel)<= (this.controles.getNiveles().size()- 1)) {
				if(this.current!= null) {
					Methods.clean(this.visitados);
					this.visitados= this.controles.toFather(this.current.getClave());
				} // if	
			  this.hijos= this.controles.toChildren(incremento, this.current.getClave(), this.current.getNivel().intValue()+nivel, 0);
			} // if
		} // try // try
		catch (Exception e) {
			throw e;
		} // catch
	} // actualizarChildren

	public void doActualizarChildren() {
		this.doVisitado(((TcKeetControlesDto)this.attrs.get("seleccionado")));
	} // doActualizar

	public void doVisitado(TcKeetControlesDto estacion) {
		try {
			this.current= estacion;
			actualizarChildren(1);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // doVisitado
	
	public void doInicio() {
		JsfBase.setFlashAttribute("current", null);
		this.init();
	} // doInicio

	protected void loadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
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
	
	public void doLoadPrototipos() {
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
	} 
	
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
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.CONTROLES.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/filtro");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
  
  public void doReporte(String nombre) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = new HashMap<>();
    Parametros comunes           = null;
    Entity contratosLotes        = null;
		try {		  
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      reporteSeleccion= EReportes.valueOf(nombre);
      params.put("filtroReporte",((TcKeetControlesDto)this.attrs.get("seleccionado")).getClave()!= null? ((TcKeetControlesDto)this.attrs.get("seleccionado")).getClave().substring(0,13).concat("%"):"%");
      this.reporte= JsfBase.toReporte();
      contratosLotes = (Entity) DaoFactory.getInstance().toEntity("VistaReportesControles", "manzanaLote", params);
      parametros= comunes.getComunes();
      if(reporteSeleccion.equals(EReportes.CONTROLES))
        parametros.put("PROTOTIPO", ((TcKeetControlesDto)this.attrs.get("seleccionado")).getNombre().toLowerCase());
      else{
        parametros.put("MZA", contratosLotes.toString("manzana"));
        parametros.put("LOTE", contratosLotes.toString("lote"));
        parametros.put("PROTOTIPO", "");
      }
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      if(doVerificarReporte())
        this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte 
	
	public boolean doVerificarReporte() {
    boolean regresar = false;
		if(this.reporte.getTotal()> 0L) {
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");	
      regresar = true;
    }
		else {
			UIBackingUtilities.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
      regresar = false;
		} // else
    return regresar;
	} // doVerificarReporte	
	
}