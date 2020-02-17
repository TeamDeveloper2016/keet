package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Lob;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2016
 *@time 11:58:22 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Entity
@Table(name="tc_keet_proyectos_lotes")
public class TcKeetProyectosLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="manzana")
  private String manzana;
  @Column (name="id_proyecto_estatus")
  private Long idProyectoEstatus;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="lote")
  private Long lote;
  @Column (name="id_contrato_proyecto")
  private Long idContratoProyecto;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proyecto_lote")
  private Long idProyectoLote;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetProyectosLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetProyectosLotesDto(Long key) {
    this(null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetProyectosLotesDto(String manzana, Long idProyectoEstatus, Long idUsuario, Long lote, Long idContratoProyecto, Long idProyectoLote) {
    setManzana(manzana);
    setIdProyectoEstatus(idProyectoEstatus);
    setIdUsuario(idUsuario);
    setLote(lote);
    setIdContratoProyecto(idContratoProyecto);
    setIdProyectoLote(idProyectoLote);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setManzana(String manzana) {
    this.manzana = manzana;
  }

  public String getManzana() {
    return manzana;
  }

  public void setIdProyectoEstatus(Long idProyectoEstatus) {
    this.idProyectoEstatus = idProyectoEstatus;
  }

  public Long getIdProyectoEstatus() {
    return idProyectoEstatus;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setLote(Long lote) {
    this.lote = lote;
  }

  public Long getLote() {
    return lote;
  }

  public void setIdContratoProyecto(Long idContratoProyecto) {
    this.idContratoProyecto = idContratoProyecto;
  }

  public Long getIdContratoProyecto() {
    return idContratoProyecto;
  }

  public void setIdProyectoLote(Long idProyectoLote) {
    this.idProyectoLote = idProyectoLote;
  }

  public Long getIdProyectoLote() {
    return idProyectoLote;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdProyectoLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idProyectoLote = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getManzana());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoProyecto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("manzana", getManzana());
		regresar.put("idProyectoEstatus", getIdProyectoEstatus());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("lote", getLote());
		regresar.put("idContratoProyecto", getIdContratoProyecto());
		regresar.put("idProyectoLote", getIdProyectoLote());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getManzana(), getIdProyectoEstatus(), getIdUsuario(), getLote(), getIdContratoProyecto(), getIdProyectoLote(), getRegistro()
    };
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("|");
    regresar.append("idProyectoLote~");
    regresar.append(getIdProyectoLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProyectoLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProyectosLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProyectoLote()!= null && getIdProyectoLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProyectosLotesDto other = (TcKeetProyectosLotesDto) obj;
    if (getIdProyectoLote() != other.idProyectoLote && (getIdProyectoLote() == null || !getIdProyectoLote().equals(other.idProyectoLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProyectoLote() != null ? getIdProyectoLote().hashCode() : 0);
    return hash;
  }

}


