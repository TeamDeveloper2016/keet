package mx.org.kaana.mantic.db.dto;

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
@Table(name="tc_mantic_regimenes_fiscales")
public class TcManticRegimenesFiscalesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_regimen_fiscal")
  private Long idRegimenFiscal;
  @Column (name="id_tipo_regimen_persona")
  private Long idTipoRegimenPersona;
  @Column (name="codigo")
  private String codigo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticRegimenesFiscalesDto() {
    this(new Long(-1L));
  }

  public TcManticRegimenesFiscalesDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcManticRegimenesFiscalesDto(String descripcion, Long idRegimenFiscal, Long idTipoRegimenPersona, String codigo, String nombre) {
    setDescripcion(descripcion);
    setIdRegimenFiscal(idRegimenFiscal);
    setIdTipoRegimenPersona(idTipoRegimenPersona);
    setCodigo(codigo);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdRegimenFiscal(Long idRegimenFiscal) {
    this.idRegimenFiscal = idRegimenFiscal;
  }

  public Long getIdRegimenFiscal() {
    return idRegimenFiscal;
  }

  public void setIdTipoRegimenPersona(Long idTipoRegimenPersona) {
    this.idTipoRegimenPersona = idTipoRegimenPersona;
  }

  public Long getIdTipoRegimenPersona() {
    return idTipoRegimenPersona;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
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

  @Transient
  @Override
  public Long getKey() {
  	return getIdRegimenFiscal();
  }

  @Override
  public void setKey(Long key) {
  	this.idRegimenFiscal = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRegimenFiscal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoRegimenPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
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
		regresar.put("descripcion", getDescripcion());
		regresar.put("idRegimenFiscal", getIdRegimenFiscal());
		regresar.put("idTipoRegimenPersona", getIdTipoRegimenPersona());
		regresar.put("codigo", getCodigo());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdRegimenFiscal(), getIdTipoRegimenPersona(), getCodigo(), getNombre(), getRegistro()
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
    regresar.append("idRegimenFiscal~");
    regresar.append(getIdRegimenFiscal());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdRegimenFiscal());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticRegimenesFiscalesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdRegimenFiscal()!= null && getIdRegimenFiscal()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticRegimenesFiscalesDto other = (TcManticRegimenesFiscalesDto) obj;
    if (getIdRegimenFiscal() != other.idRegimenFiscal && (getIdRegimenFiscal() == null || !getIdRegimenFiscal().equals(other.idRegimenFiscal))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdRegimenFiscal() != null ? getIdRegimenFiscal().hashCode() : 0);
    return hash;
  }

}


