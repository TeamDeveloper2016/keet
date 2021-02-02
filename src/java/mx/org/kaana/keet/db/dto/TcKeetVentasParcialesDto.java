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
@Table(name="tc_keet_ventas_parciales")
public class TcKeetVentasParcialesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="codigo_postal")
  private String codigoPostal;
  @Column (name="id_localidad")
  private Long idLocalidad;
  @Column (name="calle")
  private String calle;
  @Column (name="numero_exterior")
  private String numeroExterior;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_venta_parcial")
  private Long idVentaParcial;
  @Column (name="colonia")
  private String colonia;
  @Column (name="numero_interior")
  private String numeroInterior;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="permiso")
  private String permiso;
  @Column (name="id_venta")
  private Long idVenta;
  @Column (name="referencia")
  private String referencia;

  public TcKeetVentasParcialesDto() {
    this(new Long(-1L));
  }

  public TcKeetVentasParcialesDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetVentasParcialesDto(String codigoPostal, Long idLocalidad, String calle, String numeroExterior, Long idVentaParcial, String colonia, String numeroInterior, Long idUsuario, Long idContratoLote, String permiso, Long idVenta, String referencia) {
    setCodigoPostal(codigoPostal);
    setIdLocalidad(idLocalidad);
    setCalle(calle);
    setNumeroExterior(numeroExterior);
    setIdVentaParcial(idVentaParcial);
    setColonia(colonia);
    setNumeroInterior(numeroInterior);
    setRegistro(LocalDateTime.now());
    setIdUsuario(idUsuario);
    setIdContratoLote(idContratoLote);
    setPermiso(permiso);
    setIdVenta(idVenta);
    setReferencia(referencia);
  }
	
  public void setCodigoPostal(String codigoPostal) {
    this.codigoPostal = codigoPostal;
  }

  public String getCodigoPostal() {
    return codigoPostal;
  }

  public void setIdLocalidad(Long idLocalidad) {
    this.idLocalidad = idLocalidad;
  }

  public Long getIdLocalidad() {
    return idLocalidad;
  }

  public void setCalle(String calle) {
    this.calle = calle;
  }

  public String getCalle() {
    return calle;
  }

  public void setNumeroExterior(String numeroExterior) {
    this.numeroExterior = numeroExterior;
  }

  public String getNumeroExterior() {
    return numeroExterior;
  }

  public void setIdVentaParcial(Long idVentaParcial) {
    this.idVentaParcial = idVentaParcial;
  }

  public Long getIdVentaParcial() {
    return idVentaParcial;
  }

  public void setColonia(String colonia) {
    this.colonia = colonia;
  }

  public String getColonia() {
    return colonia;
  }

  public void setNumeroInterior(String numeroInterior) {
    this.numeroInterior = numeroInterior;
  }

  public String getNumeroInterior() {
    return numeroInterior;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setPermiso(String permiso) {
    this.permiso = permiso;
  }

  public String getPermiso() {
    return permiso;
  }

  public void setIdVenta(Long idVenta) {
    this.idVenta = idVenta;
  }

  public Long getIdVenta() {
    return idVenta;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getReferencia() {
    return referencia;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdVentaParcial();
  }

  @Override
  public void setKey(Long key) {
  	this.idVentaParcial = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCodigoPostal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLocalidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNumeroExterior());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVentaParcial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getColonia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNumeroInterior());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPermiso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("codigoPostal", getCodigoPostal());
		regresar.put("idLocalidad", getIdLocalidad());
		regresar.put("calle", getCalle());
		regresar.put("numeroExterior", getNumeroExterior());
		regresar.put("idVentaParcial", getIdVentaParcial());
		regresar.put("colonia", getColonia());
		regresar.put("numeroInterior", getNumeroInterior());
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("permiso", getPermiso());
		regresar.put("idVenta", getIdVenta());
		regresar.put("referencia", getReferencia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getCodigoPostal(), getIdLocalidad(), getCalle(), getNumeroExterior(), getIdVentaParcial(), getColonia(), getNumeroInterior(), getRegistro(), getIdUsuario(), getIdContratoLote(), getPermiso(), getIdVenta(), getReferencia()
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
    regresar.append("idVentaParcial~");
    regresar.append(getIdVentaParcial());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdVentaParcial());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetVentasParcialesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdVentaParcial()!= null && getIdVentaParcial()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetVentasParcialesDto other = (TcKeetVentasParcialesDto) obj;
    if (getIdVentaParcial() != other.idVentaParcial && (getIdVentaParcial() == null || !getIdVentaParcial().equals(other.idVentaParcial))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdVentaParcial() != null ? getIdVentaParcial().hashCode() : 0);
    return hash;
  }

}


