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
@Table(name="tc_keet_contratos_generadores")
public class TcKeetContratosGeneradoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_generador")
  private Long idTipoGenerador;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_generador")
  private Long idContratoGenerador;
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
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="alias")
  private String alias;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosGeneradoresDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosGeneradoresDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosGeneradoresDto(Long idTipoGenerador, Long idContratoGenerador, String archivo, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, Long idContrato, String observaciones, String alias, String nombre) {
    setIdTipoGenerador(idTipoGenerador);
    setIdContratoGenerador(idContratoGenerador);
    setArchivo(archivo);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setIdContrato(idContrato);
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

  public void setIdContratoGenerador(Long idContratoGenerador) {
    this.idContratoGenerador = idContratoGenerador;
  }

  public Long getIdContratoGenerador() {
    return idContratoGenerador;
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

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
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
  	return getIdContratoGenerador();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoGenerador = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoGenerador());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoGenerador());
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
		regresar.append(getIdContrato());
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
		regresar.put("idContratoGenerador", getIdContratoGenerador());
		regresar.put("archivo", getArchivo());
		regresar.put("ruta", getRuta());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("idContrato", getIdContrato());
		regresar.put("observaciones", getObservaciones());
		regresar.put("alias", getAlias());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoGenerador(), getIdContratoGenerador(), getArchivo(), getRuta(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getIdContrato(), getObservaciones(), getAlias(), getNombre(), getRegistro()
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
    regresar.append("idContratoGenerador~");
    regresar.append(getIdContratoGenerador());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoGenerador());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosGeneradoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoGenerador()!= null && getIdContratoGenerador()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosGeneradoresDto other = (TcKeetContratosGeneradoresDto) obj;
    if (getIdContratoGenerador() != other.idContratoGenerador && (getIdContratoGenerador() == null || !getIdContratoGenerador().equals(other.idContratoGenerador))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoGenerador() != null ? getIdContratoGenerador().hashCode() : 0);
    return hash;
  }

}


