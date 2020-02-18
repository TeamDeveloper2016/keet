package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
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
@Table(name="tc_keet_nominas_conceptos")
public class TcKeetNominasConceptosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_concepto")
  private Long idTipoConcepto;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="id_activo")
  private Long idActivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="aplicar")
  private String aplicar;
  @Column (name="inicio")
  private Date inicio;
  @Column (name="formula")
  private String formula;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_concepto")
  private Long idNominaConcepto;
  @Column (name="termino")
  private Date termino;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private Timestamp registro;

  public TcKeetNominasConceptosDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasConceptosDto(Long key) {
    this(null, null, null, null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), null, new Long(-1L), new Date(Calendar.getInstance().getTimeInMillis()), null);
    setKey(key);
  }

  public TcKeetNominasConceptosDto(Long idTipoConcepto, String descripcion, String clave, Long idActivo, Long idUsuario, String aplicar, Date inicio, String formula, Long idNominaConcepto, Date termino, String nombre) {
    setIdTipoConcepto(idTipoConcepto);
    setDescripcion(descripcion);
    setClave(clave);
    setIdActivo(idActivo);
    setIdUsuario(idUsuario);
    setAplicar(aplicar);
    setInicio(inicio);
    setFormula(formula);
    setIdNominaConcepto(idNominaConcepto);
    setTermino(termino);
    setNombre(nombre);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
  }
	
  public void setIdTipoConcepto(Long idTipoConcepto) {
    this.idTipoConcepto = idTipoConcepto;
  }

  public Long getIdTipoConcepto() {
    return idTipoConcepto;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setAplicar(String aplicar) {
    this.aplicar = aplicar;
  }

  public String getAplicar() {
    return aplicar;
  }

  public void setInicio(Date inicio) {
    this.inicio = inicio;
  }

  public Date getInicio() {
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

  public void setTermino(Date termino) {
    this.termino = termino;
  }

  public Date getTermino() {
    return termino;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNominaConcepto();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaConcepto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAplicar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFormula());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoConcepto", getIdTipoConcepto());
		regresar.put("descripcion", getDescripcion());
		regresar.put("clave", getClave());
		regresar.put("idActivo", getIdActivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("aplicar", getAplicar());
		regresar.put("inicio", getInicio());
		regresar.put("formula", getFormula());
		regresar.put("idNominaConcepto", getIdNominaConcepto());
		regresar.put("termino", getTermino());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoConcepto(), getDescripcion(), getClave(), getIdActivo(), getIdUsuario(), getAplicar(), getInicio(), getFormula(), getIdNominaConcepto(), getTermino(), getNombre(), getRegistro()
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
    regresar.append("idNominaConcepto~");
    regresar.append(getIdNominaConcepto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaConcepto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasConceptosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaConcepto()!= null && getIdNominaConcepto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasConceptosDto other = (TcKeetNominasConceptosDto) obj;
    if (getIdNominaConcepto() != other.idNominaConcepto && (getIdNominaConcepto() == null || !getIdNominaConcepto().equals(other.idNominaConcepto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaConcepto() != null ? getIdNominaConcepto().hashCode() : 0);
    return hash;
  }
}