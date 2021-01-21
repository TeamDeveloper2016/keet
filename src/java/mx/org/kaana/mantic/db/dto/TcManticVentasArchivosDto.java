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
@Table(name="tc_mantic_ventas_archivos")
public class TcManticVentasArchivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="archivo")
  private String archivo;
  @Column (name="ruta")
  private String ruta;
  @Column (name="nombre")
  private String nombre;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_venta_archivo")
  private Long idVentaArchivo;
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
  @Column (name="mes")
  private Long mes;
  @Column (name="id_venta")
  private Long idVenta;

  public TcManticVentasArchivosDto() {
    this(new Long(-1L));
  }

  public TcManticVentasArchivosDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcManticVentasArchivosDto(Long idVentaArchivo, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String alias, Long mes, Long idVenta, String nombre, String observaciones, Long ejercicio, Long idPrincipal, String archivo) {
    setArchivo(archivo);
    setRuta(ruta);
    setNombre(nombre);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setIdVentaArchivo(idVentaArchivo);
    setTamanio(tamanio);
    setIdUsuario(idUsuario);
    setIdTipoArchivo(idTipoArchivo);
    setIdPrincipal(idPrincipal);
    setObservaciones(observaciones);
    setAlias(alias);
    setMes(mes);
    setIdVenta(idVenta);
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

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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

  public void setIdVentaArchivo(Long idVentaArchivo) {
    this.idVentaArchivo = idVentaArchivo;
  }

  public Long getIdVentaArchivo() {
    return idVentaArchivo;
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

  public void setMes(Long mes) {
    this.mes = mes;
  }

  public Long getMes() {
    return mes;
  }

  public void setIdVenta(Long idVenta) {
    this.idVenta = idVenta;
  }

  public Long getIdVenta() {
    return idVenta;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdVentaArchivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idVentaArchivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getArchivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRuta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVentaArchivo());
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
		regresar.append(getMes());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVenta());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("archivo", getArchivo());
		regresar.put("ruta", getRuta());
		regresar.put("nombre", getNombre());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("idVentaArchivo", getIdVentaArchivo());
		regresar.put("tamanio", getTamanio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoArchivo", getIdTipoArchivo());
		regresar.put("idPrincipal", getIdPrincipal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("alias", getAlias());
		regresar.put("mes", getMes());
		regresar.put("idVenta", getIdVenta());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getArchivo(), getRuta(), getNombre(), getEjercicio(), getRegistro(), getIdVentaArchivo(), getTamanio(), getIdUsuario(), getIdTipoArchivo(), getIdPrincipal(), getObservaciones(), getAlias(), getMes(), getIdVenta()
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
    regresar.append("idVentaArchivo~");
    regresar.append(getIdVentaArchivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdVentaArchivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticVentasArchivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdVentaArchivo()!= null && getIdVentaArchivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticVentasArchivosDto other = (TcManticVentasArchivosDto) obj;
    if (getIdVentaArchivo() != other.idVentaArchivo && (getIdVentaArchivo() == null || !getIdVentaArchivo().equals(other.idVentaArchivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdVentaArchivo() != null ? getIdVentaArchivo().hashCode() : 0);
    return hash;
  }

}


