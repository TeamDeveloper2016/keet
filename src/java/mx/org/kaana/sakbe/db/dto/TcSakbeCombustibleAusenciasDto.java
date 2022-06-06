package mx.org.kaana.sakbe.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
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
@Table(name="tc_sakbe_combustible_ausencias")
public class TcSakbeCombustibleAusenciasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_combustible_ausente")
  private Long idCombustibleAusente;
  @Column (name="id_ausente")
  private Long idAusente;
  @Column (name="id_repartidor")
  private Long idRepartidor;
  @Column (name="limite")
  private LocalDate limite;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeCombustibleAusenciasDto() {
    this(new Long(-1L));
  }

  public TcSakbeCombustibleAusenciasDto(Long key) {
    this(null, new Long(-1L), null, null, LocalDate.now());
    setKey(key);
  }

  public TcSakbeCombustibleAusenciasDto(Long idUsuario, Long idCombustibleAusente, Long idAusente, Long idRepartidor, LocalDate limite) {
    setIdUsuario(idUsuario);
    setIdCombustibleAusente(idCombustibleAusente);
    setIdAusente(idAusente);
    setIdRepartidor(idRepartidor);
    setLimite(limite);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdCombustibleAusente(Long idCombustibleAusente) {
    this.idCombustibleAusente = idCombustibleAusente;
  }

  public Long getIdCombustibleAusente() {
    return idCombustibleAusente;
  }

  public void setIdAusente(Long idAusente) {
    this.idAusente = idAusente;
  }

  public Long getIdAusente() {
    return idAusente;
  }

  public void setIdRepartidor(Long idRepartidor) {
    this.idRepartidor = idRepartidor;
  }

  public Long getIdRepartidor() {
    return idRepartidor;
  }

  public void setLimite(LocalDate limite) {
    this.limite = limite;
  }

  public LocalDate getLimite() {
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
  	return getIdCombustibleAusente();
  }

  @Override
  public void setKey(Long key) {
  	this.idCombustibleAusente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustibleAusente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAusente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRepartidor());
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
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idCombustibleAusente", getIdCombustibleAusente());
		regresar.put("idAusente", getIdAusente());
		regresar.put("idRepartidor", getIdRepartidor());
		regresar.put("limite", getLimite());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdUsuario(), getIdCombustibleAusente(), getIdAusente(), getIdRepartidor(), getLimite(), getRegistro()
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
    regresar.append("idCombustibleAusente~");
    regresar.append(getIdCombustibleAusente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCombustibleAusente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeCombustibleAusenciasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCombustibleAusente()!= null && getIdCombustibleAusente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeCombustibleAusenciasDto other = (TcSakbeCombustibleAusenciasDto) obj;
    if (getIdCombustibleAusente() != other.idCombustibleAusente && (getIdCombustibleAusente() == null || !getIdCombustibleAusente().equals(other.idCombustibleAusente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCombustibleAusente() != null ? getIdCombustibleAusente().hashCode() : 0);
    return hash;
  }

}


