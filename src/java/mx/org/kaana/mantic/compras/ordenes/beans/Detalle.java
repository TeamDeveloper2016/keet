package mx.org.kaana.mantic.compras.ordenes.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetRequisicionesOrdenesDto;

public class Detalle extends TcKeetRequisicionesOrdenesDto implements Serializable {
  
  private static final long serialVersionUID = 1911086865764534021L;
  
  private Long id;
  private String consecutivo;
  private String contrato;
  private String residente;
  private String entrega;
  private String propio;
  private String nombre;
  private Double cantidad;
  private String folio;
  private String estatus;
  private Long idArticulo;
  private Long idOrdenCompra;
  private ESql sql;

  public Detalle() {
    this(
      -1L, // id, String 
      null, // consecutivo, 
      null, // String residente, 
      null, // Date entrega, 
      null, // String propio, 
      null, // String nombre, 
      0D, // Double cantidad, 
      null, // String estatus, 
      -1L, // Long idArticulo
      ESql.SELECT // ESql sql
    );
  }

  public Detalle(Long id, String consecutivo, String residente, String entrega, String propio, String nombre, Double cantidad, String estatus, Long idArticulo, ESql sql) {
    super(id);
    this.id = id;
    this.consecutivo = consecutivo;
    this.residente = residente;
    this.entrega = entrega;
    this.propio = propio;
    this.nombre = nombre;
    this.cantidad = cantidad;
    this.estatus = estatus;
    this.idArticulo= idArticulo; 
    this.sql = sql;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getResidente() {
    return residente;
  }

  public void setResidente(String residente) {
    this.residente = residente;
  }

  public String getEntrega() {
    return entrega;
  }

  public void setEntrega(String entrega) {
    this.entrega = entrega;
  }

  public String getPropio() {
    return propio;
  }

  public void setPropio(String propio) {
    this.propio = propio;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public String getFolio() {
    return folio;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public String getEstatus() {
    return estatus;
  }

  public void setEstatus(String estatus) {
    this.estatus = estatus;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public String getContrato() {
    return contrato;
  }

  public void setContrato(String contrato) {
    this.contrato = contrato;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Detalle other = (Detalle) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Detalle{" + "id=" + id + ", consecutivo=" + consecutivo + ", contrato=" + contrato + ", residente=" + residente + ", entrega=" + entrega + ", propio=" + propio + ", nombre=" + nombre + ", cantidad=" + cantidad + ", folio=" + folio + ", estatus=" + estatus + ", idArticulo=" + idArticulo + ", idOrdenCompra=" + idOrdenCompra + ", sql=" + sql + '}';
  }

  @Override
  public Class toHbmClass() {
    return super.toHbmClass(); 
  }

}
