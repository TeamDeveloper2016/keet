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
@Table(name="tc_keet_contratos_destajos_proveedores")
public class TcKeetContratosDestajosProveedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_estacion_estatus")
  private Long idEstacionEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_destajo_proveedor")
  private Long idContratoDestajoProveedor;
  @Column (name="costo")
  private Double costo;
  @Column (name="periodo")
  private Long periodo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="semana")
  private Long semana;
  @Column (name="id_estacion")
  private Long idEstacion;
  @Column (name="id_contrato_lote_proveedor")
  private Long idContratoLoteProveedor;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="anticipo")
  private Double anticipo;

  public TcKeetContratosDestajosProveedoresDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosDestajosProveedoresDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null, null, null, 0D);
    setKey(key);
  }

  public TcKeetContratosDestajosProveedoresDto(Long idEstacionEstatus, Long idContratoDestajoProveedor, Double costo, Long periodo, Long idUsuario, Long semana, Long idEstacion, Long idContratoLoteProveedor, Double porcentaje, Long idNomina, Double anticipo) {
    setIdEstacionEstatus(idEstacionEstatus);
    setIdContratoDestajoProveedor(idContratoDestajoProveedor);
    setCosto(costo);
    setPeriodo(periodo);
    setIdUsuario(idUsuario);
    setSemana(semana);
    setIdEstacion(idEstacion);
    setIdContratoLoteProveedor(idContratoLoteProveedor);
    setPorcentaje(porcentaje);
    setIdNomina(idNomina);
    setRegistro(LocalDateTime.now());
    this.anticipo= anticipo;
  }
	
  public void setIdEstacionEstatus(Long idEstacionEstatus) {
    this.idEstacionEstatus = idEstacionEstatus;
  }

  public Long getIdEstacionEstatus() {
    return idEstacionEstatus;
  }

  public void setIdContratoDestajoProveedor(Long idContratoDestajoProveedor) {
    this.idContratoDestajoProveedor = idContratoDestajoProveedor;
  }

  public Long getIdContratoDestajoProveedor() {
    return idContratoDestajoProveedor;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setPeriodo(Long periodo) {
    this.periodo = periodo;
  }

  public Long getPeriodo() {
    return periodo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setSemana(Long semana) {
    this.semana = semana;
  }

  public Long getSemana() {
    return semana;
  }

  public void setIdEstacion(Long idEstacion) {
    this.idEstacion = idEstacion;
  }

  public Long getIdEstacion() {
    return idEstacion;
  }

  public void setIdContratoLoteProveedor(Long idContratoLoteProveedor) {
    this.idContratoLoteProveedor = idContratoLoteProveedor;
  }

  public Long getIdContratoLoteProveedor() {
    return idContratoLoteProveedor;
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

  public Double getAnticipo() {
    return anticipo;
  }

  public void setAnticipo(Double anticipo) {
    this.anticipo = anticipo;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdContratoDestajoProveedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoDestajoProveedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEstacionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPeriodo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSemana());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAnticipo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idEstacionEstatus", getIdEstacionEstatus());
		regresar.put("idContratoDestajoProveedor", getIdContratoDestajoProveedor());
		regresar.put("costo", getCosto());
		regresar.put("periodo", getPeriodo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("semana", getSemana());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("idContratoLoteProveedor", getIdContratoLoteProveedor());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
		regresar.put("anticipo", getAnticipo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdEstacionEstatus(), getIdContratoDestajoProveedor(), getCosto(), getPeriodo(), getIdUsuario(), getSemana(), getIdEstacion(), getIdContratoLoteProveedor(), getPorcentaje(), getIdNomina(), getRegistro(), getAnticipo()
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
    regresar.append("idContratoDestajoProveedor~");
    regresar.append(getIdContratoDestajoProveedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoDestajoProveedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosDestajosProveedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoDestajoProveedor()!= null && getIdContratoDestajoProveedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosDestajosProveedoresDto other = (TcKeetContratosDestajosProveedoresDto) obj;
    if (getIdContratoDestajoProveedor() != other.idContratoDestajoProveedor && (getIdContratoDestajoProveedor() == null || !getIdContratoDestajoProveedor().equals(other.idContratoDestajoProveedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoDestajoProveedor() != null ? getIdContratoDestajoProveedor().hashCode() : 0);
    return hash;
  }

}


