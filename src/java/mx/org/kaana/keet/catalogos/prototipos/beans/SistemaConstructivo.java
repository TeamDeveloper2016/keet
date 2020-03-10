package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposConstructivosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;


public class SistemaConstructivo extends TcKeetPrototiposConstructivosDto{
	 private ESql accion;
	 private String descripcion;
	 private String grupo;
	 private UISelectEntity ikConstructivo; 

	public SistemaConstructivo() {
		super();
	}

	public SistemaConstructivo(UISelectEntity ikConstructivo) {
		this(ESql.UPDATE, ikConstructivo);
	}

	public SistemaConstructivo(ESql accion, UISelectEntity ikConstructivo) {
		super();
		this.accion = accion;
		this.setIkConstructivo(ikConstructivo);
	}
	
	public UISelectEntity getIkConstructivo() {
		return ikConstructivo;
	}

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
	

	public void setIkConstructivo(UISelectEntity ikConstructivo) {
		this.ikConstructivo = ikConstructivo;
		if(this.ikConstructivo!= null)
			this.setIdConstructivo(this.ikConstructivo.getKey());
		this.descripcion= ikConstructivo.toString("descripcion");
		this.grupo= ikConstructivo.toString("grupo");
	}

	
	
	public boolean isMostrar() {
		return !this.accion.equals(ESql.DELETE);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SistemaConstructivo other = (SistemaConstructivo) obj;
    if (getIdConstructivo() != other.getIdConstructivo() && (getIdConstructivo() == null || !getIdConstructivo().equals(other.getIdConstructivo()))) {
      return false;
    }
    return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
    hash = 68 * hash + (getIdConstructivo() != null ? getIdConstructivo().hashCode() : 0);
    return hash;
	}
	
	 @Override
  public Class toHbmClass() {
    return TcKeetPrototiposConstructivosDto.class;
  }

}
