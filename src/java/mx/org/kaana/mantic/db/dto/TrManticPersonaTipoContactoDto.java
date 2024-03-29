package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
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
@Table(name="tr_mantic_persona_tipo_contacto")
public class TrManticPersonaTipoContactoDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_persona")
  private Long idPersona;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_persona_tipo_contacto")
  private Long idPersonaTipoContacto;
  @Column (name="valor")
  private String valor;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_tipo_contacto")
  private Long idTipoContacto;
  @Column (name="id_preferido")
  private Long idPreferido;
  @Column (name="registro")
  private LocalDateTime registro;

  public TrManticPersonaTipoContactoDto() {
    this(new Long(-1L));
  }

  public TrManticPersonaTipoContactoDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null, 2L);
    setKey(key);
  }

  public TrManticPersonaTipoContactoDto(Long idPersona, Long idUsuario, Long idPersonaTipoContacto, String valor, String observaciones, Long orden, Long idTipoContacto, Long idPreferido) {
    setIdPersona(idPersona);
    setIdUsuario(idUsuario);
    setIdPersonaTipoContacto(idPersonaTipoContacto);
    setValor(valor);
    setObservaciones(observaciones);
    setOrden(orden);
    setIdTipoContacto(idTipoContacto);
    setRegistro(LocalDateTime.now());
    this.idPreferido= idPreferido;
  }
	
  public void setIdPersona(Long idPersona) {
    this.idPersona = idPersona;
  }

  public Long getIdPersona() {
    return idPersona;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdPersonaTipoContacto(Long idPersonaTipoContacto) {
    this.idPersonaTipoContacto = idPersonaTipoContacto;
  }

  public Long getIdPersonaTipoContacto() {
    return idPersonaTipoContacto;
  }

  public void setValor(String valor) {
    this.valor = valor;
  }

  public String getValor() {
    return valor;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdTipoContacto(Long idTipoContacto) {
    this.idTipoContacto = idTipoContacto;
  }

  public Long getIdTipoContacto() {
    return idTipoContacto;
  }

  public Long getIdPreferido() {
    return idPreferido;
  }

  public void setIdPreferido(Long idPreferido) {
    this.idPreferido = idPreferido;
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
  	return getIdPersonaTipoContacto();
  }

  @Override
  public void setKey(Long key) {
  	this.idPersonaTipoContacto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPersonaTipoContacto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getValor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoContacto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPreferido());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPersona", getIdPersona());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPersonaTipoContacto", getIdPersonaTipoContacto());
		regresar.put("valor", getValor());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("idTipoContacto", getIdTipoContacto());
		regresar.put("idPreferido", getIdPreferido());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdPersona(), getIdUsuario(), getIdPersonaTipoContacto(), getValor(), getObservaciones(), getOrden(), getIdTipoContacto(), getIdPreferido(), getRegistro()
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
    regresar.append("idPersonaTipoContacto~");
    regresar.append(getIdPersonaTipoContacto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPersonaTipoContacto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TrManticPersonaTipoContactoDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPersonaTipoContacto()!= null && getIdPersonaTipoContacto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrManticPersonaTipoContactoDto other = (TrManticPersonaTipoContactoDto) obj;
    if (getIdPersonaTipoContacto() != other.idPersonaTipoContacto && (getIdPersonaTipoContacto() == null || !getIdPersonaTipoContacto().equals(other.idPersonaTipoContacto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPersonaTipoContacto() != null ? getIdPersonaTipoContacto().hashCode() : 0);
    return hash;
  }

}


