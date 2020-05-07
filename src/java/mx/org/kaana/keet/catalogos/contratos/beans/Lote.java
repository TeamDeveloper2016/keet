
package mx.org.kaana.keet.catalogos.contratos.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Lote extends TcKeetContratosLotesDto implements Comparable{

	private static final long serialVersionUID = 5298362168423293347L;	
	private UISelectEntity ikPrototipo;
	private UISelectEntity ikFachada;
	private String latitud;
	private String longitud;	
	private ESql accion;

	public Lote() {
		this(ESql.UPDATE, -1L, null, null);
	}

	public Lote(ESql accion, Long key) {
		this(accion, key, null, null);
	}
	
	public Lote(ESql accion, Long key, String latitud, String longitud) {
		super(key);
		this.accion  = accion;
		this.latitud = latitud;
		this.longitud= longitud;
	}
	
	public UISelectEntity getIkPrototipo() {
		return ikPrototipo;
	}

	public UISelectEntity getIkFachada() {
		return ikFachada;
	}

	public void setIkPrototipo(UISelectEntity ikPrototipo) {
		this.ikPrototipo = ikPrototipo;
		if(this.ikPrototipo!= null)
			this.setIdPrototipo(this.ikPrototipo.getKey());
	}

	public void setIkFachada(UISelectEntity ikFachada) {
		this.ikFachada = ikFachada;
		if(this.ikFachada!= null)
			this.setIdTipoFachada(this.ikFachada.getKey());
	}

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}

	public boolean isMostar() {
		return !this.accion.equals(ESql.DELETE);
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}	
	
	public String getGeoreferencia() {
		return !Cadena.isVacio(this.latitud) && !Cadena.isVacio(this.longitud) ? "circulo-verde" : "circulo-rojo";
	}	
	
	@Override
	public int hashCode() {
		int hash = 3;
    hash = 41 * hash + (getManzana() != null ? getManzana().hashCode() : 0);
    return hash;
	}
	
	@Override
  public Class toHbmClass() {
    return TcKeetContratosLotesDto.class;
  }
	
	@Override
	public int compareTo(Object o) {
		int regresar= 0;
		if(o!= null && ((Lote)o).getFechaInicio()!= null)
			regresar= ((this.getFechaInicio().isBefore(((Lote)o).getFechaInicio())))? -1:1;
		return regresar;
	}	
}