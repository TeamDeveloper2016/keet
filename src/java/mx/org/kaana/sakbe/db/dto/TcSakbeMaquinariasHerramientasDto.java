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
@Table(name="tc_sakbe_maquinarias_herramientas")
public class TcSakbeMaquinariasHerramientasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="horas")
  private Double horas;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria_herramienta")
  private Long idMaquinariaHerramienta;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_herramienta")
  private Long idHerramienta;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeMaquinariasHerramientasDto() {
    this(new Long(-1L));
  }

  public TcSakbeMaquinariasHerramientasDto(Long key) {
    this(null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcSakbeMaquinariasHerramientasDto(Long idMaquinaria, Double horas, Long idMaquinariaHerramienta, Long idUsuario, String observaciones, Long idHerramienta) {
    setIdMaquinaria(idMaquinaria);
    setHoras(horas);
    setIdMaquinariaHerramienta(idMaquinariaHerramienta);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdHerramienta(idHerramienta);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdMaquinaria(Long idMaquinaria) {
    this.idMaquinaria = idMaquinaria;
  }

  public Long getIdMaquinaria() {
    return idMaquinaria;
  }

  public void setHoras(Double horas) {
    this.horas = horas;
  }

  public Double getHoras() {
    return horas;
  }

  public void setIdMaquinariaHerramienta(Long idMaquinariaHerramienta) {
    this.idMaquinariaHerramienta = idMaquinariaHerramienta;
  }

  public Long getIdMaquinariaHerramienta() {
    return idMaquinariaHerramienta;
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

  public void setIdHerramienta(Long idHerramienta) {
    this.idHerramienta = idHerramienta;
  }

  public Long getIdHerramienta() {
    return idHerramienta;
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
  	return getIdMaquinariaHerramienta();
  }

  @Override
  public void setKey(Long key) {
  	this.idMaquinariaHerramienta = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getHoras());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaHerramienta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdHerramienta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("horas", getHoras());
		regresar.put("idMaquinariaHerramienta", getIdMaquinariaHerramienta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idHerramienta", getIdHerramienta());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdMaquinaria(), getHoras(), getIdMaquinariaHerramienta(), getIdUsuario(), getObservaciones(), getIdHerramienta(), getRegistro()
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
    regresar.append("idMaquinariaHerramienta~");
    regresar.append(getIdMaquinariaHerramienta());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMaquinariaHerramienta());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasHerramientasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMaquinariaHerramienta()!= null && getIdMaquinariaHerramienta()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeMaquinariasHerramientasDto other = (TcSakbeMaquinariasHerramientasDto) obj;
    if (getIdMaquinariaHerramienta() != other.idMaquinariaHerramienta && (getIdMaquinariaHerramienta() == null || !getIdMaquinariaHerramienta().equals(other.idMaquinariaHerramienta))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaquinariaHerramienta() != null ? getIdMaquinariaHerramienta().hashCode() : 0);
    return hash;
  }

}


