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
@Table(name="tc_keet_anticipos_archivos")
public class TcKeetAnticiposArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
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
  @Column (name="id_principal")
  private Long idPrincipal;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="alias")
  private String alias;
  @Column (name="id_anticipo_pago")
  private Long idAnticipoPago;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_anticipo_archivo")
  private Long idAnticipoArchivo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetAnticiposArchivosDto() {
    this(new Long(-1L));
  }

  public TcKeetAnticiposArchivosDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetAnticiposArchivosDto(String archivo, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, Long idPrincipal, String observaciones, String alias, Long idAnticipoPago, String nombre, Long idAnticipoArchivo) {
    setArchivo(archivo);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setIdPrincipal(idPrincipal);
    setObservaciones(observaciones);
    setAlias(alias);
    setIdAnticipoPago(idAnticipoPago);
    setNombre(nombre);
    setIdAnticipoArchivo(idAnticipoArchivo);
    setRegistro(LocalDateTime.now());
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

  public void setIdPrincipal(Long idPrincipal) {
    this.idPrincipal = idPrincipal;
  }

  public Long getIdPrincipal() {
    return idPrincipal;
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

  public void setIdAnticipoPago(Long idAnticipoPago) {
    this.idAnticipoPago = idAnticipoPago;
  }

  public Long getIdAnticipoPago() {
    return idAnticipoPago;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdAnticipoArchivo(Long idAnticipoArchivo) {
    this.idAnticipoArchivo = idAnticipoArchivo;
  }

  public Long getIdAnticipoArchivo() {
    return idAnticipoArchivo;
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
  	return getIdAnticipoArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idAnticipoArchivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
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
		regresar.append(getIdPrincipal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("archivo", getArchivo());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("idPrincipal", getIdPrincipal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("alias", getAlias());
		regresar.put("idAnticipoPago", getIdAnticipoPago());
		regresar.put("nombre", getNombre());
		regresar.put("idAnticipoArchivo", getIdAnticipoArchivo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getArchivo(), getRuta(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getIdPrincipal(), getObservaciones(), getAlias(), getIdAnticipoPago(), getNombre(), getIdAnticipoArchivo(), getRegistro()
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
    regresar.append("idAnticipoArchivo~");
    regresar.append(getIdAnticipoArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAnticipoArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAnticiposArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAnticipoArchivo()!= null && getIdAnticipoArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAnticiposArchivosDto other = (TcKeetAnticiposArchivosDto) obj;
    if (getIdAnticipoArchivo() != other.idAnticipoArchivo && (getIdAnticipoArchivo() == null || !getIdAnticipoArchivo().equals(other.idAnticipoArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAnticipoArchivo() != null ? getIdAnticipoArchivo().hashCode() : 0);
    return hash;
  }

}


