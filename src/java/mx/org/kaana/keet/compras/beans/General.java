package mx.org.kaana.keet.compras.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.libs.pagina.JsfBase;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/04/2023
 *@time 01:00:44 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class General extends Material implements Serializable {

  private static final long serialVersionUID = -8548824091384382152L;

  public General() {
    this(-1L);
  }

  public General(Long idArticulo) {
    this(
      "", // String descripcion, 
      0.0D, // Double precioUnitario, 
      "", // String codigo, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      -1L, // Long idOrdenCompra, 
      -1L, // Long idContratoLote, 
      0.0, // Double cantidad, 
      -1L, // Long idOrdenMaterial, 
      idArticulo // Long idArticulo
    );
  }

  public General(String descripcion, Double precioUnitario, String codigo, Long idUsuario, Long idOrdenCompra, Long idContratoLote, Double cantidad, Long idOrdenMaterial, Long idArticulo) {
    super(descripcion, precioUnitario, codigo, idUsuario, idOrdenCompra, idContratoLote, cantidad, idOrdenMaterial, idArticulo);
  }

  @Override
  public int hashCode() {
    int hash = 5;
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
    final Individual other = (Individual)obj;
    if (!Objects.equals(this.getIdArticulo(), other.getIdArticulo())) 
      return false;
    return true;
  }
   
}
