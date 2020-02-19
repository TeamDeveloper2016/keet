package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
@Table(name="tc_keet_personas_conceptos")
public class TcKeetPersonasConceptosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_persona")
  private Long idPersona;
  @Column (name="id_activo")
  private Long idActivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_persona_concepto")
  private Long idPersonaConcepto;
  @Column (name="contador")
  private Long contador;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="inicio")
  private LocalDate inicio;
  @Column (name="formula")
  private String formula;
  @Column (name="id_nomina_concepto")
  private Long idNominaConcepto;
  @Column (name="termino")
  private LocalDate termino;
  @Column (name="parcialidades")
  private Long parcialidades;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPersonasConceptosDto() {
    this(new Long(-1L));
  }

  public TcKeetPersonasConceptosDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, LocalDate.now(), null, null, LocalDate.now(), null);
    setKey(key);
  }

  public TcKeetPersonasConceptosDto(Long idPersona, Long idActivo, Long idPersonaConcepto, Long contador, Long idUsuario, String observaciones, LocalDate inicio, String formula, Long idNominaConcepto, LocalDate termino, Long parcialidades) {
    setIdPersona(idPersona);
    setIdActivo(idActivo);
    setIdPersonaConcepto(idPersonaConcepto);
    setContador(contador);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setInicio(inicio);
    setFormula(formula);
    setIdNominaConcepto(idNominaConcepto);
    setTermino(termino);
    setParcialidades(parcialidades);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPersona(Long idPersona) {
    this.idPersona = idPersona;
  }

  public Long getIdPersona() {
    return idPersona;
  }

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setIdPersonaConcepto(Long idPersonaConcepto) {
    this.idPersonaConcepto = idPersonaConcepto;
  }

  public Long getIdPersonaConcepto() {
    return idPersonaConcepto;
  }

  public void setContador(Long contador) {
    this.contador = contador;
  }

  public Long getContador() {
    return contador;
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

  public void setInicio(LocalDate inicio) {
    this.inicio = inicio;
  }

  public LocalDate getInicio() {
    return inicio;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  public String getFormula() {
    return formula;
  }

  public void setIdNominaConcepto(Long idNominaConcepto) {
    this.idNominaConcepto = idNominaConcepto;
  }

  public Long getIdNominaConcepto() {
    return idNominaConcepto;
  }

  public void setTermino(LocalDate termino) {
    this.termino = termino;
  }

  public LocalDate getTermino() {
    return termino;
  }

  public void setParcialidades(Long parcialidades) {
    this.parcialidades = parcialidades;
  }

  public Long getParcialidades() {
    return parcialidades;
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
  	return getIdPersonaConcepto();
  }

  @Override
  public void setKey(Long key) {
  	this.idPersonaConcepto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPersonaConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getContador());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFormula());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getParcialidades());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPersona", getIdPersona());
		regresar.put("idActivo", getIdActivo());
		regresar.put("idPersonaConcepto", getIdPersonaConcepto());
		regresar.put("contador", getContador());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("inicio", getInicio());
		regresar.put("formula", getFormula());
		regresar.put("idNominaConcepto", getIdNominaConcepto());
		regresar.put("termino", getTermino());
		regresar.put("parcialidades", getParcialidades());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPersona(), getIdActivo(), getIdPersonaConcepto(), getContador(), getIdUsuario(), getObservaciones(), getInicio(), getFormula(), getIdNominaConcepto(), getTermino(), getParcialidades(), getRegistro()
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
    regresar.append("idPersonaConcepto~");
    regresar.append(getIdPersonaConcepto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPersonaConcepto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPersonasConceptosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPersonaConcepto()!= null && getIdPersonaConcepto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPersonasConceptosDto other = (TcKeetPersonasConceptosDto) obj;
    if (getIdPersonaConcepto() != other.idPersonaConcepto && (getIdPersonaConcepto() == null || !getIdPersonaConcepto().equals(other.idPersonaConcepto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPersonaConcepto() != null ? getIdPersonaConcepto().hashCode() : 0);
    return hash;
  }
}
