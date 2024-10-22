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
@Table(name="tc_keet_notas_facturas")
public class TcKeetNotasFacturasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nota_factura")
  private Long idNotaFactura;
  @Column (name="cadena_original")
  private String cadenaOriginal;
  @Column (name="folio_fiscal")
  private String folioFiscal;
  @Column (name="sello_digital")
  private String selloDigital;
  @Column (name="forma_pago")
  private String formaPago;
  @Column (name="razon_social")
  private String razonSocial;
  @Column (name="id_nota_entrada")
  private Long idNotaEntrada;
  @Column (name="lugar_expedicion")
  private String lugarExpedicion;
  @Column (name="version")
  private String version;
  @Column (name="certificado")
  private String certificado;
  @Column (name="rfc")
  private String rfc;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="total")
  private Double total;
  @Column (name="factura")
  private String factura;
  @Column (name="timbrado")
  private LocalDateTime timbrado;
  @Column (name="iva")
  private Double iva;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="serie")
  private String serie;
  @Column (name="moneda")
  private String moneda;
  @Column (name="tipo_cambio")
  private String tipoCambio;
  @Column (name="metodo_pago")
  private String metodoPago;
  @Column (name="regimen")
  private String regimen;

  public TcKeetNotasFacturasDto() {
    this(new Long(-1L));
  }

  public TcKeetNotasFacturasDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null, null, null, null, null, null, null, LocalDateTime.now(), null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetNotasFacturasDto(Long idNotaFactura, String cadenaOriginal, String folioFiscal, String selloDigital, String formaPago, String razonSocial, Long idNotaEntrada, String lugarExpedicion, String version, String certificado, String rfc, Double total, String factura, LocalDateTime timbrado, Double iva, Long idUsuario, Double subtotal, String serie, String moneda, String tipoCambio, String metodoPago, String regimen) {
    setIdNotaFactura(idNotaFactura);
    setCadenaOriginal(cadenaOriginal);
    setFolioFiscal(folioFiscal);
    setSelloDigital(selloDigital);
    setFormaPago(formaPago);
    setRazonSocial(razonSocial);
    setIdNotaEntrada(idNotaEntrada);
    setLugarExpedicion(lugarExpedicion);
    setVersion(version);
    setCertificado(certificado);
    setRfc(rfc);
    setRegistro(LocalDateTime.now());
    setTotal(total);
    setFactura(factura);
    setTimbrado(timbrado);
    setIva(iva);
    setIdUsuario(idUsuario);
    setSubtotal(subtotal);
    setSerie(serie);
    setMoneda(moneda);
    setTipoCambio(tipoCambio);
    setMetodoPago(metodoPago);
    setRegimen(regimen);
  }
	
  public void setIdNotaFactura(Long idNotaFactura) {
    this.idNotaFactura = idNotaFactura;
  }

  public Long getIdNotaFactura() {
    return idNotaFactura;
  }

  public void setCadenaOriginal(String cadenaOriginal) {
    this.cadenaOriginal = cadenaOriginal;
  }

  public String getCadenaOriginal() {
    return cadenaOriginal;
  }

  public void setFolioFiscal(String folioFiscal) {
    this.folioFiscal = folioFiscal;
  }

  public String getFolioFiscal() {
    return folioFiscal;
  }

  public void setSelloDigital(String selloDigital) {
    this.selloDigital = selloDigital;
  }

  public String getSelloDigital() {
    return selloDigital;
  }

  public void setFormaPago(String formaPago) {
    this.formaPago = formaPago;
  }

  public String getFormaPago() {
    return formaPago;
  }

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  public String getRazonSocial() {
    return razonSocial;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
  }

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
  }

  public void setLugarExpedicion(String lugarExpedicion) {
    this.lugarExpedicion = lugarExpedicion;
  }

  public String getLugarExpedicion() {
    return lugarExpedicion;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public void setCertificado(String certificado) {
    this.certificado = certificado;
  }

  public String getCertificado() {
    return certificado;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }

  public String getRfc() {
    return rfc;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setFactura(String factura) {
    this.factura = factura;
  }

  public String getFactura() {
    return factura;
  }

  public void setTimbrado(LocalDateTime timbrado) {
    this.timbrado = timbrado;
  }

  public LocalDateTime getTimbrado() {
    return timbrado;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setSerie(String serie) {
    this.serie = serie;
  }

  public String getSerie() {
    return serie;
  }

  public void setMoneda(String moneda) {
    this.moneda = moneda;
  }

  public String getMoneda() {
    return moneda;
  }

  public void setTipoCambio(String tipoCambio) {
    this.tipoCambio = tipoCambio;
  }

  public String getTipoCambio() {
    return tipoCambio;
  }

  public void setMetodoPago(String metodoPago) {
    this.metodoPago = metodoPago;
  }

  public String getMetodoPago() {
    return metodoPago;
  }

  public void setRegimen(String regimen) {
    this.regimen = regimen;
  }

  public String getRegimen() {
    return regimen;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNotaFactura();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaFactura = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdNotaFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCadenaOriginal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFolioFiscal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSelloDigital());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFormaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRazonSocial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEntrada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLugarExpedicion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getVersion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCertificado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRfc());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTimbrado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSerie());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMoneda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTipoCambio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMetodoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegimen());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idNotaFactura", getIdNotaFactura());
		regresar.put("cadenaOriginal", getCadenaOriginal());
		regresar.put("folioFiscal", getFolioFiscal());
		regresar.put("selloDigital", getSelloDigital());
		regresar.put("formaPago", getFormaPago());
		regresar.put("razonSocial", getRazonSocial());
		regresar.put("idNotaEntrada", getIdNotaEntrada());
		regresar.put("lugarExpedicion", getLugarExpedicion());
		regresar.put("version", getVersion());
		regresar.put("certificado", getCertificado());
		regresar.put("rfc", getRfc());
		regresar.put("registro", getRegistro());
		regresar.put("total", getTotal());
		regresar.put("factura", getFactura());
		regresar.put("timbrado", getTimbrado());
		regresar.put("iva", getIva());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("subtotal", getSubtotal());
		regresar.put("serie", getSerie());
		regresar.put("moneda", getMoneda());
		regresar.put("tipoCambio", getTipoCambio());
		regresar.put("metodoPago", getMetodoPago());
		regresar.put("regimen", getRegimen());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdNotaFactura(), getCadenaOriginal(), getFolioFiscal(), getSelloDigital(), getFormaPago(), getRazonSocial(), getIdNotaEntrada(), getLugarExpedicion(), getVersion(), getCertificado(), getRfc(), getRegistro(), getTotal(), getFactura(), getTimbrado(), getIva(), getIdUsuario(), getSubtotal(), getSerie(), getMoneda(), getTipoCambio(), getMetodoPago(), getRegimen()
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
    regresar.append("idNotaFactura~");
    regresar.append(getIdNotaFactura());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaFactura());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNotasFacturasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaFactura()!= null && getIdNotaFactura()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNotasFacturasDto other = (TcKeetNotasFacturasDto) obj;
    if (getIdNotaFactura() != other.idNotaFactura && (getIdNotaFactura() == null || !getIdNotaFactura().equals(other.idNotaFactura))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaFactura() != null ? getIdNotaFactura().hashCode() : 0);
    return hash;
  }

}


