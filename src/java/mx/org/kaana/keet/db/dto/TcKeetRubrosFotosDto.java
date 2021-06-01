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
@Table(name="tc_keet_rubros_fotos")
public class TcKeetRubrosFotosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_rubro_foto")
  private Long idRubroFoto;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_rubro")
  private Long idRubro;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetRubrosFotosDto() {
    this(new Long(-1L));
  }

  public TcKeetRubrosFotosDto(Long key) {
    this(new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetRubrosFotosDto(Long idRubroFoto, String codigo, Long idRubro, String nombre) {
    setIdRubroFoto(idRubroFoto);
    setCodigo(codigo);
    setIdRubro(idRubro);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdRubroFoto(Long idRubroFoto) {
    this.idRubroFoto = idRubroFoto;
  }

  public Long getIdRubroFoto() {
    return idRubroFoto;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdRubro(Long idRubro) {
    this.idRubro = idRubro;
  }

  public Long getIdRubro() {
    return idRubro;
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
  	return getIdRubroFoto();
  }

  @Override
  public void setKey(Long key) {
  	this.idRubroFoto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdRubroFoto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRubro());
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
		regresar.put("idRubroFoto", getIdRubroFoto());
		regresar.put("codigo", getCodigo());
		regresar.put("idRubro", getIdRubro());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdRubroFoto(), getCodigo(), getIdRubro(), getNombre(), getRegistro()
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
    regresar.append("idRubroFoto~");
    regresar.append(getIdRubroFoto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdRubroFoto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetRubrosFotosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdRubroFoto()!= null && getIdRubroFoto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetRubrosFotosDto other = (TcKeetRubrosFotosDto) obj;
    if (getIdRubroFoto() != other.idRubroFoto && (getIdRubroFoto() == null || !getIdRubroFoto().equals(other.idRubroFoto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdRubroFoto() != null ? getIdRubroFoto().hashCode() : 0);
    return hash;
  }

}


