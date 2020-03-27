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
@Table(name="tc_keet_contratos_lotes")
public class TcKeetContratosLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="manzana")
  private String manzana;
  @Column (name="clave")
  private String clave;
  @Column (name="fecha_inicio")
  private LocalDate fechaInicio;
  @Column (name="lote")
  private Long lote;
  @Column (name="fecha_termino")
  private LocalDate fechaTermino;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="id_tipo_fachada")
  private Long idTipoFachada;
  @Column (name="dias_construccion")
  private Long diasConstruccion;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="atributos")
  private String atributos;

  public TcKeetContratosLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosLotesDto(Long key) {
    this(null, null, LocalDate.now(), null, LocalDate.now(), null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosLotesDto(String manzana, String clave, LocalDate fechaInicio, Long lote, LocalDate fechaTermino, Long idUsuario, Long idContrato, Long idContratoLote, Long idTipoFachada, Long diasConstruccion, Long idPrototipo, String atributos) {
    setManzana(manzana);
    setClave(clave);
    setFechaInicio(fechaInicio);
    setLote(lote);
    setFechaTermino(fechaTermino);
    setRegistro(LocalDateTime.now());
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setIdContratoLote(idContratoLote);
    setIdTipoFachada(idTipoFachada);
    setDiasConstruccion(diasConstruccion);
    setIdPrototipo(idPrototipo);
    setAtributos(atributos);
  }
	
  public void setManzana(String manzana) {
    this.manzana = manzana;
  }

  public String getManzana() {
    return manzana;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setLote(Long lote) {
    this.lote = lote;
  }

  public Long getLote() {
    return lote;
  }

  public void setFechaTermino(LocalDate fechaTermino) {
    this.fechaTermino = fechaTermino;
  }

  public LocalDate getFechaTermino() {
    return fechaTermino;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
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

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setIdTipoFachada(Long idTipoFachada) {
    this.idTipoFachada = idTipoFachada;
  }

  public Long getIdTipoFachada() {
    return idTipoFachada;
  }

  public void setDiasConstruccion(Long diasConstruccion) {
    this.diasConstruccion = diasConstruccion;
  }

  public Long getDiasConstruccion() {
    return diasConstruccion;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
  }

  public void setAtributos(String atributos) {
    this.atributos = atributos;
  }

  public String getAtributos() {
    return atributos;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdContratoLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoLote = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getManzana());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoFachada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiasConstruccion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAtributos());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("manzana", getManzana());
		regresar.put("clave", getClave());
		regresar.put("fechaInicio", getFechaInicio());
		regresar.put("lote", getLote());
		regresar.put("fechaTermino", getFechaTermino());
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("idTipoFachada", getIdTipoFachada());
		regresar.put("diasConstruccion", getDiasConstruccion());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("atributos", getAtributos());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getManzana(), getClave(), getFechaInicio(), getLote(), getFechaTermino(), getRegistro(), getIdUsuario(), getIdContrato(), getIdContratoLote(), getIdTipoFachada(), getDiasConstruccion(), getIdPrototipo(), getAtributos()
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
    regresar.append("idContratoLote~");
    regresar.append(getIdContratoLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoLote()!= null && getIdContratoLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosLotesDto other = (TcKeetContratosLotesDto) obj;
    if (getIdContratoLote() != other.idContratoLote && (getIdContratoLote() == null || !getIdContratoLote().equals(other.idContratoLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoLote() != null ? getIdContratoLote().hashCode() : 0);
    return hash;
  }

}


