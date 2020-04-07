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
@Table(name="tc_mantic_incidentes_archivos")
public class TcManticIncidentesArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_incidente")
  private Long idIncidente;
  @Column (name="archivo")
  private String archivo;
  @Column (name="ruta")
  private String ruta;
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
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_incidente_archivo")
  private Long idIncidenteArchivo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticIncidentesArchivosDto() {
    this(new Long(-1L));
  }

  public TcManticIncidentesArchivosDto(Long key) {
    this(null, null, null, null, null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcManticIncidentesArchivosDto(Long idIncidente, String archivo, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idIncidenteArchivo, String nombre) {
    setIdIncidente(idIncidente);
    setArchivo(archivo);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setObservaciones(observaciones);
    setAlias(alias);
    setIdIncidenteArchivo(idIncidenteArchivo);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdIncidente(Long idIncidente) {
    this.idIncidente = idIncidente;
  }

  public Long getIdIncidente() {
    return idIncidente;
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

  public void setIdIncidenteArchivo(Long idIncidenteArchivo) {
    this.idIncidenteArchivo = idIncidenteArchivo;
  }

  public Long getIdIncidenteArchivo() {
    return idIncidenteArchivo;
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
  	return getIdIncidenteArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idIncidenteArchivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdIncidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRuta());
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
		regresar.append(getIdIncidenteArchivo());
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
		regresar.put("idIncidente", getIdIncidente());
		regresar.put("archivo", getArchivo());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("observaciones", getObservaciones());
		regresar.put("alias", getAlias());
		regresar.put("idIncidenteArchivo", getIdIncidenteArchivo());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdIncidente(), getArchivo(), getRuta(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getObservaciones(), getAlias(), getIdIncidenteArchivo(), getNombre(), getRegistro()
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
    regresar.append("idIncidenteArchivo~");
    regresar.append(getIdIncidenteArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIncidenteArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticIncidentesArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIncidenteArchivo()!= null && getIdIncidenteArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticIncidentesArchivosDto other = (TcManticIncidentesArchivosDto) obj;
    if (getIdIncidenteArchivo() != other.idIncidenteArchivo && (getIdIncidenteArchivo() == null || !getIdIncidenteArchivo().equals(other.idIncidenteArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIncidenteArchivo() != null ? getIdIncidenteArchivo().hashCode() : 0);
    return hash;
  }

}


