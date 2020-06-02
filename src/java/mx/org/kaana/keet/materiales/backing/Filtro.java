package mx.org.kaana.keet.materiales.backing;

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
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetMaterialesDto;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.keet.estaciones.reglas.Transaccion;
import mx.org.kaana.keet.materiales.reglas.Materiales;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;

@Named(value = "keetMaterialesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
	private List<TcKeetMaterialesDto> hijos;
	private List<TcKeetMaterialesDto> visitados;
	protected TcKeetMaterialesDto current;
	protected Materiales materiales;

	public List<TcKeetMaterialesDto> getHijos() {
		return hijos;
	}

	public void setHijos(List<TcKeetMaterialesDto> hijos) {
		this.hijos = hijos;
	}
	
	public List<TcKeetMaterialesDto> getVisitados() {
		return this.visitados;
	} // getVisitados

	public String getTitulo() {
		return this.current== null? "Nombre": this.materiales.getNiveles().get(this.current.getNivel().intValue()).getDescripcion();
	} // getVisitados

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.visitados= new ArrayList<>();
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.materiales= new Materiales();
			this.materiales.cleanLevels();
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("estacionProcess")!= null){
				this.current= (TcKeetMaterialesDto)JsfBase.getFlashAttribute("estacionProcess");
				this.actualizarChildren(1);
			} // if
			else{
				this.current=new TcKeetMaterialesDto();
				this.current.setClave("");
				this.current.setNivel(1L);
				this.actualizarChildren(0, 3);
				this.current.setNivel(3L);
			} // if	
		  this.loadCatalogs();
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
			  nodo= this.materiales.toCodeByIdContrato(lote.getIdContrato());
				this.current=new TcKeetMaterialesDto();
				this.current.setClave(this.materiales.toCode(nodo.concat(lote.getOrden().toString())));
				this.current.setNivel(4L);
				this.actualizarChildren(1);
			} // if
			else if(this.attrs.get("contrato")!=null && ((UISelectEntity)this.attrs.get("contrato")).getKey()>0L) {
				nodo= this.materiales.toCodeByIdContrato(((UISelectEntity)this.attrs.get("contrato")).getKey());
				this.current= new TcKeetMaterialesDto();
				this.current.setClave(nodo);
				this.current.setNivel(3L);
				this.actualizarChildren(1);
			} // else if
			else if(this.attrs.get("idEmpresa")!=null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>0L) {
					nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
					this.current= new TcKeetMaterialesDto();
					this.current.setClave(this.materiales.toCode(nodo));
					this.current.setNivel(1L);
					this.actualizarChildren(1,2);
					this.current.setNivel(3L);
				} // else if
				else
					this.doInicio();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
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
				  this.current=((TcKeetMaterialesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetMaterialesDto)this.attrs.get("seleccionado"));
					eaccion= EAccion.AGREGAR;
				case AGREGAR:
				case MODIFICAR:
				case CONSULTAR:
					regresar= "accion".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("accion", eaccion);      
					JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(eaccion.name()));      
					JsfBase.setFlashAttribute("idMaterial", eaccion.equals(EAccion.AGREGAR) ? -1L : ((TcKeetMaterialesDto) this.attrs.get("seleccionado")).getKey());
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Materiales/filtro");
					break;
				case SUBIR:
				case BAJAR:
				case ELIMINAR:
					transaccion= new Transaccion(new RegistroEstacion(((TcKeetMaterialesDto) this.attrs.get("seleccionado")).getKey()));
					if (transaccion.ejecutar(eaccion)){
					  this.actualizarChildren(0);
						JsfBase.addMessage("La estación se ".concat(eaccion.getTitle()).concat(" correctamente."));
					} // if
				break;
				case LISTAR:
					JsfBase.setFlashAttribute("estacionPadre", this.current);
					this.current=((TcKeetMaterialesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetMaterialesDto)this.attrs.get("seleccionado"));
					regresar= "estructura".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("estacionProcess", this.current);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/contrato");
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
			if((this.current.getNivel().intValue()+ nivel)<= this.materiales.getNiveles().size()) {
				if(this.current!= null) {
					Methods.clean(this.visitados);
					this.visitados= this.materiales.toFather(this.current.getClave());
				} // if	
			  this.hijos= this.materiales.toChildren(incremento, this.current.getClave(), this.current.getNivel().intValue()+ nivel, 0);
			} // if
		} // try 
		catch (Exception e) {
			throw e;
		} // catch
	} // actualizarChildren

	public void doActualizarChildren() {
		this.doVisitado(((TcKeetMaterialesDto)this.attrs.get("seleccionado")));
	} // doActualizar

	public void doVisitado(TcKeetMaterialesDto estacion) {
		try {
			this.current= estacion;
			this.actualizarChildren(1);
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
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params = new HashMap<>();
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
	
	protected void loadCatalogs() {
		try {
			this.loadEmpresas();
			this.doLoadContratos();
			this.doLoadLotes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCatalogs
	
	public String doUpload() {
		JsfBase.setFlashAttribute("ikContratoLote", -1L);
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.MATERIALES.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Materiales/filtro");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}

	public String doUploadIndividual() {
		Long ikContratoLote= -1L;
		if(this.attrs.get("lote")!= null && ((UISelectEntity)this.attrs.get("lote")).getKey()> 0L) 
			ikContratoLote= ((UISelectEntity)this.attrs.get("lote")).getKey();
		JsfBase.setFlashAttribute("ikContratoLote", ikContratoLote);
		JsfBase.setFlashAttribute("idTipoMasivo", ECargaMasiva.MATERIALES.getId());
		JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Materiales/filtro");
		return "/Paginas/Keet/Estaciones/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
	
}