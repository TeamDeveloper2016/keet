package mx.org.kaana.keet.catalogos.materiales.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetContratosMaterialesDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/02/2023
 *@time 09:18:25 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Material extends TcKeetContratosMaterialesDto implements Serializable  {

  private static final long serialVersionUID = -2326934210745272745L;

	private Long idEmpresa;
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikDesarrollo;
	private Long idDesarrollo;
	private UISelectEntity ikContrato;
	private UISelectEntity ikPrototipo;

  public Material() {
    this(-1L);
  }

  public Material(Long key) {
    super(key);
    this.idEmpresa   = -1L;
    this.idDesarrollo= -1L;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }
  
	public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  } 

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
		if(this.ikEmpresa!= null)
			this.idEmpresa= this.ikEmpresa.getKey();
  } 

	public UISelectEntity getIkDesarrollo() {
		return ikDesarrollo;
	} 

	public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
		this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
			this.idDesarrollo= this.ikDesarrollo.getKey();
	}   
  
  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
		if(this.ikContrato!= null)
		  this.setIdContrato(this.ikContrato.getKey());
  }
  
	public UISelectEntity getIkPrototipo() {
		return ikPrototipo;
	}

	public void setIkPrototipo(UISelectEntity ikPrototipo) {
		this.ikPrototipo = ikPrototipo;
		if(this.ikPrototipo!= null)
			this.setIdPrototipo(this.ikPrototipo.getKey());
	}
  
  @Override
  public Class toHbmClass() {
    return TcKeetContratosMaterialesDto.class;
  }

  @Override
  public String toString() {
    return "Material{" + "idEmpresa=" + idEmpresa + ", ikEmpresa=" + ikEmpresa + ", ikDesarrollo=" + ikDesarrollo + ", idDesarrollo=" + idDesarrollo + ", ikContrato=" + ikContrato + ", ikPrototipo=" + ikPrototipo + "} "+ super.toString();
  }
  
}
