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
@Table(name="tc_keet_nominas_contratos")
public class TcKeetNominasContratosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="total")
  private Double total;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_contrato")
  private Long idContrato;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_contrato")
  private Long idNominaContrato;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetNominasContratosDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasContratosDto(Long key) {
    this(null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetNominasContratosDto(Double total, Long idDesarrollo, Long idContrato, Long idNominaContrato, Double porcentaje, Long idNomina) {
    setTotal(total);
    setIdDesarrollo(idDesarrollo);
    setIdContrato(idContrato);
    setIdNominaContrato(idNominaContrato);
    setPorcentaje(porcentaje);
    setIdNomina(idNomina);
    setRegistro(LocalDateTime.now());
  }
	
  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdNominaContrato(Long idNominaContrato) {
    this.idNominaContrato = idNominaContrato;
  }

  public Long getIdNominaContrato() {
    return idNominaContrato;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
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
  	return getIdNominaContrato();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaContrato = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("total", getTotal());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idNominaContrato", getIdNominaContrato());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getTotal(), getIdDesarrollo(), getIdContrato(), getIdNominaContrato(), getPorcentaje(), getIdNomina(), getRegistro()
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
    regresar.append("idNominaContrato~");
    regresar.append(getIdNominaContrato());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaContrato());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasContratosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaContrato()!= null && getIdNominaContrato()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasContratosDto other = (TcKeetNominasContratosDto) obj;
    if (getIdNominaContrato() != other.idNominaContrato && (getIdNominaContrato() == null || !getIdNominaContrato().equals(other.idNominaContrato))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaContrato() != null ? getIdNominaContrato().hashCode() : 0);
    return hash;
  }

}


