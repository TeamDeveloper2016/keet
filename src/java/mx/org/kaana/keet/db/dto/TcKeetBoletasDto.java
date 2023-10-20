package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
@Table(name="tc_keet_boletas")
public class TcKeetBoletasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_solicito")
  private Long idSolicito;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_almacen")
  private Long idAlmacen;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_boleta_estatus")
  private Long idBoletaEstatus;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_boleta")
  private Long idBoleta;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetBoletasDto() {
    this(new Long(-1L));
  }

  public TcKeetBoletasDto(Long key) {
    this(null, null, null, null, null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetBoletasDto(String consecutivo, Long idSolicito, Long idUsuario, Long idAlmacen, String observaciones, Long idBoletaEstatus, Long idEmpresa, Long orden, Long idBoleta, Long ejercicio) {
    setConsecutivo(consecutivo);
    setIdSolicito(idSolicito);
    setIdUsuario(idUsuario);
    setIdAlmacen(idAlmacen);
    setObservaciones(observaciones);
    setIdBoletaEstatus(idBoletaEstatus);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setIdBoleta(idBoleta);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdSolicito(Long idSolicito) {
    this.idSolicito = idSolicito;
  }

  public Long getIdSolicito() {
    return idSolicito;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdAlmacen(Long idAlmacen) {
    this.idAlmacen = idAlmacen;
  }

  public Long getIdAlmacen() {
    return idAlmacen;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdBoletaEstatus(Long idBoletaEstatus) {
    this.idBoletaEstatus = idBoletaEstatus;
  }

  public Long getIdBoletaEstatus() {
    return idBoletaEstatus;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdBoleta(Long idBoleta) {
    this.idBoleta = idBoleta;
  }

  public Long getIdBoleta() {
    return idBoleta;
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
  	return getIdBoleta();
  }

  @Override
  public void setKey(Long key) {
  	this.idBoleta = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSolicito());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBoletaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBoleta());
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
		regresar.put("idSolicito", getIdSolicito());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAlmacen", getIdAlmacen());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idBoletaEstatus", getIdBoletaEstatus());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("idBoleta", getIdBoleta());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getConsecutivo(), getIdSolicito(), getIdUsuario(), getIdAlmacen(), getObservaciones(), getIdBoletaEstatus(), getIdEmpresa(), getOrden(), getIdBoleta(), getEjercicio(), getRegistro()
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
    regresar.append("idBoleta~");
    regresar.append(getIdBoleta());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdBoleta());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetBoletasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdBoleta()!= null && getIdBoleta()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetBoletasDto other = (TcKeetBoletasDto) obj;
    if (getIdBoleta() != other.idBoleta && (getIdBoleta() == null || !getIdBoleta().equals(other.idBoleta))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdBoleta() != null ? getIdBoleta().hashCode() : 0);
    return hash;
  }

}


