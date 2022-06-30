package mx.org.kaana.sakbe.db.dto;

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
@Table(name="tc_sakbe_maquinarias_insumos")
public class TcSakbeMaquinariasInsumosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="rendimiento")
  private Double rendimiento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria_insumo")
  private Long idMaquinariaInsumo;
  @Column (name="id_tipo_combustible")
  private Long idTipoCombustible;
  @Column (name="capacidad")
  private Double capacidad;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeMaquinariasInsumosDto() {
    this(new Long(-1L));
  }

  public TcSakbeMaquinariasInsumosDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcSakbeMaquinariasInsumosDto(Long idMaquinaria, Double rendimiento, Long idUsuario, String observaciones, Long idMaquinariaInsumo, Long idTipoCombustible, Double capacidad) {
    setIdMaquinaria(idMaquinaria);
    setRendimiento(rendimiento);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdMaquinariaInsumo(idMaquinariaInsumo);
    setIdTipoCombustible(idTipoCombustible);
    setCapacidad(capacidad);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdMaquinaria(Long idMaquinaria) {
    this.idMaquinaria = idMaquinaria;
  }

  public Long getIdMaquinaria() {
    return idMaquinaria;
  }

  public void setRendimiento(Double rendimiento) {
    this.rendimiento = rendimiento;
  }

  public Double getRendimiento() {
    return rendimiento;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdMaquinariaInsumo(Long idMaquinariaInsumo) {
    this.idMaquinariaInsumo = idMaquinariaInsumo;
  }

  public Long getIdMaquinariaInsumo() {
    return idMaquinariaInsumo;
  }

  public void setIdTipoCombustible(Long idTipoCombustible) {
    this.idTipoCombustible = idTipoCombustible;
  }

  public Long getIdTipoCombustible() {
    return idTipoCombustible;
  }

  public void setCapacidad(Double capacidad) {
    this.capacidad = capacidad;
  }

  public Double getCapacidad() {
    return capacidad;
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
  	return getIdMaquinariaInsumo();
  }

  @Override
  public void setKey(Long key) {
  	this.idMaquinariaInsumo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRendimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaInsumo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCapacidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("rendimiento", getRendimiento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idMaquinariaInsumo", getIdMaquinariaInsumo());
		regresar.put("idTipoCombustible", getIdTipoCombustible());
		regresar.put("capacidad", getCapacidad());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdMaquinaria(), getRendimiento(), getIdUsuario(), getObservaciones(), getIdMaquinariaInsumo(), getIdTipoCombustible(), getCapacidad(), getRegistro()
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
    regresar.append("idMaquinariaInsumo~");
    regresar.append(getIdMaquinariaInsumo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMaquinariaInsumo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasInsumosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMaquinariaInsumo()!= null && getIdMaquinariaInsumo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeMaquinariasInsumosDto other = (TcSakbeMaquinariasInsumosDto) obj;
    if (getIdMaquinariaInsumo() != other.idMaquinariaInsumo && (getIdMaquinariaInsumo() == null || !getIdMaquinariaInsumo().equals(other.idMaquinariaInsumo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaquinariaInsumo() != null ? getIdMaquinariaInsumo().hashCode() : 0);
    return hash;
  }

}


