package mx.org.kaana.keet.catalogos.contratos.reglas;

import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.ESql;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 28/08/2023
 *@time 06:14:33 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public interface IDatoContrato extends IBaseDto {

  public ESql getSql();
  public void setSql(ESql sql);
  public String getDescripcion();
  public void setDescripcion(String descripcion);
  
}
