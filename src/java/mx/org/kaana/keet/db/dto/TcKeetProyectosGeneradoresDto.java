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
@Table(name="tc_keet_proyectos_generadores")
public class TcKeetProyectosGeneradoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_generador")
  private Long idTipoGenerador;
  @Column (name="archivo")
  private String archivo;
  @Column (name="ruta")
  private String ruta;
  @Column (name="tamanio")
  private Long tamanio;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_proyecto")
  private Long idProyecto;
  @Column (name="id_tipo_archivo")
  private Long idTipoArchivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proyecto_generador")
  private Long idProyectoGenerador;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="alias")
  private String alias;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetProyectosGeneradoresDto() {
    this(new Long(-1L));
  }

  public TcKeetProyectosGeneradoresDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetProyectosGeneradoresDto(Long idTipoGenerador, String archivo, String ruta, Long tamanio, Long idUsuario, Long idProyecto, Long idTipoArchivo, Long idProyectoGenerador, String observaciones, String alias, String nombre) {
    setIdTipoGenerador(idTipoGenerador);
    setArchivo(archivo);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdProyecto(idProyecto);
    setIdTipoArchivo(idTipoArchivo);
    setIdProyectoGenerador(idProyectoGenerador);
    setObservaciones(observaciones);
    setAlias(alias);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdTipoGenerador(Long idTipoGenerador) {
    this.idTipoGenerador = idTipoGenerador;
  }

  public Long getIdTipoGenerador() {
    return idTipoGenerador;
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

  public void setIdProyecto(Long idProyecto) {
    this.idProyecto = idProyecto;
  }

  public Long getIdProyecto() {
    return idProyecto;
  }

  public void setIdTipoArchivo(Long idTipoArchivo) {
    this.idTipoArchivo = idTipoArchivo;
  }

  public Long getIdTipoArchivo() {
    return idTipoArchivo;
  }

  public void setIdProyectoGenerador(Long idProyectoGenerador) {
    this.idProyectoGenerador = idProyectoGenerador;
  }

  public Long getIdProyectoGenerador() {
    return idProyectoGenerador;
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
  	return getIdProyectoGenerador();
  }

  @Override
  public void setKey(Long key) {
  	this.idProyectoGenerador = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoGenerador());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRuta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTamanio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyecto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoGenerador());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
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
		regresar.put("idTipoGenerador", getIdTipoGenerador());
		regresar.put("archivo", getArchivo());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idProyecto", getIdProyecto());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("idProyectoGenerador", getIdProyectoGenerador());
		regresar.put("observaciones", getObservaciones());
		regresar.put("alias", getAlias());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoGenerador(), getArchivo(), getRuta(), getTamanio(), getIdUsuario(), getIdProyecto(), getIdTipoArchivo(), getIdProyectoGenerador(), getObservaciones(), getAlias(), getNombre(), getRegistro()
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
    regresar.append("idProyectoGenerador~");
    regresar.append(getIdProyectoGenerador());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProyectoGenerador());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProyectosGeneradoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProyectoGenerador()!= null && getIdProyectoGenerador()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProyectosGeneradoresDto other = (TcKeetProyectosGeneradoresDto) obj;
    if (getIdProyectoGenerador() != other.idProyectoGenerador && (getIdProyectoGenerador() == null || !getIdProyectoGenerador().equals(other.idProyectoGenerador))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProyectoGenerador() != null ? getIdProyectoGenerador().hashCode() : 0);
    return hash;
  }

}


