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
@Table(name="tc_keet_contratos_lotes_archivos")
public class TcKeetContratosLotesArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_lote_archivo")
  private Long idContratoLoteArchivo;
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
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="alias")
  private String alias;
  @Column (name="nombre")
  private String nombre;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosLotesArchivosDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosLotesArchivosDto(Long key) {
    this(new Long(-1L), null, LocalDateTime.now(), null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosLotesArchivosDto(Long idContratoLoteArchivo, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, Long idContratoLote, String alias, String nombre, String observaciones) {
    setIdContratoLoteArchivo(idContratoLoteArchivo);
    setArchivo(archivo);
    setEliminado(eliminado);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setIdContratoLote(idContratoLote);
    setAlias(alias);
    setNombre(nombre);
    setObservaciones(observaciones);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdContratoLoteArchivo(Long idContratoLoteArchivo) {
    this.idContratoLoteArchivo = idContratoLoteArchivo;
  }

  public Long getIdContratoLoteArchivo() {
    return idContratoLoteArchivo;
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

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
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

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
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
  	return getIdContratoLoteArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoLoteArchivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoLoteArchivo());
		regresar.append(Constantes.SEPARADOR);
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
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idContratoLoteArchivo", getIdContratoLoteArchivo());
		regresar.put("archivo", getArchivo());
		regresar.put("eliminado", getEliminado());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("alias", getAlias());
		regresar.put("nombre", getNombre());
		regresar.put("observaciones", getObservaciones());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdContratoLoteArchivo(), getArchivo(), getEliminado(), getRuta(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getIdContratoLote(), getAlias(), getNombre(), getObservaciones(), getRegistro()
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
    regresar.append("idContratoLoteArchivo~");
    regresar.append(getIdContratoLoteArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoLoteArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosLotesArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoLoteArchivo()!= null && getIdContratoLoteArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosLotesArchivosDto other = (TcKeetContratosLotesArchivosDto) obj;
    if (getIdContratoLoteArchivo() != other.idContratoLoteArchivo && (getIdContratoLoteArchivo() == null || !getIdContratoLoteArchivo().equals(other.idContratoLoteArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoLoteArchivo() != null ? getIdContratoLoteArchivo().hashCode() : 0);
    return hash;
  }

}


