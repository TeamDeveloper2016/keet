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
@Table(name="tc_mantic_articulos_codigos")
public class TcManticArticulosCodigosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_principal")
  private Long idPrincipal;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_articulo_codigo")
  private Long idArticuloCodigo;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="multiplo")
  private Long multiplo;

  public TcManticArticulosCodigosDto() {
    this(new Long(-1L));
  }

  public TcManticArticulosCodigosDto(Long key) {
    this(null, null, null, 2L, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcManticArticulosCodigosDto(String codigo, Long idProveedor, Long idUsuario, Long idPrincipal, String observaciones, Long idArticuloCodigo, Long orden, Long idArticulo) {
 		this(codigo, idProveedor, idUsuario, idPrincipal, observaciones, idArticuloCodigo, orden, idArticulo, 1L);
	}
	
  public TcManticArticulosCodigosDto(String codigo, Long idProveedor, Long idUsuario, Long idPrincipal, String observaciones, Long idArticuloCodigo, Long orden, Long idArticulo, Long multiplo) {
    setCodigo(codigo);
    setIdProveedor(idProveedor);
    setIdUsuario(idUsuario);
    setIdPrincipal(idPrincipal);
    setObservaciones(observaciones);
    setIdArticuloCodigo(idArticuloCodigo);
    setOrden(orden);
    setIdArticulo(idArticulo);
    setRegistro(LocalDateTime.now());
		this.multiplo= multiplo;
  }
	
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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

  public void setIdArticuloCodigo(Long idArticuloCodigo) {
    this.idArticuloCodigo = idArticuloCodigo;
  }

  public Long getIdArticuloCodigo() {
    return idArticuloCodigo;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

	public Long getMultiplo() {
		return multiplo;
	}

	public void setMultiplo(Long multiplo) {
		this.multiplo=multiplo;
	}

  @Transient
  @Override
  public Long getKey() {
  	return getIdArticuloCodigo();
  }

  @Override
  public void setKey(Long key) {
  	this.idArticuloCodigo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrincipal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticuloCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMultiplo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("codigo", getCodigo());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPrincipal", getIdPrincipal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idArticuloCodigo", getIdArticuloCodigo());
		regresar.put("orden", getOrden());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("registro", getRegistro());
		regresar.put("multiplo", getMultiplo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getCodigo(), getIdProveedor(), getIdUsuario(), getIdPrincipal(), getObservaciones(), getIdArticuloCodigo(), getOrden(), getIdArticulo(), getRegistro(), getMultiplo()
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
    regresar.append("idArticuloCodigo~");
    regresar.append(getIdArticuloCodigo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdArticuloCodigo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticArticulosCodigosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdArticuloCodigo()!= null && getIdArticuloCodigo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticArticulosCodigosDto other = (TcManticArticulosCodigosDto) obj;
    if (getIdArticuloCodigo() != other.idArticuloCodigo && (getIdArticuloCodigo() == null || !getIdArticuloCodigo().equals(other.idArticuloCodigo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdArticuloCodigo() != null ? getIdArticuloCodigo().hashCode() : 0);
    return hash;
  }

}


