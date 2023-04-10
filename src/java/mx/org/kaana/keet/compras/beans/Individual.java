package mx.org.kaana.keet.compras.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.keet.db.dto.TcKeetOrdenesMaterialesDto;
import mx.org.kaana.libs.pagina.JsfBase;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/04/2023
 *@time 01:00:44 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Individual extends Material implements Serializable {

  private static final long serialVersionUID = -8548824096384382152L;
  
  private String lote;
  
  public Individual() {
    this(-1L, -1L);
  }

  public Individual(Long idContratoLote, Long idArticulo) {
    this(
      "", // String descripcion, 
      0.0D, // Double precioUnitario, 
      "", // String codigo, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      -1L, // Long idOrdenCompra, 
      idContratoLote, // Long idContratoLote, 
      0.0, // Double cantidad, 
      -1L, // Long idOrdenMaterial, 
      idArticulo // Long idArticulo
    );
  }

  public Individual(String descripcion, Double precioUnitario, String codigo, Long idUsuario, Long idOrdenCompra, Long idContratoLote, Double cantidad, Long idOrdenMaterial, Long idArticulo) {
    super(descripcion, precioUnitario, codigo, idUsuario, idOrdenCompra, idContratoLote, cantidad, idOrdenMaterial, idArticulo);
    this.lote= "M0L0";
  }

  public String getLote() {
    return lote;
  }

  public void setLote(String lote) {
    this.lote = lote;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 79 * hash + Objects.hashCode(this.getIdContratoLote());
    hash = 79 * hash + Objects.hashCode(this.getIdArticulo());
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) 
      return true;
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final Individual other = (Individual) obj;
    if (!Objects.equals(this.getIdContratoLote(), other.getIdContratoLote())) 
      return false;
    if (!Objects.equals(this.getIdArticulo(), other.getIdArticulo())) 
      return false;
    return true;
  }
  
}
