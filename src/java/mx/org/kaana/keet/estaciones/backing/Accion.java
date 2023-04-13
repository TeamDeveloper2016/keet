package mx.org.kaana.keet.estaciones.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.estaciones.reglas.Transaccion;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetEstacionesAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = -7098585471715712054L;

	private RegistroEstacion estacion;
	private EAccion accion;
  private TcKeetEstacionesDto pivote;

	public RegistroEstacion getEstacion() {
		return estacion;
	}

	public void setEstacion(RegistroEstacion estacion) {
		this.estacion = estacion;
	}	
  
  public Boolean getIsAgregar() {
    return EAccion.AGREGAR.equals(this.accion) || EAccion.REGISTRAR.equals(this.accion);
  }
	
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= (EAccion)JsfBase.getFlashAttribute("accion");
      this.pivote= JsfBase.getFlashAttribute("pivote")== null? new TcKeetEstacionesDto(): (TcKeetEstacionesDto)JsfBase.getFlashAttribute("pivote");
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      this.attrs.put("isEstacion", JsfBase.getFlashAttribute("isEstacion")== null? Boolean.TRUE: JsfBase.getFlashAttribute("isEstacion"));
      this.attrs.put("idEstacion", JsfBase.getFlashAttribute("idEstacion"));
      this.attrs.put("padre", JsfBase.getFlashAttribute("estacionPadre"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());      
      this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.loadCatalogos();
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		try {
			this.attrs.put("clientes", UIEntity.seleccione("VistaEmpaqueUnidadDto", "row", this.attrs, "empaque"));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
	} // loadCatalogos
	
  public void doLoad() {
    try {
      switch (this.accion) {
        case REGISTRAR:											
        case AGREGAR:											
          this.estacion= new RegistroEstacion();
					this.estacion.getEstacion().setClave(((TcKeetEstacionesDto)this.attrs.get("padre")).getClave());
					this.estacion.getEstacion().setNivel(((TcKeetEstacionesDto)this.attrs.get("padre")).getNivel()); // nivel hijo
          this.estacion.getEstacion().setCantidad(1D);
          this.estacion.getEstacion().setCosto(0D);
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					this.estacion= new RegistroEstacion((Long)this.attrs.get("idEstacion"));
          break;
      } // switch
      this.toLoadRubrosConceptos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    UISelectEntity partida = null;
    UISelectEntity rubro   = null;
    try {			
      List<UISelectEntity>partidas= (List<UISelectEntity>)this.attrs.get("partidas");
      if(partidas!= null)
        partida= partidas.get(partidas.indexOf((UISelectEntity)this.attrs.get("idPartida")));
      if(Objects.equals(this.accion, EAccion.AGREGAR)) {
        List<UISelectEntity> rubros= (List<UISelectEntity>)this.attrs.get("rubros");
        if(rubros!= null) { 
          rubro= rubros.get(rubros.indexOf((UISelectEntity)this.attrs.get("idRubro")));
          this.estacion.getEstacion().setCodigo(rubro.toString("codigo"));
          this.estacion.getEstacion().setNombre(rubro.toString("nombre"));
          this.estacion.getEstacion().setDescripcion(rubro.toString("descripcion"));
        } // if  
      } // if  
			transaccion = new Transaccion(this.estacion, partida);
			if(transaccion.ejecutar(this.accion)) {
				JsfBase.setFlashAttribute("estacionProcess", ((TcKeetEstacionesDto)this.attrs.get("padre")));
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el estación de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el proyecto", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("estacionProcess", ((TcKeetEstacionesDto)this.attrs.get("padre")));
    return (String) this.attrs.get("retorno");
  } // doCancelar	

  private void toLoadRubrosConceptos() {
    List<Columna> columns       = new ArrayList<>();    
    Map<String, Object> params  = new HashMap<>();
    List<UISelectEntity>partidas= null;
    List<UISelectEntity>rubros  = null;
    Estaciones estaciones       = null;
    try {      
      estaciones= new Estaciones();
      params.put("clave", estaciones.toKey(this.pivote.getClave(), this.pivote.getNivel().intValue()));      
      params.put("nivel", this.pivote.getNivel()+ 1);      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
    	partidas= UIEntity.seleccione("VistaEstacionesDto", "partidas", params, columns, Constantes.SQL_TODOS_REGISTROS, "codigo");
      this.attrs.put("partidas", partidas);
      this.attrs.put("idPartida", partidas!= null? UIBackingUtilities.toFirstKeySelectEntity(partidas): new UISelectEntity(-1L));
      params.put("clave", estaciones.toKey(this.pivote.getClave(), this.pivote.getNivel().intValue()- 1));      
      params.put("nivel", Objects.equals(4L, this.pivote.getNivel())? this.pivote.getNivel()+ 2: this.pivote.getNivel()+ 1);      
    	rubros= UIEntity.seleccione("VistaEstacionesDto", "rubros", params, columns, Constantes.SQL_TODOS_REGISTROS, "codigo");
      this.attrs.put("rubros", rubros);
      attrs.put("idRubro", rubros!= null? UIBackingUtilities.toFirstKeySelectEntity(rubros): new UISelectEntity(-1L));
      if(Objects.equals(((TcKeetEstacionesDto)this.attrs.get("padre")).getNivel(), 4L)) {
        
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }
  
  public void doUpdateLeyenda() {
    List<UISelectEntity>rubros= (List<UISelectEntity>)this.attrs.get("rubros");
    if(rubros!= null) {
      this.attrs.put("idRubro", rubros.get(rubros.indexOf((UISelectEntity)this.attrs.get("idRubro"))));
      this.estacion.getEstacion().setDescripcion(((UISelectEntity)this.attrs.get("idRubro")).toString("descripcion"));
    } // if  
  }

}