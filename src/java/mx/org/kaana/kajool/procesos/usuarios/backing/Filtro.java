package mx.org.kaana.kajool.procesos.usuarios.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.dto.TcJanalUsuariosDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EReporte;
import mx.org.kaana.kajool.enums.ETipoBusqueda;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.usuarios.beans.Usuario;
import mx.org.kaana.kajool.procesos.usuarios.reglas.CargaInformacionUsuarios;
import mx.org.kaana.kajool.procesos.usuarios.reglas.Transaccion;
import mx.org.kaana.kajool.procesos.usuarios.reglas.beans.CriteriosBusqueda;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 1/09/2015
 * @time 05:19:51 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
@Named(value = "kajoolUsuariosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = -1279553224860143822L;
  private final Long ESTATUS_ACTIVO = 1L;
  private CriteriosBusqueda criteriosBusqueda;
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaTermino() {
		return fechaTermino;
	}

	public void setFechaTermino(LocalDate fechaTermino) {
		this.fechaTermino = fechaTermino;
	}	

  public CriteriosBusqueda getCriteriosBusqueda() {
    return criteriosBusqueda;
  }

  @Override
  @PostConstruct
  protected void init() {
    this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
    CargaInformacionUsuarios carga = null;
    try {
      this.attrs.put("estatus", 2L);
      this.criteriosBusqueda = new CriteriosBusqueda();
      carga = new CargaInformacionUsuarios(getCriteriosBusqueda());
      carga.init();
			this.toLoadCatalog();
      this.attrs.put("isPermisoDelega", JsfBase.isAdmin());
			if(JsfBase.getFlashAttribute("idPersonaProcess")!= null){
				this.attrs.put("idPersonaProcess", JsfBase.getFlashAttribute("idPersonaProcess"));
				this.doLoad();
				this.attrs.put("idPersonaProcess", null);
			} // if
      this.attrs.put("perfiles", this.criteriosBusqueda.getListaPerfiles());
      this.attrs.put("idPerfil", UIBackingUtilities.toFirstKeySelectEntity(this.criteriosBusqueda.getListaPerfiles()));
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch
  }

	private void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
    } // try
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	

  /**
   * *
   * Recarga los datos segun la acic�n que se ejecut�.
   */
  private void recargarTablaDatos(ETipoBusqueda tipoBusqueda) {
    Map<String, Object> params    = new HashMap<>();
    List<Columna> columns         = new ArrayList<>();
    CargaInformacionUsuarios carga= null;
    try {
      carga = new CargaInformacionUsuarios(this.getCriteriosBusqueda());
      columns.add(new Columna("primerApellido", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("segundoApellido", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cuenta", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descPerfil", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("ultimoAcceso", EFormatoDinamicos.FECHA_HORA_CORTA));      
			StringBuilder sb= new StringBuilder();
      switch (tipoBusqueda) {
        case NOMBRE:
          sb.append("(").append(carga.busquedaPorNombre()).append(") and ");
          break;
        case PERFIL:
          sb.append("(").append(carga.busquedaPorPerfil()).append(") and ");
          break;
        case TODOS:
          sb.append("(").append(carga.busquedaPorPerfilNombre()).append(") and ");
          break;
        case SIN_CONDICION:
          break;
      } // switch
			if(!Cadena.isVacio(this.attrs.get("idPersonaProcess")))
				sb.append("tc_mantic_personas.id_persona=").append(this.attrs.get("idPersonaProcess")).append(" and ");
			if(!Cadena.isVacio(this.attrs.get("cuenta")))
				sb.append("(upper(tc_mantic_personas.cuenta) like '%").append(this.attrs.get("cuenta")).append("%') and ");
			if(!Cadena.isVacio(this.fechaInicio))
				sb.append("(date_format(tc_janal_usuarios.ultimo_acceso, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaInicio)).append("') and ");	
			if(!Cadena.isVacio(this.fechaTermino))
				sb.append("(date_format(tc_janal_usuarios.ultimo_acceso, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaTermino)).append("') and ");	
			if(!Cadena.isVacio(this.attrs.get("estatus")))
        if((Long)this.attrs.get("estatus")== 2)
				  sb.append("tc_janal_usuarios.activo in (0, 1, 2) and ");
        else
				  sb.append("tc_janal_usuarios.activo=").append((Long)this.attrs.get("estatus")).append(" and ");
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				sb.append("(tr_mantic_empresa_personal.id_empresa= ").append(this.attrs.get("idEmpresa")).append(") and ");
      if(sb.length()== 0)
				sb.append(Constantes.SQL_VERDADERO);
			else
			  sb.delete(sb.length()- 4, sb.length());
			params.put(Constantes.SQL_CONDICION, sb.toString());
      params.put("sortOrder", "order by tc_janal_perfiles.id_perfil, tc_mantic_personas.paterno, tc_mantic_personas.materno, tc_mantic_personas.nombres");
      this.lazyModel = new FormatCustomLazy("VistaUsuariosDto", "row", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }

  public void doActivarUsuario() {
    Entity seleccionado       = (Entity) this.attrs.get("seleccionado");
    Transaccion  transaccion  = null;
    TcJanalUsuariosDto usuario= null;
    try {
      usuario = new TcJanalUsuariosDto(seleccionado.getKey());
      usuario.setActivo(seleccionado.toLong("activo").equals(ESTATUS_ACTIVO)?0L:ESTATUS_ACTIVO);
      transaccion = new Transaccion(usuario);
      if (transaccion.ejecutar(EAccion.ACTIVAR)) 
        JsfBase.addMessage(usuario.getActivo().equals(ESTATUS_ACTIVO) ? "Se activ� el usuario con �xito." : "Se desactiv� el usuario con �xito.");
      this.doLoad();
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch  
  }

  public void doActivar() {
    Entity seleccionado = (Entity) this.attrs.get("seleccionado");
    StringBuilder mensaje = new StringBuilder();
    mensaje.append(seleccionado.toLong("activo").equals(ESTATUS_ACTIVO) ? "bloquear " : " activar ");
    mensaje.append(" la cuenta de acceso de [");
    mensaje.append(seleccionado.toString("cuenta"));
    mensaje.append("]");
    this.attrs.put("mensajeAlerta", mensaje);
  }

  public String doAccion(EAccion accion) {
    Entity seleccionado = (Entity) this.attrs.get("seleccionado");
    try {
      JsfBase.setFlashAttribute("accion", accion);
      JsfBase.setFlashAttribute("idUsuario", accion.equals(EAccion.MODIFICAR) || accion.equals(EAccion.CONSULTAR) ? seleccionado.toLong("idKeyUsuario") : null);
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch
    return "accion".concat(Constantes.REDIRECIONAR);
  }

  public String doAceptar() {
    Map<String, Object> params = new HashMap<>();
    params.put("idGrupo", Numero.getLong(JsfBase.seekParameter("idGrupo").toString()));
    params.put(Constantes.SQL_CONDICION, this.attrs.get("condicion"));
    JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new Usuario(EReporte.USUARIO, params)); // CARGAR EL REPORTE
    return "/Paginas/Reportes/generar";
  } // doAceptar

  public void doRecuperarInformacionDeUsuario() {
    Entity seleccionado = (Entity) this.attrs.get("seleccionado");
    this.attrs.put("nombre", seleccionado.toString("nombres"));
    this.attrs.put("perfil", seleccionado.toString("descPerfil"));
    //return null;
  }

  public void doEliminar() {
    Transaccion tx            = null;
    TcJanalUsuariosDto usuario= null;
    Entity seleccionado       = null;
    Map<String, Object> params= new HashMap<>();
    try {
      seleccionado = (Entity) this.attrs.get("seleccionado");
      usuario = new TcJanalUsuariosDto(seleccionado.getKey());
      tx = new Transaccion(usuario);
      if (tx.ejecutar(EAccion.ELIMINAR)) {
        params.put("elemento", "el usuario [".concat(seleccionado.toString("cuenta")).concat("]"));
        //JsfBase.addMessage(UIMessage.toMessage("correcto_eliminar_elemento", params));
        JsfBase.addMessage("Se elimino el usuario [".concat(seleccionado.toString("cuenta")).concat("]"));
      } // if
      else {
        JsfBase.addMessage("Error", "El usuario no puede ser eliminado debido a que tiene informaci�n asociada", ETipoMensaje.ERROR);
      } // else
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessage("Error", "Ocurri� un error al eliminar el registro del usuario".concat(e.getMessage()), ETipoMensaje.FATAL);
    } // catch
    finally {
      Methods.clean(params);
    }// 
  }

  @Override
  public void doLoad() {
    try {
      if (Cadena.isVacio(this.criteriosBusqueda.getNombre()) && this.criteriosBusqueda.getPerfil().getKey().equals(-1L))
        recargarTablaDatos(ETipoBusqueda.SIN_CONDICION);
			else 
        if (!Cadena.isVacio(this.criteriosBusqueda.getNombre()) && this.criteriosBusqueda.getPerfil().getKey().equals(-1L)) 
          recargarTablaDatos(ETipoBusqueda.NOMBRE);
        else 
          recargarTablaDatos(Cadena.isVacio(this.criteriosBusqueda.getNombre()) ? ETipoBusqueda.PERFIL: ETipoBusqueda.TODOS);
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch
  }

  public String doDelegarUsuario() {
    String regresar = null;
    Entity seleccionado = (Entity) this.attrs.get("seleccionado");
    try {
      if (Boolean.valueOf(this.attrs.get("validaDelega").toString())) {
        JsfBase.setFlashAttribute("idUsuario", seleccionado.getKey());
        regresar = "delegar";
      } // if
      else 
        JsfBase.addMessage("Delegar usuario", "No tienes permisos para delegar a otro usuario", ETipoMensaje.ERROR);
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch
    return regresar;
  }

  public String doDelegarMismoUsuario() {
    JsfBase.setFlashAttribute("idUsuario", JsfBase.getAutentifica().getPersona().getIdUsuario());
    return "delegar";
  }

  public void doReset() {
    Transaccion transaccion    = null;
    TcManticPersonasDto persona= null;
    Entity seleccionado        = null;
    try {
      seleccionado= (Entity) this.attrs.get("seleccionado");
      persona= new TcManticPersonasDto(seleccionado.toLong("idPersona"));
      persona.setCurp(seleccionado.toString("curp"));
      persona.setPaterno(seleccionado.toString("primerApellido"));
      transaccion= new Transaccion(persona);
      if (transaccion.ejecutar(EAccion.RESTAURAR)) 
        JsfBase.addMessage("La cuenta del usuario ".concat(seleccionado.toString("cuenta")).concat(" ya fue resetada con exito."));      
      else 
        JsfBase.addMessage("Error", "No se puede resetar la contrase�a del usuario", ETipoMensaje.ERROR);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessage("Error", "Ocurri� un error al resetear la contrase�a del usuario".concat(e.getMessage()), ETipoMensaje.FATAL);
    } // catch
  }
 
  public void doChangePerfil() {
    int index= this.criteriosBusqueda.getListaPerfiles().indexOf((UISelectEntity)this.attrs.get("idPerfil"));
    if(index>= 0)
      this.criteriosBusqueda.setPerfil(this.criteriosBusqueda.getListaPerfiles().get(index));
  }
  
}