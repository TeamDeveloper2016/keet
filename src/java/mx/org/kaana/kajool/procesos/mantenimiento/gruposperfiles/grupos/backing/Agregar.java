package mx.org.kaana.kajool.procesos.mantenimiento.gruposperfiles.grupos.backing;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date Aug 27, 2015
 *@time 6:44:29 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.kajool.db.dto.TcJanalGruposDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.JsfUtilities;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.mantenimiento.gruposperfiles.grupos.reglas.Transaccion;

@Named(value="kajoolMantenimientoGruposperfilesGruposAgregar")
@ViewScoped
public class Agregar extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID=-2042129872251205736L;

  @PostConstruct
	@Override
  protected void init() {
    Long idGrupo  = null;
	EAccion accion= null;
    try {
      accion= ((EAccion)JsfUtilities.getFlashAttribute("accion"));
      this.attrs.put("accion", accion);
      switch (accion) {
        case AGREGAR:
          this.attrs.put("dto", new TcJanalGruposDto());
        break;
        case MODIFICAR:
          idGrupo = (Long) JsfUtilities.getFlashAttribute("idGrupo");
          this.attrs.put("idGrupo", idGrupo);
          this.attrs.put("dto", DaoFactory.getInstance().findById(TcJanalGruposDto.class, idGrupo));
        break;
      } // switch
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch
   } //init

  public String doAgregar() {
    String regresar = null;
    try {
      ((TcJanalGruposDto) this.attrs.get("dto")).setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
      ((TcJanalGruposDto) this.attrs.get("dto")).setRegistro(LocalDateTime.now());
      Transaccion transaccion = new Transaccion((TcJanalGruposDto) (this.attrs.get("dto")));
      if (transaccion.ejecutar((EAccion) this.attrs.get("accion"))) {
        JsfBase.addMessage("La acci�n ".concat(((EAccion) this.attrs.get("accion")).getName()).concat(" grupo se realizo con exito"));
        regresar= "filtro".concat(Constantes.REDIRECIONAR);
      }// if
    } catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    }// catch
    return regresar;
  } // doAgregar
}
