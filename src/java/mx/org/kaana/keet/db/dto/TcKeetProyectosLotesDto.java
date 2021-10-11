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
@Table(name="tc_keet_proyectos_lotes")
public class TcKeetProyectosLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="manzana")
  private String manzana;
  @Column (name="clave")
  private String clave;
  @Column (name="fecha_inicio")
  private LocalDate fechaInicio;
  @Column (name="id_proyecto")
  private Long idProyecto;
  @Column (name="lote")
  private String lote;
  @Column (name="fecha_termino")
  private LocalDate fechaTermino;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proyecto_lote")
  private Long idProyectoLote;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_fachada")
  private Long idTipoFachada;
  @Column (name="dias_construccion")
  private Long diasConstruccion;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="orden")
  private Long orden;
  @Column (name="atributos")
  private String atributos;
  @Column (name="costo")
  private Double costo;

  public TcKeetProyectosLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetProyectosLotesDto(Long key) {
    this(null, null, LocalDate.now(), null, null, LocalDate.now(), new Long(-1L), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetProyectosLotesDto(String manzana, String clave, LocalDate fechaInicio, Long idProyecto, String lote, LocalDate fechaTermino, Long idProyectoLote, Long idUsuario, Long idTipoFachada, Long diasConstruccion, Long idPrototipo, Long orden, String atributos, Double costo) {
    setManzana(manzana);
    setClave(clave);
    setFechaInicio(fechaInicio);
    setIdProyecto(idProyecto);
    setLote(lote);
    setFechaTermino(fechaTermino);
    setIdProyectoLote(idProyectoLote);
    setRegistro(LocalDateTime.now());
    setIdUsuario(idUsuario);
    setIdTipoFachada(idTipoFachada);
    setDiasConstruccion(diasConstruccion);
    setIdPrototipo(idPrototipo);
    setOrden(orden);
    setAtributos(atributos);
		setCosto(costo);
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

  public void setIdProyecto(Long idProyecto) {
    this.idProyecto = idProyecto;
  }

  public Long getIdProyecto() {
    return idProyecto;
  }

  public void setLote(String lote) {
    this.lote = lote;
  }

  public String getLote() {
    return lote;
  }

  public void setFechaTermino(LocalDate fechaTermino) {
    this.fechaTermino = fechaTermino;
  }

  public LocalDate getFechaTermino() {
    return fechaTermino;
  }

  public void setIdProyectoLote(Long idProyectoLote) {
    this.idProyectoLote = idProyectoLote;
  }

  public Long getIdProyectoLote() {
    return idProyectoLote;
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

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setAtributos(String atributos) {
    this.atributos = atributos;
  }

  public String getAtributos() {
    return atributos;
  }

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}	
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdProyectoLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idProyectoLote = key;
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
		regresar.append(getIdProyecto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoFachada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiasConstruccion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAtributos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("manzana", getManzana());
		regresar.put("clave", getClave());
		regresar.put("fechaInicio", getFechaInicio());
		regresar.put("idProyecto", getIdProyecto());
		regresar.put("lote", getLote());
		regresar.put("fechaTermino", getFechaTermino());
		regresar.put("idProyectoLote", getIdProyectoLote());
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoFachada", getIdTipoFachada());
		regresar.put("diasConstruccion", getDiasConstruccion());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("orden", getOrden());
		regresar.put("atributos", getAtributos());
		regresar.put("costo", getCosto());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getManzana(), getClave(), getFechaInicio(), getIdProyecto(), getLote(), getFechaTermino(), getIdProyectoLote(), getRegistro(), getIdUsuario(), getIdTipoFachada(), getDiasConstruccion(), getIdPrototipo(), getOrden(), getAtributos(), getCosto()
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
    regresar.append("idProyectoLote~");
    regresar.append(getIdProyectoLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProyectoLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProyectosLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProyectoLote()!= null && getIdProyectoLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProyectosLotesDto other = (TcKeetProyectosLotesDto) obj;
    if (getIdProyectoLote() != other.idProyectoLote && (getIdProyectoLote() == null || !getIdProyectoLote().equals(other.idProyectoLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProyectoLote() != null ? getIdProyectoLote().hashCode() : 0);
    return hash;
  }
}