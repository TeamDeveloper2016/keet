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
@Table(name="tc_keet_puestos_departamentos")
public class TcKeetPuestosDepartamentosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_puesto")
  private Long idPuesto;
  @Column (name="id_departamento")
  private Long idDepartamento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  @Column (name="id_puesto_agrupacion")
  private Long idPuestoAgrupacion;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPuestosDepartamentosDto() {
    this(new Long(-1L));
  }

  public TcKeetPuestosDepartamentosDto(Long key) {
    this(null, null, null, null);
    setKey(key);
  }

  public TcKeetPuestosDepartamentosDto(Long idPuesto, Long idDepartamento, Long idUsuario, Long idPuestoAgrupacion) {
    setIdPuesto(idPuesto);
    setIdDepartamento(idDepartamento);
    setIdUsuario(idUsuario);
    setIdPuestoAgrupacion(idPuestoAgrupacion);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPuesto(Long idPuesto) {
    this.idPuesto = idPuesto;
  }

  public Long getIdPuesto() {
    return idPuesto;
  }

  public void setIdDepartamento(Long idDepartamento) {
    this.idDepartamento = idDepartamento;
  }

  public Long getIdDepartamento() {
    return idDepartamento;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdPuestoAgrupacion(Long idPuestoAgrupacion) {
    this.idPuestoAgrupacion = idPuestoAgrupacion;
  }

  public Long getIdPuestoAgrupacion() {
    return idPuestoAgrupacion;
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
  	return getIdPuestoAgrupacion();
  }

  @Override
  public void setKey(Long key) {
  	this.idPuestoAgrupacion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPuesto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDepartamento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuestoAgrupacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPuesto", getIdPuesto());
		regresar.put("idDepartamento", getIdDepartamento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPuestoAgrupacion", getIdPuestoAgrupacion());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPuesto(), getIdDepartamento(), getIdUsuario(), getIdPuestoAgrupacion(), getRegistro()
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
    regresar.append("idPuestoDepartamento~");
    regresar.append(getIdPuestoAgrupacion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPuestoAgrupacion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPuestosDepartamentosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPuestoAgrupacion()!= null && getIdPuestoAgrupacion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPuestosDepartamentosDto other = (TcKeetPuestosDepartamentosDto) obj;
    if (getIdPuestoAgrupacion()!= other.idPuestoAgrupacion && (getIdPuestoAgrupacion()== null || !getIdPuestoAgrupacion().equals(other.idPuestoAgrupacion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPuestoAgrupacion()!= null ? getIdPuestoAgrupacion().hashCode() : 0);
    return hash;
  }

}


