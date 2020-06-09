package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
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
@Table(name="tc_keet_cajas_chicas")
public class TcKeetCajasChicasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_activa")
  private Long idActiva;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_caja_chica")
  private Long idCajaChica;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="limite")
  private Double limite;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetCajasChicasDto() {
    this(new Long(-1L));
  }

  public TcKeetCajasChicasDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetCajasChicasDto(Long idActiva, Long idCajaChica, Long idDesarrollo, Long idUsuario, Double limite) {
    setIdActiva(idActiva);
    setIdCajaChica(idCajaChica);
    setIdDesarrollo(idDesarrollo);
    setIdUsuario(idUsuario);
    setLimite(limite);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdActiva(Long idActiva) {
    this.idActiva = idActiva;
  }

  public Long getIdActiva() {
    return idActiva;
  }

  public void setIdCajaChica(Long idCajaChica) {
    this.idCajaChica = idCajaChica;
  }

  public Long getIdCajaChica() {
    return idCajaChica;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setLimite(Double limite) {
    this.limite = limite;
  }

  public Double getLimite() {
    return limite;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdCajaChica();
  }

  @Override
  public void setKey(Long key) {
  	this.idCajaChica = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdActiva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChica());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idActiva", getIdActiva());
		regresar.put("idCajaChica", getIdCajaChica());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("limite", getLimite());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdActiva(), getIdCajaChica(), getIdDesarrollo(), getIdUsuario(), getLimite(), getRegistro()
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
    regresar.append("idCajaChica~");
    regresar.append(getIdCajaChica());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCajaChica());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetCajasChicasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCajaChica()!= null && getIdCajaChica()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetCajasChicasDto other = (TcKeetCajasChicasDto) obj;
    if (getIdCajaChica() != other.idCajaChica && (getIdCajaChica() == null || !getIdCajaChica().equals(other.idCajaChica))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCajaChica() != null ? getIdCajaChica().hashCode() : 0);
    return hash;
  }

}


