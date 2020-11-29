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
@Table(name="tc_keet_familias")
public class TcKeetFamiliasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_familia")
  private Long idFamilia;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="nombre")
  private String nombre;
  @Column (name="acronimo")
  private String acronimo;
  @Column (name="consecutivo")
  private Long consecutivo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetFamiliasDto() {
    this(new Long(-1L));
  }

  public TcKeetFamiliasDto(Long key) {
    this(null, new Long(-1L), null, null, null, 1L);
    setKey(key);
  }

  public TcKeetFamiliasDto(String descripcion, Long idFamilia, Long idUsuario, String nombre, String acronimo, Long consecutivo) {
    setDescripcion(descripcion);
    setIdFamilia(idFamilia);
    setIdUsuario(idUsuario);
    setNombre(nombre);
    this.acronimo= acronimo;
    this.consecutivo= consecutivo;
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdFamilia(Long idFamilia) {
    this.idFamilia = idFamilia;
  }

  public Long getIdFamilia() {
    return idFamilia;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public String getAcronimo() {
    return acronimo;
  }

  public void setAcronimo(String acronimo) {
    this.acronimo = acronimo;
  }

  public Long getConsecutivo() {
    return consecutivo;
  }

  public void setConsecutivo(Long consecutivo) {
    this.consecutivo = consecutivo;
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
  	return getIdFamilia();
  }

  @Override
  public void setKey(Long key) {
  	this.idFamilia = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdFamilia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcronimo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idFamilia", getIdFamilia());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("nombre", getNombre());
		regresar.put("acronimo", getAcronimo());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getDescripcion(), getIdFamilia(), getIdUsuario(), getNombre(), getRegistro(), getAcronimo(), getConsecutivo()
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
    regresar.append("idFamilia~");
    regresar.append(getIdFamilia());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdFamilia());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetFamiliasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdFamilia()!= null && getIdFamilia()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetFamiliasDto other = (TcKeetFamiliasDto) obj;
    if (getIdFamilia() != other.idFamilia && (getIdFamilia() == null || !getIdFamilia().equals(other.idFamilia))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdFamilia() != null ? getIdFamilia().hashCode() : 0);
    return hash;
  }

}


