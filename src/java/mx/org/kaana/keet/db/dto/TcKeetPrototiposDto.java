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
@Table(name="tc_keet_prototipos")
public class TcKeetPrototiposDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="metros2")
  private Double metros2;
  @Column (name="clave")
  private String clave;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prototipo")
  private Long idPrototipo;
	@Column (name="dias_construccion")
  private Long diasConstruccion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_tipo_dia")
  private Long idTipoDia;

  public TcKeetPrototiposDto() {
    this(new Long(-1L));
  }

  public TcKeetPrototiposDto(Long key) {
    this(null, 120D, null, null, null, new Long(-1L), null, 35L, 1L);
    setKey(key);
  }


  public TcKeetPrototiposDto(String descripcion, Double metros2, String clave, Long idCliente, Long idUsuario, Long idPrototipo, String nombre, Long diasConstruccion, Long idTipoDia) {
    this.descripcion     = descripcion;
    this.metros2         = metros2;
    this.clave           = clave;
    this.idCliente       = idCliente;
    this.idUsuario       = idUsuario;
    this.idPrototipo     = idPrototipo;
    this.nombre          = nombre;		
		this.diasConstruccion= diasConstruccion;
		this.registro        = LocalDateTime.now();   
		this.idTipoDia       = idTipoDia;
  }
  
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setMetros2(Double metros2) {
    this.metros2 = metros2;
  }

  public Double getMetros2() {
    return metros2;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

	public Long getDiasConstruccion() {
		return diasConstruccion;
	}

	public void setDiasConstruccion(Long diasConstruccion) {
		this.diasConstruccion = diasConstruccion;
	}

	public Long getIdTipoDia() {
		return idTipoDia;
	}

	public void setIdTipoDia(Long idTipoDia) {
		this.idTipoDia=idTipoDia;
	}
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdPrototipo();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrototipo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMetros2());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiasConstruccion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoDia());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("metros2", getMetros2());
		regresar.put("clave", getClave());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
		regresar.put("diasConstruccion", getDiasConstruccion());
		regresar.put("idTipoDia", getIdTipoDia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getDescripcion(), getMetros2(), getClave(), getIdCliente(), getIdUsuario(), getIdPrototipo(), getNombre(), getRegistro(), getDiasConstruccion(), getIdTipoDia()
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
    regresar.append("idPrototipo~");
    regresar.append(getIdPrototipo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrototipo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrototiposDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrototipo()!= null && getIdPrototipo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrototiposDto other = (TcKeetPrototiposDto) obj;
    if (getIdPrototipo() != other.idPrototipo && (getIdPrototipo() == null || !getIdPrototipo().equals(other.idPrototipo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrototipo() != null ? getIdPrototipo().hashCode() : 0);
    return hash;
  }
	
}