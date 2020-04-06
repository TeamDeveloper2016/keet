package mx.org.kaana.keet.estaciones.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.keet.estaciones.reglas.MotorBusqueda;
import mx.org.kaana.keet.estaciones.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetEstacionesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
	private TcKeetEstacionesDto estacionesDto;
	private int TAMANIO_NIVEL= 3; 
	

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("estacionProcess")!= null){
				this.estacionesDto= (TcKeetEstacionesDto)JsfBase.getFlashAttribute("estacionProcess");
				this.doLoad();
			} // if
			else
			  doInicio();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			if(this.estacionesDto.getClave().length()>0)
				this.attrs.put("clave", this.estacionesDto.getClave().substring(0, ((this.estacionesDto.getNivel().intValue()) * TAMANIO_NIVEL)));
			else
				this.attrs.put("clave", this.estacionesDto.getClave());
			this.attrs.put("nivel", this.estacionesDto.getNivel()+1L);
      this.lazyModel = new FormatCustomLazy("VistaEstacionesDto", this.attrs, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad

  public String doAccion(String accion) {
    EAccion eaccion        = null;
		String regresar        = null;
		Transaccion transaccion= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
			switch(eaccion){
				case REGISTRAR:
				  doSelectPadre();
					eaccion= EAccion.AGREGAR;
				case AGREGAR:
				case MODIFICAR:
				case CONSULTAR:
					regresar= "accion".concat(Constantes.REDIRECIONAR);
					JsfBase.setFlashAttribute("accion", eaccion);      
					JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(eaccion.name()));      
					JsfBase.setFlashAttribute("idEstacion", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
					JsfBase.setFlashAttribute("estacionPadre", this.estacionesDto);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estaciones/filtro");
					break;
				case SUBIR:
				case BAJAR:
				case ELIMINAR:
					transaccion= new Transaccion(new RegistroEstacion(((Entity) this.attrs.get("seleccionado")).getKey()));
					transaccion.ejecutar(eaccion);
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
			regresar= new ArrayList<>();
			if(this.estacionesDto!=null){
				for (int i=1; i<=this.estacionesDto.getNivel().intValue(); i++) {
					this.attrs.put("clave", this.estacionesDto.getClave().substring(0, (TAMANIO_NIVEL*i)));
					this.attrs.put("nivel", i);
					TcKeetEstacionesDto dto=(TcKeetEstacionesDto) DaoFactory.getInstance().toEntity(TcKeetEstacionesDto.class,"TcKeetEstacionesDto", "byClaveNivel", this.attrs);
					if (dto!=null&&dto.getKey()!=-1L) {
						regresar.add(dto);
					} // if
				} // for
			}// if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		return regresar;
	} // getVisitados
	
	public void doVisitado(TcKeetEstacionesDto menu) {
		try {
			this.estacionesDto=menu;
			doLoad();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // doVisitado

	public void doInicio() {
		try {
			this.estacionesDto= new TcKeetEstacionesDto();
			this.estacionesDto.setNivel(0L);
			this.estacionesDto.setClave("000000000000000");
			doLoad();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} //doInicio
	
	public void doSelectPadre() {
		try {
			if(((Entity)this.attrs.get("seleccionado")).toLong("nivel")<5){
				this.estacionesDto= new TcKeetEstacionesDto();
				this.estacionesDto.setNivel(((Entity)this.attrs.get("seleccionado")).toLong("nivel"));
				this.estacionesDto.setClave(((Entity)this.attrs.get("seleccionado")).toString("clave"));
				doLoad();
			} //
			else
			  JsfBase.addAlert("No existen mas niveles disponibles");
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} //doInicio
	

	
	
	
}