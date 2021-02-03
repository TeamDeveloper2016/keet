package mx.org.kaana.kajool.procesos.mantenimiento.mensajes.usuarios.backing;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 22/09/2015
 * @time 01:14:37 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.kajool.db.dto.TcJanalMensajesDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.JsfUtilities;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.db.dto.TrJanalMensajesUsuariosDto;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETiposMensajes;
import mx.org.kaana.kajool.procesos.mantenimiento.mensajes.usuarios.reglas.Transaccion;

@Named(value="kajoolMensajesUsuariosAgregar")
@ViewScoped
public class Agregar extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 7918385112923122551L;

  @Override
  @PostConstruct
  protected void init() {
    Entity datosUsuario = null;
    this.attrs.put("deshabilitar", false);
    try {
      datosUsuario = ((Entity) JsfUtilities.getFlashAttribute("datosUsuario"));
      this.attrs.put("idUsuario", datosUsuario.toLong("idKey"));
      this.attrs.put("usuario", datosUsuario.toString("usuario"));
      this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("prioridades", UISelect.seleccione("TcJanalPrioridadesDto", this.attrs, "descripcion", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("dto", new TcJanalMensajesDto());
      this.attrs.put("dtoUsuario", new TrJanalMensajesUsuariosDto());
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch
  } //init

  public String doAceptar() {
    String regresar        = "detalleUsuario";
    TcJanalMensajesDto dto = null;
    try {
      dto = (TcJanalMensajesDto) this.attrs.get("dto");
      dto.setIdPrioridad(Long.valueOf(this.attrs.get("idPrioridad").toString()));
      dto.setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
      dto.setIdUsuarioModifica(JsfBase.getAutentifica().getPersona().getIdUsuario());
      dto.setRegistro(LocalDateTime.now());
      dto.setIdTipoMensaje(ETiposMensajes.USUARIO.getKey());
      dto.setFechaRepite(LocalDate.now());
      dto.setActualizacion("n");
      Transaccion transaccion = new Transaccion(dto, Numero.getLong(this.attrs.get("idUsuario").toString()), JsfBase.getAutentifica().getPersona().getIdUsuario());
      if (transaccion.ejecutar(EAccion.AGREGAR)) 
        JsfUtilities.addMessage("Se realizó la operación de forma correcta");
      else 
        JsfUtilities.addMessage("Ocurrio un error en la operación");
    } //try
    catch (Exception e) {
      JsfBase.addMessageError(e);
      Error.mensaje(e);
    } // catch
    return regresar.concat(Constantes.REDIRECIONAR);
  } //doAceptar
}
