package mx.org.kaana.sakbe.catalogos.maquinaria.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.sakbe.db.dto.TcSakbeMaquinariasHerramientasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 6/07/2022
 *@time 01:29:41 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Herramienta extends TcSakbeMaquinariasHerramientasDto implements Serializable {

  private static final long serialVersionUID = 1074085661026524903L;

  private UISelectEntity ikHerramienta;
  private String herramienta;
  private ESql sql;

  public Herramienta() {
    this(-1L);
  }

  public Herramienta(Long idMaquinariaHerramienta) {
    super(
      -1L, // Long idMaquinaria, 
      1D, // Double horas, 
      idMaquinariaHerramienta, // Long idMaquinariaHerramienta, 
      JsfBase.getIdUsuario(), // Long idUsuario
      null, // String observaciones, 
      -1L // Long idHerramienta
    );
    this.ikHerramienta= new UISelectEntity(this.getIdHerramienta());
    this.sql= ESql.INSERT;
  }

  public UISelectEntity getIkHerramienta() {
    return ikHerramienta;
  }

  public void setIkHerramienta(UISelectEntity ikHerramienta) {
    this.ikHerramienta = ikHerramienta;
    if(this.ikHerramienta!= null)
		  this.setIdHerramienta(this.ikHerramienta.getKey());    
  }

  public String getHerramienta() {
    return herramienta;
  }

  public void setHerramienta(String herramienta) {
    this.herramienta = herramienta;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }
  
  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasHerramientasDto.class;
  }  
  
}
