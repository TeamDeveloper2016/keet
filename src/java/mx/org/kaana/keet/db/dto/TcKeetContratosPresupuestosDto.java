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
@Table(name="tc_keet_contratos_presupuestos")
public class TcKeetContratosPresupuestosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="archivo")
  private String archivo;
  @Column (name="ruta")
  private String ruta;
  @Column (name="tamanio")
  private Long tamanio;
  @Column (name="id_usuarios")
  private Long idUsuarios;
  @Column (name="id_tipo_archivo")
  private Long idTipoArchivo;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_tipo_presupuesto")
  private Long idTipoPresupuesto;
  @Column (name="alias")
  private String alias;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_presupuesto")
  private Long idContratoPresupuesto;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosPresupuestosDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosPresupuestosDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetContratosPresupuestosDto(String archivo, String ruta, Long tamanio, Long idUsuarios, Long idTipoArchivo, Long idContrato, String observaciones, Long idTipoPresupuesto, String alias, String nombre, Long idContratoPresupuesto) {
    setArchivo(archivo);
    setRuta(ruta);
    setTamanio(tamanio);
    setIdUsuarios(idUsuarios);
    setIdTipoArchivo(idTipoArchivo);
    setIdContrato(idContrato);
    setObservaciones(observaciones);
    setIdTipoPresupuesto(idTipoPresupuesto);
    setAlias(alias);
    setNombre(nombre);
    setIdContratoPresupuesto(idContratoPresupuesto);
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

  public void setIdUsuarios(Long idUsuarios) {
    this.idUsuarios = idUsuarios;
  }

  public Long getIdUsuarios() {
    return idUsuarios;
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

  public void setIdTipoPresupuesto(Long idTipoPresupuesto) {
    this.idTipoPresupuesto = idTipoPresupuesto;
  }

  public Long getIdTipoPresupuesto() {
    return idTipoPresupuesto;
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

  public void setIdContratoPresupuesto(Long idContratoPresupuesto) {
    this.idContratoPresupuesto = idContratoPresupuesto;
  }

  public Long getIdContratoPresupuesto() {
    return idContratoPresupuesto;
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
  	return getIdContratoPresupuesto();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoPresupuesto = key;
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
		regresar.append(getIdUsuarios());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoPresupuesto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoPresupuesto());
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
		regresar.put("idUsuarios", getIdUsuarios());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("idContrato", getIdContrato());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idTipoPresupuesto", getIdTipoPresupuesto());
		regresar.put("alias", getAlias());
		regresar.put("nombre", getNombre());
		regresar.put("idContratoPresupuesto", getIdContratoPresupuesto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getArchivo(), getRuta(), getTamanio(), getIdUsuarios(), getIdTipoArchivo(), getIdContrato(), getObservaciones(), getIdTipoPresupuesto(), getAlias(), getNombre(), getIdContratoPresupuesto(), getRegistro()
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
    regresar.append("idContratoPresupuesto~");
    regresar.append(getIdContratoPresupuesto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoPresupuesto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosPresupuestosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoPresupuesto()!= null && getIdContratoPresupuesto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosPresupuestosDto other = (TcKeetContratosPresupuestosDto) obj;
    if (getIdContratoPresupuesto() != other.idContratoPresupuesto && (getIdContratoPresupuesto() == null || !getIdContratoPresupuesto().equals(other.idContratoPresupuesto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoPresupuesto() != null ? getIdContratoPresupuesto().hashCode() : 0);
    return hash;
  }

}


