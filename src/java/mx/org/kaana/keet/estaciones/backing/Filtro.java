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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.keet.estaciones.reglas.Transaccion;
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

@Named(value = "keetEstacionesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
	protected List<TcKeetEstacionesDto> hijos;
	protected List<TcKeetEstacionesDto> visitados;
	protected TcKeetEstacionesDto current;
	protected Estaciones estaciones;
  protected Reporte reporte;
  private List<Entity> children;

	public Reporte getReporte() {
		return reporte;
	}

	public List<TcKeetEstacionesDto> getHijos() {
		return hijos;
	}

	public void setHijos(List<TcKeetEstacionesDto> hijos) {
		this.hijos = hijos;
	}
	
	public List<TcKeetEstacionesDto> getVisitados() {
		return this.visitados;
	} // getVisitados

	public String getTitulo() {
		return this.current== null? "Nombre": this.estaciones.getNiveles().get(this.current.getNivel().intValue()).getDescripcion();
	} // getVisitados

  public List<Entity> getChildren() {
    return children;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
			this.visitados= new ArrayList<>();
			this.estaciones= new Estaciones();
			this.estaciones.cleanLevels();
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("estacionProcess")!= null){
				this.current= (TcKeetEstacionesDto)JsfBase.getFlashAttribute("estacionProcess");
				this.actualizarChildren(1);
			} // if
			else{
				this.current=new TcKeetEstacionesDto();
				this.current.setClave(this.estaciones.toCode("0012020999"));
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
        this.attrs.put("seleccionado", null);
				this.current= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, prototipo.getIdEstacion());
				this.actualizarChildren(1);
			} // if
			else if(this.attrs.get("idEmpresa")!=null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>0L) {
				nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
        this.attrs.put("seleccionado", null);
				this.current= new TcKeetEstacionesDto();
				this.current.setClave(this.estaciones.toCode(nodo));
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
    TcKeetEstacionesDto dto= null;
    try {
      if(this.attrs.get("seleccionado")== null)
        dto= this.current;
      else
        if(this.attrs.get("seleccionado") instanceof TcKeetEstacionesDto)
          dto= (TcKeetEstacionesDto)this.attrs.get("seleccionado");
        else 
          dto= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, ((Entity)this.attrs.get("seleccionado")).toLong("idEstacion"));
      eaccion = EAccion.valueOf(accion.toUpperCase());
			switch(eaccion) {
				case REGISTRAR:
				  this.current= dto;
					eaccion= EAccion.AGREGAR;
				case AGREGAR:
				case MODIFICAR:
				case CONSULTAR:
					regresar= "accion".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("isEstacion", Boolean.TRUE);
					JsfBase.setFlashAttribute("accion", eaccion);      
					JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(eaccion.name()));      
					JsfBase.setFlashAttribute("idEstacion", eaccion.equals(EAccion.AGREGAR) ? -1L : dto.getIdEstacion());
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/filtro");
					break;
				case SUBIR:
				case BAJAR:
				case ELIMINAR:
					transaccion= new Transaccion(new RegistroEstacion(dto.getIdEstacion()));
					if (transaccion.ejecutar(eaccion)){
					  this.actualizarChildren(0);
						JsfBase.addMessage("La estación se ".concat(eaccion.getTitle()).concat(" correctamente."));
					} // if
				break;
				case LISTAR:
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					this.current= dto;
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
	
	protected void actualizarChildren(int nivel) throws Exception {
		this.actualizarChildren(nivel, 0);
	}
	
	protected void actualizarChildren(int nivel, int incremento) throws Exception {
		try {
      TcKeetEstacionesDto estacion= null;
      if(this.attrs.get("seleccionado")== null)
        estacion= this.current;
      else
        if(this.attrs.get("seleccionado") instanceof TcKeetEstacionesDto)
          estacion= (TcKeetEstacionesDto)this.attrs.get("seleccionado");
        else 
          estacion= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, ((Entity)this.attrs.get("seleccionado")).toLong("idEstacion"));
			this.current= estacion;
			if((this.current.getNivel().intValue()+ nivel)<= (this.estaciones.getNiveles().size()- 1)) {
				if(this.current!= null) {
					Methods.clean(this.visitados);
					this.visitados= this.estaciones.toFather(this.current.getClave());
				} // if	
			  this.hijos= this.estaciones.toChildren(incremento, this.current.getClave(), this.current.getNivel().intValue()+ nivel, 0);
        Long idContrato= this.attrs.get("idContrato")== null? 0L: ((UISelectEntity)this.attrs.get("idContrato")).getKey();
        this.children= this.estaciones.toEntity(idContrato, incremento, this.current.getClave(), this.current.getNivel().intValue()+ nivel, 0);
			} // if
		} // try 
		catch (Exception e) {
			throw e;
		} // catch
	} // actualizarChildren

	public void doActualizarChildren() {
    try {
      TcKeetEstacionesDto estacion= null;
      if(this.attrs.get("seleccionado") instanceof TcKeetEstacionesDto)
        estacion= (TcKeetEstacionesDto)this.attrs.get("seleccionado");
      else 
        estacion= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, ((Entity)this.attrs.get("seleccionado")).toLong("idEstacion"));
      this.doVisitado(estacion);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // doActualizar

	public void doVisitado(TcKeetEstacionesDto estacion) {
		try {
      this.attrs.put("seleccionado", null);
			this.current= estacion;
			this.actualizarChildren(1);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // doVisitado
	
	public void doInicio() {
    this.attrs.put("seleccionado", null);
		JsfBase.setFlashAttribute("current", null);
		this.init();
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
  
  public void doReporte(String nombre) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
    Entity contratosLotes        = null;
    TcKeetEstacionesDto estacion = null;
    try {
      if(this.attrs.get("seleccionado")== null)
        estacion= this.current;
      else
        if(this.attrs.get("seleccionado") instanceof TcKeetEstacionesDto)
          estacion= (TcKeetEstacionesDto)this.attrs.get("seleccionado");
        else 
          estacion= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, ((Entity)this.attrs.get("seleccionado")).toLong("idEstacion"));
      params=new HashMap<>();
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      reporteSeleccion= EReportes.valueOf(nombre);
      params.put("filtroReporte", estacion== null? "%": estacion.getClave().substring(0,13).concat("%"));
      this.reporte= JsfBase.toReporte();
      contratosLotes = (Entity) DaoFactory.getInstance().toEntity("VistaReportesEstacionesDto", "manzanaLote", params);
      parametros= comunes.getComunes();
      if(reporteSeleccion.equals(EReportes.PROTOTIPOS))
        parametros.put("PROTOTIPO", estacion== null? "": estacion.getNombre().toLowerCase());
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