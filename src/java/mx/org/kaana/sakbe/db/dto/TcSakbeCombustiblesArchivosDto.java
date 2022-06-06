package mx.org.kaana.sakbe.db.dto;

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
@Table(name="tc_sakbe_combustibles_archivos")
public class TcSakbeCombustiblesArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="archivo")
  private String archivo;
  @Column (name="eliminado")
  private LocalDateTime eliminado;
  @Column (name="ruta")
  private String ruta;
  @Column (name="tamanio")
  private Long tamanio;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_archivo")
  private Long idTipoArchivo;
  @Column (name="alias")
  private String alias;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_combustible_archivo")
  private Long idCombustibleArchivo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="id_combustible")
  private Long idCombustible;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeCombustiblesArchivosDto() {
    this(new Long(-1L));
  }

  public TcSakbeCombustiblesArchivosDto(Long key) {
    this(null, LocalDateTime.now(), null, null, null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcSakbeCombustiblesArchivosDto(String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String alias, Long idCombustibleArchivo, String nombre, Long idCombustible) {
    setArchivo(archivo);
    setEliminado(eliminado);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setAlias(alias);
    setIdCombustibleArchivo(idCombustibleArchivo);
    setNombre(nombre);
    setIdCombustible(idCombustible);
    setRegistro(LocalDateTime.now());
  }
	
  public void setArchivo(String archivo) {
    this.archivo = archivo;
  }

  public String getArchivo() {
    return archivo;
  }

  public void setEliminado(LocalDateTime eliminado) {
    this.eliminado = eliminado;
  }

  public LocalDateTime getEliminado() {
    return eliminado;
  }

  public void setRuta(String ruta) {
    this.ruta = ruta;
  }

  public String getRuta() {
    return ruta;
  }

  public void setTamanio(Long tamanio) {
    this.tamanio = tamanio;
  }

  public Long getTamanio() {
    return tamanio;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdTipoArchivo(Long idTipoArchivo) {
    this.idTipoArchivo = idTipoArchivo;
  }

  public Long getIdTipoArchivo() {
    return idTipoArchivo;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getAlias() {
    return alias;
  }

  public void setIdCombustibleArchivo(Long idCombustibleArchivo) {
    this.idCombustibleArchivo = idCombustibleArchivo;
  }

  public Long getIdCombustibleArchivo() {
    return idCombustibleArchivo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdCombustible(Long idCombustible) {
    this.idCombustible = idCombustible;
  }

  public Long getIdCombustible() {
    return idCombustible;
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
  	return getIdCombustibleArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idCombustibleArchivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEliminado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRuta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTamanio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustibleArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("archivo", getArchivo());
		regresar.put("eliminado", getEliminado());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("alias", getAlias());
		regresar.put("idCombustibleArchivo", getIdCombustibleArchivo());
		regresar.put("nombre", getNombre());
		regresar.put("idCombustible", getIdCombustible());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getArchivo(), getEliminado(), getRuta(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getAlias(), getIdCombustibleArchivo(), getNombre(), getIdCombustible(), getRegistro()
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
    regresar.append("idCombustibleArchivo~");
    regresar.append(getIdCombustibleArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCombustibleArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeCombustiblesArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCombustibleArchivo()!= null && getIdCombustibleArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeCombustiblesArchivosDto other = (TcSakbeCombustiblesArchivosDto) obj;
    if (getIdCombustibleArchivo() != other.idCombustibleArchivo && (getIdCombustibleArchivo() == null || !getIdCombustibleArchivo().equals(other.idCombustibleArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCombustibleArchivo() != null ? getIdCombustibleArchivo().hashCode() : 0);
    return hash;
  }

}


