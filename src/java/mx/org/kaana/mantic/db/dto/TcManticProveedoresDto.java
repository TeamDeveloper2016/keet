package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
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
@Table(name="tc_mantic_proveedores")
public class TcManticProveedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_proveedor")
  private Long idTipoProveedor;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="clave")
  private String clave;
  @Column (name="dias_entrega")
  private Long diasEntrega;
  @Column (name="descuento")
  private String descuento;
  @Column (name="grupo")
  private String grupo;
  @Column (name="razon_social")
  private String razonSocial;
  @Column (name="nombre_comercial")
  private String nombreComercial;
  @Column (name="rfc")
  private String rfc;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_tipo_dia")
  private Long idTipoDia;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_moneda")
  private Long idTipoMoneda;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;  
  @Column (name="id_subcontratista")
  private Long idSubcontratista;  
  @Column (name="fondo_garantia")
  private Double fondoGarantia;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="comentarios")
  private String comentarios;

  public TcManticProveedoresDto() {
    this(new Long(-1L));
  }

  public TcManticProveedoresDto(Long key) {
    this(null, key, null, 5L, "0.00", null, null, null, 1L, null, null, null, null, null, 2L, 0D, 0D, null);
    setKey(key);
  }

  public TcManticProveedoresDto(Long idTipoProveedor, Long idProveedor, String clave, Long diasEntrega, String descuento, String grupo, String razonSocial, String rfc, Long idTipoDia, Long idUsuario, Long idTipoMoneda, String observaciones, Long idEmpresa, String nombreComercial, Long idSubcontratista, Double fondoGarantia, Double saldo, String comentarios) {
    setIdTipoProveedor(idTipoProveedor);
    setIdProveedor(idProveedor);
    setClave(clave);
    setDiasEntrega(diasEntrega);
    setDescuento(descuento);
    setGrupo(grupo);
    setRazonSocial(razonSocial);
    setRfc(rfc);
    setRegistro(LocalDateTime.now());
    setIdTipoDia(idTipoDia);
    setIdUsuario(idUsuario);
    setIdTipoMoneda(idTipoMoneda);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
		this.nombreComercial= nombreComercial;
		this.idSubcontratista= idSubcontratista;
    this.fondoGarantia= fondoGarantia;
    this.saldo= saldo;
    this.comentarios= comentarios;
  }
	
  public void setIdTipoProveedor(Long idTipoProveedor) {
    this.idTipoProveedor = idTipoProveedor;
  }

  public Long getIdTipoProveedor() {
    return idTipoProveedor;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setDiasEntrega(Long diasEntrega) {
    this.diasEntrega = diasEntrega;
  }

  public Long getDiasEntrega() {
    return diasEntrega;
  }

  public void setDescuento(String descuento) {
    this.descuento = descuento;
  }

  public String getDescuento() {
    return descuento;
  }

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo=grupo;
	}

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  public String getRazonSocial() {
    return razonSocial;
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

  public void setIdTipoDia(Long idTipoDia) {
    this.idTipoDia = idTipoDia;
  }

  public Long getIdTipoDia() {
    return idTipoDia;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdTipoMoneda(Long idTipoMoneda) {
    this.idTipoMoneda = idTipoMoneda;
  }

  public Long getIdTipoMoneda() {
    return idTipoMoneda;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

	public String getNombreComercial() {
		return nombreComercial;
	}

	public void setNombreComercial(String nombreComercial) {
		this.nombreComercial=nombreComercial;
	}

  public Long getIdSubcontratista() {
    return idSubcontratista;
  }

  public void setIdSubcontratista(Long idSubcontratista) {
    this.idSubcontratista = idSubcontratista;
  }
	
  public Double getFondoGarantia() {
    return fondoGarantia;
  }

  public void setFondoGarantia(Double fondoGarantia) {
    this.fondoGarantia = fondoGarantia;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public String getComentarios() {
    return comentarios;
  }

  public void setComentarios(String comentarios) {
    this.comentarios = comentarios;
  }
  
  @Transient
  @Override
  public Long getKey() {
  	return getIdProveedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idProveedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiasEntrega());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getGrupo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRazonSocial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRfc());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoDia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMoneda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());		
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombreComercial());		
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFondoGarantia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSubcontratista());		
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());		
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getComentarios());		
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoProveedor", getIdTipoProveedor());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("clave", getClave());
		regresar.put("diasEntrega", getDiasEntrega());
		regresar.put("descuento", getDescuento());
		regresar.put("grupo", getGrupo());
		regresar.put("razonSocial", getRazonSocial());
		regresar.put("rfc", getRfc());
		regresar.put("registro", getRegistro());
		regresar.put("idTipoDia", getIdTipoDia());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoMoneda", getIdTipoMoneda());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("nombreComercial", getNombreComercial());
		regresar.put("idSubcontratista", getIdSubcontratista());
		regresar.put("fondoGarantia", getFondoGarantia());
		regresar.put("saldo", getSaldo());
		regresar.put("comentarios", getComentarios());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdTipoProveedor(), getIdProveedor(), getClave(), getDiasEntrega(), getDescuento(), getGrupo(), getRazonSocial(), getRfc(), getRegistro(), getIdTipoDia(), getIdUsuario(), getIdTipoMoneda(), getObservaciones(), getIdEmpresa(), getNombreComercial(), getFondoGarantia(), getIdSubcontratista(), getSaldo(), getComentarios()
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
    regresar.append("idProveedor~");
    regresar.append(getIdProveedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProveedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticProveedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProveedor()!= null && getIdProveedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticProveedoresDto other = (TcManticProveedoresDto) obj;
    if (getIdProveedor() != other.idProveedor && (getIdProveedor() == null || !getIdProveedor().equals(other.idProveedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProveedor() != null ? getIdProveedor().hashCode() : 0);
    return hash;
  }

}


