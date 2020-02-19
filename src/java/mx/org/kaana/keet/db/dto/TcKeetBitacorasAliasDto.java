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
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Lob;
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
@Table(name="tc_keet_bitacoras_alias")
public class TcKeetBitacorasAliasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_bitacora_alia")
  private Long idBitacoraAlia;
  @Column (name="alias")
  private String alias;
  @Column (name="tabla")
  private String tabla;
  @Column (name="campo")
  private String campo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetBitacorasAliasDto() {
    this(new Long(-1L));
  }

  public TcKeetBitacorasAliasDto(Long key) {
    this(new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetBitacorasAliasDto(Long idBitacoraAlia, String alias, String tabla, String campo) {
    setIdBitacoraAlia(idBitacoraAlia);
    setAlias(alias);
    setTabla(tabla);
    setCampo(campo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdBitacoraAlia(Long idBitacoraAlia) {
    this.idBitacoraAlia = idBitacoraAlia;
  }

  public Long getIdBitacoraAlia() {
    return idBitacoraAlia;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getAlias() {
    return alias;
  }

  public void setTabla(String tabla) {
    this.tabla = tabla;
  }

  public String getTabla() {
    return tabla;
  }

  public void setCampo(String campo) {
    this.campo = campo;
  }

  public String getCampo() {
    return campo;
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
  	return getIdBitacoraAlia();
  }

  @Override
  public void setKey(Long key) {
  	this.idBitacoraAlia = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdBitacoraAlia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTabla());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCampo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idBitacoraAlia", getIdBitacoraAlia());
		regresar.put("alias", getAlias());
		regresar.put("tabla", getTabla());
		regresar.put("campo", getCampo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdBitacoraAlia(), getAlias(), getTabla(), getCampo(), getRegistro()
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
    regresar.append("idBitacoraAlia~");
    regresar.append(getIdBitacoraAlia());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdBitacoraAlia());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetBitacorasAliasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdBitacoraAlia()!= null && getIdBitacoraAlia()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetBitacorasAliasDto other = (TcKeetBitacorasAliasDto) obj;
    if (getIdBitacoraAlia() != other.idBitacoraAlia && (getIdBitacoraAlia() == null || !getIdBitacoraAlia().equals(other.idBitacoraAlia))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdBitacoraAlia() != null ? getIdBitacoraAlia().hashCode() : 0);
    return hash;
  }

}


