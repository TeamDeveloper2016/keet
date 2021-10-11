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
  @Column (name="inicio")
  private LocalDate inicio;
  @Column (name="lote")
  private String lote;
  @Column (name="termino")
  private LocalDate termino;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="id_contrato_lote_estatus")
  private Long idContratoLoteEstatus;
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
  @Column (name="orden")
  private Long orden;
  @Column (name="atributos")
  private String atributos;
  @Column (name="latitud")
  private String latitud;
  @Column (name="longitud")
  private String longitud;
	@Column (name="arranque")
  private LocalDate arranque;
	@Column (name="id_estacion")
  private Long idEstacion;
	@Column (name="costo")
  private Double costo;
	@Column (name="calle")
  private String calle;
	@Column (name="numero")
  private String numero;

  public TcKeetContratosLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosLotesDto(Long key) {
    this(null, null, LocalDate.now(), null, LocalDate.now(), null, null, null, new Long(-1L), null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosLotesDto(String manzana, String clave, LocalDate inicio, String lote, LocalDate termino, Long idUsuario, Long idContrato, Long idContratoLoteEstatus, Long idContratoLote, Long idTipoFachada, Long diasConstruccion, Long idPrototipo, Long orden, String atributos, String latitud, String longitud, LocalDate arranque, Long idEstacion, Double costo) {
    this(manzana, clave, inicio, lote, termino, idUsuario, idContrato, idContratoLoteEstatus, idContratoLote, idTipoFachada, diasConstruccion, idPrototipo, orden, atributos, latitud, longitud, arranque, idEstacion, costo, null, null);
  }
  
  public TcKeetContratosLotesDto(String manzana, String clave, LocalDate inicio, String lote, LocalDate termino, Long idUsuario, Long idContrato, Long idContratoLoteEstatus, Long idContratoLote, Long idTipoFachada, Long diasConstruccion, Long idPrototipo, Long orden, String atributos, String latitud, String longitud, LocalDate arranque, Long idEstacion, Double costo, String calle, String numero) {
    setManzana(manzana);
    setClave(clave);
    setInicio(inicio);
    setLote(lote);
    setTermino(termino);
    setRegistro(LocalDateTime.now());
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setIdContratoLoteEstatus(idContratoLoteEstatus);
    setIdContratoLote(idContratoLote);
    setIdTipoFachada(idTipoFachada);
    setDiasConstruccion(diasConstruccion);
    setIdPrototipo(idPrototipo);
    setOrden(orden);
    setAtributos(atributos);
		setLatitud(latitud);
		setLongitud(longitud);
		setArranque(arranque);
		setIdEstacion(idEstacion);
		setCosto(costo);
    this.calle= calle;
    this.numero= numero;
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

  public void setInicio(LocalDate inicio) {
    this.inicio = inicio;
  }

  public LocalDate getInicio() {
    return inicio;
  }

  public void setLote(String lote) {
    this.lote = lote;
  }

  public String getLote() {
    return lote;
  }

  public void setTermino(LocalDate termino) {
    this.termino = termino;
  }

  public LocalDate getTermino() {
    return termino;
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

  public void setIdContratoLoteEstatus(Long idContratoLoteEstatus) {
    this.idContratoLoteEstatus = idContratoLoteEstatus;
  }

  public Long getIdContratoLoteEstatus() {
    return idContratoLoteEstatus;
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

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}	

	public LocalDate getArranque() {
		return arranque;
	}

	public void setArranque(LocalDate arranque) {
		this.arranque = arranque;
	}	

	public Long getIdEstacion() {
		return idEstacion;
	}

	public void setIdEstacion(Long idEstacion) {
		this.idEstacion=idEstacion;
	}
	
	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}	

  public String getCalle() {
    return calle;
  }

  public void setCalle(String calle) {
    this.calle = calle;
  }
	
  public void setNumero(String numero) {
    this.numero = numero;
  }

  public String getNumero() {
    return numero;
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
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
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
		regresar.append(getLatitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLongitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getArranque());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNumero());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("manzana", getManzana());
		regresar.put("clave", getClave());
		regresar.put("fechaInicio", getInicio());
		regresar.put("lote", getLote());
		regresar.put("fechaTermino", getTermino());
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idContratoLoteEstatus", getIdContratoLoteEstatus());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("idTipoFachada", getIdTipoFachada());
		regresar.put("diasConstruccion", getDiasConstruccion());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("orden", getOrden());
		regresar.put("atributos", getAtributos());
		regresar.put("latitud", getLatitud());
		regresar.put("longitud", getLongitud());
		regresar.put("arranque", getArranque());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("costo", getCosto());
		regresar.put("calle", getCalle());
		regresar.put("numero", getNumero());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getManzana(), getClave(), getInicio(), getLote(), getTermino(), getRegistro(), getIdUsuario(), getIdContrato(), getIdContratoLoteEstatus(), getIdContratoLote(), getIdTipoFachada(), getDiasConstruccion(), getIdPrototipo(), getOrden(), getAtributos(), getLatitud(), getLongitud(), getArranque(), getIdEstacion(), getCosto(), getCalle(), getNumero()
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