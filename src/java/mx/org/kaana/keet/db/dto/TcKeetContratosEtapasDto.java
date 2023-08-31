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
@Table(name="tc_keet_contratos_etapas")
public class TcKeetContratosEtapasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="subcontratados")
  private Double subcontratados;
  @Column (name="id_etapa")
  private Long idEtapa;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="destajos")
  private Double destajos;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_etapa")
  private Long idContratoEtapa;
  @Column (name="materiales")
  private Double materiales;
  @Column (name="por_el_dia")
  private Double porElDia;
  @Column (name="administrativos")
  private Double administrativos;
  @Column (name="maquinaria")
  private Double maquinaria;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosEtapasDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosEtapasDto(Long key) {
    this(null, 0D, null, null, null, 0D, new Long(-1L), 0D, 0D, 0D, 0D, null);
    setKey(key);
  }

  public TcKeetContratosEtapasDto(String descripcion, Double subcontratados, Long idEtapa, Long idUsuario, Long idContrato, Double destajos, Long idContratoEtapa, Double materiales, Double porElDia, Double administrativos, Double maquinaria, String observaciones) {
    setDescripcion(descripcion);
    setSubcontratados(subcontratados);
    setIdEtapa(idEtapa);
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setDestajos(destajos);
    setIdContratoEtapa(idContratoEtapa);
    setMateriales(materiales);
    setPorElDia(porElDia);
    setAdministrativos(administrativos);
    setMaquinaria(maquinaria);
    setObservaciones(observaciones);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setSubcontratados(Double subcontratados) {
    this.subcontratados = subcontratados;
  }

  public Double getSubcontratados() {
    return subcontratados;
  }

  public void setIdEtapa(Long idEtapa) {
    this.idEtapa = idEtapa;
  }

  public Long getIdEtapa() {
    return idEtapa;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setDestajos(Double destajos) {
    this.destajos = destajos;
  }

  public Double getDestajos() {
    return destajos;
  }

  public void setIdContratoEtapa(Long idContratoEtapa) {
    this.idContratoEtapa = idContratoEtapa;
  }

  public Long getIdContratoEtapa() {
    return idContratoEtapa;
  }

  public void setMateriales(Double materiales) {
    this.materiales = materiales;
  }

  public Double getMateriales() {
    return materiales;
  }

  public void setPorElDia(Double porElDia) {
    this.porElDia = porElDia;
  }

  public Double getPorElDia() {
    return porElDia;
  }

  public void setAdministrativos(Double administrativos) {
    this.administrativos = administrativos;
  }

  public Double getAdministrativos() {
    return administrativos;
  }

  public void setMaquinaria(Double maquinaria) {
    this.maquinaria = maquinaria;
  }

  public Double getMaquinaria() {
    return maquinaria;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
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
  	return getIdContratoEtapa();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoEtapa = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubcontratados());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEtapa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDestajos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoEtapa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMateriales());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorElDia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAdministrativos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("subcontratados", getSubcontratados());
		regresar.put("idEtapa", getIdEtapa());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("destajos", getDestajos());
		regresar.put("idContratoEtapa", getIdContratoEtapa());
		regresar.put("materiales", getMateriales());
		regresar.put("porElDia", getPorElDia());
		regresar.put("administrativos", getAdministrativos());
		regresar.put("maquinaria", getMaquinaria());
    regresar.put("observaciones", getObservaciones());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getDescripcion(), getSubcontratados(), getIdEtapa(), getIdUsuario(), getIdContrato(), getDestajos(), getIdContratoEtapa(), getMateriales(), getPorElDia(), getAdministrativos(), getMaquinaria(), getObservaciones(), getRegistro()
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
    regresar.append("idContratoEtapa~");
    regresar.append(getIdContratoEtapa());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoEtapa());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosEtapasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoEtapa()!= null && getIdContratoEtapa()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosEtapasDto other = (TcKeetContratosEtapasDto) obj;
    if (getIdContratoEtapa() != other.idContratoEtapa && (getIdContratoEtapa() == null || !getIdContratoEtapa().equals(other.idContratoEtapa))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoEtapa() != null ? getIdContratoEtapa().hashCode() : 0);
    return hash;
  }

}


