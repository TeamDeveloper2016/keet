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
@Table(name="tc_keet_contratos_contratistas_archivos")
public class TcKeetContratosContratistasArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_contratista_archivo")
  private Long idContratoContratistaArchivo;
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
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="alias")
  private String alias;
  @Column (name="id_contrato_destajo_contratista")
  private Long idContratoDestajoContratista;
  @Column (name="nombre")
  private String nombre;
  @Column (name="codigo")
  private String codigo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosContratistasArchivosDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosContratistasArchivosDto(Long key) {
    this(new Long(-1L), null, LocalDateTime.now(), null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosContratistasArchivosDto(Long idContratoContratistaArchivo, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idContratoDestajoContratista, String nombre, String codigo) {
    setIdContratoContratistaArchivo(idContratoContratistaArchivo);
    setArchivo(archivo);
    setEliminado(eliminado);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setObservaciones(observaciones);
    setAlias(alias);
    setIdContratoDestajoContratista(idContratoDestajoContratista);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
    this.codigo= codigo;
  }
	
  public void setIdContratoContratistaArchivo(Long idContratoContratistaArchivo) {
    this.idContratoContratistaArchivo = idContratoContratistaArchivo;
  }

  public Long getIdContratoContratistaArchivo() {
    return idContratoContratistaArchivo;
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

  public void setIdContratoDestajoContratista(Long idContratoDestajoContratista) {
    this.idContratoDestajoContratista = idContratoDestajoContratista;
  }

  public Long getIdContratoDestajoContratista() {
    return idContratoDestajoContratista;
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

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdContratoContratistaArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoContratistaArchivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoContratistaArchivo());
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
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idContratoContratistaArchivo", getIdContratoContratistaArchivo());
		regresar.put("archivo", getArchivo());
		regresar.put("eliminado", getEliminado());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("observaciones", getObservaciones());
		regresar.put("alias", getAlias());
		regresar.put("idContratoDestajoContratista", getIdContratoDestajoContratista());
		regresar.put("nombre", getNombre());
		regresar.put("codigo", getCodigo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdContratoContratistaArchivo(), getArchivo(), getEliminado(), getRuta(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getObservaciones(), getAlias(), getIdContratoDestajoContratista(), getNombre(), getRegistro(), getCodigo()
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
    regresar.append("idContratoContratistaArchivo~");
    regresar.append(getIdContratoContratistaArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoContratistaArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosContratistasArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoContratistaArchivo()!= null && getIdContratoContratistaArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosContratistasArchivosDto other = (TcKeetContratosContratistasArchivosDto) obj;
    if (getIdContratoContratistaArchivo() != other.idContratoContratistaArchivo && (getIdContratoContratistaArchivo() == null || !getIdContratoContratistaArchivo().equals(other.idContratoContratistaArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoContratistaArchivo() != null ? getIdContratoContratistaArchivo().hashCode() : 0);
    return hash;
  }
}