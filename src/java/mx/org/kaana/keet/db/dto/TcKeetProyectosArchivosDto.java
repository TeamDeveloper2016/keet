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
@Table(name="tc_keet_proyectos_archivos")
public class TcKeetProyectosArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_plano")
  private Long idPlano;
  @Column (name="archivo")
  private String archivo;
  @Column (name="ruta")
  private String ruta;
  @Column (name="id_proyecto")
  private Long idProyecto;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proyecto_archivo")
  private Long idProyectoArchivo;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="eliminado")
  private LocalDateTime eliminado;
  @Column (name="tamanio")
  private Long tamanio;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_archivo")
  private Long idTipoArchivo;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="alias")
  private String alias;
  @Column (name="id_prototipo")
  private Long idPrototipo;

  public TcKeetProyectosArchivosDto() {
    this(new Long(-1L));
  }

  public TcKeetProyectosArchivosDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), LocalDateTime.now(), null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetProyectosArchivosDto(Long idPlano, String archivo, String ruta, Long idProyecto, String nombre, Long idProyectoArchivo, LocalDateTime eliminado, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idPrototipo) {
    setIdPlano(idPlano);
    setArchivo(archivo);
    setRuta(ruta);
    setIdProyecto(idProyecto);
    setNombre(nombre);
    setIdProyectoArchivo(idProyectoArchivo);
    setRegistro(LocalDateTime.now());
    setEliminado(eliminado);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setObservaciones(observaciones);
    setAlias(alias);
    setIdPrototipo(idPrototipo);
  }
	
  public void setIdPlano(Long idPlano) {
    this.idPlano = idPlano;
  }

  public Long getIdPlano() {
    return idPlano;
  }

  public void setArchivo(String archivo) {
    this.archivo = archivo;
  }

  public String getArchivo() {
    return archivo;
  }

  public void setRuta(String ruta) {
    this.ruta = ruta;
  }

  public String getRuta() {
    return ruta;
  }

  public void setIdProyecto(Long idProyecto) {
    this.idProyecto = idProyecto;
  }

  public Long getIdProyecto() {
    return idProyecto;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdProyectoArchivo(Long idProyectoArchivo) {
    this.idProyectoArchivo = idProyectoArchivo;
  }

  public Long getIdProyectoArchivo() {
    return idProyectoArchivo;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setEliminado(LocalDateTime eliminado) {
    this.eliminado = eliminado;
  }

  public LocalDateTime getEliminado() {
    return eliminado;
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

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getAlias() {
    return alias;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdProyectoArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idProyectoArchivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPlano());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRuta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyecto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEliminado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTamanio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPlano", getIdPlano());
		regresar.put("archivo", getArchivo());
		regresar.put("ruta", getRuta());
		regresar.put("idProyecto", getIdProyecto());
		regresar.put("nombre", getNombre());
		regresar.put("idProyectoArchivo", getIdProyectoArchivo());
		regresar.put("registro", getRegistro());
		regresar.put("eliminado", getEliminado());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("observaciones", getObservaciones());
		regresar.put("alias", getAlias());
		regresar.put("idPrototipo", getIdPrototipo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPlano(), getArchivo(), getRuta(), getIdProyecto(), getNombre(), getIdProyectoArchivo(), getRegistro(), getEliminado(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getObservaciones(), getAlias(), getIdPrototipo()
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
    regresar.append("idProyectoArchivo~");
    regresar.append(getIdProyectoArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProyectoArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProyectosArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProyectoArchivo()!= null && getIdProyectoArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProyectosArchivosDto other = (TcKeetProyectosArchivosDto) obj;
    if (getIdProyectoArchivo() != other.idProyectoArchivo && (getIdProyectoArchivo() == null || !getIdProyectoArchivo().equals(other.idProyectoArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProyectoArchivo() != null ? getIdProyectoArchivo().hashCode() : 0);
    return hash;
  }
}