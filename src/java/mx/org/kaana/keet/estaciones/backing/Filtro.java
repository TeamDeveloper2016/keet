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
			this.estacionesDto= new TcKeetEstacionesDto();
			this.estacionesDto.setNivel(1L);
			this.estacionesDto.setClave("");
			doLoad();
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
      columns.add(new Columna("domicilio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			if(this.estacionesDto.getClave().length()>0)
				this.estacionesDto.setClave(this.estacionesDto.getClave().substring(0, ((this.estacionesDto.getNivel().intValue()-1) * TAMANIO_NIVEL)+1));
      this.lazyModel = new FormatCustomLazy("VistaEstacionesDto", this.estacionesDto.toMap(), columns);
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
			regresar= "accion".concat(Constantes.REDIRECIONAR);
      eaccion = EAccion.valueOf(accion.toUpperCase());
			switch(eaccion){
				case AGREGAR:
				case MODIFICAR:
				case CONSULTAR:
					JsfBase.setFlashAttribute("accion", eaccion);      
					JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
					JsfBase.setFlashAttribute("idDesarrollo", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
					JsfBase.setFlashAttribute("estacionDto", this.estacionesDto);
					JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Desarrollos/filtro");
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
			for (int i=this.estacionesDto.getNivel().intValue()-1; i>0; i--) {
        this.attrs.put("clave", this.estacionesDto.getClave().substring(0, (TAMANIO_NIVEL*i)+1));
        this.attrs.put("nivel", this.estacionesDto.getNivel()-1L);
        TcKeetEstacionesDto dto=(TcKeetEstacionesDto) DaoFactory.getInstance().toEntity(TcKeetEstacionesDto.class,"TcKeetEstacionesDto", "byClaveNivel", this.attrs);
        if (dto!=null&&dto.getKey()!=-1L) {
          regresar.add(dto);
        } // if
      } // for
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
	
	
}