package mx.org.kaana.sakbe.catalogos.maquinaria.beans;

import java.io.Serializable;
import java.util.Random;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.sakbe.db.dto.TcSakbeMaquinariasInsumosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 29/06/2022
 *@time 02:49:31 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Insumo extends TcSakbeMaquinariasInsumosDto implements Serializable {

  private static final long serialVersionUID = 3428865120547117667L;

  private UISelectEntity ikTipoCombustible;
  private String combustible;
  private ESql sql;

  public Insumo() {
    this(-1L);
  }

  public Insumo(Long idMaquinariaInsumo) {
    super(
      -1L, // Long idMaquinaria, 
      1D, // Double rendimiento, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      null, // String observaciones, 
      (new Random().nextLong())* -1L, // Long idMaquinariaInsumo, 
      -1L, // Long idTipoCombustible, 
      40D, // Double capacidad,
      1D // Double maximo
    );
    this.ikTipoCombustible= new UISelectEntity(this.getIdTipoCombustible());
    this.sql= ESql.INSERT;
  }

  public UISelectEntity getIkTipoCombustible() {
    return ikTipoCombustible;
  }

  public void setIkTipoCombustible(UISelectEntity ikTipoCombustible) {
    this.ikTipoCombustible = ikTipoCombustible;
		if(this.ikTipoCombustible!= null)
		  this.setIdTipoCombustible(this.ikTipoCombustible.getKey());
  }

  public String getCombustible() {
    return combustible;
  }

  public void setCombustible(String combustible) {
    this.combustible = combustible;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }
  
  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasInsumosDto.class;
  }
  
}
