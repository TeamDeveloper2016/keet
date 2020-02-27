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
@Table(name="tc_keet_proyectos")
public class TcKeetProyectosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="clave")
  private String clave;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_tipos_obras")
  private Long idTiposObras;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proyecto")
  private Long idProyecto;
  @Column (name="etapa")
  private String etapa;
  @Column (name="orden")
  private Long orden;
  @Column (name="no_viviendas")
  private Long noViviendas;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetProyectosDto() {
    this(new Long(-1L));
  }

  public TcKeetProyectosDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetProyectosDto(String consecutivo, String clave, Long idCliente, Long idDesarrollo, Long idTiposObras, Long idUsuario, Long idProyecto, String etapa, Long orden, Long noViviendas, Long ejercicio) {
    setConsecutivo(consecutivo);
    setClave(clave);
    setIdCliente(idCliente);
    setIdDesarrollo(idDesarrollo);
    setIdTiposObras(idTiposObras);
    setIdUsuario(idUsuario);
    setIdProyecto(idProyecto);
    setEtapa(etapa);
    setOrden(orden);
    setNoViviendas(noViviendas);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdTiposObras(Long idTiposObras) {
    this.idTiposObras = idTiposObras;
  }

  public Long getIdTiposObras() {
    return idTiposObras;
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

  public void setEtapa(String etapa) {
    this.etapa = etapa;
  }

  public String getEtapa() {
    return etapa;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setNoViviendas(Long noViviendas) {
    this.noViviendas = noViviendas;
  }

  public Long getNoViviendas() {
    return noViviendas;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
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
  	return getIdProyecto();
  }

  @Override
  public void setKey(Long key) {
  	this.idProyecto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTiposObras());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyecto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEtapa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNoViviendas());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("clave", getClave());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idTiposObras", getIdTiposObras());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idProyecto", getIdProyecto());
		regresar.put("etapa", getEtapa());
		regresar.put("orden", getOrden());
		regresar.put("noViviendas", getNoViviendas());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getConsecutivo(), getClave(), getIdCliente(), getIdDesarrollo(), getIdTiposObras(), getIdUsuario(), getIdProyecto(), getEtapa(), getOrden(), getNoViviendas(), getEjercicio(), getRegistro()
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
    regresar.append("idProyecto~");
    regresar.append(getIdProyecto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProyecto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProyectosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProyecto()!= null && getIdProyecto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProyectosDto other = (TcKeetProyectosDto) obj;
    if (getIdProyecto() != other.idProyecto && (getIdProyecto() == null || !getIdProyecto().equals(other.idProyecto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProyecto() != null ? getIdProyecto().hashCode() : 0);
    return hash;
  }

}


