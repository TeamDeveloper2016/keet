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
@Table(name="tc_keet_nominas_contratos_costos")
public class TcKeetNominasContratosCostosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="por_obra")
  private Double porObra;
  @Column (name="total")
  private Double total;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="porcentaje_dia")
  private Double porcentajeDia;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="por_dia")
  private Double porDia;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_contrato_costo")
  private Long idNominaContratoCosto;
  @Column (name="porcentaje_obra")
  private Double porcentajeObra;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetNominasContratosCostosDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasContratosCostosDto(Long key) {
    this(0D, 0D, null, 0D, null, 0D, new Long(-1L), 0D, null);
    setKey(key);
  }

  public TcKeetNominasContratosCostosDto(Double porObra, Double total, Long idUsuario, Double porcentajeDia, Long idContrato, Double porDia, Long idNominaContratoCosto, Double porcentajeObra, Long idNomina) {
    setPorObra(porObra);
    setTotal(total);
    setIdUsuario(idUsuario);
    setPorcentajeDia(porcentajeDia);
    setIdContrato(idContrato);
    setPorDia(porDia);
    setIdNominaContratoCosto(idNominaContratoCosto);
    setPorcentajeObra(porcentajeObra);
    setIdNomina(idNomina);
    setRegistro(LocalDateTime.now());
  }
	
  public void setPorObra(Double porObra) {
    this.porObra = porObra;
  }

  public Double getPorObra() {
    return porObra;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setPorcentajeDia(Double porcentajeDia) {
    this.porcentajeDia = porcentajeDia;
  }

  public Double getPorcentajeDia() {
    return porcentajeDia;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setPorDia(Double porDia) {
    this.porDia = porDia;
  }

  public Double getPorDia() {
    return porDia;
  }

  public void setIdNominaContratoCosto(Long idNominaContratoCosto) {
    this.idNominaContratoCosto = idNominaContratoCosto;
  }

  public Long getIdNominaContratoCosto() {
    return idNominaContratoCosto;
  }

  public void setPorcentajeObra(Double porcentajeObra) {
    this.porcentajeObra = porcentajeObra;
  }

  public Double getPorcentajeObra() {
    return porcentajeObra;
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
  	return getIdNominaContratoCosto();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaContratoCosto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getPorObra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentajeDia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorDia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaContratoCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentajeObra());
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
		regresar.put("porObra", getPorObra());
		regresar.put("total", getTotal());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("porcentajeDia", getPorcentajeDia());
		regresar.put("idContrato", getIdContrato());
		regresar.put("porDia", getPorDia());
		regresar.put("idNominaContratoCosto", getIdNominaContratoCosto());
		regresar.put("porcentajeObra", getPorcentajeObra());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getPorObra(), getTotal(), getIdUsuario(), getPorcentajeDia(), getIdContrato(), getPorDia(), getIdNominaContratoCosto(), getPorcentajeObra(), getIdNomina(), getRegistro()
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
    regresar.append("idNominaContratoCosto~");
    regresar.append(getIdNominaContratoCosto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaContratoCosto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasContratosCostosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaContratoCosto()!= null && getIdNominaContratoCosto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final TcKeetNominasContratosCostosDto other = (TcKeetNominasContratosCostosDto) obj;
    if (getIdNominaContratoCosto() != other.idNominaContratoCosto && (getIdNominaContratoCosto() == null || !getIdNominaContratoCosto().equals(other.idNominaContratoCosto))) 
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaContratoCosto() != null ? getIdNominaContratoCosto().hashCode() : 0);
    return hash;
  }

}


