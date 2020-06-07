package mx.org.kaana.keet.catalogos.contratos.vales.beans;

import java.io.Serializable;

public class DetalleVale extends MaterialVale implements Serializable{
	
	private static final long serialVersionUID = -3379765561814170688L;
	private boolean check;
	
	public DetalleVale() {
		super();
	} // DetalleVale
	
	public DetalleVale(MaterialVale materialVale) {
		super(materialVale.getIdMaterial(), materialVale.getIdMaterial(), materialVale.getNivel(), materialVale.getClave(), materialVale.getIdEmpaqueUnidadMedida(), materialVale.getIdEstacionEstatus(), materialVale.getCosto(), materialVale.getCodigo(), materialVale.getNombre(), materialVale.getTotalDetalle(), materialVale.getIdArticulo(), materialVale.getCantidad(), materialVale.getPrecio());
		this.check= false;
	} // DetalleVale

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}	
	
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } // if
    if (getClass() != obj.getClass()) {
      return false;
    } // if
    final MaterialVale other = (MaterialVale) obj;
    if (getIdMaterial() != other.idMaterial && (getIdMaterial() == null || !getIdMaterial().equals(other.idMaterial))) {
      return false;
    } // if
    return true;
  } // equals

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaterial()!= null ? getIdMaterial().hashCode() : 0);
    return hash;
  } // hashCode	
}
