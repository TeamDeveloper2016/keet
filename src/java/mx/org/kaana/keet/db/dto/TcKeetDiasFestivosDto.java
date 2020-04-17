package mx.org.kaana.keet.db.dto;

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
@Table(name="tc_keet_dias_festivos")
public class TcKeetDiasFestivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="factor")
  private Double factor;
  @Column (name="dia")
  private LocalDate dia;
  @Column (name="id_oficial")
  private Long idOficial;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_dia_festivo")
  private Long idDiaFestivo;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetDiasFestivosDto() {
    this(new Long(-1L));
  }

  public TcKeetDiasFestivosDto(Long key) {
    this(null, null, null, 2D, LocalDate.now(), 1L, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetDiasFestivosDto(String descripcion, Long idUsuario, Long idEmpresa, Double factor, LocalDate dia, Long idOficial, Long idDiaFestivo, Long ejercicio) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setIdEmpresa(idEmpresa);
    setFactor(factor);
    setDia(dia);
    setIdOficial(idOficial);
    setIdDiaFestivo(idDiaFestivo);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setFactor(Double factor) {
    this.factor = factor;
  }

  public Double getFactor() {
    return factor;
  }

  public void setDia(LocalDate dia) {
    this.dia = dia;
  }

  public LocalDate getDia() {
    return dia;
  }

  public void setIdOficial(Long idOficial) {
    this.idOficial = idOficial;
  }

  public Long getIdOficial() {
    return idOficial;
  }

  public void setIdDiaFestivo(Long idDiaFestivo) {
    this.idDiaFestivo = idDiaFestivo;
  }

  public Long getIdDiaFestivo() {
    return idDiaFestivo;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
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
  	return getIdDiaFestivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idDiaFestivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOficial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDiaFestivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("factor", getFactor());
		regresar.put("dia", getDia());
		regresar.put("idOficial", getIdOficial());
		regresar.put("idDiaFestivo", getIdDiaFestivo());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuario(), getIdEmpresa(), getFactor(), getDia(), getIdOficial(), getIdDiaFestivo(), getEjercicio(), getRegistro()
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
    regresar.append("idDiaFestivo~");
    regresar.append(getIdDiaFestivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdDiaFestivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetDiasFestivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdDiaFestivo()!= null && getIdDiaFestivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetDiasFestivosDto other = (TcKeetDiasFestivosDto) obj;
    if (getIdDiaFestivo() != other.idDiaFestivo && (getIdDiaFestivo() == null || !getIdDiaFestivo().equals(other.idDiaFestivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdDiaFestivo() != null ? getIdDiaFestivo().hashCode() : 0);
    return hash;
  }
}