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
@Table(name="tc_keet_prototipos_archivos")
public class TcKeetPrototiposArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_plano")
  private Long idPlano;
  @Column (name="archivo")
  private String archivo;
  @Column (name="ruta")
  private String ruta;
  @Column (name="tamanio")
  private Long tamanio;
  @Column (name="id_usuarios")
  private Long idUsuarios;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prototipo_archivo")
  private Long idPrototipoArchivo;
  @Column (name="alias")
  private String alias;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPrototiposArchivosDto() {
    this(new Long(-1L));
  }

  public TcKeetPrototiposArchivosDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetPrototiposArchivosDto(Long idPlano, String archivo, String ruta, Long tamanio, Long idUsuarios, String observaciones, Long idPrototipoArchivo, String alias, Long idPrototipo, String nombre) {
    setIdPlano(idPlano);
    setArchivo(archivo);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuarios(idUsuarios);
    setObservaciones(observaciones);
    setIdPrototipoArchivo(idPrototipoArchivo);
    setAlias(alias);
    setIdPrototipo(idPrototipo);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
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

  public void setTamanio(Long tamanio) {
    this.tamanio = tamanio;
  }

  public Long getTamanio() {
    return tamanio;
  }

  public void setIdUsuarios(Long idUsuarios) {
    this.idUsuarios = idUsuarios;
  }

  public Long getIdUsuarios() {
    return idUsuarios;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdPrototipoArchivo(Long idPrototipoArchivo) {
    this.idPrototipoArchivo = idPrototipoArchivo;
  }

  public Long getIdPrototipoArchivo() {
    return idPrototipoArchivo;
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
  	return getIdPrototipoArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrototipoArchivo = key;
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
		regresar.append(getTamanio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuarios());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipoArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
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
		regresar.put("idPlano", getIdPlano());
		regresar.put("archivo", getArchivo());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuarios", getIdUsuarios());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idPrototipoArchivo", getIdPrototipoArchivo());
		regresar.put("alias", getAlias());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPlano(), getArchivo(), getRuta(), getTamanio(), getIdUsuarios(), getObservaciones(), getIdPrototipoArchivo(), getAlias(), getIdPrototipo(), getNombre(), getRegistro()
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
    regresar.append("idPrototipoArchivo~");
    regresar.append(getIdPrototipoArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrototipoArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrototiposArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrototipoArchivo()!= null && getIdPrototipoArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrototiposArchivosDto other = (TcKeetPrototiposArchivosDto) obj;
    if (getIdPrototipoArchivo() != other.idPrototipoArchivo && (getIdPrototipoArchivo() == null || !getIdPrototipoArchivo().equals(other.idPrototipoArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrototipoArchivo() != null ? getIdPrototipoArchivo().hashCode() : 0);
    return hash;
  }

}


