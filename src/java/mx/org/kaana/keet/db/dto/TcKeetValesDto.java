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
@Table(name="tc_keet_vales")
public class TcKeetValesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_vale_contratista")
  private Long idValeContratista;
  @Column (name="costo")
  private Double costo;
  @Column (name="semana")
  private Long semana;
  @Column (name="id_contrato_lote_proveedor")
  private Long idContratoLoteProveedor;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_vale")
  private Long idVale;
  @Column (name="id_tipo_vale")
  private Long idTipoVale;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_almacen")
  private Long idAlmacen;
  @Column (name="orden")
  private Long orden;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_contrato_lote_contratista")
  private Long idContratoLoteContratista;
  @Column (name="id_vale_estatus")
  private Long idValeEstatus;

  public TcKeetValesDto() {
    this(new Long(-1L));
  }

  public TcKeetValesDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetValesDto(String justificacion, Long idValeContratista, Double costo, Long semana, Long idContratoLoteProveedor, Long idVale, Long idTipoVale, Long ejercicio, String consecutivo, Long idUsuario, Long idAlmacen, Long orden, Double cantidad, Long idContratoLoteContratista, Long idValeEstatus) {
    setJustificacion(justificacion);
    setIdValeContratista(idValeContratista);
    setCosto(costo);
    setSemana(semana);
    setIdContratoLoteProveedor(idContratoLoteProveedor);
    setIdVale(idVale);
    setIdTipoVale(idTipoVale);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setIdUsuario(idUsuario);
    setIdAlmacen(idAlmacen);
    setOrden(orden);
    setCantidad(cantidad);
    setIdContratoLoteContratista(idContratoLoteContratista);
		setIdValeEstatus(idValeEstatus);
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdValeContratista(Long idValeContratista) {
    this.idValeContratista = idValeContratista;
  }

  public Long getIdValeContratista() {
    return idValeContratista;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setSemana(Long semana) {
    this.semana = semana;
  }

  public Long getSemana() {
    return semana;
  }

  public void setIdContratoLoteProveedor(Long idContratoLoteProveedor) {
    this.idContratoLoteProveedor = idContratoLoteProveedor;
  }

  public Long getIdContratoLoteProveedor() {
    return idContratoLoteProveedor;
  }

  public void setIdVale(Long idVale) {
    this.idVale = idVale;
  }

  public Long getIdVale() {
    return idVale;
  }

  public void setIdTipoVale(Long idTipoVale) {
    this.idTipoVale = idTipoVale;
  }

  public Long getIdTipoVale() {
    return idTipoVale;
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

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
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

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setIdContratoLoteContratista(Long idContratoLoteContratista) {
    this.idContratoLoteContratista = idContratoLoteContratista;
  }

  public Long getIdContratoLoteContratista() {
    return idContratoLoteContratista;
  }

	public Long getIdValeEstatus() {
		return idValeEstatus;
	}

	public void setIdValeEstatus(Long idValeEstatus) {
		this.idValeEstatus = idValeEstatus;
	}	
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdVale();
  }

  @Override
  public void setKey(Long key) {
  	this.idVale = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdValeContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSemana());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVale());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoVale());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdValeEstatus());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idValeContratista", getIdValeContratista());
		regresar.put("costo", getCosto());
		regresar.put("semana", getSemana());
		regresar.put("idContratoLoteProveedor", getIdContratoLoteProveedor());
		regresar.put("idVale", getIdVale());
		regresar.put("idTipoVale", getIdTipoVale());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAlmacen", getIdAlmacen());
		regresar.put("orden", getOrden());
		regresar.put("cantidad", getCantidad());
		regresar.put("idContratoLoteContratista", getIdContratoLoteContratista());
		regresar.put("idValeEstatus", getIdValeEstatus());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getJustificacion(), getIdValeContratista(), getCosto(), getSemana(), getIdContratoLoteProveedor(), getIdVale(), getIdTipoVale(), getEjercicio(), getRegistro(), getConsecutivo(), getIdUsuario(), getIdAlmacen(), getOrden(), getCantidad(), getIdContratoLoteContratista(), getIdValeEstatus()
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
    regresar.append("idVale~");
    regresar.append(getIdVale());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdVale());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetValesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdVale()!= null && getIdVale()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetValesDto other = (TcKeetValesDto) obj;
    if (getIdVale() != other.idVale && (getIdVale() == null || !getIdVale().equals(other.idVale))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdVale() != null ? getIdVale().hashCode() : 0);
    return hash;
  }
}